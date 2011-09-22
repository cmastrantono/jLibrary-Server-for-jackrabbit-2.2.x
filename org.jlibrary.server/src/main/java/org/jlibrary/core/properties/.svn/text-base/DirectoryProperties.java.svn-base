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

/**
 * @author Martin
 *
 * Properties for a directory
 */
public class DirectoryProperties extends GenericProperties {
		
	public static final String DIRECTORY_ID = "directory.id";
	public static final String DIRECTORY_NAME = "directory.name";
	public static final String DIRECTORY_PARENT = "directory.parent";
	public static final String DIRECTORY_DESCRIPTION = "directory.description";
	public static final String DIRECTORY_POSITION = "directory.position";

	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkPropertyDefinition(java.lang.Object)
	 */
	protected void checkPropertyDefinition(Object key) throws PropertyNotFoundException {

		if (!(key.equals(DIRECTORY_NAME)) && 
			!(key.equals(DIRECTORY_DESCRIPTION)) &&
			!(key.equals(DIRECTORY_POSITION)) &&
			!(key.equals(DIRECTORY_PARENT)) &&
			!(key.equals(DIRECTORY_ID))) {
			throw new PropertyNotFoundException("Property " + key + " not found");
		}
	}
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkType(org.jlibrary.core.properties.PropertyDef)
	 */
	protected void checkType(Object key, PropertyDef property) throws InvalidPropertyTypeException {
		
		int type = -1;
		
		if (key.equals(DIRECTORY_NAME)) {
			type = PropertyType.STRING;
		} else if (key.equals(DIRECTORY_DESCRIPTION)) {
			type = PropertyType.STRING;
		} else if (key.equals(DIRECTORY_POSITION)) {
			type = PropertyType.INTEGER;
		} else if (key.equals(DIRECTORY_ID)) {
			type = PropertyType.STRING;
		} else if (key.equals(DIRECTORY_PARENT)) {
			type = PropertyType.STRING;
		}
				
		if (property.getType() != type) {
			throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be " + type);
		}
	}
}
