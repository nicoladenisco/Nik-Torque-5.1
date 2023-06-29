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

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.torque.ColumnImpl;

/**
 * Tests the AggregateFunction class.
 *
 * @version $Id: AbstractFunctionTest.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class AbstractFunctionTest extends TestCase
{
    private AbstractFunction abstractFunction;

    @Override
    public void setUp()
    {
        abstractFunction = new MyFunction();
    }


    /**
     * Tests that getArguments and SetArguments work
     */
    public void testGetSetArguments()
    {
        assertTrue(Arrays.equals(
                new Object[] {},
                abstractFunction.getArguments()));

        abstractFunction.setArguments(
                new Object[] {new ColumnImpl("fooColumn"), true});
        assertTrue(Arrays.equals(
                new Object[] {new ColumnImpl("fooColumn"), true},
                abstractFunction.getArguments()));
    }

    /**
     * Tests that getArgumentList and SetArgumentList work
     */
    public void testGetSetArgumentList()
    {
        ArrayList<Object> arguments = new ArrayList<>();
        arguments.add(false);
        arguments.add(new ColumnImpl("fooColumn"));
        abstractFunction.setArgumentList(arguments);
        assertEquals(arguments, abstractFunction.getArgumentList());
    }

    /**
     * Tests that addArgument works
     */
    public void testAdArgument()
    {
        abstractFunction.addArgument(false);
        assertTrue(Arrays.equals(
                new Object[] {false},
                abstractFunction.getArguments()));
    }

    /**
     * Tests that getArgument(0) returns the column.
     */
    public void testGetArgument()
    {
        abstractFunction.setArguments(
                new Object[] {
                        new ColumnImpl("fooColumn"),
                        new ColumnImpl("barColumn"),
                        true});
        assertEquals(
                new ColumnImpl("barColumn"),
                abstractFunction.getArgument(01));
    }

    /**
     * Tests the getColumnName method always returns null.
     * This is because the function has no real column name.
     */
    public void testGetColumnName()
    {
        abstractFunction.setArguments(
                new Object[] {
                        new ColumnImpl("schema", "table", "column", "sqlExpression"),
                        true});
        assertNull(abstractFunction.getColumnName());
    }

    /**
     * Tests the getTableName method returns the table of the column argument.
     */
    public void testGetTableName()
    {
        abstractFunction.setArguments(
                new Object[] {
                        new ColumnImpl("schema", "table", "column", "sqlExpression"),
                        new ColumnImpl("schema", "table", "column", "sqlExpression"),
                        true});
        assertEquals("table", abstractFunction.getTableName());
    }

    /**
     * Tests the getTableName method returns null if different tables
     * are encountered.
     */
    public void testGetTableNameDifferentTables()
    {
        abstractFunction.setArguments(
                new Object[] {
                        new ColumnImpl(null, null, null, "sqlExpression"),
                        new ColumnImpl("schema", "table", "column", "sqlExpression"),
                        true});
        assertNull(abstractFunction.getTableName());
    }

    /**
     * Tests the getFullTableName method returns the fullTableName of the
     * column argument.
     */
    public void testGetFullTableName()
    {
        abstractFunction.setArguments(
                new Object[] {
                        new ColumnImpl("schema", "table", "column", "sqlExpression"),
                        new ColumnImpl("schema", "table", "column", "sqlExpression"),
                        true});
        assertEquals("schema.table", abstractFunction.getFullTableName());
    }

    /**
     * Tests the getFullTableName method returns null if different tables
     * are encountered.
     */
    public void testGetFullTableNameDifferentTables()
    {
        abstractFunction.setArguments(
                new Object[] {
                        new ColumnImpl(null, null, null, "sqlExpression"),
                        new ColumnImpl("schema", "table", "column", "sqlExpression"),
                        true});
        assertNull(abstractFunction.getFullTableName());
    }

    /**
     * Tests the getSchemaName method returns the schema of the column
     * argument.
     */
    public void testGetSchemaName()
    {
        abstractFunction.setArguments(
                new Object[] {
                        new ColumnImpl("schema", "table", "column", "sqlExpression"),
                        new ColumnImpl("schema", "table", "column", "sqlExpression"),
                        true});
        assertEquals("schema", abstractFunction.getSchemaName());
    }

    /**
     * Tests the getSchemaName method returns null if different schemas
     * are encountered.
     */
    public void testGetSchemaNameDifferentTables()
    {
        abstractFunction.setArguments(
                new Object[] {
                        new ColumnImpl(null, null, null, "sqlExpression"),
                        new ColumnImpl("schema", "table", "column", "sqlExpression"),
                        true});
        assertNull(abstractFunction.getSchemaName());
    }

    private static class MyFunction extends AbstractFunction
    {
        @Override
        public String getSqlExpression()
        {
            return "MYFUNCTION(" + getArgumentList().toString() + ")";
        }

    }
}
