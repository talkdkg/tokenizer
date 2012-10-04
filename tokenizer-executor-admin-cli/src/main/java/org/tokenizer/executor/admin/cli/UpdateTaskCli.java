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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.tokenizer.executor.model.api.TaskDefinition;
import org.tokenizer.executor.model.api.TaskGeneralState;

public class UpdateTaskCli extends BaseAdminCli {
  @Override
  protected String getCmdName() {
    return "update-task";
  }
  
  public static void main(String[] args) {
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
  public int run(CommandLine cmd) throws Exception {
    int result = super.run(cmd);
    if (result != 0) return result;
    
    if (!model.hasTaskDefinition(taskName)) {
      System.out.println("Task does not exist: " + taskName);
      return 1;
    }
    
    String lock = model.lockTaskDefinition(taskName);
    try {
      TaskDefinition taskDefinition = model.getMutableTaskDefinition(taskName);
      
      boolean changes = false;
      
      if (taskConfiguration != null
          && !Arrays.equals(taskConfiguration,
              taskDefinition.getConfiguration())) {
        taskDefinition.setConfiguration(taskConfiguration);
        changes = true;
      }
      
      if (generalState != null
          && generalState != taskDefinition.getGeneralState()) {
        taskDefinition.setGeneralState(generalState);
        changes = true;
      }
      
      if (buildState != null
          && buildState != taskDefinition.getBatchBuildState()) {
        taskDefinition.setBatchBuildState(buildState);
        changes = true;
      }
      
      if (changes) {
        model.updateTaskDefinition(taskDefinition, lock);
        System.out.println("Task definition updated: " + taskName);
      } else {
        System.out
            .println("Task already matches the specified settings, did not update it.");
      }
      
    } finally {
      // In case we requested deletion of a task, it might be that the lock is
      // already removed by the time we get here as part of the task deletion.
      boolean ignoreMissing = generalState != null
          && generalState == TaskGeneralState.DELETE_REQUESTED;
      model.unlockTaskDefinition(lock, ignoreMissing);
    }
    
    return 0;
  }
  
}
