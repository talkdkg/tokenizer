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

public class ClassicRobotTaskConfiguration extends TaskConfiguration {
    private static final long serialVersionUID = 1L;
    private String host;
    private boolean followRedirects;
    private boolean followExternal;
    private String agentName = "Tokenizer";
    private String emailAddress = "info@tokenizer.ca";
    private String webAddress = "http://www.tokenizer.org";

    @Override
    public String toString() {
        return "ClassicRobotTaskConfiguration [host=" + host
                + ", followRedirects=" + followRedirects + ", followExternal="
                + followExternal + ", agentName=" + agentName
                + ", emailAddress=" + emailAddress + ", webAddress="
                + webAddress + "]";
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public boolean isFollowExternal() {
        return followExternal;
    }

    public void setFollowExternal(boolean followExternal) {
        this.followExternal = followExternal;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }
}
