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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

import org.apache.commons.lang.ArrayUtils;
import org.apache.jackrabbit.core.value.InternalValue;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.value.ValueFactoryImpl;
import org.jlibrary.core.properties.RepositoryProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Several utils for repository management
 * 
 * @author martin
 *
 */
public class JCRUtils {

	static Logger logger = LoggerFactory.getLogger(JCRUtils.class);
	
	/**
	 * Adds a node reference to a property. This method won't add duplicate 
	 * values, so if the node already has that reference, then the reference 
	 * won't be added
	 * 
	 * @param uuid String uuid of the referenced node
	 * @param node Node that owns the property
	 * @param propertyId Property key
	 * 
	 * @throws javax.jcr.RepositoryException If the node can't be added
	 */
	public static void addNodeToProperty(String uuid, 
			   							 javax.jcr.Node node,
			   							 String propertyId) 
											throws javax.jcr.RepositoryException {

		Session session = node.getSession();
		Node nodeValue = session.getNodeByUUID(uuid);		

		addNodeToProperty(nodeValue,node,propertyId);		
	}	
	
	/**
	 * Adds a node reference to a property. This method won't add duplicate 
	 * values, so if the node already has that reference, then the reference 
	 * won't be added
	 * 
	 * @param value Node value
	 * @param node Node that owns the property
	 * @param propertyId Property key
	 * 
	 * @throws javax.jcr.RepositoryException If the node can't be added
	 */
	public static void addNodeToProperty(javax.jcr.Node nodeValue, 
			   							 javax.jcr.Node node,
			   							 String propertyId) 
											throws javax.jcr.RepositoryException {

		Session session = node.getSession();
		if (!node.hasProperty(propertyId)) {
			node.setProperty(propertyId,new Value[]{});
		}
		Property property = node.getProperty(propertyId);
		Value value = session.getValueFactory().createValue(nodeValue);
		
		Value[] propertyValues = property.getValues();
		if (ArrayUtils.contains(propertyValues,value)) {
			// Already referenced
			return;
		}
		Value[] newValues = new Value[propertyValues.length+1];
		System.arraycopy(propertyValues,0,newValues,0,propertyValues.length);
		newValues[propertyValues.length] = value;
		property.setValue(newValues);
	}	
	
	
	/**
	 * Adds a string to the values of a given property
	 * 
	 * @param property Property
	 * @param stringValue String value to be added
	 * 
	 * @throws javax.jcr.RepositoryException If the string object can't be added
	 */
	public static void addToProperty(javax.jcr.Property property,
			   						 String stringValue) 
										throws javax.jcr.RepositoryException {

		Session session = property.getSession();
		Value value = session.getValueFactory().createValue(stringValue);
		
		Value[] propertyValues = property.getValues();
		if (ArrayUtils.contains(propertyValues,value)) {
			// Already on the property values
			return;
		}
		Value[] newValues = new Value[propertyValues.length+1];
		System.arraycopy(propertyValues,0,newValues,0,propertyValues.length);
		newValues[propertyValues.length] = value;
		property.setValue(newValues);
	}	
	
	/**
	 * Removes a node reference from a property
	 * 
	 * @param uuid String uuid of the referenced node
	 * @param node Node that owns the property
	 * @param propertyId Property key
	 * 
	 * @throws javax.jcr.RepositoryException If the node can't be removed
	 */	
	public static void removeNodeFromProperty(String uuid, 
				 							  javax.jcr.Node node,
				 							  String propertyId) 
												throws javax.jcr.RepositoryException {
	
		Property property = node.getProperty(propertyId);
	
		Value[] propertyValues = property.getValues();
		for (int i = 0; i < propertyValues.length; i++) {
			if (propertyValues[i].getString().equals(uuid)) {
				propertyValues[i] = null;
			}
		}
		property.setValue(propertyValues);
	}
	
	/**
	 * Removes a node reference from a property
	 * 
	 * @param nodeValue Referenced node
	 * @param node Node that owns the property
	 * @param propertyId Property key
	 * 
	 * @throws javax.jcr.RepositoryException If the node can't be removed
	 */	
	public static void removeNodeFromProperty(javax.jcr.Node nodeValue, 
				 							  javax.jcr.Node node,
				 							  String propertyId) 
												throws javax.jcr.RepositoryException {

		String uuid = nodeValue.getUUID();
		removeNodeFromProperty(uuid,node,propertyId);
	}	
	
