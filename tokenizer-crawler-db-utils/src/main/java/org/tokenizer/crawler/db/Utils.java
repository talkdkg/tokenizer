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
package org.tokenizer.crawler.db;

import static org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.toUrlRecord;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.RowCallback;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;

public class Utils {

    protected static Logger LOG = LoggerFactory.getLogger(Utils.class);

    static CrawlerRepositoryCassandraImpl oldRepository = new CrawlerRepositoryCassandraImpl(
            9160);
    static CrawlerRepositoryCassandraImpl newRepository = new CrawlerRepositoryCassandraImpl(
            19160);

    public static void main(String[] args) throws ConnectionException,
            InterruptedException {

        oldRepository.setup();
        newRepository.setup();

        Thread t = new UrlMigrationThread();
        t.setDaemon(false);
        t.setName("UrlMigrationThread");
        t.start();

        t = new WebpageMigrationThread();
        t.setDaemon(false);
        t.setName("WebpageMigrationThread");
        t.start();

        t = new XmlMigrationThread();
        t.setDaemon(false);
        t.setName("XmlMigrationThread");
        t.start();

        t = new MessageMigrationThread();
        t.setDaemon(false);
        t.setName("MessageMigrationThread");
        t.start();

    }

    public static class UrlMigrationThread extends Thread {

        @Override
        public void run() {
            final AtomicLong counter = new AtomicLong();
            try {
                //@formatter:off
                    oldRepository.keyspace
                            .prepareQuery(oldRepository.CF_URL_RECORDS)
                            .getAllRows()
                            .setRowLimit(100)                           
                            .setRepeatLastToken(true)
                             .executeWithCallback(new RowCallback<String, String>() {
                                @Override
                                public void success(final Rows<String, String> rows) {
                                    for (Row<String, String> row : rows) {
                                        ColumnList<String> columns = row.getColumns();
                                        UrlRecord urlRecord = toUrlRecord(row.getKey(), columns);         
                                        try {
                                            newRepository.update(urlRecord);
                                        } catch (ConnectionException e) {
                                            LOG.error("UrlMigrationThread - " + e.getMessage());
                                            LOG.error("UrlMigrationThread - " + e.getCause().getMessage());
                                        }
                                        counter.incrementAndGet();
                                        if (counter.get() % 1000 == 0) {
                                            LOG.warn("{} records migrated...", counter.get());
                                        }

                                    }
                                }

                                @Override
                                public boolean failure(final ConnectionException e) {
                                    LOG.error(e.getMessage(), e);
                                    return false;
                                }
                            });
                    // @formatter:on
            } catch (Exception e) {
                LOG.error("", e);
            }
            LOG.error("Total {} records reindexed... ", counter.get());
        }
    }

    public static class WebpageMigrationThread extends Thread {

        @Override
        public void run() {
            final AtomicLong counter = new AtomicLong();
            try {
                //@formatter:off
                    oldRepository.keyspace
                            .prepareQuery(oldRepository.CF_WEBPAGE_RECORDS)
                            .getAllRows()
                            .setRowLimit(100)                           
                            .setRepeatLastToken(true)
                             .executeWithCallback(new RowCallback<byte[], String>() {
                                @Override
                                public void success(final Rows<byte[], String> rows) {
                                    for (Row<byte[], String> row : rows) {
                                        ColumnList<String> columns = row.getColumns();
                                        WebpageRecord record = CrawlerRepositoryCassandraImpl.toWebpageRecord(row.getKey(), columns);         
                                        try {
                                            newRepository.insert(record);
                                        } catch (ConnectionException e) {
                                            LOG.error("WebpageMigrationThread - " + e.getMessage());
                                            LOG.error("WebpageMigrationThread - " + e.getCause().getMessage());
                                        }
                                        counter.incrementAndGet();
                                        if (counter.get() % 1000 == 0) {
                                            LOG.warn("{} records migrated...", counter.get());
                                        }

                                    }
                                }

                                @Override
                                public boolean failure(final ConnectionException e) {
                                    LOG.error(e.getMessage(), e);
                                    return false;
                                }
                            });
                    // @formatter:on
            } catch (Exception e) {
                LOG.error("", e);
            }
            LOG.error("Total {} records migrated... ", counter.get());
        }
    }

    public static class XmlMigrationThread extends Thread {

        @Override
        public void run() {
            final AtomicLong counter = new AtomicLong();
            try {
                //@formatter:off
                    oldRepository.keyspace
                            .prepareQuery(oldRepository.CF_XML_RECORDS)
                            .getAllRows()
                            .setRowLimit(100)                           
                            .setRepeatLastToken(true)
                             .executeWithCallback(new RowCallback<byte[], String>() {
                                @Override
                                public void success(final Rows<byte[], String> rows) {
                                    for (Row<byte[], String> row : rows) {
                                        ColumnList<String> columns = row.getColumns();
                                        XmlRecord record = CrawlerRepositoryCassandraImpl.toXmlRecord(row.getKey(), columns);         
                                        try {
                                            newRepository.insert(record);
                                        } catch (ConnectionException e) {
                                            LOG.error("XmlMigrationThread - " + e.getMessage());
                                            LOG.error("XmlMigrationThread - " + e.getCause().getMessage());
                                        }
                                        counter.incrementAndGet();
                                        if (counter.get() % 1000 == 0) {
                                            LOG.warn("{} records migrated...", counter.get());
                                        }

                                    }
                                }

                                @Override
                                public boolean failure(final ConnectionException e) {
                                    LOG.error(e.getMessage(), e);
                                    return false;
                                }
                            });
                    // @formatter:on
            } catch (Exception e) {
                LOG.error("", e);
            }
            LOG.error("Total {} records migrated... ", counter.get());
        }
    }

    public static class MessageMigrationThread extends Thread {

        @Override
        public void run() {
            final AtomicLong counter = new AtomicLong();
            try {
                //@formatter:off
                    oldRepository.keyspace
                            .prepareQuery(oldRepository.CF_MESSAGE_RECORDS)
                            .getAllRows()
                            .setRowLimit(100)                           
                            .setRepeatLastToken(true)
                             .executeWithCallback(new RowCallback<byte[], String>() {
                                @Override
                                public void success(final Rows<byte[], String> rows) {
                                    for (Row<byte[], String> row : rows) {
                                        ColumnList<String> columns = row.getColumns();
                                        MessageRecord record = CrawlerRepositoryCassandraImpl.toMessageRecord(row.getKey(), columns);    
                                        //LOG.debug("MessageRecord: {}", record);
                                        try {
                                            newRepository.insert(record);
                                        } catch (ConnectionException e) {
                                            LOG.error("MessageMigrationThread - " + e.getMessage());
                                            LOG.error("MessageMigrationThread - Cause -" + e.getCause().getMessage(), e);
                                        }
                                        counter.incrementAndGet();
                                        if (counter.get() % 1000 == 0) {
                                            LOG.warn("{} records migrated...", counter.get());
                                        }

                                    }
                                }

                                @Override
                                public boolean failure(final ConnectionException e) {
                                    LOG.error(e.getMessage(), e);
                                    return false;
                                }
                            });
                    // @formatter:on
            } catch (Exception e) {
                LOG.error("", e);
            }
            LOG.error("Total {} records migrated... ", counter.get());
        }
    }

}
