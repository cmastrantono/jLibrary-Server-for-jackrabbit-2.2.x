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
 * Properties for a document
 */
public class UserProperties extends GenericProperties implements Serializable {
	
	private static final long serialVersionUID = 1418160318519391914L;
	
	
	public static final String USER_ADD_GROUP = "user.add.group";
	public static final String USER_DELETE_GROUP = "user.delete.group";
	public static final String USER_ADD_ROL = "user.add.rol";
	public static final String USER_DELETE_ROL = "user.delete.rol";
	public static final String USER_ADD_RESTRICTION = "user.add.restriction";
	public static final String USER_DELETE_RESTRICTION = "user.delete.restriction";

	public static final String USER_ID = "user.id";
	public static final String USER_REPOSITORY = "user.repository";
	public static final String USER_FIRSTNAME = "user.firstname";
	public static final String USER_LASTNAME = "user.lastname";
	public static final String USER_NAME = "user.name";
        public static final String USER_EMAIL = "user.email";
	public static final String USER_PASSWORD = "user.password";
	public static final String USER_ADMIN = "user.admin";
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkPropertyDefinition(java.lang.Object)
	 */
	protected void checkPropertyDefinition(Object key) throws PropertyNotFoundException {

		if (!(key.equals(USER_ADD_GROUP)) && 
			!(key.equals(USER_DELETE_GROUP)) && 
			!(key.equals(USER_ADD_ROL)) && 
			!(key.equals(USER_DELETE_ROL)) && 
			!(key.equals(USER_ADD_RESTRICTION)) && 
			!(key.equals(USER_DELETE_RESTRICTION)) && 
			!(key.equals(USER_FIRSTNAME)) && 
			!(key.equals(USER_LASTNAME)) && 
			!(key.equals(USER_NAME)) &&
            !(key.equals(USER_EMAIL)) &&
			!(key.equals(USER_ADMIN)) &&
			!(key.equals(USER_ID)) &&
			!(key.equals(USER_REPOSITORY)) && 
			!(key.equals(USER_PASSWORD))) {
			throw new PropertyNotFoundException("Property " + key + " not found");
		}
	}
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkType(org.jlibrary.core.properties.PropertyDef)
	 */
	protected void checkType(Object key, PropertyDef property) throws InvalidPropertyTypeException {
		
		int type = -1;
		
		if (key.equals(USER_ADD_ROL)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(USER_DELETE_ROL)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(USER_ADD_RESTRICTION)) {
			type = PropertyType.STRING;
		} else if (key.equals(USER_DELETE_RESTRICTION)) {
			type = PropertyType.STRING;
		} else if (key.equals(USER_FIRSTNAME)) {
			type = PropertyType.STRING;
		} else if (key.equals(USER_LASTNAME)) {
			type = PropertyType.STRING;
		} else if (key.equals(USER_NAME)) {
			type = PropertyType.STRING;
                } else if (key.equals(USER_EMAIL)) {
			type = PropertyType.STRING;                        
		} else if (key.equals(USER_PASSWORD)) {
			type = PropertyType.STRING;
		} else if (key.equals(USER_ADD_GROUP)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(USER_DELETE_GROUP)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(USER_ADMIN)) {
			type = PropertyType.BOOLEAN;
		} else if (key.equals(USER_REPOSITORY)) {
			type = PropertyType.STRING;
		} else if (key.equals(USER_ID)) {
			type = PropertyType.STRING;
		}
				
		if (property.getType() != type) {
			throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be " + type);
		}
	}
}
