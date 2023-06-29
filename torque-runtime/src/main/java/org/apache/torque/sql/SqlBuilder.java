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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.torque.Column;
import org.apache.torque.ColumnImpl;
import org.apache.torque.Database;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.criteria.Criterion;
import org.apache.torque.criteria.FromElement;
import org.apache.torque.criteria.PreparedStatementPart;
import org.apache.torque.criteria.PreparedStatementPartImpl;
import org.apache.torque.criteria.SqlEnum;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.MapHelper;
import org.apache.torque.map.TableMap;
import org.apache.torque.sql.whereclausebuilder.CurrentDateTimePsPartBuilder;
import org.apache.torque.sql.whereclausebuilder.EnumValueBuilder;
import org.apache.torque.sql.whereclausebuilder.InBuilder;
import org.apache.torque.sql.whereclausebuilder.LikeBuilder;
import org.apache.torque.sql.whereclausebuilder.NullValueBuilder;
import org.apache.torque.sql.whereclausebuilder.StandardBuilder;
import org.apache.torque.sql.whereclausebuilder.VerbatimSqlConditionBuilder;
import org.apache.torque.sql.whereclausebuilder.WhereClausePsPartBuilder;
import org.apache.torque.util.UniqueColumnList;
import org.apache.torque.util.UniqueList;

/**
 * Factored out code that is used to process SQL tables. This code comes
 * from BasePeer and is put here to reduce complexity in the BasePeer class.
 * You should not use the methods here directly!
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @version $Id: SqlBuilder.java 1870542 2019-11-28 09:32:40Z tv $
 */
public final class SqlBuilder
{
    /** Logging */
    protected static final Logger log = LogManager.getLogger(SqlBuilder.class);

    /** Delimiters for SQL functions. */
    public static final String[] FUNCTION_DELIMITERS
    = {" ", ",", "(", ")", "<", ">"};

    /**
     * The list of WhereClausePsPartBuilders which can build the where clause.
     */
    private static List<WhereClausePsPartBuilder> whereClausePsPartBuilders
        = new ArrayList<>();

    static
    {
        whereClausePsPartBuilders.add(new EnumValueBuilder());
        whereClausePsPartBuilders.add(new VerbatimSqlConditionBuilder());
        whereClausePsPartBuilders.add(new CurrentDateTimePsPartBuilder());
        whereClausePsPartBuilders.add(new NullValueBuilder());
        whereClausePsPartBuilders.add(new LikeBuilder());
        whereClausePsPartBuilders.add(new InBuilder());
        whereClausePsPartBuilders.add(new StandardBuilder());
    }

    /**
     * Private constructor to prevent instantiation.
     *
     * Class contains only static method and should therefore not be
     * instantiated.
     */
    private SqlBuilder()
    {
        // empty
    }

    /**
     * Returns the Builders which are responsible to render single where clause
     * conditions. The returned list can be modified in order to change
     * the rendered SQL.
     *
     * @return the current WhereClausePsPartBuilders, not null.
     */
    public static List<WhereClausePsPartBuilder> getWhereClausePsPartBuilders()
    {
        return whereClausePsPartBuilders;
    }

    /**
     * Builds a Query from a criteria.
     *
     * @param crit the criteria to build the query from, not null.
     *
     * @return the corresponding query to the criteria.
     *
     * @exception TorqueException if an error occurs
     */
    public static Query buildQuery(final Criteria crit)
            throws TorqueException
    {
        Query sqlStatement = new Query();

        JoinBuilder.processJoins(crit, sqlStatement);
        processModifiers(crit, sqlStatement);
        processSelectColumns(crit, sqlStatement);
        processAsColumns(crit, sqlStatement);
        processCriterions(crit, sqlStatement);
        processGroupBy(crit, sqlStatement);
        processHaving(crit, sqlStatement);
        processOrderBy(crit, sqlStatement);
        processSetOperations(crit, sqlStatement);
        processLimits(crit, sqlStatement);
        processFromElements(crit, sqlStatement);
        processForUpdate(crit, sqlStatement);
        sqlStatement.setFetchSize(crit.getFetchSize());

        return sqlStatement;
    }

