/**
 * Copyright (C) 2011 Tom Spencer <thegaffer@tpspencer.com>
 *
 * globals-wrapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * globals-wrapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with globals-wrapper. If not, see <http://www.gnu.org/licenses/>.
 */
package org.tpspencer.globals.wrapper.mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tpspencer.globals.wrapper.NodeMapping;

/**
 * Standard (and simple) implementation of the {@link NodeMapping}
 * interface.
 *
 * @author Tom Spencer
 */
public class NodeMappingImpl implements NodeMapping {

    /** Holds the nodes type */
    private NodeType type;
    /** Holds the map of sub nodes */
    private Map<String, NodeMapping> subNodes;
    /** Holds the map of properties held in the list */
    private Map<String, NodeValue> properties;
    /** Max holds the highest amount of properties, though not all neccessarily still exist */
    private int maxProperties;
    
    public NodeMappingImpl(NodeType type) {
        this.type = type;
    }
    
    public void addSubNode(String name, NodeMapping mapping) {
        if( subNodes == null ) subNodes = new HashMap<String, NodeMapping>();
        subNodes.put(name, mapping);
    }
    
    public void addProperty(NodeValue value) {
        if( properties == null ) properties = new HashMap<String, NodeMapping.NodeValue>();
        properties.put(value.getName(), value);
        
        if( value.getPosition() >= maxProperties ) maxProperties = value.getPosition() + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeType getNodeType() {
        return type;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getKeyNames() {
        Set<String> ret = new HashSet<String>();
        if( subNodes != null ) ret.addAll(subNodes.keySet());
        if( properties != null ) ret.addAll(properties.keySet());
        return ret;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidSubNode(String name) {
        return subNodes != null ? subNodes.containsKey(name) : null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NodeValue getNodeValue(String name) {
        return properties != null ? properties.get(name) : null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalFieldsInList() {
        return maxProperties;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NodeMapping getSubNodeMapping(String subNode) {
        return subNodes != null ? subNodes.get(subNode) : null;
    }
}
