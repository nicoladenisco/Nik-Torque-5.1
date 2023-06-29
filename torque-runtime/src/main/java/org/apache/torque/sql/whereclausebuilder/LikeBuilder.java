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
 * Builds a PreparedStatementPart from a WhereClauseExpression containing
 * a Like operator.
 *
 * @version $Id: LikeBuilder.java 1896195 2021-12-20 17:41:20Z gk $
 */
public class LikeBuilder extends AbstractWhereClausePsPartBuilder
{
    /** The backslash character*/
    private static final char BACKSLASH = '\\';

    /**
     * Builds the PS part for a WhereClauseExpression with a LIKE operator.
     * Multicharacter wildcards % and * may be used
     * as well as single character wildcards, _ and ?.  These
     * characters can be escaped with \.
     *
     * e.g. criteria = "fre%" -&gt; columnName LIKE 'fre%'
     *                        -&gt; UPPER(columnName) LIKE UPPER('fre%')
     *      criteria = "50\%" -&gt; columnName = '50%'
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
        if (!(whereClausePart.getRValue() instanceof String))
        {
            throw new TorqueException(
                    "rValue must be a String for the operator "
                            + whereClausePart.getOperator());
        }
        String value = (String) whereClausePart.getRValue();
        // If selection criteria contains wildcards use LIKE otherwise
        // use = (equals).  Wildcards can be escaped by prepending
        // them with \ (backslash). However, if we switch from
        // like to equals, we need to remove the escape characters.
        // from the wildcards.
        // So we need two passes: The first replaces * and ? by % and _,
        // and checks whether we switch to equals,
        // the second removes escapes if we have switched to equals.
        int position = 0;
        StringBuilder sb = new StringBuilder();
        boolean replaceWithEquals = true;
        while (position < value.length())
        {
            char checkWildcard = value.charAt(position);

            switch (checkWildcard)
            {
            case BACKSLASH:
                if (position + 1 >= value.length())
                {
                    // ignore backslashes at end
                    break;
                }
                position++;
                char escapedChar = value.charAt(position);
                if (escapedChar != '*' && escapedChar != '?')
                {
                    sb.append(checkWildcard);
                }
                // code below copies escaped character into sb
                checkWildcard = escapedChar;
                break;
            case '%':
            case '_':
                replaceWithEquals = false;
                break;
            case '*':
                replaceWithEquals = false;
                checkWildcard = '%';
                break;
            case '?':
                replaceWithEquals = false;
                checkWildcard = '_';
                break;
            default:
                break;
            }

            sb.append(checkWildcard);
            position++;
        }
        value = sb.toString();

        CombinedPreparedStatementPart result;
        if (ignoreCase)
        {
            if (adapter.useIlike() && !replaceWithEquals)
            {
                if (SqlEnum.LIKE.equals(whereClausePart.getOperator()))
                {
                    whereClausePart.setOperator(SqlEnum.ILIKE);
                }
                else if (SqlEnum.NOT_LIKE.equals(whereClausePart.getOperator()))
                {
                    whereClausePart.setOperator(SqlEnum.NOT_ILIKE);
                }
                result = new CombinedPreparedStatementPart(
                        getObjectOrColumnPsPartBuilder().buildPs(
                                whereClausePart.getLValue(),
                                false,
                                query,
                                adapter));
            }
            else
            {
                // no native case insensitive like is offered by the DB,
                // or the LIKE was replaced with equals.
                // need to ignore case manually.
                result = new CombinedPreparedStatementPart(
                        getObjectOrColumnPsPartBuilder().buildPs(
                                whereClausePart.getLValue(),
                                true,
                                query,
                                adapter));
            }
        }
        else
        {
            result = new CombinedPreparedStatementPart(getObjectOrColumnPsPartBuilder().buildPs(
                    whereClausePart.getLValue(),
                    ignoreCase,
                    query,
                    adapter));
        }

        if (replaceWithEquals
                && !whereClausePart.getOperator().equals(SqlEnum.NOT_ILIKE)
                && !whereClausePart.getOperator().equals(SqlEnum.ILIKE)
                )
        {
            if (whereClausePart.getOperator().equals(SqlEnum.NOT_LIKE)
                    || whereClausePart.getOperator().equals(SqlEnum.NOT_ILIKE))
            {
                result.appendSql(SqlEnum.NOT_EQUAL.toString());
            }
            else
            {
                result.appendSql(SqlEnum.EQUAL.toString());
            }

            // remove escape backslashes from String
            position = 0;
            sb = new StringBuilder();
            while (position < value.length())
            {
                char checkWildcard = value.charAt(position);

                if (checkWildcard == BACKSLASH
                        && position + 1 < value.length())
                {
                    position++;
                    // code below copies escaped character into sb
                    checkWildcard = value.charAt(position);
                }
                sb.append(checkWildcard);
                position++;
            }
            value = sb.toString();
        }
        else
        {
            result.appendSql(whereClausePart.getOperator().toString());
        }

        String rValueSql = "?";
        // handle ignoreCase if necessary
        if (ignoreCase && (!(adapter.useIlike()) || replaceWithEquals))
        {
            rValueSql = adapter.ignoreCase(rValueSql);
        }
        // handle escape clause if necessary
        if (!replaceWithEquals && adapter.useEscapeClauseForLike())
        {
            rValueSql = rValueSql + SqlEnum.ESCAPE + "'\\'";
        }

        result.addPreparedStatementReplacement(value);
        result.appendSql(rValueSql);
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
        if (Stream.of(
                SqlEnum.LIKE, SqlEnum.NOT_LIKE, SqlEnum.ILIKE, SqlEnum.NOT_ILIKE)
                .anyMatch(sqlEnum -> whereClauseExpression.getOperator()
                        .equals(sqlEnum)))
        {
            return true;
        }
        return false;
    }
}
