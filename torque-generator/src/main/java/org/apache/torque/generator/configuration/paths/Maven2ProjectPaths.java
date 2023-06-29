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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains information of the default integration of the Torque generator
 * into a maven 2 project.
 */
public abstract class Maven2ProjectPaths implements ProjectPaths
{
    /** The log for the class. */
    private static Log log
    = LogFactory.getLog(Maven2ProjectPaths.class);

    /**
     * Default configuration root directory,
     * relative to the project root.
     */
    protected static final String CONFIG_DIR = "src/main/torque-gen/";

    /**
     * Default generation source file directory,
     * relative to the configuration root directory.
     */
    protected static final String SOURCE_DIR = CONFIG_DIR + "src";

    /**
     * Key for the target directory for generated files
     * which are modifiable by the user, relative to the project root.
     */
    public static final String MODIFIABLE_OUTPUT_DIR_KEY
    = "modifiable";

    /**
     * Default generation target directory for generated files
     * which are modifiable by the user, relative to the project root.
     */
    public static final String MODIFIABLE_OUTPUT_DIR
    = "src/main/generated-java";

    /**
     * Default generation target directory,
     * relative to the project root.
     */
    protected static final String DEFAULT_OUTPUT_DIR
    = "target/generated-sources";

    /**
     * Default working directory.
     */
    protected static final String WORK_DIR
    = "src/main/torque-gen/work";

    /**
     * Default cache directory.
     */
    protected static final String CACHE_DIR
    = "target/torque-gen/cache";

    /**
     * The root directory of the whole maven2 project.
     */
    private final File projectRoot;

    /**
     * The output directory map which contains the mapping
     * from output directory key to output directory.
     */
    private final Map<String, File> outputDirectoryMap = new HashMap<>();

    /**
     * Constructor.
     *
     * @param projectRoot path to the project root directory, not null.
     *        The path must either be absolute or relative to the current
     *        working directory.
     *
     * @throws NullPointerException if projectRoot is null.
     */
    protected Maven2ProjectPaths(final File projectRoot)
    {
        if (projectRoot == null)
        {
            log.error("projectRoot is null");
            throw new NullPointerException("projectRoot is null");
        }
        this.projectRoot = projectRoot;
        outputDirectoryMap.put(null, new File(projectRoot, DEFAULT_OUTPUT_DIR));
        outputDirectoryMap.put(
                MODIFIABLE_OUTPUT_DIR_KEY,
                new File(projectRoot, MODIFIABLE_OUTPUT_DIR));
    }

    @Override
    public abstract File getConfigurationPath();

    @Override
    public abstract String getConfigurationPackage();

    /**
     * Returns the path to the source files.
     *
     * @return the path to for the source files, not null.
     */
    @Override
    public File getDefaultSourcePath()
    {
        return new File(projectRoot, SOURCE_DIR);
    }

    /**
     * Returns the output directory map which contains the mapping
     * from output directory key to output directory.
     *
     * @return the unmodifiable output directory map, not null.
     *         Contains a mapping for the keys null and "modifiable".
     *
     * @throws IllegalStateException if the current state of the object
     *         is not valid.
     */
    @Override
    public Map<String, File> getOutputDirectoryMap()
    {
        return Collections.unmodifiableMap(outputDirectoryMap);
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
     * @throws IllegalArgumentException if the outputDirKey is unknown.
     */
    @Override
    public File getOutputDirectory(final String outputDirKey)
    {
        File result = outputDirectoryMap.get(outputDirKey);
        if (result == null)
        {
            throw new IllegalArgumentException(
                    "unknown outputDirKey " + outputDirKey);
        }
        return result;
    }

    /**
     * Returns the default work subdirectory
     * where the torque generator can store internal files.
     *
     * @return the work subdirectory, not null.
     */
    @Override
    public File getWorkDirectory()
    {
        return new File(projectRoot, WORK_DIR);
    }

    /**
     * Returns the default cache subdirectory
     * where the torque generator can store internal files.
     *
     * @return the work subdirectory, not null.
     */
    @Override
    public File getCacheDirectory()
    {
        return new File(projectRoot, CACHE_DIR);
    }

    /**
     * returns the root directory of the whole maven 2 project.
     *
     * @return the root directory of the whole project, not null.
     */
    protected File getProjectRoot()
    {
        return projectRoot;
    }
}
