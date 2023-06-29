package org.apache.torque.util;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.torque.BaseTestCase;
import org.junit.jupiter.api.Test;

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


/**
 * Test for UniqueList
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: UniqueListTest.java 1850726 2019-01-08 10:56:07Z gk $
 */
public class UniqueListTest extends BaseTestCase
{

    /**
     * null values are not allowed
     */
	@Test
    public void testNull()
    {
        UniqueList<Object> uniqueList = new UniqueList<>();
        Object o = null;
        boolean actualReturn = uniqueList.add(o);
        assertEquals(false, actualReturn, "return value");
    }

    /**
     * duplicates values are not allowed
     */
	@Test
    public void testUnique()
    {
        UniqueList<String> uniqueList = new UniqueList<>();
        uniqueList.add("Table");
        uniqueList.add("TableA");
        uniqueList.add("Table");
        uniqueList.add("TableB");
        assertEquals(3, uniqueList.size());
    }
}
