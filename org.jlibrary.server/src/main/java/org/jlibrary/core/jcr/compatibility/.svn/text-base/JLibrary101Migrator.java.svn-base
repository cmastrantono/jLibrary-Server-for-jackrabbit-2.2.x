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
import javax.jcr.query.InvalidQueryException;

import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class does migrations from 1.0 to 1.0.1 version
 * 
 * @author martin
 */
public class JLibrary101Migrator implements Migrator {

	static Logger logger = LoggerFactory.getLogger(JLibrary101Migrator.class);
	
	/**
	 * @see Migrator#migrate(Session)
	 */
	public void migrate(Session session) throws RepositoryException {
		
		migrateNodes(session);
		session.save();
	}
	
	private void migrateNodes(Session session) throws RepositoryException {
		
		try {			
			// Fix the security bug that made all users admin users
			javax.jcr.Node systemRoot = JCRUtils.getSystemNode(session);

			javax.jcr.Node securityRoot = 
				systemRoot.getNode(JLibraryConstants.JLIBRARY_SECURITY);
			javax.jcr.Node usersNode = 
				securityRoot.getNode(JLibraryConstants.JLIBRARY_USERS);
			NodeIterator it = usersNode.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node userNode = it.nextNode();
				if (userNode.hasProperty(JLibraryConstants.JLIBRARY_SYSADMIN)) {
					if (userNode.getProperty(
							JLibraryConstants.JLIBRARY_SYSADMIN).getBoolean()) 
						continue;
				}
				String userName = userNode.getProperty(
						JLibraryConstants.JLIBRARY_NAME).getString();
				if (userName.equals(User.ADMIN_NAME)) continue;
				userNode.setProperty(JLibraryConstants.JLIBRARY_ISADMIN,false);
			}
			
			// Fix role names
			javax.jcr.Node rolesNode = 
				securityRoot.getNode(JLibraryConstants.JLIBRARY_ROLES);
			it = rolesNode.getNodes();
			int i = 0;
			while (it.hasNext()) {
				if (i == 3) break;
				javax.jcr.Node roleNode = it.nextNode();
				if (i == 0) {
					roleNode.setProperty(JLibraryConstants.JLIBRARY_NAME,
										 Rol.ADMIN_ROLE_NAME);
					roleNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,
							 Rol.ADMIN_ROLE_DESCRIPTION);
				}
				if (i == 1) {
					roleNode.setProperty(JLibraryConstants.JLIBRARY_NAME,
										 Rol.READER_ROLE_NAME);
					roleNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,
							 Rol.READER_ROLE_DESCRIPTION);
				}
				if (i == 2) {
					roleNode.setProperty(JLibraryConstants.JLIBRARY_NAME,
										 Rol.PUBLISHER_ROLE_NAME);
					roleNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,
							 Rol.PUBLISHER_ROLE_DESCRIPTION);
				}				
				i++;
			}			
			
			// Fix group names
			javax.jcr.Node groupsNode = 
				securityRoot.getNode(JLibraryConstants.JLIBRARY_GROUPS);
			it = groupsNode.getNodes();
			i = 0;
			while (it.hasNext()) {
				if (i == 3) break;
				javax.jcr.Node groupNode = it.nextNode();

				if (i == 0) {
					groupNode.setProperty(JLibraryConstants.JLIBRARY_NAME,
										  Group.PUBLISHERS_GROUP_NAME);
					groupNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,
							 			  Group.PUBLISHERS_GROUP_DESCRIPTION);
				}
				if (i == 1) {
					groupNode.setProperty(JLibraryConstants.JLIBRARY_NAME,
										  Group.ADMINS_GROUP_NAME);
					groupNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,
							 			  Group.ADMINS_GROUP_DESCRIPTION);
				}
				if (i == 2) {
					groupNode.setProperty(JLibraryConstants.JLIBRARY_NAME,
										  Group.READERS_GROUP_NAME);
					groupNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,
							 			  Group.READERS_GROUP_DESCRIPTION);
				}								
				i++;
			}	
			
			// Fix users names
			it = usersNode.getNodes();
			i = 0;
			while (it.hasNext()) {
				if (i == 1) break;
				javax.jcr.Node userNode = it.nextNode();

				if (i == 0) {
					userNode.setProperty(JLibraryConstants.JLIBRARY_NAME,
							  			 User.ADMIN_NAME);
					userNode.setProperty(JLibraryConstants.JLIBRARY_FIRSTNAME,
	 			  			 			 User.ADMIN_NAME);
					userNode.setProperty(JLibraryConstants.JLIBRARY_LASTNAME,
	 			  			 			 User.ADMIN_NAME);										
				}
				i++;
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
