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

/**
 * The possible values the javaType attribute of the column element.
 *
 * $Id: TorqueSchemaJavaType.java 1331196 2012-04-27 02:56:12Z tfischer $
 */
public enum TorqueSchemaJavaType
{
    /** The java attribute will be an object (e.g. Integer). */
    OBJECT("object"),

    /**
     * The java attribute will be a primitive (e.g. int), if possible.
     */
    PRIMITIVE("primitive");

    /**
     * The value of the javaType attribute, not null.
     */
    private String value;

    /**
     * Constructor.
     *
     * @param value the value of the javaType attribute, not null.
     */
    private TorqueSchemaJavaType(String value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the javaType attribute.
     *
     * @return the value of the javaType attribute, not null.
     */
    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
