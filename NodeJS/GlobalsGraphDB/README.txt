This is a globals-based graph database implemented in Node.js.

Three types of objects are implemented in this program: graphs, vertices and edges.

A graph has a name and a global a data within it. There are several implemented methods related to graphs, which are detailed in the comments.

A vertex also has a name datum, as well as a "parent" graph. Some of the methods related to a vertex are listConnected1() and listConnected2(). These methods return
an array of the names of the vertices which have edges from and to this edge, respectively. For methods will be implemented later.

An edge simply has two vertices as data, whose order matters.

Data is stored in this database by combining the global with the edges and vertices. For each vertex, there is a node directly below the root node in the global.
The only subscript on this node is [Vertex.name]. For each edge, there is a node with two subscripts: [FromVertex.name, Tovertex.name]. 
Data for a vertex is stored with three subscripts. For example, for the datum (key, value) about the edge from Vertex1 to Vertex2 would have the subscripts [Vertex1.name,
Vertex2.name, key]. Data about a single vertex is stored in the edge from itself to itself. Thus, if I wanted to give the "Robert" vertex an "age" value, the subscripts are 
["Robert", "Robert", "age"]. This prohibits having an edge from a vertex to itself which actually functions as an edge, but is otherwise an elegant solution, and 
(unlike the previous build) you can finally have a vertex named "data"!

In other words: within the global, things with one subscript are vertices, things with two subscripts are edges, and things with three subscripts are data. 
Something with the subscripts ["foo", "bar", "bas"] is a piece of data with the key bas. If foo==bar, then it is a piece of data about the foo vertex.
Otherwise, it is data about the edge FROM foo TO bar.

Note: you may have to configure the first four lines of code so that the program is able to find the correct globals directory.

UPDATE NOTES (7/9/12):
This update brings one adjustment to the way data is stored and several new or updated methods. Data for a vertex is now stored where data from the vertex to itself would otherwise be stored.
The README above has been changed to reflect this.

The new methods include methods for deleting part of a graph (an edge or a vertex), and several methods now have more typesafety than before.

The unfinished goAlong() method was removed as redundant