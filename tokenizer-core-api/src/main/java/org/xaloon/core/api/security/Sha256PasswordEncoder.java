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
package org.xaloon.core.api.security;

import javax.inject.Named;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author vytautas r.
 */
@Named("sha256PasswordEncoder")
public class Sha256PasswordEncoder extends PasswordEncoder {

    @Override
    public String encode(String username, String password) {
        return DigestUtils.sha256Hex(username + password);
    }

    @Override
    public String encode(String username, String password, String salt) {
        return DigestUtils.sha256Hex(encode(username, password) + salt);
    }
}
