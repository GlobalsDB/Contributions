package com.intersys.globals.graphapi.globalsgraphdb;

import java.util.logging.Level;
import java.util.logging.Logger;

/** presents a set of parameters for mapping a graph into a GlobalsDB database
 */
public class GlDictionary {
    public GlDictionary() {
        super();
    }

    Logger grNodeLgGlDictionary = null;

    private void LG(String msg) {
        if (grNodeLgGlDictionary == null) {
            grNodeLgGlDictionary = Logger.getLogger(getClass().getName());
            grNodeLgGlDictionary.setLevel(Level.INFO);
        }
        grNodeLgGlDictionary.log(Level.INFO, msg);
    }

    /** GlobalsGraphAdmin implements the default mapping to GlobalsDB
     */
    public static GlDictionary GlobalsGraphAdmin =
        new GlDictionary(
            new GlFlag("GlGraph"), 
            new GlFlag("GlNode"), 
            new GlFlag("GlEdge"),
            new StringGlobalsSubscript("nodes"), 
            new StringGlobalsSubscript("edges"),
            new StringGlobalsSubscript("name"),
            new StringGlobalsSubscript("weight")
            );
 
        GlFlag GL_GRAPH_FLAG = null;
        GlFlag GL_NODE_FLAG = null;
        GlFlag GL_EDGE_FLAG = null;
        StringGlobalsSubscript GL_NODES_SUBSCRIPT = null;
        StringGlobalsSubscript GL_EDGES_SUBSCRIPT = null;
        StringGlobalsSubscript GL_NODENAME_SUBSCRIPT = null;
        StringGlobalsSubscript GL_EDGEWEIGHT_SUBSCRIPT = null;
 
    /**totally non-default constructor
     * @param newGL_GRAPH_FLAG
     * @param newGL_NODE_FLAG
     * @param newGL_EDGE_FLAG
     * @param newGL_NODES_SUBSCRIPT
     * @param newGL_EDGES_SUBSCRIPT
     * @param newGL_NODENAME_SUBSCRIPT
     * @param newGL_EDGEWEIGHT_SUBSCRIPT
     */
    public GlDictionary (
        GlFlag newGL_GRAPH_FLAG, 
        GlFlag newGL_NODE_FLAG, 
        GlFlag newGL_EDGE_FLAG,
        StringGlobalsSubscript newGL_NODES_SUBSCRIPT,
        StringGlobalsSubscript newGL_EDGES_SUBSCRIPT, 
        StringGlobalsSubscript newGL_NODENAME_SUBSCRIPT,
        StringGlobalsSubscript newGL_EDGEWEIGHT_SUBSCRIPT
        )
    {
        super();
        GL_GRAPH_FLAG           = newGL_GRAPH_FLAG;
        GL_NODE_FLAG            = newGL_NODE_FLAG;
        GL_EDGE_FLAG            = newGL_EDGE_FLAG;
        GL_NODES_SUBSCRIPT      = newGL_NODES_SUBSCRIPT;
        GL_EDGES_SUBSCRIPT      = newGL_EDGES_SUBSCRIPT;
        GL_NODENAME_SUBSCRIPT   = newGL_NODENAME_SUBSCRIPT;
        GL_EDGEWEIGHT_SUBSCRIPT = newGL_EDGEWEIGHT_SUBSCRIPT;
    }

    /** set the flag value used to identify a graph
     * @param newGL_GRAPH_FLAG new graph flag
     */
    public void setGL_GRAPH_FLAG(GlFlag newGL_GRAPH_FLAG) {
        this.GL_GRAPH_FLAG = newGL_GRAPH_FLAG;
    }

    /** retrieve the flag identifying a graph
     * @return the flag for graph
     */
    public GlFlag getGL_GRAPH_FLAG() {
        return GL_GRAPH_FLAG;
    }

    /** set the flag value used to identify a graph node
     * @param newGL_NODE_FLAG
     */
    public void setGL_NODE_FLAG(GlFlag newGL_NODE_FLAG) {
        this.GL_NODE_FLAG = newGL_NODE_FLAG;
    }

    /** Get the flag for nodes
     * @return the flag value used in this dictionary for identifying nodes
     */
    public GlFlag getGL_NODE_FLAG() {
        return GL_NODE_FLAG;
    }

    /** set the flag used to identify Edge objects
     * @param newGL_EDGE_FLAG new flag value for Edges
     */
    public void setGL_EDGE_FLAG(GlFlag newGL_EDGE_FLAG) {
        this.GL_EDGE_FLAG = newGL_EDGE_FLAG;
    }

    /** get the edge flag
     * @return the string used to identify an object as an Edge
     */
    public GlFlag getGL_EDGE_FLAG() {
        return GL_EDGE_FLAG;
    }

    /** Set the subscript used to collect edges in the graph
     * @param newGL_EDGES_SUBSCRIPT new value for edge subscript
     */
    public void setGL_EDGES_SUBSCRIPT(StringGlobalsSubscript newGL_EDGES_SUBSCRIPT) {
        this.GL_EDGES_SUBSCRIPT = newGL_EDGES_SUBSCRIPT;
    }

    /** get the edge subscript
     * @return string value currently used as a subscript for collecting edges
     */
    public StringGlobalsSubscript getGL_EDGES_SUBSCRIPT() {
        return GL_EDGES_SUBSCRIPT;
    }

    /** Set the subscript string to be used for the collection of graph nodes
     * @param newGL_NODES_SUBSCRIPT new value for nodes subscript
     */
    public void setGL_NODES_SUBSCRIPT(StringGlobalsSubscript newGL_NODES_SUBSCRIPT) {
        this.GL_NODES_SUBSCRIPT = newGL_NODES_SUBSCRIPT;
    }

    /** retrieve the subscript used to mark the nodes
     * @return subscript used for collecting nodes in a graph
     */
    public StringGlobalsSubscript getGL_NODES_SUBSCRIPT() {
        return GL_NODES_SUBSCRIPT;
    }

    public StringGlobalsSubscript getGL_NODENAME_SUBSCRIPT() {
        return GL_NODENAME_SUBSCRIPT;
    }

    public StringGlobalsSubscript getGL_EDGEWEIGHT_SUBSCRIPT() {
        return GL_EDGEWEIGHT_SUBSCRIPT;
    }
}
