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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.OptimisticLockingFailedException;
import org.apache.torque.OptimisticLockingInterface;
import org.apache.torque.OptimisticLockingPeerInterface;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests whether optimistic locking works.
 *
 * @version $Id: BaseOptimisticLockingTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public abstract class BaseOptimisticLockingTest<T extends OptimisticLockingInterface> extends BaseDatabaseTestCase
{
    OptimisticLockingPeerInterface<T> peer;

    public BaseOptimisticLockingTest(OptimisticLockingPeerInterface<T> peer)
    {
        this.peer = peer;
    }

    /**
     * Tests whether we can update an optimistically locked table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testSave() throws Exception
    {
        // prepare
        List<T> objectList = fillDatabase();

        T toUpdate = objectList.get(1);
        assertEquals(new Integer(0), toUpdate.getVersion());

        toUpdate.setName("2a");

        // execute
        toUpdate.save();

        // verify
        assertEquals(new Integer(1), toUpdate.getVersion());
        assertEquals("2a", toUpdate.getName());
        assertDatabase(objectList);
    }

    /**
     * Tests whether we can save an optimistically locked table
     * with a connection.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testSaveWithConnection() throws Exception
    {
        // prepare
        List<T> objectList = fillDatabase();

        T toUpdate = objectList.get(1);
        assertEquals(new Integer(0), toUpdate.getVersion());

        toUpdate.setName("2a");
        Connection connection = Torque.getConnection();

        // execute
        toUpdate.save(connection);

        // cleanup
        connection.close();

        // verify
        assertEquals(new Integer(1), toUpdate.getVersion());
        assertEquals("2a", toUpdate.getName());
        assertDatabase(objectList);
    }

    /**
     * Tests whether we can update an optimistically locked table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testUpdate() throws Exception
    {
        // prepare
        List<T> objectList = fillDatabase();

        T toUpdate = objectList.get(1);
        assertEquals(new Integer(0), toUpdate.getVersion());

        toUpdate.setName("2a");

        // execute
        peer.doUpdate(toUpdate);

        // verify
        assertEquals(new Integer(1), toUpdate.getVersion());
        assertEquals("2a", toUpdate.getName());
        assertDatabase(objectList);
    }

    /**
     * Tests whether we can update an optimistically locked table
     * with passing a connection.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testUpdateWithConnection() throws Exception
    {
        // prepare
        List<T> objectList = fillDatabase();

        T toUpdate = objectList.get(1);
        assertEquals(new Integer(0), toUpdate.getVersion());

        toUpdate.setName("2a");
        Connection connection = Torque.getConnection();

        // execute
        peer.doUpdate(toUpdate, connection);

        // cleanup
        connection.close();

        // verify
        assertEquals(new Integer(1), toUpdate.getVersion());
        assertEquals("2a", toUpdate.getName());
        assertDatabase(objectList);
    }

    /**
     * Tests whether we can save an optimistically locked table twice.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testSaveTwice() throws Exception
    {
        // prepare
        List<T> objectList = fillDatabase();

        T toUpdate = objectList.get(1);
        assertEquals(new Integer(0), toUpdate.getVersion());

        toUpdate.setName("2a");
        toUpdate.save();
        assertEquals(new Integer(1), toUpdate.getVersion());

        toUpdate.setName("2b");

        // execute
        toUpdate.save();

        // verify
        assertEquals(new Integer(2), toUpdate.getVersion());
        assertEquals("2b", toUpdate.getName());
        assertDatabase(objectList);
    }

    /**
     * Tests whether optimistic locking fails if the row has been updated
     * by another access.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testOptimisticLockFailsOtherUpdate() throws Exception
    {
        // prepare
        List<T> objectList = fillDatabase();

        T concurrentUpdated = objectList.get(1);
        T toUpdate = peer.doSelectSingleRecord(concurrentUpdated);

        concurrentUpdated.setName("2a");
        concurrentUpdated.save();

        toUpdate.setName("2b");

        // execute
        try
        {
            toUpdate.save();
            fail("Exception excpected");
        }
        catch (OptimisticLockingFailedException e)
        {
            assertOptimisticLockingUpdateException(e);
        }

        // verify
        assertEquals(new Integer(0), toUpdate.getVersion());
        assertEquals("2b", toUpdate.getName());
        assertTrue(toUpdate.isModified());
        assertDatabase(objectList);
    }

    /**
     * Tests whether optimistic locking fails if the row has been deleted
     * by another access.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testOptimisticLockFailsOtherDelete() throws Exception
    {
        // prepare
        List<T> objectList = fillDatabase();

        T concurrentDeleted = objectList.remove(1);
        T toUpdate = peer.doSelectSingleRecord(concurrentDeleted);

        peer.doDelete(concurrentDeleted);

        toUpdate.setName("2b");

        // execute
        try
        {
            toUpdate.save();
            fail("Exception expected");
        }
        catch (TorqueException e)
        {
            assertOptimisticLockingDeleteException(e);
        }

        // verify
        assertEquals(new Integer(0), toUpdate.getVersion());
        assertEquals("2b", toUpdate.getName());
        assertTrue(toUpdate.isModified());
        assertDatabase(objectList);
    }

    private List<T> fillDatabase() throws TorqueException
    {
        List<T> result = new ArrayList<>();
        peer.doDelete(new Criteria());
        T optimisticLocking = newObject();
        optimisticLocking.setName("1");
        optimisticLocking.save();
        result.add(optimisticLocking);
        optimisticLocking = newObject();
        optimisticLocking.setName("2");
        optimisticLocking.save();
        result.add(optimisticLocking);
        optimisticLocking = newObject();
        optimisticLocking.setName("3");
        optimisticLocking.save();
        result.add(optimisticLocking);
        return result;
    }

    private void assertDatabase(List<T> expected)
            throws TorqueException
    {
        assertEquals(
                expected.size(),
                peer.doSelect(new Criteria()).size());
        for (T expectedElement : expected)
        {
            T actual = peer.retrieveByPK(expectedElement.getPrimaryKey());
            assertEquals(expectedElement.getName(), actual.getName());
            assertEquals(expectedElement.getVersion(), actual.getVersion());
        }
    }

    public abstract T newObject();

    public abstract void assertOptimisticLockingUpdateException(
            TorqueException e);

    public abstract void assertOptimisticLockingDeleteException(
            TorqueException e);
}
