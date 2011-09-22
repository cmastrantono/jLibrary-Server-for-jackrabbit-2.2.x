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
package org.jlibrary.core.security;

import java.net.ConnectException;
import java.util.Collection;

import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.properties.GroupProperties;
import org.jlibrary.core.properties.RolProperties;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.GroupAlreadyExistsException;
import org.jlibrary.core.security.exception.GroupNotFoundException;
import org.jlibrary.core.security.exception.RoleAlreadyExistsException;
import org.jlibrary.core.security.exception.RoleNotFoundException;
import org.jlibrary.core.security.exception.UserAlreadyExistsException;
import org.jlibrary.core.security.exception.UserNotFoundException;


public interface SecurityService {

	public final String SYSTEM_REPOSITORY = "system";
	
	/**
	 * Creates a new user
	 * 
	 * @param ticket Ticket with user information
	 * @param userProperties Properties of the user to create
	 * 
	 * @return User new user created
	 *  
	 * @throws UserAlreadyExistsException If an user with the name that come
	 * with the properties already exists
	 * @throws SecurityException If the user can't be created
	 */
	public User createUser(Ticket ticket, 
						   UserProperties userProperties) 
											throws UserAlreadyExistsException,
												   SecurityException;

	/**
	 * Finds an user
	 * 
	 * @param ticket Ticket with user and repository information
	 * @param name Name of the user to find
	 * 
	 * @return User with that name
	 * 
	 * @throws SecurityException If goes something wrong
	 * @throws UserNotFoundException If no such user exists
	 */
	public User findUserByName(Ticket ticket,
							   String name) throws SecurityException, 
							   					   UserNotFoundException;

	/**
	 * Finds an user by its id
	 * 
	 * @param ticket Ticket with user and repository information
	 * @param id Id of the user to find
	 * 
	 * @return User with that id
	 * 
	 * @throws SecurityException If goes something wrong
	 * @throws UserNotFoundException If no such user exists
	 */
	public User findUserById(Ticket ticket,
							 String id) throws SecurityException, 
							 				   UserNotFoundException;
	
	
	/**
	 * Returns all the users from this repository
	 * 
	 * @param ticket Ticket with user and repository information
	 * 
	 * @return Collection The list of users of this repository
	 * 
	 * @throws SecurityException If the users can't be loaded
	 */
	public Collection findAllUsers(Ticket ticket) throws SecurityException;
	
	/**
	 * Returns all the roles from this repository
	 * 
	 * @param ticket Ticket with user and repository information
	 * 
	 * @return Collection The list of roles of this repository
	 * 
	 * @throws SecurityException If the roles can't be loaded
	 */
	public Collection findAllRoles(Ticket ticket) throws SecurityException;
	
	/**
	 * Returns all the groups from this repository
	 * 
	 * @param ticket Ticket with user and repository information
	 * 
	 * @return Collection The list of groups of this repository
	 * 
	 * @throws SecurityException If the groups can't be loaded
	 */
	public Collection findAllGroups(Ticket ticket) throws SecurityException;
	
	/**
	 * Creates a new rol
	 * 
	 * @param ticket Ticket with user information
	 * @param rolProperties properties for the rol that will be created
	 * 
	 * @return New created rol
	 * 
	 * @throws RoleAlreadyExistsException If a role with the name that come
	 * with the properties already exists	 
	 * @throws SecurityException If the rol can't be created
	 */
	public Rol createRol(Ticket ticket,
						 RolProperties rolProperties) 
											throws RoleAlreadyExistsException,
												   SecurityException;

	/**
	 * Finds a rol
	 * 
	 * @param ticket Ticket with user and repository information
	 * @param repositoryId Id of the repository
	 * @param rolId Id of the rol to find
	 * 
	 * @return Rol with that name
	 * 
	 * @throws SecurityException If goes something wrong
	 * @throws RoleNotFoundException If no such role exists
	 */
	public Rol findRol(Ticket ticket,
					   String rolId) throws SecurityException, 
					   					    RoleNotFoundException;
	
	/**
	 * Creates a new group
	 * 
	 * @param ticket Ticket with user information
	 * @param groupProperties Properties of the group to create
	 * 
	 * @return Group group created
	 *  
	 * @throws GroupAlreadyExistsException If a group with the name that come
	 * with the properties already exists
	 * @throws SecurityException If the rol can't be created
	 */
	public Group createGroup(Ticket ticket, 
							 GroupProperties groupProperties) 
											throws GroupAlreadyExistsException,
												   SecurityException;

