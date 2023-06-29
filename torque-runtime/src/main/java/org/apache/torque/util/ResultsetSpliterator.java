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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;

import org.apache.torque.Database;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.TorqueRuntimeException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.om.mapper.RecordMapper;

/**
 * Stream support: Encapsulate iteration over a JDBC ResultSet
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class ResultsetSpliterator<T> extends AbstractSpliterator<T> implements Runnable
{
    private final RecordMapper<T> recordMapper;
    private final Criteria criteria;
    private final Statement statement;
    private final ResultSet resultSet;

    private long offset;
    private long limit;
    private long rowNumber;

    /**
     * Constructor
     *
     * @param recordMapper a RecordMapper to map ResultSet rows to entities of type T
     * @param criteria     a Criteria
     * @param statement    the statement that created the ResultSet
     * @param resultSet    the JDBC result set
     * @throws TorqueException backend database exception
     */
    public ResultsetSpliterator(RecordMapper<T> recordMapper, Criteria criteria,
            Statement statement, ResultSet resultSet) throws TorqueException
    {
        super(Long.MAX_VALUE, Spliterator.ORDERED);

        this.recordMapper = recordMapper;
        this.criteria = criteria;
        this.statement = statement;
        this.resultSet = resultSet;
        this.offset = 0; //database takes care of offset
        this.limit = -1; //database takes care of limit
        this.rowNumber = 0;

        // Set offset and limit
        if (criteria != null)
        {
            Database database = Torque.getDatabase(criteria.getDbName());
            if (!database.getAdapter().supportsNativeOffset())
            {
                offset = criteria.getOffset();
            }

            if (!database.getAdapter().supportsNativeLimit())
            {
                if (database.getAdapter().supportsNativeOffset())
                {
                    limit = criteria.getLimit();
                }
                else if (criteria.getLimit() != -1)
                {
                    limit = offset + criteria.getLimit();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see java.util.Spliterator#tryAdvance(java.util.function.Consumer)
     * 
     * Advance ResultSet and map row to entity &lt;T&gt;
     */
    @Override
    public boolean tryAdvance(Consumer<? super T> action)
    {
        try
        {
            while (resultSet.next())
            {
                if (rowNumber < offset)
                {
                    rowNumber++;
                    continue;
                }
                if (limit >= 0 && rowNumber >= limit)
                {
                    return false;
                }

                rowNumber++;
                T result = recordMapper.processRow(resultSet, 0, criteria);
                action.accept(result);
                return true;
            }
        }
        catch (SQLException e)
        {
            throw new TorqueRuntimeException(e);
        }

        return false;
    }

    /**
     * Method to be run onClose() of associated stream
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        try
        {
            resultSet.close();
        }
        catch (SQLException e)
        {
            throw new TorqueRuntimeException(e);
        }
        try
        {
            statement.close();
        }
        catch (SQLException e)
        {
            throw new TorqueRuntimeException(e);
        }
    }
}
