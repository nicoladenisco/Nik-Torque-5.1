package org.apache.torque.sql;

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

import org.apache.torque.Database;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.criteria.Criterion;
import org.apache.torque.criteria.FromElement;
import org.apache.torque.criteria.Join;
import org.apache.torque.criteria.JoinType;
import org.apache.torque.criteria.PreparedStatementPart;
import org.apache.torque.criteria.PreparedStatementPartImpl;
import org.apache.torque.util.UniqueList;

/**
 * Generates SQL for joins.
 *
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id: JoinBuilder.java 1839288 2018-08-27 09:48:33Z tv $
 */
public final class JoinBuilder
{
    /**
     * Private constructor to prevent initialisation.
     *
     * Class contains only static methods and should therefore not be
     * instantiated.
     */
    private JoinBuilder()
    {
        // empty
    }

    /**
     * Adds the Joins from the criteria to the query.
     *
     * @param criteria the criteria from which the Joins are taken.
     * @param query the query to which the Joins should be added.
     *
     * @throws TorqueException if the Joins can not be processed
     */
    public static void processJoins(
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        if (criteria.isComposite())
        {
            return;
        }

        List<Join> criteriaJoins = criteria.getJoins();

        if (criteriaJoins.isEmpty())
        {
            return;
        }

        UniqueList<FromElement> queryFromClause = query.getFromClause();
        UniqueList<String> queryWhereClause = query.getWhereClause();

        for (int i = 0; i < criteriaJoins.size(); i++)
        {
            Join join = criteriaJoins.get(i);
            // Check the join type and add the join to the
            // appropriate places in the query
            JoinType joinType  = join.getJoinType();

            if (joinType == null)
            {
                // Do not treat join as explicit join, but add
                // the join condition to the where clauses
                StringBuilder joinConditionStringBuilder = new StringBuilder();
                SqlBuilder.appendCriterion(
                        join.getJoinCondition(),
                        criteria,
                        joinConditionStringBuilder,
                        query);

                queryWhereClause.add(joinConditionStringBuilder.toString());
            }
            else
            {
                Criterion joinCondition = join.getJoinCondition();

                // get the table names
                // (and the alias names for them if necessary))
                PreparedStatementPart leftExpression;
                if (join.getLeftTable() != null)
                {
                    leftExpression = join.getLeftTable();
                }
                else
                {
                    if (joinCondition.isComposite())
                    {
                        throw new TorqueException(
                                "join condition is composite "
                                        + "and there is no leftTable defined "
                                        + "in the join. "
                                        + "Please define a leftTable in the join");
                    }
                    Object lValue = joinCondition.getLValue();
                    leftExpression = SqlBuilder.getExpressionForFromClause(
                            lValue,
                            criteria);
                }
                PreparedStatementPart rightExpression;
                if (join.getRightTable() != null)
                {
                    rightExpression = join.getRightTable();
                }
                else
                {
                    if (joinCondition.isComposite())
                    {
                        throw new TorqueException(
                                "join condition is composite "
                                        + "and there is no rightTable defined "
                                        + "in the join. "
                                        + "Please define a rightTable in the join");
                    }
                    Object rValue = joinCondition.getRValue();
                    rightExpression = SqlBuilder.getExpressionForFromClause(
                            rValue,
                            criteria);

                }
                leftExpression = addSchema(leftExpression, criteria);
                rightExpression = addSchema(rightExpression, criteria);

                // check whether the order of the join must be "reversed"
                // This if the case if the fromClause already contains
                // rightTableName

                if (!SqlBuilder.fromClauseContainsExpression(
                        queryFromClause,
                        rightExpression))
                {
                    if (!SqlBuilder.fromClauseContainsExpression(
                            queryFromClause,
                            leftExpression))
                    {
                        FromElement fromElement = new FromElement(
                                leftExpression.getSqlAsString(),
                                null,
                                null,
                                leftExpression.getPreparedStatementReplacements());
                        queryFromClause.add(fromElement);
                    }

                    FromElement fromElement = new FromElement(
                            rightExpression.getSqlAsString(),
                            joinType,
                            buildJoinCondition(joinCondition, criteria, query));
                    fromElement.getPreparedStatementReplacements().addAll(rightExpression.getPreparedStatementReplacements());
                    queryFromClause.add(fromElement);
                }
                else
                {
                    if (SqlBuilder.fromClauseContainsExpression(
                            queryFromClause,
                            leftExpression))
                    {
                        // We cannot add an explicit join if both tables
                        // are already present in the from clause
                        throw new TorqueException(
                                "Unable to create a" + joinType
                                + "because both expressions "
                                + leftExpression.getSqlAsString()
                                + " and " + rightExpression.getSqlAsString()
                                + " are already in use. "
                                + "Try to create an(other) alias.");
                    }
                    // now add the join in reverse order
                    // rightTableName must not be added
                    // because it is already present
                    FromElement fromElement = new FromElement(
                            leftExpression.getSqlAsString(),
                            reverseJoinType(joinType),
                            buildJoinCondition(joinCondition, criteria, query));
                    queryFromClause.add(fromElement);
                }
            }
        }
    }

