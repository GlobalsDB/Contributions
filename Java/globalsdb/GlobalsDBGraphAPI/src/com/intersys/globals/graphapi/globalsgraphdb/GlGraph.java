package com.intersys.globals.graphapi.globalsgraphdb;

import com.intersys.globals.Connection;
import com.intersys.globals.graphapi.GraphEdge;
import com.intersys.globals.ConnectionContext;

import com.intersys.globals.NodeReference;

import com.intersys.globals.graphapi.Graph;


import com.intersys.globals.graphapi.GraphDuplicateNodeLabelException;
import com.intersys.globals.graphapi.GraphException;

import com.intersys.globals.graphapi.GraphNode;
import com.intersys.globals.graphapi.NodesNotInGraphException;

import com.intersys.globals.impl.NodeReferenceImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**GlGraph is an implementation of the Graph package that uses the GlobalsDB free database as persistence.
 */
public class GlGraph extends java.util.HashSet<GlGraphNode> implements Graph<com.intersys.globals.graphapi.globalsgraphdb.GlGraphNode> {
    /** FIELDS IN CLASS  ************************************************************************/
    private GlDictionary graphDictionary = GlDictionary.GlobalsGraphAdmin;

    private Connection globalsConnection = null;
    private boolean addNodesWithEdges = false;
    private boolean graphIsDirected = true;

    String graphRootName = null;
    NodeReference graphRootNodeRef = null;

    /** the label map list is used to ensure we don't add nodes with duplicate labels
     */
    private Map<String, GlGraphNode> labelMapList = new HashMap<String, GlGraphNode>();
    private Map<edgelabel, GlGraphEdge> edgeSet = new HashMap<edgelabel, GlGraphEdge>();

    @Override
    public boolean removeEdge(GraphEdge edgeToRemov) {
        LG("Removing an edge: [" + edgeToRemov.getFrom().getLabel() + ", " + edgeToRemov.getTo().getLabel() + "]");
        edgeSet.remove(new edgelabel (edgeToRemov.getFrom().getLabel(), edgeToRemov.getTo().getLabel()));
        if (graphRootNodeRef != null) {
            graphRootNodeRef.setSubscriptCount(0);
            graphRootNodeRef.appendSubscript(getGrDct().getGL_EDGES_SUBSCRIPT().toString());
            graphRootNodeRef.kill(((GlGraphNode)edgeToRemov.getFrom()).idForGlNode.toString());
        }
        return true;
    }

    @Override
    public boolean remove(GraphNode grf2go) {
        LG("Removing node with label " + grf2go.getLabel());
        Iterator edgItr = grf2go.getEdges(this).iterator();
        while (edgItr.hasNext()) {
            removeEdge ((GlGraphEdge) edgItr.next());
        }
        this.labelMapList.remove(grf2go.getLabel());
        
        if (graphRootNodeRef != null) {
            graphRootNodeRef.setSubscriptCount(0);
            graphRootNodeRef.appendSubscript(getGrDct().getGL_NODES_SUBSCRIPT().toString());
            graphRootNodeRef.kill(((GlGraphNode)grf2go).idForGlNode);
        }
        return super.remove(grf2go);
    }

    @Override
    public String vizGraph() {
        StringBuffer theDot = new StringBuffer();
        int nodeCount = 0;
        theDot.append("digraph ").append(this.graphRootName).append(" {\n");
        Iterator<GlGraphNode> nodes = this.iterator();
        HashMap<String, String> nodeLabelMap = new HashMap<String, String>();
        while (nodes.hasNext()) {
            String nodePosMark = "Node" + (++nodeCount);
            String thisNdLabel = nodes.next().getLabel();
            nodeLabelMap.put(thisNdLabel, nodePosMark);
            theDot.append(nodePosMark).append(" [label=\"").append(thisNdLabel).append("\"]; \n");
        }
        
        Iterator<GlGraphEdge> edg8r = this.edgeSet.values().iterator();
        while (edg8r.hasNext()) {
            GlGraphEdge anotherDg = edg8r.next();
            theDot.append(nodeLabelMap.get(anotherDg.getFrom().getLabel())).append(" -> ");
            theDot.append(nodeLabelMap.get(anotherDg.getTo().getLabel())).append(";\n");
        }
        theDot.append("}\n");
        return theDot.toString();
    }

