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
package org.xaloon.core.api.date;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author vytautas r.
 */
public interface DateService extends Serializable {

    /**
     * @return instance of the {@link DateFormat} with short date format
     */
    DateFormat getShortDateFormat();

    /**
     * 
     * @return instance of the {@link DateFormat} with long date format
     */
    DateFormat getLongDateFormat();

    /**
     * @param dateToFormat
     * @return formatted date with long date format
     */
    String formatWithLongDate(Date dateToFormat);

}
