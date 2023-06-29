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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
/**
 * Modifiable implementation of the PreparedStatementPart interface.
 *
 * @version $Id: PreparedStatementPart.java 1701510 2015-09-06 18:45:05Z tfischer $
 */
public class PreparedStatementPartImpl implements PreparedStatementPart, Serializable
{
    /** Version id for serializing. */
    private static final long serialVersionUID = 1L;

    /**
     * The SQL for the part, not null.
     */
    private final StringBuilder sql = new StringBuilder();

    /**
     * The replacements for the prepared statement, not null.
     */
    private final List<Object> preparedStatementReplacements
        = new ArrayList<>();

    /**
     * Default constructor, creates an empty PreparedStatementPart.
     */
    public PreparedStatementPartImpl()
    {
        // empty
    }

    /**
     * Constructor, creates a pre-filled PreparedStatementPartImpl.
     *
     * @param sql The sql to fill into the sql buffer initially, or null.
     * @param preparedStatementReplacements the prepared statement replacements
     *        to start with, or null.
     */
    public PreparedStatementPartImpl(
            final String sql,
            final Object... preparedStatementReplacements)
    {
        if (!StringUtils.isEmpty(sql))
        {
            this.sql.append(sql);
        }
        if (preparedStatementReplacements != null)
        {
            this.preparedStatementReplacements.addAll(
                    Arrays.asList(preparedStatementReplacements));
        }
    }

    /**
     * Copy-Constructor.
     *
     * @param toCopy the PreparedStatementPart to copy, not null.
     */
    public PreparedStatementPartImpl(final PreparedStatementPart toCopy)
    {
        String sqlAsString = toCopy.getSqlAsString();
        if (!StringUtils.isEmpty(sqlAsString))
        {
            this.sql.append(sqlAsString);
        }
        if (toCopy.getPreparedStatementReplacements() != null)
        {
            this.preparedStatementReplacements.addAll(toCopy.getPreparedStatementReplacements());
        }
    }
    /**
     * Returns the SQL of the part.
     *
     * @return the SQL as mutable StringBuilder, not null.
     */
    public StringBuilder getSql()
    {
        return sql;
    }

    /**
     * Returns the SQL of the part as String.
     *
     * @return the SQL, not null.
     */
    @Override
    public String getSqlAsString()
    {
        return sql.toString();
    }

    /**
     * Returns the list of prepared statement replacements.
     *
     * @return the modifiable list of prepared statement replacements, not null.
     */
    @Override
    public List<Object> getPreparedStatementReplacements()
    {
        return preparedStatementReplacements;
    }

    /**
     * Appends another PreparedStatementPart to this part.
     *
     * @param toAppend the part to append, not null.
     *
     * @return this PreparedStatementPart (with toAppend appended).
     */
    public PreparedStatementPartImpl append(final PreparedStatementPart toAppend)
    {
        sql.append(toAppend.getSqlAsString());
        preparedStatementReplacements.addAll(
                toAppend.getPreparedStatementReplacements());
        return this;
    }

    /**
     * Appends a SqlEnum to this part.
     *
     * @param toAppend the part to append, not null.
     *
     * @return this PreparedStatementPart (with toAppend appended).
     */
    public PreparedStatementPartImpl append(final SqlEnum toAppend)
    {
        sql.append(toAppend);
        return this;
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(sql);
        hashCodeBuilder.append(preparedStatementReplacements);
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
        PreparedStatementPartImpl other = (PreparedStatementPartImpl) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(other.sql, this.sql);
        equalsBuilder.append(
                other.preparedStatementReplacements,
                this.preparedStatementReplacements);
        return equalsBuilder.isEquals();
    }

    @Override
    public String toString()
    {
        return sql + ", preparedStatementReplacements="
                + preparedStatementReplacements;
    }
}
