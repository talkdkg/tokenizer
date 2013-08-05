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

import java.util.UUID;

import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.AbstractTaskConfiguration;
import org.tokenizer.util.Logs;
import org.tokenizer.util.zookeeper.LeaderElection;
import org.tokenizer.util.zookeeper.LeaderElectionCallback;
import org.tokenizer.util.zookeeper.LeaderElectionSetupException;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public abstract class AbstractTask<T extends AbstractTaskConfiguration> implements Runnable, LeaderElectionCallback {

    // not static, and not final: abstract
    protected Logger LOG = LoggerFactory.getLogger(getClass().getCanonicalName());
    protected final UUID uuid;
    protected final String friendlyName;
    protected final ZooKeeperItf zk;
    protected final CrawlerRepository crawlerRepository;
    protected final WritableExecutorModel model;
    protected final HostLocker hostLocker;
    protected final MetricsCache metricsCache;
    protected Thread thread;
    protected LeaderElection leaderElection;

    protected T taskConfiguration;

    //@formatter:off
    public AbstractTask(
            final UUID uuid, 
            final String friendlyName,
            final ZooKeeperItf zk, 
            final T taskConfiguration, 
            final CrawlerRepository crawlerRepository,
            final WritableExecutorModel model, 
            final HostLocker hostLocker) {
        //@formatter:on

        this.uuid = uuid;
        this.friendlyName = friendlyName;
        this.zk = zk;
        this.taskConfiguration = taskConfiguration;
        this.crawlerRepository = crawlerRepository;
        this.model = model;
        this.hostLocker = hostLocker;
        this.metricsCache = new MetricsCache(uuid, model);
        LOG.debug("Resetting; it will produce TASK_DEFINITION_UPDATED event");
        // this.metricsCache.reset();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public T getTaskConfiguration() {
        return this.taskConfiguration;
    }

    public void setTaskConfiguration(T taskConfiguration) {
        this.taskConfiguration = taskConfiguration;
    }

    public MetricsCache getMetricsCache() {
        return metricsCache;
    }

    public boolean isAlive() {
        return thread.isAlive();
    }

    protected synchronized void shutdown() throws InterruptedException {
        LOG.warn("shutdown...");
        if (thread == null || !thread.isAlive()) {
            return;
        }
        thread.interrupt();
        Logs.logThreadJoin(thread);
        thread.join();
        LOG.warn("Shutted down.");
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                process();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            catch (ConnectionException e) {
                LOG.error("Repository unavailable; sleeping 1 second...", e);
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void start() throws InterruptedException, LeaderElectionSetupException, KeeperException {
        if (leaderElection == null) {
            leaderElection =
                new LeaderElection(zk, "Master " + this.getClass().getCanonicalName(),
                    "/org/tokenizer/executor/engine/" + this.getClass().getCanonicalName() + "/" + this.uuid, this);
        }
    }

    public void stop() {
        LOG.warn("stop...");
        try {
            if (this.leaderElection != null) {
                this.leaderElection.stop();
            }
            this.leaderElection = null;
            shutdown();
            LOG.warn("Stopped.");
        }
        catch (InterruptedException e) {
            if (thread != null) {
                thread.interrupt();
            }
            Thread.currentThread().interrupt();
            LOG.error("Interrupted while trying to stop Leader Election...", e);
        }
    }

    protected abstract void process() throws InterruptedException, ConnectionException;

    @Override
    public void activateAsLeader() throws Exception {
        LOG.warn("activateAsLeader...");
        if (thread != null && thread.isAlive()) {
            LOG.warn("Start was requested, but old thread was still there. Stopping it now.");
            thread.interrupt();
            Logs.logThreadJoin(thread);
            thread.join();
        }
        else {
            thread = new Thread(this, uuid.toString() + ": " + friendlyName);
            thread.setDaemon(true);
            thread.start();
            LOG.warn("Activated as Leader.");
        }
    }

    @Override
    public void deactivateAsLeader() throws Exception {
        LOG.warn("deactivateAsLeader...");
        shutdown();
        LOG.warn("Deactivated as Leader.");
    }

}
