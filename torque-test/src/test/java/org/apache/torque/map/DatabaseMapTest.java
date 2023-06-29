package org.apache.torque.map;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Test code for Database Map functions
 *
 * @author <a href="mailto:greg.monroe@dukece.com">Greg Monroe</a>
 * @version $Id
 */
@TestMethodOrder(OrderAnnotation.class)
public class DatabaseMapTest extends BaseDatabaseTestCase
{

    public static final String TABLE_NAME1 = "NOT_USED_BEFORE_MAP_INIT";

    public static final String DATABASE_NAME = "databaseMap";

    public static final String INVALID_DATABASE_NAME = "erotskoob";

    public static final String TABLE_NAME2 = "SECOND_MAP_INIT_TABLE";

    public static final String PROTECTED_TABLE = "PROTECTED_COLUMNS";

    public static final String[] COLUMN_NAMES =
        {
                "id", "one", "two", "three"
        };


    /**
     * Test initializing the default database. <p> Assumptions: <ul> A table
     * called NotUsedBeforeMapInit table exists in the default schema
     * (bookstore)<br> This table has not been used by any other test prior to
     * this test.<br> </ul>
     */
    @Order(1)
    @Test
    public void testDatabaseMapInitialize() throws Exception
    {
	        // Get default schema DB
	        DatabaseMap map = Torque.getDatabaseMap(DATABASE_NAME);
	        TableMap tMap = map.getTable(TABLE_NAME1);
	        assertTrue( tMap == null,
	                "Did not expect to find NotUsedBeforeMapInit table before " +
	                        "initialize!"
	                       );
	        map.initialize();
	        tMap = map.getTable(TABLE_NAME1);
	        assertTrue( tMap != null,
	                "Did not find table named NotUsedBeforeMapInit after " +
	                        "initialization!"
	                		);
    }

    /**
     * Test that XML column order is preserved in TableMap objects.
     * <p>
     * Assumptions:
     * <ul>
     * The default database is bookstore<br>
     * The book table has columns ordered as in COLUMN_NAMES property<br>
     * </ul>
     *
     * @throws TorqueException
     */
    @Test
    @Order(2)
    public void testColumnOrder() throws TorqueException
    {
        DatabaseMap map = Torque.getDatabaseMap(DATABASE_NAME);
        map.initialize();
        TableMap tMap = map.getTable(TABLE_NAME1);
        ColumnMap[] columns = tMap.getColumns();
        assertTrue(columns.length >= COLUMN_NAMES.length,
        		"TestColumnOrder: Did not get enough columns from table!");
        for (int i = 0; i < COLUMN_NAMES.length; i++)
        {
            String cName = columns[i].getColumnName();
            String expected = COLUMN_NAMES[i];
            // Handle torque.deprecated.uppercasePeer=true problems
            String upperCaseExpected = expected.toUpperCase();
            assertTrue(cName.equals(expected) ||
                    cName.equals(upperCaseExpected),
                    "TableMap for " + TABLE_NAME1
                    + " did not preserve XML column order!  Expected "
                    + expected + " but found " +
                    cName);
        }
    }

    /**
     * Test that XML table order is preserved in DatabaseMap objects.
     * <p>
     * Assumptions:
     * <ul>
     * The default database is bookstore<br>
     * TABLE_NAME1 is followed by TABLE_NAME2 in the array<br>
     * </ul>
     *
     * @throws TorqueException
     */
    @Test
    @Order(3)
    public void testTableOrder() throws TorqueException
    {
        DatabaseMap map = Torque.getDatabaseMap(DATABASE_NAME);
        map.initialize();
        TableMap[] tables = map.getTables();
        assertEquals(
        		TABLE_NAME1,
                tables[0].getName(),
                "XML Table order not preserved!\nDid not find table '"
                        + TABLE_NAME1 + "' in DatabaseMap.getTables() array!");
        assertEquals(
                TABLE_NAME2,
                tables[1].getName(),
                "XML Table order not preserved!\nDid not find table '"
                        + TABLE_NAME2 + "' after '" + TABLE_NAME1
                        + " in DatabaseMap.getTables() array!");
    }

