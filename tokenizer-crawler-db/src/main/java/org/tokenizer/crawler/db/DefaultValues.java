package org.tokenizer.crawler.db;

import java.util.Date;

import org.apache.tika.metadata.Metadata;

public interface DefaultValues {

    public static final byte[] EMPTY_ARRAY = new byte[0];
    public static final String EMPTY_STRING = "";
    public static final Date EMPTY_DATE = new Date(0);
    public static final String UTF8_CHARSET = "UTF-8";
    public static final String ASCII_CHARSET = "US-ASCII";
    public static final Metadata EMPTY_METADATA = new Metadata();
}
