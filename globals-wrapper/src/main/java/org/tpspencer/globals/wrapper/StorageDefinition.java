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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.tpspencer.globals.wrapper.mapping.ObjectNodeMappingFactory;

/**
 * This annotation can be placed on the getter for any property
 * to provide a hint as to how it should be stored in the Globals
 * DB. This is used by the {@link ObjectNodeMappingFactory} class
 * to construct the NodeMapping.
 * 
 * <p><b>Note: </b>As per the notes in {@link ObjectNodeMappingFactory}
 * I do not really like these annotations because they are imparting
 * specific persistence information into your object model making it
 * seem more difficult to move DBs, but they are provided for use.</p>
 *
 * @author Tom Spencer
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StorageDefinition {

    /** If true, then value is held as a sub-node otherwise in list */
    public boolean subNode() default false;
    /** If not a subNode, then holds position in list for this field */
    public int position() default -1;
}
