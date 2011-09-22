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
package org.jlibrary.core.jcr.modules;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.input.ProxyInputStream;

/**
 * Input stream used to read only until a certain limit
 * 
 * @author mpermar
 *
 */
public class ImportInputStream extends ProxyInputStream {

	private int limit;
	private int pos = 0;
	private int mark;
	
	public ImportInputStream(InputStream stream, int limit) {
		
		super(stream);
		this.limit = limit;
	}

	public synchronized int read() throws IOException {
		
		if (pos < limit) {
			pos++;
			return super.read();
		} else {
			return -1;
		}		
	}
	
	public synchronized int read(byte b[], int off, int len) throws IOException {
		
		if (pos >= limit) {
			return -1;
		}
		if (pos + len > limit) {
			len = limit - pos;
		}
		if (len <= 0) {
			return 0;
		}
		len = super.read(b,off,len);
		pos += len;
		return len;
	}	
	
	public synchronized long skip(long n) {

		if (pos + n > limit) {
			n = limit - pos;
		}
		if (n < 0) {
			return 0;
		}
		pos += n;
		return n;
	}
	
	public synchronized int available() {

		return limit - pos;
	}
	
	public boolean markSupported() {

		return true;
	}

	public void mark(int markpos) {

		mark = pos;
	}

	public synchronized void reset() {

		pos = mark;
	}	
}
