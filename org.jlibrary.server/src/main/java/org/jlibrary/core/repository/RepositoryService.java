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
package org.jlibrary.core.repository;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Favorite;
import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.locking.ResourceLockedException;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.RepositoryProperties;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.repository.exception.AuthorAlreadyExistsException;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.repository.exception.CategoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;
import org.jlibrary.core.repository.exception.NodeNotFoundException;
import org.jlibrary.core.repository.exception.RepositoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;

/**
 * @author martin
 *
 * Common interface for a repository implementation
 */
public interface RepositoryService {

	/**
	 * It creates a document repository. This method will create repository directory
	 * and corresponding entries into database
	 * 
	 * @param ticket Ticket with user information
	 * @param name Name of the repository
	 * @param description Description of the repository
	 * @param creator Creador del repositorio
	 * 
	 * @throws RepositoryException if the repository can't be created
	 * @throws SecurityException if the user don't have enough permissions to create the directory
	 */	
	public Repository createRepository(Ticket ticket,
	        						   String name,
									   String description,
									   User creator) 
		throws RepositoryAlreadyExistsException, 
			   RepositoryException,
			   SecurityException;

	/**
	 * Creates a directory under another directory
	 * 
	 * @param ticket Ticket with user information
	 * @param name Name of the directory
	 * @param description Description of the directory
	 * @param parentId Parent directory id
	 * 
	 * @return Directory new created directory
	 * 
	 * @throws PersistenceException If directory can't be created
	 * @throws SecurityException if the user don't have enough permissions to create the directory
	 * 
	 * @deprecated Use createDirectory(Ticket ticket, DirectoryProperties properties) instead
	 */
	public Directory createDirectory(Ticket ticket,
	        						 String name,
									 String description,
									 String parentId) throws RepositoryException, SecurityException;
	
	/**
	 * Creates a directory under another directory
	 * 
	 * @param ticket Ticket with user information
	 * @param properties Properties of the directory
	 * 
	 * @return Directory new created directory
	 * 
	 * @throws PersistenceException If directory can't be created
	 * @throws SecurityException if the user don't have enough permissions to create the directory
	 */
	public Directory createDirectory(Ticket ticket,
	        						 DirectoryProperties properties) throws RepositoryException, SecurityException;
	
	/**
	 * Removes a directory 
	 * 
	 * @param ticket Ticket with user information
	 * @param directoryId Id of the directory to be removed
	 * 
	 * @throws PersistenceException If directory can't be removed
	 * @throws SecurityException if the user don't have enough permissions to delete the directory
	 */
	public void removeDirectory(Ticket ticket, String directoryId) throws RepositoryException,
																		  SecurityException;

	/**
	 * Returns a list with all repositories existing in the database
	 * 
	 * @param ticket Ticket with user information
	 * 
	 * @return List with all repositories. This list is formed by RepositoryInfo instances
	 * 
	 * @throws RepositoryException If repositories can't be fetched
	 */
	public List findAllRepositoriesInfo(Ticket ticket) 
											throws RepositoryException;

	/**
	 * Loads a repository given a id
	 * 
	 * @param id Identificator of the repository
	 * @param ticket Ticket with user information
	 * 
	 * @return Repository repository or null if doesn't exists
	 * 
	 * @throws RepositoryNotFoundException If the repository id can't be found
	 * @throws RepositoryException If repository can't be loaded
	 * @throws SecurityException If the user don't have enough permissions 
	 * to open the repository 
	 */
	public Repository findRepository(String id, 
									 Ticket ticket) 
											throws RepositoryNotFoundException, 
										   	   	   RepositoryException, 
										   	   	   SecurityException;

	

	/**
	 * Deletes a document repository
	 * 
	 * @param ticket Ticket with user and repository id information
	 * 
	 * @throws RepositoryException if the repository can't be deleted
	 * @throws SecurityException if the user don't have enough permissions to delete the repository
	 */
	public void deleteRepository(Ticket ticket) throws RepositoryException,
													   SecurityException;

