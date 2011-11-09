/**
 * Copyright (C) 2011 Tom Spencer <thegaffer@tpspencer.com>
 *
 * eclipse-globals is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * eclipse-globals is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with eclipse-globals. If not, see <http://www.gnu.org/licenses/>.
 */
package org.tpspencer.globals.navigator;

import java.util.List;

/**
 * Interface holds all the details about a particular node
 * in the Globals DB
 *
 * @author Tom Spencer
 */
public interface NodeInformation {

    /**
     * The full path in node/index1/index2 format
     * 
     * @return The full path of the global
     */
    public String getFullPath();
    
    /**
     * The tip is the name of the node or outer index
     * 
     * @return The name of the node or outer index
     */
    public String getName();
    
    /**
     * Determine if the node is a parent node, that is
     * has children
     * 
     * @return True if the node has children, false otherwise
     */
    public boolean isParentNode();
    
    /**
     * @return True if the node has data of its own (false otherwise)
     */
    public boolean isDataNode();
    
    /**
     * @return True if the node holds data and there is only 1 piece of data
     */
    public boolean isScalarNode();
    
    /**
     * The data held in the node as a display string
     * 
     * @return The data is display form
     */
    public String getDataDisplay();
    
    /**
     * Call to get the data in the stored form. If the node
     * only holds a scalar value then the array will only
     * hold 1 value
     * 
     * @return The data in its constituent parts
     */
    public List<Object> getData();
}
