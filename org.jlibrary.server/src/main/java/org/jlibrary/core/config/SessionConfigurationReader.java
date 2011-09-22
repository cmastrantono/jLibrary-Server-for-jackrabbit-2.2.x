package org.jlibrary.core.config;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SessionConfigurationReader {

	   private static final String BUNDLE_NAME = "org.jlibrary.core.config.sessionconfig";

	   private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	   private SessionConfigurationReader() {
	   }

	   public static String getString(String key) {
	      try {
	         return RESOURCE_BUNDLE.getString(key);
	      } catch (MissingResourceException e) {
	         return '!' + key + '!';
	      }
	   }
	
}
