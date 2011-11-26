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

/**
 * This interface extends the node interface to represent the
 * root or parent node. There is nothing special in behaviour 
 * terms except that a release method is added. This should be
 * called when you've finished with the node in the current
 * thread to ensure all underlying resources are released.
 *
 * @author Tom Spencer
 */
public interface RootNode extends Node {
    
    /**
     * Call this method to commit any changes that have been made
     * to this node (or any part of it). This method does not
     * also release the node.
     * 
     * @param newTransaction If true a globals transaction is created for this update
     */
    public void commit(boolean newTransaction);
    
    /**
     * Call to discard any changes made to the node. This method
     * does not also release the node. This is a no-op if there is
     * no data changes
     */
    public void abort();
    
    /**
     * Call this method to release the parent node, thus ensuring
     * any underlying globals DB resources are released. This method
     * will fail if you have uncommitted or aborted changes
     * 
     * @return True if the release was performed, false if there is uncommited data
     */
    public boolean release();
}
