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
 * This interface defines methods related to object referencing and tracking
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: Persistent.java 1152582 2011-07-31 13:59:17Z tfischer $
 */
public interface ObjectModel
{
    /**
     * getter for the object primaryKey.
     *
     * @return the object primaryKey as an Object
     */
    ObjectKey<?> getPrimaryKey();

    /**
     * Sets the PrimaryKey for the object.
     *
     * @param primaryKey The new PrimaryKey for the object.
     * @throws TorqueException This method might throw an exception
     */
    void setPrimaryKey(ObjectKey<?> primaryKey) throws TorqueException;

    /**
     * Sets the PrimaryKey for the object.
     *
     * @param primaryKey the String should be of the form produced by
     *        ObjectKey.toString().
     * @throws TorqueException This method might throw an exception
     */
    void setPrimaryKey(String primaryKey) throws TorqueException;

    /**
     * Returns whether the object has been modified, since it was
     * last retrieved from storage.
     *
     * @return True if the object has been modified.
     */
    boolean isModified();

    /**
     * Returns whether the object has ever been saved.  This will
     * be false, if the object was retrieved from storage or was created
     * and then saved.
     *
     * @return true, if the object has never been persisted.
     */
    boolean isNew();

    /**
     * Setter for the isNew attribute.  This method will be called
     * by Torque-generated children and Peers.
     *
     * @param b the state of the object.
     */
    void setNew(boolean b);

    /**
     * Sets the modified state for the object.
     *
     * @param m The new modified state for the object.
     */
    void setModified(boolean m);
}
