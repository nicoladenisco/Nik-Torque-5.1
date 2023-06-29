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

import java.sql.Connection;

/**
 * This interface defines methods related to saving an object
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:fedor@apache.org">Fedor K.</a>
 * @version $Id: Persistent.java 1754260 2016-07-27 12:26:53Z tv $
 */
public interface Persistent extends ObjectModel
{
    /**
     * Saves the object.
     *
     * @throws Exception This method might throw an exception
     */
    void save() throws Exception;

    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.
     *
     * @param dbName the name of the database
     * @throws Exception This method might throw an exception
     */
    void save(String dbName) throws Exception;

    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.  This method
     * is meant to be used as part of a transaction, otherwise use
     * the save() method and the connection details will be handled
     * internally
     *
     * @param con the Connection used to store the object
     * @throws Exception This method might throw an exception
     */
    void save(Connection con) throws Exception;
}
