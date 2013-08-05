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
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Closer {

    /**
     * Closes anything {@link Closeable}, catches any throwable that might occur
     * during closing and logs it as an error.
     */
    public static void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable t) {
                Log log = LogFactory.getLog(Closer.class);
                log.error("Error closing object of type " + closeable.getClass().getName(), t);
            }
        }
    }

    public static void close(final Object object) {
        if (object != null) {
            try {
                Method closeMethod = null;
                Method[] methods = object.getClass().getMethods();
                for (Method method : methods) {
                    if (method.getParameterTypes().length == 0) {
                        if (method.getName().equals("close")) {
                            closeMethod = method;
                            break;
                        }
                        else if (method.getName().equals("shutdown")) {
                            closeMethod = method;
                        }
                        else if (method.getName().equals("stop")) {
                            closeMethod = method;
                        }
                    }
                }
                if (closeMethod != null) {
                    closeMethod.invoke(object);
                }
                else {
                    Log log = LogFactory.getLog(Closer.class);
                    log.error("Do not know how to close object of type " + object.getClass().getName());
                }
            } catch (Throwable t) {
                Log log = LogFactory.getLog(Closer.class);
                log.error("Error closing object of type " + object.getClass().getName(), t);
            }
        }
    }
}
