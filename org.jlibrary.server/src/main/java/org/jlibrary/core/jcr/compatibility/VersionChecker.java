/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
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

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.jlibrary.core.entities.User;
import org.jlibrary.core.jcr.JCRSecurityService;
import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.jcr.nodetypes.NodeTypeManager;
import org.jlibrary.core.properties.RepositoryProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will check jLibrary versions
 *  
 * @author martin
 */
public class VersionChecker {

	static Logger logger = LoggerFactory.getLogger(VersionChecker.class);
	
	private NodeTypeManager nodeTypeManager = new NodeTypeManager();
	
	/**
	 * Completely checks a jLibrary server trying to fix possible 
	 * incompatibilities. This method is a really heavyhewight method and so 
	 * it should only be called once, ideally on server startup.
	 * 
	 * This method will first check the node types registry. After this it will 
	 * go through all the different server workspaces looking for possible 
	 * incompatibilities.
	 * 
	 * @throws RepositoryException If there is some error donin the check 
	 * process
	 */
	public void checkServer(javax.jcr.Repository repository,
							javax.jcr.Session systemSession) 
												throws RepositoryException {
		
		// First, check the server nodetypes structure
		nodeTypeManager.checkNodeTypes(systemSession);
		javax.jcr.Node adminNode = 
			JCRSecurityService.findAdminUser(systemSession);
		String username = adminNode.getProperty(
				JLibraryConstants.JLIBRARY_NAME).getString();
		if (username.equals(User.ADMIN_NAME)) {
			username = User.ADMIN_KEYNAME;
		}
		String password = adminNode.getProperty(
				JLibraryConstants.JLIBRARY_PASSWORD).getString();
		SimpleCredentials creds =
            new SimpleCredentials(username, password.toCharArray());
		
		String[] workspaces = 
			systemSession.getWorkspace().getAccessibleWorkspaceNames();
		for (int i = 0; i < workspaces.length; i++) {
			logger.info(
					"Checking versions for repository '" + workspaces[i] +"'");

			if (workspaces[i].equals("default") || 
				(workspaces[i].equals("system"))) {
				continue;
			}
			javax.jcr.Session session;
			try {
				session = repository.login(creds,workspaces[i]);
			} catch (LoginException e) {
				// Probably a non jLibrary repository with security restrictions
				continue;
			}
			if (!JCRUtils.isJLibraryRepository(session)) {
				continue;
			}
			checkSession(session);
			if (session != null) session.logout();
		}
	}
	
	/**
	 * Checks session compatibility
	 * 
	 * @param session Session to check
	 * 
	 * @throws RepositoryException If some error happens when checking 
	 * compatibility
	 */
	public void checkSession(Session session) throws RepositoryException {
				
		if (mustUpdateTo1_0(session)) {			
			Beta4Migrator migrator = new Beta4Migrator();
			migrator.migrate(session);
			
			// We are migrating beta4
			javax.jcr.Node system = JCRUtils.getSystemNode(session);
			
			system.setProperty(JLibraryConstants.JLIBRARY_VERSION,
							   JLibraryConstants.VERSION_1_0_FINAL);
			system.getSession().save();			
		}
		if (mustUpdateTo1_0_1(session)) {
			// Migrate to 1.0.1
			
			JLibrary101Migrator migrator = new JLibrary101Migrator();
			migrator.migrate(session);
			
			javax.jcr.Node system = JCRUtils.getSystemNode(session);	
			
			system.setProperty(JLibraryConstants.JLIBRARY_VERSION,
							   JLibraryConstants.VERSION_1_0_1);
			system.getSession().save();
		}
		if (mustUpdateTo1_1(session)) {
			// Migrate to 1.1
			JLibrary11Migrator migrator = new JLibrary11Migrator();
			migrator.migrate(session);
			
			javax.jcr.Node system = JCRUtils.getSystemNode(session);				
			system.setProperty(JLibraryConstants.JLIBRARY_VERSION,
							   JLibraryConstants.VERSION_1_1);
			system.getSession().save();			
		}
	}
	
	/**
	 * This method will tell us if the repository needs to be migrated
	 * to final 1.0
	 * 
	 * @param session Session
	 * 
	 * @return <code>true</code> if must be updated and 
	 * <code>false</code> otherwise
	 */
	public static boolean mustUpdateTo1_0(Session session) 
												throws RepositoryException {
		
		javax.jcr.Node system = JCRUtils.getSystemNode(session);
		if (!system.hasProperty(JLibraryConstants.JLIBRARY_VERSION)) {
			return true;
		}		
		javax.jcr.Node root = JCRUtils.getRootNode(session);
		if (!root.hasProperty(JLibraryConstants.JLIBRARY_ACTIVE)) {
			return true;
		}
		if (!system.hasNode(JLibraryConstants.JLIBRARY_CONFIG)) {
			return true;
		} else {
			if (!JCRUtils.hasConfigProperty(
					session,RepositoryProperties.DO_LAZY_LOADING)) {
				return true;
			}
		}
		
		if (JCRUtils.hasNodesForProperty(session,"jlib:id")) {
			return true;
		}
		
		return false;
	}
		
	
	/**
	 * This method will tell us if the repository needs to be migrated
	 * to final 1.0.1
	 * 
	 * @param session Session
	 * 
	 * @return <code>true</code> if must be updated and 
	 * <code>false</code> otherwise
	 */
	public static boolean mustUpdateTo1_0_1(Session session) 
												throws RepositoryException {
		
		javax.jcr.Node system = JCRUtils.getSystemNode(session);
		if (!system.hasProperty(JLibraryConstants.JLIBRARY_VERSION)) {
			return true;
		}		
		
		String version = system.getProperty(
				JLibraryConstants.JLIBRARY_VERSION).getString();
		if (version.equals(JLibraryConstants.VERSION_1_0_FINAL)) {
			return true;
		}
		
		return false;
	}	
	
	
	/**
	 * This method will tell us if the repository needs to be migrated
	 * to final 1.1
	 * 
	 * @param session Session
	 * 
	 * @return <code>true</code> if must be updated and 
	 * <code>false</code> otherwise
	 */
	public static boolean mustUpdateTo1_1(Session session) 
												throws RepositoryException {
		
		javax.jcr.Node system = JCRUtils.getSystemNode(session);
		if (!system.hasProperty(JLibraryConstants.JLIBRARY_VERSION)) {
			return true;
		}		
		
		String version = system.getProperty(
				JLibraryConstants.JLIBRARY_VERSION).getString();
		if (!version.equals(JLibraryConstants.VERSION_1_1)) {
			return true;
		}
		
		return false;
	}	
}
