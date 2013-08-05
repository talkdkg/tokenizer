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

import java.io.Serializable;

/**
 * @author vytautas r.
 */
public interface CounterDao extends Serializable {

    /**
     * @param counterGroup
     *            refers to the type of update, such as view/count/other
     * @param categoryId
     *            refers to the table, such as blog entry, classifier/other
     * @param entityId
     *            refers to the concrete row to update
     * @return true if action was performed successfully
     */
    boolean increment(String counterGroup, Long categoryId, Long entityId);

    /**
     * @param counterGroup
     *            refers to the type of update, such as view/count/other
     * @param categoryId
     *            refers to the table, such as blog entry, classifier/other
     * @param entityId
     *            refers to the concrete row to update
     * @return counter value for selected entry
     */
    Long count(String counterGroup, Long categoryId, Long entityId);

    /**
     * @param counterGroup
     *            refers to the type of update, such as view/count/other
     * @param categoryId
     *            refers to the table, such as blog entry, classifier/other
     * @param entityId
     *            refers to the concrete row to update
     * @return true if action was performed successfully
     */
    boolean decrement(String counterGroup, Long categoryId, Long entityId);

}
