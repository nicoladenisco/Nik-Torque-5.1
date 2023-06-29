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

/**
 * Implementation of ProjectPaths if the configuration is located
 * in a jar file inside of a Maven 2 project.
 */
public class Maven2JarProjectPaths extends Maven2ProjectPaths
{
    /**
     * The path to the jar file, relative to the configuration root directory.
     */
    private String jarFileName;

    /**
     * Constructor.
     *
     * @param projectRoot path to the project root directory, not null.
     *        The path must either be absolute or relative to the current
     *        working directory.
     * @param jarFileName the name of the jar file, relative to the
     *        configuration root directory.
     */
    public Maven2JarProjectPaths(File projectRoot, String jarFileName)
    {
        super(projectRoot);
        if (jarFileName == null)
        {
            throw new NullPointerException("jarFileName must not be null");
        }
        this.jarFileName = jarFileName;
    }

    /**
     * Returns the subdirectory for the Torque generator files,
     * relative to the project root.
     *
     * @return the subdirectory for the Torque generator files, not null.
     */
    @Override
    public File getConfigurationPath()
    {
        return new File(
                getProjectRoot(),
                CONFIG_DIR + jarFileName);
    }

    /**
     * Returns the package of the Torque generator files.
     *
     * @return null.
     */
    @Override
    public String getConfigurationPackage()
    {
        return null;
    }
}
