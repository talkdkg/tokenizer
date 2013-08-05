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
package org.tokenizer.ui.demo.messages;

import org.tokenizer.ui.v7.view.AbstractScreen;
import org.tokenizer.ui.v7.view.MessageSearchComponent;

import com.vaadin.ui.Component;

public class MessageSearch extends AbstractScreen {

    @Override
    protected Component get() {
        return new MessageSearchComponent();

    }

}
