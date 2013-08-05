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
package org.tokenizer.executor.admin.cli;

import java.util.List;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.tokenizer.executor.model.api.TaskInfoBean;

public class AddTaskCli extends BaseAdminCli {

    @Override
    protected String getCmdName() {
        return "add-task";
    }

    public static void main(final String[] args) {
        new AddTaskCli().start(args);
    }

    @Override
    public List<Option> getOptions() {
        List<Option> options = super.getOptions();
        // nameOption.setRequired(true);
        // configurationOption.setRequired(true);
        options.add(nameOption);
        options.add(configurationOption);
        options.add(generalStateOption);
        options.add(buildStateOption);
        options.add(forceOption);
        return options;
    }

    @Override
    public int run(final CommandLine cmd) throws Exception {
        int result = super.run(cmd);
        if (result != 0)
            return result;
        UUID uuid = UUID.randomUUID();
        TaskInfoBean task = new TaskInfoBean(uuid);
        task.setTaskConfiguration(taskConfiguration);
        if (generalState != null) {
            task.getTaskConfiguration().setGeneralState(generalState);
        }
        model.addTask(task);
        System.out.println("Task created: " + uuid);
        return 0;
    }
}
