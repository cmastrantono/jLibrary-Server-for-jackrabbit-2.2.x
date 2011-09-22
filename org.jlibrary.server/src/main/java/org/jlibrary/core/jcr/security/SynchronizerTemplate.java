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
package org.jlibrary.core.jcr.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.jcr.JCRConstants;
import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This is the base template for jLibrary security sinchronizers. You should 
 * overwrite this class and add your own custom logic when you want to 
 * synchronize jLibrary security system and your third party security system.</p>
 * 
 * <p>You can use this synchronizer on any way you imagine. The only requirement 
 * for the synchronize is to receive a javax.jcr.Repository object and a valid 
 * username and password with administrative right on jLibrary repositories. So 
 * valid examples are running the synchronizer on a standalone java batch 
 * process, on a servlet running at web application startup, etc...</p>
 * 
 * <p>This synchronizer would take care of inserting the corresponding users, 
 * groups and roles on jLibrary repositories, using the standard jLibrary APIs.</p>
 * 
 * <p>You should subclass this class and add the custom logic needed to obtain 
 * the required information according to your third party authorization 
 * service.</p>
 * 
 * @author martin
 *
 */
public abstract class SynchronizerTemplate implements Synchronizer {

	static Logger logger = LoggerFactory.getLogger(SynchronizerTemplate.class);
	
	private static final String SYSTEM_WORKSPACE = "system";
	private static final String DEFAULT_WORKSPACE = "default";

	/**
	 * <p>Returns users from the third party authorization system that should be 
	 * synchronized.</p>
	 * <p>If an user does not exist then it will be created. If an user already 
	 * exist then its data, if available, will be updated.</p>
	 *  
	 * @return User[] Array with all the users that must be synchronized
	 * 
	 * @throws SecurityException If the users cannot be obtained
	 */
	public abstract User[] getUsersToSynchronize() throws SecurityException;

	/**
	 * <p>Returns roles from the third party authorization system that should be 
	 * synchronized.</p>
	 * <p>If a role does not exist then it will be created. If a role already 
	 * exist then its data, if available, will be updated.</p>
	 *  
	 * @return Rol[] Array with all the roles that must be synchronized
	 * 
	 * @throws SecurityException If the roles cannot be obtained
	 */	
	public abstract Rol[] getRolesToSynchronize() throws SecurityException;
	
	/**
	 * <p>Returns groups from the third party authorization system that should be 
	 * synchronized.</p>
	 * <p>If a group does not exist then it will be created. If a group already 
	 * exist then its data, if available, will be updated.</p>
	 *  
	 * @return Group[] Array with all the groups that must be synchronized
	 * 
	 * @throws SecurityException If the groups cannot be obtained
	 */	
	public abstract Group[] getGroupsToSynchronize() throws SecurityException;
	
	/**
	 * @see org.jlibrary.core.jcr.security.Synchronizer#synchronize(javax.jcr.Repository, java.lang.String, javax.jcr.Credentials)
	 */
	public void synchronize(Repository JCRRepository, 
							String jLibraryRepository, 
							Credentials credentials) throws SecurityException {
		
		if (jLibraryRepository.equals(DEFAULT_WORKSPACE)) {
			throw new SecurityException("Default workspace cannot be synchronized!");			
		}
		
		if (jLibraryRepository.equals(SYSTEM_WORKSPACE)) {
			throw new SecurityException("System workspace cannot be synchronized!");
		}
		
		try {
			Session session = 
				JCRRepository.login(credentials,jLibraryRepository);
			
			User[] users = getUsersToSynchronize();
			Rol[] roles = getRolesToSynchronize();
			Group[] groups = getGroupsToSynchronize();
				
			manageUsers(session,users);
			manageRoles(session,roles);
			manageGroups(session,groups);
			
			session.save();	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} 
	}

