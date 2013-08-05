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
package org.tokenizer.executor.engine.twitter.db;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

@Entity
@Table(name = "Status")
@Deprecated
public class StatusVO implements Status {

    private static final long serialVersionUID = 1L;
    @Id
    private long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    // @Column(columnDefinition = "VARCHAR(1024)")
    private String text;
    private String source;
    private boolean isTruncated;
    private long inReplyToStatusId;
    private long inReplyToUserId;
    private boolean isFavorited;
    private String inReplyToScreenName;
    private GeoLocation geoLocation = null;
    private Place place = null;
    private long retweetCount;
    private boolean wasRetweetedByMe;
    private long[] contributorsIDs;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private StatusVO retweetedStatus;
    private UserMentionEntity[] userMentionEntities;
    private URLEntity[] urlEntities;
    private HashtagEntity[] hashtagEntities;
    private MediaEntity[] mediaEntities;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private UserVO user = null;

    public StatusVO() {
    }

    public StatusVO(final Status status) {
        this.id = status.getId();
        this.createdAt = status.getCreatedAt();
        this.text = status.getText();
        this.source = status.getSource();
        this.isTruncated = status.isTruncated();
        this.inReplyToStatusId = status.getInReplyToStatusId();
        this.inReplyToUserId = status.getInReplyToUserId();
        this.isFavorited = status.isFavorited();
        this.inReplyToScreenName = status.getInReplyToScreenName();
        this.geoLocation = status.getGeoLocation();
        this.place = status.getPlace();
        this.retweetCount = status.getRetweetCount();
        this.wasRetweetedByMe = status.isRetweetedByMe();
        this.contributorsIDs = status.getContributors();
        if (status.getRetweetedStatus() != null) {
            this.retweetedStatus = new StatusVO(status.getRetweetedStatus());
        }
        this.userMentionEntities = status.getUserMentionEntities();
        this.urlEntities = status.getURLEntities();
        this.hashtagEntities = status.getHashtagEntities();
        this.mediaEntities = status.getMediaEntities();
        this.user = new UserVO(status.getUser());
    }

    @Override
    public Date getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public boolean isTruncated() {
        return isTruncated;
    }

    @Override
    public long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    @Override
    public long getInReplyToUserId() {
        return inReplyToUserId;
    }

    @Override
    public String getInReplyToScreenName() {
        return inReplyToScreenName;
    }

    @Override
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    @Override
    public Place getPlace() {
        return place;
    }

    @Override
    public long[] getContributors() {
        return contributorsIDs;
    }

    @Override
    public boolean isFavorited() {
        return isFavorited;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public boolean isRetweet() {
        return retweetedStatus != null;
    }

    @Override
    public StatusVO getRetweetedStatus() {
        return retweetedStatus;
    }

    @Override
    public long getRetweetCount() {
        return retweetCount;
    }

    @Override
    public boolean isRetweetedByMe() {
        return wasRetweetedByMe;
    }

    @Override
    public UserMentionEntity[] getUserMentionEntities() {
        return userMentionEntities;
    }

    @Override
    public URLEntity[] getURLEntities() {
        return urlEntities;
    }

    @Override
    public HashtagEntity[] getHashtagEntities() {
        return hashtagEntities;
    }

    @Override
    public MediaEntity[] getMediaEntities() {
        return mediaEntities;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public boolean equals(final Object obj) {
        if (null == obj)
            return false;
        if (this == obj)
            return true;
        return obj instanceof Status && ((Status) obj).getId() == this.id;
    }

    @Override
    public int compareTo(final Status o) {
        long delta = this.id - o.getId();
        if (delta < Integer.MIN_VALUE)
            return Integer.MIN_VALUE;
        else if (delta > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        return (int) delta;
    }

    @Override
    public RateLimitStatus getRateLimitStatus() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int getAccessLevel() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String toString() {
        return "StatusVO [id=" + id + ", createdAt=" + createdAt + ", text="
                + text + ", source=" + source + ", isTruncated=" + isTruncated
                + ", inReplyToStatusId=" + inReplyToStatusId
                + ", inReplyToUserId=" + inReplyToUserId + ", isFavorited="
                + isFavorited + ", inReplyToScreenName=" + inReplyToScreenName
                + ", geoLocation=" + geoLocation + ", place=" + place
                + ", retweetCount=" + retweetCount + ", wasRetweetedByMe="
                + wasRetweetedByMe + ", contributorsIDs="
                + Arrays.toString(contributorsIDs) + ", retweetedStatus="
                + retweetedStatus + ", userMentionEntities="
                + Arrays.toString(userMentionEntities) + ", urlEntities="
                + Arrays.toString(urlEntities) + ", hashtagEntities="
                + Arrays.toString(hashtagEntities) + ", mediaEntities="
                + Arrays.toString(mediaEntities) + ", user=" + user + "]";
    }

    @Override
    public long getCurrentUserRetweetId() {
        return 0;
    }

    @Override
    public boolean isPossiblySensitive() {
        return false;
    }
}
