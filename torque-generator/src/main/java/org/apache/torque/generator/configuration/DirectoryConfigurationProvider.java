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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.paths.ProjectPaths;
import org.apache.torque.generator.configuration.paths.TorqueGeneratorPaths;

/**
 * Provides InputStreams to read the configuration from a directory.
 */
public class DirectoryConfigurationProvider
extends AbstractConfigurationProvider
{
    /** The logger. */
    private static Log log
    = LogFactory.getLog(DirectoryConfigurationProvider.class);

    /**
     * The paths needed to interact with the enclosing project, not null.
     */
    private final ProjectPaths projectPaths;

    /**
     * The internal directory structure of the Torque generator configuration
     * files, not null.
     */
    private final TorqueGeneratorPaths configurationPaths;

    /**
     * Constructor.
     *
     * @param projectPaths the paths needed to interact with the enclosing
     *        project, not null.
     * @param configurationPaths The internal directory structure of the
     *        generator files, not null.
     *
     * @throws NullPointerException if projectPaths or configurationPaths
     *         are null.
     */
    public DirectoryConfigurationProvider(
            final ProjectPaths projectPaths,
            final TorqueGeneratorPaths configurationPaths)
    {
        super(configurationPaths);
        if (projectPaths == null)
        {
            throw new NullPointerException("projectPaths is null");
        }
        this.projectPaths = projectPaths;
        this.configurationPaths = configurationPaths;
    }

    @Override
    public String getControlConfigurationLocation()
            throws ConfigurationException
    {
        return getFile(
                configurationPaths.getControlConfigurationFile(),
                configurationPaths.getConfigurationDirectory(),
                "control file").getAbsolutePath();
    }

    private File getFile(
            final String name,
            final String directory,
            final String description)
                    throws ConfigurationException
    {
        File file = null;
        try
        {
            File configDir =  new File(
                    projectPaths.getConfigurationPath(),
                    directory);

            file = new File(configDir, name);
            // use canonical file to resolve . and .. directories
            file = file.getCanonicalFile();
        }
        catch (IOException e)
        {
            throw new ConfigurationException("Canonical name for "
                    + description + file
                    + " could not be calculated",
                    e);
        }
        return file;
    }

    @Override
    protected InputStream getInputStream(
            final String name,
            final String directory,
            final String description)
                    throws ConfigurationException
    {
        File file = getFile(name, directory, description);

        InputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(file);
        }
        catch (FileNotFoundException e)
        {
            throw new ConfigurationException(description + " file "
                    + file.getAbsolutePath()
                    + " not found",
                    e);
        }
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        if (log.isDebugEnabled())
        {
            log.debug("Reading " + description + " file: '"
                    + file.getAbsolutePath() + "'");
        }
        return bis;
    }

    @Override
    public Collection<String> getOutletConfigurationNames()
            throws ConfigurationException
    {
        File outletConfigDir =  new File(
                projectPaths.getConfigurationPath(),
                configurationPaths.getOutletDirectory());

        ArrayList<String> result = new ArrayList<>();
        if (!outletConfigDir.isDirectory())
        {
            log.info(
                    "OutletsConfigDirectory "
                            + outletConfigDir.getAbsolutePath()
                            + "is not a directory, "
                            + "no outlet definitions will be read "
                            + "(template outlets may still be available "
                            + "through scanning the templates directory)");
            return result;
        }

        File[] sourceFiles = outletConfigDir.listFiles();
        if ( sourceFiles != null )
        {
            result =
                    Arrays.stream(sourceFiles)
                            .filter(srcFile -> !srcFile.isDirectory() && srcFile.getPath().endsWith("xml"))
                            .map(File::getName)
                            .collect(Collectors.toCollection(ArrayList::new));
        }
        return result;
    }

    /**
     * @see ConfigurationProvider#getTemplateNames()
     */
    @Override
    public Collection<String> getTemplateNames()
            throws ConfigurationException
    {
        File templatesConfigurationSubdir = new File(
                projectPaths.getConfigurationPath()
                + "/"
                + configurationPaths.getTemplateDirectory());

        return PackageResources.getFilesInDirectoryWithSuffix(
                templatesConfigurationSubdir, null, "", true);
    }

}
