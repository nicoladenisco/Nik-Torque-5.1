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
 * The model of the view tag in a Torque schema file.
 *
 * @version $Id: $
 */
public class View
{
    /** The database to which this view belongs. */
    public Database parent;

    /** The options for this view. */
    public List<Option> optionList = new ArrayList<>();

    /** The columns of this view. */
    public List<ViewColumn> columnList;

    /** The view's name. */
    public String name;

    /** The base class of the data object class. */
    public String baseClass;

    /** The base class of the peer class. */
    public String basePeer;

    /** Whether the data object class is abstract. */
    public Boolean _abstract;

    /**
     * The unqualified name of the data object class.
     * If null, the class name will be determined from the name attribute.
     */
    public String javaName;

    /** The remainder of the sql for the view after column definitions. */
    public String sqlSuffix;

    /**
     * The complete SQL for creating the view.
     * If set, overrides all other means of generating the SQL
     * for view creation.
     */
    public String createSql;

    /** Whether sql generation should be skipped. */
    public Boolean skipSql;

    /** A description of the view. */
    public String description;

}
