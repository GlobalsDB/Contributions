package com.intersys.globals.graphapi.globalsgraphdb;

import com.intersys.globals.ValueList;
import com.intersys.globals.impl.NodeReferenceImpl;
import com.intersys.globals.NodeReference;

import com.intersys.globals.graphapi.Graph;
import com.intersys.globals.graphapi.GraphEdge;
import com.intersys.globals.graphapi.GraphException;
import com.intersys.globals.graphapi.GraphNode;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Implements a Graph node by embedding the GlobalsDB type 'NodeReferenceImpl'
 * Note that other GraphComponent implementors extend the GlGraphComponent directly; this will not work
 * for GlGraphNode because its GraphNode interface (Collection) conflicts with the Map interface used 
 * by GlGraphComponent.
 */
public class GlGraphNode extends NodeReferenceImpl implements GraphNode<NodeReference>, NodeReference {
    
    private NodeReference contents = null;
    // CANNOT extend GlGraphComponent and embed a NodeReferenceImpl because

    UUID idForGlNode = null;
    
    Logger lgGlGraphNode = null;  // Logger generated Jul 20, 2012

    private void LGL(Level forLvl, String msg) { 
        if (lgGlGraphNode == null) { 
            lgGlGraphNode = Logger.getLogger (getClass().getName());
            lgGlGraphNode.setLevel(Level.INFO); 
        }
         lgGlGraphNode.log(forLvl, msg); 
    }

    private void LG(String iMsg) { LGL (Level.INFO, iMsg); }

    private void LGZ(String zMsg) { LGL (Level.FINER, zMsg); }
    
    GlGraphComponent glcomp = new GlGraphComponent();

    /** Default constructor is package-private, not intended to be used outside.
     */
    GlGraphNode() {
        super();
    }

    /** Build a new node from a nodeReference
     * @param graphForNewNode graph in which to get the node
     * @param contents Node contents handle
     */
    GlGraphNode(GlGraph graphForNewNode, NodeReferenceImpl contents) {
        setGraph(graphForNewNode);
        this.contents = contents;
    }

    /** Simple constructor extends 
     * @param graph4nd
     * @param newNdLabel
     */
    GlGraphNode(GlGraph graph4nd, String newNdLabel) {
        super();
        glcomp.parentGraph = graph4nd;
        glcomp.label = newNdLabel;
        idForGlNode = UUID.randomUUID();
    }

    @Override
    public void Connect(GraphNode toNode) {
        LGZ("Connecting this node " + getLabel() + " to " + toNode.getLabel());
        glcomp.parentGraph.connectNodes(this, (GlGraphNode)toNode);
    }


    @Override
    public void Connect(GraphNode toNode, Long atWeight) {
        LGZ("Connecting this node " + getLabel() + " to " + toNode.getLabel() + " with weight " + atWeight);
        glcomp.parentGraph.connectNodes(this, (GlGraphNode)toNode, atWeight);
    }

    @Override
    public Graph getGraph() {
        return glcomp.parentGraph;
    }

    void setLabel(String newNdLabel) {
        glcomp.setLabel(newNdLabel);
    }

    void setGraph(GlGraph toThisGraph) {
        if (toThisGraph == glcomp.parentGraph) {
            // no problem
        } else if (glcomp.parentGraph != null) {
            throw new GraphException("Cannot replace the graph of an existing " + getClass().getName());
        } else {
            setParentGraph(toThisGraph);
        }

    }

    NodeReference thisGDBNode() {
        if (contents != null) {
            return contents;
        } else {
            return this;
        }
    }
    
    NodeReferenceImpl thisGDBNodeIm() {
        if (contents == null) {
            throw new GraphException ("Can't get NodeReference from contents, it is null");
        } else if (NodeReferenceImpl.class.isAssignableFrom(contents.getClass())) {
            return (NodeReferenceImpl) contents;
        } else {
return this;
        }
    }



    @Override
    public NodeReferenceImpl getValue() {
        return thisGDBNodeIm();
    }

    public String getName() {
        return thisGDBNode().getName();
    }

    public void acquireLock(int i, int i2) {
        thisGDBNode().acquireLock(i, i2);
    }

    public void acquireLock(int i, int i2, Object... object) {
        thisGDBNode().acquireLock(i, i2, object);
    }

    public void appendSubscript(double d) {
        thisGDBNode().appendSubscript(d);
    }

    public void appendSubscript(int i) {
        thisGDBNode().appendSubscript(i);
    }

    public void appendSubscript(long l) {
        thisGDBNode().appendSubscript(l);
    }

    public void appendSubscript(String string) {
        thisGDBNode().appendSubscript(string);
    }

    public void close() {
        thisGDBNode().close();
    }

    public boolean exists() {
        return thisGDBNode().exists();
    }

    public boolean exists(Object... object) {
        return thisGDBNode().exists(object);
    }

    public byte[] getBytes() {
        return thisGDBNode().getBytes();
    }

    public byte[] getBytes(Object... object) {
        return thisGDBNode().getBytes(object);
    }

    public double getDouble() {
        return thisGDBNode().getDouble();
    }

    public double getDouble(Object... object) {
        return thisGDBNode().getDouble(object);
    }

    public double getDoubleSubscript(int i) {
        return thisGDBNode().getDoubleSubscript(i);
    }

    public int getInt() {
        return thisGDBNode().getInt();
    }

