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

import org.tokenizer.executor.model.configuration.TaskConfiguration;

public class TaskInfoBean {

    private TaskConfiguration taskConfiguration;
    private int zkDataVersion = -1;
    private boolean immutable;
    private long submitTime = new Date().getTime();
    private Map<String, Long> counters = new HashMap<String, Long>();
    private long metricsUpdateTimestamp;

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
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((taskConfiguration == null) ? 0 : taskConfiguration
                        .hashCode());
        return result;
    }

    // this should compare by task name and implementation class only:
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskInfoBean other = (TaskInfoBean) obj;
        if (taskConfiguration == null) {
            if (other.taskConfiguration != null)
                return false;
        } else if (!taskConfiguration.equals(other.taskConfiguration))
            return false;
        return true;
    }
}
