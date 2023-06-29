package org.apache.torque.generator.configuration.source;

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

import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.xml.sax.SAXException;

/**
 * Classes implementing this interface are responsible for creating
 * <code>SourceSaxHandler</code>s for a specific source type.
 */
public abstract interface SourceSaxHandlerFactory
{
    /**
     * Returns the source type handled by the SourceSaxHandlers which are
     * created by this factory.
     *
     * @return the type of the sources, not null.
     */
    String getType();

    /**
     * Returns a SourceSaxHandler for reading the configuration of
     * sources. The SAX Handler is used as a delegate handler
     * whenever a source element with the matching type
     * is encountered in a configuration file.
     *
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers the available configuration handlers,
     *        not null.
     *
     * @return a SAX delegate handler for parsing the configuration with the
     *         given type.
     *
     * @throws SAXException if the SAX Handler for the sources can
     *         not be created from the given XML element.
     */
    SourceSaxHandler getSourceSaxHandler(
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor,
            ConfigurationHandlers configurationHandlers)
                    throws SAXException;
}
