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
package org.tpspencer.globals.wrapper.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tpspencer.globals.wrapper.Node;
import org.tpspencer.globals.wrapper.NodeMapping;
import org.tpspencer.globals.wrapper.NodeMapping.NodeType;
import org.tpspencer.globals.wrapper.NodeMapping.NodeValue;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;

/**
 * Implements the node wrapper
 *
 * @author Tom Spencer
 */
public class NodeImpl implements Node {

    /** The root node */
    private final RootNodeImpl root;
    /** The parent node to this one */
    private final NodeImpl parent;
    /** The subscript of this node */
    protected final Object subscript;
    /** The mapping for this node */
    private NodeMapping mapping;
    
    /** Holds any sub-nodes we have changed the value of */
    protected Map<String, NodeImpl> subNodes;
    /** Holds the value of this node */
    protected Object[] currentValue;
    
    protected NodeImpl(RootNodeImpl root, NodeImpl parent, Object subscript, NodeMapping mapping) {
        this.root = root;
        this.parent = parent;
        this.subscript = subscript;
        this.mapping = mapping;
    }
    
    /**
     * @return The root node
     */
    protected RootNodeImpl getRootNode() {
        return root;
    }
    
    /**
     * @return The parent node to this one (will be null if this node is the root)
     */
    protected NodeImpl getParentNode() {
        return parent;
    }
    
    /**
     * Helper to get the nodes type
     * 
     * @return The type of this node
     */
    protected NodeType getNodeType() {
        return mapping != null ? mapping.getNodeType() : NodeType.NONE;
    }
    
    /**
     * Internal helper to append the subscript for this node into
     * the given builder.
     * 
     * @param builder The StringBuilder to add into
     */
    protected void appendPath(StringBuilder builder) {
        if( parent != null ) parent.appendPath(builder);
        else getRootNode().appendPath(builder);
        builder.append('/').append(subscript);
    }
    
    /**
     * Internal helper to append the subscript for this node into
     * the given list.
     * 
     * @param lst The list to add into
     */
    protected void appendPath(List<String> lst) {
        if( parent != null ) parent.appendPath(lst);
        else getRootNode().appendPath(lst);
        lst.add(this.subscript.toString());
    }
    
    /**
     * Call to get the node reference for this node. This will
     * simply call the root to get the information.
     * 
     * @return The actual node reference
     */
    protected NodeReference getNode() {
        return getRootNode().getNode(this);
    }
    
    /**
     * This is called when a child node has its value changed. This
     * enables us to save the NodeImpl and use it in the future.
     * 
     * @param node The node that has changed
     */
    protected void changedSubNode(NodeImpl node) {
        if( subNodes == null ) subNodes = new HashMap<String, NodeImpl>();
        subNodes.put(node.getName(), node);
        if( parent != null ) parent.changedSubNode(this);
    }
    
    /**
     * Called when we need to commit the values
     */
    protected void onCommit() {
        // Save the current value if it has been set
        // If array is null then it has been unchanged
        if( currentValue != null ) {
            // Now empty, kill it
            if( currentValue.length == 0 ) {
                getNode().killNode();
            }
            
            // A single value, set it
            else if( currentValue.length == 1 ) {
                Object val = currentValue[0];
                if( val == null ) getNode().killNode();
                else if( val instanceof String ) getNode().set(val.toString());
                else if( val instanceof Double ) getNode().set((Double)val);
                else if( val instanceof Long ) getNode().set((Long)val);
                else if( val instanceof Integer ) getNode().set((Integer)val);
                else if( val instanceof byte[] ) getNode().set((byte[])val);
                else getNode().set(val.toString());
            }
            
            // A list, create list and set it
            else {
                ValueList lst = getRootNode().getTempList();
                lst.append(currentValue);
                getNode().set(lst);
            }
            
            // Clear it as now set
            currentValue = null;
        }
        
        // Tell children to change themselves
        if( subNodes != null ) {
            for( String k : subNodes.keySet() ) {
                subNodes.get(k).onCommit();
            }
        }
    }
    
    /**
     * @return Determines based on mapping if this node holds a list
     */
    protected boolean isListNode() {
        boolean ret = false;
        
        switch (getNodeType()) {
        case NONE:
        case LISTING:
        case SMALL_FOLDER:
        case LEAF_OBJECT:
            ret = false;
            break;
        
        case VALUE:
            ret = false;
            // TODO: Inspect type in mapping
            break;
            
        case SIMPLE_OBJECT:
        case COMPLEX_OBJECT:
            ret = true;
            break;
        }
        
        return ret;
    }
    
    /**
     * This helper determines if we have a property that is in the list
     * held by this node.
     * 
     * @param name The name of the property
     * @return True if it is held in the list, false (meaning its a sub-node) otherwise
     */
    protected boolean isPropertyInList(String name) {
        boolean ret = false;
        
        switch (getNodeType()) {
        case SIMPLE_OBJECT:
        case COMPLEX_OBJECT:
            NodeValue value = mapping.getNodeValue(name);
            ret = !value.isSubNode() && value.getPosition() >= 0;
            break;
        }
        
        return ret;
    }
    
