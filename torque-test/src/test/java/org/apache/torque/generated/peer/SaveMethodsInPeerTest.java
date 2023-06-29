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
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.SaveMethodInObjectFk;
import org.apache.torque.test.dbobject.SaveMethodInPeer;
import org.apache.torque.test.dbobject.SaveMethodInPeerFk;
import org.apache.torque.test.peer.SaveMethodInObjectFkPeer;
import org.apache.torque.test.peer.SaveMethodInPeerFkPeer;
import org.apache.torque.test.peer.SaveMethodInPeerPeer;
import org.apache.torque.util.CountHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Tests whether the save methods work in the peer classes.
 *
 * @version $Id: SaveMethodsInPeerTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class SaveMethodsInPeerTest extends BaseDatabaseTestCase
{
    /**
     * Tests the save method for a simple object.
     *
     * @throws Exception if a database error occurs.
     */
    public void testInsert() throws Exception
    {
        // prepare
        SaveMethodInPeerFkPeer.doDelete(new Criteria());
        SaveMethodInObjectFkPeer.doDelete(new Criteria());
        SaveMethodInPeerPeer.doDelete(new Criteria());

        SaveMethodInPeer toSave = new SaveMethodInPeer();
        toSave.setPayload("payload 1");

        // execute
        SaveMethodInPeerPeer.save(toSave);

        // verify
        assertNotNull(toSave.getId());
        assertEquals("payload 1", toSave.getPayload());

        Criteria criteria = new Criteria()
                .where(SaveMethodInPeerPeer.ID, toSave.getId())
                .and(SaveMethodInPeerPeer.PAYLOAD, "payload 1");
        List<SaveMethodInPeer> loadedList
        = SaveMethodInPeerPeer.doSelect(criteria);
        assertEquals(1, loadedList.size());

        assertEquals(
                1,
                new CountHelper().count(SaveMethodInPeerPeer.getTableMap()));
    }

    /**
     * Tests that the save method propagates "down" for a foreign key,
     * i.e. if save is called on an object which contains another
     * referencing object, the referencing object must also be saved.
     *
     * @throws Exception if a database error occurs.
     */
    public void testPropagationDown() throws Exception
    {
        // prepare
        SaveMethodInPeerFkPeer.doDelete(new Criteria());
        SaveMethodInObjectFkPeer.doDelete(new Criteria());
        SaveMethodInPeerPeer.doDelete(new Criteria());

        SaveMethodInPeer toSave = new SaveMethodInPeer();
        toSave.setPayload("payload");
        SaveMethodInPeerFk child1 = new SaveMethodInPeerFk();
        toSave.addSaveMethodInPeerFk(child1);
        child1.setPayload("payload 1");
        SaveMethodInObjectFk child2 = new SaveMethodInObjectFk();
        toSave.addSaveMethodInObjectFk(child2);
        child2.setPayload("payload 2");

        // execute
        SaveMethodInPeerPeer.save(toSave);

        // verify
        assertNotNull(toSave.getId());
        assertEquals("payload", toSave.getPayload());
        assertNotNull(child1.getId());
        assertEquals("payload 1", child1.getPayload());
        assertNotNull(child2.getId());
        assertEquals("payload 2", child2.getPayload());

        Criteria criteria = new Criteria()
                .where(SaveMethodInPeerPeer.ID, toSave.getId())
                .and(SaveMethodInPeerPeer.PAYLOAD, "payload");
        List<SaveMethodInPeer> loadedList
        = SaveMethodInPeerPeer.doSelect(criteria);
        assertEquals(1, loadedList.size());
        assertEquals(
                1,
                new CountHelper().count(SaveMethodInPeerPeer.getTableMap()));

        criteria = new Criteria()
                .where(SaveMethodInPeerFkPeer.SAVE_METHOD_IN_PEER_ID, toSave.getId())
                .and(SaveMethodInPeerFkPeer.ID, child1.getId())
                .and(SaveMethodInPeerFkPeer.PAYLOAD, "payload 1");
        List<SaveMethodInPeerFk> child1List
        = SaveMethodInPeerFkPeer.doSelect(criteria);
        assertEquals(1, child1List.size());
        assertEquals(
                1,
                new CountHelper().count(SaveMethodInPeerFkPeer.getTableMap()));

        criteria = new Criteria()
                .where(SaveMethodInObjectFkPeer.SAVE_METHOD_IN_PEER_ID, toSave.getId())
                .and(SaveMethodInObjectFkPeer.ID, child2.getId())
                .and(SaveMethodInObjectFkPeer.PAYLOAD, "payload 2");
        List<SaveMethodInObjectFk> child2List
        = SaveMethodInObjectFkPeer.doSelect(criteria);
        assertEquals(1, child2List.size());
        assertEquals(
                1,
                new CountHelper().count(
                        SaveMethodInObjectFkPeer.getTableMap()));
    }
}
