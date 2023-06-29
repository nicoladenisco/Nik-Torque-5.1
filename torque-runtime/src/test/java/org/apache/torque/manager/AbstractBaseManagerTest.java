package org.apache.torque.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.BaseTestCase;
import org.apache.torque.Column;
import org.apache.torque.ColumnImpl;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.SimpleKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class AbstractBaseManagerTest implements CacheListener<TestPersistent>
{
    private static final ObjectKey<?> TEST_PRIMARY_KEY = SimpleKey.keyFor("testID");
    private static final String CACHE_REGION = "testCache1";
    private TestManager manager;

    private boolean addedObjectCalled;
    private boolean refreshedObjectCalled;
    private boolean removedObjectCalled;

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

    /* CacheListener methods */
    @Override
    public void addedObject(TestPersistent om)
    {
        this.addedObjectCalled = true;
    }

    @Override
    public void refreshedObject(TestPersistent om)
    {
        this.refreshedObjectCalled = true;
    }

    @Override
    public void removedObject(TestPersistent om)
    {
        this.removedObjectCalled = true;
    }

    @Override
    public List<Column> getInterestedFields()
    {
        List<Column> columnList = new ArrayList<>(2);
        columnList.add(new ColumnImpl("test1"));
        columnList.add(new ColumnImpl("test2"));
        return columnList;
    }

    @BeforeEach
    public void setUp() throws Exception
    {
        this.manager = new TestManager();
        this.manager.setOMClass(TestPersistent.class);
        this.manager.setRegion(CACHE_REGION);
    }

    @AfterEach
    public void tearDown() throws Exception
    {
        manager.cache.clear();
    }

    @Test
    public void testGetOMInstance() throws TorqueException
    {
        TestPersistent test = manager.getOMInstance();
        assertNotNull(test, "Instance should not be null");
        assertTrue(test instanceof TestPersistent, "Instance should be a TestPersistent");
    }

    @Test
    public void testGetOMInstanceObjectKey() throws TorqueException
    {
        TestPersistent test = manager.getOMInstance(TEST_PRIMARY_KEY);
        assertNotNull( test, "Instance should not be null");
        assertTrue( test instanceof TestPersistent, "Instance should be a TestPersistent");
        assertEquals(TEST_PRIMARY_KEY, test.getPrimaryKey(), "Instance should have id 'testID'");
    }

    @Test
    public void testGetOMInstanceObjectKeyBoolean() throws TorqueException
    {
        TestPersistent test1 = manager.getOMInstance(TEST_PRIMARY_KEY, false);
        TestPersistent test2 = manager.getOMInstance(TEST_PRIMARY_KEY, false);
        assertNotSame( test1, test2, "Should be different instances");

        TestPersistent test3 = manager.getOMInstance(TEST_PRIMARY_KEY, true);
        TestPersistent test4 = manager.getOMInstance(TEST_PRIMARY_KEY, true);
        assertSame( test3, test4, "Should be same instances");
    }

    @Test
    public void testCacheGet() throws TorqueException
    {
        TestPersistent test1 = manager.cacheGet(TEST_PRIMARY_KEY);
        assertNull( test1, "Should not be in cache");
        test1 = manager.getOMInstance(TEST_PRIMARY_KEY, true);
        TestPersistent test2 = manager.cacheGet(TEST_PRIMARY_KEY);
        assertSame( test1, test2, "Should be same instances");
    }

    @Test
    public void testClearImpl() throws TorqueException
    {
        TestPersistent test1 = manager.getOMInstance(TEST_PRIMARY_KEY, true);
        TestPersistent test2 = manager.cacheGet(TEST_PRIMARY_KEY);
        assertNotNull( test2, "Should be in cache");
        assertSame( test1, test2, "Should be same instances");
        manager.clearImpl();
        TestPersistent test3 = manager.cacheGet(TEST_PRIMARY_KEY);
        assertNull( test3, "Should not be in cache");
    }

    @Test
    public void testRemoveInstanceImpl() throws TorqueException
    {
        TestPersistent test1 = manager.getOMInstance(TEST_PRIMARY_KEY, true);
        TestPersistent test2 = manager.removeInstanceImpl(TEST_PRIMARY_KEY);
        assertSame( test1, test2, "Should be same instances");
        TestPersistent test3 = manager.cacheGet(TEST_PRIMARY_KEY);
        assertNull(test3, "Should not be in cache");
    }

    @Test
    public void testPutInstanceImplSerializableT() throws TorqueException
    {
        TestPersistent test1 = manager.getOMInstance(TEST_PRIMARY_KEY, true);
        TestPersistent test2 = manager.getOMInstance(TEST_PRIMARY_KEY, false);
        TestPersistent test3 = manager.putInstanceImpl(TEST_PRIMARY_KEY, test2);
        assertSame(test1, test3, "Should be same instances");
        TestPersistent test4 = manager.cacheGet(TEST_PRIMARY_KEY);
        assertSame( test2, test4, "Should be same instances");
    }

    @Test
    public void testGetMethodResultCache()
    {
        MethodResultCache mrc = manager.getMethodResultCache();
        assertNotNull(mrc, "Should have MethodResultCache");
    }

    @Test
    public void testListeners()
    {
        manager.addValidField(new ColumnImpl("test1"), new ColumnImpl("test2"));
        manager.addCacheListenerImpl(this);
        this.addedObjectCalled = false;
        this.refreshedObjectCalled = false;
        this.removedObjectCalled = false;
        TestPersistent test1 = new TestPersistent();
        TestPersistent test2 = new TestPersistent();
        manager.notifyListeners(new ColumnImpl("test1"), (TestPersistent)null, test1);
        assertTrue(addedObjectCalled, "Should call addedObject");
        assertFalse(refreshedObjectCalled,"Should not call refreshedObject");
        assertFalse(removedObjectCalled, "Should not call removedObject");

        this.addedObjectCalled = false;
        this.refreshedObjectCalled = false;
        this.removedObjectCalled = false;
        manager.notifyListeners(new ColumnImpl("test2"), test2, test1);
        assertFalse(addedObjectCalled, "Should not call addedObject");
        assertTrue(refreshedObjectCalled, "Should call refreshedObject");
        assertFalse(removedObjectCalled, "Should not call removedObject");

        this.addedObjectCalled = false;
        this.refreshedObjectCalled = false;
        this.removedObjectCalled = false;
        manager.notifyListeners(new ColumnImpl("test2"), test2, null);
        assertFalse(addedObjectCalled, "Should not call addedObject");
        assertFalse(refreshedObjectCalled, "Should not call refreshedObject");
        assertTrue(removedObjectCalled, "Should call removedObject");
    }
}
