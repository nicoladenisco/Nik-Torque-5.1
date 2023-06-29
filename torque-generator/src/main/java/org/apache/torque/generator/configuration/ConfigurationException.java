package org.apache.torque.generator.configuration;

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
 * This exception is thrown if the Torque generator cannot access its
 * configuration or if an error occurs accessing the configuration.
 */
public class ConfigurationException extends GeneratorException
{
    /**
     * The version of the class
     * (for serialisation and deserialisation purposes).
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a ConfigurationException without error message.
     *
     * @see Exception#Exception()
     */
    public ConfigurationException()
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
    public ConfigurationException(String message)
    {
        super(message);
    }

    /**
     * Constructs a ConfigurationException which wraps another exception.
     *
     * @param cause The initial exception which was caught.
     *
     * @see Exception#Exception(java.lang.Throwable)
     */
    public ConfigurationException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a ConfigurationException which wraps another exception,
     * and which has its own error message.
     *
     * @param message The error message.
     * @param cause The exception to wrap.
     *
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public ConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
