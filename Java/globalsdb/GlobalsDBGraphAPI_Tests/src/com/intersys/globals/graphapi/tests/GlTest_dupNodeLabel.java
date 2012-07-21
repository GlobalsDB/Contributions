package com.intersys.globals.graphapi.tests;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersys.globals.graphapi.Graph;

import com.intersys.globals.graphapi.GraphException;
import com.intersys.globals.graphapi.globalsgraphdb.GlGraph;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class GlTest_dupNodeLabel {
        Logger lgGlTest_dupNodeLabel = null;
        
    private void LG(String msg) { 
        if (lgGlTest_dupNodeLabel == null) { 
            lgGlTest_dupNodeLabel = Logger.getLogger (getClass().getName());
            lgGlTest_dupNodeLabel.setLevel(Level.INFO); 
        }
         lgGlTest_dupNodeLabel.log(Level.INFO, msg); 
    }
    
    public GlTest_dupNodeLabel() {
    }

    public static void main(String[] args) {
        String[] args2 = { GlTest_dupNodeLabel.class.getName() };
        JUnitCore.main(args2);
    }

    Graph tstGraph = null;
    Connection tstCaseConn = null;
    public static final String globalnmGlTest_dupNodeLabel = "graphapi.tests.dupNodeLabel";
    
    public String multiLabel = "LabelToOverload";

    @Before
    public void setUp() throws Exception {
        tstCaseConn = ConnectionContext.getConnection();
        if (! tstCaseConn.isConnected()) {
            tstCaseConn.connect();
        }
        tstGraph = new GlGraph(tstCaseConn, globalnmGlTest_dupNodeLabel);
    }

    @After
    public void tearDown() throws Exception {
        tstGraph = null;
        if (tstCaseConn != null) {
            if (tstCaseConn.isConnected()) {
                tstCaseConn.close();
            }
            tstCaseConn = null;
        }
    }

    /** Make sure that the graph will reject two nodes with the same label
     * @see Graph#newGraphNode(String)
     */
    @Test(expected = GraphException.class)
    public void testDuplicateGraphNode () {
        LG ("Starting duplicate-node test");
        tstGraph.newGraphNode(multiLabel);
        tstGraph.newGraphNode(multiLabel);
    }
}
