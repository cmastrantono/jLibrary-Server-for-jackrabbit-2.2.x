package org.jlibrary.core.jcr.compatibility;

import java.io.IOException;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

/**
 * <p>This filter will ensure repositories compatibility with the 1.0 version 
 * from jLibrary. It will be used by the import process to filter XML 
 * according with the 1.0 repositories structure.</p>
 * 
 * <p>This filter will use a CompatibilityHandler1_0 instance as content 
 * handler.</p>
 * 
 * @author mpermar
 *
 */
public class CompatibilityFilter1_0 implements XMLFilter {

	private XMLReader parent;

	/**
	 * @see org.xml.sax.XMLFilter#setParent(org.xml.sax.XMLReader)
	 */
	public void setParent(XMLReader parent) {
		this.parent = parent;
	}

	/**
	 * @see org.xml.sax.XMLFilter#getParent()
	 */
	public XMLReader getParent() {
		return this.parent;
	}

	/**
	 * @see org.xml.sax.XMLReader#setContentHandler(org.xml.sax.ContentHandler)
	 */
	public void setContentHandler(ContentHandler handler) {
		parent.setContentHandler(new CompatibilityParser1_0(handler));
	}


	/**
	 * @see org.xml.sax.XMLReader#getContentHandler()
	 */
	public ContentHandler getContentHandler() {
		return parent.getContentHandler();
	}

	/**
	 * @see org.xml.sax.XMLReader#getFeature(java.lang.String)
	 */
	public boolean getFeature(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		return parent.getFeature(name);
	}

	/**
	 * @see org.xml.sax.XMLReader#setFeature(java.lang.String, boolean)
	 */
	public void setFeature(String name, boolean value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		parent.setFeature(name, value);
	}

	/**
	 * @see org.xml.sax.XMLReader#getProperty(java.lang.String)
	 */
	public Object getProperty(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		return parent.getProperty(name);
	}

	/**
	 * @see org.xml.sax.XMLReader#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		parent.setProperty(name, value);
	}

	/**
	 * @see org.xml.sax.XMLReader#setEntityResolver(org.xml.sax.EntityResolver)
	 */
	public void setEntityResolver(EntityResolver resolver) {
		parent.setEntityResolver(resolver);
	}

	/**
	 * @see org.xml.sax.XMLReader#getEntityResolver()
	 */
	public EntityResolver getEntityResolver() {
		return parent.getEntityResolver();
	}

	/**
	 * @see org.xml.sax.XMLReader#setDTDHandler(org.xml.sax.DTDHandler)
	 */
	public void setDTDHandler(DTDHandler handler) {
		parent.setDTDHandler(handler);
	}

	/**
	 * @see org.xml.sax.XMLReader#getDTDHandler()
	 */
	public DTDHandler getDTDHandler() {
		return parent.getDTDHandler();
	}

	/**
	 * @see org.xml.sax.XMLReader#setErrorHandler(org.xml.sax.ErrorHandler)
	 */
	public void setErrorHandler(ErrorHandler handler) {
		parent.setErrorHandler(handler);
	}

	/**
	 * @see org.xml.sax.XMLReader#getErrorHandler()
	 */
	public ErrorHandler getErrorHandler() {
		return parent.getErrorHandler();
	}

	/**
	 * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
	 */
	public void parse(InputSource input) throws SAXException, IOException {
		parent.parse(input);
	}

	/**
	 * @see org.xml.sax.XMLReader#parse(java.lang.String)
	 */
	public void parse(String systemId) throws SAXException, IOException {
		parent.parse(systemId);
	}
}
