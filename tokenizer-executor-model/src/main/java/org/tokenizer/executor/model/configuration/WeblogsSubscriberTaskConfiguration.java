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

public class WeblogsSubscriberTaskConfiguration extends TaskConfiguration {

    private static final long serialVersionUID = 1L;

    private String url = "";
    private int delay = 300000;

    private String agentName = "Tokenizer";
    private String emailAddress = "info@tokenizer.ca";
    private String webAddress = "http://www.tokenizer.ca";

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(final String agentName) {
        this.agentName = agentName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(final String webAddress) {
        this.webAddress = webAddress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public String getImplementationName() {
        return WeblogsSubscriberTaskConfiguration.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((agentName == null) ? 0 : agentName.hashCode());
        result = prime * result + delay;
        result = prime * result
                + ((emailAddress == null) ? 0 : emailAddress.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result
                + ((webAddress == null) ? 0 : webAddress.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        WeblogsSubscriberTaskConfiguration other = (WeblogsSubscriberTaskConfiguration) obj;
        if (agentName == null) {
            if (other.agentName != null)
                return false;
        } else if (!agentName.equals(other.agentName))
            return false;
        if (delay != other.delay)
            return false;
        if (emailAddress == null) {
            if (other.emailAddress != null)
                return false;
        } else if (!emailAddress.equals(other.emailAddress))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (webAddress == null) {
            if (other.webAddress != null)
                return false;
        } else if (!webAddress.equals(other.webAddress))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WeblogsSubscriberTaskConfiguration [url=" + url + ", delay="
                + delay + ", agentName=" + agentName + ", emailAddress="
                + emailAddress + ", webAddress=" + webAddress + "]";
    }

}
