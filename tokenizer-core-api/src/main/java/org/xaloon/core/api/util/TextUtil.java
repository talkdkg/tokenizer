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
 * Simple text utility
 * 
 * @author vytautas r.
 * @version 1.1, 09/28/10
 * @since 1.3
 */
public class TextUtil {

    /**
     * Parses class name into readable value
     * 
     * @param identifier
     *            string value which will be used to parse readable value
     * @return readable string value
     */
    public static String parseName(String identifier) {
        // Check for a complex property.
        int idx = identifier.lastIndexOf('.');
        if (idx < 0) {
            idx = identifier.lastIndexOf('$'); // Java nested classes.
        }

        if (idx >= 0 && identifier.length() > 1) {
            identifier = identifier.substring(idx + 1);
        }

        if (identifier.length() == 0) {
            return DelimiterEnum.EMPTY.value();
        }

        char[] chars = identifier.toCharArray();
        StringBuilder buf = new StringBuilder(chars.length + 10);

        buf.append(chars[0]);
        boolean lastLower = false;
        for (int i = 1; i < chars.length; ++i) {
            if (!Character.isLowerCase(chars[i])) {
                // Lower to upper case transition -- add space before it
                if (lastLower) {
                    buf.append(' ');
                }
            }

            buf.append(chars[i]);
            lastLower = Character.isLowerCase(chars[i]) || Character.isDigit(chars[i]);
        }
        String result = buf.toString().toLowerCase();
        if (result.lastIndexOf(DelimiterEnum.SPACE.value()) > 0) {
            result = result.substring(0, result.lastIndexOf(DelimiterEnum.SPACE.value()));
        }
        return result;
    }

    /**
     * @param value
     * @return replaces new lines into html new lines
     */
    public static String prepareStringForHTML(String value) {
        if (!StringUtils.isEmpty(value)) {
            return value.replaceAll("\n", HtmlElementEnum.BR.value());
        }
        return value;
    }
}
