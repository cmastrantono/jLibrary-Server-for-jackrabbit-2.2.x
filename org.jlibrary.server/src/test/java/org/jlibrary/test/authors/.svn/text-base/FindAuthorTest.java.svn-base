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
package org.jlibrary.test.authors;

import java.util.List;

import org.jlibrary.core.entities.Author;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;


/**
 * Test to check finder methods.
 * 
 * @author mpermar
 *
 */
public class FindAuthorTest extends AbstractAuthorTest {

	public void testFindById() {
		
		try {
			Author author = 
				repositoryService.findAuthorById(testTicket, testAuthor.getId());
			assertNotNull(author);
			assertEquals(author.getId(),testAuthor.getId());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testFindByName() {
		
		try {
			Author author = 
				repositoryService.findAuthorByName(testTicket, testAuthor.getName());
			assertNotNull(author);
			assertEquals(author.getName(),testAuthor.getName());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
	
	public void testFindAuthorByIdNotFound() {

		try {
			repositoryService.findAuthorById(testTicket, "-111");
			fail("Author shouldn´t exist");
		} catch (AuthorNotFoundException cnfe) {
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testFindAuthorByNameNotFound() {

		try {
			repositoryService.findAuthorByName(testTicket, "-111");
			fail("Author shouldn´t exist");
		} catch (AuthorNotFoundException cnfe) {
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testFindAllAuthors() {

		try {
			List authors = repositoryService.findAllAuthors(testTicket);
			// unknown, test author and test user			
			assertTrue(authors.size() == 3);
		} catch (AuthorNotFoundException cnfe) {
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
