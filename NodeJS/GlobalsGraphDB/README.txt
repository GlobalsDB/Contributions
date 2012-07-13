This is a globals-based graph database implemented in Node.js.

A graph is an object with a name and a global as data within it. The methods for graphs are designed so that no user has to actually call get(), set(), kill(), etc 
on a global. See the commments for details on the methods.

Data is stored in the graph in a precise format. Each graph has a name and a global, and the global actually stores all the data. Each vertex corresponds to a node directly 
below the root node in the global. The subscripts for this node are [vertexName]. 

Directly below each vertex are three nodes, whose names are "connnectedToThis", "connectedFromThis", and "data". 

Underneath "data", actual data on the vertex is stored. Thus, if you wanted to give the "Robert" vertex an "age" value, the subscripts for where that data
is stored are ["Robert", "data", "age"].

Underneath "connectedToThis" are the names of all vertices that have an edge to this node. No other information is stored here besides the names.

Underneath "connectedFromThis" are the names of all vertices that this vertex has an edge to (note that this might not be the same list as under "connectedFromThis" because
we are not necessarily talking about undirected graphs). Additionally, actual data is stored here about the edges. Thus if you want to give the edge FROM "Robert" TO "Iran"
a "Time Known" value, the subscripts would be ["Robert", "connectedFromThis", "Iran", "Time Known"].

This version has support for edges from a vertex to itself. They can be handled in exactly the same way other edges are handled.

This version has support for undirected graphs, that is, graphs where each edge from A to B is also an edge from B to A and has the same data going either way.
In order to make use of this, you must set the "reflect" parameter to true whenever you call deleteEdge() or addEdgeDatum(). Then when you call getVertexDatum()
or listEdgeKeys() you may call it with the vertices in either order.

Note: you may have to configure the first four lines of code so that the program is able to find the correct globals directory.



UPDATE NOTES (7/9/12):
This update brings one adjustment to the way data is stored and several new or updated methods. Data for a vertex is now stored where data 
from the vertex to itself would otherwise be stored.
The README above has been changed to reflect this.

The new methods include methods for deleting part of a graph (an edge or a vertex), and several methods now have more typesafety than before.

The unfinished goAlong() method was removed as redundant

UPDATE NOTES (7/9/12 part 2):
Removed Edges as an object after I realized that they were inconvenient to work with and had little independent existance. The above README was updated to 
reflect the current build.

Also created a heavily-commented sample method which goes through many of the features of GlobalsGraphDB.

UPDATE NOTES (7/10/12):
Added a deleteEdge() method to the graph class. Removed testMethod(). A few other minor tweaks.

UPDATE NOTES(7/11/12)
Added a getDatum() method so that you can actually get a piece of info from a graph without using globals get() method. Also added information on the efficiency
of several methods.

Finally, I made a testBigGraph() method to check how the program performs with large graphs. On my computer, I am constrained by the initializer (which creates
and adds data to the graph) before my methods noticeably slow. For me, the initializor gets annoyingly slow at maxValue=~100,000. The number of vertices is
maxValue, the number of datum for vertices is 2*maxValue, and the number of edges is ~6*maxValue.

UPDATE NOTES(7/11/12 part 2)
Removed vertex objects. All interfacing with the database is done through the graph object now. The old version is stored in the "LastVertexBuild" document.

Several improvements were made to the method for testing big graphs. Now a submethod is called which initializes a graph with random values. This can take a long time,
so the graph is no longer wiped between times running the program.

UPDATE NOTES(7/12/12)
This commit primarily brings some observations about the efficiency of the program. For this purpose, please see the new "Efficiency Notes" document.

There have also been some added features and tweeks. I have changed the name of listConnected() to listConnected1() and I have reintroduced listConnected2(),
a method that was removed with vertex objects. I have also changed deleteVertex() to make use of listConnected2().

UPDATE NOTES (7/13/12)
Modified how data was stored so that a vertex also has access to the names of vertices which have an edge to it. Rewrote listConnected2() to make use of this new feature,
which allows it to run much faster. Added several new methods like listVertexKeys(), listVertices(), and listEdgeKeys(). Rewrote dumpInfo() to make use of the new methods
so that it isn't so much of an eyesore. Overhauled the README to reflect these changes.

UPDATE NOTES (7/13/12 part 2)
Added support for edges from a vertex to itself and for undirected graphs. Further revisions of the README. Future updates will focus on a sample method and
more helpful commenting.
