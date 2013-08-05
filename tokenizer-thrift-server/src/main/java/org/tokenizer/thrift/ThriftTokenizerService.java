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
package org.tokenizer.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThriftTokenizerService {

  public interface Iface {

    public ThriftQueryResponse get_message_records(String query, int start, int rows) throws org.apache.thrift.TException;

    public ThriftQueryResponse get_message_records_by_date_range(String query, int start, int rows, long startTime, long endTime) throws org.apache.thrift.TException;

    public ThriftQueryResponse get_message_records_by_date_range_and_source(String query, int start, int rows, long startTime, long endTime, String source) throws org.apache.thrift.TException;

    public ThriftQueryResponse get_message_records_by_source(String query, int start, int rows, String source) throws org.apache.thrift.TException;

  }

  public interface AsyncIface {

    public void get_message_records(String query, int start, int rows, org.apache.thrift.async.AsyncMethodCallback<AsyncClient.get_message_records_call> resultHandler) throws org.apache.thrift.TException;

    public void get_message_records_by_date_range(String query, int start, int rows, long startTime, long endTime, org.apache.thrift.async.AsyncMethodCallback<AsyncClient.get_message_records_by_date_range_call> resultHandler) throws org.apache.thrift.TException;

    public void get_message_records_by_date_range_and_source(String query, int start, int rows, long startTime, long endTime, String source, org.apache.thrift.async.AsyncMethodCallback<AsyncClient.get_message_records_by_date_range_and_source_call> resultHandler) throws org.apache.thrift.TException;

    public void get_message_records_by_source(String query, int start, int rows, String source, org.apache.thrift.async.AsyncMethodCallback<AsyncClient.get_message_records_by_source_call> resultHandler) throws org.apache.thrift.TException;

  }

  public static class Client extends org.apache.thrift.TServiceClient implements Iface {
    public static class Factory implements org.apache.thrift.TServiceClientFactory<Client> {
      public Factory() {}
      public Client getClient(org.apache.thrift.protocol.TProtocol prot) {
        return new Client(prot);
      }
      public Client getClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
        return new Client(iprot, oprot);
      }
    }

    public Client(org.apache.thrift.protocol.TProtocol prot)
    {
      super(prot, prot);
    }

    public Client(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
      super(iprot, oprot);
    }

    public ThriftQueryResponse get_message_records(String query, int start, int rows) throws org.apache.thrift.TException
    {
      send_get_message_records(query, start, rows);
      return recv_get_message_records();
    }

    public void send_get_message_records(String query, int start, int rows) throws org.apache.thrift.TException
    {
      get_message_records_args args = new get_message_records_args();
      args.setQuery(query);
      args.setStart(start);
      args.setRows(rows);
      sendBase("get_message_records", args);
    }

    public ThriftQueryResponse recv_get_message_records() throws org.apache.thrift.TException
    {
      get_message_records_result result = new get_message_records_result();
      receiveBase(result, "get_message_records");
      if (result.isSetSuccess()) {
        return result.success;
      }
      throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "get_message_records failed: unknown result");
    }

    public ThriftQueryResponse get_message_records_by_date_range(String query, int start, int rows, long startTime, long endTime) throws org.apache.thrift.TException
    {
      send_get_message_records_by_date_range(query, start, rows, startTime, endTime);
      return recv_get_message_records_by_date_range();
    }

    public void send_get_message_records_by_date_range(String query, int start, int rows, long startTime, long endTime) throws org.apache.thrift.TException
    {
      get_message_records_by_date_range_args args = new get_message_records_by_date_range_args();
      args.setQuery(query);
      args.setStart(start);
      args.setRows(rows);
      args.setStartTime(startTime);
      args.setEndTime(endTime);
      sendBase("get_message_records_by_date_range", args);
    }

    public ThriftQueryResponse recv_get_message_records_by_date_range() throws org.apache.thrift.TException
    {
      get_message_records_by_date_range_result result = new get_message_records_by_date_range_result();
      receiveBase(result, "get_message_records_by_date_range");
      if (result.isSetSuccess()) {
        return result.success;
      }
      throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "get_message_records_by_date_range failed: unknown result");
    }

    public ThriftQueryResponse get_message_records_by_date_range_and_source(String query, int start, int rows, long startTime, long endTime, String source) throws org.apache.thrift.TException
    {
      send_get_message_records_by_date_range_and_source(query, start, rows, startTime, endTime, source);
      return recv_get_message_records_by_date_range_and_source();
    }

    public void send_get_message_records_by_date_range_and_source(String query, int start, int rows, long startTime, long endTime, String source) throws org.apache.thrift.TException
    {
      get_message_records_by_date_range_and_source_args args = new get_message_records_by_date_range_and_source_args();
      args.setQuery(query);
      args.setStart(start);
      args.setRows(rows);
      args.setStartTime(startTime);
      args.setEndTime(endTime);
      args.setSource(source);
      sendBase("get_message_records_by_date_range_and_source", args);
    }

    public ThriftQueryResponse recv_get_message_records_by_date_range_and_source() throws org.apache.thrift.TException
    {
      get_message_records_by_date_range_and_source_result result = new get_message_records_by_date_range_and_source_result();
      receiveBase(result, "get_message_records_by_date_range_and_source");
      if (result.isSetSuccess()) {
        return result.success;
      }
      throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "get_message_records_by_date_range_and_source failed: unknown result");
    }

    public ThriftQueryResponse get_message_records_by_source(String query, int start, int rows, String source) throws org.apache.thrift.TException
    {
      send_get_message_records_by_source(query, start, rows, source);
      return recv_get_message_records_by_source();
    }

    public void send_get_message_records_by_source(String query, int start, int rows, String source) throws org.apache.thrift.TException
    {
      get_message_records_by_source_args args = new get_message_records_by_source_args();
      args.setQuery(query);
      args.setStart(start);
      args.setRows(rows);
      args.setSource(source);
      sendBase("get_message_records_by_source", args);
    }

    public ThriftQueryResponse recv_get_message_records_by_source() throws org.apache.thrift.TException
    {
      get_message_records_by_source_result result = new get_message_records_by_source_result();
      receiveBase(result, "get_message_records_by_source");
      if (result.isSetSuccess()) {
        return result.success;
      }
      throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "get_message_records_by_source failed: unknown result");
    }

  }
  public static class AsyncClient extends org.apache.thrift.async.TAsyncClient implements AsyncIface {
    public static class Factory implements org.apache.thrift.async.TAsyncClientFactory<AsyncClient> {
      private org.apache.thrift.async.TAsyncClientManager clientManager;
      private org.apache.thrift.protocol.TProtocolFactory protocolFactory;
      public Factory(org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.protocol.TProtocolFactory protocolFactory) {
        this.clientManager = clientManager;
        this.protocolFactory = protocolFactory;
      }
      public AsyncClient getAsyncClient(org.apache.thrift.transport.TNonblockingTransport transport) {
        return new AsyncClient(protocolFactory, clientManager, transport);
      }
    }

    public AsyncClient(org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.transport.TNonblockingTransport transport) {
      super(protocolFactory, clientManager, transport);
    }

    public void get_message_records(String query, int start, int rows, org.apache.thrift.async.AsyncMethodCallback<get_message_records_call> resultHandler) throws org.apache.thrift.TException {
      checkReady();
      get_message_records_call method_call = new get_message_records_call(query, start, rows, resultHandler, this, ___protocolFactory, ___transport);
      this.___currentMethod = method_call;
      ___manager.call(method_call);
    }

    public static class get_message_records_call extends org.apache.thrift.async.TAsyncMethodCall {
      private String query;
      private int start;
      private int rows;
      public get_message_records_call(String query, int start, int rows, org.apache.thrift.async.AsyncMethodCallback<get_message_records_call> resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
        super(client, protocolFactory, transport, resultHandler, false);
        this.query = query;
        this.start = start;
        this.rows = rows;
      }

      public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
        prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("get_message_records", org.apache.thrift.protocol.TMessageType.CALL, 0));
        get_message_records_args args = new get_message_records_args();
        args.setQuery(query);
        args.setStart(start);
        args.setRows(rows);
        args.write(prot);
        prot.writeMessageEnd();
      }

      public ThriftQueryResponse getResult() throws org.apache.thrift.TException {
        if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
          throw new IllegalStateException("Method call not finished!");
        }
        org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
        org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
        return (new Client(prot)).recv_get_message_records();
      }
    }

    public void get_message_records_by_date_range(String query, int start, int rows, long startTime, long endTime, org.apache.thrift.async.AsyncMethodCallback<get_message_records_by_date_range_call> resultHandler) throws org.apache.thrift.TException {
      checkReady();
      get_message_records_by_date_range_call method_call = new get_message_records_by_date_range_call(query, start, rows, startTime, endTime, resultHandler, this, ___protocolFactory, ___transport);
      this.___currentMethod = method_call;
      ___manager.call(method_call);
    }

    public static class get_message_records_by_date_range_call extends org.apache.thrift.async.TAsyncMethodCall {
      private String query;
      private int start;
      private int rows;
      private long startTime;
      private long endTime;
      public get_message_records_by_date_range_call(String query, int start, int rows, long startTime, long endTime, org.apache.thrift.async.AsyncMethodCallback<get_message_records_by_date_range_call> resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
        super(client, protocolFactory, transport, resultHandler, false);
        this.query = query;
        this.start = start;
        this.rows = rows;
        this.startTime = startTime;
        this.endTime = endTime;
      }

      public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
        prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("get_message_records_by_date_range", org.apache.thrift.protocol.TMessageType.CALL, 0));
        get_message_records_by_date_range_args args = new get_message_records_by_date_range_args();
        args.setQuery(query);
        args.setStart(start);
        args.setRows(rows);
        args.setStartTime(startTime);
        args.setEndTime(endTime);
        args.write(prot);
        prot.writeMessageEnd();
      }

      public ThriftQueryResponse getResult() throws org.apache.thrift.TException {
        if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
          throw new IllegalStateException("Method call not finished!");
        }
        org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
        org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
        return (new Client(prot)).recv_get_message_records_by_date_range();
      }
    }

    public void get_message_records_by_date_range_and_source(String query, int start, int rows, long startTime, long endTime, String source, org.apache.thrift.async.AsyncMethodCallback<get_message_records_by_date_range_and_source_call> resultHandler) throws org.apache.thrift.TException {
      checkReady();
      get_message_records_by_date_range_and_source_call method_call = new get_message_records_by_date_range_and_source_call(query, start, rows, startTime, endTime, source, resultHandler, this, ___protocolFactory, ___transport);
      this.___currentMethod = method_call;
      ___manager.call(method_call);
    }

    public static class get_message_records_by_date_range_and_source_call extends org.apache.thrift.async.TAsyncMethodCall {
      private String query;
      private int start;
      private int rows;
      private long startTime;
      private long endTime;
      private String source;
      public get_message_records_by_date_range_and_source_call(String query, int start, int rows, long startTime, long endTime, String source, org.apache.thrift.async.AsyncMethodCallback<get_message_records_by_date_range_and_source_call> resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
        super(client, protocolFactory, transport, resultHandler, false);
        this.query = query;
        this.start = start;
        this.rows = rows;
        this.startTime = startTime;
        this.endTime = endTime;
        this.source = source;
      }

      public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
        prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("get_message_records_by_date_range_and_source", org.apache.thrift.protocol.TMessageType.CALL, 0));
        get_message_records_by_date_range_and_source_args args = new get_message_records_by_date_range_and_source_args();
        args.setQuery(query);
        args.setStart(start);
        args.setRows(rows);
        args.setStartTime(startTime);
        args.setEndTime(endTime);
        args.setSource(source);
        args.write(prot);
        prot.writeMessageEnd();
      }

      public ThriftQueryResponse getResult() throws org.apache.thrift.TException {
        if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
          throw new IllegalStateException("Method call not finished!");
        }
        org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
        org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
        return (new Client(prot)).recv_get_message_records_by_date_range_and_source();
      }
    }

    public void get_message_records_by_source(String query, int start, int rows, String source, org.apache.thrift.async.AsyncMethodCallback<get_message_records_by_source_call> resultHandler) throws org.apache.thrift.TException {
      checkReady();
      get_message_records_by_source_call method_call = new get_message_records_by_source_call(query, start, rows, source, resultHandler, this, ___protocolFactory, ___transport);
      this.___currentMethod = method_call;
      ___manager.call(method_call);
    }

    public static class get_message_records_by_source_call extends org.apache.thrift.async.TAsyncMethodCall {
      private String query;
      private int start;
      private int rows;
      private String source;
      public get_message_records_by_source_call(String query, int start, int rows, String source, org.apache.thrift.async.AsyncMethodCallback<get_message_records_by_source_call> resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
        super(client, protocolFactory, transport, resultHandler, false);
        this.query = query;
        this.start = start;
        this.rows = rows;
        this.source = source;
      }

      public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
        prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("get_message_records_by_source", org.apache.thrift.protocol.TMessageType.CALL, 0));
        get_message_records_by_source_args args = new get_message_records_by_source_args();
        args.setQuery(query);
        args.setStart(start);
        args.setRows(rows);
        args.setSource(source);
        args.write(prot);
        prot.writeMessageEnd();
      }

      public ThriftQueryResponse getResult() throws org.apache.thrift.TException {
        if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
          throw new IllegalStateException("Method call not finished!");
        }
        org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
        org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
        return (new Client(prot)).recv_get_message_records_by_source();
      }
    }

  }

  public static class Processor<I extends Iface> extends org.apache.thrift.TBaseProcessor<I> implements org.apache.thrift.TProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class.getName());
    public Processor(I iface) {
      super(iface, getProcessMap(new HashMap<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
    }

    protected Processor(I iface, Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      super(iface, getProcessMap(processMap));
    }

    private static <I extends Iface> Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> getProcessMap(Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      processMap.put("get_message_records", new get_message_records());
      processMap.put("get_message_records_by_date_range", new get_message_records_by_date_range());
      processMap.put("get_message_records_by_date_range_and_source", new get_message_records_by_date_range_and_source());
      processMap.put("get_message_records_by_source", new get_message_records_by_source());
      return processMap;
    }

    public static class get_message_records<I extends Iface> extends org.apache.thrift.ProcessFunction<I, get_message_records_args> {
      public get_message_records() {
        super("get_message_records");
      }

      public get_message_records_args getEmptyArgsInstance() {
        return new get_message_records_args();
      }

      protected boolean isOneway() {
        return false;
      }

      public get_message_records_result getResult(I iface, get_message_records_args args) throws org.apache.thrift.TException {
        get_message_records_result result = new get_message_records_result();
        result.success = iface.get_message_records(args.query, args.start, args.rows);
        return result;
      }
    }

    public static class get_message_records_by_date_range<I extends Iface> extends org.apache.thrift.ProcessFunction<I, get_message_records_by_date_range_args> {
      public get_message_records_by_date_range() {
        super("get_message_records_by_date_range");
      }

      public get_message_records_by_date_range_args getEmptyArgsInstance() {
        return new get_message_records_by_date_range_args();
      }

      protected boolean isOneway() {
        return false;
      }

      public get_message_records_by_date_range_result getResult(I iface, get_message_records_by_date_range_args args) throws org.apache.thrift.TException {
        get_message_records_by_date_range_result result = new get_message_records_by_date_range_result();
        result.success = iface.get_message_records_by_date_range(args.query, args.start, args.rows, args.startTime, args.endTime);
        return result;
      }
    }

    public static class get_message_records_by_date_range_and_source<I extends Iface> extends org.apache.thrift.ProcessFunction<I, get_message_records_by_date_range_and_source_args> {
      public get_message_records_by_date_range_and_source() {
        super("get_message_records_by_date_range_and_source");
      }

      public get_message_records_by_date_range_and_source_args getEmptyArgsInstance() {
        return new get_message_records_by_date_range_and_source_args();
      }

      protected boolean isOneway() {
        return false;
      }

      public get_message_records_by_date_range_and_source_result getResult(I iface, get_message_records_by_date_range_and_source_args args) throws org.apache.thrift.TException {
        get_message_records_by_date_range_and_source_result result = new get_message_records_by_date_range_and_source_result();
        result.success = iface.get_message_records_by_date_range_and_source(args.query, args.start, args.rows, args.startTime, args.endTime, args.source);
        return result;
      }
    }

    public static class get_message_records_by_source<I extends Iface> extends org.apache.thrift.ProcessFunction<I, get_message_records_by_source_args> {
      public get_message_records_by_source() {
        super("get_message_records_by_source");
      }

      public get_message_records_by_source_args getEmptyArgsInstance() {
        return new get_message_records_by_source_args();
      }

      protected boolean isOneway() {
        return false;
      }

      public get_message_records_by_source_result getResult(I iface, get_message_records_by_source_args args) throws org.apache.thrift.TException {
        get_message_records_by_source_result result = new get_message_records_by_source_result();
        result.success = iface.get_message_records_by_source(args.query, args.start, args.rows, args.source);
        return result;
      }
    }

  }

  public static class get_message_records_args implements org.apache.thrift.TBase<get_message_records_args, get_message_records_args._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("get_message_records_args");

    private static final org.apache.thrift.protocol.TField QUERY_FIELD_DESC = new org.apache.thrift.protocol.TField("query", org.apache.thrift.protocol.TType.STRING, (short)1);
    private static final org.apache.thrift.protocol.TField START_FIELD_DESC = new org.apache.thrift.protocol.TField("start", org.apache.thrift.protocol.TType.I32, (short)2);
    private static final org.apache.thrift.protocol.TField ROWS_FIELD_DESC = new org.apache.thrift.protocol.TField("rows", org.apache.thrift.protocol.TType.I32, (short)3);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new get_message_records_argsStandardSchemeFactory());
      schemes.put(TupleScheme.class, new get_message_records_argsTupleSchemeFactory());
    }

    public String query; // required
    public int start; // required
    public int rows; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      QUERY((short)1, "query"),
      START((short)2, "start"),
      ROWS((short)3, "rows");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 1: // QUERY
            return QUERY;
          case 2: // START
            return START;
          case 3: // ROWS
            return ROWS;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    private static final int __START_ISSET_ID = 0;
    private static final int __ROWS_ISSET_ID = 1;
    private byte __isset_bitfield = 0;
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.QUERY, new org.apache.thrift.meta_data.FieldMetaData("query", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
      tmpMap.put(_Fields.START, new org.apache.thrift.meta_data.FieldMetaData("start", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
      tmpMap.put(_Fields.ROWS, new org.apache.thrift.meta_data.FieldMetaData("rows", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(get_message_records_args.class, metaDataMap);
    }

    public get_message_records_args() {
    }

    public get_message_records_args(
      String query,
      int start,
      int rows)
    {
      this();
      this.query = query;
      this.start = start;
      setStartIsSet(true);
      this.rows = rows;
      setRowsIsSet(true);
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public get_message_records_args(get_message_records_args other) {
      __isset_bitfield = other.__isset_bitfield;
      if (other.isSetQuery()) {
        this.query = other.query;
      }
      this.start = other.start;
      this.rows = other.rows;
    }

    public get_message_records_args deepCopy() {
      return new get_message_records_args(this);
    }

    @Override
    public void clear() {
      this.query = null;
      setStartIsSet(false);
      this.start = 0;
      setRowsIsSet(false);
      this.rows = 0;
    }

    public String getQuery() {
      return this.query;
    }

    public get_message_records_args setQuery(String query) {
      this.query = query;
      return this;
    }

    public void unsetQuery() {
      this.query = null;
    }

    /** Returns true if field query is set (has been assigned a value) and false otherwise */
    public boolean isSetQuery() {
      return this.query != null;
    }

    public void setQueryIsSet(boolean value) {
      if (!value) {
        this.query = null;
      }
    }

    public int getStart() {
      return this.start;
    }

    public get_message_records_args setStart(int start) {
      this.start = start;
      setStartIsSet(true);
      return this;
    }

    public void unsetStart() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __START_ISSET_ID);
    }

    /** Returns true if field start is set (has been assigned a value) and false otherwise */
    public boolean isSetStart() {
      return EncodingUtils.testBit(__isset_bitfield, __START_ISSET_ID);
    }

    public void setStartIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __START_ISSET_ID, value);
    }

    public int getRows() {
      return this.rows;
    }

    public get_message_records_args setRows(int rows) {
      this.rows = rows;
      setRowsIsSet(true);
      return this;
    }

    public void unsetRows() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ROWS_ISSET_ID);
    }

    /** Returns true if field rows is set (has been assigned a value) and false otherwise */
    public boolean isSetRows() {
      return EncodingUtils.testBit(__isset_bitfield, __ROWS_ISSET_ID);
    }

    public void setRowsIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ROWS_ISSET_ID, value);
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case QUERY:
        if (value == null) {
          unsetQuery();
        } else {
          setQuery((String)value);
        }
        break;

      case START:
        if (value == null) {
          unsetStart();
        } else {
          setStart((Integer)value);
        }
        break;

      case ROWS:
        if (value == null) {
          unsetRows();
        } else {
          setRows((Integer)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case QUERY:
        return getQuery();

      case START:
        return Integer.valueOf(getStart());

      case ROWS:
        return Integer.valueOf(getRows());

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case QUERY:
        return isSetQuery();
      case START:
        return isSetStart();
      case ROWS:
        return isSetRows();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof get_message_records_args)
        return this.equals((get_message_records_args)that);
      return false;
    }

    public boolean equals(get_message_records_args that) {
      if (that == null)
        return false;

      boolean this_present_query = true && this.isSetQuery();
      boolean that_present_query = true && that.isSetQuery();
      if (this_present_query || that_present_query) {
        if (!(this_present_query && that_present_query))
          return false;
        if (!this.query.equals(that.query))
          return false;
      }

      boolean this_present_start = true;
      boolean that_present_start = true;
      if (this_present_start || that_present_start) {
        if (!(this_present_start && that_present_start))
          return false;
        if (this.start != that.start)
          return false;
      }

      boolean this_present_rows = true;
      boolean that_present_rows = true;
      if (this_present_rows || that_present_rows) {
        if (!(this_present_rows && that_present_rows))
          return false;
        if (this.rows != that.rows)
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(get_message_records_args other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      get_message_records_args typedOther = (get_message_records_args)other;

      lastComparison = Boolean.valueOf(isSetQuery()).compareTo(typedOther.isSetQuery());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetQuery()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.query, typedOther.query);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetStart()).compareTo(typedOther.isSetStart());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetStart()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.start, typedOther.start);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetRows()).compareTo(typedOther.isSetRows());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetRows()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rows, typedOther.rows);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("get_message_records_args(");
      boolean first = true;

      sb.append("query:");
      if (this.query == null) {
        sb.append("null");
      } else {
        sb.append(this.query);
      }
      first = false;
      if (!first) sb.append(", ");
      sb.append("start:");
      sb.append(this.start);
      first = false;
      if (!first) sb.append(", ");
      sb.append("rows:");
      sb.append(this.rows);
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
      // check for sub-struct validity
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
        __isset_bitfield = 0;
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class get_message_records_argsStandardSchemeFactory implements SchemeFactory {
      public get_message_records_argsStandardScheme getScheme() {
        return new get_message_records_argsStandardScheme();
      }
    }

    private static class get_message_records_argsStandardScheme extends StandardScheme<get_message_records_args> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, get_message_records_args struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 1: // QUERY
              if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
                struct.query = iprot.readString();
                struct.setQueryIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 2: // START
              if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                struct.start = iprot.readI32();
                struct.setStartIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 3: // ROWS
              if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                struct.rows = iprot.readI32();
                struct.setRowsIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, get_message_records_args struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.query != null) {
          oprot.writeFieldBegin(QUERY_FIELD_DESC);
          oprot.writeString(struct.query);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(START_FIELD_DESC);
        oprot.writeI32(struct.start);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(ROWS_FIELD_DESC);
        oprot.writeI32(struct.rows);
        oprot.writeFieldEnd();
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class get_message_records_argsTupleSchemeFactory implements SchemeFactory {
      public get_message_records_argsTupleScheme getScheme() {
        return new get_message_records_argsTupleScheme();
      }
    }

    private static class get_message_records_argsTupleScheme extends TupleScheme<get_message_records_args> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, get_message_records_args struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetQuery()) {
          optionals.set(0);
        }
        if (struct.isSetStart()) {
          optionals.set(1);
        }
        if (struct.isSetRows()) {
          optionals.set(2);
        }
        oprot.writeBitSet(optionals, 3);
        if (struct.isSetQuery()) {
          oprot.writeString(struct.query);
        }
        if (struct.isSetStart()) {
          oprot.writeI32(struct.start);
        }
        if (struct.isSetRows()) {
          oprot.writeI32(struct.rows);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, get_message_records_args struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(3);
        if (incoming.get(0)) {
          struct.query = iprot.readString();
          struct.setQueryIsSet(true);
        }
        if (incoming.get(1)) {
          struct.start = iprot.readI32();
          struct.setStartIsSet(true);
        }
        if (incoming.get(2)) {
          struct.rows = iprot.readI32();
          struct.setRowsIsSet(true);
        }
      }
    }

  }

  public static class get_message_records_result implements org.apache.thrift.TBase<get_message_records_result, get_message_records_result._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("get_message_records_result");

    private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("success", org.apache.thrift.protocol.TType.STRUCT, (short)0);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new get_message_records_resultStandardSchemeFactory());
      schemes.put(TupleScheme.class, new get_message_records_resultTupleSchemeFactory());
    }

    public ThriftQueryResponse success; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      SUCCESS((short)0, "success");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 0: // SUCCESS
            return SUCCESS;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("success", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ThriftQueryResponse.class)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(get_message_records_result.class, metaDataMap);
    }

    public get_message_records_result() {
    }

    public get_message_records_result(
      ThriftQueryResponse success)
    {
      this();
      this.success = success;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public get_message_records_result(get_message_records_result other) {
      if (other.isSetSuccess()) {
        this.success = new ThriftQueryResponse(other.success);
      }
    }

    public get_message_records_result deepCopy() {
      return new get_message_records_result(this);
    }

    @Override
    public void clear() {
      this.success = null;
    }

    public ThriftQueryResponse getSuccess() {
      return this.success;
    }

    public get_message_records_result setSuccess(ThriftQueryResponse success) {
      this.success = success;
      return this;
    }

    public void unsetSuccess() {
      this.success = null;
    }

    /** Returns true if field success is set (has been assigned a value) and false otherwise */
    public boolean isSetSuccess() {
      return this.success != null;
    }

    public void setSuccessIsSet(boolean value) {
      if (!value) {
        this.success = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case SUCCESS:
        if (value == null) {
          unsetSuccess();
        } else {
          setSuccess((ThriftQueryResponse)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case SUCCESS:
        return getSuccess();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case SUCCESS:
        return isSetSuccess();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof get_message_records_result)
        return this.equals((get_message_records_result)that);
      return false;
    }

    public boolean equals(get_message_records_result that) {
      if (that == null)
        return false;

      boolean this_present_success = true && this.isSetSuccess();
      boolean that_present_success = true && that.isSetSuccess();
      if (this_present_success || that_present_success) {
        if (!(this_present_success && that_present_success))
          return false;
        if (!this.success.equals(that.success))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(get_message_records_result other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      get_message_records_result typedOther = (get_message_records_result)other;

      lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(typedOther.isSetSuccess());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetSuccess()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.success, typedOther.success);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
      }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("get_message_records_result(");
      boolean first = true;

      sb.append("success:");
      if (this.success == null) {
        sb.append("null");
      } else {
        sb.append(this.success);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
      // check for sub-struct validity
      if (success != null) {
        success.validate();
      }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class get_message_records_resultStandardSchemeFactory implements SchemeFactory {
      public get_message_records_resultStandardScheme getScheme() {
        return new get_message_records_resultStandardScheme();
      }
    }

    private static class get_message_records_resultStandardScheme extends StandardScheme<get_message_records_result> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, get_message_records_result struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 0: // SUCCESS
              if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
                struct.success = new ThriftQueryResponse();
                struct.success.read(iprot);
                struct.setSuccessIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, get_message_records_result struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.success != null) {
          oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
          struct.success.write(oprot);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class get_message_records_resultTupleSchemeFactory implements SchemeFactory {
      public get_message_records_resultTupleScheme getScheme() {
        return new get_message_records_resultTupleScheme();
      }
    }

    private static class get_message_records_resultTupleScheme extends TupleScheme<get_message_records_result> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, get_message_records_result struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetSuccess()) {
          optionals.set(0);
        }
        oprot.writeBitSet(optionals, 1);
        if (struct.isSetSuccess()) {
          struct.success.write(oprot);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, get_message_records_result struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(1);
        if (incoming.get(0)) {
          struct.success = new ThriftQueryResponse();
          struct.success.read(iprot);
          struct.setSuccessIsSet(true);
        }
      }
    }

  }

  public static class get_message_records_by_date_range_args implements org.apache.thrift.TBase<get_message_records_by_date_range_args, get_message_records_by_date_range_args._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("get_message_records_by_date_range_args");

    private static final org.apache.thrift.protocol.TField QUERY_FIELD_DESC = new org.apache.thrift.protocol.TField("query", org.apache.thrift.protocol.TType.STRING, (short)1);
    private static final org.apache.thrift.protocol.TField START_FIELD_DESC = new org.apache.thrift.protocol.TField("start", org.apache.thrift.protocol.TType.I32, (short)2);
    private static final org.apache.thrift.protocol.TField ROWS_FIELD_DESC = new org.apache.thrift.protocol.TField("rows", org.apache.thrift.protocol.TType.I32, (short)3);
    private static final org.apache.thrift.protocol.TField START_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("startTime", org.apache.thrift.protocol.TType.I64, (short)4);
    private static final org.apache.thrift.protocol.TField END_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("endTime", org.apache.thrift.protocol.TType.I64, (short)5);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new get_message_records_by_date_range_argsStandardSchemeFactory());
      schemes.put(TupleScheme.class, new get_message_records_by_date_range_argsTupleSchemeFactory());
    }

    public String query; // required
    public int start; // required
    public int rows; // required
    public long startTime; // required
    public long endTime; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      QUERY((short)1, "query"),
      START((short)2, "start"),
      ROWS((short)3, "rows"),
      START_TIME((short)4, "startTime"),
      END_TIME((short)5, "endTime");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 1: // QUERY
            return QUERY;
          case 2: // START
            return START;
          case 3: // ROWS
            return ROWS;
          case 4: // START_TIME
            return START_TIME;
          case 5: // END_TIME
            return END_TIME;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    private static final int __START_ISSET_ID = 0;
    private static final int __ROWS_ISSET_ID = 1;
    private static final int __STARTTIME_ISSET_ID = 2;
    private static final int __ENDTIME_ISSET_ID = 3;
    private byte __isset_bitfield = 0;
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.QUERY, new org.apache.thrift.meta_data.FieldMetaData("query", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
      tmpMap.put(_Fields.START, new org.apache.thrift.meta_data.FieldMetaData("start", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
      tmpMap.put(_Fields.ROWS, new org.apache.thrift.meta_data.FieldMetaData("rows", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
      tmpMap.put(_Fields.START_TIME, new org.apache.thrift.meta_data.FieldMetaData("startTime", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
      tmpMap.put(_Fields.END_TIME, new org.apache.thrift.meta_data.FieldMetaData("endTime", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(get_message_records_by_date_range_args.class, metaDataMap);
    }

    public get_message_records_by_date_range_args() {
    }

    public get_message_records_by_date_range_args(
      String query,
      int start,
      int rows,
      long startTime,
      long endTime)
    {
      this();
      this.query = query;
      this.start = start;
      setStartIsSet(true);
      this.rows = rows;
      setRowsIsSet(true);
      this.startTime = startTime;
      setStartTimeIsSet(true);
      this.endTime = endTime;
      setEndTimeIsSet(true);
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public get_message_records_by_date_range_args(get_message_records_by_date_range_args other) {
      __isset_bitfield = other.__isset_bitfield;
      if (other.isSetQuery()) {
        this.query = other.query;
      }
      this.start = other.start;
      this.rows = other.rows;
      this.startTime = other.startTime;
      this.endTime = other.endTime;
    }

    public get_message_records_by_date_range_args deepCopy() {
      return new get_message_records_by_date_range_args(this);
    }

    @Override
    public void clear() {
      this.query = null;
      setStartIsSet(false);
      this.start = 0;
      setRowsIsSet(false);
      this.rows = 0;
      setStartTimeIsSet(false);
      this.startTime = 0;
      setEndTimeIsSet(false);
      this.endTime = 0;
    }

    public String getQuery() {
      return this.query;
    }

    public get_message_records_by_date_range_args setQuery(String query) {
      this.query = query;
      return this;
    }

    public void unsetQuery() {
      this.query = null;
    }

    /** Returns true if field query is set (has been assigned a value) and false otherwise */
    public boolean isSetQuery() {
      return this.query != null;
    }

    public void setQueryIsSet(boolean value) {
      if (!value) {
        this.query = null;
      }
    }

    public int getStart() {
      return this.start;
    }

    public get_message_records_by_date_range_args setStart(int start) {
      this.start = start;
      setStartIsSet(true);
      return this;
    }

    public void unsetStart() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __START_ISSET_ID);
    }

    /** Returns true if field start is set (has been assigned a value) and false otherwise */
    public boolean isSetStart() {
      return EncodingUtils.testBit(__isset_bitfield, __START_ISSET_ID);
    }

    public void setStartIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __START_ISSET_ID, value);
    }

    public int getRows() {
      return this.rows;
    }

    public get_message_records_by_date_range_args setRows(int rows) {
      this.rows = rows;
      setRowsIsSet(true);
      return this;
    }

    public void unsetRows() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ROWS_ISSET_ID);
    }

    /** Returns true if field rows is set (has been assigned a value) and false otherwise */
    public boolean isSetRows() {
      return EncodingUtils.testBit(__isset_bitfield, __ROWS_ISSET_ID);
    }

    public void setRowsIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ROWS_ISSET_ID, value);
    }

    public long getStartTime() {
      return this.startTime;
    }

    public get_message_records_by_date_range_args setStartTime(long startTime) {
      this.startTime = startTime;
      setStartTimeIsSet(true);
      return this;
    }

    public void unsetStartTime() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __STARTTIME_ISSET_ID);
    }

    /** Returns true if field startTime is set (has been assigned a value) and false otherwise */
    public boolean isSetStartTime() {
      return EncodingUtils.testBit(__isset_bitfield, __STARTTIME_ISSET_ID);
    }

    public void setStartTimeIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __STARTTIME_ISSET_ID, value);
    }

    public long getEndTime() {
      return this.endTime;
    }

    public get_message_records_by_date_range_args setEndTime(long endTime) {
      this.endTime = endTime;
      setEndTimeIsSet(true);
      return this;
    }

    public void unsetEndTime() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ENDTIME_ISSET_ID);
    }

    /** Returns true if field endTime is set (has been assigned a value) and false otherwise */
    public boolean isSetEndTime() {
      return EncodingUtils.testBit(__isset_bitfield, __ENDTIME_ISSET_ID);
    }

    public void setEndTimeIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ENDTIME_ISSET_ID, value);
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case QUERY:
        if (value == null) {
          unsetQuery();
        } else {
          setQuery((String)value);
        }
        break;

      case START:
        if (value == null) {
          unsetStart();
        } else {
          setStart((Integer)value);
        }
        break;

      case ROWS:
        if (value == null) {
          unsetRows();
        } else {
          setRows((Integer)value);
        }
        break;

      case START_TIME:
        if (value == null) {
          unsetStartTime();
        } else {
          setStartTime((Long)value);
        }
        break;

      case END_TIME:
        if (value == null) {
          unsetEndTime();
        } else {
          setEndTime((Long)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case QUERY:
        return getQuery();

      case START:
        return Integer.valueOf(getStart());

      case ROWS:
        return Integer.valueOf(getRows());

      case START_TIME:
        return Long.valueOf(getStartTime());

      case END_TIME:
        return Long.valueOf(getEndTime());

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case QUERY:
        return isSetQuery();
      case START:
        return isSetStart();
      case ROWS:
        return isSetRows();
      case START_TIME:
        return isSetStartTime();
      case END_TIME:
        return isSetEndTime();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof get_message_records_by_date_range_args)
        return this.equals((get_message_records_by_date_range_args)that);
      return false;
    }

    public boolean equals(get_message_records_by_date_range_args that) {
      if (that == null)
        return false;

      boolean this_present_query = true && this.isSetQuery();
      boolean that_present_query = true && that.isSetQuery();
      if (this_present_query || that_present_query) {
        if (!(this_present_query && that_present_query))
          return false;
        if (!this.query.equals(that.query))
          return false;
      }

      boolean this_present_start = true;
      boolean that_present_start = true;
      if (this_present_start || that_present_start) {
        if (!(this_present_start && that_present_start))
          return false;
        if (this.start != that.start)
          return false;
      }

      boolean this_present_rows = true;
      boolean that_present_rows = true;
      if (this_present_rows || that_present_rows) {
        if (!(this_present_rows && that_present_rows))
          return false;
        if (this.rows != that.rows)
          return false;
      }

      boolean this_present_startTime = true;
      boolean that_present_startTime = true;
      if (this_present_startTime || that_present_startTime) {
        if (!(this_present_startTime && that_present_startTime))
          return false;
        if (this.startTime != that.startTime)
          return false;
      }

      boolean this_present_endTime = true;
      boolean that_present_endTime = true;
      if (this_present_endTime || that_present_endTime) {
        if (!(this_present_endTime && that_present_endTime))
          return false;
        if (this.endTime != that.endTime)
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(get_message_records_by_date_range_args other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      get_message_records_by_date_range_args typedOther = (get_message_records_by_date_range_args)other;

      lastComparison = Boolean.valueOf(isSetQuery()).compareTo(typedOther.isSetQuery());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetQuery()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.query, typedOther.query);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetStart()).compareTo(typedOther.isSetStart());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetStart()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.start, typedOther.start);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetRows()).compareTo(typedOther.isSetRows());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetRows()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rows, typedOther.rows);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetStartTime()).compareTo(typedOther.isSetStartTime());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetStartTime()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.startTime, typedOther.startTime);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetEndTime()).compareTo(typedOther.isSetEndTime());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetEndTime()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.endTime, typedOther.endTime);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("get_message_records_by_date_range_args(");
      boolean first = true;

      sb.append("query:");
      if (this.query == null) {
        sb.append("null");
      } else {
        sb.append(this.query);
      }
      first = false;
      if (!first) sb.append(", ");
      sb.append("start:");
      sb.append(this.start);
      first = false;
      if (!first) sb.append(", ");
      sb.append("rows:");
      sb.append(this.rows);
      first = false;
      if (!first) sb.append(", ");
      sb.append("startTime:");
      sb.append(this.startTime);
      first = false;
      if (!first) sb.append(", ");
      sb.append("endTime:");
      sb.append(this.endTime);
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
      // check for sub-struct validity
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
        __isset_bitfield = 0;
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class get_message_records_by_date_range_argsStandardSchemeFactory implements SchemeFactory {
      public get_message_records_by_date_range_argsStandardScheme getScheme() {
        return new get_message_records_by_date_range_argsStandardScheme();
      }
    }

    private static class get_message_records_by_date_range_argsStandardScheme extends StandardScheme<get_message_records_by_date_range_args> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, get_message_records_by_date_range_args struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 1: // QUERY
              if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
                struct.query = iprot.readString();
                struct.setQueryIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 2: // START
              if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                struct.start = iprot.readI32();
                struct.setStartIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 3: // ROWS
              if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                struct.rows = iprot.readI32();
                struct.setRowsIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 4: // START_TIME
              if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
                struct.startTime = iprot.readI64();
                struct.setStartTimeIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 5: // END_TIME
              if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
                struct.endTime = iprot.readI64();
                struct.setEndTimeIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, get_message_records_by_date_range_args struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.query != null) {
          oprot.writeFieldBegin(QUERY_FIELD_DESC);
          oprot.writeString(struct.query);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(START_FIELD_DESC);
        oprot.writeI32(struct.start);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(ROWS_FIELD_DESC);
        oprot.writeI32(struct.rows);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(START_TIME_FIELD_DESC);
        oprot.writeI64(struct.startTime);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(END_TIME_FIELD_DESC);
        oprot.writeI64(struct.endTime);
        oprot.writeFieldEnd();
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class get_message_records_by_date_range_argsTupleSchemeFactory implements SchemeFactory {
      public get_message_records_by_date_range_argsTupleScheme getScheme() {
        return new get_message_records_by_date_range_argsTupleScheme();
      }
    }

    private static class get_message_records_by_date_range_argsTupleScheme extends TupleScheme<get_message_records_by_date_range_args> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, get_message_records_by_date_range_args struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetQuery()) {
          optionals.set(0);
        }
        if (struct.isSetStart()) {
          optionals.set(1);
        }
        if (struct.isSetRows()) {
          optionals.set(2);
        }
        if (struct.isSetStartTime()) {
          optionals.set(3);
        }
        if (struct.isSetEndTime()) {
          optionals.set(4);
        }
        oprot.writeBitSet(optionals, 5);
        if (struct.isSetQuery()) {
          oprot.writeString(struct.query);
        }
        if (struct.isSetStart()) {
          oprot.writeI32(struct.start);
        }
        if (struct.isSetRows()) {
          oprot.writeI32(struct.rows);
        }
        if (struct.isSetStartTime()) {
          oprot.writeI64(struct.startTime);
        }
        if (struct.isSetEndTime()) {
          oprot.writeI64(struct.endTime);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, get_message_records_by_date_range_args struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(5);
        if (incoming.get(0)) {
          struct.query = iprot.readString();
          struct.setQueryIsSet(true);
        }
        if (incoming.get(1)) {
          struct.start = iprot.readI32();
          struct.setStartIsSet(true);
        }
        if (incoming.get(2)) {
          struct.rows = iprot.readI32();
          struct.setRowsIsSet(true);
        }
        if (incoming.get(3)) {
          struct.startTime = iprot.readI64();
          struct.setStartTimeIsSet(true);
        }
        if (incoming.get(4)) {
          struct.endTime = iprot.readI64();
          struct.setEndTimeIsSet(true);
        }
      }
    }

  }

  public static class get_message_records_by_date_range_result implements org.apache.thrift.TBase<get_message_records_by_date_range_result, get_message_records_by_date_range_result._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("get_message_records_by_date_range_result");

    private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("success", org.apache.thrift.protocol.TType.STRUCT, (short)0);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new get_message_records_by_date_range_resultStandardSchemeFactory());
      schemes.put(TupleScheme.class, new get_message_records_by_date_range_resultTupleSchemeFactory());
    }

    public ThriftQueryResponse success; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      SUCCESS((short)0, "success");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 0: // SUCCESS
            return SUCCESS;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("success", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ThriftQueryResponse.class)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(get_message_records_by_date_range_result.class, metaDataMap);
    }

    public get_message_records_by_date_range_result() {
    }

    public get_message_records_by_date_range_result(
      ThriftQueryResponse success)
    {
      this();
      this.success = success;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public get_message_records_by_date_range_result(get_message_records_by_date_range_result other) {
      if (other.isSetSuccess()) {
        this.success = new ThriftQueryResponse(other.success);
      }
    }

    public get_message_records_by_date_range_result deepCopy() {
      return new get_message_records_by_date_range_result(this);
    }

    @Override
    public void clear() {
      this.success = null;
    }

    public ThriftQueryResponse getSuccess() {
      return this.success;
    }

    public get_message_records_by_date_range_result setSuccess(ThriftQueryResponse success) {
      this.success = success;
      return this;
    }

    public void unsetSuccess() {
      this.success = null;
    }

    /** Returns true if field success is set (has been assigned a value) and false otherwise */
    public boolean isSetSuccess() {
      return this.success != null;
    }

    public void setSuccessIsSet(boolean value) {
      if (!value) {
        this.success = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case SUCCESS:
        if (value == null) {
          unsetSuccess();
        } else {
          setSuccess((ThriftQueryResponse)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case SUCCESS:
        return getSuccess();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case SUCCESS:
        return isSetSuccess();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof get_message_records_by_date_range_result)
        return this.equals((get_message_records_by_date_range_result)that);
      return false;
    }

    public boolean equals(get_message_records_by_date_range_result that) {
      if (that == null)
        return false;

      boolean this_present_success = true && this.isSetSuccess();
      boolean that_present_success = true && that.isSetSuccess();
      if (this_present_success || that_present_success) {
        if (!(this_present_success && that_present_success))
          return false;
        if (!this.success.equals(that.success))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(get_message_records_by_date_range_result other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      get_message_records_by_date_range_result typedOther = (get_message_records_by_date_range_result)other;

      lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(typedOther.isSetSuccess());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetSuccess()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.success, typedOther.success);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
      }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("get_message_records_by_date_range_result(");
      boolean first = true;

      sb.append("success:");
      if (this.success == null) {
        sb.append("null");
      } else {
        sb.append(this.success);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
      // check for sub-struct validity
      if (success != null) {
        success.validate();
      }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class get_message_records_by_date_range_resultStandardSchemeFactory implements SchemeFactory {
      public get_message_records_by_date_range_resultStandardScheme getScheme() {
        return new get_message_records_by_date_range_resultStandardScheme();
      }
    }

    private static class get_message_records_by_date_range_resultStandardScheme extends StandardScheme<get_message_records_by_date_range_result> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, get_message_records_by_date_range_result struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 0: // SUCCESS
              if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
                struct.success = new ThriftQueryResponse();
                struct.success.read(iprot);
                struct.setSuccessIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, get_message_records_by_date_range_result struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.success != null) {
          oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
          struct.success.write(oprot);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class get_message_records_by_date_range_resultTupleSchemeFactory implements SchemeFactory {
      public get_message_records_by_date_range_resultTupleScheme getScheme() {
        return new get_message_records_by_date_range_resultTupleScheme();
      }
    }

    private static class get_message_records_by_date_range_resultTupleScheme extends TupleScheme<get_message_records_by_date_range_result> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, get_message_records_by_date_range_result struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetSuccess()) {
          optionals.set(0);
        }
        oprot.writeBitSet(optionals, 1);
        if (struct.isSetSuccess()) {
          struct.success.write(oprot);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, get_message_records_by_date_range_result struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(1);
        if (incoming.get(0)) {
          struct.success = new ThriftQueryResponse();
          struct.success.read(iprot);
          struct.setSuccessIsSet(true);
        }
      }
    }

  }

  public static class get_message_records_by_date_range_and_source_args implements org.apache.thrift.TBase<get_message_records_by_date_range_and_source_args, get_message_records_by_date_range_and_source_args._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("get_message_records_by_date_range_and_source_args");

    private static final org.apache.thrift.protocol.TField QUERY_FIELD_DESC = new org.apache.thrift.protocol.TField("query", org.apache.thrift.protocol.TType.STRING, (short)1);
    private static final org.apache.thrift.protocol.TField START_FIELD_DESC = new org.apache.thrift.protocol.TField("start", org.apache.thrift.protocol.TType.I32, (short)2);
    private static final org.apache.thrift.protocol.TField ROWS_FIELD_DESC = new org.apache.thrift.protocol.TField("rows", org.apache.thrift.protocol.TType.I32, (short)3);
    private static final org.apache.thrift.protocol.TField START_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("startTime", org.apache.thrift.protocol.TType.I64, (short)4);
    private static final org.apache.thrift.protocol.TField END_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("endTime", org.apache.thrift.protocol.TType.I64, (short)5);
    private static final org.apache.thrift.protocol.TField SOURCE_FIELD_DESC = new org.apache.thrift.protocol.TField("source", org.apache.thrift.protocol.TType.STRING, (short)6);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new get_message_records_by_date_range_and_source_argsStandardSchemeFactory());
      schemes.put(TupleScheme.class, new get_message_records_by_date_range_and_source_argsTupleSchemeFactory());
    }

    public String query; // required
    public int start; // required
    public int rows; // required
    public long startTime; // required
    public long endTime; // required
    public String source; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      QUERY((short)1, "query"),
      START((short)2, "start"),
      ROWS((short)3, "rows"),
      START_TIME((short)4, "startTime"),
      END_TIME((short)5, "endTime"),
      SOURCE((short)6, "source");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 1: // QUERY
            return QUERY;
          case 2: // START
            return START;
          case 3: // ROWS
            return ROWS;
          case 4: // START_TIME
            return START_TIME;
          case 5: // END_TIME
            return END_TIME;
          case 6: // SOURCE
            return SOURCE;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    private static final int __START_ISSET_ID = 0;
    private static final int __ROWS_ISSET_ID = 1;
    private static final int __STARTTIME_ISSET_ID = 2;
    private static final int __ENDTIME_ISSET_ID = 3;
    private byte __isset_bitfield = 0;
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.QUERY, new org.apache.thrift.meta_data.FieldMetaData("query", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
      tmpMap.put(_Fields.START, new org.apache.thrift.meta_data.FieldMetaData("start", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
      tmpMap.put(_Fields.ROWS, new org.apache.thrift.meta_data.FieldMetaData("rows", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
      tmpMap.put(_Fields.START_TIME, new org.apache.thrift.meta_data.FieldMetaData("startTime", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
      tmpMap.put(_Fields.END_TIME, new org.apache.thrift.meta_data.FieldMetaData("endTime", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
      tmpMap.put(_Fields.SOURCE, new org.apache.thrift.meta_data.FieldMetaData("source", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(get_message_records_by_date_range_and_source_args.class, metaDataMap);
    }

    public get_message_records_by_date_range_and_source_args() {
    }

    public get_message_records_by_date_range_and_source_args(
      String query,
      int start,
      int rows,
      long startTime,
      long endTime,
      String source)
    {
      this();
      this.query = query;
      this.start = start;
      setStartIsSet(true);
      this.rows = rows;
      setRowsIsSet(true);
      this.startTime = startTime;
      setStartTimeIsSet(true);
      this.endTime = endTime;
      setEndTimeIsSet(true);
      this.source = source;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public get_message_records_by_date_range_and_source_args(get_message_records_by_date_range_and_source_args other) {
      __isset_bitfield = other.__isset_bitfield;
      if (other.isSetQuery()) {
        this.query = other.query;
      }
      this.start = other.start;
      this.rows = other.rows;
      this.startTime = other.startTime;
      this.endTime = other.endTime;
      if (other.isSetSource()) {
        this.source = other.source;
      }
    }

    public get_message_records_by_date_range_and_source_args deepCopy() {
      return new get_message_records_by_date_range_and_source_args(this);
    }

    @Override
    public void clear() {
      this.query = null;
      setStartIsSet(false);
      this.start = 0;
      setRowsIsSet(false);
      this.rows = 0;
      setStartTimeIsSet(false);
      this.startTime = 0;
      setEndTimeIsSet(false);
      this.endTime = 0;
      this.source = null;
    }

    public String getQuery() {
      return this.query;
    }

    public get_message_records_by_date_range_and_source_args setQuery(String query) {
      this.query = query;
      return this;
    }

    public void unsetQuery() {
      this.query = null;
    }

    /** Returns true if field query is set (has been assigned a value) and false otherwise */
    public boolean isSetQuery() {
      return this.query != null;
    }

    public void setQueryIsSet(boolean value) {
      if (!value) {
        this.query = null;
      }
    }

    public int getStart() {
      return this.start;
    }

    public get_message_records_by_date_range_and_source_args setStart(int start) {
      this.start = start;
      setStartIsSet(true);
      return this;
    }

    public void unsetStart() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __START_ISSET_ID);
    }

    /** Returns true if field start is set (has been assigned a value) and false otherwise */
    public boolean isSetStart() {
      return EncodingUtils.testBit(__isset_bitfield, __START_ISSET_ID);
    }

    public void setStartIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __START_ISSET_ID, value);
    }

    public int getRows() {
      return this.rows;
    }

    public get_message_records_by_date_range_and_source_args setRows(int rows) {
      this.rows = rows;
      setRowsIsSet(true);
      return this;
    }

    public void unsetRows() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ROWS_ISSET_ID);
    }

    /** Returns true if field rows is set (has been assigned a value) and false otherwise */
    public boolean isSetRows() {
      return EncodingUtils.testBit(__isset_bitfield, __ROWS_ISSET_ID);
    }

    public void setRowsIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ROWS_ISSET_ID, value);
    }

    public long getStartTime() {
      return this.startTime;
    }

    public get_message_records_by_date_range_and_source_args setStartTime(long startTime) {
      this.startTime = startTime;
      setStartTimeIsSet(true);
      return this;
    }

    public void unsetStartTime() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __STARTTIME_ISSET_ID);
    }

    /** Returns true if field startTime is set (has been assigned a value) and false otherwise */
    public boolean isSetStartTime() {
      return EncodingUtils.testBit(__isset_bitfield, __STARTTIME_ISSET_ID);
    }

    public void setStartTimeIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __STARTTIME_ISSET_ID, value);
    }

    public long getEndTime() {
      return this.endTime;
    }

    public get_message_records_by_date_range_and_source_args setEndTime(long endTime) {
      this.endTime = endTime;
      setEndTimeIsSet(true);
      return this;
    }

    public void unsetEndTime() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ENDTIME_ISSET_ID);
    }

    /** Returns true if field endTime is set (has been assigned a value) and false otherwise */
    public boolean isSetEndTime() {
      return EncodingUtils.testBit(__isset_bitfield, __ENDTIME_ISSET_ID);
    }

    public void setEndTimeIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ENDTIME_ISSET_ID, value);
    }

    public String getSource() {
      return this.source;
    }

    public get_message_records_by_date_range_and_source_args setSource(String source) {
      this.source = source;
      return this;
    }

    public void unsetSource() {
      this.source = null;
    }

    /** Returns true if field source is set (has been assigned a value) and false otherwise */
    public boolean isSetSource() {
      return this.source != null;
    }

    public void setSourceIsSet(boolean value) {
      if (!value) {
        this.source = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case QUERY:
        if (value == null) {
          unsetQuery();
        } else {
          setQuery((String)value);
        }
        break;

      case START:
        if (value == null) {
          unsetStart();
        } else {
          setStart((Integer)value);
        }
        break;

      case ROWS:
        if (value == null) {
          unsetRows();
        } else {
          setRows((Integer)value);
        }
        break;

      case START_TIME:
        if (value == null) {
          unsetStartTime();
        } else {
          setStartTime((Long)value);
        }
        break;

      case END_TIME:
        if (value == null) {
          unsetEndTime();
        } else {
          setEndTime((Long)value);
        }
        break;

      case SOURCE:
        if (value == null) {
          unsetSource();
        } else {
          setSource((String)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case QUERY:
        return getQuery();

      case START:
        return Integer.valueOf(getStart());

      case ROWS:
        return Integer.valueOf(getRows());

      case START_TIME:
        return Long.valueOf(getStartTime());

      case END_TIME:
        return Long.valueOf(getEndTime());

      case SOURCE:
        return getSource();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case QUERY:
        return isSetQuery();
      case START:
        return isSetStart();
      case ROWS:
        return isSetRows();
      case START_TIME:
        return isSetStartTime();
      case END_TIME:
        return isSetEndTime();
      case SOURCE:
        return isSetSource();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof get_message_records_by_date_range_and_source_args)
        return this.equals((get_message_records_by_date_range_and_source_args)that);
      return false;
    }

    public boolean equals(get_message_records_by_date_range_and_source_args that) {
      if (that == null)
        return false;

      boolean this_present_query = true && this.isSetQuery();
      boolean that_present_query = true && that.isSetQuery();
      if (this_present_query || that_present_query) {
        if (!(this_present_query && that_present_query))
          return false;
        if (!this.query.equals(that.query))
          return false;
      }

      boolean this_present_start = true;
      boolean that_present_start = true;
      if (this_present_start || that_present_start) {
        if (!(this_present_start && that_present_start))
          return false;
        if (this.start != that.start)
          return false;
      }

      boolean this_present_rows = true;
      boolean that_present_rows = true;
      if (this_present_rows || that_present_rows) {
        if (!(this_present_rows && that_present_rows))
          return false;
        if (this.rows != that.rows)
          return false;
      }

      boolean this_present_startTime = true;
      boolean that_present_startTime = true;
      if (this_present_startTime || that_present_startTime) {
        if (!(this_present_startTime && that_present_startTime))
          return false;
        if (this.startTime != that.startTime)
          return false;
      }

      boolean this_present_endTime = true;
      boolean that_present_endTime = true;
      if (this_present_endTime || that_present_endTime) {
        if (!(this_present_endTime && that_present_endTime))
          return false;
        if (this.endTime != that.endTime)
          return false;
      }

      boolean this_present_source = true && this.isSetSource();
      boolean that_present_source = true && that.isSetSource();
      if (this_present_source || that_present_source) {
        if (!(this_present_source && that_present_source))
          return false;
        if (!this.source.equals(that.source))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(get_message_records_by_date_range_and_source_args other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      get_message_records_by_date_range_and_source_args typedOther = (get_message_records_by_date_range_and_source_args)other;

      lastComparison = Boolean.valueOf(isSetQuery()).compareTo(typedOther.isSetQuery());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetQuery()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.query, typedOther.query);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetStart()).compareTo(typedOther.isSetStart());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetStart()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.start, typedOther.start);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetRows()).compareTo(typedOther.isSetRows());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetRows()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rows, typedOther.rows);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetStartTime()).compareTo(typedOther.isSetStartTime());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetStartTime()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.startTime, typedOther.startTime);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetEndTime()).compareTo(typedOther.isSetEndTime());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetEndTime()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.endTime, typedOther.endTime);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetSource()).compareTo(typedOther.isSetSource());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetSource()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.source, typedOther.source);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("get_message_records_by_date_range_and_source_args(");
      boolean first = true;

      sb.append("query:");
      if (this.query == null) {
        sb.append("null");
      } else {
        sb.append(this.query);
      }
      first = false;
      if (!first) sb.append(", ");
      sb.append("start:");
      sb.append(this.start);
      first = false;
      if (!first) sb.append(", ");
      sb.append("rows:");
      sb.append(this.rows);
      first = false;
      if (!first) sb.append(", ");
      sb.append("startTime:");
      sb.append(this.startTime);
      first = false;
      if (!first) sb.append(", ");
      sb.append("endTime:");
      sb.append(this.endTime);
      first = false;
      if (!first) sb.append(", ");
      sb.append("source:");
      if (this.source == null) {
        sb.append("null");
      } else {
        sb.append(this.source);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
      // check for sub-struct validity
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
        __isset_bitfield = 0;
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class get_message_records_by_date_range_and_source_argsStandardSchemeFactory implements SchemeFactory {
      public get_message_records_by_date_range_and_source_argsStandardScheme getScheme() {
        return new get_message_records_by_date_range_and_source_argsStandardScheme();
      }
    }

    private static class get_message_records_by_date_range_and_source_argsStandardScheme extends StandardScheme<get_message_records_by_date_range_and_source_args> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, get_message_records_by_date_range_and_source_args struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 1: // QUERY
              if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
                struct.query = iprot.readString();
                struct.setQueryIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 2: // START
              if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                struct.start = iprot.readI32();
                struct.setStartIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 3: // ROWS
              if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                struct.rows = iprot.readI32();
                struct.setRowsIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 4: // START_TIME
              if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
                struct.startTime = iprot.readI64();
                struct.setStartTimeIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 5: // END_TIME
              if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
                struct.endTime = iprot.readI64();
                struct.setEndTimeIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 6: // SOURCE
              if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
                struct.source = iprot.readString();
                struct.setSourceIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, get_message_records_by_date_range_and_source_args struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.query != null) {
          oprot.writeFieldBegin(QUERY_FIELD_DESC);
          oprot.writeString(struct.query);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(START_FIELD_DESC);
        oprot.writeI32(struct.start);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(ROWS_FIELD_DESC);
        oprot.writeI32(struct.rows);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(START_TIME_FIELD_DESC);
        oprot.writeI64(struct.startTime);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(END_TIME_FIELD_DESC);
        oprot.writeI64(struct.endTime);
        oprot.writeFieldEnd();
        if (struct.source != null) {
          oprot.writeFieldBegin(SOURCE_FIELD_DESC);
          oprot.writeString(struct.source);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class get_message_records_by_date_range_and_source_argsTupleSchemeFactory implements SchemeFactory {
      public get_message_records_by_date_range_and_source_argsTupleScheme getScheme() {
        return new get_message_records_by_date_range_and_source_argsTupleScheme();
      }
    }

    private static class get_message_records_by_date_range_and_source_argsTupleScheme extends TupleScheme<get_message_records_by_date_range_and_source_args> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, get_message_records_by_date_range_and_source_args struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetQuery()) {
          optionals.set(0);
        }
        if (struct.isSetStart()) {
          optionals.set(1);
        }
        if (struct.isSetRows()) {
          optionals.set(2);
        }
        if (struct.isSetStartTime()) {
          optionals.set(3);
        }
        if (struct.isSetEndTime()) {
          optionals.set(4);
        }
        if (struct.isSetSource()) {
          optionals.set(5);
        }
        oprot.writeBitSet(optionals, 6);
        if (struct.isSetQuery()) {
          oprot.writeString(struct.query);
        }
        if (struct.isSetStart()) {
          oprot.writeI32(struct.start);
        }
        if (struct.isSetRows()) {
          oprot.writeI32(struct.rows);
        }
        if (struct.isSetStartTime()) {
          oprot.writeI64(struct.startTime);
        }
        if (struct.isSetEndTime()) {
          oprot.writeI64(struct.endTime);
        }
        if (struct.isSetSource()) {
          oprot.writeString(struct.source);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, get_message_records_by_date_range_and_source_args struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(6);
        if (incoming.get(0)) {
          struct.query = iprot.readString();
          struct.setQueryIsSet(true);
        }
        if (incoming.get(1)) {
          struct.start = iprot.readI32();
          struct.setStartIsSet(true);
        }
        if (incoming.get(2)) {
          struct.rows = iprot.readI32();
          struct.setRowsIsSet(true);
        }
        if (incoming.get(3)) {
          struct.startTime = iprot.readI64();
          struct.setStartTimeIsSet(true);
        }
        if (incoming.get(4)) {
          struct.endTime = iprot.readI64();
          struct.setEndTimeIsSet(true);
        }
        if (incoming.get(5)) {
          struct.source = iprot.readString();
          struct.setSourceIsSet(true);
        }
      }
    }

  }

  public static class get_message_records_by_date_range_and_source_result implements org.apache.thrift.TBase<get_message_records_by_date_range_and_source_result, get_message_records_by_date_range_and_source_result._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("get_message_records_by_date_range_and_source_result");

    private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("success", org.apache.thrift.protocol.TType.STRUCT, (short)0);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new get_message_records_by_date_range_and_source_resultStandardSchemeFactory());
      schemes.put(TupleScheme.class, new get_message_records_by_date_range_and_source_resultTupleSchemeFactory());
    }

    public ThriftQueryResponse success; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      SUCCESS((short)0, "success");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 0: // SUCCESS
            return SUCCESS;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("success", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ThriftQueryResponse.class)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(get_message_records_by_date_range_and_source_result.class, metaDataMap);
    }

    public get_message_records_by_date_range_and_source_result() {
    }

    public get_message_records_by_date_range_and_source_result(
      ThriftQueryResponse success)
    {
      this();
      this.success = success;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public get_message_records_by_date_range_and_source_result(get_message_records_by_date_range_and_source_result other) {
      if (other.isSetSuccess()) {
        this.success = new ThriftQueryResponse(other.success);
      }
    }

    public get_message_records_by_date_range_and_source_result deepCopy() {
      return new get_message_records_by_date_range_and_source_result(this);
    }

    @Override
    public void clear() {
      this.success = null;
    }

    public ThriftQueryResponse getSuccess() {
      return this.success;
    }

    public get_message_records_by_date_range_and_source_result setSuccess(ThriftQueryResponse success) {
      this.success = success;
      return this;
    }

    public void unsetSuccess() {
      this.success = null;
    }

    /** Returns true if field success is set (has been assigned a value) and false otherwise */
    public boolean isSetSuccess() {
      return this.success != null;
    }

    public void setSuccessIsSet(boolean value) {
      if (!value) {
        this.success = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case SUCCESS:
        if (value == null) {
          unsetSuccess();
        } else {
          setSuccess((ThriftQueryResponse)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case SUCCESS:
        return getSuccess();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case SUCCESS:
        return isSetSuccess();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof get_message_records_by_date_range_and_source_result)
        return this.equals((get_message_records_by_date_range_and_source_result)that);
      return false;
    }

    public boolean equals(get_message_records_by_date_range_and_source_result that) {
      if (that == null)
        return false;

      boolean this_present_success = true && this.isSetSuccess();
      boolean that_present_success = true && that.isSetSuccess();
      if (this_present_success || that_present_success) {
        if (!(this_present_success && that_present_success))
          return false;
        if (!this.success.equals(that.success))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(get_message_records_by_date_range_and_source_result other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      get_message_records_by_date_range_and_source_result typedOther = (get_message_records_by_date_range_and_source_result)other;

      lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(typedOther.isSetSuccess());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetSuccess()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.success, typedOther.success);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
      }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("get_message_records_by_date_range_and_source_result(");
      boolean first = true;

      sb.append("success:");
      if (this.success == null) {
        sb.append("null");
      } else {
        sb.append(this.success);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
      // check for sub-struct validity
      if (success != null) {
        success.validate();
      }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class get_message_records_by_date_range_and_source_resultStandardSchemeFactory implements SchemeFactory {
      public get_message_records_by_date_range_and_source_resultStandardScheme getScheme() {
        return new get_message_records_by_date_range_and_source_resultStandardScheme();
      }
    }

    private static class get_message_records_by_date_range_and_source_resultStandardScheme extends StandardScheme<get_message_records_by_date_range_and_source_result> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, get_message_records_by_date_range_and_source_result struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 0: // SUCCESS
              if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
                struct.success = new ThriftQueryResponse();
                struct.success.read(iprot);
                struct.setSuccessIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, get_message_records_by_date_range_and_source_result struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.success != null) {
          oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
          struct.success.write(oprot);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class get_message_records_by_date_range_and_source_resultTupleSchemeFactory implements SchemeFactory {
      public get_message_records_by_date_range_and_source_resultTupleScheme getScheme() {
        return new get_message_records_by_date_range_and_source_resultTupleScheme();
      }
    }

    private static class get_message_records_by_date_range_and_source_resultTupleScheme extends TupleScheme<get_message_records_by_date_range_and_source_result> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, get_message_records_by_date_range_and_source_result struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetSuccess()) {
          optionals.set(0);
        }
        oprot.writeBitSet(optionals, 1);
        if (struct.isSetSuccess()) {
          struct.success.write(oprot);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, get_message_records_by_date_range_and_source_result struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(1);
        if (incoming.get(0)) {
          struct.success = new ThriftQueryResponse();
          struct.success.read(iprot);
          struct.setSuccessIsSet(true);
        }
      }
    }

  }

  public static class get_message_records_by_source_args implements org.apache.thrift.TBase<get_message_records_by_source_args, get_message_records_by_source_args._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("get_message_records_by_source_args");

    private static final org.apache.thrift.protocol.TField QUERY_FIELD_DESC = new org.apache.thrift.protocol.TField("query", org.apache.thrift.protocol.TType.STRING, (short)1);
    private static final org.apache.thrift.protocol.TField START_FIELD_DESC = new org.apache.thrift.protocol.TField("start", org.apache.thrift.protocol.TType.I32, (short)2);
    private static final org.apache.thrift.protocol.TField ROWS_FIELD_DESC = new org.apache.thrift.protocol.TField("rows", org.apache.thrift.protocol.TType.I32, (short)3);
    private static final org.apache.thrift.protocol.TField SOURCE_FIELD_DESC = new org.apache.thrift.protocol.TField("source", org.apache.thrift.protocol.TType.STRING, (short)6);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new get_message_records_by_source_argsStandardSchemeFactory());
      schemes.put(TupleScheme.class, new get_message_records_by_source_argsTupleSchemeFactory());
    }

    public String query; // required
    public int start; // required
    public int rows; // required
    public String source; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      QUERY((short)1, "query"),
      START((short)2, "start"),
      ROWS((short)3, "rows"),
      SOURCE((short)6, "source");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 1: // QUERY
            return QUERY;
          case 2: // START
            return START;
          case 3: // ROWS
            return ROWS;
          case 6: // SOURCE
            return SOURCE;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    private static final int __START_ISSET_ID = 0;
    private static final int __ROWS_ISSET_ID = 1;
    private byte __isset_bitfield = 0;
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.QUERY, new org.apache.thrift.meta_data.FieldMetaData("query", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
      tmpMap.put(_Fields.START, new org.apache.thrift.meta_data.FieldMetaData("start", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
      tmpMap.put(_Fields.ROWS, new org.apache.thrift.meta_data.FieldMetaData("rows", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
      tmpMap.put(_Fields.SOURCE, new org.apache.thrift.meta_data.FieldMetaData("source", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(get_message_records_by_source_args.class, metaDataMap);
    }

    public get_message_records_by_source_args() {
    }

    public get_message_records_by_source_args(
      String query,
      int start,
      int rows,
      String source)
    {
      this();
      this.query = query;
      this.start = start;
      setStartIsSet(true);
      this.rows = rows;
      setRowsIsSet(true);
      this.source = source;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public get_message_records_by_source_args(get_message_records_by_source_args other) {
      __isset_bitfield = other.__isset_bitfield;
      if (other.isSetQuery()) {
        this.query = other.query;
      }
      this.start = other.start;
      this.rows = other.rows;
      if (other.isSetSource()) {
        this.source = other.source;
      }
    }

    public get_message_records_by_source_args deepCopy() {
      return new get_message_records_by_source_args(this);
    }

    @Override
    public void clear() {
      this.query = null;
      setStartIsSet(false);
      this.start = 0;
      setRowsIsSet(false);
      this.rows = 0;
      this.source = null;
    }

    public String getQuery() {
      return this.query;
    }

    public get_message_records_by_source_args setQuery(String query) {
      this.query = query;
      return this;
    }

    public void unsetQuery() {
      this.query = null;
    }

    /** Returns true if field query is set (has been assigned a value) and false otherwise */
    public boolean isSetQuery() {
      return this.query != null;
    }

    public void setQueryIsSet(boolean value) {
      if (!value) {
        this.query = null;
      }
    }

    public int getStart() {
      return this.start;
    }

    public get_message_records_by_source_args setStart(int start) {
      this.start = start;
      setStartIsSet(true);
      return this;
    }

    public void unsetStart() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __START_ISSET_ID);
    }

    /** Returns true if field start is set (has been assigned a value) and false otherwise */
    public boolean isSetStart() {
      return EncodingUtils.testBit(__isset_bitfield, __START_ISSET_ID);
    }

    public void setStartIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __START_ISSET_ID, value);
    }

    public int getRows() {
      return this.rows;
    }

    public get_message_records_by_source_args setRows(int rows) {
      this.rows = rows;
      setRowsIsSet(true);
      return this;
    }

    public void unsetRows() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ROWS_ISSET_ID);
    }

    /** Returns true if field rows is set (has been assigned a value) and false otherwise */
    public boolean isSetRows() {
      return EncodingUtils.testBit(__isset_bitfield, __ROWS_ISSET_ID);
    }

    public void setRowsIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ROWS_ISSET_ID, value);
    }

    public String getSource() {
      return this.source;
    }

    public get_message_records_by_source_args setSource(String source) {
      this.source = source;
      return this;
    }

    public void unsetSource() {
      this.source = null;
    }

    /** Returns true if field source is set (has been assigned a value) and false otherwise */
    public boolean isSetSource() {
      return this.source != null;
    }

    public void setSourceIsSet(boolean value) {
      if (!value) {
        this.source = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case QUERY:
        if (value == null) {
          unsetQuery();
        } else {
          setQuery((String)value);
        }
        break;

      case START:
        if (value == null) {
          unsetStart();
        } else {
          setStart((Integer)value);
        }
        break;

      case ROWS:
        if (value == null) {
          unsetRows();
        } else {
          setRows((Integer)value);
        }
        break;

      case SOURCE:
        if (value == null) {
          unsetSource();
        } else {
          setSource((String)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case QUERY:
        return getQuery();

      case START:
        return Integer.valueOf(getStart());

      case ROWS:
        return Integer.valueOf(getRows());

      case SOURCE:
        return getSource();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case QUERY:
        return isSetQuery();
      case START:
        return isSetStart();
      case ROWS:
        return isSetRows();
      case SOURCE:
        return isSetSource();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof get_message_records_by_source_args)
        return this.equals((get_message_records_by_source_args)that);
      return false;
    }

    public boolean equals(get_message_records_by_source_args that) {
      if (that == null)
        return false;

      boolean this_present_query = true && this.isSetQuery();
      boolean that_present_query = true && that.isSetQuery();
      if (this_present_query || that_present_query) {
        if (!(this_present_query && that_present_query))
          return false;
        if (!this.query.equals(that.query))
          return false;
      }

      boolean this_present_start = true;
      boolean that_present_start = true;
      if (this_present_start || that_present_start) {
        if (!(this_present_start && that_present_start))
          return false;
        if (this.start != that.start)
          return false;
      }

      boolean this_present_rows = true;
      boolean that_present_rows = true;
      if (this_present_rows || that_present_rows) {
        if (!(this_present_rows && that_present_rows))
          return false;
        if (this.rows != that.rows)
          return false;
      }

      boolean this_present_source = true && this.isSetSource();
      boolean that_present_source = true && that.isSetSource();
      if (this_present_source || that_present_source) {
        if (!(this_present_source && that_present_source))
          return false;
        if (!this.source.equals(that.source))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(get_message_records_by_source_args other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      get_message_records_by_source_args typedOther = (get_message_records_by_source_args)other;

      lastComparison = Boolean.valueOf(isSetQuery()).compareTo(typedOther.isSetQuery());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetQuery()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.query, typedOther.query);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetStart()).compareTo(typedOther.isSetStart());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetStart()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.start, typedOther.start);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetRows()).compareTo(typedOther.isSetRows());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetRows()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rows, typedOther.rows);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetSource()).compareTo(typedOther.isSetSource());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetSource()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.source, typedOther.source);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("get_message_records_by_source_args(");
      boolean first = true;

      sb.append("query:");
      if (this.query == null) {
        sb.append("null");
      } else {
        sb.append(this.query);
      }
      first = false;
      if (!first) sb.append(", ");
      sb.append("start:");
      sb.append(this.start);
      first = false;
      if (!first) sb.append(", ");
      sb.append("rows:");
      sb.append(this.rows);
      first = false;
      if (!first) sb.append(", ");
      sb.append("source:");
      if (this.source == null) {
        sb.append("null");
      } else {
        sb.append(this.source);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
      // check for sub-struct validity
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
        __isset_bitfield = 0;
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class get_message_records_by_source_argsStandardSchemeFactory implements SchemeFactory {
      public get_message_records_by_source_argsStandardScheme getScheme() {
        return new get_message_records_by_source_argsStandardScheme();
      }
    }

    private static class get_message_records_by_source_argsStandardScheme extends StandardScheme<get_message_records_by_source_args> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, get_message_records_by_source_args struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 1: // QUERY
              if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
                struct.query = iprot.readString();
                struct.setQueryIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 2: // START
              if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                struct.start = iprot.readI32();
                struct.setStartIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 3: // ROWS
              if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                struct.rows = iprot.readI32();
                struct.setRowsIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 6: // SOURCE
              if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
                struct.source = iprot.readString();
                struct.setSourceIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, get_message_records_by_source_args struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.query != null) {
          oprot.writeFieldBegin(QUERY_FIELD_DESC);
          oprot.writeString(struct.query);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(START_FIELD_DESC);
        oprot.writeI32(struct.start);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(ROWS_FIELD_DESC);
        oprot.writeI32(struct.rows);
        oprot.writeFieldEnd();
        if (struct.source != null) {
          oprot.writeFieldBegin(SOURCE_FIELD_DESC);
          oprot.writeString(struct.source);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class get_message_records_by_source_argsTupleSchemeFactory implements SchemeFactory {
      public get_message_records_by_source_argsTupleScheme getScheme() {
        return new get_message_records_by_source_argsTupleScheme();
      }
    }

    private static class get_message_records_by_source_argsTupleScheme extends TupleScheme<get_message_records_by_source_args> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, get_message_records_by_source_args struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetQuery()) {
          optionals.set(0);
        }
        if (struct.isSetStart()) {
          optionals.set(1);
        }
        if (struct.isSetRows()) {
          optionals.set(2);
        }
        if (struct.isSetSource()) {
          optionals.set(3);
        }
        oprot.writeBitSet(optionals, 4);
        if (struct.isSetQuery()) {
          oprot.writeString(struct.query);
        }
        if (struct.isSetStart()) {
          oprot.writeI32(struct.start);
        }
        if (struct.isSetRows()) {
          oprot.writeI32(struct.rows);
        }
        if (struct.isSetSource()) {
          oprot.writeString(struct.source);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, get_message_records_by_source_args struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(4);
        if (incoming.get(0)) {
          struct.query = iprot.readString();
          struct.setQueryIsSet(true);
        }
        if (incoming.get(1)) {
          struct.start = iprot.readI32();
          struct.setStartIsSet(true);
        }
        if (incoming.get(2)) {
          struct.rows = iprot.readI32();
          struct.setRowsIsSet(true);
        }
        if (incoming.get(3)) {
          struct.source = iprot.readString();
          struct.setSourceIsSet(true);
        }
      }
    }

  }

  public static class get_message_records_by_source_result implements org.apache.thrift.TBase<get_message_records_by_source_result, get_message_records_by_source_result._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("get_message_records_by_source_result");

    private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("success", org.apache.thrift.protocol.TType.STRUCT, (short)0);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new get_message_records_by_source_resultStandardSchemeFactory());
      schemes.put(TupleScheme.class, new get_message_records_by_source_resultTupleSchemeFactory());
    }

    public ThriftQueryResponse success; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      SUCCESS((short)0, "success");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 0: // SUCCESS
            return SUCCESS;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("success", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ThriftQueryResponse.class)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(get_message_records_by_source_result.class, metaDataMap);
    }

    public get_message_records_by_source_result() {
    }

    public get_message_records_by_source_result(
      ThriftQueryResponse success)
    {
      this();
      this.success = success;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public get_message_records_by_source_result(get_message_records_by_source_result other) {
      if (other.isSetSuccess()) {
        this.success = new ThriftQueryResponse(other.success);
      }
    }

    public get_message_records_by_source_result deepCopy() {
      return new get_message_records_by_source_result(this);
    }

    @Override
    public void clear() {
      this.success = null;
    }

    public ThriftQueryResponse getSuccess() {
      return this.success;
    }

    public get_message_records_by_source_result setSuccess(ThriftQueryResponse success) {
      this.success = success;
      return this;
    }

    public void unsetSuccess() {
      this.success = null;
    }

    /** Returns true if field success is set (has been assigned a value) and false otherwise */
    public boolean isSetSuccess() {
      return this.success != null;
    }

    public void setSuccessIsSet(boolean value) {
      if (!value) {
        this.success = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case SUCCESS:
        if (value == null) {
          unsetSuccess();
        } else {
          setSuccess((ThriftQueryResponse)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case SUCCESS:
        return getSuccess();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case SUCCESS:
        return isSetSuccess();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof get_message_records_by_source_result)
        return this.equals((get_message_records_by_source_result)that);
      return false;
    }

    public boolean equals(get_message_records_by_source_result that) {
      if (that == null)
        return false;

      boolean this_present_success = true && this.isSetSuccess();
      boolean that_present_success = true && that.isSetSuccess();
      if (this_present_success || that_present_success) {
        if (!(this_present_success && that_present_success))
          return false;
        if (!this.success.equals(that.success))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(get_message_records_by_source_result other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      get_message_records_by_source_result typedOther = (get_message_records_by_source_result)other;

      lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(typedOther.isSetSuccess());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetSuccess()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.success, typedOther.success);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
      }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("get_message_records_by_source_result(");
      boolean first = true;

      sb.append("success:");
      if (this.success == null) {
        sb.append("null");
      } else {
        sb.append(this.success);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
      // check for sub-struct validity
      if (success != null) {
        success.validate();
      }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class get_message_records_by_source_resultStandardSchemeFactory implements SchemeFactory {
      public get_message_records_by_source_resultStandardScheme getScheme() {
        return new get_message_records_by_source_resultStandardScheme();
      }
    }

    private static class get_message_records_by_source_resultStandardScheme extends StandardScheme<get_message_records_by_source_result> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, get_message_records_by_source_result struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 0: // SUCCESS
              if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
                struct.success = new ThriftQueryResponse();
                struct.success.read(iprot);
                struct.setSuccessIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, get_message_records_by_source_result struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.success != null) {
          oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
          struct.success.write(oprot);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class get_message_records_by_source_resultTupleSchemeFactory implements SchemeFactory {
      public get_message_records_by_source_resultTupleScheme getScheme() {
        return new get_message_records_by_source_resultTupleScheme();
      }
    }

    private static class get_message_records_by_source_resultTupleScheme extends TupleScheme<get_message_records_by_source_result> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, get_message_records_by_source_result struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetSuccess()) {
          optionals.set(0);
        }
        oprot.writeBitSet(optionals, 1);
        if (struct.isSetSuccess()) {
          struct.success.write(oprot);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, get_message_records_by_source_result struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(1);
        if (incoming.get(0)) {
          struct.success = new ThriftQueryResponse();
          struct.success.read(iprot);
          struct.setSuccessIsSet(true);
        }
      }
    }

  }

}
