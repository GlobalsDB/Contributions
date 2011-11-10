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
package org.tpspencer.globals.wrapper;

import java.util.Set;


/**
 * This interface represents a mapping class that understands
 * the contents of a node. If a mapping exists then it turns a
 * request into a child of the node into a value as appropriate.
 * Without a mapping every request for a value from a node will
 * always return the sub-index value in its native form.
 *
 * @author Tom Spencer
 */
public interface NodeMapping {
    
    /**
     * @return The type of the current node, see {@link NodeType}
     */
    public NodeType getNodeType();
    
    /**
     * @return A set of the allowed sub nodes and properties of this node
     */
    public Set<String> getKeyNames();
    
    /**
     * Called to get the mapping for a sub-node if known.
     * 
     * @param subNode The name of the sub-node
     * @return The mapping if known
     */
    public NodeMapping getSubNodeMapping(String subNode);

    /**
     * A description of a value of the node if known. Just
     * because a sub-node is valid does not mean we will
     * definitely have a description of it - this is 
     * specifically reserved for a value when the node
     * represents an object.
     * 
     * @param name The name of the property
     * @return The NodeValue if it exists
     */
    public NodeValue getNodeValue(String name);
    
    /**
     * Determines if the given subnode is valid. Just because
     * it is value does not mean it actually exists.
     * 
     * @param name The name of the subnode
     * @return True if it is valid, false otherwise
     */
    public boolean isValidSubNode(String name);
    
    /**
     * When the node represents a simple or complex object this returns
     * the total number of fields in the list.
     * 
     * @return The total number of fields in the list
     */
    public int getTotalFieldsInList();
    
    /**
     * Simple enum to describe the type of node. This has an
     * effect on the operations that can be performed.
     *
     * @author Tom Spencer
     */
    public enum NodeType {
        /** The node holds a simple value, it has no children */
        VALUE,
        /** The node represents a object with all values in a list */
        SIMPLE_OBJECT,
        /** The node represents an object whose values are stored as subscripts */
        LEAF_OBJECT,
        /** The node represents an object with some state in a list and others as sub-nodes */
        COMPLEX_OBJECT,
        
        /** The node has no data, but represents a small/searchable collection of other folders/objects */
        SMALL_FOLDER,
        /** The node has no data, but holds (potentially) lots of sub-nodes */ 
        LISTING,
        
        /** The node has no real type and can contain data and/or sub-nodes */ 
        NONE
    }
    
    /**
     * This interface describes a value of the node. This only
     * really applies when the node represents an object.
     *
     * @author Tom Spencer
     */
    public interface NodeValue {
        
        /**
         * @return The name of the value
         */
        public String getName();
        
        /**
         * @return If true, value is held in a sub-node (otherwise in a list in the node itself)
         */
        public boolean isSubNode();

        /**
         * @return The index of this value if held in a list in the nodes value directly
         */
        public int getPosition();
        
        
    }
    
    /**
     * This describes the type of the
     *
     * @author Tom Spencer
     */
    public enum ValueType {
        STRING,
        INT,
        DOUBLE,
        LONG,
        
        /** A custom object, where the values of the object are in a list */
        OBJECT
    }
}