    /**
     * Adds the select columns from the criteria to the query.
     *
     * @param criteria the criteria from which the select columns are taken.
     * @param query the query to which the select columns should be added.
     *
     * @throws TorqueException if the select columns can not be processed.
     */
    private static void processSelectColumns(
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        if (criteria.isComposite())
        {
            return;
        }
        UniqueList<String> selectClause = query.getSelectClause();
        UniqueColumnList selectColumns = criteria.getSelectColumns();

        for (Column column : selectColumns)
        {
            String sqlExpression = column.getSqlExpression();
            Column resolvedAlias = criteria.getAsColumns().get(sqlExpression);
            if (resolvedAlias != null)
            {
                // will be handled by processAsColumns
                continue;
            }
            selectClause.add(sqlExpression);
            addTableToFromClause(
                    column,
                    criteria,
                    query);
        }
    }

    /**
     * Adds the As-columns (Aliases for columns) from the criteria
     * to the query's select clause.
     *
     * @param criteria the criteria from which the As-columns are taken,
     *        not null.
     * @param query the query to which the As-columns should be added,
     *        not null.
     *
     * @throws TorqueException if the as columns can not be processed.
     */
    private static void processAsColumns(
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        if (criteria.isComposite())
        {
            return;
        }
        UniqueList<String> querySelectClause = query.getSelectClause();
        Map<String, Column> criteriaAsColumns = criteria.getAsColumns();

        for (Map.Entry<String, Column> entry : criteriaAsColumns.entrySet())
        {
            Column column = entry.getValue();
            querySelectClause.add(
                    column.getSqlExpression()
                    + SqlEnum.AS
                    + entry.getKey());
            addTableToFromClause(
                    column,
                    criteria,
                    query);
        }
    }

    /**
     * Adds the select modifiers from the criteria to the query.
     *
     * @param criteria the criteria from which the Modifiers are taken,
     *        not null.
     * @param query the query to which the Modifiers should be added,
     *        not null.
     */
    private static void processModifiers(
            final Criteria criteria,
            final Query query)
    {
        if (criteria.isComposite())
        {
            return;
        }
        UniqueList<String> selectModifiers = query.getSelectModifiers();
        UniqueList<String> modifiers = criteria.getSelectModifiers();
        for (String modifier : modifiers)
        {
            selectModifiers.add(modifier);
        }
    }

    /**
     * Adds the Criterions from the criteria to the query.
     *
     * @param criteria the criteria from which the Criterion-objects are taken
     * @param query the query to which the Criterion-objects should be added.
     *
     * @throws TorqueException if the Criterion-objects can not be processed
     */
    private static void processCriterions(
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        if (criteria.isComposite())
        {
            return;
        }
        if (criteria.getTopLevelCriterion() == null)
        {
            return;
        }
        StringBuilder where = new StringBuilder();
        appendCriterion(
                criteria.getTopLevelCriterion(),
                criteria,
                where,
                query);
        query.getWhereClause().add(where.toString());
    }

    static void appendCriterion(
            final Criterion criterion,
            final Criteria criteria,
            final StringBuilder where,
            final Query query)
                    throws TorqueException
    {
        if (criterion.isComposite())
        {
            where.append('(');
            boolean firstPart = true;
            for (Criterion part : criterion.getParts())
            {
                if (!firstPart)
                {
                    where.append(criterion.getConjunction());
                }
                appendCriterion(
                        part,
                        criteria,
                        where,
                        query);
                firstPart = false;
            }
            where.append(')');
            return;
        }
        // add the table to the from clause, if it is not already
        // contained there
        // it is important that this piece of code is executed AFTER
        // the joins are processed
        addTableToFromClause(
                criterion.getLValue(),
                criteria,
                query);
        addTableToFromClause(
                criterion.getRValue(),
                criteria,
                query);

        PreparedStatementPart whereClausePartOutput
        = processCriterion(criterion, criteria, query);

        where.append(whereClausePartOutput.getSqlAsString());
        query.getWhereClausePreparedStatementReplacements().addAll(
                whereClausePartOutput.getPreparedStatementReplacements());
    }

