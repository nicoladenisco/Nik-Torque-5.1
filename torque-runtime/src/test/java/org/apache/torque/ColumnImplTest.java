package org.apache.torque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

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
 * Test for the class ColumnImpl
 *
 * @version $Id: ColumnImplTest.java 1850726 2019-01-08 10:56:07Z gk $
 */
public class ColumnImplTest
{
	
	/**
	 * Test table column constructor
	 */
	@Test
    public void testTableColumnConstructor()
    {
        ColumnImpl column = new ColumnImpl(
                "tableName",
                "columnName");
        assertEquals(null, column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals(
                "tableName.columnName",
                column.getSqlExpression());
    }

	/**
	 * Test table column constructor with schema in table 
	 */
	@Test
    public void testTableColumnConstructorWithSchemaInTable()
    {
        ColumnImpl column = new ColumnImpl(
                "schemaName.tableName",
                "columnName");
        assertEquals("schemaName", column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals(
                "schemaName.tableName.columnName",
                column.getSqlExpression());
    }

	/**
	 * Test table column constructor with null table name
	 */
	@Test
    public void testTableColumnConstructorNullTableName()
    {
        try
        {
            new ColumnImpl(null, "columnName");
            fail("Exception expected");
        }
        catch (NullPointerException e)
        {
            // expected
        }
    }

	/**
	 * Test table column constructor with blank table name
	 */
	@Test
    public void testTableColumnConstructorBlankTableName()
    {
        try
        {
            new ColumnImpl(" ", "columnName");
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

	@Test
    public void testTableColumnConstructorNullColumnName()
    {
        try
        {
            new ColumnImpl("tableName", null);
            fail("Exception expected");
        }
        catch (NullPointerException e)
        {
            // expected
        }
    }

	@Test
    public void testTableColumnConstructorBlankColumnName()
    {
        try
        {
            new ColumnImpl("tableName", " ");
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

	@Test
    public void testSchemaTableColumnConstructor()
    {
        ColumnImpl column = new ColumnImpl(
                "schemaName",
                "tableName",
                "columnName");
        assertEquals("schemaName", column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals(
                "tableName.columnName",
                column.getSqlExpression());
    }

	@Test
    public void testSchemaTableColumnConstructorWithSchemaInConstructorAndTable()
    {
        ColumnImpl column = new ColumnImpl(
                "schemaName",
                "otherSchema.tableName",
                "columnName");
        assertEquals("schemaName", column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals(
                "tableName.columnName",
                column.getSqlExpression());
    }

	@Test
    public void testSchemaTableColumnConstructorWithSchemaInTable()
    {
        ColumnImpl column = new ColumnImpl(
                null,
                "schemaName.tableName",
                "columnName");
        assertEquals("schemaName", column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals(
                "tableName.columnName",
                column.getSqlExpression());
    }

	@Test
    public void testSchemaTableColumnConstructorNullSchemaName()
    {
        ColumnImpl column = new ColumnImpl(
                null,
                "tableName",
                "columnName");
        assertEquals(null, column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals("tableName.columnName", column.getSqlExpression());
    }

	@Test
    public void testSchemaTableColumnConstructorNullTableName()
    {
        ColumnImpl column = new ColumnImpl("schemaName", null, "columnName");
        assertEquals("schemaName", column.getSchemaName());
        assertEquals(null, column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals("columnName", column.getSqlExpression());
    }

	@Test
    public void testSchemaTableColumnConstructorBlankSchemaName()
    {
        try
        {
            new ColumnImpl(" ", "tableName", "columnName");
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

	@Test
    public void testSchemaTableColumnConstructorBlankTableName()
    {
        try
        {
            new ColumnImpl("schemaName", " ", "columnName");
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

	@Test
    public void testSchemaTableColumnConstructorNullColumnName()
    {
        try
        {
            new ColumnImpl("schemaName", "tableName", null);
            fail("Exception expected");
        }
        catch (NullPointerException e)
        {
            // expected
        }
    }

	@Test
    public void testSchemaTableColumnConstructorBlankColumnName()
    {
        try
        {
            new ColumnImpl("schemaName", "tableName", " ");
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    @Test
    public void testSqlExpressionConstructor()
    {
        ColumnImpl column = new ColumnImpl("schemaName.tableName.columnName");
        assertEquals("schemaName", column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals("tableName.columnName", column.getSqlExpression());
    }

    @Test
    public void testSqlExpressionConstructorNull()
    {
        try
        {
            new ColumnImpl(null);
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    @Test
    public void testSqlExpressionConstructorBlank()
    {
        try
        {
            new ColumnImpl("");
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    @Test
    public void testSqlExpressionConstructorNoDot()
    {
        ColumnImpl column =  new ColumnImpl("columnName");
        assertEquals(null, column.getSchemaName());
        assertEquals(null, column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals("columnName", column.getSqlExpression());
    }

    @Test
    public void testSqlExpressionConstructorAsterisk()
    {
        ColumnImpl column = new ColumnImpl("tableName.*");
        assertEquals(null, column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals(null, column.getColumnName());
        assertEquals("tableName.*", column.getSqlExpression());
    }

    @Test
    public void testSqlExpressionConstructorAsteriskAndSchema()
    {
        ColumnImpl column = new ColumnImpl("schemaName.tableName.*");
        assertEquals("schemaName", column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals(null, column.getColumnName());
        assertEquals("tableName.*", column.getSqlExpression());
    }

    @Test
    public void testSqlExpressionConstructorBlankColumnName()
    {
        try
        {
            new ColumnImpl("tableName.");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    @Test
    public void testSqlExpressionConstructorBlankTableName()
    {
        try
        {
            new ColumnImpl(".columnName");
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    @Test
    public void testSqlExpressionConstructorCount()
    {
        ColumnImpl column
            = new ColumnImpl("count(schemaName.tableName.columnName)");
        assertEquals("schemaName", column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals(
                "count(tableName.columnName)",
                column.getSqlExpression());
    }

    @Test
    public void testSqlExpressionConstructorSqlFunction()
    {
        ColumnImpl column = new ColumnImpl(
                "function(1, schemaName.tableName.columnName ,2)");
        assertEquals("schemaName", column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals(
                "function(1, tableName.columnName ,2)",
                column.getSqlExpression());
    }

    @Test
    public void testSqlExpressionConstructorComparisonAfter()
    {
        ColumnImpl column = new ColumnImpl("tableName.columnName < 10");
        assertEquals(null, column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals(
                "tableName.columnName < 10",
                column.getSqlExpression());
    }

    @Test
    public void testSqlExpressionConstructorComparisonBefore()
    {
        ColumnImpl column = new ColumnImpl("10 < tableName.columnName");
        assertEquals(null, column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals(
                "10 < tableName.columnName",
                column.getSqlExpression());
    }

    @Test
    public void testSqlExpressionConstructorInClause()
    {
        ColumnImpl column = new ColumnImpl("tableName.columnName in (1,2,3)");
        assertEquals(null, column.getSchemaName());
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals(
                "tableName.columnName in (1,2,3)",
                column.getSqlExpression());
    }

    @Test
    public void testSqlExpressionConstructorSqlFunctionAsteriskColumnName()
    {
        ColumnImpl column = new ColumnImpl("count(tableName.*)");
        assertEquals("tableName", column.getTableName());
        assertEquals(null, column.getColumnName());
        assertEquals("count(tableName.*)", column.getSqlExpression());
    }

    @Test
    public void testSqlExpressionConstructorSqlFunctionAsteriskOnly()
    {
        ColumnImpl column = new ColumnImpl("count(*)");
        assertEquals(null, column.getTableName());
        assertEquals(null, column.getColumnName());
        assertEquals("count(*)", column.getSqlExpression());
    }

    @Test
    public void testSqlExpressionConstructorSqlFunctionEmptyTableName()
    {
        try
        {
            new ColumnImpl("count(.columnName)");
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    @Test
    public void testSqlExpressionConstructorAlias()
    {
        ColumnImpl column = new ColumnImpl("tableName.columnName AS x");
        assertEquals("tableName", column.getTableName());
        assertEquals("columnName", column.getColumnName());
        assertEquals(
                "tableName.columnName AS x",
                column.getSqlExpression());
    }

    @Test
    public void testGetSqlExpressionNoSchema()
    {
        ColumnImpl column = new ColumnImpl(null, "tableName", "columnName");
        assertEquals("tableName.columnName", column.getSqlExpression());
    }

    @Test
    public void testGetSqlExpressionWithSchema()
    {
        ColumnImpl column
            = new ColumnImpl("schemaName", "tableName", "columnName");
        assertEquals(
                "tableName.columnName",
                column.getSqlExpression());
    }

    @Test
    public void testEquals()
    {
        ColumnImpl column1 = new ColumnImpl(
                "schemaName",
                "tableName",
                "columnName",
                "tableName.columnName AS x");
        ColumnImpl column2 = new ColumnImpl(
                "schemaName",
                "tableName",
                "columnName",
                "tableName.columnName AS x");
        assertTrue(column1.equals(column2));
    }

    @Test
    public void testEqualsSchemaTableColumnNull()
    {
        ColumnImpl column1 = new ColumnImpl(
                null,
                null,
                null,
                "*");
        ColumnImpl column2 = new ColumnImpl(
                null,
                null,
                null,
                "*");
        assertTrue(column1.equals(column2));
    }

    @Test
    public void testEqualsWrongColumn()
    {
        ColumnImpl column1 = new ColumnImpl(
                "schemaName",
                "tableName",
                "columnName1");
        ColumnImpl column2 = new ColumnImpl(
                "schemaName",
                "tableName",
                "columnName2");
        assertFalse(column1.equals(column2));
    }

    @Test
    public void testEqualsWrongTable()
    {
        ColumnImpl column1 = new ColumnImpl(
                "schemaName",
                "tableName1",
                "columnName");
        ColumnImpl column2 = new ColumnImpl(
                "schemaName",
                "tableName2",
                "columnName");
        assertFalse(column1.equals(column2));
    }

    @Test
    public void testEqualsWrongSchema()
    {
        ColumnImpl column1 = new ColumnImpl(
                "schemaName1",
                "tableName",
                "columnName");
        ColumnImpl column2 = new ColumnImpl(
                "schemaName2",
                "tableName",
                "columnName");
        assertFalse(column1.equals(column2));
    }

    @Test
    public void testEqualsWrongSqlExpression()
    {
        ColumnImpl column1 = new ColumnImpl("tableName.columnName AS x");
        ColumnImpl column2 = new ColumnImpl("tableName.columnName AS y");
        assertFalse(column1.equals(column2));
    }

    @Test
    public void testToString()
    {
        ColumnImpl column = new ColumnImpl(
                "schemaName1.tableName1.columnName1 AS x");
        assertEquals("ColumnImpl [columnName=columnName1, tableName="
                + "tableName1, schemaName=schemaName1, "
                + "sqlExpression=tableName1.columnName1 AS x]",
                column.toString());
    }
}
