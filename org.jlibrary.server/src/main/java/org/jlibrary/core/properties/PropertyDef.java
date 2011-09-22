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
package org.jlibrary.core.properties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Martin
 *
 * Repository property definition
 */
public class PropertyDef implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6835092429539583008L;
	
	private Object key;
	private Object value;
	private boolean custom;
	
	private int type;
	
	/**
	 * Default constructor
	 */
	public PropertyDef() {}
	
	/**
	 * Constructor
	 * 
	 * @param key Key for the property
	 * @param value Property's value
	 */
	public PropertyDef(Object key, Object value) {
		
		setKey(key);
		setValue(value);
	}
	
	/**
	 * @return Returns the key.
	 */
	public Object getKey() {
		return key;
	}
	/**
	 * @param key The key to set.
	 */
	public void setKey(Object key) {
		this.key = key;
	}
	/**
	 * @return Returns the value.
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(Object value) {
		
		this.value = value;
		
		// Hack. We don't want Calendar objects created by Axis here !!
		if (value instanceof java.util.Calendar) {
			Calendar calendar = (Calendar)value;
			this.value = calendar.getTime();
		}
		
		setType(value);
	}
	
	private void setType(Object value) {
		
		if (value instanceof String) {
			type = PropertyType.STRING;
		} else if (value instanceof Integer) {
			type = PropertyType.INTEGER;
		} else if (value instanceof byte[]) {
			type = PropertyType.BINARY;
		} else if (value instanceof Long) {
			type = PropertyType.LONG;
		} else if (value instanceof BigDecimal) {
			type = PropertyType.DOUBLE;
		} else if (value instanceof Boolean) {
			type = PropertyType.BOOLEAN;
		} else if (value instanceof Date) {
			type = PropertyType.DATE;
		} else {
			type = PropertyType.OBJECT;
		}
	}
	
	/**
	 * Returns the type of the property
	 * 
	 * @return Property's type
	 */
	public int getType() {
		
		return type;
	}
	
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Key=");
		buffer.append(key);
		buffer.append(",Value=");
		buffer.append(value);
		buffer.append("]");
		return buffer.toString();
	}

	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}
}