	/**
	 * Copies a directory into a destination directory
	 * 
	 * @param ticket Ticket with user information
	 * @param sourceId Id of the source directory 
	 * @param destinationId Id of the Destination of the source directory copy
	 * @param destinationRepository Destination repository
	 * 
	 * @return Directory new created directory
	 * 
	 * @throws PersistenceException if the directory can't be copied
	 * @throws SecurityException if the user don't have enough permissions to copy the directory
	 */
	public Directory copyDirectory( Ticket ticket,
							   		String sourceId, 
									String destinationId,
									String destinationRepository) 
												throws RepositoryException,
													   SecurityException;

	/**
	 * Copies a document into a destination directory
	 * 
	 * @param ticket Ticket with user information
	 * @param sourceId Id of the source document
	 * @param destinationId Id of the destination directory for the source document copy
	 * @param destinationRepository Destination repository
	 * 
	 * @return New create document
	 * 
	 * @throws RepositoryException if the document can't be copied
	 * @throws SecurityException if the user don't have enough permissions to copy the document
	 */
	public Document copyDocument( Ticket ticket,
							  	  String sourceId, 
								  String destinationId,
								  String destinationRepository)	
												throws RepositoryException,
								  					   SecurityException;

	/**
	 * Moves a directory to a new directory. 
	 * 
	 * @param ticket Ticket with user information
	 * @param sourceId Id of the source directory
	 * @param destination Id of the destination directory
	 * @param destinationRepository Destination repository
	 * 
	 * @return Directory New created directory
	 * 
	 * @throws RepositoryException If source directory can't be moved
	 * @throws SecurityException if the user don't have enough permissions to move the directory
	 */
	public Directory moveDirectory( Ticket ticket,
							   		String sourceId, 
									String destinationId,
									String destinationRepository) 
												throws RepositoryException,
													   SecurityException;
	

	/**
	 * Moves a document to a new directory. The directory can be in the same repository 
	 * of the document or in a different repository
	 * 
	 * @param ticket Ticket with user information
	 * @param documentId Id of the document to be moved
	 * @param directoryId Id of the destination directory
	 * @param destinationRepository Destination repository
	 * 
	 * @return Document document with updated data
	 * 
	 * @throws RepositoryException If document can't be moved
	 * @throws SecurityException if the user don't have enough permissions to move the document
	 */
	public Document moveDocument( Ticket ticket,
								  String documentId, 
								  String directoryId,
								  String destinationRepository) 
											throws RepositoryException, 
												   SecurityException;


	/**
	 * Adds a document to a repository in a given directory
	 * 
	 * @param ticket Ticket with user information
	 * @param docProperties Properties of the document
	 * 
	 * @return Document New created document
	 * 
	 * @throws RepositoryException If the document can't be added to the repository
	 * @throws SecurityException If the user don't have enough permisssions to add a document to the repository
	 */
	public Document createDocument( Ticket ticket,
									DocumentProperties docProperties) throws RepositoryException,
										 	 									 	  SecurityException;
	
	/**
	 * <p>Creates several documents at once. This method receives a list with 
	 * DocumentProperties and it will create all the documents. This method 
	 * will work like calling createDocument method several times.</p>
	 * 
	 * <p>It will return a list with all the created documents.</p>
	 * 
	 * @param ticket Ticket with user information
	 * @param properties List with the properties of all the documents that we 
	 * want to create
	 * 
	 * @return List List with all the created documents.
	 * 
	 * @throws RepositoryException If the documents cannot be created. Note that
	 * if a single document cannot be created, the exception will be thrown and 
	 * none of them will be created.
	 * @throws SecurityException If the user does not have enough rights to 
	 * perform the operation
	 */
	public List createDocuments(Ticket ticket, 
				List properties) 
							throws RepositoryException, 
								   SecurityException;	
	/**
	 * Removes a document from repository
	 * 
	 * @param ticket Ticket with user information 
	 * @param docId Id of the document to be removed
	 * 
	 * @throws RepositoryException if the document can't be deleted
	 * @throws SecurityException if the user don't have enough permissions to delete the document
	 * @throws ResourceLockedException if the resource is locked
	 */
	public void removeDocument(Ticket ticket,
	        				   String docId) throws RepositoryException, 
	        				   						SecurityException, 
	        				   						ResourceLockedException;

	
	/**
	 * Loads a document contents
	 * 
	 * @param docId Document to be loaded
	 * @param ticket Ticket with user information
	 * 
	 * @return byte[] Document's contents
	 * 
	 * @throws RepositoryException If the document's contents can't be loaded
	 * @throws SecurityException If the user don't have enough permissions to download the document
	 */
	public byte[] loadDocumentContent(String docId, Ticket ticket) throws RepositoryException, 
																		  SecurityException;

