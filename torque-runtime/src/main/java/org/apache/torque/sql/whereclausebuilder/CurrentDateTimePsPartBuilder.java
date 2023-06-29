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
import org.apache.torque.sql.Query;
import org.apache.torque.sql.WhereClauseExpression;

import java.util.stream.Stream;

/**
 * A WhereClausePsPartBuilder which handles <code>SqlEnum.CURRENT_DATE</code>
 * and <code>SqlEnum.CURRENT_TIME</code>.
 *
 * @version $Id: CurrentDateTimePsPartBuilder.java 1896195 2021-12-20 17:41:20Z gk $
 */
public class CurrentDateTimePsPartBuilder
extends AbstractWhereClausePsPartBuilder
{
    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatementPart buildPs(
            final WhereClauseExpression whereClauseExpression,
            final boolean ignoreCase,
            final Query query,
            final Adapter adapter)
                    throws TorqueException
    {
        CombinedPreparedStatementPart result
            = new CombinedPreparedStatementPart(
                getObjectOrColumnPsPartBuilder().buildPs(
                        whereClauseExpression.getLValue(),
                        ignoreCase,
                        query,
                        adapter));
        result.appendSql(whereClauseExpression.getOperator().toString());
        result.append(getObjectOrColumnPsPartBuilder().buildPs(
                whereClauseExpression.getRValue(),
                ignoreCase,
                query,
                adapter));
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
        if (Stream.of(SqlEnum.CURRENT_DATE, SqlEnum.CURRENT_TIME, SqlEnum.CURRENT_TIMESTAMP)
                .anyMatch(sqlEnum ->
                        whereClauseExpression.getOperator().equals(sqlEnum)) )
        {
            return true;
        }
        return false;
    }
}
