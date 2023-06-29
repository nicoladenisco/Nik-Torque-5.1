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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.torque.BaseTestCase;
import org.apache.torque.Column;
import org.apache.torque.ColumnImpl;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.TableMap;
import org.apache.torque.sql.OrderBy;
import org.apache.torque.sql.Query;
import org.apache.torque.sql.SqlBuilder;
import org.apache.torque.util.UniqueList;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for Criteria.
 *
 * @author <a href="mailto:celkins@scardini.com">Christopher Elkins</a>
 * @author <a href="mailto:sam@neurogrid.com">Sam Joseph</a>
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id: CriteriaTest.java 1867515 2019-09-25 15:02:03Z gk $
 */
public class CriteriaTest extends BaseTestCase
{

    /** The criteria to use in the test. */
    private Criteria c;

    /**
     * Initializes the criteria.
     */
    @BeforeEach
    public void setUp() throws Exception
    {
        super.setUp();
        c = new Criteria();
        DatabaseMap databaseMap = Torque.getDatabaseMap("postgresql");
        if (!databaseMap.containsTable("TABLE"))
        {
            TableMap tableMap = databaseMap.addTable("TABLE");
            {
                ColumnMap columnMap1 = new ColumnMap("COLUMN1", tableMap);
                columnMap1.setType("");
                columnMap1.setJavaType("String");
                tableMap.addColumn(columnMap1);
            }
            {
                ColumnMap columnMap2 = new ColumnMap("COLUMN2", tableMap);
                columnMap2.setType("");
                columnMap2.setJavaType("String");
                tableMap.addColumn(columnMap2);
            }
            {
                ColumnMap columnMap3 = new ColumnMap("COLUMN3", tableMap);
                columnMap3.setType("");
                columnMap3.setJavaType("String");
                tableMap.addColumn(columnMap3);
            }
            {
                ColumnMap columnMap4 = new ColumnMap("COLUMN4", tableMap);
                columnMap4.setType(Integer.valueOf(0));
                columnMap4.setJavaType("Integer");
                tableMap.addColumn(columnMap4);
            }
        }
    }

    /**
     * Test basic where condition on a string.
     */
    @Test
    public void testWhereString()
    {
        final Column column = new ColumnImpl("myTable", "myColumn");
        final String value = "myValue";

        c.where(column, value);

        // Verify that the Criterion is not composite
        assertFalse(c.getTopLevelCriterion().isComposite());

        // Verify that what we get out is what we put in
        assertEquals(column, c.getTopLevelCriterion().getLValue());
        assertEquals(value, c.getTopLevelCriterion().getRValue());
        assertEquals(Criteria.EQUAL, c.getTopLevelCriterion().getComparison());
    }

    /**
     * Test basic where condition on a string. The condition is reversed, i.e
     * the String preceeds the column.
     */
    @Test
    public void testWhereStringReversed()
    {
        final Column column = new ColumnImpl("myTable", "myColumn");
        final String value = "myValue";

        c.where(value, column);

        // Verify that the Criterion is not composite
        assertFalse(c.getTopLevelCriterion().isComposite());

        // Verify that what we get out is what we put in
        assertEquals(value, c.getTopLevelCriterion().getLValue());
        assertEquals(column, c.getTopLevelCriterion().getRValue());
        assertEquals(Criteria.EQUAL, c.getTopLevelCriterion().getComparison());
    }

    /**
     * Test basic where condition on a string with a non-equal.
     * comparison operator.
     */
    @Test
    public void testWhereNotEqual()
    {
        final Column column = new ColumnImpl("myTable", "myColumn");
        final String value = "myValue";

        // Add the string
        c.where(column, value, Criteria.NOT_EQUAL);

        // Verify that the Criterion is not composite
        assertFalse(c.getTopLevelCriterion().isComposite());

        // Verify that what we get out is what we put in
        assertEquals(column, c.getTopLevelCriterion().getLValue());
        assertEquals(value, c.getTopLevelCriterion().getRValue());
        assertEquals(Criteria.NOT_EQUAL, c.getTopLevelCriterion().getComparison());
    }

    /**
     * Tests that unary operators as rValue are interpreted
     * as comparison operator in the two-arg where method.
     */
    @Test
    public void testWhereUnaryOperator()
    {
        // prepare
        final Column column = new ColumnImpl("myTable", "myColumn");
        final SqlEnum operator = SqlEnum.ISNOTNULL;

        // execute
        c.where(column, operator);

        // Verify that what we get out is what we put in
        assertEquals(column, c.getTopLevelCriterion().getLValue());
        assertEquals(null, c.getTopLevelCriterion().getRValue());
        assertEquals(operator, c.getTopLevelCriterion().getComparison());
    }

