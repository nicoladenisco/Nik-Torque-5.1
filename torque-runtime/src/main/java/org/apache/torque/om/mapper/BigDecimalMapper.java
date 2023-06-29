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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;

/**
 * Maps a database record to a BigDecimal.
 *
 * @version $Id: BigDecimalMapper.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class BigDecimalMapper implements RecordMapper<BigDecimal>
{
    /** Serial Version UID. */
    private static final long serialVersionUID = 1L;

    /** The internal offset for the mapper. */
    private final int internalOffset;

    /**
     * Constructs a BigDecimalMapper with an offset of 0.
     */
    public BigDecimalMapper()
    {
        internalOffset = 0;
    }

    /**
     * Constructs a BigDecimalMapper with the given offset.
     *
     * @param offset the additional offset (0 based).
     */
    public BigDecimalMapper(final int offset)
    {
        this.internalOffset = offset;
    }

    /**
     * Maps the current row in the result to a BigDecimal.
     *
     * @param resultSet the result set to map, not null.
     * @param rowOffset a possible offset in the columns to be considered
     *        (if previous columns contain other objects), or 0 for no offset.
     * @param criteria The criteria which created the result set, or null
     *        if not known. This parameter is not used by this record mapper.
     *
     * @return the BigDecimal retrieved from the result set.
     */
    @Override
    public BigDecimal processRow(
            final ResultSet resultSet,
            final int rowOffset,
            final Criteria criteria)
                    throws TorqueException
    {
        try
        {
            return resultSet.getBigDecimal(rowOffset + internalOffset + 1);
        }
        catch (SQLException e)
        {
            throw new TorqueException(
                    "Result could not be mapped to a BigDecimal",
                    e);
        }
    }
}
