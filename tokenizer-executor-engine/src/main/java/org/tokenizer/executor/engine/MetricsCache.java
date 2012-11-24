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

import java.util.HashMap;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.util.zookeeper.ZkLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.executor.model.api.TaskConcurrentModificationException;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskModelException;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.TaskUpdateException;
import org.tokenizer.executor.model.api.TaskValidityException;
import org.tokenizer.executor.model.api.WritableExecutorModel;

/**
 * Thread safe instance of this class should be used as an attribute of
 * AbstractTask implementations
 * 
 * Only "increment" implemented. For instance, you will need explicity divide
 * total mean time by total URLs processed. "counters" are stored in local
 * cache, and added to existing counters periodically (each 10 seconds)
 * 
 */
public class MetricsCache {
    private static final Logger LOG = LoggerFactory
            .getLogger(MetricsCache.class);
    private static final long COMMIT_INTERVAL = 1 * 1000L;
    public static final String URL_ROBOTS_KEY = "robots.txt restricted";
    public static final String URL_TOTAL_KEY = "URLs processed total";
    public static final String URL_OK_KEY = "URLs fetched successfully HTTP total count";
    public static final String URL_INJECTED = "URLs injected";
    public static final String TOTAL_RESPONSE_TIME_KEY = "URLs fetched successfully HTTP total time (ms)";
    public static final String URL_ERROR_KEY = "URLs with fetch errors";
    public static final String OTHER_ERRORS = "other errors";
    public static final String TOTAL_MEAN_TIME_KEY = "total mean time (including HTTP, Lily, and Solr) (ms)";
    public static final String NOT_INDEXED_YET = "URLs retrieved but not indexed yet";
    public static final String UPDATES_COUNT = "Total updates";
    public static final String UPDATES_TIME = "Total time for updates (ms)";
    public static final String INJECTS_COUNT = "Total injects";
    public static final String SITEMAPS_PROCESSED = "sitemaps processed";
    public static final String SITEMAP_INDEXES_PROCESSED = "sitemap indexes processed";
    private long lastCommitTimestamp;
    private final Map<String, Long> cache = new HashMap<String, Long>();
    private final String taskName;
    private final WritableExecutorModel model;

    public MetricsCache(String taskName, WritableExecutorModel model) {
        this.taskName = taskName;
        this.model = model;
        this.lastCommitTimestamp = System.currentTimeMillis();
    }

    /**
     * Increments metrics with a given key <br/>
     * 
     * @param key
     */
    public synchronized void increment(String key) {
        increment(key, 1L);
    }

    /**
     * Increments metrics with a given key <br/>
     * 
     * @param key
     */
    public synchronized void increment(String key, long value) {
        Long count = cache.get(key);
        if (count == null) {
            count = 0L;
        }
        count = count + value;
        cache.put(key, count);
        commitIfTimedOut();
    }

    private synchronized void commitIfTimedOut() {
        if (System.currentTimeMillis() >= lastCommitTimestamp + COMMIT_INTERVAL) {
            commit();
        }
    }

    /**
     * Adds existing subcounts to ZK-backed counters, commits to ZK, sets
     * subcounts to zero. <br/>
     * 
     * 
     * TODO: inplement "finalize" which will flush a cache
     * 
     * @return
     */
    public synchronized boolean commit() {
        LOG.info("Committing to ZooKeeper...");
        boolean success = false;
        long currentTimestamp = System.currentTimeMillis();
        String lock = null;
        try {
            lock = model.lockTask(taskName);
            TaskInfoBean taskDefinition = model.getMutableTask(taskName);
            if (taskDefinition.getTaskConfiguration().getGeneralState() == TaskGeneralState.DELETE_REQUESTED)
                return true;
            for (String key : cache.keySet()) {
                Long value = taskDefinition.getCounters().get(key);
                if (value == null) {
                    value = 0L;
                }
                value = value + cache.get(key);
                taskDefinition.addCounter(key, value);
            }
            taskDefinition.setMetricsUpdateTimestamp(currentTimestamp);
            model.updateTask(taskDefinition, lock);
            for (String key : cache.keySet()) {
                cache.put(key, 0L);
            }
            lastCommitTimestamp = currentTimestamp;
            success = true;
        } catch (ZkLockException e) {
            LOG.error("", e);
        } catch (TaskNotFoundException e) {
            LOG.warn("fetch {} not found...", taskName);
        } catch (InterruptedException e) {
            LOG.error("", e);
        } catch (KeeperException e) {
            LOG.error("", e);
        } catch (TaskModelException e) {
            LOG.error("", e);
        } catch (TaskConcurrentModificationException e) {
            LOG.error("", e);
        } catch (TaskUpdateException e) {
            LOG.error("", e);
        } catch (TaskValidityException e) {
            LOG.error("", e);
        } finally {
            try {
                if (lock != null) {
                    model.unlockTask(lock, true);
                }
            } catch (ZkLockException e) {
                LOG.error("", e);
            }
        }
        return success;
    }

    /**
     * We need this... otherwise data can become wrong: 10000 URLs added from
     * site maps, although the same 1000 URLs were attempted to add 9 more
     * times. "Reset" will zero all counters on a Task Start/Restart.
     * 
     * The only sample of usage: constructor of AbstractTask
     * 
     * 
     * @return
     */
    public synchronized boolean reset() {
        boolean success = false;
        long currentTimestamp = System.currentTimeMillis();
        String lock = null;
        try {
            lock = model.lockTask(taskName);
            TaskInfoBean taskDefinition = model.getMutableTask(taskName);
            if (taskDefinition.getTaskConfiguration().getGeneralState() == TaskGeneralState.DELETE_REQUESTED)
                return true;
            taskDefinition.getCounters().clear();
            taskDefinition.setSubmitTime(currentTimestamp);
            taskDefinition.setMetricsUpdateTimestamp(currentTimestamp);
            model.updateTask(taskDefinition, lock);
            cache.clear();
            lastCommitTimestamp = currentTimestamp;
            success = true;
        } catch (ZkLockException e) {
            LOG.error("", e);
        } catch (TaskNotFoundException e) {
            LOG.warn("task {} not found...", taskName);
        } catch (InterruptedException e) {
            LOG.error("", e);
        } catch (KeeperException e) {
            LOG.error("", e);
        } catch (TaskModelException e) {
            LOG.error("", e);
        } catch (TaskConcurrentModificationException e) {
            LOG.error("", e);
        } catch (TaskUpdateException e) {
            LOG.error("", e);
        } catch (TaskValidityException e) {
            LOG.error("", e);
        } finally {
            try {
                if (lock != null) {
                    model.unlockTask(lock, true);
                }
            } catch (ZkLockException e) {
                LOG.error("", e);
            }
        }
        return success;
    }
}
