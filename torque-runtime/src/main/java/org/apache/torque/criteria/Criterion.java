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
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.torque.Column;

/**
 * Describes one or more where clause parts in the Criteria.
 * Either the parts list is not null and represents this criterion
 * or the column, value, comparison and ignoreStringCase columns
 * are not null and represent this criterion.
 */
public class Criterion implements Serializable
{
    /** Serial version. */
    private static final long serialVersionUID = 7157097965404611710L;

    /** Constant for the operator " AND ". */
    public static final String AND = " AND ";

    /** Constant for the operator " OR ". */
    public static final String OR = " OR ";

    /**
     * Left-hand-side value of the comparison, may be null.
     * If this object implements the Column interface, it is interpreted as
     * a value computed by the database, otherwise as verbatim value.
     */
    private Object lValue;

    /** Comparison operator. Can only be null if sql is not null. */
    private SqlEnum comparison;

    /**
     * Right-hand-side value of the comparison, may be null.
     * If this object implements the Column interface, it is interpreted as
     * a value computed by the database, otherwise as verbatim value.
     */
    private Object rValue;

    /** A verbatim SQL for this Criterion. */
    private String sql;

    /**
     * Replacements for the placeholders in the verbatim SQL.
     * Is only used if sql is not null.
     */
    private Object[] preparedStatementReplacements;

    /** Flag to ignore case in comparison */
    private boolean ignoreCase = false;

    /**
     * The criterion objects which form a composite criterion.
     * Either this list is not null and represents this criterion
     * or the column, value, comparison and ignoreStringCase columns
     * are not null and represent this criterion.
     */
    private List<Criterion> parts;

    /**
     * The operator (AND, OR...) how the composite criterions
     * are connected.
     */
    private String conjunction;


    /**
     * Create a new instance.
     * Either this Criterion represents a comparison without verbatim SQL;
     * in this case the parameters lValue and comparison must be not null,
     * rValue may be not null and sql and preparedStatementReplacements must
     * be null; or it represents a verbatim sql condition, in which case
     * the parameter comparison must be null and the sql must be not null
     * (preparedStatementReplacements may be set to contain sql placeholder
     * replacement values, and lValue and rValue can be set to add columns
     * to the automatically computed from clause of the query).
     *
     * @param lValue the left hand side value of the comparison.
     *        If this value should be a value from the database,
     *        the object must implement the
     *        <code>org.apache.torque.Column</code> interface.
     * @param rValue the right hand side value of the comparison.
     *        If this value should be a value from the database,
     *        the object must implement the
     *        <code>org.apache.torque.Column</code> interface.
     * @param comparison The comparison operator. Either this parameter or
     *        sql must be not null.
     * @param sql a verbatim sql condition. Either this parameter or
     *        comparison must be not null.
     * @param preparedStatementReplacements Values for the placeholders
     *        in the verbatim sql condition.
     *
     * @throws NullPointerException if column is null.
     */
    public Criterion(
            final Object lValue,
            final Object rValue,
            final SqlEnum comparison,
            final String sql,
            final Object[] preparedStatementReplacements)
    {
        if (comparison != null
                && (sql != null || preparedStatementReplacements != null))
        {
            throw new IllegalArgumentException("Either comparison or "
                    + "some of (sql, preparedStatementReplacements) "
                    + "can be not null, not both");
        }
        if ((lValue == null || comparison == null)
                && (sql == null))
        {
            throw new IllegalArgumentException("Either the values"
                    + "(lValue, comparison) or "
                    + "sql must be not null");
        }
        this.lValue = lValue;
        this.comparison = comparison;
        this.rValue = rValue;
        this.sql = sql;
        this.preparedStatementReplacements = preparedStatementReplacements;
    }

    /**
     * Create a new instance without verbatim sql, using equals as
     * comparison operator.
     *
     * @param lValue the left hand side value of the comparison, not null.
     *        If this value should be a value from the database,
     *        the object must implement the
     *        <code>org.apache.torque.Column</code> interface.
     * @param rValue the right hand side value of the comparison.
     *        If this value should be a value from the database,
     *        the object must implement the
     *        <code>org.apache.torque.Column</code> interface.
     */
    public Criterion(final Object lValue, final Object rValue)
    {
        this(lValue, rValue, Criteria.EQUAL, null, null);
    }

