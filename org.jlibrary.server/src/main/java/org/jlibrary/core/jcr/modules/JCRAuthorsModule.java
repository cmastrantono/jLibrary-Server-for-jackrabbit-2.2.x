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

import java.util.ArrayList;
import java.util.List;

import javax.jcr.ItemNotFoundException;
import javax.jcr.NodeIterator;
import javax.jcr.Workspace;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.jcr.JCRAdapter;
import org.jlibrary.core.jcr.JCRConstants;
import org.jlibrary.core.jcr.JCRSecurityService;
import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.jcr.SessionManager;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.repository.exception.AuthorAlreadyExistsException;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author martin
 *
 * Module to isolate author operations of the default services implementation
 */
public class JCRAuthorsModule {

	static Logger logger = LoggerFactory.getLogger(JCRAuthorsModule.class);
	
	public JCRAuthorsModule() {}

	public List findAllAuthors(Ticket ticket) throws RepositoryException {

		List authors = new ArrayList();
		
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			String statement = "//*[@jcr:primaryType='jlib:author' and @jlib:active='true']";
						
			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();
			
			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				authors.add(JCRAdapter.createAuthor(node));
			}
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		
		return authors;
	}

	public Author createAuthor(Ticket ticket, 
							   AuthorProperties properties) 
										   throws RepositoryException,
			   				   					  AuthorAlreadyExistsException {

		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			javax.jcr.Node root = JCRUtils.getRootNode(session);
			if (!JCRSecurityService.canAdmin(root, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}			
			
			javax.jcr.Node systemNode = JCRUtils.getSystemNode(session); 
			javax.jcr.Node authorsNode = 
				systemNode.getNode(JLibraryConstants.JLIBRARY_AUTHORS);
			
			String name = properties.getProperty(
					AuthorProperties.AUTHOR_NAME).getValue().toString();
			String bio = properties.getProperty(
					AuthorProperties.AUTHOR_BIO).getValue().toString();
			
			try {
				findAuthorByName(ticket,name);
				throw new AuthorAlreadyExistsException();
			} catch (AuthorNotFoundException anfe) {}
			
			javax.jcr.Node authorNode = 
				authorsNode.addNode(JLibraryConstants.JLIBRARY_AUTHOR,
									JLibraryConstants.AUTHOR_MIXIN);
			
			authorNode.addMixin(JCRConstants.JCR_REFERENCEABLE);
			
			authorNode.setProperty(JLibraryConstants.JLIBRARY_NAME,
								   name);
			authorNode.setProperty(JLibraryConstants.JLIBRARY_AUTHOR_BIO,
					   			   bio);
			
			if (ticket.isAutocommit()) {
				session.save();
			}
			
			return JCRAdapter.createAuthor(authorNode);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	public void updateAuthor(Ticket ticket, 
							 String authorId,
							 AuthorProperties properties) 
												throws RepositoryException, 
							 					   	   SecurityException,
							 					   	   AuthorNotFoundException {

		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			javax.jcr.Node root = JCRUtils.getRootNode(session);
			if (!JCRSecurityService.canAdmin(root, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			javax.jcr.Node authorNode = session.getNodeByUUID(authorId);
			
			String name = properties.getProperty(
					AuthorProperties.AUTHOR_NAME).getValue().toString();
			String bio = properties.getProperty(
					AuthorProperties.AUTHOR_BIO).getValue().toString();
			
			
			authorNode.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
			authorNode.setProperty(JLibraryConstants.JLIBRARY_AUTHOR_BIO,bio);

			if (ticket.isAutocommit()) {
				session.save();
			}
		} catch (ItemNotFoundException infe) {
			throw new AuthorNotFoundException();
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	public void deleteAuthor(Ticket ticket, 
							 String authorId) throws RepositoryException, 
							 						 SecurityException,
							 						 AuthorNotFoundException {

		// In the default implementation repositoryId is used to find if the 
		// user had permissions. Here is not needed
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new SecurityException("Session has expired. Please log in again.");
			}

			javax.jcr.Node root = JCRUtils.getRootNode(session);
			if (!JCRSecurityService.canAdmin(root, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}			
			
			javax.jcr.Node authorNode = session.getNodeByUUID(authorId);			
			// We will tag the author as not active, instead of deleting it	
			authorNode.setProperty(JLibraryConstants.JLIBRARY_ACTIVE,false);
			
			if (ticket.isAutocommit()) {
				session.save();
			}
		} catch (ItemNotFoundException infe) {
			throw new AuthorNotFoundException();
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}			
	}

	public Author findAuthorByName(Ticket ticket, 
								   String name) throws AuthorNotFoundException,
								   					   RepositoryException {

		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}

			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			
			String statement = "/jcr:root//*[@jcr:primaryType='jlib:author' and @jlib:name='"+
							   name+"' and @jlib:active='true']"; 
			
			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();
			
			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				return JCRAdapter.createAuthor(node);
			}
			throw new AuthorNotFoundException();
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}			
	}

	public Author findAuthorById(Ticket ticket, 
								 String id) throws AuthorNotFoundException,
								 				   RepositoryException {

		try {
			
			if (id == Author.UNKNOWN.getId()) {
				return Author.UNKNOWN;
			}
			
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			
			javax.jcr.Node authorNode = session.getNodeByUUID(id);
			return JCRAdapter.createAuthor(authorNode);
		} catch (javax.jcr.ItemNotFoundException infe) {
			logger.error(infe.getMessage(),infe);
			throw new AuthorNotFoundException();
		} catch (javax.jcr.RepositoryException e) {
			if (e.getMessage().startsWith("Invalid UUID")) {
				throw new AuthorNotFoundException();
			}
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}			
	}

	public javax.jcr.Node findUnknownAuthor(Ticket ticket) 
									throws RepositoryException {
		
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}

			javax.jcr.Node systemNode = JCRUtils.getSystemNode(session); 
			
			javax.jcr.Node unknownAuthor = systemNode.getProperty(
					JLibraryConstants.JLIBRARY_UNKNOWN_AUTHOR).getNode();
			
			return unknownAuthor;
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}			
	}
}
