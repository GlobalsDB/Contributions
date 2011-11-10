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

import java.util.List;

import org.tpspencer.globals.wrapper.RootNode;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;

/**
 * The root node represents the very root of Globals. A
 * set of nodes always point back to the root node.
 * 
 * @author Tom Spencer
 */
public class RootNodeImpl extends NodeImpl implements RootNode {

    /** Holds the connection to globals */
    private final Connection connection;
    /** Determines if the connection is owned by this parent node */
    private final boolean ownedConnection;
    /** Holds the node reference */
    private final NodeReference reference;
    /** Indicates where the NodeReference currently points to */
    private NodeImpl current;
    
    /** A temporary value list */
    private ValueList temporaryList;
    /** A temporary string builder */
    private StringBuilder temporaryBuffer;
    
    public RootNodeImpl(String name) {
        super(null, null, name, null);
        this.connection = ConnectionContext.getConnection();
        if( !this.connection.isConnected() ) {
            this.connection.connect("USER", "_SYSTEM", "");
            this.ownedConnection = true;
        }
        else {
            this.ownedConnection = false;
        }
        this.reference = connection.createNodeReference(name);
    }
    
    public RootNodeImpl(Connection connection, String name) {
        super(null, null, name, null);
        this.connection = connection;
        this.ownedConnection = false;
        this.reference = connection.createNodeReference(name);
    }
    
    @Override
    protected RootNodeImpl getRootNode() {
        return this;
    }
    
    /**
     * {@inheritDoc}
     * 
     * Overridden to add the root name and no further
     */
    @Override
    protected void appendPath(StringBuilder builder) {
        builder.append(subscript);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Overridden to add the root name and no further
     */
    @Override
    protected void appendPath(List<String> lst) {
        lst.add(subscript.toString());
    }
    
    /**
     * Gets the NodeReference for the appropriate node
     * 
     * @param subscripts
     * @return
     */
    protected NodeReference getNode(NodeImpl node) {
        NodeReference ret = null;
        
        // If already pointing to us, use it
        if( node == current ) {
            ret = reference;
        }
        
        // Otherwise build from the root
        else {
            reference.setSubscriptCount(0);
            List<String> paths = node.getPathList();
            for( int i = 1 ; i < paths.size() ; i++ ) {
                reference.appendSubscript(paths.get(i));
            }
            ret = reference;
        }
        
        // FUTURE: Possibly further enhancements if node in same tree as current reference
        
        return ret;
    }
    
    /**
     * @return A temporary value list to write into
     */
    public ValueList getTempList() {
        if( temporaryList == null ) temporaryList = connection.createList();
        return temporaryList;
    }
    
    /**
     * @return A temporary string builder for use in the nodes
     */
    public StringBuilder getTempBuffer() {
        if( temporaryBuffer == null ) temporaryBuffer = new StringBuilder();
        else temporaryBuffer.setLength(0);
        return temporaryBuffer;
    }
    
    ////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void commit(boolean newTransaction) {
        try {
            if( newTransaction ) connection.startTransaction();
            onCommit();
        }
        finally {
            if( newTransaction ) connection.commit();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void abort() {
        this.subNodes = null;
        this.currentValue = null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean release() {
        reference.close();
        if( ownedConnection ) {
            connection.close();
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return "RootNode: " + subscript;
    }
}
