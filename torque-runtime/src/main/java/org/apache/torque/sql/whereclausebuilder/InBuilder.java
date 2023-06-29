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

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.criteria.Criterion;
import org.apache.torque.criteria.PreparedStatementPart;
import org.apache.torque.criteria.SqlEnum;
import org.apache.torque.sql.Query;
import org.apache.torque.sql.WhereClauseExpression;

/**
 * Builds a PreparedStatementPart from a WhereClauseExpression containing
 * a Like operator.
 *
 * @version $Id: InBuilder.java 1867515 2019-09-25 15:02:03Z gk $
 */
public class InBuilder extends AbstractWhereClausePsPartBuilder
{
    /**
     * Takes a columnName and criteria and
     * builds a SQL 'IN' expression taking into account the ignoreCase
     * flag.
     *
     * @param whereClausePart the part of the where clause to build.
     *        Can be modified in this method.
     * @param ignoreCase If true and columns represent Strings, the appropriate
     *        function defined for the database will be used to ignore
     *        differences in case.
     * @param query the query which is currently built
     * @param adapter The adapter for the database for which the SQL
     *        should be created, not null.
     */
    @Override
    public PreparedStatementPart buildPs(
            final WhereClauseExpression whereClausePart,
            final boolean ignoreCase,
            final Query query,
            final Adapter adapter)
                    throws TorqueException
    {
        CombinedPreparedStatementPart result = new CombinedPreparedStatementPart();

        boolean ignoreCaseApplied = false;
        List<String> inClause = new ArrayList<>();
        boolean nullContained = false;
        if (whereClausePart.getRValue() instanceof Iterable)
        {
            for (Object listValue : (Iterable<?>) whereClausePart.getRValue())
            {
                if (listValue == null)
                {
                    nullContained = true;
                    continue;
                }
                result.addPreparedStatementReplacement(listValue);
                if (ignoreCase && listValue instanceof String)
                {
                    inClause.add(adapter.ignoreCase("?"));
                    ignoreCaseApplied = true;
                }
                else
                {
                    inClause.add("?");
                }
            }
        }
        else if (whereClausePart.getRValue().getClass().isArray())
        {
            for (Object arrayValue : (Object[]) whereClausePart.getRValue())
            {
                if (arrayValue == null)
                {
                    nullContained = true;
                    continue;
                }
                result.addPreparedStatementReplacement(arrayValue);
                if (ignoreCase && arrayValue instanceof String)
                {
                    inClause.add(adapter.ignoreCase("?"));
                    ignoreCaseApplied = true;
                }
                else
                {
                    inClause.add("?");
                }
            }
        }
        else
        {
            throw new IllegalArgumentException(
                    "Unknown rValue type "
                            + whereClausePart.getRValue().getClass().getName()
                            + ". rValue must be an instance of "
                            + " Iterable or Array");
        }

        if (nullContained)
        {
            result.appendSql("(");
        }

        result.append(getObjectOrColumnPsPartBuilder().buildPs(
                whereClausePart.getLValue(),
                ignoreCaseApplied,
                query,
                adapter));

        result.appendSql(whereClausePart.getOperator().toString()
                + '('
                + StringUtils.join(inClause.iterator(), ",")
                + ')');
        if (nullContained)
        {
            if (whereClausePart.getOperator() == SqlEnum.IN)
            {
                result.appendSql(Criterion.OR);
                result.append(getObjectOrColumnPsPartBuilder().buildPs(
                        whereClausePart.getLValue(),
                        false,
                        query,
                        adapter));
                result.appendSql(SqlEnum.ISNULL.toString());
            }
            else if (whereClausePart.getOperator() == SqlEnum.NOT_IN)
            {
                result.appendSql(Criterion.AND);
                result.append(getObjectOrColumnPsPartBuilder().buildPs(
                        whereClausePart.getLValue(),
                        false,
                        query,
                        adapter));
                result.appendSql(SqlEnum.ISNOTNULL.toString());
            }
            result.appendSql(")");
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(
            final WhereClauseExpression whereClauseExpression,
            final Adapter adapter)
    {
        if (whereClauseExpression.getOperator().equals(Criteria.IN)
                || whereClauseExpression.getOperator().equals(Criteria.NOT_IN))
        {
            if (!(whereClauseExpression.getRValue() instanceof Criteria))
            {
                return true;
            }
        }
        return false;
    }
}
