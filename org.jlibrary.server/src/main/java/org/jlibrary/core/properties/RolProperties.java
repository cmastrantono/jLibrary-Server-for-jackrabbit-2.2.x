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
 * Properties for a document
 */
public class RolProperties extends GenericProperties {
		
	public static final String ROL_ID = "rol.id";
	public static final String ROL_NAME = "rol.name";
	public static final String ROL_DESCRIPTION = "rol.description";
	public static final String ROL_REPOSITORY = "rol.repository";

	public static final String ROL_ADD_USER = "rol.add.user";
	public static final String ROL_DELETE_USER = "rol.delete.user";

	public static final String ROL_ADD_GROUP = "rol.add.group";
	public static final String ROL_DELETE_GROUP = "rol.delete.group";
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkPropertyDefinition(java.lang.Object)
	 */
	protected void checkPropertyDefinition(Object key) throws PropertyNotFoundException {

		if (!(key.equals(ROL_NAME)) && 
			!(key.equals(ROL_REPOSITORY)) && 
			!(key.equals(ROL_DESCRIPTION)) &&
			!(key.equals(ROL_ID)) &&
			!(key.equals(ROL_ADD_USER)) &&
			!(key.equals(ROL_DELETE_USER))&&
			!(key.equals(ROL_ADD_GROUP)) &&
			!(key.equals(ROL_DELETE_GROUP))) {
			throw new PropertyNotFoundException("Property " + key + " not found");
		}
	}
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkType(org.jlibrary.core.properties.PropertyDef)
	 */
	protected void checkType(Object key, PropertyDef property) throws InvalidPropertyTypeException {
		
		int type = -1;
		
		if (key.equals(ROL_NAME)) {
			type = PropertyType.STRING;
		} else if (key.equals(ROL_DESCRIPTION)) {
			type = PropertyType.STRING;
		} else if (key.equals(ROL_REPOSITORY)) {
			type = PropertyType.STRING;
		} else if (key.equals(ROL_ID)) {
			type = PropertyType.STRING;
		} else if (key.equals(ROL_ADD_USER)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(ROL_DELETE_USER)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(ROL_ADD_GROUP)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(ROL_DELETE_GROUP)) {
			type = PropertyType.OBJECT;
		}
				
		if (property.getType() != type) {
			throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be " + type);
		}
	}
}
