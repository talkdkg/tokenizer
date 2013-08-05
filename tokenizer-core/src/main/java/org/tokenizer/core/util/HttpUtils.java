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
package org.tokenizer.core.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tika.mime.MediaType;

public class HttpUtils {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(HttpUtils.class);
    public static String DEFAULT_CHARSET = "UTF-8";
    public static final String URL_PREFIX = "http://";
    public static final String URL_PREFIX_HTTPS = "https://";
    public static final int URL_PREFIX_LENGTH = 7;
    public static final String EMPTY_STRING = "";
    public static final String ASCII_CHARSET = "US-ASCII";
    public static final byte DOT_BYTE = 0x2e;
    static final String DOT_STRING = ".";

    public static String getMimeTypeFromContentType(final String contentType) {
        String result = "";
        MediaType mt = MediaType.parse(contentType);
        if (mt != null) {
            result = mt.getType() + "/" + mt.getSubtype();
        }
        return result;
    }

    public static String getCharsetFromContentType(final String contentType) {
        String result = DEFAULT_CHARSET;
        MediaType mt = MediaType.parse(contentType);
        if (mt != null) {
            String charset = mt.getParameters().get("charset");
            if (charset != null) {
                result = charset;
            }
        }
        return result;
    }

    // TODO: it doesn't cover ":8080" etc (port numbers)
    // TODO: it covers only cases when URL starts with "http://"
    @Deprecated
    public static String getHostSimplified(final String url) {
        if (url == null || url.length() == 0)
            return EMPTY_STRING;
        if (!url.startsWith(URL_PREFIX) && !url.startsWith(URL_PREFIX_HTTPS))
            return EMPTY_STRING;
        int end = url.indexOf('/', URL_PREFIX_LENGTH);
        end = end >= 0 ? end : url.length();
        return url.substring(URL_PREFIX_LENGTH, end);
    }

    public static String getHost(final String url) {
        try {
            URL url2 = new URL(url);
            return url2.getHost();
        } catch (MalformedURLException e) {
            LOG.warn("Malformed URL: {}", url);
            return EMPTY_STRING;
        }
    }

    public static void main(final String[] args) {
        String host = "www.tokenizer.com";
        List<byte[]> parts = getHostPartsInverted(host);
        for (byte[] part : parts) {
            System.out.println(new String(part));
        }
        System.out.println(new String(getHostInverted("www.tokenizer.com")));
    }

    public static byte[] getHostInverted(final String host) {
        if (host == null)
            return null;
        byte[] inverted = new byte[host.length()];
        List<byte[]> parts = getHostPartsInverted(host);
        int i = 0;
        for (byte[] part : parts) {
            for (int j = 0; j < part.length; j++) {
                inverted[i + j] = part[j];
            }
            i = i + part.length;
            if (i < inverted.length) {
                inverted[i] = DOT_BYTE;
            }
            i++;
        }
        return inverted;
    }

    public static String getHostUninverted(final byte[] hostInverted) {
        String hostInverted2;
        try {
            hostInverted2 = new String(hostInverted, ASCII_CHARSET);
            byte[] host = getHostInverted(hostInverted2);
            String host2 = new String(host, ASCII_CHARSET);
            return host2;
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
        }
        return null;
    }

    // public static String getHost(final byte[] hostInverted) {
    // try {
    // String hostInvertedString = new String(hostInverted, ASCII_CHARSET);
    // byte[] hostInvertedInverted = getHostInverted(hostInvertedString);
    // return new String(hostInvertedInverted, ASCII_CHARSET);
    // } catch (UnsupportedEncodingException e) {
    // LOG.error("", e);
    // return null;
    // }
    // }
    private static List<byte[]> getHostPartsInverted(final String host) {
        List<byte[]> parts = getHostParts(host);
        List<byte[]> partsInverted = new ArrayList<byte[]>();
        for (int i = parts.size() - 1; i >= 0; i--) {
            partsInverted.add(parts.get(i));
        }
        return partsInverted;
    }

    private static List<byte[]> getHostParts(final String host) {
        byte[] hostBytes;
        try {
            hostBytes = host.getBytes(ASCII_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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
        return parts;
    }

    private static String arrayToString(final byte[] array) {
        try {
            return new String(array, ASCII_CHARSET);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static String arraysToString(final List<byte[]> arrays) {
        StringBuffer sb = new StringBuffer();
        for (byte[] array : arrays) {
            sb.append(arrayToString(array)).append(" ");
        }
        return sb.toString();
    }

    public static byte[] intToBytes(final int i) {
        return new byte[] { (byte) (i >>> 24), (byte) (i >>> 16),
                (byte) (i >>> 8), (byte) i };
    }

    public static int bytesToInt(final byte[] b) {
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16
                | (b[0] & 0xFF) << 24;
    }
}
