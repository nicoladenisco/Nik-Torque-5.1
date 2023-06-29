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

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.AuthorPeerImpl;
import org.apache.torque.test.recordmapper.AuthorRecordMapper;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

/**
 * Tests the peerImpl initialisation.
 *
 * @version $Id: ImplInitialisationTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class ImplInitialisationTest extends BaseDatabaseTestCase
{
    /**
     * Tests that the noArg-Constructor initializes the object correctly.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testNoArgConstructor() throws Exception
    {
        AuthorPeerImpl authorPeerImpl= new AuthorPeerImpl();
        assertEquals(AuthorPeer.TABLE, authorPeerImpl.getTableMap());
        assertEquals(
                AuthorPeer.DATABASE_NAME,
                authorPeerImpl.getDatabaseName());
        assertTrue(authorPeerImpl.getRecordMapper() instanceof
                AuthorRecordMapper);
    }
}
