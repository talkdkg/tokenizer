/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright © 2013 Tokenizer Inc. All rights reserved. 
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.zookeeper.KeeperException;
import org.tokenizer.core.http.FetcherUtils;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.SimpleMultithreadedFetcherTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import crawlercommons.fetcher.http.BaseHttpFetcher;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;

public class SimpleMultithreadedFetcher extends AbstractTask<SimpleMultithreadedFetcherTaskConfiguration> {

    /** used for generic retrieval */
    private final SimpleHttpFetcher simpleHttpClient;

    /** for robots.txt and sitemaps */
    private final BaseHttpFetcher baseFetcher;
    private final Set<String> hosts = Collections
            .synchronizedSet(new HashSet<String>());
    private boolean stop = false;
    private static final int MAX_THREADS = 64;
    List<Thread> threads = new ArrayList<Thread>();
    private final static long HOSTS_REFRESH_DELAY = 60 * 60 * 1000L;
    private SimpleMultithreadedFetcherTaskConfiguration taskConfiguration;

    public SimpleMultithreadedFetcher(final UUID uuid,
            final String friendlyName, final ZooKeeperItf zk,
        final SimpleMultithreadedFetcherTaskConfiguration taskConfiguration,
            final CrawlerRepository repository,
            final WritableExecutorModel fetcherModel,
            final HostLocker hostLocker) {

        super(uuid, friendlyName, zk, taskConfiguration, repository, fetcherModel, hostLocker);

        this.simpleHttpClient = new SimpleHttpFetcher(FetcherUtils.USER_AGENT);
        this.baseFetcher = RobotUtils.createFetcher(FetcherUtils.USER_AGENT,
                1024);
        this.baseFetcher.setDefaultMaxContentSize(4 * 1024 * 1024);

    }

    @Override
    public void start() {
        stop();
        LOG.warn(
                "starting threads... hardcoded to MIN(<total number of sites>, {})...",
                MAX_THREADS);
        stop = false;
        // TODO: the problem is that IF hosts is empty we will need to restart
        // task
        // explicitly over time
        for (int i = 1; i < MAX_THREADS; i++) {
            if (i > hosts.size()) {
                break;
            }
            Thread thread = new Thread(new MyFetcherThread(),
                    "MyFetcherThread[" + i + "]");
            threads.add(thread);
            LOG.warn("starting thread {}", thread.getName());
            thread.start();
        }
    }

    @Override
    public void stop() {
        if (!threads.isEmpty()) {
            LOG.warn("Start was requested, but old threads are still there. Stopping now...");
            stop = true;
            for (Thread thread : threads) {
                if (!thread.isAlive()) {
                    threads.remove(thread);
                    thread = null;
                    continue;
                }
                thread.interrupt();
                LOG.warn("Waiting for a thread {} to die...", thread.getName());
                threads.remove(thread);
                thread = null;
            }
            threads.clear();
        }
    }

    public boolean init() {
        try {
            HostsMonitorThread monitor = new HostsMonitorThread();
            monitor.start();
            while (hosts.isEmpty()) {
                Thread.currentThread().sleep(1000);
            }
        } catch (Throwable e) {
            LOG.error("", e);
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        throw new RuntimeException("Not implemented!");
    }

    private class MyFetcherThread implements Runnable {

        @Override
        public void run() {
            while (!stop) {
                for (String host : hosts) {
                    try {
                        if (hostLocker.exists(host)) {
                            continue;
                        }
                    } catch (HostLockException e) {
                        LOG.error("", e);
                        continue;
                    } catch (InterruptedException e) {
                        LOG.error("InterruptedException catched... exiting...");
                        return;
                    } catch (KeeperException e) {
                        LOG.error("", e);
                        continue;
                    }
                    try {
                        if (!hostLocker.lock(host)) {
                            continue;
                        }
                    } catch (HostLockException e) {
                        LOG.error("", e);
                        continue;
                    }
                    BaseRobotsParser parser = new SimpleRobotRulesParser();
                    BaseRobotRules rules = null;
                    try {
                        rules = RobotUtils.getRobotRules(baseFetcher, parser,
                                new URL("http://" + host + "/robots.txt"));
                    } catch (MalformedURLException e) {
                        LOG.error("", e);
                        continue;
                        // throw new CrawlerError(e);
                    }
                    // TODO: fetch URLs
                    hostLocker.unlockLogFailure(host);
                }
            }
        }
    }

    public class HostsMonitorThread extends Thread {

        @Override
        public void run() {
            try {
                while (!stop) {
                    refreshHosts();
                    Thread.currentThread().sleep(HOSTS_REFRESH_DELAY);
                }
            } catch (InterruptedException ex) {
                LOG.error("interrupted...");
                return;
            }
        }

        /**
         * Retrieves (virtually unlimited) list of hosts
         */
        private synchronized void refreshHosts() throws InterruptedException {
            // TODO:
        }
    }

    @Override
    protected void process() throws InterruptedException, ConnectionException {
        // TODO Auto-generated method stub
    }

}
