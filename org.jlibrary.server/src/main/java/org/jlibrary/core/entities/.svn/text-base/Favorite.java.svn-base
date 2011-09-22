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

/** @author Hibernate CodeGenerator */
public class Favorite implements Serializable {
	
	static final long serialVersionUID = -6773857335395070176L;
	
    /** identifier field */
    private String id;

    /** nullable persistent field */
    private String user;
    
    private String category;

    /** nullable persistent field */
    private String document;

    private String repository;
    
    /** default constructor */
    public Favorite() {}


    public java.lang.String getId() {
        return this.id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }
	/**
	 * @return Returns the category.
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category The category to set.
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return Returns the node.
	 */
	public String getDocument() {
		return document;
	}
	/**
	 * @param node The node to set.
	 */
	public void setDocument(String document) {
		this.document = document;
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
	
    public boolean equals(Object other) {
    	
        if ( !(other instanceof Favorite) ) return false;
        Favorite castOther = (Favorite) other;
        if (id == null) return false;
        return id.equals(castOther.id);
    }

    public int hashCode() {
    	
    	if (id == null) return -1;
    	
    	return id.hashCode();
    }

    /**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		StringBuffer buffer = new StringBuffer("[");
		buffer.append((document==null) ? "" : document);
		buffer.append(",");
		buffer.append((user==null) ? "" : user);
		buffer.append(",");
		buffer.append((category==null) ? "" : category);
		buffer.append("]");
		return buffer.toString();
	}


	public String getRepository() {
		return repository;
	}


	public void setRepository(String repository) {
		this.repository = repository;
	}
}
