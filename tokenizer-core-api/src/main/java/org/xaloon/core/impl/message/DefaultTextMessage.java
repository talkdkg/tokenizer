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
package org.xaloon.core.impl.message;

import org.xaloon.core.api.classifier.ClassifierItem;
import org.xaloon.core.api.message.model.TextMessage;

/**
 * @author vytautas r.
 */
public class DefaultTextMessage extends DefaultMessage implements TextMessage {
    private static final long serialVersionUID = 1L;

    private ClassifierItem folder;

    private String subject;

    public ClassifierItem getFolder() {
        return folder;
    }

    public void setFolder(ClassifierItem folder) {
        this.folder = folder;
    }

    /**
     * @see org.xaloon.core.message.Message#getSubject()
     */
    @Override
    public String getSubject() {
        return subject;
    }

    /**
     * @see org.xaloon.core.message.Message#setSubject(java.lang.String)
     */
    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
