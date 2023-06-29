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
import java.util.Collection;

/**
 * Defines how the configuration for a generation unit can be accessed.
 * Implementing classes provide InputStreams to access the various parts
 * of the configuration.
 */
public interface ConfigurationProvider
{
    /**
     * Creates a reader to access the control configuration.
     * It is the callers responsibility to close the reader after use.
     *
     * @return a reader to access the control configuration, never null.
     *
     * @throws ConfigurationException if the reader can not be created.
     */
    InputStream getControlConfigurationInputStream()
            throws ConfigurationException;

    /**
     * Returns the location of the control configuration as human readable
     * String for debugging and error tracking purposes.
     *
     * @return the location of the control configuration, not null.
     *
     * @throws ConfigurationException if the location name can not be created.
     */
    // The control configuration location is important if loading the control
    // file fails for a project with multiple units of generation.
    // Using the location in the error messages helps finding out which
    // control configuration contains the error.
    String getControlConfigurationLocation() throws ConfigurationException;

    /**
     * Lists all available template names.
     *
     * @return a collection of all available template names, not null.
     *
     * @throws ConfigurationException if the template names
     *         cannot be determined.
     */
    Collection<String> getTemplateNames()
            throws ConfigurationException;

    /**
     * Creates a reader to access a template.
     * It is the callers responsibility to close the reader after use.
     *
     * @param name the name (==path to) of the template.
     *
     * @return a reader to access a template, never null.
     *
     * @throws ConfigurationException if the reader can not be created.
     */
    InputStream getTemplateInputStream(String name)
            throws ConfigurationException;

    /**
     * Returns a list of all found outlet configuration files in the
     * generation unit.
     *
     * @return a list with the generation configuration files, not null.
     *
     * @throws ConfigurationException if the configuration can not be read.
     */
    Collection<String> getOutletConfigurationNames()
            throws ConfigurationException;

    /**
     * Creates a reader to access the configuration for one outlet.
     * It is the callers responsibility to close the reader after use.
     *
     * @param name the name (==path to) of the outlet configuration.
     *
     * @return a reader to access the outlet configuration, never null.
     *
     * @throws ConfigurationException if the reader can not be created.
     */
    InputStream getOutletConfigurationInputStream(String name)
            throws ConfigurationException;

    /**
     * Creates a reader to access an options file.
     * It is the callers responsibility to close the reader after use.
     *
     * @param name the name (==path to) of the options file.
     *
     * @return a reader to access the options file, never null.
     *
     * @throws ConfigurationException if the reader can not be created.
     */
    InputStream getOptionsInputStream(String name)
            throws ConfigurationException;


    /**
     * Creates a reader to access a resource file.
     * It is the callers responsibility to close the reader after use.
     *
     * @param path the path to of the resource file.
     *
     * @return a reader to access the options file, never null.
     *
     * @throws ConfigurationException if the reader can not be created.
     */
    InputStream getResourceInputStream(String path)
            throws ConfigurationException;
}
