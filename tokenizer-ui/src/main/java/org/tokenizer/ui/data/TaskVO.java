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
package org.tokenizer.ui.data;

import java.io.Serializable;
import java.util.Date;

import org.tokenizer.executor.engine.MetricsCache;
import org.tokenizer.executor.model.api.TaskBatchBuildState;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.impl.TaskInfoBean;

public class TaskVO implements Serializable {

    private static final long serialVersionUID = 1L;

    public String taskName;

    public TaskGeneralState taskGeneralState;
    public TaskBatchBuildState taskBatchBuildState;

    private Date submitTime;

    private long robotsRestrictedUrlCount;
    private long totalProcessedUrlCount;
    private long fetchedSuccessfullyUrlCount;
    private long fetchErrorsCount;
    private long otherErrorsCount;
    private long injectedUrlCount;

    private long totalSuccessfulMeanTime;
    private long averageSuccessfulMeanTime;

    public TaskVO() {
    }

    public TaskVO(TaskInfoBean taskDefinition) {

        this.taskName = taskDefinition.getName();
        this.taskGeneralState = taskDefinition.getGeneralState();
        this.taskBatchBuildState = taskDefinition.getBatchBuildState();
        this.submitTime = new Date(taskDefinition.getSubmitTime());

        taskDefinition.getCounters();

        Long robotsRestrictedUrlCount = taskDefinition.getCounters().get(
                MetricsCache.URL_ROBOTS_KEY);
        if (robotsRestrictedUrlCount != null)
            this.robotsRestrictedUrlCount = robotsRestrictedUrlCount;

        Long totalProcessedUrlCount = taskDefinition.getCounters().get(
                MetricsCache.URL_TOTAL_KEY);
        if (totalProcessedUrlCount != null)
            this.totalProcessedUrlCount = totalProcessedUrlCount;

        Long fetchedSuccessfullyUrlCount = taskDefinition.getCounters().get(
                MetricsCache.URL_OK_KEY);
        if (fetchedSuccessfullyUrlCount != null)
            this.fetchedSuccessfullyUrlCount = fetchedSuccessfullyUrlCount;

        Long fetchErrorsCount = taskDefinition.getCounters().get(
                MetricsCache.URL_ERROR_KEY);
        if (fetchErrorsCount != null)
            this.fetchErrorsCount = fetchErrorsCount;

        Long otherErrorsCount = taskDefinition.getCounters().get(
                MetricsCache.OTHER_ERRORS);
        if (otherErrorsCount != null)
            this.otherErrorsCount = otherErrorsCount;

        Long injectedUrlCount = taskDefinition.getCounters().get(
                MetricsCache.INJECTS_COUNT);
        if (injectedUrlCount != null)
            this.injectedUrlCount = injectedUrlCount;

        Long totalSuccessfulMeanTime = taskDefinition.getCounters().get(
                MetricsCache.TOTAL_MEAN_TIME_KEY);
        if (totalSuccessfulMeanTime != null)
            this.totalSuccessfulMeanTime = totalSuccessfulMeanTime;

        if (this.fetchedSuccessfullyUrlCount > 0)
            this.averageSuccessfulMeanTime = (long) (this.totalSuccessfulMeanTime / this.fetchedSuccessfullyUrlCount);

    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public TaskGeneralState getTaskGeneralState() {
        return taskGeneralState;
    }

    public void setTaskGeneralState(TaskGeneralState taskGeneralState) {
        this.taskGeneralState = taskGeneralState;
    }

    public TaskBatchBuildState getTaskBatchBuildState() {
        return taskBatchBuildState;
    }

    public void setTaskBatchBuildState(TaskBatchBuildState taskBatchBuildState) {
        this.taskBatchBuildState = taskBatchBuildState;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public long getRobotsRestrictedUrlCount() {
        return robotsRestrictedUrlCount;
    }

    public void setRobotsRestrictedUrlCount(long robotsRestrictedUrlCount) {
        this.robotsRestrictedUrlCount = robotsRestrictedUrlCount;
    }

    public long getTotalProcessedUrlCount() {
        return totalProcessedUrlCount;
    }

    public void setTotalProcessedUrlCount(long totalProcessedUrlCount) {
        this.totalProcessedUrlCount = totalProcessedUrlCount;
    }

    public long getFetchedSuccessfullyUrlCount() {
        return fetchedSuccessfullyUrlCount;
    }

    public void setFetchedSuccessfullyUrlCount(long fetchedSuccessfullyUrlCount) {
        this.fetchedSuccessfullyUrlCount = fetchedSuccessfullyUrlCount;
    }

    public long getFetchErrorsCount() {
        return fetchErrorsCount;
    }

    public void setFetchErrorsCount(long fetchErrorsCount) {
        this.fetchErrorsCount = fetchErrorsCount;
    }

    public long getOtherErrorsCount() {
        return otherErrorsCount;
    }

    public void setOtherErrorsCount(long otherErrorsCount) {
        this.otherErrorsCount = otherErrorsCount;
    }

    public long getInjectedUrlCount() {
        return injectedUrlCount;
    }

    public void setInjectedUrlCount(long injectedUrlCount) {
        this.injectedUrlCount = injectedUrlCount;
    }

    public long getTotalSuccessfulMeanTime() {
        return totalSuccessfulMeanTime;
    }

    public void setTotalSuccessfulMeanTime(long totalSuccessfulMeanTime) {
        this.totalSuccessfulMeanTime = totalSuccessfulMeanTime;
    }

    public long getAverageSuccessfulMeanTime() {
        return averageSuccessfulMeanTime;
    }

    public void setAverageSuccessfulMeanTime(long averageSuccessfulMeanTime) {
        this.averageSuccessfulMeanTime = averageSuccessfulMeanTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + (int) (averageSuccessfulMeanTime ^ (averageSuccessfulMeanTime >>> 32));
        result = prime * result
                + (int) (fetchErrorsCount ^ (fetchErrorsCount >>> 32));
        result = prime
                * result
                + (int) (fetchedSuccessfullyUrlCount ^ (fetchedSuccessfullyUrlCount >>> 32));
        result = prime * result
                + (int) (injectedUrlCount ^ (injectedUrlCount >>> 32));
        result = prime * result
                + (int) (otherErrorsCount ^ (otherErrorsCount >>> 32));
        result = prime
                * result
                + (int) (robotsRestrictedUrlCount ^ (robotsRestrictedUrlCount >>> 32));
        result = prime * result
                + ((submitTime == null) ? 0 : submitTime.hashCode());
        result = prime
                * result
                + ((taskBatchBuildState == null) ? 0 : taskBatchBuildState
                        .hashCode());
        result = prime
                * result
                + ((taskGeneralState == null) ? 0 : taskGeneralState.hashCode());
        result = prime * result
                + ((taskName == null) ? 0 : taskName.hashCode());
        result = prime
                * result
                + (int) (totalProcessedUrlCount ^ (totalProcessedUrlCount >>> 32));
        result = prime
                * result
                + (int) (totalSuccessfulMeanTime ^ (totalSuccessfulMeanTime >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskVO other = (TaskVO) obj;
        if (averageSuccessfulMeanTime != other.averageSuccessfulMeanTime)
            return false;
        if (fetchErrorsCount != other.fetchErrorsCount)
            return false;
        if (fetchedSuccessfullyUrlCount != other.fetchedSuccessfullyUrlCount)
            return false;
        if (injectedUrlCount != other.injectedUrlCount)
            return false;
        if (otherErrorsCount != other.otherErrorsCount)
            return false;
        if (robotsRestrictedUrlCount != other.robotsRestrictedUrlCount)
            return false;
        if (submitTime == null) {
            if (other.submitTime != null)
                return false;
        } else if (!submitTime.equals(other.submitTime))
            return false;
        if (taskBatchBuildState != other.taskBatchBuildState)
            return false;
        if (taskGeneralState != other.taskGeneralState)
            return false;
        if (taskName == null) {
            if (other.taskName != null)
                return false;
        } else if (!taskName.equals(other.taskName))
            return false;
        if (totalProcessedUrlCount != other.totalProcessedUrlCount)
            return false;
        if (totalSuccessfulMeanTime != other.totalSuccessfulMeanTime)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TaskVO [taskName=" + taskName + ", taskGeneralState="
                + taskGeneralState + ", taskBatchBuildState="
                + taskBatchBuildState + ", submitTime=" + submitTime
                + ", robotsRestrictedUrlCount=" + robotsRestrictedUrlCount
                + ", totalProcessedUrlCount=" + totalProcessedUrlCount
                + ", fetchedSuccessfullyUrlCount="
                + fetchedSuccessfullyUrlCount + ", fetchErrorsCount="
                + fetchErrorsCount + ", otherErrorsCount=" + otherErrorsCount
                + ", injectedUrlCount=" + injectedUrlCount
                + ", totalSuccessfulMeanTime=" + totalSuccessfulMeanTime
                + ", averageSuccessfulMeanTime=" + averageSuccessfulMeanTime
                + "]";
    }

}