    static PreparedStatementPart processCriterion(
            final Criterion criterion,
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        final String dbName = criteria.getDbName();
        final Database database = Torque.getDatabase(dbName);
        final Adapter adapter = Torque.getAdapter(dbName);

        boolean ignoreCase
        = isIgnoreCase(criterion, criteria, database);

        WhereClauseExpression whereClausePartInput
            = new WhereClauseExpression(
                criterion.getLValue(),
                criterion.getComparison(),
                criterion.getRValue(),
                criterion.getSql(),
                criterion.getPreparedStatementReplacements());
        PreparedStatementPart whereClausePartOutput = null;
        for (WhereClausePsPartBuilder builder : whereClausePsPartBuilders)
        {
            if (builder.isApplicable(whereClausePartInput, adapter))
            {
                whereClausePartOutput = builder.buildPs(
                        whereClausePartInput,
                        ignoreCase,
                        query,
                        adapter);
                break;
            }
        }

        if (whereClausePartOutput == null)
        {
            // should not happen as last element in list is standardHandler
            // which takes all
            throw new RuntimeException("No handler found for whereClausePart "
                    + whereClausePartInput);
        }
        return whereClausePartOutput;
    }

    /**
     * adds the OrderBy-Columns from the criteria to the query
     * @param criteria the criteria from which the OrderBy-Columns are taken
     * @param query the query to which the OrderBy-Columns should be added
     * @throws TorqueException if the OrderBy-Columns can not be processed
     */
    private static void processOrderBy(
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        UniqueList<String> orderByClause = query.getOrderByClause();
        UniqueList<String> selectClause = query.getSelectClause();

        UniqueList<OrderBy> orderByList = criteria.getOrderByColumns();

        // Check for each String/Character column and apply
        // toUpperCase().
        for (OrderBy orderBy : orderByList)
        {
            Column column = orderBy.getColumn();
            ColumnMap columnMap = MapHelper.getColumnMap(column, criteria);
            String sqlExpression = column.getSqlExpression();

            // Either we are not able to look up the column in the
            // databaseMap, then simply use the case in orderBy and
            // hope the user knows what he is
            // doing.
            // Or we only ignore case in order by for string columns
            // which do not have a function around them
            if (columnMap == null
                    || (columnMap.getType() instanceof String
                            && sqlExpression.indexOf('(') == -1))
            {
                if (orderBy.isIgnoreCase() || criteria.isIgnoreCase())
                {
                    final Adapter adapter = Torque.getAdapter(criteria.getDbName());
                    orderByClause.add(
                            adapter.ignoreCaseInOrderBy(sqlExpression)
                            + ' ' + orderBy.getOrder());
                    selectClause.add(
                            adapter.ignoreCaseInOrderBy(sqlExpression));
                }
                else
                {
                    orderByClause.add(sqlExpression + ' ' + orderBy.getOrder());
                    if (criteria.getAsColumns().get(sqlExpression) == null)
                    {
                        selectClause.add(sqlExpression);
                    }
                }
            }
            else
            {
                orderByClause.add(sqlExpression + ' ' + orderBy.getOrder());
                if (criteria.getAsColumns().get(sqlExpression) == null)
                {
                    selectClause.add(sqlExpression);
                }
            }
            addTableToFromClause(
                    column,
                    criteria,
                    query);
        }
    }

    /**
     * Adds the GroupBy-Columns from the criteria to the query.
     *
     * @param criteria the criteria from which the GroupBy-Columns are taken.
     * @param query the query to which the GroupBy-Columns should be added.
     *
     * @throws TorqueException if the GroupBy-Columns can not be processed
     */
    private static void processGroupBy(
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        if (criteria.isComposite())
        {
            return;
        }
        UniqueList<String> groupByClause = query.getGroupByClause();
        UniqueColumnList groupBy = criteria.getGroupByColumns();

        for (Column groupByColumn : groupBy)
        {
            Column column = criteria.getAsColumns().get(
                    groupByColumn.getSqlExpression());

            if (column == null)
            {
                column = groupByColumn;
            }

            groupByClause.add(column.getSqlExpression());
            addTableToFromClause(column, criteria, query);
        }
    }

