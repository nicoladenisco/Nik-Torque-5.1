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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;

/**
 * Maps a record to a list of objects.
 * Either the database driver decides which object type is appropriate
 * for each column, or a list of classes can be defined.
 *
 * @version $Id: ObjectListMapper.java 1840416 2018-09-09 15:10:22Z tv $
 */
public class ObjectListMapper implements RecordMapper<List<Object>>
{
    /** Serial Version UID. */
    private static final long serialVersionUID = 1L;

    /** Zero value for byte. */
    private static final byte ZERO_BYTE = 0;

    /** Zero value for short. */
    private static final short ZERO_SHORT = 0;

    /**
     * Contains the classes to which the columns are mapped.
     * The size of the list determines how many columns are read.
     */
    private List<Class<?>> convertClasses = null;

    /**
     * Constructor. Determines the number of columns to map from the
     * result set's metadata (this may issue additional database queries).
     * The classes the result columns are mapped to are determined by the
     * database driver.
     */
    public ObjectListMapper()
    {
        // empty
    }

    /**
     * Constructor which sets the number of columns to map.
     * The classes the result columns are mapped to are determined by the
     * database driver.
     *
     * @param numberOfColumnsToMap how many columns should be mapped,
     *        or -1 to determine the number of columns to map from the
     *        result set's metadata (this may issue
     *        additional database queries).
     */
    public ObjectListMapper(final int numberOfColumnsToMap)
    {
        this.convertClasses = new ArrayList<>(numberOfColumnsToMap);
        if (numberOfColumnsToMap != -1)
        {
            for (int i = 0; i < numberOfColumnsToMap; ++i)
            {
                convertClasses.add(Object.class);
            }
        }
    }

    /**
     * Constructor which determines the number of columns to map
     * and the classes the result columns are mapped to.
     *
     * @param convertClasses the classes to which the columns should be mapped.
     *        The first column is mapped to the first class in the list and
     *        so on. May be null, in which case the number of columns to map
     *        and the mapped to classes are determined by the database driver.
     *        Known classes are: java.lang.reflect.Array, java.math.BigDecimal,
     *        java.io.InputStream, java.sql. Blob, Boolean, Byte, byte[],
     *        java.io.Reader, java.sql.Clob, java.sql.Date, Double, Float,
     *        Integer, Long, Object (i.e. let the database driver decide
     *        which class is returned), java.sql.Ref, Short, String,
     *        java.sql.Time, java.sql.Timestamp and java.net.URL.
     */
    public ObjectListMapper(final List<Class<?>> convertClasses)
    {
        if (convertClasses != null)
        {
            this.convertClasses = new ArrayList<>(convertClasses);
        }
    }

