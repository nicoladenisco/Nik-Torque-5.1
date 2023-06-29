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
 * Contains the attribute names of the foreign-key
 * source element which are not defined in the Torque schema.
 * These attributes might be created in the transformation.
 *
 * @version $Id: ForeignKeyAttributeName.java 1839288 2018-08-27 09:48:33Z tv $
 */
public enum ForeignKeyAttributeName implements SourceAttributeName
{
    /**
     * The name of the getter method for the foreign key object.
     */
    FOREIGN_KEY_GETTER("foreignKeyGetter"),
    /**
     * The name of the attribute containing the information whether
     * the foreign key references the primary key of the foreign table.
     */
    REFERENCES_PRIMARY_KEY("referencesPrimaryKey");

    /** The name of the source element, not null. */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the source element, not null.
     */
    private ForeignKeyAttributeName(String name)
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
