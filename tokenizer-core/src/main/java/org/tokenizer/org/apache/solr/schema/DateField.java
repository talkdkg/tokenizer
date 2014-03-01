/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tokenizer.org.apache.solr.schema;

import java.io.IOException;
import java.text.*;
import java.util.*;

// TODO: make a FlexibleDateField that can accept dates in multiple
// formats, better for human entered dates.

// TODO: make a DayField that only stores the day?

/**
 * FieldType that can represent any Date/Time with millisecond precision.
 * <p>
 * Date Format for the XML, incoming and outgoing:
 * </p>
 * <blockquote> A date field shall be of the form 1995-12-31T23:59:59Z The
 * trailing "Z" designates UTC time and is mandatory (See below for an
 * explanation of UTC). Optional fractional seconds are allowed, as long as they
 * do not end in a trailing 0 (but any precision beyond milliseconds will be
 * ignored). All other parts are mandatory. </blockquote>
 * <p>
 * This format was derived to be standards compliant (ISO 8601) and is a more
 * restricted form of the <a
 * href="http://www.w3.org/TR/xmlschema-2/#dateTime-canonical-representation"
 * >canonical representation of dateTime</a> from XML schema part 2. Examples...
 * </p>
 * <ul>
 * <li>1995-12-31T23:59:59Z</li>
 * <li>1995-12-31T23:59:59.9Z</li>
 * <li>1995-12-31T23:59:59.99Z</li>
 * <li>1995-12-31T23:59:59.999Z</li>
 * </ul>
 * <p>
 * Note that DateField is lenient with regards to parsing fractional seconds
 * that end in trailing zeros and will ensure that those values are indexed in
 * the correct canonical format.
 * </p>
 * <p>
 * This FieldType also supports incoming "Date Math" strings for computing
 * values by adding/rounding internals of time relative either an explicit
 * datetime (in the format specified above) or the literal string "NOW", ie:
 * "NOW+1YEAR", "NOW/DAY", "1995-12-31T23:59:59.999Z+5MINUTES", etc... -- see
 * {@link DateMathParser} for more examples.
 * </p>
 * <p>
 * <b>NOTE:</b> Allthough it is possible to configure a <code>DateField</code>
 * instance with a default value of "<code>NOW</code>" to compute a timestamp of
 * when the document was indexed, this is not advisable when using SolrCloud
 * since each replica of the document may compute a slightly different value.
 * {@link TimestampUpdateProcessorFactory} is recomended instead.
 * </p>
 * 
 * <p>
 * Explanation of "UTC"...
 * </p>
 * <blockquote> "In 1970 the Coordinated Universal Time system was devised by an
 * international advisory group of technical experts within the International
 * Telecommunication Union (ITU). The ITU felt it was best to designate a single
 * abbreviation for use in all languages in order to minimize confusion. Since
 * unanimous agreement could not be achieved on using either the English word
 * order, CUT, or the French word order, TUC, the acronym UTC was chosen as a
 * compromise." </blockquote>
 * 
 * 
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#dateTime">XML schema part
 *      2</a>
 * @deprecated {@link TrieDateField} is recomended for all new schemas
 */
public class DateField
{

    public static TimeZone UTC = TimeZone.getTimeZone("UTC");

    /**
     * Fixed TimeZone (UTC) needed for parsing/formating Dates in the canonical
     * representation.
     */
    protected static final TimeZone CANONICAL_TZ = UTC;

    /**
     * Fixed Locale needed for parsing/formating Milliseconds in the canonical
     * representation.
     */
    protected static final Locale CANONICAL_LOCALE = Locale.ROOT;

    // The XML (external) date format will sort correctly, except if
    // fractions of seconds are present (because '.' is lower than 'Z').
    // The easiest fix is to simply remove the 'Z' for the internal
    // format.

    protected static String NOW = "NOW";

    protected static char Z = 'Z';

    private static char[] Z_ARRAY = new char[] { Z };

    /**
     * Returns a formatter that can be use by the current thread if needed to
     * convert Date objects to the Internal representation.
     * 
     * Only the <tt>format(Date)</tt> can be used safely.
     * 
     * @deprecated - use formatDate(Date) instead
     */
    @Deprecated
    protected DateFormat getThreadLocalDateFormat()
    {
        return fmtThreadLocal.get();
    }

