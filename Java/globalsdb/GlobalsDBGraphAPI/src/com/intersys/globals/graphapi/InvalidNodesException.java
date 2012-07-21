package com.intersys.globals.graphapi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/** This exception indicates there is an inconsistency between one or mode nodes that the caller has attempted to
 * use in the context of a particular graph
 */
public class InvalidNodesException extends GraphException implements Collection<GraphNode> {

    @SuppressWarnings("compatibility:8265764351032346475")
    private static final long serialVersionUID = 1L;

    private Collection<GraphNode> internBadNodeList = new HashSet<GraphNode>();

    /** String describing why the nodes are invalid in the graph
     */
    static String reasonInvalid = null;

    /** Package private mechanism for indicating why these nodes are invalid
     * @param newReason a string to be used in constructing exception message
     */
    void setReasonInvalid(String newReason) {
        reasonInvalid = newReason;
    }

    /** Why is this set of nodes invalid?
     * @return reason string for exception
     */
    public String getReasonInvalid () {
        return reasonInvalid;
    }
    
    public InvalidNodesException() {
        super();
    }

    public InvalidNodesException(String string) {
        super(string);
    }

    public InvalidNodesException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public InvalidNodesException(Throwable throwable) {
        super(throwable);
    }

    /** Create the exception within the context of a Graph
     * @param withinGraph graph in which the exception was raised
     */
    InvalidNodesException(Graph withinGraph) {
        super();
        this.withinGraph = withinGraph;
    }

    /** Preferred public instantiation method for this exception
     * @param withinGraph Graph within which the bad nodes exception occurred
     * @param badNodes set of nodes that don't work with the graph
     */
    public InvalidNodesException(Graph withinGraph, Collection<GraphNode> badNodes) {
        super();
        this.withinGraph = withinGraph;
        internBadNodeList.addAll(badNodes);
    }
    
    /** Preferred public instantiation method for this exception
     * @param withinGraph Graph within which the bad nodes exception occurred
     * @param oneBadNode a node that doesn't work with the graph
     */
    public InvalidNodesException(Graph withinGraph, GraphNode oneBadNode) {
        super();
        this.withinGraph = withinGraph;
        internBadNodeList.add(oneBadNode);
    }


    /** Is the list of nodes empty?  If so this is inconsistent
     * @return indicator whether node list is empty
     */
    @Override
    public boolean isEmpty() {
        return internBadNodeList.isEmpty();
    }

    /** Does this exception list the referenced node as a bad node?
     * @param object Node to check
     * @return whether this node is in the bad list
     */
    @Override
    public boolean contains(Object object) {
        return internBadNodeList.contains(object);
    }

    /** Get an iterator for going through the bad nodes
     * @return iterator for nodes
     */
    @Override
    public Iterator<GraphNode> iterator() {
        return internBadNodeList.iterator();
    }

    /** Get this list as an array
     * @return array form of bad-node list
     */
    @Override
    public Object[] toArray() {
        return internBadNodeList.toArray();
    }

    /** Get another node list as an array.  This should probably be prohibited.
     * @param ts list of exception nodes
     * @return array of exception nodes
     */
    @Override
    public Object[] toArray(Object[] ts) {
        return internBadNodeList.toArray(ts);
    }

    /** Add a node to the bad-nodes list
     * @param node Node to implicate
     * @return was Node added?
     */
    @Override
    public boolean add(GraphNode node) {
        return internBadNodeList.add(node);
    }

    /** Remove a node from the bad-nodes list
     * @param object Node to exonerate
     * @return was node removed?
     */
    @Override
    public boolean remove(Object object) {
        return internBadNodeList.remove(object);
    }

    /** Are all the given nodes in the bad list?
     * @param collection Set of nodes to check
     * @return if ALL nodes from list are in the bad list
     */
    @Override
    public boolean containsAll(Collection<?> collection) {
        return internBadNodeList.containsAll(collection);
    }

    /** Bulk-add bad nodes to the bad node list
     * @param collection nodes to add
     * @return were the nodes added?
     */
    @Override
    public boolean addAll(Collection<? extends GraphNode> collection) {
        return internBadNodeList.addAll(collection);
    }


    /** Bulk-remove nodes from the bad list
     * @param collection nodes to exonerate
     * @return were nodes removed
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        return internBadNodeList.removeAll(collection);
    }

    /**Retain nodes from this list in bad list
     * @param collection nodes to retain
     * @return were nodes retained?
     */
    @Override
    public boolean retainAll(Collection<?> collection) {
        return internBadNodeList.retainAll(collection);
    }

    /** Clear the bad-node list.
     */
    @Override
    public void clear() {
        internBadNodeList.clear();
    }

    /** Is this the same invalid-node exception?
     * @param object Exception object to check
     * @return whether these are equal
     */
    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    /** get the object hash code
     * @return hash code for this InvalidNodeException
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /** In which graph is this exception occur?
     * @return Graph in reference to which this exception was raised
     */
    public Graph getWithinGraph() {
        return withinGraph;
    }

    /** Construct the invalid-nodes message
     * @return Message that includes the reason string for the invalidity, if any.
     */
    public String getMessage() {
        Iterator<GraphNode> nodeIter = withinGraph.iterator();
        long counter = 0;
        StringBuffer outMessage = new StringBuffer (super.getMessage());
        if ((reasonInvalid != null) && (! "".equals(reasonInvalid))) {
            outMessage.append(reasonInvalid).append(" ");
        }
        outMessage.append(" the following node(s) are invalid: ");
        while (nodeIter.hasNext()) {
            if (counter++ > 0) { outMessage.append(", "); }
            outMessage.append(nodeIter.next().toString() );
        }
        return outMessage.toString();
    }

    /** How large is this graph in nodes?
     * @return Count of the nodes in this graph
     */
    @Override
    public int size() {
        return internBadNodeList.size();
    }
}
