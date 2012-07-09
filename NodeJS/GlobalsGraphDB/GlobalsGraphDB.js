var rootOfGlobalsInstall = process.env.GLOBALS_HOME;
var rootOfNodeInstall = process.env.nodeRoot;
var globals = require(rootOfNodeInstall+'\\cache');
var pathToGlobalsMGR = rootOfGlobalsInstall + '/mgr';


var assert=require('assert');
testMethod();

function graph (name)
{
	//list of methods:
	this.addVertex=addVertex; //finished
	this.dumpInfo=dumpInfo; //finished
	this.killGraph=killGraph; //finished
	this.deleteVertex=deleteVertex;	//finished
	
	
	//beginning of the "actual" graph constructor
	this.name=name;
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
	{
		this.global.kill({
			global:this.name
		});
		this.global.close();
	}
	
	function deleteVertex(vertexName) //public method; deletes the named vertex, as well as all edges to or from it
	{
		this.global.kill({
			global: this.name,
			subscripts: [vertexName]
		});   //this step deletes the vertex and all edges from it
		//to delete edges to it:
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
}

function vertex (name, graph)
{
	//list of methods:
	this.addInfo=addInfo; //finished
	this.listConnected1=listConnected1; //finished
	this.listConnected2=listConnected2; //finished
	this.deleteEdge=deleteEdge; //finished
	this.deleteSelf=deleteSelf; //finished

	
	
	
	//end of "actual" vertex constructor
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
			}).defined%2===1&& this.name!=ref)
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
	
	
	function deleteEdge(vertexName) //public method; deletes the edge from this node to the vertexName node
	{
		this.graph.global.kill({
			global: this.graph.name,
			subscripts: [this.name, vertexName]
		});
	}
	
	function deleteSelf () //public method; deletes vertex and all edges to or from it
	{
		this.graph.deleteVertex(this.name);
	}
}


function edge(fromVert, toVert)
{
	//list of methods:
	this.addInfo=addInfo;  //finished
	this.deleteSelf=deleteSelf; //finished

	//beginning of "actual" edge constructor
	//check that vertices are from the same graph:
	assert.equal(fromVert.graph, toVert.graph, "The vertices of an edge need to be from the same graph!");
	this.fromVertex=fromVert;
	this.toVertex=toVert;
	this.isAnEdge=true;
	this.fromVertex.graph.global.set({
		global: this.fromVertex.graph.name,
		subscripts: [this.fromVertex.name, this.toVertex.name],
		data: this
	});
	//end of "actual" edge constructor
	
	
	function addInfo(key, value)//public method; adds a datum to an edge
	{
		this.fromVertex.graph.global.set({
			global: this.fromVertex.graph.name,
			subscripts: [this.fromVertex.name, this.toVertex.name, key],
			data: value
		});
	}
	
	
	
	function deleteSelf() //public method; removes this edge from the database
	{
		this.fromVertex.deleteEdge(this.toVertex.name);
	}
}

function isAnEdge(candidate) //private method; returns true if candidate is an edge and false otherwise
{
	if(candidate===undefined)
		{
		return false;
		}
	return(!(candidate.isAnEdge===undefined)&& candidate.isAnEdge);
}


function isAVertex(candidate) //private method; returns true if candidate is a vertex and false otherwise
	{
		if(candidate===undefined)
			{
			return false;
			}
		return(!(candidate.isAVertex===undefined)&& candidate.isAVertex);
	}




function testMethod() //a method for testing the class
{

	var a=[];
	a[0] =new graph ("Workers");
	a[1] =new vertex("Robert",a[0]);
	a[2] =new vertex("Iran",a[0]);
	a[3] =new vertex("Michael",a[0]);
	a[4] =new edge(a[1],a[2]);
	a[4].addInfo("relationship", "boss");
	a[5] =new edge(a[1],a[3]);
	a[6] =new edge(a[2],a[3]);
	a[6].addInfo("relationship", "co-worker");
	a[1].addInfo("last name", "Huben");
	a[0].dumpInfo();
	a[0].killGraph();  
}