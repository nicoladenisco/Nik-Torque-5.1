package org.apache.torque.generator.configuration.mergepoint;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.option.OptionsSaxHandlerFactory;
import org.apache.torque.generator.configuration.option.PropertiesOptionsSaxHandlerFactory;
import org.apache.torque.generator.configuration.option.XmlOptionsSaxHandlerFactory;

/**
 * A registry of OptionsSaxHandlerFactories.
 *
 * $Id: OptionsSaxHandlerFactories.java 1840416 2018-09-09 15:10:22Z tv $
 */
public class OptionsSaxHandlerFactories
{
    /** The class log. */
    private static Log log
    = LogFactory.getLog(OptionsSaxHandlerFactories.class);

    /**
     * A map containing all known ActionSaxHandlerFactories,
     * keyed by the type of the action.
     */
    private Map<String, OptionsSaxHandlerFactory> factories
        = new HashMap<>();

    /**
     * Constructor. Registers the default OptionsSaxHandlerFactories.
     */
    public OptionsSaxHandlerFactories()
    {
        try
        {
            register(new XmlOptionsSaxHandlerFactory());
            register(new PropertiesOptionsSaxHandlerFactory());
        }
        catch (ConfigurationException e)
        {
            // should not happen
            log.error("caught ConfigurationException while registering "
                    + "the default Options Sax Handler Factories", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a new OptionsSaxHandlerFactory.
     *
     * @param optionsSaxHandlerFactory the factory to register, not null.
     *
     * @throws ConfigurationException if the type of the registered factory
     *         already exists.
     */
    public void register(OptionsSaxHandlerFactory optionsSaxHandlerFactory)
            throws ConfigurationException
    {
        String type = optionsSaxHandlerFactory.getType();

        OptionsSaxHandlerFactory oldFactory = factories.get(type);
        if (oldFactory != null)
        {
            throw new ConfigurationException(
                    "Attempted to register an OptionsSaxHandlerFactory "
                            + "of type "
                            + optionsSaxHandlerFactory.getType()
                            + " and class "
                            + optionsSaxHandlerFactory.getClass().getName()
                            + " : A factory with this type already exists, "
                            + " it has the class "
                            + oldFactory.getClass().getName());
        }
        factories.put(type, optionsSaxHandlerFactory);
    }

    /**
     * Returns the OptionsSaxHandlerFactory associated with the given type.
     *
     * @param type the type top look for, not null.
     *
     * @return the OptionsSaxHandlerFactory associated with the given type,
     *         or null if no OptionsSaxHandlerFactory exists for the given type.
     */
    public OptionsSaxHandlerFactory getOptionsSaxHandlerFactory(String type)
    {
        return factories.get(type);
    }
}
