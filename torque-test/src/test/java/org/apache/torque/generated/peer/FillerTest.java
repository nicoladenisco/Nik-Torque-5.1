package org.apache.torque.generated.peer;

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

import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.ForeignKeySchemaData;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.CompIntegerVarcharFk;
import org.apache.torque.test.dbobject.CompIntegerVarcharPk;
import org.apache.torque.test.dbobject.CompNonpkFk;
import org.apache.torque.test.dbobject.NonPkOIntegerFk;
import org.apache.torque.test.dbobject.NullableOIntegerFk;
import org.apache.torque.test.dbobject.OIntegerPk;
import org.apache.torque.test.dbobject.PIntegerPk;
import org.apache.torque.test.dbobject.RequiredPIntegerFk;
import org.apache.torque.test.peer.CompIntegerVarcharFkPeer;
import org.apache.torque.test.peer.CompIntegerVarcharPkPeer;
import org.apache.torque.test.peer.CompNonpkFkPeer;
import org.apache.torque.test.peer.NonPkOIntegerFkPeer;
import org.apache.torque.test.peer.NullableOIntegerFkPeer;
import org.apache.torque.test.peer.OIntegerPkPeer;
import org.apache.torque.test.peer.PIntegerPkPeer;
import org.apache.torque.test.peer.RequiredPIntegerFkPeer;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;

