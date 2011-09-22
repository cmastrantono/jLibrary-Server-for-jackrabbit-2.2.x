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
package org.jlibrary.core.entities;

import java.io.Serializable;

import org.jlibrary.core.properties.RepositoryProperties;

/**
 * This class holds repository configuration properties. 
 * 
 * @author mpermar
 *
 */
public class RepositoryConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7483012628323203993L;

	/*
	 * If <code>true</code> documents will be physically removed
	 */
	private boolean physicalDeleteDocuments = false;
	
	/*
	 * If <code>true</code> metadata will be extracted
	 */
	private boolean extractMetadata = true;
	
	/*
	 * If <code>true</code> repository data will loaded lazily
	 */
	private boolean lazyLoadData = false;

	/**
	 * Tells if documents will be physically removed from the repository
	 * 
	 * @return boolean <code>true</code> if the documents will be 
	 * physically removed from the repository and <code>false</code> otherwise.
	 */
	public boolean isPhysicalDeleteDocuments() {
		return physicalDeleteDocuments;
	}

	/**
	 * <p>
	 * Sets if documents should be physically removed from the repository. If 
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
	 * @return boolean <code>true</code> if the documents should be 
	 * physically removed from the repository and <code>false</code> otherwise.
	 */	
	public void setPhysicalDeleteDocuments(boolean documentsPhysicalDelete) {
		this.physicalDeleteDocuments = documentsPhysicalDelete;
	}

	/**
	 * Tells if jLibrary will try to extract metadata from document contents. 
	 * 
	 * @return boolean <code>true</code> if the metadata must be extracted from
	 * the document contents and <code>false</code> otherwise.
	 */	
	public boolean isExtractMetadata() {
		return extractMetadata;
	}

	/**
	 * Sets if jLibrary will try to extract metadata from document contents. 
	 * Disabling this property should considerably boost the process of adding 
	 * documents to the repository from jLibrary GUI.  
	 * 
	 * @return boolean <code>true</code> if the metadata must be extracted from
	 * the document contents and <code>false</code> otherwise.
	 */		
	public void setExtractMetadata(boolean extractMetadata) {
		this.extractMetadata = extractMetadata;
	}
	
	/**
	 * Tells if jLibrary has activated lazy loading feature.. 
	 * 
	 * @return boolean <code>true</code> if jLibrary should do lazy loading on 
	 * node children, and <code>false</code> otherwise.
	 */	
	public boolean isEnabledLazyLoading() {
		return lazyLoadData;
	}

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
	 * @param enableLazyLoading It must be <code>true</code> if node children 
	 * should be loaded lazily and <code>false</code> otherwise.
	 */		
	public void setEnabledLazyLoading(boolean enableLazyLoading) {
		this.lazyLoadData = enableLazyLoading;
	}	
	
	/**
	 * Adds a config entry to this configuration 
	 * 
	 * @param key Key of the config entry
	 * @param value Value of that config entry
	 */
	public void addConfigEntry(String key, Object value) {
		
		if (key.equals(RepositoryProperties.EXTRACT_DOCUMENT_METADATA)) {
			setExtractMetadata(((Boolean)value).booleanValue());
		} else if (key.equals(RepositoryProperties.PHYSICAL_DELETE_DOCUMENTS)) {
			setPhysicalDeleteDocuments(((Boolean)value).booleanValue());
		} else if (key.equals(RepositoryProperties.DO_LAZY_LOADING)) {
			setEnabledLazyLoading(((Boolean)value).booleanValue());
		}
	}
}
