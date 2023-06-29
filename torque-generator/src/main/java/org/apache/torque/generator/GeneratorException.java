package org.apache.torque.generator;

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
 * This exception is the superclass of all Exceptions thrown in the Torque
 * generator. It is thrown whenever an Error occurs which is specific
 * to the Torque Generator.
 */
public class GeneratorException extends Exception
{
    /**
     * The version of the class
     * (for serialization and deserialization purposes).
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a OutletException without error message.
     */
    public GeneratorException()
    {
        super();
    }

    /**
     * Constructs a GeneratorException with the given error message.
     *
     * @param message the error message
     *
     * @see Exception#Exception(java.lang.String)
     */
    public GeneratorException(String message)
    {
        super(message);
    }

    /**
     * Constructs a GeneratorException which wraps another exception.
     *
     * @param cause The cause of the error.
     *
     * @see Exception#Exception(java.lang.Throwable)
     */
    public GeneratorException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a GeneratorException which wraps another exception,
     * and which has its own error message.
     *
     * @param message The error message.
     * @param cause The exception to wrap.
     *
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public GeneratorException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
