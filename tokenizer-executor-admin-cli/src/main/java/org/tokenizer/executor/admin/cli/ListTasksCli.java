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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.tokenizer.executor.model.api.TaskInfoBean;

public class ListTasksCli extends BaseAdminCli {

    @Override
    protected String getCmdName() {
        return "list-tasks";
    }

    public static void main(final String[] args) {
        new ListTasksCli().start(args);
    }

    @Override
    public int run(final CommandLine cmd) throws Exception {
        int result = super.run(cmd);
        if (result != 0)
            return result;
        List<TaskInfoBean> tasks = new ArrayList<TaskInfoBean>(model.getTasks());
        // Collections.sort(tasks, TaskInfoBeanComparator.INSTANCE);
        System.out.println("Number of tasks: " + tasks.size());
        System.out.println();
        for (TaskInfoBean task : tasks) {
            System.out.println(task.getUuid().toString());
            System.out.println("  + General state: "
                    + task.getTaskConfiguration().getGeneralState());
            System.out.println("  + Submitted: "
                    + new Date(task.getSubmitTime()));
            System.out.println("  + Stats: ");
            for (Map.Entry<String, Long> counter : task.getCounters()
                    .entrySet()) {
                System.out.println("    + " + counter.getKey() + ": "
                        + counter.getValue());
            }
        }
        return 0;
    }
}
