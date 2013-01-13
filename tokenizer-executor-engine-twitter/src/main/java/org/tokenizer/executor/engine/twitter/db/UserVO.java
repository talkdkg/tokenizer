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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

@Entity
@Table(name = "User")
public class UserVO implements User {

    private static final long serialVersionUID = 1L;
    @Id
    private long id;
    private String name;
    private String screenName;
    private String location;
    private String description;
    private boolean isContributorsEnabled;
    private String profileImageUrl;
    private String profileImageUrlHttps;
    private String url;
    private boolean isProtected;
    private int followersCount;
    // @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    // private StatusVO statusVO;
    private String profileBackgroundColor;
    private String profileTextColor;
    private String profileLinkColor;
    private String profileSidebarFillColor;
    private String profileSidebarBorderColor;
    private boolean profileUseBackgroundImage;
    private boolean showAllInlineMedia;
    private int friendsCount;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    private int favouritesCount;
    private int utcOffset;
    private String timeZone;
    private String profileBackgroundImageUrl;
    private String profileBackgroundImageUrlHttps;
    private boolean profileBackgroundTiled;
    private String lang;
    private int statusesCount;
    private boolean isGeoEnabled;
    private boolean isVerified;
    private boolean translator;
    private int listedCount;
    private boolean isFollowRequestSent;

    public UserVO() {
    }

    public UserVO(final User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.screenName = user.getScreenName();
        this.location = user.getLocation();
        this.description = user.getDescription();
        this.isContributorsEnabled = user.isContributorsEnabled();
        if (user.getProfileImageURL() != null) {
            this.profileImageUrl = user.getProfileImageURL().toString();
        }
        if (user.getProfileImageUrlHttps() != null) {
            this.profileImageUrlHttps = user.getProfileImageUrlHttps()
                    .toString();
        }
        if (user.getURL() != null) {
            this.url = user.getURL().toString();
        }
        this.isProtected = user.isProtected();
        this.followersCount = user.getFollowersCount();
        // this.statusVO = new StatusVO(user.getStatus());
        this.profileBackgroundColor = user.getProfileBackgroundColor();
        this.profileTextColor = user.getProfileTextColor();
        this.profileLinkColor = user.getProfileLinkColor();
        this.profileSidebarFillColor = user.getProfileSidebarFillColor();
        this.profileSidebarBorderColor = user.getProfileSidebarBorderColor();
        this.profileUseBackgroundImage = user.isProfileUseBackgroundImage();
        this.showAllInlineMedia = user.isShowAllInlineMedia();
        this.friendsCount = user.getFriendsCount();
        this.createdAt = user.getCreatedAt();
        this.favouritesCount = user.getFavouritesCount();
        this.utcOffset = user.getUtcOffset();
        this.timeZone = user.getTimeZone();
        this.profileBackgroundImageUrl = user.getProfileBackgroundImageUrl();
        this.profileBackgroundImageUrlHttps = user
                .getProfileBackgroundImageUrlHttps();
        this.profileBackgroundTiled = user.isProfileBackgroundTiled();
        this.lang = user.getLang();
        this.statusesCount = user.getStatusesCount();
        this.isGeoEnabled = user.isGeoEnabled();
        this.isVerified = user.isVerified();
        this.translator = user.isTranslator();
        this.listedCount = user.getListedCount();
        this.isFollowRequestSent = user.isFollowRequestSent();
    }

    @Override
    public int compareTo(final User that) {
        return (int) (this.id - that.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getScreenName() {
        return screenName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocation() {
        return location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContributorsEnabled() {
        return isContributorsEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfileImageURL() {
        return profileImageUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getProfileImageUrlHttps() {
        if (null == profileImageUrlHttps)
            return null;
        try {
            return new URL(profileImageUrlHttps);
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getURL() {
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProtected() {
        return isProtected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFollowersCount() {
        return followersCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfileBackgroundColor() {
        return profileBackgroundColor;
    }

    @Override
    public String getProfileTextColor() {
        return profileTextColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfileLinkColor() {
        return profileLinkColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfileSidebarFillColor() {
        return profileSidebarFillColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfileSidebarBorderColor() {
        return profileSidebarBorderColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProfileUseBackgroundImage() {
        return profileUseBackgroundImage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShowAllInlineMedia() {
        return showAllInlineMedia;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFriendsCount() {
        return friendsCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Status getStatus() {
        // return statusVO;
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFavouritesCount() {
        return favouritesCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUtcOffset() {
        return utcOffset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfileBackgroundImageUrl() {
        return profileBackgroundImageUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfileBackgroundImageUrlHttps() {
        return profileBackgroundImageUrlHttps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProfileBackgroundTiled() {
        return profileBackgroundTiled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLang() {
        return lang;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatusesCount() {
        return statusesCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGeoEnabled() {
        return isGeoEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVerified() {
        return isVerified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTranslator() {
        return translator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getListedCount() {
        return listedCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFollowRequestSent() {
        return isFollowRequestSent;
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
        return obj instanceof User && ((User) obj).getId() == this.id;
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
    public String getBiggerProfileImageURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMiniProfileImageURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getOriginalProfileImageURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileImageURLHttps() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getBiggerProfileImageURLHttps() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMiniProfileImageURLHttps() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getOriginalProfileImageURLHttps() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBackgroundImageURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBannerURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBannerRetinaURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBannerIPadURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBannerIPadRetinaURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBannerMobileURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBannerMobileRetinaURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URLEntity[] getDescriptionURLEntities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URLEntity getURLEntity() {
        // TODO Auto-generated method stub
        return null;
    }
}