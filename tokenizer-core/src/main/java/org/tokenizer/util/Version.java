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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.tokenizer.util.io.Closer;

public class Version {

    public static String readVersion(final String groupId, final String artifactId) {
        String propPath = "/META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties";
        InputStream is = Version.class.getResourceAsStream(propPath);
        if (is != null) {
            Properties properties = new Properties();
            try {
                properties.load(is);
                String version = properties.getProperty("version");
                if (version != null)
                    return version;
            } catch (IOException e) {
                // ignore
            }
            Closer.close(is);
        }
        return "undetermined (please report this as bug)";
    }
}
