package com.intersys.globals.graphapi.globalsgraphdb;

import com.intersys.globals.graphapi.GraphEdge;
import com.intersys.globals.graphapi.Graph;

/**GlGraphEdge implements the Graph API using the GlGraphNode as the base node.
 */
public class GlGraphEdge extends GlGraphComponent implements GraphEdge<GlGraphNode> {
    @SuppressWarnings("compatibility:-8441436869535637716")
    private static final long serialVersionUID = 1L;
    private GlGraph inGraph = null;
    private GlGraphNode From;
    private GlGraphNode To;
    
    public static final long DEFAULT_EDGE_WEIGHT = 1L;
    
    private Long edgeWeight = DEFAULT_EDGE_WEIGHT;
    

    /** Package-private - do not want the base constructor called from outside the package
     */
    GlGraphEdge() {
        super();
    }

    /** Private Constructor adds a new Edge to a graph with specified node.
     * @param hostGraph graph in which the new edge is recorded, along with the nodes
     * @param edgeFromNode 'from' node in the edge
     * @param edgeToNode 'to' node in the edge
     */
    GlGraphEdge(GlGraph hostGraph, GlGraphNode edgeFromNode, GlGraphNode edgeToNode) {
        inGraph = hostGraph;
        From = edgeFromNode;
        To = edgeToNode;
    }

    /** Package-private Constructor sets up an Edge node within a graph, but without actual nodes
     * @param inGraph Graph in which to build the Edge
     */
    GlGraphEdge(GlGraph inGraph) {
        super();
        this.inGraph = inGraph;
    }
    
    public GlGraphNode getFrom() {
        return From;
    }

    public GlGraphNode getTo() {
        return To;
    }

    /** Attach the Edge within the given Graph
     * @param inGraph is the graph where the Edge should be added
     */
    void setInGraph(GlGraph inGraph) {
        this.inGraph = inGraph;
    }

    /** Create the "From" attachment
     * @param newSrcNode node for "From" or "Source" in the Edge
     */
    void setFrom(GlGraphNode newSrcNode) {
        this.From = newSrcNode;
    }

    /** Create the "To" attachment
     * @param newDestNode node for "From" or "Target" in the Edge
     */
    void setTo(GlGraphNode newDestNode) {
        this.To = newDestNode;
    }


    @Override
    public Long getEdgeWeight() {
        return edgeWeight;
    }

    /** Set the weight of a new edge.  Not to be changed after instantiation, so package-private.
     * @param newWeight Weight for this edge.
     */
    void setEdgeWeight(long newWeight) {
        edgeWeight = newWeight;
    }

    @Override
    public boolean hasNode(GlGraphNode nodeToCheck) {
        return ((getFrom().equals(nodeToCheck)) || (getTo().equals(nodeToCheck)));
    }

    @Override
    public Graph getGraph() {
        return inGraph;
    }


}
