package org.apache.torque.datatypes;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.MssqlAdapter;
import org.apache.torque.adapter.MysqlAdapter;
import org.apache.torque.adapter.OracleAdapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.test.dbobject.DateTimeTimestampType;
import org.apache.torque.test.peer.DateTimeTimestampTypePeer;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Tests behavior of date, time and timestamp fields.
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @author <a href="mailto:patrick.carl@web.de">Patrick Carl</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: DateTimeTimestampTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class DateTimeTimestampTest extends BaseDatabaseTestCase
{
    private static Log log = LogFactory.getLog(DateTimeTimestampTest.class);

    /**
     * Tests the date behaviour. Date fields should be truncated to the start
     * of day when saved and reloaded. Note that this does not tell
     * anything about how the field is saved in the database, which can differ
     * between databases.
     *
     * @throws Exception if the test fails
     */
    public void testDateTime() throws Exception
    {
        cleanDateTimeTimestampTable();

        // insert new DateTest object to db
        DateTimeTimestampType dateTimeTimestamp = new DateTimeTimestampType();
        Date now = new Date();
        dateTimeTimestamp.setDateValue(now);
        dateTimeTimestamp.setTimeValue(now);
        dateTimeTimestamp.setTimestampValue(now);
        dateTimeTimestamp.save();

        // reload dateTest from db
        DateTimeTimestampType loaded = DateTimeTimestampTypePeer.retrieveByPK(
                dateTimeTimestamp.getPrimaryKey());

        // calculate expected value
        Date expected;
        //        if (adapter instanceof DBOracle)
        //        {
        //            expected = new Date(now.getTime() / 1000L * 1000L);
        //        }
        //        else
        {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(now);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            expected = calendar.getTime();
        }

        // verify
        assertEquals(expected, loaded.getDateValue());
    }

    /**
     * Tests the time behaviour. Time fields should have the date set
     * to 1.1.1970. Note that this does not tell
     * anything about how the field is saved in the database, which can differ
     * between databases.
     *
     * @throws Exception if the test fails
     */
    public void testTime() throws Exception
    {
        cleanDateTimeTimestampTable();

        // insert new DateTest object to db
        DateTimeTimestampType dateTimeTimestamp = new DateTimeTimestampType();
        Date now = new Date();
        dateTimeTimestamp.setDateValue(now);
        dateTimeTimestamp.setTimeValue(now);
        dateTimeTimestamp.setTimestampValue(now);
        dateTimeTimestamp.save();

        // reload dateTest from db
        DateTimeTimestampType loaded = DateTimeTimestampTypePeer.retrieveByPK(
                dateTimeTimestamp.getPrimaryKey());

        // calculate expected value
        Date expected;
        //        if (adapter instanceof DBOracle)
        //        {
        //            expected = new Date(now.getTime() / 1000L * 1000L);
        //        }
        //        else
        {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(now);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            calendar.set(Calendar.YEAR, 1970);
            calendar.set(Calendar.MILLISECOND, 0);
            expected = calendar.getTime();
        }

        // verify
        long loadedTimestampRounded
        = loaded.getTimeValue().getTime() / 1000L * 1000L;
        assertEquals(expected, new Date(loadedTimestampRounded));
    }

    /**
     * Tests the timestamp accuracy. Timestamp fields should have at least
     * second accuracy.
     *
     * @throws Exception if the test fails
     */
    public void testTimestamp() throws Exception
    {
        cleanDateTimeTimestampTable();

        // insert new DateTest object to db
        DateTimeTimestampType dateTimeTimestamp = new DateTimeTimestampType();
        Date now = new Date();
        dateTimeTimestamp.setDateValue(now);
        dateTimeTimestamp.setTimeValue(now);
        dateTimeTimestamp.setTimestampValue(now);
        dateTimeTimestamp.save();

        // reload dateTest from db
        DateTimeTimestampType loaded = DateTimeTimestampTypePeer.retrieveByPK(
                dateTimeTimestamp.getPrimaryKey());

        // calculate expected value
        Date min = new Date(now.getTime() / 1000L * 1000L);;
        Date max = new Date(min.getTime() + 999L);

        // verify
        DateFormat dateFormat = new SimpleDateFormat();
        Date actual = loaded.getTimestampValue();
        assertFalse(
                "the loaded value " + dateFormat.format(actual)
                + " should not be after " + dateFormat.format(min),
                min.after(actual));
        assertFalse(
                "the loaded value " + dateFormat.format(actual)
                + " should not be before " + dateFormat.format(max),
                max.before(actual));
    }

    /**
     * Checks that Criteria.CURRENT_DATE is larger than a past date value.
     *
     * @throws TorqueException if a problem occurs.
     */
    @ArgumentsSource(AdapterProvider.class)
    public void testCurrentDate(Adapter adapter) throws TorqueException
    {
        if (adapter instanceof MssqlAdapter)
        {
            log.warn("testCurrentDate(): "
                    + Criteria.CURRENT_DATE
                    + "is not supported by MSSQL");
            return;
        }
        fillDateTimeTimestampWithPastEntry();

        Criteria criteria = new Criteria().where(
                DateTimeTimestampTypePeer.DATE_VALUE,
                Criteria.CURRENT_DATE,
                Criteria.GREATER_EQUAL);
        List<DateTimeTimestampType> result
        = DateTimeTimestampTypePeer.doSelect(criteria);
        assertEquals(0, result.size());
    }

    /**
     * Checks that Criteria.CURRENT_TIME is larger than a past date value.
     *
     * @throws TorqueException if a problem occurs.
     */
    @ArgumentsSource(AdapterProvider.class)
    public void testCurrentTime(Adapter adapter) throws TorqueException
    {
        if (adapter instanceof MssqlAdapter
                || adapter instanceof OracleAdapter)
        {
            log.warn("testCurrentTime(): "
                    + Criteria.CURRENT_TIME
                    + "is not supported by MSSQL and Oracle");
            return;
        }
        fillDateTimeTimestampWithPastEntry();
        Criteria criteria = new Criteria().where(
                DateTimeTimestampTypePeer.TIME_VALUE,
                Criteria.CURRENT_TIME,
                Criteria.GREATER_EQUAL);
        List<DateTimeTimestampType> result
        = DateTimeTimestampTypePeer.doSelect(criteria);
        assertEquals(0, result.size());
        
        criteria = new Criteria().where(
                DateTimeTimestampTypePeer.TIME_VALUE,
                Criteria.CURRENT_TIME,
                Criteria.LESS_EQUAL);
        result
        = DateTimeTimestampTypePeer.doSelect(criteria);
        assertEquals(1, result.size());
    }

    /**
     * Checks that Criteria.CURRENT_TIMESTAMP is larger than a past date value.
     *
     * @throws TorqueException if a problem occurs.
     */
    @ArgumentsSource(AdapterProvider.class)
    public void testCurrentTimestamp(Adapter adapter) throws TorqueException
    {
        if (adapter instanceof MssqlAdapter)
        {
            log.warn("testCurrentTimestamp(): "
                    + Criteria.CURRENT_TIMESTAMP
                    + "is not supported by MSSQL");
            return;
        }
        fillDateTimeTimestampWithPastEntry();

        Criteria criteria = new Criteria().where(
                DateTimeTimestampTypePeer.TIMESTAMP_VALUE,
                Criteria.CURRENT_TIMESTAMP,
                Criteria.GREATER_EQUAL);
        List<DateTimeTimestampType> result
        = DateTimeTimestampTypePeer.doSelect(criteria);
        assertEquals(0, result.size());
    }

    /**
     * Checks that a select is possible using a java.util.date object
     * in a date field.
     *
     * @throws TorqueException if a problem occurs.
     */
    public void testSelectWithUtilDateOnDate() throws TorqueException
    {
        cleanDateTimeTimestampTable();

        // insert new DateTest object to db
        DateTimeTimestampType dateTimeTimestamp = new DateTimeTimestampType();
        dateTimeTimestamp.setDateValue(
                new GregorianCalendar(2010, 1, 23).getTime());
        dateTimeTimestamp.setTimeValue(new Date());
        dateTimeTimestamp.setTimestampValue(new Date());
        dateTimeTimestamp.save();

        // execute select
        Criteria criteria = new Criteria();
        criteria.where(DateTimeTimestampTypePeer.DATE_VALUE,
                new GregorianCalendar(2010, 1, 23).getTime());
        List<DateTimeTimestampType> result
        = DateTimeTimestampTypePeer.doSelect(criteria);

        // verify
        assertEquals(1, result.size());
        assertEquals(dateTimeTimestamp, result.get(0));
    }

    /**
     * Checks that a select is possible using a java.util.date object
     * in a time field.
     *
     * @throws TorqueException if a problem occurs.
     */
    public void testSelectWithUtilDateOnTime(Adapter adapter) throws TorqueException
    {
        if (adapter instanceof MssqlAdapter)
        {
            log.warn("testSelectWithUtilDateOnTime(): "
                    + "Selecting time with a date Object will not work on MSSQL"
                    + " because time is 1900 based in MSSQL but 1970 based"
                    + " in java");
            return;
        }
        cleanDateTimeTimestampTable();

        // insert new DateTest object to db
        DateTimeTimestampType dateTimeTimestamp = new DateTimeTimestampType();
        dateTimeTimestamp.setDateValue(new Date());
        dateTimeTimestamp.setTimeValue(new Date(1234000));
        dateTimeTimestamp.setTimestampValue(new Date());
        dateTimeTimestamp.save();

        // execute select
        Criteria criteria = new Criteria();
        
        // java.util.Date cannot be cast to java.sql.Time
        Date queryTimeValue = new java.sql.Time(new Date(1234000).getTime()); //new Date(1234000);
        
        criteria.where(DateTimeTimestampTypePeer.TIME_VALUE, queryTimeValue);
       
        List<DateTimeTimestampType> result
        = DateTimeTimestampTypePeer.doSelect(criteria);
        
        // verify
        assertEquals(1, result.size());
        assertEquals(dateTimeTimestamp, result.get(0));
    }

    /**
     * Checks that a select is possible using a java.sql.time object
     * in a time field.
     *
     * @throws TorqueException if a problem occurs.
     */
    @ArgumentsSource(AdapterProvider.class)
    public void testSelectWithSqlTimeOnTime(Adapter adapter) throws TorqueException
    {
        if (adapter instanceof MssqlAdapter)
        {
            log.warn("testSelectWithUtilDateOnTime(): "
                    + "Selecting time with a time Object will not work on MSSQL"
                    + " because time is 1900 based in MSSQL but 1970 based"
                    + " in java");
            return;
        }
        cleanDateTimeTimestampTable();

        // insert new DateTest object to db
        DateTimeTimestampType dateTimeTimestamp = new DateTimeTimestampType();
        dateTimeTimestamp.setDateValue(new Date());
        dateTimeTimestamp.setTimeValue(new Date(1234000));
        dateTimeTimestamp.setTimestampValue(new Date());
        dateTimeTimestamp.save();

        // execute select
        Criteria criteria = new Criteria();
        criteria.where(
                DateTimeTimestampTypePeer.TIME_VALUE,
                new java.sql.Time(1234000));
        List<DateTimeTimestampType> result
        = DateTimeTimestampTypePeer.doSelect(criteria);

        // verify
        assertEquals(1, result.size());
        assertEquals(dateTimeTimestamp, result.get(0));
    }

    /**
     * Checks that a select is possible using a java.util.date object
     * in a timestamp field.
     *
     * @throws TorqueException if a problem occurs.
     */
    public void testSelectWithUtilDateOnTimestamp() throws TorqueException
    {
        cleanDateTimeTimestampTable();

        // insert new DateTest object to db
        DateTimeTimestampType dateTimeTimestamp = new DateTimeTimestampType();
        dateTimeTimestamp.setDateValue(new Date());
        dateTimeTimestamp.setTimeValue(new Date());
        dateTimeTimestamp.setTimestampValue(
                new GregorianCalendar(2010, 1, 23).getTime());
        dateTimeTimestamp.save();

        // execute select
        Criteria criteria = new Criteria();
        criteria.where(DateTimeTimestampTypePeer.TIMESTAMP_VALUE,
                new GregorianCalendar(2010, 1, 23).getTime());
        List<DateTimeTimestampType> result
        = DateTimeTimestampTypePeer.doSelect(criteria);

        // verify
        assertEquals(1, result.size());
        assertEquals(dateTimeTimestamp, result.get(0));
    }

    /**
     * Checks that a select is possible using a java.util.date object
     * in a timestamp field and does not match for a second difference.
     *
     * @throws TorqueException if a problem occurs.
     */
    public void testSelectWithUtilDateOnTimestampMismatch()
            throws TorqueException
    {
        cleanDateTimeTimestampTable();

        // insert new DateTest object to db
        DateTimeTimestampType dateTimeTimestamp = new DateTimeTimestampType();
        dateTimeTimestamp.setDateValue(new Date());
        dateTimeTimestamp.setTimeValue(new Date());
        dateTimeTimestamp.setTimestampValue(
                new GregorianCalendar(2010, 1, 23).getTime());
        dateTimeTimestamp.save();

        // execute select
        Criteria criteria = new Criteria();
        Date toSelect = new GregorianCalendar(2010, 1, 23).getTime();
        toSelect = new Date(toSelect.getTime() - 1000L);
        criteria.where(DateTimeTimestampTypePeer.TIMESTAMP_VALUE, toSelect);
        List<DateTimeTimestampType> result
        = DateTimeTimestampTypePeer.doSelect(criteria);

        // verify
        assertEquals(0, result.size());
    }

    /**
     * Checks that a select is possible when milliseconds are used.
     * in databases where this is supported.
     *
     * @throws TorqueException if a problem occurs.
     */
    public void testSelectWithMillisecondsOnTimestampExactMatch()
            throws TorqueException
    {
        cleanDateTimeTimestampTable();

        // insert new DateTest object to db
        DateTimeTimestampType dateTimeTimestamp = new DateTimeTimestampType();
        dateTimeTimestamp.setDateValue(new Date());
        dateTimeTimestamp.setTimeValue(new Date());
        GregorianCalendar calendar = new GregorianCalendar(2010, 1, 23);
        calendar.set(GregorianCalendar.MILLISECOND, 123);
        dateTimeTimestamp.setTimestampValue(calendar.getTime());
        dateTimeTimestamp.save();

        // execute matching select
        Criteria criteria = new Criteria();
        calendar = new GregorianCalendar(2010, 1, 23);
        calendar.set(GregorianCalendar.MILLISECOND, 123);
        criteria.where(
                DateTimeTimestampTypePeer.TIMESTAMP_VALUE,
                calendar.getTime());
        List<DateTimeTimestampType> result
        = DateTimeTimestampTypePeer.doSelect(criteria);

        // verify
        assertEquals(1, result.size());
        assertEquals(dateTimeTimestamp, result.get(0));
    }

    /**
     * Checks that a select does not match when a timestamp to select
     * is a millisecond away from the timestamp saved in the database.
     *
     * @throws TorqueException if a problem occurs.
     */
    @ArgumentsSource(AdapterProvider.class)
    public void testSelectWithMillisecondsOnTimestampMillisecondMismatch(Adapter adapter)
            throws TorqueException
    {
        if (!timestampHasMillisecondAccuracy(adapter))
        {
            return;
        }
        cleanDateTimeTimestampTable();

        // insert new DateTest object to db
        DateTimeTimestampType dateTimeTimestamp = new DateTimeTimestampType();
        dateTimeTimestamp.setDateValue(new Date());
        dateTimeTimestamp.setTimeValue(new Date());
        GregorianCalendar calendar = new GregorianCalendar(2010, 1, 23);
        calendar.set(GregorianCalendar.MILLISECOND, 123);
        dateTimeTimestamp.setTimestampValue(calendar.getTime());
        dateTimeTimestamp.save();

        // execute matching select
        Criteria criteria = new Criteria();
        calendar = new GregorianCalendar(2010, 1, 23);
        calendar.set(GregorianCalendar.MILLISECOND, 124);
        criteria.where(
                DateTimeTimestampTypePeer.TIMESTAMP_VALUE,
                calendar.getTime());
        List<DateTimeTimestampType> result
        = DateTimeTimestampTypePeer.doSelect(criteria);

        // verify
        assertEquals(0, result.size());
    }

    /**
     * Checks that a select is possible using a java.sql.timestamp object
     * in a timestamp field.
     *
     * @throws TorqueException if a problem occurs.
     */
    public void testSelectWithSqlTimestampOnTimestamp() throws TorqueException
    {
        cleanDateTimeTimestampTable();

        // insert new DateTest object to db
        DateTimeTimestampType dateTimeTimestamp = new DateTimeTimestampType();
        dateTimeTimestamp.setDateValue(new Date());
        dateTimeTimestamp.setTimeValue(new Date());
        dateTimeTimestamp.setTimestampValue(
                new GregorianCalendar(2010, 1, 23).getTime());
        dateTimeTimestamp.save();

        // execute select
        Criteria criteria = new Criteria();
        Date toSelect = new GregorianCalendar(2010, 1, 23).getTime();
        criteria.where(
                DateTimeTimestampTypePeer.TIMESTAMP_VALUE,
                new java.sql.Timestamp(toSelect.getTime()));
        List<DateTimeTimestampType> result
        = DateTimeTimestampTypePeer.doSelect(criteria);

        // verify
        assertEquals(1, result.size());
        assertEquals(dateTimeTimestamp, result.get(0));
    }

    /**
     * Cleans the DateTimeTimestamp table.
     *
     * @throws TorqueException if cleaning fails.
     */
    private void cleanDateTimeTimestampTable() throws TorqueException
    {
        DateTimeTimestampTypePeer.doDelete(new Criteria());
    }

    private boolean timestampHasMillisecondAccuracy(Adapter adapter)
    {
        if (adapter instanceof MysqlAdapter)
        {
            return false;
        }
        if (adapter instanceof MssqlAdapter)
        {
            // although datetime2 has 100 nanoseconds accurary
            // it seems to get lost in the jtds driver.
            return false;
        }
        return true;
    }

    /**
     * Creates exactly one entry in the DATE_TIME_TIMESTAMP table which
     * has dates in the past.
     *
     * @throws TorqueException if the data cannot be deleted or saved.
     */
    private void fillDateTimeTimestampWithPastEntry() throws TorqueException
    {
        DateTimeTimestampTypePeer.doDelete(new Criteria());
        DateTimeTimestampType dateTimeTimestamp = new DateTimeTimestampType();
        dateTimeTimestamp.setDateValue(
                new GregorianCalendar(2000, 1, 1).getTime());
        dateTimeTimestamp.setTimeValue(
                new GregorianCalendar(2000, 1, 1).getTime());
        dateTimeTimestamp.setTimestampValue(
                new GregorianCalendar(2000, 1, 1).getTime());
        dateTimeTimestamp.save();
    }
}
