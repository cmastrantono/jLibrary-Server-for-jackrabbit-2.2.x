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
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @author Hibernate CodeGenerator */
public class DocumentMetaData implements Serializable {

	static final long serialVersionUID = -8282968020686009935L;
	
	public static final String UNKNOWN_LANGUAGE = "n/a";
	
   /**
    * Internal document id
    */
    private String id;

    /**
     * The keywords of the document. This is a mandatory field. It will be used
     * to index the document
     */
    private String keywords;

    /**
     * The title of the document. This is a mandatory field.
     */
    private String title;

    /**
     * The author of the document. This is a mandatory field. 
     */
    private Author author;

    /**
     * A reference URL for the document. 
     */
    private String url;
    
    /**
     * The date where the document was created
     */
    private Date date;

    /**
     * This attribute represents the language of the documents. It has two 
     * very important uses:
     * 
     * 1st - Tells us which analyzer use to index the document
     * 2nd - Tells us which stop words list use to index the document
     */
    private String language;
    

    /** default constructor */
    public DocumentMetaData() {
    }

    public java.lang.String getId() {
        return this.id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }
    public java.lang.String getKeywords() {
        return this.keywords;
    }

    public void setKeywords(java.lang.String keywords) {
        this.keywords = keywords;
    }
    public java.lang.String getTitle() {
        return this.title;
    }

    public void setTitle(java.lang.String title) {
        this.title = title;
    }
    public Author getAuthor() {
    	
        return this.author;
    }

    public void setAuthor(Author author) {
    	
        this.author = author;
    }
    public java.lang.String getUrl() {
        return this.url;
    }

    public void setUrl(java.lang.String url) {
        this.url = url;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public boolean equals(Object other) {
        if ( !(other instanceof DocumentMetaData) ) return false;
        DocumentMetaData castOther = (DocumentMetaData) other;
        return new EqualsBuilder()
            .append(this.id, castOther.id)
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(id)
            .toHashCode();
    }

	/**
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date The date to set.
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
}
