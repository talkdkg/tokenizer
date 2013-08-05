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
package org.xaloon.core.api.exception;

/**
 * It is thrown if class instance could not be created
 * 
 * @author vytautas r.
 * @version 1.1, 09/28/10
 * @since 1.5
 */

public class CreateClassInstanceException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Default construct.
     * 
     * @param name
     *            message for exception
     */
    public CreateClassInstanceException(String name) {
        super(name);
    }
}
