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
package org.jlibrary.core.jcr.compatibility;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.util.ISO9075;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * <p>SAX handler to filter and parse repositories imported from versions 
 * released previously to 1.0 release.</p>
 * 
 * @author martin
 * 
 */
public class CompatibilityParser1_0 implements ContentHandler {

	private ContentHandler parent;
	private boolean filtered;
	
	/**
	 * Constructor
	 * 
	 * @param handler SAX handler in which this one will delegate tasks
	 */
	public CompatibilityParser1_0(ContentHandler handler) {

		this.parent = handler;
	}

	/**
	 * @see ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {

		parent.startDocument();
	}

	/**
	 * @see ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */	
	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes atts) throws SAXException {

		if (filtered) return;
		try {
			for (int i = 0; i < atts.getLength(); i++) {
			   	Name propName = NameFactoryImpl.getInstance().create(atts.getURI(i), atts.getLocalName(i));
				// decode property name
				propName = NameFactoryImpl.getInstance().create(ISO9075.decode(propName.getLocalName()));

				// value(s)
				String attrValue = atts.getValue(i);
				if (attrValue.equals("jlib:id") || 
				    attrValue.equals("jlib:repository")) {
					filtered = true;
					return;
				}
			}
			parent.startElement(namespaceURI, localName, rawName, atts);
		} catch (Exception re) {
			throw new SAXException(re);
		}
	}

	/**
	 * @see ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */	
	public void endElement(java.lang.String namespaceURI,
			java.lang.String localName, java.lang.String qName)
			throws SAXException {
		if (filtered) {
			if (localName.equals("property")) {		
				filtered = false;
			}
			return;
		}
		parent.endElement(namespaceURI, localName, qName);
	}

	
	/**
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
	 */
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		parent.startPrefixMapping(prefix, uri);
	}

	/**
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	public void endPrefixMapping(String prefix) throws SAXException {
		parent.endPrefixMapping(prefix);
	}

	/**
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator locator) {
		parent.setDocumentLocator(locator);
	}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		parent.endDocument();
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] text, int start, int length)
			throws SAXException {
		if (filtered) return;
		parent.characters(text, start, length);
	}

	/**
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] text, int start, int length)
			throws SAXException {
		parent.ignorableWhitespace(text, start, length);
	}

	/**
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
	 */
	public void processingInstruction(String target, String data)
			throws SAXException {
		parent.processingInstruction(target, data);
	}

	/**
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	public void skippedEntity(String name) throws SAXException {
		parent.skippedEntity(name);
	}
}
