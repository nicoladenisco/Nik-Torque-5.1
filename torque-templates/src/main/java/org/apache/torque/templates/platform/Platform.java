package org.apache.torque.templates.platform;

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

import java.util.Date;

import org.apache.torque.templates.typemapping.SchemaType;
import org.apache.torque.templates.typemapping.SqlType;

/**
 * Interface for RDBMS platform specific behaviour.
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: Platform.java 1868940 2019-10-25 15:13:36Z gk $
 */
public interface Platform
{
    /**
     * Returns the db specific SQL type for a Torque type.
     *
     * @param schemaType the Torque type, not null.
     *
     * @return the db specific SQL type, or null if no SQL type is defined
     *         for the given Torque type.
     */
    SqlType getSqlTypeForSchemaType(SchemaType schemaType);

    /**
     * @param notNull flag for not null
     * @return The RDBMS-specific SQL fragment for <code>NULL</code>
     * or <code>NOT NULL</code>.
     */
    String getNullString(boolean notNull);

    /**
     * @return The RDBMS-specific SQL fragment for autoincrement.
     */
    String getAutoIncrement();

    /**
     * Returns if the RDBMS-specific SQL type has a size attribute.
     *
     * @param sqlType the SQL type
     * @return true if the type has a size attribute
     */
    boolean hasSize(String sqlType);

    /**
     * Returns if the RDBMS-specific SQL type has a scale attribute.
     *
     * @param sqlType the SQL type
     * @return true if the type has a scale attribute
     */
    boolean hasScale(String sqlType);

    /**
     * Returns a possible SQL suffix for column definitions of certain
     *  SQL Types, e.g. for Oracle VARCHAR2 columns, it typically
     *  makes sense to use 'XXX CHAR' instead of 'XXX' as size.
     *
     * @param sqlType the SQL type to determine the suffix for.
     *
     * @return the size suffix, not null, may be empty.
     */
    String getSizeSuffix(String sqlType);

    /**
     * Returns whether the "not null part" of the definition of a column
     * should be generated before the "autoincrement part" in a "create table"
     * statement.
     *
     * @return true if the "not null part" should be first,
     *         false if the "autoincrement part" should be first in a
     *         "create table" statement.
     */
    boolean createNotNullBeforeAutoincrement();

    /**
     * Quotes and escapes a string such that it can be used
     * as literal String value in SQL.
     *
     * @param value The string to escape, or null.
     *
     * @return the escaped String, not null.
     */
    String quoteAndEscape(String value);

    /**
     * Formats the given date as date string which is parseable by the database.
     *
     * @param date the date to format.
     *
     * @return the date string, inclusive string escaping.
     */
    String getDateString(Date date);

    /**
     * Formats the given date as time string which is parseable by the database.
     *
     * @param date the date to format.
     *
     * @return the time string, inclusive string escaping.
     */
    String getTimeString(Date date);

    /**
     * Formats the given date as timestamp string which is parseable
     * by the database.
     *
     * @param date the date to format.
     *
     * @return the timestamp string, inclusive string escaping.
     */
    String getTimestampString(Date date);

    /**
     * Returns whether the database has schema support where a schema
     * is not tied to a user (oracle) or database (mysql), but can be created
     * separately.
     *
     * @return true if separate schema creation is possible, false if not.
     */
    boolean usesStandaloneSchema();
    
    /**
     * Returns whether the database has schema support unique constraint
     * for columns with size
     *
     * @return true if unique column constraint has size
     */
    boolean hasUniqueConstraintSize();
}