	/**
	 * Loads a document contents
	 * 
	 * @param docId Document to be loaded
	 * @param ticket Ticket with user information
	 * @param stream Stream to store contents
	 * 
	 * @throws RepositoryException If the document's contents can't be loaded
	 * @throws SecurityException If the user don't have enough permissions to download the document
	 */
	public void loadDocumentContent(String docId, 
									Ticket ticket,
									OutputStream stream) throws RepositoryException, 
																SecurityException;	
	/**
	 * Loads a node given a id
	 * 
	 * @param id Identificator of the document
	 * 
	 * @return Node Node
	 * 
	 * @throws RepositoryException If document can't be loaded
	 * @throws NodeNotFoundException if the node can't be found
	 * @throws SecurityException if the user don't have enough permissions to find the document info
	 */
	public Node findNode(Ticket ticket, String id) throws RepositoryException, 
												   		  NodeNotFoundException,
												   		  SecurityException;	
	
	/**
	 * Loads a document given a id
	 * 
	 * @param id Identificator of the document
	 * 
	 * @return Document document or null if doesn't exists
	 * 
	 * @throws RepositoryException If document can't be loaded
	 * @throws NodeNotFoundException if the document' can't be found
	 * @throws SecurityException if the user don't have enough permissions to find the document info
	 */
	public Document findDocument(Ticket ticket, String id) throws RepositoryException, 
												   				  NodeNotFoundException,
												   				  SecurityException;	

	/**
	 * Returns a list with the repository documents that have a common name
	 * 
	 * @param ticket Ticket with user information
	 * @param name Name we want to search. The search operation will be case 
	 * sensitive.
	 * 
	 * @return Collection List with documents with that name or an empty list if
	 * no documents can be found
	 * 
	 * @throws RepositoryException If some error happens when performing the 
	 * operation
	 */
	public Collection findDocumentsByName(Ticket ticket, 
										  String name) throws RepositoryException; 
										 					  
	
	/**
	 * Loads a directory given a id
	 * 
	 * @param id Identificator of the document
	 * 
	 * @return Directory directory or null if doesn't exists
	 * 
	 * @throws RepositoryException If document can't be loaded
	 * @throws NodeNotFoundException if the document' can't be found
	 * @throws SecurityException if the user don't have enough permissions to find the document info
	 */
	public Directory findDirectory(Ticket ticket, String id) throws RepositoryException, 
												   				  	NodeNotFoundException,
												   				  	SecurityException;	
    
	/**
	 * @param ticket Ticket with user information
	 * @param docProperties updated properties
	 * 
	 * @return Document updated document instance
	 * 
	 * @throws RepositoryException if the document can't be updated
	 * @throws SecurityException If the user don't have enough permissions to update the document
	 * @throws ResourceLockedException If the document is locked by another user
	 */
	public Document updateDocument(Ticket ticket,
			   				   	   DocumentProperties docProperties) throws RepositoryException, 
								   						 					SecurityException,
								   						 					ResourceLockedException;

	/**
	 * Updates directory properties
	 * 
	 * @param ticket Ticket with user information
	 * @param directoryProperties Directory properties to be updated
	 * 
	 * @return Directory updated directory
	 * 
	 * @throws RepositoryException if some error happens when updating the directory
	 * @throws SecurityException if the user don't have enough permissions to update the directory
	 */
	public Directory updateDirectory(Ticket ticket,
									 DirectoryProperties directoryProperties) throws RepositoryException,
													  								 SecurityException;

	/**
	 * Updates repository properties
	 * 
	 * @param ticket Ticket with user information
	 * @param repositoryProperties Repository properties to be updated
	 * 
	 * @return Repository updated repository
	 * 
	 * @throws RepositoryException if some error happens when updating the repository
	 * @throws SecurityException if the user don't have enough permissions to update the repository
	 */
	public Repository updateRepository(Ticket ticket,
									   RepositoryProperties repositoryProperties) throws RepositoryException,
													  								     SecurityException;
	