    /** embedded class implements 'equals' and 'hashcode' to support the hashmap
     */
    public class edgelabel extends Object {
        String from;
        String to;

        edgelabel(String a, String b) {
            from = a;
            to = b;
        }

        public boolean equals(Object other) {
            if (!(other instanceof edgelabel)) {
                return false;
            }
            edgelabel otherlbl = (edgelabel)other;
            boolean edgelabelcompare = ((otherlbl.from.equals(from)) && (otherlbl.to.equals(to)));
            LGZ("edgelabel 'equals' returning " + edgelabelcompare);
            return edgelabelcompare;
        }

        public int hashCode() {
            return GlGraph.this.hashCode();
        }
    }

    /** MEATY functional implementations including complex accessors **************************/


    /** This method is the key to persistence.  It uses the 'graph root name' as the name of a global for loading/persisting
     * the graph
     * @param toGraphRootName name of the global for this graph
     */
    void setGraphRootName(String toGraphRootName) {
        int noNameCount = 0;
        graphRootName = toGraphRootName;
        String newndNam = null;
        graphRootNodeRef = getGlobalsConnection().createNodeReference(graphRootName);
        if (graphRootNodeRef.hasSubnodes()) {
            UUID thisNodeID = null;
            LG("Found the root, value is " + graphRootNodeRef.getObject().toString());
            graphRootNodeRef.setSubscriptCount(0);
            graphRootNodeRef.appendSubscript(getGrDct().getGL_NODES_SUBSCRIPT().toString());
            String storedUUID = graphRootNodeRef.nextSubscript("");
            while (storedUUID.length() > 0) {
                LG("UUID (?) subscript is " + storedUUID);
                thisNodeID = UUID.fromString(storedUUID);
                GlGraphNode newNode = new GlGraphNode();
                newNode.setGraph(this);
                newNode.idForGlNode = UUID.fromString(storedUUID);
                Object newndNamObj = graphRootNodeRef.getObject( 
                        storedUUID,
                        getGrDct().getGL_NODENAME_SUBSCRIPT().toString()
                    );
                if (newndNamObj == null) {
                    throw new GraphException ("Got back a null node name!");
                } else {
                    newndNam = newndNamObj.toString();
                }
                if (labelMapList.containsKey(newndNam)) {
                    LGL(Level.WARNING, "Duplicate node label loaded!!! " + newndNam);
                } else {
                    newNode.setLabel(newndNam);
                    addInternal(newNode);
                }
                graphRootNodeRef.setSubscriptCount(0);
                graphRootNodeRef.appendSubscript(getGrDct().getGL_NODES_SUBSCRIPT().toString());
                storedUUID = graphRootNodeRef.nextSubscript(storedUUID);
            }
        } else {
            graphRootNodeRef.set(getGrDct().getGL_GRAPH_FLAG().toString());
            LG("Created new graph");
        }
    }

    @Override
    public void connectNodes(GlGraphNode fromThisNd, GlGraphNode toThatNd) {
        connectNodesEdge(fromThisNd, toThatNd, GlGraphEdge.DEFAULT_EDGE_WEIGHT);
    }

    private GlGraphEdge connectNodesEdge(GlGraphNode from, GlGraphNode to, long withWt) {
        LGZ("Connecting nodes no weight");
        edgelabel chkEdge = new edgelabel(from.getLabel(), to.getLabel());
        if (edgeSet.containsKey(chkEdge)) {
            LGL(Level.INFO, "Already have these nodes connected " + chkEdge.from + " " + chkEdge.to);
            return edgeSet.get(chkEdge);
        } else {
            GlGraphEdge newEdge = new GlGraphEdge(this, from, to);
            edgeSet.put(chkEdge, newEdge);
            if (graphRootNodeRef != null) {
                graphRootNodeRef.setSubscriptCount(0);
                graphRootNodeRef.appendSubscript(getGrDct().getGL_EDGES_SUBSCRIPT().toString());
                graphRootNodeRef.appendSubscript(from.idForGlNode.toString());
                graphRootNodeRef.appendSubscript(to.idForGlNode.toString());
                graphRootNodeRef.set(getGrDct().getGL_EDGE_FLAG().toString());
                graphRootNodeRef.appendSubscript(getGrDct().getGL_EDGEWEIGHT_SUBSCRIPT().toString());
                graphRootNodeRef.set(withWt);
                graphRootNodeRef.setSubscriptCount(0);
            }
            LGZ("edgeset now has size " + edgeSet.size());
            return newEdge;
        }
    }

