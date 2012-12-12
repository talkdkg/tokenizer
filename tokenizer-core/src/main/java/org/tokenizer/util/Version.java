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
package org.tokenizer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.tokenizer.util.io.Closer;

public class Version {

    public static String readVersion(final String groupId,
            final String artifactId) {
        String propPath = "/META-INF/maven/" + groupId + "/" + artifactId
                + "/pom.properties";
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
