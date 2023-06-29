package org.apache.torque.generator.source.jdbc;

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
 * The data retrieved about a column as read from JDBC Metadata.
 *
 * @version $Id: ColumnMetadata.java 1331190 2012-04-27 02:41:35Z tfischer $
 */
public class ColumnMetadata
{
    /** The column name. */
    private String name;

    /** The data type of the column, as in java.sql.Types. */
    private Integer sqlType;

    /** The maximum size of the column. */
    private Integer size;

    /**
     * 0 if the column may not be null,
     * 1 if the column may be known,
     * 2 unknown. */
    private Integer nullType;

    /** The default value of the column, or null if no default value exists. */
    private String defValue;

    /** The number of decimal digits of the column. */
    private Integer decimalDigits;


    /**
     * Constructor.
     *
     * @param name The name of the column.
     * @param sqlType The data type of the column.
     * @param size The maximum size of the column.
     * @param nullType 0 if the column may not be null,
     *        1 if the column may be known,
     *        2 unknown.
     * @param defValue The default value of the column,
     *        or null if no default value exists.
     * @param decimalDigits The number of decimal digits of the column.
     */
    public ColumnMetadata(
            String name,
            Integer sqlType,
            Integer size,
            Integer nullType,
            String defValue,
            Integer decimalDigits)
    {
        this.name = name;
        this.sqlType = sqlType;
        this.size = size;
        this.nullType = nullType;
        this.defValue = defValue;
        this.decimalDigits = decimalDigits;
    }


    /**
     * Returns the name of the column.
     *
     * @return the name of the column.
     */
    public String getName()
    {
        return name;
    }


    /**
     * Returns the data type of the column.
     *
     * @return the data type of the column as in java.sql.Types.
     */
    public Integer getSqlType()
    {
        return sqlType;
    }


    /**
     * Returns the maximum size of the column.
     *
     * @return the maximum size of the column.
     */
    public Integer getSize()
    {
        return size;
    }


    /**
     * Returns whether the column may be null.
     *
     * @return 0 if the column may not be null,
     *         1 if the column may be known,
     *         2 unknown.
     */
    public Integer getNullType()
    {
        return nullType;
    }


    /**
     * Returns the default value of the column.
     *
     * @return the default value, or 0 if no default value exists.
     */
    public String getDefValue()
    {
        return defValue;
    }


    /**
     * Returns the number of decimal digits.
     *
     * @return the number of decimal digits.
     */
    public Integer getDecimalDigits()
    {
        return decimalDigits;
    }
}
