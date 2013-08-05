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
package org.tokenizer.ui.v7.view;

import javax.inject.Inject;

import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public class PrivateHomeView implements V7View {
	private final Label label;

	@Inject
	public PrivateHomeView() {
		super();
		label = new Label(this.getClass().getName());
	}

	@Override
	public void enter(V7ViewChangeEvent event) {

	}

	@Override
	public Component getUiComponent() {
		return label;
	}

}
