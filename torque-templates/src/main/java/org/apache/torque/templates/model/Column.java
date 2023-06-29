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
 * The model of the column tag in a Torque schema file.
 *
 * @version $Id: $
 */
public class Column
{
    // Torque schema properties

    /** The table to which the column belongs. */
    public Table parent;

    /** The list of options for this column. */
    public List<Option> optionList = new ArrayList<>();

    /** The list of inheritances for this column. */
    public List<Inheritance> inheritanceList = new ArrayList<>();

    /** The list of enum values for this column. */
    public List<EnumValue> enumValueList = new ArrayList<>();

    /** The column's name. */
    public String name;

    /** The type of the column. */
    public String type;

    /** How many decimal places, characters or bytes the column can take. */
    public String size;

    /** The scale of the column. */
    public String scale;

    /** The default value of the column. */
    public String _default;

    /**
     * Whether the database default value should be used when saving
     * this column.
     */
    public Boolean useDatabaseDefaultValue;

    /** Whether this column is a primary key of this column. */
    public Boolean primaryKey;

    /**
     * Whether this column should be automatically set by whatever
     * id generation mechanism used for this column.
     */
    public Boolean autoIncrement;

    /** Whether this column is required to be not-null. */
    public Boolean required;

    /** The field name for the column in the database object. */
    public String javaName;

    /** The type of the field for the column in the database object. */
    public String javaType;

    /** The domain reference name to set common settings. */
    public String domain;

    /** The inheritance method used. */
    public String inheritanceType;

    /**
     * Whether getters and setters for the field in the database object
     * should be protected instead of public.
     */
    public Boolean _protected;

    /** Whether this column is a version column. */
    public Boolean version;

    /** The description of (== comment for) the column. */
    public String description;

    /** The name of the enum type for the column. It can be either fully qualified or unqualified. */
    public String enumType;

    // SQL generation properties

    /** Contains the SQL to define the column. */
    public String ddlSql;

    /** the constraint name for an enum column. */
    public String enumConstraintName;

    /**
     * Whether to generate an enum constraint for the column
     * (nb: predefined enums can be defined for which no constraint can be defuned).
     */
    public Boolean generateEnum;

}
