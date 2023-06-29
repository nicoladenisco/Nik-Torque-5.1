package org.apache.torque.generated.peer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.MssqlAdapter;
import org.apache.torque.adapter.MysqlAdapter;
import org.apache.torque.adapter.OracleAdapter;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.test.dbobject.JavaDefaultValues;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

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

/**
 * Tests that the default attribute works for columns.
 *
 * @version $Id: DefaultValuesFromJavaTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class DefaultValuesFromJavaTest extends BaseDatabaseTestCase
{
    private static Log log = LogFactory.getLog(DefaultValuesFromJavaTest.class);

    /** The default date format. */
    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Checks that if CURRENT_DATE is used as non database default value (useDatabaseDefaultValue is not set)
     * then an object is constructed with current java time.
     *
     * @throws Exception if an error occurs.
     */
    @ArgumentsSource(AdapterProvider.class)
    public void testCurrentDateAsJavaDefault(Adapter adapter) throws Exception
    {
        if (adapter instanceof MssqlAdapter
                || adapter instanceof MysqlAdapter)
        {
            log.error("testCurrentDateAsJavaDefault(): "
                    + "MSSQL and Mysql do not support the CURRENT_DATE function");
            return;
        }
        GregorianCalendar currentCalendarBefore = new GregorianCalendar();
        currentCalendarBefore.set(GregorianCalendar.HOUR_OF_DAY, 0);
        currentCalendarBefore.set(GregorianCalendar.MINUTE, 0);
        currentCalendarBefore.set(GregorianCalendar.SECOND, 0);
        currentCalendarBefore.set(GregorianCalendar.MILLISECOND, 0);
        JavaDefaultValues javaDefaultValues = new JavaDefaultValues();
        
        if (javaDefaultValues.getCurrentDateValue() == null) {
            // if the schema has useDatabaseDefaultValue current time is set null.
            log.error("testCurrentDateAsJavaDefault(): "
                    + "useDatabaseDefaultValue is set, default value is set in database!");
            return;
        }
        GregorianCalendar currentCalendarAfter = new GregorianCalendar();
        currentCalendarAfter.set(GregorianCalendar.HOUR_OF_DAY, 0);
        currentCalendarAfter.set(GregorianCalendar.MINUTE, 0);
        currentCalendarAfter.set(GregorianCalendar.SECOND, 0);
        currentCalendarAfter.set(GregorianCalendar.MILLISECOND, 0);

        assertFalse(
                "currentDate " + javaDefaultValues.getCurrentDateValue()
                + " should be >= currentDateBefore "
                + currentCalendarBefore.getTime(),
                javaDefaultValues.getCurrentDateValue().before(
                        currentCalendarBefore.getTime()));
        assertFalse(
                "currentDate " + javaDefaultValues.getCurrentDateValue()
                + " should be <= currentDateAfter "
                + currentCalendarAfter.getTime(),
                javaDefaultValues.getCurrentDateValue().after(
                        currentCalendarAfter.getTime()));
    }

    /**
     * Checks that if CURRENT_TIME is used as non database default value
     * (useDatabaseDefaultValue is not set)
     * then an object is constructed with current java time.
     *
     * @throws Exception if an error occurs.
     */
    @ArgumentsSource(AdapterProvider.class)
    public void testCurrentTimeAsJavaDefault(Adapter adapter) throws Exception
    {
        if (adapter instanceof OracleAdapter
                || adapter instanceof MssqlAdapter
                || adapter instanceof MysqlAdapter)
        {
            log.error("testCurrentTimeAsJavaDefault(): "
                    + "Oracle, MSSQL and Mysql do not support "
                    + "the CURRENT_TIME function");
            return;
        }
        GregorianCalendar currentCalendarBefore = new GregorianCalendar();
        currentCalendarBefore.set(1970, 1, 1);
        JavaDefaultValues javaDefaultValues = new JavaDefaultValues();
        if (javaDefaultValues.getCurrentTimeValue() == null) {
            // if the schema has useDatabaseDefaultValue current time is set null.
            log.error("testCurrentDateAsJavaDefault(): "
                    + "useDatabaseDefaultValue is set, default value is set in database!");
            return;
        }
        GregorianCalendar currentCalendarAfter = new GregorianCalendar();
        currentCalendarAfter.set(1970, 1, 1);
        assertFalse(
                "currentTime should be >= currentCalendarBefore",
                javaDefaultValues.getCurrentTimeValue().before(
                        currentCalendarBefore.getTime()));
        assertFalse(
                "currentTime should be <= currentDateAfter",
                javaDefaultValues.getCurrentTimeValue().after(
                    currentCalendarAfter.getTime()));
    }

    /**
     * Checks that if CURRENT_TIMESTAMP is used as default value
     * then an object is constructed with current java time.
     *
     * @throws Exception if an error occurs.
     */
    @ArgumentsSource(AdapterProvider.class)
    public void testCurrentTimestampAsJavaDefault(Adapter adapter) throws Exception
    {
        if (adapter instanceof MysqlAdapter)
        {
            log.error("testCurrentTimestampAsJavaDefault(): "
                    + "Mysql does not support "
                    + "the CURRENT_TIMESTAMP function");
            return;
        }
        Date currentDateBefore = new Date();
        JavaDefaultValues javaDefaultValues = new JavaDefaultValues();
        Date currentDateAfter = new Date();

        assertFalse(
                "currentTime should be >= currentDateBefore",
                javaDefaultValues.getCurrentTimestampValue().before(
                        currentDateBefore));
        assertFalse(
                "currentTime should be <= currentDateAfter",
                javaDefaultValues.getCurrentTimestampValue().after(
                        currentDateAfter));
    }

    /**
     * Checks that we can set a java default value to an Integer column.
     *
     * @throws Exception if an error occurs.
     */
    public void testIntegerDefault() throws Exception
    {
        JavaDefaultValues javaDefaultValues = new JavaDefaultValues();

        assertEquals(
                Integer.valueOf(2),
                javaDefaultValues.getOInteger());
    }

    /**
     * Checks that we can set a java default value to an int column.
     *
     * @throws Exception if an error occurs.
     */
    public void testIntDefault() throws Exception
    {
        JavaDefaultValues javaDefaultValues = new JavaDefaultValues();

        assertEquals(4, javaDefaultValues.getPInt());
    }

    /**
     * Checks that we can set a java default value to an Varchar column.
     *
     * @throws Exception if an error occurs.
     */
    public void testVarcharDefault() throws Exception
    {
        JavaDefaultValues javaDefaultValues = new JavaDefaultValues();

        assertEquals("Default!", javaDefaultValues.getVarcharField());
    }


    /**
     * Checks that we can set a default value to an Date column.
     *
     * @throws Exception if an error occurs.
     */
    public void testDateDefault() throws Exception
    {
        JavaDefaultValues javaDefaultValues = new JavaDefaultValues();

        assertEquals("2010-09-08 00:00:00",
                toString(javaDefaultValues.getDateField()));
    }

    /**
     * Checks that we can set a default value to an Date column.
     *
     * @throws Exception if an error occurs.
     */
    public void testTimeDefault() throws Exception
    {
        JavaDefaultValues javaDefaultValues = new JavaDefaultValues();

        assertEquals("1970-01-01 10:20:30",
                toString(javaDefaultValues.getTimeField()));
    }

    /**
     * Checks that we can set a default value to an Date column.
     *
     * @throws Exception if an error occurs.
     */
    public void testTimestampDefault() throws Exception
    {
        JavaDefaultValues javaDefaultValues = new JavaDefaultValues();

        assertEquals(
                "2010-09-08 11:12:13",
                toString(javaDefaultValues.getTimestampField()));
    }

    private static String toString(Date toConvert)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(toConvert);
    }
}
