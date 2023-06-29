package org.apache.torque.generator.configuration.source;

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

import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.ELEMENTS_ATTRIBUTE;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.SKIP_DECIDER_ARRTIBUTE;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.SOURCE_TAG;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.TRANSFORMER_TAG;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.source.SourceProcessConfiguration;
import org.apache.torque.generator.source.SourceProvider;
import org.apache.torque.generator.source.SourceTransformerDefinition;
import org.apache.torque.generator.source.transform.SourceTransformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A base class for reading source definitions from the controller
 * configuration file.
 */
public abstract class SourceSaxHandler extends DefaultHandler
{
    /** The access object for the configuration files, not null. */
    private final ConfigurationProvider configurationProvider;

    /** The description of the generation unit, not null. */
    private final UnitDescriptor unitDescriptor;

    /** The known configuration handlers. */
    private final ConfigurationHandlers configurationHandlers;

    /**
     * How a source should be processed between obtaining the original source
     * and feeding it to the outlets.
     */
    private SourceProcessConfiguration sourceProcessConfiguration;

    /**
     * The transformer definitions in the source element.
     */
    private List<SourceTransformerDefinition> transformerDefinitions
        = new ArrayList<>();

    /** The handler which handles transformer elements. */
    private ConfigurableClassSaxHandler<SourceTransformer>
    transformerSaxHandler;

    /** Whether this handler has completed its task. */
    private boolean finished = false;

    /**
     * Constructor.
     *
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers All known configuration handlers, not null.
     *
     * @throws NullPointerException if an argument is null.
     */
    public SourceSaxHandler(
            final ConfigurationProvider configurationProvider,
            final UnitDescriptor unitDescriptor,
            final ConfigurationHandlers configurationHandlers)
    {
        if (configurationProvider == null)
        {
            throw new NullPointerException(
                    "configurationProvider must not be null");
        }
        if (unitDescriptor == null)
        {
            throw new NullPointerException(
                    "unitDescriptor must not be null");
        }
        if (configurationHandlers == null)
        {
            throw new NullPointerException(
                    "configurationHandlers must not be null");
        }
        this.configurationProvider = configurationProvider;
        this.unitDescriptor = unitDescriptor;
        this.configurationHandlers = configurationHandlers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(final String uri, final String localName, final String rawName,
            final Attributes attributes)
                    throws SAXException
    {
        if (transformerSaxHandler != null)
        {
            transformerSaxHandler.startElement(
                    uri, localName, rawName, attributes);
        }
        else if (TRANSFORMER_TAG.equals(rawName))
        {
            transformerSaxHandler
                = new ConfigurableClassSaxHandler<>(
                    configurationProvider,
                    unitDescriptor,
                    TRANSFORMER_TAG);
            transformerSaxHandler.startElement(
                    uri, localName, rawName, attributes);
        }
        else if (rawName.equals(SOURCE_TAG))
        {
            sourceProcessConfiguration = new SourceProcessConfiguration();
            sourceProcessConfiguration.setStartElementsPath(
                    attributes.getValue(ELEMENTS_ATTRIBUTE));
            try
            {
                sourceProcessConfiguration.setSkipDecider(
                        attributes.getValue(SKIP_DECIDER_ARRTIBUTE),
                        unitDescriptor);
            }
            catch (ConfigurationException e)
            {
                throw new SAXException("Could not create source: "
                        + e.getMessage(),
                        e);
            }
        }
        else
        {
            throw new SAXException("Unknown element " + rawName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(final String uri, final String localName, final String rawName)
            throws SAXException
    {
        if (transformerSaxHandler != null)
        {
            transformerSaxHandler.endElement(
                    uri,
                    localName,
                    rawName);
            if (transformerSaxHandler.isFinished())
            {
                transformerDefinitions.add(new SourceTransformerDefinition(
                        transformerSaxHandler.getConfiguredClass()));
                transformerSaxHandler = null;
            }
        }
        else if (rawName.equals(SOURCE_TAG))
        {
            sourceProcessConfiguration.setSourceTransformerDefinitions(
                    transformerDefinitions);
            transformerDefinitions
                = new ArrayList<>();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(final char[] ch, final int start, final int length)
            throws SAXException
    {
        if (transformerSaxHandler != null)
        {
            transformerSaxHandler.characters(ch, start, length);
        }
    }

    /**
     * Returns the configurationProvider to access the configuration.
     *
     * @return the configurationProvider to access the configuration, not null.
     */
    public ConfigurationProvider getConfigurationProvider()
    {
        return configurationProvider;
    }

    /**
     * Returns the known configuration handlers.
     *
     * @return the configuration handlers, not null.
     */
    public ConfigurationHandlers getConfigurationHandlers()
    {
        return configurationHandlers;
    }

    /**
     * Returns the description of the generation unit.
     *
     * @return the description of the generation unit, not null.
     */
    public UnitDescriptor getUnitDescriptor()
    {
        return unitDescriptor;
    }

    /**
     * Returns whether the matching snippet was completely parsed.
     *
     * @return true if the matching snippet was completely parsed,
     *         false otherwise.
     */
    public boolean isFinished()
    {
        return finished;
    }

    /**
     * Marks that the matching snippet was completely parsed.
     */
    protected void finished()
    {
        finished = true;
    }

    /**
     * Returns the information how to read the sources.
     *
     * @return the source Provider, not null if the
     *         source snippet was processed.
     */
    public abstract SourceProvider getSourceProvider();

    /**
     * Returns the information how to pre-process the sources before generating.
     *
     * @return the sourceProcessConfiguration, not null if the
     *         source snippet was processed.
     */
    public SourceProcessConfiguration getSourceProcessConfiguration()
    {
        return sourceProcessConfiguration;
    }
}
