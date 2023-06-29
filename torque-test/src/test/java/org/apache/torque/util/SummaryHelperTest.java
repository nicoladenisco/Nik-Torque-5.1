package org.apache.torque.util;

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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.Summarize1;
import org.apache.torque.test.peer.Summarize1Peer;
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
 * Test code for SummaryHelper.
 *
 * @author <a href="mailto:greg.monroe@dukece.com">Greg Monroe</a>
 * @version $Id
 */
public class SummaryHelperTest extends BaseDatabaseTestCase
{
    static Log logger = LogFactory.getLog( SummaryHelperTest.class );

    private static final String[] TEST_GROUPBY1  = {
            "A1","B1","C1","D1"
    };
    private static final int[] TEST_GROUPBY1_TYPE  = {
            1, 2, 1, 2
    };
    private static final String[] TEST_GROUPBY2  = {
            "A2","B2","C2","D2"
    };


    @BeforeEach
    public void setUp() throws Exception
    {

        // Clean up any previous failures
        Summarize1Peer.doDelete(new Criteria());

        // Create some test data
        for (int i = 0; i < TEST_GROUPBY1.length; i++)
        {
            for ( int j = 0; j < TEST_GROUPBY2.length; j++ ) {
                Summarize1 rec = new Summarize1();
                rec.setGroupBy1(TEST_GROUPBY1[i]);
                rec.setGroupBy2(TEST_GROUPBY2[j]);
                rec.setType(TEST_GROUPBY1_TYPE[j]);
                rec.setInt1( i+1 * (j+1) );
                rec.setFloat1( (i+1 * (j+1)) / 0.5f );
                rec.save();
            }
        }
    }

    /*
     * Test a simple single table with group bys
     */
    public void testSummarize()
            throws Exception
    {
        Criteria c = new Criteria();

        c.addAscendingOrderByColumn(Summarize1Peer.GROUP_BY1);

        SummaryHelper summary = new SummaryHelper();

        summary.setExcludeExprColumns(true);
        List<Class<?>> returnTypes = new ArrayList<>();

        summary.addGroupBy ( Summarize1Peer.GROUP_BY1 );
        returnTypes.add(String.class);

        summary.addAggregate("COUNT_RECS", new Count(Summarize1Peer.ID));
        returnTypes.add(Integer.class);

        summary.addAggregate("AVG_INT1", new Avg(Summarize1Peer.INT_1));
        returnTypes.add(Integer.class);

        summary.addAggregate("MIN_INT1", new Min(Summarize1Peer.INT_1));
        returnTypes.add(Integer.class);

        summary.addAggregate("MAX_INT1", new Max(Summarize1Peer.INT_1));
        returnTypes.add(Integer.class);

        summary.addAggregate("SUM_INT1", new Sum(Summarize1Peer.INT_1));
        returnTypes.add(Integer.class);

        summary.addAggregate("AVG_FLOAT1", new Avg(Summarize1Peer.FLOAT1));
        returnTypes.add(Float.class);

        summary.addAggregate("MIN_FLOAT1", new Min(Summarize1Peer.FLOAT1));
        returnTypes.add(Float.class);

        summary.addAggregate("MAX_FLOAT1", new Max(Summarize1Peer.FLOAT1));
        returnTypes.add(Float.class);

        summary.addAggregate("SUM_FLOAT1", new Sum(Summarize1Peer.FLOAT1));
        returnTypes.add(Float.class);

        List<ListOrderedMapCI<Object>> results = summary.summarize(c, returnTypes);

        StringWriter out = new StringWriter();
        summary.dumpResults(out, results, true);
        out.close();
        logger.debug("\n" + out.toString());

        assertTrue("No results returned", results.size() > 0 );
        assertTrue("Invalid number of records returned. Expected 4 but got " +
                results.size(), results.size() == 4 );

        ListOrderedMapCI<Object> rec = results.get(0);

        assertTrue( "GROUP_BY1 valued not correct",
                "A1".equals(rec.get("GROUP_BY1").toString()) );
        assertTrue("COUNT_RECS not correct value",
                ((Integer) rec.get("COUNT_RECS")).intValue() == 4 );
        assertTrue("AVG_INT1 not correct value",
                ((Integer) rec.get("AVG_INT1")).intValue() == 2 );
        assertTrue("MIN_INT1 not correct value",
                ((Integer) rec.get("MIN_INT1")).intValue() == 1 );
        assertTrue("MAX_INT1 not correct value",
                ((Integer) rec.get("MAX_INT1")).intValue() == 4 );
        assertTrue("SUM_INT1 not correct value",
                ((Integer) rec.get("SUM_INT1")).intValue() == 10 );

        rec = results.get(3);
        assertTrue( "GROUP_BY1 valued not correct",
                "D1".equals(rec.get("GROUP_BY1")));
        assertTrue("COUNT_RECS not correct value",
                ((Integer) rec.get("COUNT_RECS")).intValue() == 4 );
        assertTrue("AVG_FLOAT1 not correct value",
                ((Float) rec.get("AVG_FLOAT1")).floatValue() == 11.0f );
        assertTrue("MIN_FLOAT1 not correct value",
                ((Float) rec.get("MIN_FLOAT1")).floatValue() == 8.0f );
        assertTrue("MAX_FLOAT1 not correct value",
                ((Float) rec.get("MAX_FLOAT1")).floatValue() == 14.0f );
        assertTrue("SUM_FLOAT1 not correct value",
                ((Float) rec.get("SUM_FLOAT1")).floatValue() == 44.0f );

    }

