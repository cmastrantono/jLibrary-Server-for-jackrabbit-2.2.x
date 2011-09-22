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



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.version.Version;

import org.apache.commons.lang.StringUtils;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.DocumentMetaData;
import org.jlibrary.core.entities.DocumentVersion;
import org.jlibrary.core.entities.Favorite;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.entities.Member;
import org.jlibrary.core.entities.Note;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.entities.Restriction;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.properties.RepositoryProperties;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapts JSR-170 objects to jlibrary objects
 * 
 * @author martin
 *
 */
public class JCRAdapter {

	static Logger logger = LoggerFactory.getLogger(JCRAdapter.class);
	
	public static Repository createRepository(Ticket ticket,
											  String repositoryName,
											  javax.jcr.Node node) 
												throws RepositoryException {


		Repository repository = new Repository();
		repository.setTicket(ticket);
		
		javax.jcr.Node systemNode = JCRUtils.getSystemNode(node.getSession());
		repository.setId(node.getUUID());
		repository.setName(node.getProperty(
			JLibraryConstants.JLIBRARY_NAME).getString());
		repository.setDescription(node.getProperty(
			JLibraryConstants.JLIBRARY_DESCRIPTION).getString());
	
		repository.setCategories(new HashSet());

		javax.jcr.Node categoriesNode = systemNode.getNode(
			JLibraryConstants.JLIBRARY_CATEGORIES); 
	
		NodeIterator it = categoriesNode.getNodes();
		while (it.hasNext()) {
			javax.jcr.Node childCategory = (javax.jcr.Node) it.next();
			if (!JCRUtils.isActive(childCategory)) {
				continue;
			}
			repository.getCategories().add(
				JCRAdapter.createCategory(childCategory));
		}
		// Get config entries
		Value physicalDeletes = JCRUtils.getConfigProperty(
				node.getSession(),
				RepositoryProperties.PHYSICAL_DELETE_DOCUMENTS);
		if (physicalDeletes != null) {
			repository.getRepositoryConfig().setPhysicalDeleteDocuments(
					physicalDeletes.getBoolean());
		}
		Value keywordsExtraction = JCRUtils.getConfigProperty(
				node.getSession(),
				RepositoryProperties.EXTRACT_DOCUMENT_METADATA);
		if (keywordsExtraction != null) {
			repository.getRepositoryConfig().setExtractMetadata(
					keywordsExtraction.getBoolean());
		}
		Value lazyLoading = JCRUtils.getConfigProperty(
				node.getSession(),
				RepositoryProperties.DO_LAZY_LOADING);
		if (lazyLoading != null) {
			repository.getRepositoryConfig().setEnabledLazyLoading(
					lazyLoading.getBoolean());
		}
		
		Directory root = createDirectory(node,
				 null,
				 repository.getId(),
				 ticket.getUser().getId(),
				 repository.getRepositoryConfig().isEnabledLazyLoading());
		root.setRepository(repository.getId());

		repository.setRoot(root);

		return repository;
	}	
	
	public static Directory createDirectory(javax.jcr.Node node,
											String parentId,
											String repositoryId,
											String memberId,
											boolean lazy) 
												throws RepositoryException {
		
		JCRCreationContext context = new JCRCreationContext();
		Directory directory = 
			internalCreateDirectory(node,
									parentId,
									repositoryId,
									context,
									memberId,
									lazy);
		context.clear();
		return directory;
	}
	
	
	public static Directory createDirectory(javax.jcr.Node node,
			String parentId,
			String repositoryId,
			String memberId) 
			throws RepositoryException
	{
		return createDirectory(node, 
							parentId,
							repositoryId,
							memberId,
							 false);
	}

