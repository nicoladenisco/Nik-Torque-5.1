package org.apache.torque.util.functions;

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

import junit.framework.TestCase;

import org.apache.torque.ColumnImpl;

/**
 * Tests the Count class.
 *
 * @version $Id: CountTest.java 1448414 2013-02-20 21:06:35Z tfischer $
 */
public class CountTest extends TestCase
{
    /**
     * Tests the Count constructor with a column returns the correct SQL.
     */
    public void testCount()
    {
        Count count = new Count(new ColumnImpl("myColumn"));
        assertEquals("COUNT(myColumn)", count.getSqlExpression());
    }

    /**
     * Tests the Count constructor with a column returns the correct SQL.
     */
    public void testCountFromString()
    {
        Count count = new Count("myColumn");
        assertEquals("COUNT(myColumn)", count.getSqlExpression());
    }

    /**
     * Tests the Count constructor with distinct returns the correct SQL.
     */
    public void testCountWithDistinct()
    {
        Count count = new Count(new ColumnImpl("myColumn"), true);
        assertEquals("COUNT(DISTINCT myColumn)", count.getSqlExpression());
    }

    /**
     * tests that setFunction throws a
     */
    public void testSetFunction()
    {
        Count count = new Count(new ColumnImpl("myColumn"));
        try
        {
            count.setFunction("other");
            fail("Exception expected");
        }
        catch (UnsupportedOperationException e)
        {
            assertEquals(
                    "The function name may not be changed.",
                    e.getMessage());
        }
    }
}
