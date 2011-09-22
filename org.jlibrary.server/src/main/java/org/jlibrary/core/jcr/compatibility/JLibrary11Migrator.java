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
package org.jlibrary.core.jcr.compatibility;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class does migrations from 1.0.1 to 1.1 version
 * 
 * @author martin
 */
public class JLibrary11Migrator implements Migrator {

	static Logger logger = LoggerFactory.getLogger(JLibrary11Migrator.class);
	
	/**
	 * @see Migrator#migrate(Session)
	 */
	public void migrate(Session session) throws RepositoryException {
		
		migrateSystemNode(session);
		session.save();
	}
	
	private void migrateSystemNode(Session session) throws RepositoryException {
		
		try {			
			// Add the custom properties node
			javax.jcr.Node systemNode = JCRUtils.getSystemNode(session);
			systemNode.addNode(JLibraryConstants.JLIBRARY_CUSTOM_PROPERTIES,
					   JLibraryConstants.INTERNAL_MIXIN);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}	
}
