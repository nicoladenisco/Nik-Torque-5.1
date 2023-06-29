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

import java.util.ArrayList;
import java.util.List;

/**
 * The model of the column tag in a Torque schema file.
 *
 * @version $Id: $
 */
public class ViewColumn
{
    /** The view to which the view column belongs. */
    public View parent;

    /** The list of options for this view column. */
    public List<Option> options = new ArrayList<>();

    /** The list of inheritances for this view column. */
    public List<Inheritance> inheritances = new ArrayList<>();

    /** The view column's name. */
    public String name;

    /** The type of the view column. */
    public String type;

    /**
     * How many decimal places, characters or bytes the view column can take.
     */
    public String size;

    /** The scale of the view column. */
    public String scale;

    /** The field name for the view column in the database object. */
    public String javaName;

    /** The type of the field for the view column in the database object. */
    public String javaType;

    /** The domain reference name to set common settings. */
    public String domain;

    /** The sql snippet which contains the value to select. */
    public String select;

    /**
     * Whether getters and setters for the field in the database object
     * should be protected instead of public.
     */
    public String _protected;

    /** The description of (== comment for) the view column. */
    public String description;
}
