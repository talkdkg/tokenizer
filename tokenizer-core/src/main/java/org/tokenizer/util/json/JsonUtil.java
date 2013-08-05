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
package org.tokenizer.util.json;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class JsonUtil {

    public static JsonNode getNode(final JsonNode node, final String prop)
            throws JsonFormatException {
        if (node.get(prop) == null)
            throw new JsonFormatException("Missing required property: " + prop);
        return node.get(prop);
    }

    public static ArrayNode getArray(final JsonNode node, final String prop)
            throws JsonFormatException {
        if (node.get(prop) == null)
            throw new JsonFormatException("Missing required property: " + prop);
        if (!node.get(prop).isArray())
            throw new JsonFormatException("Not an array property: " + prop);
        return (ArrayNode) node.get(prop);
    }

    public static ArrayNode getArray(final JsonNode node, final String prop,
            final ArrayNode defaultValue) throws JsonFormatException {
        if (node.get(prop) == null)
            return defaultValue;
        if (!node.get(prop).isArray())
            throw new JsonFormatException("Not an array property: " + prop);
        return (ArrayNode) node.get(prop);
    }

    public static ObjectNode getObject(final JsonNode node, final String prop)
            throws JsonFormatException {
        if (node.get(prop) == null)
            throw new JsonFormatException("Missing required property: " + prop);
        if (!node.get(prop).isObject())
            throw new JsonFormatException("Not an object property: " + prop);
        return (ObjectNode) node.get(prop);
    }

    public static ObjectNode getObject(final JsonNode node, final String prop,
            final ObjectNode defaultValue) throws JsonFormatException {
        if (node.get(prop) == null)
            return defaultValue;
        if (!node.get(prop).isObject())
            throw new JsonFormatException("Not an object property: " + prop);
        return (ObjectNode) node.get(prop);
    }

    public static String getString(final JsonNode node, final String prop)
            throws JsonFormatException {
        if (node.get(prop) == null)
            throw new JsonFormatException("Missing required property: " + prop);
        if (!node.get(prop).isTextual())
            throw new JsonFormatException("Not a string property: " + prop);
        return node.get(prop).getTextValue();
    }

    public static String getString(final JsonNode node, final String prop,
            final String defaultValue) throws JsonFormatException {
        if (node.get(prop) == null)
            return defaultValue;
        if (!node.get(prop).isTextual())
            throw new JsonFormatException("Not a string property: " + prop);
        return node.get(prop).getTextValue();
    }

    public static boolean getBoolean(final JsonNode node, final String prop,
            final boolean defaultValue) throws JsonFormatException {
        if (node.get(prop) == null)
            return defaultValue;
        if (!node.get(prop).isBoolean())
            throw new JsonFormatException("Not a string property: " + prop);
        return node.get(prop).getBooleanValue();
    }

    public static boolean getBoolean(final JsonNode node, final String prop)
            throws JsonFormatException {
        if (node.get(prop) == null)
            throw new JsonFormatException("Missing required property: " + prop);
        if (!node.get(prop).isBoolean())
            throw new JsonFormatException("Not a string property: " + prop);
        return node.get(prop).getBooleanValue();
    }

    public static int getInt(final JsonNode node, final String prop,
            final int defaultValue) throws JsonFormatException {
        if (node.get(prop) == null)
            return defaultValue;
        if (!node.get(prop).isInt())
            throw new JsonFormatException("Not an integer property: " + prop);
        return node.get(prop).getIntValue();
    }

    public static int getInt(final JsonNode node, final String prop)
            throws JsonFormatException {
        if (node.get(prop) == null)
            throw new JsonFormatException("Missing required property: " + prop);
        if (!node.get(prop).isInt())
            throw new JsonFormatException("Not an integer property: " + prop);
        return node.get(prop).getIntValue();
    }

    public static long getLong(final JsonNode node, final String prop)
            throws JsonFormatException {
        if (node.get(prop) == null)
            throw new JsonFormatException("Missing required property: " + prop);
        if (!node.get(prop).isLong() && !node.get(prop).isInt())
            throw new JsonFormatException("Not an long property: " + prop);
        return node.get(prop).getLongValue();
    }

    public static Long getLong(final JsonNode node, final String prop,
            final Long defaultValue) throws JsonFormatException {
        if (node.get(prop) == null)
            return defaultValue;
        if (!node.get(prop).isLong() && !node.get(prop).isInt())
            throw new JsonFormatException("Not an long property: " + prop);
        return node.get(prop).getLongValue();
    }

    public static Double getDouble(final JsonNode node, final String prop,
            final Double defaultValue) throws JsonFormatException {
        if (node.get(prop) == null)
            return defaultValue;
        if (!node.get(prop).isLong() && !node.get(prop).isDouble())
            throw new JsonFormatException("Not a double property: " + prop);
        return node.get(prop).getDoubleValue();
    }

    public static byte[] getBinary(final JsonNode node, final String prop)
            throws JsonFormatException {
        if (node.get(prop) == null)
            throw new JsonFormatException("Missing required property: " + prop);
        try {
            return node.get(prop).getBinaryValue();
        } catch (IOException e) {
            throw new JsonFormatException(
                    "Error reading binary data in property " + prop, e);
        }
    }

    public static byte[] getBinary(final JsonNode node, final String prop,
            final byte[] defaultValue) throws JsonFormatException {
        if (node.get(prop) == null)
            return defaultValue;
        try {
            return node.get(prop).getBinaryValue();
        } catch (IOException e) {
            throw new JsonFormatException(
                    "Error reading binary data in property " + prop, e);
        }
    }
}
