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

public class OutlinkExtractorTaskConfiguration extends AbstractTaskConfiguration {

    private static final long serialVersionUID = 1L;

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

    protected String host;

    private String urlFilterConfig = DEFAULT_URL_FILTER;
    
    private int extractOutlinksAttemptCounter = 0;


    @Override
    public String getImplementationName() {
        return OutlinkExtractorTaskConfiguration.class.getSimpleName();
    }

    public String getUrlFilterConfig() {
        return urlFilterConfig;
    }

    public void setUrlFilterConfig(final String urlFilterConfig) {
        this.urlFilterConfig = urlFilterConfig;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    
    

    public int getExtractOutlinksAttemptCounter() {
        return extractOutlinksAttemptCounter;
    }

    public void setExtractOutlinksAttemptCounter(int extractOutlinksAttemptCounter) {
        this.extractOutlinksAttemptCounter = extractOutlinksAttemptCounter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((host == null) ? 0 : host.hashCode());
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
        OutlinkExtractorTaskConfiguration other = (OutlinkExtractorTaskConfiguration) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        }
        else if (!host.equals(other.host))
            return false;
        if (urlFilterConfig == null) {
            if (other.urlFilterConfig != null)
                return false;
        }
        else if (!urlFilterConfig.equals(other.urlFilterConfig))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "OutlinkExtractorTaskConfiguration [host=" + host + ", urlFilterConfig=" + urlFilterConfig
                + ", extractOutlinksAttemptCounter=" + extractOutlinksAttemptCounter + "]";
    }


}
