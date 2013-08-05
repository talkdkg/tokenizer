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

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.tokenizer.guice.zk.ConnectString;
import org.tokenizer.guice.zk.SessionTimeout;

/**
 * Default implementation of {@link ZooKeeperItf}.
 * 
 * <p>
 * To wait until the ZK connection is established, use {@link ZkUtil#connect(String, int)}.
 * 
 * <p>
 * For a global ZK handle to be used by a ZK-dependent application, see rather
 * {@link org.tokenizer.util.zookeeper.StateWatchingZooKeeper}.
 */
public class ZooKeeperImpl implements ZooKeeperItf {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ZooKeeperImpl.class);
    protected ZooKeeper delegate;
    protected Set<Watcher> additionalDefaultWatchers = Collections
            .newSetFromMap(new IdentityHashMap<Watcher, Boolean>());
    protected boolean connected = false;
    protected volatile boolean stop = false;
    protected final Object connectedMonitor = new Object();
    protected Thread zkEventThread;

    protected ZooKeeperImpl() {
    }

    protected void setDelegate(final ZooKeeper delegate) {
        this.delegate = delegate;
    }

    @Inject
    public ZooKeeperImpl(@ConnectString
    final String connectString, @SessionTimeout
    final int sessionTimeout) throws IOException {
        LOG.info("constructor called...");
        this.delegate = new ZooKeeper(connectString, sessionTimeout, new MyWatcher());
    }

    @Override
    public void addDefaultWatcher(final Watcher watcher) {
        additionalDefaultWatchers.add(watcher);
    }

    @Override
    public void removeDefaultWatcher(final Watcher watcher) {
        additionalDefaultWatchers.remove(watcher);
    }

    public void shutdown() {
        this.stop = true;
        synchronized (connectedMonitor) {
            connectedMonitor.notifyAll();
        }
    }

    @Override
    public void waitForConnection() throws InterruptedException {
        if (isCurrentThreadEventThread())
            throw new RuntimeException("waitForConnection should not be called from within the ZooKeeper event thread.");
        synchronized (connectedMonitor) {
            while (!connected && !stop) {
                connectedMonitor.wait();
            }
        }
        if (stop)
            throw new InterruptedException("This ZooKeeper handle is shutting down.");
    }

    @Override
    public boolean isCurrentThreadEventThread() {
        // Disclaimer: this way of detected wrong use of the event thread was
        // inspired by the ZKClient library.
        return zkEventThread != null && zkEventThread == Thread.currentThread();
    }

    protected void setConnectedState(final WatchedEvent event) {
        if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
            synchronized (connectedMonitor) {
                if (!connected) {
                    connected = true;
                    connectedMonitor.notifyAll();
                }
            }
        }
        else if (event.getState() == Watcher.Event.KeeperState.Disconnected
                || event.getState() == Watcher.Event.KeeperState.Expired) {
            synchronized (connectedMonitor) {
                if (connected) {
                    connected = false;
                    connectedMonitor.notifyAll();
                }
            }
        }
    }

    @Override
    public <T> T retryOperation(final ZooKeeperOperation<T> operation) throws InterruptedException, KeeperException {
        if (isCurrentThreadEventThread())
            throw new RuntimeException("retryOperation should not be called from within the ZooKeeper event thread.");
        int tryCount = 0;
        while (true) {
            tryCount++;
            try {
                return operation.execute();
            } catch (KeeperException.ConnectionLossException e) {
                // ok
            }
            if (tryCount > 3) {
                LOG.warn("ZooKeeper operation attempt " + tryCount + " failed due to connection loss.");
            }
            waitForConnection();
        }
    }

    @Override
    public long getSessionId() {
        return delegate.getSessionId();
    }

    @Override
    public byte[] getSessionPasswd() {
        return delegate.getSessionPasswd();
    }

    @Override
    public int getSessionTimeout() {
        return delegate.getSessionTimeout();
    }

    @Override
    public void addAuthInfo(final String scheme, final byte[] auth) {
        delegate.addAuthInfo(scheme, auth);
    }

    @Override
    public void register(final Watcher watcher) {
        delegate.register(watcher);
    }

    @Override
    public void close() {
        try {
            delegate.close();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String create(final String path, final byte[] data, final List<ACL> acl, final CreateMode createMode)
            throws KeeperException, InterruptedException {
        return delegate.create(path, data, acl, createMode);
    }

    @Override
    public void create(final String path, final byte[] data, final List<ACL> acl, final CreateMode createMode,
            final AsyncCallback.StringCallback cb, final Object ctx) {
        delegate.create(path, data, acl, createMode, cb, ctx);
    }

    @Override
    public void delete(final String path, final int version) throws InterruptedException, KeeperException {
        delegate.delete(path, version);
    }

    @Override
    public void delete(final String path, final int version, final AsyncCallback.VoidCallback cb, final Object ctx) {
        delegate.delete(path, version, cb, ctx);
    }

    @Override
    public Stat exists(final String path, final Watcher watcher) throws KeeperException, InterruptedException {
        return delegate.exists(path, watcher);
    }

    @Override
    public Stat exists(final String path, final boolean watch) throws KeeperException, InterruptedException {
        return delegate.exists(path, watch);
    }

    @Override
    public void exists(final String path, final Watcher watcher, final AsyncCallback.StatCallback cb, final Object ctx) {
        delegate.exists(path, watcher, cb, ctx);
    }

    @Override
    public void exists(final String path, final boolean watch, final AsyncCallback.StatCallback cb, final Object ctx) {
        delegate.exists(path, watch, cb, ctx);
    }

    @Override
    public byte[] getData(final String path, final Watcher watcher, final Stat stat) throws KeeperException,
            InterruptedException {
        return delegate.getData(path, watcher, stat);
    }

    @Override
    public byte[] getData(final String path, final boolean watch, final Stat stat) throws KeeperException,
            InterruptedException {
        return delegate.getData(path, watch, stat);
    }

    @Override
    public void getData(final String path, final Watcher watcher, final AsyncCallback.DataCallback cb, final Object ctx) {
        delegate.getData(path, watcher, cb, ctx);
    }

    @Override
    public void getData(final String path, final boolean watch, final AsyncCallback.DataCallback cb, final Object ctx) {
        delegate.getData(path, watch, cb, ctx);
    }

    @Override
    public Stat setData(final String path, final byte[] data, final int version) throws KeeperException,
            InterruptedException {
        return delegate.setData(path, data, version);
    }

    @Override
    public void setData(final String path, final byte[] data, final int version, final AsyncCallback.StatCallback cb,
            final Object ctx) {
        delegate.setData(path, data, version, cb, ctx);
    }

    @Override
    public List<ACL> getACL(final String path, final Stat stat) throws KeeperException, InterruptedException {
        return delegate.getACL(path, stat);
    }

    @Override
    public void getACL(final String path, final Stat stat, final AsyncCallback.ACLCallback cb, final Object ctx) {
        delegate.getACL(path, stat, cb, ctx);
    }

    @Override
    public Stat setACL(final String path, final List<ACL> acl, final int version) throws KeeperException,
            InterruptedException {
        return delegate.setACL(path, acl, version);
    }

    @Override
    public void setACL(final String path, final List<ACL> acl, final int version, final AsyncCallback.StatCallback cb,
            final Object ctx) {
        delegate.setACL(path, acl, version, cb, ctx);
    }

    @Override
    public List<String> getChildren(final String path, final Watcher watcher) throws KeeperException,
            InterruptedException {
        return delegate.getChildren(path, watcher);
    }

    @Override
    public List<String> getChildren(final String path, final boolean watch) throws KeeperException,
            InterruptedException {
        return delegate.getChildren(path, watch);
    }

    @Override
    public void getChildren(final String path, final Watcher watcher, final AsyncCallback.ChildrenCallback cb,
            final Object ctx) {
        delegate.getChildren(path, watcher, cb, ctx);
    }

    @Override
    public void getChildren(final String path, final boolean watch, final AsyncCallback.ChildrenCallback cb,
            final Object ctx) {
        delegate.getChildren(path, watch, cb, ctx);
    }

    @Override
    public List<String> getChildren(final String path, final Watcher watcher, final Stat stat) throws KeeperException,
            InterruptedException {
        return delegate.getChildren(path, watcher, stat);
    }

    @Override
    public List<String> getChildren(final String path, final boolean watch, final Stat stat) throws KeeperException,
            InterruptedException {
        return delegate.getChildren(path, watch, stat);
    }

    @Override
    public void getChildren(final String path, final Watcher watcher, final AsyncCallback.Children2Callback cb,
            final Object ctx) {
        delegate.getChildren(path, watcher, cb, ctx);
    }

    @Override
    public void getChildren(final String path, final boolean watch, final AsyncCallback.Children2Callback cb,
            final Object ctx) {
        delegate.getChildren(path, watch, cb, ctx);
    }

    @Override
    public void sync(final String path, final AsyncCallback.VoidCallback cb, final Object ctx) {
        delegate.sync(path, cb, ctx);
    }

    @Override
    public ZooKeeper.States getState() {
        return delegate.getState();
    }

    public class MyWatcher implements Watcher {

        private boolean printConnectMsg = false; // do not print connect msg on
                                                 // initial connect

        @Override
        public void process(final WatchedEvent event) {
            zkEventThread = Thread.currentThread();
            if (event.getState() == Watcher.Event.KeeperState.Disconnected) {
                System.err.println("ZooKeeper disconnected at " + new Date());
                printConnectMsg = true;
            }
            else if (event.getState() == Event.KeeperState.Expired) {
                System.err.println("ZooKeeper session expired at " + new Date());
                printConnectMsg = true;
            }
            else if (event.getState() == Event.KeeperState.SyncConnected) {
                if (printConnectMsg) {
                    System.out.println("ZooKeeper connected at " + new Date());
                }
            }
            setConnectedState(event);
            for (Watcher watcher : additionalDefaultWatchers) {
                watcher.process(event);
            }
        }
    }
}
