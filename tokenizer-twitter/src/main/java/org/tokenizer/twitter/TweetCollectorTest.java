package org.tokenizer.twitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.twitter.domain.StatusVO;

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

public class TweetCollectorTest implements Runnable {
    private static final Logger LOG = LoggerFactory
            .getLogger(TweetCollectorTest.class);
    private Twitter twitter;
    private String consumerKey = "grhYva2w8MLDfBj7Me5YA";
    private String consumerSecret = "FQVTHxiZMepRC4N1OUKveM9GXAALHD11OZCpNnQCNA";
    private String token = "186062010-sZKUy9KLkW5UVLmHexGPAzPiUe3KaG97ussXuFtB";
    private String tokenSecret = "QPAhPTtfjTyidHEI4W6lYp9YUSh2ImTaCiNrpnMzlKo";
    Collection<String> input = new TreeSet<String>();
    private double tweetsPerSecLimit = 0.5;
    private EntityManager manager;

    public TweetCollectorTest() {
        LOG.debug("Constructor called...");
        InputStream is = null;
        Reader reader = null;
        BufferedReader bufferedReader = null;
        try {
            // http://www.javapractices.com/topic/TopicAction.do?Id=42
            is = new FileInputStream("./keywords.txt");
            reader = new InputStreamReader(is, "UTF-8");
            bufferedReader = new BufferedReader(reader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                input.add(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
        // input.add("Thunderbolt");
        EntityManagerFactory factory = Persistence
                .createEntityManagerFactory("persistenceUnit");
        manager = factory.createEntityManager();
        // default read timeout 120000 see
        // twitter4j.internal.http.HttpClientImpl
        System.setProperty("twitter4j.http.readTimeout", "10000");
        // default connection time out 20000
        System.setProperty("twitter4j.http.connectionTimeout", "10000");
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
    }

    public static void main(String[] args) throws InterruptedException {
        TweetCollectorTest app = new TweetCollectorTest();
        // TwitterStream stream = new TwitterStreamFactory()
        // .getInstance(app.twitter.getAuthorization());
        Thread t = new Thread(app);
        t.setDaemon(false);
        t.start();
        // long id = 266412923881156609L;
        // Status status = app.manager.find(StatusVO.class, id);
        // LOG.info(status.toString());
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

    @Override
    public void run() {
        TwitterStream stream = null;
        TwitterStream oldStream = null;
        while (true) {
            try {
                int counter = 0;
                LOG.info("Starting over with " + input.size()
                        + " tags. indexed tweets:" + counter
                        + " tweetsPerSecLimit:" + tweetsPerSecLimit + " "
                        + input);
                if (stream != null)
                    oldStream = stream;
                BlockingQueue<Status> queue = new LinkedBlockingQueue<Status>(
                        100000);
                stream = streamingTwitter(input, queue);
                if (oldStream != null) {
                    oldStream.shutdown();
                }
                while (true) {
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
            } catch (Exception ex) {
                LOG.error("", ex);
                // try {
                // Thread.sleep(Math.round(60 * 1000));
                // } catch (InterruptedException e) {
                // break;
                // }
            }
        }
        // LOG.info("Finished...");
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
