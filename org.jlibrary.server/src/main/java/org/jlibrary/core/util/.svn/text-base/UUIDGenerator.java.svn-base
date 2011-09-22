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
package org.jlibrary.core.util;

import java.net.InetAddress;

/**
 * @author martin
 *
 * Very simple UUID generator based in Hibernate implementation
 */
public class UUIDGenerator {
	
	private static final int IP;
	static {
		int ipadd;
		try {
			ipadd = toInt( InetAddress.getLocalHost().getAddress() );
		}
		catch (Exception e) {
			ipadd = 0;
		}
		IP = ipadd;
	}
	private static short counter = (short) 0;
	private static final int JVM = (int) ( System.currentTimeMillis() >>> 8 );
	
	public UUIDGenerator() {
	}
	
	/**
	 * Unique across JVMs on this machine (unless they load this class
	 * in the same quater second - very unlikely)
	 */
	private static int getJVM() {
		return JVM;
	}
	
	/**
	 * Unique in a millisecond for this JVM instance (unless there
	 * are > Short.MAX_VALUE instances created in a millisecond)
	 */
	private static short getCount() {
		synchronized(UUIDGenerator.class) {
			if (counter<0) counter=0;
			return counter++;
		}
	}
	
	/**
	 * Unique in a local network
	 */
	private static int getIP() {
		return IP;
	}
	
	/**
	 * Unique down to millisecond
	 */
	private static short getHiTime() {
		return (short) ( System.currentTimeMillis() >>> 32 );
	}
	
	private static int getLoTime() {
		return (int) System.currentTimeMillis();
	}
	
	public static String generate(Object obj) {
		
		StringBuffer buffer = new StringBuffer(36)
		.append(getIP())
		.append(getJVM())
		.append(getHiTime())
		.append(getLoTime())
		.append(getCount());
		
		if (buffer.charAt(0) == '-') {
			buffer.deleteCharAt(0);
		}
		return buffer.toString();
	}
	
	public static int toInt( byte[] bytes ) {
		int result = 0;
		for (int i=0; i<4; i++) {
			result = ( result << 8 ) - Byte.MIN_VALUE + (int) bytes[i];
		}
		return result;
	}
}
