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
package org.xaloon.core.api.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author vytautas r.
 */
public class UrlInputStreamContainer extends AbstractInputStreamContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String url;

	/**
	 * Construct.
	 * 
	 * @param url
	 */
	public UrlInputStreamContainer(String url) {
		this(url, new InputStreamContainerOptions());
	}

	/**
	 * Construct.
	 * 
	 * @param url
	 * @param options
	 */
	public UrlInputStreamContainer(String url, InputStreamContainerOptions options) {
		super(options);
		this.url = validateAndFix(url);
	}

	private String validateAndFix(String url2) {
		// Check if link is from youtube
		Pattern rex = Pattern.compile("(?:videos\\/|v=)([\\w-]+)");
		Matcher m = rex.matcher(url2);
		if (m.find()) {
			String YouTubeVideoID = m.group(1);
			if (!StringUtils.isEmpty(YouTubeVideoID)) {
				return "http://img.youtube.com/vi/" + YouTubeVideoID + "/1.jpg";
			}
		}

		return url2;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		try {
			return new URL(url).openStream();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Could not create url", e);
		}
	}

	/**
	 * Gets url.
	 * 
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	@Override
	public void close() {
	}

	@Override
	public boolean isEmpty() {
		return StringUtils.isEmpty(url);
	}
}
