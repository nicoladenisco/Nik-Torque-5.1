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

import java.sql.Connection;
import java.util.List;

import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.map.TableMap;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.ObjectModel;
import org.apache.torque.om.mapper.RecordMapper;

/**
 * This is an abstract layer for all generated peer classes that
 * implements several convenience methods that otherwise would have
 * to be generated.
 *
 * @param <T> The data object class for this Peer.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: BasePeerImpl.java 1701342 2015-09-05 02:18:04Z tfischer $
 */
public abstract class AbstractPeerImpl<T extends ObjectModel> extends BasePeerImpl<T>
{
    /** Serial version */
    private static final long serialVersionUID = 1236684692145864194L;

    /**
     * Default constructor
     */
    public AbstractPeerImpl()
    {
        super();
    }

    /**
     * Constructor providing the objects to be injected as parameters.
     *
     * @param recordMapper a record mapper to map JDBC result sets to objects
     * @param tableMap the default table map
     * @param databaseName the name of the database
     */
    public AbstractPeerImpl(final RecordMapper<T> recordMapper, final TableMap tableMap, final String databaseName)
    {
        super(recordMapper, tableMap, databaseName);
    }

    /**
     * Build a Criteria object from the data object for this peer.
     *
     * @param obj the object to build the criteria from, not null.
     * @return Criteria object
     */
    public abstract Criteria buildCriteria(T obj);

    /**
     * Build a Criteria object from the data object for this peer,
     * skipping all binary columns.
     *
     * @param obj the object to build the criteria from, not null.
     * @return Criteria object
     */
    public abstract Criteria buildSelectCriteria(T obj);

    /**
     * Build a Criteria object which selects all objects which have a given
     * primary key.
     *
     * This method should never be called because if the table has a primary key,
     * it must be overridden in the generated code.
     *
     * @param pk the primary key value to build the criteria from, not null.
     * @return Criteria object
     */
    public Criteria buildCriteria(ObjectKey<?> pk)
    {
        throw new RuntimeException("buildCriteria(ObjectKey) called on table without primary key");
    }

    /**
     * Returns the contents of the object as ColumnValues object.
     * Primary key columns which are generated on insertion are not
     * added to the returned object if they still have their initial
     * value. Also, columns which have the useDatabaseDefaultValue
     * flag set to true are also not added to the returned object
     * if they still have their initial value.
     * 
     * @param obj the object to build the column values from
     * @return ColumnValues object
     *
     * @throws TorqueException if the table map cannot be retrieved
     *         (should not happen).
     */
    public abstract ColumnValues buildColumnValues(T obj) throws TorqueException;

    /**
     * Selects objects from the database which have
     * the same content as the passed object.
     *
     * @param obj the data object
     * @return The list of selected objects, not null.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public List<T> doSelect(T obj) throws TorqueException
    {
        return doSelect(buildSelectCriteria(obj));
    }

    /**
     * Selects at most one object from the database
     * which has the same content as the passed object.
     * 
     * @param obj the data object
     * @return the selected Object, or null if no object was selected.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public T doSelectSingleRecord(T obj) throws TorqueException
    {
        List<T> omList = doSelect(obj);
        T om = null;
        if (omList.size() > 1)
        {
            throw new TooManyRowsException("Object " + obj
                    + " matched more than one record");
        }
        if (!omList.isEmpty())
        {
            om = omList.get(0);
        }
        return om;
    }

    /**
     * Method to do inserts.  This method is to be used during a transaction,
     * otherwise use the doInsert(Criteria) method.
     *
     * @param columnValues the values to insert.
     * @param con the connection to use, not null.
     *
     * @return the primary key of the inserted row or null if the table has
     * no primary key
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    @Override
    public ObjectKey<?> doInsert(ColumnValues columnValues, Connection con)
            throws TorqueException
    {
        correctBooleans(columnValues);
        return super.doInsert(columnValues, con);
    }

    /**
     * Method to do inserts
     * 
     * @param obj the data object
     * 
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public void doInsert(T obj) throws TorqueException
    {
        ObjectKey<?> primaryKey = doInsert(buildColumnValues(obj));
        if (primaryKey != null)
        {
            obj.setPrimaryKey(primaryKey);
        }
        obj.setNew(false);
        obj.setModified(false);
    }

    /**
     * Method to do inserts.  This method is to be used during a transaction,
     * otherwise use the doInsert(T) method.  It will take
     * care of the connection details internally.
     * 
     * @param obj the data object to insert into the database.
     * @param con the connection to use
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public void doInsert(T obj, Connection con)
            throws TorqueException
    {
        ObjectKey<?> primaryKey = doInsert(buildColumnValues(obj), con);
        if (primaryKey != null)
        {
            obj.setPrimaryKey(primaryKey);
        }
        obj.setNew(false);
        obj.setModified(false);
    }

    /**
     * Updates an object in the database.
     * The primary key is used to identify the object to update.
     *
     * @param obj the data object to update in the database.
     *
     * @return the number of affected rows.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public int doUpdate(T obj) throws TorqueException
    {
        ColumnValues columnValues = buildColumnValues(obj);
        int result = doUpdate(columnValues);
        obj.setModified(false);
        return result;
    }

    /**
     * Updates an object in the database.
     * The primary key is used to identify the object to update.
     * This method is to be used during a transaction,
     * otherwise the doUpdate(T) method can be used.
     *
     * @param obj the data object to update in the database.
     * @param con the connection to use, not null.

     * @return the number of affected rows.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public int doUpdate(T obj, Connection con)
            throws TorqueException
    {
        ColumnValues columnValues = buildColumnValues(obj);
        int result = doUpdate(columnValues, con);
        obj.setModified(false);
        return result;
    }

    /**
     * Deletes a row in the database.
     *
     * @param pk the ObjectKey that identifies the row to delete.
     *
     * @return the number of deleted rows.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public int doDelete(ObjectKey<?> pk) throws TorqueException
    {
        try (TorqueConnection connection = Transaction.begin(getDatabaseName()))
        {
            int deletedRows = doDelete(pk, connection);
            Transaction.commit(connection);
            return deletedRows;
        }
    }

    /**
     * Deletes a row in the database.
     * This method is to be used during a transaction,
     * otherwise use the doDelete(ObjectKey) method.
     *
     * @param pk the ObjectKey that identifies the row to delete.
     * @param con the connection to use for deleting, not null.
     *
     * @return the number of deleted rows.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public int doDelete(ObjectKey<?> pk, Connection con)
            throws TorqueException
    {
        return doDelete(buildCriteria(pk), con);
    }
}
