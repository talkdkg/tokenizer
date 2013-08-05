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

import java.util.UUID;

public class ExecutorModelEvent {

    private final ExecutorModelEventType type;
    private final UUID uuid;

    public ExecutorModelEvent(final ExecutorModelEventType type, final UUID uuid) {
        this.type = type;
        this.uuid = uuid;
    }

    public ExecutorModelEventType getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + type.hashCode();
        result = prime * result + uuid.hashCode();
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
        ExecutorModelEvent other = (ExecutorModelEvent) obj;
        return other.type == type && other.uuid.equals(uuid);
    }

    @Override
    public String toString() {
        return "ExecutorModelEvent [type = " + type + ", uuid = " + uuid + "]";
    }
}
