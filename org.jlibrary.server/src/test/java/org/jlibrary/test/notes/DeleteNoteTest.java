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
package org.jlibrary.test.notes;

import java.util.Iterator;

import org.jlibrary.core.entities.Note;
import org.jlibrary.core.properties.DocumentProperties;

/**
 * Test to delete notes.
 * 
 * @author mpermar
 *
 */
public class DeleteNoteTest extends AbstractNoteTest {

	public void testDeleteNote() {
				
		try {
			DocumentProperties properties = testDocument.dumpProperties();
			assertTrue(testDocument.getNotes() != null);
			assertTrue(testDocument.getNotes().size() == 1);
			Iterator it = testDocument.getNotes().iterator();
			while (it.hasNext()) {
				Note note = (Note)it.next();
				properties.addProperty(DocumentProperties.DOCUMENT_DELETE_NOTE, note);
			}
			testDocument = repositoryService.updateDocument(testTicket, properties);
			
			assertNotNull(testDocument.getNotes());
			assertEquals(testDocument.getNotes().size(),0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
}
