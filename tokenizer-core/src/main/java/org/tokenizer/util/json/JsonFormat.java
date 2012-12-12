/*
 * Copyright 2012 Tokenizer Inc.
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
package org.tokenizer.util.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Json serialization & deserialization to/from Jackson's generic tree model.
 */
public class JsonFormat {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final JsonFactory JSON_FACTORY = OBJECT_MAPPER
            .getJsonFactory();
    public static final MappingJsonFactory JSON_FACTORY_NON_STD;
    static {
        JSON_FACTORY_NON_STD = new MappingJsonFactory();
        JSON_FACTORY_NON_STD.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        JSON_FACTORY_NON_STD.configure(
                JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        JSON_FACTORY_NON_STD
                .getCodec()
                .getDeserializationConfig()
                .enable(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS);
    }

    public static void serialize(final JsonNode jsonNode,
            final OutputStream outputStream) throws IOException {
        OBJECT_MAPPER.writeValue(outputStream, jsonNode);
    }

    public static byte[] serializeAsBytes(final JsonNode jsonNode)
            throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(jsonNode);
    }

    public static String serializeAsString(final JsonNode jsonNode)
            throws IOException {
        return OBJECT_MAPPER.writeValueAsString(jsonNode);
    }

    /**
     * Variant of serialize that converts the IOException to a RuntimeException.
     * 
     * @param what
     *            a label for what is being serialized, used in the error
     *            message
     */
    public static byte[] serializeAsBytesSoft(final JsonNode jsonNode,
            final String what) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(jsonNode);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing " + what
                    + " to JSON.", e);
        }
    }

    public static JsonNode deserialize(final InputStream inputStream)
            throws IOException {
        JsonParser jp = JSON_FACTORY.createJsonParser(inputStream);
        return jp.readValueAsTree();
    }

    public static JsonNode deserializeNonStd(final InputStream inputStream)
            throws IOException {
        JsonParser jp = JSON_FACTORY_NON_STD.createJsonParser(inputStream);
        return jp.readValueAsTree();
    }

    public static JsonNode deserialize(final byte[] data) throws IOException {
        JsonParser jp = JSON_FACTORY.createJsonParser(data);
        return jp.readValueAsTree();
    }

    public static JsonNode deserialize(final String data) throws IOException {
        JsonParser jp = JSON_FACTORY.createJsonParser(data);
        return jp.readValueAsTree();
    }

    /**
     * Variant of deserialize that converts the IOException to a
     * RuntimeException.
     * 
     * @param what
     *            a label for what is being deserialized, used in the error
     *            message
     */
    public static JsonNode deserializeSoft(final byte[] data, final String what) {
        try {
            JsonParser jp = JSON_FACTORY.createJsonParser(data);
            return jp.readValueAsTree();
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing " + what
                    + " from JSON.", e);
        }
    }

    public static JsonNode deserializeNonStd(final byte[] data)
            throws IOException {
        JsonParser jp = JSON_FACTORY_NON_STD.createJsonParser(data);
        return jp.readValueAsTree();
    }

    public static JsonNode deserializeNonStd(final String data)
            throws IOException {
        JsonParser jp = JSON_FACTORY_NON_STD.createJsonParser(data);
        return jp.readValueAsTree();
    }
}
