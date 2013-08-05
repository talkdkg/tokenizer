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
package org.xaloon.core.api.message.model;

import org.xaloon.core.api.classifier.ClassifierItem;

/**
 * @author vytautas r.
 */
public interface TextMessage extends Message {

    /**
     * @return folder where text message resides
     */
    ClassifierItem getFolder();

    /**
     * @param messageFolder
     */
    void setFolder(ClassifierItem messageFolder);

    /**
     * @return subject of this message
     */
    String getSubject();

    /**
     * @param messageSubject
     */
    void setSubject(String messageSubject);
}
