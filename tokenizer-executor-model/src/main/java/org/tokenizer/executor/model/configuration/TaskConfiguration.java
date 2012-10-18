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

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The configuration for a simple task.
 * 
 */
public class TaskConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;
    
    String name;
    String type;
    String tld;

    Map<String, String> properties = new TreeMap<String, String>();
    Set<String> seeds = new TreeSet<String>();

    public Set<String> getSeeds() {
        return seeds;
    }

    public void setSeeds(Set<String> seeds) {
        this.seeds = seeds;
    }

    public void addSeed(String seed) {
        this.seeds.add(seed);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getTld() {
        return tld;
    }

    public void setTld(String tld) {
        this.tld = tld;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((seeds == null) ? 0 : seeds.hashCode());
        result = prime * result + ((tld == null) ? 0 : tld.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        TaskConfiguration other = (TaskConfiguration) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        if (seeds == null) {
            if (other.seeds != null)
                return false;
        } else if (!seeds.equals(other.seeds))
            return false;
        if (tld == null) {
            if (other.tld != null)
                return false;
        } else if (!tld.equals(other.tld))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TaskConfiguration [name=" + name + ", type=" + type + ", tld="
                + tld + "]";
    }

}
