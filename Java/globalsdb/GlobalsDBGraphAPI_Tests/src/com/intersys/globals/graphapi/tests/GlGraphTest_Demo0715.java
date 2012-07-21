package com.intersys.globals.graphapi.tests;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersys.globals.GlobalsDirectory;
import com.intersys.globals.NodeReference;

import com.intersys.globals.graphapi.globalsgraphdb.GlGraph;
import org.junit.*;

public class GlGraphTest_Demo0715 {

    /**GlGraphTest_Demo0715 is the entry point for demonstrating the Java graph API implemented in Java
     */
    Connection graphConn = null;

    public GlGraphTest_Demo0715() {
        super();
    }

    private void getConn() {
        try {
            graphConn = ConnectionContext.getConnection();
            if (graphConn == null) {
                System.err.println("Fail - graph connection to globalsdb is null");
                return;
            }

            if (!graphConn.isConnected()) {
                graphConn.connect();
            }
            if (!graphConn.isConnected()) {
                System.err.println("Got a connection, it thinks it isn't connected");
            }
            System.out.println("Got a connection! - namespace is: " + graphConn.getNamespace());
        } catch (Exception cnctExc) {
            System.err.println(cnctExc.getMessage());
            return;
        }
    }
    
    private void listDir() {
        GlobalsDirectory newDir = graphConn.createGlobalsDirectory();
        String nxtName = null;
        Integer limit = 45;
        NodeReference thisGlb = null;
        //TODO: this is really better as a 'for' loop
        while ((nxtName = newDir.nextGlobalName()) != null && (!"".contentEquals(nxtName)) && (limit-- > 0)) {
            System.out.println ("Got a global....: " + nxtName);
            
            if (thisGlb != null) { thisGlb.close(); }
            
            thisGlb = graphConn.createNodeReference(nxtName);
            if (thisGlb == null) {
                System.out.println ("createnoderef returned null node ref");
            } else if (! thisGlb.exists()) {
                System.out.println ("Global marked as not exists");
            } else {
                System.out.println ("Global has subscripts " + thisGlb.getSubscriptCount());
            }
        }
        System.out.println("Done seeking globals, remaining limit is " + limit);
    }

    @Test
    public void demo() {
        getConn();
        listDir();
        GlGraph demoGraphDB = new GlGraph ();
    }

    public static void main(String[] args) {
        GlGraphTest_Demo0715 graphDemoEntry = new GlGraphTest_Demo0715();
        graphDemoEntry.demo();
    }
}
