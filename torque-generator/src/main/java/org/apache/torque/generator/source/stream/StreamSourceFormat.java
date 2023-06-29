package org.apache.torque.generator.source.stream;

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


import java.io.InputStream;

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;

/**
 * A format for a stream-based source, e.g. properties or XML.
 *
 * $Id: StreamSourceFormat.java 1839288 2018-08-27 09:48:33Z tv $
 */
public interface StreamSourceFormat
{
    /**
     * Returns an unique key for the source format.
     *
     * @return an unique key for the source format, not null.
     */
    String getKey();

    /**
     * Gets the filename extension this source type typically has.
     *
     * @return the filename extension without leading dot,
     *         or null if no typical extension exists.
     */
    String getFilenameExtension();

    /**
     * Parses a source file and returns its root element.
     *
     * @param inputStream the stream to read the source file from, not null.
     * @param controllerState the controller state, not null.
     *
     * @return the root element of the source, containing the rest of
     *         the source as linked elements.
     *
     * @throws SourceException if reading or parsing the source fails.
     * @throws NullPointerException if <code>inputStream</code> is null.
     */
    SourceElement parse(
            InputStream inputStream,
            ControllerState controllerState)
                    throws SourceException;
}
