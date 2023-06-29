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
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.apache.torque.TorqueException;
import org.apache.torque.sql.Query;

/**
 * This class is the abstract base for any database adapter
 * Support for new databases is added by subclassing this
 * class and implementing its abstract methods, and by
 * registering the new database adapter and its corresponding
 * JDBC driver in the service configuration file.
 *
 * <p>The Torque database adapters exist to present a uniform
 * interface to database access across all available databases.  Once
 * the necessary adapters have been written and configured,
 * transparent swapping of databases is theoretically supported with
 * <i>zero code changes</i> and minimal configuration file
 * modifications.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:vido@ldh.org">Augustin Vidovic</a>
 * @author <a href="mailto:greg.monroe@dukece.com">Greg Monroe</a>
 * @version $Id: AbstractAdapter.java 1850586 2019-01-06 18:46:35Z tv $
 */
public abstract class AbstractAdapter implements Adapter
{
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** A flag to determine whether Statement#getGeneratedKeys() should be used. */
    private boolean useGetGeneratedKeys = false;

    /**
     * Empty constructor.
     */
    protected AbstractAdapter()
    {
        // empty
    }

    /**
     * Wraps the input string in a database function to change it to upper case.
     *
     * @param in The string to transform to upper case, may be a literal string,
     *        a prepared statement replacement placeholder(*) or any other
     *        database expression.
     *
     * @return The wrapped input string, so that the database evaluates the
     *         returned expression to the upper case of the input.
     */
    @Override
    public abstract String toUpperCase(String in);

    /**
     * Returns the character used to indicate the beginning and end of
     * a piece of text used in a SQL statement (generally a single
     * quote).
     *
     * @return The text delimiter.
     */
    @Override
    public char getStringDelimiter()
    {
        return '\'';
    }

    /**
     * Returns the constant from the {@link
     * org.apache.torque.adapter.IDMethod} interface denoting which
     * type of primary key generation method this type of RDBMS uses.
     *
     * @return IDMethod constant
     */
    @Override
    public abstract IDMethod getIDMethodType();

    /**
     * Returns SQL used to get the most recently inserted primary key.
     * Databases which have no support for this return
     * <code>null</code>.
     *
     * @param obj Information used for key generation.
     * @return The most recently inserted database key.
     */
    @Override
    public abstract String getIDMethodSQL(Object obj);

    /**
     * Returns the clause which acquires a write lock on a row
     * when doing a select.
     *
     * @return the SQL clause to acquire a write lock.
     *         This implementation returns "FOR UPDATE";
     */
    @Override
    public String getUpdateLockClause()
    {
        return "FOR UPDATE";
    }

    /**
     * Locks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to lock.
     *
     * @throws SQLException No Statement could be created or executed.
     */
    @Override
    public abstract void lockTable(Connection con, String table)
            throws SQLException;

    /**
     * Unlocks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to unlock.
     *
     * @throws SQLException No Statement could be created or executed.
     */
    @Override
    public abstract void unlockTable(Connection con, String table)
            throws SQLException;

    /**
     * Wraps the input string in a database function to change it to
     * a case-insensitive representation.
     *
     * @param in The string to transform to a case-insensitive representation,
     *        may be a literal string, a prepared statement replacement
     *        placeholder(*) or any other database expression.
     *
     * @return The wrapped input string, so that the database evaluates the
     *         returned expression to a case-insensitive representation
     *         of the input.
     */
    @Override
    public abstract String ignoreCase(String in);

    /**
     * This method is used to ignore case in an ORDER BY clause.
     * Usually it is the same as ignoreCase, but some databases
     * (hsqldb for example) do not use the same SQL in ORDER BY
     * and other clauses.
     *
     * @param in The string whose case to ignore.
     *
     * @return The string in a case that can be ignored.
     */
    @Override
    public String ignoreCaseInOrderBy(final String in)
    {
        return ignoreCase(in);
    }

    /**
     * Returns whether the database can natively limit the size of the ResultSet
     * of a query.
     *
     * @return true if the database natively supports limiting the
     *         size of the resultset.
     */
    @Override
    public boolean supportsNativeLimit()
    {
        return true;
    }

    /**
     * Returns whether the database natively supports returning results
     * starting at an offset position other than 0.
     *
     * @return true if the database natively supports returning
     *         results starting at an offset position other than 0.
     */
    @Override
    public boolean supportsNativeOffset()
    {
        return true;
    }

    /**
     * This method is used to generate the database specific query
     * extension to limit the number of record returned.
     *
     * @param query The query to modify
     * @param offset the offset Value
     * @param limit the limit Value
     *
     * @throws TorqueException if any error occurs when building the query
     */
    @Override
    public void generateLimits(final Query query, final long offset, final int limit)
            throws TorqueException
    {
        if (supportsNativeLimit())
        {
            query.setLimit(String.valueOf(limit));
        }
    }

    /**
     * This method is for the SqlExpression.quoteAndEscape rules.  The rule is,
     * any string in a SqlExpression with a BACKSLASH will either be changed to
     * "\\" or left as "\".
     *
     * @return true if the database needs to escape text in SqlExpressions.
     */

    @Override
    public boolean escapeText()
    {
        return true;
    }

    /**
     * Whether ILIKE should be used for case insensitive like clauses.
     *
     * As most databases do not use ILIKE, this implementation returns false.
     * This behaviour may be overwritten in subclasses.
     *
     * @return true if ilike should be used for case insensitive likes,
     *         false if ignoreCase should be applied to the compared strings.
     */
    @Override
    public boolean useIlike()
    {
        return false;
    }

    /**
     * Whether an escape clause in like should be used.
     * Example : select * from AUTHOR where AUTHOR.NAME like '\_%' ESCAPE '\';
     *
     * As most databases do not need the escape clause, this implementation
     * always returns <code>false</code>. This behaviour can be overwritten
     * in subclasses.
     *
     * @return whether the escape clause should be appended or not.
     */
    @Override
    public boolean useEscapeClauseForLike()
    {
        return false;
    }

    /**
     * Whether to use the MINUS operator instead of the EXCEPT operator.
     *
     * As most databases do not need to replace the EXCEPT operator
     * by the MINUS operator, this implementation always returns
     * <code>false</code>.
     * This behaviour can be overwritten in subclasses.
     *
     * @return whether to use the MINUS operator instead of the EXCEPT operator.
     */
    @Override
    public boolean useMinusForExcept()
    {
        return false;
    }

    /**
     * whether Statement#getGeneratedKeys() should be used.
     *
     * @return a <code>boolean</code> value
     */
    public boolean useGetGeneratedKeys()
    {
        return this.useGetGeneratedKeys;
    }

    /**
     * Update static capabilities of the adapter with actual
     * readings based on the JDBC meta-data
     *
     * @param dmd database meta data
     * @throws SQLException if there are problems getting the JDBC meta data
     */
    public void setCapabilities(DatabaseMetaData dmd) throws SQLException
    {
        this.useGetGeneratedKeys = dmd.supportsGetGeneratedKeys();
    }
}
