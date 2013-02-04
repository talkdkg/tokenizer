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
package org.tokenizer.ui.views;

import org.tokenizer.executor.engine.twitter.TweetCollectorTaskConfiguration;

import com.vaadin.ui.Component;

public class TweetCollectorTaskConfigurationForm extends
        TaskConfigurationField<TweetCollectorTaskConfiguration> {

    private static final long serialVersionUID = 1L;

    @Override
    protected Component initContent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends TweetCollectorTaskConfiguration> getType() {
        return TweetCollectorTaskConfiguration.class;
    }
}
