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

public class LocalServerProfile implements ServerProfile {

	public static final String PROFILE_LOCAL_KEY = "profile_local";
	
	public String getLocation() {
		
		return PROFILE_LOCAL_KEY;
	}

	public String getName() {

		return PROFILE_LOCAL_KEY;
	}

	public String getServicesFactory() {

		return null;
	}

	public void setLocation(String location) {}

	public void setName(String name) {}
	
	public boolean equals(Object profile) {

		return getLocation().equals(((LocalServerProfile)profile).getLocation());
	}
	
	public int hashCode() {

		return getLocation().hashCode();
	}
	
	public boolean isLocal() {

		return true;
	}
	
	public String toString() {

		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(getName());
		buffer.append(",");
		buffer.append(getLocation());
		buffer.append("]");
		return buffer.toString();
	}
}
