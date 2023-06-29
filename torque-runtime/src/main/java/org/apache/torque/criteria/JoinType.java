package org.apache.torque.criteria;

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
/**
 *
 * The possible explicit join types.
 *
 * @version $Id: JoinType.java 1839288 2018-08-27 09:48:33Z tv $
 */
public enum JoinType
{
    /** SQL Expression " LEFT JOIN ". */
    LEFT_JOIN(" LEFT JOIN "),
    /** SQL Expression " RIGHT JOIN ". */
    RIGHT_JOIN(" RIGHT JOIN "),
    /** SQL Expression " INNER JOIN ". */
    INNER_JOIN(" INNER JOIN ");

    /** The SQL expression for the join type. */
    private String sql;

    /**
     * Constructor.
     *
     * @param sql The SQL expression for the join type.
     */
    private JoinType(String sql)
    {
        this.sql = sql;
    }

    /**
     * Returns the SQL expression for the join type.
     *
     * @return the SQL expression, not null.
     */
    @Override
    public String toString()
    {
        return sql;
    }

}
