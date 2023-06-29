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
 * Contains the attributes for a enum-value source element which are not
 * defined in the Torque schema.
 */
public enum EnumValueAttributeName implements SourceAttributeName
{
    /** The java value of the enum Attribute. */
    JAVA_VALUE("javaValue");

    /** The name of the source element attribute, not null. */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the source element attribute, not null.
     */
    private EnumValueAttributeName(final String name)
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
