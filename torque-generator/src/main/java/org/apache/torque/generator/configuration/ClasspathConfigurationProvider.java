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
import java.io.InputStream;
import java.util.Collection;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.paths.TorqueGeneratorPaths;

/**
 * Provides InputStreams to read a configuration of a unit of generation from a
 * jar file.
 */
public class ClasspathConfigurationProvider
extends AbstractConfigurationProvider
{
    /** The logger. */
    private static Log log
    = LogFactory.getLog(ClasspathConfigurationProvider.class);

    /** The description of the generation unit, not null. */
    private final UnitDescriptor unitDescriptor;

    /**
     * Constructor.
     * @param unitDescriptor The description of the generation unit, not null.
     *
     * @throws NullPointerException if unitDescriptor is null.
     */
    public ClasspathConfigurationProvider(final UnitDescriptor unitDescriptor)
    {
        super(unitDescriptor.getConfigurationPaths());
        this.unitDescriptor = unitDescriptor;
    }

    /**
     * @see ConfigurationProvider#getControlConfigurationLocation()
     */
    @Override
    public String getControlConfigurationLocation()
    {
        TorqueGeneratorPaths configurationPaths
        = unitDescriptor.getConfigurationPaths();
        return getFileName(
                configurationPaths.getControlConfigurationFile(),
                configurationPaths.getConfigurationDirectory());
    }

    protected String getFileName(
            final String name,
            final String directory)
    {
        String fileName
        = getConfigResourceBase()
        + "/"
        + directory
        + "/"
        + name;
        // make double dots work for resources in jar files
        fileName = FilenameUtils.normalizeNoEndSeparator(fileName);
        fileName = FilenameUtils.separatorsToUnix(fileName);
        return fileName;
    }

    @Override
    protected InputStream getInputStream(
            final String name,
            final String directory,
            final String fileDescription)
                    throws ConfigurationException
    {
        String fileName = getFileName(name, directory);

        ClassLoader classLoader = unitDescriptor.getClassLoader();
        if (classLoader == null)
        {
            classLoader = getClass().getClassLoader();
        }
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null)
        {
            throw new ConfigurationException(
                    "Could not read " + fileDescription + " file "
                            + fileName
                            + " in classpath");
        }
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        if (log.isDebugEnabled())
        {
            log.debug("Reading " + fileDescription + " file: '"
                    + fileName
                    + "' in classpath");
        }
        return bis;
    }

    @Override
    public Collection<String> getOutletConfigurationNames()
            throws ConfigurationException
    {
        String outletConfigurationSubdir = getConfigResourceBase()
                + "/"
                + unitDescriptor.getConfigurationPaths().getOutletDirectory();
        outletConfigurationSubdir
        = outletConfigurationSubdir.replace('\\', '/');

        PackageResources packageResources = new PackageResources(
                outletConfigurationSubdir,
                getClass().getClassLoader());
        return packageResources.getAllResourcesEndingWith(".xml", false);
    }

    /**
     * @see ConfigurationProvider#getTemplateNames()
     */
    @Override
    public Collection<String> getTemplateNames()
            throws ConfigurationException
    {
        String templatesConfigurationSubdir = getConfigResourceBase()
                + "/"
                + unitDescriptor.getConfigurationPaths().getTemplateDirectory();
        templatesConfigurationSubdir
        = templatesConfigurationSubdir.replace('\\', '/');

        PackageResources packageResources = new PackageResources(
                templatesConfigurationSubdir,
                getClass().getClassLoader());
        return packageResources.getAllResourcesEndingWith(null, true);
    }

    /**
     * Gets the resource name for the configuration base directory from the
     * projectPaths.
     *
     * @return the resource name for the configuration base directory,
     *         not null.
     */
    private String getConfigResourceBase()
    {
        String configResourceBase
        = unitDescriptor.getProjectPaths().getConfigurationPackage()
        .replace('.', '/');
        return configResourceBase;
    }
}
