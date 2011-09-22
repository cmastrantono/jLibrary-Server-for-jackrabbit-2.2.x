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
package org.jlibrary.core.repository.exception;

/**
 * <p>This exception is launched when the user tries to create a new repository
 * with the name of a repository that has been recently removed and jLibrary 
 * server has not been restarted.</p>
 * 
 * <p>This exception is needed due to some Jackrabbit limitations and probably 
 * it will be removed on a future.</p<
 * 
 * @author martin
 *
 */
//TODO: Remove this class when Jackrabbit supports workspace deletion
public class RecentlyRemovedRepositoryException extends RepositoryException {

	private static final long serialVersionUID = 1L;

	public RecentlyRemovedRepositoryException() {
		super();
	}
}