    /**
     * Maps the current row in the result set by reading all columns from
     * offset on to the end of the row and store an object for each column
     * in the result.
     *
     * @param resultSet the result set to map, not null.
     * @param offset the offset of this mapper within the result set.
     * @param criteria The criteria which created the result set, or null
     *        if not known. This parameter is not used by this record mapper.
     *
     * @return a list of mapped objects in the same order as the mappers
     *         were ordered, not null.
     *
     * @throws TorqueException if retrieving column values from the result set
     *         fails or if the number of available columns cannot be determined
     *         from the result set.
     */
    @Override
    public List<Object> processRow(
            final ResultSet resultSet,
            final int offset,
            final Criteria criteria)
                    throws TorqueException
    {
        try
        {
            int currentNumberOfColumnsToMap;
            if (convertClasses != null)
            {
                currentNumberOfColumnsToMap = convertClasses.size();
            }
            else
            {
                int resultSetLength = resultSet.getMetaData().getColumnCount();
                currentNumberOfColumnsToMap = resultSetLength - offset;
            }

            List<Object> result
                = new ArrayList<>(currentNumberOfColumnsToMap);
            for (int i = 0; i < currentNumberOfColumnsToMap; ++i)
            {
                Class<?> mapClass;
                if (convertClasses != null)
                {
                    mapClass = convertClasses.get(i);
                }
                else
                {
                    mapClass = Object.class;
                }
                Object columnValue;
                int columnIndex = i + offset + 1;
                if (mapClass.equals(Array.class))
                {
                    columnValue = resultSet.getArray(columnIndex);
                }
                else if (mapClass.equals(BigDecimal.class))
                {
                    columnValue = resultSet.getBigDecimal(columnIndex);
                }
                else if (mapClass.equals(InputStream.class))
                {
                    columnValue = resultSet.getBinaryStream(columnIndex);
                }
                else if (mapClass.equals(Blob.class))
                {
                    columnValue = resultSet.getBlob(columnIndex);
                }
                else if (mapClass.equals(Boolean.class))
                {
                    columnValue = resultSet.getBoolean(columnIndex);
                }
                else if (mapClass.equals(Byte.class))
                {
                    columnValue = Byte.valueOf(resultSet.getByte(columnIndex));
                    if (Byte.valueOf(ZERO_BYTE).equals(columnValue)
                            && resultSet.wasNull())
                    {
                        columnValue = null;
                    }
                }
                else if (mapClass.equals(byte[].class))
                {
                    columnValue = resultSet.getBytes(columnIndex);
                }
                else if (mapClass.equals(Reader.class))
                {
                    columnValue = resultSet.getCharacterStream(columnIndex);
                }
                else if (mapClass.equals(Clob.class))
                {
                    columnValue = resultSet.getClob(columnIndex);
                }
                else if (mapClass.equals(Date.class))
                {
                    columnValue = resultSet.getDate(columnIndex);
                }
                else if (mapClass.equals(Double.class))
                {
                    columnValue = Double.valueOf(resultSet.getDouble(columnIndex));
                    if (Double.valueOf(0d).equals(columnValue)
                            && resultSet.wasNull())
                    {
                        columnValue =  null;
                    }
                }
                else if (mapClass.equals(Float.class))
                {
                    columnValue = Float.valueOf(resultSet.getFloat(columnIndex));
                    if (Float.valueOf(0f).equals(columnValue)
                            && resultSet.wasNull())
                    {
                        columnValue =  null;
                    }
                }
                else if (mapClass.equals(Integer.class))
                {
                    columnValue = Integer.valueOf(resultSet.getInt(columnIndex));
                    if (Integer.valueOf(0).equals(columnValue)
                            && resultSet.wasNull())
                    {
                        columnValue = null;
                    }
                }
                else if (mapClass.equals(Long.class))
                {
                    columnValue = Long.valueOf(resultSet.getLong(columnIndex));
                    if (Long.valueOf(0L).equals(columnValue)
                            && resultSet.wasNull())
                    {
                        columnValue =  null;
                    }
                }
                else if (mapClass.equals(Object.class))
                {
                    columnValue = resultSet.getObject(columnIndex);
                }
                else if (mapClass.equals(Ref.class))
                {
                    columnValue = resultSet.getRef(columnIndex);
                }
                else if (mapClass.equals(Short.class))
                {
                    columnValue = Short.valueOf(resultSet.getShort(columnIndex));
                    if (Short.valueOf(ZERO_SHORT).equals(columnValue)
                            && resultSet.wasNull())
                    {
                        columnValue =  null;
                    }
                }
                else if (mapClass.equals(String.class))
                {
                    columnValue = resultSet.getString(columnIndex);
                }
                else if (mapClass.equals(Time.class))
                {
                    columnValue = resultSet.getTime(columnIndex);
                }
                else if (mapClass.equals(Timestamp.class))
                {
                    columnValue = resultSet.getTimestamp(columnIndex);
                }
                else if (mapClass.equals(URL.class))
                {
                    columnValue = resultSet.getURL(columnIndex);
                }
                else
                {
                    throw new IllegalArgumentException(
                            "Unknown convertClass " + mapClass.getName()
                            + " at position " + i);
                }
                result.add(columnValue);
            }
            return result;
        }
        catch (SQLException e)
        {
            throw new TorqueException(e);
        }
    }
}

