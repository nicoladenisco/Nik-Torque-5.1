package org.apache.torque.generated.dataobject;

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
import org.apache.torque.Torque;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.MultiRef;
import org.apache.torque.test.dbobject.MultiRefSameTable;
import org.apache.torque.test.dbobject.OIntegerPk;
import org.apache.torque.test.peer.MultiRefPeer;
import org.apache.torque.test.peer.MultiRefSameTablePeer;
import org.apache.torque.test.peer.OIntegerPkPeer;
import org.apache.torque.test.peer.PIntegerPkPeer;
import org.junit.jupiter.api.BeforeEach;
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
 * Tests the fjoin getter methods for related objects in the generated
 * data object classes.
 *
 * @version $Id: GetRelatedObjectsJoinTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class GetRelatedObjectsJoinTest extends BaseDatabaseTestCase
{
    ForeignKeySchemaData testData;

    @BeforeEach
    public void setUp() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        testData = ForeignKeySchemaData.getDefaultTestData();
        testData.save();
    }

    /**
     * Checks that the test principle for switching silent db fetch works.
     * This test does not check the functionality to test, but rather ensures
     * that we can switch of silent db fetching to check whether objects
     * are already loaded or not.
     */
    @Test
    public void testSwitchSilentFetchingOff() throws Exception
    {
        OIntegerPk baseObject = testData.getOIntegerPkList().get(0);
        baseObject = OIntegerPkPeer.doSelectSingleRecord(baseObject);
        List<MultiRef> multiRefs
        = baseObject.getMultiRefs();
        // switch silent fetching off
        Adapter adapter = Torque.getOrCreateDatabase(Torque.getDefaultDB()).getAdapter();
        try
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(null);
            try
            {
                for (MultiRef multiRef : multiRefs)
                {
                    multiRef.getPIntegerPk();
                }
                fail("Exception excpected");
            }
            catch (Exception e)
            {
                // expected
            }
        }
        finally
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(adapter);
        }
    }

    /**
     * Checks that the joinGetter works if different tables are joined.
     *
     * @throws Exception if the database fails
     */
    @Test
    public void testJoinGetter() throws Exception
    {
        OIntegerPk baseObject = testData.getOIntegerPkList().get(0);
        baseObject = OIntegerPkPeer.doSelectSingleRecord(baseObject);
        List<MultiRef> multiRefs
        = baseObject.getMultiRefsJoinPIntegerPk(
                new Criteria().addAscendingOrderByColumn(MultiRefPeer.ID));

        // verify
        assertEquals(3, multiRefs.size());
        assertEquals("multiRef111a", multiRefs.get(0).getName());
        assertEquals("multiRef111b", multiRefs.get(1).getName());
        assertEquals("multiRef120", multiRefs.get(2).getName());
        // switch silent fetching off
        Adapter adapter = Torque.getOrCreateDatabase(Torque.getDefaultDB()).getAdapter();
        try
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(null);
            // check that the join has worked
            assertEquals(
                    testData.getPIntegerPkList().get(0),
                    multiRefs.get(0).getPIntegerPk());
            assertEquals(
                    testData.getPIntegerPkList().get(0),
                    multiRefs.get(1).getPIntegerPk());
            assertEquals(
                    testData.getPIntegerPkList().get(1),
                    multiRefs.get(2).getPIntegerPk());
            // check that the back reference to the base object
            // is set in the joined objects
            assertEquals(baseObject, multiRefs.get(0).getOIntegerPk());
            assertEquals(baseObject, multiRefs.get(1).getOIntegerPk());
            assertEquals(baseObject, multiRefs.get(2).getOIntegerPk());
            assertFalse(multiRefs.get(0).isModified());
            assertFalse(multiRefs.get(1).isModified());
            assertFalse(multiRefs.get(2).isModified());
        }
        finally
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(adapter);
        }
    }

    /**
     * Checks that the joinGetter works if different tables are joined.
     *
     * @throws Exception if the database fails
     */
    @Test
    public void testJoinGetterAfterInit() throws Exception
    {
        OIntegerPk baseObject = testData.getOIntegerPkList().get(0);
        baseObject = OIntegerPkPeer.doSelectSingleRecord(baseObject);
        baseObject.initMultiRefs();
        List<MultiRef> multiRefs
        = baseObject.getMultiRefsJoinPIntegerPk(
                new Criteria().addAscendingOrderByColumn(MultiRefPeer.ID));

        // verify
        assertEquals(3, multiRefs.size());
        assertEquals("multiRef111a", multiRefs.get(0).getName());
        assertEquals("multiRef111b", multiRefs.get(1).getName());
        assertEquals("multiRef120", multiRefs.get(2).getName());
        // switch silent fetching off
        Adapter adapter = Torque.getOrCreateDatabase(Torque.getDefaultDB()).getAdapter();
        try
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(null);
            // check that the join has worked
            assertEquals(
                    testData.getPIntegerPkList().get(0),
                    multiRefs.get(0).getPIntegerPk());
            assertEquals(
                    testData.getPIntegerPkList().get(0),
                    multiRefs.get(1).getPIntegerPk());
            assertEquals(
                    testData.getPIntegerPkList().get(1),
                    multiRefs.get(2).getPIntegerPk());
            // check that the back reference to the base object
            // is set in the joined objects
            assertEquals(baseObject, multiRefs.get(0).getOIntegerPk());
            assertEquals(baseObject, multiRefs.get(1).getOIntegerPk());
            assertEquals(baseObject, multiRefs.get(2).getOIntegerPk());
            assertFalse(multiRefs.get(0).isModified());
            assertFalse(multiRefs.get(1).isModified());
            assertFalse(multiRefs.get(2).isModified());
        }
        finally
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(adapter);
        }
    }

    /**
     * Checks that the joinGetter works if different tables are joined
     * and the collection was already loaded with another criteria.
     *
     * @throws Exception if the database fails
     */
    @Test
    public void testJoinGetterWithOtherCriteria() throws Exception
    {
        OIntegerPk baseObject = testData.getOIntegerPkList().get(0);
        baseObject = OIntegerPkPeer.doSelectSingleRecord(baseObject);
        baseObject.getMultiRefsJoinPIntegerPk(
                new Criteria().addAscendingOrderByColumn(MultiRefPeer.ID));
        List<MultiRef> multiRefs
        = baseObject.getMultiRefsJoinPIntegerPk(
                new Criteria().addAscendingOrderByColumn(MultiRefPeer.ID)
                .where(PIntegerPkPeer.ID,
                        testData.getPIntegerPkList().get(0).getId()));

        // verify
        assertEquals(2, multiRefs.size());
        assertEquals("multiRef111a", multiRefs.get(0).getName());
        assertEquals("multiRef111b", multiRefs.get(1).getName());
        // switch silent fetching off
        Adapter adapter = Torque.getOrCreateDatabase(Torque.getDefaultDB()).getAdapter();
        try
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(null);
            // check that the join has worked
            assertEquals(
                    testData.getPIntegerPkList().get(0),
                    multiRefs.get(0).getPIntegerPk());
            assertEquals(
                    testData.getPIntegerPkList().get(0),
                    multiRefs.get(1).getPIntegerPk());
            // check that the back reference to the base object
            // is set in the joined objects
            assertEquals(baseObject, multiRefs.get(0).getOIntegerPk());
            assertEquals(baseObject, multiRefs.get(1).getOIntegerPk());
            assertFalse(multiRefs.get(0).isModified());
            assertFalse(multiRefs.get(1).isModified());
        }
        finally
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(adapter);
        }
    }

    /**
     * Checks that the joinGetter works if same tables are joined.
     *
     * @throws Exception if the database fails
     */
    @Test
    public void testJoinGetterSameObject() throws Exception
    {
        OIntegerPk baseObject = testData.getOIntegerPkList().get(0);
        baseObject = OIntegerPkPeer.doSelectSingleRecord(baseObject);
        List<MultiRefSameTable> multiRefSameTables
        = baseObject.getMultiRefSameTableRelatedByReference1sJoinOIntegerPkRelatedByReference2(
                new Criteria().addAscendingOrderByColumn(MultiRefSameTablePeer.ID));
        assertEquals(3, multiRefSameTables.size());
        assertEquals("multiRefSameTable111a", multiRefSameTables.get(0).getName());
        assertEquals("multiRefSameTable111b", multiRefSameTables.get(1).getName());
        assertEquals("multiRefSameTable120", multiRefSameTables.get(2).getName());
        // switch silent fetching off
        Adapter adapter = Torque.getOrCreateDatabase(Torque.getDefaultDB()).getAdapter();
        try
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(null);
            assertEquals(
                    testData.getOIntegerPkList().get(0),
                    multiRefSameTables.get(0).getOIntegerPkRelatedByReference2());
            assertEquals(
                    testData.getOIntegerPkList().get(0),
                    multiRefSameTables.get(1).getOIntegerPkRelatedByReference2());
            assertEquals(
                    testData.getOIntegerPkList().get(1),
                    multiRefSameTables.get(2).getOIntegerPkRelatedByReference2());
            // check that the back reference to the base object
            // is set in the joined objects
            assertEquals(
                    baseObject,
                    multiRefSameTables.get(0).getOIntegerPkRelatedByReference1());
            assertEquals(
                    baseObject,
                    multiRefSameTables.get(1).getOIntegerPkRelatedByReference1());
            assertEquals(
                    baseObject,
                    multiRefSameTables.get(2).getOIntegerPkRelatedByReference1());
            // check that adding the backreference did not lead to changed objects
            assertFalse(multiRefSameTables.get(0).isModified());
            assertFalse(multiRefSameTables.get(1).isModified());
            assertFalse(multiRefSameTables.get(2).isModified());
        }
        finally
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(adapter);
        }
    }
}
