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
package org.tokenizer.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Logs {

    public static void logThreadJoin(final Thread thread) {
        Log log = LogFactory.getLog("org.lilyproject.threads.join");
        if (!log.isInfoEnabled())
            return;
        String context = "";
        Exception e = new Exception();
        e.fillInStackTrace();
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length >= 2) {
            context = stackTrace[1].toString();
        }
        log.info("Waiting for thread to die: " + thread + " at " + context);
    }
}
