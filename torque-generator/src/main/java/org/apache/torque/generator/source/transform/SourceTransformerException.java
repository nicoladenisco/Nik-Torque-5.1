package org.apache.torque.generator.source.transform;

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
 * This exception is the superclass of all Exceptions thrown by
 * SourceTransformers.
 */
public class SourceTransformerException extends GeneratorException
{
    /**
     * The version of the class
     * (for serialization and deserialization purposes).
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a SourceTransformerException without error message.
     */
    public SourceTransformerException()
    {
        super();
    }

    /**
     * Constructs a SourceTransformerException with the given error message.
     *
     * @param message the error message
     *
     * @see Exception#Exception(java.lang.String)
     */
    public SourceTransformerException(String message)
    {
        super(message);
    }

    /**
     * Constructs a SourceTransformerException which wraps another exception.
     *
     * @param cause The root message.
     *
     * @see Exception#Exception(java.lang.Throwable)
     */
    public SourceTransformerException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a SourceTransformerException which wraps another exception,
     * and which has its own error message.
     *
     * @param message The error message.
     * @param cause The exception to wrap.
     *
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public SourceTransformerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
