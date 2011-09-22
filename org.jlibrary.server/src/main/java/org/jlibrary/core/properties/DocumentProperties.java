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
public class DocumentProperties extends GenericProperties {
	
	public static final String DOCUMENT_ID = "document.id";
	public static final String DOCUMENT_PARENT = "document.parent";	
	public static final String DOCUMENT_CONTENT = "document.content";
	
	public static final String DOCUMENT_ADD_RESOURCE = "document.add.resource";
	public static final String DOCUMENT_DELETE_RESOURCE = "document.delete.resource";
	public static final String DOCUMENT_ADD_NOTE = "document.add.note";
	public static final String DOCUMENT_UPDATE_NOTE = "document.update.note";
	public static final String DOCUMENT_DELETE_NOTE = "document.delete.note";
	public static final String DOCUMENT_ADD_CATEGORY = "document.add.category";
	public static final String DOCUMENT_DELETE_CATEGORY = "document.delete.category";
	public static final String DOCUMENT_ADD_RELATION = "document.add.relation";
	public static final String DOCUMENT_DELETE_RELATION = "document.delete.relation";
	
	public static final String DOCUMENT_TYPECODE = "document.typecode";
	public static final String DOCUMENT_NAME = "document.name";
	public static final String DOCUMENT_DESCRIPTION = "document.description";
	public static final String DOCUMENT_TITLE = "document.title";
	public static final String DOCUMENT_KEYWORDS = "document.keywords";
	public static final String DOCUMENT_URL = "document.url";
	public static final String DOCUMENT_AUTHOR = "document.author";
	public static final String DOCUMENT_IMPORTANCE = "document.importance";
	public static final String DOCUMENT_CREATOR = "document.creator";
	public static final String DOCUMENT_LANGUAGE = "document.language";
	public static final String DOCUMENT_PATH = "document.path";
	public static final String DOCUMENT_CREATION_DATE = "document.creation.date";
	public static final String DOCUMENT_UPDATE_DATE = "document.update.date";
	public static final String DOCUMENT_POSITION = "document.position";
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkPropertyDefinition(java.lang.Object)
	 */
	protected void checkPropertyDefinition(Object key) throws PropertyNotFoundException {

		if (! ( key.equals(DOCUMENT_CONTENT) ||
			key.equals(DOCUMENT_ADD_NOTE) ||
			key.equals(DOCUMENT_UPDATE_NOTE) ||
			key.equals(DOCUMENT_DELETE_NOTE) ||
			key.equals(DOCUMENT_ADD_CATEGORY) ||
			key.equals(DOCUMENT_DELETE_CATEGORY) ||
			key.equals(DOCUMENT_ADD_RELATION) ||
			key.equals(DOCUMENT_DELETE_RELATION) ||
			key.equals(DOCUMENT_NAME) ||
			key.equals(DOCUMENT_DESCRIPTION) ||
			key.equals(DOCUMENT_TITLE) ||
			key.equals(DOCUMENT_KEYWORDS) ||
			key.equals(DOCUMENT_URL) ||
			key.equals(DOCUMENT_AUTHOR) ||
			key.equals(DOCUMENT_IMPORTANCE) ||
			key.equals(DOCUMENT_CREATOR) ||
			key.equals(DOCUMENT_LANGUAGE)  ||
			key.equals(DOCUMENT_PARENT) ||
			key.equals(DOCUMENT_PATH) ||
			key.equals(DOCUMENT_CREATION_DATE) ||
			key.equals(DOCUMENT_UPDATE_DATE) ||
			key.equals(DOCUMENT_ID) ||
			key.equals(DOCUMENT_POSITION) ||
			key.equals(DOCUMENT_TYPECODE)
			)
        ) {
			
			throw new PropertyNotFoundException("Property " + key + " not found");
		}
	}
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkType(org.jlibrary.core.properties.PropertyDef)
	 */
	protected void checkType(Object key, PropertyDef property) throws InvalidPropertyTypeException {
		
		int type = -1;
		
		if (key.equals(DOCUMENT_CONTENT)) {
			type = PropertyType.BINARY;
		} else if (key.equals(DOCUMENT_ADD_NOTE)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(DOCUMENT_UPDATE_NOTE)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(DOCUMENT_DELETE_NOTE)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(DOCUMENT_ADD_CATEGORY)) {
			type = PropertyType.STRING;
		} else if (key.equals(DOCUMENT_DELETE_CATEGORY)) {
			type = PropertyType.STRING;
		} else if (key.equals(DOCUMENT_ADD_RELATION)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(DOCUMENT_DELETE_RELATION)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(DOCUMENT_NAME)) {
			type = PropertyType.STRING;
		} else if (key.equals(DOCUMENT_DESCRIPTION)) {
			type = PropertyType.STRING;
		} else if (key.equals(DOCUMENT_TITLE)) {
			type = PropertyType.STRING;
		} else if (key.equals(DOCUMENT_KEYWORDS)) {
			type = PropertyType.STRING;
		} else if (key.equals(DOCUMENT_URL)) {
			type = PropertyType.STRING;
		} else if (key.equals(DOCUMENT_AUTHOR)) {
			type = PropertyType.OBJECT;
		} else if (key.equals(DOCUMENT_IMPORTANCE)) {
			type = PropertyType.INTEGER;
		} else if (key.equals(DOCUMENT_CREATOR)) {
			type = PropertyType.STRING;
		} else if (key.equals(DOCUMENT_LANGUAGE)) {
			type = PropertyType.STRING;
		} else if (key.equals(DOCUMENT_PARENT)) {
			type = PropertyType.STRING;
		} else if (key.equals(DOCUMENT_PATH)) {
			type = PropertyType.STRING;
		} else if (key.equals(DOCUMENT_CREATION_DATE)) {
			type = PropertyType.DATE;
		} else if (key.equals(DOCUMENT_UPDATE_DATE)) {
			type = PropertyType.DATE;
		} else if (key.equals(DOCUMENT_ID)) {
			type = PropertyType.STRING;
		} else if (key.equals(DOCUMENT_TYPECODE)) {
			type = PropertyType.INTEGER;
		} else if (key.equals(DOCUMENT_POSITION)) {
			type = PropertyType.INTEGER;
		}
				
		if (property.getType() != type) {
			throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be " + type);
		}
	}
}
