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
package org.tokenizer.executor.model.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.tokenizer.executor.model.configuration.TaskConfiguration;

public class TaskInfoBean {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(TaskInfoBean.class);
    private TaskConfiguration taskConfiguration;
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

    private void checkIfMutable() {
        if (immutable)
            throw new RuntimeException("This TaskDefinition is immutable");
    }

    public TaskConfiguration getTaskConfiguration() {
        return taskConfiguration;
    }

    public void setTaskConfiguration(final TaskConfiguration taskConfiguration) {
        this.taskConfiguration = taskConfiguration;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        LOG.error(
                "TaskInfoBean.equals() called! Please use TaskConfiguration.equals() for configuration updates.",
                new Exception());
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskInfoBean other = (TaskInfoBean) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TaskInfoBean [taskConfiguration=" + taskConfiguration
                + ", zkDataVersion=" + zkDataVersion + ", immutable="
                + immutable + ", submitTime=" + submitTime + ", counters="
                + counters + ", metricsUpdateTimestamp="
                + metricsUpdateTimestamp + ", uuid=" + uuid + "]";
    }
}
