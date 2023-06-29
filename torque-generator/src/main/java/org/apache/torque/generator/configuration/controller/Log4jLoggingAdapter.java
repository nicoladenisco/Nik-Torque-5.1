package org.apache.torque.generator.configuration.controller;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Allows reading and setting the current loglevel using log4j.
 * @version $Id: Log4jLoggingAdapter.java 1839288 2018-08-27 09:48:33Z tv $
 *
 */
public class Log4jLoggingAdapter implements LoggingAdapter
{
    /** The logger. */
    private static Log log = LogFactory.getLog(Log4jLoggingAdapter.class);

    /** Map loglevel -> corresponding log4j level. */
    private final Map<Loglevel, Level> log4jLevels;

    public Log4jLoggingAdapter()
    {
        Map<Loglevel, Level> levels = new HashMap<>();
        levels.put(Loglevel.TRACE, Level.TRACE);
        levels.put(Loglevel.DEBUG, Level.DEBUG);
        levels.put(Loglevel.INFO, Level.INFO);
        levels.put(Loglevel.WARN, Level.WARN);
        levels.put(Loglevel.ERROR, Level.ERROR);
        log4jLevels = Collections.unmodifiableMap(levels);
    }

    /**
     * Returns the current loglevel by reading the loglevel of the root logger.
     *
     * @return the current loglevel, or INFO if the current loglevel cannot
     *         be determined.
     */
    @Override
    public Loglevel getCurrentLoglevel()
    {
        Level level = Logger.getRootLogger().getLevel();
        for (Map.Entry<Loglevel, Level> loglevel : log4jLevels.entrySet())
        {
            if (loglevel.getValue().equals(level))
            {
                return loglevel.getKey();
            }
        }
        return Loglevel.INFO;
    }

    /**
     * Sets the loglevel to the given loglevel
     * by changing the level of the log4j root logger.
     *
     *  @param loglevel the loglevel to set, not null.
     */
    @Override
    public void setLoglevel(final Loglevel loglevel)
    {
        if (loglevel == null)
        {
            return;
        }
        Level log4jLevel = log4jLevels.get(loglevel);
        if (Logger.getRootLogger().getLevel() != log4jLevels.get(loglevel))
        {
            log.info("apply() : Setting loglevel to " + loglevel);
            Logger.getRootLogger().setLevel(log4jLevel);
        }
    }
}
