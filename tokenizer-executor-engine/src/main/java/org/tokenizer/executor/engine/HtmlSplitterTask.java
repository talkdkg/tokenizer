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
    protected void process() throws InterruptedException, IOException {
        // TODO Auto-generated method stub
        
    }

    
    

    
}
