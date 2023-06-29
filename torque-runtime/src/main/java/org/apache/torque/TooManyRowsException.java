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
 * This exception indicates that more rows were returned than expected.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id: TooManyRowsException.java 1351125 2012-06-17 16:51:03Z tv $
 */
public class TooManyRowsException extends TorqueException
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = -2875429969148699158L;

    /**
     * Constructs a new <code>TorqueException</code> without specified detail
     * message.
     */
    public TooManyRowsException()
    {
        // empty
    }

    /**
     * Constructs a new <code>TorqueException</code> with specified detail
     * message.
     *
     * @param msg the error message.
     */
    public TooManyRowsException(String msg)
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
    public TooManyRowsException(Throwable nested)
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
    public TooManyRowsException(String msg, Throwable nested)
    {
        super(msg, nested);
    }
}
