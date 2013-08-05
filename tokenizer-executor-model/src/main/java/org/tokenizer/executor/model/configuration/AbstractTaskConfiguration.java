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
package org.tokenizer.executor.model.configuration;

import java.io.Serializable;

import org.tokenizer.executor.model.api.TaskGeneralState;

/**
 * The configuration for a simple task.
 * 
 */
public abstract class AbstractTaskConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private TaskGeneralState generalState = TaskGeneralState.START_REQUESTED;

    public String getName() {
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
        AbstractTaskConfiguration other = (AbstractTaskConfiguration) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }
}
