package com.intersys.globals.graphapi.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
    GlGraphTest_GraphVizOutput.class
    //, GraphTest_RemoveNodes.class
    //, GlobalsDBGraphTest_simple.class 
    //, GlTest_dupNodeLabel.class
    }
)
public class DuringDev {
    public static void main(String[] args) {
        String[] args2 = { DuringDev.class.getName() };
        JUnitCore.main(args2);
    }
}
