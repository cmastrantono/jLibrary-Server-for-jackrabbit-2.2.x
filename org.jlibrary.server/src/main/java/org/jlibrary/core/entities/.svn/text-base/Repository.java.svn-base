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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.properties.RepositoryProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Information repository entity
 * 
 * @author martin
 */
public class Repository implements IResource, Serializable {

	static Logger logger = LoggerFactory.getLogger(Repository.class);
	
	static final long serialVersionUID = -4146159798792032334L;
	
	// Ticket that represents a session within this repository
	private Ticket ticket;
	
	private transient ServerProfile serverProfile;

    /** identifier field */
    private String id;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private String path;

    /** nullable persistent field */
    private String creator;

    /** nullable persistent field */
    private org.jlibrary.core.entities.Directory root;
    
    private Set categories;
    
    // Repository configuration
    private RepositoryConfig repositoryConfig = new RepositoryConfig();
    
    /*
     * These flag are only used in client side to mark a repository as connected.
     * It has no importance for the server, it's useful for client side operations
     */
    private boolean connected;

    /** default constructor */
    public Repository() {
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
    public java.lang.String getPath() {
        return this.path;
    }

    public void setPath(java.lang.String path) {
        this.path = path;
    }
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
    public org.jlibrary.core.entities.Directory getRoot() {
        return this.root;
    }

    public void setRoot(org.jlibrary.core.entities.Directory root) {
        this.root = root;
    }

    public String toString() {
    	
        return name;
    }

    public boolean equals(Object other) {
        
    	if (!(other instanceof Repository)) {
    		return false;
    	}
    	
    	return id.equals(((Repository)other).id);
    }

    public int hashCode() {
        
    	return id.hashCode();
    }

	public ServerProfile getServerProfile() {
		
		return serverProfile;
	}
	
	public void setServerProfile(ServerProfile profile) {
		
		serverProfile = profile;
	}
	/**
	 * @return Returns the ticket.
	 */
	public Ticket getTicket() {
		return ticket;
	}
	/**
	 * @param ticket The ticket to set.
	 */
	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

    /**
     * @return Returns the categories.
     */
    public Set getCategories() {
    	
        return categories;
    }
    
    public List getFilteredCategories() {
    	
    	ArrayList list = new ArrayList();
    	if (categories !=  null) {
	    	Iterator it = categories.iterator();
	    	while (it.hasNext()) {
				Category category = (Category) it.next();
				if (category.getParent() == null) {
					list.add(category);
				}
			}
    	}
    	return list;
    }
    
    /**
     * @param categories The categories to set.
     */
    public void setCategories(Set categories) {
        this.categories = categories;
    }
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	/**
	 * Dumps all the properties into a properties object
	 * 
	 * @return RepositoryProperties Repository's properties
	 */
	public RepositoryProperties dumpProperties() {
		
		RepositoryProperties properties = new RepositoryProperties();
		try {
			if (getId() != null) {
				properties.addProperty(RepositoryProperties.REPOSITORY_ID,
						   getId());
			}
			properties.addProperty(RepositoryProperties.REPOSITORY_NAME,
								   getName());
			properties.addProperty(RepositoryProperties.REPOSITORY_DESCRIPTION,
					   getDescription());
			properties.addProperty(
					RepositoryProperties.EXTRACT_DOCUMENT_METADATA,
					new Boolean(getRepositoryConfig().isExtractMetadata()));
			properties.addProperty(
					RepositoryProperties.PHYSICAL_DELETE_DOCUMENTS,
					new Boolean(getRepositoryConfig().isPhysicalDeleteDocuments()));
			properties.addProperty(
					RepositoryProperties.DO_LAZY_LOADING,
					new Boolean(getRepositoryConfig().isEnabledLazyLoading()));
			
		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
		}
		
		return properties;
	}

	/**
	 * Returns the configuration properties for this repository
	 * 
	 * @return RepositoryConfig Configuration properties
	 */
	public RepositoryConfig getRepositoryConfig() {
		
		return repositoryConfig;
	}

	/**
	 * Sets the configuration properties for this repository
	 * 
	 * @param repositoryConfig Configuration properties
	 */
	public void setRepositoryConfig(RepositoryConfig repositoryConfig) {
		this.repositoryConfig = repositoryConfig;
	}

	/**
	 * @see IResource#getRepository()
	 */
	public String getRepository() {

		return id;
	}
}
