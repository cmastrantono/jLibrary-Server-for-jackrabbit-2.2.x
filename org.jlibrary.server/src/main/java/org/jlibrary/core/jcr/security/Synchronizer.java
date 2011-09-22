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
package org.jlibrary.core.jcr.security;

import javax.jcr.Credentials;
import javax.jcr.Repository;

import org.jlibrary.core.security.SecurityException;

/**
 * <p>This interface will define synchronization services. This services will be 
 * on charge of synchronizing jLibrary security service with third party 
 * authorization systems.</p>
 * 
 * @author martin
 *
 */
public interface Synchronizer {

	/**
	 * Synchronizes a repository with a third party system. 
	 * 
	 * @param JCRRepository Repository that must be synchronized
	 * @param jLibraryRepository Name of the jLibrary repository that want to 
	 * be synchronized
	 * @param credentials Credentials of an user with administrative rights on 
	 * the jLibrary repository. 
	 * 
	 * @throws SecurityException If the repository cannot be synchronized. 
	 */
	public void synchronize(Repository JCRRepository, 
						    String jLibraryRepository,
							Credentials credentials) throws SecurityException;
}
