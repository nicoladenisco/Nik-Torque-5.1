package org.apache.torque.datatypes;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.ClobType;
import org.apache.torque.test.peer.ClobTypePeer;

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
 * Tests the data type CLOB.
 * @version $Id: ClobTest.java 1879896 2020-07-15 15:03:46Z gk $
 */
public class ClobTest extends BaseDatabaseTestCase
{
    /**
     * Check that clob columns can be created and read.
     *
     * @throws Exception if the test fails
     */
    public void testCreateAndReadClobs() throws Exception
    {
        ClobTypePeer.doDelete(new Criteria());

        ClobType clobType = fillAndSaveClobType(
                new ClobType(),
                40000,
                "1234567890abcdefghijklmnopqrstuvwxyz");

        // read the ClobTypes from the database
        // and check the values against the original values
        List<ClobType> clobTypeList = ClobTypePeer.doSelect(new Criteria());
        assertEquals(1, clobTypeList.size());

        ClobType readClobType = clobTypeList.get(0);
        assertEquals(clobType.getClobValue(), readClobType.getClobValue());
    }

    /**
     * Check that clob columns can be updated and read correctly.
     *
     * @throws Exception if the test fails
     */
    public void testUpdateClobs() throws Exception
    {
        ClobTypePeer.doDelete(new Criteria());
        ClobType clobType = fillAndSaveClobType(
                new ClobType(),
                40000,
                "1234567890abcdefghijklmnopqrstuvwxyz");
        clobType = ClobTypePeer.doSelectSingleRecord(new Criteria());
        fillAndSaveClobType(
                clobType,
                50000,
                "0987654321abcdefghijklmnopqrstuvwxyz");

        // read the ClobTypes from the database
        // and check the values against the original values
        List<ClobType> clobTypeList = ClobTypePeer.doSelect(new Criteria());
        assertEquals(1, clobTypeList.size());

        ClobType readClobType = clobTypeList.get(0);
        assertEquals(clobType.getClobValue(), readClobType.getClobValue());
    }

    /**
     * Fills the Clob in a ClobType, writes it to to the database and returns it.
     *
     * @param clobType the clobType to fill and save, not null.
     * @param size the size of the clob.
     * @param charTemplate the template to use for filling, not null.
     *
     * @return the changed ClobType.
     *
     * @throws TorqueException if saving fails
     */
    private ClobType fillAndSaveClobType(
            ClobType clobType, int size, String charTemplate)
                    throws TorqueException
    {
        {
            StringBuffer chars = new StringBuffer();
            for (int i = 0; i < size; ++i)
            {
                chars.append(charTemplate.charAt(i % charTemplate.length()));
            }
            clobType.setClobValue(chars.toString());
        }
        clobType.save();
        return clobType;
    }
}
