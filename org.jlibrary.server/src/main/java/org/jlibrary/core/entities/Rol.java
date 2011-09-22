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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.properties.RolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Rol entity
 * 
 * @author mpermar
 *
 */
public class Rol implements Serializable, IResource {    
    
	static Logger logger = LoggerFactory.getLogger(Rol.class);
	static final long serialVersionUID = 3353966567727357644L;
	
	// This will be the three default roles. They can't be removed.
	public static final transient String READER_ROLE_NAME = "reader_role_name";
	public static final transient String ADMIN_ROLE_NAME = "admin_role_name";
	public static final transient String PUBLISHER_ROLE_NAME = "publisher_role_name";

	// Keys for i18n rol descriptions
	public static final transient String READER_ROLE_DESCRIPTION = "reader_role_description";
	public static final transient String ADMIN_ROLE_DESCRIPTION = "admin_role_description";
	public static final transient String PUBLISHER_ROLE_DESCRIPTION = "publisher_role_description";

	
    /** identifier field */
    private String name;

    private String id;
    
    private String description;
    
    private String repository;
    
    /** persistent field */
    private Set members;

	private transient HashSet insertedUsers = new HashSet();
	private transient HashSet deletedUsers = new HashSet();

	private transient HashSet insertedGroups = new HashSet();
	private transient HashSet deletedGroups = new HashSet();

	private Rol backup;
	
    
    /** default constructor */
    public Rol() {
    }

    /** minimal constructor */
    public Rol(Set members) {
        this.members = members;
    }

    public java.lang.String getName() {
        return this.name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }
    public java.util.Set getMembers() {
        return this.members;
    }

    public void setMembers(java.util.Set members) {
        this.members = members;
    }

    public String toString() {
        
    	return description;
    }

    public boolean equals(Object other) {
    	
        if ( !(other instanceof Rol) ) return false;
        Rol castOther = (Rol) other;
        if (castOther == null || id == null) {
        	return false;
        }
        return id.equals(castOther.id);
    }

    public int hashCode() {

    	if (id == null) {
    		return -1;
    	}
        return id.hashCode();
    }

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
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
	
	public boolean isAdmin() {
		
		return getName().equals(ADMIN_ROLE_NAME);
	}
	
	public boolean isPublisher() {
		
		return getName().equals(PUBLISHER_ROLE_NAME);
	}
	
	public boolean isReader() {
		
		return getName().equals(READER_ROLE_NAME);
	}

	/**
	 * Dumps all the properties into a properties object
	 * 
	 * @return RolProperties Rol's properties
	 */
	public RolProperties dumpProperties() {
		
		RolProperties properties = new RolProperties();
		try {
			properties.put(RolProperties.ROL_ID, 
						   new PropertyDef(RolProperties.ROL_ID,getId()));
			properties.put(RolProperties.ROL_NAME, 
					   new PropertyDef(RolProperties.ROL_NAME,getName()));
			properties.put(RolProperties.ROL_DESCRIPTION, 
					   new PropertyDef(RolProperties.ROL_DESCRIPTION,getDescription()));
			properties.put(RolProperties.ROL_REPOSITORY, 
					   new PropertyDef(RolProperties.ROL_REPOSITORY,getRepository()));
			
			Iterator it = getInsertedGroups().iterator();
			while (it.hasNext()) {
				Group group = (Group)it.next();
				if (group == null) {
					continue;
				}
				properties.put(RolProperties.ROL_ADD_GROUP, 
						   new PropertyDef(RolProperties.ROL_ADD_GROUP,group));				
			}

			it = getDeletedGroups().iterator();
			while (it.hasNext()) {
				Group group = (Group)it.next();
				if (group == null) {
					continue;
				}				
				properties.put(RolProperties.ROL_DELETE_GROUP, 
						   new PropertyDef(RolProperties.ROL_DELETE_GROUP,group));				
			}			
			
			it = getInsertedUsers().iterator();
			while (it.hasNext()) {
				User user = (User)it.next();
				if (user == null) {
					continue;
				}
				properties.put(RolProperties.ROL_ADD_USER, 
						   new PropertyDef(RolProperties.ROL_ADD_USER,user));				
			}

			it = getDeletedUsers().iterator();
			while (it.hasNext()) {
				User user = (User)it.next();
				if (user == null) {
					continue;
				}				
				properties.put(RolProperties.ROL_DELETE_USER, 
						   new PropertyDef(RolProperties.ROL_DELETE_USER,user));				
			}			
		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
		}
		
		return properties;
	}	

