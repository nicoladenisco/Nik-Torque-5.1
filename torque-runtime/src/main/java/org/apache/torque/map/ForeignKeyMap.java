package org.apache.torque.map;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ForeignKeyMap is used to model a foreign key in a database.
 *
 * @version $Id: ForeignKeyMap.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class ForeignKeyMap implements Serializable
{
    /**
     * Serial version.
     */
    private static final long serialVersionUID = 1L;

    /** The table to which the foreign key belongs, not null. */
    private final TableMap table;

    /**
     * The table which is referenced by the foreign key, can be null if
     * the foreign table is not yet in the database map.
     */
    private TableMap foreignTable;

    /**
     * The name of the foreign table, in the case that the foreign table
     * map builder was not yet built when this foreign key was initialized.
     */
    private String foreignTableName;

    /** The column pairs for the foreign key, not null, not empty, */
    private final List<ColumnPair> columns = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param table the local table, not null.
     * @param foreignTable the foreign table, not null.
     *
     * @throws NullPointerException if an argument is null.
     */
    public ForeignKeyMap(TableMap table, TableMap foreignTable)
    {
        if (table == null)
        {
            throw new NullPointerException("table is null");
        }
        if (foreignTable == null)
        {
            throw new NullPointerException("foreignTable is null");
        }
        this.table = table;
        this.foreignTable = foreignTable;
    }

    /**
     * Constructor.
     *
     * @param table the local table, not null.
     * @param foreignTableName the name of the foreign table, not null.
     *
     * @throws NullPointerException if an argument is null.
     */
    public ForeignKeyMap(TableMap table, String foreignTableName)
    {
        if (table == null)
        {
            throw new NullPointerException("table is null");
        }
        if (foreignTableName == null)
        {
            throw new NullPointerException("foreignTableName is null");
        }
        this.table = table;
        this.foreignTableName = foreignTableName;
    }

    /**
     * Adds a column pair to the foreign key.
     *
     * @param columnPair the column pair to add, not null.
     */
    public void addColumns(ColumnPair columnPair)
    {
        if (columnPair == null)
        {
            throw new NullPointerException("columnPair is null");
        }
        columns.add(columnPair);
    }

    /**
     * Returns the local table of the foreign key.
     *
     * @return the referencing table, not null.
     */
    public TableMap getTable()
    {
        return table;
    }

    /**
     * Returns the foreign table of the foreign key.
     *
     * @return the referenced table, not null.
     *
     * @throws IllegalStateException if the foreign table map builder
     *         was not yet built.
     */
    public TableMap getForeignTable()
    {
        if (foreignTable == null)
        {
            foreignTable = table.getDatabaseMap().getTable(foreignTableName);
            if (foreignTable == null)
            {
                throw new IllegalStateException(
                        "Map builder for "
                                + foreignTableName
                                + " was not yet built.");
            }
            foreignTableName = null;
        }
        return foreignTable;
    }

    /**
     * Returns name of the foreign table of the foreign key.
     *
     * @return the name of the referenced table, not null.
     */
    public String getForeignTableName()
    {
        if (foreignTable == null)
        {
            return foreignTableName;
        }
        return foreignTable.getName();
    }

    /**
     * Returns the column pairs.
     *
     * @return the column pairs, not null, as unmodifiable list.
     */
    public List<ColumnPair> getColumns()
    {
        return Collections.unmodifiableList(columns);
    }

    /**
     * A pair of local and Foreign column.
     * This class is immutable.
     */
    public static class ColumnPair implements Serializable
    {
        /**
         * Serial version.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The foreign key map this ColumnPair belongs to.
         */
        private final ForeignKeyMap foreignKeyMap;

        /**
         * The foreign column of the pair, not null.
         */
        private final ColumnMap local;

        /**
         * The foreign column of the pair, can be null if the
         * foreign table was not yet in the database.
         */
        private ColumnMap foreign;

        /**
         * The name of foreign column of the pair,
         * or null if the foreign column is already filled.
         */
        private String foreignName;

        /**
         * Constructor.
         *
         * @param foreignKeyMap the foreign key map this columnPait belongs to.
         * @param local the local column, not null.
         * @param foreign the foreign column, not null.
         *
         * @throws NullPointerException if local or doreign are null.
         */
        public ColumnPair(
                ForeignKeyMap foreignKeyMap,
                ColumnMap local,
                ColumnMap foreign)
        {
            if (foreignKeyMap == null)
            {
                throw new NullPointerException("foreignKeyMap is null");
            }
            if (local == null)
            {
                throw new NullPointerException("local is null");
            }
            if (foreign == null)
            {
                throw new NullPointerException("foreign is null");
            }
            this.foreignKeyMap = foreignKeyMap;
            this.local = local;
            this.foreign = foreign;
        }

        /**
         * Constructor.
         *
         * @param foreignKeyMap the foreign key map this columnPait belongs to.
         * @param local the local column, not null.
         * @param foreignName the foreign column, not null.
         *
         * @throws NullPointerException if local or doreign are null.
         */
        public ColumnPair(
                ForeignKeyMap foreignKeyMap,
                ColumnMap local,
                String foreignName)
        {
            if (foreignKeyMap == null)
            {
                throw new NullPointerException("foreignKeyMap is null");
            }
            if (local == null)
            {
                throw new NullPointerException("local is null");
            }
            if (foreignName == null)
            {
                throw new NullPointerException("foreignName is null");
            }
            this.foreignKeyMap = foreignKeyMap;
            this.local = local;
            this.foreignName = foreignName;
        }

        /**
         * Returns the associated foreign key map.
         *
         * @return the associated foreign key map, not null.
         */
        public ForeignKeyMap getForeignKeyMap()
        {
            return foreignKeyMap;
        }

        /**
         * Returns the local column of the pair.
         *
         * @return the local column of the pair, not null.
         */
        public ColumnMap getLocal()
        {
            return local;
        }

        /**
         * Returns the foreign column of the pair.
         *
         * @return the foreign column of the pair, not null.
         *
         * @throws IllegalStateException if the foreign table map builder
         *         was not yet built.
         */
        public ColumnMap getForeign()
        {
            if (foreign == null)
            {
                TableMap foreignTable =  foreignKeyMap.getForeignTable();
                foreign = foreignTable.getColumn(foreignName);
                if (foreign == null)
                {
                    throw new IllegalStateException(
                            "Table " + foreignKeyMap.getForeignTableName()
                            + " has no column named " + foreignName);
                }
            }
            return foreign;
        }
    }
}
