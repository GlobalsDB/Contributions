package com.intersys.globals.graphapi.tests;

import com.intersys.globals.Connection;

import com.intersys.globals.ConnectionContext;

import com.intersys.globals.NodeReference;
import com.intersys.globals.graphapi.Graph;

import com.intersys.globals.graphapi.GraphNode;
import com.intersys.globals.graphapi.globalsgraphdb.GlGraph;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class GlGraphTest_GraphVizOutput {
    public GlGraphTest_GraphVizOutput() {
    }

    public static void main(String[] args) {
        String[] args2 = { GlGraphTest_GraphVizOutput.class.getName() };
        JUnitCore.main(args2);
    }

    private Connection vizConn = null;
    private Graph tstThisGraph = null;
    private String grphGlName = null;

    @Before
    public void setUp() throws Exception {
        grphGlName = BackgroundTestData.rndString();
        vizConn = ConnectionContext.getConnection();
        if (! vizConn.isConnected()) {
            vizConn.connect();
        }
        tstThisGraph = new GlGraph(vizConn, grphGlName);
        
        GraphNode echo = tstThisGraph.newGraphNode("Echo");
        GraphNode foxtrot = tstThisGraph.newGraphNode("FoxTrot");
        GraphNode golf = tstThisGraph.newGraphNode("Golf");
        
        echo.Connect(foxtrot);
    }

    @After
    public void tearDown() throws Exception {
        grphGlName = null;
        NodeReference n2k = ((GlGraph)tstThisGraph).getGraphRootNodeRef();
        n2k.setSubscriptCount(0);
        n2k.kill();
        tstThisGraph = null;
        n2k = null;
        if (vizConn.isConnected()) {
            vizConn.close();
        }
        vizConn = null;
    }

    /**
     * @see com.intersys.globals.graphapi.Graph#vizGraph()
     */
    @Test
    public void testVizGraph() {        
        System.out.println(tstThisGraph.vizGraph());
    }
}
