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
package org.jlibrary.test.categories;

import java.util.List;

import org.jlibrary.core.entities.Category;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;


/**
 * Test to check finder methods.
 * 
 * @author mpermar
 *
 */
public class FindCategoryTest extends AbstractCategoryTest {

	public void testFindById() {
		
		try {
			Category category = 
				repositoryService.findCategoryById(testTicket, testCategory.getId());
			assertNotNull(category);
			assertEquals(category.getId(),testCategory.getId());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testFindByName() {
		
		try {
			Category category = 
				repositoryService.findCategoryByName(testTicket, testCategory.getName());
			assertNotNull(category);
			assertEquals(category.getName(),testCategory.getName());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
	
	public void testFindCategoryByIdNotFound() {

		try {
			repositoryService.findCategoryById(testTicket, "-111");
			fail("Category shouldn´t exist");
		} catch (CategoryNotFoundException cnfe) {
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testFindCategoryByNameNotFound() {

		try {
			repositoryService.findCategoryByName(testTicket, "-111");
			fail("Category shouldn´t exist");
		} catch (CategoryNotFoundException cnfe) {
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testFindAllCategories() {

		try {
			List categories = repositoryService.findAllCategories(testTicket);
			// Unknown and test author
			assertTrue(categories.size() == 2);
		} catch (AuthorNotFoundException cnfe) {
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
