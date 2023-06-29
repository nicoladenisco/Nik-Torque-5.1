package org.apache.torque.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.torque.BaseTestCase;
import org.apache.torque.Column;
import org.apache.torque.ColumnImpl;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.criteria.Criterion;
import org.apache.torque.criteria.FromElement;
import org.apache.torque.criteria.PreparedStatementPart;
import org.apache.torque.criteria.PreparedStatementPartImpl;
import org.apache.torque.criteria.SqlEnum;
import org.apache.torque.om.NumberKey;
import org.apache.torque.util.functions.Count;

/**
 * Tests for SqlExpression
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id: SqlBuilderTest.java 1850726 2019-01-08 10:56:07Z gk $
 */
public class SqlBuilderTest extends BaseTestCase
{
    /** Time limit in ms of builing a query from a large array. */
    private static final long LARGE_ARRAY_TIME_LIMIT = 200L;

    /**
     * Test where condition with several ANDs compairing against Strings.
     */
    public void testAndString() throws Exception
    {
        final Column column1 = new ColumnImpl("myTable", "myColumn1");
        final Column column2 = new ColumnImpl("myTable", "myColumn2");
        final String value1a = "1a";
        final String value1b = "1b";
        final String value2a = "2a";
        final String value2b = "2b";
        Criteria c = new Criteria().where(column1, value1a)
                .and(column1, value1b)
                .and(column2, value2a)
                .and(column2, value2b);

        Query query = SqlBuilder.buildQuery(c);

        assertEquals(
                "SELECT  FROM myTable "
                        + "WHERE (myTable.myColumn1=? "
                        + "AND myTable.myColumn1=? "
                        + "AND myTable.myColumn2=? "
                        + "AND myTable.myColumn2=?)",
                        query.toString());
        List<String> expectedReplacements = new ArrayList<>();
        expectedReplacements.add(value1a);
        expectedReplacements.add(value1b);
        expectedReplacements.add(value2a);
        expectedReplacements.add(value2b);
        assertEquals(
                expectedReplacements,
                query.getPreparedStatementReplacements());
    }

    /**
     * Test where condition with several ORs compairing against Strings.
     */
    public void testOrString() throws Exception
    {
        final Column column1 = new ColumnImpl("myTable", "myColumn1");
        final Column column2 = new ColumnImpl("myTable", "myColumn2");
        final String value1a = "1a";
        final String value1b = "1b";
        final String value2a = "2a";
        final String value2b = "2b";
        Criteria c = new Criteria().where(column1, value1a)
                .or(column1, value1b)
                .or(column2, value2a)
                .or(column2, value2b);

        Query query = SqlBuilder.buildQuery(c);

        assertEquals(
                "SELECT  FROM myTable "
                        + "WHERE (myTable.myColumn1=? "
                        + "OR myTable.myColumn1=? "
                        + "OR myTable.myColumn2=? "
                        + "OR myTable.myColumn2=?)",
                        query.toString());
        List<String> expectedReplacements = new ArrayList<>();
        expectedReplacements.add(value1a);
        expectedReplacements.add(value1b);
        expectedReplacements.add(value2a);
        expectedReplacements.add(value2b);
        assertEquals(
                expectedReplacements,
                query.getPreparedStatementReplacements());
    }

    /**
     * Test where condition with several ANDs compairing against Strings.
     */
    public void testAndCriterions() throws Exception
    {
        final Column column1 = new ColumnImpl("myTable", "myColumn1");
        final Column column2 = new ColumnImpl("myTable", "myColumn2");
        final String value1a = "1a";
        final String value1b = "1b";
        final String value2a = "2a";
        Criterion criterion1 = new Criterion(column1, value1a);
        Criterion criterion2 = new Criterion(column1, value1b);
        Criterion criterion3 = new Criterion(column2, value2a);
        criterion1.and(criterion2).and(criterion3);
        Criteria c = new Criteria().where(criterion1);

        Query query = SqlBuilder.buildQuery(c);

        assertEquals(
                "SELECT  FROM myTable "
                        + "WHERE (myTable.myColumn1=? "
                        + "AND myTable.myColumn1=? "
                        + "AND myTable.myColumn2=?)",
                        query.toString());
        List<String> expectedReplacements = new ArrayList<>();
        expectedReplacements.add(value1a);
        expectedReplacements.add(value1b);
        expectedReplacements.add(value2a);
        assertEquals(
                expectedReplacements,
                query.getPreparedStatementReplacements());
    }

    /**
     * Test where condition with several ORs compairing against Strings.
     */
    public void testOrCriterions() throws Exception
    {
        final Column column1 = new ColumnImpl("myTable", "myColumn1");
        final Column column2 = new ColumnImpl("myTable", "myColumn2");
        final String value1a = "1a";
        final String value1b = "1b";
        final String value2a = "2a";
        Criterion criterion1 = new Criterion(column1, value1a);
        Criterion criterion2 = new Criterion(column1, value1b);
        Criterion criterion3 = new Criterion(column2, value2a);
        criterion1.or(criterion2).or(criterion3);
        Criteria c = new Criteria().where(criterion1);

        Query query = SqlBuilder.buildQuery(c);

        assertEquals(
                "SELECT  FROM myTable "
                        + "WHERE (myTable.myColumn1=? "
                        + "OR myTable.myColumn1=? "
                        + "OR myTable.myColumn2=?)",
                        query.toString());
        List<String> expectedReplacements = new ArrayList<>();
        expectedReplacements.add(value1a);
        expectedReplacements.add(value1b);
        expectedReplacements.add(value2a);
        assertEquals(
                expectedReplacements,
                query.getPreparedStatementReplacements());
    }

