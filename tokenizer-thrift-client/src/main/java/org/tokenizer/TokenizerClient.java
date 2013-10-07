package org.tokenizer;
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


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.thrift.Gender;
import org.tokenizer.thrift.ThriftQueryResponse;
import org.tokenizer.thrift.ThriftTokenizerService;
import org.tokenizer.thrift.TokenizerDocument;


public class TokenizerClient {

    private static final Logger LOG = LoggerFactory.getLogger(TokenizerClient.class);

    private static final String HOST = "108.175.12.244";
    private static final int PORT = 32123;

    public static ThriftQueryResponse getMessageRecords(final String query, final int start, final int rows) {
        TSocket transport = null;
        ThriftQueryResponse result = null;
        try {
            transport = new TSocket(HOST, PORT);
            TProtocol protocol = new TBinaryProtocol(transport);
            ThriftTokenizerService.Client client = new ThriftTokenizerService.Client(protocol);
            transport.open();
            result = client.get_message_records(query, start, rows);
        } catch (TTransportException e) {
            LOG.error("", e);
        } catch (TException e) {
            LOG.error("", e);
        } finally {
            if (transport != null) {
                transport.close();
            }
            transport = null;
        }

        return result;
    }

    public static ThriftQueryResponse getMessageRecords(final String query, final int start, final int rows,
            final long startTime, final long endTime) {
        TSocket transport = null;
        ThriftQueryResponse result = null;
        try {
            transport = new TSocket(HOST, PORT);
            TProtocol protocol = new TBinaryProtocol(transport);
            ThriftTokenizerService.Client client = new ThriftTokenizerService.Client(protocol);
            transport.open();
            result = client.get_message_records_by_date_range(query, start, rows, startTime, endTime);
        } catch (TTransportException e) {
            LOG.error("", e);
        } catch (TException e) {
            LOG.error("", e);
        } finally {
            if (transport != null) {
                transport.close();
            }
            transport = null;
        }

        return result;
    }

    public static ThriftQueryResponse getMessageRecords(final String query, final int start, final int rows,
            final long startTime, final long endTime, final String source) {
        TSocket transport = null;
        ThriftQueryResponse result = null;
        try {
            transport = new TSocket(HOST, PORT);
            TProtocol protocol = new TBinaryProtocol(transport);
            ThriftTokenizerService.Client client = new ThriftTokenizerService.Client(protocol);
            transport.open();
            result = client
                    .get_message_records_by_date_range_and_source(query, start, rows, startTime, endTime, source);
        } catch (TTransportException e) {
            LOG.error("", e);
        } catch (TException e) {
            LOG.error("", e);
        } finally {
            if (transport != null) {
                transport.close();
            }
            transport = null;
        }

        return result;
    }

    public static ThriftQueryResponse getMessageRecords(final String query, final int start, final int rows,
            final String source) {
        TSocket transport = null;
        ThriftQueryResponse result = null;
        try {
            transport = new TSocket(HOST, PORT);
            TProtocol protocol = new TBinaryProtocol(transport);
            ThriftTokenizerService.Client client = new ThriftTokenizerService.Client(protocol);
            transport.open();
            result = client.get_message_records_by_source(query, start, rows, source);
        } catch (TTransportException e) {
            LOG.error("", e);
        } catch (TException e) {
            LOG.error("", e);
        } finally {
            if (transport != null) {
                transport.close();
            }
            transport = null;
        }

        return result;
    }

    public static ThriftQueryResponse retrieveMessages(
            final String query,
            final int start,
            final int rows,
            final long startTime, // see Date.getTime()
            final long endTime,
            final List<String> sources,
            final List<String> countryCodes,
            final int startAge,
            final int endAge,
            final Gender gender,
            final List<String> languageCodes
            ) {
        TSocket transport = null;
        ThriftQueryResponse result = null;
        try {
            transport = new TSocket(HOST, PORT);
            TProtocol protocol = new TBinaryProtocol(transport);
            ThriftTokenizerService.Client client = new ThriftTokenizerService.Client(protocol);
            transport.open();
            result = client.retrieve_messages(query,
                    start,
                    rows,
                    startTime,
                    endTime,
                    sources,
                    countryCodes,
                    startAge,
                    endAge,
                    gender,
                    languageCodes);

        } catch (TTransportException e) {
            LOG.error("", e);
        } catch (TException e) {
            LOG.error("", e);
        } finally {
            if (transport != null) {
                transport.close();
            }
            transport = null;
        }

        return result;
    }


