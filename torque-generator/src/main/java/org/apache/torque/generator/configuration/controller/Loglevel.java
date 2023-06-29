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


/**
 * The possible log levels.
 * This class uses by default log4j internally,
 * but the logging framework can be exchanged using the
 * setLoggingAdapter method.
 * The public API of this class is agnostic of the logging framework.
 */
public enum Loglevel
{
    /** Loglevel trace. */
    TRACE("trace"),
    /** Loglevel debug. */
    DEBUG("debug"),
    /** Loglevel info. */
    INFO("info"),
    /** Loglevel warn. */
    WARN("warn"),
    /** Loglevel error. */
    ERROR("error");

    /** The key of the loglevel. */
    private String key;

    /** The default logging adapter to use. */
    private static LoggingAdapter loggingAdapter = new Log4j2LoggingAdapter();

    /**
     * Constructor.
     *
     * @param key the key, not null.
     */
    private Loglevel(String key)
    {
        this.key = key;
    }

    /**
     * Returns the key of the Loglevel.
     *
     * @return the key of the Loglevel, not null.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Applies the log level.
     */
    public void apply()
    {
        loggingAdapter.setLoglevel(this);
    }

    /**
     * Returns the Loglevel for a given key.
     *
     * @param key the key to look for.
     *
     * @return the corresponding Loglevel, not null.
     *
     * @throws IllegalArgumentException if no Loglevel can be found
     *         for the key.
     */
    public static Loglevel getByKey(String key)
    {
        for (Loglevel loglevel : values())
        {
            if (loglevel.getKey().equals(key))
            {
                return loglevel;
            }
        }
        throw new IllegalArgumentException(
                "Key " + key + " does not exist");
    }

    /**
     * Returns the current loglevel.
     *
     * @return the current loglevel, or INFO if the current loglevel cannot
     *         be determined.
     */
    public static Loglevel getCurrentLoglevel()
    {
        return loggingAdapter.getCurrentLoglevel();
    }

    /**
     * Returns the currently used logging adapter.
     *
     * @return the current logging adapter, not null.
     */
    public static LoggingAdapter getLoggingAdapter()
    {
        return loggingAdapter;
    }

    /**
     * Sets the logging adapter.
     *
     * @param loggingAdapter the logging adapter
     * @throws NullPointerException if loggingAdapter is null.
     */
    public static void setLoggingAdapter(LoggingAdapter loggingAdapter)
    {
        if (loggingAdapter == null)
        {
            throw new NullPointerException("loggingAdapter must not be null");
        }
        Loglevel.loggingAdapter = loggingAdapter;
    }
}
