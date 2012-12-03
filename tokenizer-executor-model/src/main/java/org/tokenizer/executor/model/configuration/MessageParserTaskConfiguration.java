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
package org.tokenizer.executor.model.configuration;

public class MessageParserTaskConfiguration extends TaskConfiguration {
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTopicXPath() {
        return topicXPath;
    }

    public void setTopicXPath(String topicXPath) {
        this.topicXPath = topicXPath;
    }

    public String getAuthorXPath() {
        return authorXPath;
    }

    public void setAuthorXPath(String authorXPath) {
        this.authorXPath = authorXPath;
    }

    public String getAgeXPath() {
        return ageXPath;
    }

    public void setAgeXPath(String ageXPath) {
        this.ageXPath = ageXPath;
    }

    public String getSexXPath() {
        return sexXPath;
    }

    public void setSexXPath(String sexXPath) {
        this.sexXPath = sexXPath;
    }

    public String getTitleXPath() {
        return titleXPath;
    }

    public void setTitleXPath(String titleXPath) {
        this.titleXPath = titleXPath;
    }

    public String getContentXPath() {
        return contentXPath;
    }

    public void setContentXPath(String contentXPath) {
        this.contentXPath = contentXPath;
    }

    public String getDateXPath() {
        return dateXPath;
    }

    public void setDateXPath(String dateXPath) {
        this.dateXPath = dateXPath;
    }

    public String getUserRatingXPath() {
        return userRatingXPath;
    }

    public void setUserRatingXPath(String userRatingXPath) {
        this.userRatingXPath = userRatingXPath;
    }
}
