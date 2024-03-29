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
package org.jlibrary.core.jcr;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.Workspace;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.jackrabbit.core.jndi.RegistryHelper;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.jcr.compatibility.VersionChecker;
import org.jlibrary.core.jcr.modules.JCRAuthorsModule;
import org.jlibrary.core.jcr.modules.JCRCleanupModule;
import org.jlibrary.core.jcr.modules.JCRFavoritesModule;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.properties.GroupProperties;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.RolProperties;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.core.repository.exception.AuthorAlreadyExistsException;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.AuthenticationException;
import org.jlibrary.core.security.exception.GroupAlreadyExistsException;
import org.jlibrary.core.security.exception.GroupNotFoundException;
import org.jlibrary.core.security.exception.RoleAlreadyExistsException;
import org.jlibrary.core.security.exception.RoleNotFoundException;
import org.jlibrary.core.security.exception.UserAlreadyExistsException;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.jlibrary.core.util.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the security service for JSR-170 based data repositories
 * 
 * @author martin
 *
 */
public class JCRSecurityService implements SecurityService {

	static Logger logger = LoggerFactory.getLogger(JCRSecurityService.class);
	
	private static Context ctx;
	protected static javax.jcr.Repository repository;
	protected static JCRSecurityService instance;
	protected String repositoriesHome;
	
	public JCRSecurityService() {}
	
	private JCRSecurityService(Context ctx) {
		
		initService(ctx);
	}

	public void initService(Context ctx) {
		javax.jcr.Session systemSession = null;
		try {
			//TODO: This argument must be obtained from the web.xml
			logger.info(
					"Looking for jLibrary repository...");
	        repository = (javax.jcr.Repository) ctx.lookup("jackrabbit.repository");	        	
			logger.info(
					"jLibrary repository found!");

			logger.info(
					"No jLibrary system session defined. Initializing jLibrary system session");
			systemSession = checkSystemWorkspace();
	        
	        // Now lookup for pending delete operations
	        new JCRCleanupModule().deletePendingWorkspaces(systemSession);
	        new VersionChecker().checkServer(repository,systemSession);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}	
		finally{
			if (systemSession != null) {
				systemSession.logout();
			}
			
		}
	}
	
	protected javax.jcr.Session registerRepository(String repositoriesHome) 
					throws org.jlibrary.core.repository.exception.RepositoryException, 
					NamingException {
		
		String configFile = JCRSecurityService.class.getClassLoader().getResource("repository.xml").getFile();				
		return registerRepository(this.repositoriesHome, configFile);
	}
	
	protected javax.jcr.Session registerRepository(
			String repositoriesHome,
			String configFile) throws org.jlibrary.core.repository.exception.RepositoryException, 
									  NamingException {
		
		logger.info("ConfigHome :" + configFile);
		logger.info("Repositories home :" + repositoriesHome);
		
		javax.jcr.Session systemSession = null;
		
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
		        "org.apache.jackrabbit.core.jndi.provider.DummyInitialContextFactory");
		env.put(Context.PROVIDER_URL, "localhost");
		
		ctx = new InitialContext(env);
		try {
			RegistryHelper.registerRepository(
					ctx, "jlibrary.repository", configFile, repositoriesHome, true);
		    repository = (javax.jcr.Repository) ctx.lookup("jlibrary.repository");
		    systemSession = checkSystemWorkspace();
		    
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return systemSession;
	}	
	
	public void buildWorkspaceSecurity(javax.jcr.Session session) 
										throws SecurityException {
		
		logger.info("Building security structure...");			
		try {
			javax.jcr.Node system = JCRUtils.getSystemNode(session);
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			
	        javax.jcr.Node securityRoot = null;
	        if (!(system.hasNode(JLibraryConstants.JLIBRARY_SECURITY))) {
	        	securityRoot = 
	        		system.addNode(JLibraryConstants.JLIBRARY_SECURITY,
	        					   JLibraryConstants.INTERNAL_MIXIN);
	        } else {
	        	securityRoot = system.getNode(JLibraryConstants.JLIBRARY_SECURITY);
	        }
	
			logger.info("Building users structure...");			
	        javax.jcr.Node usersNode = 
	        			securityRoot.addNode(JLibraryConstants.JLIBRARY_USERS,
	        								 JLibraryConstants.INTERNAL_MIXIN);
	
			logger.info("Building groups structure...");			
	        javax.jcr.Node groupsNode = 
	        			securityRoot.addNode(JLibraryConstants.JLIBRARY_GROUPS,
	        								 JLibraryConstants.INTERNAL_MIXIN);
	
			logger.info("Building roles structure...");			
	        javax.jcr.Node rolesNode = 
        			securityRoot.addNode(JLibraryConstants.JLIBRARY_ROLES,
        								 JLibraryConstants.INTERNAL_MIXIN);
        
	    	// Create default groups
        	GroupProperties groupProperties = new GroupProperties();
        	groupProperties.setProperty(GroupProperties.GROUP_NAME,
        						   Group.PUBLISHERS_GROUP_NAME);
        	groupProperties.setProperty(GroupProperties.GROUP_DESCRIPTION,
					   			   Group.PUBLISHERS_GROUP_DESCRIPTION);
        	
	        	javax.jcr.Node publishersGroup = 
	        		internalCreateGroup(null,groupsNode,groupProperties);
        	
        	
        	groupProperties = new GroupProperties();
        	groupProperties.setProperty(GroupProperties.GROUP_NAME,
        						   Group.ADMINS_GROUP_NAME);
        	groupProperties.setProperty(GroupProperties.GROUP_DESCRIPTION,
					   			   Group.ADMINS_GROUP_DESCRIPTION);
        	
	        	javax.jcr.Node adminsGroup = 
	        		internalCreateGroup(null,groupsNode,groupProperties);
        	
        	
        	groupProperties = new GroupProperties();
        	groupProperties.setProperty(GroupProperties.GROUP_NAME,
        						   Group.READERS_GROUP_NAME);
        	groupProperties.setProperty(GroupProperties.GROUP_DESCRIPTION,
					   			   Group.READERS_GROUP_DESCRIPTION);
        	
	        	javax.jcr.Node readersGroup = 
	        		internalCreateGroup(null,groupsNode,groupProperties);            	
        	

        	// Create repository roles
    		RolProperties rolProperties = new RolProperties();
    		rolProperties.setProperty(RolProperties.ROL_NAME,
    							      Rol.ADMIN_ROLE_NAME);
    		rolProperties.setProperty(RolProperties.ROL_DESCRIPTION,
    							      Rol.ADMIN_ROLE_NAME);
    		rolProperties.setProperty(RolProperties.ROL_REPOSITORY,
    				   			   	  root.getUUID());
    		javax.jcr.Node adminRole = 
    			internalCreateRol(null,rolesNode,rolProperties);
    		
    		rolProperties.setProperty(RolProperties.ROL_NAME,
    				   			      Rol.READER_ROLE_NAME);
    		rolProperties.setProperty(RolProperties.ROL_DESCRIPTION,
    				   			   	  Rol.READER_ROLE_DESCRIPTION);
    		rolProperties.setProperty(RolProperties.ROL_REPOSITORY,
    								  root.getUUID());
    		javax.jcr.Node readerRole = 
    			internalCreateRol(null,rolesNode,rolProperties);
    		
    		rolProperties.setProperty(RolProperties.ROL_NAME,
    				   			   	  Rol.PUBLISHER_ROLE_NAME);
    		rolProperties.setProperty(RolProperties.ROL_DESCRIPTION,
    				   			   	  Rol.PUBLISHER_ROLE_DESCRIPTION);
    		rolProperties.setProperty(RolProperties.ROL_REPOSITORY,
    								  root.getUUID());
    		javax.jcr.Node publisherRole =
    			internalCreateRol(null,rolesNode,rolProperties);
    		
    		addRoleToGroup(readersGroup,readerRole);
    		addRoleToGroup(publishersGroup,publisherRole);
    		addRoleToGroup(adminsGroup,adminRole);	        
	        
	        // Create a default admin user on the repository
	    	UserProperties properties = new UserProperties();
	    	properties.setProperty(UserProperties.USER_NAME,
	    						   User.ADMIN_NAME);
	    	properties.setProperty(UserProperties.USER_FIRSTNAME,
					   			   User.ADMIN_NAME);
	    	properties.setProperty(UserProperties.USER_LASTNAME,
					   			   User.ADMIN_NAME);
	    	properties.setProperty(UserProperties.USER_PASSWORD,
	    						   User.DEFAULT_PASSWORD);
	    	properties.setProperty(UserProperties.USER_ADMIN,
					   			   Boolean.TRUE);
	    	
	    	javax.jcr.Node adminNode = 
	    		internalCreateUser(null,usersNode,properties,true,true);
	    	
	    	securityRoot.setProperty(
	    			JLibraryConstants.JLIBRARY_ADMIN_USER,
					adminNode);  
	    	
		} catch (LoginException e) {			
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}	    	
	}
	
