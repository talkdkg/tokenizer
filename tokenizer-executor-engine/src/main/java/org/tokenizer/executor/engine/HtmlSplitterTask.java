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
import org.tokenizer.core.util.xml.HXPathExpression;
import org.tokenizer.core.util.xml.LocalXPathFactory;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.WebpageRecord;
import org.tokenizer.crawler.db.XmlRecord;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.HtmlSplitterTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class HtmlSplitterTask extends AbstractTask<HtmlSplitterTaskConfiguration> {

    // single thread only!
    private HXPathExpression splitterXPathExpression = null;

    public HtmlSplitterTask(final UUID uuid, final String friendlyName, final ZooKeeperItf zk,
            final HtmlSplitterTaskConfiguration taskConfiguration, final CrawlerRepository crawlerRepository,
            final WritableExecutorModel model, final HostLocker hostLocker) {

        super(uuid, friendlyName, zk, taskConfiguration, crawlerRepository, model, hostLocker);

        try {
            splitterXPathExpression = new HXPathExpression(LocalXPathFactory.newXPath().compile(
                    this.taskConfiguration.getXpath()));
        } catch (XPathExpressionException e) {
            LOG.error("", e);
        }

    }

    @Override
    protected void process() throws InterruptedException, ConnectionException {
        if (splitterXPathExpression == null) {
            return;
        }
        List<WebpageRecord> webpageRecords = crawlerRepository.listWebpageRecords(taskConfiguration.getHost(),
                taskConfiguration.getSplitAttemptCounter(), 100);
        for (WebpageRecord webpageRecord : webpageRecords) {
            // yes, we encountered that when created dummy task with host=null:
            if (webpageRecord == null) {
                LOG.warn("webpageRecord is null, sleeping 60 seconds...");
                Thread.sleep(60000);
                continue;
            }
            LOG.debug("processing URL: {}", webpageRecord.getUrl());
            List<XmlRecord> xmlRecords = parse(webpageRecord);
            for (XmlRecord xmlRecord : xmlRecords) {
                // LOG.warn("xmlRecord: {}", xmlRecord);
                crawlerRepository.insertIfNotExist(xmlRecord);
                metricsCache.increment(MetricsCache.XML_TOTAL_KEY);
                webpageRecord.addXmlLink(xmlRecord.getDigest());
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
        return parse(page.getHost(), page.getContent(), page.getCharset());
    }

    public List<XmlRecord> parse(final String host, final byte[] content, final String charset) {
        try {
            LOG.trace("Processing HTML: {}", new String(content, charset));
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
        }
        List<XmlRecord> results = new ArrayList<XmlRecord>();
        InputStream is = new ByteArrayInputStream(content);
        InputSource source = new InputSource(is);
        source.setEncoding(charset);
        Document document = HtmlParser.parse(source);
        if (document == null) {
            return null;
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
            LOG.trace("XML Snippet Retrieved:\n{}", xml);
            XmlRecord record;
            try {
                record = new XmlRecord(host, xml.getBytes("UTF-8"));
                results.add(record);
                LOG.debug("XML record created: {}", record);
            } catch (UnsupportedEncodingException e) {
                LOG.error("", e);
            }
        }
        return results;
    }

}
