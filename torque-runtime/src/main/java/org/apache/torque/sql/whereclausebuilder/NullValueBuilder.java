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

import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.criteria.PreparedStatementPart;
import org.apache.torque.criteria.SqlEnum;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.sql.Query;
import org.apache.torque.sql.WhereClauseExpression;

import java.util.stream.Stream;

/**
 * Builds a PreparedStatementPart from a WhereClauseExpression which
 * rhs is null and which has one of the comparison opertator =, &lt;&gt;, or !=,
 * or which operator is SqlEnum.ISNULL or SqlEnum.ISNOTNULL

 *
 * @version $Id: NullValueBuilder.java 1896195 2021-12-20 17:41:20Z gk $
 */
public class NullValueBuilder extends AbstractWhereClausePsPartBuilder
{
    /**
     * Builds a PreparedStatementPart from a WhereClauseExpression which
     * rhs is null and which has one of the comparison opertator =, &lt;&gt;, or !=.
     *
     * @param whereClausePart the part of the where clause to build.
     *        Can be modified in this method.
     * @param ignoreCase If true and columns represent Strings, the appropriate
     *        function defined for the database will be used to ignore
     *        differences in case.
     * @param query the query which is currently built
     * @param adapter The adapter for the database for which the SQL
     *        should be created, not null.
     *
     * @return the rendered SQL for the WhereClauseExpression
     */
    @Override
    public PreparedStatementPart buildPs(
            final WhereClauseExpression whereClausePart,
            final boolean ignoreCase,
            final Query query,
            final Adapter adapter)
                    throws TorqueException
    {
        CombinedPreparedStatementPart result;
        if (whereClausePart.getOperator().equals(SqlEnum.ISNULL)
                || whereClausePart.getOperator().equals(SqlEnum.ISNOTNULL))
        {
            result = new CombinedPreparedStatementPart(
                    getObjectOrColumnPsPartBuilder().buildPs(
                            whereClausePart.getLValue(),
                            ignoreCase,
                            query,
                            adapter));
            result.appendSql(whereClausePart.getOperator().toString());
            return result;
        }

        // now we know from isApplicable() that rValue is null or is
        // an ObjectKey containing null
        if (whereClausePart.getOperator().equals(SqlEnum.EQUAL))
        {
            result = new CombinedPreparedStatementPart(
                    getObjectOrColumnPsPartBuilder().buildPs(
                            whereClausePart.getLValue(),
                            ignoreCase,
                            query,
                            adapter));
            result.appendSql(SqlEnum.ISNULL.toString());
            return result;
        }
        if (whereClausePart.getOperator().equals(SqlEnum.NOT_EQUAL)
                || whereClausePart.getOperator().equals(
                        SqlEnum.ALT_NOT_EQUAL))
        {
            result = new CombinedPreparedStatementPart(
                    getObjectOrColumnPsPartBuilder().buildPs(
                            whereClausePart.getLValue(),
                            ignoreCase,
                            query,
                            adapter));
            result.appendSql(SqlEnum.ISNOTNULL.toString());
            return result;
        }
        throw new IllegalStateException("unknown operator "
                + whereClausePart.getOperator());
    }

    /**
     * Returns whether this WhereClausePsPartBuilder is applicable for
     * a given WhereClauseExpression.
     *
     * @param whereClauseExpression the WhereClauseExpression in question.
     * @param adapter The adapter for the database for which the SQL
     *        should be created, not null.
     *
     * @return true if applicable, false otherwise.
     */
    @Override
    public boolean isApplicable(
            final WhereClauseExpression whereClauseExpression,
            final Adapter adapter)
    {
        if (whereClauseExpression.getOperator().equals(SqlEnum.ISNULL)
                || whereClauseExpression.getOperator().equals(SqlEnum.ISNOTNULL))
        {
            return true;
        }
        Object rValue = whereClauseExpression.getRValue();
        if (rValue != null
                && (!(rValue instanceof ObjectKey)
                        || ((ObjectKey<?>) rValue).getValue() != null))
        {
            return false;
        }
        if (Stream.of(SqlEnum.EQUAL, SqlEnum.NOT_EQUAL, SqlEnum.ALT_NOT_EQUAL)
                .anyMatch(sqlEnum ->
                        whereClauseExpression.getOperator().equals(sqlEnum)))
        {
            return true;
        }
        return false;
    }
}
