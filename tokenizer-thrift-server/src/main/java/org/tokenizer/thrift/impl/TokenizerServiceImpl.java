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
import org.tokenizer.core.util.MD5;
import org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl;
import org.tokenizer.crawler.db.model.MessageRecord;
import org.tokenizer.thrift.Gender;
import org.tokenizer.thrift.ThriftQueryResponse;
import org.tokenizer.thrift.ThriftTokenizerService;
import org.tokenizer.thrift.TokenizerDocument;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class TokenizerServiceImpl implements ThriftTokenizerService.Iface {
    
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TokenizerServiceImpl.class);
    
    private static SolrServer solrServer = SolrUtils.getSolrServerForMessages();
    
    
    private static CrawlerRepositoryCassandraImpl repository;

    public static void setup() throws Exception {
        CrawlerRepositoryCassandraImpl repo = new CrawlerRepositoryCassandraImpl(null);
        repo.setup();
        repository = repo;
    }

    public TokenizerServiceImpl() {
        super();
        try {
            setup();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
    
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
        
        public String getId() {
            return id;
        }
        
        public void setId(final String id) {
            this.id = id;
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
        } catch (Exception _ex) {}
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
    
    
    @Override
    public ThriftQueryResponse retrieve_messages(String query, int start, int rows, long startTime, long endTime,
            List<String> sources, List<String> countryCodes, int startAge, int endAge, Gender gender, List<String> languageCodes)
            throws TException {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        solrQuery.setStart(start);
        solrQuery.setRows(rows);
 
        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);
        solrQuery.addFilterQuery("date_tdt:[" + startDateTime.toString(TWITTER_DATE_FORMATTER) + " TO "
                + endDateTime.toString(TWITTER_DATE_FORMATTER) + "]");

        solrQuery.setSortField("_docid_", ORDER.asc);
         
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
        thriftQueryResponse.setDocuments(toTokenizerDocumentList(beans));
        return thriftQueryResponse;
        
    }

    
    private List<TokenizerDocument> toTokenizerDocumentList(List<MessageBean> solrDocumentList){
        
        List<TokenizerDocument> thriftMessageRecords = new ArrayList<TokenizerDocument>();
        
        for (MessageBean bean : solrDocumentList) {
            LOG.debug(bean.toString());
            String id = bean.getId();
            
            TokenizerDocument thriftMessageRecord = new TokenizerDocument();
            
            byte[] digest = MD5.hex2Byte(id);
         
            MessageRecord messageRecord;
            try {
                messageRecord = repository.retrieveMessageRecord(digest);
            } catch (ConnectionException e) {
                LOG.error("", e);
                continue;
            }
            
            if (messageRecord == null) continue;
            
            thriftMessageRecord.setId(id);
            thriftMessageRecord.setAge(messageRecord.getAge());
            
            thriftMessageRecord.setAuthor(messageRecord.getAuthor());
            thriftMessageRecord.setContent(messageRecord.getContent());
            if (messageRecord.getSex() != null) {
                thriftMessageRecord.setGender(messageRecord.getSex().equals("male") ? Gender.MALE
                        : Gender.FEMALE);
            }
            
            thriftMessageRecord.setSource(messageRecord.getHost());
            //thriftMessageRecord.setDate(messageRecord.getDate());
            
            thriftMessageRecord.setTitle(messageRecord.getTitle());
            thriftMessageRecord.setTopic(messageRecord.getTopic());
            thriftMessageRecord.setUserRating(messageRecord.getUserRating());
            
            thriftMessageRecord.setSentiment(messageRecord.getReviewText().getSentiment());
            thriftMessageRecord.setFeatures(messageRecord.getReviewText().getFeatures());
            
            thriftMessageRecords.add(thriftMessageRecord);
        }

        return thriftMessageRecords;
        
    }
    
    
}
