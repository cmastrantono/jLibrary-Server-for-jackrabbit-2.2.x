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

/**
 * @author martin
 *
 * Relation between two nodes
 */
public class Relation implements Serializable {

	static final long serialVersionUID = -1590341382749491284L;
	
	private Document destinationNode;
	private boolean bidirectional;

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object relation) {
		
		if (!(relation instanceof Relation)) {
			return false;
		}
		return destinationNode.equals(((Relation)relation).destinationNode);
	}
	
	/**
	 * @return Returns the bidirectional.
	 */
	public boolean isBidirectional() {
		return bidirectional;
	}
	/**
	 * @param bidirectional The bidirectional to set.
	 */
	public void setBidirectional(boolean bidirectional) {
		this.bidirectional = bidirectional;
	}
	/**
	 * @return Returns the destinationNode.
	 */
	public Document getDestinationNode() {
		return destinationNode;
	}
	/**
	 * @param destinationNode The destinationNode to set.
	 */
	public void setDestinationNode(Document destinationNode) {
		this.destinationNode = destinationNode;
	}
}
