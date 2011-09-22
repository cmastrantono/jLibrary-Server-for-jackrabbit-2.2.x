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
package org.jlibrary.core.i18n;

import java.util.Locale;

import org.jlibrary.core.entities.Document;

/**
 * @author martin
 *
 * Service to deal with internationalization
 */
public class LocaleService {
	
	public static final Locale RUSSIAN_LOCALE = new Locale("ru");
	public static final Locale NORWEGIAN_LOCALE = new Locale("no");
	public static final Locale FINNISH_LOCALE = new Locale("fi");
	public static final Locale SWEDISH_LOCALE = new Locale("sw");
	public static final Locale SPANISH_LOCALE = new Locale("es");
	public static final Locale ITALIAN_LOCALE = new Locale("it");
	public static final Locale PORTUGUESE_LOCALE = new Locale("nl");
	public static final Locale DUTCH_LOCALE = new Locale("po");
	public static final Locale ENGLISH_LOCALE = Locale.ENGLISH;
	public static final Locale GERMAN_LOCALE = Locale.GERMAN;
	public static final Locale FRENCH_LOCALE = Locale.FRENCH;
	
	public static final Locale DEFAULT_LOCALE = ENGLISH_LOCALE;
	
	public static Locale getLocale(Document document) {
		
		String language = document.getMetaData().getLanguage();
		return getLocale(language);
	}
	
	public static Locale getLocale(String language) {
		
		if (language.equals("en")) {
			return ENGLISH_LOCALE;
		} else if (language.equals("es")) {
			return SPANISH_LOCALE;			
		} else if (language.equals("it")) {
			return ITALIAN_LOCALE;
		} else if (language.equals("fr")) {
			return FRENCH_LOCALE;
		} else if (language.equals("de")) {
			return GERMAN_LOCALE;
		} else if (language.equals("se")) {
			return SWEDISH_LOCALE;
		} else if (language.equals("no")) {
			return NORWEGIAN_LOCALE;
		} else if (language.equals("fi")) {
			return FINNISH_LOCALE;
		} else if (language.equals("po")) {
			return PORTUGUESE_LOCALE;
		} else if (language.equals("du")) {
			return DUTCH_LOCALE;
		} else if (language.equals("ru")) {
			return RUSSIAN_LOCALE;
		}
		return DEFAULT_LOCALE;
	}
	
	public static Locale getDefaultLocale() {
		
		return DEFAULT_LOCALE;
	}
}