    /**
     * Helper method to get this nodes current value as an array
     * 
     * @return The nodes current value
     */
    protected Object[] getCurrentValue() {
        Object[] ret = currentValue;
        
        if( ret == null ) {
            NodeReference node = getNode();
            
            if( !node.exists() ) ret = null;
            else if( !isListNode() ) {
                Object val = node.getObject();
                ret = new Object[]{val};
            }
            else {
                ValueList lst = node.getList();
                ret = lst.getAll();
                lst.close();
            }
        }
        
        return ret;
    }
    
    /////////////////////////////////////////
    // Node
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath() {
        StringBuilder buf = getRootNode().getTempBuffer();
        appendPath(buf);
        return buf.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getPathList() {
        ArrayList<String> ret = new ArrayList<String>();
        appendPath(ret);
        return ret;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return subscript.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setMapping(NodeMapping mapping) {
        this.mapping = mapping;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDataNode() {
        return getNode().exists();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isParentNode() {
        return getNode().hasSubnodes();
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>If the value of the sub-node has changed this will be the same
     * instance each time, otherwise this will be a new instance.</p>
     */
    @Override
    public NodeImpl getSubNode(String name) {
        if( subNodes != null && subNodes.containsKey(name) ) return subNodes.get(name);
        else return new NodeImpl(getRootNode(), this, name, mapping != null ? mapping.getSubNodeMapping(name) : null);
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p><b>Note: </b>No checking is performed to make sure this is valid
     * for the node!!</p>
     */
    @Override
    public long increment(int number) {
        return getNode().increment(1);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Node> iterator() {
        return new NodeIterator(this, "", false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Node> iterator(String start) {
        return new NodeIterator(this, start, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Node> reverseIterator() {
        return new NodeIterator(this, "", true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Node> reverseIterator(String start) {
        return new NodeIterator(this, start, true);
    }
    
    ////////////////////////////////////////////////
    // Map Operations
    
    /**
     * {@inheritDoc}
     * 
     * <p>Simply determines if the key exists. If the key is null then this
     * purely determines if this node has a data value. If this node has
     * a mapping and that states this the key is not a valid subnode then
     * this method returns false regardless.
     */
    public boolean containsKey(Object key) {
        NodeReference node = getNode();
        
        if( key == null ) return node.exists(); 
        else if( this.mapping != null && !this.mapping.isValidSubNode(key.toString()) ) return false;
        else return node.exists(key) || node.hasSubnodes(key);
        // TODO: If property in list we should determine this!!  
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This is not supported at all</p>
     */
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Cannot determine if value exists on a node");
    }
    
    /**
     * {@inheritDoc}
     */
    public Set<Entry<String, Object>> entrySet() {
        
        Set<Entry<String, Object>> ret = new HashSet<Map.Entry<String,Object>>();
        for( String s : keySet() ) {
            final String key = s;
            ret.add(new Entry<String, Object>() {
                
                public String getKey() { 
                    return key;
                }

                public Object getValue() {
                    return get(key);
                }

                public Object setValue(Object value) {
                    throw new UnsupportedOperationException("Setting via EntrySet is not possible");
                }
            } );
        }
        
        return ret;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>Note this just detects if this node has subnodes</p>
     */
    public boolean isEmpty() {
        return !getNode().hasSubnodes();
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>The key set is only returned when the node represents an
     * object, otherwise this is null. If you want to walk a node
     * then use the iterator methods.</p>
     */
    public Set<String> keySet() {
        Set<String> ret = null;
        
        switch(getNodeType()) {
        case LEAF_OBJECT:
        case COMPLEX_OBJECT:
        case SIMPLE_OBJECT:
            ret = mapping.getKeyNames();
            break;
            
        default:
            ret = new HashSet<String>();
        }
        
        return ret;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>Uses keySet to determine the valid keys and get those values.
     * This is probably useless!</p>
     */
    public Collection<Object> values() {
        List<Object> ret = new ArrayList<Object>();
        
        for( String k : keySet() ) {
            ret.add(get(k));
        }
        
        return ret;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>We simply return the size of the keyset if there is one. This
     * may not match the number of items actually stored of which we do
     * not neccessarily know.</p>
     */
    public int size() {
        return keySet().size();
    }
    
    /**
     * {@inheritDoc}
     * 
     * Return the value against the key. If the key is null then this will
     * always get the raw value of this node. Otherwise it will get that
     * specific property
     */
    public Object get(Object key) {
        Object ret = null;
        
        // Directly get this nodes value
        if( key == null ) {
            Object[] val = getCurrentValue();
            
            if( val == null || val.length == 0 ) ret = null;
            else if( val.length == 1 ) ret = val[0];
            else ret = val;
        }
        
        // Get the value from the list
        else if( isPropertyInList(key.toString()) ) {
            currentValue = getCurrentValue();   // Ensures this is set
            
            int position = mapping.getNodeValue(key.toString()).getPosition();
            if( currentValue != null && position < currentValue.length ) ret = currentValue[position];
        }
        
        // Get the sub-node and get value from there
        else {
            Node sub = getSubNode(key.toString());
            ret = sub != null ? sub.get(null) : null;
        }
        
        return ret;
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        Set<String> keys = keySet();
        for( String k : keys ) {
            remove(k);
        }
        remove(null);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object put(String key, Object value) {
        boolean flag = false;   // Set to true if we have changed
        Object oldValue = null;
        
        // Property is in a sub node
        if( key == null ) {
            Object[] val = getCurrentValue();
            oldValue = val != null && val.length > 0 ? val[0] : null;
            if( oldValue != value && (oldValue == null || !oldValue.equals(value)) ) {
                currentValue = new Object[]{value};
                flag = true;
            }
        }
        
        // Property is in a list inside here
        else if( isPropertyInList(key.toString()) ) {
            currentValue = getCurrentValue();   // Ensures this is set
            int position = mapping.getNodeValue(key.toString()).getPosition();
            if( currentValue != null && position < currentValue.length ) {
                oldValue = currentValue[position];
                if( oldValue != value && (oldValue == null || !oldValue.equals(value)) ) {
                    currentValue[position] = value;
                    flag = true;
                }
            }
            else {
                Object[] vals = new Object[mapping.getTotalFieldsInList()];
                if( currentValue != null ) System.arraycopy(currentValue, 0, vals, 0, currentValue.length);
                vals[position] = value;
                currentValue = vals;
                flag = true;
            }
        }
        
        // Set on subnode
        else {
            Node sub = getSubNode(key.toString());
            oldValue = sub.put(null, value);
        }
        
        
        if( flag ) parent.changedSubNode(this);
        return oldValue;
    }
    
    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends String, ? extends Object> t) {
        for( String s : t.keySet() ) {
            put(s, t.get(s));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object remove(Object key) {
        boolean flag = false;   // Set to true if we have changed
        Object oldValue = null;
        
        // Self value
        if( key == null ) {
            Object[] val = getCurrentValue();
            if( val == null || val.length > 0 ) {
                currentValue = new Object[0];
                flag = true;
            }
        }
        
        // Property is in a list inside here
        else if( isPropertyInList(key.toString()) ) {
            currentValue = getCurrentValue();   // Ensures this is set
            
            int position = mapping.getNodeValue(key.toString()).getPosition();
            if( currentValue != null && position < currentValue.length ) {
                oldValue = currentValue[position];
                if( oldValue != null ) {
                    currentValue[position] = null;
                    flag = true;
                }
            }
        }
        
        // Is on subnode
        else {
            Node sub = getSubNode(key.toString());
            oldValue = sub.remove(null);
        }
        
        
        if( flag ) parent.changedSubNode(this);
        return oldValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mapping == null) ? 0 : mapping.hashCode());
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        result = prime * result + ((root == null) ? 0 : root.hashCode());
        result = prime * result + ((subscript == null) ? 0 : subscript.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if( this == obj ) return true;
        if( obj == null ) return false;
        if( getClass() != obj.getClass() ) return false;
        NodeImpl other = (NodeImpl)obj;
        if( mapping == null ) {
            if( other.mapping != null ) return false;
        }
        else if( !mapping.equals(other.mapping) ) return false;
        if( parent == null ) {
            if( other.parent != null ) return false;
        }
        else if( !parent.equals(other.parent) ) return false;
        if( root == null ) {
            if( other.root != null ) return false;
        }
        else if( !root.equals(other.root) ) return false;
        if( subscript == null ) {
            if( other.subscript != null ) return false;
        }
        else if( !subscript.equals(other.subscript) ) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Node: ");
        appendPath(buf);
        return buf.toString();
    }
    
    /////////////////////////////////////////
    // Helper Classes
    
    /**
     * This class implements the iterator interface around the current
     * node to get at it's children.
     */
    private final static class NodeIterator implements Iterator<Node> {
        
        public final NodeImpl node;
        public String current;
        public final boolean reverse;
        
        public NodeIterator(NodeImpl node, String current, boolean reverse) {
            this.node = node;
            this.current = current;
            this.reverse = reverse;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean hasNext() {
            String next = null;
            
            if( reverse ) next = node.getNode().previousSubscript(current);
            else next = node.getNode().nextSubscript(current);
            
            return next != null && !"".equals(next);
        }
        
        /**
         * {@inheritDoc}
         */
        public Node next() {
            if( reverse ) current = node.getNode().previousSubscript(current);
            else current = node.getNode().nextSubscript(current);
            
            return current != null || !"".equals(current) ? node.getSubNode(current) : null;
        }
        
        /**
         * {@inheritDoc}
         */
        public void remove() {
            String previous = current;
            
            if( reverse ) current = node.getNode().previousSubscript(current);
            else current = node.getNode().nextSubscript(current);
            
            if( current != null || !"".equals(current) ) node.getNode().kill(previous);
        }
        
        /**
         * {@inheritDoc}
         * 
         * <p>Indicates where at and node iterating through</p>
         */
        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            if( reverse ) buf.append("Reverse");
            buf.append("NodeIterator@");
            buf.append(current);
            buf.append(" from ");
            buf.append(node);
            return buf.toString();
        }
    }
}
