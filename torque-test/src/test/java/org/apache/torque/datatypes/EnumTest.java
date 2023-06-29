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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.TestEnum;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.DerbyAdapter;
import org.apache.torque.adapter.HsqldbAdapter;
import org.apache.torque.adapter.MssqlAdapter;
import org.apache.torque.adapter.MysqlAdapter;
import org.apache.torque.adapter.OracleAdapter;
import org.apache.torque.adapter.PostgresAdapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.test.dbobject.EnumTable;
import org.apache.torque.test.dbobject.IntPrimitiveColumnEnum;
import org.apache.torque.test.dbobject.MyIntColumnEnum;
import org.apache.torque.test.dbobject.VarcharColumnEnum;
import org.apache.torque.test.peer.EnumTablePeer;
import org.apache.torque.util.ColumnValues;
import org.apache.torque.util.JdbcTypedValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;


/**
 * Tests the enum data types.
 * @version $Id: BooleanIntCharTest.java 1439295 2013-01-28 08:21:41Z tfischer $
 */
public class EnumTest extends BaseDatabaseTestCase
{
    private static Log logger = LogFactory.getLog(EnumTest.class);

    private List<EnumTable> databaseStateBeforeTest;

    @BeforeEach
    public void setUp() throws Exception
    {
        fillTables();
    }

