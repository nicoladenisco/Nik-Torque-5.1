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

import java.util.Map;

import org.apache.torque.Column;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;

/**
 * Utility methods for Database, Table and Column Maps.
 *
 * @version $Id: MapHelper.java 1839288 2018-08-27 09:48:33Z tv $
 */
public final class MapHelper
{
    /** Private constructor. */
    private MapHelper()
    {
        // empty
    }

    /**
     * Returns the table map for a table name.
     * As aliases and asColumns are resolved, the returned table map need not
     * contain the same table name as the column.
     *
     * @param possibleColumn the possible column to get the table map for.
     * @param criteria A criteria containing the database name and perhaps
     *        aliases for the column and table name, not null.
     * @param defaultTableMap a default table map which is used if the table
     *        name cannot be resolved, may be null.
     *
     * @return the table map, or null if possibleColumn does not implement
     *         the column interface or if the table name cannot be resolved.
     *
     * @throws TorqueException possibly if Torque is not initialized.
     */
    public static TableMap getTableMap(
            final Object possibleColumn,
            final Criteria criteria,
            final TableMap defaultTableMap)
                    throws TorqueException
    {
        if (!(possibleColumn instanceof Column))
        {
            return null;
        }
        Column column = (Column) possibleColumn;
        TableMap result = null;
        String tableName = column.getTableName();
        if (tableName == null)
        {
            // try asColumns
            Column asColumn = criteria.getAsColumns().get(
                    column.getSqlExpression());
            if (asColumn != null)
            {
                tableName = asColumn.getTableName();
            }
        }
        if (tableName != null)
        {
            String databaseName = criteria.getDbName();
            DatabaseMap databaseMap = Torque.getDatabaseMap(databaseName);
            if (databaseMap != null)
            {
                result = databaseMap.getTable(tableName);
            }
            if (result != null)
            {
                return result;
            }
            // try aliases
            Map<String, ? extends Object> aliases = criteria.getAliases();
            Object aliasMappedTo = aliases.get(tableName);
            if (aliasMappedTo != null && aliasMappedTo instanceof String)
            {
                tableName = (String) aliasMappedTo;
                result = databaseMap.getTable(tableName);
            }
        }
        if (result == null)
        {
            result = defaultTableMap;
        }
        return result;
    }

    /**
     * Returns the column map for a column.
     * As aliases and asColumns are resolved, the returned column map need not
     * contain the same column name as the column.
     *
     * @param column the column to get the column map for.
     * @param criteria A criteria containing the database name and perhaps
     *        aliases for the column and table name, not null.
     *
     * @return the column map, or null if the column name cannot be resolved.
     *
     * @throws TorqueException if Torque is not initialized and criteria's
     *         databaseName is null.
     */
    public static ColumnMap getColumnMap(
            Column column,
            final Criteria criteria)
                    throws TorqueException
    {
        String tableName = column.getTableName();
        {
            // try asColumns
            Column asColumn = criteria.getAsColumns().get(
                    column.getSqlExpression());
            if (asColumn != null)
            {
                column = asColumn;
                tableName = asColumn.getTableName();
            }
        }

        if (tableName == null)
        {
            return null;
        }
        String databaseName = criteria.getDbName();
        DatabaseMap databaseMap = Torque.getDatabaseMap(databaseName);
        TableMap tableMap = null;
        if (databaseMap != null)
        {
            tableMap = databaseMap.getTable(tableName);
        }
        if (tableMap == null)
        {
            // try aliases
            Map<String, ? extends Object> aliases = criteria.getAliases();
            Object aliasMappedTo = aliases.get(tableName);
            if (aliasMappedTo != null && aliasMappedTo instanceof String)
            {
                tableName = (String) aliasMappedTo;
                tableMap = databaseMap.getTable(tableName);
            }
        }
        // try aliases
        if (tableMap == null)
        {
            return null;
        }
        return tableMap.getColumn(column.getColumnName());
    }
}