    /**
     * Create a new instance without verbatim sql.
     *
     * @param lValue the left hand side value of the comparison, not null.
     *        If this value should be a value from the database,
     *        the object must implement the
     *        <code>org.apache.torque.Column</code> interface.
     * @param rValue the right hand side value of the comparison.
     *        If this value should be a value from the database,
     *        the object must implement the
     *        <code>org.apache.torque.Column</code> interface.
     * @param comparison The comparison operator, not null.
     */
    public Criterion(final Object lValue, final Object rValue, final SqlEnum comparison)
    {
        this(lValue, rValue, comparison, null, null);
    }

    /**
     * Creates a shallow copy of the given Criterion.
     *
     * @param toCopy the Criterion to copy from, not null.
     */
    public Criterion(final Criterion toCopy)
    {
        this.lValue = toCopy.lValue;
        this.comparison = toCopy.comparison;
        this.rValue = toCopy.rValue;
        this.sql = toCopy.sql;
        this.preparedStatementReplacements
        = toCopy.preparedStatementReplacements;
        this.ignoreCase = toCopy.ignoreCase;
        if (toCopy.parts != null)
        {
            this.parts = new ArrayList<>(toCopy.parts.size());
            toCopy.parts.forEach(part -> this.parts.add(new Criterion(part)));
        }
        this.conjunction = toCopy.conjunction;
    }

    /**
     * Get the left hand side value of the comparison.
     *
     * @return the left hand side value of the comparison.
     *         If this value is a value computed by the database,
     *         the object implements the
     *         <code>org.apache.torque.Column</code> interface.
     */
    public Object getLValue()
    {
        return this.lValue;
    }

    /**
     * Set the left hand side value of the comparison.
     *
     * @param lValue the left hand side value of the comparison.
     *        If this value is a value computed by the database,
     *        the object must implement the
     *        <code>org.apache.torque.Column</code> interface.
     */
    public void setLValue(final Object lValue)
    {
        if (isComposite())
        {
            throw new IllegalStateException(
                    "cannot set lValue for a composite Criterion");
        }
        this.lValue = lValue;
    }

    /**
     * Get the comparison.
     *
     * @return A String with the comparison, or null if this
     *         Criterion represents a verbatim sql condition.
     */
    public SqlEnum getComparison()
    {
        return this.comparison;
    }

    /**
     * Get the right hand side value of the comparison.
     *
     * @return the right hand side value of the comparison.
     *         If this value is a value computed by the database,
     *         the object implements the
     *         <code>org.apache.torque.Column</code> interface.
     */
    public Object getRValue()
    {
        return this.rValue;
    }

    /**
     * Set the right hand side value of the comparison.
     *
     * @param rValue the right hand side value of the comparison.
     *         If this value is a value computed by the database,
     *         the object must implement the
     *         <code>org.apache.torque.Column</code> interface.
     */
    public void setRValue(final Object rValue)
    {
        if (isComposite())
        {
            throw new IllegalStateException(
                    "cannot set rValue for a composite Criterion");
        }
        this.rValue = rValue;
    }

    /**
     * Returns the verbatim sql for this condition.
     *
     * @return the verbatim sql for this condition, or null if this
     *         Criterion does not represent a verbatim sql condition.
     */
    public String getSql()
    {
        return sql;
    }

    /**
     * Returns the prepared statement replacements for a verbatim sql condition.
     *
     * @return the replacement values, or null.
     */
    public Object[] getPreparedStatementReplacements()
    {
        return preparedStatementReplacements;
    }

    /**
     * Returns whether this Criterion represents a verbatim sql condition.
     *
     * @return true if  this Criterion represents a verbatim sql condition,
     *         false if the sql is computed from lValue, comparison and rValue.
     */
    public boolean isVerbatimSqlCondition()
    {
        return (sql != null);
    }

    /**
     * Sets ignore case. ignoreCase is ignored for a verbatim sql statement.
     *
     * @param b True if case should be ignored.
     * @return A modified Criterion object.
     */
    public Criterion setIgnoreCase(final boolean b)
    {
        if (isComposite())
        {
            throw new IllegalStateException(
                    "cannot set ignoreCase for a composite Criterion");
        }
        ignoreCase = b;
        return this;
    }

    /**
     * Is ignore case on or off?
     *
     * @return True if case is ignored.
     */
    public boolean isIgnoreCase()
    {
        return ignoreCase;
    }

    /**
     * Returns the parts of which this criterion consists.
     *
     * @return an unmodifiable list of the clauses,
     *         or null if this criterion is not a composite criterion.
     */
    public List<Criterion> getParts()
    {
        if (parts == null)
        {
            return null;
        }
        return Collections.unmodifiableList(parts);
    }

