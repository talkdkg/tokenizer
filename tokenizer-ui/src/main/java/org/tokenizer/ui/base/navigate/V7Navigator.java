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
package org.tokenizer.ui.base.navigate;

import java.util.List;

import org.tokenizer.ui.base.ui.ScopedUI;
import org.tokenizer.ui.base.view.LoginView;
import org.tokenizer.ui.base.view.V7View;
import org.tokenizer.ui.base.view.V7ViewChangeListener;


import com.vaadin.server.Page.UriFragmentChangedListener;

/**
 * Looks up the view for the supplied URI, and calls on {@link ScopedUI} to present that view. Listeners are notified
 * before and after a change of view occurs. The {@link #loginSuccessful()} method is called after a successful user
 * login - this allows the navigator to change views appropriate (according to the implementation). Typically this would
 * be to either return to the view where the user was before they went to the login page, or perhaps to a specified
 * landing page (Page here refers really to a V7View - a "virtual page").
 * 
 * @author David Sowerby 20 Jan 2013
 * 
 */
public interface V7Navigator extends UriFragmentChangedListener {

	void navigateTo(String navigationState);

	/**
	 * A convenience method to look up the URI fragment for the {@link StandardPageKeys} and navigate to it
	 * 
	 * @param pageKey
	 */
	void navigateTo(StandardPageKeys pageKey);

	String getNavigationState();

	List<String> geNavigationParams();

	void addViewChangeListener(V7ViewChangeListener listener);

	void removeViewChangeListener(V7ViewChangeListener listener);

	/**
	 * A signal to the navigator that a login has been successful. The implementation defines which view should be
	 * switched to, but typically the view is changed from the {@link LoginView} to the one the user was at before
	 * requesting a log in, or to a "landing page" view.
	 */
	void loginSuccessful();

	/**
	 * Removes any historical navigation state
	 */
	void clearHistory();

	V7View getCurrentView();

	void navigateTo(SitemapNode node);

}
