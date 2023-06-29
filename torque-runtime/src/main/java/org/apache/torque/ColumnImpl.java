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

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * An implementation of the column interface.
 * This class is immutable, i.e cannot be changed once constructed.
 *
 * @version $Id: ColumnImpl.java 1879929 2020-07-16 07:42:57Z gk $
 */
public class ColumnImpl implements Column, Serializable
{
    /** Serial version uid. **/
    private static final long serialVersionUID = 1L;

    /** Delimiters for SQL functions. */
    private static final String[] FUNCTION_DELIMITERS
    = {" ", ",", "(", ")", "<", ">"};

    /** Constant for the dot. */
    private static final String DOT = ".";

    /** The column name, may be null but not empty. */
    private String columnName;

    /** The table name, may be null but not empty. */
    private String tableName;

    /** The schema name, may be null but not empty. */
    private String schemaName;

    /** The SQL expression for the column, not null. */
    private String sqlExpression;

    /**
     * Constructor.
     *
     * @param tableName the table name, not null or blank.
     * @param columnName the column name, not null or blank.
     *
     * @throws NullPointerException if columnName or tableName are null.
     * @throws IllegalArgumentException if columnName or tableName are blank.
     */
    public ColumnImpl(final String tableName, final String columnName)
    {
        if (columnName == null)
        {
            throw new NullPointerException("columnName must not be null");
        }
        if (tableName == null)
        {
            throw new NullPointerException("tableName must not be null");
        }
        setColumnName(columnName);
        setTableName(tableName);
        this.sqlExpression = tableName + DOT + columnName;
    }

    /**
     * Constructor.
     *
     * @param schemaName the schema name, may be null but not blank.
     * @param tableName the table name, may be null but not blank.
     *        If it contains a dot, then only the portion after the dot
     *        will be taken as table name.
     *        If it contains a dot and schemaName is null, then the schema
     *        name will be set as the portion before the dot.
     * @param columnName the column name, not null or blank.
     *
     * @throws NullPointerException if columnName or tableName are null.
     * @throws IllegalArgumentException if columnName or tableName are blank.
     */
    public ColumnImpl(final String schemaName, final String tableName, final String columnName)
    {
        if (columnName == null)
        {
            throw new NullPointerException("columnName must not be null");
        }
        setColumnName(columnName);
        setTableName(tableName);
        setSchemaName(schemaName);
        if (this.tableName == null)
        {
            this.sqlExpression = this.columnName;
        }
        else
        {
            this.sqlExpression = this.tableName + DOT + this.columnName;
        }
    }

    /**
     * Constructor.
     *
     * @param schemaName the schema name, may be null but not blank.
     * @param tableName the table name, may be null but not blank.
     *        If it contains a dot, then only the portion after the dot
     *        will be taken as table name.
     *        If it contains a dot and schemaName is null, then the schema
     *        name will be set as the portion before the dot.
     * @param columnName the column name, may be null but not blank.
     * @param sqlExpression the SQL expression for the column,
     *        not null or blank.
     *
     * @throws NullPointerException if tableName or sqlExpression are null.
     * @throws IllegalArgumentException if tableName or sqlExpression are blank.
     */
    public ColumnImpl(
            final String schemaName,
            final String tableName,
            final String columnName,
            final String sqlExpression)
    {
        setColumnName(columnName);
        setTableName(tableName);
        setSchemaName(schemaName);
        setSqlExpression(sqlExpression);
    }


    /**
     * Constructor which tries to guess schema, table and column names from
     * an SQL expression. If a schema name can be identified in the
     * SQL expression, it is removed from the SQL expression in the column.
     *
     * @param sqlExpression the SQL expression, not null, not blank.
     *
     * @throws NullPointerException if sqlExpression is null.
     * @throws IllegalArgumentException if table or column name cannot be
     *         guessed from sqlExpression.
     */
    public ColumnImpl(String sqlExpression)
    {
        setSqlExpression(sqlExpression);

        // Find Table.Column
        final int dotIndex = sqlExpression.lastIndexOf(DOT);
        if (dotIndex == -1)
        {
            if (StringUtils.contains(sqlExpression, "*"))
            {
                return;
            }
            if (StringUtils.indexOfAny(sqlExpression, FUNCTION_DELIMITERS)
                    != -1)
            {
                throw new IllegalArgumentException("sqlExpression "
                        + sqlExpression
                        + " is unparseable, it does not contain a dot (.) "
                        + " but function delimiters.");
            }
            setColumnName(sqlExpression);
            return;
        }
        final String pre = sqlExpression.substring(0, dotIndex);
        final String post = sqlExpression.substring(
                dotIndex + 1,
                sqlExpression.length());
        if (StringUtils.isBlank(pre))
        {
            throw new IllegalArgumentException("sqlExpression "
                    + sqlExpression
                    + " is blank before the dot (.)");
        }
        int startIndex = StringUtils.lastIndexOfAny(pre, FUNCTION_DELIMITERS);
        int endIndex = StringUtils.indexOfAny(post, FUNCTION_DELIMITERS);
        if (endIndex < 0)
        {
            endIndex = sqlExpression.length();
        }
        else
        {
            // relative to sqlExpression not to post
            endIndex += dotIndex + 1;
        }

        if (startIndex + 1 == dotIndex)
        {
            throw new IllegalArgumentException("sqlExpression "
                    + sqlExpression
                    + " is blank between the last function delimiter ("
                    + StringUtils.join(FUNCTION_DELIMITERS)
                    + ") and the dot");
        }
        setColumnName(sqlExpression.substring(dotIndex + 1, endIndex));
        // if startIndex == -1 the formula is correct
        String fullTableName
        = sqlExpression.substring(startIndex + 1, dotIndex);
        setTableName(fullTableName);
        if (fullTableName.contains(DOT))
        {
            int fullTableNameDotIndex = fullTableName.lastIndexOf(DOT);
            String extractedSchemaName
            = fullTableName.substring(0, fullTableNameDotIndex);
            setSchemaName(extractedSchemaName);
            StringBuilder sqlExpressionBuilder = new StringBuilder();
            if (startIndex != -1)
            {
                sqlExpressionBuilder.append(
                        sqlExpression.substring(0, startIndex + 1));
            }
            sqlExpressionBuilder.append(getTableName())
            .append(DOT)
            .append(post);
            setSqlExpression(sqlExpressionBuilder.toString());
        }
    }

