package org.apache.torque.generator.configuration.paths;

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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of ProjectPaths with custom paths.
 */
public class CustomProjectPaths implements ProjectPaths
{
    /**
     * The configuration root directory.
     */
    private File configurationDir;

    /**
     * the configuration root package.
     */
    private String configurationPackage;

    /**
     * The directory where the source files are located.
     */
    private File sourceDir;

    /**
     * The output directories for the files, keyed by the output directory key.
     */
    private Map<String, File>  outputDirectoryMap = new HashMap<>();

    /**
     * The work directory where the torque generator can store internal files.
     */
    private File workDir;

    /**
     * The cache directory where the torque generator can store internal files.
     */
    private File cacheDir;

    /**
     * Copy-constructor.
     * @param toCopy the default project paths to copy, not null.
     *
     * @throws NullPointerException if toCopy is null.
     */
    public CustomProjectPaths(ProjectPaths toCopy)
    {
        this.configurationDir = toCopy.getConfigurationPath();
        this.configurationPackage = toCopy.getConfigurationPackage();
        this.sourceDir = toCopy.getDefaultSourcePath();
        this.outputDirectoryMap = new HashMap<>(
                toCopy.getOutputDirectoryMap());
        this.workDir = toCopy.getWorkDirectory();
        this.cacheDir = toCopy.getCacheDirectory();
    }

    /**
     * Constructor.
     * @param configurationDir the configuration directory for the Torque
     *        generator configuration files, or null if the configuration files
     *        are loaded from the classpath.
     * @param configurationPackage the package containing the Torque
     *        generator configuration files, or null if the configuration files
     *        are loaded from the file system.
     * @param sourceDir the default directory for the source files, or null.
     * @param outputDirectoryMap The output directories for the files,
     *        keyed by the output directory key. The directory with the key
     *        null is the default output directory.
     * @param workDir work directory where the torque generator can store
     *        internal files.
     * @param cacheDir cache directory where the torque generator can store
     *        internal files.
     */
    public CustomProjectPaths(
            File configurationDir,
            String configurationPackage,
            File sourceDir,
            Map<String, File> outputDirectoryMap,
            File workDir,
            File cacheDir)
    {
        this.configurationDir = configurationDir;
        this.configurationPackage = configurationPackage;
        this.sourceDir = sourceDir;
        this.outputDirectoryMap = new HashMap<>(outputDirectoryMap);
        this.workDir = workDir;
        this.cacheDir = cacheDir;
    }

    /**
     * Sets the root directory for the Torque generator configuration files,
     * absolute or relative to the project root.
     *
     * @param configurationDir the configuration directory for the Torque
     *        generator configuration files, null to invalidate the current
     *        setting.
     */
    public void setConfigurationDir(File configurationDir)
    {
        this.configurationDir = configurationDir;
    }

    /**
     * Sets the root package for the Torque generator files,
     * relative to the project root.
     *
     * @param configurationPackage the configuration root package
     *        for the Torque generator files.
     */
    public void setConfigurationPackage(String configurationPackage)
    {
        this.configurationPackage = configurationPackage;
    }

    /**
     * Sets the default directory for the source files,
     * relative to the current directory, or absolute.
     * "Default" means that the setting can be overridden in a unit of
     * generation.
     *
     * @param sourceDir the default directory for the source files,
     *        null to invalidate the current setting.
     */
    public void setSourceDir(File sourceDir)
    {
        this.sourceDir = sourceDir;
    }

    /**
     * Sets the output directory for a given output directory key.
     *
     * @param outputDirKey the output directory key, or null for the default
     *        output directory.
     * @param outputDir the output directory for the key,
     *        relative to the current directory, or absolute.
     *        Use null to remove the output directory for the key.
     */
    public void setOutputDirectory(String outputDirKey, File outputDir)
    {
        this.outputDirectoryMap.put(outputDirKey, outputDir);
    }

    /**
     * Sets the contents of the output directory map. The directory with the key
     *        null is the default output directory.
     *
     * @param outputDirectoryMap the new output directory map.
     */
    public void setOutputDirectoryMap(Map<String, File> outputDirectoryMap)
    {
        this.outputDirectoryMap.clear();
        this.outputDirectoryMap.putAll(outputDirectoryMap);
    }

    /**
     * Sets the work directory where the torque generator can store
     * internal files, relative to the current directory, or absolute.
     *
     * @param workDir the work directory for internal files,
     *         null to invalidate the current setting.
     */
    public void setWorkDir(File workDir)
    {
        this.workDir = workDir;
    }

