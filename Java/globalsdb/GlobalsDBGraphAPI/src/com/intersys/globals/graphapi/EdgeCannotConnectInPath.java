package com.intersys.globals.graphapi;


/** Caller tried to add an Edge to the Path that is not continous with one or other end of Path
 */
public class EdgeCannotConnectInPath extends GraphException {

    /** The unusable edge
     */
    GraphEdge badEdge = null;

    /** The path in which the edge cannot be added
     */
    GraphPath badPath = null;
    
    public EdgeCannotConnectInPath(Throwable throwable) {
        super(throwable);
    }

    public EdgeCannotConnectInPath(String string, Throwable throwable) {
        super(string, throwable);
    }

    public EdgeCannotConnectInPath(String string) {
        super(string);
    }

    public EdgeCannotConnectInPath() {
        super();
    }


    /**Instantiate the exception with the Edge and the Path that are incompatible.
     * @param edgeThatIsBad Edge that cannot be used for the path
     * @param pathThatIsBad Path in which the Edge cannot be used
     */
    public EdgeCannotConnectInPath(GraphEdge edgeThatIsBad, GraphPath pathThatIsBad) {
        super();
        badEdge = edgeThatIsBad;
        badPath = pathThatIsBad;
    }
}
