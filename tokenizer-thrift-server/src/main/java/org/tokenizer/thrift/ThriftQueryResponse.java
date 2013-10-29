/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
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
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
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

public class ThriftQueryResponse implements org.apache.thrift.TBase<ThriftQueryResponse, ThriftQueryResponse._Fields>, java.io.Serializable, Cloneable, Comparable<ThriftQueryResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ThriftQueryResponse");

  private static final org.apache.thrift.protocol.TField NUM_FOUND_FIELD_DESC = new org.apache.thrift.protocol.TField("numFound", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField Q_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("qTime", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField ELAPSED_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("elapsedTime", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField THRIFT_DOCUMENTS_FIELD_DESC = new org.apache.thrift.protocol.TField("thriftDocuments", org.apache.thrift.protocol.TType.LIST, (short)4);
  private static final org.apache.thrift.protocol.TField ERROR_FIELD_DESC = new org.apache.thrift.protocol.TField("error", org.apache.thrift.protocol.TType.STRING, (short)5);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ThriftQueryResponseStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ThriftQueryResponseTupleSchemeFactory());
  }

  public long numFound; // optional
  public int qTime; // optional
  public int elapsedTime; // optional
  public List<ThriftDocument> thriftDocuments; // optional
  public String error; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    NUM_FOUND((short)1, "numFound"),
    Q_TIME((short)2, "qTime"),
    ELAPSED_TIME((short)3, "elapsedTime"),
    THRIFT_DOCUMENTS((short)4, "thriftDocuments"),
    ERROR((short)5, "error");

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
        case 1: // NUM_FOUND
          return NUM_FOUND;
        case 2: // Q_TIME
          return Q_TIME;
        case 3: // ELAPSED_TIME
          return ELAPSED_TIME;
        case 4: // THRIFT_DOCUMENTS
          return THRIFT_DOCUMENTS;
        case 5: // ERROR
          return ERROR;
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
  private static final int __NUMFOUND_ISSET_ID = 0;
  private static final int __QTIME_ISSET_ID = 1;
  private static final int __ELAPSEDTIME_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.NUM_FOUND,_Fields.Q_TIME,_Fields.ELAPSED_TIME,_Fields.THRIFT_DOCUMENTS,_Fields.ERROR};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.NUM_FOUND, new org.apache.thrift.meta_data.FieldMetaData("numFound", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.Q_TIME, new org.apache.thrift.meta_data.FieldMetaData("qTime", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.ELAPSED_TIME, new org.apache.thrift.meta_data.FieldMetaData("elapsedTime", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.THRIFT_DOCUMENTS, new org.apache.thrift.meta_data.FieldMetaData("thriftDocuments", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ThriftDocument.class))));
    tmpMap.put(_Fields.ERROR, new org.apache.thrift.meta_data.FieldMetaData("error", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ThriftQueryResponse.class, metaDataMap);
  }

  public ThriftQueryResponse() {
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ThriftQueryResponse(ThriftQueryResponse other) {
    __isset_bitfield = other.__isset_bitfield;
    this.numFound = other.numFound;
    this.qTime = other.qTime;
    this.elapsedTime = other.elapsedTime;
    if (other.isSetThriftDocuments()) {
      List<ThriftDocument> __this__thriftDocuments = new ArrayList<ThriftDocument>(other.thriftDocuments.size());
      for (ThriftDocument other_element : other.thriftDocuments) {
        __this__thriftDocuments.add(new ThriftDocument(other_element));
      }
      this.thriftDocuments = __this__thriftDocuments;
    }
    if (other.isSetError()) {
      this.error = other.error;
    }
  }

  public ThriftQueryResponse deepCopy() {
    return new ThriftQueryResponse(this);
  }

  @Override
  public void clear() {
    setNumFoundIsSet(false);
    this.numFound = 0;
    setQTimeIsSet(false);
    this.qTime = 0;
    setElapsedTimeIsSet(false);
    this.elapsedTime = 0;
    this.thriftDocuments = null;
    this.error = null;
  }

  public long getNumFound() {
    return this.numFound;
  }

  public ThriftQueryResponse setNumFound(long numFound) {
    this.numFound = numFound;
    setNumFoundIsSet(true);
    return this;
  }

  public void unsetNumFound() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __NUMFOUND_ISSET_ID);
  }

  /** Returns true if field numFound is set (has been assigned a value) and false otherwise */
  public boolean isSetNumFound() {
    return EncodingUtils.testBit(__isset_bitfield, __NUMFOUND_ISSET_ID);
  }

  public void setNumFoundIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __NUMFOUND_ISSET_ID, value);
  }

  public int getQTime() {
    return this.qTime;
  }

  public ThriftQueryResponse setQTime(int qTime) {
    this.qTime = qTime;
    setQTimeIsSet(true);
    return this;
  }

  public void unsetQTime() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __QTIME_ISSET_ID);
  }

  /** Returns true if field qTime is set (has been assigned a value) and false otherwise */
  public boolean isSetQTime() {
    return EncodingUtils.testBit(__isset_bitfield, __QTIME_ISSET_ID);
  }

  public void setQTimeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __QTIME_ISSET_ID, value);
  }

  public int getElapsedTime() {
    return this.elapsedTime;
  }

  public ThriftQueryResponse setElapsedTime(int elapsedTime) {
    this.elapsedTime = elapsedTime;
    setElapsedTimeIsSet(true);
    return this;
  }

  public void unsetElapsedTime() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ELAPSEDTIME_ISSET_ID);
  }

  /** Returns true if field elapsedTime is set (has been assigned a value) and false otherwise */
  public boolean isSetElapsedTime() {
    return EncodingUtils.testBit(__isset_bitfield, __ELAPSEDTIME_ISSET_ID);
  }

  public void setElapsedTimeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ELAPSEDTIME_ISSET_ID, value);
  }

  public int getThriftDocumentsSize() {
    return (this.thriftDocuments == null) ? 0 : this.thriftDocuments.size();
  }

  public java.util.Iterator<ThriftDocument> getThriftDocumentsIterator() {
    return (this.thriftDocuments == null) ? null : this.thriftDocuments.iterator();
  }

  public void addToThriftDocuments(ThriftDocument elem) {
    if (this.thriftDocuments == null) {
      this.thriftDocuments = new ArrayList<ThriftDocument>();
    }
    this.thriftDocuments.add(elem);
  }

  public List<ThriftDocument> getThriftDocuments() {
    return this.thriftDocuments;
  }

  public ThriftQueryResponse setThriftDocuments(List<ThriftDocument> thriftDocuments) {
    this.thriftDocuments = thriftDocuments;
    return this;
  }

  public void unsetThriftDocuments() {
    this.thriftDocuments = null;
  }

  /** Returns true if field thriftDocuments is set (has been assigned a value) and false otherwise */
  public boolean isSetThriftDocuments() {
    return this.thriftDocuments != null;
  }

  public void setThriftDocumentsIsSet(boolean value) {
    if (!value) {
      this.thriftDocuments = null;
    }
  }

  public String getError() {
    return this.error;
  }

  public ThriftQueryResponse setError(String error) {
    this.error = error;
    return this;
  }

  public void unsetError() {
    this.error = null;
  }

  /** Returns true if field error is set (has been assigned a value) and false otherwise */
  public boolean isSetError() {
    return this.error != null;
  }

  public void setErrorIsSet(boolean value) {
    if (!value) {
      this.error = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case NUM_FOUND:
      if (value == null) {
        unsetNumFound();
      } else {
        setNumFound((Long)value);
      }
      break;

    case Q_TIME:
      if (value == null) {
        unsetQTime();
      } else {
        setQTime((Integer)value);
      }
      break;

    case ELAPSED_TIME:
      if (value == null) {
        unsetElapsedTime();
      } else {
        setElapsedTime((Integer)value);
      }
      break;

    case THRIFT_DOCUMENTS:
      if (value == null) {
        unsetThriftDocuments();
      } else {
        setThriftDocuments((List<ThriftDocument>)value);
      }
      break;

    case ERROR:
      if (value == null) {
        unsetError();
      } else {
        setError((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case NUM_FOUND:
      return Long.valueOf(getNumFound());

    case Q_TIME:
      return Integer.valueOf(getQTime());

    case ELAPSED_TIME:
      return Integer.valueOf(getElapsedTime());

    case THRIFT_DOCUMENTS:
      return getThriftDocuments();

    case ERROR:
      return getError();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case NUM_FOUND:
      return isSetNumFound();
    case Q_TIME:
      return isSetQTime();
    case ELAPSED_TIME:
      return isSetElapsedTime();
    case THRIFT_DOCUMENTS:
      return isSetThriftDocuments();
    case ERROR:
      return isSetError();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ThriftQueryResponse)
      return this.equals((ThriftQueryResponse)that);
    return false;
  }

  public boolean equals(ThriftQueryResponse that) {
    if (that == null)
      return false;

    boolean this_present_numFound = true && this.isSetNumFound();
    boolean that_present_numFound = true && that.isSetNumFound();
    if (this_present_numFound || that_present_numFound) {
      if (!(this_present_numFound && that_present_numFound))
        return false;
      if (this.numFound != that.numFound)
        return false;
    }

    boolean this_present_qTime = true && this.isSetQTime();
    boolean that_present_qTime = true && that.isSetQTime();
    if (this_present_qTime || that_present_qTime) {
      if (!(this_present_qTime && that_present_qTime))
        return false;
      if (this.qTime != that.qTime)
        return false;
    }

    boolean this_present_elapsedTime = true && this.isSetElapsedTime();
    boolean that_present_elapsedTime = true && that.isSetElapsedTime();
    if (this_present_elapsedTime || that_present_elapsedTime) {
      if (!(this_present_elapsedTime && that_present_elapsedTime))
        return false;
      if (this.elapsedTime != that.elapsedTime)
        return false;
    }

    boolean this_present_thriftDocuments = true && this.isSetThriftDocuments();
    boolean that_present_thriftDocuments = true && that.isSetThriftDocuments();
    if (this_present_thriftDocuments || that_present_thriftDocuments) {
      if (!(this_present_thriftDocuments && that_present_thriftDocuments))
        return false;
      if (!this.thriftDocuments.equals(that.thriftDocuments))
        return false;
    }

    boolean this_present_error = true && this.isSetError();
    boolean that_present_error = true && that.isSetError();
    if (this_present_error || that_present_error) {
      if (!(this_present_error && that_present_error))
        return false;
      if (!this.error.equals(that.error))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(ThriftQueryResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetNumFound()).compareTo(other.isSetNumFound());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNumFound()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.numFound, other.numFound);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetQTime()).compareTo(other.isSetQTime());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetQTime()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.qTime, other.qTime);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetElapsedTime()).compareTo(other.isSetElapsedTime());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetElapsedTime()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.elapsedTime, other.elapsedTime);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetThriftDocuments()).compareTo(other.isSetThriftDocuments());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetThriftDocuments()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.thriftDocuments, other.thriftDocuments);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetError()).compareTo(other.isSetError());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetError()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.error, other.error);
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
    StringBuilder sb = new StringBuilder("ThriftQueryResponse(");
    boolean first = true;

    if (isSetNumFound()) {
      sb.append("numFound:");
      sb.append(this.numFound);
      first = false;
    }
    if (isSetQTime()) {
      if (!first) sb.append(", ");
      sb.append("qTime:");
      sb.append(this.qTime);
      first = false;
    }
    if (isSetElapsedTime()) {
      if (!first) sb.append(", ");
      sb.append("elapsedTime:");
      sb.append(this.elapsedTime);
      first = false;
    }
    if (isSetThriftDocuments()) {
      if (!first) sb.append(", ");
      sb.append("thriftDocuments:");
      if (this.thriftDocuments == null) {
        sb.append("null");
      } else {
        sb.append(this.thriftDocuments);
      }
      first = false;
    }
    if (isSetError()) {
      if (!first) sb.append(", ");
      sb.append("error:");
      if (this.error == null) {
        sb.append("null");
      } else {
        sb.append(this.error);
      }
      first = false;
    }
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

  private static class ThriftQueryResponseStandardSchemeFactory implements SchemeFactory {
    public ThriftQueryResponseStandardScheme getScheme() {
      return new ThriftQueryResponseStandardScheme();
    }
  }

  private static class ThriftQueryResponseStandardScheme extends StandardScheme<ThriftQueryResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ThriftQueryResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // NUM_FOUND
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.numFound = iprot.readI64();
              struct.setNumFoundIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // Q_TIME
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.qTime = iprot.readI32();
              struct.setQTimeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // ELAPSED_TIME
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.elapsedTime = iprot.readI32();
              struct.setElapsedTimeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // THRIFT_DOCUMENTS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list40 = iprot.readListBegin();
                struct.thriftDocuments = new ArrayList<ThriftDocument>(_list40.size);
                for (int _i41 = 0; _i41 < _list40.size; ++_i41)
                {
                  ThriftDocument _elem42;
                  _elem42 = new ThriftDocument();
                  _elem42.read(iprot);
                  struct.thriftDocuments.add(_elem42);
                }
                iprot.readListEnd();
              }
              struct.setThriftDocumentsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // ERROR
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.error = iprot.readString();
              struct.setErrorIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, ThriftQueryResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.isSetNumFound()) {
        oprot.writeFieldBegin(NUM_FOUND_FIELD_DESC);
        oprot.writeI64(struct.numFound);
        oprot.writeFieldEnd();
      }
      if (struct.isSetQTime()) {
        oprot.writeFieldBegin(Q_TIME_FIELD_DESC);
        oprot.writeI32(struct.qTime);
        oprot.writeFieldEnd();
      }
      if (struct.isSetElapsedTime()) {
        oprot.writeFieldBegin(ELAPSED_TIME_FIELD_DESC);
        oprot.writeI32(struct.elapsedTime);
        oprot.writeFieldEnd();
      }
      if (struct.thriftDocuments != null) {
        if (struct.isSetThriftDocuments()) {
          oprot.writeFieldBegin(THRIFT_DOCUMENTS_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.thriftDocuments.size()));
            for (ThriftDocument _iter43 : struct.thriftDocuments)
            {
              _iter43.write(oprot);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      if (struct.error != null) {
        if (struct.isSetError()) {
          oprot.writeFieldBegin(ERROR_FIELD_DESC);
          oprot.writeString(struct.error);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ThriftQueryResponseTupleSchemeFactory implements SchemeFactory {
    public ThriftQueryResponseTupleScheme getScheme() {
      return new ThriftQueryResponseTupleScheme();
    }
  }

  private static class ThriftQueryResponseTupleScheme extends TupleScheme<ThriftQueryResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ThriftQueryResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetNumFound()) {
        optionals.set(0);
      }
      if (struct.isSetQTime()) {
        optionals.set(1);
      }
      if (struct.isSetElapsedTime()) {
        optionals.set(2);
      }
      if (struct.isSetThriftDocuments()) {
        optionals.set(3);
      }
      if (struct.isSetError()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetNumFound()) {
        oprot.writeI64(struct.numFound);
      }
      if (struct.isSetQTime()) {
        oprot.writeI32(struct.qTime);
      }
      if (struct.isSetElapsedTime()) {
        oprot.writeI32(struct.elapsedTime);
      }
      if (struct.isSetThriftDocuments()) {
        {
          oprot.writeI32(struct.thriftDocuments.size());
          for (ThriftDocument _iter44 : struct.thriftDocuments)
          {
            _iter44.write(oprot);
          }
        }
      }
      if (struct.isSetError()) {
        oprot.writeString(struct.error);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ThriftQueryResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.numFound = iprot.readI64();
        struct.setNumFoundIsSet(true);
      }
      if (incoming.get(1)) {
        struct.qTime = iprot.readI32();
        struct.setQTimeIsSet(true);
      }
      if (incoming.get(2)) {
        struct.elapsedTime = iprot.readI32();
        struct.setElapsedTimeIsSet(true);
      }
      if (incoming.get(3)) {
        {
          org.apache.thrift.protocol.TList _list45 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.thriftDocuments = new ArrayList<ThriftDocument>(_list45.size);
          for (int _i46 = 0; _i46 < _list45.size; ++_i46)
          {
            ThriftDocument _elem47;
            _elem47 = new ThriftDocument();
            _elem47.read(iprot);
            struct.thriftDocuments.add(_elem47);
          }
        }
        struct.setThriftDocumentsIsSet(true);
      }
      if (incoming.get(4)) {
        struct.error = iprot.readString();
        struct.setErrorIsSet(true);
      }
    }
  }

}

