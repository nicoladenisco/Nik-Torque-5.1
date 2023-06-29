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

import java.io.File;
import java.util.Date;

/**
 * An entity which serves as an input for the generation process.
 */
public interface Source
{
    /**
     * Returns the root element of the source.
     *
     * @return the root element of the source, not null.
     *
     * @throws SourceException if the source cannot be constructed.
     */
    SourceElement getRootElement() throws SourceException;

    /**
     * Gets a description of this source for debugging purposes.
     *
     * @return the description, which should make it possible to identify
     *         the currently processed output.
     */
    String getDescription();

    /**
     * Returns the source file, if it exists.
     *
     * @return the source file, or null if the source is not read from a file.
     */
    File getSourceFile();

    /**
     * Returns the date when the source was last modified.
     *
     * @return the last modification date, or null when unknown.
     */
    Date getLastModified();

    /**
     * Returns the checksum of the content of the source.
     * It is not defined which checksum is returned, the only
     * requirement is that collisions should be extremely rare,
     * i.e it can be assumed that if the checksum is the same,
     * the content is also the same.
     *
     * @return the checksum of the content of the source, or null if
     *         it cannot be determined.
     */
    byte[] getContentChecksum();
}
