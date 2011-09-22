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
package org.jlibrary.core.entities;

import java.io.Serializable;

import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * @author martin
 *
 * This node represents a Resource. A resource is a simple document that is referenced 
 * by another documents.
 * 
 * A resource doesn't have any relations, metadata, resources, etc. It simply has a 
 * path and content.
 */
public class ResourceNode extends Node implements Serializable {

	static Logger logger = LoggerFactory.getLogger(ResourceNode.class);
	
	static final long serialVersionUID = 20050504224600L;
	private ResourceNode backup;
	
    /** default constructor */
    public ResourceNode() {
    	
    	setResource(Boolean.TRUE);
    }
	
    public String toString() {
        return getName();
    }

	/**
	 * Dumps all the properties into a properties object
	 * 
	 * @return ResourceNodePropertes Resource's properties
	 */
	public ResourceNodeProperties dumpProperties() {
		
		ResourceNodeProperties properties = new ResourceNodeProperties();
		try {
			if (getId() != null) {
				properties.put(ResourceNodeProperties.RESOURCE_ID, 
							   new PropertyDef(ResourceNodeProperties.RESOURCE_ID,getId()));
			}
			properties.put(ResourceNodeProperties.RESOURCE_NAME, 
					   new PropertyDef(ResourceNodeProperties.RESOURCE_NAME,getName()));
			properties.put(ResourceNodeProperties.RESOURCE_DESCRIPTION, 
					   new PropertyDef(ResourceNodeProperties.RESOURCE_DESCRIPTION,getDescription()));
			properties.put(ResourceNodeProperties.RESOURCE_PATH, 
					   new PropertyDef(ResourceNodeProperties.RESOURCE_PATH,getPath()));
			properties.put(ResourceNodeProperties.RESOURCE_PARENT_ID, 
					   new PropertyDef(ResourceNodeProperties.RESOURCE_PARENT_ID,getParent()));
			properties.put(ResourceNodeProperties.RESOURCE_TYPECODE, 
					   new PropertyDef(ResourceNodeProperties.RESOURCE_TYPECODE,getTypecode()));
			properties.put(ResourceNodeProperties.RESOURCE_POSITION, 
					   new PropertyDef(ResourceNodeProperties.RESOURCE_POSITION,getPosition()));
			
		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
		}
		
		return properties;
	}

	public void saveState() {
		
		backup = new ResourceNode();
		
		backup.setDescription(getDescription());
		backup.setName(getName());
	}
	
	public void restoreState() {
		
		setDescription(backup.getDescription());
		setName(backup.getName());
	}	
}
