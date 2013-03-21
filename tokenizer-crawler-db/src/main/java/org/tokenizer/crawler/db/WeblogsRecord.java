package org.tokenizer.crawler.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.tokenizer.core.StringPool;

import com.netflix.astyanax.annotations.Component;

public class WeblogsRecord {

    private int count;

    private List<Weblog> weblogs = new ArrayList<Weblog>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Weblog> getWeblogs() {
        return weblogs;
    }

    public void setWeblogs(List<Weblog> weblogs) {
        this.weblogs = weblogs;
    }

    public void add(Weblog weblog) {
        this.weblogs.add(weblog);
    }

    @Override
    public String toString() {
        return "WeblogBatchRecord [count=" + count + ", weblogs=" + weblogs
                + "]";
    }

    public static class Weblog {

        @Component(ordinal = 0)
        private Date updateTimestamp;

        @Component(ordinal = 1)
        private String url = StringPool.EMPTY;

        @Component(ordinal = 2)
        private String name = StringPool.EMPTY;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            if (name != null)
                this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            if (url != null)
                this.url = url;
        }

        public Date getUpdateTimestamp() {
            return updateTimestamp;
        }

        public void setUpdateTimestamp(Date updateTimestamp) {
            this.updateTimestamp = updateTimestamp;
        }

        public void setUpdateTimestamp(Date batchUpdateTimestamp, int seconds) {
            this.updateTimestamp = new Date(batchUpdateTimestamp.getTime()
                    - 1000 * seconds);
        }

        @Override
        public String toString() {
            return "\nWeblog [name=" + name + ", url=" + url
                    + ", updateTimestamp=" + updateTimestamp + "]";
        }

    }

}
