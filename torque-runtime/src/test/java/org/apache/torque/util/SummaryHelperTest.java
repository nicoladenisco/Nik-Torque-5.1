package org.apache.torque.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import org.apache.torque.BaseTestCase;
import org.apache.torque.Torque;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.TableMap;
import org.apache.torque.util.functions.Count;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test class for SummaryHelper.
 *
 * @version $Id: CriteriaTest.java 1448414 2013-02-20 21:06:35Z tfischer $
 */
public class SummaryHelperTest extends BaseTestCase
{
    /** System under Test. */
    private SummaryHelper summary;

    /** The criteria to use in the test. */
    private Criteria criteria;

    private TableMap tableMap;

    private ColumnMap columnMap1;

    private ColumnMap columnMap2;

    private ColumnMap columnMap3;

    private ColumnMap columnMap4;

    /** Mock db connection. */
    @Mock
    private Connection connection;

    /** Mock prepared statement. */
    @Mock
    private PreparedStatement preparedStatement;

    /** Mock result set. */
    @Mock
    private ResultSet resultSet;

    /** Mock result set metadata. */
    @Mock
    private ResultSetMetaData resultSetMetaData;

    /**
     * Sets up the helper objects and the test.
     */
    @BeforeEach
    public void setUp() throws Exception
    {
        super.setUp();
        summary = new SummaryHelper();
        criteria = new Criteria();

        // set up database map
        DatabaseMap databaseMap = Torque.getDatabaseMap("postgresql");
        tableMap = databaseMap.addTable("TABLE");
        {
            columnMap1 = new ColumnMap("COLUMN1", tableMap);
            columnMap1.setType("");
            columnMap1.setJavaType("String");
            tableMap.addColumn(columnMap1);
        }
        {
            columnMap2 = new ColumnMap("COLUMN2", tableMap);
            columnMap2.setType("");
            columnMap2.setJavaType("String");
            tableMap.addColumn(columnMap2);
        }
        {
            columnMap3 = new ColumnMap("COLUMN3", tableMap);
            columnMap3.setType("");
            columnMap3.setJavaType("String");
            tableMap.addColumn(columnMap3);
        }
        {
            columnMap4 = new ColumnMap("COLUMN4", tableMap);
            columnMap4.setType(Integer.valueOf(0));
            columnMap4.setJavaType("Integer");
            tableMap.addColumn(columnMap4);
        }

        // set up mocks
        MockitoAnnotations.initMocks(this);
        when(connection.prepareStatement((String) any()))
        .thenReturn(preparedStatement);
        when(connection.createStatement()).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.executeQuery((String) any())).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);

    }

    /**
     * Test basic where condition on a string.
     */
    @Test
    public void testWhereString() throws Exception
    {
        // prepare
        criteria.where(columnMap1, "abc");
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getObject(1)).thenReturn(23);
        when(resultSetMetaData.getColumnCount()).thenReturn(1);


        // execute
        summary.addAggregate("count", new Count(columnMap1));
        List<ListOrderedMapCI<Object>> results = summary.summarize(criteria, connection);

        // Verify result
        assertEquals(1, results.size());
        assertEquals(23, results.get(0).get("count"));

        // verify mock (verification order not relevant)
        verify(connection).prepareStatement(
                "SELECT COUNT(TABLE.COLUMN1) AS count "
                        + "FROM TABLE WHERE TABLE.COLUMN1=?");
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(1, "abc");
        verify(preparedStatement).close();
        verify(resultSet, times(2)).next();
        verify(resultSet).getMetaData();
        verify(resultSet).getObject(1);
        verify(resultSet).close();
        verify(resultSetMetaData).getColumnCount();
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, resultSetMetaData);
    }

}
