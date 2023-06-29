package org.apache.torque.generator.configuration.mergepoint;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.control.action.MergepointAction;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX Handler which handles for the action element in mergepoints.
 *
 */
public abstract class ActionSaxHandler extends DefaultHandler
{
    /** The logger of the class. */
    private static Log log = LogFactory.getLog(ActionSaxHandler.class);

    /** The action which configured by this handler, not null. */
    private MergepointAction action;

    /** The description of the generation unit, not null. */
    private UnitDescriptor unitDescriptor;

    /** The configuration provider for accessing the configuration, not null. */
    private ConfigurationProvider configurationProvider;

    /**
     * Constructor.
     *
     * @param action paths of the underlying project, not null.
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     *
     * @throws NullPointerException if an argument is null.
     */
    public ActionSaxHandler(
            MergepointAction action,
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor)
    {
        if (action == null)
        {
            log.error("ActionSaxHandler: "
                    + " action is null");
            throw new NullPointerException("Action is null");
        }
        if (configurationProvider == null)
        {
            log.error("ActionSaxHandler: "
                    + " configurationProvider is null");
            throw new NullPointerException("configurationProvider is null");
        }
        if (unitDescriptor == null)
        {
            log.error("ActionSaxHandler: "
                    + " unitDescriptor is null");
            throw new NullPointerException("unitDescriptor is null");
        }
        this.action = action;
        this.configurationProvider = configurationProvider;
        this.unitDescriptor = unitDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(
            String uri,
            String localName,
            String rawName,
            Attributes attributes)
                    throws SAXException
    {
        throw new SAXException("unknown Element " + rawName);
    }

    /**
     * Returns the action which was configured by this handler.
     *
     * @return the action configured by this handler, not null.
     */
    public MergepointAction getAction()
    {
        return action;
    }

    /**
     * Returns the configuration provider used by this handler.
     *
     * @return the configuration provider, not null.
     */
    protected ConfigurationProvider getConfigurationProvider()
    {
        return configurationProvider;
    }

    /**
     * Returns the description of the generation unit.
     *
     * @return the description of the generation unit, not null.
     */
    protected UnitDescriptor getUnitDescriptor()
    {
        return unitDescriptor;
    }
}