    /**
     * Returns the column name.
     *
     * @return the column name, may be null.
     */
    @Override
    public String getColumnName()
    {
        return columnName;
    }

    /**
     * Sets the column name, using null if * is passed.
     *
     * @param columnName the column name, not blank.
     */
    private void setColumnName(String columnName)
    {
        if (columnName != null && StringUtils.isBlank(columnName))
        {
            throw new IllegalArgumentException("columnName must not be blank");
        }
        if ("*".equals(columnName))
        {
            this.columnName = null;
        }
        else
        {
            this.columnName = columnName;
        }
    }

    /**
     * Returns the table name.
     *
     * @return the table name, may be null.
     */
    @Override
    public String getTableName()
    {
        return tableName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullTableName()
    {
        if (schemaName != null)
        {
            return schemaName + '.' + tableName;
        }
        return tableName;
    }

    /**
     * Sets the table name.
     * If a table name with schema prefix is passed, then the unqualified table
     * name is used as table name and the schema name will be set to the
     * extracted schema name.
     *
     * @param tableName the table name, not blank, may be null.
     *
     * @throws IllegalArgumentException if tableName is blank or null.
     */
    private void setTableName(String tableName)
    {
        if (tableName != null && StringUtils.isBlank(tableName))
        {
            throw new IllegalArgumentException("tableName must not be blank");
        }
        if (StringUtils.contains(tableName, DOT))
        {
            int dotIndex = tableName.lastIndexOf(DOT);
            this.schemaName = tableName.substring(0, dotIndex);
            this.tableName = tableName.substring(dotIndex + 1);
            return;
        }
        this.tableName = tableName;
    }

    /**
     * Returns the name of any fixed schema prefix for the column's table
     * (if any).
     *
     * @return the schema name, or null if the schema is not known.
     */
    @Override
    public String getSchemaName()
    {
        return schemaName;
    }

    /**
     * Sets the schema name, if a non-null value is passed.
     *
     * @param schemaName the schema name, or null.
     *
     * @throws IllegalArgumentException if schemaName is blank.
     */
    private void setSchemaName(String schemaName)
    {
        if (schemaName == null)
        {
            return;
        }
        if (StringUtils.isBlank(schemaName))
        {
            throw new IllegalArgumentException("schemaName must not be blank");
        }
        this.schemaName = schemaName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSqlExpression()
    {
        return sqlExpression;
    }

    /**
     * Sets the sql expression.
     *
     * @param sqlExpression the sql expression, not null.
     *
     * @throws NullPointerException if sqlExpression is null.
     * @throws IllegalArgumentException if sqlExpression is blank.
     */
    private void setSqlExpression(String sqlExpression)
    {
        if (sqlExpression == null)
        {
            throw new IllegalArgumentException(
                    "sqlExpression must not be null");
        }
        if (StringUtils.isBlank(sqlExpression))
        {
            throw new IllegalArgumentException(
                    "sqlExpression must not be blank");
        }
        this.sqlExpression = sqlExpression;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(sqlExpression)
                .append(columnName)
                .append(tableName)
                .append(schemaName)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj)
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
        ColumnImpl other = (ColumnImpl) obj;
        return new EqualsBuilder()
                .append(sqlExpression, other.sqlExpression)
                .append(columnName, other.columnName)
                .append(tableName, other.tableName)
                .append(schemaName, other.schemaName)
                .isEquals();
    }

    @Override
    public String toString()
    {
        return "ColumnImpl [columnName=" + columnName
                + ", tableName=" + tableName
                + ", schemaName=" + schemaName
                + ", sqlExpression=" + sqlExpression + "]";
    }
}