	/**
	 * Returns the root node for a repository
	 * 
	 * @param session Session
	 * 
	 * @return javax.jcr.Node Root node
	 * 
	 * @throws RepositoryException If the node can't be found
	 */
	public static javax.jcr.Node getRootNode(Session session) 
									throws javax.jcr.RepositoryException {
		
		javax.jcr.Node root = session.getRootNode();
		return root.getNode(JLibraryConstants.JLIBRARY_ROOT);
	}
	
	/**
	 * Returns the system node for a repository
	 * 
	 * @param session Session
	 * 
	 * @return javax.jcr.Node System node
	 * 
	 * @throws RepositoryException If the node can't be found
	 */
	public static javax.jcr.Node getSystemNode(Session session) 
									throws javax.jcr.RepositoryException {
		
		javax.jcr.Node root = session.getRootNode();
		if (root.hasNode(JLibraryConstants.JLIBRARY_SECURITY)) {
			// System workspace
			return root;
		}
		return root.getNode(JLibraryConstants.JLIBRARY_SYSTEM);
	}	
		
	/**
	 * Removes recursively node references
	 * 
	 * @param node Node that is going to be removed
	 * 
	 * @throws javax.jcr.RepositoryException If the node can't be removed
	 */
	public static void removeReferences (javax.jcr.Node node) 
										throws javax.jcr.RepositoryException {
		
		PropertyIterator it = node.getReferences();
		while (it.hasNext()) {
			Property property = (Property) it.next();
			if (property.getDefinition().isMultiple()) {
				Value[] values = property.getValues();
				for (int i = 0; i < values.length; i++) {
					String uuid = values[i].getString();
					if (uuid.equals(node.getUUID())) {
						values[i] = null;
					}
				}
				property.setValue(values);
			} else {
				property.setValue((Value)null);
			}
		}			
		
		NodeIterator nit = node.getNodes();
		while (nit.hasNext()) {
			javax.jcr.Node child = (javax.jcr.Node) nit.next();
			removeReferences(child);
		}
	}	
	
	/**
	 * Creates and returns an object representing an JCR value
	 * 
	 * @param value Value to obtain its object
	 * 
	 * @return Object Object representing that value
	 */
	public static String getStringValue(Value value) {
		
		try {
			switch(value.getType()) {
				case PropertyType.BINARY : return "binary content";
				case PropertyType.BOOLEAN : return String.valueOf(value.getBoolean());
				case PropertyType.DATE : return value.getDate().getTime().toString();
				case PropertyType.DOUBLE : return String.valueOf(value.getDouble());
				case PropertyType.LONG : return String.valueOf(value.getLong());
				case PropertyType.REFERENCE : return value.getString();
				case PropertyType.STRING : return value.getString();
				case PropertyType.NAME : return value.getString();
				case PropertyType.UNDEFINED : return "undefined";
			}
		} catch (ValueFormatException vfe) {
			return null;
		} catch (RepositoryException re) {
			return null;
		}
		return null;
	}
	
	/**
	 * Builds a valid name for a child node
	 * 
	 * @param parent Parent node
	 * @param extension Extension for the Node. It can be null if we do not 
	 * want to append an extension to the node name.
	 * @param name Initial name
	 * 
	 * @return A valid name. First the method will escape the passed name to 
	 * remove wrong characters. Next, if there is some sibling the method will
	 * try to create a name but enclosed with brackets.
	 * 
	 * @throws RepositoryException If the name can't be obtained
	 */
	public static String buildValidChildNodeName(
							javax.jcr.Node parent,
							String extension,
							String name) throws RepositoryException {
		
		// Fix a bug on jackrabbit scaping engine
		//TODO: Replace this if jackrabbit correctly escapes "'" symbol
		if (name.contains("'")) {
			name = name.replace('\'','_');
		}		
		
		String escapedName = Text.escape(name);
				
		String testString = escapedName;
		if (extension != null) {
			int dotIndex = escapedName.lastIndexOf(".");
			if (dotIndex == -1) {
				testString += extension;
			}
		}
		
		int i = 1;
		while (parent.hasNode(testString) == true) {
			
			javax.jcr.Node node = parent.getNode(testString);
			if (node.getProperty(
					JLibraryConstants.JLIBRARY_ACTIVE).getBoolean() == false) {
				continue;
			}
			
			String unscaped = Text.unescape(escapedName);
			String indexString = (i > 1) ? getIndexString(i-1) : "";
			if (unscaped.endsWith(indexString)) {
				int j = unscaped.lastIndexOf(indexString);
				unscaped = unscaped.substring(0,j);
			}
			escapedName = Text.escape(unscaped+"["+i+"]");
			testString = escapedName;
			i++;
		}
				
		return escapedName;
	}
	
