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
package org.jlibrary.core.jcr.modules;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Value;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.util.Text;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.jcr.JCRAdapter;
import org.jlibrary.core.jcr.JCRConstants;
import org.jlibrary.core.jcr.JCRRepositoryService;
import org.jlibrary.core.jcr.JCRSecurityService;
import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.jcr.LockUtility;
import org.jlibrary.core.jcr.SessionManager;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRResourcesModule {

	static Logger logger = LoggerFactory.getLogger(JCRResourcesModule.class);
	
	private JCRRepositoryService repositoryService;

	public JCRResourcesModule(JCRRepositoryService repositoryService) {

		this.repositoryService = repositoryService;
	}
	
	public void addResourceToDocument(Ticket ticket, 
									  String resourceId, 
									  String documentId) 
											throws RepositoryException, 
												   SecurityException {

		javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
		if (session == null) {
			throw new RepositoryException("Session has expired. Please log in again.");
		}		
		try {
			javax.jcr.Node resource = session.getNodeByUUID(resourceId);
			javax.jcr.Node document = session.getNodeByUUID(documentId);
			
			Object syncLock = LockUtility.obtainLock(document);
			synchronized(syncLock) {
				document.checkout();
				
				if (!JCRSecurityService.canWrite(document, ticket.getUser().getId())) {
					throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
				}			
				
				if (!document.hasProperty(JLibraryConstants.JLIBRARY_RESOURCES)) {
					document.setProperty(JLibraryConstants.JLIBRARY_RESOURCES,
									 new Value[]{});
				}
				
				JCRUtils.addNodeToProperty(resource.getUUID(),
										   document,
										   JLibraryConstants.JLIBRARY_RESOURCES);			
				
				if (ticket.isAutocommit()) {
					session.save();
				}		
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}

	public ResourceNode createResource(Ticket ticket, 
									   ResourceNodeProperties properties) 
													throws RepositoryException, 
														   SecurityException {

		try {
			String parentId = (String)properties.getProperty(
					ResourceNodeProperties.RESOURCE_PARENT_ID).getValue();
			String name = ((String)properties.getProperty(
					ResourceNodeProperties.RESOURCE_NAME).getValue());
			String path = ((String)properties.getProperty(
					ResourceNodeProperties.RESOURCE_PATH).getValue());
			String extension = FileUtils.getExtension(path);
			String description = ((String)properties.getProperty(
					ResourceNodeProperties.RESOURCE_DESCRIPTION).getValue());
			Integer typecode = ((Integer)properties.getProperty(
					ResourceNodeProperties.RESOURCE_TYPECODE).getValue());
			
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}			
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			javax.jcr.Node parent = session.getNodeByUUID(parentId);
			
			if (!JCRSecurityService.canWrite(parent, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			javax.jcr.Node child = null;
			Object syncLock = LockUtility.obtainLock(parent);
			synchronized(syncLock) {

				String escapedName = 
						JCRUtils.buildValidChildNodeName(parent,
														 extension,
														 name);
				child = parent.addNode(escapedName,
									   JCRConstants.JCR_FILE);
				name = Text.unescape(escapedName);
				if ((extension != null) && !escapedName.endsWith(extension)) {
					escapedName+=extension;
				}
				
				child.addMixin(JCRConstants.JCR_REFERENCEABLE);
				child.addMixin(JCRConstants.JCR_LOCKABLE);
				child.addMixin(JLibraryConstants.RESOURCE_MIXIN);
				
				child.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
				child.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,description);
				child.setProperty(JLibraryConstants.JLIBRARY_CREATED, Calendar.getInstance());
				child.setProperty(JLibraryConstants.JLIBRARY_IMPORTANCE, 
								  Node.IMPORTANCE_MEDIUM.longValue());
				child.setProperty(JLibraryConstants.JLIBRARY_CREATOR,
								  ticket.getUser().getId());
				child.setProperty(JLibraryConstants.JLIBRARY_TYPECODE,
								  typecode.longValue());
				child.setProperty(JLibraryConstants.JLIBRARY_POSITION,0);
				
				child.setProperty(JLibraryConstants.JLIBRARY_PATH,
						  repositoryService.getPath(parent) + 
						  FileUtils.getFileName(path));			
				
				javax.jcr.Node resNode = child.addNode(JCRConstants.JCR_CONTENT, 
											 		   JCRConstants.JCR_RESOURCE);
				resNode.addMixin(JLibraryConstants.CONTENT_MIXIN);
	
				byte[] content = (byte[])properties.getProperty(
						ResourceNodeProperties.RESOURCE_CONTENT).getValue();
				if (content == null) {
					// Empty resource
					content = new byte[]{};
				}
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
		        
				// Handle resource restrictions
				child.setProperty(JLibraryConstants.JLIBRARY_RESTRICTIONS,
								  new Value[]{});
				javax.jcr.Node userNode = 
					JCRSecurityService.getUserNode(session,
												   ticket.getUser().getId());
				JCRRepositoryService.addRestrictionsToNode(child,parent,userNode);
				
				if (ticket.isAutocommit()) {
					session.save();
				}
			}
			return JCRAdapter.createResource(child,parentId,root.getUUID());
		} catch (Throwable e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	public List findNodesForResource(Ticket ticket, 
									 String resourceId) 
										throws RepositoryException {

		javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
		if (session == null) {
			throw new RepositoryException("Session has expired. Please log in again.");
		}		
		ArrayList nodes = new ArrayList();
		try {
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			javax.jcr.Node resource = session.getNodeByUUID(resourceId);
			PropertyIterator it = resource.getReferences();
			while (it.hasNext()) {
				Property property = (Property) it.next();
				String docUUID = property.getParent().getUUID();
				javax.jcr.Node node = session.getNodeByUUID(docUUID);
				try {
					if (!JCRSecurityService.canRead(
							node,ticket.getUser().getId())) {
						continue;
					}
				} catch (SecurityException se) {
					logger.error(se.getMessage(),se);
					continue;
				}
					
				nodes.add(JCRAdapter.createDocument(
						node,
						node.getParent().getUUID(),
						root.getUUID()));
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		return nodes;
		
	}

	public byte[] loadResourceNodeContent(Ticket ticket, 
										  String resourceId) 
												throws RepositoryException, 
													   SecurityException {

		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			loadResourceNodeContent(ticket, resourceId, baos);
			return baos.toByteArray();
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException ioe) {
					logger.error(ioe.getMessage(),ioe);
					throw new RepositoryException(ioe);
				}
			}
		}
		
	}

	public void loadResourceNodeContent(Ticket ticket, 
										String resourceId,
										OutputStream stream) 
												throws RepositoryException, 
													   SecurityException {

		javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
		if (session == null) {
			throw new RepositoryException("Session has expired. Please log in again.");
		}		
		InputStream is = null;
		try {
			javax.jcr.Node resource = session.getNodeByUUID(resourceId);

			if (!JCRSecurityService.canRead(resource, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}			
			
			javax.jcr.Node nodeContent = resource.getNode(JCRConstants.JCR_CONTENT);
			is = nodeContent.getProperty("jcr:data").getValue().getStream();
			IOUtils.copy(is,stream);


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
	
	public void removeResourceNode(Ticket ticket, 
								   String resourceId, 
								   String documentId) 
										throws RepositoryException, 
											   SecurityException {

		javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
		if (session == null) {
			throw new RepositoryException("Session has expired. Please log in again.");
		}		
		try {
			javax.jcr.Node resource = session.getNodeByUUID(resourceId);
			javax.jcr.Node document = session.getNodeByUUID(documentId);
			
			if (!JCRSecurityService.canWrite(document, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}			

			JCRUtils.removeNodeFromProperty(resource.getUUID(),
											document,
											JLibraryConstants.JLIBRARY_RESOURCES);			
			
			if (ticket.isAutocommit()) {
				session.save();
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		

	}

	public void removeResourceNode(Ticket ticket, 
								   String resourceId) 
										throws RepositoryException, 
											   SecurityException {

		javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
		if (session == null) {
			throw new RepositoryException("Session has expired. Please log in again.");
		}		
		try {
			javax.jcr.Node resource = session.getNodeByUUID(resourceId);
			
			if (!JCRSecurityService.canWrite(resource, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}			
			
			JCRUtils.removeReferences(resource);
			
			if (JCRUtils.allowsPhysicalDeletes(session)) {
				resource.remove();
			} else {
				JCRUtils.deactivate(resource);
			}			
			
			if (ticket.isAutocommit()) {
				session.save();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}

	public ResourceNode updateResourceNode(Ticket ticket, 
										   ResourceNodeProperties properties) 
												throws RepositoryException, 
													   SecurityException {

		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}			
			String resourceId = (String)properties.getProperty(
					ResourceNodeProperties.RESOURCE_ID).getValue();
			String parentId = (String)properties.getProperty(
					ResourceNodeProperties.RESOURCE_PARENT_ID).getValue();
			String name = (String)properties.getProperty(
					ResourceNodeProperties.RESOURCE_NAME).getValue();
			String description = (String)properties.getProperty(
					ResourceNodeProperties.RESOURCE_DESCRIPTION).getValue();
			
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			javax.jcr.Node node = session.getNodeByUUID(resourceId);
			
			if (!JCRSecurityService.canWrite(node, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}			
			
			Object syncLock = LockUtility.obtainLock(node);
			synchronized(syncLock) {
				node.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,description);
				
				if (properties.getProperty(
						ResourceNodeProperties.RESOURCE_CONTENT) != null) {
					// Document content has changed
					byte[] content = (byte[])properties.getProperty(
							ResourceNodeProperties.RESOURCE_CONTENT).getValue();
					if (content != null) {
						javax.jcr.Node resNode = node.getNode(JCRConstants.JCR_CONTENT);
		
						resNode.setProperty ("jcr:data", new ByteArrayInputStream(content));
				        Calendar lastModified = Calendar.getInstance ();
				        lastModified.setTimeInMillis (new Date().getTime());
				        resNode.setProperty ("jcr:lastModified", lastModified);
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
					name = Text.unescape(escapedName);
					if ((extension != null) && !escapedName.endsWith(extension)) {
						escapedName+=extension;
					}
					node.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
					session.move(node.getPath(), 
							 	 node.getParent().getPath() + "/" + escapedName);
				}			
				
				if (ticket.isAutocommit()) {
					session.save();
				}
			}
			return JCRAdapter.createResource(node,parentId,root.getUUID());
		} catch (Exception e) {
			throw new RepositoryException(e);
		}		
	}
}
