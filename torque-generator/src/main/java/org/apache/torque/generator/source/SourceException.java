package org.apache.torque.generator.source;

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
 * This exception denotes that the source cannot be read.
 */
public class SourceException extends GeneratorException
{
    /**
     * The version of the class
     * (for serialisation and deserialisation purposes).
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a SourceException without error message.
     *
     * @see Exception#Exception()
     */
    public SourceException()
    {
        super();
    }

    /**
     * Constructs a SourceException with the given error message.
     *
     * @param message the error message
     *
     * @see Exception#Exception(java.lang.String)
     */
    public SourceException(String message)
    {
        super(message);
    }

    /**
     * Constructs a SourceException which wraps another exception.
     *
     * @param cause The root cause of the exception, can be null.
     *
     * @see Exception#Exception(java.lang.Throwable)
     */
    public SourceException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a SourceException which wraps another exception,
     * and which has its own error message.
     *
     * @param message The error mesaage.
     * @param cause The exception to wrap.
     *
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public SourceException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
