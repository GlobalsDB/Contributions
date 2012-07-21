package com.intersys.globals.graphapi.globalsgraphdb;

/** Implements a Globals subscript using a single string 
 */
public class StringGlobalsSubscript implements GlobalsNodeSubscript {

    /**String to use as the Globals subscript for building the graph
     */
    private String thisSubscriptString = "";
    
    
    /**This static class provides a default class that uses the default string for the subscript.  Note we don't actually
     * want numerous instances of this class.  In fact, it needs to be an invariant that each GlGraph has only
     * one subscript instance.
     */
    public static GlobalsNodeSubscript defaultStringSubscript = new StringGlobalsSubscript(GlobalsNodeSubscript.DEFAULT_SUBSCRIPT);

    /** Create a new GlobalsDB subscript using the given string 
     * @param withSubScriptString string to use as the subscript
     */
    public StringGlobalsSubscript (String withSubScriptString) {
        thisSubscriptString = withSubScriptString;
    }

    /** Package-private constructor.  Don't really want callers to use this.
     */
    StringGlobalsSubscript() {
    }

    /** Static method does not require instantiation
     * @return the default subscript defined in GlobalseNodeSubscript;
     */
    public static GlobalsNodeSubscript getDefault() {
        System.out.println("getDefault subscript has value " + ((StringGlobalsSubscript) defaultStringSubscript).getSubScript());
        return defaultStringSubscript;
    }


    /** What subscript are we using for this graph?
     * @return Globals subscript string
     */
    public String getSubScript() {
        return thisSubscriptString;
    }

    public String toString() {
        return getSubScript();
    }
}
