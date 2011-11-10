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

import java.util.Iterator;

import org.tpspencer.globals.wrapper.RootNode;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersys.globals.GlobalsDirectory;
import com.intersys.globals.NodeReference;

/**
 * This singleton class provides access to the root nodes in the
 * Globals DB engine. This service does not need to be used, but
 * if you want a single globals connection to be shared by all
 * nodes then it is useful to use this class so that you only
 * tell NodeWrapper about the connection once.
 * 
 * FUTURE: Keep track of root nodes so it is one call to release??
 *
 * @author Tom Spencer
 */
public class NodeService {
    
    /** The connection used by this service */
    private final Connection connection;
    /** Determines if connection is owned - if so closed when we release */
    private final boolean isOwned;
    /** The globals directory, only obtained once and released in release */
    private GlobalsDirectory directory;
    
    public NodeService() {
        this(null);
    }
    
    /**
     * Constructs the NodeService. If the supplied connection is already
     * connected then this service just uses it. Otherwise it will create
     * and/or connect to the Globals DB service and 'own' the connection,
     * meaning it will close it when you release this service.
     * 
     * @param connection The connection to use
     */
    public NodeService(Connection connection) {
        this.connection = connection != null ? connection : ConnectionContext.getConnection();
        this.isOwned = !this.connection.isConnected();
        
        // If we own this connection, then connect
        if( this.isOwned ) this.connection.connect("", "", "");
    }
    
    /**
     * @return The connection in case needed for other purposes
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Call to get a root node by name
     * 
     * @param name The name of the root node
     * @return The root node
     */
    public RootNode getNode(String name) {
        return new RootNodeImpl(connection, name);
    }
    
    /**
     * @return An root node iterator from the start of all root globals
     */
    public Iterator<RootNode> iterator() {
        if( this.directory == null ) this.directory = connection.createGlobalsDirectory();
        return new RootNodeIterator("", false);
    }
    
    /**
     * @return An root node iterator from given node name
     */
    public Iterator<RootNode> iterateFrom(String name) {
        if( this.directory == null ) this.directory = connection.createGlobalsDirectory();
        return new RootNodeIterator(name, false);
    }
    
    /**
     * @return An root node iterator from the end of all root globals
     */
    public Iterator<RootNode> reverseIterator() {
        if( this.directory == null ) this.directory = connection.createGlobalsDirectory();
        return new RootNodeIterator("", true);
    }
    
    /**
     * @return An root node iterator from given node name that will go backwards
     */
    public Iterator<RootNode> reverseIterateFrom(String name) {
        if( this.directory == null ) this.directory = connection.createGlobalsDirectory();
        return new RootNodeIterator(name, true);
    }
    
    /**
     * Call when finished with the service to release the connection.
     * 
     * <p><b>Note: </b>It is still your responsibility to have released
     * any root nodes obtained - the service does not keep track of this.</p>
     */
    public void release() {
        if( this.directory != null ) this.directory.close();
        if( this.isOwned ) this.connection.close();
    }
    
    /**
     * Implements the iterator interface around the root node
     *
     * @author Tom Spencer
     */
    private class RootNodeIterator implements Iterator<RootNode> {
        
        private String current;
        private boolean reverse;
        
        public RootNodeIterator(String from, boolean reverse) {
            this.current = from;
            if( this.current == null ) this.current = "";
            this.reverse = reverse;
        }
        
        @Override
        public boolean hasNext() {
            String next = reverse ? directory.previousGlobalName(current) : directory.nextGlobalName(current);
            return next != null && !"".equals(next);
        }
        
        @Override
        public RootNode next() {
            current = reverse ? directory.previousGlobalName(current) : directory.nextGlobalName(current);
            if( current == null || "".equals(current) ) return null;
            return new RootNodeImpl(connection, current);
        }
        
        @Override
        public void remove() {
            if( current == null || "".equals(current) ) return;
            
            NodeReference ref = connection.createNodeReference(current);
            ref.kill();
            
            directory.refresh();
        }
        
        @Override
        public String toString() {
            return reverse ? "ReverseRootNodeIterator@" + current : "RootNodeIterator@" + current;
        }
    }
}
