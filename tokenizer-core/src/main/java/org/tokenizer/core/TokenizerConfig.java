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
package org.tokenizer.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenizerConfig {

    private static final Logger LOG = LoggerFactory
            .getLogger(TokenizerConfig.class);
    public static final String MAX_WAIT_MILLISECONDS = "max.wait.milliseconds";
    public static final String MAX_SOCKETS = "max.sockets";
    public static final String MAX_ACTIVE_THREADS = "max.active.threads";
    private static Properties properties = null;

    private static String home;

    private TokenizerConfig() {
        // singleton
    }

    public static Properties getProperties() {
        if (properties != null)
            return properties;
        synchronized (TokenizerConfig.class) {
            if (properties == null) {
                home = System.getProperty("tokenizer.home");
                if (home == null || home.equals(StringPool.EMPTY)) {
                    home = "/java/git/tokenizer/conf";
                }
                try {
                    properties = new Properties();
                    properties.load(new FileInputStream(home
                            + "/config.properties"));
                } catch (IOException e) {
                    try {
                        home = "/usr/java/tokenizer/conf";
                        properties = new Properties();
                        properties.load(new FileInputStream(home
                                + "/config.properties"));
                    } catch (IOException e2) {
                        throw new RuntimeException(
                                "Can't load configuration from " + home
                                        + "/config.properties");
                    }
                }
            }
        }
        return properties;
    }

    public static final int getInt(final String key, final int defaultValue) {
        String value = getProperties().getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            LOG.warn(
                    "Can't load configuration parameter: {}; using default: {}",
                    key, defaultValue);
            return defaultValue;
        }
    }

    public static final String getString(final String key) {
        return getProperties().getProperty(key);
    }

    public static final String getHome() {
        return home;
    }

}
