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
package org.tokenizer.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class MavenUtil {

    public static File findLocalMavenRepository() throws IOException {
        String localRepositoryProperty = System.getProperty("localRepository");
        if (localRepositoryProperty != null)
            return new File(localRepositoryProperty);
        String homeDir = System.getProperty("user.home");
        File mavenSettingsFile = new File(homeDir + "/.m2/settings.xml");
        if (mavenSettingsFile.exists()) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(mavenSettingsFile);
                XPath xpath = XPathFactory.newInstance().newXPath();
                SimpleNamespaceContext nc = new SimpleNamespaceContext();
                nc.addPrefix("m", "http://maven.apache.org/POM/4.0.0");
                xpath.setNamespaceContext(nc);
                String localRepository = xpath.evaluate("string(/m:settings/m:localRepository)", document);
                if (localRepository != null && localRepository.length() > 0)
                    return new File(localRepository);
                // Usage of the POM namespace in settings.xml is optional, so
                // also try without namespace
                localRepository = xpath.evaluate("string(/settings/localRepository)", document);
                if (localRepository != null && localRepository.length() > 0)
                    return new File(localRepository);
            } catch (Exception e) {
                throw new IOException("Error reading Maven settings file at " + mavenSettingsFile.getAbsolutePath(), e);
            }
        }
        return new File(homeDir + "/.m2/repository");
    }

    public static class SimpleNamespaceContext implements NamespaceContext {

        private final Map<String, String> prefixToUri = new HashMap<String, String>();

        public void addPrefix(final String prefix, final String uri) {
            prefixToUri.put(prefix, uri);
        }

        @Override
        public String getNamespaceURI(final String prefix) {
            if (prefix == null)
                throw new IllegalArgumentException("Null argument: prefix");
            if (prefix.equals(XMLConstants.XML_NS_PREFIX))
                return XMLConstants.XML_NS_URI;
            else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE))
                return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
            String uri = prefixToUri.get(prefix);
            if (uri != null)
                return uri;
            else
                return XMLConstants.NULL_NS_URI;
        }

        @Override
        public String getPrefix(final String namespaceURI) {
            throw new RuntimeException("Not implemented.");
        }

        @Override
        public Iterator getPrefixes(final String namespaceURI) {
            throw new RuntimeException("Not implemented.");
        }
    }
}
