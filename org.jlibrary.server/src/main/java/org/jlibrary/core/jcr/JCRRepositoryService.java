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
package org.jlibrary.core.jcr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.Workspace;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.jackrabbit.util.Text;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Favorite;
import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Note;
import org.jlibrary.core.entities.Relation;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.RepositoryInfo;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.jcr.modules.JCRAuthorsModule;
import org.jlibrary.core.jcr.modules.JCRBookmarksModule;
import org.jlibrary.core.jcr.modules.JCRCategoriesModule;
import org.jlibrary.core.jcr.modules.JCRCleanupModule;
import org.jlibrary.core.jcr.modules.JCRFavoritesModule;
import org.jlibrary.core.jcr.modules.JCRImportExportModule;
import org.jlibrary.core.jcr.modules.JCRLocksModule;
import org.jlibrary.core.jcr.modules.JCRResourcesModule;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.RepositoryProperties;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.AuthorAlreadyExistsException;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.repository.exception.CategoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This is the default implementation of RepositoryService interface. It works 
 * localy directly talking to the repository. It uses several helper modules 
 * to delegate common operations. Use it on your applications if you wish 
 * good performance as an alternative to the web services-based approach.</p> 
 * 
 * <p>This repository service is completly based on the JSR-170 specification 
 * and also slightly tighted to Apache Jackrabbit reference implementation.</p>
 * 
 * @author mpermar
 *
 */
public class JCRRepositoryService implements RepositoryService {

	static Logger logger = LoggerFactory.getLogger(JCRRepositoryService.class);
	
	private JCRResourcesModule resourcesModule;
	private JCRAuthorsModule authorsModule;
	private JCRCategoriesModule categoriesModule;
	private JCRLocksModule locksModule;
	private JCRFavoritesModule favoritesModule;
	private JCRBookmarksModule bookmarksModule;
	private JCRImportExportModule importExportModule;
	private JCRCleanupModule cleanupModule;
		
	private JCRRepositoryBuilder builder;
	
	public JCRRepositoryService() {
		
		resourcesModule = new JCRResourcesModule(this);
		authorsModule = new JCRAuthorsModule();
		categoriesModule = new JCRCategoriesModule();
		locksModule = new JCRLocksModule();
		favoritesModule = new JCRFavoritesModule();
		bookmarksModule = new JCRBookmarksModule();
		importExportModule = new JCRImportExportModule();
		cleanupModule = new JCRCleanupModule();
		builder = new JCRRepositoryBuilder();
	}
	
	public Repository createRepository(Ticket ticket, 
									   String name, 
									   String description, 
									   User creator) 
											throws RepositoryAlreadyExistsException,
												   RepositoryException, 
												   SecurityException {

		return builder.createRepository(ticket, name, description, creator);
	}

	public Directory createDirectory(Ticket ticket,
									 DirectoryProperties properties) throws RepositoryException,
									 										SecurityException {
		
		String name = (String)properties.getProperty(DirectoryProperties.DIRECTORY_NAME).getValue();
		String parentId = (String)properties.getProperty(DirectoryProperties.DIRECTORY_PARENT).getValue();
		String description = (String)properties.getProperty(DirectoryProperties.DIRECTORY_DESCRIPTION).getValue();
		
		return createDirectory(ticket, name,description,parentId);
	}
	
