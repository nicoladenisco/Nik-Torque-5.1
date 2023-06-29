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

import org.apache.torque.Column;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.criteria.PreparedStatementPart;
import org.apache.torque.criteria.PreparedStatementPartImpl;
import org.apache.torque.criteria.SqlEnum;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.sql.Query;

/**
 * Builds a PreparedStatementPart from a column or single value.
 *
 * @version $Id: ObjectOrColumnPsPartBuilder.java 1849379 2018-12-20 12:33:43Z tv $
 */
public class ObjectOrColumnPsPartBuilder implements ObjectPsPartBuilder
{
    /**
     * Builds a PreparedStatementPart from a column or single value.
     *
     * @param toBuildFrom the object to build the psPart from.
     * @param ignoreCase If true and columns represent Strings, the appropriate
     *        function defined for the database will be used to ignore
     *        differences in case.
     * @param query the query which is currently built
     * @param adapter The adapter for the database for which the SQL
     *        should be created, not null.
     *
     * @return the PreparedStatementPart for the object.
     *
     * @throws TorqueException when rendering fails.
     */
    @Override
    public PreparedStatementPart buildPs(
            Object toBuildFrom,
            final boolean ignoreCase,
            final Query query,
            final Adapter adapter)
                    throws TorqueException
    {
        PreparedStatementPartImpl result = new PreparedStatementPartImpl();
        // check column
        if (toBuildFrom instanceof Column)
        {
            Column column = (Column) toBuildFrom;
            if (ignoreCase)
            {
                result.getSql().append(adapter.ignoreCase(column.getSqlExpression()));
            }
            else
            {
                result.getSql().append(column.getSqlExpression());
            }
            return result;
        }

        // check subselect
        if (toBuildFrom instanceof Criteria)
        {
            return new PreparedStatementPartForSubselect((Criteria) toBuildFrom, query);
        }

        // plain object
        if (toBuildFrom.equals(
                SqlEnum.CURRENT_DATE)
                || toBuildFrom.equals(SqlEnum.CURRENT_TIME)
                || toBuildFrom.equals(SqlEnum.CURRENT_TIMESTAMP))
        {
            result.getSql().append(toBuildFrom.toString());
            return result;
        }
        // If rValue is an ObjectKey, take the value of that ObjectKey.
        if (toBuildFrom instanceof ObjectKey)
        {
            toBuildFrom = ((ObjectKey<?>) toBuildFrom).getValue();
        }

        // handle ignoreCase
        if (ignoreCase && toBuildFrom instanceof String)
        {
            result.getSql().append(adapter.ignoreCase("?"));
        }
        else
        {
            result.getSql().append("?");
        }
        result.getPreparedStatementReplacements().add(toBuildFrom);
        return result;
    }
}
