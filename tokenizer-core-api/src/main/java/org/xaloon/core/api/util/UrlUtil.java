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
package org.xaloon.core.api.util;

import org.apache.commons.lang.StringUtils;
import org.xaloon.core.api.path.DelimiterEnum;

/**
 * Simple url utility class, which helps to construct URL representation in browser
 * 
 * @author vytautas r.
 */
public class UrlUtil {
    private static final String REGEX_DELIM_UNDER = "-";

    /**
     * Checks if context and value have duplication of DELIMITER
     * 
     * @param context
     *            string which will be added to the beginning of value
     * @param value
     *            string which will be added after context
     * @return concatenated string, for example if we have context="context" and value="/value" then result would be
     *         "context/value"
     */
    public static String mergeIntoUrl(String context, String value) {
        if (StringUtils.isEmpty(context)) {
            return value;
        }
        if (StringUtils.isEmpty(value)) {
            return context;
        }
        if (context.endsWith(DelimiterEnum.SLASH.value()) && value.startsWith(DelimiterEnum.SLASH.value())) {
            return context + value.substring(1);
        }
        else if (!context.endsWith(DelimiterEnum.SLASH.value()) && !value.startsWith(DelimiterEnum.SLASH.value())) {
            return context + DelimiterEnum.SLASH.value() + value;
        }
        else {
            return context + value;
        }
    }

    /**
     * Transliterates provided string and removes non-characters
     * 
     * @param value
     *            string value to transliterate and remove non-characters
     * @return string representation of encoded value
     */
    public static String encode(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        value = value.trim();
        value = TransliterateUtils.transliterate(value);
        value = value.replaceAll("[^a-zA-Z0-9\\s-]", DelimiterEnum.EMPTY.value());
        value = value.replaceAll(DelimiterEnum.SPACE.value(), REGEX_DELIM_UNDER);
        return value.replaceAll("---", "-");
    }
}
