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
 * MS SQL Platform implementation.
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:greg.monroe@dukece.com">Greg Monroe</a>
 * @version $Id: PlatformMssqlImpl.java 1872379 2020-01-06 13:45:27Z tv $
 */
public class PlatformMssqlImpl extends PlatformDefaultImpl
{
    /** The date format for formatting database dates. */
    private static final String DATE_FORMAT = "''yyyyMMdd HH:mm:ss''";

    /**
     * Default constructor.
     */
    public PlatformMssqlImpl()
    {
        super();
        initialize();
    }

    /**
     * Initializes db specific domain mapping.
     */
    private void initialize()
    {
        setSchemaTypeToSqlTypeMapping(
                SchemaType.INTEGER,
                new SqlType("INT"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BOOLEANINT,
                new SqlType("INT"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.DOUBLE,
                new SqlType("FLOAT"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.LONGVARCHAR,
                new SqlType("TEXT"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.TIMESTAMP,
                new SqlType("DATETIME"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BINARY,
                new SqlType("BINARY"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.VARBINARY,
                new SqlType("IMAGE"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.LONGVARBINARY,
                new SqlType("IMAGE"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BLOB,
                new SqlType("IMAGE"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.CLOB,
                new SqlType("TEXT"));
    }

    /**
     * @return Explicitly returns <code>NULL</code> if null values are
     * allowed (as recomended by Microsoft).
     * @see Platform#getNullString(boolean)
     */
    @Override
    public String getNullString(boolean notNull)
    {
        return (notNull ? "NOT NULL" : "NULL");
    }

    @Override
    protected boolean escapeBackslashes()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTimestampString(Date date)
    {
        return formatDateTimeString(date, DATE_FORMAT);
    }

    /**
     * Returns whether the database has schema support where a schema
     * is not tied to a user (oracle) or database (mysql), but can be created
     * separately.
     *
     * @return this implementation returns true.
     */
    @Override
    public boolean usesStandaloneSchema()
    {
        return true;
    }

    /**
     * @see Platform#hasSize(String)
     */
    @Override
    public boolean hasSize(String sqlType)
    {
        return !("IMAGE".equals(sqlType) || "TEXT".equals(sqlType));
    }
}
