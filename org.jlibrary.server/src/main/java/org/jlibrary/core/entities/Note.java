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

import org.apache.commons.lang.builder.ToStringBuilder;

/** @author Hibernate CodeGenerator */
public class Note implements Serializable {
	
	static final long serialVersionUID = 6434221605700145900L;
	
    /** identifier field */
    private String id;

    private String note;

    private java.util.Date date;
    
    private Node node;
    
    private String creator;
    
    /** default constructor */
    public Note() {}


    public java.lang.String getId() {
        return this.id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public boolean equals(Object other) {
        
    	if (other == null) return false;
    	if (!(other instanceof Note)) {
    		return false;
    	}
    	
    	return note.equals(((Note)other).getNote());
    }

	/**
	 * @return Returns the user.
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param user The user to set.
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * @return Returns the url.
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param url The url to set.
	 */
	public void setNote(String note) {
		this.note = note;
	}
	/**
	 * @return Returns the date.
	 */
	public java.util.Date getDate() {
		return date;
	}

	/**
	 * @param date The date to set.
	 */
	public void setDate(java.util.Date date) {
		this.date = date;
	}

    public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}	
}