    @Override
    public void connectNodes(GlGraphNode from, GlGraphNode to, long edgeWeight) {
        LGZ("Connecting nodes with weight " + edgeWeight);
        edgelabel chkEdge = new edgelabel(from.getLabel(), to.getLabel());
        if (edgeSet.containsKey(chkEdge)) {
            LGL(Level.FINE, "Already have these nodes connected " + chkEdge.from + " " + chkEdge.to);
        }
        GlGraphEdge newEdge = connectNodesEdge(from, to, GlGraphEdge.DEFAULT_EDGE_WEIGHT);
        newEdge.setEdgeWeight(edgeWeight);
        edgeSet.put(chkEdge, newEdge);

    }


    @Override
    public GlGraphNode newGraphNode(String withLabel) {
        LG("Creating new node with label " + withLabel);
        if (labelMapList.containsKey(withLabel)) {
            LGL(Level.WARNING, "A node with label '" + withLabel + "' already exists in this graph");
            return labelMapList.get(withLabel);
        } else {
            GlGraphNode newNode = new GlGraphNode(this, withLabel);
            this.add(newNode);
            return newNode;
        }
    }

    /** Determine what is the current connection in use for GlobalsDB.  Allocates new one from (static) ConnectionContext
     * if needed
     * @return GlobalsDB Connection
     */
    public Connection getGlobalsConnection() {
        if (this.globalsConnection == null) {
            GraphAPIEnv.setUp();
            this.globalsConnection = ConnectionContext.getConnection();
        }
        return globalsConnection;
    }

    private void setGlobalsConnection(Connection newCnct) {
        if ((this.globalsConnection != null) && (this.globalsConnection.isConnected())) {
            this.globalsConnection.close();
            // TODO: discard all existing nodes and edges?
            this.globalsConnection = null;
        }
        this.globalsConnection = newCnct;

        getGlobalsConnection();

        if (!globalsConnection.isConnected()) {
            globalsConnection.connect();
        }
    }
    
    @Override
    public boolean add(GlGraphNode newGlGrNd) {
        if (newGlGrNd.getGraph() != this) {
            throw new NodesNotInGraphException(this, newGlGrNd);
        }
        if ((newGlGrNd.getLabel() == null) || (newGlGrNd.getLabel().length() < 1)) {
            throw new GraphException("Cannot add an unlabeled node!");
        }
        if (labelMapList.containsKey(newGlGrNd.getLabel())) {
            throw new GraphDuplicateNodeLabelException ("Duplicate node label " + newGlGrNd.getLabel());
        }
        return addInternal(newGlGrNd);
    }
    
    boolean addInternal(GlGraphNode newGlNtrnlNd) {
        if (newGlNtrnlNd.getLabel() == null) {
            throw new GraphException ("how did we get to 'internal' without a node label?");
        }
        
        labelMapList.put(newGlNtrnlNd.getLabel(), newGlNtrnlNd);

        LGZ("add(GlGraphNode) Delegating 'add' upstairs");

        boolean reslt = super.add(newGlNtrnlNd);

        LG("Graph added(" + reslt + ") new node with label " + newGlNtrnlNd.getLabel() + ", size now " + size());

        if (graphRootNodeRef != null) {
            graphRootNodeRef.setSubscriptCount(0);
            graphRootNodeRef.appendSubscript(getGrDct().getGL_NODES_SUBSCRIPT().toString());
            graphRootNodeRef.appendSubscript(    newGlNtrnlNd.idForGlNode.toString()
 );
            graphRootNodeRef.set(
                getGrDct().getGL_NODE_FLAG().toString() // flag the global node loc as a graph node
                );
            graphRootNodeRef.appendSubscript(getGrDct().getGL_NODENAME_SUBSCRIPT().toString());
            graphRootNodeRef.set(newGlNtrnlNd.getLabel());
            graphRootNodeRef.setSubscriptCount(0);
            LG("Added information to node reference for added node " + newGlNtrnlNd.getLabel());
        } else {
            LG("Graph has no root node reference - subscripts etc. not added");
        }

        return reslt;
    }


