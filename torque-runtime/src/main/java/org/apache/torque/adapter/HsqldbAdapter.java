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
 * This is used to connect to Hsqldb databases.
 *
 * <a href="http://hsqldb.org/">http://hsqldb.org/</a>
 *
 * @author <a href="mailto:celkins@scardini.com">Christopher Elkins</a>
 * @version $Id: HsqldbAdapter.java 1848527 2018-12-09 16:27:22Z tv $
 */
public class HsqldbAdapter extends AbstractAdapter
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 8392727399615702372L;

    /**
     * Constructor.
     */
    protected HsqldbAdapter()
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
        return toUpperCase(in);
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
     * @see org.apache.torque.adapter.Adapter#getIDMethodSQL(Object obj)
     */
    @Override
    public String getIDMethodSQL(Object obj)
    {
        StringBuilder command = new StringBuilder("select IDENTITY() from ");
        String qualifiedIdentifier = (String) obj;
        command.append(qualifiedIdentifier);
        return command.toString();
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
     * This method is for the SqlExpression.quoteAndEscape rules.  The rule is,
     * any string in a SqlExpression with a BACKSLASH will either be changed to
     * "\\" or left as "\".
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
     * HSQLDB needs this, so this implementation always returns
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
}