/**
 * Tests the filler methods in the generated Peer classes.
 * The functionality test are tested for all combinations of
 * simple/composite foreign key and foreign key references primary key of
 * foreign table or not. One test also exists for each of primitive
 * and object columns.
 *
 * @version $Id: FillerTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class FillerTest extends BaseDatabaseTestCase
{
    /** How many records are many records. */
    private final int MANY = 1256;

    /**
     * Tests the functionality for the fillers for referenced Objects.
     * The foreign key is a non-required primitive Integer  column
     * and it points to the primary key of the referenced table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencedPrimitiveIntegerKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(
                RequiredPIntegerFkPeer.ID);
        List<RequiredPIntegerFk> fkList
        = RequiredPIntegerFkPeer.doSelect(criteria);
        assertEquals(3, fkList.size());
        assertEquals(
                testData.getRequiredPIntegerFkList().get(0).getId(),
                fkList.get(0).getId());
        // TODO check that NullableIntFkList entries have not yet
        //      loaded their referenced intPk object

        List<PIntegerPk> referencedPkList
        = RequiredPIntegerFkPeer.fillPIntegerPks(
                fkList);
        // TODO check that NullableIntFkList entries have now
        //      loaded their referenced intPk object

        // returned list must contain intPk2, intPk3, intPk3
        // and the last 2 objects must not be the same objects
        assertEquals(3, referencedPkList.size());
        assertEquals(
                testData.getPIntegerPkList().get(1).getId(),
                referencedPkList.get(0).getId());
        assertEquals(
                testData.getPIntegerPkList().get(2).getId(),
                referencedPkList.get(1).getId());
        assertEquals(
                testData.getPIntegerPkList().get(2).getId(),
                referencedPkList.get(2).getId());
        assertNotSame(
                referencedPkList.get(1),
                referencedPkList.get(2));

        // the fk list must reference intPk2, intPk3, intPk3
        // and the last 2 referenced objects must not be the same objects
        assertEquals(
                testData.getPIntegerPkList().get(1),
                fkList.get(0).getPIntegerPk());
        assertEquals(
                testData.getPIntegerPkList().get(2),
                fkList.get(1).getPIntegerPk());
        assertEquals(
                testData.getPIntegerPkList().get(2),
                fkList.get(2).getPIntegerPk());
        assertNotSame(
                fkList.get(1).getPIntegerPk(),
                fkList.get(2).getPIntegerPk());

        // The objects in the result list must be the same objects
        // as the referenced objects in the fk list
        assertSame(
                referencedPkList.get(0),
                fkList.get(0).getPIntegerPk());
        assertSame(
                referencedPkList.get(1),
                fkList.get(1).getPIntegerPk());
        assertSame(
                referencedPkList.get(2),
                fkList.get(2).getPIntegerPk());

        // check flags
        for (PIntegerPk pIntegerPk : referencedPkList)
        {
            assertFalse(pIntegerPk.isNew());
            assertFalse(pIntegerPk.isModified());
            assertFalse(pIntegerPk.isLoading());
            assertFalse(pIntegerPk.isDeleted());
            assertFalse(pIntegerPk.isSaving());
        }
    }

    /**
     * Tests the functionality for the fillers for referenced Objects.
     * The foreign key is a non-required object Integer column,
     * and it points to the primary key of the referenced table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencedObjectIntegerKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(
                NullableOIntegerFkPeer.ID);
        List<NullableOIntegerFk> fkList
        = NullableOIntegerFkPeer.doSelect(criteria);
        assertEquals(4, fkList.size());
        assertEquals(
                testData.getNullableOIntegerFkList().get(0).getId(),
                fkList.get(0).getId());
        // TODO check that fkList entries have not yet
        //      loaded its referenced intPk object

        List<OIntegerPk> referencedPkList
        = NullableOIntegerFkPeer.fillOIntegerPks(
                fkList);

        // returned list must contain intPk2, intPk3, intPk3
        // and the last 2 objects must not be the same objects
        assertEquals(3, referencedPkList.size());
        assertEquals(
                testData.getOIntegerPkList().get(1).getId(),
                referencedPkList.get(0).getId());
        assertEquals(
                testData.getOIntegerPkList().get(2).getId(),
                referencedPkList.get(1).getId());
        assertEquals(
                testData.getOIntegerPkList().get(2).getId(),
                referencedPkList.get(2).getId());
        assertNotSame(
                referencedPkList.get(1),
                referencedPkList.get(2));

        // the fk list must reference intPk2, intPk3, intPk3
        // and the last 2 referenced objects must not be the same objects
        assertEquals(
                testData.getOIntegerPkList().get(1),
                fkList.get(0).getOIntegerPk());
        assertEquals(
                testData.getOIntegerPkList().get(2),
                fkList.get(1).getOIntegerPk());
        assertEquals(
                testData.getOIntegerPkList().get(2),
                fkList.get(2).getOIntegerPk());
        assertNotSame(
                fkList.get(1).getOIntegerPk(),
                fkList.get(2).getOIntegerPk());

        // The objects in the result list must be the same objects
        // as the referenced objects in the fk list
        assertSame(
                referencedPkList.get(0),
                fkList.get(0).getOIntegerPk());
        assertSame(
                referencedPkList.get(1),
                fkList.get(1).getOIntegerPk());
        assertSame(
                referencedPkList.get(2),
                fkList.get(2).getOIntegerPk());
    }

    /**
     * Tests the functionality for the fillers for referenced Objects.
     * The foreign key is a non-required object Integer column,
     * and it does not point to the primary key of the referenced table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencedNonPrimaryKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(
                NonPkOIntegerFkPeer.ID);
        List<NonPkOIntegerFk> fkList
        = NonPkOIntegerFkPeer.doSelect(criteria);
        assertEquals(4, fkList.size());
        assertEquals(
                testData.getNonPkOIntegerFkList().get(0).getId(),
                fkList.get(0).getId());
        // TODO check that fkList entries have not yet
        //      loaded its referenced intPk object

        List<OIntegerPk> referencedPkList
        = NonPkOIntegerFkPeer.fillOIntegerPks(
                fkList);

        // returned list must contain intPk1, intPk1, intPk2
        // and the first 2 objects must not be the same objects
        assertEquals(3, referencedPkList.size());
        assertEquals(
                testData.getOIntegerPkList().get(0).getId(),
                referencedPkList.get(0).getId());
        assertEquals(
                testData.getOIntegerPkList().get(0).getId(),
                referencedPkList.get(1).getId());
        assertEquals(
                testData.getOIntegerPkList().get(1).getId(),
                referencedPkList.get(2).getId());
        assertNotSame(
                referencedPkList.get(0),
                referencedPkList.get(1));

        // the fk list must reference intPk1, intPk1, intPk2
        // and the first 2 referenced objects must not be the same objects
        assertEquals(
                testData.getOIntegerPkList().get(0),
                fkList.get(0).getOIntegerPk());
        assertEquals(
                testData.getOIntegerPkList().get(0),
                fkList.get(1).getOIntegerPk());
        assertEquals(
                testData.getOIntegerPkList().get(1),
                fkList.get(2).getOIntegerPk());
        assertNotSame(
                fkList.get(0).getOIntegerPk(),
                fkList.get(1).getOIntegerPk());

        // The objects in the result list must be the same objects
        // as the referenced objects in the fk list
        assertSame(
                referencedPkList.get(0),
                fkList.get(0).getOIntegerPk());
        assertSame(
                referencedPkList.get(1),
                fkList.get(1).getOIntegerPk());
        assertSame(
                referencedPkList.get(2),
                fkList.get(2).getOIntegerPk());
    }

    /**
     * Tests whether duplicate objects in the argument for the fillers
     * for referenced Objects are treated correctly.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencedDuplicateObjects() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria().where(
                NullableOIntegerFkPeer.NAME, "nullableOIntegerFk3a");

        List<NullableOIntegerFk> fkList
        = NullableOIntegerFkPeer.doSelect(criteria);
        assertEquals(1, fkList.size());
        assertEquals(
                testData.getNullableOIntegerFkList().get(1).getId(),
                fkList.get(0).getId());
        NullableOIntegerFk fkEntryCopy = fkList.get(0).copy();
        fkEntryCopy.setPrimaryKey(fkList.get(0).getPrimaryKey());
        fkList.add(fkEntryCopy);

        List<OIntegerPk> referencedPkList
        = NullableOIntegerFkPeer.fillOIntegerPks(
                fkList);

        // returned list must contain intPk3, intPk3
        // and the objects must not be the same objects
        assertEquals(2, referencedPkList.size());
        assertEquals(
                testData.getOIntegerPkList().get(2).getId(),
                referencedPkList.get(0).getId());
        assertEquals(
                testData.getOIntegerPkList().get(2).getId(),
                referencedPkList.get(1).getId());
        assertNotSame(
                referencedPkList.get(0),
                referencedPkList.get(1));

        // the fk list must reference intPk3, intPk3
        // and the last 2 referenced objects must not be the same objects
        assertEquals(
                testData.getOIntegerPkList().get(2),
                fkList.get(0).getOIntegerPk());
        assertEquals(
                testData.getOIntegerPkList().get(2),
                fkList.get(1).getOIntegerPk());
        assertNotSame(
                fkList.get(0).getOIntegerPk(),
                fkList.get(1).getOIntegerPk());

        // The objects in the result list must be the same objects
        // as the referenced objects in the fk list
        assertSame(
                referencedPkList.get(0),
                fkList.get(0).getOIntegerPk());
        assertSame(
                referencedPkList.get(1),
                fkList.get(1).getOIntegerPk());
    }

    /**
     * Tests that a referenced object that is already set is re-filled
     * by a call to the filler method.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencedAlreadySet() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(
                NullableOIntegerFkPeer.ID);
        List<NullableOIntegerFk> fkList
        = NullableOIntegerFkPeer.doSelect(criteria);

        OIntegerPk originalOIntegerPk = fkList.get(0).getOIntegerPk();
        assertNotNull(originalOIntegerPk);

        NullableOIntegerFkPeer.fillOIntegerPks(
                fkList);

        assertNotNull(originalOIntegerPk);
        assertNotSame(originalOIntegerPk, fkList.get(0).getOIntegerPk());
    }

    /**
     * Tests the functionality for the fillers for referenced Objects.
     * The foreign key is a non-required composite key, consisting
     * of an integer and varchar column,
     * and it points to the primary key of the referenced table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencedCompositeKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(
                CompIntegerVarcharFkPeer.ID);
        List<CompIntegerVarcharFk> fkList
        = CompIntegerVarcharFkPeer.doSelect(criteria);
        assertEquals(4, fkList.size());
        assertEquals(
                testData.getCompositeIntegerVarcharFkList().get(0).getId(),
                fkList.get(0).getId());
        // TODO check that fkList entries have not yet
        //      loaded its referenced intPk object

        List<CompIntegerVarcharPk> referencedPkList
        = CompIntegerVarcharFkPeer.fillCompIntegerVarcharPks(
                fkList);

        // returned list must contain compositeIntegerVarcharPk2,
        // compositeIntegerVarcharPk3, compositeIntegerVarcharPk3
        // and the last 2 objects must not be the same objects
        assertEquals(3, referencedPkList.size());
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(1).getPrimaryKey(),
                referencedPkList.get(0).getPrimaryKey());
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(2).getPrimaryKey(),
                referencedPkList.get(1).getPrimaryKey());
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(2).getPrimaryKey(),
                referencedPkList.get(2).getPrimaryKey());
        assertNotSame(
                referencedPkList.get(1),
                referencedPkList.get(2));

        // the fk list must reference compositeIntegerVarcharPk2,
        // compositeIntegerVarcharPk3, compositeIntegerVarcharPk3
        // and the last 2 referenced objects must not be the same objects
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(1),
                fkList.get(0).getCompIntegerVarcharPk());
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(2),
                fkList.get(1).getCompIntegerVarcharPk());
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(2),
                fkList.get(2).getCompIntegerVarcharPk());
        assertNotSame(
                fkList.get(1).getCompIntegerVarcharPk(),
                fkList.get(2).getCompIntegerVarcharPk());

        // The objects in the result list must be the same objects
        // as the referenced objects in the fk list
        assertSame(
                referencedPkList.get(0),
                fkList.get(0).getCompIntegerVarcharPk());
        assertSame(
                referencedPkList.get(1),
                fkList.get(1).getCompIntegerVarcharPk());
        assertSame(
                referencedPkList.get(2),
                fkList.get(2).getCompIntegerVarcharPk());
    }

    /**
     * Tests the functionality for the fillers for referenced Objects.
     * The foreign key is a non-required composite key, consisting
     * of an integer and varchar column,
     * and it does not point to the primary key of the referenced table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencedNonPrimaryCompositeKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(
                CompNonpkFkPeer.ID);
        List<CompNonpkFk> fkList
        = CompNonpkFkPeer.doSelect(criteria);
        assertEquals(3, fkList.size());
        assertEquals(
                testData.getCompositeNonpkFkList().get(0).getId(),
                fkList.get(0).getId());
        // TODO check that fkList entries have not yet
        //      loaded its referenced intPk object

        List<CompIntegerVarcharPk> referencedPkList
        = CompNonpkFkPeer.fillCompIntegerVarcharPks(
                fkList);

        // returned list must contain compositeIntegerVarcharPk1,
        // compositeIntegerVarcharPk2, compositeIntegerVarcharPk2
        // and the last 2 objects must not be the same objects
        assertEquals(3, referencedPkList.size());
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(0).getPrimaryKey(),
                referencedPkList.get(0).getPrimaryKey());
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(0).getPrimaryKey(),
                referencedPkList.get(1).getPrimaryKey());
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(1).getPrimaryKey(),
                referencedPkList.get(2).getPrimaryKey());
        assertNotSame(
                referencedPkList.get(0),
                referencedPkList.get(1));

        // the fk list must reference compositeIntegerVarcharPk1,
        // compositeIntegerVarcharPk1, compositeIntegerVarcharPk2
        // and the first 2 referenced objects must not be the same objects
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(0),
                fkList.get(0).getCompIntegerVarcharPk());
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(0),
                fkList.get(1).getCompIntegerVarcharPk());
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(1),
                fkList.get(2).getCompIntegerVarcharPk());
        assertNotSame(
                fkList.get(0).getCompIntegerVarcharPk(),
                fkList.get(1).getCompIntegerVarcharPk());

        // The objects in the result list must be the same objects
        // as the referenced objects in the fk list
        assertSame(
                referencedPkList.get(0),
                fkList.get(0).getCompIntegerVarcharPk());
        assertSame(
                referencedPkList.get(1),
                fkList.get(1).getCompIntegerVarcharPk());
        assertSame(
                referencedPkList.get(2),
                fkList.get(2).getCompIntegerVarcharPk());
    }

    /**
     * Tests that the fill method overwrites a referenced object with the
     * current database state.
     */
    @Test
    public void testFillReferencedObjectRefill() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.and(NullableOIntegerFkPeer.NAME, "nullableOIntegerFk2");
        List<NullableOIntegerFk> fkList
        = NullableOIntegerFkPeer.doSelect(criteria);
        assertEquals(1, fkList.size());
        NullableOIntegerFk fkObject = fkList.get(0);
        assertEquals(
                testData.getNullableOIntegerFkList().get(0).getId(),
                fkObject.getId());

        OIntegerPk changedPkObject = fkObject.getOIntegerPk();
        changedPkObject.setName("oIntegerPk2modified");

        List<OIntegerPk> referencedPkList
        = NullableOIntegerFkPeer.fillOIntegerPks(
                fkList);

        // returned list must contain unchanged new oIntegerPk2
        assertEquals(1, referencedPkList.size());
        assertEquals("oIntegerPk2", fkObject.getOIntegerPk().getName());
        assertNotSame(fkObject.getOIntegerPk(), changedPkObject);

        // fkObject must reference unchanged new oIntegerPk2
        assertEquals("oIntegerPk2", fkObject.getOIntegerPk().getName());
        assertNotSame(fkObject.getOIntegerPk(), changedPkObject);
    }

    /**
     * Tests that the chunk size -1 works for filling referenced objects.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencedChunkSizeMinus1() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(
                RequiredPIntegerFkPeer.ID);
        List<RequiredPIntegerFk> fkList
        = RequiredPIntegerFkPeer.doSelect(criteria);
        assertEquals(3, fkList.size());
        assertEquals(
                testData.getRequiredPIntegerFkList().get(0).getId(),
                fkList.get(0).getId());
        // TODO check that NullableIntFkList entries have not yet
        //      loaded their referenced intPk object

        List<PIntegerPk> referencedPkList
        = RequiredPIntegerFkPeer.fillPIntegerPks(
                fkList);
        // TODO check that NullableIntFkList entries have now
        //      loaded their referenced intPk object

        // returned list must contain intPk2, intPk3, intPk3
        // and the last 2 objects must not be the same objects
        assertEquals(3, referencedPkList.size());
        assertEquals(
                testData.getPIntegerPkList().get(1).getId(),
                referencedPkList.get(0).getId());
        assertEquals(
                testData.getPIntegerPkList().get(2).getId(),
                referencedPkList.get(1).getId());
        assertEquals(
                testData.getPIntegerPkList().get(2).getId(),
                referencedPkList.get(2).getId());
        assertNotSame(
                referencedPkList.get(1),
                referencedPkList.get(2));

        // the fk list must reference intPk2, intPk3, intPk3
        // and the last 2 referenced objects must not be the same objects
        assertEquals(
                testData.getPIntegerPkList().get(1),
                fkList.get(0).getPIntegerPk());
        assertEquals(
                testData.getPIntegerPkList().get(2),
                fkList.get(1).getPIntegerPk());
        assertEquals(
                testData.getPIntegerPkList().get(2),
                fkList.get(2).getPIntegerPk());
        assertNotSame(
                fkList.get(1).getPIntegerPk(),
                fkList.get(2).getPIntegerPk());

        // The objects in the result list must be the same objects
        // as the referenced objects in the fk list
        assertSame(
                referencedPkList.get(0),
                fkList.get(0).getPIntegerPk());
        assertSame(
                referencedPkList.get(1),
                fkList.get(1).getPIntegerPk());
        assertSame(
                referencedPkList.get(2),
                fkList.get(2).getPIntegerPk());
    }

    /**
     * Tests that the fill method for referenced objects works for many rows
     * in the database.
     */
    @Test
    public void testFillReferencedManyRecords() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        for (int i = 1; i <= MANY; ++i)
        {
            NullableOIntegerFk fkObject = new NullableOIntegerFk();
            fkObject.setName("nullableOIntegerFk" + i);

            if (i % 10 != 0)
            {
                OIntegerPk pkObject = new OIntegerPk();
                pkObject.setName("oIntegerPk" + i);
                pkObject.setIntegerColumn(i);
                pkObject.save();
                fkObject.setOIntegerPk(pkObject);
                fkObject.save();

            }
            else
            {
                fkObject.save();
            }
        }

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(NullableOIntegerFkPeer.ID);
        List<NullableOIntegerFk> fkObjectList
        = NullableOIntegerFkPeer.doSelect(criteria);
        assertEquals(MANY, fkObjectList.size());

        int count = 0;
        for (NullableOIntegerFk fkObject : fkObjectList)
        {
            if (fkObject.getFk() != null && fkObject.getFk() == 0)
            {
                fail("fk Object with id " + fkObject.getId()
                + " on position " + count + " has foreign key 0");
            }
            ++count;
        }
        // TODO assert that fkObjectList have not already loaded their pk object

        List<OIntegerPk> oIntegerPkList
        = NullableOIntegerFkPeer.fillOIntegerPks(fkObjectList);

        assertEquals(MANY - (MANY / 10), oIntegerPkList.size());

        int i = 1;
        for (NullableOIntegerFk fkObject : fkObjectList)
        {
            if (i % 10 != 0)
            {
                assertEquals(
                        "oIntegerPk" + i,
                        fkObject.getOIntegerPk().getName());
            }
            else
            {
                assertNull(fkObject.getOIntegerPk());
            }
            ++i;
        }
    }

    /**
     * Tests the functionality for the fillers for referencing Objects.
     * The foreign key is a required int(primitive) column
     * and it points to the primary key of the referenced table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencingPrimitiveIntegerKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(PIntegerPkPeer.ID);
        List<PIntegerPk> pkList = PIntegerPkPeer.doSelect(criteria);
        assertEquals(3, pkList.size());
        assertEquals(
                testData.getPIntegerPkList().get(0).getId(),
                pkList.get(0).getId());
        // check referencing lists are not initialized
        for (PIntegerPk pIntegerPk : pkList)
        {
            assertFalse(pIntegerPk.isRequiredPIntegerFksInitialized());
        }

        List<RequiredPIntegerFk> referencingFkList
        = PIntegerPkPeer.fillRequiredPIntegerFks(
                pkList);

        // check all referencing lists are initialized
        for (PIntegerPk pIntegerPk : pkList)
        {
            assertTrue(pIntegerPk.isRequiredPIntegerFksInitialized());
        }
        // returned list must contain requiredPIntegerFk2,
        // requiredPIntegerFk3a, requiredPIntegerFk3b
        // where the last two can be exchanged
        assertEquals(3, referencingFkList.size());
        assertEquals(
                testData.getRequiredPIntegerFkList().get(0).getId(),
                referencingFkList.get(0).getId());
        assertTrue(
                referencingFkList.contains(
                        testData.getRequiredPIntegerFkList().get(1)));
        assertTrue(
                referencingFkList.contains(
                        testData.getRequiredPIntegerFkList().get(2)));

        // The second pk entry must reference requiredPIntegerFk2
        // and the third pk entry must reference requiredPIntegerFk3a
        // and requiredPIntegerFk3b
        assertEquals(0, pkList.get(0).getRequiredPIntegerFks().size());
        assertEquals(1, pkList.get(1).getRequiredPIntegerFks().size());
        assertEquals(2, pkList.get(2).getRequiredPIntegerFks().size());
        assertEquals(
                testData.getRequiredPIntegerFkList().get(0),
                pkList.get(1).getRequiredPIntegerFks().get(0));
        assertTrue(
                pkList.get(2).getRequiredPIntegerFks().contains(
                        testData.getRequiredPIntegerFkList().get(1)));
        assertTrue(
                pkList.get(2).getRequiredPIntegerFks().contains(
                        testData.getRequiredPIntegerFkList().get(2)));

        // The objects in the result list must be the same objects
        // as the referenced objects in the pk list
        assertSame(
                referencingFkList.get(0),
                pkList.get(1).getRequiredPIntegerFks().get(0));
        assertSame(
                referencingFkList.get(1),
                pkList.get(2).getRequiredPIntegerFks().get(0));
        assertSame(
                referencingFkList.get(2),
                pkList.get(2).getRequiredPIntegerFks().get(1));

        // check flags
        for (RequiredPIntegerFk requiredPIntegerFk : referencingFkList)
        {
            assertFalse(requiredPIntegerFk.isNew());
            assertFalse(requiredPIntegerFk.isModified());
            assertFalse(requiredPIntegerFk.isLoading());
            assertFalse(requiredPIntegerFk.isDeleted());
            assertFalse(requiredPIntegerFk.isSaving());
        }
    }

    /**
     * Tests the functionality for the fillers for referencing Objects.
     * The foreign key is a nullable object integer column
     * and it points to the primary key of the referenced table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencingObjectIntegerKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(OIntegerPkPeer.ID);
        List<OIntegerPk> pkList = OIntegerPkPeer.doSelect(criteria);
        assertEquals(3, pkList.size());
        assertEquals(
                testData.getOIntegerPkList().get(0).getId(),
                pkList.get(0).getId());
        // check referencing lists are not initialized
        for (OIntegerPk oIntegerPk : pkList)
        {
            assertFalse(oIntegerPk.isNullableOIntegerFksInitialized());
        }

        List<NullableOIntegerFk> referencingFkList
        = OIntegerPkPeer.fillNullableOIntegerFks(
                pkList);

        // returned list must contain nullableOIntegerFk2,
        // nullableOIntegerFk3a, nullableOIntegerFk3b
        // where the last two can be exchanged
        assertEquals(3, referencingFkList.size());
        assertEquals(
                testData.getNullableOIntegerFkList().get(0).getId(),
                referencingFkList.get(0).getId());
        assertTrue(
                referencingFkList.contains(
                        testData.getNullableOIntegerFkList().get(1)));
        assertTrue(
                referencingFkList.contains(
                        testData.getNullableOIntegerFkList().get(2)));

        // The second pk entry must reference nullableOIntegerFk2
        // and the third pk entry must reference nullableOIntegerFk3a
        // and nullableOIntegerFk3b
        assertEquals(0, pkList.get(0).getNullableOIntegerFks().size());
        assertEquals(1, pkList.get(1).getNullableOIntegerFks().size());
        assertEquals(2, pkList.get(2).getNullableOIntegerFks().size());
        assertEquals(
                testData.getNullableOIntegerFkList().get(0),
                pkList.get(1).getNullableOIntegerFks().get(0));
        assertTrue(
                pkList.get(2).getNullableOIntegerFks().contains(
                        testData.getNullableOIntegerFkList().get(1)));
        assertTrue(
                pkList.get(2).getNullableOIntegerFks().contains(
                        testData.getNullableOIntegerFkList().get(2)));

        // The objects in the result list must be the same objects
        // as the referenced objects in the pk list
        assertSame(
                referencingFkList.get(0),
                pkList.get(1).getNullableOIntegerFks().get(0));
        assertSame(
                referencingFkList.get(1),
                pkList.get(2).getNullableOIntegerFks().get(0));
        assertSame(
                referencingFkList.get(2),
                pkList.get(2).getNullableOIntegerFks().get(1));
    }

    /**
     * Tests whether duplicate objects in the argument for the fillers
     * for referencing Objects are treated correctly.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencingDuplicateObjects() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria().where(
                OIntegerPkPeer.NAME, "oIntegerPk2");

        List<OIntegerPk> pkList
        = OIntegerPkPeer.doSelect(criteria);
        assertEquals(1, pkList.size());
        assertEquals(
                testData.getOIntegerPkList().get(1).getId(),
                pkList.get(0).getId());
        OIntegerPk pkEntryCopy = pkList.get(0).copy();
        pkEntryCopy.setPrimaryKey(pkList.get(0).getPrimaryKey());
        pkList.add(pkEntryCopy);

        List<NullableOIntegerFk> referencedFkList
        = OIntegerPkPeer.fillNullableOIntegerFks(
                pkList);

        // returned list must contain nullableOIntegerFk2, nullableOIntegerFk2
        // and the objects must not be the same objects
        assertEquals(2, referencedFkList.size());
        assertEquals(
                testData.getNullableOIntegerFkList().get(0).getId(),
                referencedFkList.get(0).getId());
        assertEquals(
                testData.getNullableOIntegerFkList().get(0).getId(),
                referencedFkList.get(1).getId());
        assertNotSame(
                referencedFkList.get(0),
                referencedFkList.get(1));

        // the fk list must reference nullableOIntegerFk2, nullableOIntegerFk2
        // and the referenced objects must not be the same objects
        assertEquals(
                testData.getNullableOIntegerFkList().get(0),
                pkList.get(0).getNullableOIntegerFks().get(0));
        assertEquals(
                testData.getNullableOIntegerFkList().get(0),
                pkList.get(1).getNullableOIntegerFks().get(0));
        assertNotSame(
                pkList.get(0).getNullableOIntegerFks().get(0),
                pkList.get(1).getNullableOIntegerFks().get(0));

        // The objects in the result list must be the same objects
        // as the referenced objects in the fk list
        assertSame(
                referencedFkList.get(0),
                pkList.get(0).getNullableOIntegerFks().get(0));
        assertSame(
                referencedFkList.get(1),
                pkList.get(1).getNullableOIntegerFks().get(0));
    }

    /**
     * Tests the functionality for the fillers for referenced Objects.
     * The foreign key is a non-required object Integer column,
     * and it does not point to the primary key of the referenced table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencingNonPrimaryKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(
                OIntegerPkPeer.ID);
        List<OIntegerPk> pkList
        = OIntegerPkPeer.doSelect(criteria);
        assertEquals(3, pkList.size());
        assertEquals(
                testData.getOIntegerPkList().get(0).getId(),
                pkList.get(0).getId());
        // check referencing lists are not initialized
        for (OIntegerPk oIntegerPk : pkList)
        {
            assertFalse(oIntegerPk.isNonPkOIntegerFksInitialized());
        }

        List<NonPkOIntegerFk> referencingFkList
        = OIntegerPkPeer.fillNonPkOIntegerFks(pkList);

        // returned list must contain nonPkOIntegerFk1a, nonPkOIntegerFk1b,
        // nonPkOIntegerFk2
        assertEquals(3, referencingFkList.size());
        assertTrue(
                referencingFkList.contains(
                        testData.getNonPkOIntegerFkList().get(0)));
        assertTrue(
                referencingFkList.contains(
                        testData.getNonPkOIntegerFkList().get(1)));
        assertEquals(
                testData.getNonPkOIntegerFkList().get(2).getId(),
                referencingFkList.get(2).getId());

        // the fk list must reference (nonPkOIntegerFk1a, nonPkOIntegerFk1b),
        // (nonPkOIntegerFk2),  ()
        assertEquals(2, pkList.get(0).getNonPkOIntegerFks().size());
        assertEquals(1, pkList.get(1).getNonPkOIntegerFks().size());
        assertEquals(0, pkList.get(2).getNonPkOIntegerFks().size());
        assertTrue(
                pkList.get(0).getNonPkOIntegerFks().contains(
                        testData.getNonPkOIntegerFkList().get(0)));
        assertTrue(
                pkList.get(0).getNonPkOIntegerFks().contains(
                        testData.getNonPkOIntegerFkList().get(1)));
        assertTrue(
                pkList.get(1).getNonPkOIntegerFks().contains(
                        testData.getNonPkOIntegerFkList().get(2)));

        // The objects in the result list must be the same objects
        // as the referenced objects in the pk list
        assertSame(
                referencingFkList.get(0),
                pkList.get(0).getNonPkOIntegerFks().get(0));
        assertSame(
                referencingFkList.get(1),
                pkList.get(0).getNonPkOIntegerFks().get(1));
        assertSame(
                referencingFkList.get(2),
                pkList.get(1).getNonPkOIntegerFks().get(0));
    }

    /**
     * Tests the functionality for the fillers for referencing Objects.
     * The foreign key is a non-required composite key, consisting
     * of an integer and varchar column,
     * and it points to the primary key of the referenced table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencingCompositeKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(CompIntegerVarcharPkPeer.ID1);
        criteria.addAscendingOrderByColumn(CompIntegerVarcharPkPeer.ID2);
        List<CompIntegerVarcharPk> pkList
        = CompIntegerVarcharPkPeer.doSelect(criteria);
        assertEquals(3, pkList.size());
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(0).getPrimaryKey(),
                pkList.get(0).getPrimaryKey());
        // check referencing lists are not initialized
        for (CompIntegerVarcharPk compIntegerVarcharPk : pkList)
        {
            assertFalse(compIntegerVarcharPk.isCompIntegerVarcharFksInitialized());
        }

        List<CompIntegerVarcharFk> referencingFkList
        = CompIntegerVarcharPkPeer.fillCompIntegerVarcharFks(
                pkList);

        // returned list must contain compositeIntegerVarcharFk2,
        // compositeIntegerVarcharFk3a, compositeIntegerVarcharFk3b
        // where the order of the last two is undefined
        assertEquals(3, referencingFkList.size());
        assertEquals(
                testData.getCompositeIntegerVarcharFkList().get(0).getId(),
                referencingFkList.get(0).getId());
        assertTrue(
                referencingFkList.contains(
                        testData.getCompositeIntegerVarcharFkList().get(1)));
        assertTrue(
                referencingFkList.contains(
                        testData.getCompositeIntegerVarcharFkList().get(2)));

        // The second pk entry must reference compositeIntegerVarcharFk2
        // and the third pk entry must reference compositeIntegerVarcharFk3a
        // and compositeIntegerVarcharFk3b
        assertEquals(0, pkList.get(0).getCompIntegerVarcharFks().size());
        assertEquals(1, pkList.get(1).getCompIntegerVarcharFks().size());
        assertEquals(2, pkList.get(2).getCompIntegerVarcharFks().size());
        assertEquals(
                testData.getCompositeIntegerVarcharFkList().get(0),
                pkList.get(1).getCompIntegerVarcharFks().get(0));
        assertTrue(
                pkList.get(2).getCompIntegerVarcharFks().contains(
                        testData.getCompositeIntegerVarcharFkList().get(1)));
        assertTrue(
                pkList.get(2).getCompIntegerVarcharFks().contains(
                        testData.getCompositeIntegerVarcharFkList().get(2)));

        // The objects in the result list must be the same objects
        // as the referenced objects in the pk list
        assertSame(
                referencingFkList.get(0),
                pkList.get(1).getCompIntegerVarcharFks().get(0));
        assertSame(
                referencingFkList.get(1),
                pkList.get(2).getCompIntegerVarcharFks().get(0));
        assertSame(
                referencingFkList.get(2),
                pkList.get(2).getCompIntegerVarcharFks().get(1));
    }

    /**
     * Tests the functionality for the fillers for referencing Objects.
     * The foreign key is a non-required composite key, consisting
     * of an integer and varchar column,
     * and it does not point to the primary key of the referenced table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testFillReferencingNonPrimaryCompositeKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(CompIntegerVarcharPkPeer.ID1);
        criteria.addAscendingOrderByColumn(CompIntegerVarcharPkPeer.ID2);
        List<CompIntegerVarcharPk> pkList
        = CompIntegerVarcharPkPeer.doSelect(criteria);
        assertEquals(3, pkList.size());
        assertEquals(
                testData.getCompositeIntegerVarcharPkList().get(0).getPrimaryKey(),
                pkList.get(0).getPrimaryKey());
        // check referencing lists are not initialized
        for (CompIntegerVarcharPk compIntegerVarcharPk : pkList)
        {
            assertFalse(compIntegerVarcharPk.isCompNonpkFksInitialized());
        }

        List<CompNonpkFk> referencingFkList
        = CompIntegerVarcharPkPeer.fillCompNonpkFks(pkList);

        // returned list must contain compositeNonpkFk1a,
        // compositeNonpkFk1b, compositeNonpkFk2
        // where the order of the first two is undefined
        assertEquals(3, referencingFkList.size());
        assertTrue(
                referencingFkList.contains(
                        testData.getCompositeNonpkFkList().get(0)));
        assertTrue(
                referencingFkList.contains(
                        testData.getCompositeNonpkFkList().get(1)));
        assertEquals(
                testData.getCompositeNonpkFkList().get(2).getId(),
                referencingFkList.get(2).getId());

        // The first pk entry must reference compositeNonpkFk1a and
        // compositeNonpkFk1b and the second pk entry must reference
        // compositeNonpkFk2
        assertEquals(2, pkList.get(0).getCompNonpkFks().size());
        assertEquals(1, pkList.get(1).getCompNonpkFks().size());
        assertEquals(0, pkList.get(2).getCompNonpkFks().size());
        assertTrue(
                pkList.get(0).getCompNonpkFks().contains(
                        testData.getCompositeNonpkFkList().get(0)));
        assertTrue(
                pkList.get(0).getCompNonpkFks().contains(
                        testData.getCompositeNonpkFkList().get(1)));
        assertEquals(
                testData.getCompositeNonpkFkList().get(2),
                pkList.get(1).getCompNonpkFks().get(0));

        // The objects in the result list must be the same objects
        // as the referenced objects in the pk list
        assertSame(
                referencingFkList.get(0),
                pkList.get(0).getCompNonpkFks().get(0));
        assertSame(
                referencingFkList.get(1),
                pkList.get(0).getCompNonpkFks().get(1));
        assertSame(
                referencingFkList.get(2),
                pkList.get(1).getCompNonpkFks().get(0));
    }

    /**
     * Tests that the fill method overwrites the referencing object collection
     * with the current database state.
     */
    @Test
    public void testReferencingObjectRefill() throws TorqueException
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.and(OIntegerPkPeer.NAME, "oIntegerPk2");
        List<OIntegerPk> pkList = OIntegerPkPeer.doSelect(criteria);
        assertEquals(1, pkList.size());
        OIntegerPk pkObject = pkList.get(0);
        assertEquals(
                testData.getOIntegerPkList().get(1).getId(),
                pkList.get(0).getId());

        NullableOIntegerFk changedFkObject
        = pkObject.getNullableOIntegerFks().get(0);
        changedFkObject.setName("nullableOIntegerFk2Modified");

        List<NullableOIntegerFk> referencingFkList
        = OIntegerPkPeer.fillNullableOIntegerFks(pkList);

        // returned list must contain unchanged new nullableOIntegerFk2
        assertEquals(1, referencingFkList.size());
        assertEquals("nullableOIntegerFk2", referencingFkList.get(0).getName());
        assertNotSame(referencingFkList.get(0), changedFkObject);

        // fkObject must be referenced by unchanged new nullableOIntegerFk2
        assertEquals(
                "nullableOIntegerFk2",
                pkObject.getNullableOIntegerFks().get(0).getName());
        assertNotSame(
                pkObject.getNullableOIntegerFks().get(0),
                changedFkObject);
    }

    /**
     * Tests that the chunk size -1 works for referencing objects.
     */
    @Test
    public void testReferencingObjectChunkSizeMinusOne() throws TorqueException
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(PIntegerPkPeer.ID);
        List<PIntegerPk> pkList = PIntegerPkPeer.doSelect(criteria);
        assertEquals(3, pkList.size());
        assertEquals(
                testData.getPIntegerPkList().get(0).getId(),
                pkList.get(0).getId());
        // check referencing lists are not initialized
        for (PIntegerPk pIntegerPk : pkList)
        {
            assertFalse(pIntegerPk.isRequiredPIntegerFksInitialized());
        }

        List<RequiredPIntegerFk> referencingFkList
        = PIntegerPkPeer.fillRequiredPIntegerFks(pkList, -1);

        // check all referencing lists are initialized
        for (PIntegerPk pIntegerPk : pkList)
        {
            assertTrue(pIntegerPk.isRequiredPIntegerFksInitialized());
        }
        // returned list must contain requiredPIntegerFk2,
        // requiredPIntegerFk3a, requiredPIntegerFk3b
        // where the last two can be exchanged
        assertEquals(3, referencingFkList.size());
        assertEquals(
                testData.getRequiredPIntegerFkList().get(0).getId(),
                referencingFkList.get(0).getId());
        assertTrue(
                referencingFkList.contains(
                        testData.getRequiredPIntegerFkList().get(1)));
        assertTrue(
                referencingFkList.contains(
                        testData.getRequiredPIntegerFkList().get(2)));

        // The second pk entry must reference requiredPIntegerFk2
        // and the third pk entry must reference requiredPIntegerFk3a
        // and requiredPIntegerFk3b
        assertEquals(0, pkList.get(0).getRequiredPIntegerFks().size());
        assertEquals(1, pkList.get(1).getRequiredPIntegerFks().size());
        assertEquals(2, pkList.get(2).getRequiredPIntegerFks().size());
        assertEquals(
                testData.getRequiredPIntegerFkList().get(0),
                pkList.get(1).getRequiredPIntegerFks().get(0));
        assertTrue(
                pkList.get(2).getRequiredPIntegerFks().contains(
                        testData.getRequiredPIntegerFkList().get(1)));
        assertTrue(
                pkList.get(2).getRequiredPIntegerFks().contains(
                        testData.getRequiredPIntegerFkList().get(2)));

        // The objects in the result list must be the same objects
        // as the referenced objects in the pk list
        assertSame(
                referencingFkList.get(0),
                pkList.get(1).getRequiredPIntegerFks().get(0));
        assertSame(
                referencingFkList.get(1),
                pkList.get(2).getRequiredPIntegerFks().get(0));
        assertSame(
                referencingFkList.get(2),
                pkList.get(2).getRequiredPIntegerFks().get(1));
    }

    /**
     * Tests that the fill method for referencing objects works for many rows
     * in the database.
     */
    @Test
    public void testFillReferencingManyRecords() throws TorqueException
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        for (int i = 1; i <= MANY; ++i)
        {
            OIntegerPk pkObject = new OIntegerPk();
            pkObject.setName("oIntegerPk" + i);
            pkObject.setIntegerColumn(i);
            pkObject.save();

            if (i % 10 != 0)
            {
                NullableOIntegerFk fkObjectA = new NullableOIntegerFk();
                fkObjectA.setName("nullableOIntegerFk" + i + "a");
                fkObjectA.setOIntegerPk(pkObject);
                fkObjectA.save();
                NullableOIntegerFk fkObjectB = new NullableOIntegerFk();
                fkObjectB.setName("nullableOIntegerFk" + i + "b");
                fkObjectB.setOIntegerPk(pkObject);
                fkObjectB.save();
            }
        }

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(OIntegerPkPeer.ID);
        List<OIntegerPk> pkObjectList = OIntegerPkPeer.doSelect(criteria);
        assertEquals(MANY, pkObjectList.size());
        // check referencing lists are not initialized
        for (OIntegerPk oIntegerPk : pkObjectList)
        {
            assertFalse(oIntegerPk.isNullableOIntegerFksInitialized());
        }

        List<NullableOIntegerFk> nullableOIntegerFkList
        = OIntegerPkPeer.fillNullableOIntegerFks(pkObjectList);

        assertEquals(2 * (MANY - (MANY / 10)), nullableOIntegerFkList.size());

        int i = 1;
        for (OIntegerPk pkObject : pkObjectList)
        {
            if (i % 10 != 0)
            {
                assertEquals(2,
                        pkObject.getNullableOIntegerFks().size());
                assertTrue(
                        pkObject.getNullableOIntegerFks().get(0).getName()
                        .startsWith("nullableOIntegerFk" + i));
                assertTrue(
                        pkObject.getNullableOIntegerFks().get(1).getName()
                        .startsWith("nullableOIntegerFk" + i));
            }
            else
            {
                assertEquals(0,
                        pkObject.getNullableOIntegerFks().size());
            }
            ++i;
        }
    }
}
