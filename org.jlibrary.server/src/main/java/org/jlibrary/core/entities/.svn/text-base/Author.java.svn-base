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

import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author entity
 * 
 * @author mpermar
 *
 */
public class Author implements Serializable, IResource {

	static Logger logger = LoggerFactory.getLogger(Author.class);
	
	static final long serialVersionUID = -6195028498718624194L;
	
	public static final String UNKNOWN_NAME = "author_unknown";
	public static final String UNKNOWN_CODE = "-1";
	public static transient Author UNKNOWN = 
		new Author(UNKNOWN_CODE,UNKNOWN_NAME,UNKNOWN_NAME);
	
    /** identifier field */
    private String id;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String bio;
    
    private String repository;
    
    private boolean active;

    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/** full constructor */
    public Author(String id,java.lang.String name, java.lang.String bio) {
    	this.id = id;
        this.name = name;
        this.bio = bio;
    }

    /** default constructor */
    public Author() {
    }

    public java.lang.String getId() {
        return this.id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }
    public java.lang.String getName() {
        return this.name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }
    public java.lang.String getBio() {
        return this.bio;
    }

    public void setBio(java.lang.String description) {
        this.bio = description;
    }


    public String toString() {
        return getName();
    }

    public boolean equals(Object other) {
        if ( !(other instanceof Author) ) return false;
        
        return getName().equals(((Author)other).getName());
    }

    public int hashCode() {
        return getId().hashCode();
    }

	public String getRepository() {
		return repository;
	}
	public void setRepository(String repository) {
		this.repository = repository;
	}
	
	public boolean isUnknown() {
		
		return getId().equals(Author.UNKNOWN_CODE);
	}
	
	/**
	 * Dumps all the properties into a properties object
	 * 
	 * @return CategoryProperties Category's properties
	 */
	public AuthorProperties dumpProperties()
	{
		AuthorProperties properties = new AuthorProperties();
		try
		{
			properties.put(AuthorProperties.AUTHOR_ID,
					new PropertyDef(AuthorProperties.AUTHOR_ID,getId()));
			properties.put(AuthorProperties.AUTHOR_REPOSITORY,
					new PropertyDef(AuthorProperties.AUTHOR_REPOSITORY,getRepository()));
			properties.put(AuthorProperties.AUTHOR_NAME,
					new PropertyDef(AuthorProperties.AUTHOR_NAME,getName()));
			properties.put(AuthorProperties.AUTHOR_BIO,
					new PropertyDef(AuthorProperties.AUTHOR_BIO,getBio()));

		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
		}

		return properties;
	}	
}
