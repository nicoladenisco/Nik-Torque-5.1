package org.apache.torque;

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
 * The metadata of a column (or pseudocolumn) in a database.
 *
 * @version $Id: Column.java 1448414 2013-02-20 21:06:35Z tfischer $
 */
public interface Column
{
    /**
     * Returns the name of the database column (not prefixed by the table name).
     *
     * @return the name of the column, may be null.
     *         (e.g. for the pseudocoulumn count(*)).
     *         Is not blank.
     */
    String getColumnName();

    /**
     * Returns the name of the associated table
     * (not prefixed by the schema name).
     *
     * @return the name of the table, may be null but not blank.
     */
    String getTableName();

    /**
     * Returns the name of any fixed schema prefix for the column's table
     * (if any).
     *
     * @return the schema name, or null if the schema is not known.
     */
    String getSchemaName();

    /**
     * Returns the table name prefixed with the schema name if it exists.
     * I.e. if a schema name exists, the result will be schemaName.tableName,
     * and otherwise it will just be tableName.
     *
     * @return the fully qualified table name of the column,
     *         may be null but not blank.
     */
    String getFullTableName();

    /**
     * Returns the SQL expression for the column, qualified by the
     * table name but not by the schema name.
     * This can also be a pseudocolumn (e.g. count(*)).
     *
     * @return the SQL expression for the column, not null.
     */
    String getSqlExpression();
}
