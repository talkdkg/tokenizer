/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright © 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
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
