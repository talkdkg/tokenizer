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
package org.tokenizer.executor.engine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.tokenizer.core.parser.HtmlParser;
import org.tokenizer.core.util.MD5;
import org.tokenizer.core.util.xml.HXPathExpression;
import org.tokenizer.core.util.xml.LocalXPathFactory;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.model.WebpageRecord;
import org.tokenizer.crawler.db.model.XmlRecord;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.HtmlSplitterTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class HtmlSplitterTask extends
		AbstractTask<HtmlSplitterTaskConfiguration> {

	// single thread only!
	private HXPathExpression splitterXPathExpression = null;
	private HXPathExpression mainSubjectXPathExpression = null;

	public HtmlSplitterTask(final UUID uuid, final String friendlyName,
			final ZooKeeperItf zk,
			final HtmlSplitterTaskConfiguration taskConfiguration,
			final CrawlerRepository crawlerRepository,
			final WritableExecutorModel model, final HostLocker hostLocker) {

		super(uuid, friendlyName, zk, taskConfiguration, crawlerRepository,
				model, hostLocker);

	}
	
	private void resetXPath() {
		try {
			splitterXPathExpression = new HXPathExpression(LocalXPathFactory
					.newXPath().compile(this.taskConfiguration.getXpath()));
		} catch (XPathExpressionException e) {
			LOG.error("", e);
		}

		try {
			mainSubjectXPathExpression = new HXPathExpression(LocalXPathFactory
					.newXPath().compile(this.taskConfiguration.getMainSubjectXPath()));
		} catch (XPathExpressionException e) {
			LOG.error("", e);
		}

	}

	@Override
	protected void process() throws InterruptedException, ConnectionException {

		// this is needed because we can change configuration at runtime...
		resetXPath();
		
		if (splitterXPathExpression == null) {
			LOG.warn("splitterXPathExpression is null, sleeping 60 seconds to avoid spinloop...");
			Thread.sleep(60000);
			return;
		}
		
		List<WebpageRecord> webpageRecords = crawlerRepository
				.listWebpageRecords(taskConfiguration.getHost(),
						taskConfiguration.getSplitAttemptCounter(), 10);
		for (WebpageRecord webpageRecord : webpageRecords) {
			// yes, we encountered that when created dummy task with host=null:
			if (webpageRecord == null) {
				LOG.warn("webpageRecord is null, sleeping 60 seconds...");
				Thread.sleep(60000);
				continue;
			}
			// LOG.debug("processing URL: {}", webpageRecord.getUrl());
			List<XmlRecord> xmlRecords = parse(webpageRecord);
			webpageRecord.getXmlLinks().clear();
			if (xmlRecords == null) {
				webpageRecord.incrementSplitAttemptCounter();
				crawlerRepository.updateSplitAttemptCounterAndLinks(webpageRecord);
				metricsCache.increment(MetricsCache.URL_TOTAL_KEY);
				return;
			}
			for (XmlRecord xmlRecord : xmlRecords) {
				// LOG.warn("xmlRecord: {}", xmlRecord);
				crawlerRepository.insertIfNotExist(xmlRecord);
				metricsCache.increment(MetricsCache.XML_TOTAL_KEY);
				webpageRecord.addXmlLink(xmlRecord.getDigest());
				
				// inverted index: XML Record -> Parent URL
				LOG.warn("Inserting MESAGE_URL_IDX: {}",  webpageRecord.getFetchedUrl());
				crawlerRepository.insertMessageUrlIDX(xmlRecord.getDigest(), webpageRecord.getFetchedUrl());
				
			}
			webpageRecord.incrementSplitAttemptCounter();
			crawlerRepository.updateSplitAttemptCounterAndLinks(webpageRecord);
			metricsCache.increment(MetricsCache.URL_TOTAL_KEY);
		}
		// to prevent spin-loop in case if no records available:
		if (webpageRecords == null || webpageRecords.size() == 0) {
			LOG.warn("Sleeping 60 seconds...");
			Thread.sleep(60000);
		}
	}

	public List<XmlRecord> parse(final WebpageRecord page) {
		// try {
		// tch (UnsupportedEncodingException e) {
		// LOG.error("", e);
		// }

		String contentString = null;

		if (LOG.isDebugEnabled()) {
			try {
				contentString = new String(page.getContent(), page.getCharset());
			} catch (UnsupportedEncodingException e) {
				LOG.error("", e);
			}
		}
		LOG.trace("Processing HTML: {}", contentString);

		List<XmlRecord> results = new ArrayList<XmlRecord>();
		InputStream is = new ByteArrayInputStream(page.getContent());
		InputSource inputSource = new InputSource(is);
		LOG.debug("charset: {}", page.getCharset());
		// iso-8859-1?

		// May be a null parameter
		//inputSource.setSystemId(MD5.toHexString(page.getDigest()));

		// May be a null parameter
		//inputSource.setPublicId(page.getBaseUrl());

		
		inputSource.setEncoding(page.getCharset());
		
		Document document = HtmlParser.parse(inputSource);
		if (document == null) {
			LOG.debug("Document is 'null'... Content size: {}", page.getContent().length);
			LOG.debug("Last 200 characters: {}",
					contentString.substring(contentString.length() - 200));
			return null;
		}
		
		String mainSubject = null;
		try
		{
			mainSubject = (mainSubjectXPathExpression == null ? null : mainSubjectXPathExpression.evalAsString(document));
		}
		catch (XPathExpressionException e)
		{
			LOG.warn("can't process:", e);
		}

		List<Node> nodes;
		try {
			nodes = splitterXPathExpression.evalAsNativeNodeList(document);
		} catch (XPathExpressionException e) {
			LOG.error(StringUtils.EMPTY, e);
			return null;
		}
		for (Node node : nodes) {
			String xml = HtmlParser.format(node);
			LOG.debug("XML Snippet Retrieved:\n{}", xml);
			XmlRecord record;
			try {
				record = new XmlRecord(page.getHost(), xml.getBytes("UTF-8"));
				results.add(record);
				record.setMainSubject(mainSubject);
				LOG.debug("XML record created: {}", record);
			} catch (UnsupportedEncodingException e) {
				LOG.error("", e);
			}

		}
		return results;
	}

}
