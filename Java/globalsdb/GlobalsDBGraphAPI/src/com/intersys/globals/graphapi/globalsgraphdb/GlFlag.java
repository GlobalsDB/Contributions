package com.intersys.globals.graphapi.globalsgraphdb;

/** A GlFlag is used to mark values of globals with known strings
 */
public class GlFlag {
    private String flagString = null;
    
    GlFlag(String withString) {
        flagString = withString;
    }
    
    public GlFlag() {
        super();
    }

    /** get the string value of the flag
     * @return string to use to flag the value
     */
    public String toString() {
        return flagString;
    }
    
}
