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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jcr.ItemNotFoundException;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.Workspace;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.jcr.JCRAdapter;
import org.jlibrary.core.jcr.JCRConstants;
import org.jlibrary.core.jcr.JCRSecurityService;
import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.jcr.SessionManager;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.repository.exception.CategoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 * 
 * Module to isolate category operations of the default services implementation
 */
public class JCRCategoriesModule {

	static Logger logger = LoggerFactory.getLogger(JCRCategoriesModule.class);
	
	public JCRCategoriesModule() {}
	
	/**
	 * Updates a category
	 * 
	 * @param ticket Ticket with user information
	 * @param categoryId id of the category we want to update
	 * @param categoryProperties properties for updating the category
	 *  
	 * @throws RepositoryException If the category can't be updated
	 * @throws SecurityException if the user don't have enough rights to update a category
	 */
	public Category updateCategory(Ticket ticket, 
							   	   String categoryId,
							   	   CategoryProperties categoryProperties) 
										throws CategoryNotFoundException,
											   RepositoryException,
							   				   SecurityException {
		
		// TODO: Check name updates with new web app
		
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}
			javax.jcr.Node systemNode = JCRUtils.getSystemNode(session);
			
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			if (!JCRSecurityService.canWrite(root, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			
			javax.jcr.Node categoriesNode = systemNode.getNode(
					JLibraryConstants.JLIBRARY_CATEGORIES); 
			
			javax.jcr.Node categoryNode = getCategoryNode(session,categoryId);
			
			PropertyDef propName = categoryProperties.getProperty(
					CategoryProperties.CATEGORY_NAME);
			if (propName.getValue() != null) {
				String name = (String) propName.getValue();
				categoryNode.setProperty(
						JLibraryConstants.JLIBRARY_NAME,name);
			}
			PropertyDef propDescription = categoryProperties.getProperty(
					CategoryProperties.CATEGORY_DESCRIPTION);
			if (propDescription.getValue() != null) {
				String description = (String) propDescription.getValue();
				categoryNode.setProperty(
						JLibraryConstants.JLIBRARY_DESCRIPTION,description);
			}			
			
			// Update nodes
			PropertyDef[] nodes = categoryProperties
					.getPropertyList(CategoryProperties.CATEGORY_ADD_NODE);
			if (nodes != null) {
				for (int i = 0; i < nodes.length; i++) {
					javax.jcr.Node node = session.getNodeByUUID( 
						((String) nodes[i].getValue()));
					
					JCRUtils.addNodeToProperty(categoryId,
											   node,
											   JLibraryConstants.JLIBRARY_CATEGORIES);					
					JCRUtils.addNodeToProperty(node.getUUID(),
							   				   categoryNode,
							   				   JLibraryConstants.JLIBRARY_NODES);					
				}
			}

			nodes = categoryProperties
					.getPropertyList(CategoryProperties.CATEGORY_REMOVE_NODE);
			if (nodes != null) {
				for (int i = 0; i < nodes.length; i++) {
					javax.jcr.Node node = session.getNodeByUUID( 
							((String) nodes[i].getValue()));
					JCRUtils.removeNodeFromProperty(categoryId,
							   node,
							   JLibraryConstants.JLIBRARY_CATEGORIES);					
					JCRUtils.removeNodeFromProperty(node.getUUID(),
							   						categoryNode,
							   						JLibraryConstants.JLIBRARY_NODES);					
				}
			}
			
			// Now check possible movements
			PropertyDef propParent = categoryProperties.getProperty(
					CategoryProperties.CATEGORY_PARENT);
			String parentId = (String) propParent.getValue();
			// Now check if the category has been moved
			String currentParentId = null;
			if (categoryNode.getParent().isNodeType(
					JLibraryConstants.CATEGORY_MIXIN)) {
				currentParentId = categoryNode.getParent().getUUID();
			}
			String source=null, destination = null;
			boolean moveit = false;
			if ((currentParentId == null) && (parentId != null)) {
				// move it down
				javax.jcr.Node nodeDestination = 
					session.getNodeByUUID(parentId);
				source = categoryNode.getPath();
				destination = nodeDestination.getPath() + 
									 "/" + categoryNode.getName();
				moveit = true;
			} else if (currentParentId != null) {
				if (parentId == null) {
					// move it up
					source = categoryNode.getPath();
					destination = categoriesNode.getPath() +
								  "/" + categoryNode.getName();
					moveit = true;
				} else if (!(parentId.equals(currentParentId))) {
					javax.jcr.Node nodeDestination = 
						session.getNodeByUUID(parentId);
					source = categoryNode.getPath();
					destination = nodeDestination.getPath() + 
										 "/" + categoryNode.getName();
					moveit = true;
				}
			}
			if (moveit) {
				session.move(source,destination);
			}
			if (ticket.isAutocommit()) {
				session.save();
			}
			
			return JCRAdapter.createCategory(categoryNode);
			
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}

	}


