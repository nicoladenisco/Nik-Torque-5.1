package org.apache.torque.generator.outlet.copy;

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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.outlet.OutletImpl;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * A Outlet which copies a given resource unchanged.
 */
public class CopyOutlet extends OutletImpl
{
    /** The log. */
    private static Log log = LogFactory.getLog(CopyOutlet.class);

    /**
     * The path to the resource to copy.
     * Relative paths are relative to the resources directory.
     */
    private final String path;

    /**
     * The configuration provider to use.
     */
    private final ConfigurationProvider configurationProvider;

    /**
     * Constructs a new CopyOutlet.
     *
     * @param name the name of this outlet, not null.
     * @param configurationProvider the provider for reading the resources,
     *        not null.
     * @param path the path to the resource, not null.
     *        May contain tokens of the form ${....}, these are parsed.
     *
     * @throws NullPointerException if name, path or configurationProvider
     *         are null.
     * @throws ConfigurationException if an error occurs while reading the
     *         template.
     */
    public CopyOutlet(
            QualifiedName name,
            ConfigurationProvider configurationProvider,
            String path)
                    throws ConfigurationException
    {
        super(name);
        if (path == null)
        {
            throw new NullPointerException("path is null");
        }
        if (configurationProvider == null)
        {
            throw new NullPointerException("configurationProvider is null");
        }
        this.path = path;
        this.configurationProvider = configurationProvider;
    }

    /**
     * Executes the generation process; the result is returned.
     *
     * @param controllerState the current controller state.
     *
     * @return the result of the generation, not null.
     *
     * @see org.apache.torque.generator.outlet.Outlet#execute(ControllerState)
     */
    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException

    {
        if (log.isDebugEnabled())
        {
            log.debug("Start executing CopyOutlet " + getName());
        }

        try
        {
            InputStream inputStream
            = configurationProvider.getResourceInputStream(path);
            OutletResult result = new OutletResult(
                    IOUtils.toByteArray(inputStream));
            return result;
        }
        catch (IOException e)
        {
            throw new GeneratorException("CopyOutlet with name "
                    + getName()
                    + ": cannot read Resource "
                    + path,
                    e);
        }
        finally
        {
            if (log.isDebugEnabled())
            {
                log.debug("End executing CopyOutlet " + getName());
            }
        }
    }

}
