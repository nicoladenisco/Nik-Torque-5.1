package org.apache.torque.generator.source;

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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.control.ControllerState;

/**
 * The input for a generation process. It can contain several sources.
 * In order to use the sources, the init() method must be called. After
 * this method was called, no more setters may be called.
 */
public abstract class SourceProvider implements Iterator<Source>
{
    /** The class log. */
    private static Log log = LogFactory.getLog(SourceProvider.class);

    /**
     * Whether the init method was already called.
     */
    private boolean initialized;

    /**
     * Initializes the source provider. Must be called before
     * <code>hasNext()</code> or <code>next()</code> is invoked.
     *
     * @param configurationHandlers the configuration handlers, not null.
     * @param controllerState the current controller state, not null.
     *
     * @throws ConfigurationException if initializing fails.
     */
    public final synchronized void init(
            ConfigurationHandlers configurationHandlers,
            ControllerState controllerState)
                    throws ConfigurationException
    {
        if (initialized)
        {
            log.warn("init() called more than once, ignoring this call");
            return;
        }
        initInternal(configurationHandlers, controllerState);
        initialized = true;
    }

    /**
     * Resets the source provider. After this method is called,
     * <code>init()</code> must be called again.
     *
     * @param configurationHandlers the configuration handlers, not null.
     * @param controllerState the current controller state, not null.
     *
     * @throws ConfigurationException if resetting fails.
     */
    public final synchronized void reset(
            ConfigurationHandlers configurationHandlers,
            ControllerState controllerState)
                    throws ConfigurationException
    {
        if (!initialized)
        {
            log.warn("reset() called on uninitialized SourceProvider, "
                    + "ignoring this call");
            return;
        }
        resetInternal(configurationHandlers, controllerState);
        initialized = false;
    }

    /**
     * Initializes the sources provided by this SourceProvider.
     *
     * @param configurationHandlers the configuration handlers, not null.
     * @param controllerState the current controller state, not null.
     *
     * @throws ConfigurationException if initializing fails.
     */
    protected abstract void initInternal(
            ConfigurationHandlers configurationHandlers,
            ControllerState controllerState)
                    throws ConfigurationException;

    /**
     * Resets the sources provided by this SourceProvider.
     *
     * @param configurationHandlers the configuration handlers, not null.
     * @param controllerState the current controller state, not null.
     *
     * @throws ConfigurationException if resetting fails.
     */
    protected abstract void resetInternal(
            ConfigurationHandlers configurationHandlers,
            ControllerState controllerState)
                    throws ConfigurationException;

    /**
     * Returns whether <code>init()</code> was already called.
     *
     * @return true if init() was already called, false otherwise.
     */
    public boolean isInit()
    {
        return initialized;
    }

    /**
     * Returns a copy of this source provider in its initial state.
     * This means the
     * {@link #init(ConfigurationHandlers, ControllerState)}
     * method of the new source provider must be called before it can be used.
     *
     * @return the SourceProvider
     * @throws ConfigurationException if the new SourceProvider cannot
     *         be initialized.
     */
    public abstract SourceProvider copy() throws ConfigurationException;

    /**
     * Copies settings which are not set in this source provider from another
     * source provider. This only works if the type of the other source
     * provider is known to this source provider.
     * Only a subset of all properties are typically used for overwriting.
     * No Properties which are already set are overwritten.
     *
     * @param sourceProvider the source provoder to copy the settings from.
     */
    public abstract void copyNotSetSettingsFrom(SourceProvider sourceProvider);
}