    /**
     * Returns the reversed Join type, i.e. the join type which would produce
     * the same result if also the joined tables were exchanged:
     * Example:<br />
     * table_a left join table_b <br />
     * produces the same result as  <br />
     * table_b right join table_a<br />
     * So "left join" is the reverse of "right join".
     *
     * @param joinType the join type to be reversed.
     *
     * @return the reversed join type.
     */
    private static JoinType reverseJoinType(final JoinType joinType)
    {
        if (JoinType.LEFT_JOIN.equals(joinType))
        {
            return JoinType.RIGHT_JOIN;
        }
        else if (JoinType.RIGHT_JOIN.equals(joinType))
        {
            return JoinType.LEFT_JOIN;
        }
        else
        {
            return joinType;
        }
    }

    /**
     * Creates the join condition string for a join.
     *
     * @param joinCondition the join condition.
     * @param criteria the enclosing criteria.
     * @param query the query which is currently built
     *
     * @return A join expression, e.g. table_a.column_a=table_b.column_b.
     *
     * @throws TorqueException if the join condition cannot be built.
     */
    private static PreparedStatementPart buildJoinCondition(
            final Criterion joinCondition,
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        PreparedStatementPartImpl joinPart = new PreparedStatementPartImpl();
        appendJoinCondition(joinCondition, criteria, joinPart, query);
        return joinPart;
    }

    /**
     * Appends a join condition to a join part.
     *
     * @param joinCondition the join condition.
     * @param criteria the enclosing criteria.
     * @param joinPart the join part to append to.
     * @param query the query which is currently built
     *
     * @return A join expression, e.g. table_a.column_a=table_b.column_b.
     *
     * @throws TorqueException if the join condition cannot be built.
     */
    private static void appendJoinCondition(
            final Criterion joinCondition,
            final Criteria criteria,
            final PreparedStatementPartImpl joinPart,
            final Query query)
                    throws TorqueException
    {
        if (joinCondition.isComposite())
        {
            joinPart.getSql().append('(');
            boolean firstPart = true;
            for (Criterion part : joinCondition.getParts())
            {
                if (!firstPart)
                {
                    joinPart.getSql().append(joinCondition.getConjunction());
                }
                appendJoinCondition(
                        part,
                        criteria,
                        joinPart,
                        query);
                firstPart = false;
            }
            joinPart.getSql().append(')');
            return;
        }
        PreparedStatementPart joinConditionStatementPart
        = SqlBuilder.processCriterion(joinCondition, criteria, query);
        joinPart.append(joinConditionStatementPart);
    }

    /**
     * Adds the default schema to a table name if necessary.
     *
     * @param tableNamePart the table name to add the schema name to, not null.
     * @param criteria the criteria from which the tableNamePart was created, not null.
     */
    private static PreparedStatementPart addSchema(final PreparedStatementPart tableNamePart, final Criteria criteria)
            throws TorqueException
    {
        String tableName = tableNamePart.getSqlAsString();
        if (tableName.indexOf('.') != -1 // table name is already qualified
                || tableName.indexOf(' ') != -1 // table name is no simple table name
                || tableName.indexOf('(') != -1) // table name is no simple table name
        {
            return tableNamePart;
        }
        Object resolvedAlias = criteria.getAliases().get(tableName);
        if (resolvedAlias != null)
        {
            return tableNamePart;
        }
        final String dbName = criteria.getDbName();
        final Database database = Torque.getDatabase(dbName);
        String resolvedSchemaName = database.getSchema();
        if (resolvedSchemaName == null)
        {
            return tableNamePart;
        }
        PreparedStatementPartImpl result = new PreparedStatementPartImpl(tableNamePart);
        result.getSql().insert(0, '.');
        result.getSql().insert(0, resolvedSchemaName);
        return result;
    }
}
