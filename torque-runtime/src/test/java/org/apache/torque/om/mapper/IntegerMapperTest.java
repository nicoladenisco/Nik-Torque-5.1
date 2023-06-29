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

public class IntegerMapperTest extends BaseTestCase
{
    /** Mock result set. */
    @Mock
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() throws Exception
    {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        when(resultSet.getInt(1)).thenReturn(0);
        when(resultSet.getInt(2)).thenReturn(-273343444);
        when(resultSet.getInt(3)).thenReturn(353344556);
        when(resultSet.getInt(0)).thenThrow(
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
        IntegerMapper mapper = new IntegerMapper();

        // execute
        Integer result1 = mapper.processRow(resultSet, 0, new Criteria());
        Integer result2 = mapper.processRow(resultSet, 1, null);

        // verify
        assertEquals(null, result1);
        assertEquals(Integer.valueOf(-273343444), result2);
        // verify mock (verification order not relevant)
        verify(resultSet).getInt(1);
        verify(resultSet).getInt(2);
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
        IntegerMapper mapper = new IntegerMapper(1);

        // execute
        Integer result1 = mapper.processRow(resultSet, 0, null);
        Integer result2 = mapper.processRow(resultSet, 1, new Criteria());

        // verify
        assertEquals(Integer.valueOf(-273343444), result1);
        assertEquals(Integer.valueOf(353344556), result2);
        verify(resultSet).getInt(2);
        verify(resultSet).getInt(3);
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
        IntegerMapper mapper = new IntegerMapper();

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
                    "Result could not be mapped to a Integer",
                    e.getMessage());
            assertEquals(SQLException.class, e.getCause().getClass());
            assertEquals("index must be >= 1", e.getCause().getMessage());
            verify(resultSet).getInt(0);
            verifyNoMoreInteractions(resultSet);
        }
    }
}
