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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * Removes startDocument and endDocument events from the SAX stream.
 */
public class StripDocumentHandler implements ContentHandler, LexicalHandler {
    private final ContentHandler ch;
    private final LexicalHandler lh;

    public StripDocumentHandler(ContentHandler contentHandler) {
        this(contentHandler, null);
    }

    public StripDocumentHandler(ContentHandler contentHandler,
            LexicalHandler lexicalHandler) {
        this.ch = contentHandler;
        this.lh = lexicalHandler;
    }

    @Override
    public void endDocument() throws SAXException {
        // intentionally empty
    }

    @Override
    public void startDocument() throws SAXException {
        // intentionally empty
    }

    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {
        this.ch.characters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char ch[], int start, int length)
            throws SAXException {
        this.ch.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        ch.endPrefixMapping(prefix);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        ch.skippedEntity(name);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        ch.setDocumentLocator(locator);
    }

    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        ch.processingInstruction(target, data);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        ch.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        ch.endElement(namespaceURI, localName, qName);
    }

    @Override
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes atts) throws SAXException {
        ch.startElement(namespaceURI, localName, qName, atts);
    }

    @Override
    public void startDTD(String name, String publicId, String systemId)
            throws SAXException {
    }

    @Override
    public void endDTD() throws SAXException {
    }

    @Override
    public void startEntity(String name) throws SAXException {
    }

    @Override
    public void endEntity(String name) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
        if (lh != null) {
            lh.startCDATA();
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        if (lh != null) {
            lh.endCDATA();
        }
    }

    @Override
    public void comment(char ch[], int start, int length) throws SAXException {
        if (lh != null) {
            lh.comment(ch, start, length);
        }
    }
}
