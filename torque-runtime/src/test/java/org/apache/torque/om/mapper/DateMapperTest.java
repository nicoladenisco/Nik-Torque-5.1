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
import java.util.Date;

import org.apache.torque.BaseTestCase;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DateMapperTest extends BaseTestCase
{
    /** Mock result set. */
    @Mock
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() throws Exception
    {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        when(resultSet.getTimestamp(1)).thenReturn(null);
        when(resultSet.getTimestamp(2)).thenReturn(
                new java.sql.Timestamp(0));
        when(resultSet.getTimestamp(3)).thenReturn(
                new java.sql.Timestamp(123456));
        when(resultSet.getTimestamp(0)).thenThrow(
                new SQLException("index must be >= 1"));
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
        DateMapper mapper = new DateMapper();

        // execute
        Date result1 = mapper.processRow(resultSet, 0, null);
        Date result2 = mapper.processRow(resultSet, 1, null);

        // verify
        assertEquals(null, result1);
        assertEquals(new Date(0), result2);
        verify(resultSet).getTimestamp(1);
        verify(resultSet).getTimestamp(2);
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
        DateMapper mapper = new DateMapper(1);

        // execute
        Date result1 = mapper.processRow(resultSet, 0, new Criteria());
        Date result2 = mapper.processRow(resultSet, 1, null);

        // verify
        assertEquals(new Date(0), result1);
        assertEquals(new Date(123456L), result2);
        verify(resultSet).getTimestamp(2);
        verify(resultSet).getTimestamp(3);
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
        DateMapper mapper = new DateMapper();

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
                    "Result could not be mapped to a Date",
                    e.getMessage());
            assertEquals(SQLException.class, e.getCause().getClass());
            assertEquals("index must be >= 1", e.getCause().getMessage());
            verify(resultSet).getTimestamp(0);
            verifyNoMoreInteractions(resultSet);
        }
    }
}
