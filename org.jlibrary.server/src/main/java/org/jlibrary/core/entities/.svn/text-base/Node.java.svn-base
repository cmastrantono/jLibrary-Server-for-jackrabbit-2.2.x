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
package org.jlibrary.core.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

/** @author Hibernate CodeGenerator */
public class Node implements IResource, Serializable, Comparable {

	static final long serialVersionUID = -2556202589507824493L;
	
	public static transient Integer IMPORTANCE_HIGHEST = new Integer(10);
	public static transient Integer IMPORTANCE_HIGH = new Integer(7);
	public static transient Integer IMPORTANCE_MEDIUM = new Integer(5);
	public static transient Integer IMPORTANCE_LOW = new Integer(3);
	public static transient Integer IMPORTANCE_LOWEST = new Integer(1);
	
	public static class Types {
		public static transient Integer PDF = new Integer(0);
		public static transient Integer WORD = new Integer(1);
		public static transient Integer TEXT = new Integer(2);
		public static transient Integer HTML = new Integer(3);
		public static transient Integer XML = new Integer(4);
		public static transient Integer FOLDER = new Integer(5);
		public static transient Integer RTF = new Integer(6);
		public static transient Integer SWF = new Integer(7);
	};

	private Integer typecode;
	
	private Date date;
	
	private BigDecimal size;

    /** identifier field */
    private String id;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

	/** nullable persistent field */
	private Set notes = new HashSet();

    /** nullable persistent field */
    private String path;
    
    // This will be the path defined as JCR does and not the logical path
    private String JCRPath;

    /** nullable persistent field */
    private String repository;

    /** nullable persistent field */
    private String parent;

    private Integer importance;

    /** persistent field */
    private Set nodes;

    private String creator;
    
    private Lock lock;
    
    private Integer position = new Integer(0);
    
    protected Boolean hasChildren = Boolean.FALSE;
    
    /*
     * These two state flags are only used in client side to mark a document 
     * as new or as deleted. This, for example, is useful to show refreshed 
     * documents that has been recently created or deleted by another user.
     */
    private transient boolean newDocument;
    private transient boolean deletedDocument;

	private Boolean directory = Boolean.FALSE;
	private Boolean resource = Boolean.FALSE;
	private Boolean document = Boolean.FALSE;

	private List restrictions;

    public void copyData(Node node) {
    	
    	setName(node.getName());
    	setDescription(node.getDescription());
    	setPath(node.getPath());
    	setNotes(node.getNotes());
    	setRepository(node.getRepository());
    	setParent(node.getParent());
    	setNodes(node.getNodes());
    	setImportance(node.getImportance());
    	setPosition(node.getPosition());
    }
    
    /** default constructor */
    public Node() {
    }

    public java.lang.String getId() {
        return this.id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }
    public java.lang.String getName() {
        return this.name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }
    public java.lang.String getDescription() {
        return this.description;
    }

    public void setDescription(java.lang.String description) {
        this.description = description;
    }
    public java.lang.String getPath() {
        return this.path;
    }

    public void setPath(java.lang.String path) {
        this.path = path;
    }
    public String getRepository() {
        return this.repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }
    public String getParent() {
    	
        return this.parent;
    }

    public void setParent(String parent) {
    	
        this.parent = parent;
    }

    public java.util.Set getNodes() {
        return this.nodes;
    }

