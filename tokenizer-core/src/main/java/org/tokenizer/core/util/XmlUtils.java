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
			LOG.debug("charset (based upon content type response header): {}", HttpUtils.getCharsetFromContentType(result.getContentType()));
		}

		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

		Reader reader = null;
		try {
			reader = new InputStreamReader(new ByteArrayInputStream(result.getContent()), HttpUtils.getCharsetFromContentType(result.getContentType()));
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
			if (xmlStreamReader.getEventType() == XMLStreamConstants.START_ELEMENT && elementName.equals(xmlStreamReader.getName().toString())) {
				outputStream = new ByteArrayOutputStream();
				transformer.transform(new StAXSource(xmlStreamReader), new StreamResult(outputStream));
				list.add(outputStream.toByteArray());
			}
		}
		return list;
	}

}
