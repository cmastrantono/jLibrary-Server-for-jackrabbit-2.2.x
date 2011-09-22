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

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.version.OnParentVersionAction;

import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;

import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;

import org.apache.jackrabbit.core.value.InternalValue;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.commons.QNodeDefinitionImpl;
import org.apache.jackrabbit.spi.commons.QNodeTypeDefinitionImpl;
import org.apache.jackrabbit.spi.commons.QPropertyDefinitionImpl;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.nodetype.QNodeDefinitionBuilder;
import org.apache.jackrabbit.spi.commons.nodetype.QNodeTypeDefinitionBuilder;
import org.apache.jackrabbit.spi.commons.nodetype.QPropertyDefinitionBuilder;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeTypeManager {

	static Logger logger = LoggerFactory.getLogger(NodeTypeManager.class);
	
	private static final Name ntBase =	NameFactoryImpl.getInstance().create(JCRConstants.JCR_NT_URL,"base");
	
	// jLibrary types
	private static final Name jlibBase = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"jlibrary");
	private static final Name jlibInternal = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"internal");
	private static final Name jlibNode = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"node");
	private static final Name jlibDirectory = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"directory");
	private static final Name jlibResource = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"resource");
	private static final Name jlibDocument = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"document");
	private static final Name jlibAuthor = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"author");
	private static final Name jlibCategory = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"category");
	private static final Name jlibNote = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"note");
	private static final Name jlibUser = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"user");
	private static final Name jlibGroup = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"group");
	private static final Name jlibRol = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"rol");
	private static final Name jlibContent = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"content");
	private static final Name jlibFavorite = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"favorite");
	private static final Name jlibBookmark = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"bookmark");
	private static final Name jlibFavorites = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"favorites");
	private static final Name jlibBookmarks = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"bookmarks");
	
	// jLibrary properties
	private static final Name pjlibName = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"name");
	private static final Name pjlibDescription = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"description");
	private static final Name pjlibCreator = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"creator");
	private static final Name pjlibImportance = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"importance");
	private static final Name pjlibTypecode = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"typecode");
	private static final Name pjlibCreated = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"created");
	private static final Name pjlibPath = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"path");
	private static final Name pjlibPosition = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"position");
	private static final Name pjlibAuthor = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"author");
	private static final Name pjlibLanguage = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"language");
	private static final Name pjlibKeywords = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"keywords");
	private static final Name pjlibCreationDate = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"creationDate");
	private static final Name pjlibURL = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"url");
	private static final Name pjlibTitle = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"title");
	private static final Name pjlibSize = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"size");
	private static final Name pjlibCategories = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"categories");
	private static final Name pjlibResources = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"resources");
	private static final Name pjlibRestrictions = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"restrictions");
	private static final Name pjlibRelations = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"relations");
	private static final Name pjlibNodes = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"nodes");
	private static final Name pjlibDate = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"date");
	private static final Name pjlibText = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"text");
	private static final Name pjlibBio = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"bio");
	private static final Name pjlibUser = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"user");
	private static final Name pjlibUsers = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"users");
	private static final Name pjlibMembers = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"members");
	private static final Name pjlibRoles = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"roles");
	private static final Name pjlibGroups = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"groups");
	private static final Name pjlibFirstName = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"firstname");
	private static final Name pjlibLastName = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"lastname");
	private static final Name pjlibEmail = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"email");
	private static final Name pjlibPassword = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"password");
	private static final Name pjlibIsAdmin = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"isAdmin");
	private static final Name pjlibIsSysadmin = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"sysAdmin");
	private static final Name pjlibNode = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"node");
	private static final Name pjlibLockUser = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"lockUser");
	private static final Name pjlibActive = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"active");

	// Additional properties
	Name pmixLockToken = NameFactoryImpl.getInstance().create(JCRConstants.JCR_MIX_URL,"lockToken");

	// Node definitions
	private QNodeTypeDefinition ntdJLibrary;
	private QNodeTypeDefinition ntdInternal;
	private QNodeTypeDefinition ntdNode;
	private QNodeTypeDefinition ntdResource;
	private QNodeTypeDefinition ntdDirectory;
	private QNodeTypeDefinition ntdDocument;
	private QNodeTypeDefinition ntdAuthor;
	private QNodeTypeDefinition ntdCategory;
	private QNodeTypeDefinition ntdNote;
	private QNodeTypeDefinition ntdUser;
	private QNodeTypeDefinition ntdGroup;
	private QNodeTypeDefinition ntdRol;
	private QNodeTypeDefinition ntdContent;
	private QNodeTypeDefinition ntdFavorite;
	private QNodeTypeDefinition ntdBookmark;
	
	public NodeTypeManager() {
		
		buildNodeTypes();
	}
	
	private Name createCustomPropertyType(String propertyName) {
		
		int colon = propertyName.lastIndexOf(":");
		propertyName = propertyName.substring(colon+1,propertyName.length());
		return NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_EXTENDED_URL,propertyName);
	}
	
	private QPropertyDefinition createCustomPropertyDef(
			String propertyName, 
			boolean multivalued, 
			int type,
			boolean autocreated,
			InternalValue[] defaultValues) {
		
		Name propName = createCustomPropertyType(propertyName);
		QPropertyDefinition customProperty = 
			createPropertyDef(propName,
							  false, // This should be mandatory
							  multivalued,
							  type,
							  jlibDocument,
							  OnParentVersionAction.COPY,
							  autocreated,
							  defaultValues);
		return customProperty;
	}
	
	/**
	 * Registers a custom property for documents under a given session. The custom property 
	 * will be registered in the repository. All the documents will have that custom property 
	 * registered.
	 * <p/>
	 * Note that if you register a custom property within a Jackrabbit repository, that property 
	 * will be shared by all repositories. So this means that if you create for example a property 
	 * <code>jlib:rate</code> all the documents in all the repositories could potentially have that 
	 * custom property. <b>Please note that this can be a potential issue when you share the same 
	 * repository between different and heterogeneus applications.</b>
	 * 
	 * @param session Session
	 * @param propertyName Name of the property
	 * @param multivalued <code>true</code> if the property is multivalued and <code>false</code> otherwise.
	 * @param type Type of the property. 
	 * @param autocreated <code>true</code> if the property is autocreated and <code>false</code> otherwise.
	 * @param defaultValues Default values if autocreated, <code>null</code> otherwise.
	 * 
	 * @throws RepositoryException If the property can�t be created
	 */
	public void registerCustomProperty(
			javax.jcr.Session session, 
			CustomPropertyDefinition property) throws RepositoryException {
		
		try {
			InternalValue[] values = null;
			if (property.getDefaultValues() != null) {
				values = JCRUtils.getInternalValues(property.getDefaultValues());
			}			
			QPropertyDefinition customProperty = createCustomPropertyDef(
					property.getName(), 
					property.isMultivalued(), 
					property.getType(),
					property.isAutocreated(),
					values);
			
			Workspace wsp = session.getWorkspace();
			javax.jcr.nodetype.NodeTypeManager ntMgr = wsp.getNodeTypeManager();
			NodeTypeRegistry ntReg = 
		        ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
			QNodeTypeDefinition def = ntReg.getNodeTypeDef(jlibDocument);
			QPropertyDefinition[] properties = def.getPropertyDefs();

			if (!isCustomPropertyRegistered(session, property.getName())) {
				QPropertyDefinition[] newProperties = new QPropertyDefinitionImpl[properties.length+1];
				System.arraycopy(properties,0,newProperties,0,properties.length);
				newProperties[properties.length] = customProperty;
				
				//Para obtener un nuevo QNodeTypeDefinition tengo q setearle
				QNodeTypeDefinitionBuilder qdb = new QNodeTypeDefinitionBuilder();
				qdb.setAbstract(ntdDocument.isAbstract());
				qdb.setChildNodeDefs(ntdDocument.getChildNodeDefs());
				qdb.setMixin(ntdDocument.isMixin());
				qdb.setName(ntdDocument.getName());
				qdb.setOrderableChildNodes(ntdDocument.hasOrderableChildNodes());
				qdb.setPrimaryItemName(ntdDocument.getPrimaryItemName());				
				qdb.setQueryable(ntdDocument.isQueryable());
				qdb.setSupertypes(ntdDocument.getSupertypes());
				qdb.setSupportedMixinTypes(ntdDocument.getSupportedMixinTypes());
				qdb.setPropertyDefs(newProperties);
				
				ntdDocument = qdb.build();
								
				ntReg.reregisterNodeType(ntdDocument);
			}
		} catch (InvalidNodeTypeDefException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}	
		session.save();
	}

	/**
	 * Unregisters a custom property for documents under a given session. The custom property 
	 * will be unregistered from the repository. All the documents will have that custom property 
	 * unregistered.
	 * <p/>
	 * Currently Jackrabbit <b>doesn�t support mandatory properties removal</b>, so the behaviour of this 
	 * method is not so predictable as it should be. Extensive testing needs to be done. Be sure to 
	 * don�t use this method with mandatory properties.
	 * <p/>
	 * This method doesn�t remove the properties from the documents. Properties should be removed 
	 * manually.
	 * 
	 * @param session Session
	 * @param propertyName Name of the property
	 * 
	 * @throws RepositoryException If the property can�t be unregistered
	 */
	public void unregisterCustomProperty(
			javax.jcr.Session session, 
			String propertyName) throws RepositoryException {
		
		try {
			Name propName = createCustomPropertyType(propertyName);
			
			Workspace wsp = session.getWorkspace();
			javax.jcr.nodetype.NodeTypeManager ntMgr = wsp.getNodeTypeManager();
			NodeTypeRegistry ntReg = 
		        ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
			QNodeTypeDefinition def = ntReg.getNodeTypeDef(jlibDocument);
			QPropertyDefinition[] properties = def.getPropertyDefs();

			if (isCustomPropertyRegistered(session, propertyName)) {
				QPropertyDefinition[] newProps = new QPropertyDefinitionImpl[properties.length-1];
				int j = 0;
				for (int i=0;i<properties.length;i++) {
					if (!properties[i].getName().equals(propName)) {
						newProps[j] = properties[i];
						j++;
					}
				}
				
				//Para obtener un nuevo QNodeTypeDefinition tengo q setearle
				QNodeTypeDefinitionBuilder qdb = new QNodeTypeDefinitionBuilder();
				qdb.setAbstract(def.isAbstract());
				qdb.setChildNodeDefs(def.getChildNodeDefs());
				qdb.setMixin(def.isMixin());
				qdb.setName(def.getName());
				qdb.setOrderableChildNodes(def.hasOrderableChildNodes());
				qdb.setPrimaryItemName(def.getPrimaryItemName());				
				qdb.setQueryable(def.isQueryable());
				qdb.setSupertypes(def.getSupertypes());
				qdb.setSupportedMixinTypes(def.getSupportedMixinTypes());
				qdb.setPropertyDefs(newProps);
				
				def = qdb.build();
				
				//Para obtener un nuevo QNodeTypeDefinition tengo q setearle				
				qdb.setAbstract(ntdDocument.isAbstract());
				qdb.setChildNodeDefs(ntdDocument.getChildNodeDefs());
				qdb.setMixin(ntdDocument.isMixin());
				qdb.setName(ntdDocument.getName());
				qdb.setOrderableChildNodes(ntdDocument.hasOrderableChildNodes());
				qdb.setPrimaryItemName(ntdDocument.getPrimaryItemName());				
				qdb.setQueryable(ntdDocument.isQueryable());
				qdb.setSupertypes(ntdDocument.getSupertypes());
				qdb.setSupportedMixinTypes(ntdDocument.getSupportedMixinTypes());
				qdb.setPropertyDefs(newProps);
				
				ntdDocument = qdb.build();
				
				ntReg.reregisterNodeType(def);
			}
		} catch (InvalidNodeTypeDefException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}	
		session.save();
	}	
	
	/**
	 * Says if a custom property has been registered in a given repository.
	 *  
	 * @param session Session
	 * @param propertyName Name of the property to check
	 * 
	 * @return boolean <code>true</code> if the property has been already registered and <code>false</code> otherwise.
	 * 
	 * @throws RepositoryException If the check cannot be performed
	 */
	public boolean isCustomPropertyRegistered(
			javax.jcr.Session session, String propertyName) throws RepositoryException {
		
		Name propName = createCustomPropertyType(propertyName);
		Workspace wsp = session.getWorkspace();
		javax.jcr.nodetype.NodeTypeManager ntMgr = wsp.getNodeTypeManager();
		NodeTypeRegistry ntReg = 
	        ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
		QNodeTypeDefinition def = ntReg.getNodeTypeDef(jlibDocument);
		QPropertyDefinition[] properties = def.getPropertyDefs();
		// check
		boolean found = false;
		for (int i = 0; i < properties.length; i++) {
			if (properties[i].getName().equals(propName)) {
				// Already exists
				found = true;
				break;
			}
		}		
		return found;		
	}
	
	/**
	 * Registers node types on a session
	 * 
	 * @param session Session
	 * 
	 * @throws RepositoryException If the node types can't be registered
	 */
	public void registerNodeTypes(Session session) 
									throws RepositoryException {
		
		Workspace wsp = session.getWorkspace();
		javax.jcr.nodetype.NodeTypeManager ntMgr = wsp.getNodeTypeManager();
		NodeTypeRegistry ntReg = 
	        ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
		
		wsp.getNamespaceRegistry().registerNamespace(
								JLibraryConstants.JLIBRARY_PREFIX, 
								JLibraryConstants.JLIBRARY_URL);
		wsp.getNamespaceRegistry().registerNamespace(
				JLibraryConstants.JLIBRARY_EXTENDED_PREFIX, 
				JLibraryConstants.JLIBRARY_EXTENDED_URL);
		session.save();
         
		try {
			ntReg.registerNodeType(ntdJLibrary);
			ntReg.registerNodeType(ntdInternal);
			ntReg.registerNodeType(ntdNode);
			ntReg.registerNodeType(ntdResource);
			ntReg.registerNodeType(ntdDirectory);
			ntReg.registerNodeType(ntdDocument);
			ntReg.registerNodeType(ntdAuthor);
			ntReg.registerNodeType(ntdCategory);
			ntReg.registerNodeType(ntdNote);
			ntReg.registerNodeType(ntdUser);
			ntReg.registerNodeType(ntdGroup);
			ntReg.registerNodeType(ntdRol);
			ntReg.registerNodeType(ntdContent);
			ntReg.registerNodeType(ntdFavorite);
			ntReg.registerNodeType(ntdBookmark);
		} catch (InvalidNodeTypeDefException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		
		session.save();
	}

	public void checkNodeTypes(Session session) throws RepositoryException {
		
		try {
			// update registries
			Workspace wsp = session.getWorkspace();
			javax.jcr.nodetype.NodeTypeManager ntMgr = wsp.getNodeTypeManager();
			NodeTypeRegistry ntReg = 
			    ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
			
			
			// New from jLibrary 1.0 final
			//TODO: Change this property to mandatory. Currently the only 
			// way to add this property is adding as non-mandatory, as 
			// Jackrabbit does not support to add mandatory properties and 
			// then change reregister/unregister node definitions
			//
			// Note that in new repositories, this property is added as 
			// mandatory.
			QPropertyDefinition propNoteUser = 
				createPropertyDef(pjlibUser,
								  false, // This should be mandatory
								  false,
								  PropertyType.REFERENCE,
								  jlibNote,
								  OnParentVersionAction.COPY,
								  false,
								  null);
			
			QNodeTypeDefinition def = ntReg.getNodeTypeDef(jlibNote);
			QPropertyDefinition[] properties = def.getPropertyDefs();
			// check
			boolean found = false;
			for (int i = 0; i < properties.length; i++) {
				if (properties[i].getName().equals(propNoteUser.getName())) {
					// Already exists
					found = true;;
				}
			}				
			if (!found) {
				QPropertyDefinition[] newProperties = new QPropertyDefinitionImpl[properties.length+1];
				System.arraycopy(properties,0,newProperties,0,properties.length);
				newProperties[properties.length] = propNoteUser;
				
				QNodeTypeDefinitionBuilder qdb = new QNodeTypeDefinitionBuilder();
				qdb.setAbstract(ntdNote.isAbstract());
				qdb.setChildNodeDefs(ntdNote.getChildNodeDefs());
				qdb.setMixin(ntdNote.isMixin());
				qdb.setName(ntdNote.getName());
				qdb.setOrderableChildNodes(ntdNote.hasOrderableChildNodes());
				qdb.setPrimaryItemName(ntdNote.getPrimaryItemName());				
				qdb.setQueryable(ntdNote.isQueryable());
				qdb.setSupertypes(ntdNote.getSupertypes());
				qdb.setSupportedMixinTypes(ntdNote.getSupportedMixinTypes());
				qdb.setPropertyDefs(newProperties);
				
				ntdNote = qdb.build();				
				
				ntReg.reregisterNodeType(ntdNote);
			}
			
			InternalValue[] defaultValues = 
				new InternalValue[]{InternalValue.create(true)};
			QPropertyDefinition propActive = 
				createPropertyDef(pjlibActive,
								  false, // This should also be mandatory
								  false,
								  PropertyType.BOOLEAN,
								  jlibBase,
								  OnParentVersionAction.COPY,
								  true,
								  defaultValues);
			
			def = ntReg.getNodeTypeDef(jlibBase);
			properties = def.getPropertyDefs();
			// check
			found = false;
			for (int i = 0; i < properties.length; i++) {
				if (properties[i].getName().equals(propActive.getName())) {
					// Already exists
					found = true;;
				}
			}		
			if (!found) {
				QPropertyDefinition[] newProperties = new QPropertyDefinitionImpl[properties.length+1];
				System.arraycopy(properties,0,newProperties,0,properties.length);
				newProperties[properties.length] = propActive;
				
				QNodeTypeDefinitionBuilder qdb = new QNodeTypeDefinitionBuilder();
				qdb.setAbstract(ntdJLibrary.isAbstract());
				qdb.setChildNodeDefs(ntdJLibrary.getChildNodeDefs());
				qdb.setMixin(ntdJLibrary.isMixin());
				qdb.setName(ntdJLibrary.getName());
				qdb.setOrderableChildNodes(ntdJLibrary.hasOrderableChildNodes());
				qdb.setPrimaryItemName(ntdJLibrary.getPrimaryItemName());				
				qdb.setQueryable(ntdJLibrary.isQueryable());
				qdb.setSupertypes(ntdJLibrary.getSupertypes());
				qdb.setSupportedMixinTypes(ntdJLibrary.getSupportedMixinTypes());
				qdb.setPropertyDefs(newProperties);
				
				ntdJLibrary = qdb.build();
								
				ntReg.reregisterNodeType(ntdJLibrary);
			}
			/*
			 * This would be great but you will get a not yet implemented
			 * exception because currently jackrabbit is not able to remove
			 * mandatory property definitions
			def = ntReg.getNodeTypeDef(jlibCategory);
			PropDef[] props = def.getPropertyDefs();
			QName pjlibRepository = 
				new QName(JLibraryConstants.JLIBRARY_URL,"repository");
			PropDef[] newProps = new PropDef[props.length-1];
			int j = 0;
			for (int i=0;i<props.length;i++) {
				if (!props[i].getName().equals(pjlibRepository)) {
					newProps[j] = props[i];
					j++;
				}
			}
			def.setPropertyDefs(newProps);
			ntReg.reregisterNodeType(def);
			*/
			session.save();
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	
	
	private void buildNodeTypes() {
		
		ntdJLibrary = createNodeTypeDef(new Name[]{ntBase},jlibBase,true);
		
		ntdInternal = createNodeTypeDef(new Name[]{jlibBase},jlibInternal,true);
		
		
		QNodeDefinitionBuilder qndb = new QNodeDefinitionBuilder();
		qndb.setAllowsSameNameSiblings(true);
		qndb.setName(NameFactoryImpl.getInstance().create("","*"));
		qndb.setDeclaringNodeType(jlibInternal);							
		
		ntdInternal = setChildNodeDefsToQNTD(ntdInternal, new QNodeDefinition[]{qndb.build()});
		
		QPropertyDefinitionBuilder qpdb = new QPropertyDefinitionBuilder();		
		qpdb.setMandatory(false);
		qpdb.setMultiple(false);
		qpdb.setDeclaringNodeType(jlibInternal);
		
		ntdInternal = setPropertyDefsToQNTD(ntdInternal, new QPropertyDefinition[]{qpdb.build()}); 	
		
		ntdNode = createNodeTypeDef(new Name[]{jlibBase},jlibNode,true);		
		ntdResource = createNodeTypeDef(new Name[]{jlibNode},jlibResource,true);
		ntdDirectory = createNodeTypeDef(new Name[]{jlibNode},jlibDirectory,true);
		ntdDocument = createNodeTypeDef(new Name[]{jlibNode},jlibDocument,true);
		ntdAuthor = createNodeTypeDef(new Name[]{jlibBase},jlibAuthor,true);
		ntdCategory = createNodeTypeDef(new Name[]{jlibBase},jlibCategory,true);			
		ntdNote = createNodeTypeDef(new Name[]{jlibBase},jlibNote,true);	
		ntdUser = createNodeTypeDef(new Name[]{jlibBase},jlibUser,true);			
		ntdGroup = createNodeTypeDef(new Name[]{jlibBase},jlibGroup,true);			
		ntdRol = createNodeTypeDef(new Name[]{jlibBase},jlibRol,true);	
		ntdContent = createNodeTypeDef(new Name[]{jlibBase},jlibContent,true);			
		ntdFavorite = createNodeTypeDef(new Name[]{jlibBase},jlibFavorite,true);			
		ntdBookmark = createNodeTypeDef(new Name[]{jlibBase},jlibBookmark,true);					
		
		registerNodeDef(new Name[]{jlibBase}, 
									jlibBase, ntdJLibrary,
						new int[]{OnParentVersionAction.COPY,
								  OnParentVersionAction.COPY});
		
		registerNodeDef(new Name[]{jlibDirectory,jlibDocument,jlibResource}, 
									jlibDirectory,ntdDirectory,
						new int[]{OnParentVersionAction.COPY,
				  				  OnParentVersionAction.COPY,
				  				  OnParentVersionAction.COPY});
		
		registerNodeDef(new Name[]{jlibContent,jlibNote},
									jlibDocument,ntdDocument,
						new int[]{OnParentVersionAction.COPY,
								  OnParentVersionAction.COPY});
		
		registerNodeDef(new Name[]{jlibCategory,jlibFavorites},
									jlibCategory,ntdCategory,
						new int[]{OnParentVersionAction.COPY,
								  OnParentVersionAction.IGNORE});
		
		registerNodeDef(new Name[]{jlibBookmarks},
									jlibUser,ntdUser,
						new int[]{OnParentVersionAction.IGNORE});

		registerNodeDef(new Name[]{jlibBookmark},
									jlibBookmark,ntdBookmark,
                        new int[]{OnParentVersionAction.IGNORE});
		
		
		InternalValue[] defaultTrue = 
			new InternalValue[]{InternalValue.create(true)};
		
		QPropertyDefinition propName = createPropertyDef(pjlibName,false,false,PropertyType.STRING,jlibBase,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propActive = createPropertyDef(pjlibActive,true,false,PropertyType.BOOLEAN,jlibBase,OnParentVersionAction.COPY,true,defaultTrue);
		
		QPropertyDefinition propDescription = createPropertyDef(pjlibDescription,true,false,PropertyType.STRING,jlibNode,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propCreator = createPropertyDef(pjlibCreator,true,false,PropertyType.STRING,jlibNode,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propImportance = createPropertyDef(pjlibImportance,true,false,PropertyType.LONG,jlibNode,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propTypecode = createPropertyDef(pjlibTypecode,true,false,PropertyType.LONG,jlibNode,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propCreated = createPropertyDef(pjlibCreated,true,false,PropertyType.DATE,jlibNode,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propPath = createPropertyDef(pjlibPath,true,false,PropertyType.STRING,jlibNode,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propPosition = createPropertyDef(pjlibPosition,true,false,PropertyType.LONG,jlibNode,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propSize = createPropertyDef(pjlibSize,true,false,PropertyType.LONG,jlibNode,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propRestrictions = createPropertyDef(pjlibRestrictions,true,true,PropertyType.REFERENCE,jlibNode,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propLockToken = createPropertyDef(pmixLockToken,false,false,PropertyType.STRING,jlibNode,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propLockUser = createPropertyDef(pjlibLockUser,false,false,PropertyType.STRING,jlibNode,OnParentVersionAction.COPY,false,null);
		
		QPropertyDefinition propAuthor = createPropertyDef(pjlibAuthor,true,false,PropertyType.REFERENCE,jlibDocument,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propLanguage = createPropertyDef(pjlibLanguage,true,false,PropertyType.STRING,jlibDocument,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propKeywords = createPropertyDef(pjlibKeywords,true,false,PropertyType.STRING,jlibDocument,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propCreationDate = createPropertyDef(pjlibCreationDate,true,false,PropertyType.DATE,jlibDocument,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propURL = createPropertyDef(pjlibURL,false,false,PropertyType.STRING,jlibDocument,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propTitle = createPropertyDef(pjlibTitle,true,false,PropertyType.STRING,jlibDocument,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propCategories = createPropertyDef(pjlibCategories,true,true,PropertyType.REFERENCE,jlibDocument,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propResources = createPropertyDef(pjlibResources,true,true,PropertyType.REFERENCE,jlibDocument,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propRelations = createPropertyDef(pjlibRelations,true,true,PropertyType.REFERENCE,jlibDocument,OnParentVersionAction.COPY,false,null);
		
		QPropertyDefinition propNodes = createPropertyDef(pjlibNodes,true,true,PropertyType.REFERENCE,jlibCategory,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propCategoryDate = createPropertyDef(pjlibDate,true,false,PropertyType.DATE,jlibCategory,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propCategoryDescription = createPropertyDef(pjlibDescription,true,false,PropertyType.STRING,jlibCategory,OnParentVersionAction.COPY,false,null);

		QPropertyDefinition propNoteDate = createPropertyDef(pjlibDate,true,false,PropertyType.DATE,jlibNote,OnParentVersionAction.COPY,false,null);		
		QPropertyDefinition propText = createPropertyDef(pjlibText,true,false,PropertyType.STRING,jlibNote,OnParentVersionAction.COPY,false,null);		
		// New from jLibrary 1.0 final
		QPropertyDefinition propNoteUser = createPropertyDef(pjlibUser,false,false,PropertyType.REFERENCE,jlibNote,OnParentVersionAction.COPY,false,null);		
		
		QPropertyDefinition propBookmarkTypecode = createPropertyDef(pjlibTypecode,true,false,PropertyType.STRING,jlibBookmark,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propBookmarkURL = createPropertyDef(pjlibURL,false,false,PropertyType.STRING,jlibBookmark,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propBookmarkUSer = createPropertyDef(pjlibUser,true,false,PropertyType.STRING,jlibBookmark,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propBookmarkDescription = createPropertyDef(pjlibDescription,true,false,PropertyType.STRING,jlibBookmark,OnParentVersionAction.COPY,false,null);
		
		QPropertyDefinition propBio = createPropertyDef(pjlibBio,true,false,PropertyType.STRING,jlibAuthor,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propAuthorUser = createPropertyDef(pjlibUser,false,false,PropertyType.STRING,jlibAuthor,OnParentVersionAction.COPY,false,null);

		QPropertyDefinition propMembers = createPropertyDef(pjlibMembers,true,true,PropertyType.REFERENCE,jlibRol,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propRolDescription = createPropertyDef(pjlibDescription,true,false,PropertyType.STRING,jlibRol,OnParentVersionAction.COPY,false,null);
		
		QPropertyDefinition propFavoriteUser= createPropertyDef(pjlibUser,false,false,PropertyType.REFERENCE,jlibFavorite,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propNode= createPropertyDef(pjlibNode,true,false,PropertyType.REFERENCE,jlibFavorite,OnParentVersionAction.COPY,false,null);
		
		QPropertyDefinition propUsers = createPropertyDef(pjlibUsers,true,true,PropertyType.REFERENCE,jlibGroup,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propGroupRoles = createPropertyDef(pjlibRoles,true,true,PropertyType.REFERENCE,jlibGroup,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propGroupDescription = createPropertyDef(pjlibDescription,true,false,PropertyType.STRING,jlibGroup,OnParentVersionAction.COPY,false,null);
		
		QPropertyDefinition propGroups = createPropertyDef(pjlibGroups,true,true,PropertyType.REFERENCE,jlibUser,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propUserRoles = createPropertyDef(pjlibRoles,true,true,PropertyType.REFERENCE,jlibUser,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propFirstName = createPropertyDef(pjlibFirstName,false,false,PropertyType.STRING,jlibUser,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propLastName = createPropertyDef(pjlibLastName,false,false,PropertyType.STRING,jlibUser,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propEmail = createPropertyDef(pjlibEmail,false,false,PropertyType.STRING,jlibUser,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propPassword = createPropertyDef(pjlibPassword,true,false,PropertyType.STRING,jlibUser,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propIsAdmin = createPropertyDef(pjlibIsAdmin,false,false,PropertyType.BOOLEAN,jlibUser,OnParentVersionAction.COPY,false,null);
		QPropertyDefinition propIsSysAdmin = createPropertyDef(pjlibIsSysadmin,false,false,PropertyType.BOOLEAN,jlibUser,OnParentVersionAction.COPY,false,null);
		
		
		ntdJLibrary = setPropertyDefsToQNTD(ntdJLibrary, new QPropertyDefinition[]{propName,propActive});
		
		ntdNode = setPropertyDefsToQNTD(ntdNode, new QPropertyDefinition[]{propDescription,
													  propCreator,
													  propImportance,
													  propTypecode,
													  propCreated,
													  propPath,
													  propPosition,
													  propSize,
													  propRestrictions,
													  propLockToken,
													  propLockUser});
		
			
		
		ntdDocument = setPropertyDefsToQNTD(ntdDocument, new QPropertyDefinition[]{propAuthor,
															  propLanguage,
															  propKeywords,
															  propCreationDate,
															  propURL,
															  propTitle,
															  propCategories,
															  propResources,
															  propRelations});
		
		
		ntdCategory = setPropertyDefsToQNTD(ntdCategory, new QPropertyDefinition[]{propNodes,
															  propCategoryDate,	
															  propCategoryDescription});
		
		
		ntdBookmark = setPropertyDefsToQNTD(ntdBookmark, new QPropertyDefinition[]{propBookmarkTypecode,
															  propBookmarkURL,
															  propBookmarkUSer,
															  propBookmarkDescription});
		
		
		ntdNote = setPropertyDefsToQNTD(ntdNote, new QPropertyDefinition[]{propNoteDate,
													  propText,
													  propNoteUser});
											
		ntdAuthor = setPropertyDefsToQNTD(ntdAuthor, new QPropertyDefinition[]{propBio,propAuthorUser});
		
	
		ntdRol = setPropertyDefsToQNTD(ntdRol, new QPropertyDefinition[]{propMembers,propRolDescription});
		
		
		ntdGroup = setPropertyDefsToQNTD(ntdGroup, new QPropertyDefinition[]{propUsers,
													   propGroupRoles,
													   propGroupDescription});
		
		
		ntdUser = setPropertyDefsToQNTD(ntdUser, new QPropertyDefinition[]{propUserRoles,
													  propGroups,
													  propFirstName,
													  propLastName,
													  propEmail,
													  propPassword,
													  propIsAdmin,
													  propIsSysAdmin});
		
		
		ntdFavorite = setPropertyDefsToQNTD(ntdFavorite, new QPropertyDefinition[]{propFavoriteUser,propNode});
		
		
	}
	
	private void registerNodeDef(Name[] nameDefs,
								 Name declaringNodeType,
								 QNodeTypeDefinition nodeType,
								 int[] onParentVersionAction) {
		
		QNodeDefinitionImpl[] defs = new QNodeDefinitionImpl[nameDefs.length];
		
		QNodeDefinitionBuilder qndb;
		
		for (int i = 0; i < defs.length; i++) {
			
			qndb = new QNodeDefinitionBuilder();
			qndb.setAllowsSameNameSiblings(true);
			qndb.setName(nameDefs[i]);
			qndb.setDeclaringNodeType(declaringNodeType);
			qndb.setOnParentVersion(onParentVersionAction[i]);
								
			defs[i] = new QNodeDefinitionImpl(qndb.build());
			
		}				
		
		nodeType = setChildNodeDefsToQNTD(nodeType, defs);
	}

	private QPropertyDefinition createPropertyDef(Name name,
			  boolean mandatory,
			  boolean multivalued,
			  int type,
			  Name declaringType,
			  int onParentVersionAction,
			  boolean autocreated,
			  InternalValue[] defaultValues) {

		QPropertyDefinitionBuilder pdef = new QPropertyDefinitionBuilder();
		pdef.setName(name);
		pdef.setMandatory(mandatory);
		pdef.setMultiple(multivalued);
		pdef.setRequiredType(type);
		pdef.setDeclaringNodeType(declaringType);
		pdef.setOnParentVersion(onParentVersionAction);
		pdef.setAutoCreated(autocreated);
		
		if (autocreated) {
		pdef.setDefaultValues(defaultValues);
		}
		
		
		return pdef.build();
	}		
	
	private QNodeTypeDefinition createNodeTypeDef(Name[] supertypes, 
										  Name name,
										  boolean mixin) {
		
		QNodeTypeDefinitionBuilder qndb = new QNodeTypeDefinitionBuilder();
						
		qndb.setMixin(mixin);
		qndb.setName(name);
		qndb.setOrderableChildNodes(true);
		qndb.setSupertypes(supertypes);
				
		
		return qndb.build();
	}
	
	private QNodeTypeDefinition setChildNodeDefsToQNTD(QNodeTypeDefinition node ,QNodeDefinition[] childs){
		
		//Para obtener un nuevo QNodeTypeDefinition tengo q setearle
		QNodeTypeDefinitionBuilder qdb = new QNodeTypeDefinitionBuilder();
		qdb.setAbstract(node.isAbstract());
		qdb.setChildNodeDefs(childs);
		qdb.setMixin(node.isMixin());
		qdb.setName(node.getName());
		qdb.setOrderableChildNodes(node.hasOrderableChildNodes());
		qdb.setPrimaryItemName(node.getPrimaryItemName());				
		qdb.setQueryable(node.isQueryable());
		qdb.setSupertypes(node.getSupertypes());
		qdb.setSupportedMixinTypes(node.getSupportedMixinTypes());
		qdb.setPropertyDefs(node.getPropertyDefs()); 
		
		return qdb.build();
	}
	
	private QNodeTypeDefinition setPropertyDefsToQNTD(QNodeTypeDefinition node ,QPropertyDefinition[] childs){
		
		//Para obtener un nuevo QNodeTypeDefinition tengo q setearle
		QNodeTypeDefinitionBuilder qdb = new QNodeTypeDefinitionBuilder();
		qdb.setAbstract(node.isAbstract());
		qdb.setChildNodeDefs(node.getChildNodeDefs());
		qdb.setMixin(node.isMixin());
		qdb.setName(node.getName());
		qdb.setOrderableChildNodes(node.hasOrderableChildNodes());
		qdb.setPrimaryItemName(node.getPrimaryItemName());				
		qdb.setQueryable(node.isQueryable());
		qdb.setSupertypes(node.getSupertypes());
		qdb.setSupportedMixinTypes(node.getSupportedMixinTypes());
		qdb.setPropertyDefs(childs); 
		
		return qdb.build();
	}

}