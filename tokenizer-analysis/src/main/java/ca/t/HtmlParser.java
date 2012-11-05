package ca.t;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.ccil.cowan.tagsoup.Parser;
import org.dom4j.io.OutputFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2DOM;

public class HtmlParser {
    // public static DocumentFragment parseTagSoup(InputSource input) throws
    // Exception {
    // HTMLDocumentImpl doc = new HTMLDocumentImpl();
    // DocumentFragment frag = doc.createDocumentFragment();
    //
    //
    // ContentHandler handler = new DOMBuilder(doc, frag);
    //
    // // this is another good sample:
    // //ContentHandler handler = new
    // com.sun.org.apache.xalan.internal.xsltc.trax.SAX2DOM();
    //
    //
    // org.ccil.cowan.tagsoup.Parser reader = new
    // org.ccil.cowan.tagsoup.Parser();
    //
    //
    // reader.setContentHandler(handler);
    //
    // reader.setFeature(org.ccil.cowan.tagsoup.Parser.ignoreBogonsFeature,
    // true);
    // reader.setFeature(org.ccil.cowan.tagsoup.Parser.bogonsEmptyFeature,
    // false);
    // reader.setProperty("http://xml.org/sax/properties/lexical-handler",
    // handler);
    //
    // reader.setFeature(Parser.namespacePrefixesFeature,true);
    // reader.setFeature(Parser.namespacesFeature, false);
    //
    // reader.parse(input);
    // return frag;
    // }
    /**
     * @param urlString
     *            The URL of the page to retrieve
     * @return A Node with a well formed XML doc coerced from the page.
     * @throws Exception
     *             if something goes wrong. No error handling at all for
     *             brevity.
     */
    public static Node parse(InputSource inputSource) throws Exception {
        SAXTransformerFactory stf = (SAXTransformerFactory) TransformerFactory
                .newInstance();
        TransformerHandler th = stf.newTransformerHandler();
        // This dom result will contain the results of the transformation
        DOMResult dr = new DOMResult();
        th.setResult(dr);
        Parser parser = new Parser();
        parser.setContentHandler(th);
        // This is where the magic happens to convert HTML to XML
        parser.parse(inputSource);
        return dr.getNode();
    }

    public static Document parse2(InputSource inputSource) throws Exception {
 
        XMLReader reader = new Parser();
        reader.setFeature(Parser.namespacesFeature, false);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        DOMResult result = new DOMResult();
        transformer.transform(new SAXSource(reader, inputSource), result);

        Node htmlNode = result.getNode();
        
        return (Document) htmlNode;
    
    }
    
    
    // and using Saxon, as per Michael Kay
    // http://stackoverflow.com/questions/6783225/tagsoup-and-xpath
    
    // processor proc = new Processor();

//    InputStream input = new FileInputStream("/tmp/g.html");
//    XMLReader reader = new Parser();
//    reader.setFeature(Parser.namespacesFeature, false);
//    Source source = new SAXSource(parser, input);
//
//    DocumentBuilder builder = proc.newDocumentBuilder();
//    XdmNode input = builder.build(source);
//
//    XPathCompiler compiler = proc.newXPathCompiler();
//    XdmValue result = compiler.evaluate("//span", input);
//    System.out.println(result.size());
    
}
