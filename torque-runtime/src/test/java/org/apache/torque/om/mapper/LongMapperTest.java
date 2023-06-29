package org.apache.torque.om.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.torque.BaseTestCase;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LongMapperTest extends BaseTestCase
{
    /** Mock result set. */
    @Mock
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() throws Exception
    {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        when(resultSet.getLong(1)).thenReturn(0L);
        when(resultSet.getLong(2)).thenReturn(-2733434442345777L);
        when(resultSet.getLong(3)).thenReturn(35334455623667566L);
        when(resultSet.getLong(0)).thenThrow(
                new SQLException("index must be >= 1"));
        when(resultSet.wasNull()).thenReturn(true);
    }

    /**
     * Tests the processRow method for a mapper without internal Offset.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testProcessRow() throws Exception
    {
        // prepare
        LongMapper mapper = new LongMapper();

        // execute
        Long result1 = mapper.processRow(resultSet, 0, new Criteria());
        Long result2 = mapper.processRow(resultSet, 1, null);

        // verify
        assertEquals(null, result1);
        assertEquals(Long.valueOf(-2733434442345777L), result2);
        // verify mock (verification order not relevant)
        verify(resultSet).getLong(1);
        verify(resultSet).getLong(2);
        verify(resultSet).wasNull();
        verifyNoMoreInteractions(resultSet);
    }

    /**
     * Tests the processRow method for a mapper with internal Offset.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testProcessRowInternalOffset() throws Exception
    {
        // prepare
        LongMapper mapper = new LongMapper(1);

        // execute
        Long result1 = mapper.processRow(resultSet, 0, null);
        Long result2 = mapper.processRow(resultSet, 1, new Criteria());

        // verify
        assertEquals(Long.valueOf(-2733434442345777L), result1);
        assertEquals(Long.valueOf(35334455623667566L), result2);
        verify(resultSet).getLong(2);
        verify(resultSet).getLong(3);
        verifyNoMoreInteractions(resultSet);
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
        LongMapper mapper = new LongMapper();

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
            assertEquals(
                    "Result could not be mapped to a Long",
                    e.getMessage());
            assertEquals(SQLException.class, e.getCause().getClass());
            assertEquals("index must be >= 1", e.getCause().getMessage());
            verify(resultSet).getLong(0);
            verifyNoMoreInteractions(resultSet);
        }
    }
}
