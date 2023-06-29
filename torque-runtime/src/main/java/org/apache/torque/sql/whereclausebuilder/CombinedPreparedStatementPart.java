package org.apache.torque.sql.whereclausebuilder;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.torque.criteria.PreparedStatementPart;

/**
 * A PreparedStatementPart which consists of a list of other PreparedStatementParts.
 * @version $Id: $
 */
public class CombinedPreparedStatementPart implements PreparedStatementPart
{
    /** The list of PreparedStatementParts of which this PreparedStatementPart is made. */
    private final List<PreparedStatementPart> parts = new ArrayList<>();

    /**
     * Constructor.
     * Constructs an empty CombinedPreparedStatementPart.
     */
    public CombinedPreparedStatementPart()
    {
    }

    /**
     * Constructor.
     * Creates a CombinedPreparedStatementPart which contains the passed PreparedStatementPart
     * as first part.
     *
     * @param toAdd the PreparedStatementPart to add, not null.
     */
    public CombinedPreparedStatementPart(final PreparedStatementPart toAdd)
    {
        append(toAdd);
    }

    /**
     * Adds a PreparedStatementPart to the list of contained PreparedStatementParts.
     *
     * @param toAdd the PreparedStatementPart to add, not null.
     */
    public void append(final PreparedStatementPart toAdd)
    {
        if (toAdd == null)
        {
            throw new NullPointerException("toAdd must not be null");
        }
        parts.add(toAdd);
    }

    /**
     * Adds a PreparedStatementPart to the list of contained PreparedStatementParts,
     * which contains only the given sql.
     *
     * @param sql the sql to add, not null.
     */
    public void appendSql(final String sql)
    {
        parts.add(new NoReplacementsPreparedStatementPart(sql));
    }

    /**
     * Adds a PreparedStatementPart to the list of contained PreparedStatementParts,
     * which contains only the given replacement.
     *
     * @param toAdd the replacement to add, not null.
     */
    public void addPreparedStatementReplacement(final Object toAdd)
    {
        parts.add(new OnlyReplacementsPreparedStatementPart(toAdd));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSqlAsString()
    {
        StringBuilder result = new StringBuilder();
        parts.forEach(part -> result.append(part.getSqlAsString()));
        return result.toString();
    }

    /**
     * Returns the list of prepared statement replacements.
     * The returned list is unmodifiable.
     *
     * @return the list of prepared statement replacements, not null.
     */
    @Override
    public List<Object> getPreparedStatementReplacements()
    {
        List<Object> result = new ArrayList<>();
        parts.forEach(part -> result.addAll(part.getPreparedStatementReplacements()));
        return Collections.unmodifiableList(result);
    }
}
