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
package org.tokenizer.ui.base.view.component;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.tokenizer.ui.base.guice.uiscope.UIScoped;
import org.tokenizer.ui.base.navigate.StandardPageKeys;
import org.tokenizer.ui.base.navigate.V7Navigator;
import org.tokenizer.ui.base.shiro.LoginStatusMonitor;
import org.tokenizer.ui.base.shiro.V7SecurityManager;
import org.tokenizer.ui.base.ui.ScopedUI;


import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ChameleonTheme;

/**
 * Represents the "logged in" status of the current {@link Subject}. Because it is UIScoped, only one can be used per
 * {@link ScopedUI}.
 * <p>
 * 
 * 
 * 
 * @author David Sowerby 16 Jan 2013
 * 
 */
// TODO I18N
@UIScoped
public class LoginStatusPanel extends Panel implements LoginStatusMonitor, ClickListener {

	private final Label usernameLabel;
	private final Button login_logout_Button;
	private final V7Navigator navigator;
	private boolean loggedIn;

	@Inject
	protected LoginStatusPanel(V7Navigator navigator, V7SecurityManager securityManager) {
		super();
		this.navigator = navigator;
		// this.setWidth("200px");
		// this.setHeight("100px");
		setSizeFull();
		addStyleName(ChameleonTheme.PANEL_BORDERLESS);
		// register with the security manager to monitor status changes
		securityManager.addListener(this);
		usernameLabel = new Label();
		login_logout_Button = new Button();
		login_logout_Button.addClickListener(this);
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		hl.addComponent(usernameLabel);
		hl.addComponent(login_logout_Button);
		this.setContent(hl);
		updateStatus(SecurityUtils.getSubject());
	}

	@Override
	public void updateStatus(Subject subject) {
		if (subject.isAuthenticated()) {
			loggedIn = true;
			login_logout_Button.setCaption("log out");
			usernameLabel.setValue(subject.getPrincipal().toString());
			return;
		}
		if (subject.isRemembered()) {
			loggedIn = false;
			login_logout_Button.setCaption("log in");
			usernameLabel.setValue(subject.getPrincipal().toString() + "?");
			return;
		}
		loggedIn = false;
		login_logout_Button.setCaption("log in");
		usernameLabel.setValue("guest");
	}

	@Override
	public String getActionLabel() {
		return login_logout_Button.getCaption();
	}

	@Override
	public String getUserId() {
		return usernameLabel.getValue();
	}

	public Button getLogin_logout_Button() {
		return login_logout_Button;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (loggedIn) {
			navigator.navigateTo(StandardPageKeys.logout);
		} else {
			navigator.navigateTo(StandardPageKeys.login);
		}

	}

}
