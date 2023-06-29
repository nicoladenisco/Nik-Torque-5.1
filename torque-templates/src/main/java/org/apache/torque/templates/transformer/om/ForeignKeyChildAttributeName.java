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
 * Contains the attribute names of the child elements for a foreign-key
 * source element which are not defined in the Torque schema.
 * These attributes might be created in the transformation.
 */
public enum ForeignKeyChildAttributeName implements SourceAttributeName
{
    /**
     * The name of the getter method in the database object
     * which joins the other table.
     */
    FOREIGN_FIELD_JOIN_GETTER("joinGetter"),

    /**
     * The name of the peer class method which joins the other table.
     */
    PEER_JOIN_SELECT_METHOD("peerJoinSelectMethod"),

    /**
     * The name of the peer class method which joins all the other tables
     * except the referenced table.
     */
    PEER_JOIN_ALL_EXCEPT_SELECT_METHOD("peerJoinAllExceptSelectMethod"),

    /**
     * The name of the cache field for the criteria with which the last
     * collection was accessed.
     */
    FOREIGN_COLUMN_CRITERIA_CACHE_FIELD("criteriaCacheField"),

    /**
     * The name of the getter in the bean class.
     */
    BEAN_GETTER("beanGetter"),

    /**
     * The name of the setter in the bean class.
     */
    BEAN_SETTER("beanSetter");

    /** The name of the source element, not null. */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the source element, not null.
     */
    private ForeignKeyChildAttributeName(String name)
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