    /**
     * Checks whether we can read enum values.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testRead() throws Exception
    {
        // prepare
        fillTables();

        // execute
        EnumTable enumTable = EnumTablePeer.retrieveByPK(databaseStateBeforeTest.get(0).getId());
        // verify
        assertEquals(databaseStateBeforeTest.get(0).getId(), enumTable.getId());
        assertEquals(MyIntColumnEnum._1, enumTable.getMyIntColumnEnum());
        assertEquals(IntPrimitiveColumnEnum._1, enumTable.getIntPrimitiveColumnEnum());
        assertEquals(TestEnum.A, enumTable.getTestEnum());
        assertEquals(VarcharColumnEnum.X, enumTable.getVarcharColumnEnum());

        // execute
        enumTable = EnumTablePeer.retrieveByPK(databaseStateBeforeTest.get(1).getId());
        // verify
        assertEquals(databaseStateBeforeTest.get(1).getId(), enumTable.getId());
        assertEquals(MyIntColumnEnum.TWO, enumTable.getMyIntColumnEnum());
        assertEquals(IntPrimitiveColumnEnum._2, enumTable.getIntPrimitiveColumnEnum());
        assertEquals(TestEnum.B, enumTable.getTestEnum());
        assertEquals(VarcharColumnEnum.Y, enumTable.getVarcharColumnEnum());
    }

    /**
     * Checks whether we can update enum values.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testUpdate() throws Exception
    {
        // prepare
        EnumTable enumTable = EnumTablePeer.retrieveByPK(databaseStateBeforeTest.get(0).getId());
        assertEquals(databaseStateBeforeTest.get(0).getId(), enumTable.getId());
        assertEquals(MyIntColumnEnum._1, enumTable.getMyIntColumnEnum());
        assertEquals(IntPrimitiveColumnEnum._1, enumTable.getIntPrimitiveColumnEnum());
        assertEquals(TestEnum.A, enumTable.getTestEnum());
        assertEquals(VarcharColumnEnum.X, enumTable.getVarcharColumnEnum());

        // execute
        enumTable.setMyIntColumnEnum(MyIntColumnEnum._3);
        enumTable.setIntPrimitiveColumnEnum(IntPrimitiveColumnEnum._3);
        enumTable.setTestEnum(TestEnum.C);
        enumTable.setVarcharColumnEnum(VarcharColumnEnum.ZZZ);
        enumTable.save();

        // verify
        enumTable = EnumTablePeer.retrieveByPK(databaseStateBeforeTest.get(0).getId());
        assertEquals(databaseStateBeforeTest.get(0).getId(), enumTable.getId());
        assertEquals(MyIntColumnEnum._3, enumTable.getMyIntColumnEnum());
        assertEquals(IntPrimitiveColumnEnum._3, enumTable.getIntPrimitiveColumnEnum());
        assertEquals(TestEnum.C, enumTable.getTestEnum());
        assertEquals(VarcharColumnEnum.ZZZ, enumTable.getVarcharColumnEnum());
    }

    /**
     * Checks whether we can select enum values by enum value and by wrapped value.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSelect() throws Exception
    {
        // prepare
        Criteria criteria = new Criteria()
                .where(EnumTablePeer.INT_COLUMN, MyIntColumnEnum._1.getValue())
                .and(EnumTablePeer.INT_PRIMITIVE_COLUMN, IntPrimitiveColumnEnum._1.getValue())
                .and(EnumTablePeer.PREDEFINED_ENUM_COLUMN, TestEnum.A.getValue())
                .and(EnumTablePeer.VARCHAR_COLUMN, VarcharColumnEnum.X.getValue());
        // execute
        EnumTable enumTable = EnumTablePeer.doSelectSingleRecord(criteria);
        // verify
        assertEquals(databaseStateBeforeTest.get(0).getId(), enumTable.getId());

        // prepare
        criteria = new Criteria()
                .where(EnumTablePeer.INT_COLUMN, MyIntColumnEnum._1)
                .and(EnumTablePeer.INT_PRIMITIVE_COLUMN, IntPrimitiveColumnEnum._1)
                .and(EnumTablePeer.PREDEFINED_ENUM_COLUMN, TestEnum.A)
                .and(EnumTablePeer.VARCHAR_COLUMN, VarcharColumnEnum.X);
        // execute
        enumTable = EnumTablePeer.doSelectSingleRecord(criteria);
        // verify
        assertEquals(databaseStateBeforeTest.get(0).getId(), enumTable.getId());
    }

    /**
     * Checks whether we can select enum values by enum value and by wrapped value.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testInsertIllegalValue(Adapter adapter) throws Exception
    {
        // check that the normal doUpdate works
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                EnumTablePeer.VARCHAR_COLUMN,
                new JdbcTypedValue(
                        VarcharColumnEnum.X.getValue(),
                        12));
        EnumTablePeer.doUpdate(
                new Criteria().where(EnumTablePeer.ID, databaseStateBeforeTest.get(0).getId()),
                columnValues);

        columnValues = new ColumnValues();
        columnValues.put(
                EnumTablePeer.VARCHAR_COLUMN,
                new JdbcTypedValue(
                        "A",
                        12));
        try
        {
            EnumTablePeer.doUpdate(
                    new Criteria().where(EnumTablePeer.ID, databaseStateBeforeTest.get(0).getId()),
                    columnValues);
            if (!(adapter instanceof MysqlAdapter))
            {
                fail("Exception expected");
            }
        }
        catch (TorqueException e)
        {
            // assert that the error is really a check violation
            // check different error codes which are employed by each database vendor.
            SQLException cause = (SQLException) e.getCause();
            if (adapter instanceof MssqlAdapter)
            {
                assertEquals(547, cause.getErrorCode());
                assertEquals("23000", cause.getSQLState());
            }
            else if (adapter instanceof OracleAdapter)
            {
                assertEquals(2290, cause.getErrorCode());
                assertEquals("23000", cause.getSQLState());
            }
            else if (adapter instanceof PostgresAdapter)
            {
                assertEquals("23514", cause.getSQLState());
            }
            else if (adapter instanceof DerbyAdapter)
            {
                assertEquals("23513", cause.getSQLState());
            }
            else if (adapter instanceof HsqldbAdapter)
            {
                assertEquals("23513", cause.getSQLState());
            }
            else
            {
                logger.warn("error code and SQL code not checked for unknown adapter " + adapter.getClass());
            }

        }
    }


    /**
     * Delete all previous data from the tested tables
     * and re-inserts test data.
     */
    private void fillTables() throws TorqueException
    {
        Criteria criteria = new Criteria();
        EnumTablePeer.doDelete(criteria);

        databaseStateBeforeTest = new ArrayList<>();
        EnumTable enumTable = new EnumTable();
        enumTable.setMyIntColumnEnum(MyIntColumnEnum._1);
        enumTable.setIntPrimitiveColumnEnum(IntPrimitiveColumnEnum._1);
        enumTable.setTestEnum(TestEnum.A);
        enumTable.setVarcharColumnEnum(VarcharColumnEnum.X);
        enumTable.save();
        databaseStateBeforeTest.add(enumTable);

        enumTable = new EnumTable();
        enumTable.setMyIntColumnEnum(MyIntColumnEnum.TWO);
        enumTable.setIntPrimitiveColumnEnum(IntPrimitiveColumnEnum._2);
        enumTable.setTestEnum(TestEnum.B);
        enumTable.setVarcharColumnEnum(VarcharColumnEnum.Y);
        enumTable.save();
        databaseStateBeforeTest.add(enumTable);
    }
}
