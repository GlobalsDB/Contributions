var rootOfGlobalsInstall = process.env.GLOBALS_HOME;
var rootOfNodeInstall = process.env.nodeRoot;
var globals = require(rootOfNodeInstall+'\\cache');
var pathToGlobalsMGR = rootOfGlobalsInstall + '/mgr';
var assert=require('assert');


testBigGraph();


//begin graph class
function graph (name)
{
	//list of methods:
	this.createVertex=createVertex; 
	this.dumpInfo=dumpInfo; 
	this.killGraph=killGraph; 
	this.deleteVertex=deleteVertex;
	this.deleteEdge=deleteEdge;
	this.getDatum=getDatum;
	this.getVertexDatum=getVertexDatum;
	this.getEdgeDatum=getEdgeDatum;
	this.addVertexDatum=addVertexDatum;
	this.addEdgeDatum=addEdgeDatum;
	this.listConnected1=listConnected1;
	this.listConnected2=listConnected2;
	
	
	//beginning of the "actual" graph constructor
	this.name=name; //name should be a string WITHOUT SPACES
	this.global=new globals.Cache();
	this.global.open({
		path: pathToGlobalsMGR,
		username: "userName",
		password: "password",
		namespace: name
	});
	//end of "actual" graph constructor
	
	
	//methods:
	function createVertex(vertexName)// public method; adds a vertex to the graph
	//efficiency: O(set)
	{
		this.global.set({
			global: this.name,
			subscripts: [vertexName],
			data:"foo"
		});
	}
	
	function dumpInfo() //note: this thing is hideous to look upon!
	/*
	 * public method, but for personal use; prints info on the graph
	 */	
	{
		var ref1="";
		var ref2;
		var ref3;
		var string;
		console.log("The graph named \"" +this.name+ "\" has the following information:");
		ref1=this.global.order({
			global:this.name,
			subscripts:[ref1]
		}).result;
		while(ref1!="") //for each vertex...
		{
			//print the data
			console.log("\tA vertex named \""+ref1+ "\" which contains the following data:");
			ref2="";
			ref2=this.global.order({
				global:this.name,
				subscripts:[ref1, ref1,ref2]
			}).result;
			while(ref2!="") //print the data
			{
				string="\t\t"+ref2+ ": ";
				string+=this.global.get({
					global:this.name,
					subscripts: [ref1, ref1, ref2]
				}).data;
				console.log(string);
				ref2=this.global.order({
					global:this.name,
					subscripts:[ref1, ref1,ref2]
				}).result;
			}																	//this is part of dumpInfo()
			
			
			//print the edges
			console.log("\tand has the following edges:");
			ref2=this.global.order({
				global:this.name,
				subscripts:[ref1, ref2]
			}).result;
			while(ref2!="")
			{
				if (ref2!=ref1)
				{
					string="\t\t("+ref1+ ", "+ ref2 + ") which has the following data:";
					console.log(string);
					
					//print the info stored in the edges
					ref3="";
					ref3=this.global.order({
						global: this.name,
						subscripts: [ref1, ref2, ref3]
					}).result;
					while (ref3!="")
					{
						string="\t\t\t"+ref3+ ": ";
						string+=this.global.get({
							global: this.name,
							subscripts: [ref1,ref2,ref3]
						}).data;
						console.log(string);
						ref3=this.global.order({
							global: this.name,
							subscripts: [ref1, ref2, ref3]
						}).result;
					}
				}
				
				ref2=this.global.order({
					global:this.name,
					subscripts:[ref1,ref2]
				}).result;
			}
				
			console.log();
			ref1=this.global.order({
				global:this.name,
				subscripts:[ref1]
			}).result;
		}
	}
	//end of dumpInfo()	
	
	function killGraph() //public method; clears the graph and closes the connection to the database
	//efficiency: O(kill+close)
	{
		this.global.kill({
			global:this.name
		});
		this.global.close();
	}
	
	function deleteVertex(vertex) //public method; deletes the named vertex, as well as all edges to or from it
	//efficiency:O(V*(kill+order))
	{
		//this step deletes the vertex and all edges from it:
		this.global.kill({
			global: this.name,
			subscripts: [vertex]
		});
		//this step deletes the edges to it:
		var toDelete=this.listConnected2(vertex);
		for (var i=0;i<toDelete.length;i++)
		{
			this.deleteEdge(toDelete[i], vertex);
		} 
	}
	
	function deleteEdge(fromVertex, toVertex, reflect)
	/*
	 * public method
	 * 
	 * deletes the edge from fromVertex to toVertex
	 * 
	 * the vertices may be either the actual vertex objects
	 * or the names of the vertices
	 * 
	 * reflect is an optional parameter
	 * if reflect==true then the edge from toVertex to 
	 * fromVertex will also be deleted
	 * 
	 * efficiency:O(kill)
	 */
	{
		this.global.kill({
			global: this.name,
			subscripts: [fromVertex, toVertex]
		});
		if (reflect==true)
		{
			this.global.kill({
				global: this.name,
				subscripts: [toVertex, fromVertex]
			});
		}
		
	}
	
	function getDatum(param1, param2, param3)
	/*
	 * public method
	 * 
	 * figures out which submethod to call to get datum
	 * 
	 * if you are getting data from a vertex, 
	 * param1 is the vertex name
	 * param2 is the key
	 * param3 is undefined
	 * 
	 * if you are getting data from an edge
	 * param1 is the from vertex
	 * param2 is the to vertex
	 * param3 is the key
	 * 
	 * vertices may be either strings or the vertex objects
	 * 
	 * efficiency: O(get)
	 */
	{
		var returnable;
		if (param3==undefined)
		{
			returnable=this.getVertexDatum(param1, param2);
		}
		else
		{	
			returnable=this.getEdgeDatum(param1, param2, param3);
		}	
		return returnable;
	}
	
	function getVertexDatum(vertex, key)
	/*
	 * private method
	 * 
	 * gets data from the vertex with the appropriate key
	 * 
	 * efficiency: O(get)
	 */
	{
		return (this.global.get({
			global: this.name,
			subscripts: [vertex, vertex, key]
		}).data);
	}
	
	function getEdgeDatum(fromVertex, toVertex, key)
	/*
	 * private method
	 * 
	 * gets data from the edge with the appropriate key
	 * 
	 * efficiency: O(get)
	 */
	{
		assert.notEqual(fromVertex, toVertex, "No data can be stored from a vertex to itself, so no data can be retrieved!");
		return (this.global.get({
			global: this.name,
			subscripts: [fromVertex, toVertex, key]
		}).data);
	}
	
	function addVertexDatum(vertex, key, value) //adds a piece of data to a vertex
	/*
	 * public method
	 * 
	 * adds a piece of data to a vertex
	 * 
	 * note that you DO NOT have to call createVertex() before calling addVertexDatum()
	 */
	{
		this.global.set({
			global: this.name,
			subscripts: [vertex, vertex, key],
			data: value
		});
	}
	
	function addEdgeDatum(fromVertex, toVertex, key, value, reflect, cheating)
	/*
	 * public method
	 * 
	 * adds a piece of data to an edge
	 * 
	 * fromVertex and toVertex are the names of the vertices
	 * they must be different, and they must have been previously initialized
	 * 
	 * key is the "name" of the data
	 * and value is the actual value being stored
	 * 
	 * reflect is an optional parameter, if it is true
	 * then the datum will also be put in the edge
	 * from toVertex to fromVertex
	 * 
	 * cheating is another option parameter
	 * if it is true, this method will run twice as fast
	 * but it allows you to do invalid things
	 */
	{
		assert.notEqual(fromVertex, toVertex, "No data can be stored from a vertex to itself.");
		if(cheating!=true)
		{
			var fromExists=this.global.data({
				global: this.name,
				subscripts: [fromVertex]
			}).result;
			var toExists=this.global.data({
				global: this.name,
				subscripts: [toVertex]
			}).result;
			assert.notEqual(fromExists, 0, "The from vertex needs to exist!");
			assert.notEqual(toExists, 0, "The to vertex needs to exist!");
		}
		this.global.set({
			global: this.name,
			subscripts: [fromVertex, toVertex, key],
			data: value
		});
		if (reflect==true)
		{
			this.global.set({
				global: this.name,
				subscripts: [toVertName, fromVertName, key],
				data: value
			});
		}
	}
	
	
	function listConnected1(vertex, a)
	/*
	 * public method
	 * 
	 * similar to listConnected1 from before
	 * 
	 * creates an array of the name of vertices to which vertex has an edge
	 * if (a===1) it prints the array with a message
	 * else it returns the array
	 *  
	 * efficiency:O(order*V)
	 */
	{
		var ref="";
		var returnable=[];
		ref=this.global.order({
			global: this.name,
			subscripts: [vertex, ref]
		}).result;
		while (ref!="")
		{
			if (ref!=vertex)
			{
				returnable.push (ref);
			}
			ref=this.global.order({
				global: this.name,
				subscripts: [vertex, ref]
			}).result;
		}
		if (a==1)
		{
			console.log("Connected vertices: " + returnable);
		}
		else
		{
			return returnable;
		}
	}
	
	function listConnected2 (vertex, a)
	{
		var ref="";
		var returnable=[];
		ref=this.global.order({
			global: this.name,
			subscripts: [ref]
		}).result;
		while(ref!="")
		{
		if (ref!=vertex)
			{
				var b= this.global.data({
					global: this.name,
					subscripts:[ref, vertex]
				}).defined;
				if (b>0)
				{
					returnable.push(ref);
				}
			}
			ref=this.global.order({
				global: this.name,
				subscripts: [ref]
			}).result;
		}
		if (a==1)
			{
			console.log("Connected Vertices: "+ returnable);
			}
		else 
		{
			return returnable;
		}
	}
}
//end graph class