    public int getInt(Object... object) {
        return thisGDBNode().getInt(object);
    }

    public int getIntSubscript(int i) {
        return thisGDBNode().getIntSubscript(i);
    }

    public ValueList getList() {
        return thisGDBNode().getList();
    }

    public ValueList getList(Object... object) {
        return thisGDBNode().getList(object);
    }

    public ValueList getList(ValueList valueList) {
        return thisGDBNode().getList(valueList);
    }

    public ValueList getList(ValueList valueList, Object... object) {
        return thisGDBNode().getList(valueList, object);
    }

    public long getLong() {
        return thisGDBNode().getLong();
    }

    public long getLong(Object... object) {
        return thisGDBNode().getLong(object);
    }

    public long getLongSubscript(int i) {
        return thisGDBNode().getLongSubscript(i);
    }

    public Object getObject() {
        return thisGDBNode().getObject();
    }

    public Object getObject(Object... object) {
        if (this.contents == null) {
            LGL(Level.INFO, "trying to get object from super for " + object.toString());
            return super.getObject(object);
        } else {
        LGL(Level.INFO, "trying to get object from embedded noderef for " + object.toString());
        return contents.getObject(object);
    }}

    public Object getObjectSubscript(int i) {
        return thisGDBNode().getObjectSubscript(i);
    }

    public int getOption(int i) {
        return thisGDBNode().getOption(i);
    }

    public String getString() {
        return thisGDBNode().getString();
    }

    public String getString(Object... object) {
        return thisGDBNode().getString(object);
    }

    public String getStringSubscript(int i) {
        return thisGDBNode().getStringSubscript(i);
    }

    public int getSubscriptCount() {
        return thisGDBNode().getSubscriptCount();
    }

    public boolean hasSubnodes() {
        return thisGDBNode().hasSubnodes();
    }

    public boolean hasSubnodes(Object... object) {
        return thisGDBNode().hasSubnodes(object);
    }

    public long increment(int i) {
        return thisGDBNode().increment(i);
    }

    public long increment(int i, Object... object) {
        return thisGDBNode().increment(i, object);
    }

    public void kill() {
        thisGDBNode().kill();
    }

    public void kill(Object... object) {
        thisGDBNode().kill(object);
    }

    public void killNode() {
        thisGDBNode().killNode();
    }

    public void killNode(Object... object) {
        thisGDBNode().killNode(object);
    }

    public String nextSubscript() {
        return thisGDBNode().nextSubscript();
    }

    public String nextSubscript(Object... object) {
        return thisGDBNode().nextSubscript(object);
    }

    public String previousSubscript() {
        return thisGDBNode().previousSubscript();
    }

    public String previousSubscript(Object... object) {
        return thisGDBNode().previousSubscript(object);
    }

    public void releaseLock(int i, int i2) {
        thisGDBNode().releaseLock(i, i2);
    }

    public void releaseLock(int i, int i2, Object... object) {
        thisGDBNode().releaseLock(i, i2, object);
    }

    public void set(byte[] b) {
        thisGDBNode().set(b);
    }

    public void set(byte[] b, Object... object) {
        thisGDBNode().set(b, object);
    }

    public void set(double d) {
        thisGDBNode().set(d);
    }

    public void set(double d, Object... object) {
        thisGDBNode().set(d, object);
    }

    public void set(int i) {
        thisGDBNode().set(i);
    }

    public void set(int i, Object... object) {
        thisGDBNode().set(i, object);
    }

    public void set(long l) {
        thisGDBNode().set(l);
    }

    public void set(long l, Object... object) {
        thisGDBNode().set(l, object);
    }

    public void set(String string) {
        thisGDBNode().set(string);
    }

    public void set(String string, Object... object) {
        thisGDBNode().set(string, object);
    }

    public void set(ValueList valueList) {
        thisGDBNode().set(valueList);
    }

    public void set(ValueList valueList, Object... object) {
        thisGDBNode().set(valueList, object);
    }

    public void setName(String string) {
        thisGDBNode().setName(string);
    }

    public void setOption(int i, int i2) {
        thisGDBNode().setOption(i, i2);
    }

    public void setSubscript(int i, double d) {
        thisGDBNode().setSubscript(i, d);
    }

    public void setSubscript(int i, int i2) {
        thisGDBNode().setSubscript(i, i2);
    }

    public void setSubscript(int i, long l) {
        thisGDBNode().setSubscript(i, l);
    }

    public void setSubscript(int i, String string) {
        thisGDBNode().setSubscript(i, string);
    }

    public void setSubscriptCount(int i) {
        thisGDBNode().setSubscriptCount(i);
    }


    private void setParentGraph(GlGraph glGraph) {
        glcomp.setParentGraph(glGraph);
    }

    @Override
    public Collection<GraphEdge> getEdges(Graph inGraph) {
        return glcomp.parentGraph.getEdgeConnections(this);
    }

    @Override
    public String getCustomString(String propertyName) {
        return glcomp.getCustomString(propertyName);
    }

    @Override
    public void setCustomString(String propertyName, String newValue) {
        glcomp.setCustomString(propertyName, newValue);
    }

    @Override
    public void deleteCustomString(String propertyName) {
        glcomp.deleteCustomString(propertyName);
    }

    @Override
    public Set<String> nonemptyPropertyNames() {
        return glcomp.nonemptyPropertyNames();
    }

    @Override
    public String getLabel() {
        return glcomp.getLabel();
    }
}
