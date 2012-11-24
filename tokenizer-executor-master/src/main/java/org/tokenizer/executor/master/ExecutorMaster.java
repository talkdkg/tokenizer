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
package org.tokenizer.executor.master;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.util.Logs;
import org.lilyproject.util.zookeeper.LeaderElection;
import org.lilyproject.util.zookeeper.LeaderElectionCallback;
import org.lilyproject.util.zookeeper.LeaderElectionSetupException;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelEventType;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.WritableExecutorModel;

/**
 * Responsible for delete existing task; only one single Master node should be
 * responsible; multiple Workers can't do "delete" concurrently
 * 
 */
public class ExecutorMaster {
    private static final Logger LOG = LoggerFactory
            .getLogger(ExecutorMaster.class);
    private LeaderElection leaderElection;
    private final ZooKeeperItf zk;
    private final ExecutorInfo executorInfo;
    private final WritableExecutorModel model;
    private final ExecutorModelListener listener = new MyListener();
    private final EventWorker eventWorker = new EventWorker();

    public ExecutorMaster(ZooKeeperItf zk, WritableExecutorModel model,
            String zkConnectString, int zkSessionTimeout,
            ExecutorInfo executorInfo, String hostName) {
        this.zk = zk;
        this.model = model;
        this.executorInfo = executorInfo;
    }

    @PostConstruct
    public void start() throws LeaderElectionSetupException, IOException,
            InterruptedException, KeeperException {
        LOG.warn("start called...");
        leaderElection = new LeaderElection(zk, "Executor Master",
                "/org/tokenizer/executor/masters",
                new MyLeaderElectionCallback());
    }

    @PreDestroy
    public void stop() {
        try {
            leaderElection.stop();
        } catch (InterruptedException e) {
            LOG.info("Interrupted while shutting down leader election.");
        }
        // Closer.close(jobClient);
    }

    private class MyLeaderElectionCallback implements LeaderElectionCallback {
        @Override
        public void activateAsLeader() throws Exception {
            LOG.info("Starting up as Master.");
            // Start these processes, but it is not until we have registered our
            // model
            // listener
            // that these will receive work.
            eventWorker.start();
            // jobStatusWatcher.start();
            Collection<TaskInfoBean> taskDefinitions = model.getTasks(listener);
            // push out fake events
            for (TaskInfoBean taskDefinition : taskDefinitions) {
                eventWorker.putEvent(new ExecutorModelEvent(
                        ExecutorModelEventType.TASK_UPDATED, taskDefinition
                                .getTaskConfiguration().getName()));
            }
            LOG.info("Startup as Master successful.");
            executorInfo.setMaster(true);
        }

        @Override
        public void deactivateAsLeader() throws Exception {
            LOG.info("Shutting down as Master.");
            // we do not interrupt the event worker thread: if there was
            // something running there that is blocked until the ZK connection
            // comes back up we want it to finish (e.g. a lock taken that should
            // be released again)
            eventWorker.shutdown(false);
            LOG.info("Shutdown as Master successful.");
            executorInfo.setMaster(false);
        }
    }

    private class EventWorker implements Runnable {
        private final BlockingQueue<ExecutorModelEvent> eventQueue = new LinkedBlockingQueue<ExecutorModelEvent>();
        private boolean stop;
        private Thread thread;

        public synchronized void shutdown(boolean interrupt)
                throws InterruptedException {
            stop = true;
            eventQueue.clear();
            if (!thread.isAlive())
                return;
            if (interrupt) {
                thread.interrupt();
            }
            Logs.logThreadJoin(thread);
            thread.join();
            thread = null;
        }

        public synchronized void start() throws InterruptedException {
            if (thread != null) {
                LOG.warn("ExecutorMaster.EventWorker start was requested, but old thread was still there. Stopping it now.");
                thread.interrupt();
                Logs.logThreadJoin(thread);
                thread.join();
            }
            eventQueue.clear();
            stop = false;
            thread = new Thread(this, "ExecutorMaster.EventWorker");
            thread.start();
        }

        public void putEvent(ExecutorModelEvent event)
                throws InterruptedException {
            if (stop)
                throw new RuntimeException(
                        "ExecutorMaster.EventWorker is stopped, no events should be added.");
            eventQueue.put(event);
        }

        @Override
        public void run() {
            long startedAt = System.currentTimeMillis();
            while (!stop && !Thread.interrupted()) {
                try {
                    ExecutorModelEvent event = null;
                    while (!stop && event == null) {
                        event = eventQueue.poll(1000, TimeUnit.MILLISECONDS);
                    }
                    if (stop || event == null || Thread.interrupted())
                        return;
                    // Warn if the queue is getting large, but do not do this
                    // just after
                    // we started, because
                    // on initial startup a fake update event is added for every
                    // defined
                    // index, which would lead
                    // to this message always being printed on startup when more
                    // than 10
                    // indexes are defined.
                    int queueSize = eventQueue.size();
                    if (queueSize >= 10
                            && (System.currentTimeMillis() - startedAt > 5000)) {
                        LOG.warn("EventWorker queue getting large, size = "
                                + queueSize);
                    }
                    if (event.getType() == ExecutorModelEventType.TASK_ADDED
                            || event.getType() == ExecutorModelEventType.TASK_UPDATED) {
                        TaskInfoBean taskDefinition = null;
                        try {
                            taskDefinition = model.getTask(event
                                    .getTaskDefinitionName());
                        } catch (TaskNotFoundException e) {
                            // ignore
                        }
                        if (taskDefinition != null) {
                            if (taskDefinition.getTaskConfiguration()
                                    .getGeneralState() == TaskGeneralState.DELETE_REQUESTED) {
                                model.deleteTask(taskDefinition
                                        .getTaskConfiguration().getName());
                                continue;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    return;
                } catch (Throwable t) {
                    LOG.error(
                            "Error processing executor model event in ExecutorMaster.",
                            t);
                }
            }
        }
    }

    private class MyListener implements ExecutorModelListener {
        @Override
        public void process(ExecutorModelEvent event) {
            try {
                eventWorker.putEvent(event);
            } catch (InterruptedException e) {
                LOG.info("ExecutorMaster.ExecutorModelListener interrupted.");
            }
        }
    }
}
