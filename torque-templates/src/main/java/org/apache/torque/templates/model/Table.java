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
 * The model of the table tag in a Torque schema file.
 *
 * @version $Id: $
 */
public class Table
{
    // properties from schema

    /** The database to which this table belongs. */
    public Database parent;

    /** The options for this table. */
    public List<Option> optionList = new ArrayList<>();

    /** The columns of this table. */
    public List<Column> columnList = new ArrayList<>();

    /** The foreign keys of this table. */
    public List<ForeignKey> foreignKeyList = new ArrayList<>();

    /** The index list for this table. */
    public List<Index> indexList = new ArrayList<>();

    /** The list of unique indices for this table. */
    public List<Unique> uniqueList = new ArrayList<>();

    /** The id method parameters for this table. */
    public List<IdMethodParameter> idMethodParameterList
        = new ArrayList<>();

    /** The table's name. */
    public String name;

    /**
     * Specifies an interface that the generated data object class implements.
     * If this is a fully qualified class name (i. e. the string contains dots),
     * the interface will simply be implemented by the data object class.
     * If the interface is a simple class name (without dots),
     * an empty interface file will be generated in the data object package.
     * When this attribute is used, all peer methods that normally would return
     * the data object type will now return the interface type.
     */
    public String _interface;

    /** The base class of the data object class. */
    public String baseClass;

    /** The base class of the peer class. */
    public String basePeer;

    /**
     * The id method to use.
     * Valid values are "idbroker", "native", "none" or null.
     */
    public String idMethod;

    /** Whether the data object class is abstract. */
    public Boolean _abstract;

    /**
     * The unqualified name of the data object class.
     * If null, the class name will be determined from the name attribute.
     */
    public String javaName;

    /** Whether sql generation should be skipped. */
    public Boolean skipSql;

    /** A description of the table. */
    public String description;

    // general generation properties

    /** Contains all primary key columns of the table. */
    public List<Column> primaryKeyList = new ArrayList<>();

    // properties for generation of ORM classes

    /** The class name of the data object class. */
    public String dbObjectClassName;

    /** The class name of the data object base class. */
    public String baseDbObjectClassName;

    /** The class name of the peer static wrapper class. */
    public String peerClassName;

    /** The class name of the peer static wrapper base class. */
    public String basePeerClassName;

    /** The class name of the peer implementation class. */
    public String peerImplClassName;

    /** The class name of the peer implementation base class. */
    public String basePeerImplClassName;

    /** The class name of the data object bean class. */
    public String beanClassName;

    /** The class name of the data object bean base class. */
    public String baseBeanClassName;

    /** The class name of the manager class. */
    public String managerClassName;

    /** The class name of the manager base class. */
    public String baseManagerClassName;

    /** The class name of the record mapper class. */
    public String recordMapperClassName;

    /** The class name of the record mapper base class. */
    public String baseRecordMapperClassName;

    /** The package of the data object class. */
    public String dbObjectPackage;

    /** The package of the data object base class. */
    public String baseDbObjectPackage;

    /** The package of the peer class. */
    public String peerPackage;

    /** The package of the peer base class. */
    public String basePeerPackage;

    /** The package of the record mapper class. */
    public String recordMapperPackage;

    /** The package of the record mapper base class. */
    public String baseRecordMapperPackage;

    /** The package of the manager class. */
    public String managerPackage;

    /** The package of the manager base class. */
    public String baseManagerPackage;

    /** The package of the data object bean class. */
    public String beanPackage;

    /** The package of the data object bean base class. */
    public String baseBeanPackage;

    /**
     * The optimistic Locking mode to use.
     * Valid values are "selectForUpdate", "simpleSelect". */
    public String optimisticLockingMode;

    /** Override flag whether manager classes are used in this table. */
    public Boolean useManagers;

    /**
     * Whether the save method resides in the data objects
     * (default is it is in the peer).
     */
    public Boolean saveMethodsInDbObjects;

    /** The interface for the peer class. */
    public String peerInterface;

    // properties for sql generation

    /**
     * The unqualified part of the table name, omitting schema and database
     * information.
     */
    public String unqualifiedName;

    /**
     * The name of the constraint defining the primary key of a table.
     */
    public String primaryKeyConstraintName;

    /**
     * The name of the sequence from which the primary key of the table
     * is generated.
     */
    public String sequenceName;

    /**
     * The attribute contains all primary key columns
     * in a comma-separated String.
     */
    public String primaryKeyColumnNames;

    @Override
    public String toString()
    {
        return "Table [name=" + name + ", idMethod=" + idMethod
                + ", _abstract=" + _abstract + ", javaName=" + javaName
                + ", skipSql=" + skipSql + ", description=" + description + "]";
    }


}
