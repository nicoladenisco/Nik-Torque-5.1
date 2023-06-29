package org.apache.torque.manager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

import static org.junit.jupiter.api.Assertions.assertSame;

import org.apache.torque.BaseTestCase;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class MethodResultCacheTest
{
    private static final String CACHE_REGION = "testCache1";
    private static final String TEST_METHOD1 = "testMethod1";
    private static final String TEST_METHOD2 = "testMethod2";
    private static final String TEST_ARG_ONE = "one";
    private static final String TEST_ARG_TWO = "two";
    private static final String TEST_ARG_THREE = "three";

    private TestManager manager;
    private MethodResultCache mrc;

    @BeforeAll
    public static void initTorque()
    {
        try
        {
            org.apache.torque.Torque.init(BaseTestCase.CONFIG_FILE);
            org.apache.torque.Torque.getConfiguration().setProperty(Torque.CACHE_KEY, Boolean.TRUE);
        }
        catch (TorqueException e)
        {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void shutdownTorque()
    {
        try
        {
            org.apache.torque.Torque.shutdown();
        }
        catch (TorqueException e)
        {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void setUp() throws Exception
    {
        this.manager = new TestManager();
        this.manager.setOMClass(TestPersistent.class);
        this.manager.setRegion(CACHE_REGION);
        this.mrc = this.manager.getMethodResultCache();
    }

    @AfterEach
    public void tearDown() throws Exception
    {
        mrc.clear();
    }

    @Test
    public void testGetAndPut()
    {
        TestPersistent test1 = new TestPersistent();
        mrc.put(test1, TestPersistent.class, TEST_METHOD1, TEST_ARG_ONE, TEST_ARG_TWO);
        TestPersistent test2 = mrc.get(TestPersistent.class, TEST_METHOD1, TEST_ARG_ONE, TEST_ARG_TWO);
        assertNotNull(test2, "Should be in cache");
        assertSame( test1, test2, "Should be same instance");
    }

    @Test
    public void testRemoveAll()
    {
        TestPersistent test1 = new TestPersistent();
        mrc.put(test1, TestPersistent.class, TEST_METHOD1, TEST_ARG_ONE, TEST_ARG_TWO);
        assertNotNull( mrc.get(TestPersistent.class, TEST_METHOD1, TEST_ARG_ONE, TEST_ARG_TWO), "Should be in cache");
        TestPersistent test2 = new TestPersistent();
        mrc.put(test2, TestPersistent.class, TEST_METHOD1, TEST_ARG_ONE, TEST_ARG_THREE);
        assertNotNull( mrc.get(TestPersistent.class, TEST_METHOD1, TEST_ARG_ONE, TEST_ARG_THREE), "Should be in cache");

        TestPersistent test3 = new TestPersistent();
        mrc.put(test3, TestPersistent.class, TEST_METHOD2, TEST_ARG_ONE, TEST_ARG_TWO);
        assertNotNull( mrc.get(TestPersistent.class, TEST_METHOD2, TEST_ARG_ONE, TEST_ARG_TWO), "Should be in cache");
        TestPersistent test4 = new TestPersistent();
        mrc.put(test4, TestPersistent.class, TEST_METHOD2, TEST_ARG_ONE, TEST_ARG_THREE);
        assertNotNull( mrc.get(TestPersistent.class, TEST_METHOD2, TEST_ARG_ONE, TEST_ARG_THREE), "Should be in cache");

        mrc.removeAll(TestPersistent.class, TEST_METHOD1);

        assertNull( mrc.get(TestPersistent.class, TEST_METHOD1, TEST_ARG_ONE, TEST_ARG_TWO), "Should not be in cache");
        assertNull( mrc.get(TestPersistent.class, TEST_METHOD1, TEST_ARG_ONE, TEST_ARG_THREE), "Should not be in cache");

        assertNotNull( mrc.get(TestPersistent.class, TEST_METHOD2, TEST_ARG_ONE, TEST_ARG_TWO), "Should still be in cache");
        assertNotNull( mrc.get(TestPersistent.class, TEST_METHOD2, TEST_ARG_ONE, TEST_ARG_THREE), "Should still be in cache");
    }

    @Test
    public void testRemove()
    {
        TestPersistent test1 = new TestPersistent();
        mrc.put(test1, TestPersistent.class, TEST_METHOD1, TEST_ARG_ONE, TEST_ARG_TWO);
        assertNotNull(mrc.get(TestPersistent.class, TEST_METHOD1, TEST_ARG_ONE, TEST_ARG_TWO), "Should be in cache");

        TestPersistent test3 = mrc.remove(TestPersistent.class, TEST_METHOD1, TEST_ARG_ONE, TEST_ARG_TWO);

        assertNull( mrc.get(TestPersistent.class, TEST_METHOD1, TEST_ARG_ONE, TEST_ARG_TWO), "Should not be in cache");
        assertSame( test1, test3, "Should be same instance");
    }
}
