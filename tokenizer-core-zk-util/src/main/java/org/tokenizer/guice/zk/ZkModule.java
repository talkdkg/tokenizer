package org.tokenizer.guice.zk;

import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.config.V7Ini.ZkParam;

import com.google.inject.AbstractModule;

public class ZkModule extends AbstractModule {

    private final V7Ini ini;

    public ZkModule(V7Ini ini) {
        super();
        this.ini = ini;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(SessionTimeout.class).to(ini.zkParam(ZkParam.sessionTimeout));
        bindConstant().annotatedWith(ConnectString.class).to(ini.zkParam(ZkParam.connectString));
    }

}
