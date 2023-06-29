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

/**
 * A Factory which creates a SAX handler for sources from JDBC Metadata.
 *
 * @version $Id: JdbcMetadataSourceSaxHandlerFactory.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class JdbcMetadataSourceSaxHandlerFactory
implements SourceSaxHandlerFactory
{
    /** The type of the source. */
    private static final String TYPE = "jdbcMetadataSource";


    /**
     * Returns the source type which can be handled by the
     * JdbcMetadataSourceSaxHandler created by this factory.
     *
     * @return "jdbcMetadataSource".
     */
    @Override
    public String getType()
    {
        return TYPE;
    }

    /**
     * Returns a JdbcMetadataSourceSaxHandler for reading the configuration of
     * JDBC metadata sources.
     *
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers All known configuration handlers, not null.
     *
     * @return a new FileSourcesSaxHandlers.
     */
    @Override
    public final SourceSaxHandler getSourceSaxHandler(
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor,
            ConfigurationHandlers configurationHandlers)
    {
        return new JdbcMetadataSourceSaxHandler(
                configurationProvider,
                unitDescriptor,
                configurationHandlers);
    }
}
