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
