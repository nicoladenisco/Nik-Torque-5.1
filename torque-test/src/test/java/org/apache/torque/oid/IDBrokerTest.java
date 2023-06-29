package org.apache.torque.oid;

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
import java.sql.Connection;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.Torque;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.util.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Tests methods in the adapters
 *
 * @version $Id: IDBrokerTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class IDBrokerTest extends BaseDatabaseTestCase
{
    /** Class logger. */
    private static Log log = LogFactory.getLog(IDBrokerTest.class);

    /** System under test. */
    private IDBroker idBroker;

    @BeforeEach
    public void setUp() throws Exception
    {
        idBroker = new IDBroker(Torque.getDatabase(Torque.getDefaultDB()));
        Connection connection = Transaction.begin();
        idBroker.updateQuantity(
                connection,
                AuthorPeer.TABLE_NAME,
                new BigDecimal("10"));
        Transaction.commit(connection);
    }

    @AfterEach
    public void tearDown() throws Exception
    {
        idBroker.stop();
    }

    /**
     * Tests that restarting the idBroker clears its cache.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testRestartIdBroker() throws Exception
    {
        log.trace("testRestartIdBroker(): start");
        idBroker.start();
        Connection connection = Transaction.begin();
        int id = idBroker.getIdAsInt(connection, AuthorPeer.TABLE_NAME);
        idBroker.stop();
        while (idBroker.isThreadRunning()) {
            Thread.sleep(1000);
        }
        idBroker.start();
        int nextId = idBroker.getIdAsInt(connection, AuthorPeer.TABLE_NAME);
        // assuming quantity > 1
        assertTrue(nextId > id + 1);
        Transaction.commit(connection);
    }

    /**
     * Tests that the quantity value is initially set to 10.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testInitialQuantityValue() throws Exception
    {
        log.trace("testInitialQuantityValue(): start");
        idBroker.start();
        Connection connection = Transaction.begin();
        idBroker.getIdAsInt(connection, AuthorPeer.TABLE_NAME);
        assertEquals(
                new BigDecimal(10),
                idBroker.getQuantity(AuthorPeer.TABLE_NAME));
        Transaction.commit(connection);
    }

    /**
     * Tests that  cleverQuantity increases the quantity value
     * after an unscheduled retrieval.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testCleverQuantityValue() throws Exception
    {
        log.trace("testCleverQuantityValue(): start");
        Configuration configuration = new PropertiesConfiguration();
        configuration.setProperty("idbroker.clever.quantity", "true");
        idBroker.start();
        idBroker.setConfiguration(configuration);
        Connection connection = Transaction.begin();
        for (int i = 1 ; i <= 11; ++i)
        {
            int id = idBroker.getIdAsInt(connection, AuthorPeer.TABLE_NAME);
            log.trace("testCleverQuantityValue(): retrieved id " + id);
        }
        assertTrue(new BigDecimal("10").compareTo(
                idBroker.getQuantity(AuthorPeer.TABLE_NAME)) < 0);
        Transaction.commit(connection);
    }

    /**
     * Tests that the quantity is not increased above cleverQuantityMax.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testCleverQuantityValueMax() throws Exception
    {
        log.trace("testCleverQuantityValueMax(): start");
        Configuration configuration = new PropertiesConfiguration();
        configuration.setProperty("idbroker.clever.quantity", "true");
        configuration.setProperty("idbroker.clever.quantity.max", "14");
        idBroker.start();
        idBroker.setConfiguration(configuration);
        Connection connection = Transaction.begin();
        for (int i = 1 ; i <= 11; ++i)
        {
            int id = idBroker.getIdAsInt(connection, AuthorPeer.TABLE_NAME);
            log.trace("testCleverQuantityValueMax(): retrieved id " + id);
        }
        assertTrue(new BigDecimal("14").compareTo(
                idBroker.getQuantity(AuthorPeer.TABLE_NAME)) <= 0);
        Transaction.commit(connection);
    }
}
