package org.apache.torque;

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

import junit.framework.TestCase;

import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.OIntegerPk;
import org.apache.torque.test.dbobject.PIntegerPk;
import org.apache.torque.test.peer.OIntegerPkPeer;
import org.apache.torque.test.peer.PIntegerPkPeer;

/**
 * Tests initialization and shutdown of Torque.
 *
 * @version $Id: InitShutdownTest.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class InitShutdownTest extends TestCase
{
    public void testInitFromScratch() throws TorqueException
    {
        // Torque can already be initialized if the test is run in eclipse
        // and other tests have run before. This is not the ideal case
        // but we consider it anyway
        if (Torque.isInit())
        {
            Torque.shutdown();
        }

        // make sure PIntegerPkPeer class is loaded
        Column dummy = PIntegerPkPeer.ID;

        initTorque();

        assertNotNull(
                Torque.getDatabaseMap().getTable(PIntegerPkPeer.TABLE_NAME));

        checkTorqueInitialisationWithPIntegerPk();
        checkTorqueInitialisationWithOIntegerPk();
    }

    /**
     * Tests whether shutdown works correctly and whether reinitialisation
     * is possible after shutdown.
     *
     * @throws TorqueException if shutdown does not exit cleanly.
     */
    public void testShutdown() throws Exception
    {
        // Torque should be initialized from previous test so this is not
        // strictly needed
        if (!Torque.isInit())
        {
            initTorque();
        }

        checkTorqueInitialisationWithOIntegerPk();
        assertNotNull(
                Torque.getDatabaseMap().getTable(OIntegerPkPeer.TABLE_NAME));
        Torque.shutdown();

        try
        {
            Torque.getDatabase(Torque.getDefaultDB());
            fail("database access should not be possible "
                    + "when Torque is shutdown()");
        }
        catch (TorqueException e)
        {
        }
        assertNotNull(
                Torque.getDatabaseMap().getTable(OIntegerPkPeer.TABLE_NAME));


        initTorque();
        checkTorqueInitialisationWithOIntegerPk();
        assertNotNull(
                Torque.getDatabaseMap().getTable(OIntegerPkPeer.TABLE_NAME));
    }

    private void initTorque()  throws TorqueException
    {
        Torque.init(System.getProperty(
                BaseDatabaseTestCase.CONFIG_FILE_SYSTEM_PROPERTY));
    }

    private void checkTorqueInitialisationWithOIntegerPk() throws TorqueException
    {
        ForeignKeySchemaData.clearTablesInDatabase();

        OIntegerPk oIntegerPk = new OIntegerPk();
        oIntegerPk.setName("shutdownName");
        oIntegerPk.save();
        List<OIntegerPk> oIntegerPkList
        = OIntegerPkPeer.doSelect(new Criteria());
        assertEquals(
                "List should contain one OIntegerPk",
                1,
                oIntegerPkList.size());
        oIntegerPk = oIntegerPkList.get(0);
        assertEquals("OIntegerPk name should be shutdownName",
                "shutdownName",
                oIntegerPk.getName());
    }

    private void checkTorqueInitialisationWithPIntegerPk() throws TorqueException
    {
        ForeignKeySchemaData.clearTablesInDatabase();
        PIntegerPk pIntegerPk = new PIntegerPk();
        pIntegerPk.setName("shutdownName");
        pIntegerPk.save();
        List<PIntegerPk> pIntegerPkList
        = PIntegerPkPeer.doSelect(new Criteria());
        assertEquals(
                "List should contain one PIntegerPk",
                1,
                pIntegerPkList.size());
        pIntegerPk = pIntegerPkList.get(0);
        assertEquals("PIntegerPk name should be shutdownName",
                "shutdownName",
                pIntegerPk.getName());
    }
}
