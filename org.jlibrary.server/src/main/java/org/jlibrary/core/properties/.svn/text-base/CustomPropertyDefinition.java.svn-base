/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
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
package org.jlibrary.core.properties;

import java.io.Serializable;

import org.apache.jackrabbit.spi.Name;


public class CustomPropertyDefinition implements Serializable {

	private static final long serialVersionUID = 1668088359345704027L;
	
	private Name qName = null;
	
	private String name;
	private int type;
	private boolean multivalued;
	private boolean autocreated;
	private Object[] defaultValues;
	
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	public boolean isAutocreated() {
		return autocreated;
	}
	public void setAutocreated(boolean autocreated) {
		this.autocreated = autocreated;
	}
	public Object[] getDefaultValues() {
		return defaultValues;
	}
	public void setDefaultValues(Object[] defaultValues) {
		this.defaultValues = defaultValues;
	}
	public boolean isMultivalued() {
		return multivalued;
	}
	public void setMultivalued(boolean multivalued) {
		this.multivalued = multivalued;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Name=");
		buffer.append(name);
		buffer.append(",Type=");
		buffer.append(type);
		buffer.append(",Multivalued=");
		buffer.append(multivalued);
		buffer.append(",Autocreated=");
		buffer.append(autocreated);
		buffer.append(",Default=");
		buffer.append(defaultValues);
		buffer.append("]");
		return buffer.toString();
	}
	
	/*
	 * 
	 * TODO .. please check whether it's ok to place here Name instead of QName 
	 */
	public Name getQName() {
		return qName;
	}
	
	public void setQName(Name namespace) {
		this.qName = namespace;
	}
}
