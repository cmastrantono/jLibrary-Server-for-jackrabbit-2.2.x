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
import javax.jcr.PathNotFoundException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.jcr.JCRAdapter;
import org.jlibrary.core.jcr.JCRConstants;
import org.jlibrary.core.jcr.JCRSecurityService;
import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.jcr.SessionManager;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author martin
 *
 * Module to isolate lock operations of the default services implementation
 */
public class JCRLocksModule {

	static Logger logger = LoggerFactory.getLogger(JCRLocksModule.class);
	
	/**
	 * Constructor
	 */
	public JCRLocksModule() {}

	public Lock lockDocument(Ticket ticket, 
							 String docId) throws RepositoryException, 
							 					  SecurityException,
							 					  ResourceLockedException {

		try {
			
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			javax.jcr.Node node = session.getNodeByUUID(docId);
			
			if (!JCRSecurityService.canWrite(node, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			checkLockAccess(ticket,node);		
			
			javax.jcr.lock.Lock lock = node.lock(false,false);
			
			node.setProperty(JCRConstants.JCR_LOCK_TOKEN,
							 node.getLock().getLockToken());
			node.setProperty(JLibraryConstants.JLIBRARY_LOCK_USER,
					 		 ticket.getUser().getId());
			
			
			if (ticket.isAutocommit()) {
				session.save();
			}
			
			return JCRAdapter.createLock(lock);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	public void unlockDocument(Ticket ticket, 
							   String docId) throws RepositoryException, 
							   						SecurityException,
							   						ResourceLockedException {

		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			javax.jcr.Node node = session.getNodeByUUID(docId);
			
			if (!JCRSecurityService.canWrite(node, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}

			if (!node.isLocked()) {
				return;
			}
			
			checkLockAccess(ticket,node);

			node.unlock();
			node.setProperty(JCRConstants.JCR_LOCK_TOKEN,(Value)null);
			if (ticket.isAutocommit()) {
				session.save();
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}

	public List findAllLocks(Ticket ticket) throws RepositoryException, 
							 					   SecurityException {

		List locks = new ArrayList();

		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}			
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			if (!JCRSecurityService.canRead(root, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			String rootPath = root.getPath();
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			String statement = "/jcr:root" +
							   rootPath + 
							   "//element(*,nt:file)[@jcr:lockOwner and @jlib:active='true']";
			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();
			
			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				locks.add(JCRAdapter.createLock(node.getLock()));
			}
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		return locks;
	}	
	
	public void checkLockAccess(Ticket ticket, 							    
								javax.jcr.Node node) 
										throws javax.jcr.RepositoryException,
											   ResourceLockedException,
											   SecurityException,
											   RepositoryException {
		
		if (node.isLocked()) {
			if (!getLockOwnerId(node).equals(
					ticket.getUser().getId())) {	
				// check if the user is admin
				if (!JCRSecurityService.canAdmin(node,ticket.getUser().getId())) {
					throw new ResourceLockedException(
										JCRAdapter.createLock(node.getLock()));
				}
			}
		}	
		if (node.hasProperty(JCRConstants.JCR_LOCK_TOKEN)) {
			String token = node.getProperty(
					JCRConstants.JCR_LOCK_TOKEN).getString();
			javax.jcr.Session session = node.getSession();
			String[] lockTokens = session.getLockTokens();
			for (int i = 0; i < lockTokens.length; i++) {
				if (lockTokens[i].equals(token)) {
					// The session already has a lock token
					return;
				}
			}
			// The session does not has a token. Add one.
			session.addLockToken(node.getProperty(
					JCRConstants.JCR_LOCK_TOKEN).getString());		
		}
		

	}
	
	/**
	 * Returns the jLibrary user id of the user that have locked a given node
	 * 
	 * @param node Node that is locked
	 * 
	 * @return String jLibrary user id.
	 * 
	 * @throws RepositoryException If the operation can't be performed
	 */
	public String getLockOwnerId(javax.jcr.Node node) throws RepositoryException {
		
		try {
			return node.getProperty(JLibraryConstants.JLIBRARY_LOCK_USER).getString();
		} catch (ValueFormatException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (PathNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
}