    /**
     * Tests that unary operators as rValue are interpreted
     * as comparison operator in the two-arg or method.
     */
    @Test
    public void testOrUnaryOperator()
    {
        // prepare
        final Column column = new ColumnImpl("myTable", "myColumn");
        final SqlEnum operator = SqlEnum.ISNOTNULL;

        // execute
        c.or(column, operator);

        // Verify that what we get out is what we put in
        assertEquals(column, c.getTopLevelCriterion().getLValue());
        assertEquals(null, c.getTopLevelCriterion().getRValue());
        assertEquals(operator, c.getTopLevelCriterion().getComparison());
    }

    /**
     * Test where condition with several ANDs compairing against Strings.
     */
    @Test
    public void testAndString()
    {
        final Column column1 = new ColumnImpl("myTable", "myColumn1");
        final Column column2 = new ColumnImpl("myTable", "myColumn2");
        final String value1a = "1a";
        final String value1b = "1b";
        final String value2a = "2a";
        final String value2b = "2b";

        // Add the string
        c.where(column1, value1a)
        .and(column1, value1b)
        .and(column2, value2a)
        .and(column2, value2b);

        // Verify that the Criterion is a composite
        assertTrue(c.getTopLevelCriterion().isComposite());

        // Verify that what we get out is what we put in
        assertEquals(
                column1,
                c.getTopLevelCriterion().getParts().get(0).getLValue());
        assertEquals(
                value1a,
                c.getTopLevelCriterion().getParts().get(0).getRValue());
        assertEquals(
                Criteria.EQUAL,
                c.getTopLevelCriterion().getParts().get(0).getComparison());
        assertEquals(
                column1,
                c.getTopLevelCriterion().getParts().get(1).getLValue());
        assertEquals(
                value1b,
                c.getTopLevelCriterion().getParts().get(1).getRValue());
        assertEquals(
                Criteria.EQUAL,
                c.getTopLevelCriterion().getParts().get(1).getComparison());
        assertEquals(
                column2,
                c.getTopLevelCriterion().getParts().get(2).getLValue());
        assertEquals(
                value2a,
                c.getTopLevelCriterion().getParts().get(2).getRValue());
        assertEquals(
                Criteria.EQUAL,
                c.getTopLevelCriterion().getParts().get(2).getComparison());
        assertEquals(
                column2,
                c.getTopLevelCriterion().getParts().get(3).getLValue());
        assertEquals(
                value2b,
                c.getTopLevelCriterion().getParts().get(3).getRValue());
        assertEquals(
                Criteria.EQUAL,
                c.getTopLevelCriterion().getParts().get(3).getComparison());
    }

    /**
     * Tests that a criterion is copied when being added as top level criterion
     * using where (also tests and).
     * checks TORQUE-243.
     */
    @Test
    public void testCriteriaTopLevelCriterionGetsCopiedWhere()
    {
        Criterion criterion = new Criterion(stringColumnMap, "abc");
        Criteria criteria = new Criteria().where(criterion);
        assertNotSame(criterion, criteria.getTopLevelCriterion());
    }

    /**
     * Tests that a criterion is copied when being added as top level criterion
     * using where (also tests and).
     * checks TORQUE-243.
     */
    @Test
    public void testCriteriaTopLevelCriterionGetsCopiedOr()
    {
        Criterion criterion = new Criterion(stringColumnMap, "abc");
        Criteria criteria = new Criteria().or(criterion);
        assertNotSame(criterion, criteria.getTopLevelCriterion());
    }

    /**
     * Tests that a criterion is copied when being added as top level criterion
     * using where (also tests and).
     * checks TORQUE-243.
     */
    @Test
    public void testCriteriaCriterionGetsCopiedWhere()
    {
        Criteria criteria = new Criteria().where(stringColumnMap, "def");
        Criterion criterion = new Criterion(stringColumnMap, "abc");
        criteria.where(criterion);
        assertNotSame(
                criterion,
                criteria.getTopLevelCriterion().getParts().get(0));
        assertNotSame(
                criterion,
                criteria.getTopLevelCriterion().getParts().get(1));
    }

    /**
     * Tests that a criterion is copied when being added as top level criterion
     * using where (also tests and).
     * checks TORQUE-243.
     */
    @Test
    public void testCriteriaCriterionGetsCopiedOr()
    {
        Criteria criteria = new Criteria().where(stringColumnMap, "def");
        Criterion criterion = new Criterion(stringColumnMap, "abc");
        criteria.or(criterion);
        assertNotSame(
                criterion,
                criteria.getTopLevelCriterion().getParts().get(0));
        assertNotSame(
                criterion,
                criteria.getTopLevelCriterion().getParts().get(1));
    }

    /**
     * Tests that a criterion does not get changed if it is added as
     * root criterion to a criteria which is afterwards changed.
     * checks TORQUE-243.
     */
    @Test
    public void testCriterionDoesNotGetChangedByCriteriaOr()
    {
        Criterion criterion = new Criterion(stringColumnMap, "abc");
        Criteria criteria = new Criteria().where(criterion);
        criteria.or(stringColumnMap, "def");
        assertFalse(criterion.isComposite());
        assertEquals(stringColumnMap, criterion.getLValue());
        assertEquals("abc", criterion.getRValue());
    }

