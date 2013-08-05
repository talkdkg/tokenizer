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

public class Pair<T1, T2> {

    private final T1 v1;
    private final T2 v2;

    public Pair(final T1 v1, final T2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public T1 getV1() {
        return v1;
    }

    public T2 getV2() {
        return v2;
    }

    @Override
    public int hashCode() {
        int result = 17;
        if (v1 != null) {
            result = 37 * result + v1.hashCode();
        }
        if (v2 != null) {
            result = 37 * result + v2.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Pair) {
            Pair other = (Pair) obj;
            return ObjectUtils.safeEquals(v1, other.v1) && ObjectUtils.safeEquals(v2, other.v2);
        }
        return false;
    }
}
