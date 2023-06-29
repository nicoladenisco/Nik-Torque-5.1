package org.apache.torque.util.functions;

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

import org.apache.torque.Column;
import org.apache.torque.ColumnImpl;

/**
 * SQL99 Standard min function.
 *
 * @version $Id: Min.java 1848281 2018-12-06 10:48:36Z tv $
 */
public class Min extends AggregateFunction
{
    /**
     * Construct an MIN function class with the column to calculate the minimum
     * from.
     *
     * @param column the Column to calculate the minimum from.
     */
    public Min(Column column)
    {
        this(column, false);
    }

    /**
     * Construct an MIN function class with an SQL expression to calculate
     * the minimum from.
     *
     * @param sqlExpression the SQL expression to calculate the minimum from.
     */
    public Min(String sqlExpression)
    {
        this(new ColumnImpl(sqlExpression), false);
    }

    /**
     * Construct an MIN function class with the column to calculate
     * the minimum from and possibly a distinct modifier.
     *
     * @param column the Column to calculate the minimum from.
     * @param distinct whether to calculate the minimum from only
     *        distinct values.
     */
    public Min(Column column, boolean distinct)
    {
        super("MIN", column, distinct);
    }

    /**
     * This method cannot be called, an UnsupportedOperationException
     * will always be thrown.
     *
     * @param function disregarded.
     *
     * @throws UnsupportedOperationException always.
     */
    @Override
    public void setFunction(String function)
    {
        throw new UnsupportedOperationException(
                "The function name may not be changed.");
    }
}