    /**
     * Tests that a criterion does not get changed if it is added as
     * root criterion to a criteria which is afterwards changed.
     * checks TORQUE-243.
     */
    @Test
    public void testCriterionDoesNotGetChangedByCriteriaAnd()
    {
        Criterion criterion = new Criterion(stringColumnMap, "abc");
        Criteria criteria = new Criteria().where(criterion);
        criteria.and(stringColumnMap, "def");
        assertFalse(criterion.isComposite());
        assertEquals(stringColumnMap, criterion.getLValue());
        assertEquals("abc", criterion.getRValue());
    }

    /**
     * Test that nesting Criterions works for equals comparison.
     */
    @Test
    public void testNestedCriterionComparisonEqual() throws TorqueException
    {
        final Column column2 = new ColumnImpl("myTable2", "myColumn2");
        final String value2 = "myValue2";

        final Column column3 = new ColumnImpl("myTable3", "myColumn3");
        final String value3 = "myValue3";

        final Column column4 = new ColumnImpl("myTable4", "myColumn4");
        final String value4 = "myValue4";

        final Column column5 = new ColumnImpl("myTable5", "myColumn5");
        final String value5 = "myValue5";

        Criterion crit2 =
                new Criterion(column2, value2, Criteria.EQUAL);
        Criterion crit3 =
                new Criterion(column3, value3, Criteria.EQUAL);
        Criterion crit4 =
                new Criterion(column4, value4, Criteria.EQUAL);
        Criterion crit5 =
                new Criterion(column5, value5, Criteria.EQUAL);

        crit2.and(crit3).or(crit4.and(crit5));
        c.where(crit2);
        c.addSelectColumn(new ColumnImpl(null, "myTable2", null, "*"));

        String expect =
                "SELECT * FROM myTable2, myTable3, myTable4, myTable5 WHERE "
                        + "((myTable2.myColumn2=? "
                        + "AND myTable3.myColumn3=?) "
                        + "OR (myTable4.myColumn4=? "
                        + "AND myTable5.myColumn5=?))";
        Query result = SqlBuilder.buildQuery(c);
        assertEquals(expect, result.toString());
        List<Object> preparedStatementReplacements
        = result.getPreparedStatementReplacements();
        assertEquals(4, preparedStatementReplacements.size());
        assertEquals("myValue2", preparedStatementReplacements.get(0));
        assertEquals("myValue3", preparedStatementReplacements.get(1));
        assertEquals("myValue4", preparedStatementReplacements.get(2));
        assertEquals("myValue5", preparedStatementReplacements.get(3));
    }

    /**
     * Test that nesting Criterions works for other comparisons than equal.
     * @throws TorqueException if fails
     */
    @Test
    public void testNestedCriterionComparisonLessGreaterThan()
            throws TorqueException
    {
        final Column column2 = new ColumnImpl("myTable2", "myColumn2");
        final String value2 = "myValue2";

        final Column column3 = new ColumnImpl("myTable3", "myColumn3");
        final String value3 = "myValue3";

        final Column column4 = new ColumnImpl("myTable4", "myColumn4");
        final String value4 = "myValue4";

        final Column column5 = new ColumnImpl("myTable5", "myColumn5");
        final String value5 = "myValue5";

        c = new Criteria();
        Criterion crit2 = new Criterion(
                column2,
                value2,
                Criteria.LESS_THAN);
        Criterion crit3 = new Criterion(
                column3,
                value3,
                Criteria.LESS_EQUAL);
        Criterion crit4 = new Criterion(
                column4,
                value4,
                Criteria.GREATER_THAN);
        Criterion crit5 = new Criterion(
                column5,
                value5,
                Criteria.GREATER_EQUAL);

        crit2.and(crit3).or(crit4).and(crit5);
        c.where(crit2);
        c.addSelectColumn(new ColumnImpl(null, "myTable2", null, "*"));
        String expect =
                "SELECT * FROM myTable2, myTable3, myTable4, myTable5 WHERE "
                        + "(((myTable2.myColumn2<? "
                        + "AND myTable3.myColumn3<=?) "
                        + "OR myTable4.myColumn4>?) "
                        + "AND myTable5.myColumn5>=?)";
        Query result = SqlBuilder.buildQuery(c);
        assertEquals(expect, result.toString());
        List<Object> preparedStatementReplacements
        = result.getPreparedStatementReplacements();
        assertEquals(4, preparedStatementReplacements.size());
        assertEquals("myValue2", preparedStatementReplacements.get(0));
        assertEquals("myValue3", preparedStatementReplacements.get(1));
        assertEquals("myValue4", preparedStatementReplacements.get(2));
        assertEquals("myValue5", preparedStatementReplacements.get(3));
    }

