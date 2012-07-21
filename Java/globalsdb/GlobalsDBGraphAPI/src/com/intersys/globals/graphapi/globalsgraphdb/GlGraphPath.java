package com.intersys.globals.graphapi.globalsgraphdb;

import com.intersys.globals.graphapi.GraphEdge;

import com.intersys.globals.graphapi.Graph;
import com.intersys.globals.graphapi.GraphException;
import com.intersys.globals.graphapi.GraphPath;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

/**GlGraphPath implements the Path interface for GlobalsDB graphs
 */
public class GlGraphPath extends Vector<GraphEdge<GlGraphNode>> implements GraphPath<com.intersys.globals.graphapi.globalsgraphdb.GlGraphNode> {

    /** Minor optimization, if we don't want to keep calculating weights, set this true.
     */
    boolean cacheTheWeights = false;

    /**What is the graph in which this path exists?
     */
    GlGraph inGraph = null;

    /** Cached total weight of the path
     */
    Long totalWeight = null;

    /** Allow public instantiation of empty path; note this is not allowed for Edges.
     */
    
    GlGraphComponent pathComp = null;
    
    public GlGraphPath(GraphEdge<GlGraphNode> initialEdge) {
        super();
        this.add(initialEdge);
    }

    /** Create a new path with this Edge in this Graph
     * @param inGraph graph in which the Path runs
     * @param initialEdge initial Edge for the path
     */
    public GlGraphPath(GlGraph inGraph, GraphEdge<GlGraphNode> initialEdge) {
        super();
        this.inGraph = inGraph;
        pathComp = new GlGraphComponent();
        pathComp.setParentGraph(inGraph);
        this.add(initialEdge);
        this.totalWeight = totalWeight;
    }

    @Override
    public Long getPathWeight() {
        if ((totalWeight == null) || (! cacheTheWeights)) {
            totalWeight = 0L;
            for (int pathIter = 0; pathIter < this.size(); pathIter++) {
                totalWeight += this.get(pathIter).getEdgeWeight();
            }
        }
        return totalWeight;
    }


    @Override
    public boolean hasEdge(GraphEdge<GlGraphNode> checkEdge) {
        throw GraphException.notYetImplemented;
    }

    /**Add the given Edge to the path.  Must be continuous with other edges already in path
     * @param edge Edge to add to the path
     * @return indicates if the edge was added successfully
     */
    @Override
    public boolean add(GraphEdge<GlGraphNode> edge) {
        throw GraphException.notYetImplemented;
    }

    /** DISALLOWED - Add edge at a specific index
     * @param i index at which to add
     * @param edge Edge to add
     */
    @Override
    public void add(int i, GraphEdge<GlGraphNode> edge) {
        throw GraphException.notYetImplemented;
    }

    /** DISALLOW: set the edge at a particular index
     * @param i Index at which to set the edge
     * @param edge Edge to put at the index
     * @return whether edge was added
     */
    @Override
    public GraphEdge<GlGraphNode> set(int i, GraphEdge<GlGraphNode> edge) {
        throw GraphException.notYetImplemented;
    }

    /** Because Path implements Edge, it should return a Weight which in this case is the aggregate path weight
     * @return aggregate weight of all paths
     */
    @Override
    public Long getEdgeWeight() {
        return getPathWeight();
    }

    /** DISALLOWED: must not set path weight!
     * @param newWeight weight for the path - not allowed to set
     */
    public void setEdgeWeight(long newWeight) {
        throw GraphException.notYetImplemented;
    }

    @Override
    public GlGraphNode getFrom() {
        throw GraphException.notYetImplemented;
    }

    @Override
    public GlGraphNode getTo() {
        throw GraphException.notYetImplemented;
    }

    @Override
    public boolean hasNode(GlGraphNode nodeToCheck) {
        throw GraphException.notYetImplemented;
    }

    @Override
    public Graph getGraph() {
        return inGraph;
    }

    @Override
    public String getCustomString(String propertyName) {
        return pathComp.getCustomString(propertyName);
    }

    @Override
    public void setCustomString(String propertyName, String newValue) {
        pathComp.setCustomString(propertyName, newValue);
    }

    @Override
    public void deleteCustomString(String propertyName) {
        pathComp.deleteCustomString(propertyName);
    }

    @Override
    public Set<String> nonemptyPropertyNames() {
        return pathComp.nonemptyPropertyNames();
    }

    @Override
    public String getLabel() {
        return pathComp.getLabel();
    }
}
