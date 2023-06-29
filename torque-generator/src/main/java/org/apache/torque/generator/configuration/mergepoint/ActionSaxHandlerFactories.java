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

/**
 * A registry of ActionSaxHandlerFactories.
 *
 * $Id: ActionSaxHandlerFactories.java 1840416 2018-09-09 15:10:22Z tv $
 */
public class ActionSaxHandlerFactories
{
    /** The class log. */
    private static Log log = LogFactory.getLog(ActionSaxHandlerFactories.class);

    /**
     * A map containing all known ActionSaxHandlerFactories,
     * keyed by the type of the action.
     */
    private Map<String, ActionSaxHandlerFactory> actionSaxHandlerFactories
        = new HashMap<>();

    /**
     * Constructor. Registers the default Factories.
     */
    public ActionSaxHandlerFactories()
    {
        try
        {
            register(new TraverseAllActionSaxHandlerFactory());
            register(new ApplyActionSaxHandlerFactory());
            register(new OptionActionSaxHandlerFactory());
            register(new SourceElementAttributeActionSaxHandlerFactory());
            register(new OutputActionSaxHandlerFactory());
        }
        catch (ConfigurationException e)
        {
            // should not happen
            log.error("caught ConfigurationException while registering "
                    + "the default Action Sax Handler Factories", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a handler for a new action type.
     *
     * @param factory the factory which handles the action of the
     *          given type.
     *
     * @throws ConfigurationException if a factory already exists
     *           for the type of the action.
     */
    public void register(
            ActionSaxHandlerFactory factory)
                    throws ConfigurationException
    {
        ActionSaxHandlerFactory oldFactory
        = actionSaxHandlerFactories.get(factory.getType());
        if (oldFactory != null)
        {
            throw new ConfigurationException(
                    "Attempted to register an ActionSaxHandlerFactory "
                            + "of type "
                            + factory.getType()
                            + " and class "
                            + factory.getClass().getName()
                            + " : A factory with this type already exists, "
                            + " it has the class "
                            + oldFactory.getClass().getName());
        }
        actionSaxHandlerFactories.put(factory.getType(), factory);
    }

    /**
     * Returns the ActionSaxHandlerFactory associated with the given type.
     *
     * @param type the type top look for, not null.
     *
     * @return the ActionSaxHandlerFactory associated with the given type,
     *         or null if no ActionSaxHandlerFactory exists for the given type.
     */
    public ActionSaxHandlerFactory getActionSaxHandlerFactory(String type)
    {
        return actionSaxHandlerFactories.get(type);
    }
}
