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

import java.util.Set;
import java.util.TreeSet;

public class RssFetcherTaskConfiguration extends TaskConfiguration {
    private static final long serialVersionUID = 1L;
    private Set<String> seeds = new TreeSet<String>();

    public Set<String> getSeeds() {
        return seeds;
    }

    public void setSeeds(Set<String> seeds) {
        this.seeds = seeds;
    }

    public void addSeed(String seed) {
        this.seeds.add(seed);
    }
}
