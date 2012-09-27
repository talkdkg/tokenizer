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
package org.tokenizer.executor.model.configuration;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.lilyproject.util.xml.DocumentHelper;
import org.lilyproject.util.xml.LocalXPathExpression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class TaskConfigurationBuilder {

	private static LocalXPathExpression EXECUTOR_TASK_XPATH_EXPRESSION = new LocalXPathExpression("/executor/task");

	private Document doc;
	private TaskConfiguration taskConfiguration;

	private TaskConfigurationBuilder() {
		// private
	}

	public static TaskConfiguration build(InputStream is) throws TaskConfigurationException {
		Document doc;
		try {
			doc = DocumentHelper.parse(is);
		} catch (Exception e) {
			throw new TaskConfigurationException("Error parsing supplied configuration.", e);
		}
		return new TaskConfigurationBuilder().build(doc);
	}

	private TaskConfiguration build(Document doc) throws TaskConfigurationException {
		this.doc = doc;
		this.taskConfiguration = new TaskConfiguration();
		try {
			buildTasks();
		} catch (Exception e) {
			throw new TaskConfigurationException("Error in the Task Configuration.", e);
		}
		return taskConfiguration;
	}

	public static void validate(InputStream is) throws TaskConfigurationException {

		MyErrorHandler errorHandler = new MyErrorHandler();

		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			URL url = TaskConfigurationBuilder.class.getClassLoader().getResource("org/tokenizer/executor/model/configuration/task-config.xsd");
			Schema schema = factory.newSchema(url);
			Validator validator = schema.newValidator();
			validator.setErrorHandler(errorHandler);
			validator.validate(new StreamSource(is));
		} catch (Exception e) {
			if (!errorHandler.hasErrors()) {
				throw new TaskConfigurationException("Error validating the task configuration.", e);
			} 
		}

		if (errorHandler.hasErrors()) {
			throw new TaskConfigurationException("The following errors occurred validating the task configuration:\n" + errorHandler.getMessage());
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

	private void buildTasks() throws Exception {

		List<Element> elements = EXECUTOR_TASK_XPATH_EXPRESSION.get().evalAsNativeElementList(doc);

		for (Element element : elements) {

			String name = DocumentHelper.getElementChild(element, "name", true).getTextContent();
			String type = DocumentHelper.getElementChild(element, "type", true).getTextContent();
			String tld = DocumentHelper.getElementChild(element, "tld", true).getTextContent();

			taskConfiguration.setName(name);
			taskConfiguration.setType(type);
			taskConfiguration.setTld(tld);

			Element[] properties = DocumentHelper.getElementChildren(element, "property");

			for (Element property : properties) {
				String key = DocumentHelper.getElementChild(property, "key", true).getTextContent();
				String value = DocumentHelper.getElementChild(property, "value", true).getTextContent();
				taskConfiguration.addProperty(key, value);
			}

			Element[] seeds = DocumentHelper.getElementChildren(element, "seed");

			for (Element seed : seeds) {
				String seedText = seed.getTextContent();
				taskConfiguration.addSeed(seedText);
			}

		}
	}

}
