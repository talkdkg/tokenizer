/*
 * Copyright 2007-2012 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tokenizer.executor.engine;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.util.Logs;
import org.lilyproject.util.zookeeper.LeaderElection;
import org.lilyproject.util.zookeeper.LeaderElectionCallback;
import org.lilyproject.util.zookeeper.LeaderElectionSetupException;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

public abstract class AbstractTask implements Runnable {
    private static final Logger LOG = LoggerFactory
            .getLogger(AbstractTask.class);
    protected final String taskName;
    protected final ZooKeeperItf zk;
    protected TaskConfiguration taskConfiguration;
    protected final CrawlerHBaseRepository crawlerRepository;
    protected final WritableExecutorModel model;
    protected final HostLocker hostLocker;
    protected final MetricsCache metricsCache;
    protected Thread thread;
    protected LeaderElection leaderElection;
    protected LeaderElectionCallback leaderElectionCallback;

    public AbstractTask(String taskName, ZooKeeperItf zk,
            TaskConfiguration taskConfiguration,
            CrawlerHBaseRepository crawlerRepository,
            WritableExecutorModel model, HostLocker hostLocker) {
        this.taskName = taskName;
        this.zk = zk;
        this.taskConfiguration = taskConfiguration;
        this.crawlerRepository = crawlerRepository;
        this.model = model;
        this.hostLocker = hostLocker;
        this.metricsCache = new MetricsCache(taskName, model);
        LOG.debug("Resetting; it will produce TASK_DEFINITION_UPDATED event");
        // this.metricsCache.reset();
    }

    public MetricsCache getMetricsCache() {
        return metricsCache;
    }

    public boolean isAlive() {
        return thread.isAlive();
    }

    public TaskConfiguration getTaskConfiguration() {
        return taskConfiguration;
    }

    /** To allow runtime configuration changes */
    public void setTaskConfiguration(TaskConfiguration taskConfiguration) {
        this.taskConfiguration = taskConfiguration;
    }

    // TODO: make it final
    protected synchronized void shutdown() throws InterruptedException {
        LOG.warn("shutdown...");
        if (!thread.isAlive()) {
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
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void start() throws InterruptedException,
            LeaderElectionSetupException, KeeperException {
        if (leaderElection == null) {
            leaderElection = new LeaderElection(zk, "Master "
                    + this.getClass().getCanonicalName(),
                    "/org/tokenizer/executor/engine/"
                            + this.getClass().getCanonicalName() + "/"
                            + this.taskName, this.leaderElectionCallback);
        }
    }

    protected void stop() {
        LOG.warn("stop...");
        try {
            this.leaderElection.stop();
            this.leaderElection = null;
            shutdown();
            LOG.warn("Stopped.");
        } catch (InterruptedException e) {
            if (thread != null)
                thread.interrupt();
            Thread.currentThread().interrupt();
            LOG.error("Interrupted while trying to stop Leader Election...", e);
        }
    }

    protected abstract void process() throws InterruptedException, IOException;
}
