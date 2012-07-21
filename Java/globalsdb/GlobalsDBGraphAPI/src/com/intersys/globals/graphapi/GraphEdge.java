package com.intersys.globals.graphapi;


/** GraphEdge connects two nodes in the context of a graph
 * @param <TNode> specialized node subtype as desired by implementor
 */
public interface GraphEdge<TNode extends GraphNode> extends GraphComponent {

    /**edgeWeight is an integer value to be used for weighted graphs.
     */
    public Long getEdgeWeight();

    /** Get the overall graph in which the edge exists
     * @return graph this edge is in
     */
    public Graph getGraph();
    
    /** "Source" node getter
     * @return the source or 'from' Node of this Edge
     */
    public TNode getFrom();

    /** "Target" node getter
     * @return the target or 'to' Node of this Edge
     */
    public TNode getTo();

    /** Checks either end of edge for the node
     * @param nodeToCheck node to seek
     * @return whether the nodeToCheck is in this Edge
     */
    public boolean hasNode(TNode nodeToCheck);
}
