package org.apache.torque.templates;

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
 * Contains all element names from the Torque schema.
 *
 * $Id: TorqueSchemaElementName.java 1839288 2018-08-27 09:48:33Z tv $
 */
public enum TorqueSchemaElementName implements SourceElementName
{
    /** element database. */
    DATABASE("database"),
    /** element include-schema */
    INCLUDE_SCHEMA("include-schema"),
    /** element external-schema */
    EXTERNAL_SCHEMA("external-schema"),
    /** element domain. */
    DOMAIN("domain"),
    /** element table. */
    TABLE("table"),
    /** element view. */
    VIEW("view"),
    /** element column. */
    COLUMN("column"),
    /** element foreign-key. */
    FOREIGN_KEY("foreign-key"),
    /** element reference. */
    REFERENCE("reference"),
    /** element inheritance. */
    INHERITANCE("inheritance"),
    /** element id-method-param. */
    ID_METHOD_PARAMETER("id-method-parameter"),
    /** element unique. */
    UNIQUE("unique"),
    /** element unique-column. */
    UNIQUE_COLUMN("unique-column"),
    /** element index. */
    INDEX("index"),
    /** element index-column. */
    INDEX_COLUMN("index-column"),
    /** element enum-value. */
    ENUM_VALUE("enum-value");

    /**
     * The name of the element, not null.
     */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the element, not null.
     */
    private TorqueSchemaElementName(final String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the element.
     *
     * @return the name of the element, not null.
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
