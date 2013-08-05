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
package org.xaloon.core.api.counting;

import org.xaloon.core.api.persistence.Persistable;

public interface CounterEntity extends Persistable {

    String getCounterGroup();

    void setCounterGroup(String counterGroup);

    /**
     * Trackable object may have the same id if there are many implementations. Category + id should ensure uniqueness
     * 
     * @return unique identifier for the same group of trackable objects
     */
    Long getCategoryId();

    void setCategoryId(Long categoryId);

    Long getEntityId();

    void setEntityId(Long entityId);

    Long getCount();

    void setCount(Long count);
}
