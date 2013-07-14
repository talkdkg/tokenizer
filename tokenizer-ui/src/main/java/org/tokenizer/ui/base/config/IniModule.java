/*
 * Copyright 2013 Tokenizer Inc.
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
package org.tokenizer.ui.base.config;

import javax.inject.Singleton;

import org.tokenizer.ui.base.navigate.Sitemap;
import org.tokenizer.ui.base.navigate.SitemapProvider;
import org.tokenizer.ui.base.navigate.TextReaderSitemapProvider;

import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.config.V7IniProvider;

import com.google.inject.AbstractModule;

public class IniModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(V7Ini.class).toProvider(V7IniProvider.class).in(Singleton.class);
        bind(SitemapProvider.class).to(TextReaderSitemapProvider.class);
        bind(Sitemap.class).toProvider(SitemapProvider.class).in(Singleton.class);
    }

}
