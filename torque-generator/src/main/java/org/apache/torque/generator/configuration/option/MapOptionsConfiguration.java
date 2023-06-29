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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.option.Option;

/**
 * An option configuration in which the options are provided inside a java map.
 *
 * $Id: MapOptionsConfiguration.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class MapOptionsConfiguration extends OptionsConfigurationBase
{
    /** The map containing the options. */
    private Map<String, String> optionMap;

    /**
     * Constructor.
     *
     * @param content the options to set.
     *
     * @throws NullPointerException if content is null.
     */
    public MapOptionsConfiguration(final Map<String, String> content)
    {
        optionMap = new HashMap<>(content);
    }

    /**
     * Returns the options map.
     *
     * @return the options map, not null.
     */
    public Map<String, String> getOptionMap()
    {
        return optionMap;
    }

    /**
     * Returns the contained options.
     *
     * @param configurationProvider the configuration provider to access
     *        configuration files, not null.
     *
     * @return the options contained in this configuration, not null.
     */
    @Override
    public Collection<Option> getOptions(
            ConfigurationProvider configurationProvider)
                    throws ConfigurationException
    {
        return toOptions(optionMap);
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("(optionMap=")
        .append(optionMap);
        result.append(")");
        return result.toString();
    }
}
