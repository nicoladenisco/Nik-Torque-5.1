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

/**
 * This is a utility class which eases counting of Datasets
 *
 * @author <a href="mailto:Martin.Goulet@sungard.com">Martin Goulet</a>
 * @author <a href="mailto:eric.lambert@sungard.com">Eric Lambert</a>
 * @author <a href="mailto:sebastien.paquette@sungard.com">Sebastien Paquette</a>
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @version $Id: CountHelper.java 1896195 2021-12-20 17:41:20Z gk $
 */
import java.sql.Connection;
import java.util.List;

import org.apache.torque.Column;
import org.apache.torque.ColumnImpl;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.criteria.SqlEnum;
import org.apache.torque.map.TableMap;
import org.apache.torque.om.mapper.IntegerMapper;
import org.apache.torque.util.functions.Count;

/**
 * Counts entries matching a criteria.
 */
public class CountHelper
{
    /**
     * The COUNT function returns the number of rows in a query.
     * Does not use a connection, hardcode the column to "*" and
     * set the distinct qualifier to false.
     * Only use this function if you have added additional constraints to
     * the criteria, otherwise Torque does not know which table it should
     * count the datasets in.
     *
     * @param c Criteria to get the count for.
     * @return number of rows matching the query provided
     * @throws TorqueException if the query could not be executed
     */
    public int count(final Criteria c)
            throws TorqueException
    {
        return count(c, null, "*", null);
    }

    /**
     * The COUNT function returns the number of rows in a query.
     * Hard code the distinct parameter to false and set the column to "*".
     * Only use this function if you have added additional constraints to
     * the criteria, otherwise Torque does not know which table it should
     * count the datasets in.
     *
     * @param c Criteria to get the count for.
     * @param conn Connection to use
     * @return number of rows matching the query provided
     * @throws TorqueException if the query could not be executed
     */
    public int count(final Criteria c, final Connection conn)
            throws TorqueException
    {
        return count(c, conn, "*", null);
    }

    /**
     * Returns the number of rows in a query.
     *
     * @param c Criteria to get the count for.
     * @param columnName Name of database Column which is counted. Preferably,
     *        use the primary key here.
     * @return number of rows matching the query provided
     * @throws TorqueException if the query could not be executed
     */
    public int count(final Criteria c, final String columnName)
            throws TorqueException
    {
        return count(c, null, columnName, null);
    }

    /**
     * Returns the number of rows in a query.
     *
     * @param c Criteria to get the count for.
     * @param column the database Column which is counted. Preferably,
     *        use the primary key here.
     * @return number of rows matching the query provided
     * @throws TorqueException if the query could not be executed
     */
    public int count(final Criteria c, final Column column)
            throws TorqueException
    {
        return count(c, column.getSqlExpression());
    }

    /**
     * Returns the number of rows in a query.
     *
     * @param c Criteria to get the count for.
     * @param conn Connection to use
     * @param column The database Column which is counted. Preferably,
     *        use the primary key here.
     * @return number of rows matching the query provided
     * @throws TorqueException if the query could not be executed
     */
    public int count(
            final Criteria c,
            final Connection conn,
            final Column column)
                    throws TorqueException
    {
        return count(c, conn, column.getSqlExpression(), null);
    }

    /**
     * Counts all rows in a table.
     *
     * @param tableMap the table map of the table to count rows in.
     *
     * @return the number of rows in the table.
     *
     * @throws TorqueException if the query could not be executed
     */
    public int count(final TableMap tableMap)
            throws TorqueException
    {
        return count(
                new Criteria(),
                null,
                "*",
                tableMap);
    }

    /**
     * Counts all rows in a table.
     *
     * @param tableMap the table map of the table to count rows in.
     * @param conn the connection to use.
     *
     * @return the number of rows in the table.
     *
     * @throws TorqueException if the query could not be executed
     */
    public int count(final TableMap tableMap, final Connection conn)
            throws TorqueException
    {
        return count(
                new Criteria(),
                conn,
                "*",
                tableMap);
    }

    /**
     * Returns the number of rows in a query.
     *
     * @param c Criteria to get the count for.
     * @param conn Connection to use
     * @param columnName Name of database Column which is counted. Preferably,
     *        use the primary key here.
     * @param tableMap the table to count the columns in, or null to determine
     *        the table automatically from the criteria.
     *
     * @return number of rows matching the query provided.
     *
     * @throws TorqueException if the query could not be executed.
     */
    public int count(
            final Criteria c,
            final Connection conn,
            final String columnName,
            final TableMap tableMap)
                    throws TorqueException
    {
        /* Clear the select columns. */
        c.getSelectColumns().clear();
        c.getOrderByColumns().clear();
        c.getGroupByColumns().clear();

        UniqueList<String> criteriaSelectModifiers
        = c.getSelectModifiers();

        boolean distinct = isAndSetDistinct(criteriaSelectModifiers);

        c.addSelectColumn(new Count(new ColumnImpl(columnName), distinct));

        String databaseName = (c.getDbName() == null)
                ? Torque.getDefaultDB()
                        : c.getDbName();

                BasePeerImpl<Integer> peer = new BasePeerImpl<>(
                        new IntegerMapper(),
                        tableMap, databaseName);

                List<Integer> result = (conn == null)
                        ? peer.doSelect(c)
                                : peer.doSelect(c, conn);

                        return result.get(0);
    }

    private boolean isAndSetDistinct(UniqueList<String> criteriaSelectModifiers) {
        boolean distinct = false;
        if (criteriaSelectModifiers != null
                && criteriaSelectModifiers.size() > 0
                && criteriaSelectModifiers.contains(SqlEnum.DISTINCT.toString()))
        {
            criteriaSelectModifiers.remove(SqlEnum.DISTINCT.toString());
            distinct = true;
        }
        return distinct;
    }
}
