package org.apache.torque.templates.transformer.om;

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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;

/**
 * Utility class containing methods to find certain elements
 * in the source tree.
 *
 * $Id: FindHelper.java 1896195 2021-12-20 17:41:20Z gk $
 */
final class FindHelper
{
    /**
     * Private constructor for utility class.
     */
    private FindHelper()
    {
    }

    /**
     * Finds a table in the given root element by name. The root element must
     * be a database element.
     *
     * @param root the database Source element, not null.
     * @param tableName the table name, not null.
     * @param searchExternalSchemata if the table should also be located
     *        in the external schemata.
     *
     * @return the column's source element, or null if no matching table
     *         exists.
     */
    public static SourceElement findTable(
            SourceElement root,
            String tableName,
            boolean searchExternalSchemata)
    {
        if (!TorqueSchemaElementName.DATABASE.getName().equals(
                root.getName()))
        {
            throw new IllegalArgumentException("Wrong source element "
                    + root.getName());
        }
        for (SourceElement table : root.getChildren(
                TorqueSchemaElementName.TABLE))
        {
            if (tableName.equals(table.getAttribute(
                    TorqueSchemaAttributeName.NAME)))
            {
                return table;
            }
        }
        if (searchExternalSchemata)
        {
            return root.getChildren(
                    TorqueSchemaElementName.EXTERNAL_SCHEMA).stream()
                    .map(externalSchema ->
                            externalSchema.getChild(TorqueSchemaElementName.DATABASE))
                    .map(databaseElement -> findTable(
                        databaseElement,
                        tableName,
                        searchExternalSchemata))
                    .filter(Objects::nonNull)
                    .findFirst().orElse(null);
        }
        return null;
    }

    /**
     * Finds a column in a table by name.
     *
     * @param table the table Source element, not null.
     * @param columnName the column name, not null.
     *
     * @return the column's source element, or null if no matching column
     *         exists.
     */
    public static SourceElement findColumn(
            SourceElement table,
            String columnName)
    {
        if (!TorqueSchemaElementName.TABLE.getName().equals(
                table.getName()))
        {
            throw new IllegalArgumentException("Wrong source element "
                    + table.getName());
        }
        return table.getChildren(
                TorqueSchemaElementName.COLUMN).stream()
                .filter(column -> column.getAttribute(TorqueSchemaAttributeName.NAME)
                .equals(columnName)).findFirst().orElse(null);
    }

    /**
     * Finds all foreign key in one table that reference a certain other table.
     *
     * @param table the table containing the foreign keys to search, not null.
     * @param tableName the name of the foreign table in the foreign Key.
     *
     * @return the foreign keys referencing the given table name.
     */
    public static List<SourceElement> findForeignKeyByReferencedTable(
            SourceElement table,
            String tableName)
    {
        List<SourceElement> result;
        if (!TorqueSchemaElementName.TABLE.getName().equals(
                table.getName()))
        {
            throw new IllegalArgumentException("Wrong source element "
                    + table.getName());
        }
        result = table.getChildren(
                TorqueSchemaElementName.FOREIGN_KEY).stream()
                .filter(foreignKey -> foreignKey.getAttribute(
                        TorqueSchemaAttributeName.FOREIGN_TABLE)
                .equals(tableName)).collect(Collectors.toList());
        return result;
    }
}
