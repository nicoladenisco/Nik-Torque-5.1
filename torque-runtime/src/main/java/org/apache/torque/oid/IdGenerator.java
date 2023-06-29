package org.apache.torque.oid;

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

import java.math.BigDecimal;
import java.sql.Connection;

import org.apache.torque.TorqueException;

/**
 * Interface to be implemented by id generators.  It is possible
 * that some implementations might not require all the arguments,
 * for example MySQL will not require a keyInfo Object, while the
 * IDBroker implementation does not require a Connection as
 * it only rarely needs one and retrieves a connection from the
 * Connection pool service only when needed.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @version $Id: IdGenerator.java 1850586 2019-01-06 18:46:35Z tv $
 */
public interface IdGenerator
{
    /**
     * Returns an id as a primitive int.  If you use numeric
     * identifiers, it's suggested that {@link
     * #getIdAsLong(Connection, Object)} be used instead (due to the
     * limited range of this method).
     *
     * @param connection The database connection to use.
     * @param keyInfo an Object that contains additional info.
     *
     * @return The id as integer.
     *
     * @exception TorqueException if a Database error occurs.
     */
    int getIdAsInt(Connection connection, Object keyInfo)
            throws TorqueException;

    /**
     * Returns an id as a primitive long.
     *
     * @param connection The database connection to use.
     * @param keyInfo an Object that contains additional info.
     *
     * @return The id as long.
     *
     * @exception TorqueException if a Database error occurs.
     */
    long getIdAsLong(Connection connection, Object keyInfo)
            throws TorqueException;

    /**
     * Returns an id as a BigDecimal.
     *
     * @param connection The database connection to use.
     * @param keyInfo an Object that contains additional info.
     *
     * @return The id as BigDecimal.
     *
     * @exception TorqueException if a Database error occurs.
     */
    BigDecimal getIdAsBigDecimal(Connection connection, Object keyInfo)
            throws TorqueException;

    /**
     * Returns an id as a String.
     *
     * @param connection The database connection to use.
     * @param keyInfo an Object that contains additional info.
     *
     * @return The id as String.
     *
     * @exception TorqueException if a Database error occurs.
     */
    String getIdAsString(Connection connection, Object keyInfo)
            throws TorqueException;

    /**
     * A flag to determine the timing of the id generation
     *
     * @return a <code>boolean</code> value
     */
    boolean isPriorToInsert();

    /**
     * A flag to determine the timing of the id generation
     *
     * @return Whether id is availble post-<code>insert</code>.
     */
    boolean isPostInsert();

    /**
     * A flag to determine whether a Connection is required to
     * generate an id.
     *
     * @return a <code>boolean</code> value
     */
    boolean isConnectionRequired();

    /**
     * A flag to determine whether Statement#getGeneratedKeys()
     * should be used.
     *
     * @return a <code>boolean</code> value
     */
    boolean isGetGeneratedKeysSupported();
}
