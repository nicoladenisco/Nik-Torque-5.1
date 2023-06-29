package org.apache.torque.om;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.torque.BaseTestCase;
import org.junit.jupiter.api.Test;

/**
 * TestCase for ComboKey
 *
 * @author <a href="mailto:drfish@cox.net">J. Russell Smyth</a>
 * @version $Id: ComboKeyTest.java 1855056 2019-03-08 17:26:27Z tv $
 */
public class ComboKeyTest extends BaseTestCase
{
    private ComboKey c1a = new ComboKey(
            new SimpleKey[]{new StringKey("key1"), new StringKey("key2")});
    private ComboKey c1b = new ComboKey(
            new SimpleKey[]{new StringKey("key1"), new StringKey("key2")});
    private ComboKey c2a = new ComboKey(
            new SimpleKey[]{new StringKey("key3"), new StringKey("key4")});
    // complex keys for test
    private java.util.Date now = new java.util.Date();
    private ComboKey c3a = new ComboKey(
            new SimpleKey[]{new StringKey("key1"), null, new DateKey(now)});
    private ComboKey c4a = new ComboKey(
            new SimpleKey[]{new StringKey("key1"), null, new NumberKey(123456)});


    /**
     *
     *
     */
    @Test
    public void testReflexive()
    {
        assertTrue(c1a.equals(c1a));
        // Complex key using null and date
        // This currently has to use looseEquals as ComboKey.equals(Obj)
        // does not accept null key values (WHY!)
        assertTrue(c3a.looseEquals(c3a));
    }

    /**
     *
     *
     */
    @Test
    public void testSymmetric()
    {
        assertTrue(c1a.equals(c1b));
        assertTrue(c1b.equals(c1a));
    }

    /**
     *
     *
     */
    public void testNull()
    {
        assertTrue(!c1a.equals(null));
    }

    /**
     *
     *
     */
    @Test
    public void testNotEqual()
    {
        assertTrue(!c1a.equals(c2a));
    }

    /**
     *
     *
     */
    @Test
    public void testRoundTripWithStringKeys()
    {
        // two strings
        ComboKey oldKey = new ComboKey(
                new SimpleKey[]{new StringKey("key1"), new StringKey("key2")});
        ComboKey newKey = null;
        String stringValue = oldKey.toString();
        try
        {
            newKey = new ComboKey(stringValue);
        }
        catch(Exception e)
        {
            fail("Exception " + e.getClass().getName()
                    + " thrown on new ComboKey(" + stringValue + "):"
                    + e.getMessage());
        }
        assertEquals(oldKey,newKey);
    }

    /**
     *
     *
     */
    @Test
    public void testRoundTripWithComplexKey()
    {
        // complex key
        ComboKey oldKey = new ComboKey(
                new SimpleKey[]{new StringKey("key1"), new NumberKey(12345),
                        new DateKey(new java.util.Date())});
        ComboKey newKey = null;
        String stringValue = oldKey.toString();
        try
        {
            newKey = new ComboKey(stringValue);
        }
        catch (Exception e)
        {
            fail("Exception " + e.getClass().getName()
                    + " thrown on new ComboKey("
                    + stringValue + "):" + e.getMessage());
        }
        assertEquals(oldKey,newKey);
    }

    /**
     *
     *
     */
    @Test
    public void testRoundTripWithNullKey()
    {
        // with null key
        ComboKey oldKey = new ComboKey(
                new SimpleKey[]{new StringKey("key1"), null});
        ComboKey newKey = null;
        String stringValue = oldKey.toString();
        try
        {
            newKey = new ComboKey(stringValue);
        }
        catch (Exception e)
        {
            fail("Exception " + e.getClass().getName()
                    + " thrown on new ComboKey("
                    + stringValue + "):" + e.getMessage());
        }
        // This currently has to use looseEquals as ComboKey.equals(Obj)
        // does not accept null key values (WHY!)
        assertTrue(oldKey.looseEquals(newKey));
    }


    /**
     * Test of appendTo method, of class org.apache.torque.om.ComboKey.
     */
    @Test
    public void testAppendTo()
    {
        StringBuilder sb = new StringBuilder();
        c1a.appendTo(sb);
        assertEquals("Skey1:Skey2:", sb.toString());
    }

    /**
     * Test of toString method, of class org.apache.torque.om.ComboKey.
     */
    @Test
    public void testToString()
    {
        assertEquals("Skey1::N123456:", c4a.toString());
    }
}
