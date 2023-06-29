package org.apache.torque.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.BaseTestCase;
import org.apache.torque.Column;
import org.apache.torque.ColumnImpl;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.IDMethod;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.TableMap;
import org.apache.torque.oid.IdGenerator;
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.mapper.CompositeMapper;
import org.apache.torque.om.mapper.IntegerMapper;
import org.apache.torque.om.mapper.ObjectListMapper;
import org.apache.torque.om.mapper.StringMapper;
import org.apache.torque.util.functions.Count;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Tests the class BasePeerImpl.
 *
 * @version $Id: BasePeerImplTest.java 1850726 2019-01-08 10:56:07Z gk $
 */
public class BasePeerImplTest extends BaseTestCase
{
    private static final BigDecimal DEFAULT_GENERATED_ID = new BigDecimal(56);

    /** System under test. */
    private BasePeerImpl<List<Object>> basePeerImpl;

    /** Mock db connection. */
    @Mock
    private TorqueConnection connection;

    /** Mock prepared statement. */
    @Mock
    private PreparedStatement preparedStatement;

    /** Mock result set. */
    @Mock
    private ResultSet resultSet;

    @Mock
    private TransactionManager transactionManager;

    private TransactionManager oldTransactionManager;

    @Mock
    private IdGenerator idGenerator;

