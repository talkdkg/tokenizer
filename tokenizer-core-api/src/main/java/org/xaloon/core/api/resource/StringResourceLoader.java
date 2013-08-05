/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.xaloon.core.api.resource;

import java.io.Serializable;
import java.util.Locale;

/**
 * Resource loader for plugins. It might have different implementations. Default implementation is based on Wicket string resource loader.
 * 
 * @author vytautas r.
 * @version 1.1, 09/28/10
 * @since 1.5
 */

public interface StringResourceLoader extends Serializable {
	/**
	 * Returns string value for selected class with provided key
	 * 
	 * @param resourceClass
	 * 			The class to get the string resource for
	 * @param key
	 * 			The key should be a String containing a lookup key into a resource bundle
	 * @return	The string resource value or null if the resource could not be loaded by this loader
	 */
	String getString(Class<?> resourceClass, String key);
	
	/**
	 * Get the string resource for the given combination of component class, resource key, locale
	 * and style. The component class provided is used to allow implementation of component specific
	 * resource loading (e.g. per page or per reusable component). The key should be a String
	 * containing a lookup key into a resource bundle. The locale should contain the locale of the
	 * current operation so that the appropriate set of resources can be selected. The style allows
	 * the set of resources to select to be varied by skin/brand.
	 * 
	 * @param resourceClass
	 * 			The class to get the string resource for
	 * @param key
	 * 			The key should be a String containing a lookup key into a resource bundle
	 * @param locale
	 * 			The locale should contain the locale of the current operation so that the
	 *          appropriate set of resources can be selected
	 * @param style
	 * 			The style identifying the resource set to select the strings from (useful for wicket implementation)
	 * @param variation
	 * 			The components variation (of the style).  Useful for wicket implementation.
	 * @return	The string resource value or null if the resource could not be loaded by this loader
	 */
	String getString(Class<?> resourceClass, String key, Locale locale, String style, String variation);
}
