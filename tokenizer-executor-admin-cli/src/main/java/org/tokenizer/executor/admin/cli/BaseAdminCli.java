/*
 * Copyright 2007-2012 Tokenizer Inc.
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
package org.tokenizer.executor.admin.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.io.FileUtils;
import org.tokenizer.core.cli.BaseZkCliTool;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.executor.model.impl.ExecutorModelImpl;
import org.tokenizer.util.io.Closer;
import org.tokenizer.util.zookeeper.StateWatchingZooKeeper;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

public abstract class BaseAdminCli extends BaseZkCliTool {

    protected Option forceOption;
    protected Option nameOption;
    protected Option configurationOption;
    protected Option generalStateOption;
    protected Option buildStateOption;
    protected Option outputFileOption;
    protected UUID uuid;
    protected TaskConfiguration taskConfiguration;
    protected String outputFileName;
    protected TaskGeneralState generalState;
    protected WritableExecutorModel model;
    private ZooKeeperItf zk;

    @SuppressWarnings("static-access")
    public BaseAdminCli() {
        forceOption = OptionBuilder
                .withDescription("Skips optional validations.")
                .withLongOpt("force").create("f");
        nameOption = OptionBuilder.withArgName("name").hasArg()
                .withDescription("Task name.").withLongOpt("name").create("n");
        configurationOption = OptionBuilder.withArgName("taskconfig.xml")
                .hasArg().withDescription("Task configuration.")
                .withLongOpt("task-config").create("c");
        generalStateOption = OptionBuilder
                .withArgName("state")
                .hasArg()
                .withDescription(
                        "General state, one of: "
                                + TaskGeneralState.START_REQUESTED + ", "
                                + TaskGeneralState.STOP_REQUESTED + ", "
                                + TaskGeneralState.DELETE_REQUESTED)
                .withLongOpt("state").create("i");
        outputFileOption = OptionBuilder.withArgName("filename").hasArg()
                .withDescription("Output file name").withLongOpt("output-file")
                .create("o");
    }

    @Override
    public List<Option> getOptions() {
        List<Option> options = super.getOptions();
        options.add(forceOption);
        return options;
    }

    @Override
    protected String getVersion() {
        return "1.0";
    }

    @Override
    protected int processOptions(final CommandLine cmd) throws Exception {
        int result = super.processOptions(cmd);
        if (result != 0)
            return result;
        if (cmd.hasOption(nameOption.getOpt())) {
            uuid = UUID.fromString(cmd.getOptionValue(nameOption.getOpt()));
        }
        if (cmd.hasOption(configurationOption.getOpt())) {
            File configurationFile = new File(
                    cmd.getOptionValue(configurationOption.getOpt()));
            if (!configurationFile.exists()) {
                System.out
                        .println("Specified task configuration file not found:");
                System.out.println(configurationFile.getAbsolutePath());
                return 1;
            }
            byte[] bytes = FileUtils.readFileToByteArray(configurationFile);
            if (!cmd.hasOption(forceOption.getOpt())) {
                // try {
                // taskConfiguration = TaskConfigurationBuilder.build(new
                // ByteArrayInputStream(
                // bytes));
                // } catch (Exception e) {
                // System.out.println(); // separator line because we might have
                // some
                // // errors on screen
                // System.out.println("Failed to parse & build the task configuration.");
                // System.out.println();
                // if (e instanceof TaskConfigurationException) {
                // printExceptionMessages(e);
                // } else {
                // e.printStackTrace();
                // }
                // return 1;
                // }
            }
        }
        if (cmd.hasOption(generalStateOption.getOpt())) {
            String stateName = cmd.getOptionValue(generalStateOption.getOpt());
            try {
                generalState = TaskGeneralState
                        .valueOf(stateName.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid general state: " + stateName);
                return 1;
            }
        }
        if (cmd.hasOption(outputFileOption.getOpt())) {
            outputFileName = cmd.getOptionValue(outputFileOption.getOpt());
            File file = new File(outputFileName);
            if (!cmd.hasOption(forceOption.getOpt()) && file.exists()) {
                System.out.println("The specified output file already exists:");
                System.out.println(file.getAbsolutePath());
                System.out.println();
                System.out.println("Use --" + forceOption.getLongOpt()
                        + " to overwrite it.");
            }
        }
        return 0;
    }

    @Override
    public int run(final CommandLine cmd) throws Exception {
        int result = super.run(cmd);
        if (result != 0)
            return result;
        zk = new StateWatchingZooKeeper(zkConnectionString, zkSessionTimeout);
        model = new ExecutorModelImpl(zk);
        return 0;
    }

    @SuppressWarnings("rawtypes")
    private String getStates(final Enum[] values) {
        StringBuilder builder = new StringBuilder();
        for (Enum value : values) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(value);
        }
        return builder.toString();
    }

    protected OutputStream getOutput() throws FileNotFoundException {
        if (outputFileName == null)
            return System.out;
        else
            return new FileOutputStream(outputFileName);
    }

    @Override
    protected void cleanup() {
        Closer.close(model);
        Closer.close(zk);
        super.cleanup();
    }
}
