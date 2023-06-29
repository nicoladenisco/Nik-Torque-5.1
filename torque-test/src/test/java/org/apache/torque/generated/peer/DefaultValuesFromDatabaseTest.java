package org.apache.torque.generated.peer;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.Torque;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.DerbyAdapter;
import org.apache.torque.adapter.HsqldbAdapter;
import org.apache.torque.adapter.MssqlAdapter;
import org.apache.torque.adapter.MysqlAdapter;
import org.apache.torque.adapter.OracleAdapter;
import org.apache.torque.adapter.PostgresAdapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.test.dbobject.CurrentDateTable;
import org.apache.torque.test.dbobject.CurrentTimeTable;
import org.apache.torque.test.dbobject.CurrentTimestampTable;
import org.apache.torque.test.dbobject.DatabaseDefaultValues;
import org.apache.torque.test.peer.CurrentDateTablePeer;
import org.apache.torque.test.peer.CurrentTimeTablePeer;
import org.apache.torque.test.peer.CurrentTimestampTablePeer;
import org.apache.torque.test.peer.DatabaseDefaultValuesPeer;
import org.apache.torque.util.ColumnValues;
import org.apache.torque.util.JdbcTypedValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ArgumentsSource;


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
 * Tests whether the useDatabaseDefaultValue attribute works.
 *
 * @version $Id: DefaultValuesFromDatabaseTest.java 1880635 2020-08-06 11:28:14Z gk $
 */
public class DefaultValuesFromDatabaseTest extends BaseDatabaseTestCase
{
    private static Log log
    = LogFactory.getLog(DefaultValuesFromDatabaseTest.class);

    /** The default date format. */
    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Checks the java default values.
     */
    @Test
    public void testJavaDefault() throws Exception
    {
        DatabaseDefaultValues databaseDefaultValues
            = new DatabaseDefaultValues();
        assertEquals(Integer.valueOf(2), databaseDefaultValues.getOInteger());
        assertEquals(4, databaseDefaultValues.getPInt());
        assertEquals("Default!", databaseDefaultValues.getVarcharField());
        // For date values, java fields are null if the default is read
        // from the database. This is because different databases
        // handle dates differently so it is difficult to predict
        // from java side what the database value will be.
        assertEquals(null, databaseDefaultValues.getDateField());
        assertEquals(null, databaseDefaultValues.getTimeField());
        assertEquals(null, databaseDefaultValues.getTimestampField());
    }

    /**
     * Checks that the buildColumnValues for an unchanged object
     * does not contain the values with database default.
     */
    @Test
    public void testGetColumnValuesNewUnchangedObject() throws Exception
    {
        DatabaseDefaultValues databaseDefaultValues
            = new DatabaseDefaultValues();
        ColumnValues columnValues
        = DatabaseDefaultValuesPeer.buildColumnValues(
                databaseDefaultValues);
        assertEquals(1, columnValues.size());
        assertEquals(
                columnValues.get(DatabaseDefaultValuesPeer.NORMAL_PAYLOAD),
                new JdbcTypedValue(0, Types.INTEGER));
    }

    /**
     * Checks the values in the database if a new object is saved.
     */
    @ArgumentsSource(AdapterProvider.class)
    public void testNewObjectDatabaseDefault(Adapter adapter) throws Exception
    {
        DatabaseDefaultValuesPeer.doDelete(new Criteria());
        DatabaseDefaultValues databaseDefaultValues
            = new DatabaseDefaultValues();

        databaseDefaultValues.save();

        // saved object should stay the same
        assertEquals(Integer.valueOf(2), databaseDefaultValues.getOInteger());
        assertEquals(4, databaseDefaultValues.getPInt());
        assertEquals("Default!", databaseDefaultValues.getVarcharField());
        assertEquals(null, databaseDefaultValues.getDateField());
        assertEquals(null, databaseDefaultValues.getTimeField());
        assertEquals(null, databaseDefaultValues.getTimestampField());
        // re-loading should give the database default values
        List<DatabaseDefaultValues> databaseDefaultValuesList
        = DatabaseDefaultValuesPeer.doSelect(new Criteria());
        assertEquals(1, databaseDefaultValuesList.size());
        DatabaseDefaultValues databaseDefaultValuesSaved
        = databaseDefaultValuesList.get(0);
        assertEquals(
                new Integer(2),
                databaseDefaultValuesSaved.getOInteger());
        assertEquals(4, databaseDefaultValuesSaved.getPInt());
        assertEquals("Default!", databaseDefaultValuesSaved.getVarcharField());
        assertEquals(toString(doSelect(toDate("2010-09-08 00:00:00"),
                java.sql.Date.class, adapter)),
                toString(databaseDefaultValuesSaved.getDateField()));
        assertEquals(toString(doSelect(toDate("1970-01-01 10:20:30"),
                java.sql.Time.class, adapter)),
                toString(databaseDefaultValuesSaved.getTimeField()));
        assertEquals(toString(doSelect(toDate("2010-09-08 11:12:13"),
                java.sql.Timestamp.class, adapter)),
                toString(databaseDefaultValuesSaved.getTimestampField()));
    }

