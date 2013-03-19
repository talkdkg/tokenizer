package org.tokenizer.executor.engine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.executor.model.configuration.WeblogsSubscriberTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.RedirectFetchException;
import crawlercommons.fetcher.http.BaseHttpFetcher.RedirectMode;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;

public class WeblogsSubscriberTask extends AbstractTask {

    private static final Logger LOG = LoggerFactory
            .getLogger(WeblogsSubscriberTask.class);
    private static String url = "http://rpc.weblogs.com/shortChanges.xml";

    private static final int FIVE_MINUTES = 5 * 60 * 1000;

    private WeblogsSubscriberTaskConfiguration taskConfiguration;

    private final SimpleHttpFetcher simpleHttpClient;

    public WeblogsSubscriberTask(UUID uuid, String friendlyName,
            ZooKeeperItf zk, final TaskConfiguration taskConfiguration,
            CrawlerRepository crawlerRepository, WritableExecutorModel model,
            HostLocker hostLocker) {
        super(uuid, friendlyName, zk, crawlerRepository, model, hostLocker);
        this.taskConfiguration = (WeblogsSubscriberTaskConfiguration) taskConfiguration;

        UserAgent userAgent = new UserAgent(
                this.taskConfiguration.getAgentName(),
                this.taskConfiguration.getEmailAddress(),
                this.taskConfiguration.getWebAddress(),
                UserAgent.DEFAULT_BROWSER_VERSION, "2.1");

        LOG.warn("userAgent: {}", userAgent.getUserAgentString());
        // to be safe if it is singleton: 1024
        simpleHttpClient = new SimpleHttpFetcher(1024, userAgent);
        simpleHttpClient.setSocketTimeout(30000);
        simpleHttpClient.setConnectionTimeout(30000);
        simpleHttpClient.setRedirectMode(RedirectMode.FOLLOW_NONE);
        simpleHttpClient.setDefaultMaxContentSize(1024 * 1024 * 1024);

    }

    @Override
    public TaskConfiguration getTaskConfiguration() {
        return this.taskConfiguration;
    }

    @Override
    public void setTaskConfiguration(TaskConfiguration taskConfiguration) {
        this.taskConfiguration = (WeblogsSubscriberTaskConfiguration) taskConfiguration;
    }

    @Override
    protected void process() throws InterruptedException, ConnectionException {

        LOG.debug("Trying URL: {}", url);
        FetchedResult fetchedResult = null;
        try {
            fetchedResult = simpleHttpClient.get(url, null);

            LOG.debug("fetchedResult: {}", fetchedResult);

            StaXParser read = new StaXParser();
            WeblogUpdates weblogUpdates = read.parseContent(fetchedResult
                    .getContent());
            for (Weblog item : weblogUpdates.getWeblogs()) {
                UrlRecord urlRecord = new UrlRecord(item.getUrl());
                crawlerRepository.insertIfNotExists(urlRecord);
                LOG.debug("new record inserted: {}", urlRecord);
            }

        } catch (RedirectFetchException e) {
            LOG.error("", e);
        } catch (BaseFetchException e) {
            LOG.error("", e);
        }

        thread.sleep(FIVE_MINUTES);
    }

    public static void main(final String[] args) throws Exception {

        UserAgent userAgent = new UserAgent("Googlebot", "", "",
                UserAgent.DEFAULT_BROWSER_VERSION, "2.1");
        SimpleHttpFetcher httpClient = new SimpleHttpFetcher(1, userAgent);
        httpClient.setSocketTimeout(30000);
        httpClient.setConnectionTimeout(30000);
        httpClient.setRedirectMode(RedirectMode.FOLLOW_NONE);
        httpClient.setDefaultMaxContentSize(1024 * 1024 * 1024);

        FetchedResult fetchedResult = httpClient.get(url, null);

        System.out.println(fetchedResult.toString());

        StaXParser read = new StaXParser();
        WeblogUpdates weblogUpdates = read.parseContent(fetchedResult
                .getContent());
        System.out.println(weblogUpdates);
    }

