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
package org.tokenizer.crawler.db;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.hbase.client.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: it doesn't cover ":8080" etc (port numbers)
// TODO: it covers only cases when URL starts with "http://"
public class UrlRecordDecoder {
    private static final Logger LOG = LoggerFactory
            .getLogger(UrlRecordDecoder.class);
    static byte SEPARATOR_BYTE = 0x00;
    public static final byte DOT_BYTE = 0x2e;
    static final String DOT_STRING = ".";
    static final String EMPTY_STRING = "";
    static final String CHARSET = "US-ASCII";
    static final String URL_PREFIX = "http://";
    static final String URL_PREFIX_HTTPS = "https://";
    static final String MESSAGE_HTTP = "<non-http-host>";
    static final int URL_PREFIX_LENGTH = 7;

    public static String decode(Result result) {
        try {
            byte[] rowKey = result.getRow();
            return decodeRecordIdInternal(rowKey);
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
            return EMPTY_STRING;
        }
    }

    public static String decode(byte[] urlEncoded) {
        try {
            return decodeRecordIdInternal(urlEncoded);
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
            return EMPTY_STRING;
        }
    }

    /**
     * URLs are written only with the graphic printable characters of the
     * US-ASCII coded character set. The octets 80-FF hexadecimal are not used
     * in US-ASCII, and the octets 00-1F and 7F hexadecimal represent control
     * characters; these must be encoded.
     * 
     * @param result
     * @return
     * @throws InterruptedException
     * @throws RepositoryException
     * @throws UnsupportedEncodingException
     */
    private static String decodeRecordIdInternal(byte[] rowKey)
            throws UnsupportedEncodingException {
        if (rowKey == null)
            return null;
        StringBuilder sb = new StringBuilder("http://");
        List<String> parts = new ArrayList<String>();
        String suffix = EMPTY_STRING;
        int start = 0;
        for (int i = 0; i < rowKey.length; i++) {
            if (rowKey[i] == DOT_BYTE) {
                String p = new String(rowKey, start, i - start, CHARSET);
                parts.add(p);
                start = i + 1;
            } else if (rowKey[i] == SEPARATOR_BYTE) {
                String p = new String(rowKey, start, i - start, CHARSET);
                parts.add(p);
                suffix = new String(rowKey, i + 1, rowKey.length - i - 1,
                        CHARSET);
                break;
            }
        }
        for (int i = parts.size() - 1; i >= 0; i--) {
            sb.append(parts.get(i));
            if (i > 0) {
                sb.append(DOT_STRING);
            }
        }
        sb.append(suffix);
        LOG.trace("Decoded URL: {}", sb);
        return sb.toString();
    }

    public static String getHost(String url) {
        if (url == null || url.length() == 0)
            return EMPTY_STRING;
        if (!url.startsWith(URL_PREFIX) && !url.startsWith(URL_PREFIX_HTTPS))
            return MESSAGE_HTTP;
        int end = url.indexOf('/', URL_PREFIX_LENGTH);
        end = end >= 0 ? end : url.length();
        return url.substring(URL_PREFIX_LENGTH, end);
    }

    private static List<byte[]> getHostParts(String host)
            throws UnsupportedEncodingException {
        byte[] hostBytes = host.getBytes(CHARSET);
        List<byte[]> parts = new ArrayList<byte[]>();
        int start = 0;
        for (int i = 0; i < hostBytes.length; i++) {
            if (hostBytes[i] == DOT_BYTE) {
                byte[] part = Arrays.copyOfRange(hostBytes, start, i);
                parts.add(part);
                start = i + 1;
            }
        }
        byte[] part = Arrays.copyOfRange(hostBytes, start, hostBytes.length);
        parts.add(part);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Uninverted: {}", arraysToString(parts));
        }
        return parts;
    }

    private static String arrayToString(byte[] array) {
        try {
            return new String(array, CHARSET);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static String arraysToString(List<byte[]> arrays) {
        StringBuffer sb = new StringBuffer();
        for (byte[] array : arrays) {
            sb.append(arrayToString(array)).append(" ");
        }
        return sb.toString();
    }

    public static List<byte[]> getHostPartsInverted(String host)
            throws UnsupportedEncodingException {
        List<byte[]> parts = getHostParts(host);
        List<byte[]> partsInverted = new ArrayList<byte[]>();
        for (int i = parts.size() - 1; i >= 0; i--) {
            partsInverted.add(parts.get(i));
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Inverted: {}", arraysToString(partsInverted));
        }
        return partsInverted;
    }

    public static final byte[] encode(String url) {
        try {
            return encodeInternal(url);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static final byte[] encodeInternal(String url)
            throws UnsupportedEncodingException {
        if (url == null || url.length() == 0 || !url.startsWith(URL_PREFIX))
            return null;
        String host = getHost(url);
        String suffix = url.substring(URL_PREFIX_LENGTH + host.length());
        int arrayLength = url.length() - URL_PREFIX_LENGTH + 1;
        byte[] encoded = new byte[arrayLength];
        List<byte[]> parts = getHostPartsInverted(host);
        int i = 0;
        for (byte[] part : parts) {
            for (int j = 0; j < part.length; j++) {
                encoded[i + j] = part[j];
            }
            i = i + part.length;
            if (i == host.length()) {
                break;
            }
            encoded[i] = DOT_BYTE;
            i++;
        }
        encoded[i] = SEPARATOR_BYTE;
        byte[] suffixBytes = suffix.getBytes(CHARSET);
        i = i + 1;
        for (int s = 0; s < suffixBytes.length; s++) {
            encoded[i + s] = suffixBytes[s];
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Encoded URL: {}", arrayToString(encoded));
        }
        return encoded;
    }
}
