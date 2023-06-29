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
 * The model of the inheritance tag in a Torque schema file.
 *
 * @version $Id: $
 */
public class Inheritance
{
    /** The column to which this inheritance definition belongs to. */
    public Column parent;

    /** A value found in the column marked as the inheritance key column. */
    public String key;

    /** The class name for the object that will inherit the record values. */
    public String _class;

    /** The class that the inheritor class will extend. */
    public String _extends;
}
