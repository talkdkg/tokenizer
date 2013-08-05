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
package org.xaloon.core.jpa.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xaloon.core.api.keyvalue.KeyValue;

/**
 * @author vytautas r.
 */
@MappedSuperclass
public class MappableKeyValue extends BookmarkableEntity implements KeyValue<String, String> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Column(name = "KEY_", nullable = false)
    private String key;

    @Column(name = "VALUE_", nullable = false)
    private String value;

    public String getKey() {
        return key;
    }

    /**
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    /**
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean isEmpty() {
        return (getKey() == null) && (getValue() == null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MappableKeyValue)) {
            return false;
        }
        MappableKeyValue jpaKeyValue = (MappableKeyValue) obj;

        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(getKey(), jpaKeyValue.getKey());
        equalsBuilder.append(getValue(), jpaKeyValue.getValue());
        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getKey());
        hashCodeBuilder.append(getValue());
        return hashCodeBuilder.hashCode();
    }

    @Override
    public String toString() {
        return "Key: " + getKey() + ", Value: " + getValue();
    }
}
