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
package org.tokenizer.core.cli;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * Base for CLI tools that need a ZooKeeper connect string.
 */
public abstract class BaseZkCliTool extends BaseCliTool {

    private static final String DEFAULT_ZK_CONNECT = "localhost";
    protected String zkConnectionString;
    protected int zkSessionTimeout = 40000;
    protected Option zkOption;

    @Override
    public List<Option> getOptions() {
        List<Option> options = super.getOptions();
        zkOption = OptionBuilder
                .withArgName("connection-string")
                .hasArg()
                .withDescription(
                        "ZooKeeper connection string: hostname1:port,hostname2:port,...")
                .withLongOpt("zookeeper").create("z");
        options.add(zkOption);
        return options;
    }

    @Override
    protected int processOptions(final CommandLine cmd) throws Exception {
        int result = super.processOptions(cmd);
        if (result != 0)
            return result;
        if (!cmd.hasOption(zkOption.getOpt())) {
            // to stderr: makes that sample config dumps of e.g. tester tool do
            // not start with this line, and
            // can thus be redirected to a file without further editing.
            System.err
                    .println("ZooKeeper connection string not specified, using default: "
                            + DEFAULT_ZK_CONNECT);
            System.err.println();
            zkConnectionString = DEFAULT_ZK_CONNECT;
        } else {
            zkConnectionString = cmd.getOptionValue(zkOption.getOpt());
        }
        return 0;
    }
}
