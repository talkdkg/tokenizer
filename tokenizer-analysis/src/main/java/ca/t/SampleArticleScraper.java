package ca.t;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;
import org.w3c.dom.traversal.NodeFilter;
import org.xml.sax.InputSource;

public class SampleArticleScraper {
    private static final Logger LOG = LoggerFactory
            .getLogger(SampleArticleScraper.class);

    public static void main(String[] args) throws Throwable {
        InputStream is = new FileInputStream(
                "src/main/resources/amazon-test.html");
        InputSource source = new InputSource(is);
        source.setEncoding("UTF-8");
        Node node = HtmlParser.parse2(source);
        System.out.println(node.getTextContent());
        // ParsedDocument node = ParsedDocumentBuilder.build(is);
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath
                .compile("//table[@id='productReviews']//td/div");
        XPathExpression nameExpr = xpath.compile("div[3]/div/div[2]/a");
        NodeList nodes = (NodeList) expr.evaluate(node, XPathConstants.NODESET);
        System.out.println(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            // initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource s = new DOMSource(nodes.item(i));
            transformer.transform(s, result);
            String xmlString = result.getWriter().toString();
            // System.out.println(xmlString);
            System.out.println(format(nodes.item(i)));
            Node nameNode = (Node) nameExpr.evaluate(nodes.item(i),
                    XPathConstants.NODE);
            if (nameNode == null)
                continue;
            String name = nameNode.getTextContent();
            name = name.replaceAll("\\s+", " ");
            System.out.println(name);
            String content = nodes.item(i).getTextContent();
            content = content.replaceAll("\\s+", " ");
            System.out.println(content);
        }
    }

    /**
     * @param node
     *            An XML DOM Tree for query
     * @param query
     *            An XPATH query to run against the DOM Tree
     * @param nc
     *            The namespaceContext that maps prefixes to XML namespace
     * @return A list of nodes that result from running the query against the
     *         node.
     * @throws Exception
     *             If anything goes wrong. No error handling for brevity
     */
    public static NodeList xPathQuery(Node node, String query) throws Exception {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        return (NodeList) xpath.evaluate(query, node, XPathConstants.NODESET);
    }

    public static String format(Node node) throws ClassCastException,
            ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        DOMImplementationRegistry registry = DOMImplementationRegistry
                .newInstance();
        DOMImplementationLS impl = (DOMImplementationLS) registry
                .getDOMImplementation("LS");
        LSSerializer writer = impl.createLSSerializer();
        writer.setFilter(new OutputFilter());
        writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        LSOutput output = impl.createLSOutput();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        output.setByteStream(out);
        writer.write(node, output);
        String xmlStr = new String(out.toByteArray());
        return xmlStr;
    }
}

class OutputFilter implements LSSerializerFilter {
    @Override
    public short acceptNode(Node n) {
        if (n instanceof Element) {
            Element element = (Element) n;
            if (element.getTagName().equals("span")
                    || element.getTagName().equals("b")
                    || element.getTagName().equals("a")) {
                return NodeFilter.FILTER_SKIP;
                // return NodeFilter.FILTER_REJECT;
            }
            return NodeFilter.FILTER_ACCEPT;
        } else {
            return NodeFilter.FILTER_SKIP;
        }
    }

    @Override
    public int getWhatToShow() {
        return NodeFilter.SHOW_ELEMENT + NodeFilter.SHOW_ATTRIBUTE;
    }
}
