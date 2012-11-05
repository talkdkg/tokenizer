package ca.t.xml;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.lilyproject.repository.api.QName;
import org.lilyproject.util.location.LocationAttributes;
import org.lilyproject.util.xml.DocumentHelper;
import org.lilyproject.util.xml.LocalXPathExpression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ParsedDocumentBuilder {

	private static LocalXPathExpression UID = new LocalXPathExpression("/asset/uid/text()");

	private static LocalXPathExpression ASSET_TYPE = new LocalXPathExpression("/asset/assetType/text()");
	private static LocalXPathExpression FULL_ASSET_TYPE = new LocalXPathExpression("/asset/fullAssetType/text()");
	private static LocalXPathExpression CATEGORY = new LocalXPathExpression("/asset/categories/category");

	private static LocalXPathExpression PUBLISH_DATE = new LocalXPathExpression(
			"/asset/properties/property[@name='PublishDate']/value/text()");
	private static LocalXPathExpression TITLE = new LocalXPathExpression(
			"/asset/properties/property[@name='Title']/value/text()");
	private static LocalXPathExpression ABSTRACT = new LocalXPathExpression(
			"/asset/properties/property[@name='Abstract']/value/text()");
	private static LocalXPathExpression BODY = new LocalXPathExpression(
			"/asset/properties/property[@name='Body']/value/text()");

	private Document doc;

	private ParsedDocument parsedDocument;

	private ParsedDocumentBuilder() {
		// prevents instantiation
	}

	public static ParsedDocument build(InputStream is) throws ParsedDocumentException {
		Document doc;
		try {
			//doc = DocumentHelper.parse(is);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			doc = factory.newDocumentBuilder().parse(is);

		} catch (Exception e) {
			throw new ParsedDocumentException("Error parsing supplied configuration.", e);
		}
		return new ParsedDocumentBuilder().build(doc);
	}

	private ParsedDocument build(Document doc) throws ParsedDocumentException {
		//validate(doc);
		this.doc = doc;
		this.parsedDocument = new ParsedDocument();

		try {
			buildIndexFields();
		} catch (Exception e) {
			throw new ParsedDocumentException("Error in the configuration.", e);
		}

		return parsedDocument;
	}

	private List<String> parseCSV(String csv) {
		String[] parts = csv.split(",");

		List<String> result = new ArrayList<String>(parts.length);

		for (String part : parts) {
			part = part.trim();
			if (part.length() > 0)
				result.add(part);
		}

		return result;
	}

	private Map<String, String> parseVariantPropertiesPattern(Element caseEl) throws Exception {
		String variant = DocumentHelper.getAttribute(caseEl, "matchVariant", false);

		Map<String, String> varPropsPattern = new HashMap<String, String>();

		if (variant == null)
			return varPropsPattern;

		String[] props = variant.split(",");
		for (String prop : props) {
			prop = prop.trim();
			if (prop.length() > 0) {
				int eqPos = prop.indexOf("=");
				if (eqPos != -1) {
					String propName = prop.substring(0, eqPos);
					String propValue = prop.substring(eqPos + 1);
					if (propName.equals("*")) {
						throw new ParsedDocumentException(
								String.format(
										"Error in matchVariant attribute: the character '*' "
												+ "can only be used as wildcard, not as variant dimension name, attribute = %1$s, at: %2$s",
										variant, LocationAttributes.getLocation(caseEl)));
					}
					varPropsPattern.put(propName, propValue);
				} else {
					varPropsPattern.put(prop, null);
				}
			}
		}

		return varPropsPattern;
	}

	private void buildIndexFields() throws Exception {

		String uid = UID.get().evalAsString(doc);
		parsedDocument.setId(uid);

		
		String assetType = ASSET_TYPE.get().evalAsString(doc);
		String fullAssetType = FULL_ASSET_TYPE.get().evalAsString(doc);

		String publishDate = PUBLISH_DATE.get().evalAsString(doc);
		String title = TITLE.get().evalAsString(doc);
		String _abstract = ABSTRACT.get().evalAsString(doc);
		String body = BODY.get().evalAsString(doc);

		List<Element> elements = CATEGORY.get().evalAsNativeElementList(doc);
		
		for (Element element : elements) {
			parsedDocument.addCategory(element.getTextContent());
		}

		parsedDocument.setAssetType(assetType);
		parsedDocument.setFullAssetType(fullAssetType);
		parsedDocument.setPublishDate(publishDate);
		parsedDocument.setTitle(title);
		parsedDocument.setAbstract(_abstract);
		parsedDocument.setBody(body);

	}

	private void validateName(String name) throws ParsedDocumentException {
		if (name.startsWith("lily.")) {
			throw new ParsedDocumentException("names starting with 'lily.' are reserved for internal uses. Name: "
					+ name);
		}
	}

	private QName parseQName(String qname, Element contextEl) throws ParsedDocumentException {
		int colonPos = qname.indexOf(":");
		if (colonPos == -1) {
			throw new ParsedDocumentException(
					"Field name is not a qualified name, it should include a namespace prefix: " + qname);
		}

		String prefix = qname.substring(0, colonPos);
		String localName = qname.substring(colonPos + 1);

		String uri = contextEl.lookupNamespaceURI(prefix);
		if (uri == null) {
			throw new ParsedDocumentException("Prefix does not resolve to a namespace: " + qname);
		}

		return new QName(uri, localName);
	}

	private void validate(Document document) throws ParsedDocumentException {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			URL url = getClass().getClassLoader().getResource(
					"org/lilyproject/indexer/model/indexerconf/indexerconf.xsd");
			Schema schema = factory.newSchema(url);
			Validator validator = schema.newValidator();
			validator.validate(new DOMSource(document));
		} catch (Exception e) {
			throw new ParsedDocumentException("Error validating indexer configuration against XML Schema.", e);
		}
	}

	public static void validate(InputStream is) throws ParsedDocumentException {
		MyErrorHandler errorHandler = new MyErrorHandler();

		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			URL url = ParsedDocumentBuilder.class.getClassLoader().getResource(
					"org/lilyproject/indexer/model/indexerconf/indexerconf.xsd");
			Schema schema = factory.newSchema(url);
			Validator validator = schema.newValidator();
			validator.setErrorHandler(errorHandler);
			validator.validate(new StreamSource(is));
		} catch (Exception e) {
			if (!errorHandler.hasErrors()) {
				throw new ParsedDocumentException("Error validating indexer configuration.", e);
			} // else it will be reported below
		}

		if (errorHandler.hasErrors()) {
			throw new ParsedDocumentException("The following errors occurred validating the indexer configuration:\n"
					+ errorHandler.getMessage());
		}
	}

	private static class MyErrorHandler implements ErrorHandler {
		private StringBuilder builder = new StringBuilder();

		public void warning(SAXParseException exception) throws SAXException {
		}

		public void error(SAXParseException exception) throws SAXException {
			addException(exception);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			addException(exception);
		}

		public boolean hasErrors() {
			return builder.length() > 0;
		}

		public String getMessage() {
			return builder.toString();
		}

		private void addException(SAXParseException exception) {
			if (builder.length() > 0)
				builder.append("\n");

			builder.append("[").append(exception.getLineNumber()).append(":").append(exception.getColumnNumber());
			builder.append("] ").append(exception.getMessage());
		}
	}
}
