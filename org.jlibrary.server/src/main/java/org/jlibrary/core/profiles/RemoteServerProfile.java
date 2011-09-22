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
package org.jlibrary.core.profiles;

import org.jlibrary.core.entities.ServerProfile;

/**
 * Profile for remote access
 * @author mpermar
 *
 */
public class RemoteServerProfile implements ServerProfile {

	private String location;
	private String name;
	
	public String getLocation() {
		
		return location;
	}

	public String getName() {

		return name;
	}

	public String getServicesFactory() {

		return null;
	}
	
	public void setLocation(String location) {
		
		this.location = location;
	}

	public void setName(String name) {
		
		this.name = name;
	}

	public boolean equals(Object profile) {

		if (!( profile instanceof RemoteServerProfile)) return false;
		return location.equals(((RemoteServerProfile)profile).getLocation());
	}
	
	public int hashCode() {

		return location.hashCode();
	}
	
	public boolean isLocal() {

		return false;
	}
	
	public String toString() {

		return getLocation();
	}	
}
