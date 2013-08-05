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

public class ThriftMessageRecord implements org.apache.thrift.TBase<ThriftMessageRecord, ThriftMessageRecord._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ThriftMessageRecord");

  private static final org.apache.thrift.protocol.TField ID_FIELD_DESC = new org.apache.thrift.protocol.TField("id", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField SOURCE_FIELD_DESC = new org.apache.thrift.protocol.TField("source", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField TOPIC_FIELD_DESC = new org.apache.thrift.protocol.TField("topic", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("timestamp", org.apache.thrift.protocol.TType.I64, (short)4);
  private static final org.apache.thrift.protocol.TField AUTHOR_FIELD_DESC = new org.apache.thrift.protocol.TField("author", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField AGE_FIELD_DESC = new org.apache.thrift.protocol.TField("age", org.apache.thrift.protocol.TType.BYTE, (short)6);
  private static final org.apache.thrift.protocol.TField GENDER_FIELD_DESC = new org.apache.thrift.protocol.TField("gender", org.apache.thrift.protocol.TType.I32, (short)7);
  private static final org.apache.thrift.protocol.TField TITLE_FIELD_DESC = new org.apache.thrift.protocol.TField("title", org.apache.thrift.protocol.TType.STRING, (short)8);
  private static final org.apache.thrift.protocol.TField CONTENT_FIELD_DESC = new org.apache.thrift.protocol.TField("content", org.apache.thrift.protocol.TType.STRING, (short)9);
  private static final org.apache.thrift.protocol.TField USER_RATING_FIELD_DESC = new org.apache.thrift.protocol.TField("userRating", org.apache.thrift.protocol.TType.STRING, (short)10);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ThriftMessageRecordStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ThriftMessageRecordTupleSchemeFactory());
  }

  public String id; // required
  public String source; // optional
  public String topic; // optional
  public long timestamp; // optional
  public String author; // optional
  public byte age; // optional
  /**
   * 
   * @see ThriftGenderType
   */
  public ThriftGenderType gender; // optional
  public String title; // optional
  public String content; // optional
  public String userRating; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID((short)1, "id"),
    SOURCE((short)2, "source"),
    TOPIC((short)3, "topic"),
    TIMESTAMP((short)4, "timestamp"),
    AUTHOR((short)5, "author"),
    AGE((short)6, "age"),
    /**
     * 
     * @see ThriftGenderType
     */
    GENDER((short)7, "gender"),
    TITLE((short)8, "title"),
    CONTENT((short)9, "content"),
    USER_RATING((short)10, "userRating");

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
        case 1: // ID
          return ID;
        case 2: // SOURCE
          return SOURCE;
        case 3: // TOPIC
          return TOPIC;
        case 4: // TIMESTAMP
          return TIMESTAMP;
        case 5: // AUTHOR
          return AUTHOR;
        case 6: // AGE
          return AGE;
        case 7: // GENDER
          return GENDER;
        case 8: // TITLE
          return TITLE;
        case 9: // CONTENT
          return CONTENT;
        case 10: // USER_RATING
          return USER_RATING;
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
  private static final int __TIMESTAMP_ISSET_ID = 0;
  private static final int __AGE_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.SOURCE,_Fields.TOPIC,_Fields.TIMESTAMP,_Fields.AUTHOR,_Fields.AGE,_Fields.GENDER,_Fields.TITLE,_Fields.CONTENT,_Fields.USER_RATING};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("id", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.SOURCE, new org.apache.thrift.meta_data.FieldMetaData("source", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TOPIC, new org.apache.thrift.meta_data.FieldMetaData("topic", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TIMESTAMP, new org.apache.thrift.meta_data.FieldMetaData("timestamp", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.AUTHOR, new org.apache.thrift.meta_data.FieldMetaData("author", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.AGE, new org.apache.thrift.meta_data.FieldMetaData("age", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BYTE)));
    tmpMap.put(_Fields.GENDER, new org.apache.thrift.meta_data.FieldMetaData("gender", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, ThriftGenderType.class)));
    tmpMap.put(_Fields.TITLE, new org.apache.thrift.meta_data.FieldMetaData("title", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CONTENT, new org.apache.thrift.meta_data.FieldMetaData("content", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.USER_RATING, new org.apache.thrift.meta_data.FieldMetaData("userRating", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ThriftMessageRecord.class, metaDataMap);
  }

  public ThriftMessageRecord() {
  }

  public ThriftMessageRecord(
    String id)
  {
    this();
    this.id = id;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ThriftMessageRecord(ThriftMessageRecord other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetId()) {
      this.id = other.id;
    }
    if (other.isSetSource()) {
      this.source = other.source;
    }
    if (other.isSetTopic()) {
      this.topic = other.topic;
    }
    this.timestamp = other.timestamp;
    if (other.isSetAuthor()) {
      this.author = other.author;
    }
    this.age = other.age;
    if (other.isSetGender()) {
      this.gender = other.gender;
    }
    if (other.isSetTitle()) {
      this.title = other.title;
    }
    if (other.isSetContent()) {
      this.content = other.content;
    }
    if (other.isSetUserRating()) {
      this.userRating = other.userRating;
    }
  }

  public ThriftMessageRecord deepCopy() {
    return new ThriftMessageRecord(this);
  }

  @Override
  public void clear() {
    this.id = null;
    this.source = null;
    this.topic = null;
    setTimestampIsSet(false);
    this.timestamp = 0;
    this.author = null;
    setAgeIsSet(false);
    this.age = 0;
    this.gender = null;
    this.title = null;
    this.content = null;
    this.userRating = null;
  }

  public String getId() {
    return this.id;
  }

  public ThriftMessageRecord setId(String id) {
    this.id = id;
    return this;
  }

  public void unsetId() {
    this.id = null;
  }

  /** Returns true if field id is set (has been assigned a value) and false otherwise */
  public boolean isSetId() {
    return this.id != null;
  }

  public void setIdIsSet(boolean value) {
    if (!value) {
      this.id = null;
    }
  }

  public String getSource() {
    return this.source;
  }

  public ThriftMessageRecord setSource(String source) {
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

  public String getTopic() {
    return this.topic;
  }

  public ThriftMessageRecord setTopic(String topic) {
    this.topic = topic;
    return this;
  }

  public void unsetTopic() {
    this.topic = null;
  }

  /** Returns true if field topic is set (has been assigned a value) and false otherwise */
  public boolean isSetTopic() {
    return this.topic != null;
  }

  public void setTopicIsSet(boolean value) {
    if (!value) {
      this.topic = null;
    }
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public ThriftMessageRecord setTimestamp(long timestamp) {
    this.timestamp = timestamp;
    setTimestampIsSet(true);
    return this;
  }

  public void unsetTimestamp() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __TIMESTAMP_ISSET_ID);
  }

  /** Returns true if field timestamp is set (has been assigned a value) and false otherwise */
  public boolean isSetTimestamp() {
    return EncodingUtils.testBit(__isset_bitfield, __TIMESTAMP_ISSET_ID);
  }

  public void setTimestampIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __TIMESTAMP_ISSET_ID, value);
  }

  public String getAuthor() {
    return this.author;
  }

  public ThriftMessageRecord setAuthor(String author) {
    this.author = author;
    return this;
  }

  public void unsetAuthor() {
    this.author = null;
  }

  /** Returns true if field author is set (has been assigned a value) and false otherwise */
  public boolean isSetAuthor() {
    return this.author != null;
  }

  public void setAuthorIsSet(boolean value) {
    if (!value) {
      this.author = null;
    }
  }

  public byte getAge() {
    return this.age;
  }

  public ThriftMessageRecord setAge(byte age) {
    this.age = age;
    setAgeIsSet(true);
    return this;
  }

  public void unsetAge() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __AGE_ISSET_ID);
  }

  /** Returns true if field age is set (has been assigned a value) and false otherwise */
  public boolean isSetAge() {
    return EncodingUtils.testBit(__isset_bitfield, __AGE_ISSET_ID);
  }

  public void setAgeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __AGE_ISSET_ID, value);
  }

  /**
   * 
   * @see ThriftGenderType
   */
  public ThriftGenderType getGender() {
    return this.gender;
  }

  /**
   * 
   * @see ThriftGenderType
   */
  public ThriftMessageRecord setGender(ThriftGenderType gender) {
    this.gender = gender;
    return this;
  }

  public void unsetGender() {
    this.gender = null;
  }

  /** Returns true if field gender is set (has been assigned a value) and false otherwise */
  public boolean isSetGender() {
    return this.gender != null;
  }

  public void setGenderIsSet(boolean value) {
    if (!value) {
      this.gender = null;
    }
  }

  public String getTitle() {
    return this.title;
  }

  public ThriftMessageRecord setTitle(String title) {
    this.title = title;
    return this;
  }

  public void unsetTitle() {
    this.title = null;
  }

  /** Returns true if field title is set (has been assigned a value) and false otherwise */
  public boolean isSetTitle() {
    return this.title != null;
  }

  public void setTitleIsSet(boolean value) {
    if (!value) {
      this.title = null;
    }
  }

  public String getContent() {
    return this.content;
  }

  public ThriftMessageRecord setContent(String content) {
    this.content = content;
    return this;
  }

  public void unsetContent() {
    this.content = null;
  }

  /** Returns true if field content is set (has been assigned a value) and false otherwise */
  public boolean isSetContent() {
    return this.content != null;
  }

  public void setContentIsSet(boolean value) {
    if (!value) {
      this.content = null;
    }
  }

  public String getUserRating() {
    return this.userRating;
  }

  public ThriftMessageRecord setUserRating(String userRating) {
    this.userRating = userRating;
    return this;
  }

  public void unsetUserRating() {
    this.userRating = null;
  }

  /** Returns true if field userRating is set (has been assigned a value) and false otherwise */
  public boolean isSetUserRating() {
    return this.userRating != null;
  }

  public void setUserRatingIsSet(boolean value) {
    if (!value) {
      this.userRating = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ID:
      if (value == null) {
        unsetId();
      } else {
        setId((String)value);
      }
      break;

    case SOURCE:
      if (value == null) {
        unsetSource();
      } else {
        setSource((String)value);
      }
      break;

    case TOPIC:
      if (value == null) {
        unsetTopic();
      } else {
        setTopic((String)value);
      }
      break;

    case TIMESTAMP:
      if (value == null) {
        unsetTimestamp();
      } else {
        setTimestamp((Long)value);
      }
      break;

    case AUTHOR:
      if (value == null) {
        unsetAuthor();
      } else {
        setAuthor((String)value);
      }
      break;

    case AGE:
      if (value == null) {
        unsetAge();
      } else {
        setAge((Byte)value);
      }
      break;

    case GENDER:
      if (value == null) {
        unsetGender();
      } else {
        setGender((ThriftGenderType)value);
      }
      break;

    case TITLE:
      if (value == null) {
        unsetTitle();
      } else {
        setTitle((String)value);
      }
      break;

    case CONTENT:
      if (value == null) {
        unsetContent();
      } else {
        setContent((String)value);
      }
      break;

    case USER_RATING:
      if (value == null) {
        unsetUserRating();
      } else {
        setUserRating((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ID:
      return getId();

    case SOURCE:
      return getSource();

    case TOPIC:
      return getTopic();

    case TIMESTAMP:
      return Long.valueOf(getTimestamp());

    case AUTHOR:
      return getAuthor();

    case AGE:
      return Byte.valueOf(getAge());

    case GENDER:
      return getGender();

    case TITLE:
      return getTitle();

    case CONTENT:
      return getContent();

    case USER_RATING:
      return getUserRating();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ID:
      return isSetId();
    case SOURCE:
      return isSetSource();
    case TOPIC:
      return isSetTopic();
    case TIMESTAMP:
      return isSetTimestamp();
    case AUTHOR:
      return isSetAuthor();
    case AGE:
      return isSetAge();
    case GENDER:
      return isSetGender();
    case TITLE:
      return isSetTitle();
    case CONTENT:
      return isSetContent();
    case USER_RATING:
      return isSetUserRating();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ThriftMessageRecord)
      return this.equals((ThriftMessageRecord)that);
    return false;
  }

  public boolean equals(ThriftMessageRecord that) {
    if (that == null)
      return false;

    boolean this_present_id = true && this.isSetId();
    boolean that_present_id = true && that.isSetId();
    if (this_present_id || that_present_id) {
      if (!(this_present_id && that_present_id))
        return false;
      if (!this.id.equals(that.id))
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

    boolean this_present_topic = true && this.isSetTopic();
    boolean that_present_topic = true && that.isSetTopic();
    if (this_present_topic || that_present_topic) {
      if (!(this_present_topic && that_present_topic))
        return false;
      if (!this.topic.equals(that.topic))
        return false;
    }

    boolean this_present_timestamp = true && this.isSetTimestamp();
    boolean that_present_timestamp = true && that.isSetTimestamp();
    if (this_present_timestamp || that_present_timestamp) {
      if (!(this_present_timestamp && that_present_timestamp))
        return false;
      if (this.timestamp != that.timestamp)
        return false;
    }

    boolean this_present_author = true && this.isSetAuthor();
    boolean that_present_author = true && that.isSetAuthor();
    if (this_present_author || that_present_author) {
      if (!(this_present_author && that_present_author))
        return false;
      if (!this.author.equals(that.author))
        return false;
    }

    boolean this_present_age = true && this.isSetAge();
    boolean that_present_age = true && that.isSetAge();
    if (this_present_age || that_present_age) {
      if (!(this_present_age && that_present_age))
        return false;
      if (this.age != that.age)
        return false;
    }

    boolean this_present_gender = true && this.isSetGender();
    boolean that_present_gender = true && that.isSetGender();
    if (this_present_gender || that_present_gender) {
      if (!(this_present_gender && that_present_gender))
        return false;
      if (!this.gender.equals(that.gender))
        return false;
    }

    boolean this_present_title = true && this.isSetTitle();
    boolean that_present_title = true && that.isSetTitle();
    if (this_present_title || that_present_title) {
      if (!(this_present_title && that_present_title))
        return false;
      if (!this.title.equals(that.title))
        return false;
    }

    boolean this_present_content = true && this.isSetContent();
    boolean that_present_content = true && that.isSetContent();
    if (this_present_content || that_present_content) {
      if (!(this_present_content && that_present_content))
        return false;
      if (!this.content.equals(that.content))
        return false;
    }

    boolean this_present_userRating = true && this.isSetUserRating();
    boolean that_present_userRating = true && that.isSetUserRating();
    if (this_present_userRating || that_present_userRating) {
      if (!(this_present_userRating && that_present_userRating))
        return false;
      if (!this.userRating.equals(that.userRating))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(ThriftMessageRecord other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    ThriftMessageRecord typedOther = (ThriftMessageRecord)other;

    lastComparison = Boolean.valueOf(isSetId()).compareTo(typedOther.isSetId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id, typedOther.id);
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
    lastComparison = Boolean.valueOf(isSetTopic()).compareTo(typedOther.isSetTopic());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTopic()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.topic, typedOther.topic);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTimestamp()).compareTo(typedOther.isSetTimestamp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTimestamp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.timestamp, typedOther.timestamp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAuthor()).compareTo(typedOther.isSetAuthor());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAuthor()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.author, typedOther.author);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAge()).compareTo(typedOther.isSetAge());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAge()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.age, typedOther.age);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetGender()).compareTo(typedOther.isSetGender());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetGender()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.gender, typedOther.gender);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTitle()).compareTo(typedOther.isSetTitle());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTitle()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.title, typedOther.title);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetContent()).compareTo(typedOther.isSetContent());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetContent()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.content, typedOther.content);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUserRating()).compareTo(typedOther.isSetUserRating());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUserRating()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.userRating, typedOther.userRating);
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
    StringBuilder sb = new StringBuilder("ThriftMessageRecord(");
    boolean first = true;

    sb.append("id:");
    if (this.id == null) {
      sb.append("null");
    } else {
      sb.append(this.id);
    }
    first = false;
    if (isSetSource()) {
      if (!first) sb.append(", ");
      sb.append("source:");
      if (this.source == null) {
        sb.append("null");
      } else {
        sb.append(this.source);
      }
      first = false;
    }
    if (isSetTopic()) {
      if (!first) sb.append(", ");
      sb.append("topic:");
      if (this.topic == null) {
        sb.append("null");
      } else {
        sb.append(this.topic);
      }
      first = false;
    }
    if (isSetTimestamp()) {
      if (!first) sb.append(", ");
      sb.append("timestamp:");
      sb.append(this.timestamp);
      first = false;
    }
    if (isSetAuthor()) {
      if (!first) sb.append(", ");
      sb.append("author:");
      if (this.author == null) {
        sb.append("null");
      } else {
        sb.append(this.author);
      }
      first = false;
    }
    if (isSetAge()) {
      if (!first) sb.append(", ");
      sb.append("age:");
      sb.append(this.age);
      first = false;
    }
    if (isSetGender()) {
      if (!first) sb.append(", ");
      sb.append("gender:");
      if (this.gender == null) {
        sb.append("null");
      } else {
        sb.append(this.gender);
      }
      first = false;
    }
    if (isSetTitle()) {
      if (!first) sb.append(", ");
      sb.append("title:");
      if (this.title == null) {
        sb.append("null");
      } else {
        sb.append(this.title);
      }
      first = false;
    }
    if (isSetContent()) {
      if (!first) sb.append(", ");
      sb.append("content:");
      if (this.content == null) {
        sb.append("null");
      } else {
        sb.append(this.content);
      }
      first = false;
    }
    if (isSetUserRating()) {
      if (!first) sb.append(", ");
      sb.append("userRating:");
      if (this.userRating == null) {
        sb.append("null");
      } else {
        sb.append(this.userRating);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (id == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'id' was not present! Struct: " + toString());
    }
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

  private static class ThriftMessageRecordStandardSchemeFactory implements SchemeFactory {
    public ThriftMessageRecordStandardScheme getScheme() {
      return new ThriftMessageRecordStandardScheme();
    }
  }

  private static class ThriftMessageRecordStandardScheme extends StandardScheme<ThriftMessageRecord> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ThriftMessageRecord struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.id = iprot.readString();
              struct.setIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // SOURCE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.source = iprot.readString();
              struct.setSourceIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // TOPIC
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.topic = iprot.readString();
              struct.setTopicIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // TIMESTAMP
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.timestamp = iprot.readI64();
              struct.setTimestampIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // AUTHOR
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.author = iprot.readString();
              struct.setAuthorIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // AGE
            if (schemeField.type == org.apache.thrift.protocol.TType.BYTE) {
              struct.age = iprot.readByte();
              struct.setAgeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // GENDER
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.gender = ThriftGenderType.findByValue(iprot.readI32());
              struct.setGenderIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // TITLE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.title = iprot.readString();
              struct.setTitleIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 9: // CONTENT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.content = iprot.readString();
              struct.setContentIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 10: // USER_RATING
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.userRating = iprot.readString();
              struct.setUserRatingIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, ThriftMessageRecord struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.id != null) {
        oprot.writeFieldBegin(ID_FIELD_DESC);
        oprot.writeString(struct.id);
        oprot.writeFieldEnd();
      }
      if (struct.source != null) {
        if (struct.isSetSource()) {
          oprot.writeFieldBegin(SOURCE_FIELD_DESC);
          oprot.writeString(struct.source);
          oprot.writeFieldEnd();
        }
      }
      if (struct.topic != null) {
        if (struct.isSetTopic()) {
          oprot.writeFieldBegin(TOPIC_FIELD_DESC);
          oprot.writeString(struct.topic);
          oprot.writeFieldEnd();
        }
      }
      if (struct.isSetTimestamp()) {
        oprot.writeFieldBegin(TIMESTAMP_FIELD_DESC);
        oprot.writeI64(struct.timestamp);
        oprot.writeFieldEnd();
      }
      if (struct.author != null) {
        if (struct.isSetAuthor()) {
          oprot.writeFieldBegin(AUTHOR_FIELD_DESC);
          oprot.writeString(struct.author);
          oprot.writeFieldEnd();
        }
      }
      if (struct.isSetAge()) {
        oprot.writeFieldBegin(AGE_FIELD_DESC);
        oprot.writeByte(struct.age);
        oprot.writeFieldEnd();
      }
      if (struct.gender != null) {
        if (struct.isSetGender()) {
          oprot.writeFieldBegin(GENDER_FIELD_DESC);
          oprot.writeI32(struct.gender.getValue());
          oprot.writeFieldEnd();
        }
      }
      if (struct.title != null) {
        if (struct.isSetTitle()) {
          oprot.writeFieldBegin(TITLE_FIELD_DESC);
          oprot.writeString(struct.title);
          oprot.writeFieldEnd();
        }
      }
      if (struct.content != null) {
        if (struct.isSetContent()) {
          oprot.writeFieldBegin(CONTENT_FIELD_DESC);
          oprot.writeString(struct.content);
          oprot.writeFieldEnd();
        }
      }
      if (struct.userRating != null) {
        if (struct.isSetUserRating()) {
          oprot.writeFieldBegin(USER_RATING_FIELD_DESC);
          oprot.writeString(struct.userRating);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ThriftMessageRecordTupleSchemeFactory implements SchemeFactory {
    public ThriftMessageRecordTupleScheme getScheme() {
      return new ThriftMessageRecordTupleScheme();
    }
  }

  private static class ThriftMessageRecordTupleScheme extends TupleScheme<ThriftMessageRecord> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ThriftMessageRecord struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.id);
      BitSet optionals = new BitSet();
      if (struct.isSetSource()) {
        optionals.set(0);
      }
      if (struct.isSetTopic()) {
        optionals.set(1);
      }
      if (struct.isSetTimestamp()) {
        optionals.set(2);
      }
      if (struct.isSetAuthor()) {
        optionals.set(3);
      }
      if (struct.isSetAge()) {
        optionals.set(4);
      }
      if (struct.isSetGender()) {
        optionals.set(5);
      }
      if (struct.isSetTitle()) {
        optionals.set(6);
      }
      if (struct.isSetContent()) {
        optionals.set(7);
      }
      if (struct.isSetUserRating()) {
        optionals.set(8);
      }
      oprot.writeBitSet(optionals, 9);
      if (struct.isSetSource()) {
        oprot.writeString(struct.source);
      }
      if (struct.isSetTopic()) {
        oprot.writeString(struct.topic);
      }
      if (struct.isSetTimestamp()) {
        oprot.writeI64(struct.timestamp);
      }
      if (struct.isSetAuthor()) {
        oprot.writeString(struct.author);
      }
      if (struct.isSetAge()) {
        oprot.writeByte(struct.age);
      }
      if (struct.isSetGender()) {
        oprot.writeI32(struct.gender.getValue());
      }
      if (struct.isSetTitle()) {
        oprot.writeString(struct.title);
      }
      if (struct.isSetContent()) {
        oprot.writeString(struct.content);
      }
      if (struct.isSetUserRating()) {
        oprot.writeString(struct.userRating);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ThriftMessageRecord struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.id = iprot.readString();
      struct.setIdIsSet(true);
      BitSet incoming = iprot.readBitSet(9);
      if (incoming.get(0)) {
        struct.source = iprot.readString();
        struct.setSourceIsSet(true);
      }
      if (incoming.get(1)) {
        struct.topic = iprot.readString();
        struct.setTopicIsSet(true);
      }
      if (incoming.get(2)) {
        struct.timestamp = iprot.readI64();
        struct.setTimestampIsSet(true);
      }
      if (incoming.get(3)) {
        struct.author = iprot.readString();
        struct.setAuthorIsSet(true);
      }
      if (incoming.get(4)) {
        struct.age = iprot.readByte();
        struct.setAgeIsSet(true);
      }
      if (incoming.get(5)) {
        struct.gender = ThriftGenderType.findByValue(iprot.readI32());
        struct.setGenderIsSet(true);
      }
      if (incoming.get(6)) {
        struct.title = iprot.readString();
        struct.setTitleIsSet(true);
      }
      if (incoming.get(7)) {
        struct.content = iprot.readString();
        struct.setContentIsSet(true);
      }
      if (incoming.get(8)) {
        struct.userRating = iprot.readString();
        struct.setUserRatingIsSet(true);
      }
    }
  }

}

