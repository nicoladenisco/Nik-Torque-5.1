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
 * The model of the foreign-key tag in a Torque schema file.
 *
 * @version $Id: $
 */
public class ForeignKey
{
    /** The table to which this foreign key definition belongs. */
    public Table parent;

    /** The list of options for this foreign key. */
    public List<Option> optionList = new ArrayList<>();

    /** The list of column references for this foreign key. */
    public List<Reference> referenceList = new ArrayList<>();

    /** The name of the foreign key. */
    public String name;

    /** The name of the foreign table. */
    public String foreignTable;

    /**
     * The action performed by the database if the referenced record is deleted.
     */
    public String onDelete;

    /**
     * The action performed by the database if the referenced record is updated.
     */
    public String onUpdate;

    // SQL generation properties

    /** Contains all local column names in a comma-separated String. */
    public String localColumnNames;

    /** Contains all foreign column names in a comma-separated String. */
    public String foreignColumnNames;

}
