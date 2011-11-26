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

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tpspencer.globals.wrapper.Node;
import org.tpspencer.globals.wrapper.NodeMapping;
import org.tpspencer.globals.wrapper.RootNode;
import org.tpspencer.globals.wrapper.NodeMapping.NodeType;
import org.tpspencer.globals.wrapper.NodeMapping.NodeValue;

import com.intersys.globals.Connection;
import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;

/**
 * Tests the NodeImpl class
 *
 * @author Tom Spencer
 */
public class TestNode {
    
    private Connection connection;
    private NodeReference reference;
    private RootNode root;
    
    @Before
    public void setup() {
        connection = mock(Connection.class);
        reference = mock(NodeReference.class);
        
        when(connection.createNodeReference("test")).thenReturn(reference);
        root = new RootNodeImpl(connection, "test");
        verify(connection).createNodeReference("test");
    }
    
    @After
    public void teardown() {
        root.release();
    }

    /**
     * Simple test for core non-mapping functionality
     */
    @Test
    public void basic() {
        // Creation
        assertEquals("test", root.getName());
        assertEquals("test", root.getPath());
        
        // Basics
        when(reference.exists()).thenReturn(false);
        when(reference.hasSubnodes()).thenReturn(true);
        assertFalse(root.isDataNode());
        assertTrue(root.isParentNode());
        assertFalse(root.isEmpty());
        
        // Ensure no size as no mapping so not known
        assertEquals(0, root.keySet().size());
        assertEquals(0, root.size());
        
        // Ensure we get null as direct value (as exists false)
        assertNull(root.get(null));
    }
    
    /**
     * Just ensures we get correct data from a sub-node using
     * the map interface
     */
    @Test
    public void simpleGetData() {
        when(reference.getObject()).thenReturn("XYZ");
        when(reference.exists()).thenReturn(true);
        assertEquals("XYZ", root.get("subscript"));
        
        verify(reference, times(1)).appendSubscript("subscript");
    }
    
    /**
     * Ensures we can iterate successfully
     */
    @Test
    public void iterate() {
        when(reference.nextSubscript("")).thenReturn("object1");
        when(reference.nextSubscript("object3")).thenReturn("object2");
        when(reference.nextSubscript("object1")).thenReturn("object3");
        when(reference.nextSubscript("object2")).thenReturn("");
        
        // Initial run to output for confirmation
        Iterator<Node> it = root.iterator();
        while( it.hasNext() ) {
            System.out.println("Found: " + it.next());
        }
        
        // Actual test
        it = root.iterator();
        assertEquals("test/object1", it.next().getPath());
        assertEquals("test/object3", it.next().getPath());
        assertEquals("test/object2", it.next().getPath());
        assertFalse(it.hasNext());
    }
    
    /**
     * Ensures we get properties via the map interface even when
     * inside a list.
     */
    @Test
    public void getWithMapping() {
        NodeMapping mapping = setupObject();
        
        ValueList lst = mock(ValueList.class);
        when(reference.exists()).thenReturn(true);
        when(reference.getList()).thenReturn(lst);
        when(lst.getAll()).thenReturn(new Object[]{"Fred Bloggs", 123456789, 245.99});
        
        Node objectNode = root.getSubNode("object");
        objectNode.setMapping(mapping);
        assertEquals(3, objectNode.size());
        assertEquals("Fred Bloggs", objectNode.get("name"));
        assertEquals(123456789, objectNode.get("accountNos"));
        assertEquals(245.99, objectNode.get("balance"));
        
        verify(reference, times(1)).appendSubscript("object");
        
        // Now get the extra prop
        when(reference.getObject()).thenReturn("Some account notes ...");
        assertEquals("Some account notes ...", objectNode.get("notes"));
        verify(reference, times(2)).appendSubscript("object"); // 2 because counts above also!
        verify(reference, times(1)).appendSubscript("notes");
    }
    
    /**
     * Ensures we can remove a value from a node
     */
    @Test
    public void remove() {
        when(reference.exists()).thenReturn(true);
        when(reference.getObject()).thenReturn("Name");
        
        Node node = root.getSubNode("simple");
        assertEquals("Name", node.get(null));
        
        node.remove(null);
        assertNull(node.get(null));
        
        root.commit(true);
        verify(connection).startTransaction();
        verify(reference).killNode();
        verify(connection).commit();
    }
    
    /**
     * Ensures we can set a nodes values and those values are stored up
     * until the commit
     */
    @Test
    public void setObject() {
        NodeMapping mapping = setupObject();
        
        Node objectNode = root.getSubNode("object");
        assertFalse(objectNode == root.getSubNode("object")); // Until changes made this will be new instance
        objectNode.setMapping(mapping);
        objectNode.put("name", "Peter Smith");
        assertEquals("Peter Smith", objectNode.get("name"));
        objectNode.put("accountNos", 987654321);
        assertEquals(987654321, objectNode.get("accountNos"));
        objectNode.put("balance", 50.00);
        assertEquals(50.00, objectNode.get("balance"));
        objectNode.put("notes", "Some notes");
        assertEquals("Some notes", objectNode.get("notes"));
        
        verify(reference, times(2)).appendSubscript("object");
        verify(reference, times(1)).appendSubscript("notes");
        
        // Ensure we get this node back now changes have been made
        assertTrue(objectNode == root.getSubNode("object"));
        
        // Now ensure we save ok
        ValueList lst = mock(ValueList.class);
        when(connection.createList()).thenReturn(lst);
        
        root.commit(true);
        
        verify(connection).startTransaction();
        verify(lst).append("Peter Smith", 987654321, 50.00);
        verify(reference).set(lst);
        verify(reference).set("Some notes");
        verify(connection).commit();
    }
    
    /**
     * Ensures we get an exception if we try to use containersValue
     */
    @Test(expected=UnsupportedOperationException.class)
    public void throwOnContainsValue() {
        RootNode root = new RootNodeImpl(connection, "test");
        root.containsValue(123);
    }
    
    /**
     * Private helper to setup object node
     */
    private NodeMapping setupObject() {
        NodeMapping mapping = mock(NodeMapping.class);
        NodeValue name = mock(NodeValue.class);
        NodeValue account = mock(NodeValue.class);
        NodeValue balance = mock(NodeValue.class);
        NodeValue notes = mock(NodeValue.class);
        
        Set<String> props = new HashSet<String>();
        props.add("name"); props.add("accountNos"); props.add("balance");
        
        when(mapping.getNodeType()).thenReturn(NodeType.COMPLEX_OBJECT);
        when(mapping.getNodeValue("name")).thenReturn(name);
        when(mapping.getNodeValue("accountNos")).thenReturn(account);
        when(mapping.getNodeValue("balance")).thenReturn(balance);
        when(mapping.getNodeValue("notes")).thenReturn(notes);
        when(mapping.getKeyNames()).thenReturn(props);
        when(mapping.getTotalFieldsInList()).thenReturn(3);
        when(name.isSubNode()).thenReturn(false);
        when(name.getPosition()).thenReturn(0);
        when(account.isSubNode()).thenReturn(false);
        when(account.getPosition()).thenReturn(1);
        when(balance.isSubNode()).thenReturn(false);
        when(balance.getPosition()).thenReturn(2);
        when(notes.isSubNode()).thenReturn(true);
        
        return mapping;
    }
}
