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

public class ByteArrayKey {

    private final byte[] key;
    private final int hash;

    public ByteArrayKey(final byte[] key) {
        this.key = Arrays.copyOf(key, key.length);
        this.hash = Arrays.hashCode(key);
    }

    public byte[] getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ByteArrayKey other = (ByteArrayKey) obj;
        return Arrays.equals(key, other.key);
    }
}
