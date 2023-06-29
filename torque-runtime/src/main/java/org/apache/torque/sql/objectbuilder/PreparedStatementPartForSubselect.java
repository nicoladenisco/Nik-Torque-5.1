package org.apache.torque.sql.objectbuilder;

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

import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.TorqueRuntimeException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.criteria.FromElement;
import org.apache.torque.criteria.PreparedStatementPart;
import org.apache.torque.criteria.PreparedStatementPartImpl;
import org.apache.torque.sql.Query;
import org.apache.torque.sql.SqlBuilder;
import org.apache.torque.util.UniqueList;

/**
 * A PreparedStatementPart which encapsulates a subselect.
 * The SQL and Replacements are not calculated immediately,
 * but wait for the outer clause to be completed,
 * as tables in the from clause which reference tables in the outer select
 * are removed, and this can only be done when the outer query is known.
 * This only works if the methofs getSqlAsString()
 * and getPreparedStatementReplacements() are called after the outer query
 * is calculated.
 *
 * @version $Id: $
 */
public class PreparedStatementPartForSubselect implements PreparedStatementPart
{
    /**
     *  The calculated PreparedStatementPart for the subselect,
     *  or null if it is not yet known.
     */
    private PreparedStatementPartImpl wrapped;

    /** The criteria to build the subselect from. */
    private final Criteria toBuildFrom;

    /** The outer query in which this subselect is embedded. */
    private final Query outerQuery;

    /**
     * Constructor.
     *
     * @param toBuildFrom The criteria to build the subselect from.
     * @param outerQuery The outer query in which this subselect is embedded.
     */
    public PreparedStatementPartForSubselect(final Criteria toBuildFrom, final Query outerQuery)
    {
        this.toBuildFrom = toBuildFrom;
        this.outerQuery = outerQuery;
    }

    private void calculate()
    {
        Query subquery;
        try
        {
            subquery = SqlBuilder.buildQuery(toBuildFrom);
        }
        catch (TorqueException e)
        {
            throw new TorqueRuntimeException(e);
        }
        // assume that table names which are in the outer from clause are references to the outer from clause
        // But only do this if any tables are remaining
        UniqueList<FromElement> remainingTableNames = new UniqueList<>(subquery.getFromClause());
        remainingTableNames.removeAll(outerQuery.getFromClause());
        if (remainingTableNames.size() > 0)
        {
            subquery.getFromClause().removeAll(outerQuery.getFromClause());
        }

        PreparedStatementPartImpl result = new PreparedStatementPartImpl("(" + subquery.toString() + ")");
        result.getPreparedStatementReplacements().addAll(
                subquery.getPreparedStatementReplacements());
        wrapped = result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSqlAsString()
    {
        if (wrapped == null)
        {
            calculate();
        }
        return wrapped.getSqlAsString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getPreparedStatementReplacements()
    {
        if (wrapped == null)
        {
            calculate();
        }
        return wrapped.getPreparedStatementReplacements();
    }
}
