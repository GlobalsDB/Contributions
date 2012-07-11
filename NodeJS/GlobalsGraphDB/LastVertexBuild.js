var rootOfGlobalsInstall = process.env.GLOBALS_HOME;
var rootOfNodeInstall = process.env.nodeRoot;
var globals = require(rootOfNodeInstall+'\\cache');
var pathToGlobalsMGR = rootOfGlobalsInstall + '/mgr';
var assert=require('assert');


testBigGraph();
//sampleMethod();



//begin graph class
function graph (name)
{
	//list of methods:
	this.addVertex=addVertex; 
	this.dumpInfo=dumpInfo; 
	this.killGraph=killGraph; 
	this.deleteVertex=deleteVertex;
	this.deleteEdge=deleteEdge;
	this.getDatum=getDatum;
	this.getVertexDatum=getVertexDatum;
	this.getEdgeDatum=getEdgeDatum;
	this.addDatum=addDatum;
	this.addEdgeDatum=addEdgeDatum;
	
	
	//beginning of the "actual" graph constructor
	this.name=name; //name should be a string WITHOUT SPACES
	this.global=new globals.Cache();
	this.global.open({
		path: pathToGlobalsMGR,
		username: "userName",
		password: "password",
		namespace: "namespace"
	});
	//end of "actual" graph constructor
	
	
	//methods:
	function addVertex(vertex)// public method; adds a vertex to the graph
	//efficiency: O(set)
	{
		this.global.set({
			global: this.name,
			subscripts: [vertex.name],
			data:vertex
		});
		vertex.graph=this;
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
	
	function deleteVertex(vertexName) //public method; deletes the named vertex, as well as all edges to or from it
	//efficiency:O(V*(kill+order))
	{
		//this step deletes the vertex and all edges from it:
		this.global.kill({
			global: this.name,
			subscripts: [vertexName]
		});
		//this step deletes the edges to it:
		var ref="";
		ref=this.global.order({
			global:this.name,
			subscripts: [ref]
		}).result;
		while(ref!="")
		{
			this.global.kill({
				global: this.name,
				subscripts: [ref, vertexName]
			});
			ref=this.global.order({
				global:this.name,
				subscripts: [ref]
			}).result;
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
		var toId; //the name of the to vertex to be deleted
		var fromId; //the name of the from vertex to be deleted
		if (isAVertex(fromVertex))
			{fromId=fromVertex.name;}
		else
			{fromId=fromVertex;}
		if (isAVertex(toVertex))
			{toId=toVertex.name;}	
		else
			{toId=toVertex;}
		this.global.kill({
			global: this.name,
			subscripts: [fromId, toId]
		});
		if (reflect==true)
		{
			this.global.kill({
				global: this.name,
				subscripts: [toId, fromId]
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
		var fromVertex;
		if (isAVertex(param1))
			{fromVertex=param1.name;}
		else 
			{fromVertex=param1;}
		if (param3==undefined)
		{
			returnable=this.getVertexDatum(fromVertex, param2);
		}
		else
		{	
			var toVertex;
			if (isAVertex(param2))
				{toVertex=param2.name;}
			else 
				{toVertex=param2;}
			returnable=this.getEdgeDatum(fromVertex, toVertex, param3);
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
		return (this.global.get({
			global: this.name,
			subscripts: [fromVertex, toVertex, key]
		}).data);
	}
	
	function addDatum(vertex, key, value)
	{
		var vertName=vertex;
		if (isAVertex(vertex))
		{
			vertName=vertex.name;
		}
		this.global.set({
			global: this.name,
			subscripts: [vertName, vertName, key],
			data: value
		});
	}
	
	function addEdgeDatum(fromVertex, toVertex, key, value)
	{
		var fromVertName=fromVertex;
		if (isAVertex(fromVertex))
			{
			fromVertName=fromVertex.name;
			}
		var toVertName=toVertex;
		if (isAVertex(toVertex))
			{
			toVertName=toVertex.name;
			}
		this.global.set({
			global: this.name,
			subscripts: [fromVertName, toVertName, key],
			data: value
		});
	}
	
}
//end graph class

//begin vertex class
function vertex (name, graph)
{
	//list of methods:
	this.addInfo=addInfo; 
	this.listConnected1=listConnected1; 
	this.listConnected2=listConnected2; 
	this.deleteEdge=deleteEdge; 
	this.deleteSelf=deleteSelf; 
	this.addEdgeInfo=addEdgeInfo; 
	this.getDatum=getDatum;
	
	
	
	//beginning of "actual" vertex constructor
	this.name=name;
	this.graph;
	this.isAVertex=true;
	if (graph!=undefined)
	{
		this.graph=graph;
		this.graph.global.set({
			global: this.graph.name,
			subscripts: [this.name],
			data: this
		});
	}
	//end of "actual" vertex constructor
	
	//methods:
	function addInfo(key, value)// public method; adds a datum to the vertex
	//efficiency:O(set)
	{
		this.graph.global.set({
			global: this.graph.name,
			subscripts: [this.name, this.name, key],
			data: value
		});
	}
	
	function listConnected1(param1) 
	/*
	 * public method
	 * 
	 *  creates an array of the vertices which this has an edge to
	 *  if (param===1) it prints the array with a message
	 *  else it returns the array
	 *  
	 *  efficiency:O(V)
	 */
	{
		var ref="";
		ref=this.graph.global.order({
			global: this.graph.name,
			subscripts: [this.name, ref]
		}).result;
		var edgeNames=[];
		while (ref!="")
		{
			if (ref!=this.name)
			{
				edgeNames.push(ref);
			}
				ref=this.graph.global.order({
					global: this.graph.name,
					subscripts: [this.name, ref]
				}).result;
		}
		if (param1===1)
		{
			console.log("The vertex named \""+ this.name+ "\" has edges to the following other vertex(ices):");
			console.log(edgeNames);
		}
		else
		{
			return edgeNames;
		}
	}
	
	function listConnected2(param1)
	/*
	 * public method
	 * 
	 *  creates an array of the vertices which have an edge to this
	 *  if (param===1) it prints the array with a message
	 *  else it returns the array
	 *  
	 *  efficiency:O(V)
	 */
	{
		var ref="";
		ref=this.graph.global.order({
			global: this.graph.name,
			subscripts: [ref]
		}).result;
		var edgeNames=[];
		while (ref!="")
		{
			if (this.graph.global.data({
				global: this.graph.name,
				subscripts: [ref, this.name]
			}).defined>0&& this.name!=ref)
			{
				edgeNames.push(ref);
			}
			ref=this.graph.global.order({
				global: this.graph.name,
				subscripts: [ref]
			}).result;
		}
		if (param1===1)
		{
			console.log("The following other vertices have an edge to \""+ this.name+ "\":");
			console.log(edgeNames);
		}
		else
		{
			return edgeNames;
		}
	}
	
	
	function deleteEdge(vertex, reflect) //public method; calls deleteEdge on the graph (see that method, above, for more detail)
	{
		this.graph.deleteEdge(this, vertex, reflect);
	}
	
	function deleteSelf () //public method; calls deleteVertex on the graph (see that method, above, for more detail)
	{
		this.graph.deleteVertex(this.name);
	}
	
	function addEdgeInfo(vertex, key, value, reflect)
	/*
	 * public method
	 * 
	 * adds data to an edge
	 * vertex can be either a vertex object OR the name of the vertex
	 * 
	 * reflect is an optional parameter
	 * if it is true, then the edge from vertex to this will receive the same information
	 * 
	 * will throw an assertion if vertex=this
	 * this prevents having an edge to yourself
	 * 
	 * efficiency:O(set)
	 */
	{
		if (isAVertex(vertex))
		{
			assert.notEqual(vertex, this, "You may not have an edge from a vertex to itself.");
			this.graph.global.set ({
				global: this.graph.name,
				subscripts: [this.name, vertex.name, key],
				data: value
			});
			if (reflect==true)
			{
				this.graph.global.set ({
					global: this.graph.name,
					subscripts: [vertex.name, this.name, key],
					data: value
				});
			}
		}
		else
		{
			assert.notEqual(vertex, this.name, "You may not have an edge from a vertex to itself.");
			this.graph.global.set ({
				global: this.graph.name,
				subscripts: [this.name, vertex, key],
				data: value
		});
			if (reflect==true)
			{
				this.graph.global.set ({
					global: this.graph.name,
					subscripts: [vertex, this.name, key],
					data: value
				});
			}
		}
	}
	
	function getDatum(param1, param2) 
	/*
	 * public method; used for accessing data
	 */
	{
		return this.graph.getDatum(this, param1, param2);
	}
}
//end vertex class



function isAVertex(candidate) //private method; returns true if candidate is a vertex and false otherwise
	{
		if(candidate===undefined)
			{
			return false;
			}
		return(!(candidate.isAVertex===undefined)&& candidate.isAVertex);
	}

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
	var maxValue=100;
	var naturalNumbers=new graph("Numbers");
//	var numberArray= [];
	console.log("Starting Initialization...");
//	createGlobal (naturalNumbers, maxValue);
	//create vertices
/*	for (var i=0;i<maxValue;i++)
	{
		if (i%Math.floor(maxValue/100)==0)
			{
			console.log("Phase 1 is " +i*100/(maxValue)+"% complete!");
			}
		numberArray[i]=new vertex(i, naturalNumbers);
	}
	//give vertices data
	for (var i=0;i<maxValue*2;i++)
	{
		if (i%Math.floor(maxValue*2/100)==0)
		{
			console.log("Phase 2 is " +i*100/(maxValue*2)+"% complete!");
		}
		var randomPosition= Math.floor(Math.random()*numberArray.length);
		numberArray[randomPosition].addInfo(randomString(), randomString());
	}
	//make edges
	for (var i=0; i<maxValue*6;i++)
	{
		if (i%Math.floor(maxValue*6/100)==0)
		{
			console.log("Phase 3 is " +i*100/(maxValue*6)+"% complete!");
		}
		var randomPosition1=Math.floor(Math.random()*numberArray.length);
		var randomPosition2=Math.floor(Math.random()*numberArray.length);
		if (randomPosition1!=randomPosition2)
		{
			numberArray[randomPosition1].addEdgeInfo(randomPosition2, randomString(), randomString());
			numberArray[randomPosition1].addEdgeInfo(randomPosition2, randomString(), randomString());
		}
	} 
	//graph is done intializing*/
	console.log("Intialization complete!"); 
//	numberArray[7].listConnected1(1);
//	numberArray[0].listConnected2(1);
	naturalNumbers.dumpInfo();
//	naturalNumbers.killGraph();
	naturalNumbers.global.close();
}

function createGlobal(graph, size)
{
	for (var i=0;i<size;i++)
	{
		graph.addDatum(i, "value", i);
	}
	//give vertices data
	for (var i=0;i<size*2;i++)
	{
		var randomPosition=Math.floor(Math.random()*size);
		graph.addDatum(randomPosition, randomString(), randomString());
	}
	for (var i=0;i<size*6;i++)
	{
		var randomPosition1=Math.floor(Math.random()*size);
		var randomPosition2=Math.floor(Math.random()*size);
		graph.addEdgeDatum(randomPosition1, randomPosition2, randomString(), randomString());
	}
}


function sampleMethod()
{
	/*
	 * This will be a heavily-documented sample of what you can do with GlobalsGraphDB
	 * 
	 * Let's start by creating a graph and putting in some data
	 */
	console.log("Creating a graph with some data...\n");
	var relationships=new graph ("Relationships"); //first we make a graph in which we will store our data
	var namesArray=["Alex","Ben","Cory"]; //we will be using an array to initialize the vertices of our graph
	var peopleArray= []; //we will also use an array to hold the references to our vertices
	for (var i=0; i<3;i++)
	{
		peopleArray[i]=new vertex(namesArray [i], relationships);
		/*
		 * notice that when calling the vertex constructor, we give it two arguments:
		 * the "name" of the vertex, 
		 * and the graph we want it to be part of
		 * 
		 * we could have only given it the first argument, which would make a vertex
		 * not attached to any graph, and then added the vertex to a graph later like so:
		 * 
		 * peopleArray[i]=new vertex(namesArray [i]);
		 * relationships.addVertex(peopleArray[i]);
		 * 
		 * but note that you cannot add data to a vertex unless it is associated to a graph
		 */
	}
	peopleArray[0].addInfo("last name", "Smith"); //we add the following datum to the vertex named "Alex": the last name "Smith"
	peopleArray[1].addInfo("last name", "Smith"); //and some more data...
	peopleArray[1].addInfo("age", 24);
	peopleArray[2].addInfo("last name", "Johnson");
	peopleArray[0].addEdgeInfo(peopleArray [1], "relationship", "sister"); 
	/*
	 * notice that to add information to an edge, we use three parameters:
	 * the "to" vertex (or its name)
	 * the key with which to reference this datum
	 * and the datum itself
	 */
	peopleArray[0].addEdgeInfo(peopleArray [1], "Met via", "being siblings", true);
	/*
	 * When adding edge info here, we use a fourth parameter as well.
	 * By setting the fourth parameter to true, we will add the data to the edge going the other way as well
	 * In other words, both the edge from person 0 to person 1 and from person 1 to person 0
	 * will now have a "met via: being siblings" value
	 */
	peopleArray[1].addEdgeInfo(peopleArray [0], "state of friendship", "loathing");
	peopleArray[0].addEdgeInfo(peopleArray [1], "state of friendship", "begrudging");
	peopleArray[1].addEdgeInfo(peopleArray [0], "relationship", "brother");
	peopleArray[2].addEdgeInfo(peopleArray [1], "relationship", "engaged", true);
	peopleArray[2].addEdgeInfo(peopleArray [1], "met at", "college", true);
	peopleArray[2].addEdgeInfo(peopleArray [0], "met at", "has never met, but has heard about");
	//Let's say that's enough data
	
	relationships.dumpInfo(); //this prints the info in the graph
	console.log("----------------------------\n\n");

	// the following methods will say which vertices have edges from and to, respectively, the reference variable
	peopleArray[1].listConnected1(1);
	peopleArray[2].listConnected2(1);
	
	/*
	 * notice that the parameter of the above is 1, which caused it to print that message
	 * if the parameter is left out, the method instead returns the array
	 * so the following will not print anything:
	 */
	peopleArray[1].listConnected1();
	peopleArray[2].listConnected2();
	
	//then we can delete a single edge:
	console.log("Deleting some data...");
	peopleArray[1].deleteEdge(peopleArray[0]);
	//and see what changes:
	peopleArray[1].listConnected1(1);
	
	
	console.log("----------------------------\n\n");
	//then we can delete an entire vertex:
	console.log("Deleting more data...");
	peopleArray[0].deleteSelf();
	//and see what changes:
	relationships.dumpInfo();	
	
	relationships.killGraph(); //this clears all the data from the graph and closes the database
}