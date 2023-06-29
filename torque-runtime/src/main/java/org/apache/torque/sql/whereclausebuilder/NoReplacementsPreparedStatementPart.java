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

import java.util.Collections;
import java.util.List;

import org.apache.torque.criteria.PreparedStatementPart;

/**
 * A PreparedStatementPart which only contains SQL and no replacements.
 *
 * @version $Id: $
 */
public class NoReplacementsPreparedStatementPart implements PreparedStatementPart
{
    /** The contained sql. */
    private final String sql;

    /**
     * Constructor.
     *
     * @param sql the contained sql.
     */
    public NoReplacementsPreparedStatementPart(final String sql)
    {
        this.sql = sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSqlAsString()
    {
        return sql;
    }

    /**
     * Returns the empty list of prepared statement replacements.
     * The returned list is unmodifiable.
     *
     * @return the list of prepared statement replacements, not null.
     */
    @Override
    public List<Object> getPreparedStatementReplacements()
    {
        return Collections.emptyList();
    }
}
