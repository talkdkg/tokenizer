package org.tokenizer.executor.worker;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.impl.ExecutorModelImpl;
import org.tokenizer.util.zookeeper.ZooKeeperImpl;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.google.inject.AbstractModule;

public class ExecutorWorkerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WritableExecutorModel.class).to(ExecutorModelImpl.class);
        bind(ZooKeeperItf.class).to(ZooKeeperImpl.class);
        bind(CrawlerRepository.class).to(CrawlerRepositoryCassandraImpl.class);
    }

}
