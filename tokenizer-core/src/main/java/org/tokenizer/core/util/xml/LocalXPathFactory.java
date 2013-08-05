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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * Serves a similar purpose as {@link LocalDocumentBuilderFactory}.
 */
public class LocalXPathFactory {
    private static ThreadLocal<XPathFactory> LOCAL = new ThreadLocal<XPathFactory>() {
        @Override
        protected XPathFactory initialValue() {
            XPathFactory factory = XPathFactory.newInstance();
            return factory;
        }
    };

    public static XPathFactory getFactory() {
        return LOCAL.get();
    }

    public static XPath newXPath() {
        return LOCAL.get().newXPath();
    }
}
