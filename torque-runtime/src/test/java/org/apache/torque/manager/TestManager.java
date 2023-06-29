package org.apache.torque.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.om.ObjectKey;

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
 * Test the manager implementation
 *
 */
public class TestManager extends AbstractBaseManager<TestPersistent>
{
    /** Serial number */
    private static final long serialVersionUID = 1021912588603493352L;

    @Override
    protected TestPersistent retrieveStoredOM(ObjectKey<?> id)
            throws TorqueException
    {
        TestPersistent test = new TestPersistent();
        test.setPrimaryKey(id);
        return test;
    }

    @Override
    protected List<TestPersistent> retrieveStoredOMs(
            List<? extends ObjectKey<?>> ids) throws TorqueException
    {
        List<TestPersistent> testList = new ArrayList<>(ids.size());

        for (ObjectKey<?> id : ids)
        {
            testList.add(retrieveStoredOM(id));
        }

        return testList;
    }
}