    /*
     * Test a total table aggregate summaries (no group bys)
     */
    public void testSummarizeEntireTable() throws Exception {
        Criteria c = new Criteria();

        SummaryHelper summary = new SummaryHelper();

        summary.setExcludeExprColumns(true);
        List<Class<?>> returnTypes = new ArrayList<>();

        summary.addAggregate("COUNT_RECS", new Count(Summarize1Peer.ID));
        returnTypes.add(Integer.class);

        summary.addAggregate("AVG_INT1", new Avg(Summarize1Peer.INT_1));
        returnTypes.add(Integer.class);

        summary.addAggregate("MIN_INT1", new Min(Summarize1Peer.INT_1));
        returnTypes.add(Integer.class);

        summary.addAggregate("MAX_INT1", new Max(Summarize1Peer.INT_1));
        returnTypes.add(Integer.class);

        summary.addAggregate("SUM_INT1", new Sum(Summarize1Peer.INT_1));
        returnTypes.add(Integer.class);

        summary.addAggregate("AVG_FLOAT1", new Avg(Summarize1Peer.FLOAT1));
        returnTypes.add(Float.class);

        summary.addAggregate("MIN_FLOAT1", new Min(Summarize1Peer.FLOAT1));
        returnTypes.add(Float.class);

        summary.addAggregate("MAX_FLOAT1", new Max(Summarize1Peer.FLOAT1));
        returnTypes.add(Float.class);

        summary.addAggregate("SUM_FLOAT1", new Sum(Summarize1Peer.FLOAT1));
        returnTypes.add(Float.class);

        List<ListOrderedMapCI<Object>> results = summary.summarize(c, returnTypes);

        StringWriter out = new StringWriter();
        summary.dumpResults(out, results, true);
        out.close();
        logger.debug("\n"+out.toString());

        assertTrue("No results returned", results.size() > 0 );
        assertTrue("Invalid number of records returned.  Expected 1 but got " +
                results.size(), results.size() == 1 );

        ListOrderedMapCI<Object> rec = results.get(0);

        assertTrue( "Number of columns incorrect! Did ExcludeExpColumns work? " +
                "Expected 9 but got " + rec.size(), rec.size() == 9 );
        assertTrue("COUNT_RECS not correct value",
                ((Integer) rec.get("COUNT_RECS")).intValue() == 16 );
        assertTrue("AVG_INT1 not correct value",
                ((Integer) rec.get("AVG_INT1")).intValue() == 4 );
        assertTrue("MIN_INT1 not correct value",
                ((Integer) rec.get("MIN_INT1")).intValue() == 1 );
        assertTrue("MAX_INT1 not correct value",
                ((Integer) rec.get("MAX_INT1")).intValue() == 7 );
        assertTrue("SUM_INT1 not correct value",
                ((Integer) rec.get("SUM_INT1")).intValue() == 64 );

        assertTrue("AVG_FLOAT1 not correct value",
                ((Float) rec.get("AVG_FLOAT1")).floatValue() == 8.0f );
        assertTrue("MIN_FLOAT1 not correct value",
                ((Float) rec.get("MIN_FLOAT1")).floatValue() == 2.0f );
        assertTrue("MAX_FLOAT1 not correct value",
                ((Float) rec.get("MAX_FLOAT1")).floatValue() == 14.0f );
        assertTrue("SUM_FLOAT1 not correct value",
                ((Float) rec.get("SUM_FLOAT1")).floatValue() == 128.0f );
    }

