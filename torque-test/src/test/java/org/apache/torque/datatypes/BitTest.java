package org.apache.torque.datatypes;

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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.DerbyAdapter;
import org.apache.torque.adapter.OracleAdapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.om.StringKey;
import org.apache.torque.test.dbobject.BitCompositePk;
import org.apache.torque.test.dbobject.BitType;
import org.apache.torque.test.peer.BitCompositePkPeer;
import org.apache.torque.test.peer.BitTypePeer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the data types BIT.
 * @version $Id: BitTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class BitTest extends BaseDatabaseTestCase
{
    private static Log log = LogFactory.getLog(BitTest.class);

    /**
     * Test whether we can insert into Tables with BIT PKs and BIT values.
     *
     * @throws Exception if anything in the test goes wrong.
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testInsertBitPk(Adapter adapter) throws Exception
    {
        if (!isBitSupported(adapter))
        {
            // failing is "expected", so exit without error
            return;
        }
        fillTables();

        List<BitCompositePk> bitCompositePks
        = BitCompositePkPeer.doSelect(new Criteria());
        assertEquals(3, bitCompositePks.size());
        List<BitType> bitTypes
        = BitTypePeer.doSelect(new Criteria());
        assertEquals(2, bitTypes.size());
    }

    /**
     * Test whether we can select values using PK values
     *
     * @throws Exception if anything in the test goes wrong.
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testSelectUsingBitPk(Adapter adapter) throws Exception
    {
        if (!isBitSupported(adapter))
        {
            // failing is "expected", so exit without error
            return;
        }
        fillTables();

        // check we get correct result when pks match
        Criteria criteria = new Criteria();
        criteria.and(BitCompositePkPeer.PK1, "false value");
        criteria.and(BitCompositePkPeer.PK2, Boolean.FALSE);
        List<BitCompositePk> result
        = BitCompositePkPeer.doSelect(criteria);
        assertEquals(1, result.size());
        assertEquals("false payload", result.get(0).getPayload());

        criteria = new Criteria();
        criteria.and(BitCompositePkPeer.PK1, "true value");
        criteria.and(BitCompositePkPeer.PK2, Boolean.TRUE);
        result = BitCompositePkPeer.doSelect(criteria);
        assertEquals(1, result.size());
        assertEquals("true payload", result.get(0).getPayload());
    }

    /**
     * Test we can update values in a table where a BIT pk is used.
     *
     * @throws Exception if anything in the test goes wrong.
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testUpdateWithBitPk(Adapter adapter) throws Exception
    {
        if (!isBitSupported(adapter))
        {
            // failing is "expected", so exit without error
            return;
        }
        fillTables();

        // check we get correct result when pks match

        // check updating works
        Criteria criteria = new Criteria().and(
                BitCompositePkPeer.PK1, "true value");
        criteria.and(BitCompositePkPeer.PK2, Boolean.TRUE);
        List<BitCompositePk> selectResult
        = BitCompositePkPeer.doSelect(criteria);
        selectResult.get(0).setPayload("true updated payload");
        selectResult.get(0).save();

        criteria = new Criteria();
        criteria.and(BitCompositePkPeer.PK1, "true value");
        criteria.and(BitCompositePkPeer.PK2, Boolean.TRUE);
        selectResult = BitCompositePkPeer.doSelect(criteria);
        assertEquals(1, selectResult.size());
        assertEquals("true updated payload", selectResult.get(0).getPayload());
    }

    /**
     * Tests whether column type BIT can be written and read correctly
     * and works in criteria as expected
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testReadBitValue(Adapter adapter) throws Exception
    {
        if (!isBitSupported(adapter))
        {
            // failing is "expected", so exit without error
            return;
        }
        fillTables();

        // read data
        BitType bitType = BitTypePeer.doSelectSingleRecord(
                new Criteria().where(BitTypePeer.ID, "t1"));
        assertTrue("BIT should be true but is: "
                + bitType.getBitValue(), bitType.getBitValue());

        bitType = BitTypePeer.retrieveByPK(new StringKey("f1"));
        assertFalse("BIT should be false but is: "
                + bitType.getBitValue(), bitType.getBitValue());
    }

    /**
     * Tests whether column type BIT works if set to true in Criteria.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testSelectBitTypeByTrueValue(Adapter adapter) throws Exception
    {
        if (!isBitSupported(adapter))
        {
            // failing is "expected", so exit without error
            return;
        }
        fillTables();

        // query data
        Criteria criteria = new Criteria()
                .where(BitTypePeer.BIT_VALUE, new Boolean(true));
        List<BitType> bitTypeList = BitTypePeer.doSelect(criteria);
        assertEquals(1, bitTypeList.size());
        BitType bitType = bitTypeList.get(0);
        // use trim() for testkey because some databases will return the
        // testkey filled up with blanks, as it is defined as char(10)
        assertEquals("t1", bitType.getId());
    }

    /**
     * Tests whether column type BIT works if set to false in Criteria.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testSelectBitTypeByFalseValue(Adapter adapter) throws Exception
    {
        if (!isBitSupported(adapter))
        {
            // failing is "expected", so exit without error
            return;
        }
        fillTables();

        Criteria criteria = new Criteria().where(
                BitTypePeer.BIT_VALUE, new Boolean(false));
        List<BitType> bitTypeList = BitTypePeer.doSelect(criteria);
        assertTrue("Should have read 1 dataset "
                + "but read " + bitTypeList.size(),
                bitTypeList.size() == 1);
        BitType bitValue = bitTypeList.get(0);
        assertTrue("Primary key of data set should be f1 but is "
                + bitValue.getId(),
                "f1".equals(bitValue.getId()));
    }

    /**
     * Checks whether the BIT type is supported in the database.
     * If not, a message is logged in the log file.
     *
     * @return true if the type is supported, false otherwise.
     */
    private boolean isBitSupported(Adapter adapter)
    {
        if (adapter instanceof OracleAdapter
                || adapter instanceof DerbyAdapter)
        {
            log.warn("isBitSupported(): "
                    + "BIT is known not to work with "
                    + "Oracle and Derby");
            return false;
        }
        return true;
    }

    /**
     * Delete all previous data from the tested tables
     * and re-inserts test data.
     */
    private void fillTables() throws TorqueException
    {
        BitCompositePkPeer.doDelete(new Criteria());
        BitTypePeer.doDelete(new Criteria());

        BitCompositePk bitCompositePk = new BitCompositePk();
        bitCompositePk.setPk1("false value");
        bitCompositePk.setPk2(Boolean.FALSE);
        bitCompositePk.setPayload("false payload");
        bitCompositePk.save();

        bitCompositePk = new BitCompositePk();
        bitCompositePk.setPk1("true value");
        bitCompositePk.setPk2(Boolean.TRUE);
        bitCompositePk.setPayload("true payload");
        bitCompositePk.save();

        bitCompositePk = new BitCompositePk();
        bitCompositePk.setPk1("value");
        bitCompositePk.setPk2(Boolean.TRUE);
        bitCompositePk.setPayload("payload");
        bitCompositePk.save();

        BitType bitType = new BitType();
        bitType.setId("t1");
        bitType.setBitValue(true);
        bitType.save();

        bitType = new BitType();
        bitType.setId("f1");
        bitType.setBitValue(false);
        bitType.save();
    }
}