    /**
     * Tests that values are saved if they are not equal to the default value
     * on a new object.
     */
    @Test
    public void testNewObjectChangedValue(Adapter adapter) throws Exception
    {
        DatabaseDefaultValuesPeer.doDelete(new Criteria());
        DatabaseDefaultValues databaseDefaultValues
            = new DatabaseDefaultValues();
        databaseDefaultValues.setOInteger(1);
        databaseDefaultValues.setPInt(3);
        databaseDefaultValues.setVarcharField("Changed!");
        databaseDefaultValues.setDateField(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .parse("2000-01-02 00:00:00"));
        databaseDefaultValues.setTimeField(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .parse("1970-01-01 11:21:31"));
        databaseDefaultValues.setTimestampField(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .parse("2010-09-08 12:13:14"));

        databaseDefaultValues.save();

        // saved object should stay the same
        assertEquals(Integer.valueOf(1), databaseDefaultValues.getOInteger());
        assertEquals(3, databaseDefaultValues.getPInt());
        assertEquals("Changed!", databaseDefaultValues.getVarcharField());
        assertEquals(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .parse("2000-01-02 00:00:00"),
                databaseDefaultValues.getDateField());
        assertEquals(    		
        		 new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                 .parse("1970-01-01 11:21:31"),
                databaseDefaultValues.getTimeField());
        assertEquals(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .parse("2010-09-08 12:13:14"),
                databaseDefaultValues.getTimestampField());
        List<DatabaseDefaultValues> defaultValuesList
        = DatabaseDefaultValuesPeer.doSelect(new Criteria());
        assertEquals(1, defaultValuesList.size());
        DatabaseDefaultValues databaseDefaultValuesSaved
        = defaultValuesList.get(0);
        assertEquals(
                new Integer(1),
                databaseDefaultValuesSaved.getOInteger());
        assertEquals(3, databaseDefaultValuesSaved.getPInt());
        assertEquals("Changed!", databaseDefaultValuesSaved.getVarcharField());
        assertEquals( toString(databaseDefaultValuesSaved.getDateField()),
                toString(doSelect(toDate("2000-01-02 00:00:00"),
                        java.sql.Date.class, adapter)));
        assertEquals( toString(databaseDefaultValuesSaved.getTimeField()),
                toString(doSelect(toDate("1970-01-01 11:21:31"),
                        java.sql.Time.class, adapter)));
        assertEquals(toString(databaseDefaultValuesSaved.getTimestampField()),
                toString(doSelect(toDate("2010-09-08 12:13:14"),
                        java.sql.Timestamp.class, adapter)));
    }

    /**
     * Checks the second save also saves unchanged values.
     */
    @Test
    public void testNotNewObjectSavesUnchangedValues() throws Exception
    {
        DatabaseDefaultValuesPeer.doDelete(new Criteria());
        DatabaseDefaultValues databaseDefaultValues
            = new DatabaseDefaultValues();
        databaseDefaultValues.save();
        // modify object in db so we can check that the unchanged values
        // are saved
        List<DatabaseDefaultValues> databaseDefaultValuesList
        = DatabaseDefaultValuesPeer.doSelect(new Criteria());
        assertEquals(1, databaseDefaultValuesList.size());
        DatabaseDefaultValues changedValuesInDatabase
        = databaseDefaultValuesList.get(0);
        changedValuesInDatabase.setOInteger(1);
        changedValuesInDatabase.setPInt(3);
        changedValuesInDatabase.setVarcharField("Changed!");
        changedValuesInDatabase.setDateField(
                new GregorianCalendar(1990, 2, 4).getTime());
        changedValuesInDatabase.setTimeField(new Date(2500L));
        changedValuesInDatabase.setTimestampField(
                new GregorianCalendar(1990, 2, 4).getTime());
        changedValuesInDatabase.save();
        databaseDefaultValues.setModified(true);

        // second save behaves differently because object is not new any more
        // unchanged values should also be saved
        databaseDefaultValues.save();

        // saved object should stay the same
        assertEquals(Integer.valueOf(2), databaseDefaultValues.getOInteger());
        assertEquals(4, databaseDefaultValues.getPInt());
        assertEquals("Default!", databaseDefaultValues.getVarcharField());
        assertEquals(null, databaseDefaultValues.getDateField());
        assertEquals(null, databaseDefaultValues.getTimeField());
        assertEquals(null, databaseDefaultValues.getTimestampField());
        // re-loading should give the unchanged values
        // (changes in the db were overwritten by the second save)
        databaseDefaultValuesList
        = DatabaseDefaultValuesPeer.doSelect(new Criteria());
        assertEquals(1, databaseDefaultValuesList.size());
        DatabaseDefaultValues databaseDefaultValuesSaved
        = databaseDefaultValuesList.get(0);
        assertEquals(
                new Integer(2),
                databaseDefaultValuesSaved.getOInteger());
        assertEquals(4, databaseDefaultValuesSaved.getPInt());
        assertEquals("Default!", databaseDefaultValuesSaved.getVarcharField());
        assertEquals(null, databaseDefaultValuesSaved.getDateField());
        assertEquals(null, databaseDefaultValuesSaved.getTimeField());
        assertEquals(null, databaseDefaultValuesSaved.getTimestampField());
    }