	/**
	 * Renames a node
	 * 
	 * @param ticket Ticket with user information
	 * @param nodeId Id of the node to be renamed
	 * @param name New name for the node
	 * 
	 * @throws RepositoryException If the rename can't be performed
	 * @throws SecurityException if the user don't have enough permissions to rename the node
	 */	
	public void renameNode(Ticket ticket,
						   String nodeId, 
						   String name) throws RepositoryException, 
						   					   SecurityException;
	
	/**
	 * Returns a list with all the authors registered into repository
	 * 
	 * @param Ticket ticket with user and repository id information
	 * 
	 * @return List authors list
	 * 
	 * @throws RepositoryException If the authors list can't be loaded
	 */
	public List findAllAuthors(Ticket ticket) throws RepositoryException;
	
	/**
	 * Returns an Author given a name
	 * 
	 * @param Ticket ticket with user information
	 * @param name Name of the author
	 * 
	 * @return Author with the given name
	 * 
	 * @throws AuthorNotFoundException If an author with that name cannot be 
	 * found
	 * @throws RepositoryException If the author can't be loaded 
	 */
	public Author findAuthorByName(Ticket ticket, 
								   String name) throws AuthorNotFoundException,
								   					   RepositoryException;
	
	/**
	 * Returns an author given its id
	 * 
	 * @param Ticket ticket with user information
	 * @param id Id of the author
	 * 
	 * @return Author with the given id
	 * 
	 * @throws AuthorNotFoundException If an author with that id cannot be found
	 * @throws RepositoryException If an error happens
	 */
	public Author findAuthorById(Ticket ticket, 
								 String id) throws AuthorNotFoundException,
								 				   RepositoryException;
	
	/**
	 * Returns a list with all the categories in a repository
	 * 
	 * @param Ticket ticket with user and repository id information
	 * 
	 * @return List with all the categories of the repository
	 * 
	 * @throws RepositoryException If the categories list can't be loaded
	 */
	public List findAllCategories(Ticket ticket) throws RepositoryException;
	
