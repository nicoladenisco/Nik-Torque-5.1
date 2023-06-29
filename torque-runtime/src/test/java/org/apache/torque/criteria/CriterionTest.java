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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.torque.BaseTestCase;
import org.apache.torque.ColumnImpl;
import org.junit.jupiter.api.Test;

/**
 * Test for the Criterion class.
 *
 * @version $Id: CriterionTest.java 1855056 2019-03-08 17:26:27Z tv $
 */
public class CriterionTest extends BaseTestCase
{

	@Test
    public void testHashCodeAndEquals()
    {
        Criterion criterion = new Criterion(
                new ColumnImpl("myTable", "myColumn"),
                "myValue",
                Criteria.GREATER_EQUAL);
        Criterion compareToCriterion = new Criterion(
                new ColumnImpl("myTable", "myColumn"),
                "myValue",
                Criteria.GREATER_EQUAL);
        assertEquals(criterion.hashCode(), compareToCriterion.hashCode());
        assertEquals(criterion, compareToCriterion);
    }

	@Test
    public void testToStringSingleCriterion()
    {
        Criterion criterion = new Criterion(
                new ColumnImpl("myTable", "myColumn"),
                "myValue");
        assertEquals("myTable.myColumn=myValue",
                criterion.toString());
    }

	@Test
    public void testToStringCompositeCriterion()
    {
        Criterion criterion = new Criterion(
                new ColumnImpl("myTable", "myColumn"),
                "myValue");
        Criterion innerCriterion = (new Criterion(
                new ColumnImpl("myTable", "myColumn2"),
                "myValue2",
                SqlEnum.LESS_THAN));
        innerCriterion.and(new Criterion(
                new ColumnImpl("myTable", "myColumn3"),
                3,
                SqlEnum.GREATER_EQUAL));
        criterion.or(innerCriterion);
        assertEquals("myTable.myColumn=myValue "
                + "OR (myTable.myColumn2<myValue2 "
                + "AND myTable.myColumn3>=3)",
                criterion.toString());
    }

    /**
     * Tests that a criterion is copied when being anded to a top level
     * criterion.
     * checks TORQUE-243.
     */
	@Test
    public void testCriterionAndedToTopLevelCriterionGetsCopied()
    {
        Criterion criterion = new Criterion(stringColumnMap, "abc");
        Criterion andedCriterion = new Criterion(stringColumnMap, "def");
        criterion.and(andedCriterion);
        assertNotSame(criterion.getParts().get(0), andedCriterion);
        assertNotSame(criterion.getParts().get(1), andedCriterion);
    }

    /**
     * Tests that a criterion is copied when being ored to a top level
     * criterion.
     * checks TORQUE-243.
     */
    @Test
    public void testCriterionOredToTopLevelCriterionGetsCopied()
    {
        Criterion criterion = new Criterion(stringColumnMap, "abc");
        Criterion oredCriterion = new Criterion(stringColumnMap, "def");
        criterion.or(oredCriterion);
        assertNotSame(criterion.getParts().get(0), oredCriterion);
        assertNotSame(criterion.getParts().get(1), oredCriterion);
    }

    /**
     * Tests that a criterion is copied when being anded to a top level
     * criterion.
     * checks TORQUE-243.
     */
    @Test
    public void testCriterionAndedToCriterionGetsCopied()
    {
        Criterion criterion = new Criterion(stringColumnMap, "abc");
        criterion.and(new Criterion(stringColumnMap, "xyz"));
        Criterion andedCriterion = new Criterion(stringColumnMap, "def");
        criterion.and(andedCriterion);
        assertNotSame(criterion.getParts().get(0), andedCriterion);
        assertNotSame(criterion.getParts().get(1), andedCriterion);
        assertNotSame(criterion.getParts().get(2), andedCriterion);
    }

    /**
     * Tests that a criterion is copied when being ored to a top level
     * criterion.
     * checks TORQUE-243.
     */
    @Test
    public void testCriterionOredToCriterionGetsCopied()
    {
        Criterion criterion = new Criterion(stringColumnMap, "abc");
        criterion.or(new Criterion(stringColumnMap, "xyz"));
        Criterion oredCriterion = new Criterion(stringColumnMap, "def");
        criterion.or(oredCriterion);
        assertNotSame(criterion.getParts().get(0), oredCriterion);
        assertNotSame(criterion.getParts().get(1), oredCriterion);
        assertNotSame(criterion.getParts().get(2), oredCriterion);
    }

    /**
     * Tests that a criterion is copied when being added as top level criterion
     * using where (also tests and).
     * checks TORQUE-243.
     */
    @Test
    public void testCopyConstructorSimpleCriterion()
    {
        Criterion criterion = new Criterion(stringColumnMap, "abc");
        Criterion copied = new Criterion(criterion);
        assertEquals(criterion.getLValue(), copied.getLValue());
        assertEquals(criterion.getComparison(), copied.getComparison());
        assertEquals(criterion.getRValue(), copied.getRValue());
        assertNull(copied.getSql());
        assertNull(copied.getPreparedStatementReplacements());
        assertFalse(copied.isIgnoreCase());
        assertNull(copied.getParts());
        assertNull(copied.getConjunction());
    }

    /**
     * Tests that a criterion is copied when being added as top level criterion
     * using where (also tests and).
     * checks TORQUE-243.
     */
    @Test
    public void testCopyConstructorCompositeCriterion()
    {
        Criterion criterion = new Criterion(stringColumnMap, "abc");
        criterion.or(new Criterion(stringColumnMap, "def"));
        Criterion copied = new Criterion(criterion);
        assertNull(criterion.getLValue());
        assertNull(criterion.getComparison());
        assertNull(criterion.getRValue());
        assertNull(copied.getSql());
        assertNull(copied.getPreparedStatementReplacements());
        assertFalse(copied.isIgnoreCase());
        assertEquals(criterion.getParts(), copied.getParts());
        assertNotSame(criterion.getParts().get(0), copied.getParts().get(0));
        assertNotSame(criterion.getParts().get(1), copied.getParts().get(1));
        assertEquals(criterion.getConjunction(), copied.getConjunction());
    }

    /**
     * Tests that a criterion is copied when being added as top level criterion
     * using where (also tests and).
     * checks TORQUE-243.
     */
    @Test
    public void testCopyConstructorSqlCriterion()
    {
        Criterion criterion = new Criterion(null, null, null, "select 1", new Object[] {1});
        Criterion copied = new Criterion(criterion);
        assertNull(criterion.getLValue());
        assertNull(criterion.getComparison());
        assertNull(criterion.getRValue());
        assertEquals(criterion.getSql(), copied.getSql());
        assertArrayEquals(
                criterion.getPreparedStatementReplacements(),
                copied.getPreparedStatementReplacements());
        assertFalse(copied.isIgnoreCase());
        assertNull(copied.getParts());
        assertNull(copied.getConjunction());
    }
}
