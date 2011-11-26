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

import java.util.ArrayList;
import java.util.List;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersys.globals.GlobalsDirectory;
import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;

/**
 * This class navigates around a Globals DB. At each point it
 * will return a page of NodeDescription objects (an inner
 * class) to describe the node. Configuration objects exist
 * to control the order of navigation (up/down) and the page
 * size.
 *
 * @author Tom Spencer
 */
public class GlobalsNavigatorImpl implements GlobalsNavigator {
    
    /** Holds the connection to globals */
    private final Connection connection;
    /** Determines if connection is owned and will therefore be closed in release */
    private final boolean ownedConnection;
    
    /** The configurable (max) page size */
    private int pageSize = 100;
    /** The configurable ascending or descending order flag */
    private boolean ascending = true;
    
    /** Holds where the navigator is looking */
    private NavigationNode currentNode;
    /** Holds the temp node */
    private NodeReference tempNode;
    /** Holds a temporary value list that we use */
    private final ValueList temporaryList;
    
    
    /**
     * Constructs a navigator that will create it's own connection
     * to Globals. If there is no an open connect then this navigator
     * treats the connection as owned and will close when this
     * object is released.
     */
    public GlobalsNavigatorImpl() {
        this.connection = ConnectionContext.getConnection();
        if( !connection.isConnected() ) {
            connection.connect("", "", "");
            this.ownedConnection = true;
        }
        else {
            this.ownedConnection = false;
        }
        
        // Create the temp list for future use
        this.temporaryList = connection.createList();
        this.currentNode = new NavigationNode();
    }
    
    /**
     * Constructs a navigator using the given (shared) connection.
     * It is assumed the connection is already connected.
     * 
     * @param connection The connection to use
     */
    public GlobalsNavigatorImpl(Connection connection) {
        this.connection = connection;
        this.ownedConnection = false;
        this.temporaryList = connection.createList();
        this.currentNode = new NavigationNode();
    }
    
    /**
     * Closes the temporary value list, the node (if there is one)
     * and the connection (if owned). This ensures no mem leaks in
     * the underlying Globals DB impl.
     */
    public void release() {
        this.temporaryList.close();
        if( this.tempNode != null ) this.tempNode.close();
        if( this.ownedConnection ) this.connection.close();
    }
    
    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Setter for the pageSize field
     *
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the ascending
     */
    public boolean isAscending() {
        return ascending;
    }

    /**
     * Setter for the ascending field
     *
     * @param ascending the ascending to set
     */
    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
    
    
    /**
     * Call to set the navigator to a child of the current
     * node.
     * 
     * @param name The name of the index (or node if at root)
     */
    public void appendIndex(String name) {
        currentNode = new NavigationNode(currentNode, name);
        
        if( tempNode == null ) tempNode = connection.createNodeReference(name);
        else tempNode.appendSubscript(name);
    }
    
