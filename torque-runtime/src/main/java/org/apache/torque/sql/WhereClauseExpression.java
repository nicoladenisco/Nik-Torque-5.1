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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.torque.criteria.SqlEnum;

/**
 * The raw values for a part of the where clause of a SQL statement,
 * either of the form lValue operator rValue, e.g. author.author_id = 1,
 * or in form of a custom sql query with sql and replacement values.
 *
 * @version $Id: WhereClauseExpression.java 1867515 2019-09-25 15:02:03Z gk $
 */
public class WhereClauseExpression
{
    /**
     * The value on the left hand side of the operator, not null.
     */
    private Object lValue;

    /**
     * The operator.
     */
    private SqlEnum operator;

    /**
     * The value on the right hand side of the operator.
     */
    private Object rValue;

    /** A verbatim SQL for this Criterion. */
    private final String sql;

    /**
     * Replacements for the placeholders in the verbatim SQL.
     * Is only used if sql is not null.
     */
    private final Object[] preparedStatementReplacements;

    /**
     * Constructor.
     *
     * @param lValue The value on the left hand side of the operator of the
     *        expression. The value represents the name of a database column.
     * @param operator the operator. Either this parameter or sql must be
     *        not null.
     * @param rValue The value on the right hand side of the operator of the
     *        expression. The value represents the name of a database column.
     * @param sql a verbatim sql condition. Either this parameter or
     *        operator must be not null.
     * @param preparedStatementReplacements Values for the placeholders
     *        in the verbatim sql condition.
     */
    public WhereClauseExpression(
            final Object lValue,
            final SqlEnum operator,
            final Object rValue,
            final String sql,
            final Object[] preparedStatementReplacements)
    {
        if (operator != null
                && (sql != null || preparedStatementReplacements != null))
        {
            throw new IllegalArgumentException("Either operator or "
                    + "some of (sql, preparedStatementReplacements) "
                    + "can be not null, not both");
        }
        if ((lValue == null || operator == null)
                && (sql == null))
        {
            throw new IllegalArgumentException("Either the values"
                    + "(lValue, comparison) or "
                    + "sql must be not null");
        }
        this.lValue = lValue;
        this.operator = operator;
        this.rValue = rValue;
        this.sql = sql;
        this.preparedStatementReplacements = preparedStatementReplacements;
    }

    /**
     * Returns the value on the left hand side of the operator of the
     * expression.
     *
     * @return the lValue.
     */
    public Object getLValue()
    {
        return lValue;
    }

    /**
     * Sets the value on the left hand side of the operator of the
     * expression. The value represents the name of a database column.
     *
     * @param lValue the value to set, not null or empty.
     *
     * @throws IllegalArgumentException if lValue is null or empty.
     */
    public void setLValue(final Object lValue)
    {
        this.lValue = lValue;
    }

    /**
     * Returns the value on the operator of the expression.
     *
     * @return the operator, or null if this Expression represents a verbatim
     *         sql expression.
     */
    public SqlEnum getOperator()
    {
        return operator;
    }

    /**
     * Sets the value on the operator of the expression.
     *
     * @param operator the value to set, or null fo no operator.
     */
    public void setOperator(final SqlEnum operator)
    {
        this.operator = operator;
    }

    /**
     * Returns the value on the right hand side of the operator of the
     * expression.
     *
     * @return the rValue, or null.
     */
    public Object getRValue()
    {
        return rValue;
    }

    /**
     * Sets the value on the right hand side of the operator of the
     * expression.
     *
     * @param rValue the value to set, or null for the empty String.
     */
    public void setRValue(final Object rValue)
    {
        this.rValue = rValue;
    }

    /**
     * Returns the verbatim sql for this expression, if any.
     *
     * @return the verbatim sql for this expression, or null if not given.
     */
    public String getSql()
    {
        return sql;
    }

    /**
     * Returns the values for the placeholders in the verbatim sql condition.
     *
     * @return the placeholder values, or null.
     */
    public Object[] getPreparedStatementReplacements()
    {
        return preparedStatementReplacements;
    }

    /**
     * Returns whether this expression represents a verbatim sql condition.
     *
     * @return true if  this Criterion represents a verbatim sql condition,
     *         false if the sql is computed from lValue, comparison and rValue.
     */
    public boolean isVerbatimSqlCondition()
    {
        return (sql != null);
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder()
                .append(lValue)
                .append(operator)
                .append(rValue)
                .append(sql)
                .append(preparedStatementReplacements);
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
        WhereClauseExpression other = (WhereClauseExpression) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder()
                .append(lValue, other.lValue)
                .append(operator, other.operator)
                .append(rValue, other.rValue)
                .append(sql, other.sql)
                .append(
                        preparedStatementReplacements,
                        other.preparedStatementReplacements);
        return equalsBuilder.isEquals();
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder(lValue.toString());
        if (operator != null)
        {
            result.append(operator);
        }
        result.append(rValue);
        return result.toString();
    }
}
