This is a globals-based graph database implemented in Node.js.

Three types of objects are implemented in this program: graphs, vertices and edges.

A graph has a name and a global associated with it. There are several implemented methods related to graphs, which are detailed in the comments.

A vertex also has a name associated with it, as well as a "parent" graph. Some of the methods related to a vertex are listConnected1() and listConnected2(). These methods return
an array of the names of the vertices which have edges from and to this edge, respectively. For methods will be implemented later.

An edge simply is associated with a pair of vertices, whose order matters.

Data is stored in this database by combining the global with the edges and vertices. For each vertex, there is a node directly below the root node in the global.
The only subscript on this node is [Vertex.name]. Data about each vertex is stored in the special "data" sub-branch of the node corresponding to each vertex. Thus, 
if you wanted to store a (key, value) pair in the database for the vertex named Vertex.name, the subscripts would be [Vertex.name, "data", key].
Edges correspond to nodes with two subscripts which are both the names of vertices. Thus the subscripts for storing (key, value) for the edge from Vertex1 to Vertex2 would be
[Vertex1.name, Vertex2.name, key]. 


Note: you may have to configure the first four lines of code so that the program is able to find the correct globals directory.