package org.apache.torque.generator.control;

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

import org.apache.torque.generator.GeneratorException;

/**
 * This Exception is thrown when an error occurs within the Controller of
 * the Torque Generator.
 */
public class ControllerException extends GeneratorException
{
    /**
     * The version of the class
     * (for serialization and deserialization purposes).
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a ConfigurationException without error message.
     *
     * @see Exception#Exception()
     */
    public ControllerException()
    {
        super();
    }

    /**
     * Constructs a ConfigurationException with the given error message.
     *
     * @param message the error message
     *
     * @see Exception#Exception(java.lang.String)
     */
    public ControllerException(String message)
    {
        super(message);
    }

    /**
     * Constructs a ControllerException which wraps another exception.
     *
     * @param cause The exception to wrap. May be null.
     *
     * @see Exception#Exception(java.lang.Throwable)
     */
    public ControllerException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a ControllerException which wraps another exception,
     * and which has its own error message.
     *
     * @param message The error message.
     * @param cause The exception to wrap, may be null.
     *
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public ControllerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
