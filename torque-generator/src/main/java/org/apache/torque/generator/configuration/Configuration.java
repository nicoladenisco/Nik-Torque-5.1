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
import java.util.List;

import org.apache.torque.generator.configuration.controller.Loglevel;

/**
 * The configuration class keeps the complete configuration needed for the
 * generation process. It is supplied with UnitDescriptors describing the
 * available units of generation, and can then read the configuration.
 * After the configuration is read, no new units of generation can be added.
 */
public class Configuration
{
    /**
     * All known units of generation.
     */
    private List<UnitDescriptor> unitDescriptors
        = new ArrayList<>();

    /**
     * The configurations for all known units of generation.
     */
    private List<UnitConfiguration> unitConfigurations;

    /**
     * The available configuration handlers.
     */
    private ConfigurationHandlers configurationHandlers
        = new ConfigurationHandlers();

    /**
     * Whether the configuration has already been read.
     */
    private boolean read = false;

    /**
     * Adds a unit of generation to the configuration.
     *
     * @param unitDescriptor Describes the unit of generation to add,
     *         not null.
     *
     * @throws NullPointerException if unitDescriptor is null.
     * @throws IllegalStateException if the configuration has already been
     *          read.
     */
    public void addUnit(UnitDescriptor unitDescriptor)
    {
        if (unitDescriptor == null)
        {
            throw new NullPointerException("unitDescriptor must not be null.");
        }
        if (read)
        {
            throw new IllegalStateException(
                    "Configuration has already been read.");
        }
        unitDescriptors.add(unitDescriptor);
    }


    /**
     * Adds several units of generation to the configuration.
     *
     * @param unitDescriptors Describes the units of generation to add,
     *         not null.
     *
     * @throws NullPointerException if unitDescriptors is null or the list
     *          contains a null entry.
     * @throws IllegalStateException if the configuration has already been
     *          read.
     */
    public void addUnits(List<UnitDescriptor> unitDescriptors)
    {
        for (UnitDescriptor unitDescriptor : unitDescriptors)
        {
            addUnit(unitDescriptor);
        }
    }

    /**
     * Reads the configuration.
     * 
     * If a {@link UnitDescriptor} does not provide a loglevel,
     * {@link Loglevel#getCurrentLoglevel()} will be applied (which is the root log level of the provided adapter).
     *
     * @throws ConfigurationException if an error occurs during reading the
     *          configuration.
     */
    public void read() throws ConfigurationException
    {
        if (read)
        {
            throw new IllegalStateException(
                    "Configuration has already been read.");
        }
        unitConfigurations = new ArrayList<>();
        Loglevel oldLoglevel = Loglevel.getCurrentLoglevel();
        for (UnitDescriptor unitDescriptor : unitDescriptors)
        {
            if (unitDescriptor.getLoglevel() != null)
            {
                unitDescriptor.getLoglevel().apply();
            }
            else
            {
                oldLoglevel.apply();
            }
            UnitConfigurationReader configurationReader
                = new UnitConfigurationReader();

            UnitConfiguration unitConfiguration
            = configurationReader.read(
                    unitDescriptor,
                    configurationHandlers);
            if (unitDescriptor.getLoglevel() != null)
            {
                unitConfiguration.setLoglevel(unitDescriptor.getLoglevel());
            }
            unitConfigurations.add(unitConfiguration);
        }
        read = true;
    }

    /**
     * Returns the list of UnitConfigurations.
     *
     * @return the list of unit configurations, never null.
     *
     * @throws IllegalStateException if the configuration was not yet read.
     */
    public List<UnitConfiguration> getUnitConfigurations()
    {
        if (!read)
        {
            throw new IllegalStateException("Configuration was not yet read");
        }
        return Collections.unmodifiableList(unitConfigurations);
    }

    /**
     * Returns the available configuration handlers.
     *
     * @return the available configuration handlers, not null.
     */
    public ConfigurationHandlers getConfigurationHandlers()
    {
        return configurationHandlers;
    }

    /**
     * Sets the available configuration handlers.
     *
     * @param configurationHandlers the available configuration handlers,
     *        not null.
     */
    public void setConfigurationHandlers(
            ConfigurationHandlers configurationHandlers)
    {
        if (configurationHandlers == null)
        {
            throw new NullPointerException(
                    "configurationHandlers must not be null");
        }
        this.configurationHandlers = configurationHandlers;
    }
}
