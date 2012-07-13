var rootOfGlobalsInstall = process.env.GLOBALS_HOME;
var rootOfNodeInstall = process.env.nodeRoot;
var globals = require(rootOfNodeInstall+'\\cache');
var pathToGlobalsMGR = rootOfGlobalsInstall + '/mgr';
var assert=require('assert');

//testUndirectedGraph();
testBigGraph();


//begin graph class
function graph (name, undirected)
{
	//list of methods:
	this.createVertex=createVertex; //public
	this.dumpInfo=dumpInfo; //public
	this.killGraph=killGraph; //public
	this.deleteVertex=deleteVertex; //public
	this.deleteEdge=deleteEdge; //public
	this.getDatum=getDatum; //public
	this.getVertexDatum=getVertexDatum; //private
	this.getEdgeDatum=getEdgeDatum; //private
	this.addDatum=addDatum; //public
	this.addVertexDatum=addVertexDatum; //private
	this.addEdgeDatum=addEdgeDatum; //private
	this.listConnected1=listConnected1; //public
	this.listConnected2=listConnected2; //public
	this.listVertexKeys=listVertexKeys; //public
	this.listVertices=listVertices; //public
	this.listEdgeKeys=listEdgeKeys; //public
	this.deleteDatum=deleteDatum; //public
	this.deleteVertexDatum=deleteVertexDatum; //private
	this.deleteEdgeDatum=deleteEdgeDatum; //private
	
	
	//beginning of the "actual" graph constructor
	this.name=name; //name should be a string WITHOUT SPACES
	if (undirected==true)
		{this.isUndirected=true;}
	else 
		{this.isUndirected=false;}
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
	
	function dumpInfo()
	/*
	 * public method, but for personal use; prints everything about the graph
	 */	
	{
		console.log("The graph named \"" +this.name+ "\" has the following information:");
		var vertexNames=this.listVertices(); //names of the vertices
		var string;
		for (var i=0;i<vertexNames.length;i++) //for each vertex...
		{
			console.log("\tA vertex named \""+vertexNames[i]+ "\" which contains the following data:");
			var dataKeys=this.listVertexKeys(vertexNames[i]); //list of the keys of the data for each vertex
			for (var j=0;j<dataKeys.length;j++) //print the data...
			{
				string="\t\t"+dataKeys[j]+ ": " + this.getDatum(vertexNames[i], dataKeys[j]);
				console.log(string);
			}
			
			//then print the edge info
			console.log("\tand has the following edges:");
			var connectedEdges=this.listConnected1(vertexNames[i]); //list of the edges which this is connected to
			for (var j=0;j<connectedEdges.length;j++)
			{
				console.log("\t\t\t An edge from "+vertexNames[i]+ " to "+connectedEdges[j]+ " with the following data:");
				var edgeDataKeys=this.listEdgeKeys(vertexNames[i], connectedEdges [j]);
				for (var k=0;k<edgeDataKeys.length;k++)
				{
					string="\t\t\t\t"+edgeDataKeys[k]+ ": "+
						this.getDatum(vertexNames[i], connectedEdges[j], edgeDataKeys[k]);
					console.log(string);
				}
			}
			console.log();
		}
	}
	
	function killGraph() //public method; clears the graph and closes the connection to the database
	//efficiency: O(kill+close), but efficiency is not really a concern
	{
		this.global.kill({
			global:this.name
		});
		this.global.close();
	}
	
	function deleteVertex(vertex) //public method; deletes the named vertex, as well as all edges to or from it
	//efficiency:O(edges to this)
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
	 * if reflect==true or this.isUndirected
	 * then the edge from toVertex to 
	 * fromVertex will also be deleted
	 * 
	 * efficiency:O(kill)
	 */
	{
		this.global.kill({
			global: this.name,
			subscripts: [fromVertex, "connectedFromThis", toVertex]
		});
		this.global.kill({
			global:this.name,
			subscripts: [toVertex, "connectedToThis", fromVertex]
		});
		if ((reflect==true||this.isUndirected)&&reflect!="superFalse")
		{
			this.deleteEdge(toVertex, fromVertex, "superFalse");
		}
		
	}
	
	function getDatum(param1, param2, param3)
	/*
	 * public method
	 * 
	 * figures out which private method to call to get datum
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
		if (param3==undefined)
		{
			return this.getVertexDatum(param1, param2);
		}
		else
		{	
			return this.getEdgeDatum(param1, param2, param3);
		}	
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
			subscripts: [vertex, "data", key]
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
			subscripts: [fromVertex, "connectedFromThis", toVertex, key]
		}).data);
	}
	
	function addDatum(param1, param2, param3, param4, param5, param6)
	/*
	 * public method
	 * 
	 * decides to call addVertexDatum or addEdgeDatum
	 * 
	 * if adding data to a vertex:
	 * param1 is the vertex,
	 * param2 is the key
	 * param3 is the value to be added
	 * param4-param6 are undefined
	 * 
	 * if adding data to an edge:
	 * param 1 is the fromVertex
	 * param 2 is the toVertex
	 * param 3 is the key
	 * param 4 is the value
	 * param 5 is the "reflect", and is optional
	 * param 6 is the "cheating" value, and is optional
	 */
	{
		if (param4==undefined)
		{
			this.addVertexDatum(param1, param2, param3);
		}
		else 
		{
			this.addEdgeDatum(param1, param2, param3, param4, param5, param6);
		}
	}
	
	function addVertexDatum(vertex, key, value)
	/*
	 * private method
	 * 
	 * adds a piece of data to a vertex
	 * 
	 * note that you DO NOT have to call createVertex() before calling addVertexDatum()
	 * and calling addVertexDatum is generally more recommended than calling createVertex
	 */
	{
		this.global.set({
			global: this.name,
			subscripts: [vertex, "data", key],
			data: value
		});
	}
	
	function addEdgeDatum(fromVertex, toVertex, key, value, reflect, cheating)
	/*
	 * private method
	 * 
	 * adds a piece of data to an edge
	 * 
	 * fromVertex and toVertex are the names of the vertices
	 * they must have been previously initialized
	 * 
	 * key is the "name" of the data
	 * and value is the actual value being stored
	 * 
	 * reflect is an optional parameter, if it is true
	 * (or if the graph is undirected)
	 * then the datum will also be put in the edge
	 * from toVertex to fromVertex
	 * 
	 * cheating is another option parameter
	 * if it is true, this method will run about twice as fast
	 * but it allows you to do invalid things
	 */
	{
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
		this.global.set({ //sets the data
			global: this.name,
			subscripts: [fromVertex, "connectedFromThis", toVertex, key],
			data: value
		});
		this.global.set({ 
			global: this.name,
			subscripts: [toVertex, "connectedToThis", fromVertex],
			data: fromVertex
		});
		if ((reflect==true||this.isUndirected)&& reflect!="superFalse")
		{
			this.addEdgeDatum(toVertex, fromVertex, key, value, "superFalse", true);
		}
	}
	
	
	function listConnected1(vertex, a)
	/*
	 * public method
	 * 
	 * creates an array of the names of vertices to which this vertex has an edge
	 * if (a===1) it prints the array with a message
	 * else it returns the array
	 */
	{
		var ref="";
		var returnable=[];
		ref=this.global.order({
			global: this.name,
			subscripts: [vertex, "connectedFromThis", ref]
		}).result;
		while (ref!="")
		{
			returnable.push (ref);
			ref=this.global.order({
				global: this.name,
				subscripts: [vertex, "connectedFromThis", ref]
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
		/*
		 * public method
		 * 
		 * creates an array of the names of vertices which have an edge to this vertex
		 * if (a===1) it prints the array with a message
		 * else it returns the array
		 */
		var ref="";
		var returnable=[];
		ref=this.global.order({
			global:this.name,
			subscripts: [vertex, "connectedToThis", ref]
		}).result;
		while (ref!="")
		{
			returnable.push(ref);
			ref=this.global.order({
				global:this.name,
				subscripts: [vertex, "connectedToThis", ref]
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
	
	function listVertexKeys(vertex, a)
	/*
	 * public method
	 * 
	 * makes an array of the keys of all the data for the given vertex
	 * 
	 * if a==1 it prints the array
	 * else it returns the array
	 * 
	 */
	{
		var ref="";
		var returnable=[];
		ref=this.global.order({
			global: this.name,
			subscripts: [vertex, "data", ref]
		}).result;
		while(ref!="")
		{
			returnable.push(ref);
			ref=this.global.order({
				global: this.name,
				subscripts: [vertex, "data", ref]
			}).result;
		}
		if (a==1)
		{
			console.log("Keys of the vertex named " + vertex+ ": "+ returnable);
		}
		else 
		{
			return returnable;
		}
	}
	
	function listEdgeKeys (fromVertex, toVertex, a)
	/*
	 * public method
	 * 
	 * makes an array of all the data keys for the edge
	 * from fromVertex to toVertex
	 * 
	 * if a==1 prints the array
	 * else returns the array
	 */
	{
		var ref="";
		var returnable=[];
		ref=this.global.order({
			global: this.name,
			subscripts: [fromVertex, "connectedFromThis", toVertex, ref]
		}).result;
		while(ref!="")
		{
			returnable.push(ref);
			ref=this.global.order({
				global: this.name,
				subscripts: [fromVertex, "connectedFromThis", toVertex, ref]
			}).result;
		}
		if (a==1)
		{
			console.log("Keys for the edge from " + fromVertex+ " to "+ toVertex+ ": "+ returnable);
		}
		else 
		{
			return returnable;
		}
	}
	
	function listVertices(a)
	/*
	 * public method
	 * 
	 * makes an array of the vertices of this graph
	 * 
	 * if a==1 it prints them out
	 * else it returns them
	 */
	{
		var ref="";
		var returnable=[];
		ref=this.global.order({
			global:this.name,
			subscripts: [ref]
		}).result;
		while (ref!="")
		{
			returnable.push(ref);
			ref=this.global.order({
				global:this.name,
				subscripts: [ref]
			}).result;
		}
		if (a==1)
		{
			console.log("Vertices: "+ returnable);
		}
		else 
		{
			return returnable;
		}
	}
	
	function deleteDatum(param1, param2, param3, param4)
	/*
	 * public method
	 * 
	 * decides to call deleteEdgeDatum or deleteVertexDatum
	 * 
	 * if it is a vertex,
	 * param1 is vertex
	 * param1 is ket
	 * param3 and param4 is undefined
	 * 
	 * if it is an edge
	 * param1 is the fromVertex
	 * param2 is the toVertex
	 * param3 is the key
	 * param4 is the "reflect" value
	 */
	{
		if (param3==undefined)
		{
			this.deleteVertexDatum(param1, param2);
		}
		else
			{
			this.deleteEdgeDatum(param1, param2, param3, param4);
			}
	}
	
	function deleteVertexDatum(vertex, key)//private method; deletes the datum at a vertex for that key
	{
		this.global.kill({
			global: this.name,
			subscripts: [vertex, "data", key]
		});
	}
	
	function deleteEdgeDatum(fromVertex, toVertex, key, reflect)
	/*
	 * private method
	 * 
	 * deletes the datum with the key key from fromVertex to toVertex
	 * 
	 * if reflect==true then it does the same thing backwards
	 */
	{
		this.global.kill({
			global: this.name,
			subscripts: [fromVertex, "connectedFromThis", toVertex, key]
		});
		var a=this.global.data({
			global: this.name,
			subscripts: [fromVertex, "connectedFromThis", toVertex]
		}).result;
		if (a==0)
		{
			this.global.kill({
				global: this.name,
				subscripts: [toVertex, "connectedToThis", fromVertex]
			});
		}
		if ((reflect==true||this.isUndirected)&&reflect!="superFalse")
		{
			this.deleteEdgeDatum(toVertex, fromVertex, key, "superFalse");
		}	
	}
	
	
}
//end graph class

