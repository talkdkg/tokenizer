package org.tokenizer.core.parser;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.lang.StringUtils;
import org.ccil.cowan.tagsoup.Parser;
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
import org.xml.sax.XMLReader;

public class HtmlParser {
    private static final LSSerializerFilter defaultLSSerializerFilter = new OutputFilter();
    private static final Logger LOG = LoggerFactory.getLogger(HtmlParser.class);

    public static Document parse(InputSource inputSource) {
        try {
            XMLReader reader = new Parser();
            reader.setFeature(Parser.namespacesFeature, false);
            reader.setFeature(Parser.namespacePrefixesFeature, false);
            reader.setFeature(Parser.ignoreBogonsFeature, true);
            reader.setFeature(Parser.bogonsEmptyFeature, false);
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();
            DOMResult result = new DOMResult();
            transformer.transform(new SAXSource(reader, inputSource), result);
            Node htmlNode = result.getNode();
            return (Document) htmlNode;
        } catch (SAXNotRecognizedException e) {
            LOG.error(StringUtils.EMPTY, e);
        } catch (SAXNotSupportedException e) {
            LOG.error(StringUtils.EMPTY, e);
        } catch (TransformerConfigurationException e) {
            LOG.error(StringUtils.EMPTY, e);
        } catch (TransformerFactoryConfigurationError e) {
            LOG.error(StringUtils.EMPTY, e);
        } catch (TransformerException e) {
            LOG.error(StringUtils.EMPTY, e);
        }
        return null;
    }

    public static String format(Node node) {
        return format(node, defaultLSSerializerFilter);
    }

    public static String format(Node node, LSSerializerFilter filter) {
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
    public short acceptNode(Node n) {
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
