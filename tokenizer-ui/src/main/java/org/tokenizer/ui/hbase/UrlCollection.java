package org.tokenizer.ui.hbase;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.ui.MyVaadinApplication;

public class UrlCollection extends AbstractCollection<String> implements
        Iterator<String> {
    private static final Logger LOG = LoggerFactory
            .getLogger(UrlCollection.class);
    private MyVaadinApplication app;
    private CrawlerHBaseRepository repository;
    private String currentUrl = null;
    private LinkedList<String> cache = new LinkedList<String>();

    protected UrlCollection() {
    }

    public UrlCollection(MyVaadinApplication app) {
        super();
        this.app = app;
        this.repository = app.getRepository();
    }

    @Override
    public Iterator<String> iterator() {
        return this;
    }

    int size = 0;

    @Override
    public int size() {
        if (size == 0) {
            synchronized (app) {
                size = (int) CrawlerHBaseRepository.countUrl();
            }
        }
        return size;
    }

    @Override
    public boolean hasNext() {
        synchronized (app) {
            int index = cache.indexOf(this.currentUrl);
            if (cache.size() == index + 1) {
                boolean available = load(100);
                return available;
            }
            return true;
        }
    }

    @Override
    public String next() {
        synchronized (app) {
            int index = cache.indexOf(this.currentUrl);
            if (cache.size() == index + 1) {
                boolean available = load(100);
                if (!available)
                    return null;
                index = cache.indexOf(this.currentUrl);
            }
            this.currentUrl = cache.get(index + 1);
            return this.currentUrl;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("not implemented");
    }

    private boolean load(int count) {
        String lastUrl = null;
        if (cache.size() > 0)
            lastUrl = cache.get(cache.size() - 1);
        List<String> nextUrls = repository.nextUrl(lastUrl, count);
        if (nextUrls.size() == 0)
            return false;
        for (String url : nextUrls) {
            cache.addLast(url);
            if (cache.size() > 1000)
                cache.removeFirst();
        }
        return true;
    }
}
