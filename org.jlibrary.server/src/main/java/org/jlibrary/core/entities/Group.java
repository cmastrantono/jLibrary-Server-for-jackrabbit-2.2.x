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

import org.jlibrary.core.properties.GroupProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Group entity
 * 
 * @author mpermar
 *
 */
public class Group extends Member implements Serializable {

	static Logger logger = LoggerFactory.getLogger(Group.class);
	
	static final long serialVersionUID = 89531413242326286L;
	
	// This will be the three default groups. They can't be removed
	public static transient Group USERS_GROUP;
	public static transient Group PUBLISHERS_GROUP;
	public static transient Group ADMINS_GROUP;
	
	// Keys for i18n group names
	public static final transient String READERS_GROUP_NAME = "users_group_name";
	public static final transient String ADMINS_GROUP_NAME = "admins_group_name";
	public static final transient String PUBLISHERS_GROUP_NAME = "publishers_group_name";

	// Keys for i18n group descriptions
	public static final transient String READERS_GROUP_DESCRIPTION = "users_group_description";
	public static final transient String ADMINS_GROUP_DESCRIPTION = "admins_group_description";
	public static final transient String PUBLISHERS_GROUP_DESCRIPTION = "publishers_group_description";

	
    /** nullable persistent field */
    private String description;

    /** persistent field */
    private Set users;

	private transient HashSet insertedRoles = new HashSet();
	private transient HashSet deletedRoles = new HashSet();
    
	private transient HashSet insertedUsers = new HashSet();
	private transient HashSet deletedUsers = new HashSet();

	private Group backup;
    
    /** default constructor */
    public Group() {
    }

    public java.lang.String getDescription() {
        return this.description;
    }

    public void setDescription(java.lang.String description) {
        this.description = description;
    }
    public java.util.Set getUsers() {
        return this.users;
    }

    public void setUsers(java.util.Set users) {
        this.users = users;
    }
    
    /**
	 * @see org.jlibrary.core.entities.Member#toString()
	 */
	public String toString() {
		
		return getDescription();
	}
	
	public boolean equals(Object other) {
		return super.equals(other);
	}
	
	public int hashCode() {
		return super.hashCode();
	}
	
	/**
	 * Dumps all the properties into a properties object
	 * 
	 * @return GroupProperties Group's properties
	 */
	public GroupProperties dumpProperties() {
		
		GroupProperties properties = new GroupProperties();
		try {
			properties.put(GroupProperties.GROUP_ID, 
						   new PropertyDef(GroupProperties.GROUP_ID,getId()));
			properties.put(GroupProperties.GROUP_NAME, 
					   new PropertyDef(GroupProperties.GROUP_NAME,getName()));
			properties.put(GroupProperties.GROUP_DESCRIPTION, 
					   new PropertyDef(GroupProperties.GROUP_DESCRIPTION,getDescription()));
			properties.put(GroupProperties.GROUP_REPOSITORY, 
					   new PropertyDef(GroupProperties.GROUP_REPOSITORY,getRepository()));
			
			Iterator it = getInsertedRoles().iterator();
			while (it.hasNext()) {
				Rol rol = (Rol)it.next();
				if (rol == null) {
					continue;
				}
				properties.put(GroupProperties.GROUP_ADD_ROL, 
						   new PropertyDef(GroupProperties.GROUP_ADD_ROL,rol));				
			}

			it = getDeletedRoles().iterator();
			while (it.hasNext()) {
				Rol rol = (Rol)it.next();
				if (rol == null) {
					continue;
				}				
				properties.put(GroupProperties.GROUP_DELETE_ROL, 
						   new PropertyDef(GroupProperties.GROUP_DELETE_ROL,rol));				
			}
			
			it = getInsertedUsers().iterator();
			while (it.hasNext()) {
				User user = (User)it.next();
				if (user == null) {
					continue;
				}
				properties.put(GroupProperties.GROUP_ADD_USER, 
						   new PropertyDef(GroupProperties.GROUP_ADD_USER,user));				
			}

			it = getDeletedUsers().iterator();
			while (it.hasNext()) {
				User user = (User)it.next();
				if (user == null) {
					continue;
				}				
				properties.put(GroupProperties.GROUP_DELETE_USER, 
						   new PropertyDef(GroupProperties.GROUP_DELETE_USER,user));				
			}			
			
		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
		}
		
		return properties;
	}	

	public void addRol(Rol rol) {
		
	    if (insertedRoles == null) {
	    	insertedRoles = new HashSet();
	    }
	    if (deletedRoles == null) {
	    	deletedRoles = new HashSet();
	    }
	    
	    insertedRoles.add(rol);
	    deletedRoles.remove(rol);
		getRoles().add(rol);
	}
	
	public void removeRol(Rol rol) {
		
	    if (insertedRoles == null) {
	    	insertedRoles = new HashSet();
	    }
	    if (deletedRoles == null) {
	    	deletedRoles = new HashSet();
	    }
	    
		if (!insertedRoles.contains(rol)) {
			deletedRoles.add(rol);
		}
		insertedRoles.remove(rol);
		getRoles().remove(rol);
	}
	
	public Set getInsertedRoles() {
		
	    if (insertedRoles == null) {
	        insertedRoles = new HashSet();
	    }
		return insertedRoles; 
	}
	
	public Set getDeletedRoles() {
		
	    if (deletedRoles == null) {
	        deletedRoles = new HashSet();
	    }
		return deletedRoles;
	}	
	
	public void clearInsertedRoles() {
		
	    if (insertedRoles == null) {
	        insertedRoles = new HashSet();
	    }
		insertedRoles.clear();
	}
	
	public void clearDeletedRoles() {
		
	    if (deletedRoles == null) {
	        deletedRoles = new HashSet();
	    }	    
		deletedRoles.clear();
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
	    getUsers().add(user);
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
		getUsers().remove(user);
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
	
	public void restoreState() {
		
		setName(backup.getName());
		setDescription(backup.getDescription());
		setRoles(backup.getRoles());
		setUsers(backup.getUsers());
				
		clearDeletedRoles();
		clearInsertedRoles();
		clearDeletedUsers();
		clearInsertedUsers();		
	}
	
	public void saveState() {
		
		backup = new Group();
		
		backup.setName(getName());
		backup.setDescription(getDescription());
		
		Set roles = new HashSet();
		if (getRoles() != null) {
			roles.addAll(getRoles());
		}
		backup.setRoles(roles);

		Set users = new HashSet();
		if (getUsers() != null) {
			users.addAll(getUsers());
		}
		backup.setUsers(users);		
		
		clearDeletedRoles();
		clearInsertedRoles();
		clearDeletedUsers();
		clearInsertedUsers();			
	}
}
