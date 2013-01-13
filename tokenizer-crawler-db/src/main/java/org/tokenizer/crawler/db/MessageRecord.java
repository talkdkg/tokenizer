package org.tokenizer.crawler.db;

import java.io.Serializable;
import java.util.Arrays;

public class MessageRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(UrlRecord.class);
    private byte[] digest = DefaultValues.EMPTY_ARRAY;
    private String host = DefaultValues.EMPTY_STRING;
    private byte[] hostInverted = DefaultValues.EMPTY_ARRAY;
    private String topic = DefaultValues.EMPTY_STRING;
    private String date = DefaultValues.EMPTY_STRING;
    private String author = DefaultValues.EMPTY_STRING;
    private String age = DefaultValues.EMPTY_STRING;
    private String sex = DefaultValues.EMPTY_STRING;
    private String title = DefaultValues.EMPTY_STRING;
    private String content = DefaultValues.EMPTY_STRING;
    private String userRating = DefaultValues.EMPTY_STRING;

    public MessageRecord(final byte[] digest, final String author,
            final String title, final String content) {
        this.digest = digest;
        if (author != null) {
            this.author = author;
        }
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
    }

    public MessageRecord(final byte[] digest, final String host,
            final byte[] hostInverted, final String topic, final String date,
            final String author, final String age, final String sex,
            final String title, final String content, final String userRating) {
        this.digest = digest;
        this.host = host;
        this.hostInverted = hostInverted;
        this.topic = topic;
        this.date = date;
        this.author = author;
        this.age = age;
        this.sex = sex;
        this.title = title;
        this.content = content;
        this.userRating = userRating;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        if (host != null) {
            this.host = host;
        }
    }

    public byte[] getHostInverted() {
        return hostInverted;
    }

    public void setHostInverted(final byte[] hostInverted) {
        if (hostInverted != null) {
            this.hostInverted = hostInverted;
        }
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(final String topic) {
        if (topic != null) {
            this.topic = topic;
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        if (date != null) {
            this.date = date;
        }
    }

    public String getAge() {
        return age;
    }

    public void setAge(final String age) {
        if (age != null) {
            this.age = age;
        }
    }

    public String getSex() {
        return sex;
    }

    public void setSex(final String sex) {
        if (sex != null) {
            this.sex = sex;
        }
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(final String userRating) {
        if (userRating != null) {
            this.userRating = userRating;
        }
    }

    public byte[] getDigest() {
        return digest;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "MessageRecord [digest=" + Arrays.toString(digest) + ", host="
                + host + ", hostInverted=" + Arrays.toString(hostInverted)
                + ", topic=" + topic + ", date=" + date + ", author=" + author
                + ", age=" + age + ", sex=" + sex + ", title=" + title
                + ", content=" + content + ", userRating=" + userRating + "]";
    }
}
