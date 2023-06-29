package org.apache.torque.util;

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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.torque.Column;

/**
 * A value for a column, with the JDBC type if it is an explicit value.
 *
 * @version $Id: JdbcTypedValue.java 1867515 2019-09-25 15:02:03Z gk $
 */
public class JdbcTypedValue
{
    /** The JDBC type as in <code>java.sql.Types</code>. **/
    private int jdbcType;

    /** The value; may be null. */
    private Object value;

    /** A database expression to use instead of the value. */
    private Column sqlExpression;

    /**
     * Constructs a JdbcTypedValue with a value and a type.
     *
     * @param jdbcType The JDBC type as in <code>java.sql.Types</code>.
     * @param value The value; may be null.
     */
    public JdbcTypedValue(final Object value, final int jdbcType)
    {
        this.jdbcType = jdbcType;
        this.value = value;
    }

    /**
     * Constructs a JdbcTypedValue with a verbatim SQL.
     *
     * @param sqlExpression The sql expression to use instead of the value.
     */
    public JdbcTypedValue(final Column sqlExpression)
    {
        if (sqlExpression == null)
        {
            throw new IllegalArgumentException(
                    "sqlExpression must not be null");
        }
        this.sqlExpression = sqlExpression;
    }

    /**
     * Returns the JDBC type as in <code>java.sql.Types</code>.
     *
     * @return the JDBC type of the value.
     */
    public int getJdbcType()
    {
        return jdbcType;
    }

    /**
     * Sets the JDBC type as in <code>java.sql.Types</code>.
     *
     * @param jdbcType the JDBC type of the value.
     *
     * @throws IllegalStateException if sqlExpression is set.
     */
    public void setJdbcType(final int jdbcType)
    {
        if (sqlExpression != null)
        {
            throw new IllegalStateException(
                    "jdbcType may not be set if sqlExpression is set");
        }
        this.jdbcType = jdbcType;
    }

    /**
     * Returns the value.
     *
     * @return value the value, or null.
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Returns the sqlExpression to use instead of the value.
     *
     * @return value the sqlExpression, or null if not set.
     */
    public Column getSqlExpression()
    {
        return sqlExpression;
    }

    /**
     * Sets the value.
     *
     * @param value the value, may be null.
     *
     * @throws IllegalStateException if sqlExpression is set.
     */
    public void setValue(final Object value)
    {
        if (sqlExpression != null)
        {
            throw new IllegalStateException(
                    "value may not be set if sqlExpression is set");
        }
        this.value = value;
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder()
                .append(jdbcType)
                .append(value)
                .append(sqlExpression);
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        JdbcTypedValue other = (JdbcTypedValue) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder()
                .append(jdbcType, other.jdbcType)
                .append(value, other.value)
                .append(sqlExpression, other.sqlExpression);
        return equalsBuilder.isEquals();
    }

    @Override
    public String toString()
    {
        if (sqlExpression == null)
        {
            return "JdbcTypedValue [jdbcType=" + jdbcType
                    + ", value=" + value + "]";
        }
        else
        {
            return "JdbcTypedValue [sqlExpression=" + sqlExpression + "]";
        }
    }
}
