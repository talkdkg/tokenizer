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
package org.tokenizer.executor.engine.twitter.db;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
public class StatusVO implements Status {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    //@Column(columnDefinition = "VARCHAR(1024)")
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
    
    @ManyToOne(cascade=CascadeType.PERSIST)
    private StatusVO retweetedStatus;
    
    private UserMentionEntity[] userMentionEntities;
    private URLEntity[] urlEntities;
    private HashtagEntity[] hashtagEntities;
    private MediaEntity[] mediaEntities;
    
    @ManyToOne(cascade=CascadeType.PERSIST)
    private UserVO user = null;



    public StatusVO() {}
    
    public StatusVO(Status status) {
        
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
        if (status.getRetweetedStatus() != null) this.retweetedStatus = new StatusVO(status.getRetweetedStatus());
        this.userMentionEntities = status.getUserMentionEntities();
        this.urlEntities = status.getURLEntities();
        this.hashtagEntities = status.getHashtagEntities();
        this.mediaEntities = status.getMediaEntities();
        this.user = new UserVO(status.getUser());
    }
    

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public long getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public String getSource() {
        return this.source;
    }

    public boolean isTruncated() {
        return isTruncated;
    }

    public long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    public long getInReplyToUserId() {
        return inReplyToUserId;
    }

    public String getInReplyToScreenName() {
        return inReplyToScreenName;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public Place getPlace() {
        return place;
    }

    public long[] getContributors() {
        return contributorsIDs;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public User getUser() {
        return user;
    }

    public boolean isRetweet() {
        return retweetedStatus != null;
    }

    public StatusVO getRetweetedStatus() {
        return retweetedStatus;
    }

    public long getRetweetCount() {
        return retweetCount;
    }

    public boolean isRetweetedByMe() {
        return wasRetweetedByMe;
    }

    public UserMentionEntity[] getUserMentionEntities() {
        return userMentionEntities;
    }

    public URLEntity[] getURLEntities() {
        return urlEntities;
    }

    public HashtagEntity[] getHashtagEntities() {
        return hashtagEntities;
    }

    public MediaEntity[] getMediaEntities() {
        return mediaEntities;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return obj instanceof Status && ((Status) obj).getId() == this.id;
    }

    @Override
    public int compareTo(Status o) {
        long delta = this.id - o.getId();
        if (delta < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        } else if (delta > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
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


}
