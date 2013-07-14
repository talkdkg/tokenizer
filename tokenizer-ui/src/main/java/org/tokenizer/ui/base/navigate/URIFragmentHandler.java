/*
 * Copyright 2013 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tokenizer.ui.base.navigate;

import java.util.List;

/**
 * Handles the decoding and encoding of a URI Fragment.
 * 
 * @author david
 * 
 */
public interface URIFragmentHandler {
	/**
	 * returns the "virtual page path" of the URIFragment. The path is assumed to finish as soon as a paired parameter
	 * is found. No attempt is made to validate the actual structure of the path, so for example something like
	 * <code>view//subview/a=b</code> will return <code>view//subview</code>. An empty String is returned if
	 * <code>navigationState</code> is null or empty. If <code>navigationState</code> contains only paired parameters,
	 * an empty String is returned.
	 * 
	 * @see #getPathSegments()
	 */
	public String virtualPage();

	public URIFragmentHandler setFragment(String fragment);

	public String fragment();

	public List<String> parameterList();

	/**
	 * Sets the value of the specified parameter. If this parameter already exists, its value is updated, otherwise the
	 * parameter is added to the URI
	 * 
	 * @param paramName
	 * @param value
	 * @return
	 */
	public URIFragmentHandler setParameterValue(String paramName, String value);

	public URIFragmentHandler removeParameter(String paramName);

	void setVirtualPage(String pageName);

	String parameterValue(String paramName);

	/**
	 * 
	 * @return
	 */
	boolean isUseBang();

	/**
	 * If true, use "#!" (hashbang) after the base URI, if false use "#" (hash).
	 * 
	 * @param useBang
	 */
	void setUseBang(boolean useBang);

	/**
	 * Returns the virtual page path, but as an array of segments
	 * 
	 * @return
	 * @see #virtualPage()
	 */
	String[] getPathSegments();

}
