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

import java.util.Set;
import java.util.TreeSet;

public class RssFetcherTaskConfiguration extends AbstractTaskConfiguration {

    private static final long serialVersionUID = 1L;
    private Set<String> seeds = new TreeSet<String>();

    public Set<String> getSeeds() {
        return seeds;
    }

    public void setSeeds(final Set<String> seeds) {
        this.seeds = seeds;
    }

    public void addSeed(final String seed) {
        this.seeds.add(seed);
    }

    @Override
    public String getImplementationName() {
        return RssFetcherTaskConfiguration.class.getSimpleName();
    }
}
