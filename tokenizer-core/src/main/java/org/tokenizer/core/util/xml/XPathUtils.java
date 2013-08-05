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

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Convenience XPath methods, intended for occassional use or cases where
 * performance is not an issue (e.g. in testcases), as no compiled expression
 * caching is involved.
 * 
 * <p>
 * See {@link LocalXPathExpression} for heavy-duty usages.
 */
public class XPathUtils {
    public static NodeList evalNodeList(String expression, Node node) {
        try {
            return (NodeList) LocalXPathFactory.newXPath().evaluate(expression,
                    node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public static Element evalElement(String expression, Node node) {
        try {
            Node result = (Node) LocalXPathFactory.newXPath().evaluate(
                    expression, node, XPathConstants.NODE);
            if (!(result instanceof Element))
                throw new RuntimeException(
                        "Expected an element from the evaluation of the xpath expression "
                                + expression + ", but got a "
                                + result.getClass().getName());
            return (Element) result;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public static String evalString(String expression, Node node) {
        try {
            return (String) LocalXPathFactory.newXPath().evaluate(expression,
                    node, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public static int evalInt(String expression, Node node) {
        try {
            Number result = (Number) LocalXPathFactory.newXPath().evaluate(
                    expression, node, XPathConstants.NUMBER);
            return result.intValue();
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}