    /*
     * Test a summary using a select statement
     */
    public void testSummarizeWithSelect() throws Exception {
        Criteria c = new Criteria();
        c.where(Summarize1Peer.GROUP_BY1, TEST_GROUPBY1[0]);

        SummaryHelper summary = new SummaryHelper();

        summary.setExcludeExprColumns(true);
        List<Class<?>> returnTypes = new ArrayList<>();

        summary.addAggregate("COUNT_RECS", new Count(Summarize1Peer.ID));
        returnTypes.add(Integer.class);

        summary.addAggregate("AVG_INT1", new Avg(Summarize1Peer.INT_1));
        returnTypes.add(Integer.class);

        summary.addAggregate("MIN_INT1", new Min(Summarize1Peer.INT_1));
        returnTypes.add(Integer.class);

        summary.addAggregate("MAX_INT1", new Max(Summarize1Peer.INT_1));
        returnTypes.add(Integer.class);

        summary.addAggregate("SUM_INT1", new Sum(Summarize1Peer.INT_1));
        returnTypes.add(Integer.class);

        summary.addAggregate("AVG_FLOAT1", new Avg(Summarize1Peer.FLOAT1));
        returnTypes.add(Float.class);

        summary.addAggregate("MIN_FLOAT1", new Min(Summarize1Peer.FLOAT1));
        returnTypes.add(Float.class);

        summary.addAggregate("MAX_FLOAT1", new Max(Summarize1Peer.FLOAT1));
        returnTypes.add(Float.class);

        summary.addAggregate("SUM_FLOAT1", new Sum(Summarize1Peer.FLOAT1));
        returnTypes.add(Float.class);

        List<ListOrderedMapCI<Object>> results = summary.summarize(c, returnTypes);

        StringWriter out = new StringWriter();
        summary.dumpResults(out, results, true);
        out.close();
        logger.debug("\n"+out.toString());

        assertTrue("No results returned", results.size() > 0 );
        assertTrue("Invalid number of records returned.  Expected 1 but got " +
                results.size(), results.size() == 1 );

        ListOrderedMapCI<Object> rec = results.get(0);

        assertTrue("COUNT_RECS not correct value",
                ((Integer) rec.get("COUNT_RECS")).intValue() == 4 );
        assertTrue("AVG_INT1 not correct value",
                ((Integer) rec.get("AVG_INT1")).intValue() == 2 );
        assertTrue("MIN_INT1 not correct value",
                ((Integer) rec.get("MIN_INT1")).intValue() == 1 );
        assertTrue("MAX_INT1 not correct value",
                ((Integer) rec.get("MAX_INT1")).intValue() == 4 );
        assertTrue("SUM_INT1 not correct value",
                ((Integer) rec.get("SUM_INT1")).intValue() == 10 );
    }

}