    /**
     * Tests &lt;= and =&gt;.
     * @throws TorqueException if fail
     */
    @Test
    public void testBetweenCriterion() throws TorqueException
    {
        Criterion cn1 = new Criterion(
                new ColumnImpl("INVOICE", "COST"),
                -1,
                Criteria.GREATER_EQUAL);
        Criterion cn2 = new Criterion(
                new ColumnImpl("INVOICE", "COST"),
                1,
                Criteria.LESS_EQUAL);
        c.where(cn1).and(cn2);
        String expect =
                "SELECT  FROM INVOICE WHERE "
                        + "(INVOICE.COST>=? AND INVOICE.COST<=?)";
        Query result = SqlBuilder.buildQuery(c);

        assertEquals(expect, result.toString());
        assertEquals(2, result.getPreparedStatementReplacements().size());
        assertEquals(-1, result.getPreparedStatementReplacements().get(0));
        assertEquals(1, result.getPreparedStatementReplacements().get(1));
    }

    /**
     * Test Criterion.setIgnoreCase().
     */
    @Test
    public void testCriterionIgnoreCase() throws TorqueException
    {
        Criterion criterion1 = new Criterion(
                new ColumnImpl("TABLE", "COLUMN1"), "FoObAr1", Criteria.LIKE);
        criterion1.setIgnoreCase(true);
        Criterion criterion2 = new Criterion(
                new ColumnImpl("TABLE", "COLUMN2"), "FoObAr2", Criteria.EQUAL);
        criterion2.setIgnoreCase(true);
        Criterion criterion3 = new Criterion(
                new ColumnImpl("TABLE", "COLUMN3"), "FoObAr3", Criteria.EQUAL);
        Criterion criterion4 = new Criterion(
                new ColumnImpl("TABLE", "COLUMN4"), Integer.valueOf(1), Criteria.EQUAL);
        criterion4.setIgnoreCase(true);
        c.where(criterion1.and(criterion2).and(criterion3).and(criterion4));
        c.addSelectColumn(new ColumnImpl(null, "TABLE", null, "*"));
        c.setDbName("postgresql");
        Query result = SqlBuilder.buildQuery(c);

        String expect = "SELECT * FROM TABLE WHERE "
                + "(UPPER(TABLE.COLUMN1)=UPPER(?)"
                + " AND UPPER(TABLE.COLUMN2)=UPPER(?)"
                + " AND TABLE.COLUMN3=? AND TABLE.COLUMN4=?)";
        assertEquals(expect, result.toString());
        List<Object> replacements = result.getPreparedStatementReplacements();
        assertEquals(4, replacements.size());
        assertEquals("FoObAr1", replacements.get(0));
        assertEquals("FoObAr2", replacements.get(1));
        assertEquals("FoObAr3", replacements.get(2));
        assertEquals(Integer.valueOf(1), replacements.get(3));
    }

    /**
     * Test that true is evaluated correctly in Mysql.
     */
    @Test
    public void testBooleanMysql() throws TorqueException
    {
        c.where(new ColumnImpl("TABLE", "COLUMN"), true);
        c.setDbName("mysql");

        Query result = SqlBuilder.buildQuery(c);

        assertEquals(
                "SELECT  FROM TABLE WHERE TABLE.COLUMN=?",
                result.toString());
        List<Object> preparedStatementReplacements
        = result.getPreparedStatementReplacements();
        assertEquals(1, preparedStatementReplacements.size());
        assertEquals(Boolean.TRUE, preparedStatementReplacements.get(0));
    }

    /**
     * Test that true is evaluated correctly in Postgresql.
     */
    @Test
    public void testBooleanPostgresql() throws TorqueException
    {
        // test the postgresql variation
        c.where(new ColumnImpl("TABLE", "COLUMN"), true);
        c.setDbName("postgresql");

        Query result = SqlBuilder.buildQuery(c);

        assertEquals(
                "SELECT  FROM TABLE WHERE TABLE.COLUMN=?",
                result.toString());
        List<Object> preparedStatementReplacements
        = result.getPreparedStatementReplacements();
        assertEquals(1, preparedStatementReplacements.size());
        assertEquals(Boolean.TRUE, preparedStatementReplacements.get(0));
    }

    /**
     * Testcase for whereDate()
     */
    @Test
    public void testWhereDate() throws TorqueException
    {
        c.whereDate(new ColumnImpl("TABLE", "DATE_COLUMN"), 2003, 0, 22);

        Query result = SqlBuilder.buildQuery(c);

        assertEquals(
                "SELECT  FROM TABLE WHERE TABLE.DATE_COLUMN=?",
                result.toString());
        List<Object> preparedStatementReplacements
        = result.getPreparedStatementReplacements();
        assertEquals(1, preparedStatementReplacements.size());
        assertEquals(
                new GregorianCalendar(2003, 0, 22).getTime(),
                preparedStatementReplacements.get(0));
    }

