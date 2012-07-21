package com.intersys.globals.graphapi;

import java.util.Set;

/** Common interface used to provide extension points to nodes and edges
 */
public interface GraphComponent {

    /**Retrieve a custom property string
     * @param propertyName Name of the property to retrieve
     * @return value of the requested property
     */
    public String getCustomString(String propertyName);

    /**Set a custom property string to a specified value
     * @param propertyName Name of the (new or existing) property
     * @param newValue New value for the property
     */
    public void setCustomString (String propertyName, String newValue);

    /**Delete a custom property
     * @param propertyName Name of the property to delete
     */
    public void deleteCustomString (String propertyName);

    /** What property names are attached to this component?
     * @return an iterable list of property-name strings
     */
    public Set<String> nonemptyPropertyNames();

    /**Get the graph in which this component resides
     * @return the graph for this node.
     */
    Graph getGraph();

    /** Each node and edge is labeled
     * @return string that labels the component
     */
    String getLabel();
}
