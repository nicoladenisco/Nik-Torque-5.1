package org.apache.torque;

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
 * This exception is thrown if the database detects that a row in a table
 * with optimistic locking was updated by another process.
 *
 * @version $Id: OptimisticLockingFailedException.java 1448414 2013-02-20 21:06:35Z tfischer $
 */
public class OptimisticLockingFailedException extends TorqueException
{
    /**
     * Serial version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>ConcurrentUpdateException</code>
     * with specified nested <code>Throwable</code>.
     *
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public OptimisticLockingFailedException(Throwable nested)
    {
        super(nested);
    }

    /**
     * Constructs a new <code>ConcurrentUpdateException</code>
     * with specified detail message and nested <code>Throwable</code>.
     *
     * @param msg the error message.
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public OptimisticLockingFailedException(
            String msg,
            Throwable nested)
    {
        super(msg, nested);
    }

    /**
     * Constructs a new <code>ConcurrentUpdateException</code>
     * with specified detail message.
     *
     * @param msg the error message.
     */
    public OptimisticLockingFailedException(
            String msg)
    {
        super(msg);
    }
}
