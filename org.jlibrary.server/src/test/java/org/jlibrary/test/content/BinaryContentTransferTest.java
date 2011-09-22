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
package org.jlibrary.test.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.DocumentVersion;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.test.util.MockHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test to create documents.
 * 
 * @author mpermar
 *
 */
public class BinaryContentTransferTest extends AbstractContentTest {

	static Logger logger = LoggerFactory.getLogger(BinaryContentTransferTest.class);
	
	static List<Document> documents = new ArrayList<Document>();
	static List<ResourceNode> resources = new ArrayList<ResourceNode>();
	
	public void testCreateDocuments() {
		
		if (!documentsFolder.exists() || 
			!documentsFolder.isDirectory()) {
			logger.warn("There is no folder available for the size test. The size test won´t be executed!");
			return;
		}
		
		Directory parent = repository.getRoot();
		
		try {
			File[] files = documentsFolder.listFiles();
			for (File file: files) {
				if (file.isDirectory()) continue;
				logger.info("Adding document with size : " + file.length() + "Kb");
				DocumentProperties properties = MockHelper.createDocument(
						testTicket, parent.getId(), file,false);
				Document document = repositoryService.createDocument(testTicket, properties);
				assertNotNull(document);
				assertNotNull(document.getId());
				assertEquals(document.getParent(),parent.getId());
				documents.add(document);				
				
				FileInputStream fis = new FileInputStream(file);
				Document updatedDocument = (Document)
					repositoryService.updateContent(testTicket, document.getId(), fis);
				fis.close();				
				assertNotNull(updatedDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	public void testUpdateWithBytesParameter() {
		
		if (!documentsFolder.exists() || 
			!documentsFolder.isDirectory()) {
			logger.warn("There is no folder available for the size test. The size test won´t be executed!");
			return;
		}
		
		Directory parent = repository.getRoot();
		
		try {
			DocumentProperties properties = MockHelper.createDocument(testTicket,parent.getId());
			Document document = repositoryService.createDocument(testTicket,properties);
			Document updatedDocument = (Document)
				repositoryService.updateContent(testTicket, document.getId(), new byte[]{1,2,3});

			assertNotNull(updatedDocument);
			byte[] content = repositoryService.loadDocumentContent(document.getId(), testTicket);
			assertNotNull(content);
			assertEquals(content.length,3);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	public void testLoadDocumentsContent() {
		
		try {
			for (Document document: documents) {
				File tempContent = File.createTempFile("jlib","tmp");
				tempContent.deleteOnExit();
				FileOutputStream fos = new FileOutputStream(tempContent);
				repositoryService.loadDocumentContent(document.getId(), testTicket, fos);
				fos.close();
				
				assertTrue(tempContent.exists());
				assertTrue(tempContent.length() > 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testVersionContent() {
		
		try {
			for (Document document: documents) {
				// First rename the document to create a version
				repositoryService.renameNode(testTicket, document.getId(), "renamed");
				DocumentVersion version = (DocumentVersion)
					repositoryService.getVersions(testTicket, document.getId()).get(0);
				
				File tempContent = File.createTempFile("jlib","tmp");
				tempContent.deleteOnExit();
				FileOutputStream fos = new FileOutputStream(tempContent);
				repositoryService.loadVersionContent(testTicket, version.getId(), fos);
				fos.close();
				
				assertTrue(tempContent.exists());
				assertTrue(tempContent.length() > 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
	
	public void testLoadDocumentContent() {
		
		try {
			byte[] content = repositoryService.loadDocumentContent(testDocument.getId(), testTicket);
			assertNotNull(content);
			assertTrue(content.length > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
	
	public void testCreateResources() {
		
		if (!documentsFolder.exists() || 
			!documentsFolder.isDirectory()) {
			logger.warn("There is no folder available for the size test. The size test won´t be executed!");
			return;
		}
		
		Directory parent = repository.getRoot();
		
		try {
			File[] files = documentsFolder.listFiles();
			for (File file: files) {
				if (file.isDirectory()) continue;
				logger.info("Adding document with size : " + file.length() + "Kb");
				ResourceNodeProperties properties = MockHelper.createResourceNode(
						testTicket, parent.getId());
				ResourceNode resource = repositoryService.createResource(testTicket, properties);
				assertNotNull(resource);
				assertNotNull(resource.getId());
				assertEquals(resource.getParent(),parent.getId());
				resources.add(resource);				
				
				FileInputStream fis = new FileInputStream(file);
				repositoryService.updateContent(testTicket, resource.getId(), fis);
				fis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}	
	
	public void testLoadResourcesContent() {
		
		try {
			for (ResourceNode resource: resources) {
				File tempContent = File.createTempFile("jlib","tmp");
				tempContent.deleteOnExit();
				FileOutputStream fos = new FileOutputStream(tempContent);
				repositoryService.loadResourceNodeContent(testTicket, resource.getId(), fos);
				fos.close();
				
				assertTrue(tempContent.exists());
				assertTrue(tempContent.length() > 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
}
