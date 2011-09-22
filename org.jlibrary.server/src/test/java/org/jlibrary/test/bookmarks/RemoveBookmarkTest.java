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
package org.jlibrary.test.bookmarks;

import org.jlibrary.core.entities.User;

/**
 * Test to remove bookmarks.
 * 
 * @author mpermar
 *
 */
public class RemoveBookmarkTest extends AbstractBookmarkTest {

	public void testRemoveBookmark() {
		
		try {
			User user = securityService.findUserById(testTicket, testTicket.getUser().getId());			
			assertEquals(findAllUserBookmarks(user),3);
			repositoryService.removeBookmark(testTicket, testRootBookmark.getId());
			
			user = securityService.findUserById(testTicket, testTicket.getUser().getId());						
			assertEquals(findAllUserBookmarks(user),2);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testRemoveParentBookmark() {
		
		try {
			User user = securityService.findUserById(testTicket, testTicket.getUser().getId());			
			assertEquals(findAllUserBookmarks(user),2);			
			repositoryService.removeBookmark(testTicket, testParentBookmark.getId());

			user = securityService.findUserById(testTicket, testTicket.getUser().getId());						
			assertEquals(findAllUserBookmarks(user),0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
