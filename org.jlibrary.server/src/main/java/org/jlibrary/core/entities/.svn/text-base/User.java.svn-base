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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.jlibrary.core.properties.UserProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User entity
 * 
 * @author mpermar
 *
 */
public class User extends Member implements Serializable, Comparable {

	static Logger logger = LoggerFactory.getLogger(User.class);
	static final long serialVersionUID = 3974316916488001493L;
	
	/**
	 * This is a joker name. If the user don't know the localized string
	 * for the administrator name, i.e. Administrador, Administrator, etc., 
	 * he can also use the generic keyword admin. 
	 */
	public final static transient String ADMIN_KEYNAME = "admin";
	
	public final static transient String DEFAULT_PASSWORD = "changeme";
	public final static transient String ADMIN_NAME = "admin_name";
	public final static transient String ADMIN_CODE = "-1";
    
    public final static transient User ADMIN_USER = 
        new User(ADMIN_CODE,ADMIN_NAME,ADMIN_NAME,ADMIN_NAME,DEFAULT_PASSWORD,true);
    
    private boolean admin;
    
	private transient HashSet insertedRoles = new HashSet();
	private transient HashSet deletedRoles = new HashSet();
    
	private transient HashSet insertedGroups = new HashSet();
	private transient HashSet deletedGroups = new HashSet();
    
    public boolean isAdmin() {
        
    	return admin;
    }
    