	public Category findCategoryByName(Ticket ticket, 
									   String name) 
											throws CategoryNotFoundException,
												   RepositoryException {

		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}

			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			
			String statement = "/jcr:root//*[@jcr:primaryType='jlib:category' and @jlib:name='"+
							   name+"' and @jlib:active='true']"; 
			
			javax.jcr.query.Query query = 
				queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult result = query.execute();
			
			NodeIterator it = result.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				return JCRAdapter.createCategory(node);
			}
			throw new CategoryNotFoundException();
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	/**
	 * Finds all the nodes within a category
	 * 
	 * @param ticket Ticket with user information
	 * @param categoryId category
	 * 
	 * @return Nodes of that category
	 * 
	 * @throws RepositoryException If the categories can't be loaded
	 */	
	public List findNodesForCategory(Ticket ticket, 
									 String categoryId) 
											throws CategoryNotFoundException,
												   RepositoryException {

		javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
		if (session == null) {
			throw new RepositoryException("Session has expired. Please log in again.");
		}
		
		ArrayList nodes = new ArrayList();
		try {
			javax.jcr.Node category = getCategoryNode(session,categoryId);
			if (category.hasProperty(JLibraryConstants.JLIBRARY_NODES)) {
				Value[] values = category.getProperty(
						JLibraryConstants.JLIBRARY_NODES).getValues();
				for (int i = 0; i < values.length; i++) {
					javax.jcr.Node node =
						session.getNodeByUUID(values[i].getString());
					try {
						if (!JCRSecurityService.canRead(
								node,ticket.getUser().getId())) {
							continue;
						}
					} catch (SecurityException se) {
						logger.error(se.getMessage(),se);
						continue;
					}
					javax.jcr.Node root = JCRUtils.getRootNode(session);
					nodes.add(
							JCRAdapter.createDocument(
									node,
									node.getParent().getUUID(),
									root.getUUID()));
				}		
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		return nodes;
	}
	
	/**
	 * Finds all the categories for a given node
	 * 
	 * @param ticket Ticket with user information
	 * @param nodeId Node
	 * 
	 * @return Catgories of that node
	 * 
	 * @throws RepositoryException If the categories can't be loaded
	 * @throws SecurityException If the user don't have enough permissions to load the categories
	 */	
	public List findCategoriesForNode(Ticket ticket, 
									  String nodeId) throws RepositoryException,
															SecurityException {

		javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
		if (session == null) {
			throw new RepositoryException("Session has expired. Please log in again.");
		}
		
		ArrayList categories = new ArrayList();
		try {
			javax.jcr.Node node = session.getNodeByUUID(nodeId);
			
			if (!JCRSecurityService.canRead(node, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			
			if (node.hasProperty(JLibraryConstants.JLIBRARY_CATEGORIES)) {
				Value[] values = node.getProperty(
						JLibraryConstants.JLIBRARY_CATEGORIES).getValues();
				for (int i = 0; i < values.length; i++) {
					try {
						javax.jcr.Node categoryNode = 
							getCategoryNode(session,values[i].getString());
						if (!JCRUtils.isActive(categoryNode)) {
							continue;
						}
						categories.add(JCRAdapter.createCategory(categoryNode));
					} catch (CategoryNotFoundException cnfe) {
						continue;
					}						
				}		
			}
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		return categories;
	}

	
	/**
	 * Returns a list with all the categories in a repository
	 * 
	 * @param Ticket ticket with user information
	 * 
	 * @return List with all the categories of the repository
	 * 
	 * @throws RepositoryException If the categories list can't be loaded
	 */	
	public List findAllCategories(Ticket ticket) throws RepositoryException {

		List categories = new ArrayList();
		
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}

			javax.jcr.Node systemNode = JCRUtils.getSystemNode(session); 
			javax.jcr.Node categoriesNode = systemNode.getNode(
					JLibraryConstants.JLIBRARY_CATEGORIES); 
			
			NodeIterator it = categoriesNode.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node node = (javax.jcr.Node) it.next();
				if (!JCRUtils.isActive(node)) {
					continue;
				}
				categories.add(JCRAdapter.createCategory(node));
			}
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		
		return categories;

	}
	
	public Category createCategory(Ticket ticket, 
	        					   CategoryProperties categoryProperties) 
										throws CategoryAlreadyExistsException,
											   RepositoryException,
	        					  			   SecurityException {

		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}

			javax.jcr.Node parentNode = null;
			
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			if (!JCRSecurityService.canWrite(root, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
						
			String parentId = null;
			PropertyDef propParent = categoryProperties.getProperty(
					CategoryProperties.CATEGORY_PARENT);
			javax.jcr.Node systemNode = JCRUtils.getSystemNode(session);
			if (propParent.getValue() != null) {
				parentId = (String)propParent.getValue();
				try {
					parentNode = getCategoryNode(session,parentId);
				} catch (CategoryNotFoundException e) {
					parentNode = systemNode.getNode(
							JLibraryConstants.JLIBRARY_CATEGORIES); 
				}
			} else {
				parentNode = systemNode.getNode(
						JLibraryConstants.JLIBRARY_CATEGORIES); 
			}
			String name = (String)categoryProperties.getProperty(
					CategoryProperties.CATEGORY_NAME).getValue();
			
			
			try {
				if (findCategoryByName(ticket,
									   name) != null) {
					throw new CategoryAlreadyExistsException();
				}
			} catch (CategoryNotFoundException e) {}
			
			/*
			//TODO: jLibrary 1.1 stores categories with a generic name. Add code to migrate 
			// old repositories replacing this categories generic name with the concrete name 
			// of the category. Having categories named by its real name makes easier to look 
			// categories by path
			String escapedName = 
				JCRUtils.buildValidChildNodeName(parentNode,null,name);
			*/
			javax.jcr.Node categoryNode = 
				parentNode.addNode(JLibraryConstants.JLIBRARY_CATEGORY,
								   JLibraryConstants.CATEGORY_MIXIN);
			
			categoryNode.addMixin(JCRConstants.JCR_REFERENCEABLE);
			categoryNode.setProperty(JLibraryConstants.JLIBRARY_NAME,name);
			categoryNode.setProperty(
					JLibraryConstants.JLIBRARY_DESCRIPTION,
					(String)categoryProperties.getProperty(
							CategoryProperties.CATEGORY_DESCRIPTION).getValue());
			
			Calendar date = Calendar.getInstance();
			date.setTime(new Date());
			categoryNode.setProperty(JLibraryConstants.JLIBRARY_DATE,date);
			categoryNode.setProperty(JLibraryConstants.JLIBRARY_NODES, new Value[]{});
			if (ticket.isAutocommit()) {
				session.save();
			}
			
			return JCRAdapter.createCategory(categoryNode);
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	
	public Category findCategoryById(Ticket ticket,
									 String id) 
										throws CategoryNotFoundException,
											   RepositoryException {


		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}

			javax.jcr.Node category = getCategoryNode(session,id);
			
			return JCRAdapter.createCategory(category);
		} catch (ItemNotFoundException infe) {
			throw new CategoryNotFoundException();
		} catch (InvalidQueryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	/**
	 * Deletes a category
	 * 
	 * @param ticket Ticket with user information
	 * @param categoryId if of the category to be deleted
	 * 
	 * @throws RepositoryException If the category can't be deleted
	 * @throws SecurityException if the user don't have enough rights to delete a category
	 */	
	public void deleteCategory(Ticket ticket, 
							   String categoryId) 
										throws RepositoryException, 
											   SecurityException {
		
		javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
		if (session == null) {
			throw new RepositoryException("Session has expired. Please log in again.");
		}
		
		try {
			
			javax.jcr.Node root = JCRUtils.getRootNode(session);
			if (!JCRSecurityService.canWrite(root, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}			
			
			javax.jcr.Node category = getCategoryNode(session,categoryId);
			removeReferences(ticket,category);
			category.remove();	
			if (ticket.isAutocommit()) {
				session.save();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		
	}

	private void removeReferences(Ticket ticket,
								  javax.jcr.Node category) 
										throws javax.jcr.RepositoryException {
		
		String categoryId = category.getUUID();
		PropertyIterator it = category.getReferences();
		while (it.hasNext()) {
			Property property = (Property) it.next();
			Value[] values = property.getValues();
			for (int i = 0; i < values.length; i++) {
				String uuid = values[i].getString();
				if (uuid.equals(categoryId)) {
					values[i] = null;
				}
			}
			property.setValue(values);
			if (values.length == 1) {
				// There were only one node. So we must add this node to the 
				// unknown Category
				javax.jcr.Node referencedNode = property.getParent();
				addCategory(ticket,
							findUnknownCategory(ticket),
							referencedNode);
			}
		}
		
		NodeIterator nodeIterator = category.getNodes();
		while (nodeIterator.hasNext()) {
			javax.jcr.Node child = nodeIterator.nextNode();
			if (child.isNodeType(JLibraryConstants.CATEGORY_MIXIN)) {
				removeReferences(ticket,child);
			}
		}
	}

	
	public javax.jcr.Node findUnknownCategory(Ticket ticket) 
											throws javax.jcr.RepositoryException {

		javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);

		return findUnknownCategory(session);
	}
	
	
	public javax.jcr.Node findUnknownCategory(javax.jcr.Session session) 
											throws javax.jcr.RepositoryException {

		javax.jcr.Node systemNode = JCRUtils.getSystemNode(session); 

		javax.jcr.Node unknownCategory = systemNode.getProperty(
				JLibraryConstants.JLIBRARY_UNKNOWN_CATEGORY).getNode();

		return unknownCategory;
	}	
	
	public javax.jcr.Node getCategoryNode(
			javax.jcr.Session session, 
			String categoryId) throws CategoryNotFoundException, 
									  RepositoryException {

		try {
			if (categoryId.equals(Category.UNKNOWN.getId())) {
				return findUnknownCategory(session);
			} else {
				return session.getNodeByUUID(categoryId);
			}
		} catch (javax.jcr.ItemNotFoundException infe) {
			throw new CategoryNotFoundException();
		} catch (javax.jcr.RepositoryException e) {
			if (e.getMessage().startsWith("Invalid UUID")) {
				throw new CategoryNotFoundException();
			}
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}			
	}	
	
	public void addUnknownCategory(Ticket ticket, 
								   javax.jcr.Node node) 
										throws javax.jcr.RepositoryException {
		
		javax.jcr.Node unknownCategory = 
			findUnknownCategory(ticket);
		addCategory(ticket,unknownCategory,node);
	}
	
	public void addCategory(Ticket ticket,
							javax.jcr.Node category,
			   				javax.jcr.Node node) 
										throws javax.jcr.RepositoryException {

		JCRUtils.addNodeToProperty(
				category.getUUID(),
				node,
				JLibraryConstants.JLIBRARY_CATEGORIES);
		JCRUtils.addNodeToProperty(
				node.getUUID(),
				category,
				JLibraryConstants.JLIBRARY_NODES);
	}	
	
	public void removeCategory(Ticket ticket,
							   javax.jcr.Node category,
							   javax.jcr.Node node) 
										throws javax.jcr.RepositoryException {

		JCRUtils.removeNodeFromProperty(
				category.getUUID(),
				node,
				JLibraryConstants.JLIBRARY_CATEGORIES);
		JCRUtils.removeNodeFromProperty(
				node.getUUID(),
				category,
				JLibraryConstants.JLIBRARY_NODES);

	}	
	
	public boolean containsUnknownCategory(Ticket ticket,
										   javax.jcr.Node node) 
										throws javax.jcr.RepositoryException {
		
		javax.jcr.Node unknownCategory = findUnknownCategory(ticket);
		Value[] values = 
			node.getProperty(JLibraryConstants.JLIBRARY_CATEGORIES).getValues();
		for (int i = 0; i < values.length; i++) {
			javax.jcr.Node reference = 
				node.getSession().getNodeByUUID(values[i].getString());
			if (reference.equals(unknownCategory)) {
				return true;
			}
		}
		return false;
	}
	
	public void removeUnknownCategory(Ticket ticket,
			   						  javax.jcr.Node node) 
										throws javax.jcr.RepositoryException {

		javax.jcr.Node unknownCategory = findUnknownCategory(ticket);
		removeCategory(ticket,unknownCategory,node);
	}	
	
	public int numberOfCategories(javax.jcr.Node node) 
										throws javax.jcr.RepositoryException {
		
		Value[] values = 
			node.getProperty(JLibraryConstants.JLIBRARY_CATEGORIES).getValues();
		return values.length;
	}
	
	/**
	 * Loads a category by path
	 * 
	 * @param ticket Ticket with user session information
	 * @param path Category's path
	 * 
	 * @return Category Category 
	 * 
	 * @throws RepositoryException If there is any problem loading the category
	 * @throws CategoryNotFoundException If the category does not exist
	 * @throws SecurityException If the user does not have enough rights to load the category
	 */
	public Category findCategoryByPath(Ticket ticket, 
			 			 	   		   String path) throws RepositoryException, 
			 				   			   		   		   CategoryNotFoundException, 
			 				   			   		   		   SecurityException {

		//TODO: Add security check on this method
		
		SessionManager manager = SessionManager.getInstance();
		Session session = manager.getSession(ticket);
		if (!path.startsWith(JLibraryConstants.JLIBRARY_SYSTEM)) {
			if (path.charAt(0) == '/') {
				path = path.substring(1,path.length());
			}
			path = JLibraryConstants.JLIBRARY_SYSTEM + "/" + 
				   JLibraryConstants.JLIBRARY_CATEGORIES + "/" + path;
		}
		try {
			javax.jcr.Node node = session.getRootNode().getNode(path);
			return JCRAdapter.createCategory(node);
		} catch (PathNotFoundException pnfe) {
			logger.error("Category with path [" + path + "] not found");
			throw new CategoryNotFoundException();
		} catch (ItemNotFoundException infe) {
			logger.error(infe.getMessage(),infe);
			throw new CategoryNotFoundException();
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}		
	}	
}