package org.apache.torque.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import org.apache.torque.BaseTestCase;
import org.apache.torque.criteria.FromElement;
import org.apache.torque.util.UniqueList;

/**
 * Tests for Query
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @version $Id: QueryTest.java 1850726 2019-01-08 10:56:07Z gk $
 */
public class QueryTest extends BaseTestCase
{
    /**
     * Test for String toString()
     */
    public void testColumns()
    {
        Query query = new Query();

        UniqueList<String> columns = query.getSelectClause();
        columns.add("tableA.column1");
        columns.add("tableA.column2");
        columns.add("tableB.column1");

        assertEquals(
                "SELECT tableA.column1, tableA.column2, tableB.column1 FROM ",
                query.toString());
    }

    /**
     * Test for String toString()
     */
    public void testToString()
    {
        String expected = "SELECT tableA.column1, tableA.column2, "
                + "tableB.column1 FROM tableA, tableB WHERE tableA.A = tableB.A"
                + " AND tableA.B = 1234";
        Query query = new Query();

        UniqueList<String> columns = query.getSelectClause();
        columns.add("tableA.column1");
        columns.add("tableA.column2");
        columns.add("tableB.column1");

        UniqueList<FromElement> tables = query.getFromClause();
        tables.clear();
        tables.add(new FromElement("tableA"));
        tables.add(new FromElement("tableB"));

        UniqueList<String> where = query.getWhereClause();
        where.clear();
        where.add("tableA.A = tableB.A");
        where.add("tableA.B = 1234");

        assertEquals(expected, query.toString());
    }
}
