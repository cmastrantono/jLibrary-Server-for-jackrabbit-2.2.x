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


/**
 * <p>This is a common interface for the entities from the business model.</p>
 * <p>It will group common methods for common entities.</p>
 * 
 * @author martin
 */
public interface IResource {
	
	/**
	 * Returns the id of this object's repository
	 * 
	 * @return String Object's repository id
	 */
	public String getRepository();
	
	/**
	 * Returns the id for this resource.
	 * 
	 *  @return String id
	 */
	public String getId();
	
	/**
	 * Returns the name for this resource
	 * 
	 * @return String Name
	 */
	public String getName();
}
