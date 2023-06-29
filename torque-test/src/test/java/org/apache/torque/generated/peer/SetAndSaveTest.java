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

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.ForeignKeySchemaData;
import org.apache.torque.TorqueException;
import org.apache.torque.test.dbobject.CompIntegerVarcharFk;
import org.apache.torque.test.dbobject.CompIntegerVarcharPk;
import org.apache.torque.test.dbobject.CompPkContainsFk;
import org.apache.torque.test.dbobject.CompPkOtherFk;
import org.apache.torque.test.dbobject.NonPkOIntegerFk;
import org.apache.torque.test.dbobject.NullableOIntegerFk;
import org.apache.torque.test.dbobject.OIntegerPk;
import org.apache.torque.test.peer.CompIntegerVarcharPkPeer;
import org.apache.torque.test.peer.OIntegerPkPeer;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

/**
 * Tests the setAndSave method for different constellations.
 *
 * @version $Id: SetAndSaveTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class SetAndSaveTest extends BaseDatabaseTestCase
{
    /**
     * Tests whether the setAndSave method works
     * where the referencing object has a non-composite primary key
     * and the foreign key is not composite
     * and the foreign key references the primary key of the referenced object.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSetAndSavePkIntegerForeignKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        OIntegerPk oIntegerPk = testData.getOIntegerPkList().get(2);
        List<NullableOIntegerFk> fkList = new ArrayList<>();
        // object already associated to this object
        fkList.add(testData.getNullableOIntegerFkList().get(1));
        // object already associated to another object
        fkList.add(testData.getNullableOIntegerFkList().get(0));
        // object not associated yet
        fkList.add(testData.getNullableOIntegerFkList().get(3));
        // new object
        NullableOIntegerFk newNullableOIntegerFk = new NullableOIntegerFk();
        newNullableOIntegerFk.setName("newNullableOIntegerFk");
        fkList.add(newNullableOIntegerFk);

        OIntegerPkPeer.setAndSaveNullableOIntegerFks(
                oIntegerPk,
                fkList);

        List<NullableOIntegerFk> cachedFks
        = oIntegerPk.getNullableOIntegerFks();
        assertEquals(4, cachedFks.size());
        assertEquals(
                testData.getNullableOIntegerFkList().get(1).getId(),
                cachedFks.get(0).getId());
        assertEquals(
                oIntegerPk.getId(),
                cachedFks.get(0).getFk());
        assertEquals(
                testData.getNullableOIntegerFkList().get(1).getName(),
                cachedFks.get(0).getName());
        assertEquals(
                testData.getNullableOIntegerFkList().get(0).getId(),
                cachedFks.get(1).getId());
        assertEquals(
                oIntegerPk.getId(),
                cachedFks.get(1).getFk());
        assertEquals(
                testData.getNullableOIntegerFkList().get(0).getName(),
                cachedFks.get(1).getName());
        assertEquals(
                testData.getNullableOIntegerFkList().get(3).getId(),
                cachedFks.get(2).getId());
        assertEquals(
                oIntegerPk.getId(),
                cachedFks.get(2).getFk());
        assertEquals(
                testData.getNullableOIntegerFkList().get(3).getName(),
                cachedFks.get(2).getName());
        assertEquals(
                newNullableOIntegerFk.getId(),
                cachedFks.get(3).getId());
        assertEquals(
                oIntegerPk.getId(),
                cachedFks.get(3).getFk());
        assertEquals(
                newNullableOIntegerFk.getName(),
                cachedFks.get(3).getName());

        // check database
        ForeignKeySchemaData.assertNullableOIntegerFksInDatabaseEquals(
                cachedFks);
    }

    /**
     * Tests whether the setAndSave method works
     * where the referencing object has a non-composite primary key
     * and the foreign key is not composite
     * and the foreign key references not the primary key of the referenced
     * object.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSetAndSaveNonpkIntegerForeignKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        OIntegerPk oIntegerPk = testData.getOIntegerPkList().get(0);
        // the original list size is 2, one of the object remains
        // the other is removed
        assertEquals(2, oIntegerPk.getNonPkOIntegerFks().size());
        // this object is removed from the list later on
        assertTrue(oIntegerPk.getNonPkOIntegerFks().contains(
                testData.getNonPkOIntegerFkList().get(0)));
        // this object remains in the list later on
        assertTrue(oIntegerPk.getNonPkOIntegerFks().contains(
                testData.getNonPkOIntegerFkList().get(1)));

        List<NonPkOIntegerFk> fkList = new ArrayList<>();
        // object already associated to this object
        fkList.add(testData.getNonPkOIntegerFkList().get(1));
        // object already associated to another object
        fkList.add(testData.getNonPkOIntegerFkList().get(2));
        // object not associated yet
        fkList.add(testData.getNonPkOIntegerFkList().get(3));
        // new object
        NonPkOIntegerFk newNonPkOIntegerFk = new NonPkOIntegerFk();
        newNonPkOIntegerFk.setName("newNonPkOIntegerFk");
        fkList.add(newNonPkOIntegerFk);

        OIntegerPkPeer.setAndSaveNonPkOIntegerFks(
                oIntegerPk,
                fkList);

        assertSetAndSaveForOIntegerPk(testData, oIntegerPk, newNonPkOIntegerFk);
    }

    /**
     * Tests whether the setAndSave method works
     * where the passed collection is the cached list of the object.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSetAndSaveCachedCollection() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        OIntegerPk oIntegerPk = testData.getOIntegerPkList().get(0);
        List<NonPkOIntegerFk> fkList = oIntegerPk.initNonPkOIntegerFks();
        fkList.clear();
        // object already associated to this object
        fkList.add(testData.getNonPkOIntegerFkList().get(1));
        // object already associated to another object
        fkList.add(testData.getNonPkOIntegerFkList().get(2));
        // object not associated yet
        fkList.add(testData.getNonPkOIntegerFkList().get(3));
        // new object
        NonPkOIntegerFk newNonPkOIntegerFk = new NonPkOIntegerFk();
        newNonPkOIntegerFk.setName("newNonPkOIntegerFk");
        fkList.add(newNonPkOIntegerFk);

        OIntegerPkPeer.setAndSaveNonPkOIntegerFks(
                oIntegerPk,
                fkList);

        assertSetAndSaveForOIntegerPk(testData, oIntegerPk, newNonPkOIntegerFk);
    }

    /**
     * Checks that the database content is expected as for SetAndSave
     * for OIntegerPk.
     *
     * @param testData the original database content.
     * @param oIntegerPk the oIntegerPk object which was saved.
     * @param newNonPkOIntegerFk the new object which was added.
     *
     * @throws TorqueException if an error occurs
     */
    private void assertSetAndSaveForOIntegerPk(
            final ForeignKeySchemaData testData,
            final OIntegerPk oIntegerPk,
            final NonPkOIntegerFk newNonPkOIntegerFk)
                    throws TorqueException
    {
        List<NonPkOIntegerFk> cachedFks
        = oIntegerPk.getNonPkOIntegerFks();
        assertEquals(4, cachedFks.size());
        assertEquals(
                testData.getNonPkOIntegerFkList().get(1).getId(),
                cachedFks.get(0).getId());
        assertEquals(
                oIntegerPk.getIntegerColumn(),
                cachedFks.get(0).getFk());
        assertEquals(
                testData.getNonPkOIntegerFkList().get(1).getName(),
                cachedFks.get(0).getName());
        assertEquals(
                testData.getNonPkOIntegerFkList().get(2).getId(),
                cachedFks.get(1).getId());
        assertEquals(
                oIntegerPk.getIntegerColumn(),
                cachedFks.get(1).getFk());
        assertEquals(
                testData.getNonPkOIntegerFkList().get(2).getName(),
                cachedFks.get(1).getName());
        assertEquals(
                testData.getNonPkOIntegerFkList().get(3).getId(),
                cachedFks.get(2).getId());
        assertEquals(
                oIntegerPk.getIntegerColumn(),
                cachedFks.get(2).getFk());
        assertEquals(
                testData.getNonPkOIntegerFkList().get(3).getName(),
                cachedFks.get(2).getName());
        assertEquals(
                newNonPkOIntegerFk.getId(),
                cachedFks.get(3).getId());
        assertEquals(
                oIntegerPk.getIntegerColumn(),
                cachedFks.get(3).getFk());
        assertEquals(
                newNonPkOIntegerFk.getName(),
                cachedFks.get(3).getName());

        // check database
        ForeignKeySchemaData.assertNonPkOIntegerFksInDatabaseEquals(
                cachedFks);
    }

    /**
     * Tests whether the setAndSave method works
     * where the referencing object has a non-composite primary key
     * and the foreign key is composite
     * and the foreign key references the primary key of the referenced object.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSetAndSaveCompositePkIntegerForeignKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        CompIntegerVarcharPk compIntegerVarcharPk
        = testData.getCompositeIntegerVarcharPkList().get(2);
        List<CompIntegerVarcharFk> fkList
            = new ArrayList<>();
        // object already associated to this object
        fkList.add(testData.getCompositeIntegerVarcharFkList().get(1));
        // object already associated to another object
        fkList.add(testData.getCompositeIntegerVarcharFkList().get(0));
        // object not associated yet
        fkList.add(testData.getCompositeIntegerVarcharFkList().get(3));
        // new object
        CompIntegerVarcharFk newCompIntegerVarcharFk
            = new CompIntegerVarcharFk();
        newCompIntegerVarcharFk.setName("newCompositeIntegerVarcharFk");
        fkList.add(newCompIntegerVarcharFk);

        CompIntegerVarcharPkPeer.setAndSaveCompIntegerVarcharFks(
                compIntegerVarcharPk,
                fkList);

        List<CompIntegerVarcharFk> cachedFks
        = compIntegerVarcharPk.getCompIntegerVarcharFks();
        assertEquals(4, cachedFks.size());
        assertEquals(
                testData.getCompositeIntegerVarcharFkList().get(1).getId(),
                cachedFks.get(0).getId());
        assertEquals(
                compIntegerVarcharPk.getId1(),
                cachedFks.get(0).getFk1());
        assertEquals(
                compIntegerVarcharPk.getId2(),
                cachedFks.get(0).getFk2());
        assertEquals(
                testData.getCompositeIntegerVarcharFkList().get(1).getName(),
                cachedFks.get(0).getName());
        assertEquals(
                testData.getCompositeIntegerVarcharFkList().get(0).getId(),
                cachedFks.get(1).getId());
        assertEquals(
                compIntegerVarcharPk.getId1(),
                cachedFks.get(1).getFk1());
        assertEquals(
                compIntegerVarcharPk.getId2(),
                cachedFks.get(1).getFk2());
        assertEquals(
                testData.getCompositeIntegerVarcharFkList().get(0).getName(),
                cachedFks.get(1).getName());
        assertEquals(
                testData.getCompositeIntegerVarcharFkList().get(3).getId(),
                cachedFks.get(2).getId());
        assertEquals(
                compIntegerVarcharPk.getId1(),
                cachedFks.get(2).getFk1());
        assertEquals(
                compIntegerVarcharPk.getId2(),
                cachedFks.get(2).getFk2());
        assertEquals(
                testData.getCompositeIntegerVarcharFkList().get(3).getName(),
                cachedFks.get(2).getName());
        assertEquals(
                newCompIntegerVarcharFk.getId(),
                cachedFks.get(3).getId());
        assertEquals(
                compIntegerVarcharPk.getId1(),
                cachedFks.get(3).getFk1());
        assertEquals(
                compIntegerVarcharPk.getId2(),
                cachedFks.get(3).getFk2());
        assertEquals(
                newCompIntegerVarcharFk.getName(),
                cachedFks.get(3).getName());

        // check database
        ForeignKeySchemaData.assertCompositeIntegerVarcharFksInDatabaseEquals(
                cachedFks);
    }

    /**
     * Tests whether the setAndSave method works
     * where the referencing object has a composite primary key
     * and the foreign key is not composite and not part of the primary key
     * and the foreign key references the primary key of the referenced object.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSetAndSaveCompositePkOtherFk() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        OIntegerPk oIntegerPk = testData.getOIntegerPkList().get(0);
        List<CompPkOtherFk> fkList = new ArrayList<>();
        // object already associated to this object
        fkList.add(testData.getCompositePkOtherFkList().get(0));
        // object already associated to another object
        fkList.add(testData.getCompositePkOtherFkList().get(2));
        // new object
        CompPkOtherFk newCompPkOtherFk = new CompPkOtherFk();
        newCompPkOtherFk.setName("newCompPkOtherFk");
        newCompPkOtherFk.setId1(42);
        newCompPkOtherFk.setId2("new");
        fkList.add(newCompPkOtherFk);

        OIntegerPkPeer.setAndSaveCompPkOtherFks(
                oIntegerPk,
                fkList);

        List<CompPkOtherFk> cachedFks = oIntegerPk.getCompPkOtherFks();
        assertEquals(3, cachedFks.size());
        assertEquals(
                testData.getCompositePkOtherFkList().get(0).getId1(),
                cachedFks.get(0).getId1());
        assertEquals(
                testData.getCompositePkOtherFkList().get(0).getId2(),
                cachedFks.get(0).getId2());
        assertEquals(
                oIntegerPk.getId(),
                cachedFks.get(0).getFk());
        assertEquals(
                testData.getCompositePkOtherFkList().get(0).getName(),
                cachedFks.get(0).getName());
        assertEquals(
                testData.getCompositePkOtherFkList().get(2).getId1(),
                cachedFks.get(1).getId1());
        assertEquals(
                testData.getCompositePkOtherFkList().get(2).getId2(),
                cachedFks.get(1).getId2());
        assertEquals(
                oIntegerPk.getId(),
                cachedFks.get(1).getFk());
        assertEquals(
                testData.getCompositePkOtherFkList().get(2).getName(),
                cachedFks.get(1).getName());
        assertEquals(
                newCompPkOtherFk.getId1(),
                cachedFks.get(2).getId1());
        assertEquals(
                newCompPkOtherFk.getId2(),
                cachedFks.get(2).getId2());
        assertEquals(
                oIntegerPk.getId(),
                cachedFks.get(1).getFk());
        assertEquals(
                newCompPkOtherFk.getName(),
                cachedFks.get(2).getName());

        // check database
        ForeignKeySchemaData.assertCompositePkOtherFksInDatabaseEquals(
                cachedFks);
    }

    /**
     * Tests whether the setAndSave method works
     * where the referencing object has a composite primary key
     * and the foreign key is not composite and part of the primary key
     * and the foreign key references the primary key of the referenced object.
     *
     * Note that in this case re-attaching a saved object to another object
     * will not work because torque will not update a primary key.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSetAndSaveCompositePkContainsFk() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        OIntegerPk oIntegerPk = testData.getOIntegerPkList().get(0);
        List<CompPkContainsFk> fkList = new ArrayList<>();
        // object already associated to this object
        fkList.add(testData.getCompositePkContainsFkList().get(0));
        // new object
        CompPkContainsFk newCompPkContainsFk = new CompPkContainsFk();
        newCompPkContainsFk.setName("newCompPkContainsFk");
        newCompPkContainsFk.setId2("new");
        fkList.add(newCompPkContainsFk);

        OIntegerPkPeer.setAndSaveCompPkContainsFks(
                oIntegerPk,
                fkList);

        List<CompPkContainsFk> cachedFks = oIntegerPk.getCompPkContainsFks();
        assertEquals(2, cachedFks.size());
        assertEquals(
                oIntegerPk.getId(),
                cachedFks.get(0).getId1());
        assertEquals(
                testData.getCompositePkContainsFkList().get(0).getId2(),
                cachedFks.get(0).getId2());
        assertEquals(
                testData.getCompositePkContainsFkList().get(0).getName(),
                cachedFks.get(0).getName());
        assertEquals(
                oIntegerPk.getId(),
                cachedFks.get(1).getId1());
        assertEquals(
                newCompPkContainsFk.getId2(),
                cachedFks.get(1).getId2());
        assertEquals(
                newCompPkContainsFk.getName(),
                cachedFks.get(1).getName());

        // check database
        List<CompPkContainsFk> expectedInDb
            = new ArrayList<>(cachedFks);
        expectedInDb.add(testData.getCompositePkContainsFkList().get(2));
        ForeignKeySchemaData.assertCompositePkContainsFksInDatabaseEquals(
                expectedInDb);
    }

    /**
     * Tests that the setAndSave method saves an object which was already
     * associated to the referenced object but which value differs
     * from the database value.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSetAndSaveModifiedReferencingObject() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        OIntegerPk oIntegerPk = testData.getOIntegerPkList().get(2);
        List<NullableOIntegerFk> fkList = new ArrayList<>();
        // copy object already associated to this object
        NullableOIntegerFk alreadyAssociated
        = testData.getNullableOIntegerFkList().get(1).copy();
        // id does not get copied, set manually
        alreadyAssociated.setId(
                testData.getNullableOIntegerFkList().get(1).getId());
        alreadyAssociated.setName("modifiedName");
        alreadyAssociated.setNew(true); // saving must also work with wrong new flag
        alreadyAssociated.setModified(false); // saving must work without the modified flag
        fkList.add(alreadyAssociated);

        OIntegerPkPeer.setAndSaveNullableOIntegerFks(
                oIntegerPk,
                fkList);

        List<NullableOIntegerFk> cachedFks
        = oIntegerPk.getNullableOIntegerFks();
        assertEquals(1, cachedFks.size());
        assertEquals(
                testData.getNullableOIntegerFkList().get(1).getId(),
                cachedFks.get(0).getId());
        assertEquals(
                oIntegerPk.getId(),
                cachedFks.get(0).getFk());
        assertEquals(
                "modifiedName",
                cachedFks.get(0).getName());

        // check database
        List<NullableOIntegerFk>expectedInDb
            = new ArrayList<>();
        expectedInDb.addAll(cachedFks);
        expectedInDb.add(testData.getNullableOIntegerFkList().get(0));
        expectedInDb.add(testData.getNullableOIntegerFkList().get(3));
        ForeignKeySchemaData.assertNullableOIntegerFksInDatabaseEquals(
                expectedInDb);
    }

    /**
     * Tests that the referenced object is NOT saved by the setAndSave method.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSetAndSaveModifiedReferencedObject() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        OIntegerPk oIntegerPk = testData.getOIntegerPkList().get(2);
        oIntegerPk.setName("modified");
        List<NullableOIntegerFk> fkList = new ArrayList<>();
        fkList.add(testData.getNullableOIntegerFkList().get(1));

        OIntegerPkPeer.setAndSaveNullableOIntegerFks(
                oIntegerPk,
                fkList);

        assertTrue(oIntegerPk.isModified());
        OIntegerPk oIntegerPkFromDb
        = OIntegerPkPeer.retrieveByPK(oIntegerPk.getId());
        assertEquals("oIntegerPk3", oIntegerPkFromDb.getName());
    }

    /**
     * Tests whether the setAndSave method works for empty lists
     * where a non-composite foreign key links the two objects.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSetAndSaveEmptyListSingleFk() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        // we want to avoid fk errors in MultiRef
        testData.getMultiRefList().clear();
        testData.save();

        OIntegerPk oIntegerPk = testData.getOIntegerPkList().get(2);
        List<NullableOIntegerFk> fkList = new ArrayList<>();

        OIntegerPkPeer.setAndSaveNullableOIntegerFks(
                oIntegerPk,
                fkList);

        List<NullableOIntegerFk> cachedFks
        = oIntegerPk.getNullableOIntegerFks();
        assertEquals(0, cachedFks.size());

        // check database
        List<NullableOIntegerFk> dbFks
        = testData.getNullableOIntegerFkList();
        // remove entries where oIntegerPk is referenced
        dbFks.remove(2);
        dbFks.remove(1);
        ForeignKeySchemaData.assertNullableOIntegerFksInDatabaseEquals(
                dbFks);
    }

    /**
     * Tests whether the setAndSave method works for empty lists
     * where a composite foreign key links the two objects.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSetAndSaveEmptyListCompositeFk() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        CompIntegerVarcharPk compIntegerVarcharPk
        = testData.getCompositeIntegerVarcharPkList().get(2);
        List<CompIntegerVarcharFk> fkList
            = new ArrayList<>();

        CompIntegerVarcharPkPeer.setAndSaveCompIntegerVarcharFks(
                compIntegerVarcharPk,
                fkList);

        List<CompIntegerVarcharFk> cachedFks
        = compIntegerVarcharPk.getCompIntegerVarcharFks();
        assertEquals(0, cachedFks.size());

        // check database
        List<CompIntegerVarcharFk> dbFks
        = testData.getCompositeIntegerVarcharFkList();
        // remove entries where oIntegerPk is referenced
        dbFks.remove(2);
        dbFks.remove(1);
        ForeignKeySchemaData.assertCompositeIntegerVarcharFksInDatabaseEquals(
                dbFks);
    }


}
