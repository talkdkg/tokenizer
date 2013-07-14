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
package org.tokenizer.ui.util;

import java.io.File;

import com.vaadin.server.VaadinService;

public class ResourceUtils {
	/**
	 * Returns the base directory path for the application if there VaadinService is running, or the current directory
	 * if not (the latter case is mainly for testing)
	 * 
	 * @return
	 */
	public static String applicationBasePath() {
		if (VaadinService.getCurrent() != null) {
			return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		} else {
			return new File(".").getAbsolutePath();
		}

	}

	/**
	 * Returns the base directory for the application if there VaadinService is running, or the current directory if not
	 * (the latter case is mainly for testing)
	 * 
	 * @return
	 */
	public static File applicationBaseDirectory() {
		return new File(applicationBasePath());
	}
}
