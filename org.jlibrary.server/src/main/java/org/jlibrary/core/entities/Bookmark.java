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
import java.util.Set;

/** @author Hibernate CodeGenerator */
public class Bookmark implements Serializable, IResource {
	
	static final long serialVersionUID = 7277729504430824963L;
	
	public static final String FAVORITE = "B";
	public static final String FOLDER = "F";
	
    /** identifier field */
    private String id;

    /** nullable persistent field */
    private String name;
    
    private String url;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private org.jlibrary.core.entities.Bookmark parent;
    
    private Set bookmarks;
    
    private String type;
    
    private String user;
    
    private String repository;

    /** default constructor */
    public Bookmark() {}


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
    public java.lang.String getDescription() {
        return this.description;
    }

    public void setDescription(java.lang.String description) {
        this.description = description;
    }
    public org.jlibrary.core.entities.Bookmark getParent() {
        return this.parent;
    }

    public void setParent(org.jlibrary.core.entities.Bookmark parent) {
        this.parent = parent;
    }

    public String toString() {

		if (isFolder()) {
			return name;
		}
		return url;
    }

    public boolean equals(Object other) {
        
    	if (other == null) return false;
    	if (!(other instanceof Bookmark)) {
    		return false;
    	}
    	return getId().equals(((Bookmark)other).getId());
    }

    public int hashCode() {
        
    	return id.hashCode();
    }

	/**
	 * @return Returns the favorites.
	 */
	public Set getBookmarks() {
		return bookmarks;
	}

	/**
	 * @param favorites The favorites to set.
	 */
	public void setBookmarks(Set bookmarks) {
		
		this.bookmarks = bookmarks;
	}

	/**
	 * @return Returns the url.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url The url to set.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}

	public boolean isFolder() {
		
		if (type == null) {
			return false;
		}
		return type.equals(FOLDER);
	}
	
	
	/**
	 * @return Returns the user.
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user The user to set.
	 */
	public void setUser(String user) {
		this.user = user;
	}


	public String getRepository() {
		return repository;
	}


	public void setRepository(String repository) {
		this.repository = repository;
	}
}
