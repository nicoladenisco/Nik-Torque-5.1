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

import org.apache.torque.sql.Query;

/**
 * This is used to connect to an embedded Apache Derby Database using
 * the supplied JDBC driver.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>, tins
 * @version $Id: DerbyAdapter.java 1850591 2019-01-06 19:00:34Z tv $
 */
public class DerbyAdapter extends AbstractAdapter
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 6265962681516206415L;

    /**
     * Empty constructor.
     */
    protected DerbyAdapter()
    {
        // empty
    }

    /**
     * This method is used to ignore case.
     *
     * @param str The string to transform to upper case.
     * @return The upper case string.
     */
    @Override
    public String toUpperCase(String str)
    {
        return new StringBuilder("UPPER(")
                .append(str)
                .append(")")
                .toString();
    }

    /**
     * This method is used to ignore case.
     *
     * @param str The string whose case to ignore.
     * @return The string in a case that can be ignored.
     */
    @Override
    public String ignoreCase(String str)
    {
        return toUpperCase(str);
    }

    /**
     * @see org.apache.torque.adapter.Adapter#getIDMethodType()
     */
    @Override
    public IDMethod getIDMethodType()
    {
        return IDMethod.AUTO_INCREMENT;
    }

    /**
     * Returns the SQL to get the database key of the last row
     * inserted, which in this case is
     * <code>VALUES IDENTITY_VAL_LOCAL()</code>.
     *
     * @see org.apache.torque.adapter.Adapter#getIDMethodSQL(Object obj)
     */
    @Override
    public String getIDMethodSQL(Object obj)
    {
        return "VALUES IDENTITY_VAL_LOCAL()";
    }

    /**
     * Locks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to lock.
     * @exception SQLException No Statement could be created or executed.
     */
    @Override
    public void lockTable(Connection con, String table)
            throws SQLException
    {
        try (Statement statement = con.createStatement())
        {
            StringBuilder stmt = new StringBuilder();
            stmt.append("LOCK TABLE ")
            .append(table).append(" IN EXCLUSIVE MODE");
            statement.executeUpdate(stmt.toString());
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
    public void unlockTable(Connection con, String table)
            throws SQLException
    {
        // empty
    }

    /**
     * Whether backslashes (\) should be escaped in explicit SQL strings.
     * If true is returned, a BACKSLASH will be changed to "\\". If false
     * is returned, a BACKSLASH will be left as "\".
     *
     * As derby does not need escaping of Backslashes, this method always
     * returns false.
     *
     * @return true if the database needs to escape backslashes
     *         in SqlExpressions.
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
     * Derby needs this, so this implementation always returns
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
     * Derby supports this feature but does not report it via JDBC
     *
     * @see org.apache.torque.adapter.AbstractAdapter#useGetGeneratedKeys()
     */
    @Override
    public boolean useGetGeneratedKeys()
    {
        return true;
    }

    /**
     * Build Derby-style query with limit or offset.
     * The resulting query may look like this:
     * <pre>
     *  select * from TABLENAME fetch next 3 rows only;
     *  select * from TABLENAME offset 3 rows fetch next 3 rows only;
     * </pre>
     *
     * @param query The query to modify.
     * @param offset the offset value.
     * @param limit the limit value.
     */
    @Override
    public void generateLimits(Query query, long offset, int limit)
    {
        StringBuilder postLimit = new StringBuilder();

        if (offset > 0)
        {
            postLimit.append(" OFFSET ").append(offset).append(" ROWS");
        }

        if (limit >= 0)
        {
            postLimit.append(" FETCH NEXT ").append(limit).append(" ROWS ONLY");
        }

        query.setPostLimit(postLimit.toString());
        query.setLimit(null);
        query.setOffset(null);
    }
}
