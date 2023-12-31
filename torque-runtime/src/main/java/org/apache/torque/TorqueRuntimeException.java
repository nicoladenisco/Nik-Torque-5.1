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
 * This is the base class of all non-checked exceptions in Torque.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id: TorqueRuntimeException.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class TorqueRuntimeException
extends RuntimeException
{
    /**
     * Serial version.
     */
    private static final long serialVersionUID = -2997617341459640541L;

    /**
     * Constructs a new <code>TorqueRuntimeException</code> without specified
     * detail message.
     */
    public TorqueRuntimeException()
    {
        super();
    }

    /**
     * Constructs a new <code>TorqueRuntimeException</code> with specified
     * detail message.
     *
     * @param msg the error message.
     */
    public TorqueRuntimeException(String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new <code>TorqueRuntimeException</code> with specified
     * nested <code>Throwable</code>.
     *
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public TorqueRuntimeException(Throwable nested)
    {
        super(nested);
    }

    /**
     * Constructs a new <code>TorqueRuntimeException</code> with specified
     * detail message and nested <code>Throwable</code>.
     *
     * @param msg the error message.
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public TorqueRuntimeException(String msg, Throwable nested)
    {
        super(msg, nested);
    }
}
