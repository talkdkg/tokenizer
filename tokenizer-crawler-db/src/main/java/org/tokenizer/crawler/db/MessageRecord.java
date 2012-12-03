package org.tokenizer.crawler.db;

import org.tokenizer.core.util.MD5;

public class MessageRecord {
    private String host;
    private String digest;
    // HOST("host"), TOPIC("topic"), DATE("date"), AUTHOR("author"), AGE("age"),
    // SEX(
    // "sex"), TITLE("title"), CONTENT("content");
    private String topic;
    private String date;
    private String author;
    private String age;
    private String sex;
    private String title;
    private String content;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDigest() {
        if (digest == null) {
            digest = MD5.MD5(author + title + content);
        }
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
