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

import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.jcr.JCRAdapter;
import org.jlibrary.core.jcr.JCRConstants;
import org.jlibrary.core.jcr.JCRSecurityService;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.jcr.SessionManager;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author martin
 *
 * Module to isolate bookmark operations of the default services implementation
 */
public class JCRBookmarksModule {

	static Logger logger = LoggerFactory.getLogger(JCRBookmarksModule.class);
	
	/**
	 * Constructor
	 */
	public JCRBookmarksModule() {}

	public Bookmark findBookmark(Ticket ticket,
								 String bookmarkId) throws RepositoryException {

		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}

			javax.jcr.Node bookmarkNode = 
				session.getNodeByUUID(bookmarkId);		
			return JCRAdapter.createBookmark(bookmarkNode);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	public Bookmark createBookmark(Ticket ticket,
								   Bookmark bookmark) throws RepositoryException {
		
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			javax.jcr.Node parentNode = null;
			if (bookmark.getParent() == null) {
				javax.jcr.Node userNode = 
					JCRSecurityService.getUserNode(session, 
												   bookmark.getUser());

				if (!userNode.hasNode(JLibraryConstants.JLIBRARY_BOOKMARKS)) {
					parentNode = userNode.addNode(
							JLibraryConstants.JLIBRARY_BOOKMARKS,
							JLibraryConstants.INTERNAL_MIXIN);
				} else {
					parentNode = userNode.getNode(
							JLibraryConstants.JLIBRARY_BOOKMARKS);
				}
			} else {
				parentNode = session.getNodeByUUID(bookmark.getParent().getId());
			}
			
			javax.jcr.Node bookmarkNode = 
				parentNode.addNode(JLibraryConstants.JLIBRARY_BOOKMARK,
								   JLibraryConstants.BOOKMARK_MIXIN);
			
			bookmarkNode.addMixin(JCRConstants.JCR_REFERENCEABLE);
			bookmarkNode.setProperty(
					JLibraryConstants.JLIBRARY_NAME,
					bookmark.getName());
			bookmarkNode.setProperty(
					JLibraryConstants.JLIBRARY_DESCRIPTION,
					bookmark.getDescription());
			bookmarkNode.setProperty(
					JLibraryConstants.JLIBRARY_USER,
					bookmark.getUser());
			bookmarkNode.setProperty(
					JLibraryConstants.JLIBRARY_DOCUMENT_URL,
					bookmark.getUrl());
			bookmarkNode.setProperty(
					JLibraryConstants.JLIBRARY_TYPECODE,
					bookmark.getType());
			
			if (ticket.isAutocommit()) {
				session.save();
			}
		
			return JCRAdapter.createBookmark(bookmarkNode);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	public void removeBookmark(Ticket ticket, 
							   String bookmarkId) throws RepositoryException {
		
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}

			javax.jcr.Node favoriteNode = 
				session.getNodeByUUID(bookmarkId);		
			favoriteNode.remove();
			if (ticket.isAutocommit()) {
				session.save();
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}
	
	public Bookmark updateBookmark(Ticket ticket,
							   Bookmark bookmark) throws RepositoryException {
		
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			
			javax.jcr.Node node = session.getNodeByUUID(bookmark.getId());
			
			node.setProperty(JLibraryConstants.JLIBRARY_NAME,
					   			   bookmark.getName());
			node.setProperty(JLibraryConstants.JLIBRARY_DESCRIPTION,
					   			   bookmark.getDescription());
			node.setProperty(JLibraryConstants.JLIBRARY_DOCUMENT_URL,
		   			   bookmark.getUrl());
			node.setProperty(JLibraryConstants.JLIBRARY_TYPECODE,
		   			   bookmark.getType());
			node.setProperty(JLibraryConstants.JLIBRARY_USER,
		   			   bookmark.getUser());
			
			String currentParent = null;
			if (node.getParent().isNodeType(JLibraryConstants.BOOKMARK_MIXIN)) {
				currentParent = node.getParent().getUUID();
			}
			
			if (currentParent == null) {
				if (bookmark.getParent() != null) {
					// move the bookmark to a child
					javax.jcr.Node destinationBookmark = 
						session.getNodeByUUID(bookmark.getParent().getId());
					session.move(node.getPath(), 
								 destinationBookmark.getPath() + "/" + node.getName());
				}
			} else {
				if (bookmark.getParent() == null) {
					// move the bookmark to the bookmarks root
					javax.jcr.Node userNode = 
						JCRSecurityService.getUserNode(session, 
													   bookmark.getUser());
					javax.jcr.Node bookmarksNode = 
						userNode.getNode(JLibraryConstants.JLIBRARY_BOOKMARKS);
					
					session.move(node.getPath(), 
								 bookmarksNode.getPath() + "/" + node.getName());
				}
			}
			
			if (ticket.isAutocommit()) {
				session.save();
			}
			
			return JCRAdapter.createBookmark(node);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}			
}
