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
package org.tpspencer.globals.wrapper.mapping;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.tpspencer.globals.wrapper.NodeMapping;
import org.tpspencer.globals.wrapper.NodeMapping.NodeType;
import org.tpspencer.globals.wrapper.StorageDefinition;

/**
 * This factory takes bean classes in and turns them into
 * a NodeMapping that can be used in the Node wrapper.
 * 
 * <p>Note: I don't particularly recommend using this class,
 * and rather specify your node mapping manually. This is
 * so the class can change (order of properties etc, etc) - 
 * otherwise adding a new property can invalidate all stored
 * instances of this class! There is a StorageDefinition
 * annotation that can be used to fix the positions of 
 * fields, but I don't like this and, again, recommend you
 * construct your NodeMapping manually or using another
 * mechanism.</p> 
 *
 * @author Tom Spencer
 */
public class ObjectNodeMappingFactory {

    /**
     * Call to create a node mapping from a bean class
     * 
     * @param beanClass The bean objects class
     * @return The NodeMapping to use for this class.
     */
    public static NodeMapping create(Class<?> beanClass) {
        NodeMappingImpl mapping = new NodeMappingImpl(NodeType.COMPLEX_OBJECT);
        
        int nextPosition = 0;
        
        try {
            PropertyDescriptor[] props = Introspector.getBeanInfo(beanClass).getPropertyDescriptors();
            if( props != null ) {
                for( PropertyDescriptor prop : props ) {
                    if( prop.getName().equals("class") ) continue;
                    String name = prop.getName();
                    
                    StorageDefinition storage = prop.getReadMethod().getAnnotation(StorageDefinition.class);
                    if( storage != null ) {
                        if( storage.subNode() ) {
                            mapping.addProperty(new NodeValueImpl(name, -1));
                        }
                        else {
                            int pos = storage.position();
                            if( pos < 0 ) pos = nextPosition;
                            mapping.addProperty(new NodeValueImpl(name, pos));
                            nextPosition = pos + 1;
                        }
                    }
                    else {
                        mapping.addProperty(new NodeValueImpl(name, nextPosition));
                        nextPosition += 1;
                    }
                }
            }
        }
        catch( Exception e ) {
            throw new RuntimeException(e);
        }
               
        return mapping;
    }
}
