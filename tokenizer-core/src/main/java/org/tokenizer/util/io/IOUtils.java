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
package org.tokenizer.util.io;

import java.io.Closeable;

import org.apache.commons.logging.LogFactory;

public class IOUtils {

    public static void closeQuietly(final Closeable cl) {
        if (cl != null) {
            try {
                cl.close();
            } catch (Throwable t) {
                LogFactory.getLog(IOUtils.class).error("Problem closing a source or destination.", t);
            }
        }
    }

    public static void closeQuietly(final Closeable cl, final String identification) {
        if (cl != null) {
            try {
                cl.close();
            } catch (Throwable t) {
                LogFactory.getLog(IOUtils.class).error("Problem closing a source or destination on " + identification,
                        t);
            }
        }
    }
}
