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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * Helper class for generating pretty-printed XML, with some higher-level
 * methods than what SAX's ContentHandler provides.
 * 
 * <p>
 * Hint: for attributes, use LinkedHashMap if you want to maintain the order.
 */
public class XmlProducer {
    private final ContentHandler result;
    private int indentLevel = 0;
    private static final int INDENT = 2;
    private final Attributes EMPTY_ATTRS = new AttributesImpl();
    private final NamespaceSupport namespaceSupport = new NamespaceSupport();
    private static ThreadLocal LOCAL = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            SAXTransformerFactory transformerFactory = (SAXTransformerFactory) SAXTransformerFactory
                    .newInstance();
            return transformerFactory;
        }
    };
    private static char[] LINE_SEP = new char[] { '\n' };
    private static int LINE_SEP_LENGTH = LINE_SEP.length;

    public XmlProducer(OutputStream outputStream) throws SAXException,
            TransformerConfigurationException {
        TransformerHandler serializer = getTransformerHandler();
        serializer.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
        serializer.getTransformer().setOutputProperty(OutputKeys.ENCODING,
                "UTF-8");
        serializer.setResult(new StreamResult(outputStream));
        this.result = serializer;
        result.startDocument();
        newLine();
    }

    private static TransformerHandler getTransformerHandler()
            throws TransformerConfigurationException {
        return ((SAXTransformerFactory) LOCAL.get()).newTransformerHandler();
    }

    public void flush() throws SAXException {
        result.endDocument();
    }

    public void declarePrefix(String prefix, String uri) throws SAXException {
        result.startPrefixMapping(prefix, uri);
        namespaceSupport.declarePrefix(prefix, uri);
    }

    public void startElement(String name) throws SAXException {
        startElement(name, (Map<String, String>) null);
    }

    public void startElement(String uri, String name) throws SAXException {
        startElement(uri, name, null);
    }

    public void startElement(String name, Map<String, String> attrs)
            throws SAXException {
        startElement(null, name, attrs);
    }

    public void startElement(String uri, String name, Map<String, String> attrs)
            throws SAXException {
        outputSpaces(indentLevel * INDENT);
        outputStartElement(uri, name, attrs);
        newLine();
        indentLevel++;
    }

    private void outputSpaces(int count) throws SAXException {
        char[] spaces = spaces(count).toCharArray();
        result.characters(spaces, 0, spaces.length);
    }

    public void endElement(String name) throws SAXException {
        endElement(null, name);
    }

    public void endElement(String uri, String name) throws SAXException {
        indentLevel--;
        outputSpaces(indentLevel * INDENT);
        closeElement(uri, name);
        newLine();
    }

    private void outputStartElement(String uri, String name,
            Map<String, String> attrs) throws SAXException {
        Attributes attributes;
        if (attrs == null) {
            attributes = EMPTY_ATTRS;
        } else {
            attributes = new MapAttributes(attrs);
        }
        if (uri == null) {
            uri = "";
        }
        String prefix = getPrefixWithColon(uri);
        result.startElement(uri, name, prefix + name, attributes);
        namespaceSupport.pushContext();
    }

    private String getPrefixWithColon(String uri) {
        if (uri.length() > 0) {
            String prefix = namespaceSupport.getPrefix(uri);
            if (prefix == null)
                return "";
            else
                return prefix + ":";
        }
        return "";
    }

    public void emptyElement(String name, Map<String, String> attrs)
            throws SAXException {
        emptyElement(null, name, attrs);
    }

    public void emptyElement(String uri, String name, Map<String, String> attrs)
            throws SAXException {
        outputSpaces(indentLevel * INDENT);
        outputStartElement(uri, name, attrs);
        closeElement(uri, name);
        newLine();
    }

    public void simpleElement(String name, String value) throws SAXException {
        simpleElement(null, name, value, null);
    }

    public void simpleElement(String uri, String name, String value)
            throws SAXException {
        simpleElement(uri, name, value, null);
    }

    public void simpleElement(String name, String value,
            Map<String, String> attrs) throws SAXException {
        simpleElement(null, name, value, attrs);
    }

    public void simpleElement(String uri, String name, String value,
            Map<String, String> attrs) throws SAXException {
        outputSpaces(indentLevel * INDENT);
        outputStartElement(uri, name, attrs);
        result.characters(value.toCharArray(), 0, value.length());
        closeElement(uri, name);
        newLine();
    }

    public void closeElement(String uri, String name) throws SAXException {
        namespaceSupport.popContext();
        if (uri == null) {
            uri = "";
        }
        String prefix = getPrefixWithColon(uri);
        result.endElement(uri, name, prefix + name);
    }

    public void newLine() throws SAXException {
        result.characters(LINE_SEP, 0, LINE_SEP_LENGTH);
    }

    public void embedXml(InputStream is) throws SAXException,
            ParserConfigurationException, IOException {
        StripDocumentHandler handler = new StripDocumentHandler(result,
                (LexicalHandler) result);
        XMLReader reader = LocalSAXParserFactory.newXmlReader();
        reader.setContentHandler(handler);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler",
                handler);
        reader.parse(new InputSource(is));
        newLine();
    }

    public void embedXml(Element element) throws TransformerException,
            SAXException {
        Transformer transformer = LocalTransformerFactory.get()
                .newTransformer();
        transformer.transform(new DOMSource(element), new SAXResult(
                new StripDocumentHandler(result)));
        newLine();
    }

    static class MapAttributes implements Attributes {
        private final Map<String, String> attrs;
        private final Map.Entry<String, String>[] attrList;

        public MapAttributes(Map<String, String> attrs) {
            this.attrs = attrs;
            this.attrList = attrs.entrySet().toArray(new Map.Entry[0]);
        }

        @Override
        public int getLength() {
            return attrs.size();
        }

        @Override
        public String getURI(int index) {
            return "";
        }

        @Override
        public String getLocalName(int index) {
            return attrList[index].getKey();
        }

        @Override
        public String getQName(int index) {
            return attrList[index].getKey();
        }

        @Override
        public String getType(int index) {
            return "CDATA";
        }

        @Override
        public String getValue(int index) {
            return attrList[index].getValue();
        }

        @Override
        public int getIndex(String uri, String localName) {
            if (uri.length() != 0)
                return -1;
            for (int i = 0; i < attrList.length; i++) {
                if (attrList[i].getKey().equals(localName))
                    return i;
            }
            return -1;
        }

        @Override
        public int getIndex(String qName) {
            return getIndex(null, qName);
        }

        @Override
        public String getType(String uri, String localName) {
            return "CDATA";
        }

        @Override
        public String getType(String qName) {
            return "CDATA";
        }

        @Override
        public String getValue(String uri, String localName) {
            if (uri.length() != 0)
                return null;
            return attrs.get(localName);
        }

        @Override
        public String getValue(String qName) {
            return attrs.get(qName);
        }
    }

    private static String spaces(int count) {
        if (count == 2)
            return "  ";
        else if (count == 4)
            return "    ";
        else if (count == 6)
            return "      ";
        else if (count == 8)
            return "        ";
        else {
            StringBuilder spaces = new StringBuilder(count);
            for (int i = 0; i < count; i++) {
                spaces.append(' ');
            }
            return spaces.toString();
        }
    }
}