    /**
     * Checks that if CURRENT_DATE is used as default value
     * then the database saves the current date.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testCurrentDateAsDefault(Adapter adapter) throws Exception
    {
        if (!canUseCurrentDateAsDefaultForDate(adapter))
        {
            return;
        }
        CurrentDateTablePeer.doDelete(new Criteria());
        CurrentDateTable currentDate = new CurrentDateTable();

        Date currentDateBefore = doSelect("CURRENT_DATE", java.sql.Date.class, adapter);
        currentDate.save();
        Date currentDateAfter = doSelect("CURRENT_DATE", java.sql.Date.class, adapter);

        List<CurrentDateTable> dbStateList
        = CurrentDateTablePeer.doSelect(new Criteria());
        assertEquals(1, dbStateList.size());
        CurrentDateTable dbState = dbStateList.get(0);
        assertFalse(
                "currentDate should be >= currentDateBefore",
                dbState.getCurrentDateValue().before(currentDateBefore));
        assertFalse(
                "currentDate should be <= currentDateAfter",
                dbState.getCurrentDateValue().after(currentDateAfter));
    }

    /**
     * Checks that if CURRENT_TIME is used as default value
     * then the database saves the current date.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testCurrentTimeAsDefault(Adapter adapter) throws Exception
    {
        if (!canUseCurrentTimeAsDefaultForTime(adapter))
        {
            return;
        }
        CurrentTimeTablePeer.doDelete(new Criteria());
        CurrentTimeTable currentTime = new CurrentTimeTable();

        Date currentDateBefore = doSelect("CURRENT_TIME", Time.class, adapter);
        currentTime.save();
        Date currentDateAfter = doSelect("CURRENT_TIME", Time.class, adapter);

        List<CurrentTimeTable> dbStateList
        = CurrentTimeTablePeer.doSelect(new Criteria());
        assertEquals(1, dbStateList.size());
        CurrentTimeTable dbState = dbStateList.get(0);
        if (timezoneBuggyInCurrentTime(adapter))
        {
            return;
        }
        assertFalse(
                "currentDate should be >= currentDateBefore",
                dbState.getCurrentTimeValue().before(currentDateBefore));
        assertFalse(
                "currentDate should be <= currentDateAfter",
                dbState.getCurrentTimeValue().after(currentDateAfter));
    }

    /**
     * Checks that if CURRENT_TIMESTAMP is used as default value
     * then the database saves the current date.
     *
     * @throws Exception if an error occurs.
     */ 
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testCurrentTimestampAsDefault(Adapter adapter) throws Exception
    {
        if (!canUseCurrentTimestampAsDefaultForTimestamp(adapter))
        {
            return;
        }
        CurrentTimestampTablePeer.doDelete(new Criteria());
        CurrentTimestampTable currentTimestamp = new CurrentTimestampTable();

        Date currentTimestampBefore = doSelect("CURRENT_TIMESTAMP", Timestamp.class, adapter);
        currentTimestamp.save();
        Date currentTimestampAfter = doSelect("CURRENT_TIMESTAMP", Timestamp.class, adapter);

        List<CurrentTimestampTable> dbStateList
        = CurrentTimestampTablePeer.doSelect(new Criteria());
        assertEquals(1, dbStateList.size());
        CurrentTimestampTable dbState = dbStateList.get(0);
        assertFalse(
                "currentDate should be >= currentDateBefore",
                dbState.getCurrentTimestampValue().before(
                        currentTimestampBefore));
        assertFalse(
                "currentDate should be <= currentDateAfter",
                dbState.getCurrentTimestampValue().after(
                        currentTimestampAfter));
    }

