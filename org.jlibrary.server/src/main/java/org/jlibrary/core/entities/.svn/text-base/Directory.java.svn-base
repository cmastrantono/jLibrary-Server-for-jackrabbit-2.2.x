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

import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A directory within a repository
 * 
 * @author martin
 */
public class Directory extends Node implements Serializable {

	static Logger logger = LoggerFactory.getLogger(Directory.class);
	
	static final long serialVersionUID = 1089125344561609944L;
	
    /** default constructor */
    public Directory() {
    	
    	setDirectory(Boolean.TRUE);
    }

    public void copyData(Directory directory) {
    	
    	super.copyData(directory);
    }

    public String toString() {
        return getName();
    }	
	
	/**
	 * Dumps all the properties into a properties object
	 * 
	 * @return DirectoryProperties Directory's properties
	 */
	public DirectoryProperties dumpProperties() {
		
		DirectoryProperties properties = new DirectoryProperties();
		try {
			if (getId() != null) {
				properties.put(DirectoryProperties.DIRECTORY_ID, 
							   new PropertyDef(DirectoryProperties.DIRECTORY_ID,getId()));
			}
			properties.put(DirectoryProperties.DIRECTORY_NAME, 
					   new PropertyDef(DirectoryProperties.DIRECTORY_NAME,getName()));
			properties.put(DirectoryProperties.DIRECTORY_PARENT, 
					   new PropertyDef(DirectoryProperties.DIRECTORY_PARENT,getParent()));
			properties.put(DirectoryProperties.DIRECTORY_DESCRIPTION, 
					   new PropertyDef(DirectoryProperties.DIRECTORY_DESCRIPTION,getDescription()));
			properties.put(DirectoryProperties.DIRECTORY_POSITION, 
					   new PropertyDef(DirectoryProperties.DIRECTORY_POSITION,getPosition()));
			
		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
		}
		
		return properties;
	}
}
