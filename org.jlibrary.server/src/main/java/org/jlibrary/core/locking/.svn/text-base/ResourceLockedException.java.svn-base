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
package org.jlibrary.core.locking;

import org.jlibrary.core.entities.Lock;
import org.jlibrary.core.repository.exception.RepositoryException;

/**
 * @author martin
 *
 * Exception thrown when a node or a resource is locked
 */
public class ResourceLockedException extends RepositoryException {
	
	static final long serialVersionUID = 8273305254794760954L;
	private Lock lock;
	
	/**
	 * Default constructor
	 *
	 */
	public ResourceLockedException() {}
	
	/**
	 * Creates an exception with a wrapped lock
	 * 
	 * @param lock Lock that generated the exception
	 */
	public ResourceLockedException(Lock lock) {
		super();
		
		this.lock = lock;
	}

	/**
	 * Returns the lock attached to this exception
	 * 
	 * @return Lock Lock that had originated the exception
	 */
	public Lock getLock() {
		return lock;
	}

	/**
	 * Sets the lock
	 * 
	 * @param lock Lock
	 */
	public void setLock(Lock lock) {
		this.lock = lock;
	}
}
