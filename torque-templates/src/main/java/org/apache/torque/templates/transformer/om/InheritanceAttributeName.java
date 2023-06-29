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
 * Contains the attributes for a inheritance source element which are not
 * defined in the Torque schema. Find them in {@link TorqueSchemaAttributeName}.
 *  
 */
public enum InheritanceAttributeName implements SourceAttributeName
{
    /**
     * The name of the constant in the peer which contains the key
     * for the processed inheritance class.
     */
    CLASSKEY_CONSTANT("classkeyConstant"),

    /**
     * The name of the class created from this inheritance element.
     */
    CLASS_NAME("className"),
    /**
     * The package of the class created from this inheritance element.
     * now in {@link TorqueSchemaAttributeName}.
     */
    @Deprecated
    PACKAGE("package"),

    /**
     * The package of the bean class created from this inheritance element.
     */
    BEAN_PACKAGE("beanPackage"),

    /**
     * The base class of the bean class created from this inheritance element.
     */
    BEAN_EXTENDS("beanExtends"),

    /**
     * The class name of the bean class created from this inheritance element.
     */
    BEAN_CLASS_NAME("beanClassName");

    /** The name of the source element attribute, not null. */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the source element attribute, not null.
     */
    private InheritanceAttributeName(String name)
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
