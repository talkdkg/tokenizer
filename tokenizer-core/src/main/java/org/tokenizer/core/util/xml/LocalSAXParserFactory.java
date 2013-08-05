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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * Serves a similar purpose as {@link LocalDocumentBuilderFactory}.
 */
public final class LocalSAXParserFactory {
    private static ThreadLocal<SAXParserFactory> LOCAL = new ThreadLocal<SAXParserFactory>() {
        @Override
        protected SAXParserFactory initialValue() {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            safeSetFeature(parserFactory,
                    "http://xml.org/sax/features/validation", false);
            safeSetFeature(parserFactory,
                    "http://xml.org/sax/features/external-general-entities",
                    false);
            safeSetFeature(parserFactory,
                    "http://xml.org/sax/features/external-parameter-entities",
                    false);
            safeSetFeature(
                    parserFactory,
                    "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                    false);
            parserFactory.setNamespaceAware(true);
            parserFactory.setValidating(false);
            return parserFactory;
        }
    };

    public static SAXParserFactory getSAXParserFactory() {
        return LOCAL.get();
    }

    public static XMLReader newXmlReader() throws ParserConfigurationException,
            SAXException {
        return getSAXParserFactory().newSAXParser().getXMLReader();
    }

    private static void safeSetFeature(SAXParserFactory factory,
            String feature, boolean value) {
        try {
            factory.setFeature(feature, value);
        } catch (SAXNotRecognizedException e) {
            // ignore
        } catch (SAXNotSupportedException e) {
            // ignore
        } catch (ParserConfigurationException e) {
            // ignore
        }
    }
}
