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

import org.apache.torque.generator.source.SourceElementName;

/**
 * Contains the child elements for a table source element which are not
 * defined in the Torque schema. These elements might be created
 * in the transformation of a table.
 */
public enum TableChildElementName implements SourceElementName
{
    /**
     * The element containing the inheritance column.
     */
    INHERITANCE_COLUMN("inheritance-column"),

    /**
     * Contains all foreign keys referencing this table.
     */
    REFERENCING_FOREIGN_KEYS("referencing-foreign-keys"),

    /**
     * Contains all primary key columns of the table.
     */
    PRIMARY_KEYS("primary-keys"),

    /**
     * A getter to join a table remote by two foreign key relations.
     */
    JOIN_GETTER("join-getter"),

    /**
     * The local elements of the JOIN_GETTER.
     */
    LOCAL("local"),

    /**
     * The foreign elements of the JOIN_GETTER.
     */
    FOREIGN("foreign");

    /** The name of the source element, not null. */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the source element, not null.
     */
    private TableChildElementName(final String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the referenced source element.
     *
     * @return the name of the referenced source element.
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
