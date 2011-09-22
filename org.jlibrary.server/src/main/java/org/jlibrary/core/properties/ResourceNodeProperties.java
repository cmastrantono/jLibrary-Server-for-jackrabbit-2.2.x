/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, and individual 
* contributors as indicated by the @authors tag. See copyright.txt in the
* distribution for a full listing of individual contributors.
* All rights reserved.
* 
* This is free software; you can redistribute it and/or modify it
* under the terms of the Modified BSD License as published by the Free 
* Software Foundation.
* 
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Modified
* BSD License for more details.
* 
* You should have received a copy of the Modified BSD License along with 
* this software; if not, write to the Free Software Foundation, Inc., 
* 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the
* FSF site: http://www.fsf.org.
*/
package org.jlibrary.core.properties;

import java.io.Serializable;

/**
 * @author Martin
 *
 * Properties for a ResourceNode
 */
public class ResourceNodeProperties extends GenericProperties 
									implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String RESOURCE_ID = "resource.id";
	public static final String RESOURCE_NAME = "resource.name";
	public static final String RESOURCE_TYPECODE = "resource.type";
	public static final String RESOURCE_CONTENT = "resource.content";
	public static final String RESOURCE_PATH = "resource.path";
	public static final String RESOURCE_PARENT_ID = "resource.parent.id";
	public static final String RESOURCE_DESCRIPTION = "resource.description";
	public static final String RESOURCE_POSITION = "resource.position";

	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkPropertyDefinition(java.lang.Object)
	 */
	protected void checkPropertyDefinition(Object key) throws PropertyNotFoundException {

		if (!(key.equals(RESOURCE_NAME)) && 
			!(key.equals(RESOURCE_PATH)) &&
			!(key.equals(RESOURCE_CONTENT)) &&
			!(key.equals(RESOURCE_TYPECODE)) && 
			!(key.equals(RESOURCE_ID)) &&
			!(key.equals(RESOURCE_PARENT_ID)) &&
			!(key.equals(RESOURCE_DESCRIPTION)) &&
			!(key.equals(RESOURCE_POSITION))) {
			throw new PropertyNotFoundException("Property " + key + " not found");
		}
	}
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkType(org.jlibrary.core.properties.PropertyDef)
	 */
	protected void checkType(Object key, PropertyDef property) throws InvalidPropertyTypeException {
		
		if (key.equals(RESOURCE_NAME)) {
			if (property.getType() != PropertyType.STRING) {
				throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be String ");
			}
		} else if (key.equals(RESOURCE_CONTENT)) {
			if (property.getType() != PropertyType.BINARY) {
				throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be binary. ");
			}
			
		} else if (key.equals(RESOURCE_ID)) {
			if (property.getType() != PropertyType.STRING) {
				throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be string. ");
			}
			
		} else if (key.equals(RESOURCE_TYPECODE)) {
			if (property.getType() != PropertyType.INTEGER) {
				throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be integer. ");
			}
		} else if (key.equals(RESOURCE_PATH)) {
			if (property.getType() != PropertyType.STRING) {
				throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be String. ");
			}
		} else if (key.equals(RESOURCE_PARENT_ID)) {
			if (property.getType() != PropertyType.STRING) {
				throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be String. ");
			}
		} else if (key.equals(RESOURCE_DESCRIPTION)) {
			if (property.getType() != PropertyType.STRING) {
				throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be String. ");
			}
		} else if (key.equals(RESOURCE_POSITION)) {
			if (property.getType() != PropertyType.INTEGER) {
				throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be String. ");
			}
		}
	}
}
