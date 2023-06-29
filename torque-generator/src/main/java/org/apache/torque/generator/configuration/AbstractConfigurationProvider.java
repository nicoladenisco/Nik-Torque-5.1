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


import java.io.InputStream;

import org.apache.torque.generator.configuration.paths.TorqueGeneratorPaths;

/**
 * A base class for all ConfigurationProvider implementations.
 * @version $Id$
 */
public abstract class AbstractConfigurationProvider
implements ConfigurationProvider
{
    /**
     * The internal directory structure of the Torque generator
     * configuration files, not null.
     */
    private final TorqueGeneratorPaths configurationPaths;

    /**
     * Constructor.
     *
     * @param configurationPaths The internal directory structure of the
     *        Torque generator configuration files, not null.
     *
     * @throws NullPointerException if configurationPaths is null.
     */
    public AbstractConfigurationProvider(
            final TorqueGeneratorPaths configurationPaths)
    {
        if (configurationPaths == null)
        {
            throw new NullPointerException("configurationPaths is null");
        }
        this.configurationPaths = configurationPaths;
    }

    /* (non-Javadoc)
     * @see org.apache.torque.generator.configuration.ConfigurationProvider#getControlConfigurationInputStream()
     */
    @Override
    public InputStream getControlConfigurationInputStream()
            throws ConfigurationException
    {
        return getInputStream(
                configurationPaths.getControlConfigurationFile(),
                configurationPaths.getConfigurationDirectory(),
                "control file");
    }

    /* (non-Javadoc)
     * @see org.apache.torque.generator.configuration.ConfigurationProvider#getTemplateInputStream(java.lang.String)
     */
    @Override
    public InputStream getTemplateInputStream(final String name)
            throws ConfigurationException
    {
        return getInputStream(
                name,
                configurationPaths.getTemplateDirectory(),
                "template");
    }

    /* (non-Javadoc)
     * @see org.apache.torque.generator.configuration.ConfigurationProvider#getOutletConfigurationInputStream(java.lang.String)
     */
    @Override
    public InputStream getOutletConfigurationInputStream(final String name)
            throws ConfigurationException
    {
        return getInputStream(
                name,
                configurationPaths.getOutletDirectory(),
                "outlet configuration");
    }

    /* (non-Javadoc)
     * @see org.apache.torque.generator.configuration.ConfigurationProvider#getResourceInputStream(java.lang.String)
     */
    @Override
    public InputStream getResourceInputStream(final String name)
            throws ConfigurationException
    {
        return getInputStream(
                name,
                configurationPaths.getResourceDirectory(),
                "resource");
    }

    /* (non-Javadoc)
     * @see org.apache.torque.generator.configuration.ConfigurationProvider#getOptionsInputStream(java.lang.String)
     */
    @Override
    public InputStream getOptionsInputStream(final String name)
            throws ConfigurationException
    {
        return getInputStream(
                name,
                configurationPaths.getConfigurationDirectory(),
                "option");
    }

    /**
     * @param name input stream name
     * @param directory location
     * @param fileDescription describes the file
     * @return an InputStream
     * @throws ConfigurationException if unable to process
     */
    protected abstract InputStream getInputStream(
            String name,
            String directory,
            String fileDescription)
                    throws ConfigurationException;
}