	public static Directory internalCreateDirectory(javax.jcr.Node node,
													String parentId,
													String repositoryId,
													JCRCreationContext context,
													String memberId,
													boolean lazy) 
													throws RepositoryException {
		
		Directory directory = new Directory();

		if (node.isNodeType(JCRConstants.JCR_REFERENCEABLE)) {
			directory.setId(node.getUUID());
		} else {
			if (node.hasProperty(JCRConstants.JCR_UUID)) {
				directory.setId(node.getProperty(JCRConstants.JCR_UUID).getValue().getString());
			} else {
				directory.setId("temp://" + UUIDGenerator.generate(directory));
			}
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_NAME)) {
			directory.setName(node.getProperty(
					JLibraryConstants.JLIBRARY_NAME).
					getValue().getString());
		} else {
			directory.setName(node.getName());
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_DESCRIPTION)) {
			directory.setDescription(node.getProperty(
					JLibraryConstants.JLIBRARY_DESCRIPTION).
					getValue().getString());
		} else {
			directory.setDescription(node.getName());
		}
		
		directory.setParent(parentId);
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_CREATED)) {
			Calendar date = node.getProperty(JLibraryConstants.JLIBRARY_CREATED).getDate();
			directory.setDate(date.getTime());
		}
		if (node.hasProperty(JLibraryConstants.JLIBRARY_CREATOR)) {
			directory.setCreator(node.getProperty(
					JLibraryConstants.JLIBRARY_CREATOR).
					getString());			
		} else {
			directory.setCreator(User.ADMIN_CODE);
		}
		if (node.hasProperty(JLibraryConstants.JLIBRARY_IMPORTANCE)) {
			directory.setImportance(new Integer((int)node.getProperty(
					JLibraryConstants.JLIBRARY_IMPORTANCE).
					getLong()));			
		} else {
			directory.setImportance(org.jlibrary.core.entities.Node.IMPORTANCE_MEDIUM);
		}
		if (node.hasProperty(JLibraryConstants.JLIBRARY_TYPECODE)) {
			directory.setTypecode(new Integer((int)node.getProperty(
					JLibraryConstants.JLIBRARY_TYPECODE).
					getLong()));			
		} else {
			directory.setTypecode(Types.FOLDER);
		}

		if (node.hasProperty(JLibraryConstants.JLIBRARY_POSITION)) {
			directory.setPosition(new Integer((int)node.getProperty(
					JLibraryConstants.JLIBRARY_POSITION).
					getLong()));
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_PATH)) {
			/*
			directory.setPath(node.getProperty(
					JLibraryConstants.JLIBRARY_PATH).
					getString());
					*/
			String path = StringUtils.difference("/"+JLibraryConstants.JLIBRARY_ROOT,node.getPath());
			if (path.equals("")) {
				path = "/"; 
			}
			directory.setPath(path);			
		}
		
		directory.setJCRPath(node.getPath());
		directory.setNodes(new HashSet());
		directory.setRepository(repositoryId);
		directory.setSize(new BigDecimal(0));
		directory.setRestrictions(obtainRestrictions(node));
		
		if (node.hasNodes()) {
			directory.setHasChildren(Boolean.TRUE);
		}
		
		if (!lazy)
		{
			NodeIterator it = node.getNodes();
			while (it.hasNext()) {
				Node child = (Node)it.next();
				if (!JCRUtils.isActive(child)) {
					continue;
				}
				
				try {
					if ((memberId != null) &&
						!JCRSecurityService.canRead(child,memberId)) {
						continue;
					}
				} catch (SecurityException se) {
					logger.error(se.getMessage(),se);
					continue;
				}
				if (child.isNodeType(JLibraryConstants.RESOURCE_MIXIN)) {
					directory.getNodes().add(
							createResource(child,directory.getId(),repositoryId));
				} else if (child.isNodeType(JLibraryConstants.DIRECTORY_MIXIN)) {
					directory.getNodes().add(
							internalCreateDirectory(child,
													directory.getId(),
													repositoryId,
													context,
													memberId, lazy));
				} else if (child.isNodeType(JLibraryConstants.DOCUMENT_MIXIN)) {
					directory.getNodes().add(
							internalCreateDocument(child,
												   directory.getId(),
												   repositoryId,
												   context));
				}
			}
		}
		return directory;
	}
	
	public static ResourceNode createResource(javax.jcr.Node node,
										      String parentId,
										      String repositoryId) 
													throws RepositoryException {

		ResourceNode resource = new ResourceNode();

		if (node.isNodeType(JCRConstants.JCR_REFERENCEABLE)) {
			resource.setId(node.getUUID());
		} else {
			resource.setId(node.getProperty(JCRConstants.JCR_UUID).
					getValue().getString());
		}

		if (node.hasProperty(JLibraryConstants.JLIBRARY_NAME)) {
			resource.setName(node.getProperty(
					JLibraryConstants.JLIBRARY_NAME).
					getValue().getString());
		}
		if (node.hasProperty(JLibraryConstants.JLIBRARY_DESCRIPTION)) {
			resource.setDescription(node.getProperty(
					JLibraryConstants.JLIBRARY_DESCRIPTION).
					getValue().getString());
		}

		resource.setJCRPath(node.getPath());
		resource.setParent(parentId);

		if (node.hasProperty(JLibraryConstants.JLIBRARY_CREATED)) {
			Calendar date = node.getProperty(JLibraryConstants.JLIBRARY_CREATED).getDate();
			resource.setDate(date.getTime());
		}
		if (node.hasProperty(JLibraryConstants.JLIBRARY_CREATOR)) {
			resource.setCreator(node.getProperty(
					JLibraryConstants.JLIBRARY_CREATOR).
					getString());			
		}
		if (node.hasProperty(JLibraryConstants.JLIBRARY_IMPORTANCE)) {
			resource.setImportance(new Integer((int)node.getProperty(
					JLibraryConstants.JLIBRARY_IMPORTANCE).
					getLong()));			
		}
		if (node.hasProperty(JLibraryConstants.JLIBRARY_TYPECODE)) {
			resource.setTypecode(new Integer((int)node.getProperty(
					JLibraryConstants.JLIBRARY_TYPECODE).
					getLong()));			
		}

		if (node.hasProperty(JLibraryConstants.JLIBRARY_PATH)) {
			/*
			resource.setPath(node.getProperty(
					JLibraryConstants.JLIBRARY_PATH).
					getString());
			*/
			String path = StringUtils.difference("/"+JLibraryConstants.JLIBRARY_ROOT, node.getPath());
			resource.setPath(path);			
		}

		if (node.hasProperty(JLibraryConstants.JLIBRARY_SIZE)) {
			resource.setSize(new BigDecimal(node.getProperty(
					JLibraryConstants.JLIBRARY_SIZE).
					getLong()));			
		}
		
		resource.setNodes(new HashSet());
		resource.setRepository(repositoryId);
		resource.setRestrictions(obtainRestrictions(node));
		
		return resource;
	}

	public static Document createDocument(javax.jcr.Node node,
			  							  String parentId,
			  							  String repositoryId) 
													throws RepositoryException {
		
		JCRCreationContext context = new JCRCreationContext();
		Document document = 
			internalCreateDocument(node,parentId,repositoryId,context);
		
		context.clear();
		return document;
	}	
	
	private static void processRelations(Node node, 
										 Document document, 
										 String repositoryId,
										 JCRCreationContext context) 
													throws RepositoryException {

		// Handle relations
		document.setRelations(new HashSet());
		
		if (!node.hasProperty(JLibraryConstants.JLIBRARY_RELATIONS)) {
			return;
		}
		
		javax.jcr.Property relationsProperty = 
			node.getProperty(JLibraryConstants.JLIBRARY_RELATIONS);
		Value[] values = relationsProperty.getValues();
		for (int i = 0; i < values.length; i++) {
			String uuid = values[i].getString();
			Document relationDocument = (Document)context.getNode(uuid);			
			if (relationDocument == null) {
				javax.jcr.Node relationNode = 
					node.getSession().getNodeByUUID(uuid);
				relationDocument = 
					internalCreateDocument(relationNode,
										   relationNode.getParent().getUUID(),
										   repositoryId,
										   context);
				context.addNode(relationDocument);
			}
			
			document.getRelations().add(relationDocument);
		}		
	}

	public static Document internalCreateDocument(javax.jcr.Node node,
										  		  String parentId,
										  		  String repositoryId,
										  		  JCRCreationContext context)
													throws RepositoryException {
	
		Document document = (Document)context.getNode(node.getUUID());
		if (document != null) {
			return document;
		}
		// Not already generated
		document = new Document();
		
		DocumentMetaData metadata = new DocumentMetaData();
		document.setMetaData(metadata);
	
		if (node.isNodeType(JCRConstants.JCR_REFERENCEABLE)) {
			document.setId(node.getUUID());
		} else {
			document.setId(node.getProperty(JCRConstants.JCR_UUID).
					getValue().getString());
		}
	
		if (node.hasProperty(JLibraryConstants.JLIBRARY_NAME)) {
			document.setName(node.getProperty(
					JLibraryConstants.JLIBRARY_NAME).
					getValue().getString());
		}
		if (node.hasProperty(JLibraryConstants.JLIBRARY_DESCRIPTION)) {		
			document.setDescription(node.getProperty(
					JLibraryConstants.JLIBRARY_DESCRIPTION).
					getValue().getString());
		}
	
		document.setJCRPath(node.getPath());
		document.setParent(parentId);
	
		if (node.hasProperty(JLibraryConstants.JLIBRARY_CREATED)) {
			Calendar date = node.getProperty(JLibraryConstants.JLIBRARY_CREATED).getDate();
			document.setDate(date.getTime());
		}
		if (node.hasProperty(JLibraryConstants.JLIBRARY_CREATOR)) {
			document.setCreator(node.getProperty(
					JLibraryConstants.JLIBRARY_CREATOR).
					getString());			
		}
		if (node.hasProperty(JLibraryConstants.JLIBRARY_IMPORTANCE)) {
			document.setImportance(new Integer((int)node.getProperty(
					JLibraryConstants.JLIBRARY_IMPORTANCE).
					getLong()));			
		}
		if (node.hasProperty(JLibraryConstants.JLIBRARY_TYPECODE)) {
			document.setTypecode(new Integer((int)node.getProperty(
					JLibraryConstants.JLIBRARY_TYPECODE).
					getLong()));			
		}
	
		if (node.hasProperty(JLibraryConstants.JLIBRARY_PATH)) {
			/*
			document.setPath(node.getProperty(
					JLibraryConstants.JLIBRARY_PATH).
					getString());
			*/
			String path = StringUtils.difference("/"+JLibraryConstants.JLIBRARY_ROOT, node.getPath());
			document.setPath(path);
		}
	
		if (node.hasProperty(JLibraryConstants.JLIBRARY_POSITION)) {
			document.setPosition(new Integer((int)node.getProperty(
					JLibraryConstants.JLIBRARY_POSITION).
					getLong()));			
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_TITLE)) {
			metadata.setTitle(node.getProperty(
					JLibraryConstants.JLIBRARY_TITLE).
					getString());			
		}
	
		if (node.hasProperty(JLibraryConstants.JLIBRARY_DOCUMENT_URL)) {
			metadata.setUrl(node.getProperty(
					JLibraryConstants.JLIBRARY_DOCUMENT_URL).
					getString());			
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_AUTHOR)) {
			String authorUUID = node.getProperty(
					JLibraryConstants.JLIBRARY_AUTHOR).getNode().getUUID();
			javax.jcr.Node authorNode = 
				node.getSession().getNodeByUUID(authorUUID);
			metadata.setAuthor(createAuthor(authorNode));	
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_KEYWORDS)) {
			metadata.setKeywords(node.getProperty(
					JLibraryConstants.JLIBRARY_KEYWORDS).
					getString());			
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_LANGUAGE)) {
			metadata.setLanguage(node.getProperty(
					JLibraryConstants.JLIBRARY_LANGUAGE).
					getString());			
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_CREATION_DATE)) {
			Calendar date = node.getProperty(
					JLibraryConstants.JLIBRARY_CREATION_DATE).getDate();
			metadata.setDate(date.getTime());
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_SIZE)) {
			document.setSize(new BigDecimal(node.getProperty(
					JLibraryConstants.JLIBRARY_SIZE).
					getLong()));			
		}		
		
		if (node.isLocked()) {
			Session session = node.getSession();
			if (node.getLock().getLockOwner().equals(session.getUserID())) {
				try {
					String lockToken = node.getProperty(
							JCRConstants.JCR_LOCK_TOKEN).getString();
					session.addLockToken(lockToken);
				} catch (Throwable t) {
					logger.error(t.getMessage(),t);
				}
			}
			document.setLock(createLock(node.getLock()));
		}
		
		document.setNodes(new HashSet());
		document.setRepository(repositoryId);
		document.setRestrictions(obtainRestrictions(node));
	
		// Handle resources
		HashSet resources = new HashSet();
		if (node.hasProperty(JLibraryConstants.JLIBRARY_RESOURCES)) {
			javax.jcr.Property resourcesProperty = 
				node.getProperty(JLibraryConstants.JLIBRARY_RESOURCES);
			Session session = node.getSession();
			Value[] values = resourcesProperty.getValues();
			for (int i = 0; i < values.length; i++) {
				String uuid = values[i].getString();
				javax.jcr.Node resourceNode = session.getNodeByUUID(uuid);
				String resourceParentId = resourceNode.getParent().getUUID();
				resources.add(createResource(resourceNode,
						 resourceParentId,
						 repositoryId));
			}
		}
		document.setResourceNodes(resources);
	
		// Handle notes
		HashSet notes = new HashSet();
		NodeIterator iterator = node.getNodes();
		while (iterator.hasNext()) {
			javax.jcr.Node child = (javax.jcr.Node)iterator.next();
			if (!JCRUtils.isActive(child)) {
				continue;
			}
			if (child.isNodeType(JLibraryConstants.NOTE_MIXIN)) {
				Note note = createNote(child,document);
				notes.add(note);
			}
		}
		document.setNotes(notes);
		
		// Handle versions
		if (node.hasNode(JCRConstants.JCR_BASE_VERSION)) {
			Version[] predecessors = node.getBaseVersion().getPredecessors();
			if (predecessors.length > 0 && predecessors[0].getPredecessors().length > 0) {
				document.setLastVersionId(predecessors[0].getUUID());
			} else {
				document.setLastVersionId(node.getUUID());			
			}
		} else {
			document.setLastVersionId(node.getUUID());
		}
		context.addNode(document);
		
		try {
			processCustomProperties(node,document);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		// Process relations. This depends on context stored data, so it must 
		// be done as late as possible
		processRelations(node,document,repositoryId,context);
		
		return document;
	}
	
	private static void processCustomProperties(Node node, Document document) throws Exception {
		
		PropertyIterator it = node.getProperties();
		while (it.hasNext()) {
			Property property = it.nextProperty();
			if (!property.getName().startsWith(JLibraryConstants.JLIBRARY_PREFIX) &&
				!property.getName().startsWith(JLibraryConstants.JCR_PREFIX)) {
				if (!property.getDefinition().isMultiple()) {
					document.putProperty(property.getName(), JCRUtils.getObject(property.getValue()));
				} else {
					document.putProperty(property.getName(), JCRUtils.getObjects(property.getValues()));					
				}
			}
		}
	}

	public static List createRestrictions(javax.jcr.Node node) 
													throws RepositoryException {
		
		List restrictions = new ArrayList();
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_RESTRICTIONS)) {
			javax.jcr.Node adminUser = 
				JCRSecurityService.findAdminUser(node.getSession()); 

			javax.jcr.Property restrictionsProperty = 
				node.getProperty(JLibraryConstants.JLIBRARY_RESTRICTIONS);
			Value[] values = restrictionsProperty.getValues();
			for (int i = 0; i < values.length; i++) {
				String uuid = values[i].getString();
				if (uuid.equals(adminUser.getUUID())) {
					uuid = User.ADMIN_CODE;
				}
				Restriction restriction = new Restriction();
				restriction.setId(node.getUUID());
				restriction.setMember(uuid);
				restriction.setNode(node.getUUID());
				
				restrictions.add(restriction);
			}
		}	
		return restrictions;
	}
	
	/**
	 * This method will obtain the ids of all the members that have a 
	 * restriction on the given node
	 * 
	 * @param node Node for checking restrictions
	 * 
	 * @return List with all the restricted member ids for that node
	 * 
	 * @throws RepositoryException If the restrictions cannot be loaded
	 */
	public static List obtainRestrictions(javax.jcr.Node node) 
													throws RepositoryException {
		
		List restrictions = new ArrayList();
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_RESTRICTIONS)) {
			javax.jcr.Property restrictionsProperty = 
				node.getProperty(JLibraryConstants.JLIBRARY_RESTRICTIONS);
			Value[] values = restrictionsProperty.getValues();
			for (int i = 0; i < values.length; i++) {
				String uuid = values[i].getString();
				restrictions.add(uuid);
			}
		}	
		return restrictions;
	}	
	
	public static DocumentVersion createVersion(javax.jcr.Node node,
												Version versionParent,
												Document document) 
										throws javax.jcr.RepositoryException {
		
		javax.jcr.Node version = 
			versionParent.getNode(JCRConstants.JCR_FROZEN_NODE);

		DocumentVersion documentVersion = new DocumentVersion();

		documentVersion.setNode(node.getUUID());
		documentVersion.setId(versionParent.getUUID());
		documentVersion.setRepository(document.getRepository());
		
		if (version.hasProperty(JLibraryConstants.JLIBRARY_NAME)) {
			documentVersion.setName(version.getProperty(
						JLibraryConstants.JLIBRARY_NAME).getString());
		} else {
			documentVersion.setName(document.getName());
		}
		if (version.hasProperty(JLibraryConstants.JLIBRARY_DESCRIPTION)) {
			documentVersion.setDescription(version.getProperty(
						JLibraryConstants.JLIBRARY_DESCRIPTION).getString());
		} else {
			documentVersion.setDescription(document.getDescription());
		}
		if (version.hasProperty(JLibraryConstants.JLIBRARY_CREATED)) {
			Calendar date = version.getProperty(
					JLibraryConstants.JLIBRARY_CREATED).getDate();
			documentVersion.setDate(date.getTime());
		} else {
			documentVersion.setDate(document.getDate());
		}
		if (version.hasProperty(JLibraryConstants.JLIBRARY_CREATOR)) {
			documentVersion.setCreator(version.getProperty(
						JLibraryConstants.JLIBRARY_CREATOR).
						getString());
		} else {
			documentVersion.setCreator(document.getCreator());
		}
		if (version.hasProperty(JLibraryConstants.JLIBRARY_IMPORTANCE)) {
			documentVersion.setImportance(new Integer((int)version.getProperty(
						JLibraryConstants.JLIBRARY_IMPORTANCE).
						getLong()));
		} else {
			documentVersion.setImportance(document.getImportance());
		}			
		if (version.hasProperty(JLibraryConstants.JLIBRARY_TYPECODE)) {
			documentVersion.setTypecode(new Integer((int)version.getProperty(
						JLibraryConstants.JLIBRARY_TYPECODE).
						getLong()));			
		} else {
			documentVersion.setTypecode(document.getTypecode());
		}			
		if (version.hasProperty(JLibraryConstants.JLIBRARY_POSITION)) {
			documentVersion.setPosition(new Integer((int)version.getProperty(
						JLibraryConstants.JLIBRARY_POSITION).
						getLong()));
		} else {
			documentVersion.setImportance(document.getImportance());
		}			
		if (version.hasProperty(JLibraryConstants.JLIBRARY_TITLE)) {
			documentVersion.setTitle(version.getProperty(
						JLibraryConstants.JLIBRARY_TITLE).
						getString());			
		} else {
			documentVersion.setTitle(document.getMetaData().getTitle());
		}
		if (version.hasProperty(JLibraryConstants.JLIBRARY_DOCUMENT_URL)) {
			documentVersion.setUrl(version.getProperty(
						JLibraryConstants.JLIBRARY_DOCUMENT_URL).
						getString());		
		} else {
			documentVersion.setUrl(document.getMetaData().getUrl());
		}			

		if (version.hasProperty(JLibraryConstants.JLIBRARY_DOCUMENT_URL)) {
			String authorUUID = version.getProperty(
					JLibraryConstants.JLIBRARY_AUTHOR).getNode().getUUID();
			documentVersion.setAuthor(authorUUID);
		} else {
			documentVersion.setAuthor(document.getMetaData().getAuthor().getId());
		}
			
		if (version.hasProperty(JLibraryConstants.JLIBRARY_CREATION_DATE)) {
			documentVersion.setKeywords(version.getProperty(
						JLibraryConstants.JLIBRARY_KEYWORDS).
						getString());			
		} else {
			documentVersion.setKeywords(document.getMetaData().getKeywords());
		}			
		if (version.hasProperty(JLibraryConstants.JLIBRARY_CREATION_DATE)) {
			java.util.Calendar date = node.getProperty(
					JLibraryConstants.JLIBRARY_CREATION_DATE).getDate();
			documentVersion.setDocumentDate(date.getTime());
		} else {
			documentVersion.setDocumentDate(document.getMetaData().getDate());
		}

		return documentVersion;
	}
	
	public static Note createNote(javax.jcr.Node node,
		      					  Document document) 
										throws RepositoryException {

		Note note = new Note();

		if (node.isNodeType(JCRConstants.JCR_REFERENCEABLE)) {
			note.setId(node.getUUID());
		}
		
		note.setDate(node.getProperty(
				JLibraryConstants.JLIBRARY_DATE).getDate().getTime());
		note.setNote(
				node.getProperty(JLibraryConstants.JLIBRARY_TEXT).getString());	
		if (node.hasProperty(JLibraryConstants.JLIBRARY_USER)) {
			javax.jcr.Node userNode = 
				node.getSession().getNodeByUUID(node.getProperty(
						JLibraryConstants.JLIBRARY_USER).getString());
			
			if (userNode.hasProperty(JLibraryConstants.JLIBRARY_SYSADMIN)) {
				note.setCreator(User.ADMIN_CODE);
			} else {
				note.setCreator(userNode.getUUID());			
			}			
		} else {
			note.setCreator(User.ADMIN_USER.getId());
		}
		note.setNode(document);

		return note;
	}
	
	public static Author createAuthor(javax.jcr.Node node)
										throws RepositoryException {
	
		Author author = new Author();
		String repositoryId = JCRUtils.getRootNode(node.getSession()).getUUID();
		author.setRepository(repositoryId);	
		if (node.isNodeType(JCRConstants.JCR_REFERENCEABLE)) {
			author.setId(node.getUUID());
		} else {
			author.setId(node.getProperty(JCRConstants.JCR_UUID).
					getValue().getString());
		}
		
		if (!JCRUtils.isActive(node)) {
			author.setActive(false);
		} else {
			author.setActive(true);
		}

		author.setName(node.getProperty(
				JLibraryConstants.JLIBRARY_NAME).getString());
		
		if (author.getName().equals(Author.UNKNOWN_NAME)) {
			// Little trick. We will change the id because this is the 
			// unknown author
			author.setId(Author.UNKNOWN.getId());
		}		
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_AUTHOR_BIO)) {
			author.setBio(node.getProperty(
					JLibraryConstants.JLIBRARY_AUTHOR_BIO).getString());
		}			
		return author;
	}

	public static Category createCategory(javax.jcr.Node node)
											throws RepositoryException {
		
		JCRCreationContext context = new JCRCreationContext();
		Category category = internalCreateCategory(node,context);
		context.clear();
		return category;
	}
	
	public static Category internalCreateCategory(javax.jcr.Node node,
												  JCRCreationContext context)
													throws RepositoryException {

		Category category = context.getCategory(node.getUUID());
		if (category != null) {
			return category;
		}
		
		category = new Category();
		String repositoryId = JCRUtils.getRootNode(node.getSession()).getUUID();
		category.setRepository(repositoryId);
		category.setFavorites(new HashSet());

		if (node.isNodeType(JCRConstants.JCR_REFERENCEABLE)) {
			category.setId(node.getUUID());
		} else {
			category.setId(node.getProperty(JCRConstants.JCR_UUID).
					getValue().getString());
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_NAME)) {
			category.setName(node.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString());
			
			if (category.getName().equals(Category.UNKNOWN_NAME)) {
				// Little trick. We will change the id because this is the 
				// unknown category
				category.setId(Category.UNKNOWN.getId());
				category.setUnknownCategory(true);
			}
		}		
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_DESCRIPTION)) {
			category.setDescription(node.getProperty(
					JLibraryConstants.JLIBRARY_DESCRIPTION).getString());
		}

		if (node.hasProperty(JLibraryConstants.JLIBRARY_DATE)) {
			Calendar date = node.getProperty(
					JLibraryConstants.JLIBRARY_DATE).getDate();
			category.setDate(date.getTime());
		}
		
		context.addCategory(category);
		
		HashSet categories = new HashSet();
		NodeIterator it = node.getNodes();
		while (it.hasNext()) {
			Node child = (Node)it.next();
			if (!JCRUtils.isActive(child)) {
				continue;
			}
			if (child.isNodeType(JLibraryConstants.CATEGORY_MIXIN)) {
				categories.add(internalCreateCategory(child,context));
			}
		}		
		category.setCategories(categories);
		
		javax.jcr.Node parent = node.getParent();
		if (parent.isNodeType(JLibraryConstants.CATEGORY_MIXIN)) {
			Category parentCategory = context.getCategory(parent.getUUID());
			if (parentCategory != null) {
				category.setParent(parentCategory);
			} else { 
				category.setParent(internalCreateCategory(parent,context));
			}
		} else {
			category.setParent(null);
		}
		
		// Handle favorites
		if (node.hasNode(JLibraryConstants.JLIBRARY_FAVORITES)) {
			javax.jcr.Node favoritesNode = 
				node.getNode(JLibraryConstants.JLIBRARY_FAVORITES);
			it = favoritesNode.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node favorite = it.nextNode();
				category.getFavorites().add(createFavorite(favorite));
			}
		}
		
		return category;
	}

	public static Lock createLock(javax.jcr.lock.Lock nodeLock) 
										throws javax.jcr.RepositoryException {
		
		javax.jcr.Node node = nodeLock.getNode();
		String repositoryId = 
			JCRUtils.getRootNode(node.getSession()).getUUID();
		Lock lock = new Lock();
		lock.setId(nodeLock.getNode().getUUID());
		lock.setRepository(repositoryId);
		lock.setUserId(node.getProperty(
				JLibraryConstants.JLIBRARY_LOCK_USER).getString());
		
		return lock;
	}

	public static User createUser(Ticket ticket, 
								  Node node) 
										throws javax.jcr.RepositoryException {
		
		JCRCreationContext context = new JCRCreationContext();
		User user = createUser(ticket,node,context);
		context.clear();
		return user;
		
	}
	
	private static User createUser(Ticket ticket, 
								   Node node, 
								   JCRCreationContext context) 
										throws javax.jcr.RepositoryException {

		User user = new User();
		user.setGroups(new HashSet());
		user.setRoles(new HashSet());
		user.setFavorites(new HashSet());
		user.setBookmarks(new HashSet());
		
		if (node.isNodeType(JCRConstants.JCR_REFERENCEABLE)) {
			user.setId(node.getUUID());
		} else {
			user.setId(node.getProperty(JCRConstants.JCR_UUID).
					getValue().getString());
		}			
		
		javax.jcr.Node rootNode = JCRUtils.getRootNode(node.getSession());
		if (rootNode.isNodeType(JCRConstants.JCR_REFERENCEABLE)) {
			user.setRepository(rootNode.getUUID());
		} else {
			user.setRepository("-1");
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_NAME)) {
			user.setName(node.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString());
		}
		
		if (node.hasNode(JLibraryConstants.JLIBRARY_BOOKMARKS)) {
			javax.jcr.Node bookmarksNode = 
				node.getNode(JLibraryConstants.JLIBRARY_BOOKMARKS);
			NodeIterator it = bookmarksNode.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node child = (javax.jcr.Node) it.next();
				if (!JCRUtils.isActive(child)) {
					continue;
				}
				user.getBookmarks().add(createBookmark(child,context));
			}
		}
		
		// The other items must be only added if the user has enough rights. 
		// When user is null this means that the user has not loged in yet.
		if ((ticket == null) || 
			(ticket.getUser() == null) || 
			(JCRSecurityService.isAdmin(node.getSession(),
										 ticket.getUser().getId()))) {
			
			if (node.hasProperty(JLibraryConstants.JLIBRARY_PASSWORD)) {
				user.setPassword(node.getProperty(
						JLibraryConstants.JLIBRARY_PASSWORD).getString());
			}
	
			if (node.hasProperty(JLibraryConstants.JLIBRARY_FIRSTNAME)) {
				user.setFirstName(node.getProperty(
						JLibraryConstants.JLIBRARY_FIRSTNAME).getString());
			}
	
			if (node.hasProperty(JLibraryConstants.JLIBRARY_LASTNAME)) {
				user.setLastName(node.getProperty(
						JLibraryConstants.JLIBRARY_LASTNAME).getString());
			}
			
			if (node.hasProperty(JLibraryConstants.JLIBRARY_EMAIL)) {
				user.setEmail(node.getProperty(
						JLibraryConstants.JLIBRARY_EMAIL).getString());
			}
	
			if (node.hasProperty(JLibraryConstants.JLIBRARY_ISADMIN)) {
				user.setAdmin(node.getProperty(
						JLibraryConstants.JLIBRARY_ISADMIN).getBoolean());
			}
				
			if (node.hasProperty(JLibraryConstants.JLIBRARY_SYSADMIN)) {
				user.setAdmin(true);
				user.setId(User.ADMIN_CODE);
			}			
			
			context.addMember(user);
		
			javax.jcr.Session session = node.getSession();
			javax.jcr.Property groupsProperty = 
				node.getProperty(JLibraryConstants.JLIBRARY_GROUPS);
			Value[] values = groupsProperty.getValues();
			for (int i = 0; i < values.length; i++) {
				String uuid = values[i].getString();
				Group group = (Group)context.getMember(uuid);
				if (group == null) {
					javax.jcr.Node groupNode = session.getNodeByUUID(uuid);
					group = createGroup(ticket,groupNode,context);
				}
				user.getGroups().add(group);
			}		
	
			javax.jcr.Property rolesProperty = 
				node.getProperty(JLibraryConstants.JLIBRARY_ROLES);
			values = rolesProperty.getValues();
			for (int i = 0; i < values.length; i++) {
				String uuid = values[i].getString();
				Rol rol = (Rol)context.getRol(uuid);
				if (rol == null) {
					javax.jcr.Node rolNode = session.getNodeByUUID(uuid);
					rol = createRol(ticket,rolNode,context);
				}
				user.getRoles().add(rol);
			}				
		}		
		return user;
	}
	
	public static Group createGroup(Ticket ticket, Node node) 
										throws javax.jcr.RepositoryException {

		JCRCreationContext context = new JCRCreationContext();
		Group group = createGroup(ticket,node,context);
		context.clear();
		return group;
	}	
	
	public static Group createGroup(Ticket ticket,
									Node node,
								    JCRCreationContext context) 
										throws javax.jcr.RepositoryException {

		javax.jcr.Node rootNode = JCRUtils.getRootNode(node.getSession());

		Group group = new Group();
		group.setUsers(new HashSet());
		group.setRoles(new HashSet());
		group.setRepository(rootNode.getUUID());
		
		group.setId(node.getUUID());
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_NAME)) {
			group.setName(node.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString());
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_DESCRIPTION)) {
			group.setDescription(node.getProperty(
					JLibraryConstants.JLIBRARY_DESCRIPTION).getString());
		}
		context.addMember(group);

		if ((ticket == null) || 
			(ticket.getUser() == null) || 
			(JCRSecurityService.isAdmin(node.getSession(),
										ticket.getUser().getId()))) {
			
			// This information will be filtered
			javax.jcr.Session session = node.getSession();
			javax.jcr.Property usersProperty = 
				node.getProperty(JLibraryConstants.JLIBRARY_USERS);
			Value[] values = usersProperty.getValues();
			for (int i = 0; i < values.length; i++) {
				String uuid = values[i].getString();
				User user = (User)context.getMember(uuid);
				if (user == null) {
					javax.jcr.Node userNode = session.getNodeByUUID(uuid);
					user = createUser(ticket,userNode,context);
				}
				group.getUsers().add(user);
			}				
			
			javax.jcr.Property rolesProperty = 
				node.getProperty(JLibraryConstants.JLIBRARY_ROLES);
			values = rolesProperty.getValues();
			for (int i = 0; i < values.length; i++) {
				String uuid = values[i].getString();
				Rol rol = (Rol)context.getRol(uuid);
				if (rol == null) {
					javax.jcr.Node rolNode = session.getNodeByUUID(uuid);
					rol = createRol(ticket,rolNode,context);
				}
				group.getRoles().add(rol);
			}
		}		
		return group;
	}

	public static Rol createRol(Ticket ticket,
								Node node) 
										throws javax.jcr.RepositoryException {
		
		JCRCreationContext context = new JCRCreationContext();
		Rol rol = createRol(ticket,node,context);
		context.clear();
		return rol;		
	}
	
	
	private static Rol createRol(Ticket ticket,
								 Node node,
								 JCRCreationContext context) 
										throws javax.jcr.RepositoryException {

		Rol rol = new Rol();
		String repositoryId = JCRUtils.getRootNode(node.getSession()).getUUID();
		rol.setRepository(repositoryId);
		rol.setMembers(new HashSet());
		
		rol.setId(node.getUUID());
		if (node.hasProperty(JLibraryConstants.JLIBRARY_NAME)) {
			rol.setName(node.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString());
		}

		if (node.hasProperty(JLibraryConstants.JLIBRARY_DESCRIPTION)) {
			rol.setDescription(node.getProperty(
					JLibraryConstants.JLIBRARY_DESCRIPTION).getString());
		}
		
		context.addRol(rol);
		
		if ((ticket == null) || 
			(ticket.getUser() == null) || 
			(JCRSecurityService.isAdmin(node.getSession(),
										ticket.getUser().getId()))) {
			
			// This information will be filtered
			javax.jcr.Session session = node.getSession();
			if (node.hasProperty(JLibraryConstants.JLIBRARY_MEMBERS)) {
				javax.jcr.Property membersProperty = 
							node.getProperty(JLibraryConstants.JLIBRARY_MEMBERS);
				Value[] values = membersProperty.getValues();
				for (int i = 0; i < values.length; i++) {
					String uuid = values[i].getString();
					Member member = (Member)context.getMember(uuid);
					if (member == null) {
						javax.jcr.Node memberNode = session.getNodeByUUID(uuid);
						if (memberNode.isNodeType(JLibraryConstants.USER_MIXIN)) {
							member = createUser(ticket,memberNode,context);
						} else if (memberNode.isNodeType(JLibraryConstants.GROUP_MIXIN)) {
							member = createGroup(ticket,memberNode,context);
						}
					}
					rol.getMembers().add(member);
				}			
			}
		}
		return rol;
	}
	
	public static Favorite createFavorite(Node node) 
									throws javax.jcr.RepositoryException {

		Favorite favorite = new Favorite();
		favorite.setId(node.getUUID());

		javax.jcr.Node rootNode = JCRUtils.getRootNode(node.getSession());
		favorite.setRepository(rootNode.getUUID()); 
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_NODE)) {
			favorite.setDocument(node.getProperty(
					JLibraryConstants.JLIBRARY_NODE).getString());
		}

		if (node.hasProperty(JLibraryConstants.JLIBRARY_USER)) {
			javax.jcr.Node userNode = 
				node.getSession().getNodeByUUID(node.getProperty(
						JLibraryConstants.JLIBRARY_USER).getString());
			
			if (node.hasProperty(JLibraryConstants.JLIBRARY_SYSADMIN)) {
				favorite.setUser(User.ADMIN_CODE);
			} else {
				favorite.setUser(userNode.getUUID());			
			}
		}

		favorite.setCategory(node.getParent().getParent().getUUID());

		return favorite;
	}	

	public static Bookmark createBookmark(Node node) 
									throws javax.jcr.RepositoryException {

		JCRCreationContext context = new JCRCreationContext();
		Bookmark bookmark = createBookmark(node,context);
		context.clear();
		return bookmark;		
	}
	
	private static Bookmark createBookmark(Node node,
									       JCRCreationContext context) 
									throws javax.jcr.RepositoryException {

		Bookmark bookmark = context.getBookmark(node.getUUID());
		if (bookmark != null) {
			return bookmark;
		}
		
		bookmark = new Bookmark();
		String repositoryId = JCRUtils.getRootNode(node.getSession()).getUUID();
		bookmark.setRepository(repositoryId);
		bookmark.setId(node.getUUID());
		bookmark.setBookmarks(new HashSet());
		if (node.getParent().isNodeType(JLibraryConstants.BOOKMARK_MIXIN)) {
			bookmark.setParent(createBookmark(node.getParent(),context));
		} else {
			bookmark.setParent(null);
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_NAME)) {
			bookmark.setName(node.getProperty(
					JLibraryConstants.JLIBRARY_NAME).getString());
		}

		if (node.hasProperty(JLibraryConstants.JLIBRARY_DESCRIPTION)) {
			bookmark.setDescription(node.getProperty(
					JLibraryConstants.JLIBRARY_DESCRIPTION).getString());
		}
		
		if (node.hasProperty(JLibraryConstants.JLIBRARY_USER)) {
			bookmark.setUser(node.getProperty(
					JLibraryConstants.JLIBRARY_USER).getString());
		}

		if (node.hasProperty(JLibraryConstants.JLIBRARY_DOCUMENT_URL)) {
			bookmark.setUrl(node.getProperty(
					JLibraryConstants.JLIBRARY_DOCUMENT_URL).getString());
		}

		if (node.hasProperty(JLibraryConstants.JLIBRARY_TYPECODE)) {
			bookmark.setType(node.getProperty(
					JLibraryConstants.JLIBRARY_TYPECODE).getString());
		}
		context.addBookmark(bookmark);
		
		NodeIterator it = node.getNodes();
		while (it.hasNext()) {
			javax.jcr.Node child = (javax.jcr.Node) it.next();
			if (!JCRUtils.isActive(child)) {
				continue;
			}
			bookmark.getBookmarks().add(createBookmark(child,context));
		}
		
		return bookmark;
	}	
}
