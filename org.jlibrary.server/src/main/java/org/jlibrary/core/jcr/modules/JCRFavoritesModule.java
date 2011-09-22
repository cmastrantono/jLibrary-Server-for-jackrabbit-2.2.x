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

import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Workspace;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.jlibrary.core.entities.Favorite;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.jcr.JCRAdapter;
import org.jlibrary.core.jcr.JCRConstants;
import org.jlibrary.core.jcr.JCRSecurityService;
import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.jcr.SessionManager;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Module to isolate favorite operations of the default services implementation
 */
public class JCRFavoritesModule {

	static Logger logger = LoggerFactory.getLogger(JCRFavoritesModule.class);
	
	/**
	 * Conscructor
	 */
	public JCRFavoritesModule() {}

	/**
	 * Delete a favorite given its id
	 * 
	 * @param ticket Ticket with user information
	 * @param favoriteId Id of the favorite to be deleted
	 * 
	 * @throws RepositoryException If the favorite can't be deleted
	 * @throws SecurityException if the user don't have enough rights to delete a favorite
	 */	
	public void deleteFavorite(Ticket ticket, 
							   String favoriteId) throws RepositoryException, 
							   							 SecurityException {


		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}

			javax.jcr.Node root = JCRUtils.getRootNode(session);
			if (!JCRSecurityService.canWrite(root, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}			
			
			javax.jcr.Node favoriteNode = 
				session.getNodeByUUID(favoriteId);		
			favoriteNode.remove();
			if (ticket.isAutocommit()) {
				session.save();
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	

	
	/**
	 * Finds a favorite 
	 * 
	 * @param ticket Ticket with user information
	 * @param favoriteId Favorite's id
	 * 
	 * @return Favorite or <code>null</code> if can't be found
	 * 
	 * @throws RepositoryException If some error happens
	 */	
	public Favorite findFavorite(Ticket ticket,
								 String favoriteId) throws RepositoryException {

		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			
			javax.jcr.Node favoriteNode = 
				session.getNodeByUUID(favoriteId);		
			return JCRAdapter.createFavorite(favoriteNode);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	/**
	 * Creates a favorite
	 * 
	 * @param ticket Ticket with user and repository id information
	 * @param favorite Favorite item to be created
	 * 
	 * @return New created favorite
	 * 
	 * @throws RepositoryException If the favorite can't be created
	 * @throws SecurityException if the user don't have enough rights to create a favorite
	 */	
	public Favorite createFavorite(Ticket ticket, 
								   Favorite favorite) throws RepositoryException, 
								   							 SecurityException {

		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			if (!JCRSecurityService.canWrite(root, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			javax.jcr.Node categoryNode = 
								session.getNodeByUUID(favorite.getCategory());
			javax.jcr.Node documentNode = 
				session.getNodeByUUID(favorite.getDocument());

			javax.jcr.Node favoritesNode = null;
			if (!categoryNode.hasNode(JLibraryConstants.JLIBRARY_FAVORITES)) {
				favoritesNode = categoryNode.addNode(
						JLibraryConstants.JLIBRARY_FAVORITES,
						JLibraryConstants.INTERNAL_MIXIN);
			} else {
				favoritesNode = categoryNode.getNode(
						JLibraryConstants.JLIBRARY_FAVORITES);
			}
			javax.jcr.Node favoriteNode =  
				favoritesNode.addNode(JLibraryConstants.JLIBRARY_FAVORITE,
									  JLibraryConstants.FAVORITE_MIXIN);
			
			favoriteNode.addMixin(JCRConstants.JCR_REFERENCEABLE);
			favoriteNode.setProperty(
					JLibraryConstants.JLIBRARY_NODE,
					documentNode);
			if (favorite.getUser() != null) {
				javax.jcr.Node userNode = 
					JCRSecurityService.getUserNode(session,favorite.getUser());
				favoriteNode.setProperty(
						JLibraryConstants.JLIBRARY_USER,
						userNode);
			}			
			if (ticket.isAutocommit()) {
				session.save();
			}
		
			return JCRAdapter.createFavorite(favoriteNode);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	public static List findFavoritesForUser(Ticket ticket,
											String userId) throws RepositoryException {

		List nodes = new ArrayList();
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();

			String statement = "/jcr:root//*[@jcr:primaryType='jlib:favorite' and @jlib:user='"+
							   userId+"' and @jlib:active='true']"; 

			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();

			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				nodes.add(it.nextNode());
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		return nodes;
	}	
	
	/**
	 * Removes recursively node references for favorites
	 * 
	 * @param node Node that is going to be removed
	 * 
	 * @throws javax.jcr.RepositoryException If the node can't be removed
	 */
	public void removeFavoriteReferences (javax.jcr.Node node) 
										throws javax.jcr.RepositoryException {
		
		boolean modified = false;
		if (node.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
			PropertyIterator it = node.getReferences();
			while (it.hasNext()) {
				Property property = (Property) it.next();
				javax.jcr.Node parent = property.getParent();
				if (parent.isNodeType(
						JLibraryConstants.FAVORITE_MIXIN)) {
					javax.jcr.Node favorite = property.getParent();
					favorite.remove();
					modified = true;
				}
			}			
			if (modified) {
				node.getSession().save();
			}
		} else {
			// go recursive if directory
			NodeIterator nit = node.getNodes();
			while (nit.hasNext()) {
				javax.jcr.Node child = (javax.jcr.Node) nit.next();
				removeFavoriteReferences(child);
			}
		}
	}	
}
