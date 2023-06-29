package org.apache.torque.criteria;

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

import java.util.List;
/**
 * The rendered SQL for a part of a prepared statement, including replacements for ? placeholders.
 *
 * @version $Id: PreparedStatementPart.java 1839288 2018-08-27 09:48:33Z tv $
 */
public interface PreparedStatementPart
{
    /**
     * Returns the SQL of the part as String.
     *
     * @return the SQL, not null.
     */
    String getSqlAsString();

    /**
     * Returns the list of prepared statement replacements.
     * The implementation may or may not return a list which is modifiable
     * and which may or may not, in case of modification,
     * change the internal state of the surrounding PreparedStatementPart.
     *
     * @return the list of prepared statement replacements, not null.
     */
    List<Object> getPreparedStatementReplacements();
}
