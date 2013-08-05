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
package org.tokenizer.executor.engine.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.tokenizer.core.util.xml.HXPathExpression;
import org.tokenizer.core.util.xml.LocalXPathFactory;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.MessageRecord;
import org.tokenizer.crawler.db.XmlRecord;
import org.tokenizer.executor.engine.AbstractTask;
import org.tokenizer.executor.engine.HostLocker;
import org.tokenizer.executor.engine.MetricsCache;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.MessageParserTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class MessageParserTask extends AbstractTask<MessageParserTaskConfiguration> {

    // single thread only!
    private HXPathExpression topicXPathExpression;
    private HXPathExpression authorXPathExpression;
    private HXPathExpression ageXPathExpression;
    private HXPathExpression sexXPathExpression;
    private HXPathExpression titleXPathExpression;
    private HXPathExpression contentXPathExpression;
    private HXPathExpression dateXPathExpression;
    private HXPathExpression userRatingXPathExpression;

    public MessageParserTask(final UUID uuid, final String friendlyName, final ZooKeeperItf zk,
            final MessageParserTaskConfiguration taskConfiguration, final CrawlerRepository crawlerRepository,
            final WritableExecutorModel model, final HostLocker hostLocker) {

        super(uuid, friendlyName, zk, taskConfiguration, crawlerRepository, model, hostLocker);

        try {
            topicXPathExpression = new HXPathExpression(LocalXPathFactory.newXPath().compile(
                    this.taskConfiguration.getTopicXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            authorXPathExpression = new HXPathExpression(LocalXPathFactory.newXPath().compile(
                    this.taskConfiguration.getAuthorXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            ageXPathExpression = new HXPathExpression(LocalXPathFactory.newXPath().compile(
                    this.taskConfiguration.getAgeXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            sexXPathExpression = new HXPathExpression(LocalXPathFactory.newXPath().compile(
                    this.taskConfiguration.getSexXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            titleXPathExpression = new HXPathExpression(LocalXPathFactory.newXPath().compile(
                    this.taskConfiguration.getTitleXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            contentXPathExpression = new HXPathExpression(LocalXPathFactory.newXPath().compile(
                    this.taskConfiguration.getContentXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            dateXPathExpression = new HXPathExpression(LocalXPathFactory.newXPath().compile(
                    this.taskConfiguration.getDateXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            userRatingXPathExpression = new HXPathExpression(LocalXPathFactory.newXPath().compile(
                    this.taskConfiguration.getUserRatingXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        LOG.debug("Instance created");
    }

    @Override
    protected void process() throws InterruptedException, ConnectionException {
        if (topicXPathExpression == null && authorXPathExpression == null && ageXPathExpression == null
                && sexXPathExpression == null && userRatingXPathExpression == null && titleXPathExpression == null
                && contentXPathExpression == null && dateXPathExpression == null) {
            return;
        }
        List<XmlRecord> xmlRecords = crawlerRepository.listXmlRecords(taskConfiguration.getHost(),
                taskConfiguration.getParseAttemptCounter(), 100);
        for (XmlRecord xmlRecord : xmlRecords) {
            LOG.trace("xmlRecord: {}", xmlRecord);
            try {
                MessageRecord message = parse(xmlRecord);
                LOG.trace(message.toString());
                crawlerRepository.insertIfNotExists(message);
                metricsCache.increment(MetricsCache.MESSAGE_TOTAL_KEY);
                xmlRecord.incrementParseAttemptCounter();
                crawlerRepository.updateParseAttemptCounter(xmlRecord);
            } catch (XPathExpressionException e) {
                LOG.error("", e);
            } catch (ParserConfigurationException e) {
                LOG.error("", e);
            } catch (SAXException e) {
                LOG.error("", e);
            } catch (IOException e) {
                LOG.error("", e);
            }
        }
    }

    public MessageRecord parse(final XmlRecord xmlRecord) throws XPathExpressionException,
            ParserConfigurationException, SAXException, IOException {
        InputStream is = new ByteArrayInputStream(xmlRecord.getContent());
        InputSource source = new InputSource(is);
        // Node node = HtmlParser.parse(source);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document node = db.parse(source);
        if (node == null) {
            return null;
        }
        String topic = (topicXPathExpression == null ? null : topicXPathExpression.evalAsString(node));
        String author = (authorXPathExpression == null ? null : authorXPathExpression.evalAsString(node));
        String title = (titleXPathExpression == null ? null : titleXPathExpression.evalAsString(node));
        String content = (contentXPathExpression == null ? null : contentXPathExpression.evalAsString(node));
        String date = (dateXPathExpression == null ? null : dateXPathExpression.evalAsString(node));
        String age = (ageXPathExpression == null ? null : ageXPathExpression.evalAsString(node));
        String sex = (sexXPathExpression == null ? null : sexXPathExpression.evalAsString(node));
        String userRating = (userRatingXPathExpression == null ? null : userRatingXPathExpression.evalAsString(node));
        MessageRecord messageRecord = new MessageRecord(xmlRecord.getDigest(), author, title, content);
        messageRecord.setHost(xmlRecord.getHost());
        messageRecord.setTopic(topic);
        messageRecord.setDate(date);
        messageRecord.setAge(age);
        messageRecord.setSex(sex);
        messageRecord.setUserRating(userRating);
        return messageRecord;
    }

}
