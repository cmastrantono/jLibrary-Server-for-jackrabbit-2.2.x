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
package org.jlibrary.core.jcr.compatibility;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Defines an interface for "migrator" classes. A migrator will help to migrate 
 * contents from one repository implementation to another
 * 
 * @author martin
 *
 */
public interface Migrator {

	/**
	 * Migrates a session
	 * 
	 * @param session Session with the contents to migrate
	 * 
	 * @throws RepositoryException If the contents cannot be migrated
	 */
	public void migrate(Session session) throws RepositoryException;
}
