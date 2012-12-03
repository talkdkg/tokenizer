package org.tokenizer.engine.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.xpath.XPathExpressionException;

import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.parser.HtmlParser;
import org.tokenizer.core.util.xml.HXPathExpression;
import org.tokenizer.core.util.xml.LocalXPathFactory;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.crawler.db.MessageRecord;
import org.tokenizer.crawler.db.XmlRecord;
import org.tokenizer.crawler.db.XmlRecordScanner;
import org.tokenizer.executor.engine.AbstractTask;
import org.tokenizer.executor.engine.HostLocker;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.MessageParserTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

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

    public MessageParserTask(String taskName, ZooKeeperItf zk,
            TaskConfiguration taskConfiguration,
            CrawlerHBaseRepository crawlerRepository,
            WritableExecutorModel model, HostLocker hostLocker) {
        super(taskName, zk, crawlerRepository, model, hostLocker);
        this.taskConfiguration = (MessageParserTaskConfiguration) taskConfiguration;
        try {
            topicXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(this.taskConfiguration.getTopicXPath()));
        } catch (XPathExpressionException e) {
            LOG.error("", e);
        }
        try {
            authorXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath()
                    .compile(this.taskConfiguration.getAuthorXPath()));
        } catch (XPathExpressionException e) {
            LOG.error("", e);
        }
        try {
            ageXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(this.taskConfiguration.getAgeXPath()));
        } catch (XPathExpressionException e) {
            LOG.error("", e);
        }
        try {
            sexXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(this.taskConfiguration.getSexXPath()));
        } catch (XPathExpressionException e) {
            LOG.error("", e);
        }
        try {
            titleXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(this.taskConfiguration.getTitleXPath()));
        } catch (XPathExpressionException e) {
            LOG.error("", e);
        }
        try {
            contentXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(
                            this.taskConfiguration.getContentXPath()));
        } catch (XPathExpressionException e) {
            LOG.error("", e);
        }
        try {
            dateXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(this.taskConfiguration.getDateXPath()));
        } catch (XPathExpressionException e) {
            LOG.error("", e);
        }
        try {
            userRatingXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(
                            this.taskConfiguration.getUserRatingXPath()));
        } catch (XPathExpressionException e) {
            LOG.error("", e);
        }
        LOG.debug("Instance created");
    }

    @Override
    protected void process() throws InterruptedException, IOException {
        if (topicXPathExpression == null && authorXPathExpression == null
                && ageXPathExpression == null && sexXPathExpression == null
                && userRatingXPathExpression == null
                && titleXPathExpression == null
                && contentXPathExpression == null
                && dateXPathExpression == null)
            return;
        XmlRecordScanner xmlRecordScanner = new XmlRecordScanner(
                taskConfiguration.getHost(), crawlerRepository);
        for (XmlRecord xmlRecord : xmlRecordScanner) {
            LOG.trace("xmlRecord: {}", xmlRecord);
            try {
                if (xmlRecord.getXml() == null) {
                    continue;
                }
                MessageRecord message = parse(xmlRecord);
                LOG.trace(message.toString());
                crawlerRepository.createMessage(message);
            } catch (XPathExpressionException e) {
                LOG.error("", e);
            }
            //
            // taskConfiguration.getHost());
        }
    }

    public MessageRecord parse(XmlRecord xmlRecord)
            throws XPathExpressionException {
        InputStream is = new ByteArrayInputStream(xmlRecord.getXml());
        InputSource source = new InputSource(is);
        Node node = HtmlParser.parse(source);
        if (node == null)
            return null;
        String topic = topicXPathExpression == null ? null
                : topicXPathExpression.evalAsString(node);
        String author = authorXPathExpression == null ? null
                : authorXPathExpression.evalAsString(node);
        String title = titleXPathExpression == null ? null
                : titleXPathExpression.evalAsString(node);
        String content = contentXPathExpression == null ? null
                : contentXPathExpression.evalAsString(node);
        String date = dateXPathExpression == null ? null : dateXPathExpression
                .evalAsString(node);
        String age = ageXPathExpression == null ? null : ageXPathExpression
                .evalAsString(node);
        String sex = sexXPathExpression == null ? null : sexXPathExpression
                .evalAsString(node);
        String userRating = userRatingXPathExpression == null ? null
                : userRatingXPathExpression.evalAsString(node);
        MessageRecord messageRecord = new MessageRecord();
        messageRecord.setHost(xmlRecord.getHost());
        messageRecord.setTopic(topic);
        messageRecord.setAuthor(author);
        messageRecord.setTitle(title);
        messageRecord.setContent(content);
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
    public void setTaskConfiguration(TaskConfiguration taskConfiguration) {
        this.taskConfiguration = (MessageParserTaskConfiguration) taskConfiguration;
    }
}
