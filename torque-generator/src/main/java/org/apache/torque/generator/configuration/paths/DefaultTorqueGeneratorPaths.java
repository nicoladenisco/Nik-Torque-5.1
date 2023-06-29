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
 * The default organization of the torque-generator directory.
 * 
 * <p>
 * This is:
 * <pre>
 * - torque-generator root
 * - - conf
 * - - - control.xml
 * - - outlets
 * - - templates
 * - - resources
 * </pre>
 * 
 */
public class DefaultTorqueGeneratorPaths implements TorqueGeneratorPaths
{
    /**
     * The path to the control configuration file, relative to the
     * configuration directory.
     */
    private static final String CONTROL_CONFIGURATION_FILE_NAME
    = "control.xml";

    /**
     * The path to the template directory, relative to the torque-generator
     * root directory.
     */
    private static final String TEMPLATES_DIRECTORY = "templates";

    /**
     * The path to the configuration directory, relative to the
     * torque-generator root directory.
     */
    private static final String CONFIGURATION_DIRECTORY = "conf";

    /**
     * The path to the outlet definitions directory, relative to the
     * configuration root directory.
     */
    private static final String OUTLET_DIRECTORY
    = "outlets";

    /**
     * The path to the resource directory, relative to the
     * configuration root directory.
     */
    private static final String RESOURCE_DIRECTORY
    = "resources";

    @Override
    public String getControlConfigurationFile()
    {
        return CONTROL_CONFIGURATION_FILE_NAME;
    }

    @Override
    public String getTemplateDirectory()
    {
        return TEMPLATES_DIRECTORY;
    }

    @Override
    public String getConfigurationDirectory()
    {
        return CONFIGURATION_DIRECTORY;
    }

    @Override
    public String getOutletDirectory()
    {
        return OUTLET_DIRECTORY;
    }

    @Override
    public String getResourceDirectory()
    {
        return RESOURCE_DIRECTORY;
    }
}
