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

import java.util.Arrays;
import java.util.Date;

import org.apache.torque.templates.typemapping.SchemaType;
import org.apache.torque.templates.typemapping.SqlType;

/**
 * MySql Platform implementation.
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: PlatformMysqlImpl.java 1896195 2021-12-20 17:41:20Z gk $
 */
public class PlatformMysqlImpl extends PlatformDefaultImpl
{
    /** The date format for formatting database dates. */
    private static final String DATE_FORMAT = "''yyyy-MM-dd''";
    
    /** The date format for formatting database dates. */
    private static final String TIMESTAMP_FORMAT = "''yyyy-MM-dd HH:mm:ss.SSS''";

    /**
     * Default constructor.
     */
    public PlatformMysqlImpl()
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
                SchemaType.NUMERIC,
                new SqlType("DECIMAL"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.LONGVARCHAR,
                new SqlType("MEDIUMTEXT"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.TIMESTAMP,
                new SqlType("DATETIME")); // support milliseconds
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BINARY,
                new SqlType("BLOB"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.VARBINARY,
                new SqlType("MEDIUMBLOB"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.LONGVARBINARY,
                new SqlType("LONGBLOB"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.BLOB,
                new SqlType("LONGBLOB"));
        setSchemaTypeToSqlTypeMapping(
                SchemaType.CLOB,
                new SqlType("LONGTEXT"));
    }

    /**
     * @see Platform#getAutoIncrement()
     */
    @Override
    public String getAutoIncrement()
    {
        return "AUTO_INCREMENT";
    }

    /**
     * @see Platform#hasSize(String)
     */
    @Override
    public boolean hasSize(String sqlType)
    {
        return !(Arrays.asList("MEDIUMTEXT", "LONGTEXT", "BLOB", "MEDIUMBLOB", "LONGBLOB")
                .contains(sqlType));
    }
    
    
    /**
     * precison in time formats is more like a scale, which is the number of digits to the right of the decimal point in a number.
     *  
     * @see Platform#hasScale(String)
     */
    @Override
    public boolean hasScale(String sqlType)
    {
        return Arrays.asList("TIMESTAMP", "DATETIME", "DATE", "TIME").contains(sqlType);
    }
   

    @Override
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
        return formatDateTimeString(date, DATE_FORMAT);
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
     * {@inheritDoc}
     */
    @Override
    public boolean hasUniqueConstraintSize() {
        return true;
    }
}
