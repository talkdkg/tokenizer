package org.tokenizer.executor.engine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.parser.HtmlParser;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.UrlScanner;
import org.tokenizer.crawler.db.WebpageRecord;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.HtmlSplitterTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class HtmlSplitterTask extends AbstractTask {
    private static final Logger LOG = LoggerFactory
            .getLogger(HtmlSplitterTask.class);
    HtmlSplitterTaskConfiguration taskConfiguration;

    public HtmlSplitterTask(String taskName, ZooKeeperItf zk,
            TaskConfiguration taskConfiguration,
            CrawlerHBaseRepository crawlerRepository,
            WritableExecutorModel model, HostLocker hostLocker) {
        super(taskName, zk, crawlerRepository, model, hostLocker);
        this.taskConfiguration = (HtmlSplitterTaskConfiguration) taskConfiguration;
        LOG.debug("Instance created");
    }

    @Override
    protected void process() throws InterruptedException, IOException {
        
        
        UrlScanner urlScanner = new UrlScanner(taskConfiguration.getHost(),
                crawlerRepository);
        for (UrlRecord urlRecord : urlScanner) {
            LOG.trace("urlRecord: {}", urlRecord);
            if (urlRecord.getHttpResponseCode() != 200)
                continue;
            // TODO: implement filter via configuration
            // if (urlRecord.getUrl().contains("/product-reviews/") continue;
            WebpageRecord page = crawlerRepository.getWebpageRecord(urlRecord
                    .getDigest());
            if (page == null)
                continue;
            String[] xmlObjects = parse(page);
            if (xmlObjects == null || xmlObjects.length == 1)
                continue;
            crawlerRepository.createXmlObjects(xmlObjects,
                    taskConfiguration.getHost());
        }
    }

    public static String[] parse(WebpageRecord page) {
        return parse(page.getContent(), page.getCharset());
    }

    public static String[] parse(byte[] content, String charset) {
        InputStream is = new ByteArrayInputStream(content);
        InputSource source = new InputSource(is);
        source.setEncoding(charset);
        Node node = HtmlParser.parse(source);
        if (node == null)
            return null;
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr;
        try {
            expr = xpath.compile("//table[@id='productReviews']//td/div");
            NodeList nodes = (NodeList) expr.evaluate(node,
                    XPathConstants.NODESET);
            String[] xmlObjects = new String[nodes.getLength()];
            for (int i = 0; i < nodes.getLength(); i++) {
                String xml = HtmlParser.format(nodes.item(i));
                xmlObjects[i] = xml;
                LOG.trace("XML Snippet Retrieved:\n{}", xml);
            }
            return xmlObjects;
        } catch (XPathExpressionException e) {
            LOG.error(StringUtils.EMPTY, e);
        }
        return null;
    }

    @Override
    public TaskConfiguration getTaskConfiguration() {
        return this.taskConfiguration;
    }

    @Override
    public void setTaskConfiguration(TaskConfiguration taskConfiguration) {
        this.taskConfiguration = (HtmlSplitterTaskConfiguration) taskConfiguration;
    }
}
