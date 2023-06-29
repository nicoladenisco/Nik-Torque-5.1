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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.Column;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.FromElement;
import org.apache.torque.util.ColumnValues;
import org.apache.torque.util.JdbcTypedValue;
import org.apache.torque.util.UniqueList;

/**
 * Contains the various parts of a SQL statement (select, update or delete).
 * Attributes exist for the sections of these statements:
 * modifiers, columns, from clause, where clause, and order by clause.
 * Most parts of the query are appended to buffers which only accept
 * unique entries.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:sam@neurogrid.com">Sam Joseph</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @version $Id: Query.java 1867515 2019-09-25 15:02:03Z gk $
 *
 * TODO rename to SqlStatement
 */
public class Query
{
    /** All types of the statement. */
    public enum Type
    {
        /** The instance contains the parts of a select statement. */
        SELECT,
        /** The instance contains the parts of an update statement. */
        UPDATE,
        /** The instance contains the parts of a delete statement. */
        DELETE
    }

    /** Constant for text "SELECT ". */
    private static final String SELECT = "SELECT ";
    /** Constant for text "UPDATE ". */
    private static final String UPDATE = "UPDATE ";
    /** Constant for text "DELETE FROM ". */
    private static final String DELETE_FROM = "DELETE FROM ";
    /** Constant for text " FROM ". */
    private static final String FROM = " FROM ";
    /** Constant for text " SET ". */
    private static final String SET = " SET ";
    /** Constant for text " WHERE ". */
    private static final String WHERE = " WHERE ";
    /** Constant for text " AND ". */
    private static final String AND = " AND ";
    /** Constant for text " ORDER BY ". */
    private static final String ORDER_BY = " ORDER BY ";
    /** Constant for text " GROUP BY ". */
    private static final String GROUP_BY = " GROUP BY ";
    /** Constant for text " HAVING ". */
    private static final String HAVING = " HAVING ";
    /** Constant for text " LIMIT ". */
    private static final String LIMIT = " LIMIT ";
    /** Constant for text " OFFSET ". */
    private static final String OFFSET = " OFFSET ";
    /** Constant for text " SET ROWCOUNT ". */
    private static final String SET_ROWCOUNT = " SET ROWCOUNT ";

    /**
     * The select modifiers. E.g. DISTINCT.
     */
    private final UniqueList<String> selectModifiers = new UniqueList<>();

    /**
     * The select columns for a select statement.
     */
    private final UniqueList<String> columns = new UniqueList<>();

    /**
     * The columns to update and the values to update to
     * for an update statement.
     */
    private final ColumnValues updateValues = new ColumnValues();

    /**
     * The tables to select from (including join operators) for
     * a select clause, or the tables to update or delete for update
     * or delete statements.
     */
    private final UniqueList<FromElement> fromClause = new UniqueList<>();

    /**
     * The where clause identifying the rows to select/update/delete.
     */
    private final UniqueList<String> whereClause = new UniqueList<>();

    /**
     * Contains all replacement objects which are inserted into the prepared
     * statement ? placeholders for the where Clause.
     */
    private final List<Object> whereClausePreparedStatementReplacements
        = new ArrayList<>();

    /** The order by columns, possibly including direction (ASC or DESC). */
    private final UniqueList<String> orderByColumns = new UniqueList<>();

    /**  The group by columns. */
    private final UniqueList<String> groupByColumns = new UniqueList<>();

    /** The having clause, or null for none. */
    private String having;

    /** The limit clause, or null for none. */
    private String limit;

    /**
     * Some databases need a clause to wrap the statement in for limit;
     * This field contains the starting part of the clause.
     * Null if the clause is not wanted.
     */
    private String preLimit;

    /**
     * Some databases need a clause to wrap the statement in for limit;
     * This field contains the end part of the clause.
     * Null if the clause is not wanted.
     */
    private String postLimit;

    /**
     * The offset clause, or null for none.
     */
    private String offset;

    /**
     * The set rowcount clause, or null for none.
     */
    private String rowcount;

    /**
     * The FOR UPDATE clause which should be rendered.
     */
    private String forUpdate;

    /** The type of the statement. */
    private Type type = Type.SELECT;

