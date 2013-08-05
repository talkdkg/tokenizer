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
package org.xaloon.core.api.audit;

import java.io.Serializable;
import java.util.List;

import org.xaloon.core.api.audit.model.AuditEntity;
import org.xaloon.core.api.audit.model.AuditState;

/**
 * Interface is used to audit object via it's annotations
 * 
 * @author vytautas r.
 * 
 */
public interface AuditFacade extends Serializable {
    /**
     * Read if audit object has required annotation and then generate audit record for that object.
     * 
     * @param state
     *            {@link AuditState} what kind of state auditable object is in
     * @param auditObject
     *            auditable object to parse
     */
    void audit(AuditState state, Object auditObject);

    List<AuditEntity> search(List<String> objectNames, int first, int count);
}
