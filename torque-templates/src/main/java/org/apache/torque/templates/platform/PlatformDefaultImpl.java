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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.templates.typemapping.SchemaType;
import org.apache.torque.templates.typemapping.SqlType;


/**
 * Default implementation for the Platform interface.
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: PlatformDefaultImpl.java 1896195 2021-12-20 17:41:20Z gk $
 */
public class PlatformDefaultImpl implements Platform
{
    /** The date format for formatting database timestamps. */
    private static final String TIMESTAMP_FORMAT = "''yyyy-MM-dd HH:mm:ss''";

    /**
     * Maps the Torque schema types to sql types.
     */
    private Map<SchemaType, SqlType> schemaTypeToSqlTypeMap;

    /**
     * Default constructor.
     */
    public PlatformDefaultImpl()
    {
        initialize();
    }

    private void initialize()
    {
        schemaTypeToSqlTypeMap
        = Collections.synchronizedMap(
                new HashMap<SchemaType, SqlType>());
        for (SchemaType schemaType : SchemaType.values())
        {
            setSchemaTypeToSqlTypeMapping(
                    schemaType,
                    new SqlType(schemaType.name()));
        }
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BOOLEANCHAR,
                new SqlType("CHAR"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BOOLEANINT,
                new SqlType("INTEGER"));
    }

    /**
     * Adds a mapping to the torque schema type -&gt; sql type map.
     *
     * @param schemaType the torque schema type which should be mapped,
     *        not null.
     * @param sqlType the sql type for the torque schema type, not null.
     */
    protected void setSchemaTypeToSqlTypeMapping(
            SchemaType schemaType,
            SqlType sqlType)
    {
        if (schemaType == null)
        {
            throw new NullPointerException("schemaType must not be null");
        }
        if (sqlType == null)
        {
            throw new NullPointerException("sqlType must not be null");
        }
        schemaTypeToSqlTypeMap.put(schemaType, sqlType);
    }
    
    /**
     * Helper function to format date values to a platform-specific string.
     * 
     * set {@link ZoneId} to GMT timezone.
     * 
     * @param date the Date object
     * @param format the format string
     * @return the formatted string
     */
    protected String formatDateTimeString(Date date, String format)
    {
        return DateTimeFormatter.ofPattern(format)
                .format(date.toInstant()
                        .atZone(ZoneId.of("GMT")));
    }

    /**
     * @see Platform#getSqlTypeForSchemaType(SchemaType)
     */
    @Override
    public SqlType getSqlTypeForSchemaType(SchemaType schemaType)
    {
        return schemaTypeToSqlTypeMap.get(schemaType);
    }

    /**
     * @return Only produces a SQL fragment if null values are
     * disallowed.
     * @see Platform#getNullString(boolean)
     */
    @Override
    public String getNullString(boolean notNull)
    {
        return (notNull ? "NOT NULL" : "");
    }

    /**
     * @see Platform#getAutoIncrement()
     */
    @Override
    public String getAutoIncrement()
    {
        return "IDENTITY";
    }

    /**
     * @see Platform#hasScale(String)
     * TODO collect info for all platforms
     */
    @Override
    public boolean hasScale(String sqlType)
    {
        return true;
    }

    /**
     * @see Platform#hasSize(String)
     * TODO collect info for all platforms
     */
    @Override
    public boolean hasSize(String sqlType)
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
     */
    @Override
    public String getSizeSuffix(String sqlType)
    {
        return StringUtils.EMPTY;
    }

    /**
     * @see Platform#createNotNullBeforeAutoincrement()
     */
    @Override
    public boolean createNotNullBeforeAutoincrement()
    {
        return true;
    }

    /**
     * @see Platform#quoteAndEscape(String)
     */
    @Override
    public String quoteAndEscape(String text)
    {
        String result = text.replace("'", "''");
        if (escapeBackslashes())
        {
            result = result.replace("\\", "\\\\");
        }
        return "'" + result + "'";
    }

    /**
     * Returns whether backslashes must be escaped in string literals.
     *
     * @return true if backslashes bust be escaped, false otherwise.
     */
    protected boolean escapeBackslashes()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDateString(Date date)
    {
        return getTimestampString(date);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTimeString(Date date)
    {
        return getTimestampString(date);
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
     * Returns whether the database has schema support where a schema
     * is not tied to a user (oracle) or database (mysql), but can be created
     * separately.
     *
     * @return this implementation returns false.
     */
    @Override
    public boolean usesStandaloneSchema()
    {
        return false;
    }

    @Override
    public boolean hasUniqueConstraintSize() {
        return false;
    }
}
