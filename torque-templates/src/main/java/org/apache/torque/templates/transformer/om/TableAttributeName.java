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
import org.apache.torque.templates.TorqueSchemaAttributeName;

/**
 * Contains the attributes for a table source element which are not
 * defined in the Torque schema. Find them in {@link TorqueSchemaAttributeName}.
 */
public enum TableAttributeName implements SourceAttributeName
{
    /**
     * The unqualified (without schema) table name.
     */
    UNQUALIFIED_NAME("unqualifiedName"),

    /**
     * The name of the manager class.
     */
    MANAGER_CLASS_NAME("managerClassName"),

    /**
     * The name of the base manager class.
     */
    BASE_MANAGER_CLASS_NAME("baseManagerClassName"),

    /**
     * The name of the database object class.
     */
    DB_OBJECT_CLASS_NAME("dbObjectClassName"),

    /**
     * The package name of the database object.
     */
    DB_OBJECT_PACKAGE("dbObjectPackage"),

    /**
     * The name of the bean class.
     */
    BEAN_CLASS_NAME("beanClassName"),

    /**
     * The package name of the bean class.
     */
    BEAN_PACKAGE("beanPackage"),

    /**
     * The name of the field holding the peer implementation class.
     */
    PEER_IMPL_FIELD_NAME("peerImplFieldName"),

    /**
     * The name of the peer implementation class.
     */
    PEER_IMPL_CLASS_NAME("peerImplClassName"),

    /**
     * The name of the getter for the peer implementation class.
     */
    PEER_IMPL_GETTER("peerImplGetter"),

    /**
     * The name of the setter for the peer implementation class.
     */
    PEER_IMPL_SETTER("peerImplSetter"),

    /**
     * The name of the sequence generated for the table's primary key.
     */
    SEQUENCE_NAME("sequenceName"),

    /**
     * The type which is passed to the save method, if any.
     */
    SAVE_METHOD_INPUT_TYPE("saveMethodInputType"),

    /**
     * The variable name in which the object to save is stored, if any.
     */
    SAVE_METHOD_TO_SAVE_VARIABLE("saveMethodToSaveVariable"),

    /**
     * The variable name in which the name of the getDefaultDate method
     * us defined, is it should be generated.
     */
    GET_CURRENT_DATE_METHOD_NAME("getCurrentDateMethodName"),

    /**
     * The variable name in which the name of the getDefaultTime method
     * us defined, is it should be generated.
     */
    GET_CURRENT_TIME_METHOD_NAME("getCurrentTimeMethodName"),

    /**
     * The variable name in which the name of the getDefaultTimestamp method
     * us defined, is it should be generated.
     */
    GET_CURRENT_TIMESTAMP_METHOD_NAME("getCurrentTimestampMethodName");


    /** The name of the source element attribute, not null. */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the source element attribute, not null.
     */
    private TableAttributeName(String name)
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
