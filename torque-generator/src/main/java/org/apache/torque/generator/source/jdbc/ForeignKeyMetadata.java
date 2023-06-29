package org.apache.torque.generator.source.jdbc;

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

import java.util.ArrayList;
import java.util.List;

/**
 * The data about a foreign key as read from JDBC Metadata.
 *
 * @version $Id: ForeignKeyMetadata.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class ForeignKeyMetadata
{
    /** The name of the referenced (foreign) table. */
    private String referencedTable;

    /** The name of the foreign key. */
    private String foreignKeyName;

    /** The local columns of the foreign key. */
    private List<String> localColumns = new ArrayList<>();

    /** The foreign columns of the foreign key. */
    private List<String> foreignColumns = new ArrayList<>();

    /**
     * Returns the name of the referenced (foreign) table.
     *
     * @return the name of the referenced (foreign) table.
     */
    public String getReferencedTable()
    {
        return referencedTable;
    }

    /**
     * Sets the name of the referenced (foreign) table.
     *
     * @param referencedTable the name of the referenced (foreign) table.
     */
    public void setReferencedTable(String referencedTable)
    {
        this.referencedTable = referencedTable;
    }

    /**
     * Returns the name of the foreign key.
     *
     * @return the name of the foreign key.
     */
    public String getForeignKeyName()
    {
        return foreignKeyName;
    }

    /**
     * Sets the name of the foreign key.
     *
     * @param foreignKeyName the name of the foreign key.
     */
    public void setForeignKeyName(String foreignKeyName)
    {
        this.foreignKeyName = foreignKeyName;
    }

    /**
     * Returns the names of the local columns.
     * To change the list in this object, the returned list can be modified.
     *
     * @return the names of the local columns.
     */
    public List<String> getLocalColumns()
    {
        return localColumns;
    }

    /**
     * Returns the names of the foreign columns.
     * To change the list in this object, the returned list can be modified.
     *
     * @return the names of the foreign columns.
     */
    public List<String> getForeignColumns()
    {
        return foreignColumns;
    }
}
