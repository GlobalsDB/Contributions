package com.intersys.globals.graphapi.globalsgraphdb;

import com.intersys.globals.GlobalsException;
import com.intersys.globals.NodeReference;

import java.io.PrintStream;

import java.util.Map;

import java.lang.reflect.Field;

import java.util.Collections;
import java.util.HashMap;

/** this is a collection of utility objects and functions for making use of the globals graph api
 */
public class GraphAPIEnv {

    /** Property identifies the home directory for the globals installation
     */
    public static final String GLOBALHOME_PROPNAME = "globalsdbutils.home";

    public GraphAPIEnv() {
        super();
    }

    /** Goes through a NodeReference and reports values and subscripts
     * @param globalNodeToSpelunk node to report
     */
    public static void printAllSubscripts(NodeReference globalNodeToSpelunk) {
        globalNodeToSpelunk.setSubscriptCount(0);
        String subscript = "";
        String subsubscript = "";
        String globalName = globalNodeToSpelunk.getName();

        /* To iterate over the subnodes of a given node, specify enough subscripts
         * to identify that node, plus one additional subscript for the level at
         * which you are iterating.  Start iterating at the beginning or end
         * of the sequence of subnodes by specifying subscript value "" (empty string).
         * Method nextSubscript returns (as a String) the subscript of the next
         * subnode in sequence.  Use the returned value as the final subscript, to
         * continue the iteration.  When there are no more subnodes in the sequence,
         * nextSubscript returns "" (empty string).
         *
         * For example, this loop iterates through the first-level subscripts of
         * " + globalName + " in ascending order:
         */
        /* This loop iterates throught the first-level subnodes of " + globalName + " in ascending
         * order, getting the value of each node as well as the subscript.
         */
        Object globVal = globalNodeToSpelunk.getObject();
        if (globVal == null) { globVal = "<UNDEFINED>"; }
        System.out.println("The value of global '" + globalName + "' is " + globVal);
        System.out.println("First- to fourth-level subscripts and values of " + globalName + " in ascending order:");
        Object nodeValue = null;
        Object subNodeValue = null;
        String subscript3 = "";
        Object subNodeVal3 = null;
        String subscript4 = "";
        Object subNodeValu4 = null;
        
        do {
            subscript = globalNodeToSpelunk.nextSubscript(subscript);
            if (subscript.length() > 0) {
                nodeValue = globalNodeToSpelunk.getObject(subscript);
                System.out.print("  " + globalName + "[" + subscript + "] value: ");
                if (nodeValue == null)
                    System.out.println("<UNDEFINED>");
                else
                    System.out.println(nodeValue);

                subsubscript = "";
                do {
                    subsubscript = globalNodeToSpelunk.nextSubscript(subscript, subsubscript);
                    if (subsubscript.length() > 0) {
                        subNodeValue = globalNodeToSpelunk.getObject(subscript, subsubscript);
                        if (subNodeValue == null) {
                            subNodeValue = "<UNDEFINED>";
                        }
                        System.out.println("      " + globalName + "[" + subscript + ", " + subsubscript + "] " +
                                           subNodeValue);
                        
                        subscript3 = "";
                        do {
                            subscript3 = globalNodeToSpelunk.nextSubscript(subscript, subsubscript, subscript3);
                            if (subscript3.length() > 0) {
                                subNodeVal3 = globalNodeToSpelunk.getObject(subscript, subsubscript, subscript3);
                                if (subNodeVal3 == null) {
                                    subNodeVal3 = "<UNDEFINED>";
                                }
                                System.out.println("      " + globalName + "[" + subscript + ", " + subsubscript +
                                                  ", " + subscript3 + "] " + subNodeVal3);

                            }
                            
                            //TODO: print out level 4 subscripts and values a la zwrite

                        } 
                        while (subscript3.length() > 0);
                        

                    }
                } while (subsubscript.length() > 0);
            }
        } while (subscript.length() > 0);

    }

    protected static void setEnv(Map<String, String> newenv) {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>)theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField =
                processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>)theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        } catch (NoSuchFieldException e) {
            try {
                Class[] classes = Collections.class.getDeclaredClasses();
                Map<String, String> env = System.getenv();
                for (Class cl : classes) {
                    if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                        Field field = cl.getDeclaredField("m");
                        field.setAccessible(true);
                        Object obj = field.get(env);
                        Map<String, String> map = (Map<String, String>)obj;
                        map.clear();
                        map.putAll(newenv);
                    }
                }
            } catch (Exception e2) {
                System.out.println("setEnv exception e2");
                // e2.printStackTrace();
            }
        } catch (Exception e1) {
            System.out.println("setEnv exception e1");
            // e1.printStackTrace();
        }
    }

    public static void printAnEnv(String envKey, PrintStream toOut) {
        toOut.println("Environment variable '" + envKey + "' has value '" + System.getenv(envKey) + "'");
    }

    /** Diagnoses environment variables
     * @param envVarName environment variable to print
     */
    public static void printAnEnv(String envVarName) {
        printAnEnv(envVarName, System.out);
    }

    /** Diagnoses system properties, redirectable output
     * @param propName property to print
     * @param toDest printstream on which to print
     */
    public static void printAProp(String propName, PrintStream toDest) {
        toDest.println("Property '" + propName + "' has value '" + System.getProperty(propName) + "'");
    }

    /** Diagnose system properties, emits to System.out
     * @param propToPrint property to print
     */
    public static void printAProp(String propToPrint) {
        printAProp(propToPrint, System.out);
    }

    /** Tool for initializing the environment for JNI calls
     * @param globlshm home directory for GlobalsDB installation
     */
    public static void setupEnvVars(String globlshm) {
        System.out.println("Setting env vars with globals home " + globlshm);

        String pth = System.getenv("PATH");

        if ((pth != null) && (!"".equals(pth))) {
            pth = ":" + pth;
        }

        Map<String, String> newEnvMap = new HashMap<String, String>();
        newEnvMap.put("PATH", globlshm + "/bin:" + globlshm + "/mgr" + pth);
        newEnvMap.put("GLOBALS_HOME", globlshm);
        newEnvMap.put("LD_LIBRARY_PATH", globlshm + "/bin");
        newEnvMap.put("DYLD_LIBRARY_PATH", globlshm + "/bin");
        setEnv(newEnvMap);
        System.out.println("done with setupEnvVars");
    }

    /** overall diagnostic - print out the environment variables and system properties related to GlobalsDB
     */
    public static void reportStuff() {
        printAnEnv("PATH");
        printAnEnv("GLOBALS_HOME");
        printAnEnv("DYLD_LIBRARY_PATH");
        printAnEnv("LD_LIBRARY_PATH");

        printAProp("java.library.path");
        printAProp(GLOBALHOME_PROPNAME);
    }


    /** setup routine for JNI interface to GlobalsDB - no arguments calls 'quietly'
     */
    public static void setUp() {
        setUp(0);
    }

    /** Setup routine with selectible logging messages
     * @param squawk log level (0 or 1)
     */
    public static void setUp(int squawk) {
        System.out.println("doing setup now");

        if (1 <= squawk) {
            reportStuff();
        }

        setupEnvVars(System.getProperty(GLOBALHOME_PROPNAME));

        if (1 <= squawk) {
            reportStuff();
        }
        System.out.println("Finished setting env");
    }

    /** Entry point - static main
     * @param arguments command-line arguments
     */
    public static void main(String arguments[]) {
        setUp();
    }

}
