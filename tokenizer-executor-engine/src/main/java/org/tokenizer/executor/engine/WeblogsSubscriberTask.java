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
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.model.WeblogRecord;
import org.tokenizer.crawler.db.model.WeblogRecord.Weblog;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.WeblogsSubscriberTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.RedirectFetchException;
import crawlercommons.fetcher.http.BaseHttpFetcher.RedirectMode;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;

public class WeblogsSubscriberTask extends AbstractTask<WeblogsSubscriberTaskConfiguration> {

    private static String url = "http://rpc.weblogs.com/shortChanges.xml";

    private static final int FIVE_MINUTES = 5 * 60 * 1000;

    private final SimpleHttpFetcher simpleHttpClient;

    public WeblogsSubscriberTask(UUID uuid, String friendlyName, ZooKeeperItf zk,
            final WeblogsSubscriberTaskConfiguration taskConfiguration, CrawlerRepository crawlerRepository,
            WritableExecutorModel model, HostLocker hostLocker) {

        super(uuid, friendlyName, zk, taskConfiguration, crawlerRepository, model, hostLocker);

        UserAgent userAgent = new UserAgent(this.taskConfiguration.getAgentName(),
                this.taskConfiguration.getEmailAddress(), this.taskConfiguration.getWebAddress(),
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
    protected void process() throws InterruptedException, ConnectionException {

        LOG.debug("Trying URL: {}", url);
        FetchedResult fetchedResult = null;
        try {
            fetchedResult = simpleHttpClient.get(url, null);

            LOG.debug("fetchedResult: {}", fetchedResult);

            StaXParser read = new StaXParser();

            WeblogRecord weblogsRecord = read.parseContent(fetchedResult.getContent());

            long start = System.currentTimeMillis();
            crawlerRepository.insert(weblogsRecord);

            LOG.debug("Record inserted in {}ms)", System.currentTimeMillis() - start);

            // for (Weblog item : weblogsRecord.getWeblogs()) {
            // UrlRecord urlRecord = new UrlRecord(item.getUrl());
            // crawlerRepository.insertIfNotExists(urlRecord);
            // LOG.debug("new record inserted: {}", urlRecord);
            // }

        } catch (RedirectFetchException e) {
            LOG.error("", e);
        } catch (BaseFetchException e) {
            LOG.error("", e);
        }

        thread.sleep(FIVE_MINUTES);
    }

    public static void main(final String[] args) throws Exception {

        UserAgent userAgent = new UserAgent("Googlebot", "", "", UserAgent.DEFAULT_BROWSER_VERSION, "2.1");
        SimpleHttpFetcher httpClient = new SimpleHttpFetcher(1, userAgent);
        httpClient.setSocketTimeout(30000);
        httpClient.setConnectionTimeout(30000);
        httpClient.setRedirectMode(RedirectMode.FOLLOW_NONE);
        httpClient.setDefaultMaxContentSize(1024 * 1024 * 1024);

        FetchedResult fetchedResult = httpClient.get(url, null);

        System.out.println(fetchedResult.toString());

        StaXParser read = new StaXParser();
        WeblogRecord weblogBatchRecord = read.parseContent(fetchedResult.getContent());
        System.out.println(weblogBatchRecord);
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
        public WeblogRecord parseContent(byte[] content) {
            WeblogRecord weblogBatchRecord = new WeblogRecord();
            try {
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                InputStream in = new ByteArrayInputStream(content);
                XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

                Weblog weblog = null;
                Date batchUpdateTimestamp = null;

                while (eventReader.hasNext()) {
                    XMLEvent event = eventReader.nextEvent();

                    if (event.isStartElement()) {
                        StartElement startElement = event.asStartElement();

                        if (startElement.getName().getLocalPart().equals(WEBLOG_UPDATES)) {
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                if (attribute.getName().toString().equals(UPDATED)) {
                                    batchUpdateTimestamp = new Date(attribute.getValue());
                                }
                                else if (attribute.getName().toString().equals(COUNT)) {
                                    weblogBatchRecord.setCount(Integer.parseInt(attribute.getValue()));
                                }
                            }

                        }

                        if (startElement.getName().getLocalPart().equals(WEBLOG)) {
                            weblog = new Weblog();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                if (attribute.getName().toString().equals(NAME)) {
                                    weblog.setName(attribute.getValue());
                                }
                                else if (attribute.getName().toString().equals(URL)) {
                                    weblog.setUrl(attribute.getValue());
                                }
                                else if (attribute.getName().toString().equals(WHEN)) {
                                    weblog.setUpdateTimestamp(batchUpdateTimestamp,
                                            Integer.parseInt(attribute.getValue()));
                                }
                            }
                        }

                    }

                    if (event.isEndElement()) {
                        EndElement endElement = event.asEndElement();
                        if (endElement.getName().getLocalPart() == (WEBLOG)) {
                            weblogBatchRecord.add(weblog);
                        }
                    }

                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
            return weblogBatchRecord;
        }

    }

}