    public boolean isEditor() {
    	
    	Iterator it = getRoles().iterator();
    	while (it.hasNext()) {
    		Rol rol = (Rol)it.next();
    		if (rol.getName().equals(Rol.PUBLISHER_ROLE_NAME)) {
    			return true;
    		}
    	}
    	
    	it = getGroups().iterator();
    	while (it.hasNext()) {
    		Group group = (Group)it.next();
    		if (group.getName().equals(Group.ADMINS_GROUP_NAME)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * @param admin The admin to set.
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    /** nullable persistent field */
    private String password;

    /** nullable persistent field */
    private String firstName;

    /** nullable persistent field */
    private String lastName;

    /** nullable persistent field */
    private String email;

    /** persistent field */
    private Set groups;
    
    private Set bookmarks = new HashSet();
    
    private Set favorites = new HashSet();

	private User backup;
    
    public User(String id, 
    			String name, 
				String firstName, 
				String lastName, 
				String password,
				boolean admin) {

        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        setId(id);
        setName(name);
        this.admin = admin;
    }
    
    /** default constructor */
    public User() {
    }

    public java.lang.String getPassword() {
        return this.password;
    }

    public void setPassword(java.lang.String password) {
        this.password = password;
    }
    public java.lang.String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }
    public java.lang.String getLastName() {
        return this.lastName;
    }

    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }
    public java.lang.String getEmail() {
        return this.email;
    }

    public void setEmail(java.lang.String email) {
        this.email = email;
    }
    public java.util.Set getGroups() {
        return this.groups;
    }

    public void setGroups(java.util.Set groups) {
        this.groups = groups;
    }

    public String toString() {
    	
    	StringBuffer description = new StringBuffer();
    	description.append(getFirstName());
    	description.append(" ");
    	if (getLastName() != null) {
    		description.append(getLastName());
    	}
    	
    	return description.toString();
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
	 * @return Returns the favorites.
	 */
	public Set getFavorites() {
		
		//TODO: Remove this. It won't be used with the JCR impl.
		return favorites;
	}
	/**
	 * @param favorites The favorites to set.
	 */
	public void setFavorites(Set favorites) {
		this.favorites = favorites;
	}
	
    public List getFilteredBookmarks() {
    	
    	ArrayList list = new ArrayList();
    	if (bookmarks != null) {
	    	Iterator it = bookmarks.iterator();
	    	while (it.hasNext()) {
				Bookmark bookmark = (Bookmark) it.next();
				if (bookmark.getParent() == null) {
					list.add(bookmark);
				}
			}
    	}
    	return list;
    }
    
    /**
	 * @see org.jlibrary.core.entities.Member#hashCode()
	 */
	public int hashCode() {

		return super.hashCode();
	}
	
	/**
	 * @see org.jlibrary.core.entities.Member#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		
		return super.equals(other);
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

	public void addGroup(Group group) {
		
	    if (insertedGroups == null) {
	    	insertedGroups = new HashSet();
	    }
	    if (deletedGroups == null) {
	    	deletedGroups = new HashSet();
	    }
	    
	    insertedGroups.add(group);
	    deletedGroups.remove(group);
	    getGroups().add(group);
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
		getGroups().remove(group);
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
	
	public void saveState() {
		
		backup = new User();
		
		backup.setName(getName());
		backup.setFirstName(getFirstName());
		backup.setLastName(getLastName());
		backup.setEmail(getEmail());
		backup.setPassword(getPassword());
		backup.setAdmin(isAdmin());
		
		Set roles = new HashSet();
		if (getRoles() != null) {
			roles.addAll(getRoles());
		}
		backup.setRoles(roles);

		Set groups = new HashSet();
		if (getGroups() != null) {
			groups.addAll(getGroups());
		}
		backup.setGroups(groups);		
		
		clearDeletedRoles();
		clearInsertedRoles();
		clearDeletedGroups();
		clearInsertedGroups();		
		
	}
	
	public void restoreState() {
		
		setName(backup.getName());
		setFirstName(backup.getFirstName());
		setLastName(backup.getLastName());
		setEmail(backup.getEmail());
		setPassword(backup.getPassword());
		setAdmin(backup.isAdmin());
		setRoles(backup.getRoles());
		setGroups(backup.getGroups());
				
		clearDeletedRoles();
		clearInsertedRoles();
		clearDeletedGroups();
		clearInsertedGroups();
	}
	
	/**
	 * Dumps all the properties into a properties object
	 * 
	 * @return UserProperties User's properties
	 */
	public UserProperties dumpProperties() {
		
		UserProperties properties = new UserProperties();
		try {
			properties.put(UserProperties.USER_ID, 
						   new PropertyDef(UserProperties.USER_ID,getId()));
			properties.put(UserProperties.USER_REPOSITORY, 
					   new PropertyDef(UserProperties.USER_REPOSITORY,getRepository()));
			properties.put(UserProperties.USER_NAME, 
					   new PropertyDef(UserProperties.USER_NAME,getName()));
			if (getFirstName() != null) {
				properties.put(UserProperties.USER_FIRSTNAME, 
					   new PropertyDef(UserProperties.USER_FIRSTNAME,getFirstName()));
			}
			if (getLastName() != null) {
				properties.put(UserProperties.USER_LASTNAME, 
					   new PropertyDef(UserProperties.USER_ID,getLastName()));
			}
			if (getEmail() != null) {
				properties.put(UserProperties.USER_EMAIL, 
					   new PropertyDef(UserProperties.USER_EMAIL,getEmail()));
			}
			properties.put(UserProperties.USER_PASSWORD, 
					   new PropertyDef(UserProperties.USER_PASSWORD,getPassword()));
			properties.put(UserProperties.USER_ADMIN, 
					   new PropertyDef(UserProperties.USER_ADMIN,new Boolean(isAdmin())));
			
			Iterator it = getInsertedRoles().iterator();
			while (it.hasNext()) {
				Rol rol = (Rol)it.next();
				if (rol == null) {
					continue;
				}
				properties.put(UserProperties.USER_ADD_ROL, 
						   new PropertyDef(UserProperties.USER_ADD_ROL,rol));				
			}

			it = getDeletedRoles().iterator();
			while (it.hasNext()) {
				Rol rol = (Rol)it.next();
				if (rol == null) {
					continue;
				}				
				properties.put(UserProperties.USER_DELETE_ROL, 
						   new PropertyDef(UserProperties.USER_DELETE_ROL,rol));				
			}
			
			it = getInsertedGroups().iterator();
			while (it.hasNext()) {
				Group group = (Group)it.next();
				if (group == null) {
					continue;
				}
				properties.put(UserProperties.USER_ADD_GROUP, 
						   new PropertyDef(UserProperties.USER_ADD_GROUP,group));				
			}

			it = getDeletedGroups().iterator();
			while (it.hasNext()) {
				Group group = (Group)it.next();
				if (group == null) {
					continue;
				}				
				properties.put(UserProperties.USER_DELETE_GROUP, 
						   new PropertyDef(UserProperties.USER_DELETE_GROUP,group));				
			}			
			
		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
		}
		
		clearDeletedRoles();
		clearInsertedRoles();
		clearDeletedGroups();
		clearInsertedGroups();
		
		return properties;
	}	
}
