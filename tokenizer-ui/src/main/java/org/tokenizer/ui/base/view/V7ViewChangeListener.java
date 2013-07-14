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

public interface V7ViewChangeListener {
	/**
	 * Invoked before the view is changed.
	 * <p>
	 * This method may e.g. open a "save" dialog or question about the change, which may re-initiate the navigation
	 * operation after user action.
	 * <p>
	 * If this listener does not want to block the view change (e.g. does not know the view in question), it should
	 * return true. If any listener returns false, the view change is not allowed and <code>afterViewChange()</code>
	 * methods are not called.
	 * 
	 * @param event
	 *            view change event
	 * @return true if the view change should be allowed or this listener does not care about the view change, false to
	 *         block the change
	 */
	public boolean beforeViewChange(V7ViewChangeEvent event);

	/**
	 * Invoked after the view is changed. If a <code>beforeViewChange</code> method blocked the view change, this method
	 * is not called. Be careful of unbounded recursion if you decide to change the view again in the listener. Note
	 * that this is fired even if the view does not change, but the URL does (this would only happen if the same view
	 * class is used for multiple URLs). This is because some listeners actually want to know about the URL change
	 * 
	 * @param event
	 *            view change event
	 */
	public void afterViewChange(V7ViewChangeEvent event);
}
