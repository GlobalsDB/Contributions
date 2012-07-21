package com.intersys.globals.graphapi;

import com.intersys.globals.NodeReference;


import java.util.Collection;

/**Node presents a simple interface for a node in a graph.  Note this cannot exist apart from a graph; if it did, it would 
 * only be an Object!  Consequently its methods include Graphs.
 */
public interface GraphNode<forType> extends GraphComponent {

    /** Get the graph in which this node resides
     * @return the graph for this node.
     */
    public Graph getGraph();

    /** Enumerate the Edges in given graph in which this node participates
     * @param inGraph Graph in which to check
     * @return collection of edges in graph that include this node
     */
    public Collection<GraphEdge> getEdges(Graph inGraph);

    /** Adds an edge from this node to another Node in the graph.  This node is "From" or "Source" on the edge.
     * @param toNode the new "To" or "Target" Node for the new Edge
     */
    public void Connect(GraphNode toNode);
    
    /**Adds an Edge from this node to another Node in the graph.  This node is "From" or "Source" on the edge.
     * @param toNode the new "To" or "Target" Node for the new Edge
     * @param atWeight weight of the new Edge
     */
    public void Connect(GraphNode toNode, Long atWeight);

    /** Get the actual node-type value.  Note that the value is created in the CONSTRUCTOR or from the Graph
     * @return The value of the node type
     */
    public forType getValue();

}
