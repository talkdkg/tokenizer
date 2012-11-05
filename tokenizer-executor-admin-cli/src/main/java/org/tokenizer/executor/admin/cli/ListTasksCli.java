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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskInfoBeanComparator;

public class ListTasksCli extends BaseAdminCli {
    @Override
    protected String getCmdName() {
        return "list-tasks";
    }

    public static void main(String[] args) {
        new ListTasksCli().start(args);
    }

    @Override
    public int run(CommandLine cmd) throws Exception {
        int result = super.run(cmd);
        if (result != 0)
            return result;

        List<TaskInfoBean> tasks = new ArrayList<TaskInfoBean>(model.getTasks());

        Collections.sort(tasks, TaskInfoBeanComparator.INSTANCE);

        System.out.println("Number of tasks: " + tasks.size());
        System.out.println();

        for (TaskInfoBean task : tasks) {
            System.out.println(task.getName());
            System.out.println("  + General state: " + task.getGeneralState());
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