package org.tokenizer.executor.engine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.parser.HtmlParser;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.xml.HXPathExpression;
import org.tokenizer.core.util.xml.LocalXPathFactory;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.WebpageRecord;
import org.tokenizer.crawler.db.WebpageRecords;
import org.tokenizer.crawler.db.XmlRecord;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.HtmlSplitterTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;
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

    public HtmlSplitterTask(final String taskName, final ZooKeeperItf zk,
            final TaskConfiguration taskConfiguration,
            final CrawlerRepository crawlerRepository,
            final WritableExecutorModel model, final HostLocker hostLocker) {
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
        int splitAttemptCounter = 0;
        WebpageRecords webpageRecords = crawlerRepository.listWebpageRecords(
                taskConfiguration.getHost(), splitAttemptCounter, 100);
        for (WebpageRecord webpageRecord : webpageRecords) {
            List<XmlRecord> xmlRecords = parse(webpageRecord);
            for (XmlRecord xmlRecord : xmlRecords) {
                LOG.warn("xmlRecord: {}", xmlRecord);
                crawlerRepository.insertIfNotExist(xmlRecord);
                metricsCache.increment(MetricsCache.XML_TOTAL_KEY);
            }
            webpageRecord.setSplitAttemptCounter(1);
            crawlerRepository.updateSplitAttemptCounter(webpageRecord);
            metricsCache.increment(MetricsCache.URL_TOTAL_KEY);
        }
        // to prevent spin loop in case if collection is empty:
        LOG.warn("sleeping 10 seconds...");
        Thread.currentThread().sleep(10000);
    }

    public List<XmlRecord> parse(final WebpageRecord page) {
        return parse(page.getHost(), page.getContent(), page.getCharset());
    }

    public List<XmlRecord> parse(final String host, final byte[] content,
            final String charset) {
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
            xml = xml.replaceAll("\\s+", " ");
            LOG.trace("XML Snippet Retrieved:\n{}", xml);
            XmlRecord record;
            try {
                record = new XmlRecord(xml.getBytes("UTF-8"));
                record.setHost(host);
                record.setHostInverted(HttpUtils.getHostInverted(host));
            } catch (UnsupportedEncodingException e) {
                LOG.error("", e);
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
    public void setTaskConfiguration(final TaskConfiguration taskConfiguration) {
        this.taskConfiguration = (HtmlSplitterTaskConfiguration) taskConfiguration;
    }
}
