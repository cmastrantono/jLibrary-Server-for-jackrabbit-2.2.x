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
 * Properties for a repository
 */
public class RepositoryProperties extends GenericProperties {
		
	/**
	 * Repository UUID
	 */
	public static final String REPOSITORY_ID = "repository.id";
	
	/**
	 * Repository name
	 */
	public static final String REPOSITORY_NAME = "repository.name";
	
	/**
	 * Repository description
	 */
	public static final String REPOSITORY_DESCRIPTION = "repository.description";

	/**
	 * <p>
	 * Tags if documents should be physically removed from the repository. If 
	 * the documents are not physically removed, then a copy will be maintained 
	 * hidden on the repository. 
	 * </p>
	 * <p>
	 * By default, repositories will be created with this property set to 
	 * <code>false</code>. Logical deletions can be very useful on environments 
	 * when you have to maintain a registry from all documents, even if they 
	 * were deleted, but it can hurts repository scalability if the number of 
	 * deleted documents is really very high.
	 * </p>
	 */
	public static final String PHYSICAL_DELETE_DOCUMENTS = 
		"physical.delete.documents";
	
	/**
	 * <p>Tags if the jLibrary node hierarchy should be lazy loaded. If this 
	 * property is set to <code>true</code>, then jLibrary nodes won't have 
	 * their <code>nodes</code> attribute filled with the node's children and 
	 * so you will have to load the nodes programatically.</p>
	 * <p>If the property is set to <code>false</code>, then you will have the 
	 * entire child hierarchy under each directory. By consequence, each time 
	 * you get a repository, you will have attached the entire node 
	 * structure. Note that this takes some time to load, but makes later work 
	 * easier.</p> 
	 * <p>Enabling this property is very useful if you plan to work with big 
	 * repositories, because it will drastically decrease the size of the 
	 * information transfered when obtaining repositories and directory 
	 * structures, and by consequence jLibrary client startup should also 
	 * be faster.</p>  
	 * 
	 */	
	public static final String DO_LAZY_LOADING =
		"do.lazy.loading";	
	
	/**
	 * Tags if jLibrary will try to extract metadata from document contents. 
	 * Disabling this property should considerably boost the process of adding 
	 * documents to the repository from jLibrary GUI.  
	 */	
	public static final String EXTRACT_DOCUMENT_METADATA =
		"extract.document.metadata";			
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkPropertyDefinition(java.lang.Object)
	 */
	protected void checkPropertyDefinition(Object key) throws PropertyNotFoundException {

		if (!(key.equals(REPOSITORY_NAME)) && 
			!(key.equals(REPOSITORY_DESCRIPTION)) &&
			!(key.equals(REPOSITORY_ID)) &&
			!(key.equals(EXTRACT_DOCUMENT_METADATA)) &&
			!(key.equals(PHYSICAL_DELETE_DOCUMENTS)) &&
			!(key.equals(DO_LAZY_LOADING))) {
			
			throw new PropertyNotFoundException("Property " + key + " not found");
		}
	}
	
	/**
	 * @see org.jlibrary.core.properties.GenericProperties#checkType(org.jlibrary.core.properties.PropertyDef)
	 */
	protected void checkType(Object key, PropertyDef property) throws InvalidPropertyTypeException {
		
		int type = -1;
		
		if (key.equals(REPOSITORY_NAME)) {
			type = PropertyType.STRING;
		} else if (key.equals(REPOSITORY_DESCRIPTION)) {
			type = PropertyType.STRING;
		} else if (key.equals(REPOSITORY_ID)) {
			type = PropertyType.STRING;
		} else if (key.equals(PHYSICAL_DELETE_DOCUMENTS)) {
			type = PropertyType.BOOLEAN;
		} else if (key.equals(EXTRACT_DOCUMENT_METADATA)) {
			type = PropertyType.BOOLEAN;
		} else if (key.equals(DO_LAZY_LOADING)) {
			type = PropertyType.BOOLEAN;
		}
				
		if (property.getType() != type) {
			throw new InvalidPropertyTypeException("Property " + key + " has an invalid type. Should be " + type);
		}
	}
}
