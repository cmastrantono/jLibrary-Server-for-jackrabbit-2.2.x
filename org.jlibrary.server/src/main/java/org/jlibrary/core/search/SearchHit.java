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

package org.jlibrary.core.search;

import java.io.Serializable;

/**
 * @author martin
 *
 * <p>This is a simple holder class for a search result entry.</p> 
 */
public class SearchHit implements Serializable, Comparable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8406722916298052635L;
	
	private double score;
	private String id;
	private String name;
	private String repository;
	private Integer importance;
	private String path;
	private String excerpt;

	public String getExcerpt() {
		return excerpt;
	}

	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}

	public Integer getImportance() {
		return importance;
	}

	public void setImportance(Integer importance) {
		this.importance = importance;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param string
	 */
	public void setId(String string) {
		id = string;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}


	/**
	 * @param d
	 */
	public void setScore(double d) {
		score = d;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		return "[ Id : " + id + 
			   ", name : " + name + 
			   ", score : " + score + 
			   " ]";
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {		
		if (!(obj instanceof SearchHit)) return false;
		boolean result =id.equals(((SearchHit)obj).id);		
		return result;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		
		return id.hashCode();
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}
	
	public int compareTo(Object arg0) {

		if (!(arg0 instanceof SearchHit)) return -1;
		
		SearchHit hit = (SearchHit)arg0;
		if (hit.score > this.score) {
			return 1;
		} else if (hit.score < this.score) {
			return -1;
		} 
		return 0;
	}
}
