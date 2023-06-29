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

import org.apache.torque.templates.typemapping.SchemaType;
import org.apache.torque.templates.typemapping.SqlType;

import java.util.stream.Stream;

/**
 * Postgresql Platform implementation.
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: PlatformPostgresqlImpl.java 1896195 2021-12-20 17:41:20Z gk $
 */
public class PlatformPostgresqlImpl extends PlatformDefaultImpl
{
    /**
     * Default constructor.
     */
    public PlatformPostgresqlImpl()
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
                SchemaType.BIT,
                new SqlType("BOOLEAN"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.TINYINT,
                new SqlType("INT2"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.SMALLINT,
                new SqlType("INT2"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BIGINT,
                new SqlType("INT8"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.REAL,
                new SqlType("FLOAT"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BOOLEANCHAR,
                new SqlType("CHAR"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BOOLEANINT,
                new SqlType("INT2"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.DOUBLE,
                new SqlType("DOUBLE PRECISION"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.LONGVARCHAR,
                new SqlType("TEXT"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BINARY,
                new SqlType("BYTEA"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.VARBINARY,
                new SqlType("BYTEA"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.LONGVARBINARY,
                new SqlType("BYTEA"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BLOB,
                new SqlType("BYTEA"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.CLOB,
                new SqlType("TEXT"));
    }

    /**
     * @see Platform#getAutoIncrement()
     */
    @Override
    public String getAutoIncrement()
    {
        return "";
    }

    /**
     * @see Platform#hasScale(String)
     */
    @Override
    public boolean hasScale(String sqlType)
    {
        if (Stream.of("INT2", "TEXT", "BYTEA")
                .anyMatch(s -> s.equalsIgnoreCase(sqlType)))
        {
            return false;
        }
        return true;
    }

    /**
     * @see Platform#hasSize(String)
     */
    @Override
    public boolean hasSize(String sqlType)
    {
        if (Stream.of("INT2", "TEXT", "BYTEA")
                .anyMatch(s -> s.equalsIgnoreCase(sqlType)))
        {
            return false;
        }
        return true;
    }

    @Override
    protected boolean escapeBackslashes()
    {
        return true;
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
    
}
