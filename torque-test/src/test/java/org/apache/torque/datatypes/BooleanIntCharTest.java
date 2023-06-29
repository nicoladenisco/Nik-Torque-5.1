package org.apache.torque.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import java.sql.Types;
import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.ColumnImpl;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.criteria.Criterion;
import org.apache.torque.om.StringKey;
import org.apache.torque.test.dbobject.BintBcharType;
import org.apache.torque.test.peer.BintBcharTypePeer;
import org.apache.torque.util.ColumnValues;
import org.apache.torque.util.JdbcTypedValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Tests the data types BOOLEANINT and BOOLEANCHAR.
 * @version $Id: BooleanIntCharTest.java 1879896 2020-07-15 15:03:46Z gk $
 */
public class BooleanIntCharTest extends BaseDatabaseTestCase
{
    @BeforeEach
    public void setUp() throws Exception
    {
        
        fillTables();
    }

    /**
     * Checks whether we can read boolean true values.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testReadBooleanIntCharTrueValue() throws Exception
    {
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("t1"));
        assertEquals(true, bc.getBintValue());
        assertEquals(true, bc.getBcharValue());
        assertEquals(Boolean.TRUE, bc.getBintObjectValue());
        assertEquals(Boolean.TRUE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can read boolean false values.
     *
     * @throws Exception if the test fails
     */
    public void testReadBooleanIntCharFalseValue() throws Exception
    {
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("f1"));
        assertEquals(false, bc.getBintValue());
        assertEquals(false, bc.getBcharValue());
        assertEquals(Boolean.FALSE, bc.getBintObjectValue());
        assertEquals(Boolean.FALSE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can read Boolean null values.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testReadBooleanIntCharNullValue() throws Exception
    {
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("null"));
        assertEquals(null, bc.getBintObjectValue());
        assertEquals(null, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can insert boolean true values.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testInsertBooleanIntCharTrueValue() throws Exception
    {
        // prepare
        BintBcharType bc = new BintBcharType();
        bc.setPrimaryKey("new");
        bc.setBintValue(true);
        bc.setBcharValue(true);
        bc.setBintObjectValue(Boolean.TRUE);
        bc.setBcharObjectValue(Boolean.TRUE);

        // execute
        bc.save();

        // verify
        bc = BintBcharTypePeer.retrieveByPK(new StringKey("new"));
        assertEquals(true, bc.getBintValue());
        assertEquals(true, bc.getBcharValue());
        assertEquals(Boolean.TRUE, bc.getBintObjectValue());
        assertEquals(Boolean.TRUE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can insert boolean false values.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testInsertBooleanIntCharFalseValue() throws Exception
    {
        // prepare
        BintBcharType bc = new BintBcharType();
        bc.setPrimaryKey("new");
        bc.setBintValue(false);
        bc.setBcharValue(false);
        bc.setBintObjectValue(Boolean.FALSE);
        bc.setBcharObjectValue(Boolean.FALSE);

        // execute
        bc.save();

        // verify
        bc = BintBcharTypePeer.retrieveByPK(new StringKey("new"));
        assertEquals(false, bc.getBintValue());
        assertEquals(false, bc.getBcharValue());
        assertEquals(Boolean.FALSE, bc.getBintObjectValue());
        assertEquals(Boolean.FALSE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can insert Boolean null values.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testInserteBooleanIntCharNullValue() throws Exception
    {
        // prepare
        BintBcharType bc = new BintBcharType();
        bc.setPrimaryKey("new");
        bc.setBintObjectValue(null);
        bc.setBcharObjectValue(null);

        // execute
        bc.save();

        // verify
        bc = BintBcharTypePeer.retrieveByPK(new StringKey("new"));
        assertEquals(null, bc.getBintObjectValue());
        assertEquals(null, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can update values to boolean true.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testUpdateBooleanIntCharTrueValue() throws Exception
    {
        // prepare
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("f1"));
        bc.setBintValue(true);
        bc.setBcharValue(true);
        bc.setBintObjectValue(Boolean.TRUE);
        bc.setBcharObjectValue(Boolean.TRUE);

        // execute
        bc.save();

        // verify
        bc = BintBcharTypePeer.retrieveByPK(new StringKey("f1"));
        assertEquals(true, bc.getBintValue());
        assertEquals(true, bc.getBcharValue());
        assertEquals(Boolean.TRUE, bc.getBintObjectValue());
        assertEquals(Boolean.TRUE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can update values to boolean false.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testWriteBooleanIntCharFalseValue() throws Exception
    {
        // prepare
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("t1"));
        bc.setBintValue(false);
        bc.setBcharValue(false);
        bc.setBintObjectValue(Boolean.FALSE);
        bc.setBcharObjectValue(Boolean.FALSE);

        // execute
        bc.save();

        // verify
        bc = BintBcharTypePeer.retrieveByPK(new StringKey("t1"));
        assertEquals(false, bc.getBintValue());
        assertEquals(false, bc.getBcharValue());
        assertEquals(Boolean.FALSE, bc.getBintObjectValue());
        assertEquals(Boolean.FALSE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can update values to Boolean null.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testUpdateBooleanIntCharNullValue() throws Exception
    {
        // prepare
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("t1"));
        bc.setBintObjectValue(null);
        bc.setBcharObjectValue(null);

        // execute
        bc.save();

        // verify
        bc = BintBcharTypePeer.retrieveByPK(new StringKey("t1"));
        assertEquals(null, bc.getBintObjectValue());
        assertEquals(null, bc.getBcharObjectValue());
    }

    /**
     * Check whether we can impose the condition Boolean True to
     * booleanint/booleanchar columns.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testBooleanTrueSelect() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(BintBcharTypePeer.BCHAR_VALUE, new Boolean(true))
                .and(BintBcharTypePeer.BINT_VALUE, new Boolean(true))
                .and(BintBcharTypePeer.BCHAR_OBJECT_VALUE, new Boolean(true))
                .and(BintBcharTypePeer.BINT_OBJECT_VALUE, new Boolean(true));
        List<BintBcharType> selectedList
        = BintBcharTypePeer.doSelect(criteria);
        assertEquals(1, selectedList.size());
        BintBcharType bintBcharType = selectedList.get(0);
        assertEquals("t1", bintBcharType.getId());
    }

    /**
     * Check whether we can impose the condition Boolean False to
     * booleanint/booleanchar columns.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testBooleanFalseSelect() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(BintBcharTypePeer.BCHAR_VALUE, new Boolean(false))
                .and(BintBcharTypePeer.BINT_VALUE, new Boolean(false))
                .and(BintBcharTypePeer.BCHAR_OBJECT_VALUE, new Boolean(false))
                .and(BintBcharTypePeer.BINT_OBJECT_VALUE, new Boolean(false));
        List<BintBcharType> selectedList
        = BintBcharTypePeer.doSelect(criteria);
        assertEquals(1, selectedList.size());
        BintBcharType bintBcharType = selectedList.get(0);
        assertEquals("f1", bintBcharType.getId());
    }

    /**
     * Check whether we can impose the condition Boolean Null to
     * booleanint/booleanchar columns and get objects where the columns
     * are null.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testBooleanObjectNullSelect() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(BintBcharTypePeer.BCHAR_OBJECT_VALUE, null)
                .and(BintBcharTypePeer.BINT_OBJECT_VALUE, null);
        List<BintBcharType> selectedList
        = BintBcharTypePeer.doSelect(criteria);
        assertEquals(1, selectedList.size());
        BintBcharType bintBcharType = selectedList.get(0);
        assertEquals("null", bintBcharType.getId());
    }

    /**
     * Check whether we can impose the condition Boolean Null to
     * booleanint/booleanchar primitive columns and get no hit.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testPrimitiveNullSelect() throws Exception
    {
        Criteria criteria = new Criteria()
                .and(BintBcharTypePeer.BCHAR_VALUE, null)
                .and(BintBcharTypePeer.BINT_VALUE, null);
        List<BintBcharType> selectedList
        = BintBcharTypePeer.doSelect(criteria);
        assertTrue("Should have read 0 dataset with both values false "
                + "but read " + selectedList.size(),
                selectedList.size() == 0);
    }

    /**
     * Check whether we can impose a Boolean condition to booleanint/booleanchar
     * columns via joins.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testBooleanSelectViaJoins() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAlias("bc", BintBcharTypePeer.TABLE_NAME);
        criteria.addJoin(
                BintBcharTypePeer.ID,
                new ColumnImpl("bc.id"));
        criteria.where(new ColumnImpl("bc.BINT_VALUE"), new Boolean(false))
        .and(new ColumnImpl("bc.BCHAR_VALUE"), new Boolean(false))
        .and(new ColumnImpl("bc.BINT_OBJECT_VALUE"), new Boolean(false))
        .and(new ColumnImpl("bc.BCHAR_OBJECT_VALUE"), new Boolean(false));
        List<BintBcharType> selectedList
        = BintBcharTypePeer.doSelect(criteria);
        assertTrue("Should have read 1 dataset with both values false "
                + "but read " + selectedList.size(),
                selectedList.size() == 1);
        BintBcharType bintBcharType = selectedList.get(0);
        assertEquals("f1", bintBcharType.getId());
    }

    /**
     * Check whether we can impose a Boolean condition in
     * chained criterions and get no hits using a unfullfillable condition.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testBooleanSelectInChainedCriterionsNoHits() throws Exception
    {
        // check whether complex criteria are overwritten by
        // replaceBooleans
        Criteria criteria = new Criteria();
        Criterion criterion1 = new Criterion(
                BintBcharTypePeer.BCHAR_VALUE,
                Boolean.FALSE,
                Criteria.EQUAL);
        Criterion criterion2 = new Criterion(
                BintBcharTypePeer.BCHAR_VALUE,
                null,
                Criteria.ISNULL);
        criteria.where(criterion1.and(criterion2));
        List<BintBcharType> selectedList
        = BintBcharTypePeer.doSelect(criteria);
        // List should be empty, because and creates unfulfillable condition
        // If BasePeer.correctBooleans() replaces Criterion wrongly,
        // then we get entries in the list.
        assertTrue("List should be empty but contains "
                + selectedList.size() + " datasets",
                selectedList.isEmpty());
    }

    /**
     * Check whether we can impose a Boolean condition in
     * chained criterions and get a hits using OR.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testBooleanSelectInChainedCriterionsUsingOr() throws Exception
    {
        Criteria criteria = new Criteria();
        Criterion criterion1 = new Criterion(
                BintBcharTypePeer.BCHAR_VALUE,
                null,
                Criteria.ISNULL);
        Criterion criterion2 = new Criterion(
                BintBcharTypePeer.BCHAR_VALUE,
                Boolean.FALSE,
                Criteria.EQUAL);
        criteria.where(criterion1.or(criterion2));
        List<BintBcharType> selectedList
        = BintBcharTypePeer.doSelect(criteria);
        assertTrue("Should have read 1 dataset complex Criteria "
                + "but read " + selectedList.size(),
                selectedList.size() == 1);
        BintBcharType selected = selectedList.get(0);
        // use trim() for testkey because some databases will return the
        // testkey filled up with blanks, as it is defined as char(10)
        assertTrue("Primary key of data set should be f1 but is "
                + selected.getId(),
                "f1".equals(selected.getId()));
    }

    /**
     * Check whether CorrectBooleans works also on unqualified columns.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testCorrectBooleansInUnqualifiedColumns() throws Exception
    {
        // check whether booleans are replaced with unqualified columns
        Criteria criteria = new Criteria()
                .where(new ColumnImpl("BINT_VALUE"), true)
                .and(new ColumnImpl("BCHAR_VALUE"), true);

        BintBcharTypePeer.correctBooleans(criteria);

        Criterion criterionInt
        = criteria.getTopLevelCriterion().getParts().get(0);
        Object intValue = criterionInt.getRValue();

        assertTrue("The boolean value should be an instance of Integer",
                intValue instanceof Integer);

        Criterion criterionChar
        = criteria.getTopLevelCriterion().getParts().get(1);
        Object charValue = criterionChar.getRValue();

        assertTrue("The boolean value should be an instance of String",
                charValue instanceof String);
    }

    /**
     * Check whether CorrectBooleans leaves unknown columns alone.
     *
     * @throws Exception if the test fails
     */
    public void testCorrectBooleansUnknownColumns() throws Exception
    {
        Criteria criteria = new Criteria()
                .where("BooleanCheck.bint_value", true)
                .and("BooleanCheck.bchar_value", true);

        BintBcharTypePeer.correctBooleans(criteria);

        Criterion criterionBool1
        = criteria.getTopLevelCriterion().getParts().get(0);
        Object boolValue1 = criterionBool1.getRValue();

        assertTrue("The boolean value should be an instance of Boolean",
                boolValue1 instanceof Boolean);

        Criterion criterionBool2
        = criteria.getTopLevelCriterion().getParts().get(1);
        Object boolValue2 = criterionBool2.getRValue();

        assertTrue("The boolean value should be an instance of Boolean",
                boolValue2 instanceof Boolean);
    }

    /**
     * Checks whether we can pass boolean true values to doInsert.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoInsertBooleanTrueValue() throws Exception
    {
        // prepare
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                BintBcharTypePeer.ID,
                new JdbcTypedValue("new", Types.VARCHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_VALUE,
                new JdbcTypedValue(Boolean.TRUE, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BCHAR_VALUE,
                new JdbcTypedValue(Boolean.TRUE, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BINT_OBJECT_VALUE,
                new JdbcTypedValue(Boolean.TRUE, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BCHAR_OBJECT_VALUE,
                new JdbcTypedValue(Boolean.TRUE, Types.BIT));

        // execute
        BintBcharTypePeer.doInsert(columnValues);

        // verify
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("new"));
        assertEquals(true, bc.getBintValue());
        assertEquals(true, bc.getBcharValue());
        assertEquals(Boolean.TRUE, bc.getBintObjectValue());
        assertEquals(Boolean.TRUE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can pass native true values to doInsert.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoInsertNativeTrueValue() throws Exception
    {
        // prepare
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                BintBcharTypePeer.ID,
                new JdbcTypedValue("new", Types.VARCHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_VALUE,
                new JdbcTypedValue(1, Types.INTEGER));
        columnValues.put(
                BintBcharTypePeer.BCHAR_VALUE,
                new JdbcTypedValue("Y", Types.CHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_OBJECT_VALUE,
                new JdbcTypedValue(1, Types.INTEGER));
        columnValues.put(
                BintBcharTypePeer.BCHAR_OBJECT_VALUE,
                new JdbcTypedValue("Y", Types.CHAR));

        // execute
        BintBcharTypePeer.doInsert(columnValues);

        // verify
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("new"));
        assertEquals(true, bc.getBintValue());
        assertEquals(true, bc.getBcharValue());
        assertEquals(Boolean.TRUE, bc.getBintObjectValue());
        assertEquals(Boolean.TRUE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can pass boolean false values to doInsert.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoInsertBooleanFalseValue() throws Exception
    {
        // prepare
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                BintBcharTypePeer.ID,
                new JdbcTypedValue("new", Types.VARCHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_VALUE,
                new JdbcTypedValue(Boolean.FALSE, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BCHAR_VALUE,
                new JdbcTypedValue(Boolean.FALSE, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BINT_OBJECT_VALUE,
                new JdbcTypedValue(Boolean.FALSE, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BCHAR_OBJECT_VALUE,
                new JdbcTypedValue(Boolean.FALSE, Types.BIT));

        // execute
        BintBcharTypePeer.doInsert(columnValues);

        // verify
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("new"));
        assertEquals(false, bc.getBintValue());
        assertEquals(false, bc.getBcharValue());
        assertEquals(Boolean.FALSE, bc.getBintObjectValue());
        assertEquals(Boolean.FALSE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can pass native false values to doInsert.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoInsertNativeFalseValue() throws Exception
    {
        // prepare
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                BintBcharTypePeer.ID,
                new JdbcTypedValue("new", Types.VARCHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_VALUE,
                new JdbcTypedValue(0, Types.INTEGER));
        columnValues.put(
                BintBcharTypePeer.BCHAR_VALUE,
                new JdbcTypedValue("N", Types.CHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_OBJECT_VALUE,
                new JdbcTypedValue(0, Types.INTEGER));
        columnValues.put(
                BintBcharTypePeer.BCHAR_OBJECT_VALUE,
                new JdbcTypedValue("N", Types.CHAR));

        // execute
        BintBcharTypePeer.doInsert(columnValues);

        // verify
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("new"));
        assertEquals(false, bc.getBintValue());
        assertEquals(false, bc.getBcharValue());
        assertEquals(Boolean.FALSE, bc.getBintObjectValue());
        assertEquals(Boolean.FALSE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can pass Boolean null values to doInsert.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoInsertBooleanNullValue() throws Exception
    {
        // prepare
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                BintBcharTypePeer.ID,
                new JdbcTypedValue("new", Types.VARCHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_VALUE,
                new JdbcTypedValue(true, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BCHAR_VALUE,
                new JdbcTypedValue(false, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BINT_OBJECT_VALUE,
                new JdbcTypedValue(null, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BCHAR_OBJECT_VALUE,
                new JdbcTypedValue(null, Types.BIT));

        // execute
        BintBcharTypePeer.doInsert(columnValues);

        // verify
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("new"));
        assertEquals(null, bc.getBintObjectValue());
        assertEquals(null, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can pass native null values to doInsert.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoInsertNativeNullValue() throws Exception
    {
        // prepare
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                BintBcharTypePeer.ID,
                new JdbcTypedValue("new", Types.VARCHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_VALUE,
                new JdbcTypedValue(0, Types.INTEGER));
        columnValues.put(
                BintBcharTypePeer.BCHAR_VALUE,
                new JdbcTypedValue("N", Types.CHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_OBJECT_VALUE,
                new JdbcTypedValue(null, Types.INTEGER));
        columnValues.put(
                BintBcharTypePeer.BCHAR_OBJECT_VALUE,
                new JdbcTypedValue(null, Types.CHAR));

        // execute
        BintBcharTypePeer.doInsert(columnValues);

        // verify
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("new"));
        assertEquals(null, bc.getBintObjectValue());
        assertEquals(null, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can pass boolean true values to doUpdate.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoUpdateBooleanTrueValue() throws Exception
    {
        // prepare
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                BintBcharTypePeer.ID,
                new JdbcTypedValue("f1", Types.VARCHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_VALUE,
                new JdbcTypedValue(Boolean.TRUE, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BCHAR_VALUE,
                new JdbcTypedValue(Boolean.TRUE, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BINT_OBJECT_VALUE,
                new JdbcTypedValue(Boolean.TRUE, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BCHAR_OBJECT_VALUE,
                new JdbcTypedValue(Boolean.TRUE, Types.BIT));

        // execute
        BintBcharTypePeer.doUpdate(columnValues);

        // verify
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("f1"));
        assertEquals(true, bc.getBintValue());
        assertEquals(true, bc.getBcharValue());
        assertEquals(Boolean.TRUE, bc.getBintObjectValue());
        assertEquals(Boolean.TRUE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can pass native true values to doUpdate.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoUpdateNativeTrueValue() throws Exception
    {
        // prepare
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                BintBcharTypePeer.ID,
                new JdbcTypedValue("f1", Types.VARCHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_VALUE,
                new JdbcTypedValue(1, Types.INTEGER));
        columnValues.put(
                BintBcharTypePeer.BCHAR_VALUE,
                new JdbcTypedValue("Y", Types.CHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_OBJECT_VALUE,
                new JdbcTypedValue(1, Types.INTEGER));
        columnValues.put(
                BintBcharTypePeer.BCHAR_OBJECT_VALUE,
                new JdbcTypedValue("Y", Types.CHAR));

        // execute
        BintBcharTypePeer.doUpdate(columnValues);

        // verify
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("f1"));
        assertEquals(true, bc.getBintValue());
        assertEquals(true, bc.getBcharValue());
        assertEquals(Boolean.TRUE, bc.getBintObjectValue());
        assertEquals(Boolean.TRUE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can pass boolean false values to doUpdate.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoUpdateBooleanFalseValue() throws Exception
    {
        // prepare
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                BintBcharTypePeer.ID,
                new JdbcTypedValue("t1", Types.VARCHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_VALUE,
                new JdbcTypedValue(Boolean.FALSE, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BCHAR_VALUE,
                new JdbcTypedValue(Boolean.FALSE, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BINT_OBJECT_VALUE,
                new JdbcTypedValue(Boolean.FALSE, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BCHAR_OBJECT_VALUE,
                new JdbcTypedValue(Boolean.FALSE, Types.BIT));

        // execute
        BintBcharTypePeer.doUpdate(columnValues);

        // verify
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("t1"));
        assertEquals(false, bc.getBintValue());
        assertEquals(false, bc.getBcharValue());
        assertEquals(Boolean.FALSE, bc.getBintObjectValue());
        assertEquals(Boolean.FALSE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can pass native false values to doUpdate.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoUpdateNativeFalseValue() throws Exception
    {
        // prepare
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                BintBcharTypePeer.ID,
                new JdbcTypedValue("t1", Types.VARCHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_VALUE,
                new JdbcTypedValue(0, Types.INTEGER));
        columnValues.put(
                BintBcharTypePeer.BCHAR_VALUE,
                new JdbcTypedValue("N", Types.CHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_OBJECT_VALUE,
                new JdbcTypedValue(0, Types.INTEGER));
        columnValues.put(
                BintBcharTypePeer.BCHAR_OBJECT_VALUE,
                new JdbcTypedValue("N", Types.CHAR));

        // execute
        BintBcharTypePeer.doUpdate(columnValues);

        // verify
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("t1"));
        assertEquals(false, bc.getBintValue());
        assertEquals(false, bc.getBcharValue());
        assertEquals(Boolean.FALSE, bc.getBintObjectValue());
        assertEquals(Boolean.FALSE, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can pass Boolean null values to doUpdate.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoUpdateBooleanNullValue() throws Exception
    {
        // prepare
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                BintBcharTypePeer.ID,
                new JdbcTypedValue("t1", Types.VARCHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_OBJECT_VALUE,
                new JdbcTypedValue(null, Types.BIT));
        columnValues.put(
                BintBcharTypePeer.BCHAR_OBJECT_VALUE,
                new JdbcTypedValue(null, Types.BIT));

        // execute
        BintBcharTypePeer.doUpdate(columnValues);

        // verify
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("t1"));
        assertEquals(null, bc.getBintObjectValue());
        assertEquals(null, bc.getBcharObjectValue());
    }

    /**
     * Checks whether we can pass native null values to doUpdate.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoUpdateNativeNullValue() throws Exception
    {
        // prepare
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                BintBcharTypePeer.ID,
                new JdbcTypedValue("t1", Types.VARCHAR));
        columnValues.put(
                BintBcharTypePeer.BINT_OBJECT_VALUE,
                new JdbcTypedValue(null, Types.NUMERIC));
        columnValues.put(
                BintBcharTypePeer.BCHAR_OBJECT_VALUE,
                new JdbcTypedValue(null, Types.CHAR));

        // execute
        BintBcharTypePeer.doUpdate(columnValues);

        // verify
        BintBcharType bc
        = BintBcharTypePeer.retrieveByPK(new StringKey("t1"));
        assertEquals(null, bc.getBintObjectValue());
        assertEquals(null, bc.getBcharObjectValue());
    }

    /**
     * Delete all previous data from the tested tables
     * and re-inserts test data.
     */
    private void fillTables() throws TorqueException
    {
        Criteria criteria = new Criteria();
        BintBcharTypePeer.doDelete(criteria);

        BintBcharType bc = new BintBcharType();
        bc.setId("t1");
        bc.setBintValue(true);
        bc.setBcharValue(true);
        bc.setBintObjectValue(Boolean.TRUE);
        bc.setBcharObjectValue(Boolean.TRUE);
        bc.save();
        bc = new BintBcharType();
        bc.setId("f1");
        bc.setBintValue(false);
        bc.setBcharValue(false);
        bc.setBintObjectValue(Boolean.FALSE);
        bc.setBcharObjectValue(Boolean.FALSE);
        bc.save();
        bc = new BintBcharType();
        bc.setId("null");
        bc.setBintValue(false);
        bc.setBcharValue(true);
        bc.setBintObjectValue(null);
        bc.setBcharObjectValue(null);
        bc.save();
    }
}