    /**
     * Test the andVerbatimSql method with null replacements.
     */
    public void testAndVerbatimSqlReplacementNull() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(new ColumnImpl("table1.a"), "a")
                .andVerbatimSql(
                        "foo(table1.x) = bar(table2.y)",
                        null);

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT  FROM table1 "
                        + "WHERE (table1.a=? "
                        + "AND foo(table1.x) = bar(table2.y))",
                        query.toString());
        List<String> expectedReplacements = new ArrayList<>();
        expectedReplacements.add("a");
        assertEquals(
                expectedReplacements,
                query.getPreparedStatementReplacements());
    }

    /**
     * Test the andVerbatimSql method with replacements.
     */
    public void testAndVerbatimSqlWithReplacements() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(new ColumnImpl("table1.a"), "a")
                .andVerbatimSql(
                        "foo(table1.x, ?) = bar(table2.y, ?)",
                        new Object[] {"y", "z"});

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT  FROM table1 "
                        + "WHERE (table1.a=? "
                        + "AND foo(table1.x, ?) = bar(table2.y, ?))",
                        query.toString());
        List<String> expectedReplacements = new ArrayList<>();
        expectedReplacements.add("a");
        expectedReplacements.add("y");
        expectedReplacements.add("z");
        assertEquals(
                expectedReplacements,
                query.getPreparedStatementReplacements());
    }

    /**
     * Test the andVerbatimSql method with from Columns.
     */
    public void testAndVerbatimSqlWithFromColumns() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(new ColumnImpl("table1.a"), "a")
                .andVerbatimSql(
                        "foo(table1.x) = bar(table2.y)",
                        new Object[] {},
                        new ColumnImpl("table1.x"),
                        new ColumnImpl("table2.y"));

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT  FROM table1, table2 "
                        + "WHERE (table1.a=? "
                        + "AND foo(table1.x) = bar(table2.y))",
                        query.toString());
        List<String> expectedReplacements = new ArrayList<>();
        expectedReplacements.add("a");
        assertEquals(
                expectedReplacements,
                query.getPreparedStatementReplacements());
    }

    /**
     * Test the orVerbatimSql method with null replacements.
     */
    public void testOrVerbatimSqlReplacementNull() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(new ColumnImpl("table1.a"), "a")
                .orVerbatimSql(
                        "foo(table1.x) = bar(table2.y)",
                        null);

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT  FROM table1 "
                        + "WHERE (table1.a=? "
                        + "OR foo(table1.x) = bar(table2.y))",
                        query.toString());
        List<String> expectedReplacements = new ArrayList<>();
        expectedReplacements.add("a");
        assertEquals(
                expectedReplacements,
                query.getPreparedStatementReplacements());
    }

    /**
     * Test the orVerbatimSql method with replacements.
     */
    public void testOrVerbatimSqlWithReplacements() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(new ColumnImpl("table1.a"), "a")
                .orVerbatimSql(
                        "foo(table1.x, ?) = bar(table2.y, ?)",
                        new Object[] {"y", "z"});

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT  FROM table1 "
                        + "WHERE (table1.a=? "
                        + "OR foo(table1.x, ?) = bar(table2.y, ?))",
                        query.toString());
        List<String> expectedReplacements = new ArrayList<>();
        expectedReplacements.add("a");
        expectedReplacements.add("y");
        expectedReplacements.add("z");
        assertEquals(
                expectedReplacements,
                query.getPreparedStatementReplacements());
    }

    /**
     * Test the orVerbatimSql method with from Columns.
     */
    public void testOrVerbatimSqlWithFromColumns() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(new ColumnImpl("table1.a"), "a")
                .orVerbatimSql(
                        "foo(table1.x) = bar(table2.y)",
                        new Object[] {},
                        new ColumnImpl("table1.x"),
                        new ColumnImpl("table2.y"));

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT  FROM table1, table2 "
                        + "WHERE (table1.a=? "
                        + "OR foo(table1.x) = bar(table2.y))",
                        query.toString());
        List<String> expectedReplacements = new ArrayList<>();
        expectedReplacements.add("a");
        assertEquals(
                expectedReplacements,
                query.getPreparedStatementReplacements());
    }

    /**
     * Test the whereVerbatimSql method with null replacements.
     */
    public void testWhereVerbatimSqlReplacementNull() throws Exception
    {
        Criteria criteria = new Criteria()
                .whereVerbatimSql(
                        "foo(table1.x) = bar(table2.y)",
                        null);

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT  FROM  "
                        + "WHERE foo(table1.x) = bar(table2.y)",
                        query.toString());
        assertEquals(
                new ArrayList<String>(),
                query.getPreparedStatementReplacements());
    }

    /**
     * Test the whereVerbatimSql method with replacements.
     */
    public void testWhereVerbatimSqlWithReplacements() throws Exception
    {
        Criteria criteria = new Criteria()
                .whereVerbatimSql(
                        "foo(table1.x, ?) = bar(table2.y, ?)",
                        new Object[] {"y", "z"});

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT  FROM  "
                        + "WHERE foo(table1.x, ?) = bar(table2.y, ?)",
                        query.toString());
        List<String> expectedReplacements = new ArrayList<>();
        expectedReplacements.add("y");
        expectedReplacements.add("z");
        assertEquals(
                expectedReplacements,
                query.getPreparedStatementReplacements());
    }

    /**
     * Test the whereVerbatimSql method with from Columns.
     */
    public void testWhereVerbatimSqlWithFromColumns() throws Exception
    {
        Criteria criteria = new Criteria()
                .whereVerbatimSql(
                        "foo(table1.x) = bar(table2.y)",
                        new Object[] {},
                        new ColumnImpl("table1.x"),
                        new ColumnImpl("table2.y"));

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT  FROM table1, table2 "
                        + "WHERE foo(table1.x) = bar(table2.y)",
                        query.toString());
        assertEquals(
                new ArrayList<String>(),
                query.getPreparedStatementReplacements());
    }

    /**
     * Test that unknown columns are treated case-insensitive if ignoreCase
     * is set.
     */
    public void testignoreCaseUnknownColumnType() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("unknownTable.column1"));
        criteria.where(new ColumnImpl("column1"), "1");
        criteria.setIgnoreCase(true);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT unknownTable.column1 FROM unknownTable "
                        + "WHERE UPPER(column1)=UPPER(?)",
                        query.toString());
    }

    public void testIgnoreCaseStringColumnType() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(stringColumnMap);
        criteria.where(stringColumnMap, "1");
        criteria.setIgnoreCase(true);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT TABLE.COLUMN1 FROM TABLE "
                        + "WHERE UPPER(TABLE.COLUMN1)=UPPER(?)",
                        query.toString());
    }

    public void testIgnoreCaseIntegerColumnType() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(integerColumnMap);
        criteria.where(integerColumnMap, "1");
        criteria.setIgnoreCase(true);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT TABLE.COLUMN4 FROM TABLE "
                        + "WHERE TABLE.COLUMN4=?",
                        query.toString());
    }

    public void testOrderByDesc() throws TorqueException
    {
        Criteria criteria = new Criteria();
        criteria.addDescendingOrderByColumn(new ColumnImpl("table.column1"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT table.column1 FROM table ORDER BY table.column1 DESC",
                query.toString());
    }

    public void testOrderByAsc() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(new ColumnImpl("table.column1"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT table.column1 FROM table ORDER BY table.column1 ASC",
                query.toString());
    }

    public void testOrderByNullColumn() throws Exception
    {
        Criteria criteria = new Criteria();
        try
        {
            criteria.addAscendingOrderByColumn((Column) null);
            fail("Exception expected");
        }
        catch (NullPointerException e)
        {
            assertEquals("column is null", e.getMessage());
        }
    }

    public void testOrderByMultiple() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(new ColumnImpl("table.column1"));
        criteria.addDescendingOrderByColumn(new ColumnImpl("table2.column2"));
        criteria.addAscendingOrderByColumn(new ColumnImpl("table3.column1"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT table.column1, table2.column2, table3.column1"
                        + " FROM table, table2, table3"
                        + " ORDER BY table.column1 ASC,"
                        + " table2.column2 DESC,"
                        + " table3.column1 ASC",
                        query.toString());
    }

    public void testOrderByWithDefaultSchema() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(new ColumnImpl("table.column1"));
        String oldSchema = database.getSchema();
        try
        {
            database.setSchema("schema1");
            criteria.setDbName(database.getName());
            Query query = SqlBuilder.buildQuery(criteria);
            assertEquals(
                    "SELECT table.column1 FROM schema1.table "
                            + "ORDER BY table.column1 ASC",
                            query.toString());
        }
        finally
        {
            database.setSchema(oldSchema);
        }
    }

    public void testOrderByWithFunction() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(
                new ColumnImpl("count(table.column1)"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT count(table.column1) FROM table "
                        + "ORDER BY count(table.column1) ASC",
                        query.toString());
    }

    public void testOrderByWithAsColumn() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAsColumn("asColumn", stringColumnMap);
        criteria.addAscendingOrderByColumn(new ColumnImpl("asColumn"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT TABLE.COLUMN1 AS asColumn "
                        + "FROM TABLE "
                        + "ORDER BY asColumn ASC",
                        query.toString());
    }

    public void testOrderByWithAsColumnIgnoreCase() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAsColumn("asColumn", stringColumnMap);
        criteria.addAscendingOrderByColumn(new ColumnImpl("asColumn"), true);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT TABLE.COLUMN1 AS asColumn, UPPER(asColumn) "
                        + "FROM TABLE "
                        + "ORDER BY UPPER(asColumn) ASC",
                        query.toString());
    }

    public void testOrderByWithAsColumnAndAliasIgnoreCase() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAlias("alias", stringColumnMap.getTableName());
        criteria.addAsColumn(
                "asColumn",
                new ColumnImpl("alias", stringColumnMap.getColumnName()));
        criteria.addAscendingOrderByColumn(new ColumnImpl("asColumn"), true);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT alias.COLUMN1 AS asColumn, UPPER(asColumn) "
                        + "FROM TABLE alias "
                        + "ORDER BY UPPER(asColumn) ASC",
                        query.toString());
    }

    public void testOrderByAscendingIgnoreCaseString() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(stringColumnMap, true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT UPPER(TABLE.COLUMN1) FROM TABLE "
                        + "ORDER BY UPPER(TABLE.COLUMN1) ASC",
                        query.toString());
    }

    public void testOrderByAscendingIgnoreCaseInteger() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(integerColumnMap, true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT TABLE.COLUMN4 FROM TABLE "
                        + "ORDER BY TABLE.COLUMN4 ASC",
                        query.toString());
    }

    public void testOrderByAscendingIgnoreCaseStringInCriteria()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(stringColumnMap);
        criteria.setIgnoreCase(true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT UPPER(TABLE.COLUMN1) FROM TABLE "
                        + "ORDER BY UPPER(TABLE.COLUMN1) ASC",
                        query.toString());
    }

    public void testOrderByAscendingIgnoreCaseIntegerInCriteria()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(integerColumnMap);
        criteria.setIgnoreCase(true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT TABLE.COLUMN4 FROM TABLE "
                        + "ORDER BY TABLE.COLUMN4 ASC",
                        query.toString());
    }

    public void testOrderByDescendingIgnoreCaseString() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addDescendingOrderByColumn(stringColumnMap, true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT UPPER(TABLE.COLUMN1) FROM TABLE "
                        + "ORDER BY UPPER(TABLE.COLUMN1) DESC",
                        query.toString());
    }

    public void testOrderByDescendingIgnoreCaseInteger() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addDescendingOrderByColumn(integerColumnMap, true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT TABLE.COLUMN4 FROM TABLE "
                        + "ORDER BY TABLE.COLUMN4 DESC",
                        query.toString());
    }

    public void testOrderByDescendingIgnoreCaseStringInCriteria()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addDescendingOrderByColumn(stringColumnMap);
        criteria.setIgnoreCase(true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT UPPER(TABLE.COLUMN1) FROM TABLE "
                        + "ORDER BY UPPER(TABLE.COLUMN1) DESC",
                        query.toString());
    }

    public void testOrderByDescendingIgnoreCaseIntegerInCriteria()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addDescendingOrderByColumn(integerColumnMap);
        criteria.setIgnoreCase(true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT TABLE.COLUMN4 FROM TABLE "
                        + "ORDER BY TABLE.COLUMN4 DESC",
                        query.toString());
    }

    public void testAlias() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAlias("alias", "table");
        criteria.addSelectColumn(new ColumnImpl("alias.column1"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT alias.column1 FROM table alias",
                query.toString());
    }

    public void testAliasWithDefaultSchema() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAlias("alias", "table");
        criteria.addSelectColumn(new ColumnImpl("alias.column1"));
        String oldSchema = database.getSchema();
        try
        {
            database.setSchema("schema1");
            criteria.setDbName(database.getName());
            Query query = SqlBuilder.buildQuery(criteria);
            assertEquals(
                    "SELECT alias.column1 FROM schema1.table alias",
                    query.toString());
        }
        finally
        {
            database.setSchema(oldSchema);
        }
    }

    public void testAliasWithIgnoreCaseUnknownColumnType() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAlias("alias", "table");
        criteria.addSelectColumn(new ColumnImpl("alias.column1"));
        criteria.where(new ColumnImpl("alias.column1"), "1");
        criteria.setIgnoreCase(true);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT alias.column1 FROM table alias "
                        + "WHERE UPPER(alias.column1)=UPPER(?)",
                        query.toString());
    }

    public void testAliasWithSubquery() throws Exception
    {
        Criteria subquery = new Criteria()
                .addSelectColumn(new ColumnImpl("table2.column1"))
                .where(new ColumnImpl("table2.column2"),
                        new ColumnImpl("table2.column3"));

        Criteria criteria = new Criteria();
        criteria.addAlias("alias", subquery);
        criteria.addSelectColumn(new ColumnImpl("alias.column1"));

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT alias.column1 FROM ("
                        + "SELECT table2.column1 FROM table2 "
                        + "WHERE table2.column2=table2.column3) "
                        + "alias",
                        query.toString());
        assertEquals(0, query.getPreparedStatementReplacements().size());
    }

    public void testAliasWithSubqueryAndReplacements() throws Exception
    {
        Criteria subquery = new Criteria()
                .addSelectColumn(new ColumnImpl("table2.column1"))
                .where(new ColumnImpl("table2.column2"), "x");

        Criteria criteria = new Criteria();
        criteria.addAlias("alias", subquery);
        criteria.addSelectColumn(new ColumnImpl("alias.column1"));

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT alias.column1 FROM ("
                        + "SELECT table2.column1 FROM table2 "
                        + "WHERE table2.column2=?) "
                        + "alias",
                        query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals("x", query.getPreparedStatementReplacements().get(0));
    }

    public void testAliasWithIgnoreCaseStringColumnType() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAlias("alias", tableMap.getName());
        criteria.addSelectColumn(new ColumnImpl("alias.COLUMN1"));
        criteria.where(new ColumnImpl("alias.COLUMN1"), "1");
        criteria.setIgnoreCase(true);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT alias.COLUMN1 FROM TABLE alias "
                        + "WHERE UPPER(alias.COLUMN1)=UPPER(?)",
                        query.toString());
    }

    public void testAliasWithIgnoreCaseIntegerColumnType() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAlias("alias", tableMap.getName());
        criteria.addSelectColumn(new ColumnImpl("alias.COLUMN4"));
        criteria.where(new ColumnImpl("alias.COLUMN4"), "1");
        criteria.setIgnoreCase(true);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT alias.COLUMN4 FROM TABLE alias "
                        + "WHERE alias.COLUMN4=?",
                        query.toString());
    }

    public void testAliasWithIgnoreCaseStringColumnTypeAndDefaultSchema()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAlias("alias", tableMap.getName());
        criteria.addSelectColumn(new ColumnImpl("alias.COLUMN1"));
        criteria.where(new ColumnImpl("alias.COLUMN1"), "1");
        criteria.setIgnoreCase(true);
        String oldSchema = database.getSchema();
        try
        {
            database.setSchema("schema1");
            criteria.setDbName(database.getName());
            Query query = SqlBuilder.buildQuery(criteria);
            assertEquals(
                    "SELECT alias.COLUMN1 FROM schema1.TABLE alias "
                            + "WHERE UPPER(alias.COLUMN1)=UPPER(?)",
                            query.toString());
        }
        finally
        {
            database.setSchema(oldSchema);
        }
    }

    public void testAliasWithIgnoreCaseIntegerColumnTypeAndDefaultSchema()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAlias("alias", tableMap.getName());
        criteria.addSelectColumn(new ColumnImpl("alias.COLUMN4"));
        criteria.where(new ColumnImpl("alias.COLUMN4"), "1");
        criteria.setIgnoreCase(true);
        String oldSchema = database.getSchema();
        try
        {
            database.setSchema("schema1");
            criteria.setDbName(database.getName());
            Query query = SqlBuilder.buildQuery(criteria);
            assertEquals(
                    "SELECT alias.COLUMN4 FROM schema1.TABLE alias "
                            + "WHERE alias.COLUMN4=?",
                            query.toString());
        }
        finally
        {
            database.setSchema(oldSchema);
        }
    }

    public void testAsColumn() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAsColumn("columnAlias", stringColumnMap);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT TABLE.COLUMN1 AS columnAlias FROM TABLE",
                query.toString());
    }

    public void testAsColumnWithIgnoreCaseUnknownColumn() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAsColumn("columnAlias", new ColumnImpl("table.column"));
        criteria.where(new ColumnImpl("columnAlias"), "1");
        criteria.setIgnoreCase(true);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT table.column AS columnAlias FROM table"
                        + " WHERE UPPER(columnAlias)=UPPER(?)",
                        query.toString());
    }

    public void testAsColumnWithIgnoreCaseStringColumn() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAsColumn("columnAlias", stringColumnMap);
        criteria.where(new ColumnImpl("columnAlias"), "1");
        criteria.setIgnoreCase(true);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT TABLE.COLUMN1 AS columnAlias FROM TABLE"
                        + " WHERE UPPER(columnAlias)=UPPER(?)",
                        query.toString());
    }

    public void testAsColumnWithIgnoreCaseIntegerColumn() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAsColumn("columnAlias", integerColumnMap);
        criteria.where(new ColumnImpl("columnAlias"), "1");
        criteria.setIgnoreCase(true);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT TABLE.COLUMN4 AS columnAlias FROM TABLE"
                        + " WHERE columnAlias=?",
                        query.toString());
    }

    public void testAsColumnWithIgnoreCaseStringColumnAndDefaultSchema()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAsColumn("columnAlias", stringColumnMap);
        criteria.addSelectColumn(new ColumnImpl("columnAlias"));
        criteria.where(new ColumnImpl("columnAlias"), "1");
        criteria.setIgnoreCase(true);
        String oldSchema = database.getSchema();
        try
        {
            database.setSchema("schema1");
            criteria.setDbName(database.getName());
            Query query = SqlBuilder.buildQuery(criteria);
            assertEquals(
                    "SELECT TABLE.COLUMN1 AS columnAlias FROM schema1.TABLE"
                            + " WHERE UPPER(columnAlias)=UPPER(?)",
                            query.toString());
        }
        finally
        {
            database.setSchema(oldSchema);
        }
    }

    public void testAsColumnWithIgnoreCaseIntegerColumnAndDefaultSchema()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAsColumn("columnAlias", integerColumnMap);
        criteria.addSelectColumn(new ColumnImpl("columnAlias"));
        criteria.where(new ColumnImpl("columnAlias"), "1");
        criteria.setIgnoreCase(true);
        String oldSchema = database.getSchema();
        try
        {
            database.setSchema("schema1");
            criteria.setDbName(database.getName());
            Query query = SqlBuilder.buildQuery(criteria);
            assertEquals(
                    "SELECT TABLE.COLUMN4 AS columnAlias FROM schema1.TABLE"
                            + " WHERE columnAlias=?",
                            query.toString());
        }
        finally
        {
            database.setSchema(oldSchema);
        }
    }

    public void testInnerJoinImplicit()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table1.column"));
        criteria.addJoin(
                new ColumnImpl("table1.column1"),
                new ColumnImpl("table2.column2"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT table1.column FROM table1, table2"
                        + " WHERE table1.column1=table2.column2",
                        query.toString());
    }

    public void testInnerJoinImplicitWithComparison()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table1.column"));
        criteria.addJoin(
                "table1",
                "table2",
                new Criterion(
                        new ColumnImpl("table1.column1"),
                        new ColumnImpl("table2.column2"),
                        SqlEnum.NOT_EQUAL),
                null);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT table1.column FROM table1, table2"
                        + " WHERE table1.column1<>table2.column2",
                        query.toString());
    }

    public void testInnerJoinExplicit()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table1.column1"));
        criteria.addJoin(
                new ColumnImpl("table1.column1"),
                new ColumnImpl("table2.column2"),
                Criteria.INNER_JOIN);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT table1.column1 FROM table1 INNER JOIN table2"
                        + " ON table1.column1=table2.column2",
                        query.toString());
    }

    public void testInnerJoinWithExcplicitExistingRightTable()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table1.column1"));
        criteria.addJoin(
                new ColumnImpl("table2.column2"),
                new ColumnImpl("table3.column3"),
                Criteria.INNER_JOIN);
        criteria.addJoin(
                new ColumnImpl("table1.column1"),
                new ColumnImpl("table2.column2"),
                Criteria.INNER_JOIN);
        Query query = SqlBuilder.buildQuery(criteria);
        // second join condition must be changed in order to satisfy
        // first join condition
        assertEquals(
                "SELECT table1.column1"
                        + " FROM table2 INNER JOIN table3"
                        + " ON table2.column2=table3.column3"
                        + " INNER JOIN table1"
                        + " ON table1.column1=table2.column2",
                        query.toString());
    }

    public void testInnerJoinWithExcplicitExistingRightTableAndOperator()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table1.column1"));
        criteria.addJoin(
                "table2",
                "table3",
                new Criterion(
                        new ColumnImpl("table2.column2"),
                        new ColumnImpl("table3.column3"),
                        Criteria.LESS_THAN),
                Criteria.INNER_JOIN);
        criteria.addJoin(
                "table1",
                "table2",
                new Criterion(
                        new ColumnImpl("table1.column1"),
                        new ColumnImpl("table2.column2"),
                        Criteria.GREATER_THAN),
                Criteria.INNER_JOIN);
        Query query = SqlBuilder.buildQuery(criteria);
        // second join condition must be changed in order to satisfy
        // first join condition
        assertEquals(
                "SELECT table1.column1"
                        + " FROM table2 INNER JOIN table3"
                        + " ON table2.column2<table3.column3"
                        + " INNER JOIN table1"
                        + " ON table1.column1>table2.column2",
                        query.toString());
    }

    public void testInnerJoinExcplicitWithExistingRightAndLeftTable()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table1.column1"));
        criteria.addJoin(
                new ColumnImpl("table2.column2"),
                new ColumnImpl("table3.column3"),
                Criteria.INNER_JOIN);
        criteria.addJoin(
                new ColumnImpl("table1.column1"),
                new ColumnImpl("table4.column4"),
                Criteria.INNER_JOIN);
        criteria.addJoin(
                new ColumnImpl("table1.column1"),
                new ColumnImpl("table2.column2"),
                Criteria.INNER_JOIN);
        try
        {
            SqlBuilder.buildQuery(criteria);
            fail("Exception expected");
        }
        catch (TorqueException e)
        {
            assertEquals("Unable to create a INNER JOIN "
                    + "because both expressions table1 and table2 "
                    + "are already in use. Try to create an(other) alias.",
                    e.getMessage());
        }
    }

    public void testInnerJoinExplicitWithComplicatedCondition()
            throws Exception
    {
        Criterion join1 = new Criterion(new ColumnImpl("table1.column2"), "x");
        Criterion join2 = new Criterion(
                "y",
                new ColumnImpl("table2.column2"),
                SqlEnum.NOT_EQUAL);
        join2.setIgnoreCase(true);
        Criterion join3 = new Criterion(
                new ColumnImpl("table1.column2"),
                new ColumnImpl("table2.column2"));
        Criterion join = new Criterion(join1).and(join2).and(join3);

        Criteria criteria = new Criteria();
        criteria.setDbName(databasePostgresql.getName());
        criteria.addSelectColumn(new ColumnImpl("table1.column1"));
        criteria.addJoin("table1", "table2", join, Criteria.INNER_JOIN);

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT table1.column1 FROM table1 INNER JOIN table2 "
                        + "ON (table1.column2=? "
                        + "AND UPPER(?)<>UPPER(table2.column2) "
                        + "AND table1.column2=table2.column2)",
                        query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals("x", query.getPreparedStatementReplacements().get(0));
        assertEquals("y", query.getPreparedStatementReplacements().get(1));
    }

    /**
     * Tests that a subselect can be added to the from clause.
     *
     * @throws Exception if an error occurs
     */
    public void testInnerJoinExplicitWithSubselect() throws Exception
    {
        Criteria subselect = new Criteria()
                .where(new ColumnImpl("table2.column2"), 5)
                .addSelectColumn(new ColumnImpl("table2.column1"));
        Query subselectQuery = SqlBuilder.buildQuery(subselect);
        PreparedStatementPart fromClause = new PreparedStatementPartImpl(
                "(" + subselectQuery.toString() + ") alias",
                subselectQuery.getPreparedStatementReplacements().toArray());
        Criterion join = new Criterion(new ColumnImpl("table1.column1"), new ColumnImpl("alias.column1"));

        Criteria criteria = new Criteria()
                .addSelectColumn(new ColumnImpl("table1.column1"))
                .addJoin(new PreparedStatementPartImpl("table1"), fromClause, join, Criteria.INNER_JOIN)
                .where(new ColumnImpl("table1.column3"), 3);

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT table1.column1 FROM table1 INNER JOIN "
                        + "(SELECT table2.column1 FROM table2 WHERE table2.column2=?) alias"
                        + " ON table1.column1=alias.column1"
                        + " WHERE table1.column3=?",
                        query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals(5, query.getPreparedStatementReplacements().get(0));
        assertEquals(3, query.getPreparedStatementReplacements().get(1));
    }

    public void testInnerJoinWithJoinCriteriaAndDefaultSchema()
            throws Exception
    {
        String oldSchema = database.getSchema();
        try
        {
            database.setSchema("TestSchema");
            Column otherTableJoinColumn = new ColumnImpl("table2.column2");
            Criterion joinCriterion = new Criterion(stringColumnMap, otherTableJoinColumn);

            Criteria criteria = new Criteria()
                    .addSelectColumn(stringColumnMap)
                    .addJoin(stringColumnMap.getTableName(), "table2", joinCriterion, Criteria.INNER_JOIN);

            Query query = SqlBuilder.buildQuery(criteria);

            assertEquals(
                    "SELECT TABLE.COLUMN1 FROM TestSchema.TABLE INNER JOIN TestSchema.table2 ON TABLE.COLUMN1=table2.column2",
                    query.toString());
            assertEquals(0, query.getPreparedStatementReplacements().size());
        }
        finally
        {
            database.setSchema(oldSchema);
        }
    }

    public void testLeftJoin()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table1.column1"));
        criteria.addJoin(
                new ColumnImpl("table1.column"),
                new ColumnImpl("table2.column"),
                Criteria.LEFT_JOIN);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT table1.column1"
                        + " FROM table1 LEFT JOIN table2"
                        + " ON table1.column=table2.column",
                        query.toString());
    }

    public void testLeftJoinWithExistingRightTable()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(
                new ColumnImpl("table1.column1"));
        criteria.addJoin(
                new ColumnImpl("table2.column2"),
                new ColumnImpl("table3.column3"),
                Criteria.INNER_JOIN);
        criteria.addJoin(
                new ColumnImpl("table1.column1"),
                new ColumnImpl("table2.column2"),
                Criteria.LEFT_JOIN);
        Query query = SqlBuilder.buildQuery(criteria);
        // left join must be converted to right join to satisfy
        // first join condition
        assertEquals(
                "SELECT table1.column1"
                        + " FROM table2 INNER JOIN table3"
                        + " ON table2.column2=table3.column3"
                        + " RIGHT JOIN table1"
                        + " ON table1.column1=table2.column2",
                        query.toString());
    }

    public void testRightJoin()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table1.column1"));
        criteria.addJoin(
                new ColumnImpl("table1.column"),
                new ColumnImpl("table2.column"),
                Criteria.RIGHT_JOIN);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT table1.column1"
                        + " FROM table1 RIGHT JOIN table2"
                        + " ON table1.column=table2.column",
                        query.toString());
    }

    public void testRightJoinWithExistingRightTable()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(
                new ColumnImpl("table1.column1"));
        criteria.addJoin(
                new ColumnImpl("table2.column2"),
                new ColumnImpl("table3.column3"),
                Criteria.INNER_JOIN);
        criteria.addJoin(
                new ColumnImpl("table1.column1"),
                new ColumnImpl("table2.column2"),
                Criteria.RIGHT_JOIN);
        Query query = SqlBuilder.buildQuery(criteria);
        // right join must be converted to left join to satisfy
        // first join condition
        assertEquals(
                "SELECT table1.column1"
                        + " FROM table2 INNER JOIN table3"
                        + " ON table2.column2=table3.column3"
                        + " LEFT JOIN table1"
                        + " ON table1.column1=table2.column2",
                        query.toString());
    }

    public void testInnerJoinImplicitWithAlias()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("alias.column1"));
        criteria.addAlias("alias", "table1");
        criteria.addJoin(
                new ColumnImpl("alias.column"),
                new ColumnImpl("table2.column"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT alias.column1 FROM table1 alias, table2"
                        + " WHERE alias.column=table2.column",
                        query.toString());
    }

    public void testInnerJoinImplicitWithAliasAndAsColumn()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAsColumn("x", new ColumnImpl("alias.column"));
        criteria.addAlias("alias", "table1");
        criteria.addJoin(new ColumnImpl("x"), new ColumnImpl("table2.column"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT alias.column AS x FROM table2, table1 alias"
                        + " WHERE x=table2.column",
                        query.toString());
    }


    public void testInnerJoinImplicitWithDefaultSchema()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("*"));
        criteria.addJoin(
                new ColumnImpl("table1.column"),
                new ColumnImpl("table2.column"));
        String oldSchema = database.getSchema();
        try
        {
            database.setSchema("schema1");
            criteria.setDbName(database.getName());
            Query query = SqlBuilder.buildQuery(criteria);
            assertEquals(
                    "SELECT *"
                            + " FROM schema1.table1, schema1.table2"
                            + " WHERE table1.column=table2.column",
                            query.toString());
        }
        finally
        {
            database.setSchema(oldSchema);
        }
    }

    public void testInnerJoinImplicitWithAliasAndDefaultSchema()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("alias.column1"));
        criteria.addAlias("alias", "table1");
        criteria.addJoin(
                new ColumnImpl("alias.column"),
                new ColumnImpl("table2.column"));
        String oldSchema = database.getSchema();
        try
        {
            database.setSchema("schema1");
            criteria.setDbName(database.getName());
            Query query = SqlBuilder.buildQuery(criteria);
            assertEquals(
                    "SELECT alias.column1 FROM schema1.table1 alias, schema1.table2"
                            + " WHERE alias.column=table2.column",
                            query.toString());
        }
        finally
        {
            database.setSchema(oldSchema);
        }
    }

    public void testInnerJoinImplicitWithAliasAndSchema()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(
                new ColumnImpl("schema1.alias.column1"));
        criteria.addAlias("alias", "table1");
        criteria.addJoin(
                new ColumnImpl("schema1.alias.column"),
                new ColumnImpl("schema2.table2.column"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT alias.column1 FROM schema1.table1 alias, schema2.table2"
                        + " WHERE alias.column=table2.column",
                        query.toString());
    }

    public void testInnerJoinImplicitWithSubqueryAndReplacements()
            throws Exception
    {
        Criteria subquery = new Criteria()
                .addSelectColumn(new ColumnImpl("table2.column2a"))
                .where(new ColumnImpl("table2.column2b"), "x");

        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("alias.column1"));
        criteria.addAlias("alias", subquery);
        criteria.addJoin(
                new ColumnImpl("alias.column1"),
                new ColumnImpl("table2.column2"));

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT alias.column1 FROM ("
                        + "SELECT table2.column2a FROM table2 "
                        + "WHERE table2.column2b=?) "
                        + "alias, table2 "
                        + "WHERE alias.column1=table2.column2",
                        query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals("x", query.getPreparedStatementReplacements().get(0));
    }

    public void testInnerJoinImplicitWithComplicatedCondition()
            throws Exception
    {
        Criterion join1 = new Criterion(new ColumnImpl("table1.column2"), "x");
        Criterion join2 = new Criterion(
                "y",
                new ColumnImpl("table2.column2"),
                SqlEnum.NOT_EQUAL);
        join2.setIgnoreCase(true);
        Criterion join3 = new Criterion(
                new ColumnImpl("table1.column2"),
                new ColumnImpl("table2.column2"));
        Criterion join = new Criterion(join1).and(join2).and(join3);

        Criteria criteria = new Criteria();
        criteria.setDbName(databasePostgresql.getName());
        criteria.addSelectColumn(new ColumnImpl("table1.column1"));
        criteria.addJoin("table1", "table2", join, null);
        criteria.where(new ColumnImpl("table1.column3"), null, Criteria.ISNULL);

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals(
                "SELECT table1.column1 FROM table1, table2 "
                        + "WHERE (table1.column2=? "
                        + "AND UPPER(?)<>UPPER(table2.column2) "
                        + "AND table1.column2=table2.column2) "
                        + "AND table1.column3 IS NULL",
                        query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals("x", query.getPreparedStatementReplacements().get(0));
        assertEquals("y", query.getPreparedStatementReplacements().get(1));
    }

    public void testDistinct()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table1.column1"));
        criteria.setDistinct();
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT DISTINCT table1.column1 FROM table1",
                query.toString());
    }

    public void testGroupBy()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addGroupByColumn(stringColumnMap);
        criteria.addSelectColumn(integerColumnMap);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT TABLE.COLUMN4 FROM TABLE GROUP BY TABLE.COLUMN1",
                query.toString());
    }

    public void testLimitPostgresql() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(stringColumnMap);
        criteria.setLimit(20);
        criteria.setDbName(databasePostgresql.getName());
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT TABLE.COLUMN1 FROM TABLE "
                + "LIMIT 20",
                query.toString());
    }

    public void testOffsetPostgresql() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(stringColumnMap);
        criteria.setOffset(10);
        criteria.setDbName(databasePostgresql.getName());
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT TABLE.COLUMN1 FROM TABLE "
                + "OFFSET 10",
                query.toString());
    }

    public void testLimitOffsetPostgresql() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(stringColumnMap);
        criteria.setLimit(20);
        criteria.setOffset(10);
        criteria.setDbName(databasePostgresql.getName());
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT TABLE.COLUMN1 FROM TABLE "
                + "LIMIT 20 OFFSET 10",
                query.toString());
    }

    public void testLimitMysql() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(stringColumnMap);
        criteria.setLimit(20);
        criteria.setDbName(databaseMysql.getName());
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT TABLE.COLUMN1 FROM TABLE "
                + "LIMIT 20",
                query.toString());
    }

    public void testOffsetMysql() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(stringColumnMap);
        criteria.setOffset(10);
        criteria.setDbName(databaseMysql.getName());
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT TABLE.COLUMN1 FROM TABLE "
                + "LIMIT 18446744073709551615 OFFSET 10",
                query.toString());
    }

    public void testLimitOffsetMysql() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(stringColumnMap);
        criteria.setLimit(20);
        criteria.setOffset(10);
        criteria.setDbName(databaseMysql.getName());
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT TABLE.COLUMN1 FROM TABLE "
                + "LIMIT 20 OFFSET 10",
                query.toString());
    }

    public void testLimitOracle() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(stringColumnMap);
        criteria.setLimit(20);
        criteria.setDbName(databaseOracle.getName());
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT B.* FROM ("
                + " SELECT A.*, rownum AS TORQUE$ROWNUM FROM "
                + "( SELECT TABLE.COLUMN1 FROM TABLE ) A  ) B"
                + " WHERE  B.TORQUE$ROWNUM <= 20",
                query.toString());
    }

    public void testOffsetOracle() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(stringColumnMap);
        criteria.setOffset(10);
        criteria.setDbName(databaseOracle.getName());
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT B.* FROM ("
                + " SELECT A.*, rownum AS TORQUE$ROWNUM FROM "
                + "( SELECT TABLE.COLUMN1 FROM TABLE ) A  ) B"
                + " WHERE  B.TORQUE$ROWNUM > 10",
                query.toString());
    }

    public void testLimitOffsetOracle() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(stringColumnMap);
        criteria.setLimit(20);
        criteria.setOffset(10);
        criteria.setDbName(databaseOracle.getName());
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT B.* FROM ("
                + " SELECT A.*, rownum AS TORQUE$ROWNUM FROM "
                + "( SELECT TABLE.COLUMN1 FROM TABLE ) A  ) B"
                + " WHERE  B.TORQUE$ROWNUM > 10 AND B.TORQUE$ROWNUM <= 30",
                query.toString());
    }

    public void testSelectForUpdate() throws Exception
    {
        Criteria criteria = new Criteria()
                .addSelectColumn(stringColumnMap)
                .setLimit(20)
                .setOffset(10)
                .forUpdate();
        criteria.setDbName(databaseOracle.getName());
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT B.* FROM ("
                + " SELECT A.*, rownum AS TORQUE$ROWNUM FROM "
                + "( SELECT TABLE.COLUMN1 FROM TABLE ) A  ) B"
                + " WHERE  B.TORQUE$ROWNUM > 10 AND B.TORQUE$ROWNUM <= 30"
                + " FOR UPDATE",
                query.toString());
    }

    public void testHaving() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addGroupByColumn(stringColumnMap);
        criteria.addAsColumn("count", new ColumnImpl("count(*)"));
        criteria.addSelectColumn(stringColumnMap);
        criteria.addHaving(
                new Criterion("count", 10, Criteria.GREATER_EQUAL));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT TABLE.COLUMN1, count(*) AS count FROM TABLE "
                + "GROUP BY TABLE.COLUMN1 HAVING count>=10",
                query.toString());
    }

    public void testSelectColumnWithoutTable()
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("*"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT * FROM ", query.toString());
    }


    public void testCriterionCustomSql() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        Criterion criterion
            = new Criterion("A", null, null, "A = functionOf(B)", null);
        criteria.where(criterion);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table WHERE A = functionOf(B)",
                query.toString());
        assertEquals(0, query.getPreparedStatementReplacements().size());
    }

    public void testLvalueIsObject() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        criteria.where(1, 2);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table WHERE ?=?",
                query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals(1, query.getPreparedStatementReplacements().get(0));
        assertEquals(2, query.getPreparedStatementReplacements().get(1));
    }

    public void testCurrentDate() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        criteria.where(new ColumnImpl("column"), Criteria.CURRENT_DATE);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table "
                + "WHERE column=CURRENT_DATE",
                query.toString());
        assertEquals(0, query.getPreparedStatementReplacements().size());
    }

    public void testCurrentTime() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        criteria.where(new ColumnImpl("column"), Criteria.CURRENT_TIME);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table "
                + "WHERE column=CURRENT_TIME",
                query.toString());
        assertEquals(0, query.getPreparedStatementReplacements().size());
    }

    public void testCurrentTimestamp() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        criteria.where(new ColumnImpl("column"), Criteria.CURRENT_TIMESTAMP);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table "
                + "WHERE column=CURRENT_TIMESTAMP",
                query.toString());
        assertEquals(0, query.getPreparedStatementReplacements().size());
    }

    public void testObjectKey() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        criteria.where(new ColumnImpl("column"), new NumberKey(11));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table "
                + "WHERE column=?",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                new BigDecimal(11),
                query.getPreparedStatementReplacements().get(0));
    }

    public void testNullValue() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        criteria.where(new ColumnImpl("column"), null);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table "
                + "WHERE column IS NULL",
                query.toString());
        assertEquals(0, query.getPreparedStatementReplacements().size());
    }

    public void testNullValueNotEqual() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        criteria.where(
                new ColumnImpl("column"),
                (Object) null,
                Criteria.NOT_EQUAL);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table "
                + "WHERE column IS NOT NULL",
                query.toString());
        assertEquals(0, query.getPreparedStatementReplacements().size());
    }

    public void testNullValueAltNotEqual() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        criteria.where(
                new ColumnImpl("column"),
                (Object) null,
                Criteria.ALT_NOT_EQUAL);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table "
                + "WHERE column IS NOT NULL",
                query.toString());
        assertEquals(0, query.getPreparedStatementReplacements().size());
    }

    public void testIsNull() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        criteria.where(
                new ColumnImpl("column"),
                "value ignored",
                Criteria.ISNULL);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table "
                + "WHERE column IS NULL",
                query.toString());
        assertEquals(0, query.getPreparedStatementReplacements().size());
    }

    public void testIsNotNull() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        criteria.where(
                new ColumnImpl("column"),
                "value ignored",
                Criteria.ISNOTNULL);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table "
                + "WHERE column IS NOT NULL",
                query.toString());
        assertEquals(0, query.getPreparedStatementReplacements().size());
    }

    public void testSubselect() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));

        Criteria subselect = new Criteria();
        subselect.where(new ColumnImpl("table.column2"), "value2");
        subselect.addSelectColumn(new ColumnImpl("table.column3"));
        criteria.where(new ColumnImpl("table.column3"), subselect);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE table.column3=("
                + "SELECT table.column3 FROM table "
                + "WHERE table.column2=?)",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals("value2", query.getPreparedStatementReplacements().get(0));
    }

    public void testSubselectReferenceOuterTable() throws Exception
    {
        Criteria subselect = new Criteria()
                .where(new ColumnImpl("table.column1"), new ColumnImpl("table2.column2"))
                .and(new ColumnImpl("table.column2"), 2)
                .addSelectColumn(new Count("*"));

        Criteria criteria = new Criteria()
                .where(subselect, 1)
                .addSelectColumn(new ColumnImpl("table.column1"));

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals(
                "SELECT table.column1 FROM table WHERE "
                        + "(SELECT COUNT(*) FROM table2 WHERE (table.column1=table2.column2 AND table.column2=?))=?",
                        query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals(2, query.getPreparedStatementReplacements().get(0));
        assertEquals(1, query.getPreparedStatementReplacements().get(1));
    }

    public void testLike() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                new ColumnImpl("table.column2"),
                "*v%al_e2?",
                Criteria.LIKE);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE table.column2 LIKE ?",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                "%v%al_e2_",
                query.getPreparedStatementReplacements().get(0));
    }

    /**
     * Test whether LIKE clauses with Escapes are built correctly.
     */
    public void testLikeWithEscape() throws TorqueException
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                new ColumnImpl("table.column2"),
                "\\*v\\%al\\_e\\\\*2\\?\\",
                Criteria.LIKE);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE table.column2 LIKE ?",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                "*v\\%al\\_e\\\\%2?\\",
                query.getPreparedStatementReplacements().get(0));
    }

    /**
     * Test whether LIKE clauses with Escapes are built correctly in Oracle.
     * Oracle needs to have an ESCAPE clause
     */
    public void testLikeWithEscapeOracle() throws TorqueException
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                new ColumnImpl("table.column2"),
                "\\*v\\%al\\_e\\\\*2\\?\\",
                Criteria.LIKE);
        criteria.setDbName(databaseOracle.getName());

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE table.column2 LIKE ? ESCAPE '\\'",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                "*v\\%al\\_e\\\\%2?\\",
                query.getPreparedStatementReplacements().get(0));
    }

    public void testLikeIgnoreCase() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                new ColumnImpl("table.column2"),
                "*v%al_e2?",
                Criteria.LIKE);
        criteria.setIgnoreCase(true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE table.column2 ILIKE ?",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                "%v%al_e2_",
                query.getPreparedStatementReplacements().get(0));
    }

    public void testLikeIgnoreCaseNoWildcard() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                new ColumnImpl("table.column2"),
                "value\\\\2",
                Criteria.LIKE);
        criteria.setIgnoreCase(true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE UPPER(table.column2)=UPPER(?)",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                "value\\2",
                query.getPreparedStatementReplacements().get(0));
    }

    public void testLikeInteger() throws TorqueException
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(new ColumnImpl("table.column2"), 1, Criteria.LIKE);

        try
        {
            SqlBuilder.buildQuery(criteria);
            fail("Exception expected");
        }
        catch (TorqueException e)
        {
            assertEquals("rValue must be a String for the operator  LIKE ",
                    e.getMessage());
        }
    }

    public void testNotLike() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                new ColumnImpl("table.column2"),
                "*val_e2?",
                Criteria.NOT_LIKE);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE table.column2 NOT LIKE ?",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                "%val_e2_",
                query.getPreparedStatementReplacements().get(0));
    }

    public void testNotLikeIgnoreCase() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                new ColumnImpl("table.column2"),
                "*v%al_e2?",
                Criteria.NOT_LIKE);
        criteria.setIgnoreCase(true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE table.column2 NOT ILIKE ?",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                "%v%al_e2_",
                query.getPreparedStatementReplacements().get(0));
    }

    public void testNotLikeIgnoreCaseNoWildcard() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                new ColumnImpl("table.column2"),
                "value\\\\2",
                Criteria.NOT_LIKE);
        criteria.setIgnoreCase(true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE UPPER(table.column2)<>UPPER(?)",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                "value\\2",
                query.getPreparedStatementReplacements().get(0));
    }

    public void testIlike() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                new ColumnImpl("table.column2"),
                "*val_e2?",
                Criteria.ILIKE);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE table.column2 ILIKE ?",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                "%val_e2_",
                query.getPreparedStatementReplacements().get(0));
    }

    public void testIlikeNoWildcard() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                new ColumnImpl("table.column2"),
                "value2",
                Criteria.ILIKE);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE table.column2 ILIKE ?",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                "value2",
                query.getPreparedStatementReplacements().get(0));
    }

    public void testNotIlike() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                new ColumnImpl("table.column2"),
                "*val_e2?",
                Criteria.NOT_ILIKE);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE table.column2 NOT ILIKE ?",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                "%val_e2_",
                query.getPreparedStatementReplacements().get(0));
    }

    public void testNotIlikeNoWildcard() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                new ColumnImpl("table.column2"),
                "value2",
                Criteria.NOT_ILIKE);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE table.column2 NOT ILIKE ?",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(
                "value2",
                query.getPreparedStatementReplacements().get(0));
    }

    public void testLvalueString() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        criteria.where("X", "Y");
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table WHERE ?=?",
                query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals(
                "X",
                query.getPreparedStatementReplacements().get(0));
        assertEquals(
                "Y",
                query.getPreparedStatementReplacements().get(1));
    }

    public void testLvalueNull() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        try
        {
            criteria.where(null, new ColumnImpl("table.column"));
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Either the values(lValue, comparison) or "
                    + "sql must be not null",
                    e.getMessage());
        }
    }

    public void testLvalueCriteria() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column"));
        Criteria subselect = new Criteria();
        subselect.addSelectColumn(new ColumnImpl("table2.column2"));
        criteria.where(subselect, new ColumnImpl("table.column"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column FROM table WHERE "
                + "(SELECT table2.column2 FROM table2)=table.column",
                query.toString());
        assertEquals(0, query.getPreparedStatementReplacements().size());
    }

    public void testInArray() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        String[] inValue = new String[] {"a", "b", null, null};
        criteria.whereIn(new ColumnImpl("table.column2"), inValue.clone());

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals("SELECT table.column1 FROM table "
                + "WHERE (table.column2 IN (?,?) OR table.column2 IS NULL)",
                query.toString());
        List<Object> replacements = query.getPreparedStatementReplacements();
        assertEquals(2, replacements.size());
        assertEquals(inValue[0], replacements.get(0));
        assertEquals(inValue[1], replacements.get(1));
    }

    public void testInArrayIgnoreCase() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        String[] inValue = new String[] {"a", "b", null, null};
        criteria.whereIn(new ColumnImpl("table.column2"), inValue.clone());
        criteria.setIgnoreCase(true);

        Query query = SqlBuilder.buildQuery(criteria);

        assertEquals("SELECT table.column1 FROM table "
                + "WHERE (UPPER(table.column2) IN (UPPER(?),UPPER(?))"
                + " OR table.column2 IS NULL)",
                query.toString());
        List<Object> replacements = query.getPreparedStatementReplacements();
        assertEquals(2, replacements.size());
        assertEquals(inValue[0], replacements.get(0));
        assertEquals(inValue[1], replacements.get(1));
    }

    public void testInList() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        List<Integer> inList = new ArrayList<>();
        inList.add(1);
        inList.add(null);
        inList.add(2);
        inList.add(null);
        criteria.whereIn(new ColumnImpl("table.column2"), inList);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE (table.column2 IN (?,?) OR table.column2 IS NULL)",
                query.toString());
        assertEquals(
                2,
                query.getPreparedStatementReplacements().size());
        assertEquals(1, query.getPreparedStatementReplacements().get(0));
        assertEquals(2, query.getPreparedStatementReplacements().get(1));
    }

    public void testInListIgnoreCase() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        List<String> inList = new ArrayList<>();
        inList.add("a");
        inList.add("b");
        inList.add(null);
        inList.add(null);
        criteria.whereIn(new ColumnImpl("table.column2"), inList);
        criteria.setIgnoreCase(true);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE (UPPER(table.column2) IN (UPPER(?),UPPER(?))"
                + " OR table.column2 IS NULL)",
                query.toString());
        List<Object> replacements = query.getPreparedStatementReplacements();
        assertEquals(2, replacements.size());
        assertEquals("a", replacements.get(0));
        assertEquals("b", replacements.get(1));
    }

    public void testNotInList() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        List<Integer> inList = new ArrayList<>();
        inList.add(1);
        inList.add(null);
        inList.add(2);
        inList.add(null);
        criteria.where(
                new ColumnImpl("table.column2"),
                inList,
                Criteria.NOT_IN);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE (table.column2 NOT IN (?,?) "
                + "AND table.column2 IS NOT NULL)",
                query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals(1, query.getPreparedStatementReplacements().get(0));
        assertEquals(2, query.getPreparedStatementReplacements().get(1));
    }

    public void testInLargeArray() throws TorqueException
    {
        int size = 10000;
        String[] values = new String[size];
        for (int i = 0; i < size; i++)
        {
            Array.set(values, i, String.valueOf(i));
        }
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.whereIn(new ColumnImpl("table.column2"), values);
        long start = System.currentTimeMillis();
        Query query = SqlBuilder.buildQuery(criteria);
        long end =  System.currentTimeMillis();
        List<Object> replacements = query.getPreparedStatementReplacements();
        assertEquals(size, replacements.size());
        assertTrue( end - start < LARGE_ARRAY_TIME_LIMIT,
                    "Exceeded time limit of " + LARGE_ARRAY_TIME_LIMIT
                    + " ms. Execution time was " + (end - start) + " ms");
    }

    public void testInString() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table.column1"));
        criteria.where(
                "table.column2",
                "illegal in value",
                Criteria.IN);

        try
        {
            SqlBuilder.buildQuery(criteria);
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Unknown rValue type java.lang.String. "
                    + "rValue must be an instance of  Iterable or Array",
                    e.getMessage());
        }
    }

    public void testFromElementsSetExplicitly() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table1.column1"));
        criteria.addFrom(new FromElement("table3"));
        criteria.where(new ColumnImpl("table2.column2"), 1);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table1.column1 FROM table3 "
                + "WHERE table2.column2=?",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(1, query.getPreparedStatementReplacements().get(0));
    }

    public void testFromElementsSetExplicitlyAsString() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(new ColumnImpl("table1.column1"));
        criteria.addFrom("table3");
        criteria.where(new ColumnImpl("table2.column2"), 1);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table1.column1 FROM table3 "
                + "WHERE table2.column2=?",
                query.toString());
        assertEquals(1, query.getPreparedStatementReplacements().size());
        assertEquals(1, query.getPreparedStatementReplacements().get(0));
    }


    public void testUnion() throws Exception
    {
        Criteria criteria = new Criteria()
                .addSelectColumn(new ColumnImpl("table2.column2"))
                .where(new ColumnImpl("table2.columnb"), 3)
                .union(new Criteria()
                        .addSelectColumn(new ColumnImpl("table1.column1"))
                        .where(new ColumnImpl("table1.columna"), 1));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("(SELECT table2.column2 FROM table2 "
                + "WHERE table2.columnb=?) "
                + "UNION (SELECT table1.column1 FROM table1 "
                + "WHERE table1.columna=?)",
                query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals(3, query.getPreparedStatementReplacements().get(0));
        assertEquals(1, query.getPreparedStatementReplacements().get(1));
    }

    public void testUnionAll() throws Exception
    {
        Criteria criteria = new Criteria()
                .addSelectColumn(new ColumnImpl("table2.column2"))
                .where(new ColumnImpl("table2.columnb"), 3)
                .unionAll(new Criteria()
                        .addSelectColumn(new ColumnImpl("table1.column1"))
                        .where(new ColumnImpl("table1.columna"), 1));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("(SELECT table2.column2 FROM table2 "
                + "WHERE table2.columnb=?) "
                + "UNION ALL (SELECT table1.column1 FROM table1 "
                + "WHERE table1.columna=?)",
                query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals(3, query.getPreparedStatementReplacements().get(0));
        assertEquals(1, query.getPreparedStatementReplacements().get(1));
    }

    public void testExcept() throws Exception
    {
        Criteria criteria = new Criteria()
                .addSelectColumn(new ColumnImpl("table2.column2"))
                .where(new ColumnImpl("table2.columnb"), 3)
                .except(new Criteria()
                        .addSelectColumn(new ColumnImpl("table1.column1"))
                        .where(new ColumnImpl("table1.columna"), 1));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("(SELECT table2.column2 FROM table2 "
                + "WHERE table2.columnb=?) "
                + "EXCEPT (SELECT table1.column1 FROM table1 "
                + "WHERE table1.columna=?)",
                query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals(3, query.getPreparedStatementReplacements().get(0));
        assertEquals(1, query.getPreparedStatementReplacements().get(1));
    }

    public void testExceptAll() throws Exception
    {
        Criteria criteria = new Criteria()
                .addSelectColumn(new ColumnImpl("table2.column2"))
                .where(new ColumnImpl("table2.columnb"), 3)
                .exceptAll(new Criteria()
                        .addSelectColumn(new ColumnImpl("table1.column1"))
                        .where(new ColumnImpl("table1.columna"), 1));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("(SELECT table2.column2 FROM table2 "
                + "WHERE table2.columnb=?) "
                + "EXCEPT ALL (SELECT table1.column1 FROM table1 "
                + "WHERE table1.columna=?)",
                query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals(3, query.getPreparedStatementReplacements().get(0));
        assertEquals(1, query.getPreparedStatementReplacements().get(1));
    }

    public void testIntersect() throws Exception
    {
        Criteria criteria = new Criteria()
                .addSelectColumn(new ColumnImpl("table2.column2"))
                .where(new ColumnImpl("table2.columnb"), 3)
                .intersect(new Criteria()
                        .addSelectColumn(new ColumnImpl("table1.column1"))
                        .where(new ColumnImpl("table1.columna"), 1));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("(SELECT table2.column2 FROM table2 "
                + "WHERE table2.columnb=?) "
                + "INTERSECT (SELECT table1.column1 FROM table1 "
                + "WHERE table1.columna=?)",
                query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals(3, query.getPreparedStatementReplacements().get(0));
        assertEquals(1, query.getPreparedStatementReplacements().get(1));
    }

    public void testIntersectAll() throws Exception
    {
        Criteria criteria = new Criteria()
                .addSelectColumn(new ColumnImpl("table2.column2"))
                .where(new ColumnImpl("table2.columnb"), 3)
                .intersectAll(new Criteria()
                        .addSelectColumn(new ColumnImpl("table1.column1"))
                        .where(new ColumnImpl("table1.columna"), 1));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("(SELECT table2.column2 FROM table2 "
                + "WHERE table2.columnb=?) "
                + "INTERSECT ALL (SELECT table1.column1 FROM table1 "
                + "WHERE table1.columna=?)",
                query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals(3, query.getPreparedStatementReplacements().get(0));
        assertEquals(1, query.getPreparedStatementReplacements().get(1));
    }

    public void testUnionOrderByLimitOffset() throws Exception
    {
        Criteria criteria = new Criteria()
                .addSelectColumn(new ColumnImpl("table2.column2"))
                .where(new ColumnImpl("table2.columnb"), 3)
                .union(new Criteria()
                        .addSelectColumn(new ColumnImpl("table1.column1"))
                        .where(new ColumnImpl("table1.columna"), 1))
                .addAscendingOrderByColumn(new ColumnImpl("table2.column2"))
                .setLimit(10)
                .setOffset(20);
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("(SELECT table2.column2 FROM table2 "
                + "WHERE table2.columnb=?) "
                + "UNION (SELECT table1.column1 FROM table1 "
                + "WHERE table1.columna=?)"
                + " ORDER BY table2.column2 ASC"
                + " LIMIT 10 OFFSET 20",
                query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals(3, query.getPreparedStatementReplacements().get(0));
        assertEquals(1, query.getPreparedStatementReplacements().get(1));
    }

    public void testSetOperationBraces() throws Exception
    {
        Criteria criteria = new Criteria()
                .addSelectColumn(new ColumnImpl("table4.column4"))
                .where(new ColumnImpl("table4.columnd"), 4);
        Criteria otherCriteria = new Criteria()
                .addSelectColumn(new ColumnImpl("table3.column3"))
                .where(new ColumnImpl("table3.columnc"), 3);
        criteria.unionAll(otherCriteria);
        Criteria intersectCriteria = new Criteria()
                .addSelectColumn(new ColumnImpl("table2.column2"))
                .where(new ColumnImpl("table2.columnb"), 2);
        otherCriteria = new Criteria()
                .addSelectColumn(new ColumnImpl("table1.column1"))
                .where(new ColumnImpl("table1.columna"), 1);
        intersectCriteria.unionAll(otherCriteria);
        criteria.intersect(intersectCriteria);

        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("((SELECT table4.column4 FROM table4 "
                + "WHERE table4.columnd=?) "
                + "UNION ALL (SELECT table3.column3 FROM table3 "
                + "WHERE table3.columnc=?))"
                + " INTERSECT ((SELECT table2.column2 FROM table2 "
                + "WHERE table2.columnb=?) "
                + "UNION ALL (SELECT table1.column1 FROM table1 "
                + "WHERE table1.columna=?))",
                query.toString());
        assertEquals(4, query.getPreparedStatementReplacements().size());
        assertEquals(4, query.getPreparedStatementReplacements().get(0));
        assertEquals(3, query.getPreparedStatementReplacements().get(1));
        assertEquals(2, query.getPreparedStatementReplacements().get(2));
        assertEquals(1, query.getPreparedStatementReplacements().get(3));
    }

    public void testEnumValues() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(new ColumnImpl("table.column1"), EnumWithValueMethod.A)
                .and(EnumWithValueMethod.B, new ColumnImpl("table.column2"))
                .addSelectColumn(new ColumnImpl("table.column1"));
        Query query = SqlBuilder.buildQuery(criteria);
        assertEquals("SELECT table.column1 FROM table "
                + "WHERE (table.column1=? "
                + "AND ?=table.column2)",
                query.toString());
        assertEquals(2, query.getPreparedStatementReplacements().size());
        assertEquals("A", query.getPreparedStatementReplacements().get(0));
        assertEquals("B", query.getPreparedStatementReplacements().get(1));
    }

    /**
     * Tests that an enum which does not have a getValue() method cannot be added as select value.
     */
    public void testEnumValuesNoGetValueMethod() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(new ColumnImpl("table.column1"), EnumWithoutValueMethod.A);
        try
        {
            SqlBuilder.buildQuery(criteria);
            fail("Exception expected");
        }
        catch (TorqueException e)
        {
            assertEquals("An enum is used as Criterion value but its class, "
                    + "org.apache.torque.sql.SqlBuilderTest$EnumWithoutValueMethod, "
                    + "does not have a parameterless getValue() method",
                    e.getMessage());
        }
    }

    public static enum EnumWithValueMethod
    {
        A,
        B,
        C;

        public String getValue()
        {
            return toString();
        }

        public static EnumWithValueMethod getByValue(final String arg)
        {
            return valueOf(arg);
        }
    }

    public static enum EnumWithoutValueMethod
    {
        A,
        B,
        C;
    }
}
