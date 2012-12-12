/*
 * Copyright 2012 Tokenizer Inc.
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
package org.tokenizer.util.zookeeper;

import static org.apache.zookeeper.ZooKeeper.States.CONNECTED;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZooKeeperMXBean {

    private final ZooKeeperItf zk;
    private final String connectString;
    private final int sessionTimeout;

    public ZooKeeperMXBean(final String connectString,
            final int sessionTimeout, final ZooKeeperItf zk) {
        this.zk = zk;
        this.connectString = connectString;
        this.sessionTimeout = sessionTimeout;
    }

    public long getSessionId() {
        return zk.getSessionId();
    }

    public String getSessionIdHex() {
        return "0x" + Long.toHexString(zk.getSessionId());
    }

    /**
     * Invalidates our ZooKeeper's session. Meant for testing purposes.
     * 
     * <p>
     * Note that you can also close connections and sessions through the JMX
     * beans provided by the ZooKeeper server(s), which I find often more
     * practical.
     */
    public void invalidateSession() throws IOException, InterruptedException {
        // The below is the standard way to invalidate a session from the
        // client.
        // See also
        // http://github.com/phunt/zkexamples/blob/master/src/test_session_expiration/TestSessionExpiration.java
        // where it is mentioned that this could also lead to a session moved
        // exception.
        Watcher watcher = new Watcher() {

            @Override
            public void process(final WatchedEvent event) {
            }
        };
        ZooKeeper zk2 = new ZooKeeper(connectString, sessionTimeout, watcher,
                zk.getSessionId(), zk.getSessionPasswd());
        long waitUntil = System.currentTimeMillis() + sessionTimeout;
        while (zk2.getState() != CONNECTED
                && waitUntil > System.currentTimeMillis()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
        if (zk2.getState() != CONNECTED)
            throw new IOException(
                    "Failed to make a connection with ZooKeeper within the timeout "
                            + sessionTimeout + ", connect string: "
                            + connectString);
        else {
            zk2.close();
        }
    }
}
