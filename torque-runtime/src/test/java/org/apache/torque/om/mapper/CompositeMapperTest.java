package org.apache.torque.om.mapper;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.List;

import org.apache.torque.BaseTestCase;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CompositeMapperTest extends BaseTestCase
{
    /** System under test. */
    CompositeMapper mapper;

    /** Mock result set. */
    @Mock
    private ResultSet resultSet;

    /** Mock delegate RecordMapper. */
    @Mock
    private RecordMapper<?> recordMapper1;

    /** Mock delegate RecordMapper. */
    @Mock
    private RecordMapper<Integer> recordMapper2;

    /** Mock delegate RecordMapper. */
    @Mock
    private RecordMapper<Long> recordMapper3;

    /** The fake criteria which produced the query .*/
    private Criteria criteria = new Criteria();

    @Override
    @BeforeEach
    public void setUp() throws Exception
    {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        when(recordMapper1.processRow(resultSet,3, criteria))
        .thenReturn(null);
        when(recordMapper2.processRow(resultSet, 4, criteria))
        .thenReturn(Integer.valueOf(1));
        when(recordMapper3.processRow(resultSet, 5, criteria))
        .thenReturn(new Long(2));

        mapper = new CompositeMapper();
        mapper.addMapper(recordMapper1, 1);
        mapper.addMapper(recordMapper2, 2);
        mapper.addMapper(recordMapper3, 3);
    }

    /**
     * Tests the processRow method for a mapper without internal Offset.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testProcessRow() throws Exception
    {
        // execute
        List<Object> result = mapper.processRow(resultSet, 2, criteria);

        // verify
        assertEquals(3, result.size());
        assertEquals(null, result.get(0));
        assertEquals(Integer.valueOf(1), result.get(1));
        assertEquals(new Long(2), result.get(2));
        verify(recordMapper1).processRow(resultSet, 3, criteria);
        verify(recordMapper2).processRow(resultSet, 4, criteria);
        verify(recordMapper3).processRow(resultSet, 5, criteria);
        verifyNoMoreInteractions(resultSet);
    }

    /**
     * Tests the processRow method if a delegate throws a SqlException.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testProcessRowSqlException() throws Exception
    {
        // prepare
        when(recordMapper1.processRow(resultSet, 1, criteria))
        .thenThrow(new TorqueException("thrown by mock"));

        // execute
        try
        {
            mapper.processRow(resultSet, 0, criteria);
            // verify 1
            fail("Exception expected");
        }
        catch (TorqueException e)
        {
            // verify 2
            assertEquals("thrown by mock", e.getMessage());
            verify(recordMapper1).processRow(resultSet, 1, criteria);
            verifyNoMoreInteractions(resultSet);
        }
    }
}
