package org.apache.torque.om;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.torque.BaseTestCase;
import org.junit.jupiter.api.Test;


/**
 * Tests the NumberKey class.
 *
 * @author <a href="mailto:stephenh@chase3000.com">Stephen Haberman</a>
 * @version $Id: NumberKeyTest.java 1850726 2019-01-08 10:56:07Z gk $
 */
public class NumberKeyTest extends BaseTestCase
{

    /** Test value. */
    private NumberKey n1a = new NumberKey(1);
    /** Test value. */
    private NumberKey n1b = new NumberKey(1);
    /** Test value. */
    private NumberKey n1c = new NumberKey(1);
    /** Test value. */
    private NumberKey n2a = new NumberKey(2);

    /**
     * Test a.equals(a)
     */
    @Test
    public void testEqualsReflexive()
    {
        assertTrue(n1a.equals(n1a));
    }

    /**
     * Test a.hashCode().equals(a.hashCode())
     */
    @Test
    public void testHashCodeEqual()
    {
        assertEquals(n1a.hashCode(), n1b.hashCode());
    }

    /**
     * Test !a.equals(b)
     */
    @Test
    public void testEqualsFalse()
    {
        assertFalse(n1a.equals(n2a));
        assertTrue(n1a.hashCode() == n1a.hashCode());
    }

    /**
     * Test a.equals(b) = b.equals(a)
     */
    @Test
    public void testEqualsSymmetric()
    {
        assertTrue(n1a.equals(n1b));
        assertTrue(n1b.equals(n1a));

        assertFalse("1".equals(n1a));
        assertFalse(n1a.equals("1"));
        assertFalse(n1a.equals(Integer.valueOf(1)));
        assertFalse(Integer.valueOf(1).equals(n1a));
    }

    /**
     * Test a.equals(b) = b.equals(c) = c.equals(a)
     */
    @Test
    public void testEqualsTransitive()
    {
        assertTrue(n1a.equals(n1b));
        assertTrue(n1b.equals(n1c));
        assertTrue(n1c.equals(n1a));
    }

    /**
     * Test !a.equals(null)
     */
    @Test
    public void testEqualsNull()
    {
        assertFalse(n1a.equals(null));
    }

    /**
     * Test sorting.
     */
    @Test
    public void testList()
    {
        Object[] array = new Object[] {n1a, n2a, n1b};
        Arrays.sort(array);

        assertEquals(n1a, array[0]);
        assertEquals(n1b, array[1]);
        assertEquals(n2a, array[2]);
    }

    /**
     * Test copy constructor.
     */
    @Test
    public void testEmptyConstructor()
    {
        NumberKey key = new NumberKey();
        assertEquals(null, key.getValue());
    }

    /**
     * Test copy constructor.
     */
    @Test
    public void testCopyConstructor()
    {
        NumberKey key = new NumberKey(new NumberKey("44"));
        assertEquals("44", key.toString());
    }

    /**
     * Test copy constructor.
     */
    @Test
    public void testCopyConstructorNullValue()
    {
        NumberKey key = new NumberKey((NumberKey) null);
        assertEquals(null, key.getValue());
    }

    /**
     * Test copy constructor.
     */
    @Test
    public void testBigDecimalConstructor()
    {
        NumberKey key = new NumberKey(new BigDecimal("20.42"));
        assertEquals(new BigDecimal("20.42"), key.getValue());
    }

    /**
     * Test long constructor.
     */
    @Test
    public void testLongConstructor()
    {
        NumberKey key = new NumberKey(9900000000000001L);
        assertEquals("9900000000000001", key.toString());
    }

    /**
     * Test double constructor.
     */
    @Test
    public void testDoubleConstructor()
    {
        NumberKey key = new NumberKey(5d);
        assertEquals("5", key.toString());
    }

    /**
     * Test int constructor.
     */
    @Test
    public void testIntConstructor()
    {
        NumberKey key = new NumberKey(432);
        assertEquals("432", key.toString());
    }

    /**
     * Test number constructor.
     */
    @Test
    public void testNumberConstructor()
    {
        NumberKey key = new NumberKey(Integer.valueOf(432));
        assertEquals("432", key.toString());
    }

    /**
     * Test number constructor with null value.
     */
    @Test
    public void testNumberConstructorNull()
    {
        NumberKey key = new NumberKey((Integer) null);
        assertEquals(null, key.getValue());
    }

    /**
     * Test String constructor.
     */
    @Test
    public void testStringConstructor()
    {
        NumberKey key = new NumberKey("9900000000000001");
        assertEquals("9900000000000001", key.toString());
    }

    /**
     * Test String constructor with null value.
     */
    @Test
    public void testStringConstructorNull()
    {
        NumberKey key = new NumberKey((String) null);
        assertEquals(null, key.getValue());
    }

    /**
     * Test setValue(String) method.
     */
    @Test
    public void testSetValueString()
    {
        NumberKey key = new NumberKey();
        key.setValue("9900000000000001");
        assertEquals("9900000000000001", key.toString());
    }

    /**
     * Test setValue(String) method with null argument.
     */
    @Test
    public void testSetValueStringNull()
    {
        NumberKey key = new NumberKey();
        key.setValue((String) null);
        assertEquals(null, key.getValue());
    }

    /**
     * Test setValue(BigDecimal) method.
     */
    @Test
    public void testSetValueBigDecimal()
    {
        NumberKey key = new NumberKey();
        key.setValue(new BigDecimal("13.56"));
        assertEquals("13.56", key.toString());
    }

    /**
     * Test setValue(NumberKey) method.
     */
    @Test
    public void testSetValueNumberKey()
    {
        NumberKey key = new NumberKey();
        key.setValue(new NumberKey("13.56"));
        assertEquals("13.56", key.toString());
    }

    /**
     * Test setValue(NumberKey) method with null argument.
     */
    @Test
    public void testSetValueNumberKeyNull()
    {
        NumberKey key = new NumberKey();
        key.setValue((NumberKey) null);
        assertEquals(null, key.getValue());
    }
}
