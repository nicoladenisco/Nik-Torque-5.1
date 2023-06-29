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

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.templates.typemapping.SchemaType;
import org.apache.torque.templates.typemapping.SqlType;

/**
 * Oracle Platform implementation.
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: PlatformOracleImpl.java 1872379 2020-01-06 13:45:27Z tv $
 */
public class PlatformOracleImpl extends PlatformDefaultImpl
{
    /** The date format for formatting database dates. */
    private static final String DATE_FORMAT
    = "'to_date('''yyyy-MM-dd''',''yyyy-mm-dd'')'";

    /** The date format for formatting database dates. */
    private static final String TIME_FORMAT
    = "'to_date('''1970-01-01 HH:mm:ss''',''yyyy-mm-dd hh24:mi:ss'')'";

    /** The date format for formatting database dates. */
    private static final String TIMESTAMP_FORMAT
    = "'to_timestamp('''yyyy-MM-dd HH:mm:ss''',''yyyy-mm-dd hh24:mi:ss'')'";

    /**
     * Default constructor.
     */
    public PlatformOracleImpl()
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
                SchemaType.TINYINT,
                new SqlType("NUMBER", "3", "0"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.SMALLINT,
                new SqlType("NUMBER", "5", "0"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.INTEGER,
                new SqlType("NUMBER", "10", "0"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BOOLEANINT,
                new SqlType("NUMBER", "1", "0"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BIGINT,
                new SqlType("NUMBER", "20", "0"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.REAL,
                new SqlType("NUMBER"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.DOUBLE,
                new SqlType("NUMBER"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.DECIMAL,
                new SqlType("NUMBER"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.NUMERIC,
                new SqlType("NUMBER"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.VARCHAR,
                new SqlType("VARCHAR2"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.LONGVARCHAR,
                new SqlType("VARCHAR2", "2000"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.TIME,
                new SqlType("DATE"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BINARY,
                new SqlType("BLOB"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.VARBINARY,
                new SqlType("BLOB"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.LONGVARBINARY,
                new SqlType("BLOB"));
    }

    /**
     * @see Platform#getAutoIncrement()
     */
    @Override
    public String getAutoIncrement()
    {
        return "";
    }

    @Override
    protected boolean escapeBackslashes()
    {
        return true;
    }

    /**
     * Returns a possible SQL suffix for column definitions of certain
     * SQL Types, e.g. for Oracle VARCHAR2 columns, it typically
     * makes sense to use 'x CHAR' instead of 'x' as size.
     *
     * @param sqlType the SQL type to determine the suffix for.
     *
     * @return The size suffix, not null.
     *         This implementation always returns the empty string.
     *
     */
    @Override
    public String getSizeSuffix(String sqlType)
    {
        if ("VARCHAR2".equals(sqlType))
        {
            return " CHAR";
        }
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDateString(Date date)
    {
        return formatDateTimeString(date, DATE_FORMAT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTimeString(Date date)
    {
        return formatDateTimeString(date, TIME_FORMAT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTimestampString(Date date)
    {
        return formatDateTimeString(date, TIMESTAMP_FORMAT);
    }

    /**
     * @see Platform#hasSize(String)
     */
    @Override
    public boolean hasSize(String sqlType)
    {
        return !("BLOB".equals(sqlType) || "CLOB".equals(sqlType));
    }

    /**
     * @see Platform#hasScale(String)
     */
    @Override
    public boolean hasScale(String sqlType)
    {
        return "TIMESTAMP".equals(sqlType) || "NUMBER".equals(sqlType);
    }
}