    /**
     * Check that the external schema tables are added to the database map.
     * <p>
     * @throws TorqueException
     */
    @Test
    public void testExternalSchemaTables() throws TorqueException
    {
        DatabaseMap map = Torque.getDatabaseMap();
        map.initialize();

        TableMap table = map.getTable("ext");
        assertTrue( table != null,"Did not find external schema table, 'ext'!"
               );

        table = map.getTable("extext");
        assertTrue( table != null,"Did not find external schema table, 'extext'!");
    }

    /**
     * Test that various table properties get set correctly from the XML
     */
    @Test
    public void testTableAttributes() throws TorqueException
    {
        DatabaseMap map = Torque.getDatabaseMap(DATABASE_NAME);
        map.initialize();
        TableMap table = map.getTable(TABLE_NAME1);

        validateAttribute("TableMap Javaname", "NotUsedBeforeMapInit", table
                .getJavaName());
        validateAttribute(
                "TableMap description",
                "Table used for database map initialisation checks",
                table.getDescription());
        assertTrue( table.getOMClass() != null, "TableMap OM Class was null!");
        assertTrue( table.getPeerClass() != null, "TableMap Peer Class was null!");

        table = map.getTable(TABLE_NAME2);
        validateAttribute(
                "TableMap Javaname",
                "SecondMapInit",
                table.getJavaName());
    }

    /**
     * Test that various column properties get set correctly from the XML
     */
    @Test
    public void testColumnAttributes() throws TorqueException
    {
        DatabaseMap map = Torque.getDatabaseMap(DATABASE_NAME);
        map.initialize();
        TableMap table = map.getTable(TABLE_NAME1);

        ColumnMap column = table.getColumn("id");
        // Handle torque.deprecated.uppercasePeer=true problems
        if ( column == null ) {
            column = table.getColumn("ID");
        }

        validateAttribute("Column JavaName", "Id", column.getJavaName());
        validateAttribute("Column description", "id column", column
                .getDescription());

        assertTrue( column.isPrimaryKey(), "Column isPrimaryKey attribute returned false instead of true!");
        assertTrue( column.isAutoIncrement(),  "Column isAutoIncrement attribute returned false instead of true!");
        assertTrue( column.isNotNull(),   "Column isNotNull attribute returned false instead of true!");
        assertTrue( column.isUsePrimitive(), "Column isUsePrimitive attribute returned false instead of true!");
        assertTrue( column.getType() instanceof Integer, "Column type attribute was not Integer!");

        column = table.getColumn("one");
        // Handle torque.deprecated.uppercasePeer=true problems
        if ( column == null ) {
            column = table.getColumn("ONE");
        }
        assertFalse(column.isProtected(), "Column isProtected attribute returned true instead of false!");
        assertTrue(column.getSize() == 50, "Column size != 50");
        validateAttribute("Column default", "unknown", column
                .getDefault());

        column = table.getColumn("three");
        // Handle torque.deprecated.uppercasePeer=true problems
        if ( column == null ) {
            column = table.getColumn("THREE");
        }
        assertTrue( column.getPosition() == 4, "Column position attribute != 4");

        TableMap tableWithProtectedColumn = map.getTable(PROTECTED_TABLE);
        column = tableWithProtectedColumn.getColumn("id");
        assertTrue(column.isProtected(), "Column isProtected attribute returned false instead of true!");
        column = tableWithProtectedColumn.getColumn("name");
        assertTrue( column.isProtected(), "Column isProtected attribute returned false instead of true!");
    }

