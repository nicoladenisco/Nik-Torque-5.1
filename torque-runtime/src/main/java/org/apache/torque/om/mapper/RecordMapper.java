package org.apache.torque.om.mapper;

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

import java.io.Serializable;
import java.sql.ResultSet;

import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;

/**
 * Maps an object to a database record and back.
 * This means that the object can be read from a database resultSet
 * and that it can produce PreparedStatements which insert or update
 * the record in the the database.
 *
 * @param <T> the class to map from and to a database record
 *
 * @version $Id: RecordMapper.java 1839288 2018-08-27 09:48:33Z tv $
 */
@FunctionalInterface
public interface RecordMapper<T> extends Serializable
{
    /**
     * Constructs the object from the current row in the resultSet.
     * Implementing methods can be sure that the resultSet contains a row,
     * but they must only operate on the current row, i.e they must not call
     * resultSet.next().
     *
     * @param resultSet the resultSet to operate on, already pointing
     *        to the correct row. Not null.
     * @param rowOffset a possible offset in the columns to be considered
     *        (if previous columns contain other objects), or 0 for no offset.
     * @param criteria the Criteria which contains the query to process,
     *        or null if not known or the query was not produced by a Criteria.
     *        Can be used by the RecordMapper to determine the columns
     *        contained in the result set.
     *
     * @return the mapped object, not null.
     *
     * @throws TorqueException when the mapping fails.
     */
    T processRow(
            ResultSet resultSet,
            int rowOffset,
            Criteria criteria)
                    throws TorqueException;
}
