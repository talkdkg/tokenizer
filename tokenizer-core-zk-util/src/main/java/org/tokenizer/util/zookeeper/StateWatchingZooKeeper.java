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
package org.tokenizer.util.zookeeper;

import static org.apache.zookeeper.Watcher.Event.KeeperState.Disconnected;
import static org.apache.zookeeper.Watcher.Event.KeeperState.Expired;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;
import static org.apache.zookeeper.ZooKeeper.States.CONNECTED;

import java.io.IOException;

import javax.annotation.PreDestroy;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * This implementation of {@link ZooKeeperItf} is meant for use as a global
 * ZooKeeper handle within a ZK-dependent application.
 * 
 * <p>
 * It will:
 * 
 * <ul>
 * <li>on startup (= constructor) wait for the ZK connection to come up, if it does not come up within the session
 * timeout an exception will be thrown. This avoids the remainder of the application starting up in the absence of a
 * valid ZK connection.
 * <li>when the session expires or the ZK connection is lost for longer than the session timeout, it will shut down the
 * application.
 * </ul>
 * 
 * <p>
 * So this is a good solution for applications which can not function in absence of ZooKeeper.
 */
public class StateWatchingZooKeeper extends ZooKeeperImpl {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StateWatchingZooKeeper.class);
    private final int requestedSessionTimeout;
    private int sessionTimeout;
    /**
     * Ready becomes true once the ZooKeeper delegate has been set.
     */
    private volatile boolean ready;
    private volatile boolean stopping;
    private volatile boolean connected;
    private boolean firstConnect = true;
    private Thread stateWatcherThread;
    private Runnable endProcessHook;

    public StateWatchingZooKeeper(final String connectString, final int sessionTimeout) throws IOException {
        this(connectString, sessionTimeout, sessionTimeout);
    }

    public StateWatchingZooKeeper(final String connectString, final int sessionTimeout, final int startupTimeOut)
            throws IOException {
        this.requestedSessionTimeout = sessionTimeout;

        this.sessionTimeout = sessionTimeout;

        ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, new MyWatcher());
        setDelegate(zk);
        ready = true;
        // Wait for connection to come up: if we fail to connect to ZK now, we
        // do not want to continue
        // starting up the Lily node.
        long waitUntil = System.currentTimeMillis() + startupTimeOut;
        int count = 0;
        while (zk.getState() != CONNECTED && waitUntil > System.currentTimeMillis()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
            count++;
            if (count == 30) {
                // Output a message every 3s
                LOG.info("Waiting for ZooKeeper connection to be established");
                count = 0;
            }
        }
        if (zk.getState() != CONNECTED) {
            stopping = true;
            try {
                zk.close();
            } catch (Throwable t) {
                // ignore
            }
            throw new IOException("Failed to connect with Zookeeper within timeout " + startupTimeOut
                    + ", connection string: " + connectString);
        }
        LOG.info("ZooKeeper session ID is 0x" + Long.toHexString(zk.getSessionId()));
    }

    @Override
    @PreDestroy
    public void shutdown() {
        super.shutdown();
        stopping = true;
        if (stateWatcherThread != null) {
            stateWatcherThread.interrupt();
        }
        close();
    }

    public void setEndProcessHook(final Runnable endProcessHook) {
        this.endProcessHook = endProcessHook;
    }

    private void endProcess(final String message) {
        if (stopping)
            return;
        if (endProcessHook != null) {
            endProcessHook.run();
        }
        super.shutdown();
        LOG.error(message);
        System.err.println(message);
        System.exit(1);
    }

    private class MyWatcher implements Watcher {

        @Override
        public void process(final WatchedEvent event) {
            if (stopping)
                return;
            zkEventThread = Thread.currentThread();
            try {
                if (event.getState() == Expired) {
                    endProcess("ZooKeeper session expired, shutting down.");
                }
                else if (event.getState() == Disconnected) {
                    LOG.warn("Disconnected from ZooKeeper");
                    connected = false;
                    waitForZk();
                    if (stateWatcherThread != null) {
                        stateWatcherThread.interrupt();
                    }
                    stateWatcherThread = new Thread(new StateWatcher(), "LilyZkStateWatcher");
                    stateWatcherThread.start();
                }
                else if (event.getState() == SyncConnected) {
                    if (firstConnect) {
                        firstConnect = false;
                        // For the initial connection, it is not interesting to
                        // log that we are connected.
                    }
                    else {
                        LOG.warn("Connected to ZooKeeper");
                    }
                    connected = true;
                    waitForZk();
                    if (stateWatcherThread != null) {
                        stateWatcherThread.interrupt();
                        stateWatcherThread = null;
                    }
                    int negotiatedSessionTimeout = getSessionTimeout();
                    // It could be that we again lost the ZK connection by now,
                    // in which case getSessionTimeout() will
                    // return 0, and sessionTimeout should not be set to 0 since
                    // it is used to decide to shut down (see
                    // StateWatcher thread).
                    sessionTimeout = negotiatedSessionTimeout > 0 ? negotiatedSessionTimeout : requestedSessionTimeout;
                    if (negotiatedSessionTimeout == 0) {
                        // We could consider not even distributing this event
                        // further, but not sure about that, so
                        // just logging it for now.
                        LOG.info("The negotiated ZooKeeper session timeout is " + negotiatedSessionTimeout + ", which"
                                + "indicates that the connection has been lost again.");
                    }
                    else if (sessionTimeout != requestedSessionTimeout) {
                        LOG.info("The negotiated ZooKeeper session timeout is different from the requested one."
                                + " Requested: " + requestedSessionTimeout + ", negotiated: " + sessionTimeout);
                    }
                }
            } catch (InterruptedException e) {
                // someone wants us to stop
                return;
            }
            setConnectedState(event);
            for (Watcher watcher : additionalDefaultWatchers) {
                watcher.process(event);
            }
        }

        private void waitForZk() throws InterruptedException {
            while (!ready) {
                LOG.debug("Still waiting for reference to ZooKeeper.");
                Thread.sleep(5);
            }
        }
    }

    private class StateWatcher implements Runnable {

        private long startNotConnected;

        @Override
        public void run() {
            startNotConnected = System.currentTimeMillis();
            while (true) {
                // We do not use ZooKeeper.getState() here, because I noticed
                // that when we get a DisConnected
                // event in the watcher, the state still takes some time to move
                // to CONNECTING.
                if (connected)
                    // We are connected again, so we should not longer bother
                    // watching the state
                    return;
                // Using a margin of twice the session timeout per
                // http://markmail.org/thread/uvefxjnuliuqwwph
                int margin = sessionTimeout * 2;
                if (startNotConnected + margin < System.currentTimeMillis()) {
                    endProcess("ZooKeeper connection lost for longer than " + margin
                            + " ms. Session will already be expired by server so shutting down.");
                    return;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Someone requested us to stop
                    return;
                }
            }
        }
    }
}