	/**
	 * Creates a new category
	 * 
	 * @param ticket Ticket with user information
	 * @param categoryProperties properties for creating the category
	 * 
	 * @return Category created
	 * 
	 * @throws CategoryAlreadyExistsException If the category already exists
	 * @throws RepositoryException If the category can't be created
	 * @throws SecurityException if the user don't have enough rights to create a category
	 */
	public Category createCategory(Ticket ticket,
	        					   CategoryProperties categoryProperties) 
										throws CategoryAlreadyExistsException,
											   RepositoryException,
	        					   			   SecurityException;
	
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
	        				   String categoryId) throws RepositoryException,
														 SecurityException;
	
	/**
	 * Updates a category
	 * 
	 * @param ticket Ticket with user information
	 * @param categoryId id of the category we want to update
	 * @param categoryProperties properties for updating the category
	 *  
	 * @return Categoy Updated category
	 *  
	 * @throws CategoryNotFoundException If the category cannot be found
	 * @throws RepositoryException If the category can't be updated
	 * @throws SecurityException if the user don't have enough rights to update a category
	 */
	public Category updateCategory(Ticket ticket, 
	        				   	   String categoryId,
	        				   	   CategoryProperties categoryProperties) 
											throws CategoryNotFoundException,
												   RepositoryException,
												   SecurityException;
	
	/**
	 * Finds a category given its id
	 * 
	 * @param Ticket ticket with user information
	 * @param id Category's id
	 * 
	 * @return Category with that id
	 * 
	 * @throws CategoryNotFoundException If the category can't be found
	 * @throws RepositoryException If some error happens looking for the 
	 * category
	 */
	public Category findCategoryById(Ticket ticket,
									 String id) throws CategoryNotFoundException,
									 				   RepositoryException;
	
	/**
	 * Finds a category given its name
	 * 
	 * @param Ticket ticket with user and repository id information
	 * @param name Category's name
	 * 
	 * @return Category with that name
	 * 
	 * @throws CategoryNotFoundException If the category can't be found
	 * @throws RepositoryException If some error happens looking for the 
	 * category
	 */
	public Category findCategoryByName(Ticket ticket,
									   String name) 
											throws CategoryNotFoundException,
												   RepositoryException;
	
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
															 SecurityException;

	
	/**
	 * Delete a favorite given its id
	 * 
	 * @param ticket Ticket with user and repository id information
	 * @param favoriteId Id of the favorite to be deleted
	 * 
	 * @throws RepositoryException If the favorite can't be deleted
	 * @throws SecurityException if the user don't have enough rights to delete a favorite
	 */
	public void deleteFavorite(Ticket ticket, 
							   String favoriteId) throws RepositoryException,
														 SecurityException;
	
	/**
	 * Adds a bookmark to the list of user bookmarks
	 * 
	 * @param ticket Ticket with user information
	 * @param bookmark New bookmark
	 * 
	 * @return New bookmark created
	 * 
	 * @throws RepositoryException If the bookmark can't be created
	 */
	public Bookmark createBookmark(Ticket ticket, Bookmark bookmark) throws RepositoryException;
	
	/**
	 * Creates a new author
	 * 
	 * @param ticket Ticket with user information
	 * @param properties Author's properties
	 * 
	 * @return New created author
	 * 
	 * @throws RepositoryException If the author can't be created
	 * @throws SecurityException If the user doesn't have enough rights to
	 * perform this operation
	 * @throws AuthorAlreadyExistsException if an author with this name 
	 * already exists 
	 */
	public Author createAuthor(Ticket ticket,
							   AuthorProperties properties) 
											throws RepositoryException,
							   					   SecurityException,
							   					   AuthorAlreadyExistsException;
	
	/**
	 * Updates author information
	 * 
	 * @param ticket Ticket with user information
	 * @param authorId Id of the repository
	 * @param properties Author's properties
	 * 
	 * @throws RepositoryException If the author can't be updated
	 * @throws SecurityException If the user doesn't have enough rights to 
	 * perform this operation
	 * @throws AuthorNotFoundException If the author cannot be found
	 */
	public void updateAuthor(Ticket ticket,
							 String authorId,
							 AuthorProperties properties) 
											throws RepositoryException,
							   					   SecurityException,
							   					   AuthorNotFoundException;	
	
	/**
	 * Deletes an author
	 * 
	 * @param ticket Ticket with user information
	 * @param authorId Id of the author 
	 * 
	 * @throws RepositoryException If the author can't be removed
	 * @throws SecurityException If the user doesn't have enough rights to 
	 * perform this operation
	 * @throws AuthorNotFoundException If the author cannot be found
	 */
	public void deleteAuthor(Ticket ticket,
							 String authorId) throws RepositoryException,
							   					   	 SecurityException,
							   					   	 AuthorNotFoundException;
	
	/**
	 * Removes an user's bookmark
	 * 
	 * @param ticket Ticket with user information
	 * @param bookmarkId Id of the bookmark to be removed
	 * 
	 * @throws RepositoryException If the bookmark can't be removed
	 */
	public void removeBookmark(Ticket ticket, 
							   String bookmarkId) throws RepositoryException;
	
	/**
	 * Updates a bookmark
	 * 
	 * @param Ticket ticket with user information
	 * @param bookmark Bookmark object with the contents to be updated
	 *
	 * @return Bookmark Bookmark object with the updated contents
	 * 
	 * @throws RepositoryException If the bookmark can't be updated
	 */
	public Bookmark updateBookmark(Ticket ticket, Bookmark bookmark) throws RepositoryException;
	
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
	public List findCategoriesForNode(Ticket ticket, String nodeId) throws RepositoryException,
																		   SecurityException;
	
	/**
	 * Finds all the nodes within a category
	 * 
	 * @param ticket Ticket with user information
	 * @param categoryId category
	 * 
	 * @return Nodes of that category
	 * 
	 * @throws CategoryNotFoundException If the category cannot be found
	 * @throws RepositoryException If the categories can't be loaded
	 */
	public List findNodesForCategory(Ticket ticket, 
									 String categoryId) 
											throws CategoryNotFoundException,
												   RepositoryException;
	
	/**
	 * Exports a repository for local storage
	 * 
	 * @param ticket Ticket with user information
	 * 
	 * @return A byte array with repository contents and metadata
	 * 
	 * @throws RepositoryNotFoundException If the repository id can't be found
	 * @throws RepositoryException If the repository can't be exported
	 * @throws SecurityException If the user don't have enough permissions to export a repository
	 */
	public byte[] exportRepository(Ticket ticket) 
											throws RepositoryNotFoundException,
												   RepositoryException,
												   SecurityException;
	
	/**
	 * Exports a repository for local storage
	 * 
	 * @param ticket Ticket with user information
	 * @param OutputStream Stream that will be used to save information
	 * 
	 * @throws RepositoryNotFoundException If the repository id can't be found
	 * @throws RepositoryException If the repository can't be exported
	 * @throws SecurityException If the user don't have enough permissions to export a repository
	 */
	public void exportRepository(Ticket ticket, 
								 OutputStream stream) 
									throws RepositoryNotFoundException, 
										   RepositoryException, 
										   SecurityException;	
	/**
	 * Imports a repository from local storage
	 * 
	 * @param ticket Ticket with user information
	 * @param content A byte array with repository contents and metadata
	 * @param name Name for the repository
	 * 
	 * @throws RepositoryException If the repository can't be imported
	 * @throws SecurityException If the user don't have enough permissions to import the repository
	 */
	public void importRepository(Ticket ticket, 
								 byte[] content,
								 String name) throws RepositoryAlreadyExistsException,
								 					 RepositoryException,
													 SecurityException;
	
	/**
	 * Imports a repository from local storage
	 * 
	 * @param ticket Ticket with user information
	 * @param name Name for the repository
	 * @param stream Repository contents/metadata input stream
	 * 
	 * @throws RepositoryException If the repository can't be imported
	 * @throws SecurityException If the user don't have enough permissions to import the repository
	 */
	public void importRepository(Ticket ticket,
								 String name,
								 InputStream stream) throws RepositoryAlreadyExistsException,
								 					 		RepositoryException,
								 					 		SecurityException;	
	
	/**
	 * Updates the content of the given node (resource or document) through the given input stream
	 * 
	 * @param ticket Ticket with user information
	 * @param docId Node to update
	 * @param stream Node contents stream
	 * 
	 * @return Node Updated node
	 * 
	 * @throws SecurityException If the user don't have enough permissions to import the repository
	 * @throws RepositoryException If the contents cannot be udpated
	 */
	public Node updateContent(Ticket ticket,
							  String docId,
							  InputStream stream) throws SecurityException, RepositoryException;

	/**
	 * Updates the content of the given node (resource or document) through the given input stream
	 * 
	 * @param ticket Ticket with user information
	 * @param docId Node to update
	 * @param byte[] Node contents
	 * 
	 * @return Node Updated node
	 * 
	 * @throws SecurityException If the user don't have enough permissions to import the repository
	 * @throws RepositoryException If the contents cannot be udpated
	 */
	public Node updateContent(Ticket ticket,
							  String docId,
							  byte[] content) throws SecurityException, RepositoryException;
	
	/**
	 * Loads the content of a document version
	 * 
	 * @param ticket Ticket with user information
	 * @param versionId Id of the version to load
	 * 
	 * @return byte[] Content of the version
	 * 
	 * @throws RepositoryException If the version can't be loaded
	 * @throws SecurityException If the user hasn't enough permissions to load the document contents
	 */
	public byte[] loadVersionContent(Ticket ticket, 
								     String versionId) throws RepositoryException,
	 														  SecurityException;	
	
	/**
	 * Loads the content of a document version
	 * 
	 * @param ticket Ticket with user information
	 * @param versionId Id of the version to load
	 * @param stream STream to store contents
	 * 
	 * @return Content of the version
	 * 
	 * @throws RepositoryException If the version can't be loaded
	 * @throws SecurityException If the user hasn't enough permissions to load the document contents
	 */
	public void loadVersionContent(Ticket ticket, 
								   String versionId,
								   OutputStream stream) throws RepositoryException,
	 														   SecurityException;

	/**
	 * Locks an specific document
	 * 
	 * @param ticket Ticket with user information
	 * @param docId Id of the document to be locked
	 * 
	 * @throws RepositoryException If the document can't be locked
	 * @throws SecurityException If the user doesn't have enough permissions to 
	 * lock the document. The user must have at least write permissions on the 
	 * document.
	 * @throws ResourceLockedException if the resource is already locked by another
	 * user.
	 */
	public Lock lockDocument(Ticket ticket,
							 String docId) throws RepositoryException,
							 					  SecurityException,
												  ResourceLockedException;
	
	/**
	 * Unlocks an specific document
	 * 
	 * @param ticket Ticket with user information
	 * @param docId Id of the document to be locked
	 * 
	 * @throws RepositoryException If the document can't be unlocked
	 * @throws SecurityException If the user doesn't have enough permissions to 
	 * unlock the document. The user must have at least write permissions on the 
	 * document.
	 */
	public void unlockDocument(Ticket ticket,
							   String docId) throws RepositoryException,
							 					  	SecurityException,
							 					  	ResourceLockedException;
	
	/**
	 * Finds all the resources locked on a repository
	 * 
	 * @param ticket Ticket with user and repository id information
	 * 
	 * @return All the locks in the repository
	 * 
	 * @throws RepositoryException If the resources can't be loaded
	 * @throws SecurityException If the user doesn't have rights to perform this operaions
	 */
	public List findAllLocks(Ticket ticket) throws RepositoryException, 
												   SecurityException;
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#createResource(org.jlibrary.core.entities.Ticket, org.jlibrary.core.properties.ResourceNodeProperties)
	 */
	public ResourceNode createResource(Ticket ticket, 
							   		   ResourceNodeProperties properties) throws RepositoryException,
							   		   											 SecurityException;
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#addResourceToDocument(org.jlibrary.core.entities.Ticket, org.jlibrary.core.entities.ResourceNode, org.jlibrary.core.entities.Document)
	 */
	public void addResourceToDocument(Ticket ticket,
			  						  String resourceId,
			  						  String documentId) throws RepositoryException,
			  													SecurityException;
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#findNodesForResource(org.jlibrary.core.entities.Ticket, java.lang.String)
	 */	
	public List findNodesForResource(Ticket ticket, 
									 String resourceId) throws RepositoryException;
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#loadResourceNodeContent(org.jlibrary.core.entities.Ticket, java.lang.String)
	 */	
	public byte[] loadResourceNodeContent(Ticket ticket,
			  							  String resourceId) throws RepositoryException, 
			  							  							SecurityException;	
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#loadResourceNodeContent(org.jlibrary.core.entities.Ticket, java.lang.String, java.io.OutputStream)
	 */	
	public void loadResourceNodeContent(Ticket ticket,
			  							String resourceId,
			  							OutputStream stream) throws RepositoryException, 
			  							  							SecurityException;		
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#updateResourceNode(org.jlibrary.core.entities.Ticket, org.jlibrary.core.properties.ResourceNodeProperties)
	 */	
	public ResourceNode updateResourceNode(Ticket ticket,
			   							   ResourceNodeProperties properties) throws RepositoryException, 
			   											 							 SecurityException;
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#removeResourceNode(org.jlibrary.core.entities.Ticket, java.lang.String)
	 */	
	public void removeResourceNode(Ticket ticket, 
								   String resourceId) throws RepositoryException,
								   							 SecurityException;
	
	/**
	 * @see org.jlibrary.core.repository.def.ResourcesModule#removeResourceNode(org.jlibrary.core.entities.Ticket, java.lang.String, java.lang.String)
	 */	
	public void removeResourceNode(Ticket ticket, 
								   String resourceId,
								   String docId) throws RepositoryException,
								   						SecurityException;

	public ResourceNode copyResource(Ticket ticket, 
							 		 String resourceId, 
							 		 String directoryId,
							 		 String destinationRepository) 
													throws RepositoryException, 
							 							   SecurityException;	
	
	
	public ResourceNode moveResource(Ticket ticket, 
							 		 String resourceId, 
							 		 String directoryId,
							 		 String destinationRepository) 
													throws RepositoryException, 
							 							   SecurityException;
	
	/**
	 * Copies a node into a destination directory
	 * 
	 * @param ticket Ticket with user information
	 * @param sourceId Id of the source document
	 * @param destinationId Id of the destination directory for the source document copy
	 * @param destinationRepository Destination repository
	 * 
	 * @return New created node
	 * 
	 * @throws RepositoryException if the node can't be copied
	 * @throws SecurityException if the user don't have enough permissions to copy the document
	 */
	public Node copyNode( Ticket ticket,
					  	  String sourceId, 
						  String destinationId,
						  String destinationRepository)	
										throws RepositoryException,
						  					   SecurityException;

	/**
	 * Moves a node into a destination directory
	 * 
	 * @param ticket Ticket with user information
	 * @param sourceId Id of the source document
	 * @param destinationId Id of the destination directory for the source document copy
	 * @param destinationRepository Destination repository
	 * 
	 * @return Moved node
	 * 
	 * @throws RepositoryException if the node can't be copied
	 * @throws SecurityException if the user don't have enough permissions to copy the document
	 */
	public Node moveNode( Ticket ticket,
					  	  String sourceId, 
						  String destinationId,
						  String destinationRepository)	
												throws RepositoryException,
						  							   SecurityException;
	
	/**
	 * Return document versions list. This will be a list with instances of 
	 * DocumentVersion entity. 
	 * 
	 * @param ticket Ticket with user information
	 * @param documentId Id of the document
	 * 
	 * @return List List with document versions
	 * 
	 * @throws RepositoryException If the list can't be obtained
	 * @throws SecurityException If the user does not have permissions to get 
	 * the document versions list
	 */
	public List getVersions(Ticket ticket, 
							String documentId) throws RepositoryException, 
									  				  SecurityException;
	

	/**
	 * Loads a node's children given a id
	 * 
	 * @param ticket Ticket with user information
	 * @param id Identificator of the document
	 * 
	 * @return Collection Collection with the node's children
	 * 
	 * @throws RepositoryException If document can't be loaded
	 * @throws NodeNotFoundException if the node can't be found
	 * @throws SecurityException if the user don't have enough permissions to find the document info
	 */
	public Collection findNodeChildren(Ticket ticket, 
									   String id) throws RepositoryException,
									   					 NodeNotFoundException, 
									   					 SecurityException;
	
	/**
	 * Forces a session save operation. Ideally this method would be used when 
	 * working with autocommit mode set to false
	 * 
	 * @param ticket Ticket with user information
	 * 
	 * @throws RepositoryException If the save operation cannot be performed
	 */
	public void saveSession(Ticket ticket) throws RepositoryException;
	
	/**
	 * Registers a custom property in the repository
	 * 
	 * @param ticket Session ticket
	 * @param property Property to register
	 * 
	 * @throws RepositoryException If the property cannot be registered
	 */
	public void registerCustomProperty(Ticket ticket, 
			   	CustomPropertyDefinition property) throws RepositoryException;

	/**
	 * Unregisters a property from the repository
	 * 
	 * @param ticket Session ticket
	 * @param property Property to unregister
	 * 
	 * @throws RepositoryException If the property cannot be unregistered
	 */
	public void unregisterCustomProperty(Ticket ticket, 
				CustomPropertyDefinition property) throws RepositoryException;

	/**
	 * Checks if a property exists in the repository
	 * 
	 * @param ticket Session ticket
	 * @param propertyName Name of the property to check
	 * 
	 * @return <code>true</code> if the property exists and <code>false</code> otherwise.
	 * 
	 * @throws RepositoryException If there is any error doing the checks
	 * 
	 * @deprecated Use {@link #isPropertyRegistered(Ticket, String, String)}
	 */
	public boolean isPropertyRegistered(Ticket ticket, String propertyName) throws RepositoryException;

	/**
	 * Checks if a property exists in the repository
	 * 
	 * @param ticket Session ticket
	 * @param uri URI for that property
	 * @param propertyName Name of the property to check
	 * 
	 * @return <code>true</code> if the property exists and <code>false</code> otherwise.
	 * 
	 * @throws RepositoryException If there is any error doing the checks
	 */	
	public boolean isPropertyRegistered(Ticket ticket, String uri, String propertyName) throws RepositoryException;

		
}

