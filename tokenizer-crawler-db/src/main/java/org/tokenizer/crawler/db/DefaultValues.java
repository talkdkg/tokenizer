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
package org.tokenizer.crawler.db;

import java.util.Date;

import org.apache.tika.metadata.Metadata;

public interface DefaultValues {

    public static final byte[] EMPTY_ARRAY = new byte[0];
    public static final String EMPTY_STRING = "";
    public static final Date EMPTY_DATE = new Date(0);
    public static final String UTF8_CHARSET = "UTF-8";
    public static final String ASCII_CHARSET = "US-ASCII";
    public static final Metadata EMPTY_METADATA = new Metadata();
}
