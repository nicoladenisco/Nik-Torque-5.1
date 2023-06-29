package org.apache.torque.criteria;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import org.apache.torque.BaseTestCase;
import org.apache.torque.ColumnImpl;
import org.junit.jupiter.api.Test;


/**
 * Test for the Join class.
 *
 * @version $Id: JoinTest.java 1850726 2019-01-08 10:56:07Z gk $
 */
public class JoinTest extends BaseTestCase
{
	@Test
    public void testHashCodeAndEquals()
    {
        Join join = new Join(
                new ColumnImpl("myTable1", "myColumn1"),
                new ColumnImpl("myTable2", "myColumn2"),
                Criteria.EQUAL,
                Criteria.LEFT_JOIN);
        Join compareToJoin = new Join(
                new ColumnImpl("myTable1", "myColumn1"),
                new ColumnImpl("myTable2", "myColumn2"),
                Criteria.EQUAL,
                Criteria.LEFT_JOIN);
        assertEquals(join.hashCode(), compareToJoin.hashCode());
        assertEquals(join, compareToJoin);
    }

	@Test
    public void testToString()
    {
        Join join = new Join(
                new ColumnImpl("myTable1", "myColumn1"),
                new ColumnImpl("myTable2", "myColumn2"),
                Criteria.NOT_EQUAL,
                Criteria.LEFT_JOIN);
        assertEquals(" LEFT JOIN (null, null): "
                + "myTable1.myColumn1<>myTable2.myColumn2",
                join.toString());
    }
}
