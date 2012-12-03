/*
 * Copyright 2007-2012 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
