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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.option.Option;
import org.apache.torque.generator.option.OptionImpl;

/**
 * Base class with common functionality for all option configurations.
 *
 * $Id: OptionsConfigurationBase.java 1839288 2018-08-27 09:48:33Z tv $
 */
abstract class OptionsConfigurationBase implements OptionsConfiguration
{
    /** The class log. */
    private static Log log = LogFactory.getLog(OptionsConfigurationBase.class);

    /**
     * Creates options from a Map and returns them.
     *
     * @param optionsMap the map containing the option qualified names as key
     *        and the option value as value. optionsMap and the keys therein
     *        must not be null.
     *
     * @return the options, not null.
     */
    protected Collection<Option> toOptions(
            Map<? extends Object, ? extends Object> optionsMap)
    {
        Set<Option> options = new HashSet<>();
        for (Entry<? extends Object, ? extends Object> entry
                : optionsMap.entrySet())
        {
            Option option = new OptionImpl(
                    entry.getKey().toString(),
                    entry.getValue());
            options.add(option);
            if (log.isTraceEnabled())
            {
                log.trace("Setting option " + entry.getKey()
                + " to value " + entry.getValue());
            }
        }
        return options;
    }
}
