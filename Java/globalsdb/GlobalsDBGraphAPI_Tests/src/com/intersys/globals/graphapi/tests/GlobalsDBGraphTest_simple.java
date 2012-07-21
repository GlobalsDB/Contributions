package com.intersys.globals.graphapi.tests;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersys.globals.NodeReference;
import com.intersys.globals.graphapi.Graph;
import com.intersys.globals.graphapi.GraphNode;
import com.intersys.globals.graphapi.globalsgraphdb.GlGraph;

import com.intersys.globals.graphapi.globalsgraphdb.GlGraphNode;

import com.intersys.globals.graphapi.globalsgraphdb.GraphAPIEnv;

import java.util.Iterator;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import com.intersys.globals.graphapi.GraphDuplicateNodeLabelException;

import java.util.Random;

import org.junit.Before;

public class GlobalsDBGraphTest_simple {
    public static final String TESTMASTERNODE = "tstGraphSimpleD21H0252";



    com.intersys.globals.graphapi.Graph newGraph = null;
    Connection tstConn = null;

    public GlobalsDBGraphTest_simple() {
    }

    @Before
    public void setUpConn(){
            tstConn = ConnectionContext.getConnection();

            if (!tstConn.isConnected()) {
                tstConn.connect();
            }
    }

    @Test
    public void DupeDupe(){
        loadAndDupeTest();
        loadAndDupeTest();
    }

    /**Very simple test mimicking expected call sequence
     */
    @Test
    public void loadAndDupeTest () {
        LG("Beginning 'loadAndDupeTest' test - class " + getClass().getName());

        GlGraph newGraph = new GlGraph(tstConn, TESTMASTERNODE, true);
        
        LG("Trying to add some pieces to the graph");
        
        tryToAddSome(newGraph);
        
        GraphAPIEnv.printAllSubscripts(newGraph.getGraphRootNodeRef());
        
        newGraph = null;
    }
    
    public void tryToAddSome(Graph newGraph) {
        GraphNode grNdLarry = newGraph.newGraphNode("larry");
        GraphNode grNdBob = newGraph.newGraphNode("bob");
        LG("Trying to connect Larry and Bob");
        newGraph.connectNodes(grNdLarry, grNdBob);
    }
    
    @Test
    public void simpleNewGraphTest() {
        String newGlNam = BackgroundTestData.rndString();
        LG("Beginning simple new graph test with global name " + newGlNam);
        GlGraph grfForNew = new GlGraph(tstConn, newGlNam);        
        tryToAdd(grfForNew, true);
        
        GlGraph altGrf = new GlGraph(tstConn, newGlNam);   
        GraphAPIEnv.printAllSubscripts(altGrf.getGraphRootNodeRef());
    }
        
    void tryToAdd(GlGraph inThisGLGraph, boolean squashDupExc){
        
        Graph inThisGraph = (Graph) inThisGLGraph;

        Integer nodesInGraph = inThisGraph.size();

        LG("Graph size is: " + nodesInGraph);

        Iterator<NodeReference> iterNodes = inThisGraph.iterator();

        GraphAPIEnv.printAllSubscripts(((GlGraph)inThisGraph).getGraphRootNodeRef());
        
        GraphNode phredNd =  inThisGraph.newGraphNode("phred");

        assertTrue("First node should be contained in graph", inThisGraph.contains(phredNd));

        LGZ("newgraphnode-phred Graph size is: " + inThisGraph.size());

        GraphNode secondNd = null;
        
        assertFalse("Uninitialized second node should NOT be contained in graph", inThisGraph.contains(secondNd));
        
        LGZ("null second node - Graph size is: " + inThisGraph.size());

        secondNd = inThisGraph.newGraphNode("rndJul 20, 2012 - 11.23.53 AM");

        LG("second node rndjul created, Graph size is: " + inThisGraph.size());

        assertFalse("Nodes should not be connected yet", inThisGraph.areConnected(phredNd, secondNd));

        try {
            inThisGraph.add(phredNd);
            } catch (RuntimeException e) {
                if (squashDupExc) {
                    assertTrue(
                        "Looking for a GraphDuplicateNodeLabelException",
                        GraphDuplicateNodeLabelException.class.isAssignableFrom(e.getClass()));
                } else {
                    LGL(Level.WARNING, "re-adding(l 145) phredNd raised an exception other than dup label");
                    throw e;
                }
            }
        
        LGZ("added phred to graph, Graph size is: " + inThisGraph.size());
        
        inThisGraph.printLabels(Level.FINE);

        try {
            inThisGraph.add(secondNd);
        } catch (RuntimeException e) {
            if (squashDupExc) {
                assertTrue(
                    "Looking for a GraphDuplicateNodeLabelException",
                    GraphDuplicateNodeLabelException.class.isAssignableFrom(e.getClass()));
            } else {
                LGL(Level.WARNING, "re-adding(l 161) secondNd raised an exception other than dup label");
                throw e;
            }
        }
        
        LGZ("added secondnd to graph, Graph size is: " + inThisGraph.size());
        inThisGraph.printLabels(Level.FINE);

        inThisGraph.newGraphNode(secondNd.getLabel());

        phredNd.Connect(secondNd);
        
        assertFalse ("Two nodes with different labels shouldn't be equal!", phredNd.equals(secondNd));
        
        LGZ("phred connect to second (A) Graph size is: " + inThisGraph.size());
        inThisGraph.printLabels(Level.FINE);


        phredNd.Connect(secondNd);

        LG("phred connect to second (B) Graph size is: " + inThisGraph.size());
        inThisGraph.printLabels(Level.FINE);

        phredNd.Connect(secondNd);
        phredNd.Connect(secondNd);
        phredNd.Connect(secondNd);

        LG("repeated connects of same node to phred are finished");

        assertTrue ("Connect status from phredNd to secondNd should be true", inThisGraph.areConnected(phredNd, secondNd));
        
        assertTrue("Added node should be contained in graph", inThisGraph.contains(phredNd));
        
        assertEquals("We've added two nodes, the graph size should be two", 2, inThisGraph.size());
    }


    public static void main(String[] args) {
        String[] args2 = { GlobalsDBGraphTest_simple.class.getName() };
        JUnitCore.main(args2);
    }

    protected void finalize() throws Throwable {
        newGraph = null;
        if ((tstConn != null) && (tstConn.isConnected())) {
            tstConn.close();
        }
        tstConn = null;
        LG("Test teardown complete, test class is " + getClass().getName());
        super.finalize();
    }


    Logger lgGlobalsDBGraphTest_simple = null;  // Logger generated Jul 20, 2012

    private void LGL(Level forLvl, String msg) { 
        if (lgGlobalsDBGraphTest_simple == null) { 
            lgGlobalsDBGraphTest_simple = Logger.getLogger (getClass().getName());
            lgGlobalsDBGraphTest_simple.setLevel(Level.INFO); 
        }
         lgGlobalsDBGraphTest_simple.log(forLvl, msg); 
    }

    private void LG(String iMsg) { LGL (Level.INFO, iMsg); }

    private void LGZ(String zMsg) { LGL (Level.FINER, zMsg); }

}
