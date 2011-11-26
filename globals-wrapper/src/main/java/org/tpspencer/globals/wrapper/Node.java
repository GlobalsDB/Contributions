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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This interface represents a node in the Globals DB. The
 * node interface extends a generic Map, which allows you
 * to treat the node as a map in cases where there are 
 * known children. However, there are also some additional 
 * methods to allow you to iterate children of this node for
 * instances where you don't.
 * 
 * <p><b>Note: </b>Instances of this interface are typically
 * not thread-safe. As they wrap the basic globals access they
 * do so by holding some temp objects that are shared by all
 * nodes.</p> 
 *
 * @author Tom Spencer
 */
public interface Node extends Map<String, Object> {

    /**
     * The full path this node represents
     * 
     * @return The path
     */
    public String getPath();
    
    /**
     * The path of this node as a list. If this is a root node then
     * the list will contain 1 entry.
     * 
     * @return The path of the node as a list
     */
    public List<String> getPathList();
    
    /**
     * The name of this node relative to its parent, i.e. the very
     * tip of the node.
     * 
     * @return The name of the node
     */
    public String getName();
    
    /**
     * Call to set a mapping on the node, which changes how it extracts
     * it's data.
     * 
     * @param mapping The mapping
     */
    public void setMapping(NodeMapping mapping);
    
    /**
     * Returns if this node has data directly within it
     * 
     * @return True if this node has data directly, false otherwise
     */
    public boolean isDataNode();
    
    /**
     * Returns if this node is a parent to other nodes (and
     * therefore you can use iterator and reverse_iterator).
     * 
     * @return True if this node is a parent node, false otherwise
     */
    public boolean isParentNode();
    
    /**
     * Call to get a sub-node when it is known directly
     * 
     * @param node The name of the subnode
     * @return The subnode
     */
    public Node getSubNode(String node);
    
    /**
     * Helper to increment the current value of the node. This is
     * done in place and will not wait for a commit.
     * 
     * <p><b>Note: </b>No checks are currently made to ensure this
     * is valid. If you set the value of this node (set(null)) in
     * any other way then this value will be set in commit overwriting
     * any incremented value.</p>
     * 
     * @param number The amount to increment by
     * @return The new number
     */
    public long increment(int number);
    
    /**
     * An iterator to iterate this nodes children
     * 
     * @return The iterator
     */
    public Iterator<Node> iterator();
    
    /**
     * An iterator to iterate this nodes children from the
     * given location (this point given does not need to
     * actually exist)
     * 
     * @param start The starting point
     * @return The iterator
     */
    public Iterator<Node> iterator(String start);
    
    /**
     * An iterator to iterate this nodes children in reverse
     * 
     * @return The reverse iterator
     */
    public Iterator<Node> reverseIterator();
    
    /**
     * An iterator to iterate this nodes children in reverse
     * from a given point (this point given does not need to
     * actually exist)
     * 
     * @param start The starting point
     * @return The reverse iterator
     */
    public Iterator<Node> reverseIterator(String start);
    
    
}
