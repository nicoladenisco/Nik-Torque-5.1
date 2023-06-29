package org.apache.torque.adapter;

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

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.Torque;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.util.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests methods in the adapters
 *
 * @version $Id: AdapterTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class AdapterTest extends BaseDatabaseTestCase
{
    @BeforeEach
    public void setUp() throws Exception
    {
        cleanBookstore();
        insertBookstoreData();
    }

    /**
     * Tests that locking tables throws no error.
     * Other behavior is difficult to test as the database may wait
     * for the connection holding the lock being closed.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testLockTableThrowsNoError() throws Exception
    {
        Connection connection = null;
        try
        {
            connection = Transaction.begin();
            // lock table
            Torque.getAdapter(Torque.getDefaultDB()).lockTable(
                    connection,
                    AuthorPeer.TABLE_NAME);
        }
        finally
        {
            if (connection != null)
            {
                Torque.getAdapter(Torque.getDefaultDB()).unlockTable(
                        connection,
                        AuthorPeer.TABLE_NAME);
                Transaction.safeRollback(connection);
            }
        }
    }
}
