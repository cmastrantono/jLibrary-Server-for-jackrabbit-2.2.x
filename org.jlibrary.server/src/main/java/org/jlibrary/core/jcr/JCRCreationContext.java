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
package org.jlibrary.core.jcr;

import java.util.HashMap;

import org.jlibrary.core.entities.Bookmark;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Member;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Rol;

/**
 * Stores contents in the context of a object create operation. This is 
 * specially useful for handling node references
 * 
 * @author martin
 *
 */
public class JCRCreationContext {

	private HashMap references = new HashMap();
	
	/**
	 * Adds a node to the context
	 * 
	 * @param node Node to be added
	 */
	public void addNode(Node node) {
		
		references.put(node.getId(),node);
	}
	
	/**
	 * Gets a node in the context
	 * 
	 * @param id Node's id
	 * 
	 * @return Node Node from the context
	 */
	public Node getNode(String id) {
		
		return (Node)references.get(id);
	}
	
	/**
	 * Adds a category to the context
	 * 
	 * @param category Category to be added
	 */
	public void addCategory(Category category) {
		
		references.put(category.getId(),category);
	}
	
	/**
	 * Adds a member to the context
	 * 
	 * @param member Member to be added
	 */
	public void addMember(Member member) {
		
		references.put(member.getId(),member);
	}	
	
	/**
	 * Adds a rol to the context
	 * 
	 * @param rol Rol to be added
	 */
	public void addRol(Rol rol) {
		
		references.put(rol.getId(),rol);
	}	
	
	/**
	 * Adds a bookmark to the context
	 * 
	 * @param bookmark Bookmark to be added
	 */
	public void addBookmark(Bookmark bookmark) {
		
		references.put(bookmark.getId(),bookmark);
	}	
	
	/**
	 * Gets a category in the context
	 * 
	 * @param id Category id
	 * 
	 * @return Category Category from the context
	 */
	public Category getCategory(String id) {
		
		return (Category)references.get(id);
	}	
	
	/**
	 * Gets a member in the context
	 * 
	 * @param id Member id
	 * 
	 * @return member Member from the context
	 */
	public Member getMember(String id) {
		
		return (Member)references.get(id);
	}	
	
	/**
	 * Gets a rol in the context
	 * 
	 * @param id Rol id
	 * 
	 * @return rol Rol from the context
	 */
	public Rol getRol(String id) {
		
		return (Rol)references.get(id);
	}
	
	/**
	 * Gets a bookmark in the context
	 * 
	 * @param id Bookmark id
	 * 
	 * @return bookmark Bookmark from the context
	 */
	public Bookmark getBookmark(String id) {
		
		return (Bookmark)references.get(id);
	}	
	
	/**
	 * Clears the context contents
	 *
	 */
	public void clear() {
		
		references.clear();
	}
}
