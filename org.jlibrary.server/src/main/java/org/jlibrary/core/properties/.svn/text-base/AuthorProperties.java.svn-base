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
 * Properties for an author
 */
public class AuthorProperties extends GenericProperties implements Serializable {
	
	private static final long serialVersionUID = 6740350830646582037L;	

	public static final String AUTHOR_ID = "author.id";
	public static final String AUTHOR_NAME = "author.name";
    public static final String AUTHOR_BIO = "author.bio";
    public static final String AUTHOR_REPOSITORY = "author.repository";
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkPropertyDefinition(java.lang.Object)
	 */
	protected void checkPropertyDefinition(Object key) throws PropertyNotFoundException {

		if (!(key.equals(AUTHOR_ID)) && 
			!(key.equals(AUTHOR_NAME)) && 
			!(key.equals(AUTHOR_BIO)) && 
			!(key.equals(AUTHOR_REPOSITORY))) {
			throw new PropertyNotFoundException("Property " + key + " not found");
		}
	}
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkType(org.jlibrary.core.properties.PropertyDef)
	 */
	protected void checkType(Object key, PropertyDef property) throws InvalidPropertyTypeException {
		
		int type = -1;
		
		if (key.equals(AUTHOR_NAME)) {
			type = PropertyType.STRING;
		} else if (key.equals(AUTHOR_BIO)) {
			type = PropertyType.STRING;
		} else if (key.equals(AUTHOR_ID)) {
			type = PropertyType.STRING;
		} else if (key.equals(AUTHOR_REPOSITORY)) {
			type = PropertyType.STRING;
		}
				
		if (property.getType() != type) {
			throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be " + type);
		}
	}
}
