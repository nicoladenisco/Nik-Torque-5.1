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

import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.DRIVER_OPTION_ATTRIBUTE;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.PASSWORD_OPTION_ATTRIBUTE;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.SCHEMA_OPTION_ATTRIBUTE;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.SOURCE_TAG;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.URL_OPTION_ATTRIBUTE;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.USERNAME_OPTION_ATTRIBUTE;

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.source.SourceProvider;
import org.apache.torque.generator.source.jdbc.JdbcMetadataSourceProvider;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Reads jdbc metadata source definitions from the controller configuration
 * file.
 */
public class JdbcMetadataSourceSaxHandler extends SourceSaxHandler
{
    /**
     * The source provider which is configured by this SAX handler.
     */
    private SourceProvider sourceProvider;

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
    public JdbcMetadataSourceSaxHandler(
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor,
            ConfigurationHandlers configurationHandlers)
    {
        super(configurationProvider, unitDescriptor, configurationHandlers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String rawName,
            Attributes attributes)
                    throws SAXException
    {
        if (rawName.equals(SOURCE_TAG))
        {
            String urlOption
            = attributes.getValue(URL_OPTION_ATTRIBUTE);
            String driverOption
            = attributes.getValue(DRIVER_OPTION_ATTRIBUTE);
            String usernameOption
            = attributes.getValue(USERNAME_OPTION_ATTRIBUTE);
            String passwordOption
            = attributes.getValue(PASSWORD_OPTION_ATTRIBUTE);
            String schemaOption
            = attributes.getValue(SCHEMA_OPTION_ATTRIBUTE);
            try
            {
                sourceProvider = new JdbcMetadataSourceProvider(
                        urlOption,
                        driverOption,
                        usernameOption,
                        passwordOption,
                        schemaOption);
            }
            catch (ConfigurationException e)
            {
                throw new SAXException(
                        "Could not parse Source Tag: " + e.getMessage(),
                        e);
            }
        }
        super.startElement(uri, localName, rawName, attributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String rawName)
            throws SAXException
    {
        super.endElement(uri, localName, rawName);
        if (rawName.equals(SOURCE_TAG))
        {
            finished();
        }
    }

    /**
     * Returns the configuration filled with the contents of the parsed snippet.
     *
     * @return the configuration which was filled, not null if a
     *         matching snippet was processed.
     */
    @Override
    public SourceProvider getSourceProvider()
    {
        return sourceProvider;
    }
}