    /**
     * Testcase for andDate().
     */
    @Test
    public void testAndDate() throws TorqueException
    {
        c.whereDate(new ColumnImpl("TABLE", "DATE_COLUMN"),
                2003,
                0,
                22,
                Criteria.GREATER_THAN);
        c.andDate(new ColumnImpl("TABLE", "DATE_COLUMN"),
                2004,
                2,
                24,
                Criteria.LESS_THAN);

        Query result = SqlBuilder.buildQuery(c);
        assertEquals(
                "SELECT  FROM TABLE WHERE "
                        + "(TABLE.DATE_COLUMN>? AND TABLE.DATE_COLUMN<?)",
                        result.toString());
        List<Object> preparedStatementReplacements
        = result.getPreparedStatementReplacements();
        assertEquals(2, preparedStatementReplacements.size());
        assertEquals(
                new GregorianCalendar(2003, 0, 22).getTime(),
                preparedStatementReplacements.get(0));
        assertEquals(
                new GregorianCalendar(2004, 2, 24).getTime(),
                preparedStatementReplacements.get(1));
    }

    /**
     * testcase for where(Date)
     */
    @Test
    public void testDateWhere() throws TorqueException
    {
        Calendar cal = new GregorianCalendar(2003, 0, 22);
        Date date = cal.getTime();
        c.where(new ColumnImpl("TABLE", "DATE_COLUMN"), date);

        Query result = SqlBuilder.buildQuery(c);
        assertEquals(
                "SELECT  FROM TABLE WHERE "
                        + "TABLE.DATE_COLUMN=?",
                        result.toString());

        List<Object> preparedStatementReplacements
        = result.getPreparedStatementReplacements();
        assertEquals(1, preparedStatementReplacements.size());
        assertEquals(
                new GregorianCalendar(2003, 0, 22).getTime(),
                preparedStatementReplacements.get(0));
    }

    @Test
    public void testAndCurrentDate() throws TorqueException
    {
        c.where(new ColumnImpl("TABLE", "DATE_COLUMN"), Criteria.CURRENT_DATE);
        c.addSelectColumn(new ColumnImpl(null, "TABLE", null, "COUNT(*)"));

        Query result = SqlBuilder.buildQuery(c);
        assertEquals(
                "SELECT COUNT(*) FROM TABLE WHERE "
                        + "TABLE.DATE_COLUMN=CURRENT_DATE",
                        result.toString());

        List<Object> preparedStatementReplacements
        = result.getPreparedStatementReplacements();
        assertEquals(0, preparedStatementReplacements.size());
    }

    @Test
    public void testAndCurrentTime() throws TorqueException
    {
        c.where(new ColumnImpl("TABLE", "TIME_COLUMN"), Criteria.CURRENT_TIME);
        c.addSelectColumn(new ColumnImpl(null, "TABLE", null, "COUNT(*)"));

        Query result = SqlBuilder.buildQuery(c);
        assertEquals(
                "SELECT COUNT(*) FROM TABLE WHERE "
                        + "TABLE.TIME_COLUMN=CURRENT_TIME",
                        result.toString());

        List<Object> preparedStatementReplacements
        = result.getPreparedStatementReplacements();
        assertEquals(0, preparedStatementReplacements.size());
    }

    @Test
    public void testCriteriaOffsetLimit() throws TorqueException
    {
        c.whereDate(new ColumnImpl("TABLE", "DATE_COLUMN"), 2003, 0, 22);
        c.setOffset(3).setLimit(5);
        c.addSelectColumn(new ColumnImpl(null, "TABLE", null, "COUNT(*)"));

        Query result = SqlBuilder.buildQuery(c);
        String expect
        = "SELECT COUNT(*) FROM TABLE WHERE TABLE.DATE_COLUMN=?"
                + " LIMIT 5 OFFSET 3";
        assertEquals(expect, result.toString());
    }

    @Test
    public void testCriteriaWithOffsetNoLimitPostgresql()
            throws TorqueException
    {
        c.whereDate(new ColumnImpl("TABLE", "DATE_COLUMN"), 2003, 0, 22);
        c.setOffset(3);
        c.addSelectColumn(new ColumnImpl(null, "TABLE", null, "COUNT(*)"));

        Query result = SqlBuilder.buildQuery(c);
        String expect
        = "SELECT COUNT(*) FROM TABLE WHERE TABLE.DATE_COLUMN=?"
                + " OFFSET 3";
        assertEquals(expect, result.toString());
    }

    /**
     * TORQUE-87
     */
    @Test
    public void testCriteriaWithOffsetNoLimitMysql() throws TorqueException
    {
        c.whereDate(new ColumnImpl("TABLE", "DATE_COLUMN"), 2003, 0, 22);
        c.setOffset(3);
        c.addSelectColumn(new ColumnImpl(null, "TABLE", null, "COUNT(*)"));
        c.setDbName("mysql");

        Query result = SqlBuilder.buildQuery(c);
        String expect
        = "SELECT COUNT(*) FROM TABLE WHERE TABLE.DATE_COLUMN=?"
                + " LIMIT 18446744073709551615 OFFSET 3";
        assertEquals(expect, result.toString());
    }

