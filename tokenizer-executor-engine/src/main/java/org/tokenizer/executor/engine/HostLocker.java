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
package org.tokenizer.executor.engine;

import java.util.Arrays;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.util.zookeeper.ZkUtil;
import org.tokenizer.util.zookeeper.ZooKeeperItf;
import org.tokenizer.util.zookeeper.ZooKeeperOperation;

public class HostLocker {

    private final ZooKeeperItf zk;
    private int maxWaitTime = 20000;
    private static final Logger LOG = LoggerFactory.getLogger(HostLocker.class);
    private static final String LOCK_PATH = "/org/tokenizer/executor/engine/hostlock";

    public HostLocker(final ZooKeeperItf zk) throws InterruptedException, KeeperException {
        this.zk = zk;
        ZkUtil.createPath(zk, LOCK_PATH);
    }

    public HostLocker(final ZooKeeperItf zk, final int waitBetweenTries, final int maxWaitTime)
            throws InterruptedException, KeeperException {
        this.zk = zk;
        this.maxWaitTime = maxWaitTime;
        ZkUtil.createPath(zk, LOCK_PATH);
    }

    /**
     * Obtain a lock for the given record. The lock is thread-based, i.e. it is
     * re-entrant, obtaining a lock for the same record twice from the same {ZK
     * session, thread} will silently succeed.
     * 
     * <p>
     * If this method returns without failure, you obtained the lock
     * 
     * @throws HostLockException
     *             if the lock could not be obtained within the given timeout.
     */
    public boolean lock(final String host) throws HostLockException {
        if (zk.isCurrentThreadEventThread())
            throw new RuntimeException("HostLocker should not be used from within the ZooKeeper event thread.");
        try {
            long startTime = System.currentTimeMillis();
            final String lockPath = getPath(host);
            final byte[] data = longToBytes(Thread.currentThread().getId());
            while (true) {
                if (System.currentTimeMillis() - startTime > maxWaitTime)
                    // we have been attempting long enough to get the lock,
                    // without
                    // success
                    throw new HostLockTimeoutException("Failed to obtain a lock for host " + host + " within "
                            + maxWaitTime + " ms.");
                try {
                    zk.retryOperation(new ZooKeeperOperation<Object>() {

                        @Override
                        public Object execute() throws KeeperException, InterruptedException {
                            zk.create(lockPath, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                            return null;
                        }
                    });
                    // We successfully created the node, hence we have the lock.
                    return true;
                } catch (KeeperException.NodeExistsException e) {
                    // ignore, see next
                }
                // In case creating the node failed, it does not mean we do not
                // have the
                // lock: in case
                // of connection loss, we might not know if we actually
                // succeeded
                // creating the node, therefore
                // read the owner and thread id to check.
                boolean hasLock = zk.retryOperation(new ZooKeeperOperation<Boolean>() {

                    @Override
                    public Boolean execute() throws KeeperException, InterruptedException {
                        try {
                            Stat stat = new Stat();
                            byte[] currentData = zk.getData(lockPath, false, stat);
                            return (stat.getEphemeralOwner() == zk.getSessionId() && Arrays.equals(currentData, data));
                        } catch (KeeperException.NoNodeException e) {
                            return false;
                        }
                    }
                });
                if (hasLock)
                    return true;
                return false;
            }
        } catch (Throwable throwable) {
            if (throwable instanceof HostLockException)
                throw (HostLockException) throwable;
            throw new HostLockException("Error locking a host " + host, throwable);
        }
    }

    public void unlock(final String host) throws HostLockException, InterruptedException, KeeperException {
        if (zk.isCurrentThreadEventThread())
            throw new RuntimeException("HostLocker should not be used from within the ZooKeeper event thread.");
        final String lockPath = getPath(host);
        // The below loop is because, even if our thread is interrupted, we
        // still
        // want to remove the lock.
        // The interruption might be because just one IndexUpdater is being shut
        // down, rather than the
        // complete application, and hence session expiration will then not
        // remove
        // the lock.
        boolean tokenOk;
        boolean interrupted = false;
        while (true) {
            try {
                tokenOk = zk.retryOperation(new ZooKeeperOperation<Boolean>() {

                    @Override
                    public Boolean execute() throws KeeperException, InterruptedException {
                        Stat stat = new Stat();
                        byte[] data = zk.getData(lockPath, false, stat);
                        if (stat.getEphemeralOwner() == zk.getSessionId()
                                && Arrays.equals(data, longToBytes(Thread.currentThread().getId()))) {
                            zk.delete(lockPath, -1);
                            return true;
                        }
                        else
                            return false;
                    }
                });
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        if (!tokenOk)
            throw new HostLockException("You cannot remove the lock for a host " + host
                    + " because the token is incorrect.");
    }

    public void unlockLogFailure(final String host) {
        try {
            unlock(host);
        } catch (Throwable t) {
            LOG.error("Error releasing lock on a host " + host, t);
        }
    }

    /**
     * returns true if current thread owns lock
     * 
     * @param host
     * @return
     * @throws HostLockException
     * @throws InterruptedException
     * @throws KeeperException
     */
    public boolean hasLock(final String host) throws HostLockException, InterruptedException, KeeperException {
        if (zk.isCurrentThreadEventThread())
            throw new RuntimeException("HostLocker should not be used from within the ZooKeeper event thread.");
        final String lockPath = getPath(host);
        return zk.retryOperation(new ZooKeeperOperation<Boolean>() {

            @Override
            public Boolean execute() throws KeeperException, InterruptedException {
                try {
                    Stat stat = new Stat();
                    byte[] data = zk.getData(lockPath, false, stat);
                    return stat.getEphemeralOwner() == zk.getSessionId()
                            && Arrays.equals(data, longToBytes(Thread.currentThread().getId()));
                } catch (KeeperException.NoNodeException e) {
                    return false;
                }
            }
        });
    }

    public boolean exists(final String host) throws HostLockException, InterruptedException, KeeperException {
        final String lockPath = getPath(host);
        return zk.retryOperation(new ZooKeeperOperation<Boolean>() {

            @Override
            public Boolean execute() throws KeeperException, InterruptedException {
                try {
                    if (null != zk.exists(lockPath, false))
                        return true;
                    return false;
                } catch (KeeperException.NoNodeException e) {
                    return false;
                }
            }
        });
    }

    private String getPath(final String host) {
        return LOCK_PATH + "/" + host;
    }

    public final byte[] longToBytes(final long v) {
        byte[] writeBuffer = new byte[8];
        writeBuffer[0] = (byte) (v >>> 56);
        writeBuffer[1] = (byte) (v >>> 48);
        writeBuffer[2] = (byte) (v >>> 40);
        writeBuffer[3] = (byte) (v >>> 32);
        writeBuffer[4] = (byte) (v >>> 24);
        writeBuffer[5] = (byte) (v >>> 16);
        writeBuffer[6] = (byte) (v >>> 8);
        writeBuffer[7] = (byte) (v >>> 0);
        return writeBuffer;
    }
}
