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
 * @author root
 *
 * Class that represents a session within a repository
 */
public class Ticket implements Serializable {

	static final long serialVersionUID = -86930421021919694L;
	
	private User user;
	private boolean autoConnect;
	private String id;
	private String repositoryId;
	
	private boolean autocommit = true;
	
	public Ticket() {}
	
	/**
	 * @return Returns the user.
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user The user to set.
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * @return Returns the autoConnect.
	 */
	public boolean isAutoConnect() {
		return autoConnect;
	}
	/**
	 * @param autoConnect The autoConnect to set.
	 */
	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public boolean equals(Object obj) {

		if (!(obj instanceof Ticket)) {
			return false;
		}
		return id.equals(((Ticket)obj).id);
	}
	
	public int hashCode() {
		
		if (id == null) {
			return -1;
		}
		return id.hashCode();
	}

	public boolean isAutocommit() {
		return autocommit;
	}

	public void setAutocommit(boolean autocommit) {
		this.autocommit = autocommit;
	}
}
