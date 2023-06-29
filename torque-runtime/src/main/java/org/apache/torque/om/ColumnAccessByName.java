package org.apache.torque.om;

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

import org.apache.torque.TorqueException;

/**
 * Define accessors by name.
 *
 * @version $Id: ColumnAccessByName.java 1448414 2013-02-20 21:06:35Z tfischer $
 */
public interface ColumnAccessByName
{
    /**
     * Retrieves a field from the object by name.
     *
     * @param field The name of the field to retrieve.
     *
     * @return The retrieved field value
     */
    Object getByName(String field);

    /**
     * Set a field in the object by field (Java) name.
     *
     * @param name field name.
     * @param value field value.
     *
     * @return True if value was set, false if not (invalid name / protected
     *         field).
     *
     * @throws IllegalArgumentException if object type of value does not match
     *             field object type.
     * @throws TorqueException If a problem occurs with the set[Field] method.
     */
    boolean setByName(String name, Object value)
            throws TorqueException;

    /**
     * Retrieves a field from the object by name passed in as a String.
     *
     * @param name field name.
     *
     * @return value of the field.
     */
    Object getByPeerName(String name);

    /**
     * Set field values by Peer Field Name-
     *
     * @param name field name.
     * @param value field value.
     *
     * @return True if value was set, false if not (invalid name / protected
     *         field).
     *
     * @throws IllegalArgumentException if object type of value does not match
     *             field object type.
     * @throws TorqueException If a problem occurs with the set[Field] method.
     */
    boolean setByPeerName(String name, Object value)
            throws TorqueException;

    /**
     * Retrieves a field from the object by position as specified in a database
     * schema for example.
     *
     * @param pos field position.
     *
     * @return the value of the field.
     */
    Object getByPosition(int pos);

    /**
     * Set field values by it's position (zero based) in the XML schema.
     *
     * @param position The field position.
     * @param value field value.
     *
     * @return True if value was set, false if not (invalid position / protected
     *         field).
     *
     * @throws IllegalArgumentException if object type of value does not match
     *             field object type.
     * @throws TorqueException If a problem occurs with the set[Field] method.
     */
    boolean setByPosition(int position, Object value)
            throws TorqueException;
}
