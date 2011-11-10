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

import org.tpspencer.globals.wrapper.NodeMapping.NodeValue;

/**
 * Basic and simple implementation of the NodeValue interface
 *
 * @author Tom Spencer
 */
public class NodeValueImpl implements NodeValue {
    
    private final String name;
    private final boolean subNode;
    private final int position;
    
    public NodeValueImpl(String name, int position) {
        this.name = name;
        this.subNode = position < 0;
        this.position = position;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public int getPosition() {
        return position;
    }
    
    @Override
    public boolean isSubNode() {
        return subNode;
    }
}
