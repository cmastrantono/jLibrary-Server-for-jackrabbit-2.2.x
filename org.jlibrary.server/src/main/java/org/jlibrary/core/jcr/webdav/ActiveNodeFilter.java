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
package org.jlibrary.core.jcr.webdav;

import javax.jcr.Item;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.webdav.simple.DefaultItemFilter;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This is a filter for WebDAV access. It will filter all jLibrary non 
 * active nodes.</p>
 * 
 * @author mpermar
 *
 */
public class ActiveNodeFilter extends DefaultItemFilter {

	static Logger logger = LoggerFactory.getLogger(ActiveNodeFilter.class);
	
	/**
	 * @see org.apache.jackrabbit.webdav.simple.DefaultItemFilter#isFilteredItem(javax.jcr.Item)
	 */
	public boolean isFilteredItem(Item item) {
		
		boolean filtered = super.isFilteredItem(item);
		if (filtered) {
			return true;
		}
		try {
			if (item.isNode()) {
				javax.jcr.Node node = (javax.jcr.Node)item;
				
				if (node.isNodeType(JLibraryConstants.JLIBRARY_MIXIN)) {
					if (node.hasProperty(JLibraryConstants.JLIBRARY_ACTIVE)) {
						boolean active = node.getProperty(
								JLibraryConstants.JLIBRARY_ACTIVE).getBoolean();
						return !active;
					} else {
						return true;
					}
				}
			}
			return filtered;
		} catch (RepositoryException re) {
			logger.error(re.getMessage(),re);
			return true;
		}
	}
}
