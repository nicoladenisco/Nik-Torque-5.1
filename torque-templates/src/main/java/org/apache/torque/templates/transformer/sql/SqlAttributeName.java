package org.apache.torque.templates.transformer.sql;

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
 * Contains the attributes created by the SQLTransformer.
 */
public enum SqlAttributeName implements SourceAttributeName
{
    /**
     * The unqualified (without schema) table name.
     */
    UNQUALIFIED_NAME("unqualifiedName"),

    /**
     * The attribute contains all primary key columns
     * in a comma-separated String.
     */
    PRIMARY_KEY_COLUMN_NAMES("primaryKeyColumnNames"),

    /**
     * The attribute contains all unique column names
     * in a comma-separated String.
     */
    UNIQUE_COLUMN_NAMES("uniqueColumnNames"),
    
    /**
     * The attribute contains all unique column sizes
     * in a comma-separated String.
     */
    
    UNIQUE_COLUMN_SIZES("uniqueColumnSizes"),

    /**
     * The attribute contains all index column names
     * in a comma-separated String.
     */
    INDEX_COLUMN_NAMES("indexColumnNames"),

    /**
     * The attribute contains all local column names
     * in a comma-separated String.
     */
    LOCAL_COLUMN_NAMES("localColumnNames"),

    /**
     * The attribute contains all foreign column names
     * in a comma-separated String.
     */
    FOREIGN_COLUMN_NAMES("foreignColumnNames"),

    /**
     * The attribute contains the name of the constraint defining the
     * primary key of a table.
     */
    PRIMARY_KEY_CONSTRAINT_NAME("primaryKeyConstraintName"),

    /**
     * The attribute contains the name of the sequence from which
     * the primary key of the table is generated.
     */
    SEQUENCE_NAME("sequenceName"),

    /**
     * The attribute contains the SQL to define the column.
     */
    DDL_SQL("ddlSql"),

    /**
     * The attribute contains the constraint name if the column contains an enum.
     */
    ENUM_CONSTRAINT_NAME("enumConstraintName"),

    /**
     * The attribute contains the properly escaped and transformed sql value for the element.
     */
    SQL_VALUE("sqlValue");

    /** The name of the source element attribute, not null. */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the source element attribute, not null.
     */
    private SqlAttributeName(final String name)
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
