package org.apache.torque.generator.configuration.controller;

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

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.option.OptionsConfiguration;
import org.apache.torque.generator.configuration.source.EntityReferences;


/**
 * Represents the control configuration of a generation unit.
 *
 * @version $Id: ControlConfiguration.java 1840416 2018-09-09 15:10:22Z tv $
 */
public class ControlConfiguration
{
    /** The list of all activities of the generation unit. */
    private List<Output> outputList = new ArrayList<>();

    /** The list of all options definitions within the generation unit. */
    private List<OptionsConfiguration> optionsConfigurations
        = new ArrayList<>();

    /** The entity references within the generation unit. */
    private EntityReferences entityReferences
        = new EntityReferences();

    /** The loglevel during generation, not null. */
    private Loglevel loglevel = Loglevel.INFO;

    /**
     * Adds an output file to the list of output files.
     *
     * @param output the output to add, not null.
     *
     * @throws NullPointerException if outputFile is null.
     * @throws ConfigurationException if an output
     *         with the same name already exists.
     */
    void addOutput(Output output) throws ConfigurationException
    {
        if (output == null)
        {
            throw new NullPointerException("output must not be null");
        }
        for (Output existing : outputList)
        {
            if (existing.getName().equals(output.getName()))
            {
                throw new ConfigurationException("Output with name "
                        + output.getName() + " already exists");
            }
        }
        outputList.add(output);
    }

    /**
     * Returns all output files.
     *
     * @return an unmodifiable list of output files, not null.
     */
    public List<Output> getOutputFiles()
    {
        return Collections.unmodifiableList(outputList);
    }

    /**
     * Adds a configuration for options.
     *
     * @param optionsConfiguration the option configuration to add, not null.
     *
     * @throws NullPointerException if optionConfiguration is null.
     */
    void addOptionsConfiguration(
            OptionsConfiguration optionsConfiguration)
    {
        if (optionsConfiguration == null)
        {
            throw new NullPointerException("optionsConfiguration is null");
        }
        optionsConfigurations.add(optionsConfiguration);
    }

    /**
     * Returns all options configurations.
     *
     * @return a unmodifiable list of option configurations, not null.
     */
    public List<OptionsConfiguration> getOptionsConfigurations()
    {
        return Collections.unmodifiableList(optionsConfigurations);
    }

    /**
     * Returns the entity references.
     *
     * @return the entity references, not null.
     */
    public EntityReferences getEntityReferences()
    {
        return entityReferences;
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("(outputFiles=")
        .append(outputList.toString())
        .append(", optionsConfigurations=")
        .append(optionsConfigurations.toString())
        .append(")");
        return result.toString();
    }

    /**
     * Returns the loglevel during generation.
     *
     * @return the loglevel during generation.
     */
    public Loglevel getLoglevel()
    {
        return loglevel;
    }

    /**
     * Set the loglevel during generation.
     *
     * @param loglevel the loglevel, not null.
     *
     * @throws NullPointerException if loglevel is null.
     */
    public void setLoglevel(Loglevel loglevel)
    {
        if (loglevel == null)
        {
            throw new NullPointerException("loglevel is null");
        }
        this.loglevel = loglevel;
    }
}
