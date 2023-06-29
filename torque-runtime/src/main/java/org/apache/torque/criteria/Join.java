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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.torque.Column;

/**
 * Data object to describe a join between two tables, for example
 * <pre>
 * table_a LEFT JOIN table_b ON table_a.id = table_b.a_id
 * </pre>
 */
public class Join implements Serializable
{
    /** Version id for serializing. */
    private static final long serialVersionUID = 1L;

    /** The join condition, not null. */
    private Criterion joinCondition = null;

    /**
     * The left table of the join,
     * or null to be determined from join condition.
     */
    private PreparedStatementPart leftTable = null;

    /**
     * The right table of the join,
     * or null to be determined from join condition.
     */
    private PreparedStatementPart rightTable = null;
    /**
     * The type of the join (LEFT JOIN, ...),
     * or null for an implicit inner join.
     */
    private JoinType joinType = null;

    /**
     * Constructor with the comparison operator.
     *
     * @param leftColumn the left column of the join condition;
     *        might contain an alias name, not null.
     * @param rightColumn the right column of the join condition
     *        might contain an alias name, not null.
     * @param comparison the comparison, not null.
     *        The operator CUSTOM is not supported.
     * @param joinType the type of the join, or null
     *        (adding the join condition to the where clause).
     *
     * @throws NullPointerException if leftColumn, comparison or rightColumn
     *         are null.
     * @throws IllegalArgumentException if comparison id SqlEnum.CUSTOM
     */
    public Join(
            final Column leftColumn,
            final Column rightColumn,
            final SqlEnum comparison,
            final JoinType joinType)
    {
        if (leftColumn == null)
        {
            throw new NullPointerException("leftColumn is null");
        }
        if (rightColumn == null)
        {
            throw new NullPointerException("rightColumn is null");
        }
        if (comparison == null)
        {
            throw new NullPointerException("comparison is null");
        }
        this.joinCondition = new Criterion(leftColumn, rightColumn, comparison);
        this.joinType = joinType;
    }

    /**
     * Constructor.
     *
     * @param leftTable the left table of the join, might contain an alias name,
     *        or null to be determined from the join clause.
     * @param rightTable the right table of the join, might contain an alias
     *        name, or null to be determined from the join clause.
     * @param joinCondition the join condition, not null.
     * @param joinType the type of the join, or null
     *        (adding the join condition to the where clause).
     */
    public Join(
            final PreparedStatementPart leftTable,
            final PreparedStatementPart rightTable,
            final Criterion joinCondition,
            final JoinType joinType)
    {
        if (joinCondition == null)
        {
            throw new NullPointerException("joinCondition is null");
        }
        this.leftTable = leftTable;
        this.rightTable = rightTable;
        this.joinCondition = joinCondition;
        this.joinType = joinType;
    }

    /**
     * @return the type of the join, i.e. SqlEnum.LEFT_JOIN, ...,
     *         or null for adding the join condition to the where Clause
     */
    public final Criterion getJoinCondition()
    {
        return joinCondition;
    }

    /**
     * @return the type of the join, i.e. SqlEnum.LEFT_JOIN, ...,
     *         or null for adding the join condition to the where Clause
     */
    public final JoinType getJoinType()
    {
        return joinType;
    }

    /**
     * @return the left table of the join condition.
     */
    public final PreparedStatementPart getLeftTable()
    {
        return leftTable;
    }

    /**
     * @return the right table of the join condition.
     */
    public final PreparedStatementPart getRightTable()
    {
        return rightTable;
    }

    /**
     * Returns a String representation of the class,
     * mainly for debugging purposes.
     *
     * @return a String representation of the class
     */
    @Override
    public String toString()
    {

        return joinType + "(" + leftTable + ", " + rightTable + "): "
                + joinCondition.toString();
    }

    /**
     * This method checks another Criteria.Join to see if they contain the
     * same attributes.
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if ((obj == null) || !(obj instanceof Join))
        {
            return false;
        }

        Join join = (Join) obj;
        return new EqualsBuilder()
                .append(leftTable, join.leftTable)
                .append(rightTable, join.rightTable)
                .append(joinCondition, join.joinCondition)
                .append(joinType, join.getJoinType())
                .isEquals();
    }

    /**
     * Returns the hash code value for this Join.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(leftTable)
                .append(rightTable)
                .append(joinCondition)
                .append(joinType)
                .toHashCode();
    }
}
