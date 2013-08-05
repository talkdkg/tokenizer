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

/**
 * Class to run the leader election by itself for test purposes.
 */
public class LeaderElectionMain implements Runnable {

    public static void main(final String[] args) throws Exception {
        Thread t = new Thread(new LeaderElectionMain());
        t.setDaemon(false);
        t.start();
        while (!Thread.interrupted()) {
            Thread.sleep(Long.MAX_VALUE);
        }
    }

    @Override
    public void run() {
        try {
            ZooKeeperItf zk = ZkUtil.connect("localhost:2181,localhost:21812", 5000);
            new LeaderElection(zk, "electiontest", "/lily/electiontest/leaders", new Callback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Callback implements LeaderElectionCallback {

        @Override
        public void activateAsLeader() {
            System.out.println("I am the leader.");
        }

        @Override
        public void deactivateAsLeader() {
            System.out.println("I am no longer the leader.");
        }
    }
}
