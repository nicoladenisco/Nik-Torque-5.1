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

import java.util.HashMap;
import java.util.Map;

import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.IDMethod;
import org.apache.torque.dsfactory.DataSourceFactory;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.TableMap;
import org.apache.torque.oid.IDBroker;
import org.apache.torque.oid.IdGenerator;

/**
 * Bundles all information about a database. This includes the database adapter,
 * the database Map and the Data Source Factory.
 */
public class Database
{
    /**
     * The name of the database.
     */
    private final String name;

    /**
     * The Database adapter which encapsulates database-specific peculiarities.
     */
    private Adapter adapter;

    /**
     * The Map of this database.
     */
    private DatabaseMap databaseMap;

    /**
     * The DataSourceFactory to obtain connections to this database.
     */
    private DataSourceFactory dataSourceFactory;

    /** The Schema name of this database, may be null if not set. */
    private String schema = null;

    /**
     * A special table used to generate primary keys for the other tables.
     */
    private TableMap idTable = null;

    /** The IDBroker that goes with the idTable. */
    private IDBroker idBroker = null;

    /** The IdGenerators, keyed by type of idMethod. */
    private final Map<IDMethod, IdGenerator> idGenerators
        = new HashMap<>();

    /**
     * Creates a new Database with the given name.
     *
     * @param name the name of the database, not null.
     *
     * @throws NullPointerException if name is null.
     */
    Database(final String name)
    {
        if (name == null)
        {
            throw new NullPointerException("name is null");
        }
        this.name = name;
        this.databaseMap = new DatabaseMap(this);
    }

    /**
     * Rturns the name of the database.
     *
     * @return the name of the database, not null.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the adapter to this database.
     *
     * @return the adapter to this database, or null if no adapter is set.
     */
    public Adapter getAdapter()
    {
        return adapter;
    }

    /**
     * Sets the adapter for this database.
     *
     * @param anAdapter The adapter for this database, or null to remove the
     *        current adapter from this database.
     */
    public void setAdapter(final Adapter anAdapter)
    {
        this.adapter = anAdapter;
    }

    /**
     * Returns the database map for this database.
     * 
     * @return the DatabaseMap for this database, or null
     * if no DatabaseMap exists for this database.
     */
    public DatabaseMap getDatabaseMap()
    {
        return databaseMap;
    }

    /**
     * Returns the DataSourceFactory for this database.
     * The DataSourceFactory is responsible to create connections
     * to this database.
     *
     * @return the DataSourceFactory for this database, or null if no
     *         DataSourceFactory exists for this database.
     */
    public DataSourceFactory getDataSourceFactory()
    {
        return dataSourceFactory;
    }

    /**
     * Sets the DataSourceFactory for this database.
     * The DataSourceFactory is responsible to create connections
     * to this database.
     *
     * @param aDataSourceFactory The new DataSorceFactory for this database,
     *        or null to remove the current DataSourceFactory.
     */
    public void setDataSourceFactory(final DataSourceFactory aDataSourceFactory)
    {
        this.dataSourceFactory = aDataSourceFactory;
    }

    /**
     * Get the ID table for this database.
     *
     * @return A TableMap, or null if not yet initialized or no id table exists
     *         for this database.
     */
    public TableMap getIdTable()
    {
        return idTable;
    }

    /**
     * Set the ID table for this database.
     *
     * @param idTable The TableMap representation for the ID table.
     */
    public void setIdTable(final TableMap idTable)
    {
        this.idTable = idTable;
        getDatabaseMap().setIdTable(idTable);
    }

    /**
     * Set the ID table for this database.
     *
     * @param tableName The name for the ID table.
     */
    public void setIdTable(final String tableName)
    {
        TableMap tmap = new TableMap(tableName, getDatabaseMap());
        setIdTable(tmap);
    }

    /**
     * Get the IDBroker for this database.
     *
     * @return The IDBroker for this database, or null if no IdBroker has
     *         been started for this database.
     */
    public IDBroker getIdBroker()
    {
        return idBroker;
    }

    /**
     * Creates the IDBroker for this Database and registers it with Torque.
     * so it is either started instantly if Torque is already initialized
     * or it is started when Torque is initialized.
     * The information about the IdTable is stored in the databaseMap.
     * If an IDBroker already exists for this Database, the method
     * does nothing.
     *
     * @return true if a new IDBroker was created, false otherwise.
     */
    public synchronized boolean createAndRegisterIdBroker()
    {
        if (idBroker != null)
        {
            return false;
        }
        setIdTable(IDBroker.ID_TABLE);
        TableMap tMap = getIdTable();
        ColumnMap idTableId = new ColumnMap(IDBroker.COL_TABLE_ID, tMap);
        idTableId.setType(Integer.valueOf(0));
        idTableId.setPrimaryKey(true);
        tMap.addColumn(idTableId);
        ColumnMap tableName = new ColumnMap(IDBroker.COL_TABLE_NAME, tMap);
        tableName.setType("");
        tMap.addColumn(tableName);
        ColumnMap nextId = new ColumnMap(IDBroker.COL_NEXT_ID, tMap);
        nextId.setType(Integer.valueOf(0));
        tMap.addColumn(nextId);
        ColumnMap quantity = new ColumnMap(IDBroker.COL_QUANTITY, tMap);
        quantity.setType(Integer.valueOf(0));
        tMap.addColumn(quantity);
        idBroker = new IDBroker(this);
        addIdGenerator(IDMethod.ID_BROKER, idBroker);
        return true;
    }

    /**
     * Returns the IdGenerator of the given type for this Database.
     *
     * @param type The type (i.e.name) of the IdGenerator.
     *
     * @return The IdGenerator of the requested type, or null if no IdGenerator
     *         exists for the requested type.
     */
    public IdGenerator getIdGenerator(final IDMethod type)
    {
        return idGenerators.get(type);
    }

    /**
     * Adds an IdGenerator to the database.
     *
     * @param type The type of the IdGenerator.
     * @param idGen The new IdGenerator for the type, or null
     *        to remove the IdGenerator of the given type.
     */
    public void addIdGenerator(final IDMethod type, final IdGenerator idGen)
    {
        idGenerators.put(type, idGen);
    }

    /**
     * Returns the database schema for this Database.
     *
     * @return the database schema for this database, or null if no schema
     *         has been set.
     */
    public String getSchema()
    {
        return schema;
    }

    /**
     * Sets the schema for this database.
     *
     * @param schema the name of the database schema to set, or null to remove
     *        the current schema.
     */
    public void setSchema(final String schema)
    {
        this.schema = schema;
    }
}
