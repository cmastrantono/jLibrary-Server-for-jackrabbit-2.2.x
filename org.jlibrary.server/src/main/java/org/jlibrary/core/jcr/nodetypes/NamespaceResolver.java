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
package org.jlibrary.core.jcr.nodetypes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.jcr.RepositoryException;
import javax.jcr.Workspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class offers different operations for handling JCR namespaces. 
 * 
 * @author mpermar
 *
 */
public class NamespaceResolver {

	static Logger logger = LoggerFactory.getLogger(NamespaceResolver.class);

	private static final String NAMESPACES_FILE = "namespaces.cnd";
	
	/**
	 * This method loads the NAMESPACES_FILE file and tries to register each of the namespaces
	 * defined in that file. 
	 *  
	 * @throws RepositoryException If there is any error registering the namespaces
	 */
	public void registerAvailableNamespaces(Workspace wsp) throws RepositoryException {
		
		Properties properties = new Properties();
		InputStream stream = null;
		try {
			stream = getClass().getClassLoader().getResourceAsStream(
					NAMESPACES_FILE);
			if (stream == null) {
				logger.error("Could not read namespaces file: "
						+ NAMESPACES_FILE);
			}
			properties.load(stream);
		} catch (IOException e) {
			logger.error("Error loading properties file", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		for (Object key: properties.keySet()) {
			String strKey = (String)key;
			String strValue = (String)properties.get(strKey);
			registerNamespace(wsp, strKey, strValue);
		}
	}
	
	/**
	 * Returns true if a given namespace exists within a given workspace
	 * 
	 * @param wsp Workspace
	 * @param prx Namespace prefix
	 * 
	 * @return boolean <code>true</code> if the namespace exists in the workspace and 
	 * <code>false</code> otherwise
	 * 
	 * @throws RepositoryException If there is any error checking the namespace
	 */
	public boolean existNamespace(Workspace wsp, String prx) throws RepositoryException {
		
		boolean prefixExists = false;
		String[] prefixes = wsp.getNamespaceRegistry().getPrefixes();
		for(String prefix: prefixes) {
			if (prefix.equals(prx)) {
				prefixExists = true;
				break;
			}			
		}
		return prefixExists;
	}
	
	/**
	 * Tries to register a given namespace in the given workspace. If the namespace already 
	 * exists no operation is performed. 
	 * 
	 * @param wsp Workspace
	 * @param prefix Namespace prefix
	 * @param uri Namespace URI
	 * 
	 * @throws RepositoryException If there is any error registering the namespace in the workspace
	 */
	public void registerNamespace(Workspace wsp, 
								  String prefix, 
								  String uri) throws RepositoryException {
		
		if (!existNamespace(wsp, prefix)) {
			logger.debug("Registering namespace: [" + prefix+ "," + uri + "]");
			wsp.getNamespaceRegistry().registerNamespace(prefix,uri);
		} else {
			logger.debug("The namespace [" + prefix+ "," + uri + "] is already registered");
		}		
	}
}
