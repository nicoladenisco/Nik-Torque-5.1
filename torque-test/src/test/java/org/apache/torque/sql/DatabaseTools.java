package org.apache.torque.sql;

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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DatabaseTools
{
    /**
     * Returns all table names available for this Connection.
     *
     * @param connection the database connection to.
     *
     * @return the table names in upper case.
     *
     * @throws SQLException if an error occurs.
     */
    public static Set<String> getTableNames(Connection connection)
            throws SQLException
    {
        DatabaseMetaData metadata = connection.getMetaData();
        ResultSet tableResultSet = metadata.getTables(
                null, null, "%", new String[] {"TABLE"});
        Set<String> result = new HashSet<>();
        while (tableResultSet.next())
        {
            String tableName = tableResultSet.getString(3);
            result.add(tableName.toUpperCase());
        }
        return result;
    }

    /**
     * Returns whether a table with the given name exists.
     *
     * @param connection the database connection to.
     * @param name the name of the table to check.
     *
     * @return whether the table name exists.
     *
     * @throws SQLException if an error occurs.
     */
    public static boolean tableExists(String name, Connection connection)
            throws SQLException
    {
        Set<String> tableNames = getTableNames(connection);
        return tableNames.contains(name.toUpperCase());
    }
}
