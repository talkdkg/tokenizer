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
package org.tokenizer.ui.base.view;


import com.vaadin.ui.Button;

/**
 * Bind this to your implementation in your ViewModule
 * 
 * @author David Sowerby 1 Jan 2013
 * 
 */
public interface LoginView extends V7View {

	void setUsername(String username);

	void setPassword(String password);

	Button getSubmitButton();

	/**
	 * The message indicating login attempt pass or fail
	 * 
	 * @return
	 */
	String getStatusMessage();

	void setStatusMessage(String invalidLogin);

}
