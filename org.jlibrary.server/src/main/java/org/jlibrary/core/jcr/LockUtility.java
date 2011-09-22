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
package org.jlibrary.core.jcr;

import java.util.HashMap;

import javax.jcr.RepositoryException;

/**
 * <p>Utility to obtain a lock attached to a node. These locks can be shared 
 * between different sessions to handle synchronization and concurrency.</p> 
 * 
 * <p>Note that this LockUtility class defines a simple synchronization 
 * mechanism that will allow sessions to be synchronized by the same unique 
 * object per JCR node. Methods calling this utility class will use the acquired
 * lock objects to synchronize access.</p>
 * 
 * <p>A better synchronization system could be created using the Lock facility 
 * from the JCR specification, but that is currently out of the scope of this 
 * simple implementation.</p>
 * 
 * @author mpermar
 *
 */
public class LockUtility {

	private static HashMap locks = new HashMap();
	
	/**
	 * Obtains a lock object for a given node. As every session will use this 
	 * object to synchronize access we can use it to handle locking and to 
	 * synchronize operations like multiple access to the same node from 
	 * different sessions
	 * 
	 * @param node Node for which we want to obtain a lock synchronization 
	 * object
	 * 
	 * @return Object A shared obect associated with the same node that can be 
	 * used to synchronize access to the repository
	 * 
	 * @throws RepositoryException If the lock object cannot be obtained
	 */
	public static synchronized Object obtainLock(
			javax.jcr.Node node) throws RepositoryException {
		
		String id = node.getIdentifier();
		Object lock = locks.get(id);
		if (lock == null) {
			lock = new Object();
			locks.put(id,lock);
		}
		return lock;
	}
}