    /**
     * Test that the foreign key properties get set correctly from the XML
     */
    @Test
    public void testForeignKeyAttributes() throws TorqueException
    {
        DatabaseMap map = Torque.getDatabaseMap(DATABASE_NAME);
        map.initialize();
        TableMap table = map.getTable(TABLE_NAME1);
        List<ForeignKeyMap> foreignKeys = table.getForeignKeys();
        assertEquals(1, foreignKeys.size());
        ForeignKeyMap foreignKey = foreignKeys.get(0);
        assertEquals(TABLE_NAME2, foreignKey.getForeignTableName());
        assertEquals(1, foreignKey.getColumns().size());
        ForeignKeyMap.ColumnPair columnPair = foreignKey.getColumns().get(0);
        assertEquals(
                "three",
                columnPair.getLocal().getColumnName());
        assertEquals(
                "id",
                columnPair.getForeign().getColumnName());
    }

    /**
     * Test that Inheritance info is stored correctly
     */
    @Test
    public void testInheritanceMapping() throws TorqueException
    {
        DatabaseMap map = Torque.getDatabaseMap(DATABASE_NAME);
        map.initialize();
        TableMap table = map.getTable(TABLE_NAME1);
        assertTrue(table
                .isUseInheritance(),
                "Table isUseInheritance returned false!");

        ColumnMap column = table.getColumn("CLASS_NAME");

        assertTrue(column
                .isUseInheritance(),
                "Column isUseInheritance returned false!");

        InheritanceMap[] inhArray = column.getInheritanceMaps();

        assertEquals(3, inhArray.length, "Did not get 3 mappings back!");
        InheritanceMap inh = column.getInheritanceMap("C");
        assertTrue(inh.getKey()
                .equals(inhArray[1].getKey()),
                "Inheritance map did not preserve XML order!");
        validateAttribute("Inheritance key", "C", inh.getKey());
        validateAttribute("Inheritance className", "MapInheritanceChildC", inh
                .getClassName());
        validateAttribute("Inheritance extends",
                "org.apache.torque.test.dbobject.NotUsedBeforeMapInit",
                inh.getExtends());
    }

    /**
     * Test for controlled error on getting invalid database
     */
    @Test
    public void testInvalidDatabaseName() throws TorqueException
    {
        DatabaseMap map = Torque.getDatabaseMap(INVALID_DATABASE_NAME);
        try
        {
            map.initialize();
        }
        catch (TorqueException e)
        {
            return;
        }
        fail("DatabaseMap.initialize() should fail if invalid DB name used!");
    }

    /**
     * Tests whether all options are present
     */
    @Test
    public void testOptions() throws TorqueException
    {
        DatabaseMap databaseMap = Torque.getDatabaseMap(DATABASE_NAME);
        databaseMap.initialize();
        assertEquals(2, databaseMap.getOptions().size());
        assertEquals(
                "databaseOptionValue1",
                databaseMap.getOption("databaseOptionKey1"));
        assertEquals(
                "databaseOptionValue2",
                databaseMap.getOption("databaseOptionKey2"));

        TableMap tableMap = databaseMap.getTable("OPTION_TABLE");
        assertEquals(2, tableMap.getOptions().size());
        assertEquals(
                "tableOptionValue1",
                tableMap.getOption("tableOptionKey1"));
        assertEquals(
                "tableOptionValue2",
                tableMap.getOption("tableOptionKey2"));

        ColumnMap columnMap = tableMap.getColumn("ID");
        assertEquals(2, columnMap.getOptions().size());
        assertEquals(
                "columnOptionValue1",
                columnMap.getOption("columnOptionKey1"));
        assertEquals(
                "columnOptionValue2",
                columnMap.getOption("columnOptionKey2"));
    }

    /**
     * Validate that the attribute value matches
     * @param name
     */
    protected void validateAttribute(final String name,
            final String expected,
            final String result)
    {
        assertTrue(expected
                .equals(result),
                name + " attribute not set correctly!\n Expected '"
                + expected + "' and got '" + result + "'");
    }
}
