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

/**
 * @author martin
 */
public class Restriction implements Serializable, Comparable {

	static final long serialVersionUID = -8166114682304249568L;
	
	/**
	 * @return Returns the member.
	 */
	public String getMember() {
		return member;
	}
	
	/**
	 * @param member The member to set.
	 */
	public void setMember(String member) {
		this.member = member;
	}
	/**
	 * @return Returns the node.
	 */
	public String getNode() {
		return node;
	}
	/**
	 * @param node The node to set.
	 */
	public void setNode(String node) {
		this.node = node;
	}
	private String node;
	private String member;
	private String id;
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		return member;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		
		if (!(obj instanceof Restriction)) {
			return false;
		}
		Restriction castOther = (Restriction)obj;
		return (castOther.member.equals(member) &&
				castOther.node.equals(node));
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		
		// Assume a error probability
		return id.hashCode();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {

		Restriction restriction = (Restriction)arg0;
		if (getId() == null) return -1;
		if (restriction.getId() == null) return 1;
		return getId().compareTo(restriction.getId());
	}
}
