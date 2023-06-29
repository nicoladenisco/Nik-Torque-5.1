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
 * The model of the index tag in a Torque schema file.
 *
 * @version $Id: $
 */
public class Index
{
    // schema properties

    /** The table to which the index belongs. */
    public Table parent;

    /** The list of otions for the index. */
    public List<Option> optionList = new ArrayList<>();

    /** The list of indexColumns for the index. */
    public List<IndexColumn> indexColumnList = new ArrayList<>();

    /** The name of the index. */
    public String name;

    // SQL generatiopn properties

    /** Contains all index column names in a comma-separated String. */
    public String indexColumnNames;

}
