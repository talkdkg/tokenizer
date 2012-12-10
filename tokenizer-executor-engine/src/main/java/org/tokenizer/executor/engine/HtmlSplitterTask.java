package org.tokenizer.executor.engine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.parser.HtmlParser;
import org.tokenizer.core.util.xml.HXPathExpression;
import org.tokenizer.core.util.xml.LocalXPathFactory;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.WebpageRecord;
import org.tokenizer.crawler.db.XmlRecord;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.HtmlSplitterTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class HtmlSplitterTask extends AbstractTask {

    private static final Logger LOG = LoggerFactory
            .getLogger(HtmlSplitterTask.class);
    HtmlSplitterTaskConfiguration taskConfiguration;
    // single thread only!
    private HXPathExpression splitterXPathExpression = null;

    public HtmlSplitterTask(String taskName, ZooKeeperItf zk,
            TaskConfiguration taskConfiguration,
            CrawlerRepository crawlerRepository, WritableExecutorModel model,
            HostLocker hostLocker) {
        super(taskName, zk, crawlerRepository, model, hostLocker);
        this.taskConfiguration = (HtmlSplitterTaskConfiguration) taskConfiguration;
        try {
            splitterXPathExpression = new HXPathExpression(LocalXPathFactory
                    .newXPath().compile(this.taskConfiguration.getXpath()));
        } catch (XPathExpressionException e) {
            LOG.error("", e);
        }
        LOG.debug("Instance created");
    }

    @Override
    protected void process() throws InterruptedException, ConnectionException {
        if (splitterXPathExpression == null)
            return;
        boolean splitterProcessFinished = false;
        List<WebpageRecord> scanner = crawlerRepository.listWebpageRecords(
                taskConfiguration.getHost(), 0, 100, splitterProcessFinished);
        for (WebpageRecord page : scanner) {
            List<XmlRecord> xmlRecords = parse(page);
            if (xmlRecords == null || xmlRecords.isEmpty()) {
                continue;
            }
            crawlerRepository.insertIfNotExist(xmlRecords);
        }
    }

    public List<XmlRecord> parse(WebpageRecord page) {
        return parse(page.getHost(), page.getContent(), page.getCharset());
    }

    public List<XmlRecord> parse(String host, byte[] content, String charset) {
        List<XmlRecord> results = new ArrayList<XmlRecord>();
        InputStream is = new ByteArrayInputStream(content);
        InputSource source = new InputSource(is);
        source.setEncoding(charset);
        Document document = HtmlParser.parse(source);
        if (document == null)
            return null;
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
            } catch (UnsupportedEncodingException e) {
                return null;
            }
            results.add(record);
        }
        return results;
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
