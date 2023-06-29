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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationException;

/**
 * A registry of SourceSaxHandlerFactories.
 *
 * $Id: SourceSaxHandlerFactories.java 1840416 2018-09-09 15:10:22Z tv $
 */
public class SourceSaxHandlerFactories
{
    /** The class log. */
    private static Log log
    = LogFactory.getLog(SourceSaxHandlerFactories.class);

    /**
     * A map containing all known SourceSaxHandlerFactories,
     * keyed by the type of the sources.
     */
    private Map<String, SourceSaxHandlerFactory> factories
        = new HashMap<>();

    /**
     * Constructor. Registers the default SourceSaxHandlerFactories.
     */
    public SourceSaxHandlerFactories()
    {
        try
        {
            register(new FileSourceSaxHandlerFactory());
            register(new JdbcMetadataSourceSaxHandlerFactory());
        }
        catch (ConfigurationException e)
        {
            // should not happen
            log.error("caught ConfigurationException while registering "
                    + "the default Source Sax Handler Factories", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a new SourcseSaxHandlerFactory.
     *
     * @param sourceSaxHandlerFactory the factory to register, not null.
     *
     * @throws ConfigurationException if the type of the registered factory
     *         already exists.
     */
    public void register(SourceSaxHandlerFactory sourceSaxHandlerFactory)
            throws ConfigurationException
    {
        String type = sourceSaxHandlerFactory.getType();

        SourceSaxHandlerFactory oldFactory = factories.get(type);
        if (oldFactory != null)
        {
            throw new ConfigurationException(
                    "Attempted to register an SourceSaxHandlerFactory "
                            + "of type "
                            + sourceSaxHandlerFactory.getType()
                            + " and class "
                            + sourceSaxHandlerFactory.getClass().getName()
                            + " : A factory with this type already exists, "
                            + " it has the class "
                            + oldFactory.getClass().getName());
        }
        factories.put(type, sourceSaxHandlerFactory);
    }

    /**
     * Returns the SourceSaxHandlerFactory associated with the given type.
     *
     * @param type the type top look for, not null.
     *
     * @return the SourceSaxHandlerFactory associated with the given type,
     *         or null if no SourceSaxHandlerFactory exists for the given type.
     */
    public SourceSaxHandlerFactory getSourceSaxHandlerFactory(String type)
    {
        return factories.get(type);
    }

    /**
     * Returns the known source types.
     *
     * @return the known source types, not null.
     */
    public Set<String> getSourceTypes()
    {
        return factories.keySet();
    }
}
