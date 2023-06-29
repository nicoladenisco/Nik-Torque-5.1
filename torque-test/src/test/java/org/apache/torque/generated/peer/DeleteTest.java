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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.ForeignKeySchemaData;
import org.apache.torque.PkSchemaData;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.test.dbobject.Nopk;
import org.apache.torque.test.dbobject.NullableOIntegerFk;
import org.apache.torque.test.dbobject.OIntegerPk;
import org.apache.torque.test.peer.NopkPeer;
import org.apache.torque.test.peer.NullableOIntegerFkPeer;
import org.apache.torque.test.peer.OIntegerPkPeer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

/**
 * Tests the delete methods in the generated Peer classes.
 *
 * @version $Id: DeleteTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class DeleteTest extends BaseDatabaseTestCase
{
    private static Log log = LogFactory.getLog(DeleteTest.class);

    /**
     * Tests that the delete(DatabaseObject) method deletes the correct
     * Object and no related objects.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDeleteByObject() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        // we want to avoid fk errors in MultiRef
        testData.getMultiRefList().clear();
        testData.save();

        NullableOIntegerFk toDelete
        = testData.getNullableOIntegerFkList().get(0);
        int preDeleteId = toDelete.getId();

        // check that four entries are in the NullableOIntegerFk table
        List<NullableOIntegerFk> nullableOIntegerFkList
        = getNullableOIntegerFkList();
        assertEquals(4, nullableOIntegerFkList.size());
        assertTrue(nullableOIntegerFkList.contains(toDelete));

        // call delete method and check result.
        int deleted = NullableOIntegerFkPeer.doDelete(toDelete);
        assertEquals(1, deleted);
        assertEquals(preDeleteId, toDelete.getId().intValue());

        // check that there are three entries remaining in the database
        // and the toDelete object was deleted
        nullableOIntegerFkList = getNullableOIntegerFkList();
        assertEquals(3, nullableOIntegerFkList.size());
        assertFalse(nullableOIntegerFkList.contains(toDelete));

        // check that no associated object has been deleted
        List<OIntegerPk> oIntegerPkList
        = OIntegerPkPeer.doSelect(new Criteria());
        assertEquals(3, oIntegerPkList.size());
    }

    /**
     * Tests the delete(DatabaseObject) method if the object to delete
     * does not exist in the database.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDeleteByObjectNoMatch() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        NullableOIntegerFk toDelete
        = testData.getNullableOIntegerFkList().get(0);
        toDelete.setId(toDelete.getId() - 1);
        int preDeleteId = toDelete.getId();

        // check that four entries are in the NullableOIntegerFk table
        List<NullableOIntegerFk> nullableOIntegerFkList
        = getNullableOIntegerFkList();
        assertEquals(4, nullableOIntegerFkList.size());
        assertFalse(nullableOIntegerFkList.contains(toDelete));

        // call delete method and check result.
        int deleted = NullableOIntegerFkPeer.doDelete(toDelete);
        assertEquals(0, deleted);
        assertEquals(preDeleteId, toDelete.getId().intValue());

        // check that all entries remain in the database
        nullableOIntegerFkList = getNullableOIntegerFkList();
        assertEquals(4, nullableOIntegerFkList.size());
    }

    /**
     * Tests that the delete(DatabaseObject) method still deletes an object
     * if the pk matches but other column values are changed.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDeleteByObjectChangedNopkColumn() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        // we want to avoid fk errors in MultiRef
        testData.getMultiRefList().clear();
        testData.save();

        NullableOIntegerFk toDelete
        = testData.getNullableOIntegerFkList().get(0);
        toDelete.setName("nullableOIntegerFk2Changed");
        int preDeleteId = toDelete.getId();

        // check that isDeleted() is false before deletion
        assertFalse(toDelete.isDeleted());

        // check that four entries are in the NullableOIntegerFk table
        List<NullableOIntegerFk> nullableOIntegerFkList
        = getNullableOIntegerFkList();
        assertEquals(4, nullableOIntegerFkList.size());
        assertTrue(nullableOIntegerFkList.contains(toDelete));

        // call delete method and check result.
        int deleted = NullableOIntegerFkPeer.doDelete(toDelete);
        assertEquals(1, deleted);
        assertEquals(preDeleteId, toDelete.getId().intValue());
        assertTrue(toDelete.isDeleted());

        // check that there are three entries remaining in the database
        // and the toDelete object was deleted
        nullableOIntegerFkList = getNullableOIntegerFkList();
        assertEquals(3, nullableOIntegerFkList.size());
        assertFalse(nullableOIntegerFkList.contains(toDelete));
    }

    /**
     * Tests that the delete(DatabaseObject) method also works for objects
     * without primary key (pk).
     *
     * @throws Exception if a database error occurs.
     */
    public void testDeleteByObjectWithoutPk() throws Exception
    {
        PkSchemaData.clearTablesInDatabase();
        PkSchemaData testData = PkSchemaData.getDefaultTestData();
        testData.save();

        Nopk toDelete = testData.getNopkList().get(1);

        // check that isDeleted() is false before deletion
        assertFalse(toDelete.isDeleted());
        // check that three entries are in the Nopk table
        List<Nopk> nopkList = getNopkList();
        assertEquals(3, nopkList.size());
        // check toDelete object is in database
        // equals does not work without pk so check intcol
        assertEquals(2, nopkList.get(1).getIntcol());

        // call delete method and check result.
        int deleted = NopkPeer.doDelete(toDelete);
        assertEquals(1, deleted);
        assertTrue(toDelete.isDeleted());

        // check that there are two entries remaining in the database
        // and the toDelete object was deleted
        // (use intcol for latter as equals does not work)
        nopkList = getNopkList();
        assertEquals(2, nopkList.size());
        assertFalse(2 == nopkList.get(0).getIntcol());
        assertFalse(2 == nopkList.get(1).getIntcol());
    }

    /**
     * Tests that the delete(DatabaseObject) does not delete objects
     * without primary key where a not-binary column has been changed.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDeleteByObjectWithoutPkChangedColumn() throws Exception
    {
        PkSchemaData.clearTablesInDatabase();
        PkSchemaData testData = PkSchemaData.getDefaultTestData();
        testData.save();

        Nopk toDelete = testData.getNopkList().get(1);
        toDelete.setName("nopk1Changed");

        // check that three entries are in the Nopk table
        List<Nopk> nopkList = getNopkList();
        assertEquals(3, nopkList.size());
        // check toDelete object is in database
        // equals does not work without pk so check intcol
        assertEquals(2, nopkList.get(1).getIntcol());

        // call delete method and check result.
        int deleted = NopkPeer.doDelete(toDelete);
        assertEquals(0, deleted);
        // flag is true even if nothing was deleted
        assertTrue(toDelete.isDeleted());

        // check that there are all entries remaining in the database
        // and the toDelete object is still there
        // (use intcol for latter as equals does not work)
        nopkList = getNopkList();
        assertEquals(3, nopkList.size());
        assertEquals(2, nopkList.get(1).getIntcol());
    }

    /**
     * Tests that the delete(Collection&lt;DatabaseObject&gt;) method deletes
     * the correct Objects and no related objects.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDeleteByObjectCollection() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        // we want to avoid fk errors in MultiRef
        testData.getMultiRefList().clear();
        testData.save();

        List<NullableOIntegerFk> toDelete
            = new ArrayList<>();
        toDelete.add(testData.getNullableOIntegerFkList().get(0));
        toDelete.add(testData.getNullableOIntegerFkList().get(2));
        List<Integer> preDeleteIds = new ArrayList<>();
        preDeleteIds.add(toDelete.get(0).getId());
        preDeleteIds.add(toDelete.get(1).getId());

        // check that isDeleted() is false before deletion
        assertFalse(toDelete.get(0).isDeleted());
        assertFalse(toDelete.get(1).isDeleted());
        // check that four entries are in the NullableOIntegerFk table
        List<NullableOIntegerFk> nullableOIntegerFkList
        = getNullableOIntegerFkList();
        assertEquals(4, nullableOIntegerFkList.size());
        assertTrue(nullableOIntegerFkList.contains(toDelete.get(0)));
        assertTrue(nullableOIntegerFkList.contains(toDelete.get(1)));

        // call delete method and check that two objects have been deleted.
        // and that the ids of the delete objects did not change
        int deleted = NullableOIntegerFkPeer.doDelete(toDelete);
        assertEquals(2, deleted);
        assertEquals(preDeleteIds.get(0), toDelete.get(0).getId());
        assertEquals(preDeleteIds.get(1), toDelete.get(1).getId());
        assertTrue(toDelete.get(0).isDeleted());
        assertTrue(toDelete.get(1).isDeleted());

        // check that there are two entries remaining in the database
        // and the objects contained in toDelete object were deleted
        nullableOIntegerFkList = getNullableOIntegerFkList();
        assertEquals(2, nullableOIntegerFkList.size());
        assertFalse(nullableOIntegerFkList.contains(toDelete.get(0)));
        assertFalse(nullableOIntegerFkList.contains(toDelete.get(1)));

        // check that no associated object has been deleted
        List<OIntegerPk> oIntegerPkList
        = OIntegerPkPeer.doSelect(new Criteria());
        assertEquals(3, oIntegerPkList.size());
    }

    /**
     * Tests that the delete(Collection&lt;DatabaseObject&gt;) method deletes
     * no object if a object to delete does not exist in the database.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDeleteByObjectCollectionNoMatch() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        List<NullableOIntegerFk> toDelete
            = new ArrayList<>();
        toDelete.add(testData.getNullableOIntegerFkList().get(0));
        toDelete.add(testData.getNullableOIntegerFkList().get(2));
        toDelete.get(0).setId(toDelete.get(0).getId() - 1);
        int preDeleteId = toDelete.get(0).getId();

        // check that isDeleted() is false before deletion
        assertFalse(toDelete.get(0).isDeleted());
        assertFalse(toDelete.get(1).isDeleted());
        // check that four entries are in the NullableOIntegerFk table
        // prior to deletion
        List<NullableOIntegerFk> nullableOIntegerFkList
        = getNullableOIntegerFkList();
        assertEquals(4, nullableOIntegerFkList.size());
        assertFalse(nullableOIntegerFkList.contains(toDelete.get(0)));
        assertTrue(nullableOIntegerFkList.contains(toDelete.get(1)));

        // call delete method and check result.
        int deleted = NullableOIntegerFkPeer.doDelete(toDelete);
        assertEquals(1, deleted);
        assertEquals(preDeleteId, toDelete.get(0).getId().intValue());
        // flag is true even if the object was not deleted
        assertTrue(toDelete.get(0).isDeleted());
        assertTrue(toDelete.get(1).isDeleted());

        // check that three entries remain in the database
        nullableOIntegerFkList = getNullableOIntegerFkList();
        assertEquals(3, nullableOIntegerFkList.size());
        assertFalse(nullableOIntegerFkList.contains(toDelete.get(1)));
    }

    /**
     * Tests the delete(ObjectKey) method.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDeleteByPrimaryKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        // we want to avoid fk errors in MultiRef
        testData.getMultiRefList().clear();
        testData.save();

        NullableOIntegerFk toDelete
        = testData.getNullableOIntegerFkList().get(0);
        int preDeleteId = toDelete.getId();

        // check that four entries are in the NullableOIntegerFk table
        List<NullableOIntegerFk> nullableOIntegerFkList
        = getNullableOIntegerFkList();
        assertEquals(4, nullableOIntegerFkList.size());
        assertTrue(nullableOIntegerFkList.contains(toDelete));

        // calculate and check primary key
        ObjectKey<?> primaryKey = toDelete.getPrimaryKey();
        assertTrue(primaryKey instanceof NumberKey);
        assertEquals(new BigDecimal(preDeleteId), primaryKey.getValue());

        // call delete method and check result.
        int deleted = NullableOIntegerFkPeer.doDelete(primaryKey);
        assertEquals(1, deleted);
        assertEquals(preDeleteId, toDelete.getId().intValue());

        // check that there are three entries remaining in the database
        // and the toDelete object was deleted
        nullableOIntegerFkList = getNullableOIntegerFkList();
        assertEquals(3, nullableOIntegerFkList.size());
        assertFalse(nullableOIntegerFkList.contains(toDelete));

        // check that no associated object has been deleted
        List<OIntegerPk> oIntegerPkList
        = OIntegerPkPeer.doSelect(new Criteria());
        assertEquals(3, oIntegerPkList.size());

    }

    /**
     * Tests the delete(ObjectKey) method if the object to delete
     * does not exist in the database.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDeleteByPrimaryKeyNoMatch() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        NullableOIntegerFk toDelete
        = testData.getNullableOIntegerFkList().get(0);
        toDelete.setId(toDelete.getId() - 1);
        int preDeleteId = toDelete.getId();

        // check that four entries are in the NullableOIntegerFk table
        List<NullableOIntegerFk> nullableOIntegerFkList
        = getNullableOIntegerFkList();
        assertEquals(4, nullableOIntegerFkList.size());
        assertFalse(nullableOIntegerFkList.contains(toDelete));

        // calculate and check primary key
        ObjectKey<?> primaryKey = toDelete.getPrimaryKey();
        assertTrue(primaryKey instanceof NumberKey);
        assertEquals(new BigDecimal(preDeleteId), primaryKey.getValue());

        // call delete method and check result.
        int deleted = NullableOIntegerFkPeer.doDelete(primaryKey);
        assertEquals(0, deleted);
        assertEquals(preDeleteId, toDelete.getId().intValue());

        // check that all entries remain in the database
        nullableOIntegerFkList = getNullableOIntegerFkList();
        assertEquals(4, nullableOIntegerFkList.size());
    }

    /**
     * Checks that rows can be deleted by a Criteria.
     *
     * @throws Exception if a database error occurs
     */
    public void testDeleteByCriteria() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        // we want to avoid fk errors in MultiRef
        testData.getMultiRefList().clear();
        testData.save();

        // call delete method
        Criteria criteria = new Criteria();
        criteria.and(
                NullableOIntegerFkPeer.ID,
                testData.getNullableOIntegerFkList().get(1).getId(),
                Criteria.LESS_EQUAL);
        int deletedCount = NullableOIntegerFkPeer.doDelete(criteria);
        assertEquals(2, deletedCount);

        // check that only the last two entries remains
        // in the NullableOIntegerFk table
        List<NullableOIntegerFk> nullableOIntegerFkList
        = getNullableOIntegerFkList();
        assertEquals(2, nullableOIntegerFkList.size());
        assertEquals(
                testData.getNullableOIntegerFkList().get(2).getId(),
                nullableOIntegerFkList.get(0).getId());

        // check that no associated object has been deleted
        List<OIntegerPk> oIntegerPkList
        = OIntegerPkPeer.doSelect(new Criteria());
        assertEquals(3, oIntegerPkList.size());
    }

    /**
     * Checks that a non-matching Criteria does not delete any rows.
     *
     * @throws Exception if a database error occurs
     */
    public void testDeleteByCriteriaNoMatch() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        // call delete method
        Criteria criteria = new Criteria();
        criteria.and(NullableOIntegerFkPeer.NAME, "noMatch");
        int deletedCount = NullableOIntegerFkPeer.doDelete(criteria);
        assertEquals(0, deletedCount);

        // check that three entries remain in the NullableOIntegerFk table
        List<NullableOIntegerFk> nullableOIntegerFkList
        = getNullableOIntegerFkList();
        assertEquals(4, nullableOIntegerFkList.size());
    }

    /**
     * Checks that delete fails if a column from another table is added.
     * See TORQUE-113
     *
     * @throws Exception if a database error occurs
     */
    public void testDeleteWithOtherTableColumn() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        // call delete method
        Criteria criteria = new Criteria();
        criteria.and(
                OIntegerPkPeer.ID,
                testData.getOIntegerPkList().get(0).getId());
        try
        {
            NullableOIntegerFkPeer.doDelete(criteria);
            fail("Exception should be thrown");
        }
        catch (TorqueException e)
        {
            // expected
        }
    }

    /**
     * Checks that delete by criteria using a join works correctly.
     * This test accepts two results: Either an exception is thrown
     * (i.e the database does not support join clauses in delete statements)
     * or deletion works correctly.
     *
     * @throws Exception if a database error occurs
     */
    public void testDeleteByCriteriaWithJoins() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        ForeignKeySchemaData testData
        = ForeignKeySchemaData.getDefaultTestData();
        testData.save();

        // call delete method
        Criteria criteria = new Criteria();
        criteria.addJoin(
                NullableOIntegerFkPeer.FK,
                OIntegerPkPeer.ID,
                Criteria.INNER_JOIN);
        criteria.and(
                OIntegerPkPeer.ID,
                testData.getOIntegerPkList().get(0).getId());
        try
        {
            int deletedCount = NullableOIntegerFkPeer.doDelete(criteria);
            assertEquals(1, deletedCount);
        }
        catch (TorqueException e)
        {
            log.debug("Delete by joins does not work for this database.");
            return;
        }

        // check that the last two entries remains in the NullableOIntegerFk
        // table
        List<NullableOIntegerFk> nullableOIntegerFkList
        = getNullableOIntegerFkList();
        assertEquals(2, nullableOIntegerFkList.size());
        assertTrue(nullableOIntegerFkList.contains(
                testData.getNullableOIntegerFkList().get(1)));
        assertTrue(nullableOIntegerFkList.contains(
                testData.getNullableOIntegerFkList().get(2)));

        // check that no associated object has been deleted
        List<OIntegerPk> oIntegerPkList
        = OIntegerPkPeer.doSelect(new Criteria());
        assertEquals(3, oIntegerPkList.size());
    }

    /**
     * Checks that delete by criteria throws an exception if a non-matching
     * condition is added.
     *
     * @throws Exception if a database error occurs
     */
    public void testDeleteByCriteriaWithWrongTable() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.and(OIntegerPkPeer.ID, 1);
        try
        {
            NullableOIntegerFkPeer.doDelete(criteria);
            fail("Database error expected");
        }
        catch (TorqueException e)
        {
            // expected
            return;
        }
    }

    /**
     * Reads all NullableOIntegerFk rows from the database.
     *
     * @return the NullableOIntegerFk rows
     *
     * @throws TorqueException if reading fails.
     */
    // TODO to ForeignKeyTestData
    private List<NullableOIntegerFk> getNullableOIntegerFkList()
            throws TorqueException
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(NullableOIntegerFkPeer.ID);
        List<NullableOIntegerFk> result
        = NullableOIntegerFkPeer.doSelect(criteria);
        return result;
    }

    /**
     * Reads all Nopk rows from the database.
     *
     * @return the Nopk rows
     *
     * @throws TorqueException if reading fails.
     */
    private List<Nopk> getNopkList() throws TorqueException
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(NopkPeer.INTCOL);
        List<Nopk> result = NopkPeer.doSelect(criteria);
        return result;
    }
}