    /** CONSTRUCTORS **************************************************************************/

    public GlGraph(Connection cnctForGraph, String withGlobalName, boolean addNodesIfAbsent, GlDictionary wDict) {
        setAddNodesWithEdges(addNodesIfAbsent);
        LG("New graph, addnodes is " + addNodesIfAbsent);
        setGlobalsConnection(cnctForGraph);
        // TODO: check for the default subscript
        // use the node string to create a nodereference and instantiate globalsdbnode with it
        // add the globalsdbnode to the graph

        LGZ("Node string name is " + withGlobalName);
        setGraphRootName(withGlobalName);

    }


    /** Constructor accepts a dictionary of markers for the graph name
     * @param dictForGraph configuration within GlobalsDB for the subscripts and flags for this graph
     */
    GlGraph(GlDictionary dictForGraph) {
        super();
        graphDictionary = dictForGraph;
    }


    /** Simple constructor is package private
     */
    public GlGraph() {
        super();
    }

    /** Instantiates a graph using a specific back-end connection
     * @param connForGlobals live connection to a GlobalsDB database
     */
    public GlGraph(Connection connForGlobals) {
        setGlobalsConnection(connForGlobals);
    }

    public GlGraph(Connection cnctForGraph, String withGlobalName) {
        this(cnctForGraph, withGlobalName, true);
    }

    public GlGraph(Connection cnctForGraph, String withGlobalName, boolean addNodesIfAbsent) {
        this(cnctForGraph, withGlobalName, addNodesIfAbsent, GlDictionary.GlobalsGraphAdmin);
    }


    /** SIMPLE accessors **********************************************************************/
    @Override
    public boolean areConnected(GlGraphNode nodeX, GlGraphNode nodeY) {
        edgelabel edgeKey = new edgelabel(nodeX.getLabel(), nodeY.getLabel());
        return edgeSet.containsKey(edgeKey);
    }


    @Override
    public Collection getEdges() {
        return ((Collection<GlGraphEdge>)edgeSet.values());
    }

    @Override
    public boolean isAddNodesWithEdges() {
        return addNodesWithEdges;
    }


    @Override
    public boolean isDirected() {
        return graphIsDirected;
    }

    public NodeReference getGraphRootNodeRef() {
        return graphRootNodeRef;
    }

    @Override
    public Collection getEdgeConnections(GlGraphNode ofNode) {
        return edgeSet.values();
    }

    public GlobalsNodeSubscript getGraphSubscript() {
        return graphDictionary.getGL_NODES_SUBSCRIPT();
    }


    public void setAddNodesWithEdges(boolean addNodesWithEdges) {
        this.addNodesWithEdges = addNodesWithEdges;
    }


    void setGraphDictionary(GlDictionary graphDictionary) {
        this.graphDictionary = graphDictionary;
    }

    GlDictionary getGrDct() {
        return graphDictionary;
    }

    @Override
    public String getName() {
        return "Graph under global named " + graphRootName;
    }


    /** Utility and ancillary functions after this *********************************************/

    Logger lgGlGraph = null; // Logger generated Jul 20, 2012

    private void LGL(Level forLvl, String msg) {
        if (lgGlGraph == null) {
            lgGlGraph = Logger.getLogger(getClass().getName());
            lgGlGraph.setLevel(Level.INFO);
        }
        lgGlGraph.log(forLvl, msg);
    }


    private void LG(String iMsg) {
        LGL(Level.INFO, iMsg);
    }

    private void LGZ(String zMsg) {
        LGL(Level.FINER, zMsg);
    }


    public void printLabels(Level atLevel) {
        int nodeCounter = 0;
        Iterator<GlGraphNode> iter = iterator();
        while (iter.hasNext()) {
            LGL(atLevel, ++nodeCounter + ": printlabel - Node member has label " + iter.next().getLabel());
        }
        LGL(atLevel, "Graph printed out labels for members numbering " + nodeCounter);
    }


}
