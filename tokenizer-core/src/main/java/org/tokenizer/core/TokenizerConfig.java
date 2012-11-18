/*
 * Copyright 2007-2012 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    private TokenizerConfig() {
        // singleton
    }

    public static Properties getProperties() {
        if (properties != null)
            return properties;
        synchronized (TokenizerConfig.class) {
            if (properties == null) {
                String home = System.getProperty("tokenizer.home");
                if (home == null || home.equals(StringPool.EMPTY))
                    home = "/java/git/tokenizer/conf";
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

    public static final int getInt(String key, int defaultValue) {
        String value = getProperties().getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            LOG.warn(
                    "Can't load configuration parameter: {}; using default: {}",
                    key, value);
            return defaultValue;
        }
    }
    
    public static final String getString(String key) {
        return getProperties().getProperty(key);
    }
    
}
