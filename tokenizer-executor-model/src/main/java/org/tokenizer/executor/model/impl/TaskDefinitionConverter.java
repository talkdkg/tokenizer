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
package org.tokenizer.executor.model.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import net.iharder.Base64;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.lilyproject.util.json.JsonFormat;
import org.lilyproject.util.json.JsonUtil;
import org.tokenizer.executor.model.api.TaskBatchBuildState;
import org.tokenizer.executor.model.api.TaskDefinition;
import org.tokenizer.executor.model.api.TaskGeneralState;

public class TaskDefinitionConverter {
  
  public static TaskDefinitionConverter INSTANCE = new TaskDefinitionConverter();
  
  public void fromJsonBytes(byte[] json, TaskDefinitionImpl taskDefinition) {
    ObjectNode node;
    try {
      node = (ObjectNode) JsonFormat
          .deserialize(new ByteArrayInputStream(json));
    } catch (IOException e) {
      throw new RuntimeException("Error parsing TaskDefinition JSON.", e);
    }
    fromJson(node, taskDefinition);
  }
  
  public void fromJson(ObjectNode node, TaskDefinitionImpl taskDefinition) {
    TaskGeneralState state = TaskGeneralState.valueOf(JsonUtil.getString(node,
        "generalState"));
    TaskBatchBuildState buildState = TaskBatchBuildState.valueOf(JsonUtil
        .getString(node, "batchBuildState"));
    
    String queueSubscriptionId = JsonUtil.getString(node,
        "queueSubscriptionId", null);
    
    byte[] configuration;
    try {
      String configurationAsString = JsonUtil.getString(node, "configuration");
      configuration = Base64.decode(configurationAsString);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    ObjectNode info = JsonUtil.getObject(node, "info");
    taskDefinition.setSubmitTime(JsonUtil.getLong(info, "submitTime"));
    ObjectNode countersNode = JsonUtil.getObject(info, "counters");
    Iterator<String> it = countersNode.getFieldNames();
    while (it.hasNext()) {
      String key = it.next();
      long value = JsonUtil.getLong(countersNode, key);
      taskDefinition.addCounter(key, value);
    }
    
    taskDefinition.setMetricsUpdateTimestamp(JsonUtil.getLong(info,
        "metricsUpdateTimestamp"));
    
    taskDefinition.setGeneralState(state);
    taskDefinition.setBatchBuildState(buildState);
    taskDefinition.setQueueSubscriptionId(queueSubscriptionId);
    taskDefinition.setConfiguration(configuration);
    
  }
  
  public byte[] toJsonBytes(TaskDefinition taskDefinition) {
    try {
      return JsonFormat.serializeAsBytes(toJson(taskDefinition));
    } catch (IOException e) {
      throw new RuntimeException("Error serializing TaskDefinition to JSON.", e);
    }
  }
  
  public ObjectNode toJson(TaskDefinition taskDefinition) {
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    
    node.put("generalState", taskDefinition.getGeneralState().toString());
    node.put("batchBuildState", taskDefinition.getBatchBuildState().toString());
    
    if (taskDefinition.getQueueSubscriptionId() != null) node.put(
        "queueSubscriptionId", taskDefinition.getQueueSubscriptionId());
    
    String configurationAsString;
    try {
      configurationAsString = Base64.encodeBytes(
          taskDefinition.getConfiguration(), Base64.GZIP);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    node.put("configuration", configurationAsString);
    
    ObjectNode info = node.putObject("info");
    info.put("submitTime", taskDefinition.getSubmitTime());
    ObjectNode countersNode = info.putObject("counters");
    for (Map.Entry<String,Long> counter : taskDefinition.getCounters()
        .entrySet()) {
      countersNode.put(counter.getKey(), counter.getValue());
    }
    
    info.put("metricsUpdateTimestamp",
        taskDefinition.getMetricsUpdateTimestamp());
    
    return node;
    
  }
}
