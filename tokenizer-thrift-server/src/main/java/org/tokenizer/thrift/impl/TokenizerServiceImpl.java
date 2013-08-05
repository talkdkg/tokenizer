/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.tokenizer.thrift.impl;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.thrift.TException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.tokenizer.core.solr.SolrUtils;
import org.tokenizer.thrift.ThriftGenderType;
import org.tokenizer.thrift.ThriftMessageRecord;
import org.tokenizer.thrift.ThriftQueryResponse;
import org.tokenizer.thrift.ThriftTokenizerService;

public class TokenizerServiceImpl implements ThriftTokenizerService.Iface {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TokenizerServiceImpl.class);

    private static SolrServer solrServer = SolrUtils.getSolrServerForMessages();

    @Override
    public ThriftQueryResponse get_message_records(final String query, final int start, final int rows)
            throws TException {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        solrQuery.setStart(start);
        solrQuery.setRows(rows);
        solrQuery.setParam("df", "content_en");
        solrQuery.setSortField("_docid_", ORDER.asc);

        return query(solrQuery);

    }

    public static class MessageBean implements Serializable {

        private static final long serialVersionUID = 1L;

        @Field
        String id;

        @Field("host_s")
        String host;

        @Field("content_en")
        String content;

        @Field("age_ti")
        Integer age;

        @Field("author_en")
        String author;

        @Field("date_tdt")
        Date date;

        @Field("sex_s")
        String sex;

        @Field("title_en")
        String title;

        @Field("topic_en")
        String topic;

        @Field("userRating_s")
        String userRating;

        String highlightSnippet;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getHost() {
            return host;
        }

        public void setHost(final String host) {
            this.host = host;
        }

        public String getContent() {
            return content;
        }

        public void setContent(final String content) {
            this.content = content;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(final Integer age) {
            this.age = age;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(final String author) {
            this.author = author;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(final Date date) {
            this.date = date;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(final String sex) {
            this.sex = sex;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(final String title) {
            this.title = title;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(final String topic) {
            this.topic = topic;
        }

        public String getUserRating() {
            return userRating;
        }

        public void setUserRating(final String userRating) {
            this.userRating = userRating;
        }

        public String getHighlightSnippet() {
            return highlightSnippet;
        }

        public void setHighlightSnippet(final String highlightSnippet) {
            this.highlightSnippet = highlightSnippet;
        }

        @Override
        public String toString() {
            return "MessageBean [id=" + id + ", host=" + host + ", content=" + content + ", age=" + age + ", author="
                    + author + ", date=" + date + ", sex=" + sex + ", title=" + title + ", topic=" + topic
                    + ", userRating=" + userRating + ", highlightSnippet=" + highlightSnippet + "]";
        }

    }

    public static String exceptionToString(final Throwable th) {
        String stTrace = null;
        try {
            StringWriter sout = new StringWriter();
            PrintWriter out = new PrintWriter(sout);
            th.printStackTrace(out);
            out.close();
            sout.close();
            stTrace = sout.toString();
            sout = null;
            out = null;
        } catch (Exception _ex) {
        }
        return stTrace;
    }

    private static final DateTimeFormatter TWITTER_DATE_FORMATTER = DateTimeFormat
            .forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public ThriftQueryResponse get_message_records_by_date_range(final String query, final int start, final int rows,
            final long startTime, final long endTime) throws TException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        solrQuery.setStart(start);
        solrQuery.setRows(rows);
        solrQuery.setParam("df", "content_en");
        solrQuery.setSortField("_docid_", ORDER.asc);

        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);

        solrQuery.addFilterQuery("date_tdt:[" + startDateTime.toString(TWITTER_DATE_FORMATTER) + " TO "
                + endDateTime.toString(TWITTER_DATE_FORMATTER) + "]");

        return query(solrQuery);
    }

    @Override
    public ThriftQueryResponse get_message_records_by_date_range_and_source(final String query, final int start,
            final int rows, final long startTime, final long endTime, final String source) throws TException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        solrQuery.setStart(start);
        solrQuery.setRows(rows);
        solrQuery.setParam("df", "content_en");
        solrQuery.setSortField("_docid_", ORDER.asc);

        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);

        solrQuery.addFilterQuery("date_tdt:[" + startDateTime.toString(TWITTER_DATE_FORMATTER) + " TO "
                + endDateTime.toString(TWITTER_DATE_FORMATTER) + "]");

        solrQuery.addFilterQuery("host_s:" + source);

        return query(solrQuery);
    }

    @Override
    public ThriftQueryResponse get_message_records_by_source(final String query, final int start, final int rows,
            final String source) throws TException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        solrQuery.setStart(start);
        solrQuery.setRows(rows);
        solrQuery.setParam("df", "content_en");
        solrQuery.setSortField("_docid_", ORDER.asc);

        solrQuery.addFilterQuery("host_s:" + source);

        return query(solrQuery);
    }

    private ThriftQueryResponse query(final SolrQuery solrQuery) throws TException {

        List<MessageBean> beans;
        QueryResponse queryResponse = null;
        ThriftQueryResponse thriftQueryResponse = new ThriftQueryResponse();

        try {
            LOG.debug("Querying Solr... {}", solrQuery);
            queryResponse = solrServer.query(solrQuery);
        } catch (SolrServerException e) {
            LOG.error(e.getMessage());
            thriftQueryResponse.setError(exceptionToString(e));
            return thriftQueryResponse;
        }

        LOG.debug(queryResponse.toString());

        thriftQueryResponse.setElapsedTime((int) queryResponse.getElapsedTime());
        thriftQueryResponse.setQTime(queryResponse.getQTime());
        thriftQueryResponse.setNumFound(queryResponse.getResults().getNumFound());

        beans = queryResponse.getBeans(MessageBean.class);

        List<ThriftMessageRecord> thriftMessageRecords = new ArrayList<ThriftMessageRecord>();

        for (MessageBean bean : beans) {
            LOG.debug(bean.toString());
            String id = bean.getId();

            ThriftMessageRecord thriftMessageRecord = new ThriftMessageRecord();

            thriftMessageRecord.setId(id);
            thriftMessageRecord.setAge(bean.getAge().byteValue());
            thriftMessageRecord.setAuthor(bean.getAuthor());
            thriftMessageRecord.setContent(bean.getContent());
            if (bean.getSex() != null) {
                thriftMessageRecord.setGender(bean.getSex().equals("male") ? ThriftGenderType.MALE
                        : ThriftGenderType.FEMALE);
            }

            thriftMessageRecord.setSource(bean.getHost());
            thriftMessageRecord.setTimestamp(bean.getDate().getTime());

            thriftMessageRecord.setTitle(bean.getTitle());
            thriftMessageRecord.setTopic(bean.getTopic());
            thriftMessageRecord.setUserRating(bean.getUserRating());

            thriftMessageRecords.add(thriftMessageRecord);
        }

        thriftQueryResponse.setMessages(thriftMessageRecords);
        return thriftQueryResponse;

    }

}
