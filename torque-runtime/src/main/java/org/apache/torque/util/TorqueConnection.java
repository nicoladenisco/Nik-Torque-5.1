package org.apache.torque.util;

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

import org.apache.torque.TorqueException;

/**
 * Torque's extension to the JDBC connection
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public interface TorqueConnection extends Connection
{
    @Override
    void close() throws TorqueException;

    /**
     * Called by TransactionManagerImpl.commit() to track state
     *
     * @param committed the committed state to set
     */
    void setCommitted(boolean committed);

    /**
     * Has Transaction.commit() been called successfully on
     * this connection?
     *
     * @return true, if Transaction.commit() has been called.
     */
    boolean isCommitted();

    /**
     * Called by TransactionManagerImpl.rollback() to track state
     *
     * @param rolledBack the rolledBack state to set
     */
    void setRolledBack(boolean rolledBack);

    /**
     * Has Transaction.rollback() been called successfully on
     * this connection?
     *
     * @return true, if Transaction.rollback() has been called.
     */
    boolean isRolledBack();
}
