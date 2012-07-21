package com.intersys.globals.graphapi;


import java.util.List;

/** A Path looks like an Edge (extends Edge) but also contains a list of constituent Edges
 * @param <NdType> What is the node type for this path?
 */
public interface GraphPath<NdType extends GraphNode> extends List<GraphEdge<NdType>>, GraphEdge<NdType> {
    /** Calculate aggregate weight of the Path
     * @return total of all weights
     */
    public Long getPathWeight();


    /** Does the given Edge exist in this Path?
     * @param checkEdge Edge to check
     * @return whether Edge is in this Path
     */
    public boolean hasEdge(GraphEdge<NdType> checkEdge);
    
}
