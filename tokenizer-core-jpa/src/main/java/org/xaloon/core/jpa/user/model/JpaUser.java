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
package org.xaloon.core.jpa.user.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.xaloon.core.api.keyvalue.KeyValue;
import org.xaloon.core.api.path.DelimiterEnum;
import org.xaloon.core.api.storage.FileDescriptor;
import org.xaloon.core.jpa.storage.model.JpaFileDescriptor;

/**
 * @author vytautas r.
 */
@Cacheable
@Entity
@DiscriminatorValue("2")
@Table(name = "XAL_USER", uniqueConstraints = { @UniqueConstraint(columnNames = { "USERNAME" }),
        @UniqueConstraint(columnNames = { "EMAIL" }) })
public class JpaUser extends AbstractUser {
    private static final long serialVersionUID = 1L;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "TIMEZONE")
    private String timezone;

    @Column(name = "SIGNATURE", length = 4000)
    private String signature;

    @Column(name = "IS_EXTERNAL", nullable = false)
    private boolean external;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "PHOTO_THUMBNAIL_ID", referencedColumnName = "ID")
    private JpaFileDescriptor photoThumbnail;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user", orphanRemoval = true)
    private List<JpaUserProperties> properties = new ArrayList<JpaUserProperties>();

    private transient String displayName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public FileDescriptor getPhotoThumbnail() {
        return photoThumbnail;
    }

    /**
     * @param photoThumbnail
     */
    public void setPhotoThumbnail(JpaFileDescriptor photoThumbnail) {
        this.photoThumbnail = photoThumbnail;
    }

    public String getDisplayName() {
        if (!StringUtils.isEmpty(displayName)) {
            return displayName;
        }
        StringBuilder result = new StringBuilder();
        if (!StringUtils.isEmpty(firstName)) {
            result.append(firstName);
        }
        if (!StringUtils.isEmpty(lastName)) {
            result.append(DelimiterEnum.SPACE.value());
            result.append(lastName);
        }
        if (result.length() > 1) {
            displayName = result.toString().trim();
            return displayName;
        }
        else {
            return username;
        }

    }

    @Override
    public void setPhotoThumbnail(FileDescriptor photoThumbnail) {
        this.photoThumbnail = (JpaFileDescriptor) photoThumbnail;
    }

    /**
     * Gets properties.
     * 
     * @return properties
     */
    public List<JpaUserProperties> getProperties() {
        return properties;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setProperties(List<? extends KeyValue<String, String>> properties) {
        this.properties = (List<JpaUserProperties>) properties;
    }

    @Override
    public String toString() {
        return String.format("[%s] username=%s, firstName=%s, lastName=%s, email=%s, external=%b", this.getClass()
                .getSimpleName(), getUsername(), getFirstName(), getLastName(), getEmail(), isExternal());
    }
}
