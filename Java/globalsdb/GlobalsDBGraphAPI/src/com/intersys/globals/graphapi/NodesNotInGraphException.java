package com.intersys.globals.graphapi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/** The caller has attempted to use a Node in a Graph to which it has not been explicitly added.
 * Known failure case is adding an Edge to a Graph when the Edge has non-member Nodes
 */
public class NodesNotInGraphException extends InvalidNodesException implements Collection<GraphNode> {
    /** Preferred public instantiation method for this exception
     * @param withinGraph Graph within which the bad nodes exception occurred
     * @param badNodes set of nodes that are not in this graph but which were required for the failed operation
     */
    public NodesNotInGraphException(Graph withinGraph, Collection<GraphNode> badNodes) {
        super(withinGraph, badNodes);
    }

    /** Alternate public instantiation method for this exception
     * @param withinGraph Graph within which the bad nodes exception occurred
     * @param singleBad node that is not in this graph but which was required for the failed operation
     */
    public NodesNotInGraphException(Graph withinGraph, GraphNode singleBad) {
        super(withinGraph, singleBad);
    }

    public NodesNotInGraphException(Graph withinGraph) {
        super(withinGraph);
    }

    /** String used to build this exception class, used in message renderer in super()
     */
    static final String reasonInvalid = "Referenced nodes are not members of graph";

    @SuppressWarnings("compatibility:-5661724481618718318")
    private static final long serialVersionUID = 1L;

    public NodesNotInGraphException() {
        super();
    }

    public NodesNotInGraphException(String string) {
        super(string);
    }

    public NodesNotInGraphException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public NodesNotInGraphException(Throwable throwable) {
        super(throwable);
    }


}