    @Test
    public void testCriteriaToStringLimit() throws TorqueException
    {
        c.whereDate(new ColumnImpl("TABLE", "DATE_COLUMN"), 2003, 0, 22);
        c.setLimit(5);
        c.addSelectColumn(new ColumnImpl(null, "TABLE", null, "COUNT(*)"));

        Query result = SqlBuilder.buildQuery(c);
        String expect
        = "SELECT COUNT(*) FROM TABLE WHERE TABLE.DATE_COLUMN=?"
                + " LIMIT 5";
        assertEquals(expect, result.toString());
    }

    /**
     * This test case verifies if the Criteria.LIKE comparison type will
     * get replaced through Criteria.EQUAL if there are no SQL wildcards
     * in the given value.
     */
    @Test
    public void testLikeWithoutWildcards() throws TorqueException
    {
        c.where(new ColumnImpl("TABLE", "COLUMN"),
                "no wildcards",
                Criteria.LIKE);

        Query result = SqlBuilder.buildQuery(c);
        assertEquals(
                "SELECT  FROM TABLE WHERE "
                        + "TABLE.COLUMN=?",
                        result.toString());

        List<Object> preparedStatementReplacements
        = result.getPreparedStatementReplacements();
        assertEquals(1, preparedStatementReplacements.size());
        assertEquals(
                "no wildcards",
                preparedStatementReplacements.get(0));
    }

    /**
     * This test case verifies if the Criteria.NOT_LIKE comparison type will
     * get replaced through Criteria.NOT_EQUAL if there are no SQL wildcards
     * in the given value.
     */
    @Test
    public void testNotLikeWithoutWildcards()
    {
        c.where(new ColumnImpl("TABLE", "COLUMN"),
                "no wildcards",
                Criteria.NOT_LIKE);

        String firstExpect = "SELECT  FROM TABLE WHERE TABLE.COLUMN!=?";
        String secondExpect = "SELECT  FROM TABLE WHERE TABLE.COLUMN<>?";

        Query result = null;
        try
        {
            result = SqlBuilder.buildQuery(c);
        }
        catch (TorqueException e)
        {
            e.printStackTrace();
            fail("TorqueException thrown in SqlBuilder.buildQuery()");
        }

        assertTrue(result.toString().equals(firstExpect)
                || result.toString().equals(secondExpect));
        List<Object> preparedStatementReplacements
        = result.getPreparedStatementReplacements();
        assertEquals(1, preparedStatementReplacements.size());
        assertEquals("no wildcards", preparedStatementReplacements.get(0));
    }

