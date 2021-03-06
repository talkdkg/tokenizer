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
package org.tokenizer.executor.model.configuration;

public class MessageParserTaskConfiguration extends AbstractTaskConfiguration {
    
    private static final long serialVersionUID = 1L;
    private String host;
    private String topicXPath;
    private String authorXPath;
    private String ageXPath;
    private String sexXPath;
    private String titleXPath;
    private String contentXPath;
    private String dateXPath;
    private String userRatingXPath;
    private String locationXPath;
    private int parseAttemptCounter = 0;
    
    public int getParseAttemptCounter() {
        return parseAttemptCounter;
    }
    
    public void setParseAttemptCounter(final int parseAttemptCounter) {
        this.parseAttemptCounter = parseAttemptCounter;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public String getTopicXPath() {
        return topicXPath;
    }
    
    public void setTopicXPath(final String topicXPath) {
        this.topicXPath = topicXPath;
    }
    
    public String getAuthorXPath() {
        return authorXPath;
    }
    
    public void setAuthorXPath(final String authorXPath) {
        this.authorXPath = authorXPath;
    }
    
    public String getAgeXPath() {
        return ageXPath;
    }
    
    public void setAgeXPath(final String ageXPath) {
        this.ageXPath = ageXPath;
    }
    
    public String getSexXPath() {
        return sexXPath;
    }
    
    public void setSexXPath(final String sexXPath) {
        this.sexXPath = sexXPath;
    }
    
    public String getTitleXPath() {
        return titleXPath;
    }
    
    public void setTitleXPath(final String titleXPath) {
        this.titleXPath = titleXPath;
    }
    
    public String getContentXPath() {
        return contentXPath;
    }
    
    public void setContentXPath(final String contentXPath) {
        this.contentXPath = contentXPath;
    }
    
    public String getDateXPath() {
        return dateXPath;
    }
    
    public void setDateXPath(final String dateXPath) {
        this.dateXPath = dateXPath;
    }
    
    public String getUserRatingXPath() {
        return userRatingXPath;
    }
    
    public void setUserRatingXPath(final String userRatingXPath) {
        this.userRatingXPath = userRatingXPath;
    }
    
    public String getLocationXPath() {
        return locationXPath;
    }
    
    public void setLocationXPath(String locationXPath) {
        this.locationXPath = locationXPath;
    }
    
    @Override
    public String getImplementationName() {
        return MessageParserTaskConfiguration.class.getSimpleName();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((ageXPath == null) ? 0 : ageXPath.hashCode());
        result = prime * result + ((authorXPath == null) ? 0 : authorXPath.hashCode());
        result = prime * result + ((contentXPath == null) ? 0 : contentXPath.hashCode());
        result = prime * result + ((dateXPath == null) ? 0 : dateXPath.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((locationXPath == null) ? 0 : locationXPath.hashCode());
        result = prime * result + parseAttemptCounter;
        result = prime * result + ((sexXPath == null) ? 0 : sexXPath.hashCode());
        result = prime * result + ((titleXPath == null) ? 0 : titleXPath.hashCode());
        result = prime * result + ((topicXPath == null) ? 0 : topicXPath.hashCode());
        result = prime * result + ((userRatingXPath == null) ? 0 : userRatingXPath.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        MessageParserTaskConfiguration other = (MessageParserTaskConfiguration) obj;
        if (ageXPath == null) {
            if (other.ageXPath != null) return false;
        } else if (!ageXPath.equals(other.ageXPath)) return false;
        if (authorXPath == null) {
            if (other.authorXPath != null) return false;
        } else if (!authorXPath.equals(other.authorXPath)) return false;
        if (contentXPath == null) {
            if (other.contentXPath != null) return false;
        } else if (!contentXPath.equals(other.contentXPath)) return false;
        if (dateXPath == null) {
            if (other.dateXPath != null) return false;
        } else if (!dateXPath.equals(other.dateXPath)) return false;
        if (host == null) {
            if (other.host != null) return false;
        } else if (!host.equals(other.host)) return false;
        if (locationXPath == null) {
            if (other.locationXPath != null) return false;
        } else if (!locationXPath.equals(other.locationXPath)) return false;
        if (parseAttemptCounter != other.parseAttemptCounter) return false;
        if (sexXPath == null) {
            if (other.sexXPath != null) return false;
        } else if (!sexXPath.equals(other.sexXPath)) return false;
        if (titleXPath == null) {
            if (other.titleXPath != null) return false;
        } else if (!titleXPath.equals(other.titleXPath)) return false;
        if (topicXPath == null) {
            if (other.topicXPath != null) return false;
        } else if (!topicXPath.equals(other.topicXPath)) return false;
        if (userRatingXPath == null) {
            if (other.userRatingXPath != null) return false;
        } else if (!userRatingXPath.equals(other.userRatingXPath)) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "MessageParserTaskConfiguration [host=" + host + ", topicXPath=" + topicXPath + ", authorXPath=" + authorXPath
                + ", ageXPath=" + ageXPath + ", sexXPath=" + sexXPath + ", titleXPath=" + titleXPath + ", contentXPath="
                + contentXPath + ", dateXPath=" + dateXPath + ", userRatingXPath=" + userRatingXPath + ", locationXPath="
                + locationXPath + ", parseAttemptCounter=" + parseAttemptCounter + "]";
    }
    
}