    /**
     * Adds the Having-Columns from the criteria to the query.
     *
     * @param criteria the criteria from which the Having-Columns are taken
     * @param query the query to which the Having-Columns should be added
     * @throws TorqueException if the Having-Columns can not be processed
     */
    private static void processHaving(
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        if (criteria.isComposite())
        {
            return;
        }
        Criterion having = criteria.getHaving();
        if (having != null)
        {
            query.setHaving(having.toString());
        }
    }

    /**
     * Adds a Limit clause to the query if supported by the database.
     *
     * @param criteria the criteria from which the Limit and Offset values
     *        are taken
     * @param query the query to which the Limit clause should be added
     *
     * @throws TorqueException if the Database adapter cannot be obtained
     */
    private static void processLimits(
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        int limit = criteria.getLimit();
        long offset = criteria.getOffset();

        if (offset > 0 || limit >= 0)
        {
            Adapter adapter = Torque.getAdapter(criteria.getDbName());
            adapter.generateLimits(query, offset, limit);
        }
    }

    /**
     * Checks the fromElements in the criteria and replaces the automatically
     * calculated fromElements in the query by them, if they are filled.
     *
     * @param criteria the criteria from which the query should be built.
     * @param query the query to build.
     */
    private static void processFromElements(
            final Criteria criteria,
            final Query query)
    {
        if (criteria.isComposite())
        {
            return;
        }
        if (criteria.getFromElements().isEmpty())
        {
            log.trace("criteria's from Elements is empty,"
                    + " using automatically calculated from clause");
            return;
        }
        query.getFromClause().clear();
        query.getFromClause().addAll(criteria.getFromElements());
    }

    /**
     * Adds a possible FOR UPDATE Clause to the query.
     *
     * @param criteria the criteria from which the query should be built.
     * @param query the query to build.
     *
     * @throws TorqueException if the Database adapter cannot be obtained
     */
    private static void processForUpdate(
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        if (criteria.isForUpdate())
        {
            Adapter adapter = Torque.getAdapter(criteria.getDbName());
            query.setForUpdate(adapter.getUpdateLockClause());
        }
    }

    /**
     * Adds set operations to the query.
     *
     * @param criteria the criteria from which the query should be built.
     * @param query the query to build.
     *
     * @throws TorqueException if the Database adapter cannot be obtained
     */
    private static void processSetOperations(
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        if (!criteria.isComposite())
        {
            return;
        }
        final String dbName = criteria.getDbName();
        final Adapter adapter = Torque.getAdapter(dbName);

        SqlEnum setOperator = criteria.getSetOperator();
        if (adapter.useMinusForExcept())
        {
            if (SqlEnum.EXCEPT == setOperator)
            {
                setOperator = SqlEnum.MINUS;
            }
            else if (SqlEnum.EXCEPT_ALL == setOperator)
            {
                setOperator = SqlEnum.MINUS_ALL;
            }
        }
        query.setPartOperator(setOperator.toString());
        for (Criteria part : criteria.getSetCriteriaParts())
        {
            Query queryPart = buildQuery(part);
            query.getParts().add(queryPart);
        }
    }

