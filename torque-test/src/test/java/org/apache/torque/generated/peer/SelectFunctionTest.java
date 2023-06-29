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
import org.apache.torque.criteria.Criteria;
import org.apache.torque.om.mapper.IntegerMapper;
import org.apache.torque.test.dbobject.Summarize1;
import org.apache.torque.test.peer.Summarize1Peer;
import org.apache.torque.util.BasePeerImpl;
import org.apache.torque.util.functions.Avg;
import org.apache.torque.util.functions.Count;
import org.apache.torque.util.functions.Max;
import org.apache.torque.util.functions.Min;
import org.apache.torque.util.functions.Sum;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Tests selects using functions.
 *
 * @version $Id: SelectFunctionTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class SelectFunctionTest extends BaseDatabaseTestCase
{
    private static final int[] INT_VALUES  = {
            1, 1, 1, 5
    };

    private BasePeerImpl<Integer> peer;

    @BeforeEach
    public void setUp() throws Exception
    {
        // Clean up any previous failures
        Summarize1Peer.doDelete(new Criteria());

        // Create some test data
        for (int i = 0; i < INT_VALUES.length; i++)
        {
            Summarize1 rec = new Summarize1();
            rec.setInt1(INT_VALUES[i] );
            rec.save();
        }

        peer = new BasePeerImpl<>(
                new IntegerMapper(),
                Summarize1Peer.getTableMap(),
                Summarize1Peer.DATABASE_NAME);
    }

    /**
     * Tests a select using the Avg function.
     *
     * @throws Exception if the test fails
     */
    public void testSelectAvg() throws Exception
    {
        Criteria criteria = new Criteria().addSelectColumn(
                new Avg(Summarize1Peer.INT_1));

        Integer avg = peer.doSelectSingleRecord(criteria);

        assertEquals(new Integer(2), avg);
    }

    /**
     * Tests a select using the Avg function with distinct.
     *
     * @throws Exception if the test fails
     */
    public void testSelectAvgDistinct() throws Exception
    {
        Criteria criteria = new Criteria().addSelectColumn(
                new Avg(Summarize1Peer.INT_1, true));

        Integer avg = peer.doSelectSingleRecord(criteria);

        assertEquals(new Integer(3), avg);
    }

    /**
     * Tests a select using the count function.
     *
     * @throws Exception if the test fails
     */
    public void testSelectCount() throws Exception
    {
        Criteria criteria = new Criteria().addSelectColumn(new Count("*"));

        Integer count = peer.doSelectSingleRecord(criteria);

        assertEquals(new Integer(4), count);
    }

    /**
     * Tests a select using the count function with distinct.
     *
     * @throws Exception if the test fails
     */
    public void testSelectCountDistinct() throws Exception
    {
        Criteria criteria = new Criteria().addSelectColumn(
                new Count(Summarize1Peer.INT_1, true));

        Integer count = peer.doSelectSingleRecord(criteria);

        assertEquals(new Integer(2), count);
    }

    /**
     * Tests a select using the min function.
     *
     * @throws Exception if the test fails
     */
    public void testSelectMin() throws Exception
    {
        Criteria criteria = new Criteria().addSelectColumn(
                new Min(Summarize1Peer.INT_1));

        Integer min = peer.doSelectSingleRecord(criteria);

        assertEquals(new Integer(1), min);
    }
    /**

     * Tests a select using the max function.
     *
     * @throws Exception if the test fails
     */
    public void testSelectMax() throws Exception
    {
        Criteria criteria = new Criteria().addSelectColumn(
                new Max(Summarize1Peer.INT_1));

        Integer max = peer.doSelectSingleRecord(criteria);

        assertEquals(new Integer(5), max);
    }

    /**
     * Tests a select using the sum function.
     *
     * @throws Exception if the test fails
     */
    public void testSelectSum() throws Exception
    {
        Criteria criteria = new Criteria().addSelectColumn(
                new Sum(Summarize1Peer.INT_1));

        Integer sum = peer.doSelectSingleRecord(criteria);

        assertEquals(new Integer(8), sum);
    }

    /**
     * Tests a select using the sum function with distinct.
     *
     * @throws Exception if the test fails
     */
    public void testSelectSumDistinct() throws Exception
    {
        Criteria criteria = new Criteria().addSelectColumn(
                new Sum(Summarize1Peer.INT_1, true));

        Integer sum = peer.doSelectSingleRecord(criteria);

        assertEquals(new Integer(6), sum);
    }
}
