package com.intersys.globals.graphapi.tests;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import com.intersys.globals.graphapi.globalsgraphdb.GlGraph;
import com.intersys.globals.graphapi.Graph;
import com.intersys.globals.graphapi.globalsgraphdb.GraphAPIEnv;


public class GlobalsDBGraphTest_getconn {
    public GlobalsDBGraphTest_getconn() {
    }
    
    public static final long SLEEP_BY_SECONDS = 1000;
    
    long sleepToCheckProcess = 0;

    public static void main(String[] args) {
        String[] args2 = { GlobalsDBGraphTest_getconn.class.getName() };
        JUnitCore.main(args2);
    }

    @Test
    public void testGetGlobalsConnection() {
        System.out.println ("hello out there");

        GraphAPIEnv.setUp();
        
        Connection glbConn = ConnectionContext.getConnection();
        assertNotNull("Connection must not be null", glbConn);
        
        if (! glbConn.isConnected()) {
            System.out.println ("Connection variable is not connected to Globals, attempting connect.  Is Globals running?");
            glbConn.connect();
        }
        
        
        Graph myGraph = new GlGraph(glbConn);
        assertNotNull("Graph must not be null", myGraph);
        if (sleepToCheckProcess > 0) try {
            System.out.println ("Now sleeping " + sleepToCheckProcess + " seconds for process inspection");
            Thread.currentThread().sleep(sleepToCheckProcess * SLEEP_BY_SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if (glbConn.isConnected()) {
            glbConn.close();
        }
        System.out.println("made it...");
    }
    
    
}
