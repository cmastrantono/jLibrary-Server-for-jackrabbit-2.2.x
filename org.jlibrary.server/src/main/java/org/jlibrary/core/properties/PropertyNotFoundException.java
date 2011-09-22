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

/**
 * @author Martin
 *
 * Exception thrown when a property can't be found
 */
public class PropertyNotFoundException extends Exception {

	static final long serialVersionUID = 1090011306701126013L;
	
	/**
	 * 
	 */
	public PropertyNotFoundException() {
		super();

	}
	/**
	 * @param message
	 */
	public PropertyNotFoundException(String message) {
		super(message);

	}
	/**
	 * @param message
	 * @param cause
	 */
	public PropertyNotFoundException(String message, Throwable cause) {
		super(message, cause);

	}
	/**
	 * @param cause
	 */
	public PropertyNotFoundException(Throwable cause) {
		super(cause);

	}
}
