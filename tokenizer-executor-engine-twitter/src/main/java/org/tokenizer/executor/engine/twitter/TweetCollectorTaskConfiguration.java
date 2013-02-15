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
package org.tokenizer.executor.engine.twitter;

import org.tokenizer.executor.model.configuration.TaskConfiguration;

public class TweetCollectorTaskConfiguration extends TaskConfiguration {

    private static final long serialVersionUID = 1L;
    private String keywords;

    public String getKeywords() {
        if (keywords == null)
            return "";
        return keywords;
    }

    public void setKeywords(final String keywords) {
        this.keywords = keywords;
    }

    @Override
    public String getImplementationName() {
        return TweetCollectorTaskConfiguration.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((keywords == null) ? 0 : keywords.hashCode());
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
        TweetCollectorTaskConfiguration other = (TweetCollectorTaskConfiguration) obj;
        if (keywords == null) {
            if (other.keywords != null)
                return false;
        } else if (!keywords.equals(other.keywords))
            return false;
        return true;
    }
}
