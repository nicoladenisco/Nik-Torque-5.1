package org.apache.torque.criteria;

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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This class describes an Element in the From-part of a SQL clause.
 * It must contain the name of the database table.
 * It might contain an alias for the table name, a join type
 * a join condition and prepared statement replacements.
 * The class is immutable.
 */
public class FromElement implements Serializable
{
    /** serial Version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * The fromExpression, e.g. a simple table name or a table name
     * or a subquery with an appended alias name.
     */
    private String fromExpression = null;

    /** The type of the join, e.g. JoinType.LEFT_JOIN */
    private JoinType joinType = null;

    /** The join condition, e.g. table_a.id = table_b.a_id */
    private String joinCondition = null;

    /** The replacements which might occur in the fromExpression. */
    private final List<Object> preparedStatementReplacements
        = new ArrayList<>();

    /**
     * Constructor with join type null and joinCondition null.
     *
     * @param tableName the table name, might contain an appended alias name, e.g.
     * 		<p> table_1 </p>
     *      <p> table_1 alias_for_table_1 </p>
     */
    public FromElement(final String tableName)
    {
        this(tableName, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param fromExpression the expression to add to the from clause,
     *        e.g. a simple table name or a table name with an alias,
     * 		<p> table_1 </p>
     *      <p> table_1 alias_for_table_1 </p>
     *      
     * @param joinType the type of the join, e.g. JoinType.LEFT_JOIN,
     *        or null if no excplicit join is wanted
     * @param joinCondition the join condition,
     *        e.g. table_a.id = table_b.a_id,
     *        or null if no explicit join is wanted
     *        (In this case, the join condition is appended to the
     *         whereClause instead)
     */
    public FromElement(
            final String fromExpression,
            final JoinType joinType,
            final String joinCondition)
    {
        this(fromExpression, joinType, joinCondition, null);
    }

    /**
     * Constructor.
     *
     * @param fromExpression the expression to add to the from clause,
     *        e.g. a simple table name or a table name with an alias,
     * 		<p> table_1 </p>
     *      <p> table_1 alias_for_table_1 </p>
     *      
     * @param joinType the type of the join, e.g. JoinType.LEFT_JOIN,
     *        or null if no explicit join is wanted
     * @param joinCondition the join condition,
     *        e.g. table_a.id = table_b.a_id,
     *        or null if no explicit join is wanted
     *        (In this case, the join condition is appended to the
     *         whereClause instead)
     * @param preparedStatementReplacements the replacements for placeholders
     *        which might occur in the fromExpression, may be null.
     */
    public FromElement(
            final String fromExpression,
            final JoinType joinType,
            final String joinCondition,
            final List<Object> preparedStatementReplacements)
    {
        this.fromExpression = fromExpression;
        this.joinType = joinType;
        this.joinCondition = joinCondition;
        if (preparedStatementReplacements != null)
        {
            this.preparedStatementReplacements.addAll(
                    preparedStatementReplacements);
        }
    }

    /**
     * Constructor.
     *
     * @param fromExpression the expression to add to the from clause,
     *        e.g. a simple table name or a table name with an alias,
     * 		<p> table_1 </p>
     *      <p> table_1 alias_for_table_1 </p>
     *      
     * @param joinType the type of the join, e.g. JoinType.LEFT_JOIN,
     *        or null if no explicit join is wanted
     * @param joinCondition the join condition,
     *        e.g. table_a.id = table_b.a_id, not null.
     */
    public FromElement(
            final String fromExpression,
            final JoinType joinType,
            final PreparedStatementPart joinCondition)
    {
        this(fromExpression,
                joinType,
                joinCondition.getSqlAsString(),
                joinCondition.getPreparedStatementReplacements());
    }


    /**
     * Returns the join condition.
     *
     * @return the join condition, e.g. table_a.id = table_b.a_id,
     *         or null if the join is not an explicit join
     */
    public String getJoinCondition()
    {
        return joinCondition;
    }

    /**
     * Returns the join type.
     *
     * @return the type of the join, e.g. JoinType.LEFT_JOIN,
     *         or null if the join is not an explicit join
     */
    public JoinType getJoinType()
    {
        return joinType;
    }

    /**
     * Returns the fromExpression, which might e.g. be a simple table name or
     *         a table name or a subquery with an alias appended.
     *
     * @return the tablename, might contain an appended alias name, e.g.
     * 		<p> table_1 </p>
     *      <p> table_1 alias_for_table_1 </p>
     *
     */
    public String getFromExpression()
    {
        return fromExpression;
    }

    /**
     * Returns the replacements for prepared statement placeholders in the
     * fromExpression.
     *
     * @return the replacements, not null.

     */
    public List<Object> getPreparedStatementReplacements()
    {
        return preparedStatementReplacements;
    }

    /**
     * Returns a SQL representation of the element.
     *
     * @return a SQL representation of the element
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        if (joinType != null)
        {
            result.append(joinType);
        }
        result.append(fromExpression);
        if (joinCondition != null)
        {
            result.append(SqlEnum.ON);
            result.append(joinCondition);
        }
        return result.toString();
    }


    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(joinCondition)
                .append(joinType)
                .append(fromExpression)
                .append(preparedStatementReplacements)
                .toHashCode();
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
        FromElement other = (FromElement) obj;
        return new EqualsBuilder()
                .append(joinCondition, other.joinCondition)
                .append(joinType, other.joinType)
                .append(fromExpression, other.fromExpression)
                .append(preparedStatementReplacements,
                        other.preparedStatementReplacements)
                .isEquals();
    }
}
