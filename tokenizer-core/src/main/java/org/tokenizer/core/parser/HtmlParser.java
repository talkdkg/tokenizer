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

import java.io.ByteArrayOutputStream;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.commons.lang.StringUtils;
import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;
import org.w3c.dom.traversal.NodeFilter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class HtmlParser {

	private static final LSSerializerFilter defaultLSSerializerFilter = new NoOutputFilter();
	private static final Logger LOG = LoggerFactory.getLogger(HtmlParser.class);
	private static final Schema HTML_SCHEMA = new HTMLSchema();

	public static Document parse(final InputSource inputSource) {
		try {
			Parser parser = new Parser();
			parser.setFeature(Parser.namespacesFeature, false);
			parser.setFeature(Parser.namespacePrefixesFeature, false);
			parser.setProperty(org.ccil.cowan.tagsoup.Parser.schemaProperty,
					HTML_SCHEMA);
			parser.setFeature(Parser.ignoreBogonsFeature, true);

			SAXTransformerFactory stf = (SAXTransformerFactory) TransformerFactory
					.newInstance();

			TransformerHandler transformerHandler = stf.newTransformerHandler();

			DOMResult domResult = new DOMResult();
			transformerHandler.setResult(domResult);
			parser.setContentHandler(transformerHandler);
			parser.parse(inputSource);

			return (Document) domResult.getNode();

		} catch (SAXNotRecognizedException e) {
			LOG.error(StringUtils.EMPTY, e);
		} catch (SAXNotSupportedException e) {
			LOG.error(StringUtils.EMPTY, e);
		} catch (TransformerConfigurationException e) {
			LOG.error(StringUtils.EMPTY, e);
		} catch (TransformerFactoryConfigurationError e) {
			LOG.error(StringUtils.EMPTY, e);
		} catch (Throwable t) {
			LOG.error("", t);
		}

		return null;
	}

	public static String format(final Node node) {
		return format(node, defaultLSSerializerFilter);
	}

	public static String format(final Node node, final LSSerializerFilter filter) {
		try {
			DOMImplementationRegistry registry = DOMImplementationRegistry
					.newInstance();
			DOMImplementationLS impl = (DOMImplementationLS) registry
					.getDOMImplementation("LS");
			LSSerializer writer = impl.createLSSerializer();
			writer.setFilter(filter);
			writer.getDomConfig().setParameter("format-pretty-print",
					Boolean.TRUE);
			LSOutput output = impl.createLSOutput();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			output.setByteStream(out);
			writer.write(node, output);
			String xmlStr = new String(out.toByteArray());
			return xmlStr;
		} catch (ClassCastException e) {
			LOG.error(StringUtils.EMPTY, e);
		} catch (ClassNotFoundException e) {
			LOG.error(StringUtils.EMPTY, e);
		} catch (InstantiationException e) {
			LOG.error(StringUtils.EMPTY, e);
		} catch (IllegalAccessException e) {
			LOG.error(StringUtils.EMPTY, e);
		}
		return null;
	}
	// and using Saxon, as per Michael Kay
	// http://stackoverflow.com/questions/6783225/tagsoup-and-xpath
	// processor proc = new Processor();
	// InputStream input = new FileInputStream("/tmp/g.html");
	// XMLReader reader = new Parser();
	// reader.setFeature(Parser.namespacesFeature, false);
	// Source source = new SAXSource(parser, input);
	//
	// DocumentBuilder builder = proc.newDocumentBuilder();
	// XdmNode input = builder.build(source);
	//
	// XPathCompiler compiler = proc.newXPathCompiler();
	// XdmValue result = compiler.evaluate("//span", input);
	// System.out.println(result.size());
}

class OutputFilter implements LSSerializerFilter {

	@Override
	public short acceptNode(final Node n) {
		if (n instanceof Element) {
			Element element = (Element) n;
			// Commented out: <a>Fuad</a>Efendi will become FuadEfendi (without
			// spaces) and we do not need that...
			// if (element.getTagName().equals("span")
			// || element.getTagName().equals("b")
			// || element.getTagName().equals("a")) {
			// return NodeFilter.FILTER_SKIP;
			// }
			return NodeFilter.FILTER_ACCEPT;
		} else
			return NodeFilter.FILTER_SKIP;
	}

	@Override
	public int getWhatToShow() {
		return NodeFilter.SHOW_ELEMENT + NodeFilter.SHOW_ATTRIBUTE;
	}
}

class NoOutputFilter implements LSSerializerFilter {

    @Override
    public short acceptNode(final Node n) {
             return NodeFilter.FILTER_ACCEPT;
    }

    @Override
    public int getWhatToShow() {
        return NodeFilter.SHOW_ALL;
    }
}
