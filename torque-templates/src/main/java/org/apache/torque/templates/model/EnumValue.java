package org.apache.torque.templates.model;

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
 * The model of the enum-value tag in a Torque schema file.
 *
 * @version $Id: $
 */
public class EnumValue
{
    /** The column to which this enum-value definition belongs to. */
    public Column parent;

    /** The value of the enum-value. */
    public String value;

    /** The java name of the enum-value. */
    public String javaName;

    /** The description of the enum-value. */
    public String description;

    // sql generation properties
    /** The properly escaped sql value for the value. */
    public String sqlValue;

    /** Whether another enumValue exists in the list of enumValues where this enumValue belongs to. */
    public boolean hasNext;
}
