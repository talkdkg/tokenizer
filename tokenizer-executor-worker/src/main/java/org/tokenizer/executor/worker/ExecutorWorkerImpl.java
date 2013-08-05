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
package org.tokenizer.executor.worker;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.zookeeper.KeeperException;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.executor.engine.AbstractTask;
import org.tokenizer.executor.engine.ClassicRobotTask;
import org.tokenizer.executor.engine.HostLocker;
import org.tokenizer.executor.engine.HtmlSplitterTask;
import org.tokenizer.executor.engine.RssFetcherTask;
import org.tokenizer.executor.engine.SimpleMultithreadedFetcher;
import org.tokenizer.executor.engine.SitemapsFetcherTask;
import org.tokenizer.executor.engine.SitemapsLinkedPageFetcherTask;
import org.tokenizer.executor.engine.SitemapsPageFetcherTask;
import org.tokenizer.executor.engine.WeblogsCrawlerTask;
import org.tokenizer.executor.engine.WeblogsParserTask;
import org.tokenizer.executor.engine.WeblogsSubscriberTask;
import org.tokenizer.executor.engine.message.MessageParserTask;
import org.tokenizer.executor.engine.twitter.TweetCollectorTask;
import org.tokenizer.executor.engine.twitter.TweetCollectorTaskConfiguration;
import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelEventType;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.AbstractTaskConfiguration;
import org.tokenizer.executor.model.configuration.ClassicRobotTaskConfiguration;
import org.tokenizer.executor.model.configuration.HtmlSplitterTaskConfiguration;
import org.tokenizer.executor.model.configuration.MessageParserTaskConfiguration;
import org.tokenizer.executor.model.configuration.RssFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.SimpleMultithreadedFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.SitemapsFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.SitemapsLinkedPageFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.SitemapsPageFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.WeblogsCrawlerTaskConfiguration;
import org.tokenizer.executor.model.configuration.WeblogsParserTaskConfiguration;
import org.tokenizer.executor.model.configuration.WeblogsSubscriberTaskConfiguration;
import org.tokenizer.util.zookeeper.LeaderElectionSetupException;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Worker is responsible for starting/stopping tasks.
 * 
 * <p>
 * Worker does not shut down the tasks when the ZooKeeper connection is lost.
 */
