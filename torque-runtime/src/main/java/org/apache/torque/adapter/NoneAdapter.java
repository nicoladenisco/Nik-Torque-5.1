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

/**
 * This DatabaseHandler is used when you do not have a database
 * installed.
 *
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @version $Id: NoneAdapter.java 1848527 2018-12-09 16:27:22Z tv $
 */
public class NoneAdapter extends AbstractAdapter
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = -285009315025818009L;

    /**
     * Empty protected constructor.
     */
    protected NoneAdapter()
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
        return in;
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
        return in;
    }

    /**
     * @see org.apache.torque.adapter.Adapter#getIDMethodType()
     */
    @Override
    public IDMethod getIDMethodType()
    {
        return IDMethod.NO_ID_METHOD;
    }

    /**
     * @see org.apache.torque.adapter.Adapter#getIDMethodSQL(Object obj)
     */
    @Override
    public String getIDMethodSQL(Object obj)
    {
        return null;
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
        // empty
    }

    /**
     * Unlocks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to unlock.
     * @exception SQLException No Statement could be created or executed.
     */
    @Override
    public void unlockTable(Connection con, String table) throws SQLException
    {
        // empty
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
        return false;
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
        return false;
    }
}