	/**
	 * Finds a group
	 * 
	 * @param ticket Ticket with user and repository information
	 * @param id Id of the group
	 * 
	 * @return Group with that id
	 * 
	 * @throws SecurityException If goes something wrong
	 * @throws GroupNotFoundException If no such group exists
	 */
	public Group findGroupById(Ticket ticket,
						   	   String id) throws SecurityException, 
						   		 					 GroupNotFoundException;
	
	/**
	 * Removes an user
	 * 
	 * @param ticket Ticket with user and repository information
	 * @param userId Id of the user to be removed
	 * 
	 * @throws SecurityException If the user can't be removed
	 * @throws UserNotFoundException if the user can't be found
	 */
	public void removeUser(Ticket ticket, 
						   String userId) throws SecurityException, 
						   						 UserNotFoundException;
	
	/**
	 * Removes an group
	 * 
	 * @param ticket Ticket with user and repository information
	 * @param groupId Id of the group to be removed
	 * 
	 * @throws SecurityException If the group can't be removed
	 * @throws GroupNotFoundException if the group can't be found	 
	 */
	public void removeGroup(Ticket ticket, 
						    String groupId) throws SecurityException, 
						    					   GroupNotFoundException;
	
	/**
	 * Removes a rol
	 * 
	 * @param ticket Ticket with user and repository information
	 * @param rolId Id of the rol to be removed
	 * 
	 * @throws SecurityException If the rol can't be removed
	 * @throws RoleNotFoundException if the rol can't be found
	 */
	public void removeRol(Ticket ticket, 
						  String rolId) throws SecurityException, 
						  					   RoleNotFoundException;
	
	/**
	 * Updates an user
	 * 
	 * @param ticket Ticket with user information
	 * @param UserProperties properties for updating the user
	 * 
	 * @return User updated user
	 * 
	 * @throws SecurityException If the user can't be updated
	 */
	public User updateUser(Ticket ticket, 
						   UserProperties userProperties) 
													throws SecurityException;
	
	/**
	 * Updates a group
	 * 
	 * @param ticket Ticket with user information
	 * @param groupProperties properties for updating the group
	 * 
	 * @return Group updated group
	 * 
	 * @throws SecurityException If the group can't be updated
	 */
	public Group updateGroup(Ticket ticket, 
							 GroupProperties groupProperties) 
													throws SecurityException;
	
	/**
	 * Updates a rol
	 * 
	 * @param ticket Ticket with user information
	 * @param rolProperties properties for updating the rol
	 *
	 * @return Rol updated rol
	 * 
	 * @throws SecurityException If the rol can't be updated
	 */
	public Rol updateRol(Ticket ticket, 
						 RolProperties rolProperties) 
													throws SecurityException;	
	
	/**
	 * Does a login against a jLibrary server and a repository
	 * 
	 * @param credentials Credentials for performing the login
	 * @param name Repository name
	 * 
	 * @return Ticket A ticket that allows access to the jLibrary server
	 * 
	 * @throws UserNotFoundException If the users doesn't exists
	 * @throws AuthenticationException If the user password is wrong
	 * @throws SecurityException If some other internal error happens
	 * @throws ConnectException If the server isn't available
	 * @throws RepositoryNotFoundException If the repository cannot be found
	 */
	public Ticket login(Credentials credentials,
						String name) throws UserNotFoundException,
											AuthenticationException, 
											SecurityException,
											ConnectException,
											RepositoryNotFoundException;
	
	/**
	 * Disconnects the user from the server
	 * 
	 * @param ticket Ticket with the user information
	 * 
	 * @throws SecurityException If the user can't be disconnected
	 */
	public void disconnect(Ticket ticket) throws SecurityException;	
	
	/**
	 * Returns all the defined restrictions for a given node
	 * 
	 * @param ticket Ticket with user information
	 * @param String Node id
	 * 
	 * @return Collection The list of node restrictions
	 * 
	 * @throws SecurityException If the restrictions can't be loaded
	 */
	public Collection findAllRestrictions(Ticket ticket,
								    	  String nodeId) 
													throws SecurityException;
	
}