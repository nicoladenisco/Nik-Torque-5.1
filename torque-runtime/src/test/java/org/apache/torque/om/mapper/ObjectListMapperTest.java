package org.apache.torque.om.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.torque.BaseTestCase;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ObjectListMapperTest extends BaseTestCase
{
    /** Mock result set. */
    @Mock
    private ResultSet resultSet;

    /** Mock result set metadata. */
    @Mock
    private ResultSetMetaData resultSetMetaData;

    /** Mock array. */
    @Mock
    private Array array;

    /** Mock blob. */
    @Mock
    private Blob blob;

    /** Mock clob. */
    @Mock
    private Clob clob;

    /** Mock input stream. */
    @Mock
    private InputStream inputStream;

    /** Mock reader. */
    @Mock
    private Reader reader;

    /** Mock ref. */
    @Mock
    private Ref ref;

    /** Object instance. */
    private Object object = new Object();

    @BeforeEach
    public void setUp() throws Exception
    {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        when(resultSet.getObject(0)).thenThrow(
                new SQLException("index is 1 based"));
        when(resultSet.getBigDecimal(1)).thenReturn(null);
        when(resultSet.getObject(1)).thenReturn(null);
        when(resultSet.getArray(2)).thenReturn(array);
        when(resultSet.getObject(2)).thenReturn(array);
        when(resultSet.getBigDecimal(3)).thenReturn(new BigDecimal("123.456"));
        when(resultSet.getObject(3)).thenReturn(new BigDecimal("123.456"));
        when(resultSet.getBinaryStream(4)).thenReturn(inputStream);
        when(resultSet.getObject(4)).thenReturn(inputStream);
        when(resultSet.getBlob(5)).thenReturn(blob);
        when(resultSet.getBoolean(6)).thenReturn(true);
        when(resultSet.getByte(7)).thenReturn(Byte.valueOf("7"));
        when(resultSet.getBytes(8)).thenReturn(new byte[] {8});
        when(resultSet.getCharacterStream(9)).thenReturn(reader);
        when(resultSet.getClob(10)).thenReturn(clob);
        when(resultSet.getDate(11)).thenReturn(new Date(11L));
        when(resultSet.getDouble(12)).thenReturn(12d);
        when(resultSet.getFloat(13)).thenReturn(13f);
        when(resultSet.getInt(14)).thenReturn(14);
        when(resultSet.getLong(15)).thenReturn(15L);
        when(resultSet.getObject(16)).thenReturn(object);
        when(resultSet.getRef(17)).thenReturn(ref);
        when(resultSet.getShort(18)).thenReturn(Short.valueOf("18"));
        when(resultSet.getString(19)).thenReturn("abc");
        when(resultSet.getTime(20)).thenReturn(new Time(20L));
        when(resultSet.getTimestamp(21)).thenReturn(new Timestamp(21L));
        when(resultSet.getURL(22)).thenReturn(new URL("http://22"));
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSetMetaData.getColumnCount()).thenReturn(3);
    }

    /**
     * Tests the processRow method for a mapper where the length is determined
     * from the Metadata.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testProcessRowLengthDeterminedFromMetadata() throws Exception
    {
        // prepare
        ObjectListMapper mapper = new ObjectListMapper();

        // execute
        List<Object> result = mapper.processRow(resultSet, 0, new Criteria());

        // verify
        assertEquals(3, result.size());
        assertEquals(null, result.get(0));
        assertEquals(array, result.get(1));
        assertEquals(new BigDecimal("123.456"), result.get(2));
        verify(resultSet).getMetaData();
        verify(resultSetMetaData).getColumnCount();
        verify(resultSet).getObject(1);
        verify(resultSet).getObject(2);
        verify(resultSet).getObject(3);
        verifyNoMoreInteractions(resultSet, resultSetMetaData);
    }

    /**
     * Tests the processRow method for a mapper with offset
     * where the length is determined from the Metadata.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testProcessRowLengthDeterminedFromMetadataWithOffset()
            throws Exception
    {
        // prepare
        ObjectListMapper mapper = new ObjectListMapper();

        // execute
        List<Object> result = mapper.processRow(resultSet, 1, new Criteria());

        // verify
        assertEquals(2, result.size());
        assertEquals(array, result.get(0));
        assertEquals(new BigDecimal("123.456"), result.get(1));
        verify(resultSet).getMetaData();
        verify(resultSetMetaData).getColumnCount();
        verify(resultSet).getObject(2);
        verify(resultSet).getObject(3);
        verifyNoMoreInteractions(resultSet, resultSetMetaData);
    }

    /**
     * Tests the processRow method for a mapper with a fixed column count.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testProcessRowLenghtAsArgument() throws Exception
    {
        // prepare
        ObjectListMapper mapper = new ObjectListMapper(2);

        // execute
        List<Object> result = mapper.processRow(resultSet, 1, new Criteria());

        // verify
        assertEquals(2, result.size());
        assertEquals(array, result.get(0));
        assertEquals(new BigDecimal("123.456"), result.get(1));
        verify(resultSet).getObject(2);
        verify(resultSet).getObject(3);
        verifyNoMoreInteractions(resultSet, resultSetMetaData);
    }

    /**
     * Tests the processRow method for a mapper with a fixed column count.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testProcessRowClassListAsArgument() throws Exception
    {
        // prepare
        List<Class<?>> classList = new ArrayList<>();
        classList.add(BigDecimal.class);
        classList.add(Array.class);
        classList.add(BigDecimal.class);
        classList.add(InputStream.class);
        classList.add(Blob.class);
        classList.add(Boolean.class);
        classList.add(Byte.class);
        classList.add(byte[].class);
        classList.add(Reader.class);
        classList.add(Clob.class);
        classList.add(Date.class);
        classList.add(Double.class);
        classList.add(Float.class);
        classList.add(Integer.class);
        classList.add(Long.class);
        classList.add(Object.class);
        classList.add(Ref.class);
        classList.add(Short.class);
        classList.add(String.class);
        classList.add(Time.class);
        classList.add(Timestamp.class);
        classList.add(URL.class);
        ObjectListMapper mapper = new ObjectListMapper(classList);

        // execute
        List<Object> result = mapper.processRow(resultSet, 0, null);

        // verify
        assertEquals(22, result.size());
        assertEquals(null, result.get(0));
        assertEquals(array, result.get(1));
        assertEquals(new BigDecimal("123.456"), result.get(2));
        assertEquals(inputStream, result.get(3));
        assertEquals(blob, result.get(4));
        assertEquals(true, result.get(5));
        assertEquals(Byte.valueOf("7"), result.get(6));
        assertTrue(Arrays.equals(new byte[] {8}, (byte[]) result.get(7)));
        assertEquals(reader, result.get(8));
        assertEquals(clob, result.get(9));
        assertEquals(new Date(11L), result.get(10));
        assertEquals(12d, result.get(11));
        assertEquals(13f, result.get(12));
        assertEquals(14, result.get(13));
        assertEquals(15L, result.get(14));
        assertEquals(object, result.get(15));
        assertEquals(ref, result.get(16));
        assertEquals(Short.valueOf("18"), result.get(17));
        assertEquals("abc", result.get(18));
        assertEquals(new Time(20L), result.get(19));
        assertEquals(new Timestamp(21L), result.get(20));
        assertEquals(new URL("http://22"), result.get(21));


        verify(resultSet).getBigDecimal(1);
        verify(resultSet).getArray(2);
        verify(resultSet).getBigDecimal(3);
        verify(resultSet).getBinaryStream(4);
        verify(resultSet).getBlob(5);
        verify(resultSet).getBoolean(6);
        verify(resultSet).getByte(7);
        verify(resultSet).getBytes(8);
        verify(resultSet).getCharacterStream(9);
        verify(resultSet).getClob(10);
        verify(resultSet).getDate(11);
        verify(resultSet).getDouble(12);
        verify(resultSet).getFloat(13);
        verify(resultSet).getInt(14);
        verify(resultSet).getLong(15);
        verify(resultSet).getObject(16);
        verify(resultSet).getRef(17);
        verify(resultSet).getShort(18);
        verify(resultSet).getString(19);
        verify(resultSet).getTime(20);
        verify(resultSet).getTimestamp(21);
        verify(resultSet).getURL(22);
        verifyNoMoreInteractions(resultSet, resultSetMetaData);
    }

    /**
     * Tests the processRow method if the resultSet throws a SqlException.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testProcessRowSqlException() throws Exception
    {
        // prepare
        ObjectListMapper mapper = new ObjectListMapper(1);

        // execute
        try
        {
            mapper.processRow(resultSet, -1, new Criteria());
            // verify 1
            fail("Exception expected");
        }
        catch (TorqueException e)
        {
            // verify 2
            assertEquals("java.sql.SQLException: index is 1 based", e.getMessage());
            assertEquals(SQLException.class, e.getCause().getClass());
            assertEquals("index is 1 based", e.getCause().getMessage());
            verify(resultSet).getObject(0);
            verifyNoMoreInteractions(resultSet);
        }
    }

    /**
     * Tests the processRow method if the resultSet throws a SqlException.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testUnknownClass() throws Exception
    {
        // prepare
        List<Class<?>> classList = new ArrayList<>();
        classList.add(RecordMapper.class);
        ObjectListMapper mapper = new ObjectListMapper(classList);

        // execute
        try
        {
            mapper.processRow(resultSet, -1, new Criteria());
            // verify 1
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            // verify 2
            assertEquals("Unknown convertClass "
                    + "org.apache.torque.om.mapper.RecordMapper at position 0",
                    e.getMessage());
            verifyNoMoreInteractions(resultSet);
        }
    }

    /**
     * Tests the byte null for empty result
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testByteNull() throws Exception
    {
        // prepare
        byte zero = 0;
        when(resultSet.getByte(1)).thenReturn(zero);
        when(resultSet.wasNull()).thenReturn(true);
        List<Class<?>> classList = new ArrayList<>();
        classList.add(Byte.class);
        ObjectListMapper mapper = new ObjectListMapper(classList);

        // execute
        List<Object> result = mapper.processRow(resultSet, 0, new Criteria());

        // verify
        assertEquals(1, result.size());
        assertEquals(null, result.get(0));
        verify(resultSet).getByte(1);
        verify(resultSet).wasNull();
        verifyNoMoreInteractions(resultSet);
    }

    /**
     * Tests the short null for empty result
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testShortNull() throws Exception
    {
        // prepare
        short zero = 0;
        when(resultSet.getShort(1)).thenReturn(zero);
        when(resultSet.wasNull()).thenReturn(true);
        List<Class<?>> classList = new ArrayList<>();
        classList.add(Short.class);
        ObjectListMapper mapper = new ObjectListMapper(classList);

        // execute
        List<Object> result = mapper.processRow(resultSet, 0, new Criteria());

        // verify
        assertEquals(1, result.size());
        assertEquals(null, result.get(0));
        verify(resultSet).getShort(1);
        verify(resultSet).wasNull();
        verifyNoMoreInteractions(resultSet);
    }

    /**
     * Tests the integer null for empty result
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testIntegerNull() throws Exception
    {
        // prepare
        when(resultSet.getInt(1)).thenReturn(0);
        when(resultSet.wasNull()).thenReturn(true);
        List<Class<?>> classList = new ArrayList<>();
        classList.add(Integer.class);
        ObjectListMapper mapper = new ObjectListMapper(classList);

        // execute
        List<Object> result = mapper.processRow(resultSet, 0, new Criteria());

        // verify
        assertEquals(1, result.size());
        assertEquals(null, result.get(0));
        verify(resultSet).getInt(1);
        verify(resultSet).wasNull();
        verifyNoMoreInteractions(resultSet);
    }

    /**
     * Tests the long null for empty result
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testLongNull() throws Exception
    {
        // prepare
        when(resultSet.getLong(1)).thenReturn(0L);
        when(resultSet.wasNull()).thenReturn(true);
        List<Class<?>> classList = new ArrayList<>();
        classList.add(Long.class);
        ObjectListMapper mapper = new ObjectListMapper(classList);

        // execute
        List<Object> result = mapper.processRow(resultSet, 0, new Criteria());

        // verify
        assertEquals(1, result.size());
        assertEquals(null, result.get(0));
        verify(resultSet).getLong(1);
        verify(resultSet).wasNull();
        verifyNoMoreInteractions(resultSet);
    }

    /**
     * Tests the double null for empty result
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoubleNull() throws Exception
    {
        // prepare
        when(resultSet.getDouble(1)).thenReturn(0d);
        when(resultSet.wasNull()).thenReturn(true);
        List<Class<?>> classList = new ArrayList<>();
        classList.add(Double.class);
        ObjectListMapper mapper = new ObjectListMapper(classList);

        // execute
        List<Object> result = mapper.processRow(resultSet, 0, new Criteria());

        // verify
        assertEquals(1, result.size());
        assertEquals(null, result.get(0));
        verify(resultSet).getDouble(1);
        verify(resultSet).wasNull();
        verifyNoMoreInteractions(resultSet);
    }

    /**
     * Tests the float null for empty result
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testFloatNull() throws Exception
    {
        // prepare
        when(resultSet.getFloat(1)).thenReturn(0f);
        when(resultSet.wasNull()).thenReturn(true);
        List<Class<?>> classList = new ArrayList<>();
        classList.add(Float.class);
        ObjectListMapper mapper = new ObjectListMapper(classList);

        // execute
        List<Object> result = mapper.processRow(resultSet, 0, new Criteria());

        // verify
        assertEquals(1, result.size());
        assertEquals(null, result.get(0));
        verify(resultSet).getFloat(1);
        verify(resultSet).wasNull();
        verifyNoMoreInteractions(resultSet);
    }
}
