/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
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
package org.jlibrary.test.favorites;

import org.jlibrary.core.entities.Category;
import org.jlibrary.test.util.MockHelper;

/**
 * Test to create favorites.
 * 
 * @author mpermar
 *
 */
public class CreateFavoriteTest extends AbstractFavoriteTest {

	public void testCreateFavorite() {
		
		try {

			testFavorite = MockHelper.createFavorite(testTicket, testCategory.getId(), testDocument.getId()); 			
			testFavorite = repositoryService.createFavorite(testTicket, testFavorite);
			
			assertNotNull(testFavorite);
			assertEquals(testFavorite.getCategory(),testCategory.getId());
			assertEquals(testFavorite.getDocument(), testDocument.getId());

			Category category = 
				repositoryService.findCategoryById(testTicket, testCategory.getId());			
			assertTrue(category.getFavorites().contains(testFavorite));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