	/*
	 * Checks system workspace. This is he workspace where system properties 
	 * like users our groups are stored
	 */
	protected javax.jcr.Session checkSystemWorkspace() throws SecurityException {

		javax.jcr.Session systemSession = null;		
		try {
			SimpleCredentials creds =
                new SimpleCredentials("admin", "admin".toCharArray());
            try {
    			logger.info("Login into system workspace...");
            	systemSession = repository.login(creds,"system");
    			logger.info("Login successful.");
            } catch (NoSuchWorkspaceException nswe) {
    			logger.info("[No system workspace has been found. Doing default login now...");
            	systemSession = repository.login(creds);
    			logger.info("Default login successful...");
    			logger.info("Creating jLibrary system workspace...");
            	((WorkspaceImpl)systemSession.getWorkspace()).createWorkspace("system");
    			logger.info("jLibrary system workspace created successfully.");
    			logger.info("Trying to register node types...");
                new org.jlibrary.core.jcr.nodetypes.NodeTypeManager().registerNodeTypes(systemSession);
    			logger.info("Node type hierarchy successfully registered.");
    			logger.info("Trying to login again on jLibrary system workspace...");    	
    			systemSession.logout(); //hago el logout antes de loguearme en otro.
            	systemSession = repository.login(creds,"system");
    			logger.info("Login successful.");
            }
            
            // SessionManager.getInstance().setRepository(repository);
            // SessionManager.getInstance().setSystemSession(systemSession);
            
            javax.jcr.Node root = systemSession.getRootNode();
            javax.jcr.Node securityRoot = null;
            if (!(root.hasNode(JLibraryConstants.JLIBRARY_SECURITY))) {
            	securityRoot = root.addNode(JLibraryConstants.JLIBRARY_SECURITY,
            							    JLibraryConstants.INTERNAL_MIXIN);
            	
            } else {
            	securityRoot = root.getNode(JLibraryConstants.JLIBRARY_SECURITY);
            }
            if (!(root.hasNode(JLibraryConstants.JLIBRARY_ROOT))) {
	    			root.addNode(JLibraryConstants.JLIBRARY_ROOT,
	    							 JCRConstants.JCR_FOLDER);            
            }
            if (!(root.hasProperty(JLibraryConstants.JLIBRARY_DELETE_SCHEDULE))) {
    			root.setProperty(JLibraryConstants.JLIBRARY_DELETE_SCHEDULE,
    							 new Value[]{});            
            }
            
            javax.jcr.Node usersNode = null;
            if (!(securityRoot.hasNode(JLibraryConstants.JLIBRARY_USERS))) {
            	usersNode = securityRoot.addNode(JLibraryConstants.JLIBRARY_USERS,
            									 JLibraryConstants.INTERNAL_MIXIN);
            } else {
            	usersNode = securityRoot.getNode(JLibraryConstants.JLIBRARY_USERS);
            }
            // check default admin user
            if (!(usersNode.hasNodes())) {
            	UserProperties properties = new UserProperties();
            	properties.setProperty(UserProperties.USER_NAME,
            						   User.ADMIN_NAME);
            	properties.setProperty(UserProperties.USER_FIRSTNAME,
						   			   User.ADMIN_NAME);
            	properties.setProperty(UserProperties.USER_LASTNAME,
						   			   User.ADMIN_NAME);
            	properties.setProperty(UserProperties.USER_PASSWORD,
            						   User.DEFAULT_PASSWORD);
            	properties.setProperty(UserProperties.USER_ADMIN,
						   			   Boolean.TRUE);
            	
            	javax.jcr.Node adminNode = 
            		internalCreateUser(null,usersNode,properties,true,true);
            	
            	securityRoot.setProperty(
            			JLibraryConstants.JLIBRARY_ADMIN_USER,
						adminNode);
            	securityRoot.getSession().save();
            }
		} catch (LoginException e) {			
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
		return systemSession;
	}

	public User createUser(Ticket ticket, 
						   UserProperties userProperties) 
											throws UserAlreadyExistsException,
												   SecurityException {

		try {
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}
			if (!isAdmin(session,ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
						
			javax.jcr.Node rootNode = JCRUtils.getRootNode(session);
			javax.jcr.Node systemRoot = JCRUtils.getSystemNode(session);
	        javax.jcr.Node securityRoot = 
	        	systemRoot.getNode(JLibraryConstants.JLIBRARY_SECURITY);        
	        javax.jcr.Node usersNode = 
	        	securityRoot.getNode(JLibraryConstants.JLIBRARY_USERS);
	
			javax.jcr.Node userNode = 
				internalCreateUser(ticket,usersNode,userProperties,false,false);
		
			// Add a read restriction. So the user can log into the repository
			addRestriction(session,userNode,rootNode,false);
			
			User user = JCRAdapter.createUser(ticket,userNode);
					
			// Create an author
			JCRAuthorsModule authorsModule = new JCRAuthorsModule();
			try {
				authorsModule.findAuthorByName(ticket,user.getName());
			} catch (AuthorNotFoundException anfe) {
				try {
					AuthorProperties properties = new AuthorProperties();
					properties.addProperty(
							AuthorProperties.AUTHOR_NAME,user.getName());
					properties.addProperty(
							AuthorProperties.AUTHOR_BIO,
								user.getFirstName() + " - " + 
								user.getLastName());

					authorsModule.createAuthor(ticket,
											   properties);
				} catch (AuthorAlreadyExistsException e) {}
			}			
			return user;
		} catch (org.jlibrary.core.repository.exception.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
	}

	public javax.jcr.Node internalCreateUser(Ticket ticket,
											 javax.jcr.Node usersNode, 
			   					   			 UserProperties userProperties,
			   					   			 boolean localAdmin,
			   					   			 boolean sysAdmin) 
										throws UserAlreadyExistsException,
											   SecurityException {

		try {
			String name="";
			PropertyDef propName = userProperties.getProperty(UserProperties.USER_NAME);
			if (propName.getValue() != null) {
				name = (String)propName.getValue();
			}
			
			try {
				if (findUserByName(ticket, usersNode.getSession(),name) != null) {
					throw new UserAlreadyExistsException();
				}
			} catch (UserNotFoundException e) {}
			
			javax.jcr.Node userNode =
				usersNode.addNode(JLibraryConstants.JLIBRARY_USER,
								  JLibraryConstants.USER_MIXIN);
			
			userNode.addMixin(JCRConstants.JCR_REFERENCEABLE);
			
			userNode.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
			PropertyDef propEmail = userProperties.getProperty(UserProperties.USER_EMAIL);
			if (propEmail.getValue() != null) {
				String email = (String)propEmail.getValue();
				userNode.setProperty(JLibraryConstants.JLIBRARY_EMAIL,email);
			}
			PropertyDef propFirstName = userProperties.getProperty(UserProperties.USER_FIRSTNAME);
			if (propFirstName.getValue() != null) {
				String firstName = (String)propFirstName.getValue();
				userNode.setProperty(JLibraryConstants.JLIBRARY_FIRSTNAME,firstName);
			}
			PropertyDef propLastName = userProperties.getProperty(UserProperties.USER_LASTNAME);
			if (propLastName.getValue() != null) {
				String lastName = (String)propLastName.getValue();
				userNode.setProperty(JLibraryConstants.JLIBRARY_LASTNAME,lastName);
			}
			PropertyDef propPassword = userProperties.getProperty(UserProperties.USER_PASSWORD);
			if (propPassword.getValue() != null) {
				String password = (String)propPassword.getValue();
				userNode.setProperty(JLibraryConstants.JLIBRARY_PASSWORD,password);
			}
			userNode.setProperty(JLibraryConstants.JLIBRARY_ISADMIN,localAdmin);
			
			userNode.setProperty(JLibraryConstants.JLIBRARY_GROUPS,new Value[]{});
			userNode.setProperty(JLibraryConstants.JLIBRARY_ROLES,new Value[]{});
			
			if (sysAdmin) {
				userNode.setProperty(JLibraryConstants.JLIBRARY_SYSADMIN,true);				
			}
				
			usersNode.getSession().save();
			
			if (!sysAdmin) {
				Rol readersRole = findRolByName(ticket, usersNode.getSession(), Rol.READER_ROLE_NAME);
				JCRUtils.addNodeToProperty(readersRole.getId(),
		   				   				   userNode,
		   				   				   JLibraryConstants.JLIBRARY_ROLES);
				
				// A new user will automatically be given the readers rol
				javax.jcr.Node readersGroup = findReadersGroup(userNode.getSession());
				JCRUtils.addNodeToProperty(readersGroup,
										   userNode,
										   JLibraryConstants.JLIBRARY_GROUPS);
			}			
			return userNode;
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
	}	
	
	public User findUserByName(Ticket ticket,
							   String name) 
										throws SecurityException, 
											   UserNotFoundException {

		Session session = SessionManager.getInstance().getSession(ticket); 
		if (session == null) {
			throw new SecurityException("Session has expired. Please log in again.");
		}

		return findUserByName(ticket,session,name);
	}

	public User findUserByName(Ticket ticket,
							   javax.jcr.Session session,
			   				   String name) 
										throws SecurityException, 
											   UserNotFoundException {

		try {

			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			String statement = "//*[@jcr:primaryType='jlib:user' and @jlib:name='" + 
			   				   name + "' and @jlib:active='true']";
		
			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();

			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				return JCRAdapter.createUser(ticket,node);
			}
			throw new UserNotFoundException();
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
	}	
	
	private Group findGroupByName(Ticket ticket,
								  javax.jcr.Session session,
			   					  String name) throws SecurityException, 
			   					  					  GroupNotFoundException {

		try {

			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			String statement = "//*[@jcr:primaryType='jlib:group' and @jlib:name='" + 
			   				   name + "' and @jlib:active='true']";

			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();

			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				return JCRAdapter.createGroup(ticket,node);
			}
			throw new GroupNotFoundException();
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
	}	
	
	private Rol findRolByName(Ticket ticket,
			  				  javax.jcr.Session session,
			  				  String name) throws SecurityException, 
				  					  			  RoleNotFoundException {

		try {

			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			String statement = "//*[@jcr:primaryType='jlib:rol' and @jlib:name='" + 
			   				   name + "' and @jlib:active='true']";

			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();

			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				return JCRAdapter.createRol(ticket,node);
			}
			throw new RoleNotFoundException();
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
	}	
	
	public User findUserById(Ticket ticket, 
							 String id) throws SecurityException, 
											   UserNotFoundException {
		try {
			Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}
			
			javax.jcr.Node userNode = getUserNode(session,id);
			return JCRAdapter.createUser(ticket,userNode);
		} catch (javax.jcr.ItemNotFoundException infe) {
			throw new UserNotFoundException();
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
	}

	public Collection findAllUsers(Ticket ticket) throws SecurityException {

		List users = new ArrayList();
		try {
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			String statement = "//*[@jcr:primaryType='jlib:user' and @jlib:active='true']";
						
			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();
			
			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				users.add(JCRAdapter.createUser(ticket,node));
			}
			if (users.size() == 0) {
				users.add(User.ADMIN_USER);
			}
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
		return users;
	}

	public Collection findAllRoles(Ticket ticket) 
												throws SecurityException {

		List roles = new ArrayList();
		
		try {
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			String statement = "//*[@jcr:primaryType='jlib:rol' and @jlib:active='true']";
						
			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();
			
			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				roles.add(JCRAdapter.createRol(ticket,node));
			}
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
		return roles;		
	}

	public Collection findAllGroups(Ticket ticket) throws SecurityException {

		List groups = new ArrayList();
		
		try {
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			String statement = "//*[@jcr:primaryType='jlib:group' and @jlib:active='true']";
						
			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();
			
			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				groups.add(JCRAdapter.createGroup(ticket,node));
			}
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
		return groups;
	}

	public Rol createRol(Ticket ticket, 
						 RolProperties rolProperties) 
											throws SecurityException,
												   RoleAlreadyExistsException {

		try {						
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			if (!isAdmin(session,ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
	        javax.jcr.Node root = JCRUtils.getSystemNode(session);
	        javax.jcr.Node securityRoot = 
	        	root.getNode(JLibraryConstants.JLIBRARY_SECURITY);        
	        javax.jcr.Node rolesNode = 
	        	securityRoot.getNode(JLibraryConstants.JLIBRARY_ROLES);
			
			javax.jcr.Node rolNode = 
				internalCreateRol(ticket,rolesNode,rolProperties);
			
			return JCRAdapter.createRol(ticket,rolNode);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}		
	}

	private javax.jcr.Node internalCreateRol(Ticket ticket,
											 javax.jcr.Node rolesNode, 
			 						 		 RolProperties rolProperties) 
											throws RoleAlreadyExistsException,
												   SecurityException {

		try {			
			PropertyDef propName = rolProperties.getProperty(
					RolProperties.ROL_NAME);
			String name = (String)propName.getValue();
			
			try {
				if (findRolByName(ticket,rolesNode.getSession(),name) != null) {
					throw new RoleAlreadyExistsException();
				}
			} catch (RoleNotFoundException e) {}			
			
			javax.jcr.Node rolNode = 
				rolesNode.addNode(JLibraryConstants.JLIBRARY_ROL,
								  JLibraryConstants.ROL_MIXIN);      

			rolNode.addMixin(JCRConstants.JCR_REFERENCEABLE);

			rolNode.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
			PropertyDef propDescription = rolProperties.getProperty(
					RolProperties.ROL_DESCRIPTION);
			if (propDescription.getValue() != null) {
				String description = (String)propDescription.getValue();
				rolNode.setProperty(
						JLibraryConstants.JLIBRARY_DESCRIPTION,description);
			}
			rolNode.setProperty(JLibraryConstants.JLIBRARY_MEMBERS, 
		  						new Value[]{});
	
			rolesNode.getSession().save();
			
			return rolNode;
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}		
	}	
	
	public Rol findRol(Ticket ticket,
					   String rolId) throws SecurityException, 
					   						RoleNotFoundException {

		try {
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			javax.jcr.Node rolNode = session.getNodeByUUID(rolId);
			return JCRAdapter.createRol(ticket,rolNode);
		} catch (javax.jcr.ItemNotFoundException infe) {
			throw new RoleNotFoundException();
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
	}

	public Group createGroup(Ticket ticket, 
							 GroupProperties groupProperties) 
											throws SecurityException,
												   GroupAlreadyExistsException {

		try {		
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			if (!isAdmin(session,ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
	        javax.jcr.Node root = JCRUtils.getSystemNode(session);
	        javax.jcr.Node securityRoot = 
	        	root.getNode(JLibraryConstants.JLIBRARY_SECURITY);        
	        javax.jcr.Node groupsNode = 
	        	securityRoot.getNode(JLibraryConstants.JLIBRARY_GROUPS);
			
			javax.jcr.Node groupNode = internalCreateGroup(ticket,
														   groupsNode,
														   groupProperties);
			
			return JCRAdapter.createGroup(ticket,groupNode);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
	}

	public javax.jcr.Node internalCreateGroup(Ticket ticket,
											  javax.jcr.Node groupsNode, 
											  GroupProperties groupProperties) 
											throws SecurityException,
												   GroupAlreadyExistsException {
		
		try {						
			PropertyDef propName = groupProperties.getProperty(
					GroupProperties.GROUP_NAME);
			String name = (String)propName.getValue();
			
			try {
				if (findGroupByName(ticket,groupsNode.getSession(),name) != null) {
					throw new GroupAlreadyExistsException();
				}
			} catch (GroupNotFoundException e) {}
			
			javax.jcr.Node groupNode = 
				groupsNode.addNode(JLibraryConstants.JLIBRARY_GROUP,
								   JLibraryConstants.GROUP_MIXIN);      
	        
			groupNode.addMixin(JCRConstants.JCR_REFERENCEABLE);
			
			groupNode.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
			PropertyDef propDescription = groupProperties.getProperty(
					GroupProperties.GROUP_DESCRIPTION);
			if (propDescription.getValue() != null) {
				String description = (String)propDescription.getValue();
				groupNode.setProperty(
						JLibraryConstants.JLIBRARY_DESCRIPTION,description);
			}
			
			groupNode.setProperty(JLibraryConstants.JLIBRARY_USERS,new Value[]{});			
			groupNode.setProperty(JLibraryConstants.JLIBRARY_ROLES,new Value[]{});
			
			groupsNode.getSession().save();
			
			return groupNode;
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}		
	}
	
	public Group findGroupById(Ticket ticket, 
						   		 String id) throws SecurityException, 
												   GroupNotFoundException {

		try {
			Session session = SessionManager.getInstance().getSession(ticket); 			
			javax.jcr.Node group = session.getNodeByUUID(id);
			
			return JCRAdapter.createGroup(ticket,group);
		} catch (ItemNotFoundException infe) {
			throw new GroupNotFoundException();
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
	}

	public void removeUser(Ticket ticket, 
						   String userId) throws SecurityException, 
										   		 UserNotFoundException {

		try {
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			if (!isAdmin(session,ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			javax.jcr.Node userNode = getUserNode(session,userId); 
			if (userNode.getProperty(JLibraryConstants.JLIBRARY_ACTIVE).getBoolean() == false) {
				throw new UserNotFoundException();
			}
			String userName = userNode.getProperty(
						JLibraryConstants.JLIBRARY_NAME).getString();

			new JCRFavoritesModule().removeFavoriteReferences(userNode);
			JCRUtils.removeReferences(userNode);
			
			// Now we will tag the user as not active instead of deleting it
			userNode.setProperty(JLibraryConstants.JLIBRARY_ACTIVE,false);
			session.save();
			
			// Remove author
			JCRAuthorsModule authorsModule = new JCRAuthorsModule();
			
			try {
				Author author = authorsModule.findAuthorByName(ticket,userName);
				authorsModule.deleteAuthor(ticket,author.getId());
			} catch (AuthorNotFoundException e) {}
		} catch (ItemNotFoundException infe) {
			throw new UserNotFoundException();			
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (org.jlibrary.core.repository.exception.RepositoryException re) {
			logger.error(re.getMessage(),re);
			throw new SecurityException(re);
		}
	}

	public void removeGroup(Ticket ticket, 
							String groupId) throws SecurityException, 
												   GroupNotFoundException {

		try {
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			if (!isAdmin(session,ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			javax.jcr.Node groupNode = session.getNodeByUUID(groupId);
			
			// process user references
			JCRUtils.removeReferences(groupNode);			
			groupNode.remove();
			session.save();
		} catch (ItemNotFoundException infe) {
			throw new GroupNotFoundException();			
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}				
	}

	public void removeRol(Ticket ticket,	
						  String rolId) throws SecurityException, 
						  					   RoleNotFoundException {

		try {
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			if (!isAdmin(session,ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			javax.jcr.Node rolNode = session.getNodeByUUID(rolId);
			
			JCRUtils.removeReferences(rolNode);
			rolNode.remove();
			session.save();
		} catch (ItemNotFoundException infe) {
			throw new RoleNotFoundException();
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}		
	}

	public User updateUser(Ticket ticket, 
						   UserProperties userProperties) 
												throws SecurityException {


		try {
			String userId = (String)userProperties.getProperty(
									UserProperties.USER_ID).getValue();
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			if (!isAdmin(session,ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
						
			javax.jcr.Node userNode = getUserNode(session,userId);
			
			String userName = userNode.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString();
			// Do not allow property change on default users
			if (!userName.equals(User.ADMIN_NAME)) {
			
				PropertyDef propFirstName = userProperties.getProperty(UserProperties.USER_FIRSTNAME);
				if (propFirstName.getValue() != null) {
					String firstName = (String)propFirstName.getValue();
					userNode.setProperty(JLibraryConstants.JLIBRARY_FIRSTNAME,
										 firstName);
				}
				PropertyDef propLastName = userProperties.getProperty(UserProperties.USER_LASTNAME);
				if (propLastName != null) {
					String lastName = (String)propLastName.getValue();
					userNode.setProperty(JLibraryConstants.JLIBRARY_LASTNAME,
							 			 lastName);
				}
				PropertyDef propEmail = userProperties.getProperty(UserProperties.USER_EMAIL);
				if (propEmail.getValue() != null) {
					String email = (String)propEmail.getValue();
					userNode.setProperty(JLibraryConstants.JLIBRARY_EMAIL,
							 			 email);
				}                        

			}

			// Check for password change
			PropertyDef propPassword = userProperties.getProperty(UserProperties.USER_PASSWORD);
			if (propPassword.getValue() != null) {
				String password = (String)propPassword.getValue();
				userNode.setProperty(JLibraryConstants.JLIBRARY_PASSWORD,
						 			 password);
			}			
			
			PropertyDef propRepository = userProperties.getProperty(
					UserProperties.USER_REPOSITORY);
			
			if (propRepository.getValue() != null) {				
				// Update roles
				PropertyDef[] roles = userProperties.getPropertyList(
						UserProperties.USER_ADD_ROL);
				if (roles != null) {
					for (int i = 0; i < roles.length; i++) {
						Rol rol = (Rol)roles[i].getValue();
						
						javax.jcr.Node rolNode = 
							session.getNodeByUUID(rol.getId());

						JCRUtils.addNodeToProperty(userNode,
								   				   rolNode,
								   				   JLibraryConstants.JLIBRARY_MEMBERS);			
						JCRUtils.addNodeToProperty(rolNode,
				   				   				   userNode,
				   				   				   JLibraryConstants.JLIBRARY_ROLES);			
					}
				}

				roles = userProperties.getPropertyList(UserProperties.USER_DELETE_ROL);
				if (roles != null) {
					for (int i = 0; i < roles.length; i++) {
						Rol rol = (Rol)roles[i].getValue();
						javax.jcr.Node rolNode = 
							session.getNodeByUUID(rol.getId());

						JCRUtils.removeNodeFromProperty(userNode,
				   				   						rolNode,
				   				   						JLibraryConstants.JLIBRARY_MEMBERS);			

						JCRUtils.removeNodeFromProperty(rolNode,
				   				   				   		userNode,
				   				   				   		JLibraryConstants.JLIBRARY_ROLES);			
					}
				}

				// Update groups
				PropertyDef[] groups = userProperties.getPropertyList(
						UserProperties.USER_ADD_GROUP);
				if (groups != null) {
					for (int i = 0; i < groups.length; i++) {
						Group group = (Group)groups[i].getValue();
						
						javax.jcr.Node groupNode = 
							session.getNodeByUUID(group.getId());

						JCRUtils.addNodeToProperty(userNode,
								   				   groupNode,
								   				   JLibraryConstants.JLIBRARY_USERS);			
						JCRUtils.addNodeToProperty(groupNode,
				   				   				   userNode,
				   				   				   JLibraryConstants.JLIBRARY_GROUPS);			
					}
				}

				groups = userProperties.getPropertyList(UserProperties.USER_DELETE_GROUP);
				if (groups != null) {
					for (int i = 0; i < groups.length; i++) {
						Group group = (Group)groups[i].getValue();
						javax.jcr.Node groupNode = 
							session.getNodeByUUID(group.getId());

						JCRUtils.removeNodeFromProperty(userNode,
				   				   						groupNode,
				   				   						JLibraryConstants.JLIBRARY_USERS);			

						JCRUtils.removeNodeFromProperty(groupNode,
				   				   				   		userNode,
				   				   				   		JLibraryConstants.JLIBRARY_GROUPS);			
					}
				}
				// Update restrictions
				PropertyDef[] restrictions = userProperties.getPropertyList(
						UserProperties.USER_ADD_RESTRICTION);
				
				if (restrictions!= null) {
					for (int i = 0; i < restrictions.length; i++) {
						String nodeId = (String)restrictions[i].getValue();
						javax.jcr.Node node = 
							session.getNodeByUUID(nodeId);
						addRestriction(session,userNode, node, true);			
					}
				}

				restrictions = userProperties.getPropertyList(
						UserProperties.USER_DELETE_RESTRICTION);
				if (restrictions!= null) {
					for (int i = 0; i < restrictions.length; i++) {
						String nodeId = (String)restrictions[i].getValue();
						javax.jcr.Node node = 
							session.getNodeByUUID(nodeId);

						removeRestriction(session,userNode, node);			
					}
				}				
				
			}	
			session.save();
			
			return JCRAdapter.createUser(ticket,userNode);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
	}

	private void removeRestriction(javax.jcr.Session session,
								   javax.jcr.Node userNode, 
								   javax.jcr.Node node) throws RepositoryException {
		
		if (node.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
				node.checkout();
		}
		JCRUtils.removeNodeFromProperty(userNode,
				   				   	 	node,
				   				   	 	JLibraryConstants.JLIBRARY_RESTRICTIONS);
		session.save();
		
		NodeIterator it = node.getNodes();
		while (it.hasNext()) {
			javax.jcr.Node child = (javax.jcr.Node)it.next();
			if (!JCRUtils.isActive(child)) {
				continue;
			}
			if (child.isNodeType(JLibraryConstants.DOCUMENT_MIXIN) ||
				child.isNodeType(JLibraryConstants.DIRECTORY_MIXIN) ||
				child.isNodeType(JLibraryConstants.RESOURCE_MIXIN)) {
				removeRestriction(session,userNode,child);
			}
		}
		
	}

	private void addRestriction(javax.jcr.Session session,
								javax.jcr.Node memberNode, 
								javax.jcr.Node node,
								boolean propagate) throws RepositoryException {
		
		if (node.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
				node.checkout();
		}
		JCRUtils.addNodeToProperty(memberNode,
				   				   node,
				   				   JLibraryConstants.JLIBRARY_RESTRICTIONS);
		session.save();
		
		if (propagate) {
			NodeIterator it = node.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node child = (javax.jcr.Node)it.next();
				if (!JCRUtils.isActive(child)) {
					continue;
				}
				if (child.isNodeType(JLibraryConstants.DOCUMENT_MIXIN) ||
						child.isNodeType(JLibraryConstants.DIRECTORY_MIXIN) ||
						child.isNodeType(JLibraryConstants.RESOURCE_MIXIN)) {
					addRestriction(session,memberNode,child,true);
				}
			}
		}
	}

	public Group updateGroup(Ticket ticket, 
							 GroupProperties groupProperties) 
												throws SecurityException {

		try {
			String groupId = (String)groupProperties.getProperty(
								GroupProperties.GROUP_ID).getValue();
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			if (!isAdmin(session,ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			javax.jcr.Node groupNode = session.getNodeByUUID(groupId);
			String groupName = groupNode.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString();
			
			// Do not allow property change on defaul groups
			if (!groupName.equals(Group.ADMINS_GROUP_NAME) &&
				!groupName.equals(Group.READERS_GROUP_NAME) &&
				!groupName.equals(Group.PUBLISHERS_GROUP_NAME)) {
				
				PropertyDef propName = 
					groupProperties.getProperty(GroupProperties.GROUP_NAME);
				if (propName.getValue() != null) {
					String name = (String)propName.getValue();
					groupNode.setProperty(JLibraryConstants.JLIBRARY_NAME,
										  name);
				}
				PropertyDef propDescription = 
					groupProperties.getProperty(GroupProperties.GROUP_DESCRIPTION);
				if (propDescription.getValue() != null) {
					String description = (String)propDescription.getValue();
					groupNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,
							 			  description);
				}
			}
						
			// Update roles
			PropertyDef[] roles = groupProperties.getPropertyList(
					GroupProperties.GROUP_ADD_ROL);
			if (roles != null) {
				for (int i = 0; i < roles.length; i++) {
					Rol rol = (Rol)roles[i].getValue();
					
					javax.jcr.Node rolNode = 
						session.getNodeByUUID(rol.getId());
					
					addRoleToGroup(groupNode, rolNode);			
				}
			}

			roles = groupProperties.getPropertyList(GroupProperties.GROUP_DELETE_ROL);
			if (roles != null) {
				for (int i = 0; i < roles.length; i++) {
					Rol rol = (Rol)roles[i].getValue();
					javax.jcr.Node rolNode = 
						session.getNodeByUUID(rol.getId());

					JCRUtils.removeNodeFromProperty(groupNode,
		   											rolNode,
		   											JLibraryConstants.JLIBRARY_MEMBERS);			

					JCRUtils.removeNodeFromProperty(rolNode,
		   				   							groupNode,
		   				   							JLibraryConstants.JLIBRARY_ROLES);			

				}
			}
			
			// Update restrictions
			PropertyDef[] restrictions = groupProperties.getPropertyList(
					GroupProperties.GROUP_ADD_RESTRICTION);
			
			if (restrictions!= null) {
				for (int i = 0; i < restrictions.length; i++) {
					String nodeId = (String)restrictions[i].getValue();
					javax.jcr.Node node = 
						session.getNodeByUUID(nodeId);

					addRestriction(session,groupNode, node, true);			
			
				}
				session.save();
			}

			restrictions = groupProperties.getPropertyList(
					GroupProperties.GROUP_DELETE_RESTRICTION);
			if (restrictions!= null) {
				for (int i = 0; i < restrictions.length; i++) {
					String nodeId = (String)restrictions[i].getValue();
					javax.jcr.Node node = 
						session.getNodeByUUID(nodeId);

					removeRestriction(session,groupNode,node);		
				}
				session.save();
			}			
			
			// Update users
			PropertyDef[] users = groupProperties.getPropertyList(
					GroupProperties.GROUP_ADD_USER);
			if (users != null) {
				for (int i = 0; i < users.length; i++) {
					User user = (User)users[i].getValue();
					javax.jcr.Node userNode = getUserNode(session,user.getId());
					JCRUtils.addNodeToProperty(userNode.getUUID(),
							   				   groupNode,
							   				   JLibraryConstants.JLIBRARY_USERS);			
					JCRUtils.addNodeToProperty(groupNode.getUUID(),
			   				   				   userNode,
			   				   				   JLibraryConstants.JLIBRARY_GROUPS);			
				}
			}

			users = groupProperties.getPropertyList(GroupProperties.GROUP_DELETE_USER);
			if (users != null) {
				for (int i = 0; i < users.length; i++) {
					User user = (User)users[i].getValue();
					javax.jcr.Node userNode = getUserNode(session,user.getId());
					JCRUtils.removeNodeFromProperty(userNode.getUUID(),
			   				   						groupNode,
			   				   						JLibraryConstants.JLIBRARY_USERS);			
					JCRUtils.removeNodeFromProperty(groupNode.getUUID(),
			   				   						userNode,
			   				   						JLibraryConstants.JLIBRARY_GROUPS);			
				}
			}

			session.save();
			
			return JCRAdapter.createGroup(ticket,groupNode);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}		
	}

	private void addRoleToGroup(javax.jcr.Node groupNode, 
								javax.jcr.Node rolNode) 
												throws RepositoryException {
		
		JCRUtils.addNodeToProperty(groupNode,
				   				   rolNode,
				   				   JLibraryConstants.JLIBRARY_MEMBERS);			
		JCRUtils.addNodeToProperty(rolNode,
				   				   groupNode,
				   				   JLibraryConstants.JLIBRARY_ROLES);
	}

	public Rol updateRol(Ticket ticket, 
						 RolProperties rolProperties) 
										throws SecurityException {


		try {
			String rolId = (String)rolProperties.getProperty(
					RolProperties.ROL_ID).getValue();
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			if (!isAdmin(session,ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			javax.jcr.Node rolNode = session.getNodeByUUID(rolId);
			String rolName = rolNode.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString();
			// Do not allow property change on defaul groups
			if (!rolName.equals(Rol.ADMIN_ROLE_NAME) &&
				!rolName.equals(Rol.PUBLISHER_ROLE_NAME) &&
				!rolName.equals(Rol.READER_ROLE_NAME)) {
							
				PropertyDef propName = 
					rolProperties.getProperty(RolProperties.ROL_NAME);
				if (propName.getValue() != null) {
					String name = (String)propName.getValue();
					rolNode.setProperty(JLibraryConstants.JLIBRARY_NAME,
										  name);
				}
				PropertyDef propDescription = 
					rolProperties.getProperty(RolProperties.ROL_DESCRIPTION);
				if (propDescription != null) {
					String description = (String)propDescription.getValue();
					rolNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,
							 			  description);
				}
			}

			// Update groups
			PropertyDef[] groups = rolProperties.getPropertyList(
					RolProperties.ROL_ADD_GROUP);
			if (groups != null) {
				for (int i = 0; i < groups.length; i++) {
					Group group = (Group)groups[i].getValue();
					
					javax.jcr.Node groupNode = 
						session.getNodeByUUID(group.getId());

					JCRUtils.addNodeToProperty(rolNode,
							   				   groupNode,
							   				   JLibraryConstants.JLIBRARY_ROLES);			
					JCRUtils.addNodeToProperty(groupNode,
			   				   				   rolNode,
			   				   				   JLibraryConstants.JLIBRARY_MEMBERS);			
				}
			}

			groups = rolProperties.getPropertyList(RolProperties.ROL_DELETE_GROUP);
			if (groups != null) {
				for (int i = 0; i < groups.length; i++) {
					Group group = (Group)groups[i].getValue();
					javax.jcr.Node groupNode = 
						session.getNodeByUUID(group.getId());

					JCRUtils.removeNodeFromProperty(rolNode,
			   				   						groupNode,
			   				   						JLibraryConstants.JLIBRARY_ROLES);			

					JCRUtils.removeNodeFromProperty(groupNode,
			   				   				   		rolNode,
			   				   				   		JLibraryConstants.JLIBRARY_MEMBERS);			
				}
			}
			
			// Update users
			PropertyDef[] users = rolProperties.getPropertyList(
					RolProperties.ROL_ADD_USER);
			if (users != null) {
				for (int i = 0; i < users.length; i++) {
					User user = (User)users[i].getValue();
					javax.jcr.Node userNode = getUserNode(session,user.getId());
					JCRUtils.addNodeToProperty(userNode.getUUID(),
							   				   rolNode,
							   				   JLibraryConstants.JLIBRARY_MEMBERS);			
					JCRUtils.addNodeToProperty(rolNode.getUUID(),
			   				   				   userNode,
			   				   				   JLibraryConstants.JLIBRARY_ROLES);			
				}
			}

			users = rolProperties.getPropertyList(RolProperties.ROL_DELETE_USER);
			if (users != null) {
				for (int i = 0; i < users.length; i++) {
					User user = (User)users[i].getValue();
					javax.jcr.Node userNode = getUserNode(session,user.getId());
					JCRUtils.removeNodeFromProperty(userNode.getUUID(),
			   				   						rolNode,
			   				   						JLibraryConstants.JLIBRARY_MEMBERS);			
					JCRUtils.removeNodeFromProperty(rolNode.getUUID(),
			   				   						userNode,
			   				   						JLibraryConstants.JLIBRARY_ROLES);			
				}
			}
			
			session.save();
			
			return JCRAdapter.createRol(ticket,rolNode);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}		
		
	}

	private Ticket systemLogin(Credentials credentials) 
											throws UserNotFoundException, 
												   AuthenticationException, 
												   SecurityException, 
												   ConnectException,
												   RepositoryNotFoundException{
		logger.info(
			"Trying to login to jLibrary with user '" +
			credentials.getUser()+"'");
				
		Ticket ticket = null;
		try {
			javax.jcr.Session systemSession = null;
			
			if (repository == null) {
				// reregister repository
				systemSession = registerRepository(this.repositoriesHome);
				SessionManager.getInstance().setRepository(repository);				
			}
			else{
				SessionManager.getInstance().setRepository(repository);				
				systemSession = SessionManager.getInstance().getNewSystemSession();				
			}
			
			User user = canAccessToRepository(credentials, systemSession);			
			ticket = new Ticket();
			ticket.setId(UUIDGenerator.generate(ticket));
			// The system repository always is a jLibrary repository
			ticket.setUser(user);
			ticket.setRepositoryId("-1");
			
			SessionManager sessionManager = SessionManager.getInstance();
			if (sessionManager.getSession(ticket) == null) {
				SessionManager.getInstance().attachSystemSession(ticket, systemSession);
			}
			
		} catch (org.jlibrary.core.repository.exception.RepositoryException re) {
			logger.error(re.getMessage(),re);
			throw new SecurityException(re);
		} catch (NamingException ne) {
			logger.error(ne.getMessage(),ne);
			throw new SecurityException(ne);		
		} catch (NoSuchWorkspaceException e) {
			throw new RepositoryNotFoundException(e);			
		} catch (LoginException e) {			
			logger.error(e.getMessage(),e);
			throw new AuthenticationException(e);
		} catch (RepositoryException e) {
			throw new SecurityException(e);
		}
		return ticket;
	}	
	
	public Ticket login(Credentials credentials,
						String name) throws UserNotFoundException, 
											AuthenticationException, 
											SecurityException, 
											ConnectException,
											RepositoryNotFoundException {
		
		if (name.equals(SecurityService.SYSTEM_REPOSITORY)) {
			return systemLogin(credentials);
		}
		return internalLogin(credentials,name);
	}
	
	private Ticket internalLogin(Credentials credentials,
						String name) throws UserNotFoundException, 
											AuthenticationException, 
											SecurityException, 
											ConnectException,
											RepositoryNotFoundException {
		Ticket ticket = null;
		try {
			javax.jcr.Session systemSession = null;
			
			if (repository == null) {
				// reregister repository
				systemSession = registerRepository(this.repositoriesHome);
				SessionManager.getInstance().setRepository(repository);				
			}
			else{
				SessionManager.getInstance().setRepository(repository);				
				systemSession = SessionManager.getInstance().getNewSystemSession();
			}
			
			SimpleCredentials creds = 
				new SimpleCredentials(credentials.getUser(), 
									  credentials.getPassword().toCharArray());
			name = JCRUtils.lookupWorkspaceName(systemSession,name);

			Session session = repository.login(creds, name);
			
			ticket = new Ticket();
			ticket.setId(UUIDGenerator.generate(ticket));
			
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			ticket.setRepositoryId(root.getUUID());
		
			User user = findUserByName(ticket,
									   session, 
									   credentials.getUser());
			if (!(user.getPassword().equals(credentials.getPassword()))) {
				throw new AuthenticationException("Invalid password");
			}
			ticket.setUser(user);

			SessionManager sessionManager = SessionManager.getInstance();
			if (sessionManager.getSession(ticket) == null) {
				SessionManager.getInstance().attachSession(ticket, session);
				SessionManager.getInstance().attachSystemSession(ticket, systemSession);
			}
		} catch (org.jlibrary.core.repository.exception.RepositoryException re) {
			logger.error(re.getMessage(),re);
			throw new SecurityException(re);			
		} catch (NamingException ne) {
			logger.error(ne.getMessage(),ne);
			throw new SecurityException(ne);
		} catch (UserNotFoundException unfe) {
			logger.error(unfe.getMessage(),unfe);
			throw unfe;
		} catch (LoginException e) {			
			logger.error(e.getMessage(),e);
			throw new AuthenticationException(e);
		} catch (NoSuchWorkspaceException e) {
			throw new RepositoryNotFoundException(e);
		} catch (RepositoryException e) {
			throw new SecurityException(e);
		}		
		return ticket;
	}

	private User canAccessToRepository(Credentials credentials,  Session systemSession) 
										throws javax.jcr.RepositoryException,
											   UserNotFoundException, 
											   AuthenticationException, 
											   SecurityException {
		
		try {
			Workspace workspace = systemSession.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			String statement = "//*[@jcr:primaryType='jlib:user' and @jlib:name='" 
				+ credentials.getUser()+"']";
						
			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();
			
			NodeIterator it = result.getNodes();
			if (it.hasNext()) {
				javax.jcr.Node node = it.nextNode();
				User user = JCRAdapter.createUser(null,node);
				if (user.getPassword().equals(credentials.getPassword())) {
					return user;
				} else {
					throw new AuthenticationException();
				}
			}
			throw new UserNotFoundException();
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
	}
	
	public static javax.jcr.Node findAdminUser(javax.jcr.Session session) 
										throws RepositoryException {

		try {
			javax.jcr.Node systemNode = JCRUtils.getSystemNode(session);
			
			javax.jcr.Node securityRoot = 
				systemNode.getNode(
						JLibraryConstants.JLIBRARY_SECURITY);

			javax.jcr.Node adminUser = securityRoot.getProperty(
					JLibraryConstants.JLIBRARY_ADMIN_USER).getNode();

			return adminUser;
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}			
	}
	
	public static javax.jcr.Node getUserNode(javax.jcr.Session session, 
											 String userId) 
										throws RepositoryException {

		try {
			if (userId.equals(User.ADMIN_CODE)) {
				return findAdminUser(session);
			} else {
				return session.getNodeByUUID(userId);
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}			
	}
	
	/**
	 * Check node access
	 * 
	 * @param node Node that we want to access
	 * @param memberId User that want to access
	 * 
	 * @return <code>true</code> if the user can access and <code>false</code>
	 * otherwise
	 * 
	 * @throws SecurityException If there is any problem checking node 
	 * access
	 */
	private static boolean canAccess(javax.jcr.Node node,
									 String memberId) 
										throws SecurityException {
		
		
		try {
			if (!node.hasProperty(JLibraryConstants.JLIBRARY_RESTRICTIONS)) {
				return false;
			}
			
			// Check if the user is an admin user
			javax.jcr.Node userNode = 
				JCRSecurityService.getUserNode(node.getSession(),memberId);
			if (userNode.hasProperty(JLibraryConstants.JLIBRARY_ISADMIN)) {
				boolean isAdmin = userNode.getProperty(
						JLibraryConstants.JLIBRARY_ISADMIN).getBoolean();
				if (isAdmin) {
					return true;
				}
			}
			
			Value[] userGroups = userNode.getProperty(
					JLibraryConstants.JLIBRARY_GROUPS).getValues();
			
			javax.jcr.Property property = 
				node.getProperty(JLibraryConstants.JLIBRARY_RESTRICTIONS);
			Value[] restrictions = property.getValues();
			for (int i = 0; i < restrictions.length; i++) {
				if (restrictions[i].getString().equals(memberId)) {
					return true;
				}
				for (int j = 0;j<userGroups.length;j++) {
					if (restrictions[i].getString().equals(
							userGroups[j].getString())) {
						return true;
					}
				}
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}			
		return false;
	}
	
	public static boolean canRead(javax.jcr.Node node, String memberId) 
										throws javax.jcr.RepositoryException,
											   SecurityException {

		if (!canAccess(node, memberId)) {
			return false;
		}

		javax.jcr.Node userNode = 
			JCRSecurityService.getUserNode(node.getSession(),memberId);		
		if (userNode.hasProperty(JLibraryConstants.JLIBRARY_ISADMIN)) {
			boolean isAdmin = userNode.getProperty(
					JLibraryConstants.JLIBRARY_ISADMIN).getBoolean();
			if (isAdmin) {
				return true;
			}
		}
		
		if (!userNode.hasProperty(JLibraryConstants.JLIBRARY_ROLES)) {
			return false;
		}
		
		String[] roles = obtainRoles(userNode);
		
		for (int i = 0; i < roles.length; i++) {
			javax.jcr.Node rol = userNode.getSession().getNodeByUUID(roles[i]);
			String propertyName = rol.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString();
			if ((propertyName.equals(Rol.ADMIN_ROLE_NAME)) ||
				(propertyName.equals(Rol.READER_ROLE_NAME)) ||
				(propertyName.equals(Rol.PUBLISHER_ROLE_NAME))) {
				return true;
			}
		}
		return false;
	}
	
	private static String[] obtainRoles(Node userNode) 
									throws javax.jcr.RepositoryException {

		javax.jcr.Session session = userNode.getSession();
		HashSet set = new HashSet();
		
		Value[] roles = userNode.getProperty(
				JLibraryConstants.JLIBRARY_ROLES).getValues();
		for (int i = 0; i < roles.length; i++) {
			set.add(roles[i].getString());
		}

		// Now we will get all the roles from user's groups
		Value[] userGroups = userNode.getProperty(
				JLibraryConstants.JLIBRARY_GROUPS).getValues();
		for (int i = 0; i < userGroups.length; i++) {
			String groupId = userGroups[i].getString();
			javax.jcr.Node groupNode = session.getNodeByUUID(groupId);
			Value[] groupRoles = groupNode.getProperty(
					JLibraryConstants.JLIBRARY_ROLES).getValues();
			for (int j = 0; j < groupRoles.length; j++) {
				set.add(groupRoles[j].getString());
			}			
		}
		
		return (String[])set.toArray(new String[]{});
	}
	
	/**
	 * <p>This method will return true if the node passed as a parameter has been 
	 * created by a jLibrary "system" administrator user. In this case will only 
	 * return true when the node has been created by the default administrator user 
	 * and will return false when the node has been created by a regular user with 
	 * administrator rights.</p>
	 * 
	 * <p>The idea is that nodes created by the system administrator must have special 
	 * permissions like for example can only be removed by users with administrator 
	 * rights but not by editor users.</p>
	 * 
	 * @param node
	 * @return
	 * @throws javax.jcr.RepositoryException
	 * @throws SecurityException
	 */
	private static boolean createdByAdmin(javax.jcr.Node node) 
										throws javax.jcr.RepositoryException,
											   SecurityException {
		
		String creatorId = node.getProperty(JLibraryConstants.JLIBRARY_CREATOR).getValue().getString();
		javax.jcr.Node creatorNode = 
			JCRSecurityService.getUserNode(node.getSession(),creatorId);
		boolean adminCreated = false;
		if (creatorNode.hasProperty(JLibraryConstants.JLIBRARY_ISADMIN)) {
			adminCreated = creatorNode.getProperty(
					JLibraryConstants.JLIBRARY_ISADMIN).getBoolean();
			if (adminCreated) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean canWrite(javax.jcr.Node node, String memberId) 
										throws javax.jcr.RepositoryException,
											   SecurityException {

		canAccess(node, memberId);
		
		javax.jcr.Node userNode = 
			JCRSecurityService.getUserNode(node.getSession(),memberId);
		
		if (!userNode.hasProperty(JLibraryConstants.JLIBRARY_ROLES)) {
			return false;
		}
		if (userNode.hasProperty(JLibraryConstants.JLIBRARY_ISADMIN)) {
			boolean isAdmin = userNode.getProperty(
					JLibraryConstants.JLIBRARY_ISADMIN).getBoolean();
			if (isAdmin) {
				return true;
			}
		}

		boolean adminCreated = createdByAdmin(node);
		String[] roles = obtainRoles(userNode);
		for (int i = 0; i < roles.length; i++) {
			javax.jcr.Node rol = userNode.getSession().getNodeByUUID(roles[i]);
			String propertyName = rol.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString();
			if (!adminCreated) {
				if ((propertyName.equals(Rol.ADMIN_ROLE_NAME)) ||
					(propertyName.equals(Rol.PUBLISHER_ROLE_NAME))) {
					return true;
				}
			} else {
				if (propertyName.equals(Rol.ADMIN_ROLE_NAME)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean canAdmin(javax.jcr.Node node, String memberId) 
										throws javax.jcr.RepositoryException,
										   	   SecurityException {

		canAccess(node, memberId);
		return isAdmin(node.getSession(),memberId);
	}	
	
	public static boolean isAdmin(javax.jcr.Session session, 
								  String memberId) 
										throws javax.jcr.RepositoryException{

		javax.jcr.Node userNode = 
			JCRSecurityService.getUserNode(session,memberId);
		
		if (!userNode.hasProperty(JLibraryConstants.JLIBRARY_ROLES)) {
			return false;
		}
		if (userNode.hasProperty(JLibraryConstants.JLIBRARY_ISADMIN)) {
			boolean isAdmin = userNode.getProperty(
					JLibraryConstants.JLIBRARY_ISADMIN).getBoolean();
			if (isAdmin) {
				return true;
			}
		}

		// To be an admin the user will have to: 
		//   1 - Have the admin role
		//   2 - Be on a group with admin rights on the repository root node
		javax.jcr.Node node = JCRUtils.getRootNode(session);
		String[] roles = obtainRoles(userNode);

		for (int i = 0; i < roles.length; i++) {
			javax.jcr.Node rol = userNode.getSession().getNodeByUUID(roles[i]);
			String propertyName = rol.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString();
			if ((propertyName.equals(Rol.ADMIN_ROLE_NAME))) {
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * 	 * @param ctx
	 */
	public static void initRepository(Context ctx) {
		
		//logger.info("initRepository(Context)");
		if (repository == null) {
			instance = new JCRSecurityService(ctx);
		}
	}	

	/**
	 * Return the readers group
	 * 
	 * @param session Session
	 * @return Node Readers group or <code>null</code> if it can't be found
	 * 
	 * @throws RepositoryException If some error happens
	 */
	static javax.jcr.Node findReadersGroup(javax.jcr.Session session) 
											throws RepositoryException {

		try {
			javax.jcr.Node systemNode = JCRUtils.getSystemNode(session);

			javax.jcr.Node securityRoot = systemNode.getNode(
					JLibraryConstants.JLIBRARY_SECURITY);

			javax.jcr.Node groupsNode = securityRoot.getNode(
					JLibraryConstants.JLIBRARY_GROUPS);
			NodeIterator it = groupsNode.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node groupNode = (javax.jcr.Node) it.next();
				if (groupNode.getProperty(JLibraryConstants.JLIBRARY_NAME).
						getString().equals(Group.READERS_GROUP_NAME)) {
					return groupNode;
				}
			}

			return null;
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}			
	}
	
	public Collection findAllRestrictions(Ticket ticket, 
										  String nodeId) 
													throws SecurityException {

		try {
			Session session = SessionManager.getInstance().getSession(ticket); 
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			javax.jcr.Node node = session.getNodeByUUID(nodeId);
			return JCRAdapter.createRestrictions(node);
			
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
	}
	
	/**
	 * Disconnects an user from this repository. This method does not 
	 * do additional operations. If you want to close or unregister the 
	 * repository, then you should use <code>shutdownRepository()</code>
	 * method.
	 * 
	 * @param ticket Ticket with user information
	 * 
	 * @throws SecurityException If there is some problem disconnecting 
	 * the user from the server
	 */
	public void disconnect(Ticket ticket) throws SecurityException {
		
		SessionManager sessionManager = SessionManager.getInstance();
		sessionManager.dettach(ticket);		
	}	
	
	/**
	 * This method will unregister the repository. It must be called to 
	 * definitively release open resources on J2EE containers. 
	 *
	 */
	public void shutdownRepository() throws SecurityException {
		
		try {
			if (ctx != null) {
				// We will unregister, unlocking and close then
				RegistryHelper.unregisterRepository(ctx,"jlibrary.repository");
				repository = null;
				ctx = null;
			}
		} catch (NamingException e) {
			
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}		
	}

}
