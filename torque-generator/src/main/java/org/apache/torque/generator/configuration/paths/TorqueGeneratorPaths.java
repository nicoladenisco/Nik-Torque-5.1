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

/**
 * The paths describing the internal organization (i.e. file paths)
 * of the Torque generator files.
 */
public interface TorqueGeneratorPaths
{
    /**
     * Returns the path to the file containing the control configuration,
     * relative to the configuration directory.
     *
     * @return the path to the control configuration file, not null.
     */
    String getControlConfigurationFile();

    /**
     * Returns the path to the directory containing the templates,
     * relative to the torque-gen directory.
     *
     * @return the path to the template directory. not null.
     */
    String getTemplateDirectory();

    /**
     * Returns the path to the directory containing the configuration,
     * relative to the torque-gen directory.
     *
     * @return the path to the configuration directory. not null.
     */
    String getConfigurationDirectory();

    /**
     * Returns the path to the directory containing the outlet definitions,
     * relative to the Torque generator configuration directory.
     *
     * @return the path to the outlet definition directory, not null.
     */
    String getOutletDirectory();

    /**
     * Returns the path to the directory containing the resources,
     * relative to the Torque generator configuration directory.
     *
     * @return the path to the resources directory, not null.
     */
    String getResourceDirectory();
}
