package org.apache.torque.util;

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

import org.apache.torque.TorqueException;

/**
 * Encapsulates transaction and connection handling within Torque.
 *
 * If the underlying database does not support transaction or the database
 * pool returns autocommit connections, the commit and rollback methods
 * fallback to simple connection pool handling.
 *
 * @author <a href="mailto:stephenh@chase3000.com">Stephen Haberman</a>
 * @version $Id: Transaction.java 1839284 2018-08-27 08:57:56Z tv $
 */
public final class Transaction
{
    /** The transaction manager to use. */
    private static TransactionManager transactionManager;

    /**
     * Private constructor to prevent instantiation.
     *
     * Class contains only static method and should therefore not be
     * instantiated.
     */
    private Transaction()
    {
        // empty
    }

    /**
     * Sets the transaction manager to use.
     *
     * @param transactionManager the transaction manager to use.
     */
    public static void setTransactionManager(
            final TransactionManager transactionManager)
    {
        Transaction.transactionManager = transactionManager;
    }

    /**
     * Returns the current transaction manager.
     *
     * @return the current transaction manager.
     */
    public static TransactionManager getTransactionManager()
    {
        return transactionManager;
    }

    /**
     * Begin a transaction by retrieving a connection from the default database
     * connection pool.
     * WARNING: If the database does not support transaction or the pool has set
     * autocommit to true on the connection, the database will commit after
     * every statement, regardless of when a commit or rollback is issued.
     *
     * @return The Connection for the transaction.
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static TorqueConnection begin() throws TorqueException
    {
        return transactionManager.begin();
    }

    /**
     * Begin a transaction by retrieving a connection from the named database
     * connection pool.
     * WARNING: If the database does not support transaction or the pool has set
     * autocommit to true on the connection, the database will commit after
     * every statement, regardless of when a commit or rollback is issued.
     *
     * @param dbName Name of database.
     *
     * @return The Connection for the transaction.
     *
     * @throws TorqueException If the connection cannot be retrieved.
     */
    public static TorqueConnection begin(final String dbName) throws TorqueException
    {
        return transactionManager.begin(dbName);
    }


    /**
     * Commit a transaction and close the connection.
     * If the connection is in autocommit mode or the database does not support
     * transactions, only a connection close is performed
     *
     * @param con The Connection for the transaction.
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static void commit(final Connection con) throws TorqueException
    {
        transactionManager.commit(con);
    }

    /**
     * Roll back a transaction and release the connection.
     * In databases that do not support transactions or if autocommit is true,
     * no rollback will be performed, but the connection will be closed anyway.
     *
     * @param con The Connection for the transaction.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static void rollback(final Connection con) throws TorqueException
    {
        transactionManager.rollback(con);
    }

    /**
     * Roll back a transaction without throwing errors if they occur.
     * A null Connection argument is logged at the debug level and other
     * errors are logged at warn level.
     *
     * @param con The Connection for the transaction.
     * @see Transaction#rollback(Connection)
     */
    public static void safeRollback(final Connection con)
    {
        transactionManager.safeRollback(con);
    }
}
