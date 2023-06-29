package org.apache.torque.avalon;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.apache.torque.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Basic testing of the Torque Avalon Component
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: AvalonTest.java 1850726 2019-01-08 10:56:07Z gk $
 */
public class AvalonTest extends BaseUnit5Test
{
    private Torque torque = null;

    @BeforeEach
    public void setUp() throws Exception
    {
    }

    /**
     * Verifies that the container initialization and lookup works properly.
     */
    @Test
    public void testAvalonTorqueNotInitialized() throws Exception
    {
        setConfigurationFileName("src/test/resources/TestComponentConfig.xml");
        setRoleFileName("src/test/resources/TestRoleConfig.xml");
        torque = (Torque) this.lookup(Torque.class.getName());

        assertTrue(torque.isInit());
        assertTrue(torque == org.apache.torque.Torque.getInstance(),
                        "Instances should be identical");
    }

    /**
     * Verifies that the container initialization and lookup works properly.
     */
    @Test
    public void testAvalonTorqueInitialized() throws Exception
    {
        org.apache.torque.Torque.setInstance(null);
        org.apache.torque.Torque.init(BaseTestCase.CONFIG_FILE);
        setConfigurationFileName("src/test/resources/TestComponentConfig.xml");
        setRoleFileName("src/test/resources/TestRoleConfig.xml");
        torque = (Torque) this.lookup(Torque.class.getName());

        assertTrue(torque.isInit());
        assertTrue(torque == org.apache.torque.Torque.getInstance(),
                        "Instances should be identical");
    }
}
