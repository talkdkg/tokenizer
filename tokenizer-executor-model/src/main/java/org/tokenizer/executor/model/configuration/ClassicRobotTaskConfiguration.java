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
    private String webAddress = "http://www.tokenizer.ca";
    //@formatter:off
    public static final String DEFAULT_URL_FILTER = 
            "# The default url filter.\n\n" +
            "# Each non-comment, non-blank line contains a regular expression\n" +
            "# prefixed by '+' or '-'.  The first matching pattern in the file\n" +
            "# determines whether a URL is included or ignored.  If no pattern\n" +
            "# matches, the URL is ignored.\n\n" +
            "# skip file: ftp: and mailto: urls\n" +
            "-(file|ftp|mailto):.*\n" +
            "# skip image and other suffixes\n" +
            "-.*\\.(gif|GIF|jpg|JPG|png|PNG|ico|ICO|css|CSS|sit|SIT|eps|EPS|wmf|WMF|zip|ZIP|ppt|PPT|mpg|MPG|xls|XLS|gz|GZ|rpm|RPM|tgz|TGZ|mov|MOV|exe|EXE|jpeg|JPEG|bmp|BMP|js|JS)\n\n" +
            "# skip URLs containing certain characters as probable queries, etc. (uncomment)\n" +
            "# -.*[?*!@=].*\n" +
            "# accept anything else\n" +
            "+.*\n";
    //@formatter:on
    private String urlFilterConfig = DEFAULT_URL_FILTER;

    @Override
    public String toString() {
        return "ClassicRobotTaskConfiguration [host=" + host
                + ", followRedirects=" + followRedirects + ", followExternal="
                + followExternal + ", agentName=" + agentName
                + ", emailAddress=" + emailAddress + ", webAddress="
                + webAddress + ", urlFilterConfig=" + urlFilterConfig + "]";
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(final boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public boolean isFollowExternal() {
        return followExternal;
    }

    public void setFollowExternal(final boolean followExternal) {
        this.followExternal = followExternal;
    }

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

    @Override
    public String getImplementationName() {
        return ClassicRobotTaskConfiguration.class.getSimpleName();
    }

    public String getUrlFilterConfig() {
        return urlFilterConfig;
    }

    public void setUrlFilterConfig(final String urlFilterConfig) {
        this.urlFilterConfig = urlFilterConfig;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
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
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
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
        if (urlFilterConfig == null) {
            if (other.urlFilterConfig != null)
                return false;
        } else if (!urlFilterConfig.equals(other.urlFilterConfig))
            return false;
        return true;
    }
}
