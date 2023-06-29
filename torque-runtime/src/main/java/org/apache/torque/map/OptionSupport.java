package org.apache.torque.map;

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

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OptionSupport provides the basic methods for the management of options
 * within the database schema model.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: ColumnMap.java 1484252 2013-05-19 09:58:00Z tfischer $
 */
public abstract class OptionSupport implements Serializable
{
    /**
     * Serial version.
     */
    private static final long serialVersionUID = 1L;

    /** Associated options. */
    private final Map<String, String> optionsMap = new ConcurrentHashMap<>();

    /**
     * Returns an unmodifiable map of all options.
     *
     * @return A map containing all options, not null.
     */
    public Map<String, String> getOptions()
    {
        return Collections.unmodifiableMap(optionsMap);
    }

    /**
     * Sets an option.
     *
     * @param key the key of the option
     * @param value the value of the option.
     */
    public void setOption(final String key, final String value)
    {
        optionsMap.put(key, value);
    }

    /**
     * Returns the value of an option.
     *
     * @param key the key of the option.
     *
     * @return the value of the option, or null if not set.
     */
    public String getOption(final String key)
    {
        return optionsMap.get(key);
    }

    /**
     * Clears all options.
     */
    public void clearOptions()
    {
        optionsMap.clear();
    }
}