    /** The JDBC statement fetch size, if any. */
    private Integer fetchSize;

    /**
     * The parts of this query.
     */
    private final List<Query> parts = new ArrayList<>();

    /**
     * The Operator connecting the parts of the query.
     */
    private String partOperator;

    /**
     * Retrieve the modifier buffer in order to add modifiers to this
     * query.  E.g. DISTINCT and ALL.
     *
     * @return An UniqueList used to add modifiers.
     */
    public UniqueList<String> getSelectModifiers()
    {
        return selectModifiers;
    }

    /**
     * Retrieve the columns buffer in order to specify which columns
     * are returned in this query.
     *
     * @return An UniqueList used to add columns to be selected.
     */
    public UniqueList<String> getSelectClause()
    {
        return columns;
    }

    /**
     * Retrieve the values to update to in case of an update statement.
     *
     * @return A modifiable ColumnValues object containing the current
     *         update values.
     */
    public ColumnValues getUpdateValues()
    {
        return updateValues;
    }

    /**
     * Retrieve the from buffer in order to specify which tables are
     * involved in this query.
     *
     * @return An UniqueList used to add tables involved in the query.
     */
    public UniqueList<FromElement> getFromClause()
    {
        return fromClause;
    }

    /**
     * Retrieve the where buffer in order to specify the selection
     * criteria E.g. column_a=?.  Expressions added to the buffer will
     * be separated using AND.
     *
     * @return An UniqueList used to add selection criteria.
     */
    public UniqueList<String> getWhereClause()
    {
        return whereClause;
    }

    /**
     * Retrieves the replacements which are inserted into prepared statement
     * placeholders in the where clause.
     * The number and order of the elements in the list must correspond
     * to the order of the placeholders in the where clause string.
     *
     * @return A List containing all the replacements for the prepared
     *         statement placeholders, not null.
     */
    public List<Object> getWhereClausePreparedStatementReplacements()
    {
        return whereClausePreparedStatementReplacements;
    }

    /**
     * Returns all preparedStatementReplacements in the query.
     *
     * @return an unmodifiable list of all preparedStatementReplacements.
     */
    public List<Object> getPreparedStatementReplacements()
    {
        ArrayList<Object> result = new ArrayList<>();
        for (FromElement fromElement : fromClause)
        {
            result.addAll(fromElement.getPreparedStatementReplacements());
        }
        for (Query part : parts)
        {
            result.addAll(part.getPreparedStatementReplacements());
        }
        result.addAll(whereClausePreparedStatementReplacements);
        return Collections.unmodifiableList(result);
    }

    /**
     * Retrieve the order by columns buffer in order to specify which
     * columns are used to sort the results of the query.
     *
     * @return An UniqueList used to add columns to sort on.
     */
    public UniqueList<String> getOrderByClause()
    {
        return orderByColumns;
    }

    /**
     * Retrieve the group by columns buffer in order to specify which
     * columns are used to group the results of the query.
     *
     * @return An UniqueList used to add columns to group on.
     */
    public UniqueList<String> getGroupByClause()
    {
        return groupByColumns;
    }

    /**
     * Get the having clause.  This is used to restrict which
     * rows are returned based on some condition.
     *
     * @return A String that is the having clause.
     */
    public String getHaving()
    {
        return having;
    }

    /**
     * Set the having clause.  This is used to restrict which rows
     * are returned.
     *
     * @param having A String.
     */
    public void setHaving(final String having)
    {
        this.having = having;
    }

    /**
     * Get the limit number.  This is used to limit the number of
     * returned by a query in Postgres.
     *
     * @return A String with the limit.
     */
    public String getLimit()
    {
        return limit;
    }

    /**
     * Set the limit number.  This is used to limit the number of rows
     * returned by a query.
     *
     * @param limit A String.
     */
    public void setLimit(final String limit)
    {
        this.limit = limit;
    }

    /**
     * Get the Pre limit String. Oracle and DB2 want to encapsulate
     * a query into a subquery for limiting.
     *
     * @return A String with the preLimit.
     */
    public String getPreLimit()
    {
        return preLimit;
    }

