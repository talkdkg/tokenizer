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
package org.xaloon.core.impl.path;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.xaloon.core.api.path.AbsolutePathStrategy;
import org.xaloon.core.api.path.DelimiterEnum;
import org.xaloon.core.api.storage.FileDescriptor;
import org.xaloon.core.api.util.UrlUtil;

/**
 * @author vytautas r.
 */
public class DefaultFileDescriptorAbsolutePathStrategy implements AbsolutePathStrategy<FileDescriptor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String generateAbsolutePath(FileDescriptor descriptor, boolean generateUuid, String prefix) {
		if (descriptor == null) {
			throw new IllegalArgumentException("File descriptor is missing in argument list.");
		}
		String name = descriptor.getName();
		String suffix = null;
		if (name.contains(DelimiterEnum.SLASH.value())) {
			name = name.substring(name.lastIndexOf(DelimiterEnum.SLASH.value()) + 1);
		}
		int suffixIndex = name.lastIndexOf(DelimiterEnum.DOT.value());
		if (suffixIndex > 0) {
			suffix = name.substring(suffixIndex + 1);
			name = name.substring(0, suffixIndex);
		}
		StringBuilder result = new StringBuilder();

		// 1. Add prefix
		if (!StringUtils.isEmpty(prefix)) {
			result.append(prefix).append(DelimiterEnum.DASH.value());
		}

		// 2. Generate unique id
		if (generateUuid) {
			result.append(UUID.randomUUID().toString()).append(DelimiterEnum.DASH.value());
		}

		// 3. Add current file name
		result.append(UrlUtil.encode(name));

		// 4. Add default suffix
		if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(suffix)) {
			result.append(DelimiterEnum.DOT.value()).append(suffix);
		}

		return result.toString();
	}

	@Override
	public String generateAbsolutePath(FileDescriptor descriptor) {
		return generateAbsolutePath(descriptor, true, null);
	}
}
