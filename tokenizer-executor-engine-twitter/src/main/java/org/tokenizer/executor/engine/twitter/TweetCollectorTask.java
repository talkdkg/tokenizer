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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.TokenizerConfig;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.executor.engine.AbstractTask;
import org.tokenizer.executor.engine.HostLocker;
import org.tokenizer.executor.engine.twitter.db.StatusVO;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

public class TweetCollectorTask extends AbstractTask {
    private static final Logger LOG = LoggerFactory
            .getLogger(TweetCollectorTask.class);
    private Twitter twitter;
    private String consumerKey = TokenizerConfig
            .getString("twitter.consumerKey");
    private String consumerSecret = TokenizerConfig
            .getString("twitter.consumerSecret");
    private String token = TokenizerConfig.getString("twitter.token");
    private String tokenSecret = TokenizerConfig
            .getString("twitter.tokenSecret");
    static {
        // default read timeout 120000 see
        // twitter4j.internal.http.HttpClientImpl
        System.setProperty("twitter4j.http.readTimeout", "10000");
        // default connection time out 20000
        System.setProperty("twitter4j.http.connectionTimeout", "10000");
    }
    Collection<String> input = new TreeSet<String>();
    private double tweetsPerSecLimit = 0.5;
    private EntityManager manager;
    private TweetCollectorTaskConfiguration taskConfiguration;
    BlockingQueue<Status> queue;
    TwitterStream stream;

    public TweetCollectorTask(String taskName, ZooKeeperItf zk,
            TaskConfiguration taskConfiguration,
            CrawlerHBaseRepository crawlerRepository,
            WritableExecutorModel model, HostLocker hostLocker) {
        super(taskName, zk, crawlerRepository, model, hostLocker);
        this.taskConfiguration = (TweetCollectorTaskConfiguration) taskConfiguration;
        BufferedReader bufferedReader = new BufferedReader(new StringReader(
                this.taskConfiguration.getKeywords()));
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                input.add(line);
            }
        } catch (IOException e) {
            LOG.error("", e);
        }
        EntityManagerFactory factory = Persistence
                .createEntityManagerFactory("persistenceUnit");
        manager = factory.createEntityManager();
        AccessToken aToken = new AccessToken(token, tokenSecret);
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(aToken);
        try {
            // RequestToken requestToken = t.getOAuthRequestToken();
            // System.out.println("TW-URL:" +
            // requestToken.getAuthorizationURL());
            twitter.verifyCredentials();
            String str = "<user>";
            try {
                str = twitter.getScreenName();
            } catch (Exception e) {
                LOG.error("", e);
            }
            LOG.info("create new TwitterSearch for " + str);
        } catch (TwitterException ex) {
            // if (ex.getStatusCode() == 400)
            throw new RuntimeException(ex);
        }
        queue = new LinkedBlockingQueue<Status>(100000);
        LOG.debug("Instance created");
    }

    public TwitterStream streamingTwitter(Collection<String> track,
            final Queue<Status> queue) throws TwitterException {
        String[] trackArray = track.toArray(new String[track.size()]);
        TwitterStream stream = new TwitterStreamFactory().getInstance(twitter
                .getAuthorization());
        stream.addListener(new StatusListener() {
            @Override
            public void onStatus(Status status) {
                if (isEmpty(status.getUser().getScreenName()))
                    return;
                if (!queue.offer(status))
                    LOG.error("Cannot add tweet as input queue for streaming is full:"
                            + queue.size());
            }

            @Override
            public void onDeletionNotice(
                    StatusDeletionNotice statusDeletionNotice) {
                LOG.warn("We do not support onDeletionNotice at the moment! Tweet id: "
                        + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                LOG.warn("onTrackLimitationNotice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onException(Exception ex) {
                LOG.error("onException", ex);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                LOG.warn(warning.toString());
            }
        });
        stream.filter(new FilterQuery(0, new long[0], trackArray));
        return stream;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    @Override
    public TaskConfiguration getTaskConfiguration() {
        return this.taskConfiguration;
    }

    @Override
    public void setTaskConfiguration(TaskConfiguration taskConfiguration) {
        this.taskConfiguration = (TweetCollectorTaskConfiguration) taskConfiguration;
    }

    @Override
    protected void process() throws InterruptedException, IOException {
        if (stream == null)
            try {
                stream = streamingTwitter(input, queue);
            } catch (TwitterException e) {
                LOG.error("", e);
            }
        Status status = queue.take();
        StatusVO vo = new StatusVO(status);
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        try {
            manager.merge(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tx.commit();
        LOG.debug("Status: {}", status);
    }

    @Override
    protected synchronized void shutdown() throws InterruptedException {
        if (this.stream != null)
            this.stream.shutdown();
        super.shutdown();
    }
}
