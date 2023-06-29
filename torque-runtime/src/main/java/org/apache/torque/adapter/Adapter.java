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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.apache.torque.TorqueException;
import org.apache.torque.sql.Query;

/**
 * <code>DB</code> defines the interface for a Torque database
 * adapter.  Support for new databases is added by implementing this
 * interface. A couple of default settings is provided by
 * subclassing <code>AbstractDBAdapter</code>. The new database adapter
 * and its corresponding JDBC driver need to be registered in the service
 * configuration file.
 *
 * <p>The Torque database adapters exist to present a uniform
 * interface to database access across all available databases.  Once
 * the necessary adapters have been written and configured,
 * transparent swapping of databases is theoretically supported with
 * <i>zero code changes</i> and minimal configuration file
 * modifications.
 *
 * All database adapters need to be thread safe, as they are instantiated
 * only once fore a given configured database and may be accessed
 * simultaneously from several threads.
 * *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:vido@ldh.org">Augustin Vidovic</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @author <a href="mailto:greg.monroe@dukece.com">Greg Monroe</a>
 * @version $Id: Adapter.java 1850586 2019-01-06 18:46:35Z tv $
 */
public interface Adapter extends Serializable
{
    /** Key for the configuration which contains database adapters. */
    String ADAPTER_KEY = "adapter";

    /** Key for the configuration which contains database drivers. */
    String DRIVER_KEY = "driver";

    /** Special adapter for auto-detection. */
    String AUTODETECT_ADAPTER = "auto";

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
    String toUpperCase(String in);

    /**
     * Returns the character used to indicate the beginning and end of
     * a piece of text used in a SQL statement (generally a single
     * quote).
     *
     * @return The text delimiter.
     */
    char getStringDelimiter();

    /**
     * Returns the constant from the {@link
     * org.apache.torque.adapter.IDMethod} interface denoting which
     * type of primary key generation method this type of RDBMS uses.
     *
     * @return IDMethod constant
     */
    IDMethod getIDMethodType();

    /**
     * Returns SQL used to get the most recently inserted primary key.
     * Databases which have no support for this return
     * <code>null</code>.
     *
     * @param obj Information used for key generation.
     *
     * @return The most recently inserted database key.
     */
    String getIDMethodSQL(Object obj);

    /**
     * Returns the clause which acquires a write lock on a row
     * when doing a select.
     * Most databases use the "for update" clause.
     *
     * @return the SQL clause to acquire a write lock.
     */
    String getUpdateLockClause();

    /**
     * Locks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to lock.
     *
     * @throws SQLException No Statement could be created or executed.
     */
    void lockTable(Connection con, String table)
            throws SQLException;

    /**
     * Unlocks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to unlock.
     *
     * @throws SQLException No Statement could be created or executed.
     */
    void unlockTable(Connection con, String table)
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
    String ignoreCase(String in);

    /**
     * This method is used to ignore case in an ORDER BY clause.
     * Usually it is the same as ignoreCase, but some databases
     * (hsqldb for example) does not use the same SQL in ORDER BY
     * and other clauses.
     *
     * @param in The string whose case to ignore.
     *
     * @return The string in a case that can be ignored.
     */
    String ignoreCaseInOrderBy(String in);

    /**
     * This method is used to check whether the database natively
     * supports limiting the size of the resultset.
     *
     * @return true if the database natively supports limiting the
     *         size of the resultset.
     */
    boolean supportsNativeLimit();

    /**
     * This method is used to check whether the database natively
     * supports returning results starting at an offset position other
     * than 0.
     *
     * @return true if the database natively supports returning
     *         results starting at an offset position other than 0.
     */
    boolean supportsNativeOffset();

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
    void generateLimits(Query query, long offset, int limit)
            throws TorqueException;

    /**
     * Determines whether backslashes (\) should be escaped in explicit SQL
     * strings. If true is returned, a BACKSLASH will be changed to "\\".
     * If false is returned, a BACKSLASH will be left as "\".
     *
     * @return true if the database needs to escape backslashes
     *         in SqlExpressions.
     */
    boolean escapeText();

    /**
     * Whether ILIKE should be used for case insensitive like clauses.
     *
     * @return true if ilike should be used for case insensitive likes,
     *         false if ignoreCase should be applied to the compared strings.
     */
    boolean useIlike();

    /**
     * Whether an escape clause in like should be used.
     * Example : select * from AUTHOR where AUTHOR.NAME like '\_%' ESCAPE '\';
     *
     * @return whether the escape clause should be appended or not.
     */
    boolean useEscapeClauseForLike();

    /**
     * Whether to use the MINUS operator instead of the EXCEPT operator.
     *
     * @return whether to use the MINUS operator instead of the EXCEPT operator.
     */
    boolean useMinusForExcept();

    /**
     * whether Statement#getGeneratedKeys() should be used.
     *
     * @return a <code>boolean</code> value
     */
    boolean useGetGeneratedKeys();

    /**
     * Update static capabilities of the adapter with actual
     * readings based on the JDBC meta-data
     *
     * @param dmd database meta data
     * @throws SQLException if there are problems getting the JDBC meta data
     */
    void setCapabilities(DatabaseMetaData dmd) throws SQLException;
}
