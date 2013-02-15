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
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Queue;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.TokenizerConfig;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.LanguageDetector;
import org.tokenizer.core.util.MD5;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.MessageRecord;
import org.tokenizer.executor.engine.AbstractTask;
import org.tokenizer.executor.engine.HostLocker;
import org.tokenizer.executor.engine.MetricsCache;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

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

import com.cybozu.labs.langdetect.LangDetectException;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class TweetCollectorTask extends AbstractTask {

    private static final Logger LOG = LoggerFactory
            .getLogger(TweetCollectorTask.class);
    private final Twitter twitter;
    private final String consumerKey = TokenizerConfig
            .getString("twitter.consumerKey");
    private final String consumerSecret = TokenizerConfig
            .getString("twitter.consumerSecret");
    private final String token = TokenizerConfig.getString("twitter.token");
    private final String tokenSecret = TokenizerConfig
            .getString("twitter.tokenSecret");
    SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ");

    static {
        // default read timeout 120000 see
        // twitter4j.internal.http.HttpClientImpl
        System.setProperty("twitter4j.http.readTimeout", "10000");
        // default connection time out 20000
        System.setProperty("twitter4j.http.connectionTimeout", "10000");
    }
    Collection<String> input = new TreeSet<String>();
    private EntityManager manager;
    private TweetCollectorTaskConfiguration taskConfiguration;
    BlockingQueue<Status> queue;
    TwitterStream stream;

    public TweetCollectorTask(final UUID uuid, final String friendlyName,
            final ZooKeeperItf zk, final TaskConfiguration taskConfiguration,
            final CrawlerRepository crawlerRepository,
            final WritableExecutorModel model, final HostLocker hostLocker) {
        super(uuid, friendlyName, zk, crawlerRepository, model, hostLocker);
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

        // TODO: uncomment or remove
        // EntityManagerFactory factory = Persistence
        // .createEntityManagerFactory("persistenceUnit");
        // manager = factory.createEntityManager();

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

    public TwitterStream streamingTwitter(final Collection<String> track,
            final Queue<Status> queue) throws TwitterException {
        String[] trackArray = track.toArray(new String[track.size()]);
        TwitterStream stream = new TwitterStreamFactory().getInstance(twitter
                .getAuthorization());
        stream.addListener(new StatusListener() {

            @Override
            public void onStatus(final Status status) {
                if (isEmpty(status.getUser().getScreenName()))
                    return;

                /*
                 * if (!status.getUser().getLang().equals("en")) return;
                 * 
                 * try { if
                 * (!LanguageDetector.detect(status.getText()).equals("en"))
                 * return; } catch (LangDetectException e) { // TODO
                 * Auto-generated catch block e.printStackTrace(); return; }
                 */

                if (!queue.offer(status)) {
                    LOG.error("Cannot add tweet as input queue for streaming is full:"
                            + queue.size());
                }
            }

            @Override
            public void onDeletionNotice(
                    final StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(
                    final int numberOfLimitedStatuses) {
            }

            @Override
            public void onException(final Exception ex) {
                LOG.error("onException", ex);
            }

            @Override
            public void onScrubGeo(final long userId, final long upToStatusId) {
            }

            @Override
            public void onStallWarning(final StallWarning warning) {
                LOG.warn(warning.toString());
            }
        });
        stream.filter(new FilterQuery(0, new long[0], trackArray));
        return stream;
    }

    public static boolean isEmpty(final String str) {
        return str == null || str.isEmpty();
    }

    public TwitterStream sampleStream(final Queue<Status> queue)
            throws TwitterException {
        TwitterStream stream = new TwitterStreamFactory().getInstance(twitter
                .getAuthorization());
        stream.addListener(new StatusListener() {

            @Override
            public void onStatus(final Status status) {
                if (isEmpty(status.getUser().getScreenName()))
                    return;
                if (!status.getUser().getLang().equals("en"))
                    return;

                try {
                    if (!LanguageDetector.detect(status.getText()).equals("en"))
                        return;
                } catch (LangDetectException e) {
                    LOG.debug(e.getMessage());
                    return;
                }

                if (!queue.offer(status)) {
                    LOG.error("Cannot add tweet as input queue for streaming is full:"
                            + queue.size());
                }
            }

            @Override
            public void onDeletionNotice(
                    final StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(
                    final int numberOfLimitedStatuses) {
            }

            @Override
            public void onException(final Exception ex) {
                LOG.error("onException", ex);
            }

            @Override
            public void onScrubGeo(final long userId, final long upToStatusId) {
            }

            @Override
            public void onStallWarning(final StallWarning warning) {
                LOG.warn(warning.toString());
            }
        });
        stream.sample();
        return stream;
    }

    @Override
    public TaskConfiguration getTaskConfiguration() {
        return this.taskConfiguration;
    }

    @Override
    public void setTaskConfiguration(final TaskConfiguration taskConfiguration) {
        this.taskConfiguration = (TweetCollectorTaskConfiguration) taskConfiguration;
    }

    //@formatter:off
    @Override
    protected void process() throws InterruptedException, ConnectionException {
        if (stream == null) {
            try {
                //stream = streamingTwitter(input, queue);
                stream = sampleStream(queue);
            } catch (TwitterException e) {
                LOG.error("", e);
            }
        }
        Status status = queue.take();
    
        /*
        StatusVO vo = new StatusVO(status);
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        try {
            manager.merge(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tx.commit();
        
        */
        
        MessageRecord messageRecord = new MessageRecord(MD5.digest(status
                .toString()), status.getUser().getName(), null,
                status.getText());

        messageRecord.setHost("stream.twitter.com");
        messageRecord.setHostInverted(HttpUtils
                .getHostInverted("stream.twitter.com"));

        messageRecord.setDate(
                org.apache.solr.schema.DateField.formatExternal(
            status.getCreatedAt()));

        crawlerRepository.insertIfNotExists(messageRecord);
        metricsCache.increment(MetricsCache.MESSAGE_TOTAL_KEY);

        LOG.debug("Status: {}", status.getText());
        //LOG.debug("messageRecord: {}\n\n\n", messageRecord.getContent());
        
        
        
    }

    
    
    
    //@formatter:on

    @Override
    protected synchronized void shutdown() throws InterruptedException {
        if (this.stream != null) {
            this.stream.shutdown();
        }
        super.shutdown();
    }
}
