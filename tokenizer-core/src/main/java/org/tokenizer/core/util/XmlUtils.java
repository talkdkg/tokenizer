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
package org.tokenizer.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.fetcher.FetchedResult;

public class XmlUtils {

    private static final Logger LOG = LoggerFactory.getLogger(XmlUtils.class);

    public static List<byte[]> split(FetchedResult result, String elementName) throws Exception {

        if (LOG.isDebugEnabled()) {
            LOG.debug("content type: {}", result.getContentType());
            LOG.debug("charset (based upon content type response header): {}",
                    HttpUtils.getCharsetFromContentType(result.getContentType()));
        }

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

        Reader reader = null;
        try {
            reader = new InputStreamReader(new ByteArrayInputStream(result.getContent()),
                    HttpUtils.getCharsetFromContentType(result.getContentType()));
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
            return null;
        }

        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(reader);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        ByteArrayOutputStream outputStream;
        List<byte[]> list = new ArrayList<byte[]>();
        while (xmlStreamReader.hasNext() && XMLStreamConstants.END_DOCUMENT != xmlStreamReader.next()) {
            if (xmlStreamReader.getEventType() == XMLStreamConstants.START_ELEMENT
                    && elementName.equals(xmlStreamReader.getName().toString())) {
                outputStream = new ByteArrayOutputStream();
                transformer.transform(new StAXSource(xmlStreamReader), new StreamResult(outputStream));
                list.add(outputStream.toByteArray());
            }
        }
        return list;
    }

}