    public static void main(final String[] args) {

        long oneMonthMillis = 30 * 24 * 3600 * 1000L;
		org.apache.log4j.BasicConfigurator.configure();

        // get 10 records from record number 30 to 39:
		ArrayList<String> sources = new ArrayList<>();
		sources.add("www.amazon.com");
		sources.add("stream.twitter.com");
		ArrayList<String> countryCodes = new ArrayList<>();
		ArrayList<String> languageCodes = new ArrayList<>();
		languageCodes.add("en");
		languageCodes.add("fr");
		languageCodes.add("ru");
		languageCodes.add("es");
		Calendar startDay = Calendar.getInstance();

		startDay.set(Calendar.YEAR, 2013);
		startDay.set(Calendar.MONTH, 1);
		startDay.set(Calendar.DATE, 1);


		Calendar endDay = Calendar.getInstance();

		endDay.set(Calendar.YEAR, 2013);
		endDay.set(Calendar.MONTH, 12);
		endDay.set(Calendar.DATE, 31);
        ThriftQueryResponse r = TokenizerClient.retrieveMessages("*:*", 0, 10,startDay.getTimeInMillis(),endDay.getTimeInMillis(),sources,countryCodes,0,10,Gender.MALE,languageCodes);
        // System.out.println("Error: " + r.error);
		System.out.println("TEST ONE");
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getFeatures());
        }
		System.out.println("END OF CONTENT");
		System.out.println("-----------------------------");
		System.out.println("TEST TWO");
		r = TokenizerClient.retrieveMessages("*:*", 0, 10,startDay.getTimeInMillis(),endDay.getTimeInMillis(),sources,countryCodes,0,10,Gender.MALE,languageCodes);
        // System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");
		System.out.println("-----------------------------");
		System.out.println("TEST THREE");

		r = TokenizerClient.retrieveMessages("*:*", 0, 10,startDay.getTimeInMillis(),endDay.getTimeInMillis(),sources,countryCodes,0,10,Gender.FEMALE,languageCodes);
        // System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");
		System.out.println("-----------------------------");
		System.out.println("TEST FOUR");

		r = TokenizerClient.retrieveMessages("*:*", 0, 10,startDay.getTimeInMillis(),endDay.getTimeInMillis(),sources,countryCodes,0,10,Gender.FEMALE,languageCodes);
        // System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");
		System.out.println("-----------------------------");
		System.out.println("TEST FIVE");
		r = TokenizerClient.getMessageRecords("*:*", 0, 10);
        // System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");
		System.out.println("-----------------------------");
		System.out.println("TEST SIX");
		r = TokenizerClient.getMessageRecords("*:*", 0, 10,startDay.getTimeInMillis(),endDay.getTimeInMillis());
        // System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");
		System.out.println("-----------------------------");
		System.out.println("TEST SEVEN");
		r = TokenizerClient.getMessageRecords("*:*", 0, 10,startDay.getTimeInMillis(),endDay.getTimeInMillis(),"www.amazon.com");
        // System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");
		System.out.println("-----------------------------");
		System.out.println("TEST EIGHT");
		r = TokenizerClient.getMessageRecords("*:*", 0, 10,startDay.getTimeInMillis(),endDay.getTimeInMillis(),"www.amazon.com");
        // System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");

		System.out.println("-----------------------------");
		System.out.println("TEST NINE");
		r = TokenizerClient.getMessageRecords("*:*", 0, 10,startDay.getTimeInMillis(),endDay.getTimeInMillis(),"stream.twitter.com");
        // System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");
		System.out.println("-----------------------------");
		System.out.println("TEST TEN");
		r = TokenizerClient.retrieveMessages("*:*", 0, 100,startDay.getTimeInMillis(),endDay.getTimeInMillis(),sources,countryCodes,0,10,Gender.FEMALE,languageCodes);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");

		System.out.println("-----------------------------");
		System.out.println("TEST ELEVEN");
		r = TokenizerClient.retrieveMessages("*:*", 0, 100,startDay.getTimeInMillis(),endDay.getTimeInMillis(),sources,countryCodes,0,100,Gender.FEMALE,languageCodes);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");


		System.out.println("-----------------------------");
		System.out.println("TEST TWELVE");
		r = TokenizerClient.retrieveMessages("*:*", 0, 100,startDay.getTimeInMillis(),endDay.getTimeInMillis(),sources,countryCodes,0,50,Gender.FEMALE,languageCodes);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");

		System.out.println("-----------------------------");
		System.out.println("TEST THIRTEEN");
		r = TokenizerClient.getMessageRecords("*:*", 0, 20,startDay.getTimeInMillis(),endDay.getTimeInMillis());
        // System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");

		System.out.println("-----------------------------");
		System.out.println("TEST FOURTEEN");
		r = TokenizerClient.getMessageRecords("review", 0, 10,startDay.getTimeInMillis(),endDay.getTimeInMillis(),"www.amazon.com");
        // System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");
		System.out.println("-----------------------------");

		System.out.println("-----------------------------");
		System.out.println("TEST FIFTEEN");
		r = TokenizerClient.getMessageRecords("hotel", 0, 10,0,System.currentTimeMillis(),"www.tripadvisor.com");
        // System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (TokenizerDocument tmr : r.getDocuments()) {
			System.out.println(tmr.getAuthor());
        }
		System.out.println("END OF CONTENT");
		System.out.println("-----------------------------");
	}

}
