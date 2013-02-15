package org.tokenizer.executor.engine.twitter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.TokenizerConfig;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.MD5;
import org.tokenizer.crawler.db.MessageRecord;

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

public final class PrintSampleStream {

    private static final Logger LOG = LoggerFactory
            .getLogger(TweetCollectorTask.class);

    private final static String consumerKey = TokenizerConfig
            .getString("twitter.consumerKey");
    private final static String consumerSecret = TokenizerConfig
            .getString("twitter.consumerSecret");
    private final static String token = TokenizerConfig
            .getString("twitter.token");
    private final static String tokenSecret = TokenizerConfig
            .getString("twitter.tokenSecret");
    static {
        // default read timeout 120000 see
        // twitter4j.internal.http.HttpClientImpl
        System.setProperty("twitter4j.http.readTimeout", "10000");
        // default connection time out 20000
        System.setProperty("twitter4j.http.connectionTimeout", "10000");
    }

    static BlockingQueue<Status> queue;

    public static void main(String[] args) throws TwitterException,
            InterruptedException {

        AccessToken aToken = new AccessToken(token, tokenSecret);
        Twitter twitter = new TwitterFactory().getInstance();
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

        TwitterStream twitterStream = new TwitterStreamFactory()
                .getInstance(twitter.getAuthorization());
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                if (isEmpty(status.getUser().getScreenName()))
                    return;

                if (!status.getUser().getLang().equals("en"))
                    return;

                if (!queue.offer(status)) {
                    LOG.error("Cannot add tweet as input queue for streaming is full:"
                            + queue.size());
                }
            }

            @Override
            public void onDeletionNotice(
                    StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:"
                        + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:"
                        + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId
                        + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        twitterStream.sample();

        for (;;) {
            Status status = queue.take();

            MessageRecord messageRecord = new MessageRecord(MD5.digest(status
                    .toString()), status.getUser().getName(), null,
                    status.getText());

            messageRecord.setHost("stream.twitter.com");
            messageRecord.setHostInverted(HttpUtils
                    .getHostInverted("stream.twitter.com"));

            messageRecord.setDate(status.getCreatedAt().toGMTString());

            // crawlerRepository.insertIfNotExists(message);

            System.out.println(status);
        }

    }

    public static boolean isEmpty(final String str) {
        return str == null || str.isEmpty();
    }

}