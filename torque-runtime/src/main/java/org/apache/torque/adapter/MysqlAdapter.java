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
 * This is used in order to connect to a MySQL database using the MM
 * drivers.  Simply comment the above and uncomment this code below and
 * fill in the appropriate values for DB_NAME, DB_HOST, DB_USER,
 * DB_PASS.
 *
 * <P><a href="http://www.mysql.com/">http://www.mysql.com/</a>
 * <p>"jdbc:mysql://" + DB_HOST + "/" + DB_NAME + "?user=" +
 * DB_USER + "&amp;password=" + DB_PASS;
 *
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id: MysqlAdapter.java 1850965 2019-01-10 17:21:29Z painter $
 */
public class MysqlAdapter extends AbstractAdapter
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 7547291410802807010L;

    /**
     * Empty protected constructor.
     */
    protected MysqlAdapter()
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
        return IDMethod.AUTO_INCREMENT;
    }

    /**
     * Returns the SQL to get the database key of the last row
     * inserted, which in this case is <code>SELECT
     * LAST_INSERT_ID()</code>.
     *
     * @see org.apache.torque.adapter.Adapter#getIDMethodSQL(Object obj)
     */
    @Override
    public String getIDMethodSQL(Object obj)
    {
        return "SELECT LAST_INSERT_ID()";
    }

    /**
     * Locks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to lock.
     * @exception SQLException No Statement could be created or
     * executed.
     */
    @Override
    public void lockTable(Connection con, String table) throws SQLException
    {
        try (Statement statement = con.createStatement())
        {
            StringBuilder stmt = new StringBuilder();
            stmt.append("LOCK TABLE ").append(table).append(" WRITE");
            statement.executeUpdate(stmt.toString());
        }
    }

    /**
     * Unlocks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to unlock.
     * @exception SQLException No Statement could be created or
     * executed.
     */
    @Override
    public void unlockTable(Connection con, String table) throws SQLException
    {
        try (Statement statement = con.createStatement())
        {
            statement.executeUpdate("UNLOCK TABLES");
        }
    }

    /**
     * Generate a LIMIT offset, limit clause if offset &gt; 0
     * or an LIMIT limit clause if limit is &gt; 0 and offset
     * is 0.
     *
     * @param query The query to modify
     * @param offset the offset Value
     * @param limit the limit Value
     */
    @Override
    public void generateLimits(Query query, long offset, int limit)
    {
        if (offset > 0)
        {
            if (limit >= 0)
            {
                query.setLimit(Integer.toString(limit));
            }
            else
            {
                // Limit must always be set in mysql if offset is set
                query.setLimit("18446744073709551615");
            }
            query.setOffset(Long.toString(offset));
        }
        else
        {
            if (limit >= 0)
            {
                query.setLimit(Integer.toString(limit));
            }
        }

        query.setPreLimit(null);
        query.setPostLimit(null);
    }
}
