package com.intersys.globals.graphapi.tests;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersys.globals.NodeReference;
import com.intersys.globals.graphapi.GraphEdge;
import com.intersys.globals.graphapi.GraphNode;
import com.intersys.globals.graphapi.globalsgraphdb.GlGraph;


import java.util.Collection;

import java.util.Iterator;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class GraphTest_RemoveNodes {
    public GraphTest_RemoveNodes() {
    }

    public static void main(String[] args) {
        String[] args2 = { GraphTest_RemoveNodes.class.getName() };
        JUnitCore.main(args2);
    }

    private GlGraph grfToTest = null;
    private String globlToTest = BackgroundTestData.rndString();

    @Before
    public void setUp() throws Exception {
        Connection nuCnn = ConnectionContext.getConnection();
        if (!nuCnn.isConnected()) {
            nuCnn.connect();
        }
        grfToTest = new GlGraph(nuCnn, globlToTest);
    }

    @Test
    public void NodeRemoveTest() {
        GraphNode anode = grfToTest.newGraphNode("aaa");
        GraphNode bnode = grfToTest.newGraphNode("bbbb");
        GraphNode cnode = grfToTest.newGraphNode("cc");
        bnode.Connect(bnode);
        bnode.Connect(anode);
        cnode.Connect(anode);
        Collection<GraphEdge> edGes = grfToTest.getEdges();
        Iterator<GraphEdge> egIt = edGes.iterator();
        if (egIt.hasNext()) {
            ((com.intersys.globals.graphapi.Graph)grfToTest).removeEdge(egIt.next());
        }
    }

    @After
    public void tearDown() throws Exception {
        NodeReference getNdRef = grfToTest.getGraphRootNodeRef();
        Connection extConn = grfToTest.getGlobalsConnection();
        grfToTest = null;
        getNdRef.setSubscriptCount(0);
        getNdRef.kill();
        getNdRef = null;
        extConn.close();

    }
}
