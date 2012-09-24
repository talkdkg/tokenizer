/*
 * Copyright 2007-2012 Tokenizer Inc.
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
package org.tokenizer.core.datum;

import java.util.Map;

public class ParsedDatum extends UrlDatum {

	private static final long serialVersionUID = 1L;
	private String hostAddress;

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public String getParsedText() {
		return parsedText;
	}

	public void setParsedText(String parsedText) {
		this.parsedText = parsedText;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Outlink[] getOutlinks() {
		return outlinks;
	}

	public void setOutlinks(Outlink[] outlinks) {
		this.outlinks = outlinks;
	}

	public Map<String, String> getParsedMeta() {
		return parsedMeta;
	}

	public void setParsedMeta(Map<String, String> parsedMeta) {
		this.parsedMeta = parsedMeta;
	}

	private String parsedText;
	private String language;
	private String title;
	private Outlink[] outlinks;
	private Map<String, String> parsedMeta;

	/**
	 * No argument constructor for use with FutureTask
	 */
	public ParsedDatum() {
	}

	public ParsedDatum(String url, String hostAddress, String parsedText, String language, String title, Outlink[] outlinks, Map<String, String> parsedMeta) {

		setUrl(url);
		setHostAddress(hostAddress);
		setParsedText(parsedText);
		setLanguage(language);
		setTitle(title);
		setOutlinks(outlinks);
		setParsedMeta(parsedMeta);
	}

}
