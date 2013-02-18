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
import java.util.UUID;

import org.apache.zookeeper.KeeperException;
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
import org.tokenizer.util.zookeeper.ZkLockException;

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
    public static final String URL_TOTAL_KEY = "URLs processed";
    public static final String XML_TOTAL_KEY = "XML created";
    public static final String MESSAGE_TOTAL_KEY = "Messages created";
    public static final String URL_OK_KEY = "URLs fetched";
    public static final String URL_INJECTED = "URLs injected";
    public static final String TOTAL_HTTP_RESPONSE_TIME_MS = "Total HTTP response time (ms)";
    public static final String AVERAGE_HTTP_RESPONSE_TIME_MS = "Average HTTP response time (ms)";
    public static final String URL_ERROR_KEY = "URLs with fetch errors";
    public static final String OTHER_ERRORS = "other errors";
    public static final String TOTAL_MEAN_TIME_KEY = "Total mean time (ms)";
    public static final String NOT_INDEXED_YET = "URLs retrieved but not indexed yet";
    public static final String UPDATES_COUNT = "Total updates";
    public static final String UPDATES_TIME = "Total time for updates (ms)";
    public static final String INJECTS_COUNT = "Total injects";
    public static final String SITEMAPS_PROCESSED = "Sitemaps processed";
    public static final String SITEMAP_INDEXES_PROCESSED = "Sitemap indexes processed";
    public static final String REDIRECT_COUNT = "Redirect count";
    private long lastCommitTimestamp;
    private final Map<String, Long> cache = new HashMap<String, Long>();
    private final UUID uuid;
    private final WritableExecutorModel model;

    public MetricsCache(final UUID uuid, final WritableExecutorModel model) {
        this.uuid = uuid;
        this.model = model;
        this.lastCommitTimestamp = System.currentTimeMillis();
    }

    /**
     * Increments metrics with a given key <br/>
     * 
     * @param key
     * @throws InterruptedException
     */
    public synchronized void increment(final String key)
            throws InterruptedException {
        increment(key, 1L);
    }

    /**
     * Increments metrics with a given key <br/>
     * 
     * @param key
     * @throws InterruptedException
     */
    public synchronized void increment(final String key, final long value)
            throws InterruptedException {
        Long count = cache.get(key);
        if (count == null) {
            count = 0L;
        }
        count = count + value;
        cache.put(key, count);
        commitIfTimedOut();
    }

    private synchronized void commitIfTimedOut() throws InterruptedException {
        if (System.currentTimeMillis() >= lastCommitTimestamp + COMMIT_INTERVAL) {
            commit();
        }
    }

    /**
     * Adds existing subcounts to ZK-backed counters, commits to ZK, sets
     * subcounts to zero. <br/>
     * 
     * 
     * TODO: inplement "finalize" or "pre-destroy" which will flush the cache
     * 
     * @return
     * @throws InterruptedException
     */
    public synchronized boolean commit() throws InterruptedException {
        LOG.info("Committing to ZooKeeper...");
        boolean success = false;
        long currentTimestamp = System.currentTimeMillis();
        String lock = null;
        try {
            lock = model.lockTask(uuid);
        } catch (ZkLockException e1) {
            LOG.debug(e1.getMessage());
        } catch (TaskNotFoundException e1) {
            LOG.debug(e1.getMessage());
        } catch (KeeperException e1) {
            LOG.debug(e1.getMessage());
        } catch (TaskModelException e1) {
            LOG.debug(e1.getMessage());
        }
        if (lock == null)
            return false;
        try {
            TaskInfoBean taskDefinition = model.getMutableTask(uuid);
            for (String key : cache.keySet()) {
                Long value = taskDefinition.getCounters().get(key);
                if (value == null) {
                    value = 0L;
                }
                value = value + cache.get(key);
                taskDefinition.addCounter(key, value);
                // it will update twice, and second update will be correct one:
                if (key.equals(URL_OK_KEY)
                        || key.equals(TOTAL_HTTP_RESPONSE_TIME_MS)) {
                    Long count = taskDefinition.getCounters().get(URL_OK_KEY);
                    Long time = taskDefinition.getCounters().get(
                            TOTAL_HTTP_RESPONSE_TIME_MS);
                    if (count != null && count > 0 && time != null) {
                        Long average = (long) (time / count);
                        taskDefinition.addCounter(
                                AVERAGE_HTTP_RESPONSE_TIME_MS, average);
                    }
                }
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
            LOG.warn("task not found: {}", uuid);
        } catch (KeeperException e) {
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
            lock = model.lockTask(uuid);
            TaskInfoBean taskDefinition = model.getMutableTask(uuid);
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
            LOG.warn("task not found: {}", uuid);
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

    public static void main(final String[] args) {
        Long count = new Long(684L);
        Long time = new Long(455679L);
        Long average = (time / count);
        System.out.println("average: " + average + "   " + time / count);
    }
}