	private void manageUsers(Session session, 
							 User[] users) throws RepositoryException {
		
		ArrayList usersList = new ArrayList(Arrays.asList(users));
		javax.jcr.Node systemRoot = JCRUtils.getSystemNode(session);
        javax.jcr.Node securityRoot = 
        	systemRoot.getNode(JLibraryConstants.JLIBRARY_SECURITY);        
        javax.jcr.Node usersNode = 
        	securityRoot.getNode(JLibraryConstants.JLIBRARY_USERS);
		
        NodeIterator it = usersNode.getNodes();
        while (it.hasNext()) {
			Node userNode = (Node) it.next();
			String nodeName = userNode.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString();
			boolean active = userNode.getProperty(
					JLibraryConstants.JLIBRARY_ACTIVE).getBoolean();
			if (active) {
				for (int i=0;i<users.length;i++) {
					if (users[i].getName().equalsIgnoreCase(nodeName)) {
						updateUser(session,userNode,users[i]);
						usersList.remove(users[i]);
					}
				}
			}
		}
        Iterator it2 = usersList.iterator();
        while (it2.hasNext()) {
			User user = (User) it2.next();
			createUser(session,usersNode,user);
			createAuthor(session,user);
		}
	}
	
	private void manageGroups(Session session, 
			 				  Group[] groups) throws RepositoryException {

		ArrayList groupsList = new ArrayList(Arrays.asList(groups));
		javax.jcr.Node systemRoot = JCRUtils.getSystemNode(session);
		javax.jcr.Node securityRoot = 
			systemRoot.getNode(JLibraryConstants.JLIBRARY_SECURITY);        
		javax.jcr.Node groupsNode = 
			securityRoot.getNode(JLibraryConstants.JLIBRARY_GROUPS);

		NodeIterator it = groupsNode.getNodes();
		while (it.hasNext()) {
			Node groupNode = (Node) it.next();
			String nodeName = groupNode.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString();
			boolean active = groupNode.getProperty(
					JLibraryConstants.JLIBRARY_ACTIVE).getBoolean();
			if (active) {			
				for (int i=0;i<groups.length;i++) {
					if (groups[i].getName().equalsIgnoreCase(nodeName)) {
						updateGroup(session,groupNode,groups[i]);
						groupsList.remove(groups[i]);
					}
				}
			}
		}
		Iterator it2 = groupsList.iterator();
		while (it2.hasNext()) {
			Group group = (Group) it2.next();
			createGroup(session,groupsNode,group);
		}
	}	
	
	private void manageRoles(Session session, 
			 				  Rol[] roles) throws RepositoryException {

		ArrayList rolesList = new ArrayList(Arrays.asList(roles));
		javax.jcr.Node systemRoot = JCRUtils.getSystemNode(session);
		javax.jcr.Node securityRoot = 
			systemRoot.getNode(JLibraryConstants.JLIBRARY_SECURITY);        
		javax.jcr.Node rolesNode = 
			securityRoot.getNode(JLibraryConstants.JLIBRARY_ROLES);

		NodeIterator it = rolesNode.getNodes();
		while (it.hasNext()) {
			Node roleNode = (Node) it.next();
			String nodeName = roleNode.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString();
			boolean active = roleNode.getProperty(
					JLibraryConstants.JLIBRARY_ACTIVE).getBoolean();
			if (active) {			
				for (int i=0;i<roles.length;i++) {
					if (roles[i].getName().equalsIgnoreCase(nodeName)) {
						updateRole(session,roleNode,roles[i]);
						rolesList.remove(roles[i]);
					}
				}
			}
		}
		Iterator it2 = rolesList.iterator();
		while (it2.hasNext()) {
			Rol role = (Rol) it2.next();
			createRole(session,rolesNode,role);
		}
	}

	public void createUser(Session session,
			   			   Node usersNode,
			   			   User user) throws RepositoryException {

		javax.jcr.Node userNode =
			usersNode.addNode(JLibraryConstants.JLIBRARY_USER,
					  		  JLibraryConstants.USER_MIXIN);

		userNode.addMixin(JCRConstants.JCR_REFERENCEABLE);
		updateUser(session,userNode,user);
	}	
	
	public void updateUser(Session session,
						   Node userNode,
						   User user) throws RepositoryException {

		userNode.setProperty(JLibraryConstants.JLIBRARY_NAME,user.getName());
		if (user.getEmail() != null) {
			userNode.setProperty(JLibraryConstants.JLIBRARY_EMAIL,
								 user.getEmail());
		} else {
			userNode.setProperty(JLibraryConstants.JLIBRARY_EMAIL,"");			
		}
		if (user.getFirstName() != null) {
			userNode.setProperty(JLibraryConstants.JLIBRARY_FIRSTNAME,
								 user.getFirstName());
		} else {
			userNode.setProperty(JLibraryConstants.JLIBRARY_FIRSTNAME,"");			
		}
		if (user.getLastName() != null) {
			userNode.setProperty(JLibraryConstants.JLIBRARY_LASTNAME,
								 user.getLastName());
		} else {
			userNode.setProperty(JLibraryConstants.JLIBRARY_FIRSTNAME,"");						
		}
		if (user.getPassword() != null) {
			userNode.setProperty(JLibraryConstants.JLIBRARY_PASSWORD,
								 user.getPassword());
		} else {
			userNode.setProperty(JLibraryConstants.JLIBRARY_PASSWORD,"");			
		}
		if (!userNode.hasProperty(JLibraryConstants.JLIBRARY_GROUPS)) {
			userNode.setProperty(JLibraryConstants.JLIBRARY_GROUPS,new Value[]{});
		}
		if (!userNode.hasProperty(JLibraryConstants.JLIBRARY_ROLES)) {
			userNode.setProperty(JLibraryConstants.JLIBRARY_ROLES,new Value[]{});
		}
	}


