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
package org.jlibrary.core.jcr.nodetypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.version.OnParentVersionAction;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.commons.cnd.CompactNodeTypeDefReader;
import org.apache.jackrabbit.commons.cnd.DefinitionBuilderFactory;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;

import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;

import org.apache.jackrabbit.core.value.InternalValue;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.commons.QPropertyDefinitionImpl;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceMapping;
import org.apache.jackrabbit.spi.commons.namespace.SessionNamespaceResolver;
import org.apache.jackrabbit.spi.commons.nodetype.QDefinitionBuilderFactory;
import org.apache.jackrabbit.spi.commons.nodetype.QNodeTypeDefinitionBuilder;
import org.apache.jackrabbit.spi.commons.nodetype.QPropertyDefinitionBuilder;
import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeTypeManager {

	static Logger logger = LoggerFactory.getLogger(NodeTypeManager.class);

	private static final String DEFAULT_CND_LOCATION = "jlibrary.cnd";

	private NamespaceResolver namespaceResolver;
	
	public NodeTypeManager() {
		
		namespaceResolver = new NamespaceResolver();
	}
		
	/**
	 * Registers node types on a session
	 * 
	 * @param session Session
	 * 
	 * @throws RepositoryException If the node types can't be registered
	 */
	@SuppressWarnings("unchecked")
	public void registerNodeTypes(Session session) 
									throws RepositoryException {
		
		Workspace wsp = session.getWorkspace();
		namespaceResolver.registerAvailableNamespaces(wsp);
		session.save();
		
		InputStream stream = getClass().getClassLoader().getResourceAsStream(DEFAULT_CND_LOCATION);
		if (stream == null) {
			logger.error("CND file could not be found");
			throw new RepositoryException("CND file could not be found");				
		}
		CompactNodeTypeDefReader cndReader;
		try {
	        // Read in the CND file
	        InputStreamReader reader = new InputStreamReader(stream);
	        
	        // Create a CompactNodeTypeDefReader
	        cndReader = new CompactNodeTypeDefReader(reader, DEFAULT_CND_LOCATION, new QDefinitionBuilderFactory());
	  
	        
	        //CompactNodeTypeDefReader<QNodeTypeDefinition,NamespaceMapping> readerch = new CompactNodeTypeDefReader<QNodeTypeDefinition, NamespaceMapping>(reader, DEFAULT_CND_LOCATION, mapping, new QDefinitionBuilderFactory());
	        
		} catch (ParseException e) {
			logger.error("CND file could not be parsed");
			throw new RepositoryException("CND file could not be parsed",e);	
		} finally {
			IOUtils.closeQuietly(stream);
		}
		
        // Get the List of NodeTypeDef objects
        List ntdList = cndReader.getNodeTypeDefinitions();
        
        // Get the NodeTypeManager from the Workspace.
        // Note that it must be cast from the generic JCR NodeTypeManager to the
        // Jackrabbit-specific implementation.
        NodeTypeManagerImpl ntmgr =(NodeTypeManagerImpl)wsp.getNodeTypeManager();

        // Acquire the NodeTypeRegistry
        NodeTypeRegistry ntreg = ntmgr.getNodeTypeRegistry();

        // Loop through the prepared NodeTypeDefs
        for (Iterator i = ntdList.iterator(); i.hasNext();) {

            // Get the NodeTypeDef...
            QNodeTypeDefinition ntd = (QNodeTypeDefinition)i.next();
            if (ntreg.isRegistered(ntd.getName())) {
            	logger.debug("Nodetype " + ntd.getName() + " is already registered. Skipping it.");
            } else {
            	logger.debug("Registering nodetype " + ntd.getName());
	            // ...and register it
	            try {
					ntreg.registerNodeType(ntd);
				} catch (InvalidNodeTypeDefException e) {
					logger.error("Impossible to register nodetype " + ntd.getName(),e);
				}
            }
        }	
		session.save();
	}


	
	public void checkNodeTypes(javax.jcr.Session Session) {
		
		logger.debug("Nodetype checking has been disabled since jLibrary 1.1");
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
		
		Name jlibDocument = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"document");
		try {
			InternalValue[] values = null;
			if (property.getDefaultValues() != null) {
				values = JCRUtils.getInternalValues(property.getDefaultValues());
			}			
			
			Name propertyName = null;
			if (property.getQName() != null) {
				propertyName = property.getQName();
			} else {
				propertyName = createCustomPropertyType(property.getName());
			}
			
			QPropertyDefinition customProperty = createCustomPropertyDef(
					propertyName, 
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

			if (!isCustomPropertyRegistered(session, propertyName)) {
				QPropertyDefinition[] newProperties = new QPropertyDefinitionImpl[properties.length+1];
				System.arraycopy(properties,0,newProperties,0,properties.length);
				newProperties[properties.length] = customProperty;	
				
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
				qdb.setPropertyDefs(newProperties);
				
				def = qdb.build();
				
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
		
		Name propQName = createCustomPropertyType(propertyName);
		unregisterCustomProperty(session, propQName);
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
	 * @param propQName QName of the property
	 * 
	 * @throws RepositoryException If the property can�t be unregistered
	 */
	public void unregisterCustomProperty(
			javax.jcr.Session session, 
			Name propName) throws RepositoryException {
		
		try {			
			Name jlibDocument = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"document");
			
			Workspace wsp = session.getWorkspace();
			javax.jcr.nodetype.NodeTypeManager ntMgr = wsp.getNodeTypeManager();
			NodeTypeRegistry ntReg = 
		        ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
			QNodeTypeDefinition def = ntReg.getNodeTypeDef(jlibDocument);
			QPropertyDefinition[] properties = def.getPropertyDefs();

			if (isCustomPropertyRegistered(session, propName)) {
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
				
		// Externally, with this method we can only check jLibrary extended properties
		Name propName = createCustomPropertyType(propertyName);
		return isCustomPropertyRegistered(session, propName);	
	}
	
	/**
	 * Says if a custom property has been registered in a given repository.
	 *  
	 * @param session Session
	 * @param propQName QName of the property to check
	 * 
	 * @return boolean <code>true</code> if the property has been already registered and <code>false</code> otherwise.
	 * 
	 * @throws RepositoryException If the check cannot be performed
	 */
	public boolean isCustomPropertyRegistered(
			javax.jcr.Session session, Name propQName) throws RepositoryException {
		
		Name jlibDocument = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"document");
		Workspace wsp = session.getWorkspace();
		javax.jcr.nodetype.NodeTypeManager ntMgr = wsp.getNodeTypeManager();
		NodeTypeRegistry ntReg = 
	        ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
		QNodeTypeDefinition def = ntReg.getNodeTypeDef(jlibDocument);
		QPropertyDefinition[] properties = def.getPropertyDefs();
		// check
		boolean found = false;
		for (int i = 0; i < properties.length; i++) {
			if (properties[i].getName().equals(propQName)) {
				// Already exists
				found = true;
				break;
			}
		}		
		return found;	
	}
	
	private Name createCustomPropertyType(String propertyName) {
		
		int colon = propertyName.lastIndexOf(":");
		propertyName = propertyName.substring(colon+1,propertyName.length());
		return NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_EXTENDED_URL,propertyName);
	}
	
	private QPropertyDefinition createCustomPropertyDef(
			Name propName, 
			boolean multivalued, 
			int type,
			boolean autocreated,
			InternalValue[] defaultValues) {
		
		Name jlibDocument = NameFactoryImpl.getInstance().create(JLibraryConstants.JLIBRARY_URL,"document");
		
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
}
