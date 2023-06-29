package org.apache.torque.oid;

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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.om.mapper.BigDecimalMapper;
import org.apache.torque.om.mapper.IntegerMapper;
import org.apache.torque.om.mapper.LongMapper;
import org.apache.torque.om.mapper.RecordMapper;
import org.apache.torque.om.mapper.StringMapper;
import org.apache.torque.sql.SqlBuilder;
import org.apache.torque.util.BasePeerImpl;

/**
 * This class serves as a common base class for the sequence-based and the
 * autoincrement-based id generators
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: AbstractIdGenerator.java 1850965 2019-01-10 17:21:29Z painter $
 */
public abstract class AbstractIdGenerator implements IdGenerator
{
    /** The adapter that knows the correct sql syntax */
    protected Adapter adapter;

    /**
     * The internal name of the Database that this Generator is connected to.
     */
    protected String databaseName = null;

    /**
     * Creates an IdGenerator which will work with the specified database.
     *
     * @param adapter the adapter that knows the correct sql syntax.
     * @param databaseName The name of the databaseName to find the correct
     *        schema.
     */
    public AbstractIdGenerator(final Adapter adapter, final String databaseName)
    {
        this.adapter = adapter;
        this.databaseName = databaseName;
    }

    /**
     * Returns the last ID used by this connection.
     *
     * @param connection The database connection to read the new id, not null.
     * @param keyInfo the name of the table for which the id is retrieved.
     *
     * @return An int with the new id.
     *
     * @throws TorqueException if a database error occurs.
     */
    @Override
    public int getIdAsInt(final Connection connection, final Object keyInfo)
            throws TorqueException
    {
        return getId(connection, keyInfo, new IntegerMapper());
    }

    /**
     * Returns the last ID used by this connection.
     *
     * @param connection The database connection to read the new id, not null.
     * @param keyInfo the name of the table for which the id is retrieved.
     *
     * @return A long with the new id.
     *
     * @throws TorqueException if a database error occurs.
     */
    @Override
    public long getIdAsLong(final Connection connection, final Object keyInfo)
            throws TorqueException
    {
        return getId(connection, keyInfo, new LongMapper());
    }

    /**
     * Returns the last ID used by this connection.
     *
     * @param connection The database connection to read the new id, not null.
     * @param keyInfo the name of the table for which the id is retrieved.
     *
     * @return A BigDecimal with the new id.
     *
     * @throws TorqueException if a database error occurs.
     */
    @Override
    public BigDecimal getIdAsBigDecimal(final Connection connection, final Object keyInfo)
            throws TorqueException
    {
        return getId(connection, keyInfo, new BigDecimalMapper());
    }


    /**
     * Returns the last ID used by this connection.
     *
     * @param connection The database connection to read the new id, not null.
     * @param keyInfo the name of the table for which the id is retrieved.
     *
     * @return A String with the new id.
     *
     * @throws TorqueException if a database error occurs.
     */
    @Override
    public String getIdAsString(final Connection connection, final Object keyInfo)
            throws TorqueException
    {
        return getId(connection, keyInfo, new StringMapper());
    }

    /**
     * A flag to determine the timing of the id generation
     *
     * @return a <code>boolean</code> value
     */
    @Override
    public abstract boolean isPriorToInsert();

    /**
     * A flag to determine the timing of the id generation
     *
     * @return a <code>boolean</code> value
     */
    @Override
    public abstract boolean isPostInsert();

    /**
     * A flag to determine whether a Connection is required to
     * generate an id.
     *
     * @return a <code>boolean</code> value
     */
    @Override
    public abstract boolean isConnectionRequired();

    /**
     * A flag to determine whether Statement#getGeneratedKeys()
     * should be used.
     *
     * @return a <code>boolean</code> value
     */
    @Override
    public abstract boolean isGetGeneratedKeysSupported();

    /**
     * Returns the last ID used by this connection.
     *
     * @param <T> the id object class
     * @param connection A Connection.
     * @param keyInfo an Object that contains additional info.
     * @param mapper The RecordMapper that maps from a ResultSet to the
     *        appropriate java object.
     *
     * @return The generated id.
     * @throws TorqueException if a database error occurs.
     */
    protected <T> T getId(
            final Connection connection,
            final Object keyInfo,
            final RecordMapper<T> mapper)
                    throws TorqueException
    {
        if (isGetGeneratedKeysSupported() && keyInfo instanceof Statement)
        {
            try (ResultSet generatedKeys = ((Statement) keyInfo).getGeneratedKeys())
            {
                if (generatedKeys.next())
                {
                    return mapper.processRow(generatedKeys, 0, null);
                }
                else
                {
                    throw new SQLException("Could not obtain ID from statement, result set is empty");
                }
            }
            catch (SQLException e)
            {
                throw new TorqueException("Error getting ID", e);
            }
        }
        else
        {
            String idSql = getIdSql(keyInfo);

            BasePeerImpl<T> peer = new BasePeerImpl<>(mapper, null, databaseName);
            List<T> result = peer.doSelect(idSql, connection);
            return result.get(0);
        }
    }

    /**
     * Returns the SQL to retrieve the next id.
     *
     * @param keyInfo an Object that contains additional info.
     *
     * @return the SQL to retrieve the next id.
     *
     * @throws TorqueException if a database error occurs.
     */
    public String getIdSql(final Object keyInfo) throws TorqueException
    {
        String tableName = SqlBuilder.getFullTableName(
                String.valueOf(keyInfo),
                databaseName);
        String idSql = adapter.getIDMethodSQL(tableName);
        return idSql;
    }
}
