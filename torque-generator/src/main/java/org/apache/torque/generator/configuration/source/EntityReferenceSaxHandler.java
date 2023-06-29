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

import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.ENTITY_REFERENCE;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.RESOURCE_ATTRIBUTE;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.SYSTEM_ID_ATTRIBUTE;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads an entity reference from the controller configuration file.
 */
public class EntityReferenceSaxHandler extends DefaultHandler
{
    /** The systemId to resolve. */
    private String systemId;

    /** The resource where to find the resolved resource. */
    private String resource;

    /** The configurationProvider. */
    private ConfigurationProvider configurationProvider;

    /**
     * Constructor.
     *
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     *
     * @throws NullPointerException if an argument is null.
     */
    public EntityReferenceSaxHandler(
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor)
    {
        if (configurationProvider == null)
        {
            throw new NullPointerException("configurationProvider must not be null");
        }
        if (unitDescriptor == null)
        {
            throw new NullPointerException("unitDescriptor must not be null");
        }
        this.configurationProvider = configurationProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String rawName,
            Attributes attributes)
                    throws SAXException
    {
        if (rawName.equals(ENTITY_REFERENCE))
        {
            resource = attributes.getValue(
                    RESOURCE_ATTRIBUTE);
            if (resource == null)
            {
                throw new SAXException(
                        "The attribute " + RESOURCE_ATTRIBUTE
                        + " must not be null for the element "
                        + ENTITY_REFERENCE);
            }
            systemId = attributes.getValue(
                    SYSTEM_ID_ATTRIBUTE);
            if (systemId == null)
            {
                throw new SAXException(
                        "The attribute " + SYSTEM_ID_ATTRIBUTE
                        + " must not be null for the element "
                        + ENTITY_REFERENCE);
            }
        }
        else
        {
            throw new SAXException("Unknown element " + rawName);
        }
    }

    /**
     * Returns the parsed resource path.
     *
     * @return the the parsed resource path, not null if a
     *         matching snippet was processed.
     */
    public String getResource()
    {
        return resource;
    }

    public String getSystemId()
    {
        return systemId;
    }

    /**
     * Returns whether this handler has finished parsing.
     *
     * @return true if this handler is finished, false otherwise.
     */
    public boolean isFinished()
    {
        return systemId != null;
    }

    /**
     * Reads the resource defined in the parsed XML .
     *
     * @return the content of the resource, not null.
     *
     * @throws ConfigurationException if an error occurs
     *         while reading the resource.
     */
    public byte[] readResource()
            throws ConfigurationException
    {
        InputStream inputStream
        = configurationProvider.getResourceInputStream(resource);
        byte[] content;
        try
        {
            content = IOUtils.toByteArray(inputStream);
        }
        catch (IOException e)
        {
            throw new ConfigurationException(
                    "Could not read Stream from resource path " + resource,
                    e);
        }

        return content;
    }
}
