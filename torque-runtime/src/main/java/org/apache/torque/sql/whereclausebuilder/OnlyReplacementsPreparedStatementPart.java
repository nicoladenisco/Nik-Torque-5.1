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
import java.util.List;

import org.apache.torque.criteria.PreparedStatementPart;

/**
 * A PreparedStatementPart which only contains replacements, no sql.
 *
 * @version $Id: $
 */
public class OnlyReplacementsPreparedStatementPart implements PreparedStatementPart
{
    /** The contained replacements. */
    private final List<Object> replacements = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param replacement the first replacement to be contained in this PreparedStatementPart.
     */
    public OnlyReplacementsPreparedStatementPart(final Object replacement)
    {
        replacements.add(replacement);
    }

    /**
     * returns the empty sql.
     *
     * @return the empty sql, not null.
     */
    @Override
    public String getSqlAsString()
    {
        return "";
    }

    /**
     * Returns the list of prepared statement replacements.
     * The returned list is modifiable.
     * On modification of the returned list, the state of this object is changed.
     *
     * @return the list of prepared statement replacements, not null.
     */
    @Override
    public List<Object> getPreparedStatementReplacements()
    {
        return replacements;
    }
}
