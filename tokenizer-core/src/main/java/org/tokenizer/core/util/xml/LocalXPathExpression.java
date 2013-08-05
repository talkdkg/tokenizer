/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright © 2013 Tokenizer Inc. All rights reserved. 
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

import javax.xml.xpath.XPathExpressionException;

/**
 * Compiled XPathExpressions are not thread-safe (and not re-entrant either).
 * This class helps by managing a thread-based cache of an XPath expression.
 * Instances of this class should typically be stored in static fields.
 */
public class LocalXPathExpression {
    private final String expression;
    private final ThreadLocal<HXPathExpression> LOCAL = new ThreadLocal<HXPathExpression>() {
        @Override
        protected HXPathExpression initialValue() {
            HXPathExpression expr;
            try {
                expr = new HXPathExpression(LocalXPathFactory.newXPath()
                        .compile(expression));
            } catch (XPathExpressionException e) {
                throw new RuntimeException(e);
            }
            return expr;
        }
    };

    public LocalXPathExpression(String expression) {
        this.expression = expression;
    }

    public HXPathExpression get() {
        return LOCAL.get();
    }
}