	private static String getIndexString(int index) {
		
		StringBuffer indexString = new StringBuffer();
		indexString.append("[");
		indexString.append(index);
		indexString.append("]");
		
		return indexString.toString();
	}
	
	/**
	 * Tells is a node is active
	 * 
	 * @param node Node to check
	 * 
	 * @return boolean <code>true</code> if the node is active and 
	 * <code>false</code> otherwise
	 * 
	 * @throws RepositoryException If some error happen
	 */
	public static boolean isActive(javax.jcr.Node node) 
										throws RepositoryException {
		
		if (!node.hasProperty(JLibraryConstants.JLIBRARY_ACTIVE)) {
			return true;
		}
		
		return node.getProperty(JLibraryConstants.JLIBRARY_ACTIVE).getBoolean();
	}
	
	/**
	 * Sets a config entry on this workspace. If a config entry with that key 
	 * already exists, then the value will be updated. If there is no config 
	 * entry with such key, then the config entry will be created.
	 * 
	 * @param session Session
	 * @param key Key of the config entry
	 * @param value Value of the config entry
	 * 
	 * @throws RepositoryException If the config entry cannot be set
	 */
	public static void setConfigEntry(javax.jcr.Session session,
									  String key,
									  Boolean value) throws RepositoryException {

		javax.jcr.Node systemNode = getSystemNode(session);		
		javax.jcr.Node configNode = 
			systemNode.getNode(JLibraryConstants.JLIBRARY_CONFIG);
		NodeIterator it = configNode.getNodes();
		while (it.hasNext()) {
			javax.jcr.Node configEntry = (javax.jcr.Node) it.next();
			String configKey = configEntry.getProperty(
							JLibraryConstants.JLIBRARY_CONFIG_KEY).getString();
			if (configKey.equals(key)) {				
				configEntry.setProperty(
							JLibraryConstants.JLIBRARY_CONFIG_VALUE,
							value.booleanValue());
				return;
			}
		}
		// Not found, create a new config entry
		javax.jcr.Node configEntry = 
			configNode.addNode(JLibraryConstants.JLIBRARY_CONFIG_ENTRY,
							   JLibraryConstants.INTERNAL_MIXIN);
		configEntry.setProperty(JLibraryConstants.JLIBRARY_CONFIG_KEY,
								key);
		configEntry.setProperty(JLibraryConstants.JLIBRARY_CONFIG_VALUE,
								value.booleanValue());
	}
	
	/**
	 * Returns the value of a config entry
	 * 
	 * @param session Session
	 * @param key Key of the entry that we are looking for
	 * 
	 * @return Value Value of that entry or <code>null</code> if the entry 
	 * cannot be found
	 */
	public static Value getConfigProperty(Session session, 
								          String key) throws RepositoryException {
		
		javax.jcr.Node systemNode = getSystemNode(session);		
		javax.jcr.Node configNode = 
			systemNode.getNode(JLibraryConstants.JLIBRARY_CONFIG);

		NodeIterator it = configNode.getNodes();
		while (it.hasNext()) {
			javax.jcr.Node configEntry = (javax.jcr.Node) it.next();
			String configKey = configEntry.getProperty(
							JLibraryConstants.JLIBRARY_CONFIG_KEY).getString();
			if (configKey.equals(key)) {				
				return configEntry.getProperty(
						JLibraryConstants.JLIBRARY_CONFIG_VALUE).getValue();
			}
		}
		return null;		
	}
	
	/**
	 * Tells if a given config property exists.
	 * 
	 * @param session Session to check
	 * @param key Key for the property we are looking for
	 * 
	 * @return boolean <code>true</code> if the property exists and 
	 * <code>false</code> otherwise
	 * 
	 * @throws RepositoryException If the config check cannot be done due to 
	 * some error.
	 */
	public static boolean hasConfigProperty(Session session, 
	          			  					String key) throws RepositoryException {

		javax.jcr.Node systemNode = getSystemNode(session);		
		javax.jcr.Node configNode = 
			systemNode.getNode(JLibraryConstants.JLIBRARY_CONFIG);

		NodeIterator it = configNode.getNodes();
		while (it.hasNext()) {
			javax.jcr.Node configEntry = (javax.jcr.Node) it.next();
			String configKey = configEntry.getProperty(
					JLibraryConstants.JLIBRARY_CONFIG_KEY).getString();
			if (configKey.equals(key)) {				
				return true;
			}
		}
		return false;		
	}	
	
