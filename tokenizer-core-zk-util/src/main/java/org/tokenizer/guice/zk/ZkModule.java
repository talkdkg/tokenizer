package org.tokenizer.guice.zk;

import org.apache.shiro.config.Ini;
import org.apache.shiro.config.Ini.Section;

import com.google.inject.AbstractModule;

public class ZkModule extends AbstractModule {

    private final Ini ini;

    public ZkModule(Ini ini) {
        super();
        this.ini = ini;
    }

    @Override
    protected void configure() {
        Section zk = ini.getSection("zookeeper");
        bindConstant().annotatedWith(ConnectString.class).to(zk.get("connectString"));
        bindConstant().annotatedWith(SessionTimeout.class).to(zk.get("sessionTimeout"));
    }

}