    @BeforeEach
    public void setUp() throws Exception
    {
        basePeerImpl = new BasePeerImpl<>();
        super.setUp();
        basePeerImpl.setTableMap(tableMap);
        basePeerImpl.setDatabaseName("databaseName");
        basePeerImpl.setRecordMapper(new ObjectListMapper());
        oldTransactionManager = Transaction.getTransactionManager();
        MockitoAnnotations.initMocks(this);
        when(connection.prepareStatement((String) any()))
        .thenReturn(preparedStatement);
        when(connection.createStatement()).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.executeQuery((String) any())).thenReturn(resultSet);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        when(transactionManager.begin(databaseMap.getName())).thenReturn(connection);
        when(transactionManager.begin(basePeerImpl.getDatabaseName())).thenReturn(connection);
        when(transactionManager.begin(null)).thenReturn(connection);
        when(idGenerator.getIdAsBigDecimal(connection, null))
        .thenReturn(DEFAULT_GENERATED_ID);
        when(idGenerator.isPriorToInsert()).thenReturn(true);
        when(idGenerator.isPostInsert()).thenReturn(false);
        when(idGenerator.isGetGeneratedKeysSupported()).thenReturn(false);
        Transaction.setTransactionManager(transactionManager);
    }

    @AfterEach
    public void tearDown()
    {
        Transaction.setTransactionManager(oldTransactionManager);
    }

    /**
     * Check that a basic doSelect works.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testDoSelect() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(stringColumnMap);
        criteria.addSelectColumn(integerColumnMap);
        criteria.setOffset(2);
        criteria.setLimit(1);
        // single result "fd", 23 found
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString(1)).thenReturn("fd");
        when(resultSet.getInt(2)).thenReturn(23);

        CompositeMapper mapper = new CompositeMapper();
        mapper.addMapper(new StringMapper(), 0);
        mapper.addMapper(new IntegerMapper(), 1);

        List<List<Object>> result = basePeerImpl.doSelect(
                criteria, mapper);

        // verify no additional select columns are added
        UniqueColumnList selectColumns = criteria.getSelectColumns();
        assertEquals(2, selectColumns.size());
        assertSame(stringColumnMap, selectColumns.get(0));
        assertSame(integerColumnMap, selectColumns.get(1));


        // verify mock (verification order not relevant)
        verify(connection).prepareStatement(
                "SELECT TABLE.COLUMN1, TABLE.COLUMN4 FROM TABLE LIMIT 1 OFFSET 2");
        verify(connection).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).next();
        verify(resultSet).getString(1);
        verify(resultSet).getInt(2);
        verify(resultSet).close();
        verify(transactionManager).begin(null);
        verify(transactionManager).commit(connection);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, transactionManager);
        // verify result
        assertEquals(1, result.size()); // one row
        assertEquals(2, result.get(0).size()); // two columns
        assertEquals("fd", result.get(0).get(0));
        assertEquals(23, result.get(0).get(1));
    }

    /**
     * Check that a doSelect with a query.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testDoSelectWithQuery() throws Exception
    {
        // single result "fd", 23 found
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString(1)).thenReturn("fd");
        when(resultSet.getInt(2)).thenReturn(23);

        CompositeMapper mapper = new CompositeMapper();
        mapper.addMapper(new StringMapper(), 0);
        mapper.addMapper(new IntegerMapper(), 1);
        basePeerImpl.setRecordMapper(mapper);

        List<List<Object>> result = basePeerImpl.doSelect(
                "SELECT * from TABLE");

        // verify mock (verification order not relevant)
        verify(connection).createStatement();
        verify(connection).close();
        verify(preparedStatement).executeQuery("SELECT * from TABLE");
        verify(preparedStatement).close();
        verify(resultSet, times(2)).next();
        verify(resultSet).getString(1);
        verify(resultSet).getInt(2);
        verify(resultSet).close();
        verify(transactionManager).begin("databaseName");
        verify(transactionManager).commit(connection);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, transactionManager);
        // verify result
        assertEquals(1, result.size()); // one row
        assertEquals(2, result.get(0).size()); // two columns
        assertEquals("fd", result.get(0).get(0));
        assertEquals(23, result.get(0).get(1));
    }

    @Test
    public void testAddSelectColumns()
    {
        // prepare
        Criteria criteria = new Criteria();

        // execute
        basePeerImpl.addSelectColumns(criteria);

        // verify
        UniqueColumnList selectColumns = criteria.getSelectColumns();
        assertEquals(4, selectColumns.size());
        assertSame(stringColumnMap, selectColumns.get(0));
        assertSame(stringColumnMap2, selectColumns.get(1));
        assertSame(stringColumnMap3, selectColumns.get(2));
        assertSame(integerColumnMap, selectColumns.get(3));
    }

    /**
     * Checks that doSelect ads the select columns if no select columns were
     * already added.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testDoSelectNoSelectColumnsPresent() throws Exception
    {
        // prepare
        Criteria criteria = new Criteria();
        basePeerImpl.setDatabaseName(Torque.getDefaultDB());

        // execute
        basePeerImpl.doSelect(criteria, connection);

        // verify
        UniqueColumnList selectColumns = criteria.getSelectColumns();
        assertEquals(4, selectColumns.size());
        assertSame(stringColumnMap, selectColumns.get(0));
        assertSame(stringColumnMap2, selectColumns.get(1));
        assertSame(stringColumnMap3, selectColumns.get(2));
        assertSame(integerColumnMap, selectColumns.get(3));
    }

    /**
     * Checks that doSelect does add Select Columns if an AsColumn was already
     * added.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testAddSelectColumnsWithAsColumns() throws Exception
    {
        // prepare
        Criteria criteria = new Criteria();
        criteria.addAsColumn("asColumnName", new Count("*"));
        basePeerImpl.setDatabaseName(Torque.getDefaultDB());

        // execute
        basePeerImpl.doSelect(criteria, connection);

        // verify
        UniqueColumnList selectColumns = criteria.getSelectColumns();
        assertEquals(4, selectColumns.size());
        assertSame(stringColumnMap, selectColumns.get(0));
        assertSame(stringColumnMap2, selectColumns.get(1));
        assertSame(stringColumnMap3, selectColumns.get(2));
        assertSame(integerColumnMap, selectColumns.get(3));
    }

    /**
     * Check that the fetch size gets set on the prepared statement when
     * set in the criteria.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testDoSelectAllFetchSize() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.setFetchSize(13);
        // no results found
        when(resultSet.next()).thenReturn(false);

        basePeerImpl.doSelect(criteria, new IntegerMapper(), connection);
        verify(preparedStatement).setFetchSize(13);
    }

    @Test
    public void testDeleteWithQualifiedTableName()
            throws Exception
    {
        // prepare
        DatabaseMap fullyQualifiedDatatabaseMap
        = Torque.getDatabaseMap("fullyQualifiedDatatabaseMap");
        TableMap tableMap = new TableMap(
                "schema.fully_qualified_table",
                fullyQualifiedDatatabaseMap);
        ColumnMap columnMap = new ColumnMap("column", tableMap);
        basePeerImpl.setTableMap(tableMap);
        Criteria criteria = new Criteria();
        criteria.where(columnMap, 42);

        // execute
        int deleteCount = basePeerImpl.doDelete(criteria);

        // verify mock
        verify(connection).prepareStatement(
                "DELETE FROM schema.fully_qualified_table "
                        + "WHERE schema.fully_qualified_table.column=?");
        verify(connection).close();
        verify(preparedStatement).setInt(1, 42);
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
        verify(transactionManager).begin("databaseName");
        verify(transactionManager).commit(connection);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, transactionManager);

        // verify result
        assertEquals(1, deleteCount);
    }

    @Test
    public void testDeleteWithError()
            throws Exception
    {
        // prepare
        Criteria criteria = new Criteria();
        criteria.where(stringColumnMap, "abc");
        SQLException toThrow = new SQLException("message");
        when(preparedStatement.executeUpdate()).thenThrow(toThrow);

        // execute
        try
        {
            basePeerImpl.doDelete(criteria);
            fail("Exception expected");
        }
        catch (TorqueException e)
        {
            assertEquals("java.sql.SQLException: message", e.getMessage());
            assertSame(toThrow, e.getCause());
        }

        // verify mock
        verify(connection).prepareStatement(
                "DELETE FROM TABLE WHERE TABLE.COLUMN1=?");
        verify(connection).close();
        verify(preparedStatement).setString(1, "abc");
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
        verify(transactionManager).begin("databaseName");
        // This is tested elsewhere
        // verify(transactionManager).safeRollback(connection);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, transactionManager);
    }

    /**
     * Check that a simple doInsert works.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testDoInsertWithoutKeygen() throws Exception
    {
        // prepare
        ColumnValues insertValues = new ColumnValues(databaseMap.getName());
        insertValues.put(stringColumnMap, new JdbcTypedValue("value", Types.VARCHAR));
        insertValues.put(stringColumnMap2, new JdbcTypedValue(new ColumnImpl("someDbFunction")));
        insertValues.put(stringColumnMap3, new JdbcTypedValue(null, Types.VARCHAR));
        insertValues.put(integerColumnMap, new JdbcTypedValue(42, Types.INTEGER));

        // execute
        ObjectKey<?> result = basePeerImpl.doInsert(insertValues);

        // verify mock (verification order not relevant)
        verify(connection).prepareStatement(
                "INSERT INTO TABLE(COLUMN1,COLUMN2,COLUMN3,COLUMN4) VALUES (?,someDbFunction,?,?)");
        verify(connection).close();
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).setObject(
                1,
                "value",
                Types.VARCHAR);
        verify(preparedStatement).setNull(
                2,
                Types.VARCHAR);
        verify(preparedStatement).setObject(
                3,
                42,
                Types.INTEGER);
        verify(preparedStatement).close();
        verify(transactionManager).begin(databaseMap.getName());
        verify(transactionManager).commit(connection);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, transactionManager);
        // verify result
        // returns null because no id generator is defined
        assertEquals(null, result);
    }

    /**
     * Check that a doInsert with subselects works.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testDoInsertWithSubselect() throws Exception
    {
        // prepare
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(stringColumnMap);
        criteria.addSelectColumn(stringColumnMap2);
        criteria.addSelectColumn(stringColumnMap3);
        criteria.addSelectColumn(integerColumnMap);
        criteria.where(stringColumnMap2, stringColumnMap3);

        // execute
        int result = basePeerImpl.doInsert(
                new Column[] {
                        stringColumnMap2,
                        stringColumnMap3,
                        stringColumnMap,
                        integerColumnMap},
                criteria,
                databaseMap.getName());

        // verify mock (verification order not relevant)
        verify(connection).prepareStatement(
                "INSERT INTO TABLE(COLUMN2,COLUMN3,COLUMN1,COLUMN4) SELECT TABLE.COLUMN1, TABLE.COLUMN2, TABLE.COLUMN3, TABLE.COLUMN4 FROM TABLE WHERE TABLE.COLUMN2=TABLE.COLUMN3");
        verify(connection).close();
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
        verify(transactionManager).begin(databaseMap.getName());
        verify(transactionManager).commit(connection);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, transactionManager);
        // verify result
        assertEquals(1, result);
    }

    /**
     * Check that Exception handling for doInsert works.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testDoInsertWithException() throws Exception
    {
        // prepare
        ColumnValues insertValues = new ColumnValues(databaseMap.getName());
        insertValues.put(stringColumnMap, new JdbcTypedValue("value", Types.VARCHAR));
        insertValues.put(integerColumnMap, new JdbcTypedValue(42, Types.INTEGER));
        SQLException toThrow = new SQLException("message");
        when(preparedStatement.executeUpdate()).thenThrow(toThrow);

        // execute
        try
        {
            basePeerImpl.doInsert(insertValues);
            fail("Exception expected");
        }
        catch (TorqueException e)
        {
            assertEquals("java.sql.SQLException: message", e.getMessage());
            assertSame(toThrow, e.getCause());
        }

        // verify mock (verification order not relevant)
        verify(connection).prepareStatement(
                "INSERT INTO TABLE(COLUMN1,COLUMN4) VALUES (?,?)");
        verify(connection).close();
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).setObject(
                1,
                "value",
                Types.VARCHAR);
        verify(preparedStatement).setObject(
                2,
                42,
                Types.INTEGER);
        verify(preparedStatement).close();
        verify(transactionManager).begin(databaseMap.getName());
        // This is tested elsewhere
        // verify(transactionManager).safeRollback(connection);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, transactionManager);
    }

    /**
     * Check that a doInsert with a key generator works.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testDoInsertWithKeygen() throws Exception
    {
        // prepare
        database.addIdGenerator(IDMethod.SEQUENCE, idGenerator);
        ColumnValues insertValues = new ColumnValues(databaseMap.getName());
        insertValues.put(stringColumnMap, new JdbcTypedValue("value", Types.VARCHAR));

        // execute
        ObjectKey<?> result = basePeerImpl.doInsert(insertValues);

        // verify mock (verification order not relevant)
        verify(connection).prepareStatement(
                "INSERT INTO TABLE(COLUMN1,COLUMN4) VALUES (?,?)");
        verify(connection).close();
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).setObject(
                1,
                "value",
                Types.VARCHAR);
        verify(preparedStatement).setObject(
                2,
                DEFAULT_GENERATED_ID,
                Types.NUMERIC);
        verify(preparedStatement).close();
        verify(transactionManager).begin(databaseMap.getName());
        verify(transactionManager).commit(connection);
        verify(idGenerator).isPriorToInsert();
        verify(idGenerator).isPostInsert();
        verify(idGenerator).isGetGeneratedKeysSupported();
        verify(idGenerator).getIdAsBigDecimal(connection, null);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, transactionManager, idGenerator);
        // verify result
        assertEquals(new NumberKey(DEFAULT_GENERATED_ID), result);
    }

    /**
     * Check that a simple doUpdate works.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testDoUpdate() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.where(integerColumnMap, 37);

        ColumnValues updateValues = new ColumnValues(databaseMap.getName());
        updateValues.put(stringColumnMap, new JdbcTypedValue("value", Types.VARCHAR));
        updateValues.put(stringColumnMap2, new JdbcTypedValue(stringColumnMap3));
        updateValues.put(stringColumnMap3, new JdbcTypedValue(null, Types.VARCHAR));
        updateValues.put(integerColumnMap, new JdbcTypedValue(42, Types.INTEGER));

        int result = basePeerImpl.doUpdate(criteria, updateValues);

        // verify mock (verification order not relevant)
        verify(connection).prepareStatement(
                "UPDATE TABLE SET COLUMN1=?,"
                        + "COLUMN2=TABLE.COLUMN3,"
                        + "COLUMN3=?,"
                        + "COLUMN4=? "
                        + "WHERE TABLE.COLUMN4=?");
        verify(connection).close();
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).setObject(
                1,
                "value");
        verify(preparedStatement).setNull(
                2,
                Types.VARCHAR);
        verify(preparedStatement).setObject(
                3,
                42);
        verify(preparedStatement).setInt(
                4,
                37);
        verify(preparedStatement).close();
        verify(transactionManager).begin(databaseMap.getName());
        verify(transactionManager).commit(connection);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, transactionManager);
        // verify result
        // returns null because no id generator is defined
        assertEquals(1, result);
    }

    /**
     * Check that a simple doUpdate works.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testDoUpdateWithPk() throws Exception
    {
        ColumnValues updateValues = new ColumnValues(databaseMap.getName());
        updateValues.put(stringColumnMap, new JdbcTypedValue("value", Types.VARCHAR));
        updateValues.put(stringColumnMap2, new JdbcTypedValue(new ColumnImpl("someDbFunction")));
        updateValues.put(stringColumnMap3, new JdbcTypedValue(null, Types.VARCHAR));
        updateValues.put(integerColumnMap, new JdbcTypedValue(42, Types.INTEGER));

        int result = basePeerImpl.doUpdate(updateValues);

        // verify mock (verification order not relevant)
        verify(connection).prepareStatement(
                "UPDATE TABLE SET COLUMN1=?,"
                        + "COLUMN2=someDbFunction,"
                        + "COLUMN3=? "
                        + "WHERE TABLE.COLUMN4=?");
        verify(connection).close();
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).setObject(
                1,
                "value");
        verify(preparedStatement).setNull(
                2,
                Types.VARCHAR);
        verify(preparedStatement).setInt(
                3,
                42);
        verify(preparedStatement).close();
        verify(transactionManager).begin(databaseMap.getName());
        verify(transactionManager).commit(connection);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, transactionManager);
        // verify result
        // returns null because no id generator is defined
        assertEquals(1, result);
    }


    /**
     * Check that executeStatements with a set of named replacements works.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testExecuteStatementNamed() throws Exception
    {
        Map<String, JdbcTypedValue> replacementValues = new HashMap<>();
        replacementValues.put("key1", new JdbcTypedValue(1, 1));
        replacementValues.put("key3", new JdbcTypedValue("3", 3));

        basePeerImpl.executeStatement("SELECT * from TABLE WHERE Column1=:key1 AND COLUMN2=:notExistingKey AND COLUMN3=:key3",
                replacementValues);

        // verify mock (verification order not relevant)
        verify(connection).prepareStatement(
                "SELECT * from TABLE WHERE Column1=? AND COLUMN2=:notExistingKey AND COLUMN3=?");
        verify(connection).close();
        verify(preparedStatement).setObject(1, 1, 1);
        verify(preparedStatement).setObject(2, "3", 3);
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
        verify(transactionManager).begin("postgresql");
        verify(transactionManager).commit(connection);
        verifyNoMoreInteractions(connection, preparedStatement, transactionManager);
    }

    /**
     * Check that executeStatements with a set of named replacements works when the statement contains no replacements.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testExecuteStatementNamedNoReplacements() throws Exception
    {
        Map<String, JdbcTypedValue> replacementValues = new HashMap<>();
        replacementValues.put("key1", new JdbcTypedValue(null, 42));
        replacementValues.put("unusedKey", new JdbcTypedValue("3", 3));

        basePeerImpl.executeStatement("SELECT * from TABLE", replacementValues);

        // verify mock (verification order not relevant)
        verify(connection).prepareStatement("SELECT * from TABLE");
        verify(connection).close();
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
        verify(transactionManager).begin("postgresql");
        verify(transactionManager).commit(connection);
        verifyNoMoreInteractions(connection, preparedStatement, transactionManager);
    }
}