    public void setNodes(java.util.Set nodes) {
        this.nodes = nodes;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public boolean equals(Object other) {
    	
        if ( !(other instanceof Node) ) return false;
        Node castOther = (Node) other;
        if (id == null) return false;
        return id.equals(castOther.id);
    }

    public int hashCode() {
    	
    	if (id == null) return -1;
    	
    	return id.hashCode();
    }

	/**
	 * @return
	 */
	public Set getNotes() {
		return notes;
	}

	/**
	 * @param string
	 */
	public void setNotes(Set set) {
		notes = set;
	}

	/**
	 * Returns the date.
	 * @return Date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the size.
	 * @return BigDecimal
	 */
	public BigDecimal getSize() {
		return size;
	}

	/**
	 * Returns the type.
	 * @return Integer
	 */
	public Integer getTypecode() {
		return typecode;
	}

	/**
	 * Sets the date.
	 * @param date The date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Sets the size.
	 * @param size The size to set
	 */
	public void setSize(BigDecimal size) {
		this.size = size;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setTypecode(Integer type) {
		this.typecode = type;
	}

	public final boolean isDocument() {
		
		return document.booleanValue();
	}
	
	public final boolean isDirectory() {
		
		return directory.booleanValue();
	}
	
	public final boolean isResource() {
		
		return resource.booleanValue();
	}
	
	public final void setDirectory(Boolean yesno) {
		directory = yesno;
	}

	public final void setResource(Boolean yesno) {
		resource = yesno;
	}

	public final void setDocument(Boolean yesno) {
		document = yesno;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		
		Node node = (Node)arg0;
		if (getId() == null) return -1;
		if (node.getId() == null) return 1;
		return getId().compareTo(node.getId());
	}
	/**
	 * @return Returns the importance.
	 */
	public Integer getImportance() {
		return importance;
	}
	/**
	 * @param importance The importance to set.
	 */
	public void setImportance(Integer importance) {
		
		if (importance == null) {
			this.importance = IMPORTANCE_MEDIUM;
			return;
		}
		if (importance.intValue() < IMPORTANCE_LOWEST.intValue()) {
			this.importance = IMPORTANCE_LOWEST;
		} else if (importance.intValue() > IMPORTANCE_HIGHEST.intValue()) {
			this.importance = IMPORTANCE_HIGHEST;
		} else {
			this.importance = importance;
		}
	}
	/**
	 * @return Returns the creator.
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * @param creator The creator to set.
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public Lock getLock() {
		return lock;
	}
	public void setLock(Lock lock) {
		this.lock = lock;
	}
	public boolean isDeletedDocument() {
		return deletedDocument;
	}
	public void setDeletedDocument(boolean deletedDocument) {
		this.deletedDocument = deletedDocument;
	}
	public boolean isNewDocument() {
		return newDocument;
	}
	public void setNewDocument(boolean newDocument) {
		this.newDocument = newDocument;
	}
	
	public boolean isEmpty() {
		
		if (nodes == null) {
			return true;
		}
		return nodes.size() == 0;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		
		if (position == null) {
			this.position = new Integer(0);
		} else {
			this.position = position;
		}
	}

	public boolean hasChildren() {
		
		if (hasChildren.booleanValue()) return true;
		
		if (getNodes() == null) return false;
		// Here we can't simply return false, because maybe someone has 
		// added child nodes to this instance.
		return getNodes().size() > 0;
	}

	public void setHasChildren(Boolean hasChildren) {
		
		this.hasChildren = hasChildren;
	}

	public Boolean getHasChildren() {

		return hasChildren;
	}

	/**
	 * Returns the restrictions for this node
	 * 
	 *  @return List List with all the restrictions
	 */
	public List getRestrictions() {
		
		return restrictions;
	}
	
	/**
	 * Sets the restrictions for a Node. This will be a list of String ids 
	 * that are the ids of the members that can access to this node on the 
	 * moment that the Node was fetched from the server.
	 * 
	 * Note that this is a restrictions snapshot that can be used to speed up 
	 * some user interfaces. The node restrictions can change at any moment, 
	 * so you should use this method carefully and probably have a look at the 
	 * security service that can obtain the restrictions for a node on real time.
	 * 
	 * @param restrictions List of restrictions
	 */
	public void setRestrictions(List restrictions) {
		this.restrictions = restrictions;
	}

	public String getJCRPath() {
		return JCRPath;
	}

	public void setJCRPath(String path) {
		JCRPath = path;
	}
}
