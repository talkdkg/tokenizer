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

import org.apache.hadoop.conf.Configuration;
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
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.impl.TaskInfoBean;

/**
 * Responsible for start/stop/watch MapReduce, and Delete existing task; only
 * one single Master node should be responsible; multiple Workers can't do
 * "delete" concurrently
 * 
 */
public class ExecutorMaster {

    private static final Logger LOG = LoggerFactory
            .getLogger(ExecutorMaster.class);

    private LeaderElection leaderElection;
    private final ZooKeeperItf zk;
    private ExecutorInfo executorInfo;
    private WritableExecutorModel model;
    private ExecutorModelListener listener = new MyListener();
    private EventWorker eventWorker = new EventWorker();

    public ExecutorMaster(ZooKeeperItf zk, WritableExecutorModel model,
            Configuration mapReduceConf, Configuration hbaseConf,
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
        public void activateAsLeader() throws Exception {

            LOG.info("Starting up as Master.");

            // Start these processes, but it is not until we have registered our
            // model
            // listener
            // that these will receive work.
            eventWorker.start();
            // jobStatusWatcher.start();

            Collection<TaskInfoBean> taskDefinitions = model
                    .getTaskDefinitions(listener);

            // push out fake events
            for (TaskInfoBean taskDefinition : taskDefinitions) {
                eventWorker.putEvent(new ExecutorModelEvent(
                        ExecutorModelEventType.TASK_DEFINITION_UPDATED,
                        taskDefinition.getName()));
            }

            LOG.info("Startup as Master successful.");
            executorInfo.setMaster(true);
        }

        public void deactivateAsLeader() throws Exception {

            LOG.info("Shutting down as Master.");

            // indexerModel.unregisterListener(listener);

            // Argument false for shutdown: we do not interrupt the event worker
            // thread: if there
            // was something running there that is blocked until the ZK
            // connection
            // comes back up
            // we want it to finish (e.g. a lock taken that should be released
            // again)
            eventWorker.shutdown(false);
            // jobStatusWatcher.shutdown(false);

            LOG.info("Shutdown as Master successful.");
            executorInfo.setMaster(false);
        }
    }

    private class EventWorker implements Runnable {

        private BlockingQueue<ExecutorModelEvent> eventQueue = new LinkedBlockingQueue<ExecutorModelEvent>();

        private boolean stop;

        private Thread thread;

        public synchronized void shutdown(boolean interrupt)
                throws InterruptedException {
            stop = true;
            eventQueue.clear();

            if (!thread.isAlive()) {
                return;
            }

            if (interrupt)
                thread.interrupt();
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
            if (stop) {
                throw new RuntimeException(
                        "ExecutorMaster.EventWorker is stopped, no events should be added.");
            }
            eventQueue.put(event);
        }

        public void run() {
            long startedAt = System.currentTimeMillis();

            while (!stop && !Thread.interrupted()) {
                try {
                    ExecutorModelEvent event = null;
                    while (!stop && event == null) {
                        event = eventQueue.poll(1000, TimeUnit.MILLISECONDS);
                    }

                    if (stop || event == null || Thread.interrupted()) {
                        return;
                    }

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

                    if (event.getType() == ExecutorModelEventType.TASK_DEFINITION_ADDED
                            || event.getType() == ExecutorModelEventType.TASK_DEFINITION_UPDATED) {
                        TaskInfoBean taskDefinition = null;
                        try {
                            taskDefinition = model.getTaskDefinition(event
                                    .getTaskDefinitionName());
                        } catch (TaskNotFoundException e) {
                            // ignore
                        }

                        if (taskDefinition != null) {
                            if (taskDefinition.getGeneralState() == TaskGeneralState.DELETE_REQUESTED
                                    || taskDefinition.getGeneralState() == TaskGeneralState.DELETING) {

                                prepareDeleteTaskDefinition(taskDefinition
                                        .getName());

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
        public void process(ExecutorModelEvent event) {
            try {
                // Let the events be processed by another thread. Especially
                // important
                // since
                // we take ZkLock's in the event handlers (see ZkLock javadoc).
                eventWorker.putEvent(event);
            } catch (InterruptedException e) {
                LOG.info("ExecutorMaster.ExecutorModelListener interrupted.");
            }
        }
    }

    private void prepareDeleteTaskDefinition(String name) {
        boolean canBeDeleted = false;
        try {
            TaskInfoBean taskDefinition = model.getMutableTaskDefinition(name);
            if (taskDefinition.getGeneralState() == TaskGeneralState.DELETE_REQUESTED
                    || taskDefinition.getGeneralState() == TaskGeneralState.DELETING) {

                // TODO: some logic for MapReduce tasks in the future...

                canBeDeleted = true;
            }
        } catch (Throwable t) {
            LOG.error("Error preparing deletion of task definition " + name, t);
        }

        if (canBeDeleted) {
            deleteTaskDefinition(name);
        }
    }

    private void deleteTaskDefinition(String name) {
        boolean success = false;
        try {
            model.deleteTaskDefinition(name);
            success = true;
        } catch (Throwable t) {
            LOG.error("Failed to delete task definition.", t);
        }

        if (!success) {
            try {
                TaskInfoBean taskDefinition = model
                        .getMutableTaskDefinition(name);
                taskDefinition.setGeneralState(TaskGeneralState.DELETE_FAILED);
                model.updateTaskDefinitionInternal(taskDefinition);
            } catch (Throwable t) {
                LOG.error("Failed to set task definition state to "
                        + TaskGeneralState.DELETE_FAILED, t);
            }
        }
    }

}
