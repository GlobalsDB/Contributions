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

import org.junit.Assert;
import org.junit.Test;
import org.tpspencer.globals.wrapper.Node;
import org.tpspencer.globals.wrapper.NodeMapping;
import org.tpspencer.globals.wrapper.RootNode;
import org.tpspencer.globals.wrapper.mapping.ObjectNodeMappingFactory;

/**
 * This is an integration test over the NodeWrapper
 * connecting directly to Globals
 *
 * @author Tom Spencer
 */
public class NodeWrapperITCase {
    
    /** Max number of nodes to iterate within a node */
    private final static int MAX = 1000;
    
    @Test
    public void basic() {
        NodeService service = new NodeService();
        
        int total = 0;
        Iterator<RootNode> it = service.iterator();
        while( it.hasNext() && total < 100 ) {
            RootNode root = it.next();
            System.out.println(root + "=" + root.get(null));
            total += 1;
            iterateNode(root, total);
        }
    }
    
    @Test
    public void storeObject() {
        NodeService service = new NodeService();
        NodeMapping mapping = ObjectNodeMappingFactory.create(SimpleBean.class);
        
        RootNode root = service.getNode("nodeWrapperTest");
        Node node = root.getSubNode("objects").getSubNode("1");
        node.setMapping(mapping);
        node.put("name", "Tom Spencer");
        node.put("accountNos", 555666777);
        node.put("balance", 250.00);
        node.put("notes", "Some notes ...");
        root.commit(true);
        
        // Now check it has saved
        root = service.getNode("nodeWrapperTest");
        node = root.getSubNode("objects").getSubNode("1");
        // System.out.println("Value: " + node.get(null));
        Assert.assertEquals("Some notes ...", node.get("notes"));
        
        // Now delete
        //service.getConnection().createNodeReference("nodeWrapperTest").kill();
    }
    
    /**
     * Helper to recursively iterate around a node
     */
    private int iterateNode(Node node, int total) {
        Iterator<Node> it = node.iterator();
        while( it.hasNext() && total < MAX ) {
            Node newNode = it.next();
            System.out.println(newNode + "=" + newNode.get(null));
            total += 1;
            total = iterateNode(newNode, total);
        }
        
        if( it.hasNext() && total >= MAX ) System.out.println("Stopping Output ...");
        
        return total;
    }
}
