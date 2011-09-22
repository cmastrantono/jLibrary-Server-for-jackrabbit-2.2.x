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
import java.util.HashSet;
import java.util.Set;

import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Hibernate CodeGenerator */
public class Category implements Serializable, Comparable, IResource {

	static Logger logger = LoggerFactory.getLogger(Category.class);
	
	static final long serialVersionUID = -8757457815734202858L;
	
	public final static transient String UNKNOWN_NAME = 
		"category_unknown";	
	public final static transient String UNKNOWN_DESCRIPTION = 
		"category_unknown_description";	
    public final static transient Category UNKNOWN = 
    	new Category("-1",
    				 UNKNOWN_NAME,
    				 UNKNOWN_DESCRIPTION,
    				 null);
    
    /** identifier field */
    private String id;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    private Set categories;
    
    private Set favorites;
    
    private Category parent;
    
    private Date date;
    
    private String repository;

    private boolean unknownCategory;
    
    /*
     * Este atributo no se debería usar en el cliente. Se utiliza sólo para 
     * conseguir que Hibernate haga el cascade-delete de la tabla intermedia
     * CATEGORY_NODES cuando eliminamos directorios o documentos.
     */
    private transient Set categoryNodes;	
	
    /**
     * @return Returns the repository.
     */
    public String getRepository() {
        return repository;
    }
    /**
     * @param repository The repository to set.
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }
    /** full constructor */
    public Category(String id, String name, String description, Date date) {
    	
        this.name = name;
        this.description = description;
        this.id = id;
        this.date = date;
        this.parent = null;
        this.categories = new HashSet();
    }

    /** default constructor */
    public Category() {
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
    public java.lang.String getDescription() {
        return this.description;
    }

    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    public String toString() {
        
    	return description;
    }

    public boolean equals(Object other) {
        
    	if (!(other instanceof Category)) {
    		return false;
    	}
    	String otherId = ((Category)other).getId();
    	return id.equals(otherId);
    }

    public int hashCode() {
        
    	if (getId() == null) {
    		return -1;
    	}
    	return id.hashCode();
    }
    
    
	/**
	 * @return Returns the categories.
	 */
	public Set getCategories() {
		return categories;
	}
	/**
	 * @param categories The categories to set.
	 */
	public void setCategories(Set categories) {
		this.categories = categories;
	}
	/**
	 * @return Returns the parent.
	 */
	public Category getParent() {
		return parent;
	}
	/**
	 * @param parent The parent to set.
	 */
	public void setParent(Category parent) {
		this.parent = parent;
	}
	/**
	 * @return Returns the favorites.
	 */
	public Set getFavorites() {
		return favorites;
	}
	/**
	 * @param favorites The favorites to set.
	 */
	public void setFavorites(Set favorites) {
		this.favorites = favorites;
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
	
	public boolean isUnknownCategory() {
		
		return unknownCategory;
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {

		Category category = (Category)arg0;
		if (getId() == null) return -1;
		if (category.getId() == null) return 1;
		return getId().compareTo(category.getId());
	}
	public Set getCategoryNodes() {
		return categoryNodes;
	}
	public void setCategoryNodes(Set categoryNodes) {
		this.categoryNodes = categoryNodes;
	}
	public void setUnknownCategory(boolean unknownCategory) {
		this.unknownCategory = unknownCategory;
	}
	/**
	 * Dumps all the properties into a properties object
	 * 
	 * @return CategoryProperties Category's properties
	 */
	public CategoryProperties dumpProperties()
	{
		CategoryProperties properties = new CategoryProperties();
		try
		{
			properties.put(CategoryProperties.CATEGORY_ID,
					new PropertyDef(CategoryProperties.CATEGORY_ID,getId()));
			properties.put(CategoryProperties.CATEGORY_REPOSITORY,
					new PropertyDef(CategoryProperties.CATEGORY_REPOSITORY,getRepository()));
			properties.put(CategoryProperties.CATEGORY_NAME,
					new PropertyDef(CategoryProperties.CATEGORY_NAME,getName()));
			properties.put(CategoryProperties.CATEGORY_DESCRIPTION,
					new PropertyDef(CategoryProperties.CATEGORY_DESCRIPTION,getDescription()));
		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
		}

		return properties;
	}
}
