package com.intersys.globals.graphapi;


import com.intersys.globals.graphapi.globalsgraphdb.GlGraphEdge;
import com.intersys.globals.graphapi.globalsgraphdb.GlGraphNode;


import java.util.Collection;
import java.util.logging.Level;

/** Graph presents a collection of graph nodes but also has an attached list of edges among those nodes
 * @param <TNode> is the data type to be used as a Node in the graph
 */
public interface Graph<TNode extends GraphNode> extends Collection<TNode> {

    /** Is this graph a directed graph?
     * @return indication of whether this graph is directed or not
     */
    public boolean isDirected();


    /** Label the units of each edge in the graph with this String.  Note is it invalid for edges in a graph to have different
     * units.
     */
    public String UnitsLabel = "Edge";

    /** Have the Graph give you a new Node
     * @param withLabel label the node with this string
     * @return handle to the new Node
     */
    public TNode newGraphNode (String withLabel);

    /**Indicates whether the graph as instantiated, when adding an Edge, will allow the Graph to be extended with
     * nodes in the added Edge that are not already in the Graph
     * @return addNodesWithEdges status (yes/no)
     */
    public boolean isAddNodesWithEdges();

    /** Enumerate each Edge in the graph.  
     * @return Collection of Edges
     */
    public Collection<GraphEdge<TNode>> getEdges();


    /** Create an Edge between two nodes in the graph.  Add nodes to graph if missing.
     * @param from one of the nodes to connect
     * @param to the other node to connect
     */
    public void connectNodes(TNode from, TNode to);


    /**Create an Edge between two nodes in the graph.  Add nodes to graph if missing.
     * @param from one of the nodes to connect
     * @param to the other node to connect
     * @param edgeWeight numeric weight of the new edge
     */
    public void connectNodes(TNode from, TNode to, long edgeWeight);

    /** Enumerate the Edges in the Graph that are connected to a specific node
     * @param ofNode Node to check for edges
     * @return collection of edges
     */
    public Collection<GraphEdge<TNode>> getEdgeConnections(TNode ofNode);


    /** Determine if two nodes are directly connected in the graph (note - not a Path function)
     * @param nodeX one node to check
     * @param nodeY other node to check
     * @return indicates if connected
     */
    public boolean areConnected(TNode nodeX, TNode nodeY);


    /** Get the name of the graph (by whatever means is needed or appropriate in implementing class)
     * @return Name by which this graph is known
     */
    public String getName();

    /** Indicate whether to tolerate non-member nodes on add
     * @param addNodesWithEdges setting; if true, nodes in edges will be added to the graph when absent
     */
    public void setAddNodesWithEdges(boolean addNodesWithEdges);

    /** Generate a GraphViz .DOT graph
     * @return the graph in textual format
     */
    public String vizGraph();

    /** Report out all the nodes in the graph
     * @param lgLevel logging level
     */
    public void printLabels(Level lgLevel);

    /** Remove a single edge from the graph
     * @param edgeToRemov edge to remove
     * @return success indicator
     */
    boolean removeEdge(GraphEdge edgeToRemov);

    /** Remove a GraphNode and all its edges
     * @param grf2go node to remove
     * @return success indicator
     */
    boolean remove(GraphNode grf2go);
}
