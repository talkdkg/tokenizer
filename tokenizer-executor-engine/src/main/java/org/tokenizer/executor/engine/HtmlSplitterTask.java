package org.tokenizer.executor.engine;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.util.Logs;
import org.lilyproject.util.zookeeper.LeaderElection;
import org.lilyproject.util.zookeeper.LeaderElectionCallback;
import org.lilyproject.util.zookeeper.LeaderElectionSetupException;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

public class HtmlSplitterTask extends AbstractTask {

    
    private static final Logger LOG = LoggerFactory
            .getLogger(HtmlSplitterTask.class);

 

    public HtmlSplitterTask(String taskName, ZooKeeperItf zk,
            TaskConfiguration taskConfiguration,
            CrawlerHBaseRepository crawlerRepository,
            WritableExecutorModel model, HostLocker hostLocker) {
        super(taskName, zk, taskConfiguration, crawlerRepository, model,
                hostLocker);

        LOG.debug("Instance created");

    }


    
     @Override
    public void start() throws InterruptedException,
            LeaderElectionSetupException, KeeperException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }
    

    @Override
    protected void process() throws InterruptedException, IOException {
        // TODO Auto-generated method stub
        
    }

    
    
    
    ///////////////////////////////////////////////////////
    // Standard Template Methods, to be refactored later //
    ///////////////////////////////////////////////////////
    
    
    private class MyLeaderElectionCallback implements LeaderElectionCallback {

        public void activateAsLeader() throws Exception {
            LOG.warn("activateAsLeader...");
            if (thread != null && thread.isAlive()) {
                LOG.warn("Start was requested, but old thread was still there. Stopping it now.");
                thread.interrupt();
                Logs.logThreadJoin(thread);
                thread.join();
            } else {
                thread = new Thread(HtmlSplitterTask.this,
                        HtmlSplitterTask.this.taskName);
                thread.setDaemon(true);
                thread.start();
                LOG.warn("Activated as Leader.");
            }
        }

        public void deactivateAsLeader() throws Exception {
            LOG.warn("deactivateAsLeader...");
            shutdown();
            LOG.warn("Deactivated as Leader.");
        }

    }





    
    
    
    
}
