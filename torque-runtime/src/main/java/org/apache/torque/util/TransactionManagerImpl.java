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
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

/**
 * Standard connection and transaction management for Torque.
 * Uses JDBC connection operations and Torque's own database pools
 * for managing connections and transactions.
 *
 * @author <a href="mailto:stephenh@chase3000.com">Stephen Haberman</a>
 * @version $Id: TransactionManagerImpl.java 1880635 2020-08-06 11:28:14Z gk $
 */
public class TransactionManagerImpl implements TransactionManager
{

    /** The log. */
    private static final Logger log = LogManager.getLogger(TransactionManagerImpl.class);

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
    @Override
    public TorqueConnection begin() throws TorqueException
    {
        return begin(Torque.getDefaultDB());
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
    @Override
    public TorqueConnection begin(String dbName) throws TorqueException
    {
        TorqueConnection con = new TorqueConnectionImpl(Torque.getConnection(dbName));
        return con;
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
    @Override
    public void commit(Connection con) throws TorqueException
    {
        if (con == null)
        {
            throw new NullPointerException("Connection object was null. "
                    + "This could be due to a misconfiguration of the "
                    + "DataSourceFactory. Check the logs and Torque.properties "
                    + "to better determine the cause.");
        }

        try
        {
            if (con.getMetaData().supportsTransactions())
            {
                if (!con.getAutoCommit()) 
                {
                    con.commit();
                }

                if (con instanceof TorqueConnection)
                {
                    ((TorqueConnection) con).setCommitted(true);
                }
            }
        }
        catch (SQLException e)
        {
            throw ExceptionMapper.getInstance().toTorqueException(e);
        }
        finally
        {
            Torque.closeConnection(con);
        }
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
    @Override
    public void rollback(Connection con) throws TorqueException
    {
        if (con == null)
        {
            throw new TorqueException("Connection object was null. "
                    + "This could be due to a misconfiguration of the "
                    + "DataSourceFactory. Check the logs and Torque.properties "
                    + "to better determine the cause.");
        }
        else
        {
            try
            {
            	if (con.isClosed()) {
            		log.warn("Connection is already closed. "
                            + "Reason could be, if rollback was already called before.");
            	} else if (con.getMetaData().supportsTransactions()
                        && !con.getAutoCommit())
                {
                    con.rollback();

                    if (con instanceof TorqueConnection)
                    {
                        ((TorqueConnection) con).setRolledBack(true);
                    }
                }
            }
            catch (SQLException e)
            {
                log.error("An attempt was made to rollback a transaction "
                        + "but the database did not allow the operation to be "
                        + "rolled back.", e);
                throw ExceptionMapper.getInstance().toTorqueException(e);
            }
            finally
            {
                Torque.closeConnection(con);
            }
        }
    }

    /**
     * Roll back a transaction without throwing errors if they occur.
     * A null Connection argument is logged at the debug level and other
     * errors are logged at warn level.
     *
     * @param con The Connection for the transaction.
     * @see TransactionManagerImpl#rollback(Connection)
     */
    @Override
    public void safeRollback(Connection con)
    {
        if (con == null)
        {
            log.debug("called safeRollback with null argument");
        }
        else
        {
            try
            {
                rollback(con);
            }
            catch (TorqueException e)
            {
                log.warn("An error occured during rollback.", e);
            }
        }
    }
}