    /**
     * Returns the tablename which can be added to a From Clause.
     * This takes care of any aliases that might be defined.
     * For example, if an alias "a" for the table AUTHOR is defined
     * in the Criteria criteria, getTableNameForFromClause("a", criteria)
     * returns "AUTHOR a".
     *
     * @param toAddToFromClause the column to extract the table name from,
     *        or a literal object value.
     * @param criteria a criteria object to resolve a possible alias.
     *
     * @return A prepared statement part containing either the table name
     *         itself if tableOrAliasName is not an alias,
     *         or a String of the form "tableName tableOrAliasName"
     *         if tableOrAliasName is an alias for a table name,
     *         or a ? with the replacement if toAddToFromClause is not a Column.
     */
    static PreparedStatementPart getExpressionForFromClause(
            final Object toAddToFromClause,
            final Criteria criteria)
                    throws TorqueException
    {
        if (!(toAddToFromClause instanceof Column))
        {
            // toAddToFromClause is a literal Value
            return new PreparedStatementPartImpl("?", toAddToFromClause);
        }
        Column column = (Column) toAddToFromClause;
        Column resolvedColumn
        = resolveAliasAndAsColumnAndSchema(column, criteria);
        String fullTableName
        = resolvedColumn.getFullTableName();

        if (!StringUtils.equals(
                resolvedColumn.getTableName(),
                column.getTableName()))
        {
            // If the tables have an alias, add an "<xxx> <yyy> statement"
            // <xxx> AS <yyy> causes problems on oracle
            PreparedStatementPartImpl result = new PreparedStatementPartImpl();
            result.getSql()
            .append(fullTableName)
            .append(" ")
            .append(column.getTableName());
            return result;
        }
        Object resolvedAlias = criteria.getAliases().get(
                resolvedColumn.getTableName());
        if (resolvedAlias != null)
        {
            if (resolvedAlias instanceof Criteria)
            {
                Criteria subquery = (Criteria) resolvedAlias;
                Query renderedSubquery = SqlBuilder.buildQuery(subquery);
                PreparedStatementPartImpl result = new PreparedStatementPartImpl();
                result.getSql().append("(")
                .append(renderedSubquery.toString())
                .append(") ")
                .append(resolvedColumn.getTableName());
                result.getPreparedStatementReplacements().addAll(
                        renderedSubquery.getPreparedStatementReplacements());
                return result;
            }
            else
            {
                throw new TorqueException("Table name "
                        + resolvedColumn.getTableName()
                        + " resolved to an unhandleable class "
                        + resolvedAlias.getClass().getName());
            }
        }

        return new PreparedStatementPartImpl(fullTableName);
    }

    /**
     * Fully qualify a table name with an optional schema reference.
     *
     * @param table The table name to use.
     *              If null is passed in, null is returned.
     * @param dbName The name of the database to which this tables belongs.
     *               If null is passed, the default database is used.
     *
     * @return The table name to use inside the SQL statement.
     *         If null is passed into this method, null is returned.
     * @exception TorqueException if Torque is not yet initialized.
     */
    public static String getFullTableName(
            final String table,
            final String dbName)
                    throws TorqueException
    {
        if (table == null)
        {
            return table;
        }

        int dotIndex = table.indexOf(".");
        if (dotIndex == -1) // No schema given
        {
            String targetDBName = (dbName == null)
                    ? Torque.getDefaultDB()
                            : dbName;

                    String targetSchema = Torque.getSchema(targetDBName);

                    // If we have a default schema, fully qualify the
                    // table and return.
                    if (StringUtils.isNotEmpty(targetSchema))
                    {
                        return new StringBuilder()
                                .append(targetSchema)
                                .append(".")
                                .append(table)
                                .toString();
                    }
        }

        return table;
    }

    /**
     * Unqualify a table or column name.
     *
     * @param name the name to unqualify.
     *        If null is passed in, null is returned.
     * @param dbName name of the database
     * @return The unqualified name.
     * @throws TorqueException if the name cannot be determined.
     */
    public static String getUnqualifiedName(
            final String name,
            final String dbName)
                    throws TorqueException
    {
        if (name == null)
        {
            return null;
        }

        int dotIndex = name.lastIndexOf(".");
        if (dotIndex == -1)
        {
            return name;
        }

        return name.substring(dotIndex + 1);
    }

    /**
     * Guesses a table name from a criteria by inspecting the first
     * column in the criteria.
     *
     * @param criteria the criteria to guess the table name from.
     *
     * @return the table name, not null.
     *
     * @throws TorqueException if the table name cannot be determined.
     */
    public static String guessFullTableFromCriteria(final Criteria criteria)
            throws TorqueException
    {
        org.apache.torque.criteria.Criterion criterion
        = criteria.getTopLevelCriterion();
        if (criterion == null)
        {
            throw new TorqueException("Could not determine table name "
                    + " as criteria contains no criterion");
        }
        while (criterion.isComposite())
        {
            criterion = criterion.getParts().iterator().next();
        }
        String tableName = null;

        Object lValue = criterion.getLValue();
        if (lValue instanceof Column)
        {
            Column column = (Column) lValue;
            tableName = column.getFullTableName();
        }
        if (tableName == null)
        {
            throw new TorqueException("Could not determine table name "
                    + " as first criterion contains no table name");
        }
        return tableName;
    }

