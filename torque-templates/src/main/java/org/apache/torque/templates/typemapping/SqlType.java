package org.apache.torque.templates.typemapping;

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

import org.apache.commons.lang3.StringUtils;

/**
 * The SQL type data for a column. Can contain additional information such as
 * default size, scale and defaultValue.
 * This class is immutable.
 *
 * @version $Id: SqlType.java 1856067 2019-03-22 15:32:47Z gk $
 */
public class SqlType
{
    /**
     * The default size for the columns with this type.
     */
    private String size;

    /**
     * The default scale for the columns with this type.
     */
    private String scale;

    /**
     * The default "default value" for the columns with this type.
.     */
    private String defaultValue;

    /**
     * The SQL expression for the type name, or null if unknown.
     */
    private final String sqlTypeName;

    /**
     * Creates a new SqlType with the given SQL Type.
     * Size, scale and defaultValue are set to null.
     *
     * @param sqlTypeName the SQL name of the SQL type, or null.
     */
    public SqlType(final String sqlTypeName)
    {
        this.sqlTypeName = sqlTypeName;
    }

    /**
     * Creates a new SqlType with null scale and null default value.
     *
     * @param sqlTypeName the SQL name of the SQL type, not null.
     * @param size the default size of the columns with this SQL type.
     *        Can be overridden in the column definition.
     *
     * @throws NullPointerException if sqlTypeName is null.
     */
    public SqlType(
            final String sqlTypeName,
            final String size)
    {
        this(sqlTypeName);
        this.size = size;
    }

    /**
     * Creates a new SqlType with null default value.
     *
     * @param sqlTypeName the SQL name of the SQL type, not null.
     * @param size the default size of the columns with this SQL type.
     *        Can be overridden in the column definition.
     * @param scale the default scale of the columns with this SQL type.
     *        Can be overridden in the column definition.
     *
     * @throws NullPointerException if sqlTypeName is null.
     */
    public SqlType(
            final String sqlTypeName,
            final String size,
            final String scale)
    {
        this(sqlTypeName, size);
        this.scale = scale;
    }

    /**
     * Creates a new SqlType.
     *
     * @param sqlTypeName the SQL name of the SQL type, not null.
     * @param size the default size of the columns with this SQL type.
     *        Can be overridden in the column definition.
     * @param scale the default scale of the columns with this SQL type.
     *        Can be overridden in the column definition.
     * @param defaultValue the default "default value" of the columns with this
     *        SQL type. Can be overridden in the column definition.
     *
     * @throws NullPointerException if sqlTypeName is null.
     */
    public SqlType(
            final String sqlTypeName,
            final String size,
            final String scale,
            final String defaultValue)
    {
        this(sqlTypeName, size, scale);
        this.defaultValue = defaultValue;
    }

    /**
     * Creates a new SqlType by copying another sql type.
     *
     * @param sqlType the SQL type, to copy, not null.
     * @param size the default size of the columns with this SQL type.
     * @param scale the default scale of the columns with this SQL type.
     * @param defaultValue the default "default value" of the columns with this
     *        SQL type.
     *
     * @throws NullPointerException if sqlType is null.
     */
    public SqlType(
            final SqlType sqlType,
            final String size,
            final String scale,
            final String defaultValue)
    {
        this(sqlType.getSqlTypeName());
        if (size != null)
        {
            this.size = size;
        }
        else
        {
            this.size = sqlType.getSize();
        }
        if (scale != null)
        {
            this.scale = scale;
        }
        else
        {
            this.scale = sqlType.getScale();
        }
        if (defaultValue != null)
        {
            this.defaultValue = defaultValue;
        }
        else
        {
            this.defaultValue = sqlType.getDefaultValue();
        }
    }

    /**
     * @return Returns the scale.
     */
    public String getScale()
    {
        return scale;
    }

    /**
     * @return Returns the size.
     */
    public String getSize()
    {
        return size;
    }

    /**
     * @return Returns the defaultValue.
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Returns the SQL type name.
     *
     * @return The SQL type name for this column type, or null if the type
     *         is not set.
     */
    public String getSqlTypeName()
    {
        return sqlTypeName;
    }

    /**
     * Return the size and scale in brackets for use in an SQL script.
     * 
     * @return size and scale, size or an empty String if there are no values
     *         available.
     */
    public String printSize(final String sizeSuffix)
    {
        if (StringUtils.isNotBlank(size) && StringUtils.isNotBlank(scale))
        {
            return '(' + size + sizeSuffix + ',' + scale + ')';
        }
        else if (StringUtils.isNotBlank(size))
        {
            return '(' + size + sizeSuffix + ')';
        }
        else
        {
            return "";
        }
    }
    
    /**
     * Return the scale in brackets for use in an SQL script.
     *
     * @return scale or an empty String if there are no values
     *         available.
     */
    public String printScale()
    {
        if (StringUtils.isNotBlank(scale))
        {
            return '(' + scale + ')';
        }
        else
        {
            return "";
        }
    }

    /**
     * Returns a new instance with the given sqlTypeName, size, scale
     * and default value.
     * @param size of the sql
     * @param scale of the sql
     * @param defaultValue to be provided
     * @return a new instance with the given parameters.
     */
    public SqlType getNew(
            final String size,
            final String scale,
            final String defaultValue)
    {
        return new SqlType(this, size, scale, defaultValue);
    }

}
