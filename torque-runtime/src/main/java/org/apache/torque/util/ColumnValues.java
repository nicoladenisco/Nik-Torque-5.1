package org.apache.torque.util;

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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.torque.Column;

/**
 * A class containing values for database columns.
 * The insertion order of the entries is preserved.
 *
 * @version $Id: ColumnValues.java 1840416 2018-09-09 15:10:22Z tv $
 */
public class ColumnValues implements Map<Column, JdbcTypedValue>
{
    /** The column values, keyed by the column names. */
    private final Map<Column, JdbcTypedValue> columnValues
        = new LinkedHashMap<>();

    /**
     * The name of the database handle to use for connection opening if needed,
     * or null to use the default database handle for the table.
     */
    private String dbName;

    /**
     * Constructor with no contained column values.
     *
     * @throws NullPointerException if table is null.
     */
    public ColumnValues()
    {
    }

    /**
     * Constructor with no contained column values.
     *
     * @param dbName the name of the database handle to use for connection
     *        opening if needed, or null to use the default database handle
     *        for the table.
     *
     * @throws NullPointerException if table is null.
     */
    public ColumnValues(final String dbName)
    {
        this();
        this.dbName = dbName;
    }

    /**
     * Constructor.
     *
     * @param columnValues the column values, or null.
     *
     * @throws NullPointerException if table is null.
     */
    public ColumnValues(
            final Map<Column, JdbcTypedValue> columnValues)
    {
        if (columnValues != null)
        {
            this.columnValues.putAll(columnValues);
        }
    }

    /**
     * Constructor.
     *
     * @param columnValues the column values, or null.
     * @param dbName the name of the database handle to use for connection
     *        opening if needed, or null to use the default database handle
     *        for the table.
     *
     * @throws NullPointerException if table is null.
     */
    public ColumnValues(
            final Map<Column, JdbcTypedValue> columnValues,
            final String dbName)
    {
        this(columnValues);
        this.dbName = dbName;
    }

    /**
     * Returns the name of the database handle to use for connection
     * opening.
     *
     * @return the database name, or null to use the default database handle
     *         for the table.
     */
    public String getDbName()
    {
        return dbName;
    }

    @Override
    public int size()
    {
        return columnValues.size();
    }

    @Override
    public boolean isEmpty()
    {
        return columnValues.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key)
    {
        return columnValues.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value)
    {
        return columnValues.containsValue(value);
    }

    @Override
    public JdbcTypedValue get(final Object key)
    {
        return columnValues.get(key);
    }

    @Override
    public JdbcTypedValue put(final Column key, final JdbcTypedValue value)
    {
        return columnValues.put(key, value);
    }

    @Override
    public JdbcTypedValue remove(final Object key)
    {
        return columnValues.remove(key);
    }

    @Override
    public void putAll(final Map<? extends Column, ? extends JdbcTypedValue> t)
    {
        columnValues.putAll(t);
    }

    @Override
    public void clear()
    {
        columnValues.clear();
    }

    @Override
    public Set<Column> keySet()
    {
        return columnValues.keySet();
    }

    @Override
    public Collection<JdbcTypedValue> values()
    {
        return columnValues.values();
    }

    @Override
    public Set<java.util.Map.Entry<Column, JdbcTypedValue>> entrySet()
    {
        return columnValues.entrySet();
    }

    @Override
    public String toString()
    {
        return "ColumnValues [dbName=" + dbName
                + ", columnValues=" + columnValues + "]";
    }

}
