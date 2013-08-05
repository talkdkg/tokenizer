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

/**
 * An outgoing link from a page.
 */
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
public class Outlink {

	private static final String NO_FOLLOW_REL_ATTRIBUTE = "nofollow";

	private String toUrl;
	private String anchorText;
	private String relAttributes;

	public Outlink() {
	}

	public Outlink(String toUrl, String anchorText, String relAttributes) {
		this.toUrl = toUrl;
		this.anchorText = anchorText;
		this.relAttributes = relAttributes;
	}

	public Outlink(String toUrl, String anchorText) {
		this(toUrl, anchorText, null);
	}

	public String getToUrl() {
		return toUrl;
	}

	public String getAnchor() {
		return anchorText;
	}

	public String getRelAttributes() {
		return relAttributes;
	}

	public boolean isNoFollow() {
		String relAttributesString = getRelAttributes();
		if (relAttributesString != null) {
			String[] relAttributes = relAttributesString.split("[, \t]");
			for (String relAttribute : relAttributes) {
				if (relAttribute.equalsIgnoreCase(NO_FOLLOW_REL_ATTRIBUTE)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "toUrl: " + toUrl + " anchor: " + anchorText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((anchorText == null) ? 0 : anchorText.hashCode());
		result = prime * result + ((toUrl == null) ? 0 : toUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Outlink other = (Outlink) obj;
		if (anchorText == null) {
			if (other.anchorText != null)
				return false;
		} else if (!anchorText.equals(other.anchorText))
			return false;
		if (toUrl == null) {
			if (other.toUrl != null)
				return false;
		} else if (!toUrl.equals(other.toUrl))
			return false;
		return true;
	}

}
