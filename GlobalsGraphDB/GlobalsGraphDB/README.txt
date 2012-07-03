This document describes the GlobalsGraphDB API. 
Copyright InterSystems 2012

The GlobalsGraphDB API is a simple .NET wrapper API for the Intersystems GlobalsDB API, 
implementing a simple graph-oriented database using the GlobalsDB data storage structures. 

It is currently provided as a 2-project solution consisting of 1) the API project and 2) a sample 
GUI project that illustrates how to use it. 

A .NET application referencing GlobalsGraphDB can create and manage graph objects without needing 
any references to the GlobalsDB API, and does not need to be written with any knowledge of the 
Globals API or Globals concepts. Of course, Globals needs to be installed and the GlobalsGraphDB 
API references the GlobalsDB API. 

Each "graph" is embodied in the Globals database by a single "global". That global contains all 
the data for the nodes and edges in the graph. A graph need not be connected but there is no 
interconnection between separate graphs. 


It features the following main classes: 
GlobalsGraphAdmin - static admin class
GlGraph     - a graph, consisting of nodes and edges
GlGraphNode - a graph node. Nodes support custom string properties. 
GlGraphEdge - a directed graph edge between nodes. Edges support custom string properties. 
GlGraphComponent - an abstract parent class for GlGraphNode and GlGraphEdge, containing common declarations
 
 
Global storage format: 

A graph is stored as a single global with a top level string value of "GlGraph", as a flag to 
distinguish graph globals from other globals that may be in the same namespace. The following 
examples use the name "mygraph" as the name of the global. 

Graph nodes are keyed by GUIDs, but the GUIDs are never exposed to the client code. They are 
used only within this API. Client code using this API should only need to traffic in object 
references. 

Examples: 

mygraph = "GlGraph"

-- NODES: 
mygraph["nodes", <guid>] = "GlNode" (dummy placeholder to ensure the node will 'persist' even if it has no edges or properties)
-- custom properties
mygraph["nodes", <guid>, "name"] = "XYZ"
mygraph["nodes", <guid>, "fave_color"] = "blue"  

-- EDGES: 
mygraph["edges", <start node guid>, <target node guid>] = "GlEdge" (dummy)
--custom properties: 
mygraph["edges", <start node guid>, <target node guid>, "linethickness"] = "dotted" 
mygraph["edges", <start node guid>, <target node guid>, "priority"] = "high"
 

USAGE: 

Assuming that the Globals database is running, any client application can start by 
using static methods in GlobalsGraphDB (a static class) to browse, open and create 
graphs. Once an application has a GlGraph object it can its methods to open, 
manipulate and create nodes and edges, which are embodied by the GlGraphNode and 
GlGraphEdge classes. These two classes are subclasses of a common parent called 
GlGraphComponent which exists to declare common functionality; the client app 
generally will not need to deal with GlGraphComponent at all. It is free to deal 
specifically with GlGraphNode and GlGraphEdge. 

GlobalsGraphDB graphs are DIRECTED, meaning that every edge has a start node 
and an end node. At most one edge can go from a particular node to another 
particular node. An edge can connect a node to itself. It is possible for 
one edge to go from A->B while another goes from B->A. This all means that the 
set of edges can be thought of as a 2-dimensional matrix over the set of nodes, 
with each spot in the matrix either empty or containing one edge. 

GlGraphNode and GlGraphEdge support custom properties; the client can set a 
string value on a node or edge, keyed by a property name. 

Edges are created using GlGraphNode.ConnectTo, which accepts a GlGraphNode object 
as a parameter identifying the destination of the new edge. 

POSSIBLE FUTURE FEATURES 

- transaction support, using Globals API methods like StartTransaction, Commit, Rollback, etc.

- querying for nodes & edges based on property values (this would be relatively simple as such), 
followed possibly by more free-form query parsing

- non-string custom properties (integers, etc.)
 
- support for non-directed graphs (there are a couple of possible ways to do this)