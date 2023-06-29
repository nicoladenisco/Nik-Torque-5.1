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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.paths.ProjectPaths;
import org.apache.torque.generator.configuration.paths.TorqueGeneratorPaths;

/**
 * Provides InputStreams to read a configuration of a unit of generation from a
 * jar file.
 */
public class JarConfigurationProvider
extends AbstractConfigurationProvider
{
    /** The logger. */
    private static Log log
    = LogFactory.getLog(JarConfigurationProvider.class);

    /**
     * The paths needed to interact with the enclosing project, not null.
     */
    private final ProjectPaths projectPaths;

    /**
     * The internal directory structure of the generator configuration files,
     * not null.
     */
    private final TorqueGeneratorPaths configurationPaths;

    /**
     * The jar file from which the configuration should be read, not null.
     */
    private JarFile jarFile;

    /**
     * Constructor.
     * @param projectPaths the paths needed to interact with the enclosing
     *         project, not null.
     * @param configurationPaths The internal directory structure of the
     *         generator configuration files, not null.
     *
     * @throws NullPointerException if projectPaths or configurationPaths
     *          are null.
     * @throws ConfigurationException if the jar file can not be accessed.
     */
    public JarConfigurationProvider(
            final ProjectPaths projectPaths,
            final TorqueGeneratorPaths configurationPaths)
                    throws ConfigurationException
    {
        super(configurationPaths);
        if (projectPaths == null)
        {
            throw new NullPointerException("projectPaths is null");
        }
        this.projectPaths = projectPaths;
        this.configurationPaths = configurationPaths;

        try
        {
            jarFile = new JarFile(projectPaths.getConfigurationPath());
        }
        catch (IOException e)
        {
            log.error("Could not open jar File "
                    + projectPaths.getConfigurationPath()
                    .getAbsolutePath());
            throw new ConfigurationException(e);
        }
    }

    @Override
    public String getControlConfigurationLocation()
            throws ConfigurationException
    {
        return projectPaths.getConfigurationPath() + ":"
                + configurationPaths.getConfigurationDirectory() + "/"
                + configurationPaths.getControlConfigurationFile();
    }

    @Override
    protected InputStream getInputStream(
            final String name,
            final String directory,
            final String description)
                    throws ConfigurationException
    {
        String fileName = directory + "/" + name;

        InputStream inputStream = null;
        try
        {
            JarEntry jarEntry = jarFile.getJarEntry(fileName);
            inputStream = jarFile.getInputStream(jarEntry);
        }
        catch (IOException e)
        {
            throw new ConfigurationException(
                    "Could not read " + description + " file "
                            + fileName
                            + " in jar file "
                            + projectPaths.getConfigurationPath(),
                            e);
        }
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        if (log.isDebugEnabled())
        {
            log.debug("Reading " + description + " file: '"
                    + projectPaths.getConfigurationPath()
                    + "' in jar file "
                    + projectPaths.getConfigurationPath());
        }
        return bis;
    }

    @Override
    public Collection<String> getOutletConfigurationNames()
            throws ConfigurationException
    {
        return PackageResources.getFilesInJarDirectoryWithSuffix(
                configurationPaths.getOutletDirectory(),
                jarFile,
                ".xml",
                false);
    }

    /**
     * @see ConfigurationProvider#getTemplateNames()
     */
    @Override
    public Collection<String> getTemplateNames()
            throws ConfigurationException
    {
        JarFile templatesConfigurationFile;
        try
        {
            templatesConfigurationFile = new JarFile(
                    projectPaths.getConfigurationPath());
        }
        catch (IOException e)
        {
            throw new ConfigurationException(e);
        }

        return PackageResources.getFilesInJarDirectoryWithSuffix(
                configurationPaths.getTemplateDirectory(),
                templatesConfigurationFile,
                null,
                true);
    }
}
