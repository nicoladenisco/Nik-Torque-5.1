package org.apache.torque.util.functions;

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

import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.torque.ColumnImpl;

/**
 * Tests the AggregateFunction class.
 *
 * @version $Id: AggregateFunctionTest.java 1448414 2013-02-20 21:06:35Z tfischer $
 */
public class AggregateFunctionTest extends TestCase
{
    /**
     * Tests the aggregateFunction constructor without distinct
     * returns the correct SQL.
     */
    public void testAggregateFunction()
    {
        AggregateFunction aggregateFunction  = new AggregateFunction(
                "FUNCTION",
                new ColumnImpl("myColumn"),
                false);
        assertEquals(
                "FUNCTION(myColumn)",
                aggregateFunction.getSqlExpression());
    }

    /**
     * Tests the aggregateFunction constructor with distinct
     * returns the correct SQL.
     */
    public void testAggregateFunctionWithDistinct()
    {
        AggregateFunction aggregateFunction  = new AggregateFunction(
                "FUNCTION",
                new ColumnImpl("myColumn"),
                true);
        assertEquals(
                "FUNCTION(DISTINCT myColumn)",
                aggregateFunction.getSqlExpression());
    }

    /**
     * Tests that setFunction works
     */
    public void testSetFunction()
    {
        AggregateFunction aggregateFunction  = new AggregateFunction(
                "FUNCTION",
                new ColumnImpl("myColumn"),
                false);
        assertEquals("FUNCTION", aggregateFunction.getFunction());
        aggregateFunction.setFunction("OTHERFUNCTION");
        assertEquals("OTHERFUNCTION", aggregateFunction.getFunction());
        assertEquals(
                "OTHERFUNCTION(myColumn)",
                aggregateFunction.getSqlExpression());
    }

    /**
     * Tests that getArguments and SetArguments work
     */
    public void testGetSetArguments()
    {
        AggregateFunction aggregateFunction  = new AggregateFunction(
                "FUNCTION",
                new ColumnImpl("myColumn"),
                false);
        assertTrue(Arrays.equals(
                new Object[] {new ColumnImpl("myColumn"), false},
                aggregateFunction.getArguments()));

        aggregateFunction.setArguments(
                new Object[] {new ColumnImpl("otherColumn")});
        assertTrue(Arrays.equals(
                new Object[] {new ColumnImpl("otherColumn"), false},
                aggregateFunction.getArguments()));

        aggregateFunction.setArguments(
                new Object[] {new ColumnImpl("fooColumn"), true});
        assertTrue(Arrays.equals(
                new Object[] {new ColumnImpl("fooColumn"), true},
                aggregateFunction.getArguments()));
    }

    /**
     * Tests that getArgument(0) returns the column.
     */
    public void testGetFirstArgument()
    {
        AggregateFunction aggregateFunction  = new AggregateFunction(
                "FUNCTION",
                new ColumnImpl("myColumn"),
                false);
        assertEquals(
                new ColumnImpl("myColumn"),
                aggregateFunction.getArgument(0));
    }

    /**
     * Tests that getArgument(1) returns the distinct value.
     */
    public void testGetSecondArgument()
    {
        AggregateFunction aggregateFunction  = new AggregateFunction(
                "FUNCTION",
                new ColumnImpl("myColumn"),
                false);
        assertEquals(
                false,
                aggregateFunction.getArgument(1));
    }

    /**
     * Tests that getArgument(2) returns null.
     */
    public void testGetThirdArgument()
    {
        AggregateFunction aggregateFunction  = new AggregateFunction(
                "FUNCTION",
                new ColumnImpl("myColumn"),
                false);
        assertNull(aggregateFunction.getArgument(2));
    }

    /**
     * Tests that setColumn cannot be called with a null value.
     */
    public void testSetColumnNull()
    {
        AggregateFunction aggregateFunction  = new AggregateFunction(
                "FUNCTION",
                new ColumnImpl("myColumn"),
                false);
        try
        {
            aggregateFunction.setColumn(null);
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("column must not be null", e.getMessage());
        }
    }

    /**
     * Tests the getColumnName method always returns null.
     * This is because the function has no real column name.
     */
    public void testGetColumnName()
    {
        AggregateFunction aggregateFunction  = new AggregateFunction(
                "FUNCTION",
                new ColumnImpl("schema", "table", "column", "sqlExpression"),
                false);
        assertNull(aggregateFunction.getColumnName());
    }

    /**
     * Tests the getTableName method returns the table of the column argument.
     */
    public void testGetTableName()
    {
        AggregateFunction aggregateFunction  = new AggregateFunction(
                "FUNCTION",
                new ColumnImpl("schema", "table", "column", "sqlExpression"),
                false);
        assertEquals("table", aggregateFunction.getTableName());
    }

    /**
     * Tests the getFullTableName method returns the fullTableName of the
     * column argument.
     */
    public void testGetFullTableName()
    {
        AggregateFunction aggregateFunction  = new AggregateFunction(
                "FUNCTION",
                new ColumnImpl("schema", "table", "column", "sqlExpression"),
                false);
        assertEquals("schema.table", aggregateFunction.getFullTableName());
    }

    /**
     * Tests the getSchemaName method returns the schema of the column
     * argument.
     */
    public void testGetSchemaName()
    {
        AggregateFunction aggregateFunction  = new AggregateFunction(
                "FUNCTION",
                new ColumnImpl("schema", "table", "column", "sqlExpression"),
                false);
        assertEquals("schema", aggregateFunction.getSchemaName());
    }
}
