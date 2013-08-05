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

import java.util.Arrays;

public class ObjectUtils {

    public static boolean safeEquals(final Object obj1, final Object obj2) {
        return obj1 == null && obj2 == null || !(obj1 == null || obj2 == null)
                && obj1.equals(obj2);
    }

    public static boolean safeEquals(final Object[] obj1, final Object[] obj2) {
        return obj1 == null && obj2 == null || !(obj1 == null || obj2 == null)
                && Arrays.equals(obj1, obj2);
    }

    public static boolean safeEquals(final byte[] obj1, final byte[] obj2) {
        return obj1 == null && obj2 == null || !(obj1 == null || obj2 == null)
                && Arrays.equals(obj1, obj2);
    }
}
