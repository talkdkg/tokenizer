/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright © 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
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