    /**
     * Get the Pre limit String. Oracle and DB2 want to encapsulate
     * a query into a subquery for limiting.
     *
     * @param preLimit A String with the preLimit.
     */
    public void setPreLimit(final String preLimit)
    {
        this.preLimit = preLimit;
    }

    /**
     * Get the Post limit String. Oracle and DB2 want to encapsulate
     * a query into a subquery for limiting.
     *
     * @return A String with the preLimit.
     */
    public String getPostLimit()
    {
        return postLimit;
    }

    /**
     * Set the Post limit String. Oracle and DB2 want to encapsulate
     * a query into a subquery for limiting.
     *
     * @param postLimit A String with the postLimit.
     */
    public void setPostLimit(final String postLimit)
    {
        this.postLimit = postLimit;
    }

    /**
     * Get the offset number.  This is used to set the row where the
     * resultset starts.
     *
     * @return A String with the offset, or null if no offset is set.
     */
    public String getOffset()
    {
        return offset;
    }

    /**
     * Set the offset number.  This is used to set the row where the
     * resultset starts.
     *
     * @param offset A String.
     */
    public void setOffset(final String offset)
    {
        this.offset = offset;
    }

    /**
     * Get the rowcount number.  This is used to limit the number of
     * returned by a query in Sybase and MS SQL/Server.
     *
     * @return A String with the row count.
     */
    public String getRowcount()
    {
        return rowcount;
    }

    /**
     * Set the rowcount number.  This is used to limit the number of
     * rows returned by Sybase and MS SQL/Server.
     *
     * @param rowcount A String.
     */
    public void setRowcount(final String rowcount)
    {
        this.rowcount = rowcount;
    }

    /**
     * Sets the FOR UPDATE clause which should be added to the query.
     *
     * @param forUpdate the FOR UPDATE clause which should be added,
     *        null if no FOR UPDATE clause should be used.
     */
    public void setForUpdate(final String forUpdate)
    {
        this.forUpdate = forUpdate;
    }

    /**
     * Returns the FOR UPDATE clause which should be added to the query.
     *
     * @return the FOR UPDATE clause, or null if none should be added.
     */
    public String getForUpdate()
    {
        return forUpdate;
    }

    /**
     * True if this query has a limit clause registered.
     *
     * @return true if a limit clause exists.
     */
    public boolean hasLimit()
    {
        return ((preLimit != null)
                || (postLimit != null)
                || (limit != null));
    }

    /**
     * Returns the type of this SQL statement.
     *
     * @return type the new type, not null.
     */
    public Type getType()
    {
        return type;
    }

    /**
     * Sets the type of this SQL statement.
     *
     * @param type the new type, not null.
     *
     * @throws NullPointerException if <code>type</code> is null.
     */
    public void setType(final Type type)
    {
        if (type == null)
        {
            throw new NullPointerException("type is null");
        }
        this.type = type;
    }

    /**
     * Returns the JDBC statement fetch size to use for queries.
     *
     * @return the fetch size, or null if none is set.
     */
    public Integer getFetchSize()
    {
        return fetchSize;
    }

    /**
     * Sets the JDBC statement fetch size to use for queries.
     *
     * @param fetchSize the fetch size, or null for not set.
     */
    public void setFetchSize(final Integer fetchSize)
    {
        this.fetchSize = fetchSize;
    }

    /**
     * Returns the parts of this query.
     *
     * @return a modifiable list containing the parts of this query, not null.
     */
    public List<Query> getParts()
    {
        return parts;
    }

    /**
     * Returns the operator connecting the query parts.
     *
     * @return the operator connecting the parts, or null.
     */
    public String getPartOperator()
    {
        return this.partOperator;
    }

    /**
     * Sets the operator connecting the query parts.
     *
     * @param partOperator the operator connecting the parts, or null.
     */
    public void setPartOperator(final String partOperator)
    {
        this.partOperator = partOperator;
    }

    /**
     * Outputs the query statement.
     *
     * @return A String with the query statement.
     */
    @Override
    public String toString()
    {
        return toStringBuilder(new StringBuilder()).toString();
    }

