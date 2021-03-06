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
package org.tokenizer.executor.model.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.iharder.Base64;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.tokenizer.core.util.JavaSerializationUtils;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.configuration.AbstractTaskConfiguration;
import org.tokenizer.util.json.JsonFormat;
import org.tokenizer.util.json.JsonUtil;

public class TaskInfoBeanConverter {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskInfoBeanConverter.class);

    private TaskInfoBeanConverter() {
    }

    public static TaskInfoBean fromJsonBytes(final byte[] json) {
        ObjectNode node;
        try {
            node = (ObjectNode) JsonFormat.deserialize(new ByteArrayInputStream(json));
        } catch (IOException e) {
            throw new RuntimeException("Error parsing TaskDefinition JSON.", e);
        }
        return fromJson(node);
    }

    public static TaskInfoBean fromJson(final ObjectNode node) {
        byte[] configuration;
        try {
            String configurationAsString = JsonUtil.getString(node, "configuration");
            configuration = Base64.decode(configurationAsString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObjectNode info = JsonUtil.getObject(node, "info");
        String uuidString = JsonUtil.getString(info, "uuid", null);
        UUID uuid = null;
        if (uuidString != null) {
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException e) {
            }
        }
        if (uuid == null) {
            LOG.error("Task found with null UUID!");
        }
        TaskInfoBean task = new TaskInfoBean(uuid);
        task.setSubmitTime(JsonUtil.getLong(info, "submitTime"));
        ObjectNode countersNode = JsonUtil.getObject(info, "counters");
        Iterator<String> it = countersNode.getFieldNames();
        while (it.hasNext()) {
            String key = it.next();
            long value = JsonUtil.getLong(countersNode, key);
            task.addCounter(key, value);
        }
        task.setMetricsUpdateTimestamp(JsonUtil.getLong(info, "metricsUpdateTimestamp"));
        AbstractTaskConfiguration config = (AbstractTaskConfiguration) JavaSerializationUtils
                .deserialize(configuration);
        task.setTaskConfiguration(config);
        return task;
    }

    public static byte[] toJsonBytes(final TaskInfoBean task) {
        try {
            return JsonFormat.serializeAsBytes(toJson(task));
        } catch (IOException e) {
            throw new RuntimeException("Error serializing TaskDefinition to JSON.", e);
        }
    }

    public static ObjectNode toJson(final TaskInfoBean task) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        String configurationAsString;
        configurationAsString = Base64.encodeBytes(JavaSerializationUtils.serialize(task.getTaskConfiguration()));
        node.put("configuration", configurationAsString);
        ObjectNode info = node.putObject("info");
        info.put("submitTime", task.getSubmitTime());
        ObjectNode countersNode = info.putObject("counters");
        for (Map.Entry<String, Long> counter : task.getCounters().entrySet()) {
            countersNode.put(counter.getKey(), counter.getValue());
        }
        info.put("metricsUpdateTimestamp", task.getMetricsUpdateTimestamp());
        info.put("uuid", task.getUuid().toString());
        return node;
    }
}
