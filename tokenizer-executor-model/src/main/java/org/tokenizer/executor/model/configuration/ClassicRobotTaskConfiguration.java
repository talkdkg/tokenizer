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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((agentName == null) ? 0 : agentName.hashCode());
        result = prime * result
                + ((emailAddress == null) ? 0 : emailAddress.hashCode());
        result = prime * result + (followExternal ? 1231 : 1237);
        result = prime * result + (followRedirects ? 1231 : 1237);
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result
                + ((webAddress == null) ? 0 : webAddress.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClassicRobotTaskConfiguration other = (ClassicRobotTaskConfiguration) obj;
        if (agentName == null) {
            if (other.agentName != null)
                return false;
        } else if (!agentName.equals(other.agentName))
            return false;
        if (emailAddress == null) {
            if (other.emailAddress != null)
                return false;
        } else if (!emailAddress.equals(other.emailAddress))
            return false;
        if (followExternal != other.followExternal)
            return false;
        if (followRedirects != other.followRedirects)
            return false;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (webAddress == null) {
            if (other.webAddress != null)
                return false;
        } else if (!webAddress.equals(other.webAddress))
            return false;
        return true;
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
