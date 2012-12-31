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
package org.tokenizer.executor.model.configuration;

import java.io.Serializable;

import org.tokenizer.executor.model.api.TaskGeneralState;

/**
 * The configuration for a simple task.
 * 
 */
public abstract class TaskConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private TaskGeneralState generalState = TaskGeneralState.START_REQUESTED;

    // TODO: temporary commented out due to UUID migration
    // public String getName() {
    // return name;
    // }
    public String getNameTemp() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public TaskGeneralState getGeneralState() {
        return generalState;
    }

    public void setGeneralState(final TaskGeneralState generalState) {
        this.generalState = generalState;
    }

    public abstract String getImplementationName();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskConfiguration other = (TaskConfiguration) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