    /**
     * Test that serialization works.
     */
    @Test
    public void testSerialization()
    {
        c.setOffset(10);
        c.setLimit(11);
        c.setIgnoreCase(true);
        c.setSingleRecord(true);
        c.setDbName("myDB");
        c.setAll();
        c.setDistinct();
        c.addSelectColumn(new ColumnImpl("Author", "NAME"));
        c.addSelectColumn(new ColumnImpl("Author", "AUTHOR_ID"));
        c.addDescendingOrderByColumn(new ColumnImpl("Author", "NAME"));
        c.addAscendingOrderByColumn(new ColumnImpl("Author", "AUTHOR_ID"));
        c.addAlias("Writer", "Author");
        c.addAsColumn("AUTHOR_NAME", new ColumnImpl("Author", "NAME"));
        c.addJoin(new ColumnImpl("Author", "AUTHOR_ID"),
                new ColumnImpl("Book", "AUTHOR_ID"),
                Criteria.INNER_JOIN);
        c.where(new ColumnImpl("Author", "NAME"), "author%", Criteria.LIKE);
        c.addFrom(new FromElement("Author"));

        // Some direct Criterion checks
        Criterion cn = c.getTopLevelCriterion();
        cn.setIgnoreCase(true);
        assertEquals("author%", cn.getRValue());
        assertEquals(Criteria.LIKE, cn.getComparison());
        Criterion cnDirectClone = (Criterion) SerializationUtils.clone(cn);
        assertEquals(cn, cnDirectClone);

        // Clone the object
        Criteria cClone = (Criteria) SerializationUtils.clone(c);

        // Check the clone
        assertEquals(c.getTopLevelCriterion(), cClone.getTopLevelCriterion());
        assertEquals(10, cClone.getOffset());
        assertEquals(c.getOffset(), cClone.getOffset());
        assertEquals(11, cClone.getLimit());
        assertEquals(c.getLimit(), cClone.getLimit());
        assertEquals(true, cClone.isIgnoreCase());
        assertEquals(c.isIgnoreCase(), cClone.isIgnoreCase());
        assertEquals(true, cClone.isSingleRecord());
        assertEquals(c.isSingleRecord(), cClone.isSingleRecord());
        assertEquals("myDB", cClone.getDbName());
        assertEquals(c.getDbName(), cClone.getDbName());
        List<String> selectModifiersClone = cClone.getSelectModifiers();
        assertTrue(selectModifiersClone.contains(
                Criteria.ALL.toString()));
        assertTrue(selectModifiersClone.contains(
                Criteria.DISTINCT.toString()));
        assertEquals(c.getSelectModifiers(), cClone.getSelectModifiers());
        List<Column> selectColumnsClone = cClone.getSelectColumns();
        assertTrue(selectColumnsClone.contains(
                new ColumnImpl("Author", "NAME")));
        assertTrue(selectColumnsClone.contains(
                new ColumnImpl("Author", "AUTHOR_ID")));
        assertEquals(c.getSelectColumns(), cClone.getSelectColumns());
        List<OrderBy> orderByColumnsClone = cClone.getOrderByColumns();
        assertTrue(orderByColumnsClone.contains(new OrderBy(
                new ColumnImpl("Author.NAME"),
                SqlEnum.DESC,
                false)));
        assertTrue(orderByColumnsClone.contains(new OrderBy(
                new ColumnImpl("Author.AUTHOR_ID"),
                SqlEnum.ASC,
                false)));
        assertEquals(c.getOrderByColumns(), cClone.getOrderByColumns());
        Map<String, Object> aliasesClone = cClone.getAliases();
        assertTrue(aliasesClone.containsKey("Writer"));
        assertEquals("Author", aliasesClone.get("Writer"));
        assertEquals(c.getAliases(), cClone.getAliases());
        Map<String, Column> asColumnsClone = cClone.getAsColumns();
        assertTrue(asColumnsClone.containsKey("AUTHOR_NAME"));
        assertEquals(new ColumnImpl("Author", "NAME"),
                asColumnsClone.get("AUTHOR_NAME"));
        assertEquals(c.getAsColumns(), cClone.getAsColumns());
        List<FromElement>fromElementsClone = new UniqueList<>();
        fromElementsClone.add(new FromElement("Author"));
        assertEquals(c.getFromElements(), fromElementsClone);

        // Check Joins
        List<Join> joinsClone = cClone.getJoins();
        Join joinClone = joinsClone.get(0);
        assertEquals(new ColumnImpl("Author", "AUTHOR_ID"),
                joinClone.getJoinCondition().getLValue());
        assertEquals(new ColumnImpl("Book", "AUTHOR_ID"),
                joinClone.getJoinCondition().getRValue());
        assertEquals(Criteria.INNER_JOIN, joinClone.getJoinType());
        assertEquals(c.getJoins(), cClone.getJoins());

        // Some Criterion checks
        Criterion cnClone = cClone.getTopLevelCriterion();
        assertEquals("author%", cnClone.getRValue());
        assertEquals(Criteria.LIKE, cnClone.getComparison());
        assertEquals(cn.isIgnoreCase(), cnClone.isIgnoreCase());

        // Confirm that equals() checks all of the above.
        assertEquals(c, cClone);

        // Check hashCode() too.
        assertEquals(c.hashCode(), cClone.hashCode());
    }

