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

import java.sql.SQLException;

/**
 * The base class of all checked exceptions thrown by Torque.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:jvz@apache.org">Jason van Zyl</a>
 * @version $Id: TorqueException.java 1839304 2018-08-27 12:19:46Z tv $
 */
public class TorqueException extends SQLException
{
    /**
     * Serial version.
     */
    private static final long serialVersionUID = 3090544800848674368L;

    /**
     * Constructs a new <code>TorqueException</code> without specified detail
     * message.
     */
    public TorqueException()
    {
        super();
    }

    /**
     * Constructs a new <code>TorqueException</code> with specified detail
     * message.
     *
     * @param msg the error message.
     */
    public TorqueException(String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new <code>TorqueException</code> with specified nested
     * <code>Throwable</code>.
     *
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public TorqueException(Throwable nested)
    {
        super(nested);
    }

    /**
     * Constructs a new <code>TorqueException</code> with specified detail
     * message and nested <code>Throwable</code>.
     *
     * @param msg the error message.
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public TorqueException(String msg, Throwable nested)
    {
        super(msg, nested);
    }
}
