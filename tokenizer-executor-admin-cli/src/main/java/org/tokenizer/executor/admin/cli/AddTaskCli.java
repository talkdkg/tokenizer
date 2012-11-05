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

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.tokenizer.executor.model.api.TaskInfoBean;

public class AddTaskCli extends BaseAdminCli {
    @Override
    protected String getCmdName() {
        return "add-task";
    }

    public static void main(String[] args) {
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
    public int run(CommandLine cmd) throws Exception {
        int result = super.run(cmd);
        if (result != 0)
            return result;
        TaskInfoBean task = new TaskInfoBean();

        task.setTaskConfiguration(taskConfiguration);

        if (generalState != null)
            task.setGeneralState(generalState);
        if (buildState != null)
            task.setBatchBuildState(buildState);
        model.addTask(task);
        System.out.println("Task created: " + taskName);
        return 0;
    }

}
