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

import java.sql.Connection;
import java.util.List;

import org.apache.torque.criteria.Criteria;
import org.apache.torque.om.ObjectKey;

public interface OptimisticLockingPeerInterface<T>
{
    int doUpdate(T toUpdate) throws TorqueException;

    int doUpdate(T toUpdate, Connection con) throws TorqueException;

    T doSelectSingleRecord(T toSelect) throws TorqueException;

    T retrieveByPK(ObjectKey<?> key) throws TorqueException;

    int doDelete(T toDelete) throws TorqueException;

    int doDelete(Criteria criteria) throws TorqueException;

    List<T> doSelect(Criteria criteria) throws TorqueException;
}
