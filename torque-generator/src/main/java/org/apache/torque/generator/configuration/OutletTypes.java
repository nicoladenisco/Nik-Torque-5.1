package org.apache.torque.generator.configuration;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.generator.configuration.outlet.GroovyOutletSaxHandlerFactory;
import org.apache.torque.generator.configuration.outlet.JavaOutletSaxHandlerFactory;
import org.apache.torque.generator.configuration.outlet.ReflectionOutletSaxHandlerFactory;
import org.apache.torque.generator.configuration.outlet.TypedOutletSaxHandlerFactory;
import org.apache.torque.generator.configuration.outlet.UntypedOutletSaxHandlerFactory;
import org.apache.torque.generator.configuration.outlet.VelocityOutletSaxHandlerFactory;

/**
 * Manages the types of known Outlets. This include information
 * about how to read the configuration of each outlet type.
 */
public class OutletTypes
{
    /**
     * A map containing all typed OutletSaxHandlerFactories,
     * keyed by the type of the outlet.
     */
    private final Map<String, TypedOutletSaxHandlerFactory>
    typedOutletHandlerFactories
        = new HashMap<>();

    /**
     * A List containing all untyped OutletSaxHandlerFactories.
     */
    private final List<UntypedOutletSaxHandlerFactory>
    untypedOutletHandlerFactories
        = new ArrayList<>();

    /**
     * Constructor. Creates a new instance containing the mappings to the
     * default outlet types.
     */
    public OutletTypes()
    {
        // register default outlet handler factories
        try
        {
            registerTypedOutletHandlerFactory(
                    new VelocityOutletSaxHandlerFactory());
            registerTypedOutletHandlerFactory(
                    new GroovyOutletSaxHandlerFactory());
            registerTypedOutletHandlerFactory(
                    new JavaOutletSaxHandlerFactory());
            registerUntypedOutletHandlerFactory(
                    new ReflectionOutletSaxHandlerFactory());
        }
        catch (final ConfigurationException e)
        {
            // should not happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a handler for a new type of outlets.
     *
     * @param factory the factory which handles the outlets of the
     *         given type.
     *
     * @throws NullPointerException if factory is null.
     * @throws ConfigurationException if a factory already exists
     *          for the type of the outlet.
     */
    public void registerTypedOutletHandlerFactory(
            final TypedOutletSaxHandlerFactory factory)
                    throws ConfigurationException
    {
        if (factory == null)
        {
            throw new NullPointerException("factory must not be null");
        }
        final String type = factory.getType();
        final TypedOutletSaxHandlerFactory oldFactory
        = typedOutletHandlerFactories.get(type);
        if (oldFactory != null)
        {
            throw new ConfigurationException(
                    "Attempted to register a OutletSaxHandlerFactory "
                            + "of type "
                            + factory.getType()
                            + " and class "
                            + factory.getClass().getName()
                            + " : A factory with this type already exists, "
                            + " it has the class "
                            + oldFactory.getClass().getName());
        }
        typedOutletHandlerFactories.put(factory.getType(), factory);
    }

    /**
     * Registers a untyped handler.
     *
     * @param factory the factory which can handle outlets of different types
     *
     * @throws NullPointerException if factory is null.
     * @throws ConfigurationException if a factory already exists
     *          for the type of the outlet.
     */
    public void registerUntypedOutletHandlerFactory(
            final UntypedOutletSaxHandlerFactory factory)
                    throws ConfigurationException
    {
        if (factory == null)
        {
            throw new NullPointerException("factory must not be null");
        }
        untypedOutletHandlerFactories.add(factory);
    }

    /**
     * Returns an unmodifiable map containing all typed outlet handler
     * factories, keyed by their type.
     *
     * @return all typed outlet handler factories, not null.
     */
    public Map<String, TypedOutletSaxHandlerFactory>
    getTypedOutletHandlerFactories()
    {
        return Collections.unmodifiableMap(typedOutletHandlerFactories);
    }

    /**
     * Returns an unmodifiable list containing all untyped outlet handler
     * factories.
     *
     * @return all untyped outlet handler factories, not null.
     */
    public List<UntypedOutletSaxHandlerFactory>
    getUntypedOutletHandlerFactories()
    {
        return Collections.unmodifiableList(untypedOutletHandlerFactories);
    }
}