function randomString() //creates a random string 8 characters long
{
	var characters= "0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
	var s="";
	var rnum;
	for (var i=0;i<8;i++)
	{
		rnum=Math.floor(Math.random()*characters.length);
		s+=characters.substring(rnum, rnum+1);
	}
	return s;
}

function testBigGraph() //a method for testing the scalability of the graph DB
{
	var naturalNumbers=new graph("Numbers");
	
	randomGraph (naturalNumbers, 500000); //this initializes the graph, running takes a while though

//	naturalNumbers.dumpInfo();  //note: dumpInfo prints EVERYTHING, so it is not practical for large graphs

//	testGetDatum(naturalNumbers);
	
//	naturalNumbers.dumpInfo();  //note: dumpInfo prints EVERYTHING, so it is not practical for large graphs

//	testListConnected1(naturalNumbers);
//	testListConnected2(naturalNumbers);
	naturalNumbers.global.close();
	//	naturalNumbers.killGraph(); //run this to clear everything and kill the graph
}

function randomGraph(graph, size)
/*
 * personal method
 * 
 * gives graph vertices with the names 0,1,2,..., size-1
 * also gives it 2*size data and approximately 6*size edges 
 * 
 * should be O(size)
 */
{
	console.log("Starting intialization of the graph...");
	graph.global.kill(graph.name); //clears everything from the graph
	//makes the vertices
	for (var i=0;i<size;i++)
	{
		graph.addVertexDatum(i, "Invariant", randomString());
		if (i%50000==0)
		{
			console.log("Phase 1 is "+ i*100/size+ "% complete.");
		}
	}
	console.log("Phase 1 complete!");
	//give vertices data:
	for (var i=0;i<size*2;i++)
	{
		var randomPosition=Math.floor(Math.random()*size);
		graph.addVertexDatum(randomPosition, randomString(), randomString());
		if (i%50000==0)
		{
			console.log("Phase 2 is "+ i*100/(size*2)+ "% complete.");
		}
	}
	console.log("Phase 2 complete!");
	//makes edges with data:
	for (var i=0;i<size*6;i++)
	{
		var randomPosition1=Math.floor(Math.random()*size);
		var randomPosition2=Math.floor(Math.random()*size);
		if (randomPosition2!=randomPosition1)
		{
			graph.addEdgeDatum(randomPosition1, randomPosition2, randomString(), randomString(), false, true);
		}
		if (i%50000==0)
		{
			console.log("Phase 3 is "+ i*100/(size*6)+ "% complete.");
		}
	}
	console.log("Intialization complete!");
}

function testListConnected1(graph)
{
	console.log("Starting test of listConnected1() on 100,000 vertices");
	for (var i=0;i<100000;i++)
	{
		graph.listConnected1(i);
	}
	console.log("End test of listConnected1()");
}

function testListConnected2(graph)
{
	console.log("Starting test of listConnected2() on 10 vertices");
	for (var i=0;i<10;i++)
	{
		graph.listConnected2(i, 1);
	}
	console.log("End test of listConnected2()");
}

function testDeleteVertex(graph)
{
	console.log("Starting test of deleteVertex() on 10 vertices");
	for (var i=0;i<10;i++)
		{
		graph.deleteVertex(i);
		console.log("The vertex " +i+ " was deleted.");
		}
	console.log("End test of deleteVertex()");
}

function testGetDatum(graph)
{
	console.log("Starting test of getDatum() on 500,000 vertices");
	for (var i=0;i<500000;i++)
		{
		graph.getDatum(i, "Invariant");

		}
	console.log("End test of getDatum()");
}
