package org.apache.torque.generator.template;

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

import java.io.IOException;
import java.io.InputStream;

/**
 * An Filter which preprocesses a template.
 */
public interface TemplateFilter
{
    /**
     * Filters (i.e modifies) a template.
     *
     * @param toFilter the input stream for the template to filter.
     * @param encoding the encoding of the template, or null for the system
     *        encoding.
     *
     * @return A stream containing the filtered template.
     *
     * @throws IOException if an error occurs while reading or filtering
     *         the template.
     */
    InputStream filter(InputStream toFilter, String encoding)
            throws IOException;
}
