package org.apache.torque.templates.model;

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
 * The model of the database tag in a Torque schema file.
 *
 * @version $Id: $
 */
public class Database
{
    // properties from schema
    /** The list of options for this database. */
    public List<Option> optionList = new ArrayList<>();

    /** The list of included schemas for this database. */
    public List<IncludeSchema> includeSchemaList
        = new ArrayList<>();

    /** The list of external schemas for this database. */
    public List<ExternalSchema> externalSchemaList
        = new ArrayList<>();

    /** The List of domains for this database. */
    public List<Domain> domainList = new ArrayList<>();

    /**
     * The list of tables for this database.
     * This may include the tables from included schemas,
     * depending on whether included schemas were already resolved,
     * but never includes the tables from external schemas.
     */
    public List<Table> tableList = new ArrayList<>();

    /**
     * The list of views for this database.
     * This may include the views from included schemas,
     * depending on whether included schemas were already resolved,
     * but never includes the views from external schemas.
     */
    public List<View> viewList = new ArrayList<>();

    /** The name of the database. */
    public String name;

    /**
     * Whether the columns in this database will use primitive types if
     * possible ("primitive") or will always use object types ("object"). */
    public String defaultJavaType;

    /**
     * The default id method for this database. One of "native", "idbroker"
     * or "none".
     */
    public String defaultIdMethod;

    // general generation properties

    /**
     * The name of the root database. Either the name of of this database
     * if this database was not included or referenced as external schema,
     * or the name of the root inclusion or root external-schema database.
     */
    public String rootDatabaseName;

    /**
     * List of all tables, including the tables from loaded
     * external-schema definitions.
     */
    public List<Table> allTables = new ArrayList<>();

    /**
     * List of all views, including the views from loaded
     * external-schema definitions.
     */
    public List<View> allViews = new ArrayList<>();

    /**
     * List of all schema names in the tables of the database.
     */
    public List<String> schemaNameList = new ArrayList<>();

    @Override
    public String toString()
    {
        return "Database [name=" + name + ", defaultJavaType="
                + defaultJavaType + ", defaultIdMethod=" + defaultIdMethod
                + ", rootDatabaseName=" + rootDatabaseName + "]";
    }

}
