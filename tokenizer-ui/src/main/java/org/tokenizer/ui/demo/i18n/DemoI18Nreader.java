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
package org.tokenizer.ui.demo.i18n;

import java.lang.annotation.Annotation;

import org.tokenizer.ui.i18n.I18NAnnotationReader;
import org.tokenizer.ui.i18n.I18NKeys;


public class DemoI18Nreader implements I18NAnnotationReader {

	@Override
	public I18NKeys<?> caption(Annotation annotation) {
		return ((DemoI18N) annotation).caption();
	}

	@Override
	public I18NKeys<?> description(Annotation annotation) {
		return ((DemoI18N) annotation).description();
	}

	@Override
	public I18NKeys<?> value(Annotation annotation) {
		return ((DemoI18N) annotation).value();
	}
}
