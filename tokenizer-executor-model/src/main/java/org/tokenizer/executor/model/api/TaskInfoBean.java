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
package org.tokenizer.executor.model.api;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.tokenizer.executor.model.configuration.AbstractTaskConfiguration;


public class TaskInfoBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskInfoBean.class);
    private AbstractTaskConfiguration taskConfiguration;
    private int zkDataVersion = -1;
    private boolean immutable;
    private long submitTime = new Date().getTime();
    private Map<String, Long> counters = new HashMap<String, Long>();
    private long metricsUpdateTimestamp;
    private final UUID uuid;


    public String getName() {
        return taskConfiguration.getName();
    }


    public String getImplementationName() {
        return taskConfiguration.getImplementationName();
    }


    public String getGeneralState() {
        return taskConfiguration.getGeneralState().toString();
    }


    public TaskInfoBean(final UUID uuid) {
        this.uuid = uuid;
    }


    public UUID getUuid() {
        return uuid;
    }


    public long getMetricsUpdateTimestamp() {
        return metricsUpdateTimestamp;
    }


    public void setMetricsUpdateTimestamp(final long metricsUpdateTimestamp) {
        this.metricsUpdateTimestamp = metricsUpdateTimestamp;
    }


    public long getSubmitTime() {
        return submitTime;
    }


    public Date getSubmitDate() {
        return new Date(submitTime);
    }


    public void setSubmitTime(final long submitTime) {
        this.submitTime = submitTime;
    }


    public Map<String, Long> getCounters() {
        return counters;
    }


    public void setCounters(final Map<String, Long> counters) {
        this.counters = counters;
    }


    public void addCounter(final String key, final Long value) {
        this.counters.put(key, value);
    }


    public int getZkDataVersion() {
        return zkDataVersion;
    }


    public void setZkDataVersion(final int zkDataVersion) {
        checkIfMutable();
        this.zkDataVersion = zkDataVersion;
    }


    public void makeImmutable() {
        this.immutable = true;
    }

    public void makeMutable() {
        this.immutable = false;
    }

    private void checkIfMutable() {

        // TODO: I commented it out because I can't update table cell in UI...

        // if (immutable) {
        // throw new RuntimeException("This TaskDefinition is immutable");
        // }
    }


    public AbstractTaskConfiguration getTaskConfiguration() {
        return taskConfiguration;
    }


    public void setTaskConfiguration(final AbstractTaskConfiguration taskConfiguration) {
        this.taskConfiguration = taskConfiguration;
    }


    @Override
    public int hashCode() {
        return uuid.hashCode();
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TaskInfoBean other = (TaskInfoBean) obj;
        if (uuid == null) {
            if (other.uuid != null) {
                return false;
            }
        } else if (!uuid.equals(other.uuid)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "TaskInfoBean [taskConfiguration=" + taskConfiguration + ", zkDataVersion=" + zkDataVersion + ", immutable=" + immutable + ", submitTime=" + submitTime
                + ", counters=" + counters + ", metricsUpdateTimestamp=" + metricsUpdateTimestamp + ", uuid=" + uuid + "]";
    }
}
