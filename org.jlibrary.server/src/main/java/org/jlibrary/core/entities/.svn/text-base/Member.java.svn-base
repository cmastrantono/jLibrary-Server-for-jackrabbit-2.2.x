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

import org.apache.commons.lang.builder.ToStringBuilder;

/** @author Hibernate CodeGenerator */
public class Member implements Serializable, Comparable, IResource {

	static final long serialVersionUID = 6450117877405074989L;
	
	private String id;
	
    /** identifier field */
    private String name;

    /** persistent field */
    private Set roles;

    private String repository;
    
    /** default constructor */
    public Member() {
    }

    public java.lang.String getName() {
        return this.name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }
    public java.util.Set getRoles() {
        return roles;
    }
    
    public void setRoles(java.util.Set r) {
        roles = r;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public boolean equals(Object other) {
    	
        if (name == null) return false;
        if ( !(other instanceof Member) ) return false;
        Member castOther = (Member) other;
        return getName().equals(castOther.getName());
    }

    public int hashCode() {
        
    	if (name == null) {
    		return -1;
    	}
    	int hc = name.hashCode();
    	return hc;
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
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		
		Member member = (Member)o;
		if (getId().equals(member.getId())) {
			return 0;
		}
		
		if ((member instanceof Group) && (this instanceof User)) {
			return 1;
		}
		
		if ((member instanceof User) && (this instanceof Group)) {
			return -1;
		}
		
		return getName().compareTo(member.getName());
		
	}
	
    public java.util.Set getRoles(String repository) {
        
    	HashSet set = new HashSet();
    	Iterator it = roles.iterator();
    	while (it.hasNext()) {
			Rol rol = (Rol) it.next();
			if (rol.getRepository().equals(repository)) {
				set.add(rol);
			}
		}
    	return set;
    }

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}	
}
