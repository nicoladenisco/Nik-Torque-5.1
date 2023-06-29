package org.apache.torque.sql;

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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.torque.Column;
import org.apache.torque.criteria.SqlEnum;

/**
 * An order by clause.
 * @version $Id: OrderBy.java 1867515 2019-09-25 15:02:03Z gk $
 *
 */
public class OrderBy implements Serializable
{
    /** SerialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The column to order by. */
    private Column column;

    /** The order to order by (ascending or descending). */
    private SqlEnum order;

    /** Whether case should be ignored for String columns. */
    private boolean ignoreCase = true;

    /**
     * Constructor.
     *
     * @param column the column to order by, not null.
     * @param order the order, either SqlEnum.DESC or SqlEnum.ASC, not null.
     * @param ignoreCase whether case should be ignored for String columns
     *
     * @throws NullPointerException if null is passed.
     * @throws IllegalArgumentException if an unknown order is passed.
     */
    public OrderBy(Column column, SqlEnum order, boolean ignoreCase)
    {
        if (column == null)
        {
            throw new NullPointerException("column is null");
        }
        if (order == null)
        {
            throw new NullPointerException("order is null");
        }
        if (SqlEnum.DESC != order && SqlEnum.ASC != order)
        {
            throw new IllegalArgumentException("unknown order: " + order);
        }
        this.column = column;
        this.order = order;
        this.ignoreCase = ignoreCase;
    }

    /**
     * Returns the column to order by.
     *
     * @return the column to order by, not null.
     */
    public Column getColumn()
    {
        return column;
    }

    /**
     * Returns the order to order by (ASC or DESC).
     *
     * @return the order, either SqlEnum.DESC or SqlEnum.ASC, not null.
     */
    public SqlEnum getOrder()
    {
        return order;
    }

    /**
     * Returns whether case should be ignored for String columns.
     *
     * @return true if case should be ignored for String columns,
     *         false otherwise.
     */
    public boolean isIgnoreCase()
    {
        return ignoreCase;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(this.column.getSqlExpression())
                .append(this.column.getSchemaName())
                .append(this.column.getTableName())
                .append(this.column.getColumnName())
                .append(this.order)
                .append(this.ignoreCase)
                .toHashCode();
    }

    /**
     * Checks whether two orderBy are equal.
     * This is true if and only if the orders are equal and if the contained
     * columns have the same schema name, table name, column name
     * and sql expression.
     *
     * @param obj the object to compare to.
     *
     * @return true if this object is equal to obj, false otherwise.
     */
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
        OrderBy other = (OrderBy) obj;
        return new EqualsBuilder()
                .append(this.column.getSqlExpression(),
                        other.column.getSqlExpression())
                .append(this.column.getSchemaName(),
                        other.column.getSchemaName())
                .append(this.column.getTableName(),
                        other.column.getTableName())
                .append(this.column.getColumnName(),
                        other.column.getColumnName())
                .append(this.order, other.order)
                .append(this.ignoreCase, other.ignoreCase)
                .isEquals();
    }
}
