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
package org.tokenizer.core.parser;

import java.util.ArrayList;
import java.util.List;

import org.tokenizer.core.datum.Outlink;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class SimpleLinkExtractor extends BaseLinkExtractor {
    private boolean _inHead;
    private boolean _skipLinks;
    private List<Outlink> _outlinks = new ArrayList<Outlink>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws org.xml.sax.SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (!_inHead && localName.equalsIgnoreCase("head")) {
            _inHead = true;
        }
        else if (_inHead && localName.equalsIgnoreCase("meta")) {
            // See if we have a robots directive
            String attrName = attributes.getValue("name");
            String content = attributes.getValue("content");
            if ((attrName != null) && attrName.equalsIgnoreCase("robots") && (content != null)) {
                String[] directives = content.split(",");
                for (String directive : directives) {
                    directive = directive.trim().toLowerCase();
                    if (directive.equals("none") || directive.equals("nofollow")) {
                        _skipLinks = true;
                        break;
                    }
                }
            }
        }
    };

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        super.endElement(uri, localName, name);

        if (_inHead && localName.equalsIgnoreCase("head")) {
            _inHead = false;
        }
    }

    @Override
    public void reset() {
        super.reset();
        _outlinks.clear();
        _inHead = false;
        _skipLinks = false;
    }

    @Override
    public void addLink(Outlink link) {
        if (!_skipLinks) {
            _outlinks.add(link);
        }
    }

    @Override
    public Outlink[] getLinks() {
        return _outlinks.toArray(new Outlink[_outlinks.size()]);
    }
}