    /**
     * Returns the conjunction for the parts of this criterion
     *
     * @return the conjunction, or null if this criterion is not a
     *         composite criterion.
     */
    public String getConjunction()
    {
        return conjunction;
    }

    /**
     * Returns whether this criterion is a composite criterion.
     *
     * @return true if this criterion is a composite criterion,
     *         false if it represents a single condition.
     */
    public boolean isComposite()
    {
        return parts != null;
    }

    /**
     * Replaces this criterion's condition with
     * (this criterion's condition AND criterion).
     *
     * @param criterion the criterion to and with this criterion,
     *        not null.
     *        
     * @return the modified Criteria object.
     *
     */
    public Criterion and(final Criterion criterion)
    {
        addCompositeCriterion(criterion, AND);
        return this;
    }

    /**
     * Replaces this criterion's condition with
     * (this criterion's condition OR criterion).
     *
     * @param criterion the criterion to and with this criterion,
     *        not null.
     *        
     * @return the modified Criteria object.
     */
    public Criterion or(final Criterion criterion)
    {
        addCompositeCriterion(criterion, OR);
        return this;
    }

    /**
     * Add a composite criterion to this criterion.
     *
     * @param criterion the criterion to add, not null.
     * @param conjunction the conjunction by which to add the criterion,
     *        not null.
     *
     * @throws NullPointerException if criterion is null.
     */
    private void addCompositeCriterion(
            final Criterion criterion,
            final String conjunction)
    {
        if (criterion == null)
        {
            throw new NullPointerException("criterion must not be null");
        }
        if (isComposite() && this.conjunction.equals(conjunction))
        {
            parts.add(new Criterion(criterion));
        }
        else
        {
            Criterion copy = new Criterion(this);
            parts = new ArrayList<>();
            parts.add(copy);
            parts.add(new Criterion(criterion));
            this.conjunction = conjunction;
            this.rValue = null;
            this.comparison = null;
            this.lValue = null;
            this.sql = null;
            this.preparedStatementReplacements = null;
            this.ignoreCase = false;
        }
    }

    /**
     * Appends a debug String representation of the Criterion
     * onto the String builder.
     * 
     * @param sb the string representation
     */
    public void appendTo(final StringBuilder sb)
    {
        if (isComposite())
        {
            boolean first = true;
            for (Criterion part : parts)
            {
                if (!first)
                {
                    sb.append(conjunction);
                }
                if (part.isComposite())
                {
                    sb.append('(');
                }
                part.appendTo(sb);
                if (part.isComposite())
                {
                    sb.append(')');
                }
                first = false;
            }
        }
        else
        {
            if (isVerbatimSqlCondition())
            {
                sb.append(sql);
            }
            else
            {
                String lValueDisplay;
                if (lValue instanceof Column)
                {
                    lValueDisplay = ((Column) lValue).getSqlExpression();
                }
                else if (lValue != null)
                {
                    lValueDisplay = lValue.toString();
                }
                else
                {
                    lValueDisplay = "";
                }
                String rValueDisplay;
                if (rValue instanceof Column)
                {
                    rValueDisplay = ((Column) rValue).getSqlExpression();
                }
                else if (rValue != null)
                {
                    rValueDisplay = rValue.toString();
                }
                else
                {
                    rValueDisplay = "";
                }
                sb.append(lValueDisplay)
                .append(comparison)
                .append(rValueDisplay);
            }
        }
    }

    /**
     * Build a string representation of the Criterion for debug purposes.
     *
     * @return A String with the representation of the Criterion.
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        appendTo(builder);
        return builder.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     * 
     * This method checks another Criteria.Criterion to see if they contain
     * the same attributes.
     */
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
        if (obj.getClass() != this.getClass())
        {
            return false;
        }

        Criterion criterion = (Criterion) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(criterion.lValue, this.lValue)
        .append(criterion.comparison, this.comparison)
        .append(criterion.rValue, this.rValue)
        .append(criterion.sql, this.sql)
        .append(criterion.preparedStatementReplacements,
                this.preparedStatementReplacements)
        .append(criterion.ignoreCase, this.ignoreCase)
        .append(criterion.parts, this.parts)
        .append(criterion.conjunction, this.conjunction);
        return equalsBuilder.isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     * 
     * Returns a hash code value for the object.
     */
    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(this.lValue)
        .append(this.comparison)
        .append(this.rValue)
        .append(this.sql)
        .append(this.preparedStatementReplacements)
        .append(this.ignoreCase)
        .append(this.parts)
        .append(this.conjunction);
        return hashCodeBuilder.toHashCode();
    }
}

