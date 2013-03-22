package org.tokenizer;

import java.util.Date;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.thrift.ThriftMessageRecord;
import org.tokenizer.thrift.ThriftQueryResponse;
import org.tokenizer.thrift.ThriftTokenizerService;

public class TokenizerClient {

    private static final Logger LOG = LoggerFactory
            .getLogger(TokenizerClient.class);

    private static final String HOST = "108.175.12.244";
    private static final int PORT = 32123;

    public static ThriftQueryResponse getMessageRecords(final String query,
            final int start, final int rows) {
        TSocket transport = null;
        ThriftQueryResponse result = null;
        try {
            transport = new TSocket(HOST, PORT);
            TProtocol protocol = new TBinaryProtocol(transport);
            ThriftTokenizerService.Client client = new ThriftTokenizerService.Client(
                    protocol);
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

    public static ThriftQueryResponse getMessageRecords(final String query,
            final int start, final int rows, final long startTime,
            final long endTime) {
        TSocket transport = null;
        ThriftQueryResponse result = null;
        try {
            transport = new TSocket(HOST, PORT);
            TProtocol protocol = new TBinaryProtocol(transport);
            ThriftTokenizerService.Client client = new ThriftTokenizerService.Client(
                    protocol);
            transport.open();
            result = client.get_message_records_by_date_range(query, start,
                    rows, startTime, endTime);
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

    public static ThriftQueryResponse getMessageRecords(final String query,
            final int start, final int rows, final long startTime,
            final long endTime, final String source) {
        TSocket transport = null;
        ThriftQueryResponse result = null;
        try {
            transport = new TSocket(HOST, PORT);
            TProtocol protocol = new TBinaryProtocol(transport);
            ThriftTokenizerService.Client client = new ThriftTokenizerService.Client(
                    protocol);
            transport.open();
            result = client.get_message_records_by_date_range_and_source(query,
                    start, rows, startTime, endTime, source);
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

    public static ThriftQueryResponse getMessageRecords(final String query,
            final int start, final int rows, final String source) {
        TSocket transport = null;
        ThriftQueryResponse result = null;
        try {
            transport = new TSocket(HOST, PORT);
            TProtocol protocol = new TBinaryProtocol(transport);
            ThriftTokenizerService.Client client = new ThriftTokenizerService.Client(
                    protocol);
            transport.open();
            result = client.get_message_records_by_source(query, start, rows,
                    source);
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

        // get 10 records from record number 30 to 39:
        ThriftQueryResponse r = getMessageRecords("travel", 30, 10);
        //System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (ThriftMessageRecord tmr : r.getMessages()) {
            System.out.println(tmr);
        }

        // get 10 records from record number 0 to 9
        // filered by date range "last month"
        r = getMessageRecords("travel", 0, 10, System.currentTimeMillis()
                - oneMonthMillis, System.currentTimeMillis());
        //System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (ThriftMessageRecord tmr : r.getMessages()) {
            System.out.println(tmr);
        }

        // get 10 records from record number 0 to 9
        // filered by date range "last month"
        // filtered by "source" (host)
        r = getMessageRecords("travel", 0, 10, System.currentTimeMillis()
                - oneMonthMillis, System.currentTimeMillis(), "www.amazon.com");
        //System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (ThriftMessageRecord tmr : r.getMessages()) {
            System.out.println(tmr);
        }

        // get 10 records from record number 0 to 9
        // filtered by "source" (host)
        r = getMessageRecords("travel", 0, 10, "stream.twitter.com");
        //System.out.println("Error: " + r.error);
        System.out.println("Elapsed Time: " + r.elapsedTime);
        System.out.println("Number of records found: " + r.numFound);
        for (ThriftMessageRecord tmr : r.getMessages()) {
            System.out.println(tmr);
        }

        System.out.println(new Date(1361328830000L));

    }

}