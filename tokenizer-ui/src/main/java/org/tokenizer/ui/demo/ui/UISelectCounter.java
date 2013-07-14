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
package org.tokenizer.ui.demo.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UISelectCounter {

	private int counter;

	@Inject
	protected UISelectCounter() {
		super();
	}

	public int getCounter() {
		return counter;
	}

	/**
	 * Just a reminder that singletons should be thread-safe http://code.google.com/p/google-guice/wiki/Scopes
	 */
	public synchronized void inc() {
		counter++;
	}

}
