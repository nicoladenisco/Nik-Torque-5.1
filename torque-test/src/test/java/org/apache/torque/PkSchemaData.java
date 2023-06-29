package org.apache.torque;

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

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.Nopk;
import org.apache.torque.test.peer.NopkPeer;

/**
 * Contains data (i.e. table records) for the tables in the pk schema.
 *
 * @version $Id: PkSchemaData.java 1840416 2018-09-09 15:10:22Z tv $
 */
public class PkSchemaData
{
    private List<Nopk> nopkList
        = new ArrayList<>();

    /**
     * Creates the default Test data for the pk schema.
     * The test data is filled as follows (p=primitive, o=object):
     *
     * nopk1
     * nopk2
     * nopk3
     *
     *
     * @return a new instance filled with the default test data.
     *
     * @throws TorqueException should not occur.
     */
    public static PkSchemaData getDefaultTestData()
            throws TorqueException
    {
        PkSchemaData result = new PkSchemaData();

        fillNopks(result);

        return result;
    }

    private static void fillNopks(PkSchemaData result)
    {
        Nopk nopk1 = new Nopk();
        nopk1.setName("nopk1");
        nopk1.setIntcol(1);
        result.getNopkList().add(nopk1);

        Nopk nopk2 = new Nopk();
        nopk2.setName("nopk2");
        nopk2.setIntcol(2);
        result.getNopkList().add(nopk2);

        Nopk nopk3 = new Nopk();
        nopk3.setName("nopk3");
        nopk3.setIntcol(3);
        result.getNopkList().add(nopk3);
    }


    public List<Nopk> getNopkList()
    {
        return nopkList;
    }

    /**
     * Saves all contained data if the data is new or was changed
     * after the last save.
     *
     * @throws TorqueException If saving fails.
     */
    public void save() throws TorqueException
    {
        for (Nopk nopk : nopkList)
        {
            nopk.save();
        }
    }

    /**
     * Deletes all records in the  pk schema's tables.
     *
     * @throws TorqueException if the tables could not be cleaned
     */
    public static void clearTablesInDatabase() throws TorqueException
    {
        Criteria criteria = new Criteria();
        NopkPeer.doDelete(criteria);
    }
}
