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
package org.tokenizer.core.parser;

@SuppressWarnings("serial")
public class SimpleContentExtractor extends BaseContentExtractor {
	private StringBuilder _content = new StringBuilder();

	@Override
	public void reset() {
		super.reset();
		_content.setLength(0);
	}

	@Override
	public void addContent(char[] ch, int start, int length) {
		_content.append(ch, start, length);
	}

	@Override
	public void addContent(char ch) {
		_content.append(ch);
	}

	@Override
	public String getContent() {
		return _content.toString();
	}
}
