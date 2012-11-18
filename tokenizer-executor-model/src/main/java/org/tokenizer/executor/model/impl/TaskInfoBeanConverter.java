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
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

public class TaskInfoBeanConverter {
    private TaskInfoBeanConverter() {
    }

    public static void fromJsonBytes(byte[] json, TaskInfoBean task) {
        ObjectNode node;
        try {
            node = (ObjectNode) JsonFormat
                    .deserialize(new ByteArrayInputStream(json));
        } catch (IOException e) {
            throw new RuntimeException("Error parsing TaskDefinition JSON.", e);
        }
        fromJson(node, task);
    }

    public static void fromJson(ObjectNode node, TaskInfoBean task) {
        String name = JsonUtil.getString(node, "name");
        task.setName(name);
        //String type = JsonUtil.getString(node, "type");
        //task.setType(type);
        TaskGeneralState state = TaskGeneralState.valueOf(JsonUtil.getString(
                node, "generalState"));
        TaskBatchBuildState buildState = TaskBatchBuildState.valueOf(JsonUtil
                .getString(node, "batchBuildState"));
        byte[] configuration;
        try {
            String configurationAsString = JsonUtil.getString(node,
                    "configuration");
            configuration = Base64.decode(configurationAsString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObjectNode info = JsonUtil.getObject(node, "info");
        task.setSubmitTime(JsonUtil.getLong(info, "submitTime"));
        ObjectNode countersNode = JsonUtil.getObject(info, "counters");
        Iterator<String> it = countersNode.getFieldNames();
        while (it.hasNext()) {
            String key = it.next();
            long value = JsonUtil.getLong(countersNode, key);
            task.addCounter(key, value);
        }
        task.setMetricsUpdateTimestamp(JsonUtil.getLong(info,
                "metricsUpdateTimestamp"));
        task.setGeneralState(state);
        task.setBatchBuildState(buildState);
        TaskConfiguration config = (TaskConfiguration) JavaSerializationUtils
                .deserialize(configuration);
        task.setTaskConfiguration(config);
    }

    public static byte[] toJsonBytes(TaskInfoBean task) {
        try {
            return JsonFormat.serializeAsBytes(toJson(task));
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error serializing TaskDefinition to JSON.", e);
        }
    }

    public static ObjectNode toJson(TaskInfoBean task) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("name", task.getName().toString());
        //node.put("type", task.getType().toString());
        node.put("generalState", task.getGeneralState().toString());
        node.put("batchBuildState", task.getBatchBuildState().toString());
        String configurationAsString;
        configurationAsString = Base64.encodeBytes(JavaSerializationUtils
                .serialize(task.getTaskConfiguration()));
        node.put("configuration", configurationAsString);
        ObjectNode info = node.putObject("info");
        info.put("submitTime", task.getSubmitTime());
        ObjectNode countersNode = info.putObject("counters");
        for (Map.Entry<String, Long> counter : task.getCounters().entrySet()) {
            countersNode.put(counter.getKey(), counter.getValue());
        }
        info.put("metricsUpdateTimestamp", task.getMetricsUpdateTimestamp());
        return node;
    }
}
