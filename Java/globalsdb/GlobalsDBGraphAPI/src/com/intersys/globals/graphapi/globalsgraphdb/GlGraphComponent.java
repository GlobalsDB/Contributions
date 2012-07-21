package com.intersys.globals.graphapi.globalsgraphdb;

import com.intersys.globals.graphapi.Graph;
import com.intersys.globals.graphapi.GraphComponent;

import com.intersys.globals.graphapi.GraphException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/** implementation class for graph component, extends hashmap
 */
public class GlGraphComponent extends HashMap<String, String> implements GraphComponent {
    /** handle to the enclosing Graph object
     */
    protected GlGraph parentGraph = null;
    
    static final long serialVersionUID = 1L;
    
    String label = null;

    public GlGraphComponent() {
        super();
    }

    GlGraphComponent(String withNewLabel) {
        label = withNewLabel;
    }
    
    public GlGraphComponent(GlGraph inGraph, String withNewLabel) {
        parentGraph = inGraph;
        label = withNewLabel;
    }

    public GlGraphComponent(GlGraph forThisGrf) {
        parentGraph = forThisGrf;
    }

    @Override
    public String getCustomString(String propertyName) {
        return get(propertyName);
    }

    @Override
    public void setCustomString(String propertyName, String newValue) {
        if (propertyName == null) {
            throw new GraphException(parentGraph, "Nulls not allowed for component custom property names");
        } else if ("".equals(propertyName)) {
            throw new GraphException(parentGraph, "Empty strings not allowed for component custom property names");
        }
        put (propertyName, newValue);
    }

    @Override
    public void deleteCustomString(String propertyName) {
        remove(propertyName);
    }

    @Override
    public Set<String> nonemptyPropertyNames() {
        return keySet();
    }

    @Override
    public Graph getGraph() {
        return parentGraph;
    }

    /** package-private method attaches component to a graph
     * @param parentGraph graph for this component
     */
    void setParentGraph(GlGraph forGraph) {
        parentGraph = forGraph;
    }

    @Override
    public String getLabel() {
        return label;
    }
    
    void setLabel(String newLabel) {
        label = newLabel;
    }
}
