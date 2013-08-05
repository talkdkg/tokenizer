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
package org.tokenizer.core.util.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

/**
 * Since lookup of DocumentBuilderFactory is slow, and DocumentBuilderFactory is
 * not guaranteed to be thread-safe, this class exploits the technique of using
 * a thread local variable to cache a DocumentBuilderFactory instance.
 */
public class LocalDocumentBuilderFactory {
    private static ThreadLocal<DocumentBuilderFactory> LOCAL = new ThreadLocal<DocumentBuilderFactory>() {
        @Override
        protected DocumentBuilderFactory initialValue() {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            return factory;
        }
    };

    public static DocumentBuilderFactory getFactory() {
        return LOCAL.get();
    }

    public static DocumentBuilder getBuilder() {
        try {
            return LOCAL.get().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document newDocument() {
        try {
            return LOCAL.get().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
