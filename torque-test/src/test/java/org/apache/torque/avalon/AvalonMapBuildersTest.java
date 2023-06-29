package org.apache.torque.avalon;
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

import org.apache.avalon.framework.component.ComponentException;
import org.apache.fulcrum.testcontainer.BaseUnit5Test;

import org.apache.torque.TorqueException;
import org.apache.torque.junit5.extension.HostCallback;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.test.peer.AuthorPeer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing of the correct MapBuilder handling of the Torque Avalon Component
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: AvalonMapBuildersTest.java 1870877 2019-12-05 14:03:08Z gk $
 */
@HostCallback
public class AvalonMapBuildersTest extends BaseUnit5Test
{
    private Torque torque = null;
    private org.apache.torque.TorqueInstance instance = null;

    /**
     * Constructor for test.
     *
     */
    public AvalonMapBuildersTest()
    {

        // trigger static constructor before Torque initialization
        AuthorPeer.getAuthorPeerImpl();

        // store old instance for comparison
        instance = org.apache.torque.Torque.getInstance();
    }

    @BeforeEach
    public void setUp() throws Exception
    {
//        if (!org.apache.torque.Torque.isInit())
//        {
//            org.apache.torque.Torque.init(System.getProperty(
//                    BaseDatabaseTestCase.CONFIG_FILE_SYSTEM_PROPERTY));
//        }
        setConfigurationFileName("src/test/resources/TestComponentConfig.xml");
        setRoleFileName("src/test/resources/TestRoleConfig.xml");
        try
        {
            torque = (Torque) this.lookup(Torque.ROLE);
        }
        catch (ComponentException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Verifies that the MapBuilder is available in the DatabaseMap.
     */
    @Test
    public void testMapBuilder()
    {
        DatabaseMap dbMap = null;

        try
        {
            dbMap = torque.getDatabaseMap(AuthorPeer.DATABASE_NAME);
        }
        catch (TorqueException e)
        {
            fail(e.getMessage());
        }

        assertTrue(
                dbMap.containsTable(AuthorPeer.TABLE_NAME),
                "Table author should be in the DatabaseMap");
        assertFalse(torque == instance, "Torque instances should be different");
    }
}
