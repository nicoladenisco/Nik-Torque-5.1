package org.apache.torque.generator.configuration.option;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.option.Option;

/**
 * An option configuration which reads the options from a properties file.
 *
 * $Id: PropertiesOptionConfiguration.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class PropertiesOptionConfiguration extends FileOptionsConfiguration
{
    /**
     * Reads the options from the property file given in the path and
     * returns them.
     *
     * @param configurationProvider the provider to access configuration files,
     *        not null.
     *
     * @return the options contained in the file, not null.
     *
     * @throws ConfigurationException if the file cannot be accessed or parsed.
     */
    @Override
    public Collection<Option> getOptions(
            ConfigurationProvider configurationProvider)
                    throws ConfigurationException
    {
        Properties properties = new Properties();
        try (InputStream optionsInputStream = configurationProvider.getOptionsInputStream(getPath()))
        {
            properties.load(optionsInputStream);
        }
        catch (IOException | RuntimeException e)
        {
            throw new ConfigurationException("Error reading options file "
                    + getPath(),
                    e);
        }
        return toOptions(properties);
    }
}
