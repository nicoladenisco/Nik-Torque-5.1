package org.apache.torque.adapter;

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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.apache.torque.sql.Query;
import org.apache.torque.util.UniqueList;

/**
 * This code should be used for an Oracle database pool.
 *
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:bschneider@vecna.com">Bill Schneider</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id: OracleAdapter.java 1850965 2019-01-10 17:21:29Z painter $
 */
public class OracleAdapter extends AbstractAdapter
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 8966976210230241194L;

    /**
     * Empty constructor.
     */
    protected OracleAdapter()
    {
        // empty
    }

    /**
     * This method is used to ignore case.
     *
     * @param in The string to transform to upper case.
     * @return The upper case string.
     */
    @Override
    public String toUpperCase(final String in)
    {
        return new StringBuilder("UPPER(").append(in).append(")").toString();
    }

    /**
     * This method is used to ignore case.
     *
     * @param in The string whose case to ignore.
     * @return The string in a case that can be ignored.
     */
    @Override
    public String ignoreCase(final String in)
    {
        return new StringBuilder("UPPER(").append(in).append(")").toString();
    }

    /**
     * @see org.apache.torque.adapter.Adapter#getIDMethodType()
     */
    @Override
    public IDMethod getIDMethodType()
    {
        return IDMethod.SEQUENCE;
    }

    /**
     * @see org.apache.torque.adapter.AbstractAdapter#getIDMethodSQL(java.lang.Object)
     * 
     * Returns the next key from a sequence.  Uses the following
     * implementation:
     *
     * <pre>
     * 	<code>select sequenceName.nextval from dual</code>
     * </pre>
     *
     * @param sequenceName The name of the sequence (should be of type <code>String</code>).
     * @return SQL to retrieve the next database key.
     * @see org.apache.torque.adapter.Adapter#getIDMethodSQL(Object)
     */
    @Override
    public String getIDMethodSQL(final Object sequenceName)
    {
        return ("select " + sequenceName + ".nextval from dual");
    }

    /**
     * Locks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to lock.
     * @exception SQLException No Statement could be created or executed.
     */
    @Override
    public void lockTable(final Connection con, final String table) throws SQLException
    {
        try (Statement statement = con.createStatement())
        {
            statement.execute("SELECT * FROM " +  table + " FOR UPDATE");
        }
    }

    /**
     * Unlocks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to unlock.
     * @exception SQLException No Statement could be created or executed.
     */
    @Override
    public void unlockTable(final Connection con, final String table) throws SQLException
    {
        // Tables in Oracle are unlocked when a commit is issued.  The
        // user may have issued a commit but do it here to be sure.
        con.commit();
    }

    /**
     * Build Oracle-style query with limit or offset.
     * If the original SQL is in variable: query then the requlting
     * SQL looks like this:
     * <pre>
     * SELECT B.* FROM (
     *          SELECT A.*, rownum as TORQUE$ROWNUM FROM (
     *                  query
     *          ) A
     *     ) B WHERE B.TORQUE$ROWNUM &gt; offset AND B.TORQUE$ROWNUM
     *     &lt;= offset + limit
     * </pre>
     *
     * @param query The query to modify
     * @param offset the offset Value
     * @param limit the limit Value
     */
    @Override
    public void generateLimits(final Query query, final long offset, final int limit)
    {
        StringBuilder preLimit = new StringBuilder()
                .append("SELECT B.* FROM ( ")
                .append("SELECT A.*, rownum AS TORQUE$ROWNUM FROM ( ");

        StringBuilder postLimit = new StringBuilder()
                .append(" ) A ")
                .append(" ) B WHERE ");

        if (offset > 0)
        {
            postLimit.append(" B.TORQUE$ROWNUM > ")
            .append(offset);

            if (limit >= 0)
            {
                postLimit.append(" AND B.TORQUE$ROWNUM <= ")
                .append(offset + limit);
            }
        }
        else
        {
            postLimit.append(" B.TORQUE$ROWNUM <= ")
            .append(limit);
        }

        query.setPreLimit(preLimit.toString());
        query.setPostLimit(postLimit.toString());
        query.setLimit(null);

        // the query must not contain same column names or aliases.
        // Find double column names and aliases and create unique aliases
        // TODO: does not work for functions yet
        UniqueList<String> selectColumns = query.getSelectClause();
        int replacementSuffix = 0;
        Set<String> columnNames = new HashSet<>();
        // first pass: only remember aliased columns
        // No replacements need to take place because double aliases
        // are not allowed anyway
        // So alias names will be retained
        for (String selectColumn : selectColumns)
        {
            // check for sql function
            if ((selectColumn.indexOf('(') != -1)
                    || (selectColumn.indexOf(')') != -1))
            {
                // Sql function. Disregard.
                continue;
            }

            // check if alias name exists
            int spacePos = selectColumn.lastIndexOf(' ');
            if (spacePos == -1)
            {
                // no alias, disregard for now
                continue;
            }

            String aliasName = selectColumn.substring(spacePos + 1);
            columnNames.add(aliasName);
        }

        // second pass. Regard ordinary columns only
        for (ListIterator<String> columnIt = selectColumns.listIterator();
                columnIt.hasNext();)
        {
            String selectColumn = columnIt.next();

            // check for sql function
            if ((selectColumn.indexOf('(') != -1)
                    || (selectColumn.indexOf(')') != -1))
            {
                // Sql function. Disregard.
                continue;
            }

            {
                int spacePos = selectColumn.lastIndexOf(' ');
                if (spacePos != -1)
                {
                    // alias, already processed in first pass
                    continue;
                }
            }
            // split into column name and tableName
            String column;
            {
                int dotPos = selectColumn.lastIndexOf('.');
                if (dotPos != -1)
                {
                    column = selectColumn.substring(dotPos + 1);
                }
                else
                {
                    column = selectColumn;
                }
            }
            if (columnNames.contains(column))
            {
                // column needs to be aliased
                // get replacement name
                String aliasName;
                do
                {
                    aliasName = "a" + replacementSuffix;
                    ++replacementSuffix;
                }
                while (columnNames.contains(aliasName));

                selectColumn = selectColumn + " " + aliasName;
                columnIt.set(selectColumn);
                columnNames.add(aliasName);
            }
            else
            {
                columnNames.add(column);
            }
        }
    }

    /**
     * This method is for the SqlExpression.quoteAndEscape rules.  The rule is,
     * any string in a SqlExpression with a BACKSLASH will either be changed to
     * "\\" or left as "\".  SapDB does not need the escape character.
     *
     * @return false.
     */
    @Override
    public boolean escapeText()
    {
        return false;
    }

    /**
     * Whether an escape clause in like should be used.
     * Example : select * from AUTHOR where AUTHOR.NAME like '\_%' ESCAPE '\';
     *
     * Oracle needs this, so this implementation always returns
     * <code>true</code>.
     *
     * @return whether the escape clause should be appended or not.
     */
    @Override
    public boolean useEscapeClauseForLike()
    {
        return true;
    }

    /**
     * Whether to use the MINUS operator instead of the EXCEPT operator.
     *
     * Oracle needs this, so this implementation always returns
     * <code>true</code>.
     *
     * @return whether to use the MINUS operator instead of the EXCEPT operator.
     */
    @Override
    public boolean useMinusForExcept()
    {
        return true;
    }
}
