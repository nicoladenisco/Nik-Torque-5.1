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

import org.apache.torque.sql.Query;

/**
 * This is used to connect to PostgresQL databases.
 *
 * <a href="http://www.postgresql.org/">http://www.postgresql.org/</a>
 *
 * @author <a href="mailto:hakan42@gmx.de">Hakan Tandogan</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id: PostgresAdapter.java 1848527 2018-12-09 16:27:22Z tv $
 */
public class PostgresAdapter extends AbstractAdapter
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 7643304924262475272L;

    /**
     * Empty constructor.
     */
    protected PostgresAdapter()
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
        String s = new StringBuilder("UPPER(").append(in).append(")").toString();
        return s;
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
        String s = new StringBuilder("UPPER(").append(in).append(")").toString();
        return s;
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
     * @param name The name of the field (should be of type
     *      <code>String</code>).
     * @return SQL to retreive the next database key.
     * @see org.apache.torque.adapter.Adapter#getIDMethodSQL(Object)
     */
    @Override
    public String getIDMethodSQL(Object name)
    {
        return ("select nextval('" + name + "')");
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
     * Generate a LIMIT limit OFFSET offset clause if offset &gt; 0
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
            query.setOffset(Long.toString(offset));
        }
        if (limit >= 0)
        {
            query.setLimit(Integer.toString(limit));
        }

        query.setPreLimit(null);
        query.setPostLimit(null);
    }

    /**
     * Whether ILIKE should be used for case insensitive like clauses.
     *
     * As postgres uses ILIKE, this mimplementation returns true.
     *
     * @return true if ilike should be used for case insensitive likes,
     *         false if ignoreCase should be applied to the compared strings.
     */
    @Override
    public boolean useIlike()
    {
        return true;
    }
}
