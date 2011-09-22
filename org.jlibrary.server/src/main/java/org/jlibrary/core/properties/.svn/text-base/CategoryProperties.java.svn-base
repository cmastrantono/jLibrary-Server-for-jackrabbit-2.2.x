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
 * Properties for a category
 */
public class CategoryProperties extends GenericProperties {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 6961366032282291073L;
	
	public static final String CATEGORY_ID = "category.id";
	public static final String CATEGORY_NAME = "category.name";
	public static final String CATEGORY_DESCRIPTION = "category.description";
	public static final String CATEGORY_PARENT = "category.parent";
	public static final String CATEGORY_REPOSITORY = "category.repository";

	public static final String CATEGORY_ADD_NODE = "category.add.node";
	public static final String CATEGORY_REMOVE_NODE = "category.remove.node";

	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkPropertyDefinition(java.lang.Object)
	 */
	protected void checkPropertyDefinition(Object key) throws PropertyNotFoundException {

		if (!(key.equals(CATEGORY_ID)) &&
			!(key.equals(CATEGORY_NAME)) && 
			!(key.equals(CATEGORY_REPOSITORY)) &&
			!(key.equals(CATEGORY_PARENT)) &&
			!(key.equals(CATEGORY_ADD_NODE)) && 
			!(key.equals(CATEGORY_REMOVE_NODE)) && 			
			!(key.equals(CATEGORY_DESCRIPTION))) {
			throw new PropertyNotFoundException("Property " + key + " not found");
		}
	}
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkType(org.jlibrary.core.properties.PropertyDef)
	 */
	protected void checkType(Object key, PropertyDef property) throws InvalidPropertyTypeException {
		
		int type = -1;
		
		if (key.equals(CATEGORY_ID)) {
			type = PropertyType.STRING;
		} else if (key.equals(CATEGORY_NAME)) {
			type = PropertyType.STRING;
		} else if (key.equals(CATEGORY_DESCRIPTION)) {
			type = PropertyType.STRING;
		} else if (key.equals(CATEGORY_PARENT)) {
			type = PropertyType.STRING;
		} else if (key.equals(CATEGORY_ADD_NODE)) {
			type = PropertyType.STRING;	
		} else if (key.equals(CATEGORY_REMOVE_NODE)) {
			type = PropertyType.STRING;				
		} else if (key.equals(CATEGORY_REPOSITORY)) {
			type = PropertyType.STRING;
		}

		if (property.getValue() == null) {
			if (key.equals(CATEGORY_PARENT)) {
				return;
			}
		}

		
		if (property.getType() != type) {
			throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be " + type);
		}
	}
}
