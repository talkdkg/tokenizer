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

public class HtmlSplitterTaskConfiguration extends TaskConfiguration {
    private static final long serialVersionUID = 1L;
    private String xpath;
    private String host;

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((xpath == null) ? 0 : xpath.hashCode());
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
        HtmlSplitterTaskConfiguration other = (HtmlSplitterTaskConfiguration) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (xpath == null) {
            if (other.xpath != null)
                return false;
        } else if (!xpath.equals(other.xpath))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "HtmlSplitterTaskConfiguration [xpath=" + xpath + ", host="
                + host + "]";
    }
}