	/**
	 * Tells if the jLibrary repository is configured to allow jlibrary nodes 
	 * physical delete operations. When allowed, jLibrary documents, resources 
	 * and directories will be physically deleted. When not allowed, jLibrary 
	 * documents, resources and directories will be marked as non-active but 
	 * will not be physically deleted.
	 * 
	 * @param session Session 
	 * 
	 * @return boolean <code>true</code> if the jlibrary nodes should be 
	 * physically deleted and <code>false</code> otherwise
	 * 
	 * @throws RepositoryException If the property can't be get
	 */
	public static boolean allowsPhysicalDeletes(Session session) 
												throws RepositoryException {
		
		Value physicalDeletes = JCRUtils.getConfigProperty(
				session,
				RepositoryProperties.PHYSICAL_DELETE_DOCUMENTS);
		if (physicalDeletes == null) {
			return true;
		}
		return physicalDeletes.getBoolean();
	}
	
	/**
	 * Deactivates jLibrary nodes a node all his children
	 * 
	 * @param node Node to be deactivated
	 * 
	 * @throws javax.jcr.RepositoryException If the node or his children cannot 
	 * be deactivated
	 */
	public static void deactivate(javax.jcr.Node node) 
									throws javax.jcr.RepositoryException {

		node.setProperty(JLibraryConstants.JLIBRARY_ACTIVE,false);
		NodeIterator it = node.getNodes();
		while (it.hasNext()) {
			javax.jcr.Node child = (javax.jcr.Node) it.next();
			if (child.hasProperty(JLibraryConstants.JLIBRARY_ACTIVE)) {
				deactivate(child);
			}
		}
	}

	/**
	 * Checks if a node needs to be checked in, and checks in it if needed. A 
	 * node will be checked in if it does not have any versions created.
	 * 
	 * @param node Node to be checked
	 * 
	 * @throws RepositoryException If some error happens on the check in 
	 * operation
	 */
	public static void checkinIfNecessary(javax.jcr.Node node) 
													throws RepositoryException {

		int versions = 0;		
		VersionManager vm = node.getSession().getWorkspace().getVersionManager();			
		VersionHistory history = vm.getVersionHistory(node.getCorrespondingNodePath(node.getSession().getWorkspace().getName()));
        for (VersionIterator it = history.getAllLinearVersions(); it.hasNext();) {
          Version version = (Version) it.next();
          if (version.getIdentifier().equals(vm.getBaseVersion(node.getCorrespondingNodePath(node.getSession().getWorkspace().getName())).getIdentifier())) {
        	  continue;
          }
          versions++;
        }
		if (versions == 0) {
			vm.checkin(node.getCorrespondingNodePath(node.getSession().getWorkspace().getName()));		
		}
	}
	
	/**
	 * Check if there are any nodes on a workspace that have a property with 
	 * a given name
	 * 
	 * @param session Repository session
	 * @param property Property to check
	 * 
	 * @return boolean <code>true</code> if there is at least one node on the 
	 * workspace with the given property and <code>false</code> otherwise.
	 * 
	 * @throws RepositoryException If the operation can't be performed
	 */
	public static boolean hasNodesForProperty(Session session,
									    	  String property) 
												throws RepositoryException {

		Workspace workspace = session.getWorkspace();
		QueryManager queryManager = workspace.getQueryManager();
		String statement = "/jcr:root//element(*,jlib:jlibrary)[@" + property + "]";
						
		javax.jcr.query.Query query = 
			queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
		QueryResult result = query.execute();		
		NodeIterator it = result.getNodes();
		return it.hasNext();
	}	
	
	/**
	 * JCR define case-sensitive workspace names. This method will look for a 
	 * workspace name like the one passed as a parameter but ignoring cases. 
	 * 
	 * If the workspace name is found, then it will be returned. Otherwise the 
	 * same name will be returned.
	 * 
	 * @param session Session to perform the check. Probably it will be the 
	 * system session.
	 * @param name Workspace name to lookup
	 * 
	 * @return String Case sensitive name if exists and the same name if a 
	 * match cannot be found
	 */
	public static String lookupWorkspaceName(javax.jcr.Session session,
											 String name) 
													throws RepositoryException {
		
		String[] availableWorkspaces = 
			session.getWorkspace().getAccessibleWorkspaceNames();
		for (int i = 0; i < availableWorkspaces.length; i++) {
			if (availableWorkspaces[i].equalsIgnoreCase(name)) {
				return availableWorkspaces[i];
			}
		}
		return name;
	}
	
