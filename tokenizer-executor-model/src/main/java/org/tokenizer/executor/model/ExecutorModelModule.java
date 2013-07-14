package org.tokenizer.executor.model;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.impl.ExecutorModelImpl;
import org.tokenizer.util.zookeeper.ZooKeeperImpl;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.google.inject.AbstractModule;

public class ExecutorModelModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WritableExecutorModel.class).to(ExecutorModelImpl.class);
        bind(ZooKeeperItf.class).to(ZooKeeperImpl.class);
    }

}