    /**
     * Call to get back 1 node
     */
    public void removeIndex() {
        currentNode = currentNode.getParent();
        
        if( currentNode.getParent() == null && tempNode != null ) {
            tempNode.close();
            tempNode = null;
        }
        else if( tempNode != null ) {
            tempNode.setSubscriptCount(tempNode.getSubscriptCount()-1);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNext() {
        return currentNode.hasNext();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPrevious() {
        return currentNode.hasPrevious();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<NodeInformation> getPage() {
        List<NodeInformation> ret = new ArrayList<NodeInformation>();
        
        String last = null;
        if( currentNode.getParent() == null ) last = getPageRoot(ret, currentNode.getStart());
        else last = getPageNode(ret, currentNode.getStart());
        currentNode.setNavigation(currentNode.getStart(), last);
        
        return ret;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<NodeInformation> getNextPage() {
        List<NodeInformation> ret = new ArrayList<NodeInformation>();
        
        String last = null;
        if( currentNode.getParent() == null ) last = getPageRoot(ret, currentNode.getEnd());
        else last = getPageNode(ret, currentNode.getEnd());
        currentNode.setNavigation(currentNode.getEnd(), last);
        
        return ret;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<NodeInformation> getPreviousPage() {
        // TODO: Not yet supported
        return null;
    }
    
    /**
     * Helper to navigate the root nodes
     */
    private String getPageRoot(List<NodeInformation> nodeList, String first) {
        GlobalsDirectory dir = connection.createGlobalsDirectory();
        String name = ascending ? dir.nextGlobalName(first) : dir.previousGlobalName(first);
        int totalNodes = 0;
        while( name != null && name.length() > 0 && totalNodes < pageSize ) {
            NodeReference node = connection.createNodeReference(name);
            nodeList.add(new NodeInformationImpl(null, name, getNodeData(node), node.hasSubnodes()));
            name = ascending ? dir.nextGlobalName(name) : dir.previousGlobalName(name);
            node.close();
            totalNodes++;
        }
        
        return name;
    }
    
    /**
     * Helper to navigate a node itself
     */
    private String getPageNode(List<NodeInformation> nodeList, String first) {
        String ret = null;
        
        try {
            List<String> path = currentNode.getFullPath();
            
            tempNode.appendSubscript(first);
            String name = ascending ? tempNode.nextSubscript() : tempNode.previousSubscript();
            int totalNodes = 0;
            while( name != null && name.length() > 0 && totalNodes < pageSize ) {
                tempNode.setSubscript(tempNode.getSubscriptCount(), name);
                nodeList.add(new NodeInformationImpl(path, name, getNodeData(tempNode), tempNode.hasSubnodes()));
                name = ascending ? tempNode.nextSubscript() : tempNode.previousSubscript();
                totalNodes++;
            }
            
            ret = name;
        }
        finally {
            tempNode.setSubscriptCount(tempNode.getSubscriptCount()-1);
        }
        
        return ret;
    }

    private List<Object> getNodeData(NodeReference node) {
        if( !node.exists() ) return null;
        
        List<Object> ret = new ArrayList<Object>();
        try {
            // ValueList lst = node.getList(temporaryList);
            ValueList lst = node.getList();
            for( int i = 0 ; i < lst.length() ; i++ ) ret.add(lst.getNextObject());
            lst.close();
        }
        catch( Exception e ) {
            ret.add(node.getObject());
        }
        
        return ret;
    }
    
    private static class NavigationNode {
        /** The parent node (if any) */
        private final NavigationNode parent;
        /** The name of the current node */
        private final String current;
        /** The first element to start from (if null or "" then at start) */
        private String subStart;
        /** The end element (where to start next page from) */
        private String subEnd;
        
        public NavigationNode() {
            this.parent = null;
            this.current = null;
            subStart = null;
            subEnd = null;
        }
        
        public NavigationNode(NavigationNode parent, String name) {
            this.parent = parent;
            this.current = name;
            subStart = null;
            subEnd = null;
        }
        
        public NavigationNode getParent() {
            return parent;
        }
        
        public boolean hasPrevious() {
            return (subStart != null && subStart.length() > 0);
        }
        
        public boolean hasNext() {
            return (subEnd != null && subEnd.length() > 0);
        }
        
        public List<String> getFullPath() {
            List<String> ret = parent != null ? parent.getFullPath() : new ArrayList<String>();
            if( current != null ) ret.add(current);
            return ret;
        }
        
        /**
         * Call after getting a page to set start and end point
         * appropriately.
         * 
         * @param start
         * @param end
         */
        public void setNavigation(String start, String end) {
            this.subStart = start;
            this.subEnd = end;
        }
        
        public String getStart() {
            return subStart != null ? subStart : "";
        }
        
        public String getEnd() {
            return subEnd != null ? subEnd : "";
        }
    }

    /**
     * This class describes a node.
     *
     * @author Tom Spencer
     */
    private static class NodeInformationImpl implements NodeInformation {
        /** The root subscripts */
        private final List<String> root;
        /** The name of the name */
        private final String name;
        /** The data held at the node (if any) */
        private final List<Object> data;
        /** Whether the node has child subnodes */
        private final boolean children;
        
        public NodeInformationImpl(List<String> root, String name, List<Object> data, boolean children) {
            this.root = root;
            this.name = name;
            this.data = data;
            this.children = children;
        }
        
        @Override
        public String getFullPath() {
            if( root == null || root.size() == 0 ) return name;
            
            StringBuilder buf = new StringBuilder();
            for( String s : root ) buf.append('/').append(s);
            buf.append('/').append(name);
            return buf.toString();
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public boolean isParentNode() {
            return children;
        }
        
        @Override
        public boolean isDataNode() {
            return data != null && data.size() > 0;
        }
        
        @Override
        public boolean isScalarNode() {
            return data != null && data.size() == 1;
        }
        
        @Override
        public List<Object> getData() {
            return data;
        }
        
        @Override
        public String getDataDisplay() {
            if( data == null || data.size() == 0 ) return "";
            
            if( data.size() == 1 ) {
                return data.get(0).toString();
            }
            
            else {
                StringBuilder buf = new StringBuilder();
                for( Object d : data ) {
                    if( d != null ) {
                        if( buf.length() == 0 ) buf.append("[ "); 
                        else buf.append(", ");
                        buf.append(d.toString());
                    }
                }
                if( buf.length() > 0 ) buf.append(" ]");
                return buf.toString();
            }
        }
                
        @Override
        public String toString() {
            return getFullPath();
        }
    }
}
