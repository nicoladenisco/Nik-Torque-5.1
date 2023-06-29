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
 * Tests the Max class
 *
 * @version $Id: MaxTest.java 1448414 2013-02-20 21:06:35Z tfischer $
 */
public class MaxTest extends TestCase
{
    /**
     * Tests the max constructor with a column returns the correct SQL.
     */
    public void testMax()
    {
        Max max = new Max(new ColumnImpl("myColumn"));
        assertEquals("MAX(myColumn)", max.getSqlExpression());
    }

    /**
     * Tests the max constructor with a column returns the correct SQL.
     */
    public void testMaxFromString()
    {
        Max max = new Max("myColumn");
        assertEquals("MAX(myColumn)", max.getSqlExpression());
    }

    /**
     * Tests the max constructor with distinct returns the correct SQL.
     */
    public void testMaxWithDistinct()
    {
        Max max = new Max(new ColumnImpl("myColumn"), true);
        assertEquals("MAX(DISTINCT myColumn)", max.getSqlExpression());
    }

    /**
     * tests that setFunction throws a
     */
    public void testSetFunction()
    {
        Max max = new Max(new ColumnImpl("myColumn"));
        try
        {
            max.setFunction("other");
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
