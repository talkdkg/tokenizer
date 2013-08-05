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
package org.xaloon.core.api.image;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * @author vytautas r.
 */
public class ImageSize implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int width;

	private int height;

	private String title;

	private String location;

	/**
	 * Construct.
	 * 
	 * @param width
	 * @param height
	 */
	public ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Construct.
	 * 
	 * @param width
	 */
	public ImageSize(int width) {
		this.width = width;
	}

	/**
	 * Gets title.
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets width.
	 * 
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets width.
	 * 
	 * @param width
	 *            width
	 * @return the instance
	 */
	public ImageSize setWidth(int width) {
		this.width = width;
		return this;
	}

	/**
	 * Gets height.
	 * 
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets height.
	 * 
	 * @param height
	 *            height
	 * @return the instance
	 */
	public ImageSize setHeight(int height) {
		this.height = height;
		return this;
	}

	public ImageSize location(String location) {
		this.location = location;
		return this;
	}

	/**
	 * Gets location.
	 * 
	 * @return location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Constructs visual description of image size
	 * 
	 * @return
	 */
	public String getFullTitle() {
		StringBuilder fullTitle = new StringBuilder();
		fullTitle.append('(').append(width).append(" px)");
		if (!StringUtils.isEmpty(title)) {
			fullTitle.append(' ').append(title);
		}
		return fullTitle.toString();
	}

	/**
	 * Sets title.
	 * 
	 * @param title
	 *            title
	 * @return
	 */
	public ImageSize title(String title) {
		this.title = title;
		return this;
	}
}