    /**
     * Test that cloning works.
     */
    @Test
    public void testClone()
    {
        c.setOffset(10);
        c.setLimit(11);
        c.setIgnoreCase(true);
        c.setSingleRecord(true);
        c.setDbName("myDB");
        c.setAll();
        c.setDistinct();
        c.addSelectColumn(new ColumnImpl("Author", "NAME"));
        c.addSelectColumn(new ColumnImpl("Author", "AUTHOR_ID"));
        c.addDescendingOrderByColumn(new ColumnImpl("Author", "NAME"));
        c.addAscendingOrderByColumn(new ColumnImpl("Author", "AUTHOR_ID"));
        c.addAlias("Writer", "Author");
        c.addAsColumn("AUTHOR_NAME", new ColumnImpl("Author", "NAME"));
        c.addJoin(new ColumnImpl("Author", "AUTHOR_ID"),
                new ColumnImpl("Book", "AUTHOR_ID"),
                Criteria.INNER_JOIN);
        c.where(new ColumnImpl("Author", "NAME"), "author%", Criteria.LIKE);

        // Clone the object
        Criteria cClone = (Criteria) c.clone();

        // Check the clone
        assertEquals(c.getTopLevelCriterion(), cClone.getTopLevelCriterion());
        assertEquals(10, cClone.getOffset());
        assertEquals(c.getOffset(), cClone.getOffset());
        assertEquals(11, cClone.getLimit());
        assertEquals(c.getLimit(), cClone.getLimit());
        assertEquals(true, cClone.isIgnoreCase());
        assertEquals(c.isIgnoreCase(), cClone.isIgnoreCase());
        assertEquals(true, cClone.isSingleRecord());
        assertEquals(c.isSingleRecord(), cClone.isSingleRecord());
        assertEquals("myDB", cClone.getDbName());
        assertEquals(c.getDbName(), cClone.getDbName());
        List<String> selectModifiersClone = cClone.getSelectModifiers();
        assertTrue(selectModifiersClone.contains(
                Criteria.ALL.toString()));
        assertTrue(selectModifiersClone.contains(
                Criteria.DISTINCT.toString()));
        assertEquals(c.getSelectModifiers(), cClone.getSelectModifiers());
        List<Column> selectColumnsClone = cClone.getSelectColumns();
        assertTrue(selectColumnsClone.contains(
                new ColumnImpl("Author", "NAME")));
        assertTrue(selectColumnsClone.contains(
                new ColumnImpl("Author", "AUTHOR_ID")));
        assertEquals(c.getSelectColumns(), cClone.getSelectColumns());
        List<OrderBy> orderByColumnsClone = cClone.getOrderByColumns();
        assertTrue(orderByColumnsClone.contains(new OrderBy(
                new ColumnImpl("Author.NAME"),
                SqlEnum.DESC,
                false)));
        assertTrue(orderByColumnsClone.contains(new OrderBy(
                new ColumnImpl("Author.AUTHOR_ID"),
                SqlEnum.ASC,
                false)));
        assertEquals(c.getOrderByColumns(), cClone.getOrderByColumns());
        Map<String, Object> aliasesClone = cClone.getAliases();
        assertTrue(aliasesClone.containsKey("Writer"));
        assertEquals("Author", aliasesClone.get("Writer"));
        assertEquals(c.getAliases(), cClone.getAliases());
        Map<String, Column> asColumnsClone = cClone.getAsColumns();
        assertTrue(asColumnsClone.containsKey("AUTHOR_NAME"));
        assertEquals(new ColumnImpl("Author", "NAME"),
                asColumnsClone.get("AUTHOR_NAME"));
        assertEquals(c.getAsColumns(), cClone.getAsColumns());

        // Check Joins
        List<Join> joinsClone = cClone.getJoins();
        Join joinClone = joinsClone.get(0);
        assertEquals(new ColumnImpl("Author", "AUTHOR_ID"),
                joinClone.getJoinCondition().getLValue());
        assertEquals(new ColumnImpl("Book", "AUTHOR_ID"),
                joinClone.getJoinCondition().getRValue());
        assertEquals(Criteria.INNER_JOIN, joinClone.getJoinType());
        assertEquals(c.getJoins(), cClone.getJoins());

        // Some Criterion checks
        Criterion cnClone = cClone.getTopLevelCriterion();
        assertEquals("author%", cnClone.getRValue());
        assertEquals(Criteria.LIKE, cnClone.getComparison());
        assertEquals(c.getTopLevelCriterion().isIgnoreCase(), cnClone.isIgnoreCase());

        // Confirm that equals() checks all of the above.
        assertEquals(c, cClone);

        // Check hashCode() too.
        assertEquals(c.hashCode(), cClone.hashCode());
    }

    /**
     * Test that {@link Criteria#equals(Object)} works correctly for a simple
     * Criteria object.
     * @throws TorqueException
     */
    @Test
    public void testEquals() throws TorqueException
    {
        c.addSelectColumn(new ColumnImpl("Author", "NAME"));
        c.addSelectColumn(new ColumnImpl("Author", "AUTHOR_ID"));
        c.where(new ColumnImpl("Author", "NAME"), "foobar");
        Criteria cClone = (Criteria) SerializationUtils.clone(c);
        assertTrue(c.equals(cClone));
    }

    /**
     * Checks whether orderBy works.
     */
    @Test
    public void testOrderBy() throws TorqueException
    {
        // we need a rudimentary databaseMap for this test case to work
        DatabaseMap dbMap = Torque.getDatabaseMap(Torque.getDefaultDB());

        TableMap tableMap = dbMap.addTable("AUTHOR");

        ColumnMap columnMap = new ColumnMap("NAME", tableMap);
        columnMap.setType("");
        tableMap.addColumn(columnMap);

        columnMap = new ColumnMap("AUTHOR_ID", tableMap);
        columnMap.setType(Integer.valueOf(0));
        tableMap.addColumn(columnMap);

        // check that alias'ed tables are referenced by their alias
        // name when added to the select clause.
        c.addSelectColumn(new ColumnImpl("AUTHOR", "NAME"));
        c.addAlias("a", "AUTHOR");
        c.addJoin(new ColumnImpl("AUTHOR", "AUTHOR_ID"),
                new ColumnImpl("a", "AUTHOR_ID"));
        c.addAscendingOrderByColumn(
                new ColumnImpl("a", "NAME"));

        Query result = SqlBuilder.buildQuery(c);
        assertEquals("SELECT AUTHOR.NAME, a.NAME "
                + "FROM AUTHOR, AUTHOR a "
                + "WHERE AUTHOR.AUTHOR_ID=a.AUTHOR_ID "
                + "ORDER BY a.NAME ASC",
                result.toString());
        List<Object> preparedStatementReplacements
        = result.getPreparedStatementReplacements();
        assertEquals(0, preparedStatementReplacements.size());
    }
}