    /**
     * Returns the table map for a table.
     *
     * @param tableName the name of the table.
     * @param dbName the name of the database, null for the default db.
     *
     * @return the table map for the table, not null.
     *
     * @throws TorqueException if the database or table is unknown.
     */
    public static TableMap getTableMap(final String tableName, String dbName)
            throws TorqueException
    {

        if (dbName == null)
        {
            dbName = Torque.getDefaultDB();
        }
        DatabaseMap databaseMap = Torque.getDatabaseMap(dbName);
        if (databaseMap == null)
        {
            throw new TorqueException("Could not find database map"
                    + " for database "
                    + dbName);
        }
        String unqualifiedTableName = getUnqualifiedName(tableName, dbName);
        TableMap result = databaseMap.getTable(unqualifiedTableName);
        if (result == null)
        {
            throw new TorqueException("Could not find table "
                    + tableName
                    + " in database map of database "
                    + dbName);
        }
        return result;
    }

    /**
     * Returns the database name of a column.
     *
     * @param columnToResolve the name of a column or the alias for a column.
     * @param criteria a criteria object to resolve a possible alias.
     *
     * @return either the tablename itself if tableOrAliasName is not an alias,
     *         or a String of the form "tableName tableOrAliasName"
     *         if tableOrAliasName is an alias for a table name
     *         
     * @throws TorqueException if the column is not found.
     */
    static Column resolveAliasAndAsColumnAndSchema(
            final Column columnToResolve,
            final Criteria criteria)
                    throws TorqueException
    {
        String columnNameToResolve = columnToResolve.getColumnName();
        Column resolvedColumn = criteria.getAsColumns().get(columnNameToResolve);
        boolean sqlExpressionModified = false;
        if (resolvedColumn == null)
        {
            resolvedColumn = columnToResolve;
        }
        else
        {
            sqlExpressionModified = true;
        }
        String tableNameToResolve = resolvedColumn.getTableName();
        Object resolvedAlias = criteria.getAliases().get(tableNameToResolve);
        String resolvedTableName;
        if (resolvedAlias == null || !(resolvedAlias instanceof String))
        {
            resolvedTableName = tableNameToResolve;
        }
        else
        {
            resolvedTableName = (String) resolvedAlias;
            sqlExpressionModified = true;
        }
        String resolvedSchemaName = resolvedColumn.getSchemaName();
        if (resolvedSchemaName == null)
        {
            final String dbName = criteria.getDbName();
            final Database database = Torque.getDatabase(dbName);
            resolvedSchemaName = database.getSchema();
        }
        if (sqlExpressionModified)
        {
            return new ColumnImpl(
                    resolvedSchemaName,
                    resolvedTableName,
                    resolvedColumn.getColumnName());
        }
        else
        {
            return new ColumnImpl(
                    resolvedSchemaName,
                    resolvedTableName,
                    resolvedColumn.getColumnName(),
                    resolvedColumn.getSqlExpression());
        }
    }

