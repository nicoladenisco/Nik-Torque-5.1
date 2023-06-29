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
 * Contains the child elements for a foreign-key source element which are not
 * defined in the Torque schema. These elements might be created
 * in the transformation.
 */
public enum ForeignKeyChildElementName implements SourceElementName
{
    /**
     * The child element containing information about the field in the local
     * database object containing the referenced foreign database object.
     */
    LOCAL_FIELD("local-field"),

    /**
     * The child element containing information about the field in the foreign
     * database object containing the referenced local database object.
     */
    FOREIGN_FIELD("foreign-field"),

    /**
     * The child element containing information about the field in the local
     * bean containing the referenced foreign bean.
     */
    LOCAL_FIELD_IN_BEAN("local-field-in-bean"),

    /**
     * The child element containing information about the field in the foreign
     * bean containing the referenced local beans.
     */
    FOREIGN_FIELD_IN_BEAN("foreign-field-in-bean");

    /** The name of the source element, not null. */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the source element, not null.
     */
    private ForeignKeyChildElementName(String name)
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