	public Directory createDirectory(Ticket ticket, 
									 String name, 
									 String description, 
									 String parentId) throws RepositoryException, 
									 						 SecurityException {
				
		try {

			SessionManager manager = SessionManager.getInstance();
			Session session = manager.getSession(ticket);
			
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			javax.jcr.Node parent = session.getNodeByUUID(parentId);
			
			Object syncLock = LockUtility.obtainLock(parent);
			
			javax.jcr.Node child;
			synchronized(syncLock) {
				if (!JCRSecurityService.canWrite(parent, ticket.getUser().getId())) {
					throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
				}
				
				String escapedName = 
					JCRUtils.buildValidChildNodeName(parent,null,name); 
				child = parent.addNode(escapedName,JCRConstants.JCR_FOLDER);
				name = Text.unescape(escapedName);
				child.addMixin(JCRConstants.JCR_REFERENCEABLE);
				child.addMixin(JCRConstants.JCR_LOCKABLE);
				child.addMixin(JLibraryConstants.DIRECTORY_MIXIN);
				
				child.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
				child.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,description);
				child.setProperty(JLibraryConstants.JLIBRARY_CREATED, Calendar.getInstance());
				child.setProperty(JLibraryConstants.JLIBRARY_IMPORTANCE, 
								  Node.IMPORTANCE_MEDIUM.longValue());
				child.setProperty(JLibraryConstants.JLIBRARY_CREATOR,
								  ticket.getUser().getId());
				child.setProperty(JLibraryConstants.JLIBRARY_TYPECODE,
						  Types.FOLDER.longValue());
				child.setProperty(JLibraryConstants.JLIBRARY_POSITION,0);
				child.setProperty(JLibraryConstants.JLIBRARY_SIZE,0);
				
				String path = getPath(parent) + 
		  		  obtainAvailableDirectoryName(parent,child);
				
				child.setProperty(JLibraryConstants.JLIBRARY_PATH,path);
				
				// Handle directory restrictions
				child.setProperty(JLibraryConstants.JLIBRARY_RESTRICTIONS,
								  new Value[]{});
				javax.jcr.Node userNode = 
					JCRSecurityService.getUserNode(session,
												   ticket.getUser().getId());
				addRestrictionsToNode(child,parent,userNode);
				if (ticket.isAutocommit()) {
					session.save();
				}
			}
			return JCRAdapter.createDirectory(child,
											  parentId,
											  root.getUUID(),
											  ticket.getUser().getId());
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	public static void addRestrictionsToNode(javax.jcr.Node node, 
			   						   		 javax.jcr.Node parent, 
			   						   		 javax.jcr.Node userNode) throws javax.jcr.RepositoryException {

		List restrictions = JCRAdapter.obtainRestrictions(parent);
		if (!restrictions.contains(userNode.getUUID())) {
			restrictions.add(userNode.getUUID());
		}
		for (Object id: restrictions) {
			JCRUtils.addNodeToProperty((String)id,node,JLibraryConstants.JLIBRARY_RESTRICTIONS);
		}
	}
	
	public String getPath(javax.jcr.Node node) throws RepositoryException {
		
		try {
			if (node.hasProperty(JLibraryConstants.JLIBRARY_CREATED)) {
				javax.jcr.Property pathProperty = 
					node.getProperty(JLibraryConstants.JLIBRARY_PATH);
			
				if (pathProperty == null) {
					return "/";
				} else {
					String path = pathProperty.getValue().getString();
					if (!path.endsWith("/")) {
						return path+"/";
					}
					return path;
				}
			} else {
				return "/";
			}
		} catch (PathNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		
	}
	
	public void removeDirectory(Ticket ticket, 
								String directoryId) throws RepositoryException, 
														   SecurityException {

		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);
		
		try {
			javax.jcr.Node directory = session.getNodeByUUID(directoryId);
			
			if (!JCRSecurityService.canWrite(directory, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			// Remove favorites
			favoritesModule.removeFavoriteReferences(directory);			
			
			// process references
			JCRUtils.removeReferences(directory);
								
			if (JCRUtils.allowsPhysicalDeletes(session)) {
				directory.remove();
			} else {
				JCRUtils.deactivate(directory);
			}			
						
			if (ticket.isAutocommit()) {
				session.save();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		
	}
	
	public List findAllRepositoriesInfo(Ticket ticket) throws RepositoryException {

		try {		
			javax.jcr.Session systemSession = SessionManager.getInstance().getSystemSession(ticket);			
			String[] workspaces = 
				systemSession.getWorkspace().getAccessibleWorkspaceNames();
			
			ArrayList list = new ArrayList();
			for (int i = 0; i < workspaces.length; i++) {
				RepositoryInfo info = new RepositoryInfo();
				info.setId(workspaces[i]);
				info.setName(workspaces[i]);
				list.add(info);
			}
						
			return list;
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	public Repository findRepository(String name, 
									 Ticket ticket) 
										throws RepositoryNotFoundException, 
											   RepositoryException, 
											   SecurityException {

		javax.jcr.Session session = null;
		try {
			SimpleCredentials creds =
				  new SimpleCredentials(
						  ticket.getUser().getName(),
						  ticket.getUser().getPassword().toCharArray());
				  
			javax.jcr.Repository repository = SessionManager.getInstance().getRepository();
			// Change the name to lowercase
			name = name.toLowerCase();
			session = repository.login(creds,name);			 
			
			Repository jLibraryRepository = null;

			// Normal case, we want to load a jlibrary repository
			javax.jcr.Node jLibraryRootNode = JCRUtils.getRootNode(session);

			if (!JCRSecurityService.canRead(jLibraryRootNode, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			jLibraryRepository = JCRAdapter.createRepository(ticket,
															 name,
															 jLibraryRootNode);
			
			return jLibraryRepository;
			
		} catch (javax.jcr.NoSuchWorkspaceException nswe) {
			throw new RepositoryNotFoundException();
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} finally {
			if (session != null) {
				session.logout();
			}
		}
	}

	public void deleteRepository(Ticket ticket) 
										throws RepositoryException, 
											   SecurityException {

		//TODO: Improve this method when Jackrabbit support workspace deletes
		
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			javax.jcr.Node repositoryNode = JCRUtils.getRootNode(session);
			String name = repositoryNode.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString();
			
			String workspacePath = 
				((WorkspaceImpl)session.getWorkspace()).getConfig().getHomeDir();
			session.logout();
			SessionManager.getInstance().dettach(ticket);

			
			File f = new File(workspacePath);
			try {
				org.apache.commons.io.FileUtils.forceDelete(f);
			} catch (IOException e) {
				//logger.error(e.getMessage(),e);
			}	
			javax.jcr.Session systemSession = SessionManager.getInstance().getSystemSession(ticket);			
			cleanupModule.scheduleForDelete(systemSession,name,workspacePath);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	/**
	 * Note that destinationRepository is not used on this implementation
	 * 
	 * @see RepositoryService#copyDirectory(Ticket, String, String, String)
	 */
	public Directory copyDirectory(Ticket ticket, 
								   String sourceId,
								   String destinationId,
								   String destinationRepository) 
											throws RepositoryException, 
												   SecurityException {

		try {			
			javax.jcr.Node node = internalCopyNode(ticket,sourceId,destinationId);
			javax.jcr.Node root = JCRUtils.getRootNode(node.getSession());
			return JCRAdapter.createDirectory(node,
											  node.getParent().getUUID(),
											  root.getUUID(),
											  ticket.getUser().getId());
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}

	/**
	 * Note that destinationRepository is not used on this implementation
	 * 
	 * @see RepositoryService#copyDocument(Ticket, String, String, String)
	 */
	public Document copyDocument(Ticket ticket, 
								 String sourceId, 
								 String destinationId,
								 String destinationRepository) 
										throws RepositoryException, 
											   SecurityException {

		try {
			javax.jcr.Node node = internalCopyNode(ticket,sourceId,destinationId);
			javax.jcr.Node root = JCRUtils.getRootNode(node.getSession());
			return JCRAdapter.createDocument(node,
											 node.getParent().getUUID(),
											 root.getUUID());
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}

	/**
	 * Note that destinationRepository is not used on this implementation
	 * 
	 * @see RepositoryService#moveDirectory(Ticket, String, String, String)
	 */
	public Directory moveDirectory(Ticket ticket, 
								   String sourceId, 
								   String destinationId,
								   String destinationRepository) 
												throws RepositoryException, 
													   SecurityException {

		try {
			javax.jcr.Node node = internalMoveNode(ticket,sourceId,destinationId);
			javax.jcr.Node root = JCRUtils.getRootNode(node.getSession());
			return JCRAdapter.createDirectory(node,
											  node.getParent().getUUID(),
											  root.getUUID(),
											  ticket.getUser().getId());
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	/**
	 * Note that destinationRepository is not used on this implementation
	 * 
	 * @see RepositoryService#moveDocument(Ticket, String, String, String)
	 */
	public Document moveDocument(Ticket ticket, 
								 String documentId, 
								 String directoryId,
								 String destinationRepository) 
										throws RepositoryException, 
											   SecurityException {

		try {
			javax.jcr.Node node = internalMoveNode(ticket,documentId,directoryId);
			javax.jcr.Node root = JCRUtils.getRootNode(node.getSession());
			return JCRAdapter.createDocument(node,
											 node.getParent().getUUID(),
											 root.getUUID());
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	public List createDocuments(Ticket ticket, 
			   					List properties) 
												throws RepositoryException, 
													   SecurityException {
		
		List nodes = new ArrayList();
		List documents = new ArrayList();
		
		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);

		try {
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			
			//The operation will be synchronized on the first element. 
			//TODO: Improve synchronization here
			DocumentProperties first = (DocumentProperties)properties.get(0);
			String parentId = (String)first.getProperty(
					DocumentProperties.DOCUMENT_PARENT).getValue();
			javax.jcr.Node parent = session.getNodeByUUID(parentId);
			Object syncLock = LockUtility.obtainLock(parent);
			Iterator it;
			synchronized(syncLock) {
				it = properties.iterator();
				while (it.hasNext()) {
					DocumentProperties docProperties = (DocumentProperties)it.next();
				
					javax.jcr.Node node = internalCreateDocument(session,
										 						 ticket,
										 						 docProperties);
					nodes.add(node);
				}
				if (ticket.isAutocommit()) {
					session.save();
				}
			}
			it = nodes.iterator();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				documents.add(JCRAdapter.createDocument(
						node,
							node.getParent().getUUID(),
							root.getUUID()));				
			}
			nodes.clear();
		} catch (AccessDeniedException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		
		return documents;
	}	
	
	public Document createDocument(Ticket ticket, 
			   					   DocumentProperties properties) 
													throws RepositoryException, 
														   SecurityException {
		
		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);
		try {
			String parentId = (String)properties.getProperty(
					DocumentProperties.DOCUMENT_PARENT).getValue();
			javax.jcr.Node parent = session.getNodeByUUID(parentId);
			javax.jcr.Node node = null;
			synchronized (LockUtility.obtainLock(parent)) {
				node = internalCreateDocument(session,ticket,properties);
				if (ticket.isAutocommit()) {
					session.save();
				}
			}
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			return JCRAdapter.createDocument(node,
											 node.getParent().getUUID(),
											 root.getUUID());
		} catch (AccessDeniedException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}	
	
	private javax.jcr.Node internalCreateDocument(javax.jcr.Session session,
												  Ticket ticket, 
								   				  DocumentProperties properties) 
													throws RepositoryException, 
														   SecurityException {
	
		ByteArrayInputStream bais = null;
		try {
			String parentId = (String)properties.getProperty(
					DocumentProperties.DOCUMENT_PARENT).getValue();
			String name = ((String)properties.getProperty(
					DocumentProperties.DOCUMENT_NAME).getValue());
			String path = ((String)properties.getProperty(
					DocumentProperties.DOCUMENT_PATH).getValue());
			String extension = FileUtils.getExtension(path);
			String description = ((String)properties.getProperty(
					DocumentProperties.DOCUMENT_DESCRIPTION).getValue());
			Integer typecode = ((Integer)properties.getProperty(
					DocumentProperties.DOCUMENT_TYPECODE).getValue());
			Integer position = ((Integer)properties.getProperty(
					DocumentProperties.DOCUMENT_POSITION).getValue());
			Integer importance = ((Integer)properties.getProperty(
					DocumentProperties.DOCUMENT_IMPORTANCE).getValue());
			String title = (String)properties.getProperty(
					DocumentProperties.DOCUMENT_TITLE).getValue();
			String url = ((String)properties.getProperty(
					DocumentProperties.DOCUMENT_URL).getValue());
			String language = ((String)properties.getProperty(
					DocumentProperties.DOCUMENT_LANGUAGE).getValue());
			Author author = ((Author)properties.getProperty(
					DocumentProperties.DOCUMENT_AUTHOR).getValue());
			Date metadataDate = ((Date)properties.getProperty(
					DocumentProperties.DOCUMENT_CREATION_DATE).getValue());			
			Calendar date = Calendar.getInstance();
			date.setTime(metadataDate);
					
			String keywords = ((String)properties.getProperty(
					DocumentProperties.DOCUMENT_KEYWORDS).getValue());
	
			javax.jcr.Node parent = session.getNodeByUUID(parentId);
			
			if (!JCRSecurityService.canWrite(parent, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			String escapedName = 
				JCRUtils.buildValidChildNodeName(parent,extension,name);
			name = Text.unescape(escapedName);
			if ((extension != null) && !escapedName.endsWith(extension)) {
				escapedName+=extension;
			}
			
			javax.jcr.Node child = 
				parent.addNode(escapedName,JCRConstants.JCR_FILE);
			
			child.addMixin(JCRConstants.JCR_REFERENCEABLE);
			child.addMixin(JCRConstants.JCR_LOCKABLE);
			child.addMixin(JCRConstants.JCR_VERSIONABLE);
			child.addMixin(JLibraryConstants.DOCUMENT_MIXIN);
			
			child.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
			child.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,description);
			child.setProperty(JLibraryConstants.JLIBRARY_CREATED, Calendar.getInstance());
			child.setProperty(JLibraryConstants.JLIBRARY_IMPORTANCE, 
							  importance.longValue());
			child.setProperty(JLibraryConstants.JLIBRARY_CREATOR,
							  ticket.getUser().getId());
			child.setProperty(JLibraryConstants.JLIBRARY_TYPECODE,
							  typecode.longValue());
			child.setProperty(JLibraryConstants.JLIBRARY_POSITION,
							  position.longValue());
			
			child.setProperty(JLibraryConstants.JLIBRARY_TITLE,title);
			child.setProperty(JLibraryConstants.JLIBRARY_DOCUMENT_URL,url);
			child.setProperty(JLibraryConstants.JLIBRARY_KEYWORDS,keywords);
			child.setProperty(JLibraryConstants.JLIBRARY_LANGUAGE,language);
			child.setProperty(JLibraryConstants.JLIBRARY_CREATION_DATE,date);
	
			child.setProperty(JLibraryConstants.JLIBRARY_PATH,
					  		  getPath(parent) + 
					  		  FileUtils.getFileName(path));			
									
			// Handle authors
			if (author.equals(Author.UNKNOWN)) {
				child.setProperty(
						JLibraryConstants.JLIBRARY_AUTHOR,
						authorsModule.findUnknownAuthor(ticket));
			} else {
				javax.jcr.Node authorNode = 
					session.getNodeByUUID(author.getId());
				child.setProperty(JLibraryConstants.JLIBRARY_AUTHOR,
								  authorNode.getUUID());
			}
			
			// Handle document categories
			child.setProperty(JLibraryConstants.JLIBRARY_CATEGORIES, 
							  new Value[]{});
			PropertyDef[] categories = properties.getPropertyList(
					DocumentProperties.DOCUMENT_ADD_CATEGORY);
			if (categories != null) {
				for (int i = 0; i < categories.length; i++) {
					String category = (String)categories[i].getValue();
					javax.jcr.Node categoryNode = 
						categoriesModule.getCategoryNode(session,category);
					categoriesModule.addCategory(ticket,categoryNode,child);		
				}
			} else {
				categoriesModule.addUnknownCategory(ticket,child);
			}
	
			// Handle document relations
			child.setProperty(JLibraryConstants.JLIBRARY_RELATIONS,
							  new Value[]{});
			
			// Handle document restrictions
			child.setProperty(JLibraryConstants.JLIBRARY_RESTRICTIONS,
							  new Value[]{});
			javax.jcr.Node userNode = 
				JCRSecurityService.getUserNode(session,
											   ticket.getUser().getId());
			addRestrictionsToNode(child,parent,userNode);

			// Handle document resources. 
			child.setProperty(JLibraryConstants.JLIBRARY_RESOURCES, 
							  new Value[]{});
	
			
			javax.jcr.Node resNode = child.addNode(JcrConstants.JCR_CONTENT, 
												   JCRConstants.JCR_RESOURCE);
			resNode.addMixin(JLibraryConstants.CONTENT_MIXIN);
	
			byte[] content = (byte[])properties.getProperty(
								DocumentProperties.DOCUMENT_CONTENT).getValue();
			if (content == null) {
				// Empty file
				content = new byte[]{};
			}
	
			bais = new ByteArrayInputStream(content);
			//TODO: Handle encoding
			String mimeType = 
				Types.getMimeTypeForExtension(FileUtils.getExtension(path));
	        resNode.setProperty (JCRConstants.JCR_MIME_TYPE, mimeType);
	        resNode.setProperty (JCRConstants.JCR_ENCODING, 
	        					 JCRConstants.DEFAULT_ENCODING);
	        resNode.setProperty (JCRConstants.JCR_DATA, 
	        					 new ByteArrayInputStream(content));
	        Calendar lastModified = Calendar.getInstance ();
	        lastModified.setTimeInMillis (new Date().getTime());
	        resNode.setProperty (JCRConstants.JCR_LAST_MODIFIED, lastModified);
	
			child.setProperty(JLibraryConstants.JLIBRARY_SIZE,content.length);
	        
			return child;
		} catch (Throwable e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} finally {
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException ioe) {
					logger.error(ioe.getMessage(),ioe);
					throw new RepositoryException(ioe);
				}
			}
		}
	}

	public void removeDocument(Ticket ticket, 
							   String docId) 
									throws RepositoryException, 
										   SecurityException, 
										   ResourceLockedException {

		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);
		
		try {
			javax.jcr.Node document = session.getNodeByUUID(docId);
			
			locksModule.checkLockAccess(ticket,document);
			
			if (!JCRSecurityService.canWrite(document, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			// Remove favorites
			favoritesModule.removeFavoriteReferences(document);
			// Remove the other references
			JCRUtils.removeReferences(document);

			if (JCRUtils.allowsPhysicalDeletes(session)) {
				//TODO: Replace this when JCR-134 is fixed
				//See more details at http://issues.apache.org/jira/browse/JCR-134
				VersionHistory vh = document.getVersionHistory();
				VersionIterator vi = vh.getAllVersions();
				while (vi.hasNext()) {
					Version currenVersion = vi.nextVersion();
					String versionName = currenVersion.getName();
                    if (!versionName.equals("jcr:rootVersion") &&
                        !versionName.equals("1.0") &&
                    	!versionName.equals("1.1") &&
                    	!versionName.equals("1.2") &&
                    	!versionName.equals("1.3")) {
                    	try {
                    		vh.removeVersion(versionName);
                    	} catch (ReferentialIntegrityException rif) {
                    		logger.error("Unable to remove version: " + rif.getMessage());
                    	}
                    } 					
                } 				
				
				document.remove();
			} else {
				JCRUtils.deactivate(document);
			}
			if (ticket.isAutocommit()) {
				session.save();
			}
		} catch (ResourceLockedException rle) {
			throw rle;
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}
	
	public void loadDocumentContent(String docId, 
			  						Ticket ticket,
			  						OutputStream stream) 
										throws RepositoryException, 
											   SecurityException {
		
		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);
		
		InputStream is = null;
		try {
			javax.jcr.Node resource = session.getNodeByUUID(docId);
			
			if (!JCRSecurityService.canRead(resource, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
						
			javax.jcr.Node nodeContent = resource.getNode(JCRConstants.JCR_CONTENT);
			is = nodeContent.getProperty("jcr:data").getValue().getStream();
			IOUtils.copy(is, stream);
			
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {
					logger.error(ioe.getMessage(),ioe);
					throw new RepositoryException(ioe);
				}
			}
		}
	}
	
	public byte[] loadDocumentContent(String docId, 
									  Ticket ticket) 
											throws RepositoryException, 
												   SecurityException {

		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			loadDocumentContent(docId, ticket, baos);
			return baos.toByteArray();

		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException ioe) {
					logger.error(ioe.getMessage(), ioe);
					throw new RepositoryException(ioe);
				}
			}
		}
	}

	public Category findCategoryByPath(Ticket ticket, 
			 			 	   		   String path) throws RepositoryException, 
			 				   			   		   		   CategoryNotFoundException, 
			 				   			   		   		   SecurityException {

		return categoriesModule.findCategoryByPath(ticket, path);
	}
	
	public Node findNodeByPath(Ticket ticket, 
			 			 	   String path) throws RepositoryException, 
			 				   			   		   NodeNotFoundException, 
			 				   			   		   SecurityException {

		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);
		if (!path.startsWith(JLibraryConstants.JLIBRARY_ROOT)) {
			if (path.charAt(0) == '/') {
				path = path.substring(1,path.length());
			}
			path = JLibraryConstants.JLIBRARY_ROOT + "/" + path;
		}
		try {

			javax.jcr.Node root = JCRUtils.getRootNode(session); 			
			javax.jcr.Node node = session.getRootNode().getNode(path);
			if (!JCRSecurityService.canRead(node, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}

			javax.jcr.Node parent = node.getParent();
			String uuid = null;
			if (parent.isNodeType(JCRConstants.JCR_REFERENCEABLE)) {
				uuid = parent.getUUID();
			} else {
				uuid = root.getUUID(); 
			}
			
			if (node.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
				return JCRAdapter.createDocument(node,
						 						 uuid,
						 						 root.getUUID());
			} else if (node.isNodeType(JLibraryConstants.DIRECTORY_MIXIN)) {
				return JCRAdapter.createDirectory(node,
												  uuid,
						 						  root.getUUID(),
						 						  ticket.getUser().getId());
			} else if (node.isNodeType(JLibraryConstants.RESOURCE_MIXIN)) {
				return JCRAdapter.createResource(node,
						 						 uuid,
						 						 root.getUUID());
			}
			throw new NodeNotFoundException();
		} catch (PathNotFoundException pnfe) {
			logger.error("Node with path [" + path + "] not found");
			throw new NodeNotFoundException();
		} catch (ItemNotFoundException infe) {
			logger.error(infe.getMessage(),infe);
			throw new NodeNotFoundException(infe);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}
	
	public Node findNode(Ticket ticket, 
			 			 String id) throws RepositoryException, 
			 				   			   NodeNotFoundException, 
			 				   			   SecurityException {

		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);

		try {
			javax.jcr.Node node = session.getNodeByUUID(id);
			javax.jcr.Node parent = node.getParent();
			javax.jcr.Node root = JCRUtils.getRootNode(session); 
			
			String uuid = null;
			if (parent.isNodeType(JCRConstants.JCR_REFERENCEABLE)) {
				uuid = parent.getUUID();
			} else {
				uuid = root.getUUID(); 
			}

			if (!JCRSecurityService.canRead(node, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			
			if (node.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
				return JCRAdapter.createDocument(node,
						 						 uuid,
						 						 root.getUUID());
			} else if (node.isNodeType(JLibraryConstants.DIRECTORY_MIXIN)) {
				return JCRAdapter.createDirectory(node,
												  uuid,
						 						  root.getUUID(),
						 						  ticket.getUser().getId());
			} else if (node.isNodeType(JLibraryConstants.RESOURCE_MIXIN)) {
				return JCRAdapter.createResource(node,
						 						 uuid,
						 						 root.getUUID());
			}
			throw new NodeNotFoundException();
		} catch (ItemNotFoundException infe) {
			logger.error(infe.getMessage(),infe);
			throw new NodeNotFoundException(infe);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}
	
	
	public Document findDocument(Ticket ticket, 
								 String id) throws RepositoryException, 
								 				   NodeNotFoundException, 
								 				   SecurityException {

		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);
		
		try {
			javax.jcr.Node document = session.getNodeByUUID(id);
			
			if (!JCRSecurityService.canRead(document, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			return JCRAdapter.createDocument(document,
											 document.getParent().getUUID(),
											 JCRUtils.getRootNode(session).getUUID());
		} catch (ItemNotFoundException infe) {
			logger.error(infe.getMessage(),infe);
			throw new NodeNotFoundException(infe);			
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}

	public Collection findDocumentsByName(Ticket ticket, 
			 							  String name) throws RepositoryException {

		ArrayList documents = new ArrayList();
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			javax.jcr.Node root = JCRUtils.getRootNode(session);			
			String rootPath = root.getPath();

			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			String statement = "/jcr:root" +
				   			   rootPath + 
				   			   "//element(*,jlib:document)[@jlib:active='true'"+
				   			   " and @jlib:name='" + name + "']";
		
			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();

			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				if (!JCRSecurityService.canRead(node,ticket.getUser().getId())) {
					continue;
				}
				documents.add(JCRAdapter.createDocument(node,
														node.getParent().getUUID(),
														ticket.getRepositoryId()));
			}
		} catch (SecurityException se) {
			logger.error(se.getMessage(),se);
			throw new RepositoryException(se);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}	
		return documents;
	}
	
	
	public Directory findDirectory(Ticket ticket, 
								   String id) 
										throws RepositoryException, 
											   NodeNotFoundException, 
											   SecurityException {

		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);
		
		try {
			javax.jcr.Node directory = session.getNodeByUUID(id);
			
			if (!JCRSecurityService.canRead(directory, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			String parentUUID = null;
			if (directory.getParent() != session.getRootNode()) {
				parentUUID = directory.getParent().getUUID();
			}
			return JCRAdapter.createDirectory(directory,
											 parentUUID,
											 JCRUtils.getRootNode(session).getUUID(),
											 ticket.getUser().getId());
		} catch (ItemNotFoundException infe) {
			logger.error(infe.getMessage(),infe);
			throw new NodeNotFoundException(infe);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	public Document updateDocument(Ticket ticket, 
								   DocumentProperties properties) 
										throws RepositoryException, 
											   SecurityException, 
											   ResourceLockedException {
		
		// TODO: Check name updates with new code
		
		ByteArrayInputStream bais = null;
		try {
			String docId = (String)properties.getProperty(
					DocumentProperties.DOCUMENT_ID).getValue();
			String parentId = (String)properties.getProperty(
					DocumentProperties.DOCUMENT_PARENT).getValue();
			String name = ((String)properties.getProperty(
					DocumentProperties.DOCUMENT_NAME).getValue());
			String description = ((String)properties.getProperty(
					DocumentProperties.DOCUMENT_DESCRIPTION).getValue());
			Integer typecode = ((Integer)properties.getProperty(
					DocumentProperties.DOCUMENT_TYPECODE).getValue());
			Integer position = ((Integer)properties.getProperty(
					DocumentProperties.DOCUMENT_POSITION).getValue());
			Integer importance = ((Integer)properties.getProperty(
					DocumentProperties.DOCUMENT_IMPORTANCE).getValue());
			String title = (String)properties.getProperty(
					DocumentProperties.DOCUMENT_TITLE).getValue();
			String url = ((String)properties.getProperty(
					DocumentProperties.DOCUMENT_URL).getValue());
			String language = ((String)properties.getProperty(
					DocumentProperties.DOCUMENT_LANGUAGE).getValue());
			Author author = ((Author)properties.getProperty(
					DocumentProperties.DOCUMENT_AUTHOR).getValue());
			Date metadataDate = ((Date)properties.getProperty(
					DocumentProperties.DOCUMENT_CREATION_DATE).getValue());
			Calendar date = Calendar.getInstance();
			date.setTime(metadataDate);

			String keywords = ((String)properties.getProperty(
					DocumentProperties.DOCUMENT_KEYWORDS).getValue());

			
			SessionManager manager = SessionManager.getInstance();
			Session session = manager.getSession(ticket);
			
			javax.jcr.Node node = session.getNodeByIdentifier(docId);
			if (!JCRSecurityService.canWrite(node, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			Object syncLock = LockUtility.obtainLock(node);
			synchronized(syncLock) {
				locksModule.checkLockAccess(ticket,node);			
							
				//Si versiones = 0 , checkin.
				JCRUtils.checkinIfNecessary(node);
				VersionManager vm = node.getSession().getWorkspace().getVersionManager();				
				vm.checkout(node.getCorrespondingNodePath(node.getSession().getWorkspace().getName()));		
				
				node.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,description);
				node.setProperty(JLibraryConstants.JLIBRARY_CREATED, Calendar.getInstance());
				node.setProperty(JLibraryConstants.JLIBRARY_IMPORTANCE, 
								  importance.longValue());
				node.setProperty(JLibraryConstants.JLIBRARY_CREATOR,
								  ticket.getUser().getId());
				node.setProperty(JLibraryConstants.JLIBRARY_TYPECODE,
								  typecode.longValue());
				node.setProperty(JLibraryConstants.JLIBRARY_POSITION,
								  position.longValue());
				
				node.setProperty(JLibraryConstants.JLIBRARY_TITLE,title);
				node.setProperty(JLibraryConstants.JLIBRARY_DOCUMENT_URL,url);
				node.setProperty(JLibraryConstants.JLIBRARY_KEYWORDS,keywords);
				node.setProperty(JLibraryConstants.JLIBRARY_LANGUAGE,language);
				node.setProperty(JLibraryConstants.JLIBRARY_CREATION_DATE,date);
				
				//Handle relations			
				PropertyDef[] relations = properties.getPropertyList(
						DocumentProperties.DOCUMENT_DELETE_RELATION);
				if (relations != null) {
					for (int i = 0; i < relations.length; i++) {
						Relation relation = (Relation)relations[i].getValue();
						String destinationId = 
							relation.getDestinationNode().getId();
						
						JCRUtils.removeNodeFromProperty(
								destinationId,
								node,
								JLibraryConstants.JLIBRARY_RELATIONS);
						if (relation.isBidirectional()) {
							javax.jcr.Node referencedNode = 
								session.getNodeByIdentifier(destinationId);
							JCRUtils.removeNodeFromProperty(
									node.getIdentifier(),
									referencedNode,
									JLibraryConstants.JLIBRARY_RELATIONS);
							
						}
					}
				}
				
				relations = properties.getPropertyList(
						DocumentProperties.DOCUMENT_ADD_RELATION);
				if (relations != null) {
					for (int i = 0; i < relations.length; i++) {
						Relation relation = (Relation)relations[i].getValue();
						String destinationId = 
							relation.getDestinationNode().getId();
						
						JCRUtils.addNodeToProperty(
								destinationId,
								node,
								JLibraryConstants.JLIBRARY_RELATIONS);
						if (relation.isBidirectional()) {
							javax.jcr.Node referencedNode = 
								session.getNodeByUUID(destinationId);
							JCRUtils.addNodeToProperty(
									node.getUUID(),
									referencedNode,
									JLibraryConstants.JLIBRARY_RELATIONS);
							
						}
					}
				}
				
				
				//Handle authors
				if (author.equals(Author.UNKNOWN)) {
					node.setProperty(
							JLibraryConstants.JLIBRARY_AUTHOR,
							authorsModule.findUnknownAuthor(ticket));
				} else {
					javax.jcr.Node authorNode = 
						session.getNodeByUUID(author.getId());
					node.setProperty(JLibraryConstants.JLIBRARY_AUTHOR,
									  authorNode.getUUID());
				}
				
				// Handle document categories
				PropertyDef[] categories = properties.getPropertyList(
						DocumentProperties.DOCUMENT_DELETE_CATEGORY);
				if (categories != null) {
					for (int i = 0; i < categories.length; i++) {
						String category = (String)categories[i].getValue();
						javax.jcr.Node categoryNode = 
							categoriesModule.getCategoryNode(session,category);
						categoriesModule.removeCategory(ticket,categoryNode,node);
					}
					if (categoriesModule.numberOfCategories(node) == 0) {
						categoriesModule.addUnknownCategory(ticket,node);
					}
				}
				
				categories = properties.getPropertyList(
						DocumentProperties.DOCUMENT_ADD_CATEGORY);
				if (categories != null) {
					if (categoriesModule.containsUnknownCategory(ticket,node)) {
						categoriesModule.removeUnknownCategory(ticket,node);
					}
					for (int i = 0; i < categories.length; i++) {
						String category = (String)categories[i].getValue();
						javax.jcr.Node categoryNode = 
							categoriesModule.getCategoryNode(session,category);
						categoriesModule.addCategory(ticket,categoryNode,node);
					}
				}			
								
				// Handle document notes
				PropertyDef[] notes = properties.getPropertyList(
						DocumentProperties.DOCUMENT_ADD_NOTE);
				if (notes != null) {
					javax.jcr.Node userNode = JCRSecurityService.getUserNode(
							session,ticket.getUser().getId());
					for (int i = 0; i < notes.length; i++) {
						Note note = (Note)notes[i].getValue();
						javax.jcr.Node noteNode = node.addNode(
								JLibraryConstants.JLIBRARY_NOTE,
								JLibraryConstants.NOTE_MIXIN);
						noteNode.addMixin(JCRConstants.JCR_REFERENCEABLE);
						Calendar noteDate = Calendar.getInstance();
						noteDate.setTime(new Date());
	
						noteNode.setProperty(JLibraryConstants.JLIBRARY_DATE,
											 noteDate);
						noteNode.setProperty(JLibraryConstants.JLIBRARY_TEXT,
								 			 note.getNote());
						noteNode.setProperty(JLibraryConstants.JLIBRARY_USER,
								userNode.getUUID());
					}
				}
	
				notes = properties.getPropertyList(
						DocumentProperties.DOCUMENT_UPDATE_NOTE);
				if (notes != null) {
					for (int i = 0; i < notes.length; i++) {
						Note note = (Note)notes[i].getValue();
						javax.jcr.Node noteNode = session.getNodeByUUID(note.getId());
						Calendar noteDate = Calendar.getInstance();
						noteDate.setTime(new Date());
						
						noteNode.setProperty(JLibraryConstants.JLIBRARY_DATE,
								 			 noteDate);
						noteNode.setProperty(JLibraryConstants.JLIBRARY_TEXT,
					 			 			 note.getNote());
						noteNode.setProperty(JLibraryConstants.JLIBRARY_USER,
		 			 			 			 ticket.getUser().getId());
					}
				}			
				
				notes = properties.getPropertyList(
						DocumentProperties.DOCUMENT_DELETE_NOTE);
				if (notes != null) {
					for (int i = 0; i < notes.length; i++) {
						Note note = (Note)notes[i].getValue();
						NodeIterator it = node.getNodes();
						while (it.hasNext()) {
							javax.jcr.Node childNode = (javax.jcr.Node) it.next();
							if (childNode.isNodeType(JLibraryConstants.NOTE_MIXIN)) {
								String noteId = childNode.getUUID();
								if (noteId.equals(note.getId())) {
									childNode.remove();
								}
							}
						}
					}
				}
	
				// Handle custom properties
				List customProperties = properties.getCustomProperties();
				Iterator it = customProperties.iterator();
				while (it.hasNext()) {
					PropertyDef property = (PropertyDef)it.next();
					node.setProperty(property.getKey().toString(),
									 JCRUtils.getValue(property.getValue()));
				}
				
				// Handle content
				if (properties.getProperty(
						DocumentProperties.DOCUMENT_CONTENT) != null) {
					byte[] content = (byte[])properties.getProperty(
							DocumentProperties.DOCUMENT_CONTENT).getValue();
					if (content != null) {
						javax.jcr.Node child = node.getNode(JCRConstants.JCR_CONTENT); 				
						
						bais = new ByteArrayInputStream(content);
						String path = node.getProperty(
								JLibraryConstants.JLIBRARY_PATH).getString();
						String mimeType = 
							Types.getMimeTypeForExtension(FileUtils.getExtension(path));
				        child.setProperty (JCRConstants.JCR_MIME_TYPE, mimeType);
				        child.setProperty (JCRConstants.JCR_ENCODING, 
				        					 JCRConstants.DEFAULT_ENCODING);
				        child.setProperty (JCRConstants.JCR_DATA, 
				        					 new ByteArrayInputStream(content));
				        Calendar lastModified = Calendar.getInstance ();
				        lastModified.setTimeInMillis (new Date().getTime());
				        child.setProperty (JCRConstants.JCR_LAST_MODIFIED, lastModified);
											
						node.setProperty(JLibraryConstants.JLIBRARY_SIZE,content.length);
					}
				}
								
				String previousName = 
					node.getProperty(JLibraryConstants.JLIBRARY_NAME).getString();
				if (!previousName.equals(name)) {
					
					String path = 
						node.getProperty(JLibraryConstants.JLIBRARY_NAME).getString();
					String extension = FileUtils.getExtension(path);
					String escapedName = 
						JCRUtils.buildValidChildNodeName(node.getParent(),
														 extension,
														 name);
					if ((extension != null) && !escapedName.endsWith(extension)) {
						escapedName+=extension;
					}				
					name = Text.unescape(escapedName);
					node.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
					session.move(node.getPath(), 
							 	 node.getParent().getPath() + "/" + escapedName);
				}
				
				
				if (ticket.isAutocommit()) {
					session.save();
				}
				
				// create version
				vm.checkin(node.getCorrespondingNodePath(node.getSession().getWorkspace().getName()));		
				// restore to read-write state
				vm.checkout(node.getCorrespondingNodePath(node.getSession().getWorkspace().getName()));		
			}
			
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			return JCRAdapter.createDocument(node,parentId,root.getIdentifier());
		} catch (Throwable e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} finally {
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException ioe) {
					logger.error(ioe.getMessage(),ioe);
					throw new RepositoryException(ioe);
				}
			}
		}
		
	}

	public Directory updateDirectory(Ticket ticket, 
									 DirectoryProperties directoryProperties) 
											throws RepositoryException, 
												   SecurityException {

		try {
			SessionManager manager = SessionManager.getInstance();
			Session session = manager.getSession(ticket);
			
			String directoryId = (String)directoryProperties.getProperty(
					DirectoryProperties.DIRECTORY_ID).getValue();
			String parentId = (String)directoryProperties.getProperty(
					DirectoryProperties.DIRECTORY_PARENT).getValue();
			String name = (String)directoryProperties.getProperty(
					DirectoryProperties.DIRECTORY_NAME).getValue();
			String description = (String)directoryProperties.getProperty(
					DirectoryProperties.DIRECTORY_DESCRIPTION).getValue();
			Integer position = (Integer)directoryProperties.getProperty(
					DirectoryProperties.DIRECTORY_POSITION).getValue();
			
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			javax.jcr.Node directory = session.getNodeByUUID(directoryId);
			
			if (!JCRSecurityService.canWrite(directory, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			Object syncLock = LockUtility.obtainLock(directory);
			synchronized(syncLock) {

				directory.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,description);
				directory.setProperty(JLibraryConstants.JLIBRARY_POSITION,position.longValue());
				
				String previousName = 
					directory.getProperty(JLibraryConstants.JLIBRARY_NAME).getString();
				if (!previousName.equals(name)) {
					String escapedName = 
						JCRUtils.buildValidChildNodeName(directory.getParent(),
														 null,
														 name);
					name = Text.unescape(escapedName);
					directory.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
					session.move(directory.getPath(), 
							directory.getParent().getPath() + "/" + escapedName);
				}
				
				if (ticket.isAutocommit()) {
					session.save();
				}
			}
			return JCRAdapter.createDirectory(directory,
											  parentId,
											  root.getUUID(),
											  ticket.getUser().getId());
		} catch (Exception e) {
			throw new RepositoryException(e);
		}		
	}

	public Repository updateRepository(Ticket ticket, 
									   RepositoryProperties repositoryProperties) 
													throws RepositoryException, 
														   SecurityException {

		try {
			
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			
			javax.jcr.Node repositoryNode = JCRUtils.getRootNode(session);
			
			if (!JCRSecurityService.canWrite(repositoryNode, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			String name;
			Object syncLock = LockUtility.obtainLock(repositoryNode);
			synchronized(syncLock) {
				name = (String)repositoryProperties.getProperty(
						RepositoryProperties.REPOSITORY_NAME).getValue();
				String description = (String)repositoryProperties.getProperty(
						RepositoryProperties.REPOSITORY_DESCRIPTION).getValue();
				Boolean physicalDelete = (Boolean)repositoryProperties.getProperty(
						RepositoryProperties.PHYSICAL_DELETE_DOCUMENTS).getValue();
				Boolean extractKeywords = (Boolean)repositoryProperties.getProperty(
						RepositoryProperties.EXTRACT_DOCUMENT_METADATA).getValue();
				Boolean lazyLoading = (Boolean)repositoryProperties.getProperty(
						RepositoryProperties.DO_LAZY_LOADING).getValue();
				
				repositoryNode.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
				repositoryNode.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,description);
				
				// Create config entries
				if (extractKeywords != null) {
					JCRUtils.setConfigEntry(session,
											RepositoryProperties.EXTRACT_DOCUMENT_METADATA,
											extractKeywords);
				}
				if (physicalDelete != null) {
					JCRUtils.setConfigEntry(session,
											RepositoryProperties.PHYSICAL_DELETE_DOCUMENTS,
											physicalDelete);
				}
				if (lazyLoading != null) {
					JCRUtils.setConfigEntry(session,
											RepositoryProperties.DO_LAZY_LOADING,
											lazyLoading);
				}
				
				if (ticket.isAutocommit()) {
					session.save();
				}
			}			
			return JCRAdapter.createRepository(ticket,name,repositoryNode);
		} catch (Exception e) {
			throw new RepositoryException(e);
		}		
	}
		

	public void renameNode(Ticket ticket, 
						   String nodeId, 
						   String name) throws RepositoryException, 
						   					   SecurityException {

		try {
			SessionManager manager = SessionManager.getInstance();
			Session session = manager.getSession(ticket);
			javax.jcr.Node node = session.getNodeByUUID(nodeId);
			
			if (!JCRSecurityService.canWrite(node, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}

			Object syncLock = LockUtility.obtainLock(node);
			synchronized(syncLock) {
				String extension = null;
				if (node.isNodeType(JLibraryConstants.RESOURCE_MIXIN) ||
					node.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
					node.checkout();
					String path = node.getProperty(
							JLibraryConstants.JLIBRARY_PATH).getString();
					extension = FileUtils.getExtension(path);
				}
							
				String escapedName = 
					JCRUtils.buildValidChildNodeName(node.getParent(),
													 extension,
													 name);	
				name = Text.unescape(escapedName);
				if ((extension != null) && !escapedName.endsWith(extension)) {
					escapedName+=extension;
				}
				node.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
							
				// Now we have to update relative paths
				session.move(node.getPath(), 
							 node.getParent().getPath() + "/" + escapedName);
				
				if (ticket.isAutocommit()) {
					session.save();
				}
				if (node.isNodeType(JLibraryConstants.RESOURCE_MIXIN) ||
					node.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
						// Create a new version
						node.checkin();
						// Restore it to read-write state
						node.checkout();
				}
			}
		} catch (Exception e) {
			throw new RepositoryException(e);
		}		
		
	}

	public List findAllAuthors(Ticket ticket) throws RepositoryException {

		return authorsModule.findAllAuthors(ticket);
	}

	public Author findAuthorByName(Ticket ticket, 
								   String name) throws AuthorNotFoundException,
								   					   RepositoryException {

		return authorsModule.findAuthorByName(ticket,name);
	}

	public Author findAuthorById(Ticket ticket, 
								 String id) throws AuthorNotFoundException,
								 				   RepositoryException {

		return authorsModule.findAuthorById(ticket,id);
	}

	public List findAllCategories(Ticket ticket) throws RepositoryException {
		
		return categoriesModule.findAllCategories(ticket);
	}

	public Category createCategory(Ticket ticket, 
								   CategoryProperties categoryProperties) 
										throws CategoryAlreadyExistsException,
											   RepositoryException, 
											   SecurityException {

		return categoriesModule.createCategory(ticket,categoryProperties);
	}

	public void deleteCategory(Ticket ticket, 
							   String categoryId) 
										throws RepositoryException, 
											   SecurityException {

		categoriesModule.deleteCategory(ticket,categoryId);
	}

	public Category updateCategory(Ticket ticket, 
							   	   String categoryId, 
							   	   CategoryProperties categoryProperties) 
									throws CategoryNotFoundException,
										   RepositoryException, 
										   SecurityException {

		return categoriesModule.updateCategory(ticket,categoryId,categoryProperties);
	}

	public Category findCategoryById(Ticket ticket, 
									 String id) 
											throws CategoryNotFoundException,
												   RepositoryException {

		return categoriesModule.findCategoryById(ticket,id);
	}

	public Category findCategoryByName(Ticket ticket, 
									   String name) 
											throws CategoryNotFoundException,
												   RepositoryException {

		return categoriesModule.findCategoryByName(ticket,name);
	}

	public Favorite createFavorite(Ticket ticket, 
								   Favorite favorite) 
											throws RepositoryException, 
												   SecurityException {

		return favoritesModule.createFavorite(ticket,favorite);
	}

	public void deleteFavorite(Ticket ticket, 
							   String favoriteId) 
											throws RepositoryException, 
												   SecurityException {


		favoritesModule.deleteFavorite(ticket,favoriteId);
	}

	public Bookmark createBookmark(Ticket ticket, 
								   Bookmark bookmark) throws RepositoryException {

		
		return bookmarksModule.createBookmark(ticket,bookmark);
	}

	public Author createAuthor(Ticket ticket, 
							   AuthorProperties properties) 
										   throws RepositoryException, 
							   					  SecurityException,
							   					  AuthorAlreadyExistsException {

		return authorsModule.createAuthor(ticket,properties);
	}

	public void updateAuthor(Ticket ticket, 
							 String authorId, 
							 AuthorProperties properties) 
												throws RepositoryException, 
							 					   	   SecurityException,
							 					   	   AuthorNotFoundException {

		authorsModule.updateAuthor(ticket,authorId,properties);
	}

	public void deleteAuthor(Ticket ticket, 
							 String authorId) throws RepositoryException, 
							 						 SecurityException,
							 						 AuthorNotFoundException {

		authorsModule.deleteAuthor(ticket,authorId);
	}

	public void removeBookmark(Ticket ticket, 
							   String bookmarkId) throws RepositoryException {

		bookmarksModule.removeBookmark(ticket, bookmarkId);
	}

	public Bookmark updateBookmark(Ticket ticket, 
							   Bookmark bookmark) throws RepositoryException {
		
		return bookmarksModule.updateBookmark(ticket,bookmark);
	}

	public List findCategoriesForNode(Ticket ticket, 
									  String nodeId) 
											throws RepositoryException, 
												   SecurityException {

		return categoriesModule.findCategoriesForNode(ticket,nodeId);
	}

	public List findNodesForCategory(Ticket ticket, 
									 String categoryId) 
										throws CategoryNotFoundException,
											   RepositoryException {

		return categoriesModule.findNodesForCategory(ticket,categoryId);
	}

	public byte[] exportRepository(Ticket ticket) 
											throws RepositoryNotFoundException, 
												   RepositoryException, 
												   SecurityException {
		
		return importExportModule.exportRepository(ticket);
	}

	public void exportRepository(Ticket ticket, OutputStream stream) 
											throws RepositoryNotFoundException, 
												   RepositoryException, 
												   SecurityException {
		
		importExportModule.exportRepository(ticket,stream);
	}
	
	public void importRepository(Ticket ticket, 
								 byte[] content,
								 String name) 
											throws RepositoryAlreadyExistsException,
												   RepositoryException, 
									   			   SecurityException {

		try {
			importExportModule.importRepository(ticket,content,name);
		} catch (RepositoryAlreadyExistsException raee) {
			throw raee;
		} catch (RepositoryException re) {
			try {
				// In this case we must delete the recently created workspace				
				deleteWorkspace(ticket, name);
				throw re;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				// throw previous exception, otherwise it will be confusing
				throw re;
			}
		} catch (SecurityException se) {
			throw se;
		}
	}

	private void deleteWorkspace(Ticket ticket, 
								 String name) 
										throws javax.jcr.RepositoryException, 
											   RepositoryException {
		
		javax.jcr.Repository repository = SessionManager.getInstance().getRepository();
		SimpleCredentials creds =
		    new SimpleCredentials(ticket.getUser().getName(), 
		    					  ticket.getUser().getPassword().toCharArray());
		
		javax.jcr.Session session = repository.login(creds,name);
		String workspacePath = 
			((WorkspaceImpl)session.getWorkspace()).getConfig().getHomeDir();
		session.logout();

		File f = new File(workspacePath);
		try {
			org.apache.commons.io.FileUtils.forceDelete(f);
		} catch (IOException e) {
			//logger.error(e.getMessage(),e);
		}				
		
		javax.jcr.Session systemSession = SessionManager.getInstance().getSystemSession(ticket);
		cleanupModule.scheduleForDelete(systemSession,name,workspacePath);
	}

	public byte[] loadVersionContent(Ticket ticket, 
									 String versionId) 
											throws RepositoryException, 
												   SecurityException {

		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			loadVersionContent(ticket, versionId, baos);
			return baos.toByteArray();
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					throw new RepositoryException(e);
				}
			}
		}
	}


	public void loadVersionContent(Ticket ticket, 
								   String versionId,
								   OutputStream stream) 
											throws RepositoryException, 
												   SecurityException {

		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);
		
		InputStream is = null;
		try {
			javax.jcr.version.Version version = 
				(javax.jcr.version.Version)session.getNodeByUUID(versionId);
			javax.jcr.Node frozenNode = version.getNode(
					JCRConstants.JCR_FROZEN_NODE);
			
			if (frozenNode.hasNode(JCRConstants.JCR_CONTENT)) {
				javax.jcr.Node nodeContent = frozenNode.getNode(
						JCRConstants.JCR_CONTENT);
				is = nodeContent.getProperty("jcr:data").getValue().getStream();
				IOUtils.copy(is,stream);

			} else {
				// lookup in older versions
				javax.jcr.version.Version[] predecessors = 
					version.getPredecessors();
				for (int i = 0; i < predecessors.length; i++) {
					frozenNode = predecessors[i].getNode(
							JCRConstants.JCR_FROZEN_NODE);
					if (frozenNode.hasNode(JCRConstants.JCR_CONTENT)) {
						javax.jcr.Node nodeContent = frozenNode.getNode(
								JCRConstants.JCR_CONTENT);
						is = nodeContent.getProperty("jcr:data").getValue().getStream();
						IOUtils.copy(is,stream);

					}
				}
				// content not found
				frozenNode = version.getNode(
						JCRConstants.JCR_FROZEN_NODE);
				String nodeUUID = frozenNode.getProperty(
						JCRConstants.JCR_FROZEN_UUID).getString();
				loadDocumentContent(nodeUUID,ticket,stream);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {
					logger.error(ioe.getMessage(),ioe);
					throw new RepositoryException(ioe);
				}
			}
		}

	}	
	
	public Lock lockDocument(Ticket ticket, 
							 String docId) throws RepositoryException, 	
							 					  SecurityException, 
							 					  ResourceLockedException {

		return locksModule.lockDocument(ticket,docId);
	}

	public void unlockDocument(Ticket ticket, 
							   String docId) throws RepositoryException, 
							   						SecurityException, 
							   						ResourceLockedException {

		locksModule.unlockDocument(ticket,docId);
	}

	public List findAllLocks(Ticket ticket) throws RepositoryException, 
							 					   SecurityException {

		return locksModule.findAllLocks(ticket);
	}

	public ResourceNode createResource(Ticket ticket, 
									   ResourceNodeProperties properties) 
											throws RepositoryException, 
												   SecurityException {

		return resourcesModule.createResource(ticket,properties);		
	}

	public void addResourceToDocument(Ticket ticket, 
									  String resourceId, 
									  String documentId) 
											throws RepositoryException, 
												   SecurityException {

		resourcesModule.addResourceToDocument(ticket,resourceId,documentId);
	}

	public List findNodesForResource(Ticket ticket, String resourceId) throws RepositoryException {

		return resourcesModule.findNodesForResource(ticket,resourceId);
	}

	public byte[] loadResourceNodeContent(Ticket ticket, 
										  String resourceId) 
												throws RepositoryException, 
													   SecurityException {
		
		return resourcesModule.loadResourceNodeContent(ticket,resourceId);
	}

	
	public void loadResourceNodeContent(Ticket ticket, 
			  							String resourceId,
			  							OutputStream stream) 
											throws RepositoryException, 
											   	   SecurityException {
		
		resourcesModule.loadResourceNodeContent(ticket,resourceId,stream);
	}	

	public ResourceNode updateResourceNode(Ticket ticket, 
										   ResourceNodeProperties properties) 
													throws RepositoryException, 
														   SecurityException {

		return resourcesModule.updateResourceNode(ticket,properties);
	}

	public void removeResourceNode(Ticket ticket, 
								   String resourceId) 
										throws RepositoryException, 
											   SecurityException {
		
		resourcesModule.removeResourceNode(ticket,resourceId);
	}

	public void removeResourceNode(Ticket ticket, 
								   String resourceId, 
								   String docId) 
										throws RepositoryException, 
											   SecurityException {

		resourcesModule.removeResourceNode(ticket,resourceId,docId);
	}

	/**
	 * Note that destinationRepository is not used on this implementation
	 * 
	 * @see RepositoryService#copyResource(Ticket, String, String, String)
	 */
	public ResourceNode copyResource(Ticket ticket, 
									 String resourceId, 
									 String directoryId,
									 String destinationRepository) 
												throws RepositoryException, 
													   SecurityException {

		try {
			javax.jcr.Node node = internalCopyNode(ticket,resourceId,directoryId);
			javax.jcr.Node root = JCRUtils.getRootNode(node.getSession());
			return JCRAdapter.createResource(node,
											 node.getParent().getUUID(),
											 root.getUUID());
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	public javax.jcr.Node internalMoveNode(Ticket ticket, 
										   String resourceId, 
										   String directoryId) 
													throws RepositoryException, 
														   SecurityException {
	
		try {
			SessionManager manager = SessionManager.getInstance();
			Session session = manager.getSession(ticket);
			javax.jcr.Node source = session.getNodeByUUID(resourceId);
			javax.jcr.Node destination = session.getNodeByUUID(directoryId);
			
			if (!JCRSecurityService.canRead(source, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
	
			if (!JCRSecurityService.canWrite(destination, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			javax.jcr.Node resultNode;
			Object syncLock = LockUtility.obtainLock(destination);
			synchronized(syncLock) {
			
				String extension = null;
				if (source.isNodeType(JLibraryConstants.RESOURCE_MIXIN) ||
					source.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
					String path = source.getProperty(
							JLibraryConstants.JLIBRARY_PATH).getString();
					extension = FileUtils.getExtension(path);
				}
							
				String validName = JCRUtils.buildValidChildNodeName(
		   				source.getParent(),
		   				extension,
		   				source.getName());
				if ((extension != null) && !validName.endsWith(extension)) {
					validName+=extension;
				}
				
				String destinationPath = destination.getPath() + "/" + validName;
				destinationPath = StringUtils.replace(destinationPath,"//","/");
				session.move(source.getPath(),destinationPath);
				
				resultNode = destination.getNode(validName);
				changePathRecursively(resultNode,destination);
				
				if (ticket.isAutocommit()) {
					session.save();
				}
			}			
			return resultNode;
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	private void changePathRecursively(javax.jcr.Node resultNode,
									   javax.jcr.Node parent) 
												throws RepositoryException {
		
		try {
			String destinationName = "";
			if (resultNode.isNodeType(JLibraryConstants.DIRECTORY_MIXIN)) {
				destinationName = obtainAvailableDirectoryName(parent,resultNode);
			} else {
				String path = resultNode.getProperty(
						JLibraryConstants.JLIBRARY_PATH).getString();
				destinationName = FileUtils.getFileName(path);
			}
			
			resultNode.setProperty(JLibraryConstants.JLIBRARY_PATH,
					  getPath(parent) + destinationName);
			
			NodeIterator it = resultNode.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node child = (javax.jcr.Node) it.next();
				if (!JCRUtils.isActive(child)) {
					continue;
				}
				if ((child.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) ||
				   (child.isNodeType(JLibraryConstants.DIRECTORY_MIXIN)) ||
				   (child.isNodeType(JLibraryConstants.RESOURCE_MIXIN))) {
					changePathRecursively(child,resultNode);
				}
			}
			
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
		
	}

	public javax.jcr.Node internalCopyNode(Ticket ticket, 
			   							   String resourceId, 
			   							   String directoryId) 
												throws RepositoryException, 
													   SecurityException {
	
		try {
			SessionManager manager = SessionManager.getInstance();
			Session session = manager.getSession(ticket);
			javax.jcr.Node source = session.getNodeByUUID(resourceId);
			javax.jcr.Node destination = session.getNodeByUUID(directoryId);
	
			if (!JCRSecurityService.canRead(source, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
	
			if (!JCRSecurityService.canWrite(destination, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			javax.jcr.Node resultNode;
			Object syncLock = LockUtility.obtainLock(destination);
			synchronized(syncLock) {

				String extension = null;
				if (source.isNodeType(JLibraryConstants.RESOURCE_MIXIN) ||
					source.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
					String path = source.getProperty(
							JLibraryConstants.JLIBRARY_PATH).getString();
					extension = FileUtils.getExtension(path);
				}
							
				String validName = JCRUtils.buildValidChildNodeName(
		   				source.getParent(),
		   				extension,
		   				source.getName());
				if ((extension != null) && !validName.endsWith(extension)) {
					validName+=extension;
				}
				
				String destinationPath = destination.getPath() + "/" + validName;
				destinationPath = StringUtils.replace(destinationPath,"//","/");							
				session.getWorkspace().copy(source.getPath(),destinationPath);
				
				resultNode = destination.getNode(validName);
				changePathRecursively(resultNode,destination);	
				
				if (ticket.isAutocommit()) {
					session.save();
				}
			}
			return resultNode;
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	/**
	 * Note that destinationRepository is not used on this implementation
	 * 
	 * @see RepositoryService#moveResource(Ticket, String, String, String)
	 */
	public ResourceNode moveResource(Ticket ticket, 
									 String resourceId, 
									 String directoryId,
									 String destinationRepository) 
											throws RepositoryException, 
												   SecurityException {

		try {
			javax.jcr.Node node = internalMoveNode(ticket,resourceId,directoryId);
			javax.jcr.Node root = JCRUtils.getRootNode(node.getSession());
			return JCRAdapter.createResource(node,
											 node.getParent().getUUID(),
											 root.getUUID());
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	/**
	 * Note that destinationRepository is not used on this implementation
	 * 
	 * @see RepositoryService#copyNode(Ticket, String, String, String)
	 */
	public Node copyNode(Ticket ticket, 
						 String sourceId, 
						 String destinationId,
						 String destinationRepository) 
								throws RepositoryException, 
									   SecurityException {

		try {
			SessionManager manager = SessionManager.getInstance();
			Session session = manager.getSession(ticket);
			javax.jcr.Node node = session.getNodeByUUID(sourceId);
			if (node.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
				return copyDocument(ticket,sourceId,destinationId,destinationRepository);
			} else if (node.isNodeType(JLibraryConstants.DIRECTORY_MIXIN)) {
				return copyDirectory(ticket,sourceId,destinationId,destinationRepository);
			} else if (node.isNodeType(JLibraryConstants.RESOURCE_MIXIN)) {
				return copyResource(ticket,sourceId,destinationId,destinationRepository);
			} else {
				return null;
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		
	}

	/**
	 * Note that destinationRepository is not used on this implementation
	 * 
	 * @see RepositoryService#moveNode(Ticket, String, String, String)
	 */
	public Node moveNode(Ticket ticket, 
						 String sourceId, 
						 String destinationId,
						 String destinationRepository) 
								throws RepositoryException, 
									   SecurityException {

		try {
			SessionManager manager = SessionManager.getInstance();
			Session session = manager.getSession(ticket);
			javax.jcr.Node node = session.getNodeByUUID(sourceId);
			if (node.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
				return moveDocument(ticket,sourceId,destinationId,destinationRepository);
			} else if (node.isNodeType(JLibraryConstants.DIRECTORY_MIXIN)) {
				return moveDirectory(ticket,sourceId,destinationId,destinationRepository);
			} else if (node.isNodeType(JLibraryConstants.RESOURCE_MIXIN)) {
				return moveResource(ticket,sourceId,destinationId,destinationRepository);
			} else {
				return null;
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	/**
	 * <p>Obtain a valid directory name for a directory path. In jLibrary, all 
	 * the nodes will have a unique non repository dependent path. With this 
	 * path we ensure that changes on node hierarchy names does not conflict 
	 * with node rendering on external applications. Anyways, external 
	 * applications can choose to use or ignore this path.</p>
	 * 
	 * <p>The node paths are assigned on sequency, being the first number 0. This 
	 * method returns the first available node path name. Not that this number 
	 * must not be mandatory the next number to the biggest one available, as 
	 * probably some nodes could have been removed and so they will left empty 
	 * slots.</p> 
	 * 
	 * <p>This method is synchronized as otherwise we could have concurrency 
	 * problems with several threads trying to obtain available directory names 
	 * on the same path.</p>
	 * 
	 * @param parent Parent from which we will look for a path
	 * @param node Node for which we are looking for a path
	 * 
	 * @return String First available path slot
	 * 
	 * @throws javax.jcr.RepositoryException If the path cannot be obtained
	 */
	public synchronized String obtainAvailableDirectoryName(
			javax.jcr.Node parent,
			javax.jcr.Node node) throws javax.jcr.RepositoryException {
		
		ArrayList names = new ArrayList();
		javax.jcr.NodeIterator it = parent.getNodes();
		while (it.hasNext()) {
			javax.jcr.Node child = it.nextNode();
			// Ignore the node for which we are looking a path, if exists
			if (child.getUUID().equals(node.getUUID())) continue;
			
			if (child.isNodeType(JLibraryConstants.DIRECTORY_MIXIN)) {
				if (child.hasProperty(JLibraryConstants.JLIBRARY_PATH)) {
					String path = child.getProperty(
							JLibraryConstants.JLIBRARY_PATH).getValue().getString();
					String name = FileUtils.getFileName(path);
					names.add(name);
				}
			}
		}
		
		ArrayList ids = new ArrayList();
		Iterator it2 = names.iterator();
		while (it2.hasNext()) {
			String name = (String) it2.next();
			ids.add(new Integer(Integer.parseInt(name)));
		}

		Collections.sort(ids);
		
		String dirId = null;
		for (int i = 0;i<ids.size();i++) {
			Integer id = (Integer)ids.get(i);
			if (!(id.intValue() == i)) {
				dirId = String.valueOf(i);
			}
		}
		if (dirId == null) {
			dirId = String.valueOf(ids.size());
		}
		
		return dirId;
	}	
	
	public List getVersions(Ticket ticket, 
							String documentId) throws RepositoryException, 
													  SecurityException {

		try {
			SessionManager manager = SessionManager.getInstance();
			Session session = manager.getSession(ticket);
			javax.jcr.Node node = session.getNodeByIdentifier(documentId);

			if (!JCRSecurityService.canRead(node, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}			
			javax.jcr.Node root = JCRUtils.getRootNode(node.getSession());
			
			Document document = 
				JCRAdapter.createDocument(node, 
										  node.getParent().getIdentifier(), 
										  root.getIdentifier());
			List versions = new ArrayList();
			
			VersionManager vm = session.getWorkspace().getVersionManager();			
			VersionHistory history = vm.getVersionHistory(node.getCorrespondingNodePath(session.getWorkspace().getName()));
			
	        for (VersionIterator it = history.getAllLinearVersions(); it.hasNext();) {
	          Version version = (Version) it.next();
	          if (version.getIdentifier().equals(vm.getBaseVersion(node.getCorrespondingNodePath(node.getSession().getWorkspace().getName())).getIdentifier())) {
	        	  continue;
	          }
	          versions.add(JCRAdapter.createVersion(node,version,document));	         
	        }	        	        
	       
	        return versions;
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		
	}
	
	public Collection findNodeChildren(Ticket ticket, String id) throws RepositoryException,
			NodeNotFoundException, SecurityException
	{

		ArrayList children = new ArrayList();
		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);

		try {
			javax.jcr.Node node = session.getNodeByUUID(id);
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			
			if (!JCRSecurityService.canRead(node, ticket.getUser().getId())) {
				throw new SecurityException(
						SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			if (node.isNodeType(JLibraryConstants.DOCUMENT_MIXIN))
				return Collections.EMPTY_LIST;

			NodeIterator it = node.getNodes();
			Value lazyLoading = JCRUtils.getConfigProperty(
					node.getSession(),
					RepositoryProperties.DO_LAZY_LOADING);
			boolean lazy = lazyLoading.getBoolean();
			while(it.hasNext())
			{
				javax.jcr.Node child = (javax.jcr.Node)it.next();
				if (!JCRUtils.isActive(child)) {
					continue;
				}
				if (!JCRSecurityService.canRead(child, ticket.getUser().getId())) {
					continue;
				}
				if (child.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
					children.add(JCRAdapter.createDocument(child, id, root.getUUID()));
				} else if (child.isNodeType(JLibraryConstants.DIRECTORY_MIXIN)) {
					children.add(JCRAdapter.createDirectory(child, id, root.getUUID(),
							ticket.getUser().getId(), lazy));
				} else if (child.isNodeType(JLibraryConstants.RESOURCE_MIXIN)) {
					children.add(JCRAdapter.createResource(child, id, root.getUUID()));
				}
			}
			return children;

		} catch (ItemNotFoundException infe) {
			logger.error(infe.getMessage(),infe);
			throw new NodeNotFoundException(infe);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	/**
	 * @see RepositoryService#saveSession(Ticket)
	 */
	public void saveSession(Ticket ticket) throws RepositoryException {
		
		javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
		if (session == null) {
			throw new RepositoryException("Session has expired. Please log in again.");
		}
		try {
			session.save();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	public void registerCustomProperty(Ticket ticket, 
									   CustomPropertyDefinition property) throws RepositoryException {
		
		builder.registerCustomProperty(ticket, property);
	}
	
	public void unregisterCustomProperty(Ticket ticket, 
			   							 CustomPropertyDefinition property) throws RepositoryException {

		builder.unregisterCustomProperty(ticket, property);
	}
	
	public boolean isPropertyRegistered(Ticket ticket, String propertyName) throws RepositoryException {

		return builder.isPropertyRegistered(ticket, null, propertyName);
	}

	public boolean isPropertyRegistered(Ticket ticket,
										String uri,
										String propertyName) throws RepositoryException {
		
		return builder.isPropertyRegistered(ticket, uri, propertyName);
	}
	
	public void importRepository(Ticket ticket, 
								 String name, 
								 InputStream stream) throws RepositoryAlreadyExistsException, 
								 							RepositoryException, 
								 							SecurityException {

		try {
			importExportModule.importRepository(ticket, name, stream);
		} catch (RepositoryException re) {
			try {
				// In this case we must delete the recently created workspace				
				deleteWorkspace(ticket, name);
				throw re;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				// throw previous exception, otherwise it will be confusing
				throw re;
			}
		} catch (SecurityException se) {
			throw se;
		}		
	}
	
	public Node updateContent(Ticket ticket, 
			  				  String nodeId,
			  				  byte[] content) throws SecurityException, 
			  						 				 RepositoryException {
		
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(content);
			Node updatedNode = updateContent(ticket, nodeId, bais);
			return updatedNode;
		} finally {
			try {
			    if (bais != null) {
					bais.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new RepositoryException(e);
			}
		}
		
	}
	
	public Node updateContent(Ticket ticket, 
							  String nodeId,
							  InputStream stream) throws SecurityException, 
							  							 	 RepositoryException {

		try {
			SessionManager manager = SessionManager.getInstance();
			Session session = manager.getSession(ticket);
			
			javax.jcr.Node node = session.getNodeByUUID(nodeId);
			if (!JCRSecurityService.canWrite(node, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			Object syncLock = LockUtility.obtainLock(node);
			synchronized(syncLock) {
				locksModule.checkLockAccess(ticket,node);			
				
				if (ticket.isAutocommit()) {
					// If commit is disabled we still don't have the node in the repository
					if (node.isNodeType(JCRConstants.JCR_VERSIONABLE)) {
						JCRUtils.checkinIfNecessary(node);				
						node.checkout();
					}
				}
				
				javax.jcr.Node child = node.getNode(JCRConstants.JCR_CONTENT); 								
				String path = node.getProperty(
						JLibraryConstants.JLIBRARY_PATH).getString();
			    child.setProperty (JCRConstants.JCR_DATA, stream);
			    Calendar lastModified = Calendar.getInstance ();
			    lastModified.setTimeInMillis (new Date().getTime());
			    child.setProperty (JCRConstants.JCR_LAST_MODIFIED, lastModified);
	
			    //TODO: Check how to update size						
			    //node.setProperty(JLibraryConstants.JLIBRARY_SIZE,content.length);
				if (ticket.isAutocommit()) {
					session.save();
					
					if (node.isNodeType(JCRConstants.JCR_VERSIONABLE)) {
						// create first version
						node.checkin();
						// restore to read-write state
						node.checkout();
					}
				}
			}
			
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			if (node.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
				return JCRAdapter.createDocument(node,node.getParent().getUUID(),root.getUUID());
			} else if (node.isNodeType(JLibraryConstants.RESOURCE_MIXIN)) {
				return JCRAdapter.createResource(node, node.getParent().getUUID(), root.getUUID());
			}
			return null;
		} catch (Throwable e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}	
	
}
