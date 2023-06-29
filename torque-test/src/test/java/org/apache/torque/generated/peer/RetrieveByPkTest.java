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
import java.util.Date;
import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.NoRowsException;
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.SimpleKey;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.MultiPkPeer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

/**
 * Tests the retrieveByPk methods.
 *
 * @version $Id: RetrieveByPkTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class RetrieveByPkTest extends BaseDatabaseTestCase
{
    private List<Author> authorList;

    @BeforeEach
    public void setUp() throws Exception
    {
       
        cleanBookstore();
        authorList = insertBookstoreData();
    }

    /**
     * Tests retrieveByPk using a simple primary key
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testRetrieveByPk() throws Exception
    {
        ObjectKey<?> primaryKey = authorList.get(1).getPrimaryKey();
        Author author = AuthorPeer.retrieveByPK(primaryKey);
        assertEquals("Expected author with Id "
                + authorList.get(1).getAuthorId()
                + " at first position but got "
                + author.getAuthorId(),
                authorList.get(1).getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests retrieveByPk using a non-existent primary key
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testRetrieveByNonExistingPk() throws Exception
    {
        ObjectKey<?> primaryKey = new NumberKey(-1);
        try
        {
            AuthorPeer.retrieveByPK(primaryKey);
        }
        catch (NoRowsException e)
        {
            assertEquals("Failed to select a row.", e.getMessage());
        }
    }

    /**
     * Tests retrieveByPk using a non-existent primary key
     * for an object with multiple PKs (TORQUE-318)
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testRetrieveByNonExistingPkMultiplePKs() throws Exception
    {
        try
        {
            MultiPkPeer.retrieveByPK("", 1, "", 1, (byte) 1, (short) 1, 1l, 1d, 1d, new Date(1l));
        }
        catch (NoRowsException e)
        {
            assertEquals("Failed to select a row.", e.getMessage());
        }
    }

    /**
     * Tests retrieveByPk using a key object with a value of null.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testRetrieveByNullValuePk() throws Exception
    {
        ObjectKey<?> primaryKey = new NumberKey((BigDecimal) null);
        try
        {
            AuthorPeer.retrieveByPK(primaryKey);
        }
        catch (NoRowsException e)
        {
            assertEquals("Failed to select a row.", e.getMessage());
        }
    }

    /**
     * Tests retrieveByPk using a null key.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testRetrieveByNullPk() throws Exception
    {
        try
        {
            AuthorPeer.retrieveByPK(null);
        }
        catch (NoRowsException e)
        {
            assertEquals("Failed to select a row.", e.getMessage());
        }
    }
    // TODO test retrieveByPks

    /**
     * Tests the retrieveByObjectKeys method
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testRetrieveByObjectKeys() throws Exception
    {
        List<ObjectKey<?>> objectKeys = new ArrayList<>();
        objectKeys.add(authorList.get(1).getPrimaryKey());
        objectKeys.add(authorList.get(2).getPrimaryKey());
        objectKeys.add(SimpleKey.keyFor(-5L));

        List<Author> result = AuthorPeer.retrieveByObjectKeys(objectKeys);
        assertEquals(2, result.size());
        assertTrue("authorList should contain author 1", result.contains(authorList.get(1)));
        assertTrue("authorList should contain author 2", result.contains(authorList.get(2)));
    }

    /**
     * Tests the retrieveByObjectKeys method
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testRetrieveByTypedKeys() throws Exception
    {
        List<Integer> typedKeys = new ArrayList<>();
        typedKeys.add(authorList.get(1).getAuthorId());
        typedKeys.add(authorList.get(2).getAuthorId());
        typedKeys.add(-5);

        List<Author> result = AuthorPeer.retrieveByTypedPKs(typedKeys);
        assertEquals(2, result.size());
        assertTrue("authorList should contain author 1", result.contains(authorList.get(1)));
        assertTrue("authorList should contain author 2", result.contains(authorList.get(2)));
    }
}
