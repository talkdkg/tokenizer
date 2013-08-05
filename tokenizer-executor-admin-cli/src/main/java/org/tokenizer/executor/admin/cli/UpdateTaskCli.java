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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;

public class UpdateTaskCli extends BaseAdminCli {

    @Override
    protected String getCmdName() {
        return "update-task";
    }

    public static void main(final String[] args) {
        new UpdateTaskCli().start(args);
    }

    @Override
    public List<Option> getOptions() {
        List<Option> options = super.getOptions();
        nameOption.setRequired(true);
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
        if (!model.hasTask(uuid)) {
            System.out.println("Task does not exist: " + uuid);
            return 1;
        }
        String lock = model.lockTask(uuid);
        try {
            TaskInfoBean task = model.getMutableTask(uuid);
            boolean changes = false;
            if (taskConfiguration != null
                    && !taskConfiguration.equals(task.getTaskConfiguration())) {
                task.setTaskConfiguration(taskConfiguration);
                changes = true;
            }
            if (generalState != null
                    && generalState != task.getTaskConfiguration()
                            .getGeneralState()) {
                task.getTaskConfiguration().setGeneralState(generalState);
                changes = true;
            }
            if (changes) {
                model.updateTask(task, lock);
                System.out.println("Task definition updated: " + uuid);
            } else {
                System.out
                        .println("Task already matches the specified settings, did not update it.");
            }
        } finally {
            // In case we requested deletion of a task, it might be that the
            // lock is
            // already removed by the time we get here as part of the task
            // deletion.
            boolean ignoreMissing = generalState != null
                    && generalState == TaskGeneralState.DELETE_REQUESTED;
            model.unlockTask(lock, ignoreMissing);
        }
        return 0;
    }
}