    private Date doSelect(final Date toSelect, final Class<?> classToSelect, Adapter adapter)
            throws Exception
    {
        String dateFormat;
        if (adapter instanceof OracleAdapter)
        {
            dateFormat = "'to_timestamp('''yyyy-MM-dd HH:mm:ss''',''yyyy-mm-dd hh24:mi:ss'')'";
        }
        else if (adapter instanceof MysqlAdapter
                && (classToSelect == java.sql.Time.class))
        {
            dateFormat = "''HH:mm:ss''";
        }
        else if (adapter instanceof MssqlAdapter)
        {
            dateFormat = "''yyyyMMdd HH:mm:ss''";
        }
        else if (adapter instanceof HsqldbAdapter
                || adapter instanceof DerbyAdapter)
        {
            if (classToSelect == java.sql.Date.class)
            {
                dateFormat = "''yyyy-MM-dd''";
            }
            else if (classToSelect == java.sql.Time.class)
            {
                dateFormat = "''HH:mm:ss''";
            }
            else
            {
                dateFormat = "''yyyy-MM-dd HH:mm:ss''";
            }
        }
        else
        {
            dateFormat = "''yyyy-MM-dd HH:mm:ss''";
        }
        String dateString = new SimpleDateFormat(dateFormat).format(toSelect);
        return doSelect(dateString, classToSelect, adapter);
    }

    private Date doSelect(final String toSelect, final Class<?> classToSelect, Adapter adapter)
            throws Exception
    {
        String sql;
        if (adapter instanceof OracleAdapter)
        {
            sql = "select " + toSelect + " from dual";
        }
        else if (adapter instanceof DerbyAdapter)
        {
            sql = "values(" + toSelect + ")";
        }
        else if (adapter instanceof HsqldbAdapter)
        {
            sql = "call " + toSelect;
        }
        else if (adapter instanceof MssqlAdapter)
        {
            sql = "select convert(datetime," + toSelect +")";
        }
        else
        {
            sql = "select " + toSelect;
        }
        Connection connection = null;
        try
        {
            connection = Torque.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (!resultSet.next())
            {
                fail("Statement " + sql + " returned 0 rows");
            }
            Date result;
            if (Time.class == classToSelect)
            {
                result= resultSet.getTime(1);
            }
            else if (java.sql.Date.class == classToSelect)
            {
                result= resultSet.getDate(1);
            }
            else if (Timestamp.class == classToSelect)
            {
                result= resultSet.getTimestamp(1);
            }
            else
            {
                throw new IllegalArgumentException("unknown classToSelect "
                        + classToSelect.getName());
            }
            if (resultSet.next())
            {
                fail("Statement " + sql + " returned more than 1 row");
            }
            return result;
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (Exception e)
                {
                    // ignore
                }
            }
        }
    }

    private static boolean canUseCurrentDateAsDefaultForDate(Adapter adapter)
    {
        if (adapter instanceof MysqlAdapter
                || adapter instanceof MssqlAdapter)
        {
            log.warn("canUseCurrentDateAsDefaultForDate(): "
                    + "CURRENT_DATE cannot be used as default value "
                    + "for Date columns for MySQL and MSSQL");
            return false;
        }
        return true;
    }

    private static boolean canUseCurrentTimeAsDefaultForTime(Adapter adapter)
    {
        if (adapter instanceof MysqlAdapter
                || adapter instanceof OracleAdapter
                || adapter instanceof MssqlAdapter)
        {
            log.warn("canUseCurrentTimeAsDefaultForTime(): "
                    + "CURRENT_TIME cannot be used as default value "
                    + "for Time columns for MySQL, Oracle and MSSQL");
            return false;
        }
        return true;
    }

    private static boolean canUseCurrentTimestampAsDefaultForTimestamp(Adapter adapter)
    {
        if (adapter instanceof MysqlAdapter)
        {
            log.warn("canUseCurrentDateAsDefaultForDate(): "
                    + "CURRENT_TIMESTAMP cannot be used as default value "
                    + "for Timestamp columns for MySQL");
            return false;
        }
        return true;
    }

    private static boolean timezoneBuggyInCurrentTime(Adapter adapter)
    {
        if (adapter instanceof PostgresAdapter)
        {
            log.warn("Timezone is buggy in CURRENT_TIME in Postgres");
            return true;
        }
        return false;
    }

    private static Date toDate(final String toConvert) throws ParseException
    {
        return new SimpleDateFormat(DATE_FORMAT).parse(toConvert);
    }

    private static String toString(final Date toConvert)
    {
        return new SimpleDateFormat(DATE_FORMAT).format(toConvert);
    }
}
