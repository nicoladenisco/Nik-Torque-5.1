package org.apache.torque.templates.transformer.om;

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

import org.apache.torque.generator.source.SourceAttributeName;

/**
 * Contains the attributes for a column source element which are not
 * defined in the Torque schema.
 */
public enum ColumnAttributeName implements SourceAttributeName
{
    /** Whether the column is a primitive column. */
    PRIMITIVE_TYPE("primitive"),

    /** Whether the column is a number column. */
    NUMBER_TYPE("number"),

    /** The name of constant for the column name in the peer class. */
    PEER_COLUMN_NAME("peerColumnName"),

    /**
     * The fully qualified name of the column,
     * i. e. prefixed with the database schema name if a schema name was given.
     */
    QUALIFIED_COLUMN_NAME("qualifiedColumnName"),

    /** The column index, 1 based. First column 1, second column 2 etc. */
    POSITION("position"),

    /** The object (non-primitive) type for a field. */
    FIELD_OBJECT_TYPE("fieldObjectType"),

    /** The getter to get the column from a result set. */
    RESULT_SET_GETTER("resultSetGetter"),

    /** An instance of the object for the type map. */
    SAMPLE_OBJECT("sampleObject"),

    /** The unqualified name of the enum type of the column. */
    ENUM_CLASS_NAME("enumClassName"),

    /** The package of the enum type of the column. */
    ENUM_PACKAGE("enumPackage"),

    /** The unqualified class name of the value contained in enum type of the column. */
    ENUM_VALUE_CLASS_NAME("enumValueClassName"),

    /** Whether to generate an enum type for the column (nb: predefined enums can be defined which need not be generated). */
    GENERATE_ENUM("generateEnum"),

    /** Whether this column is an enum. */
    IS_ENUM("isEnum");

    /** The name of the source element attribute, not null. */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the source element attribute, not null.
     */
    private ColumnAttributeName(final String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the referenced source element attribute.
     *
     * @return the name of the referenced source element attribute.
     */
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