    /**
     * Checks if a fromExpression is already contained in a from clause.
     * Different aliases for the same table are treated
     * as different tables: E.g.
     * fromClauseContainsTableName(fromClause, "table_a a") returns false if
     * fromClause contains only another alias for table_a ,
     * e.g. "table_a aa" and the unaliased tablename "table_a".
     * Special case: If fromClause is null or empty, false is returned.
     *
     * @param fromClause The list to check against.
     * @param fromExpression the fromExpression to check, not null.
     *
     * @return whether the fromExpression is already contained in the from
     *         clause.
     */
    static boolean fromClauseContainsExpression(
            final UniqueList<FromElement> fromClause,
            final PreparedStatementPart fromExpression)
    {
        if (fromExpression == null || fromExpression.getSqlAsString().length() == 0)
        {
            return false;
        }
        String fromExpressionSql = fromExpression.getSqlAsString();
        for (FromElement fromElement : fromClause)
        {
            if (fromExpressionSql.equals(fromElement.getFromExpression()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a table to the from clause of a query, if it is not already
     * contained there.
     *
     * @param tableOrAliasName the name of a table
     *        or the alias for a table. If null, the from clause is left
     *        unchanged.
     * @param criteria a criteria object to resolve a possible alias
     * @param query the query where the the table name should be added
     *        to the from clause
     */
    static void addTableToFromClause(
            final Object possibleColumn,
            final Criteria criteria,
            final Query query)
                    throws TorqueException
    {
        if (possibleColumn == null)
        {
            return;
        }
        if (!(possibleColumn instanceof Column))
        {
            return;
        }
        Column column = (Column) possibleColumn;
        if (column.getTableName() == null)
        {
            return;
        }
        PreparedStatementPart fromClauseExpression = getExpressionForFromClause(
                column,
                criteria);

        UniqueList<FromElement> queryFromClause = query.getFromClause();

        // it is important that this piece of code is executed AFTER
        // the joins are processed
        if (!fromClauseContainsExpression(
                queryFromClause,
                fromClauseExpression))
        {
            FromElement fromElement = new FromElement(
                    fromClauseExpression.getSqlAsString(),
                    null,
                    null,
                    fromClauseExpression.getPreparedStatementReplacements());
            queryFromClause.add(fromElement);
        }
    }

    /**
     * Checks whether ignoreCase is used for this criterion.
     * This is the case if ignoreCase is either set on the criterion
     * or the criteria and if ignoreCase is applicable for both values.
     *
     * @param criterion the value to check.
     * @param criteria the criteria where the criterion stems from.
     * @param database The database to check.
     *
     * @return Whether to use ignoreCase for the passed criterion.
     *
     * @throws TorqueException in the case of an error.
     */
    static boolean isIgnoreCase(
            final Criterion criterion,
            final Criteria criteria,
            final Database database)
                    throws TorqueException
    {
        boolean ignoreCase
        = criteria.isIgnoreCase() || criterion.isIgnoreCase();
        ignoreCase = ignoreCase
                && ignoreCaseApplicable(
                        criterion.getLValue(),
                        criteria,
                        database)
                && ignoreCaseApplicable(
                        criterion.getRValue(),
                        criteria,
                        database);
        return ignoreCase;
    }

    /**
     * Checks whether ignoreCase is applicable for this column.
     * This is not the case if the value is a column and the column type is
     * not varchar, or if the object is no column and not a String;
     * in all other cases ignoreCase is applicable.
     *
     * @param value the value to check.
     * @param criteria the criteria where the value stems from.
     * @param database The database to check.
     *
     * @return false if ignoreCase is not applicable, true otherwise.
     *
     * @throws TorqueException in the case of an error.
     */
    private static boolean ignoreCaseApplicable(
            final Object value,
            final Criteria criteria,
            final Database database)
                    throws TorqueException
    {
        if (value == null)
        {
            return true;
        }
        if (!(value instanceof Column))
        {
            if (value instanceof String
                    || value instanceof Iterable
                    || value.getClass().isArray())
            {
                return true;
            }
            return false;
        }
        Column column = (Column) value;
        Column databaseColumn = resolveAliasAndAsColumnAndSchema(
                column,
                criteria);
        ColumnMap columnMap = null;
        {
            DatabaseMap databaseMap = database.getDatabaseMap();
            TableMap tableMap = databaseMap.getTable(
                    databaseColumn.getTableName());
            if (tableMap != null)
            {
                columnMap = tableMap.getColumn(
                        databaseColumn.getColumnName());
            }
        }
        if (columnMap == null)
        {
            return true;
        }
        // do not use ignoreCase on columns
        // which do not contain String values
        return columnMap.getType() instanceof String;
    }
}