	public void addUser(User user) {
		
	    if (insertedUsers == null) {
	    	insertedUsers = new HashSet();
	    }
	    if (deletedUsers == null) {
	    	deletedUsers = new HashSet();
	    }
	    
	    insertedUsers.add(user);
	    deletedUsers.remove(user);
	    getMembers().add(user);
	}
	
	public void removeUser(User user) {
		
	    if (insertedUsers == null) {
	    	insertedUsers = new HashSet();
	    }
	    if (deletedUsers == null) {
	    	deletedUsers = new HashSet();
	    }
	    
		if (!insertedUsers.contains(user)) {
			deletedUsers.add(user);
		}
		insertedUsers.remove(user);
		getMembers().remove(user);
	}
	
	public Set getInsertedUsers() {
		
	    if (insertedUsers == null) {
	        insertedUsers = new HashSet();
	    }
		return insertedUsers; 
	}
	
	public Set getDeletedUsers() {
		
	    if (deletedUsers == null) {
	        deletedUsers = new HashSet();
	    }
		return deletedUsers;
	}	
	
	public void clearInsertedUsers() {
		
	    if (insertedUsers == null) {
	        insertedUsers = new HashSet();
	    }
		insertedUsers.clear();
	}
	
	public void clearDeletedUsers() {
		
	    if (deletedUsers == null) {
	        deletedUsers = new HashSet();
	    }	    
		deletedUsers.clear();
	}

	public void addGroup(Group group) {
		
	    if (insertedGroups == null) {
	    	insertedGroups = new HashSet();
	    }
	    if (deletedGroups == null) {
	    	deletedGroups = new HashSet();
	    }
	    
	    insertedGroups.add(group);
	    deletedGroups.remove(group);
	    getMembers().add(group);
	}
	
	public void removeGroup(Group group) {
		
	    if (insertedGroups == null) {
	    	insertedGroups = new HashSet();
	    }
	    if (deletedGroups == null) {
	    	deletedGroups = new HashSet();
	    }
	    
		if (!insertedGroups.contains(group)) {
			deletedGroups.add(group);
		}
		insertedGroups.remove(group);
		getMembers().remove(group);
	}
	
	public Set getInsertedGroups() {
		
	    if (insertedGroups == null) {
	        insertedGroups = new HashSet();
	    }
		return insertedGroups; 
	}
	
	public Set getDeletedGroups() {
		
	    if (deletedGroups == null) {
	        deletedGroups = new HashSet();
	    }
		return deletedGroups;
	}	
	
	public void clearInsertedGroups() {
		
	    if (insertedGroups == null) {
	        insertedGroups = new HashSet();
	    }
		insertedGroups.clear();
	}
	
	public void clearDeletedGroups() {
		
	    if (deletedGroups == null) {
	        deletedGroups = new HashSet();
	    }	    
		deletedGroups.clear();
	}
	
	public void restoreState() {
		
		setName(backup.getName());
		setDescription(backup.getDescription());
		setMembers(backup.getMembers());
				
		clearDeletedUsers();
		clearInsertedGroups();
		clearDeletedUsers();
		clearInsertedUsers();		
	}
	
	public void saveState() {
		
		backup = new Rol();
		
		backup.setName(getName());
		backup.setDescription(getDescription());
		
		Set members = new HashSet();
		if (getMembers() != null) {
			members.addAll(getMembers());
		}
		backup.setMembers(members);		
		
		clearDeletedGroups();
		clearInsertedGroups();
		clearDeletedUsers();
		clearInsertedUsers();			
	}
	
}
