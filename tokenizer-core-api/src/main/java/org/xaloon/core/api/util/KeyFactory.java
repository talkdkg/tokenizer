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
package org.xaloon.core.api.util;

import java.io.Serializable;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

/**
 * @author vytautas r.
 */
public class KeyFactory implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * @return random generated string
     */
    public static String generateKey() {
        String random = RandomStringUtils.random(128);
        return DigestUtils.md5Hex(random);
    }
}