@Singleton
public class ExecutorWorkerImpl implements ExecutorWorker {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ExecutorWorkerImpl.class);
    private final WritableExecutorModel executorModel;
    private final CrawlerRepository repository;
    private final ZooKeeperItf zk;
    private final ExecutorModelListener listener = new MyListener();
    private final BlockingQueue<ExecutorModelEvent> eventQueue = new LinkedBlockingQueue<ExecutorModelEvent>();
    private Thread eventWorkerThread;
    private final Map<UUID, AbstractTask> tasks = new HashMap<UUID, AbstractTask>();
    /** will be shared between tasks; multithreaded access */
    private final HostLocker hostLocker;

    @Inject
    public ExecutorWorkerImpl(final WritableExecutorModel executorModel, final ZooKeeperItf zk,
        final CrawlerRepository repository) throws IOException, TaskNotFoundException, InterruptedException,
        KeeperException {
        this.executorModel = executorModel;
        this.zk = zk;
        this.repository = repository;
        hostLocker = new HostLocker(zk);
        init();
    }

    // @PostConstruct
    public void init() {
        eventWorkerThread = new Thread(new EventWorker(), "ExecutorWorker.EventWorker");
        eventWorkerThread.start();
        Collection<TaskInfoBean> taskDefinitions = executorModel.getTasks(listener);
        for (TaskInfoBean taskDefinition : taskDefinitions) {
            try {
                eventQueue.put(new ExecutorModelEvent(ExecutorModelEventType.TASK_ADDED, taskDefinition.getUuid()));
            }
            catch (InterruptedException e) {
                eventWorkerThread.interrupt();
                Thread.currentThread().interrupt();
            }
        }
    }

    // @PreDestroy
    public void stop() {
        eventWorkerThread.interrupt();
        try {
            eventWorkerThread.join();
        }
        catch (InterruptedException e) {
        }
        for (AbstractTask task : tasks.values()) {
            // although each task should be "daemon"... to be safe:
            task.stop();
            // LOG.warn("committing metrics for {}...", task.getTaskName());
            // task.getMetricsCache().commit();
            // LOG.warn("committed successfully {}.", task.getTaskName());
        }
    }

    private class MyListener implements ExecutorModelListener {

        @Override
        public void process(final ExecutorModelEvent event) {
            LOG.debug("Event: {}", event.getType());
            try {
                eventQueue.put(event);
            }
            catch (InterruptedException e) {
                eventWorkerThread.interrupt();
                Thread.currentThread().interrupt();
            }
        }
    }

    private class EventWorker implements Runnable {

        @Override
        public void run() {
            LOG.info("Starting EventWorker thread...");
            while (!Thread.interrupted()) {
                try {
                    int queueSize = eventQueue.size();
                    LOG.debug("queue size: {}", queueSize);
                    if (queueSize >= 10) {
                        LOG.warn("EventWorker queue getting large: {}" + queueSize);
                    }
                    ExecutorModelEvent event = eventQueue.take();
                    LOG.debug("Event removed from queue: {}", event);
                    if (event.getType() == ExecutorModelEventType.TASK_ADDED) {
                        TaskInfoBean taskInfo = executorModel.getTask(event.getUuid());
                        AbstractTask task = createTask(taskInfo);
                        if (taskInfo.getTaskConfiguration().getGeneralState() == TaskGeneralState.START_REQUESTED) {
                            task.start();
                        }
                        tasks.put(event.getUuid(), task);
                        continue;
                    }
                    else if (event.getType() == ExecutorModelEventType.TASK_UPDATED) {
                        TaskInfoBean taskInfo = executorModel.getTask(event.getUuid());
                        TaskGeneralState taskGeneralState = taskInfo.getTaskConfiguration().getGeneralState();
                        AbstractTask task = tasks.get(event.getUuid());
                        boolean configurationChanged =
                            !task.getTaskConfiguration().equals(taskInfo.getTaskConfiguration());
                        LOG.debug("configurationChanged: {}", configurationChanged);
                        if (!configurationChanged) {
                            if (taskGeneralState.equals(TaskGeneralState.START_REQUESTED)) {
                                LOG.info("start requested...");
                                task.start();
                            }
                            else if (taskGeneralState.equals(TaskGeneralState.STOP_REQUESTED)) {
                                LOG.info("stop requested...");
                                task.stop();
                            }
                            continue;
                        }
                        if (configurationChanged) {
                            task.setTaskConfiguration(taskInfo.getTaskConfiguration());
                            continue;
                        }
                    }
                    else if (event.getType() == ExecutorModelEventType.TASK_REMOVED) {
                        LOG.debug("Task definition removed...");
                        tasks.get(event.getUuid()).stop();
                        tasks.remove(event.getUuid());
                    }
                }
                catch (InterruptedException e) {
                    LOG.warn("exiting...");
                    return;
                }
                catch (LeaderElectionSetupException e) {
                    LOG.error("LeaderElectionSetupException... continue", e);
                    continue;
                }
                catch (KeeperException e) {
                    LOG.error("KeeperException... continue", e);
                    continue;
                }
                catch (TaskNotFoundException e) {
                    LOG.error("TaskNotFoundException... continue", e);
                    continue;
                }
            }
        }
    }

    private AbstractTask createTask(final TaskInfoBean taskInfo) {
        AbstractTask task = null;
        AbstractTaskConfiguration taskConfiguration = taskInfo.getTaskConfiguration();
        LOG.debug(taskConfiguration.toString());
        if (taskConfiguration instanceof SitemapsFetcherTaskConfiguration) {
            task =
                new SitemapsFetcherTask(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (SitemapsFetcherTaskConfiguration) taskConfiguration, repository, executorModel, hostLocker);
        }
        else if (taskConfiguration instanceof HtmlSplitterTaskConfiguration) {
            task =
                new HtmlSplitterTask(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (HtmlSplitterTaskConfiguration) taskConfiguration, repository, executorModel, hostLocker);
        }
        else if (taskConfiguration instanceof MessageParserTaskConfiguration) {
            task =
                new MessageParserTask(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (MessageParserTaskConfiguration) taskConfiguration, repository, executorModel, hostLocker);
        }
        else if (taskConfiguration instanceof TweetCollectorTaskConfiguration) {
            task =
                new TweetCollectorTask(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (TweetCollectorTaskConfiguration) taskConfiguration, repository, executorModel, hostLocker);
        }
        else if (taskConfiguration instanceof ClassicRobotTaskConfiguration) {
            task =
                new ClassicRobotTask(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (ClassicRobotTaskConfiguration) taskConfiguration, repository, executorModel, hostLocker);
        }
        else if (taskConfiguration instanceof RssFetcherTaskConfiguration) {
            task =
                new RssFetcherTask(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (RssFetcherTaskConfiguration) taskConfiguration, repository, executorModel, hostLocker);
        }
        else if (taskConfiguration instanceof SimpleMultithreadedFetcherTaskConfiguration) {
            task =
                new SimpleMultithreadedFetcher(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (SimpleMultithreadedFetcherTaskConfiguration) taskConfiguration, repository, executorModel,
                    hostLocker);
        }
        else if (taskConfiguration instanceof WeblogsSubscriberTaskConfiguration) {
            task =
                new WeblogsSubscriberTask(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (WeblogsSubscriberTaskConfiguration) taskConfiguration, repository, executorModel, hostLocker);
        }
        else if (taskConfiguration instanceof WeblogsCrawlerTaskConfiguration) {
            task =
                new WeblogsCrawlerTask(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (WeblogsCrawlerTaskConfiguration) taskConfiguration, repository, executorModel, hostLocker);
        }
        else if (taskConfiguration instanceof SitemapsFetcherTaskConfiguration) {
            task =
                new SitemapsFetcherTask(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (SitemapsFetcherTaskConfiguration) taskConfiguration, repository, executorModel, hostLocker);
        }
        else if (taskConfiguration instanceof SitemapsPageFetcherTaskConfiguration) {
            task =
                new SitemapsPageFetcherTask(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (SitemapsPageFetcherTaskConfiguration) taskConfiguration, repository, executorModel, hostLocker);
        }
        else if (taskConfiguration instanceof SitemapsLinkedPageFetcherTaskConfiguration) {
            task =
                new SitemapsLinkedPageFetcherTask(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (SitemapsLinkedPageFetcherTaskConfiguration) taskConfiguration, repository, executorModel,
                    hostLocker);
        }
        else if (taskConfiguration instanceof WeblogsParserTaskConfiguration) {
            task =
                new WeblogsParserTask(taskInfo.getUuid(), taskInfo.getTaskConfiguration().getName(), zk,
                    (WeblogsParserTaskConfiguration) taskConfiguration, repository, executorModel, hostLocker);
        }
        return task;
    }
}
