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

import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.User;
import org.jlibrary.test.util.MockHelper;

/**
 * Test to create bookmarks.
 * 
 * @author mpermar
 *
 */
public class CreateBookmarkTest extends AbstractBookmarkTest {

	public void testCreateRootBookmark() {
		
		try {
			User user = securityService.findUserById(testTicket, testTicket.getUser().getId());			
			assertTrue(findAllUserBookmarks(user) == 0);
			testRootBookmark = MockHelper.createBookmark(testTicket, null, Bookmark.FAVORITE); 			
			testRootBookmark = repositoryService.createBookmark(testTicket, testRootBookmark);
			assertNotNull(testRootBookmark);
			assertNotNull(testRootBookmark.getId());
			assertNull(testRootBookmark.getParent());
			
			user = securityService.findUserById(testTicket, testTicket.getUser().getId());			
			assertTrue(findAllUserBookmarks(user) == 1);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testCreateParentBookmark() {
		
		try {
			User user = securityService.findUserById(testTicket, testTicket.getUser().getId());			
			assertTrue(findAllUserBookmarks(user) == 1);
			testParentBookmark = MockHelper.createBookmark(testTicket, null, Bookmark.FOLDER); 			
			testParentBookmark = repositoryService.createBookmark(testTicket, testParentBookmark);
			assertNotNull(testParentBookmark);
			assertNotNull(testParentBookmark.getId());
			assertNull(testParentBookmark.getParent());
			
			user = securityService.findUserById(testTicket, testTicket.getUser().getId());			
			assertTrue(findAllUserBookmarks(user) == 2);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testCreateChildBookmark() {
		
		try {
			User user = securityService.findUserById(testTicket, testTicket.getUser().getId());			
			assertTrue(findAllUserBookmarks(user) == 2);
			testChildBookmark = MockHelper.createBookmark(testTicket, testParentBookmark, Bookmark.FAVORITE); 			
			testChildBookmark = repositoryService.createBookmark(testTicket, testChildBookmark);
			assertNotNull(testRootBookmark);
			assertNotNull(testRootBookmark.getId());
			assertEquals(testParentBookmark, testChildBookmark.getParent());
			
			user = securityService.findUserById(testTicket, testTicket.getUser().getId());						
			assertTrue(findAllUserBookmarks(user) == 3);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
}
