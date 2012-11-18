package org.tokenizer.executor.model.configuration;

import java.util.Set;
import java.util.TreeSet;

public class RssFetcherTaskConfiguration implements TaskConfiguration {
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
