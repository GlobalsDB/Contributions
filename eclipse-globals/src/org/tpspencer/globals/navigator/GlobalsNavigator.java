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
 * Interface to navigate globals with
 *
 * @author Tom Spencer
 */
public interface GlobalsNavigator {
    
    /**
     * @return The current page size
     */
    public int getPageSize();

    /**
     * Call to set the page size
     * 
     * @param pageSize The page size
     */
    public void setPageSize(int pageSize);
    
    /**
     * Call to determine if we are ascending or descending
     * (i.e. reverses next page/previous page)
     * 
     * @return True if ascending, false otherwise
     */
    public boolean isAscending();
    
    /**
     * Call to turn this navigator to ascend or descend
     * 
     * @param ascending
     */
    public void setAscending(boolean ascending);
    
    /**
     * Call to effectively go in to node/index indicate by name
     * 
     * @param name The node name/index to go into
     */
    public void appendIndex(String name);
    
    /**
     * Call to go back out to parent index/node/root
     */
    public void removeIndex();
    
    /**
     * @return True if there is/was a previous page
     */
    public boolean isPrevious();
    
    /**
     * @return True if there is a next page
     */
    public boolean isNext();
    
    /**
     * Get the current page
     * 
     * @return The current page
     */
    public List<NodeInformation> getPage();
    
    /**
     * Get the next page from where we are now
     * 
     * @return The next page
     */
    public List<NodeInformation> getNextPage();
    
    /**
     * Get the previous page from where we are now
     * 
     * @return The previous page
     */
    public List<NodeInformation> getPreviousPage();
}
