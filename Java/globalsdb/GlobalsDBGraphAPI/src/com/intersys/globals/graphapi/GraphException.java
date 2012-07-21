package com.intersys.globals.graphapi;

import com.intersys.globals.graphapi.globalsgraphdb.GlGraph;

import java.util.logging.Level;
import java.util.logging.Logger;

/** GraphException indicates the programmer has made a logical error in using the graph.
 */
public class GraphException extends RuntimeException {

    Logger lgGraphException = null;  // Logger generated Jul 20, 2012

    private void LGL(Level forLvl, String msg) { 
        if (lgGraphException == null) { 
            lgGraphException = Logger.getLogger (getClass().getName());
            lgGraphException.setLevel(Level.INFO); 
        }
         lgGraphException.log(forLvl, msg); 
    }

    private void LG(String iMsg) { LGL (Level.INFO, iMsg); }

    private void LGZ(String zMsg) { LGL (Level.FINER, zMsg); }

    /** Marker exception for methods that have not yet been implemented.
     */
    public static GraphException notYetImplemented = new GraphException("Operation has not been implemented");

    /** Graph in which the exception occurred.
     */
    protected Graph withinGraph = null;

    public GraphException(Throwable throwable) {
        super(throwable);
    }

    public GraphException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public GraphException(String string) {
        super(string);
    }

    public GraphException() {
        super();
    }

    public GraphException(Graph graphWithErr, String withMessage) {
        this(withMessage);
        this.withinGraph = graphWithErr;
        LG(getMessage());
    }


    /** Format a message with the name of the Graph
     * @return Message for this exception
     */
    public String getMessage() {
        StringBuffer excMessage = new StringBuffer();
        if (withinGraph != null) {
            if ((withinGraph.getName() != null) && (!"".equals(withinGraph.getName()))) {
                excMessage.append("Exception in graph '").append(withinGraph.getName()).append("'");
                excMessage.append(" - ");
            }
            if (GlGraph.class.isAssignableFrom(withinGraph.getClass())) {
                GlGraph gdbGraph = (GlGraph)withinGraph;
                excMessage.append("Globals DB subscript is '").append(gdbGraph.getGraphSubscript().toString()).append("'");
                excMessage.append(" - ");
            }
        }
        excMessage.append(super.getMessage());
        return excMessage.toString();
    }
}
