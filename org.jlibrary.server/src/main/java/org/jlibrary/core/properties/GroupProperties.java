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
public class GroupProperties extends GenericProperties {
		
	public static final String GROUP_ID = "group.id";	
	public static final String GROUP_REPOSITORY = "group.repository";	
	public static final String GROUP_ADD_USER = "group.add.user";
	public static final String GROUP_DELETE_USER = "group.delete.user";
	public static final String GROUP_ADD_ROL = "group.add.rol";
	public static final String GROUP_DELETE_ROL = "group.delete.rol";
	public static final String GROUP_ADD_RESTRICTION = "group.add.restriction";
	public static final String GROUP_DELETE_RESTRICTION = "group.delete.restriction";
	public static final String GROUP_NAME = "group.name";
	public static final String GROUP_DESCRIPTION = "group.description";

	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkPropertyDefinition(java.lang.Object)
	 */
	protected void checkPropertyDefinition(Object key) throws PropertyNotFoundException {

		if (!(key.equals(GROUP_ADD_USER)) && 
			!(key.equals(GROUP_DELETE_USER)) &&
			!(key.equals(GROUP_ADD_ROL)) && 
			!(key.equals(GROUP_DELETE_ROL)) &&
			!(key.equals(GROUP_ADD_RESTRICTION)) && 
			!(key.equals(GROUP_DELETE_RESTRICTION)) &&
			!(key.equals(GROUP_NAME)) &&
			!(key.equals(GROUP_REPOSITORY)) && 
			!(key.equals(GROUP_DESCRIPTION)) &&
			!(key.equals(GROUP_ID))) {
			throw new PropertyNotFoundException("Property " + key + " not found");
		}
	}
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkType(org.jlibrary.core.properties.PropertyDef)
	 */
	protected void checkType(Object key, PropertyDef property) throws InvalidPropertyTypeException {
		
		int type = -1;
		
		if (key.equals(GROUP_ADD_USER)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(GROUP_DELETE_USER)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(GROUP_ADD_ROL)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(GROUP_DELETE_ROL)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(GROUP_ADD_RESTRICTION)) {
			type = PropertyType.STRING;
		} else if (key.equals(GROUP_DELETE_RESTRICTION)) {
			type = PropertyType.STRING;
		} else if (key.equals(GROUP_NAME)) {
			type = PropertyType.STRING;
		} else if (key.equals(GROUP_DESCRIPTION)) {
			type = PropertyType.STRING;
		} else if (key.equals(GROUP_REPOSITORY)) {
			type = PropertyType.STRING;
		} else if (key.equals(GROUP_ID)) {
			type = PropertyType.STRING;
		}
				
		if (property.getType() != type) {
			throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be " + type);
		}
	}
}