    /**
     * Thread safe method that can be used by subclasses to format a Date using
     * the Internal representation.
     */
    protected String formatDate(Date d)
    {
        return fmtThreadLocal.get().format(d);
    }

    /**
     * Return the standard human readable form of the date
     */
    public static String formatExternal(Date d)
    {
        return fmtThreadLocal.get().format(d) + 'Z';
    }

    /**
     * @see #formatExternal
     */
    public String toExternal(Date d)
    {
        return formatExternal(d);
    }

    /**
     * Thread safe method that can be used by subclasses to parse a Date that is
     * already in the internal representation
     */
    public static Date parseDate(String s) throws ParseException
    {
        return fmtThreadLocal.get().parse(s);
    }

    /**
     * Thread safe DateFormat that can <b>format</b> in the canonical ISO8601
     * date format, not including the trailing "Z" (since it is left off in the
     * internal indexed values)
     */
    private final static ThreadLocalDateFormat fmtThreadLocal = new ThreadLocalDateFormat(
            new ISO8601CanonicalDateFormat());

    private static class ISO8601CanonicalDateFormat extends SimpleDateFormat
    {

        protected NumberFormat millisParser = NumberFormat.getIntegerInstance(CANONICAL_LOCALE);

        protected NumberFormat millisFormat = new DecimalFormat(".###",
                new DecimalFormatSymbols(CANONICAL_LOCALE));

        public ISO8601CanonicalDateFormat()
        {
            super("yyyy-MM-dd'T'HH:mm:ss", CANONICAL_LOCALE);
            this.setTimeZone(CANONICAL_TZ);
        }

        @Override
        public Date parse(String i, ParsePosition p)
        {
            /* delegate to SimpleDateFormat for easy stuff */
            Date d = super.parse(i, p);
            int milliIndex = p.getIndex();
            /* worry about the milliseconds ourselves */
            if (null != d &&
                    -1 == p.getErrorIndex() &&
                    milliIndex + 1 < i.length() &&
                    '.' == i.charAt(milliIndex))
            {
                p.setIndex(++milliIndex); // NOTE: ++ to chomp '.'
                Number millis = millisParser.parse(i, p);
                if (-1 == p.getErrorIndex())
                {
                    int endIndex = p.getIndex();
                    d = new Date(d.getTime()
                            + (long) (millis.doubleValue() *
                            Math.pow(10, (3 - endIndex + milliIndex))));
                }
            }
            return d;
        }

        @Override
        public StringBuffer format(Date d, StringBuffer toAppendTo,
                FieldPosition pos)
        {
            /* delegate to SimpleDateFormat for easy stuff */
            super.format(d, toAppendTo, pos);
            /* worry aboutthe milliseconds ourselves */
            long millis = d.getTime() % 1000l;
            if (0L == millis)
            {
                return toAppendTo;
            }
            if (millis < 0L)
            {
                // original date was prior to epoch
                millis += 1000L;
            }
            int posBegin = toAppendTo.length();
            toAppendTo.append(millisFormat.format(millis / 1000d));
            if (DateFormat.MILLISECOND_FIELD == pos.getField())
            {
                pos.setBeginIndex(posBegin);
                pos.setEndIndex(toAppendTo.length());
            }
            return toAppendTo;
        }

        @Override
        public DateFormat clone()
        {
            ISO8601CanonicalDateFormat c = (ISO8601CanonicalDateFormat) super.clone();
            c.millisParser = NumberFormat.getIntegerInstance(CANONICAL_LOCALE);
            c.millisFormat = new DecimalFormat(".###",
                    new DecimalFormatSymbols(CANONICAL_LOCALE));
            return c;
        }
    }

    private static class ThreadLocalDateFormat extends ThreadLocal<DateFormat>
    {
        DateFormat proto;

        public ThreadLocalDateFormat(DateFormat d)
        {
            super();
            proto = d;
        }

        @Override
        protected DateFormat initialValue()
        {
            return (DateFormat) proto.clone();
        }
    }

}
