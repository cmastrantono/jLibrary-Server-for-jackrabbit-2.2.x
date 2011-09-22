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

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.Workspace;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.jlibrary.core.entities.User;
import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.properties.RepositoryProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Perform migrations from beta4 to the next version i.e. final version
 * 
 * @author martin
 */
public class Beta4Migrator implements Migrator {

	static Logger logger = LoggerFactory.getLogger(Beta4Migrator.class);
	
	/**
	 * @see Migrator#migrate(Session)
	 */
	public void migrate(Session session) throws RepositoryException {
		
		migrateNotes(session);
		migrateNodes(session);
		createConfig(session);
		session.save();
	}

	private void createConfig(Session session) throws RepositoryException {
		
		try {
			javax.jcr.Node systemNode = JCRUtils.getSystemNode(session);
		
			if (!systemNode.hasNode(JLibraryConstants.JLIBRARY_CONFIG)) {
				systemNode.addNode(JLibraryConstants.JLIBRARY_CONFIG,
								   JLibraryConstants.INTERNAL_MIXIN);
			}

			// Create default config entries
			if (!JCRUtils.hasConfigProperty(
					session,
					RepositoryProperties.EXTRACT_DOCUMENT_METADATA)) {
				JCRUtils.setConfigEntry(
						session,
						RepositoryProperties.EXTRACT_DOCUMENT_METADATA,
						Boolean.TRUE);
			}
			
			if (!JCRUtils.hasConfigProperty(
					session,
					RepositoryProperties.PHYSICAL_DELETE_DOCUMENTS)) {
				JCRUtils.setConfigEntry(session,
										RepositoryProperties.PHYSICAL_DELETE_DOCUMENTS,
										Boolean.TRUE);
			}
			
			if (!JCRUtils.hasConfigProperty(
					session,
					RepositoryProperties.DO_LAZY_LOADING)) {			
				JCRUtils.setConfigEntry(session,
										RepositoryProperties.DO_LAZY_LOADING,
										Boolean.FALSE);
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}

	private void migrateNotes(Session session) throws RepositoryException {
		
		try {
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			
			String rootPath = root.getPath();
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			String statement = "/jcr:root" +
							   rootPath + 
							   "//element(*,jlib:note)";
			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();
			
			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				node.setProperty(JLibraryConstants.JLIBRARY_USER,
								 User.ADMIN_USER.getId());
			}
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}
	
	private void migrateNodes(Session session) throws RepositoryException {
		
		try {			
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			String statement = "/jcr:root/jlib:root//*";
			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();
			
			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				if (!node.hasProperty(JLibraryConstants.JLIBRARY_ACTIVE)) {
					// Add the active property
					node.setProperty(JLibraryConstants.JLIBRARY_ACTIVE,true);
				}
			}
			
			// check for deprecated properties and remove them
			statement = "/jcr:root//element(*,jlib:jlibrary)[@jlib:id]";
			query =  queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			result = query.execute();
			
			it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				node.setProperty("jlib:id",(Value)null);
			}			
			
			statement = "/jcr:root/jlib:system//*";
			query =	queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			result = query.execute();
			
			it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				
				if (!node.hasProperty(JLibraryConstants.JLIBRARY_ACTIVE)) {
					// Add the active property
					node.setProperty(JLibraryConstants.JLIBRARY_ACTIVE,true);
				}
			}			
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}	
}
