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

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * HXPath stands for "helpful" XPath, a small wrapper around XPathExpression
 * providing some convenience.
 */
public class HXPathExpression {
    private final XPathExpression expr;

    public HXPathExpression(XPathExpression expr) {
        this.expr = expr;
    }

    public String evalAsString(Node node) throws XPathExpressionException {
        String result = expr.evaluate(node);
        if (result.length() == 0)
            return null;
        return result;
    }

    public Boolean evalAsBoolean(Node node) throws XPathExpressionException {
        String value = expr.evaluate(node);
        if (value.length() == 0)
            return null;
        return Boolean.valueOf(value);
    }

    public Integer evalAsInteger(Node node) throws XPathExpressionException {
        String value = expr.evaluate(node);
        if (value.length() == 0)
            return null;
        return Integer.valueOf(value);
    }

    public NodeList evalAsNodeList(Node node) throws XPathExpressionException {
        return (NodeList) expr.evaluate(node, XPathConstants.NODESET);
    }

    public List<Node> evalAsNativeNodeList(Node node)
            throws XPathExpressionException {
        NodeList list = (NodeList) expr.evaluate(node, XPathConstants.NODESET);
        List<Node> newList = new ArrayList<Node>(list.getLength());
        for (int i = 0; i < list.getLength(); i++) {
            newList.add(list.item(i));
        }
        return newList;
    }

    public List<Element> evalAsNativeElementList(Node node)
            throws XPathExpressionException {
        NodeList list = (NodeList) expr.evaluate(node, XPathConstants.NODESET);
        List<Element> newList = new ArrayList<Element>(list.getLength());
        for (int i = 0; i < list.getLength(); i++) {
            newList.add((Element) list.item(i));
        }
        return newList;
    }

    public Element evalAsNativeElement(Node node)
            throws XPathExpressionException {
        return (Element) expr.evaluate(node, XPathConstants.NODE);
    }
}
