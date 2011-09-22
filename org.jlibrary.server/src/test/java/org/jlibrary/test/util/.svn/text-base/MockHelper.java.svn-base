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
package org.jlibrary.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Favorite;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Note;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Node.Types;
import org.jlibrary.core.properties.AuthorProperties;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to create several mock objects.
 * 
 * @author mpermar
 *
 */
public class MockHelper {

	static Logger logger = LoggerFactory.getLogger(MockHelper.class);
	
	public static Note createNote(Ticket ticket, Node node) {
		
		Note note = new Note();
		note.setCreator(ticket.getUser().getId());
		note.setDate(Calendar.getInstance().getTime());
		note.setNode(node);
		note.setNote(random(250,true,true));
		
		return note;
	}
	
	public static CategoryProperties createCategory(Ticket ticket, String parentId) {
		
		CategoryProperties properties = new CategoryProperties();
		try {
			properties.addProperty(CategoryProperties.CATEGORY_NAME, random(10,true,true));
			properties.addProperty(CategoryProperties.CATEGORY_DESCRIPTION, random(80,true,true));
			properties.addProperty(CategoryProperties.CATEGORY_REPOSITORY, ticket.getRepositoryId());
			if (parentId != null) {
				properties.addProperty(CategoryProperties.CATEGORY_PARENT, parentId);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;		
	}
	
	public static DocumentProperties createDocument(Ticket ticket, String parentId) {
		
		DocumentProperties properties = new DocumentProperties();
		try {			
			properties.addProperty(DocumentProperties.DOCUMENT_NAME, random(5,true,false));
			properties.addProperty(DocumentProperties.DOCUMENT_DESCRIPTION, random(50,true,true));
			properties.addProperty(DocumentProperties.DOCUMENT_CONTENT, random(5000,true,true).getBytes());
			properties.addProperty(DocumentProperties.DOCUMENT_CREATION_DATE, new Date());
			properties.addProperty(DocumentProperties.DOCUMENT_CREATOR, ticket.getUser().getId());
			properties.addProperty(DocumentProperties.DOCUMENT_IMPORTANCE, Document.IMPORTANCE_MEDIUM);
			properties.addProperty(DocumentProperties.DOCUMENT_KEYWORDS, random(5,true,false));
			properties.addProperty(DocumentProperties.DOCUMENT_LANGUAGE, "es_ES");
			properties.addProperty(DocumentProperties.DOCUMENT_PARENT, parentId);
			properties.addProperty(DocumentProperties.DOCUMENT_POSITION, 1);
			properties.addProperty(DocumentProperties.DOCUMENT_TITLE, random(20,true,false));
			properties.addProperty(DocumentProperties.DOCUMENT_TYPECODE, Types.TEXT);
			properties.addProperty(DocumentProperties.DOCUMENT_URL, random(20,true,false));
			properties.addProperty(DocumentProperties.DOCUMENT_AUTHOR, Author.UNKNOWN);
			properties.addProperty(DocumentProperties.DOCUMENT_PATH, random(8,true,false)+".txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}

	public static DocumentProperties createDocument(Ticket ticket, String parentId, File file) {

		return createDocument(ticket,parentId,file,false);
	}
	
	public static DocumentProperties createDocument(Ticket ticket, String parentId, File file, boolean addContent) {
		
		if (!file.exists() || !file.isFile()) {
			logger.warn("File " + file + " couldn´t be found");
		}
		
		DocumentProperties properties = new DocumentProperties();
		try {			
			properties.addProperty(DocumentProperties.DOCUMENT_NAME, random(5,true,false));
			properties.addProperty(DocumentProperties.DOCUMENT_DESCRIPTION, random(50,true,true));
			properties.addProperty(DocumentProperties.DOCUMENT_CONTENT, random(5000,true,true).getBytes());
			properties.addProperty(DocumentProperties.DOCUMENT_CREATION_DATE, new Date());
			properties.addProperty(DocumentProperties.DOCUMENT_CREATOR, ticket.getUser().getId());
			properties.addProperty(DocumentProperties.DOCUMENT_IMPORTANCE, Document.IMPORTANCE_MEDIUM);
			properties.addProperty(DocumentProperties.DOCUMENT_KEYWORDS, random(5,true,false));
			properties.addProperty(DocumentProperties.DOCUMENT_LANGUAGE, "es_ES");
			properties.addProperty(DocumentProperties.DOCUMENT_PARENT, parentId);
			properties.addProperty(DocumentProperties.DOCUMENT_POSITION, 1);
			properties.addProperty(DocumentProperties.DOCUMENT_TITLE, random(20,true,false));
			properties.addProperty(DocumentProperties.DOCUMENT_TYPECODE, Types.TEXT);
			properties.addProperty(DocumentProperties.DOCUMENT_URL, random(20,true,false));
			properties.addProperty(DocumentProperties.DOCUMENT_AUTHOR, Author.UNKNOWN);
			properties.addProperty(DocumentProperties.DOCUMENT_PATH, random(8,true,false)+".txt");
			
			if (addContent) {
				try {
					FileInputStream fis = new FileInputStream(file);
					byte[] content = IOUtils.toByteArray(fis);
					properties.addProperty(
							DocumentProperties.DOCUMENT_CONTENT,content);
					fis.close();
				} catch (Exception e) {
					throw new RepositoryException(e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}
	
	public static DirectoryProperties createDirectory(Ticket ticket, String parentId) {
		
		DirectoryProperties properties = new DirectoryProperties();
		try {			
			properties.addProperty(DirectoryProperties.DIRECTORY_NAME, random(5,true,false));
			properties.addProperty(DirectoryProperties.DIRECTORY_DESCRIPTION, random(50,true,true));
			properties.addProperty(DirectoryProperties.DIRECTORY_PARENT, parentId);
			properties.addProperty(DirectoryProperties.DIRECTORY_POSITION, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}	
	
	public static ResourceNodeProperties createResourceNode(Ticket ticket, String parentId) {
		
		ResourceNodeProperties properties = new ResourceNodeProperties();
		try {			
			properties.addProperty(ResourceNodeProperties.RESOURCE_NAME, random(5,true,false));
			properties.addProperty(ResourceNodeProperties.RESOURCE_DESCRIPTION, random(50,true,true));
			properties.addProperty(ResourceNodeProperties.RESOURCE_PARENT_ID, parentId);
			properties.addProperty(ResourceNodeProperties.RESOURCE_POSITION, 1);
			properties.addProperty(ResourceNodeProperties.RESOURCE_CONTENT, random(5000,true,true).getBytes());
			properties.addProperty(ResourceNodeProperties.RESOURCE_TYPECODE, Types.TEXT);
			properties.addProperty(ResourceNodeProperties.RESOURCE_PATH, random(8,true,false)+".txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}
	
	public static AuthorProperties createAuthor(Ticket ticket) {

		AuthorProperties properties = new AuthorProperties();
		try {
			properties.addProperty(AuthorProperties.AUTHOR_NAME, random(10,true,true));
			properties.addProperty(AuthorProperties.AUTHOR_REPOSITORY, ticket.getRepositoryId());
			properties.addProperty(AuthorProperties.AUTHOR_BIO, random(255,true,true));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;		
	}

	public static Bookmark createBookmark(Ticket ticket, Bookmark parent, String type) {
		
		Bookmark bookmark = new Bookmark();
		bookmark.setDescription(MockHelper.random(20, true, false));
		bookmark.setName(MockHelper.random(5,true,false));
		bookmark.setParent(parent);
		bookmark.setRepository(ticket.getRepositoryId());
		bookmark.setType(type);
		bookmark.setUrl(MockHelper.random(30,true,false));
		bookmark.setUser(ticket.getUser().getId());
		
		return bookmark;
	}
	
	public static String random(int size, boolean letters, boolean numbers) {
		
		return RandomStringUtils.random(size, letters, numbers);
	}

	public static Favorite createFavorite(Ticket testTicket, String categoryId, String documentId) {

		Favorite favorite = new Favorite();
		favorite.setCategory(categoryId);
		favorite.setDocument(documentId);
		favorite.setRepository(testTicket.getRepositoryId());
		favorite.setUser(null);
		
		return favorite;
	}
	
}