	public void createAuthor(Session session,
			   			     User user) throws RepositoryException {

		javax.jcr.Node systemNode = JCRUtils.getSystemNode(session); 
		javax.jcr.Node authorsNode = 
			systemNode.getNode(JLibraryConstants.JLIBRARY_AUTHORS);

		NodeIterator it = authorsNode.getNodes();
		while (it.hasNext()) {
			Node authorNode = (Node) it.next();
			String nodeName = authorNode.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString();
			boolean active = authorNode.getProperty(
					JLibraryConstants.JLIBRARY_ACTIVE).getBoolean();
			if (nodeName.equals(user.getName()) && active) {
				return;
			}
		}		
		
		javax.jcr.Node authorNode = 
			authorsNode.addNode(JLibraryConstants.JLIBRARY_AUTHOR,
								JLibraryConstants.AUTHOR_MIXIN);

		authorNode.addMixin(JCRConstants.JCR_REFERENCEABLE);

		authorNode.setProperty(JLibraryConstants.JLIBRARY_NAME,user.getName());
		authorNode.setProperty(JLibraryConstants.JLIBRARY_AUTHOR_BIO,"");
	}		
	
	public void createGroup(Session session,
			   				Node groupsNode,
			   				Group group) throws RepositoryException {

		javax.jcr.Node groupNode =
			groupsNode.addNode(JLibraryConstants.JLIBRARY_GROUP,
		  		  			   JLibraryConstants.GROUP_MIXIN);

		groupNode.addMixin(JCRConstants.JCR_REFERENCEABLE);
		updateGroup(session,groupNode,group);
	}	

	public void updateGroup(Session session,
			   				Node groupNode,
			   				Group group) throws RepositoryException {

		groupNode.setProperty(JLibraryConstants.JLIBRARY_NAME,group.getName());
		if (group.getDescription() != null) {
			groupNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,
					 			  group.getDescription());
		} else {
			groupNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,"");			
		}
		if (!groupNode.hasProperty(JLibraryConstants.JLIBRARY_USERS)) {
			groupNode.setProperty(JLibraryConstants.JLIBRARY_USERS,new Value[]{});			
		}
		if (!groupNode.hasProperty(JLibraryConstants.JLIBRARY_ROLES)) {
			groupNode.setProperty(JLibraryConstants.JLIBRARY_ROLES,new Value[]{});
		}		
	}
	
	public void createRole(Session session,
			   			   Node rolesNode,
			   			   Rol role) throws RepositoryException {

		javax.jcr.Node roleNode =
			rolesNode.addNode(JLibraryConstants.JLIBRARY_ROL,
		  		  			   JLibraryConstants.ROL_MIXIN);

		roleNode.addMixin(JCRConstants.JCR_REFERENCEABLE);
		updateRole(session,roleNode,role);
	}	

	public void updateRole(Session session,
			   			   Node roleNode,
			   			   Rol role) throws RepositoryException {

		roleNode.setProperty(JLibraryConstants.JLIBRARY_NAME,role.getName());
		if (role.getDescription() != null) {
			roleNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,
					 			  role.getDescription());
		} else {
			roleNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,"");			
		}
		if (!roleNode.hasProperty(JLibraryConstants.JLIBRARY_USERS)) {
			roleNode.setProperty(JLibraryConstants.JLIBRARY_USERS,new Value[]{});			
		}
		if (!roleNode.hasProperty(JLibraryConstants.JLIBRARY_GROUPS)) {
			roleNode.setProperty(JLibraryConstants.JLIBRARY_GROUPS,new Value[]{});
		}		
	}		
}