    /**
     * Appends the query to a string builder.
     *
     * @param stringBuilder the stringBuilder to append to, not null.
     *
     * @return the modified passed in string builder.
     */
    public StringBuilder toStringBuilder(final StringBuilder stringBuilder)
    {
        if (preLimit != null)
        {
            stringBuilder.append(preLimit);
        }

        if (rowcount != null)
        {
            stringBuilder.append(SET_ROWCOUNT)
            .append(rowcount)
            .append(" ");
        }

        if (parts.isEmpty())
        {
            if (Type.SELECT == type)
            {
                stringBuilder.append(SELECT)
                .append(StringUtils.join(selectModifiers.iterator(), " "))
                .append(StringUtils.join(columns.iterator(), ", "))
                .append(FROM);
            }
            else if (Type.UPDATE == type)
            {
                stringBuilder.append(UPDATE);
            }
            else if (Type.DELETE == type)
            {
                stringBuilder.append(DELETE_FROM);
            }

            boolean first = true;
            for (Iterator<FromElement> it = fromClause.iterator(); it.hasNext();)
            {
                FromElement fromElement = it.next();

                if (!first && fromElement.getJoinCondition() == null)
                {
                    stringBuilder.append(", ");
                }
                first = false;
                stringBuilder.append(fromElement.toString());
            }

            if (Type.SELECT == type
                    && (forUpdate != null)
                    && !"FOR UPDATE".equals(forUpdate))
            {
                stringBuilder.append(" ").append(forUpdate);
            }

            if (Type.UPDATE == type)
            {
                stringBuilder.append(SET);
                first = true;
                for (Map.Entry<Column, JdbcTypedValue> entry
                        : updateValues.entrySet())
                {
                    if (!first)
                    {
                        stringBuilder.append(",");
                    }
                    Column column = entry.getKey();
                    String columnName = column.getColumnName();
                    if (columnName == null)
                    {
                        columnName = column.getSqlExpression();
                    }
                    stringBuilder.append(columnName);
                    if (entry.getValue().getSqlExpression() == null)
                    {
                        stringBuilder.append("=?");
                    }
                    else
                    {
                        Column sqlExpression = entry.getValue().getSqlExpression();
                        stringBuilder.append("=")
                        .append(sqlExpression.getSqlExpression());
                    }
                    first = false;
                }
            }

            if (!whereClause.isEmpty())
            {
                stringBuilder.append(WHERE)
                .append(StringUtils.join(whereClause.iterator(), AND));
            }
            if (!groupByColumns.isEmpty())
            {
                stringBuilder.append(GROUP_BY)
                .append(StringUtils.join(groupByColumns.iterator(), ", "));
            }
            if (having != null)
            {
                stringBuilder.append(HAVING)
                .append(having);
            }
        }
        else
        {
            boolean first = true;
            for (Query part : parts)
            {
                if (!first)
                {
                    stringBuilder.append(partOperator);
                }
                stringBuilder.append('(');
                part.toStringBuilder(stringBuilder);
                stringBuilder.append(')');
                first = false;
            }
        }
        if (!orderByColumns.isEmpty())
        {
            stringBuilder.append(ORDER_BY)
            .append(StringUtils.join(orderByColumns.iterator(), ", "));
        }
        if (limit != null)
        {
            stringBuilder.append(LIMIT)
            .append(limit);
        }
        if (offset != null)
        {
            stringBuilder.append(OFFSET)
            .append(offset);
        }
        if (rowcount != null)
        {
            stringBuilder.append(SET_ROWCOUNT)
            .append("0");
        }
        if (postLimit != null)
        {
            stringBuilder.append(postLimit);
        }
        if (Type.SELECT == type
                && (forUpdate != null)
                && "FOR UPDATE".equals(forUpdate))
        {
            stringBuilder.append(" ").append(forUpdate);
        }

        return stringBuilder;
    }

    /**
     * Returns a String to display this query.
     *
     * @return the SQL query for display.
     *
     * @exception TorqueException Trouble creating the query string.
     */
    public String getDisplayString()
            throws TorqueException
    {
        StringBuilder stringBuilder = new StringBuilder();
        toStringBuilder(stringBuilder);
        stringBuilder.append(" Replacements: [");
        boolean first = true;
        for (Object replacement : getPreparedStatementReplacements())
        {
            if (!first)
            {
                stringBuilder.append(",");
            }
            stringBuilder.append(replacement);
            first = false;
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}
