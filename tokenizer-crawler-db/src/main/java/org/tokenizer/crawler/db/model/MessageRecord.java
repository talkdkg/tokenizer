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
package org.tokenizer.crawler.db.model;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.tokenizer.core.util.MD5;
import org.tokenizer.crawler.db.DefaultValues;

public class MessageRecord implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(UrlRecord.class);
    
    private byte[] digest = DefaultValues.EMPTY_ARRAY;
    private String host = DefaultValues.EMPTY_STRING;
    private String topic = DefaultValues.EMPTY_STRING;
    private String date = DefaultValues.EMPTY_STRING;
    private String author = DefaultValues.EMPTY_STRING;
    private String age = DefaultValues.EMPTY_STRING;
    private String sex = DefaultValues.EMPTY_STRING;
    private String title = DefaultValues.EMPTY_STRING;
    private String content = DefaultValues.EMPTY_STRING;
    private String userRating = DefaultValues.EMPTY_STRING;
    private String location = DefaultValues.EMPTY_STRING;
    
    private static final DateTimeFormatter fmt = DateTimeFormat
            .forPattern("MMMM d, yyyy");
    
    private static final DateTimeFormatter TWITTER_DATE_FORMATTER = DateTimeFormat
            .forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    public static void main(final String[] args) {
        
        String test = "2013-02-20T02:00:53Z";
        TWITTER_DATE_FORMATTER.parseDateTime(test);
        
    }
    
    public MessageRecord(final byte[] digest, final String author,
            final String title, final String content) {
        this.digest = digest;
        if (author != null) {
            this.author = author;
        }
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
    }
    
    public MessageRecord(final byte[] digest, final String host,
            final String topic, final String date, final String author,
            final String age, final String sex, final String title,
            final String content, final String userRating, final String location) {
        this.digest = digest;
        if (host != null) this.host = host;
        if (topic != null) this.topic = topic;
        if (date != null) this.date = date;
        if (author != null) this.author = author;
        if (age != null) this.age = age;
        if (sex != null) this.sex = sex;
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (userRating != null) this.userRating = userRating;
        if (location != null) this.location = location;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(final String host) {
        if (host != null) {
            this.host = host;
        }
    }
    
    public String getTopic() {
        return topic;
    }
    
    public void setTopic(final String topic) {
        if (topic != null) {
            this.topic = topic;
        }
    }
    
    public String getDate() {
        return date;
    }
    
    public String getISO8601Date() {
        
        try {
            DateTime dt = TWITTER_DATE_FORMATTER.parseDateTime(date);
            return TWITTER_DATE_FORMATTER.print(dt);
            
        } catch (Exception e) {
            
            try {
                DateTime dt = fmt.parseDateTime(date);
                 return TWITTER_DATE_FORMATTER.print(dt);
            } catch (IllegalArgumentException e2) {
                LOG.error("Can't parse date: {}", date);
            }
            
        }
        return TWITTER_DATE_FORMATTER.print(new DateTime(0));
        
    }
    
    public void setDate(final String date) {
        if (date != null) {
            this.date = date;
        }
    }
    
    public String getAge() {
        return age;
    }
    
    public void setAge(final String age) {
        if (age != null) {
            this.age = age;
        }
    }
    
    public String getSex() {
        return sex;
    }
    
    public void setSex(final String sex) {
        if (sex != null) {
            this.sex = sex;
        }
    }
    
    public String getUserRating() {
        return userRating;
    }
    
    public void setUserRating(final String userRating) {
        if (userRating != null) {
            this.userRating = userRating;
        }
    }
    
    public byte[] getDigest() {
        return digest;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        if (location != null) this.location = location;
    }
    
    @Override
    public String toString() {
        return "MessageRecord [digest=" + MD5.toHexString(digest) + ", host="
                + host + ", topic=" + topic + ", date=" + date + ", author="
                + author + ", age=" + age + ", sex=" + sex + ", title=" + title
                + ", content=" + content + ", userRating=" + userRating
                + ", location=" + location + "]";
    }
}
