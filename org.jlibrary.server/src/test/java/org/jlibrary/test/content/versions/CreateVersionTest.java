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
package org.jlibrary.test.content.versions;

import java.util.List;

import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.test.content.AbstractContentTest;
import org.jlibrary.test.util.MockHelper;

/**
 * Test for creating versions
 * 
 * @author mpermar
 *
 */
public class CreateVersionTest extends AbstractContentTest {

	public void testCreateNewVersion() {
		
		try {
			// After doing an update you should have a new version
			List versions = 
				repositoryService.getVersions(testTicket, testDocument.getId());
			int size = versions.size();
			repositoryService.renameNode(testTicket, testDocument.getId(), "versioned");
			versions = 
				repositoryService.getVersions(testTicket, testDocument.getId());
			assertEquals(size+1,versions.size());			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testVersionsAtCreate() {
		
		try {
			Directory parent = repository.getRoot();
			DocumentProperties properties =
				MockHelper.createDocument(testTicket, parent.getId());
			Document document = repositoryService.createDocument(testTicket, properties);
			// After doing an update you should have a new version
			List versions = 
				repositoryService.getVersions(testTicket, document.getId());
			assertEquals(versions.size(),0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
}