    /**
     * Sets the cache directory where the torque generator can store
     * internal files, relative to the current directory, or absolute.
     *
     * @param cacheDir the cache directory for internal files,
     *         null to invalidate the current setting.
     */
    public void setCacheDir(File cacheDir)
    {
        this.cacheDir = cacheDir;
    }

    /**
     * Returns the root directory for the Torque generator files,
     * relative to the project root.
     *
     * @return the directory for the Torque generator files, not null.
     * @throws IllegalStateException if one of the required parameters
     *         is not set.
     */
    @Override
    public File getConfigurationPath()
    {
        checkInit();
        return configurationDir;
    }

    /**
     * Returns the root package of the Torque generator files.
     *
     * @return the root package of the Torque generator files.
     * @throws IllegalStateException if one of the required parameters
     *         is not set.
     */
    @Override
    public String getConfigurationPackage()
    {
        checkInit();
        return configurationPackage;
    }


    /**
     * Returns the default directory for the source files,
     * relative to the project root. "Default" means that the setting
     * can be overridden in a unit of generation.
     *
     * @return the directory for the source files, not null.
     * @throws IllegalStateException if one of the required parameters
     *         is not set.
     */
    @Override
    public File getDefaultSourcePath()
    {
        checkInit();
        return sourceDir;
    }

    /**
     * Returns the output directory for a given output directory key.
     *
     * @param outputDirKey the output directory key, or null for the default
     *        output directory.
     *
     * @return the output directory for the key,
     *         relative to the current directory, or absolute, not null.
     *
     * @throws IllegalStateException if one of the required parameters
     *         is not set.
     * @throws IllegalArgumentException if the outputDirKey is unknown.
     */
    @Override
    public File getOutputDirectory(String outputDirKey)
    {
        checkInit();
        File result = outputDirectoryMap.get(outputDirKey);
        if (result == null)
        {
            throw new IllegalArgumentException(
                    "unknown outputDirKey " + outputDirKey);
        }
        return result;
    }

    /**
     * Returns the output directory map which contains the mapping
     * from output directory key to output directory.
     *
     * @return the mutable output directory map, not null, contains at least
     *         a mapping for the key null.
     *
     * @throws IllegalStateException if one of the required parameters
     *         is not set.
     */
    @Override
    public Map<String, File> getOutputDirectoryMap()
    {
        checkInit();
        return outputDirectoryMap;
    }

    /**
     * Returns the work directory where the torque generator can store
     * internal files, relative to the project root.
     *
     * @return the work directory where the torque generator can store
     *         internal files, not null.
     * @throws IllegalStateException if one of the required parameters
     *         is not set.
     */
    @Override
    public File getWorkDirectory()
    {
        checkInit();
        return workDir;
    }

    /**
     * Returns the cache directory where the torque generator can store
     * internal files, relative to the project root.
     *
     * @return the cache directory where the torque generator can store
     *         internal files, not null.
     * @throws IllegalStateException if one of the required parameters
     *         is not set.
     */
    @Override
    public File getCacheDirectory()
    {
        checkInit();
        return cacheDir;
    }

    /**
     * Checks whether the current settings are valid.
     * It is checked whether all necessary informations are set.
     * If not, an IllegalStateException is thrown.
     *
     * @throws IllegalStateException if the current settings are valid, false otherwise.
     */
    public void checkInit()
    {
        if (configurationDir == null && configurationPackage == null)
        {
            throw new IllegalStateException(
                    "configurationDir or configurationPackage must not be null.");
        }
        if (sourceDir == null)
        {
            throw new IllegalStateException(
                    "sourceDir must not be null.");
        }
        if (outputDirectoryMap.get(null) == null)
        {
            throw new IllegalStateException(
                    "default output directory must not be null.");
        }
        if (workDir == null)
        {
            throw new IllegalStateException(
                    "workDir must not be null.");
        }
        if (cacheDir == null)
        {
            throw new IllegalStateException(
                    "cacheDir must not be null.");
        }
    }

    @Override
    public String toString()
    {
        return "(CustomProjectPaths: configurationDir=" + configurationDir
                + ", configurationPackage=" + configurationPackage
                + ", sourceDir=" + sourceDir
                + ", outputDirectoryMap=" + outputDirectoryMap
                + ", workDir=" + workDir
                + ", cacheDir=" + cacheDir
                + ")";
    }
}
