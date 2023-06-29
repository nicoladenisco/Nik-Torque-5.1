package org.apache.torque.sql.objectbuilder;

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

import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.criteria.PreparedStatementPart;
import org.apache.torque.sql.Query;

/**
 * Builds a PreparedStatementPart from a single object
 * (e.g. a column or a SQL value).
 *
 * @version $Id: ObjectPsPartBuilder.java 1839288 2018-08-27 09:48:33Z tv $
 */
public interface ObjectPsPartBuilder
{
    /**
     * Builds a PreparedStatementPart from a single Object.
     *
     * @param toBuildFrom the object to build the psPart from.
     * @param ignoreCase If true and columns represent Strings, the appropriate
     *        function defined for the database will be used to ignore
     *        differences in case.
     * @param query the query which is currently built
     * @param adapter The adapter for the database for which the SQL
     *        should be created, not null.
     *
     * @return the PreparedStatementPart for the object.
     *
     * @throws TorqueException when rendering fails.
     */
    PreparedStatementPart buildPs(
            final Object toBuildFrom,
            final boolean ignoreCase,
            final Query query,
            final Adapter adapter)
                    throws TorqueException;
}
