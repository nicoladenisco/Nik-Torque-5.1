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
import java.util.Map;

/**
 * Provides the default paths which Torque generator needs to interact with
 * its surrounding project.
 * "Default" means that the settings can be overridden in a unit of generation.
 * NB: This object does not contain any information about how the generator
 * configuration is organized internally.
 */
public interface ProjectPaths
{
    /**
     * Returns the path to the Torque generator configuration directory.
     * The path must either be absolute or relative to the current working
     * directory.
     *
     * @return the path to the Torque generator configuration;
     *         may (but must not) be null if no configuration is contained
     *         in the surrounding project;
     *         must be null if the configuration is read from the class path.
     *
     * @throws IllegalStateException if the current state of the object
     *         is not valid.
     */
    File getConfigurationPath();

    /**
     * Returns the package of the Torque generator configuration.
     *
     * @return the package to the Torque generator configuration. Must be null
     *         if the configuration is read from the file system or from
     *         a jar file. Must not be null if the configuration is read
     *         from the class path.
     *
     * @throws IllegalStateException if the current state of the object
     *         is not valid.
     */
    String getConfigurationPackage();

    /**
     * Returns the path to the default source directory for the
     * Torque generator.
     * The path must either be absolute or relative to the current working
     * directory.
     * "Default" means that the setting can be overridden in a unit of
     * generation. This usually points to a subdirectory
     * where the sources are located.
     *
     * @return the path for the source files, not null.
     *
     * @throws IllegalStateException if the current state of the object
     *         is not valid.
     */
    File getDefaultSourcePath();

    /**
     * Returns the path to the base directory for the generated output
     * for the given output key.
     *
     * The returned path is either absolute or relative to the current working
     * directory.
     *
     * @param outputDirKey the key which output directory should be returned.
     *        Null is the key for the default output directory
     *        and is always mapped to a non-null value.
     *
     * @return the base directory for the generated files, not null.
     *
     * @throws IllegalStateException if the current state of the object
     *         is not valid.
     * @throws IllegalArgumentException if the outputDirKey is unknown.
     */
    File getOutputDirectory(String outputDirKey);

    /**
     * Returns the output directory map which contains the mapping
     * from output directory key to output directory.
     *
     * @return the output directory map, not null, contains at least
     *         a mapping for the key null.
     *
     * @throws IllegalStateException if the current state of the object
     *         is not valid.
     */
    Map<String, File> getOutputDirectoryMap();

    /**
     * Returns the work directory where the torque generator can store
     * internal files.
     * These files should remain intact between runs of generation.
     *
     * @return the work directory, not null.
     *
     * @throws IllegalStateException if the current state of the object
     *         is not valid.
     */
    File getWorkDirectory();

    /**
     * Returns the cache directory where the torque generator can store
     * internal files.
     * These files can in principle be removed after each generation,
     * however, removal of these files may result in a longer work
     * of the generator.
     *
     * @return the cache directory, not null.
     *
     * @throws IllegalStateException if the current state of the object
     *         is not valid.
     */
    File getCacheDirectory();
}