	/**
	 * Tells if a repository is really from jLibrary
	 * 
	 * @param session Session for the repository
	 * 
	 * @return boolean If the repository is not a jLibrary repository
	 * 
	 * @throws RepositoryException If there is some error doing the operation
	 */
	public static boolean isJLibraryRepository(javax.jcr.Session session) 
													throws RepositoryException {
		
		javax.jcr.Node root = session.getRootNode();
		return root.hasNode(JLibraryConstants.JLIBRARY_ROOT);		
	}
	
	/**
	 * Returns an array of internal values for the given objects. 
	 * 
	 * @param objects Objects
	 * 
	 * @return InternalValue[] Array with the associated internal values
	 */
	public static InternalValue[] getInternalValues(Object[] objects) {
		
		InternalValue[] values = new InternalValue[objects.length];
		for (int i=0;i<values.length;i++) {
			values[i] = getInternalValue(objects[i]);
		}	
		return values;
	}
	
	/**
	 * Returns an array of values for the given objects. 
	 * 
	 * @param objects Objects
	 * 
	 * @return InternalValue[] Array with the associated values
	 */
	public static Value[] getValues(Object[] objects) {
		
		Value[] values = new Value[objects.length];
		for (int i=0;i<values.length;i++) {
			values[i] = getValue(objects[i]);
		}	
		return values;
	}	
	
	/**
	 * Returns a value for the given object
	 * 
	 * @param object Object to transform
	 * 
	 * @return Value Associated value or <code>null</code> if the object cannot be 
	 * transformed
	 */
	public static Value getValue(Object object) {

		ValueFactory valueFactory = ValueFactoryImpl.getInstance();
		if (object instanceof Long) {
			return valueFactory.createValue(((Long)object).longValue());
		} else if (object instanceof Double) {
			return valueFactory.createValue(((Double)object).doubleValue());
		} else if (object instanceof BigDecimal) {
			return valueFactory.createValue(((BigDecimal)object).doubleValue());
		} else if (object instanceof Integer) {
			return valueFactory.createValue(((Integer)object).intValue());
		} else if (object instanceof String) {
			return valueFactory.createValue((String)object);
		} else if (object instanceof Calendar) {
			return valueFactory.createValue((Calendar)object);
		} else if (object instanceof InputStream) {
			return valueFactory.createValue((InputStream)object);
		}
		return null;
	}	
	
	/**
	 * Returns an internal value for the given object
	 * 
	 * @param object Object to transform
	 * 
	 * @return InternalValue Associated value or <code>null</code> if the object cannot be 
	 * transformed
	 */
	public static InternalValue getInternalValue(Object object) {

		if (object instanceof Long) {
			return InternalValue.create(((Long)object).longValue());
		} else if (object instanceof Double) {
			return InternalValue.create(((Double)object).doubleValue());
		} else if (object instanceof BigDecimal) {
			return InternalValue.create(((BigDecimal)object).doubleValue());
		} else if (object instanceof Integer) {
			return InternalValue.create(((Integer)object).intValue());
		} else if (object instanceof String) {
			return InternalValue.create((String)object);
		} else if (object instanceof Calendar) {
			return InternalValue.create((Calendar)object);
		} else if (object instanceof Boolean) {
			return InternalValue.create((Boolean)object);			
		} else if (object instanceof File) {
			try {
				return InternalValue.create(new FileInputStream((File)object));
			} catch (IOException ioe) {logger.error(ioe.getMessage(),ioe);
			} catch (RepositoryException e) {logger.error(e.getMessage(),e);}
		} else if (object instanceof InputStream) {
			try {
				return InternalValue.create((InputStream)object);
			} catch (RepositoryException re) {logger.error(re.getMessage(),re);}
		} else if (object instanceof byte[]) {
			return InternalValue.create((byte[])object);
		}
		return null;
	}

	/**
	 * Returns an array of objects for the given values. 
	 * 
	 * @param values Values
	 * 
	 * @return Object[] Array with the associated objects
	 */
	public static Object[] getObjects(Value[] values) {
		
		Object[] objects = new Object[values.length];
		for (int i=0;i<values.length;i++) {
			objects[i] = getObject(values[i]);
		}	
		return objects;
	}	
	
	public static Object getObject(Value value) {

		try {
			if (value.getType() == PropertyType.DOUBLE) {
				return value.getDouble();
			} else if (value.getType() == PropertyType.DATE) {
				return value.getDate().getTime();
			} else if (value.getType() == PropertyType.LONG) {
				return new Integer((int)value.getLong());
			} else if (value.getType() == PropertyType.STRING) {
				return value.getString();
			} else if (value.getType() == PropertyType.BOOLEAN) {
				return value.getBoolean();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}	
}