    public static class WeblogUpdates {

        private String version;
        private Date updated;
        private int count;
        private List<Weblog> weblogs;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Date getUpdated() {
            return updated;
        }

        public void setUpdated(Date updated) {
            this.updated = updated;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<Weblog> getWeblogs() {
            return weblogs;
        }

        public void setWeblogs(List<Weblog> weblogs) {
            this.weblogs = weblogs;
        }

        @Override
        public String toString() {
            return "WeblogUpdates [version=" + version + ", updated=" + updated
                    + ", count=" + count + ", weblogs=" + weblogs + "]";
        }

    }

    public static class Weblog {
        private String name;
        private String url;
        private int when;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWhen() {
            return when;
        }

        public void setWhen(int when) {
            this.when = when;
        }

        @Override
        public String toString() {
            return "\nWeblog [name=" + name + ", url=" + url + ", when=" + when
                    + "]";
        }

    }

    public static class StaXParser {
        static final String WEBLOG_UPDATES = "weblogUpdates";
        static final String VERSION = "version";
        static final String UPDATED = "updated";
        static final String COUNT = "count";
        static final String WEBLOG = "weblog";
        static final String NAME = "name";
        static final String URL = "url";
        static final String WHEN = "when";

        @SuppressWarnings({ "unchecked", "null" })
        public WeblogUpdates parseContent(byte[] content) {
            WeblogUpdates weblogUpdates = new WeblogUpdates();
            List<Weblog> items = new ArrayList<Weblog>();
            weblogUpdates.setWeblogs(items);
            try {
                // First create a new XMLInputFactory
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                // Setup a new eventReader
                InputStream in = new ByteArrayInputStream(content);
                XMLEventReader eventReader = inputFactory
                        .createXMLEventReader(in);
                // Read the XML document
                Weblog item = null;

                while (eventReader.hasNext()) {
                    XMLEvent event = eventReader.nextEvent();

                    if (event.isStartElement()) {
                        StartElement startElement = event.asStartElement();

                        if (startElement.getName().getLocalPart()
                                .equals(WEBLOG_UPDATES)) {
                            Iterator<Attribute> attributes = startElement
                                    .getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                if (attribute.getName().toString()
                                        .equals(VERSION)) {
                                    weblogUpdates.setVersion(attribute
                                            .getValue());
                                } else if (attribute.getName().toString()
                                        .equals(UPDATED)) {
                                    Date date = new Date(attribute.getValue());
                                    weblogUpdates.setUpdated(date);
                                } else if (attribute.getName().toString()
                                        .equals(COUNT)) {
                                    weblogUpdates.setCount(Integer
                                            .parseInt(attribute.getValue()));
                                }
                            }

                        }

                        // If we have a item element we create a new item
                        if (startElement.getName().getLocalPart()
                                .equals(WEBLOG)) {
                            item = new Weblog();
                            // We read the attributes from this tag and add the
                            // date
                            // attribute to our object
                            Iterator<Attribute> attributes = startElement
                                    .getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                if (attribute.getName().toString().equals(NAME)) {
                                    item.setName(attribute.getValue());
                                } else if (attribute.getName().toString()
                                        .equals(URL)) {
                                    item.setUrl(attribute.getValue());
                                } else if (attribute.getName().toString()
                                        .equals(WHEN)) {
                                    item.setWhen(Integer.parseInt(attribute
                                            .getValue()));
                                }
                            }
                        }

                    }
                    // If we reach the end of an item element we add it to the
                    // list
                    if (event.isEndElement()) {
                        EndElement endElement = event.asEndElement();
                        if (endElement.getName().getLocalPart() == (WEBLOG)) {
                            items.add(item);
                        }
                    }

                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
            return weblogUpdates;
        }

    }

}
