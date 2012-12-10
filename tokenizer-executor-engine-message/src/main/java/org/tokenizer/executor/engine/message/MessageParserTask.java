package org.tokenizer.executor.engine.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.xml.HXPathExpression;
import org.tokenizer.core.util.xml.LocalXPathFactory;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.MessageRecord;
import org.tokenizer.crawler.db.XmlRecord;
import org.tokenizer.crawler.db.XmlRecords;
import org.tokenizer.executor.engine.AbstractTask;
import org.tokenizer.executor.engine.HostLocker;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.MessageParserTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class MessageParserTask extends AbstractTask {

    private static final Logger LOG = LoggerFactory
            .getLogger(MessageParserTask.class);
    MessageParserTaskConfiguration taskConfiguration;
    // single thread only!
    private HXPathExpression topicXPathExpression;
    private HXPathExpression authorXPathExpression;
    private HXPathExpression ageXPathExpression;
    private HXPathExpression sexXPathExpression;
    private HXPathExpression titleXPathExpression;
    private HXPathExpression contentXPathExpression;
    private HXPathExpression dateXPathExpression;
    private HXPathExpression userRatingXPathExpression;

    public MessageParserTask(final String taskName, final ZooKeeperItf zk,
            final TaskConfiguration taskConfiguration,
            final CrawlerRepository crawlerRepository,
            final WritableExecutorModel model, final HostLocker hostLocker) {
        super(taskName, zk, crawlerRepository, model, hostLocker);
        this.taskConfiguration = (MessageParserTaskConfiguration) taskConfiguration;
        try {
            topicXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(this.taskConfiguration.getTopicXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            authorXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath()
                    .compile(this.taskConfiguration.getAuthorXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            ageXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(this.taskConfiguration.getAgeXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            sexXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(this.taskConfiguration.getSexXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            titleXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(this.taskConfiguration.getTitleXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            contentXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(
                            this.taskConfiguration.getContentXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            dateXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(this.taskConfiguration.getDateXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        try {
            userRatingXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(
                            this.taskConfiguration.getUserRatingXPath()));
        } catch (XPathExpressionException e) {
            LOG.warn(e.getMessage());
        }
        LOG.debug("Instance created");
    }

    @Override
    protected void process() throws InterruptedException, ConnectionException {
        if (topicXPathExpression == null && authorXPathExpression == null
                && ageXPathExpression == null && sexXPathExpression == null
                && userRatingXPathExpression == null
                && titleXPathExpression == null
                && contentXPathExpression == null
                && dateXPathExpression == null)
            return;
        int parseAttemptCounter = 0;
        XmlRecords xmlRecords = crawlerRepository.listXmlRecords(
                taskConfiguration.getHost(), parseAttemptCounter, 100);
        for (XmlRecord xmlRecord : xmlRecords) {
            LOG.trace("xmlRecord: {}", xmlRecord);
            try {
                MessageRecord message = parse(xmlRecord);
                LOG.trace(message.toString());
                crawlerRepository.insertIfNotExists(message);
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

    public MessageRecord parse(final XmlRecord xmlRecord)
            throws XPathExpressionException, ParserConfigurationException,
            SAXException, IOException {
        InputStream is = new ByteArrayInputStream(xmlRecord.getContent());
        InputSource source = new InputSource(is);
        // Node node = HtmlParser.parse(source);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document node = db.parse(source);
        if (node == null)
            return null;
        String topic = (topicXPathExpression == null ? null
                : topicXPathExpression.evalAsString(node));
        String author = (authorXPathExpression == null ? null
                : authorXPathExpression.evalAsString(node));
        String title = (titleXPathExpression == null ? null
                : titleXPathExpression.evalAsString(node));
        String content = (contentXPathExpression == null ? null
                : contentXPathExpression.evalAsString(node));
        String date = (dateXPathExpression == null ? null : dateXPathExpression
                .evalAsString(node));
        String age = (ageXPathExpression == null ? null : ageXPathExpression
                .evalAsString(node));
        String sex = (sexXPathExpression == null ? null : sexXPathExpression
                .evalAsString(node));
        String userRating = (userRatingXPathExpression == null ? null
                : userRatingXPathExpression.evalAsString(node));
        MessageRecord messageRecord = new MessageRecord(author, title, content);
        messageRecord.setHost(xmlRecord.getHost());
        messageRecord.setHostInverted(HttpUtils.getHostInverted(xmlRecord
                .getHost()));
        messageRecord.setTopic(topic);
        messageRecord.setDate(date);
        messageRecord.setAge(age);
        messageRecord.setSex(sex);
        messageRecord.setUserRating(userRating);
        return messageRecord;
    }

    @Override
    public TaskConfiguration getTaskConfiguration() {
        return this.taskConfiguration;
    }

    @Override
    public void setTaskConfiguration(final TaskConfiguration taskConfiguration) {
        this.taskConfiguration = (MessageParserTaskConfiguration) taskConfiguration;
    }
}