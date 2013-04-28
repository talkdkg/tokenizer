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
package org.tokenizer.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class JavaSerializationUtils {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(JavaSerializationUtils.class);

    private JavaSerializationUtils() {
    }

    public static byte[] serialize(final Serializable object) {
        LOG.debug("Serializing object: {}", object.toString());
        ByteArrayOutputStream out = new ByteArrayOutputStream(128);
        ObjectOutputStream objstream;
        try {
            objstream = new ObjectOutputStream(out);
            objstream.writeObject(object);
            objstream.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object deserialize(final byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return null;
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream objstream;
        try {
            objstream = new ObjectInputStream(in);
            Object object = objstream.readObject();
            objstream.close();
            LOG.debug("Deserialized: {}", object.toString());
            return object;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}