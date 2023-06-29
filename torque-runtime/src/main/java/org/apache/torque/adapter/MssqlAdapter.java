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

import org.apache.torque.TorqueException;
import org.apache.torque.sql.Query;

/**
 * This is used to connect to a MSSQL database.
 * This is tested with the jtds driver from sourceforge.
 *
 * @author <a href="mailto:gonzalo.diethelm@sonda.com">Gonzalo Diethelm</a>
 * @version $Id: MssqlAdapter.java 1848527 2018-12-09 16:27:22Z tv $
 */
public class MssqlAdapter extends AbstractAdapter
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = -2924485528975497044L;

    /**
     * Empty constructor.
     */
    protected MssqlAdapter()
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
    public String toUpperCase(String in)
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
    public String ignoreCase(String in)
    {
        return new StringBuilder("UPPER(").append(in).append(")").toString();
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
     * Returns the last value from an identity column (available on a
     * per-session basis from the global variable
     * <code>@@identity</code>).
     *
     * @see org.apache.torque.adapter.Adapter#getIDMethodSQL(Object obj)
     */
    @Override
    public String getIDMethodSQL(Object unused)
    {
        return "select @@identity";
    }

    /**
     * Returns the clause which acquires a write lock on a row
     * when doing a select.
     *
     * @return the SQL clause to acquire a write lock.
     *         This implementation returns "WITH (UPDLOCK)";
     */
    @Override
    public String getUpdateLockClause()
    {
        return "WITH (UPDLOCK)";
    }

    /**
     * Locks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to lock.
     * @exception SQLException No Statement could be created or executed.
     */
    @Override
    public void lockTable(Connection con, String table) throws SQLException
    {
        try (Statement statement = con.createStatement())
        {
            statement.execute("SELECT * FROM " +  table + " WITH (TABLOCKX)");
        }
    }

    /**
     * Tries to unlock the specified table.
     * This implementation does nothing as tables in MSSQL are unlocked
     * when a commit or rollback is issued.
     * This has unintended side effects, as well as we do not know whether
     * to use commit or rollback.
     * The lock will go away automatically when the transaction is ended.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to unlock.
     * @throws SQLException No Statement could be created or executed.
     */
    @Override
    public void unlockTable(Connection con, String table) throws SQLException
    {
        // do nothing
    }

    /**
     * Returns whether the database natively supports returning results
     * starting at an offset position other than 0.
     * Return false for MSSQL
     *
     * @return true if the database natively supports returning
     *         results starting at an offset position other than 0.
     */
    @Override
    public boolean supportsNativeOffset()
    {
        return false;
    }

    /**
     * Modify a query to add limit and offset values for MSSQL.
     *
     * @param query The query to modify
     * @param offset the offset Value
     * @param limit the limit Value
     *
     * @throws TorqueException if any error occurs when building the query
     */
    @Override
    public void generateLimits(Query query, long offset, int limit)
            throws TorqueException
    {
        if (limit < 0 && offset >= 0) // Offset only test
        {
            return;
        }
        if (limit + offset > 0)
        {
            query.setRowcount(String.valueOf(limit + offset));
        }
        else if (limit + offset == 0)
        {
            // This is necessary to create the empty result set that Torque expects
            query.getWhereClause().add("1=0");
        }
    }

    /**
     * Determines whether backslashes (\) should be escaped in explicit SQL
     * strings. If true is returned, a BACKSLASH will be changed to "\\".
     * If false is returned, a BACKSLASH will be left as "\".
     *
     * Sybase (and MSSQL) doesn't define a default escape character,
     * so false is returned.
     *
     * @return false
     * @see org.apache.torque.adapter.Adapter#escapeText()
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
     * MSSQL needs this, so this implementation always returns
     * <code>true</code>.
     *
     * @return whether the escape clause should be appended or not.
     */
    @Override
    public boolean useEscapeClauseForLike()
    {
        return true;
    }
}
