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
import java.util.ArrayList;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;

/**
 * Uses a list of mappers to map a database record to a list of objects.
 *
 * @version $Id: CompositeMapper.java 1840416 2018-09-09 15:10:22Z tv $
 */
public class CompositeMapper implements RecordMapper<List<Object>>
{
    /** Serial Version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * All mappers which should be appliead to a result set, combined with
     * their individual offset.
     */
    private final List<MapperWithOffset> mapperList
        = new ArrayList<>();

    /**
     * Adds a new mapper to be applied to a result set.
     *
     * @param mapper the mapper.
     * @param offset the offset of the mapper within this mapper, 0 based.
     */
    public void addMapper(final RecordMapper<?> mapper, final int offset)
    {
        mapperList.add(new MapperWithOffset(mapper, offset));
    }

    /**
     * Maps the current row in the result Set by applying all known mappers
     * and putting the result of each mapper in the result list.
     *
     * @param resultSet the result set to map, not null.
     * @param offset the total column offset of this mapper
     *        within the result set.
     * @param criteria The criteria which created the result set, or null
     *        if not known. This parameter is not used by this record mapper.
     *
     * @return a list of mapped objects in the same order as the mappers
     *         were ordered, not null.
     */
    @Override
    public List<Object> processRow(
            final ResultSet resultSet,
            final int offset,
            final Criteria criteria)
                    throws TorqueException
    {
        List<Object> result = new ArrayList<>(mapperList.size());
        for (MapperWithOffset mapperWithOffset : mapperList)
        {
            int totalOffset = offset +  mapperWithOffset.getOffset();
            RecordMapper<?> mapper = mapperWithOffset.getMapper();
            result.add(mapper.processRow(resultSet, totalOffset, criteria));
        }
        return result;
    }

    /** Contains a row mapper plus the internal offset of the mapper. */
    private static class MapperWithOffset implements Serializable
    {
        /** Serial Version UID. */
        private static final long serialVersionUID = 1L;

        /** The row mapper. */
        private final RecordMapper<?> mapper;

        /** The offset. */
        private final int offset;

        /**
         * Constructor.
         *
         * @param mapper the mapper, not null.
         * @param offset the internal offset of the mapper.
         */
        public MapperWithOffset(final RecordMapper<?> mapper, final int offset)
        {
            this.mapper = mapper;
            this.offset = offset;
        }

        /**
         * Returns the mapper.
         *
         * @return the mapper.
         */
        public RecordMapper<?> getMapper()
        {
            return mapper;
        }

        /**
         * Returns the offset.
         *
         * @return the offset.
         */
        public int getOffset()
        {
            return offset;
        }
    }
}

