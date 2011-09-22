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
package org.jlibrary.core.properties;

/**
 * @author Martin
 *
 * Constants with property types
 */
public class PropertyType {
	
	public static final int STRING = 0;
	public static final int BINARY = 1;
	public static final int DATE = 2;
	public static final int LONG = 3;
	public static final int DOUBLE = 4;
	public static final int BOOLEAN = 5;
	public static final int SOFTLINK = 6;
	public static final int REFERENCE = 7;
	
	// Ojo, no JCR
	public static final int INTEGER = 8;
	public static final int OBJECT = 9;
}
