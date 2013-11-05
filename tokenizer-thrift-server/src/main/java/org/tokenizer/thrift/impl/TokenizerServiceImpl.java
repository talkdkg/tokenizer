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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
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
import org.tokenizer.nlp.Sentence;
import org.tokenizer.thrift.ThriftDocument;
import org.tokenizer.thrift.ThriftGender;
import org.tokenizer.thrift.ThriftQueryResponse;
import org.tokenizer.thrift.ThriftSentence;
import org.tokenizer.thrift.ThriftTokenizerService;

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
        
        SolrQuery solrQuery = prepareDefaultQuery(query, start, rows);
        
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

    	SolrQuery solrQuery = prepareDefaultQuery(query, start, rows);
        
        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);
        
        solrQuery.addFilterQuery("date_tdt:[" + startDateTime.toString(TWITTER_DATE_FORMATTER) + " TO "
                + endDateTime.toString(TWITTER_DATE_FORMATTER) + "]");
        
        return query(solrQuery);
    }
    
    @Override
    public ThriftQueryResponse get_message_records_by_date_range_and_source(final String query, final int start,
            final int rows, final long startTime, final long endTime, final String source) throws TException {

        SolrQuery solrQuery = prepareDefaultQuery(query, start, rows);

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

        SolrQuery solrQuery = prepareDefaultQuery(query, start, rows);

        solrQuery.addFilterQuery("host_s:" + source);
        
        return query(solrQuery);
    }
    
    @Override
    public ThriftQueryResponse retrieve_messages(
            String query,
            int start,
            int rows,
            long startTime,
            long endTime,
            List<String> sources,
            List<String> locations,
            int startAge,
            int endAge,
            ThriftGender thriftGender,
            List<String> languageCodes) throws TException {
        
        SolrQuery solrQuery = prepareDefaultQuery(query, start, rows);
        
        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);
        solrQuery.addFilterQuery("date_tdt:[" + startDateTime.toString(TWITTER_DATE_FORMATTER) + " TO "
                + endDateTime.toString(TWITTER_DATE_FORMATTER) + "]");
        
        
        StringBuilder languageCodesFilterQuery = new StringBuilder("");
        
        for (int i=0; i < languageCodes.size(); i++) {
        	String languageCode = languageCodes.get(i);
        	
        	languageCodesFilterQuery.append("language_s:")
        	.append(languageCode);
        	
         	if (i < languageCodes.size() - 1) {
        		languageCodesFilterQuery.append(" OR ");
        	}
         	
        }
        
        if (languageCodes != null && languageCodes.size() > 0) {
        	 solrQuery.addFilterQuery(languageCodesFilterQuery.toString());
        }
        
        
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
        thriftQueryResponse.setThriftDocuments(toTokenizerDocumentList(beans));
        return thriftQueryResponse;
        
    }
    
    private List<ThriftDocument> toTokenizerDocumentList(List<MessageBean> solrDocumentList) {
        
        List<ThriftDocument> thriftMessageRecords = new ArrayList<ThriftDocument>();
        
        for (MessageBean bean : solrDocumentList) {
            LOG.debug(bean.toString());
            String id = bean.getId();
            
            ThriftDocument thriftDocument = new ThriftDocument();
            
            byte[] digest = MD5.hex2Byte(id);
            
            MessageRecord messageRecord;
            try {
                messageRecord = repository.retrieveMessageRecord(digest);
            } catch (ConnectionException e) {
                LOG.error("", e);
                continue;
            }
            
            if (messageRecord == null) continue;
            
            thriftDocument.setId(id);
            thriftDocument.setSource(messageRecord.getHost());
            thriftDocument.setDate(messageRecord.getISO8601Date());
            thriftDocument.setAuthor(messageRecord.getAuthor());
            thriftDocument.setAge(messageRecord.getAge());
  
            thriftDocument.setThriftGender(ThriftGender.UNDEFINED);
            if (messageRecord.getSex() != null) {
            	if (messageRecord.getSex().equals("male"))  thriftDocument.setThriftGender(ThriftGender.MALE);
            	else if (messageRecord.getSex().equals("female"))  thriftDocument.setThriftGender(ThriftGender.FEMALE);
            }
            
            thriftDocument.setTitle(messageRecord.getTitle());
            thriftDocument.setContent(messageRecord.getContent());
            thriftDocument.setTopic(messageRecord.getTopic());
            thriftDocument.setUserRating(messageRecord.getUserRating());
            
            thriftDocument.setMainSubject(messageRecord.getMainSubject());
            
            try
			{
				thriftDocument.setUrls(new HashSet(repository.listUrlByMessageDigest(digest)));
			}
			catch (ConnectionException e1)
			{
				LOG.error("", e1);
			}
            
            thriftDocument.setSentiment(messageRecord.getReviewText().getSentiment());
            thriftDocument.setFeatures(messageRecord.getReviewText().getFeatures());
            
            try
			{
				List<String> urls = repository.listUrlByMessageDigest(messageRecord.getDigest());
				thriftDocument.setUrls(new HashSet(urls));
			}
			catch (ConnectionException e)
			{
                LOG.error("", e);
			}
            
            
            for (Sentence s : messageRecord.getReviewText().getSentences()) {
                thriftDocument.addToThriftSentences(toThriftSentence(s));
            }
            
            thriftMessageRecords.add(thriftDocument);
        }
        
        return thriftMessageRecords;
        
    }
    
    private ThriftSentence toThriftSentence(Sentence sentence) {
        ThriftSentence thriftSentence = new ThriftSentence();
        thriftSentence.setChunks(sentence.getChunks());
        thriftSentence.setFeatures(sentence.getFeatures());
        thriftSentence.setSentence(sentence.getSentence());
        thriftSentence.setSentiment(sentence.getSentiment());
        thriftSentence.setTreebank(sentence.getTreebank());
        return thriftSentence;
    }
    
    
    private SolrQuery prepareDefaultQuery(String query, final int start, final int rows) {
        
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        solrQuery.setStart(start);
        solrQuery.setRows(rows);
        solrQuery.setParam("df", "text_en");
        solrQuery.setParam("q.op", "AND");
        
        SortClause defaultSortClause = new SortClause("_docid_", ORDER.asc);
        solrQuery.setSort(defaultSortClause);

        return solrQuery;
        
    }
    
}
