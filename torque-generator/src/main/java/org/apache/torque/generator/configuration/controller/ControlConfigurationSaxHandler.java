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

import static org.apache.torque.generator.configuration.controller.ControlConfigurationTags.CONTROL_LOGLEVEL_ATTRIBUTE;
import static org.apache.torque.generator.configuration.controller.ControlConfigurationTags.CONTROL_TAG;
import static org.apache.torque.generator.configuration.controller.OutputConfigurationTags.OUTPUT_TAG;
import static org.apache.torque.generator.configuration.option.OptionTags.OPTIONS_TAG;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.ENTITY_REFERENCE;

import java.io.IOException;

import org.apache.torque.generator.configuration.ConfigurationEntityResolver;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.SaxHelper;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.XMLConstants;
import org.apache.torque.generator.configuration.mergepoint.OptionsSaxHandlerFactories;
import org.apache.torque.generator.configuration.option.OptionsConfiguration;
import org.apache.torque.generator.configuration.option.OptionsSaxHandler;
import org.apache.torque.generator.configuration.option.OptionsSaxHandlerFactory;
import org.apache.torque.generator.configuration.source.EntityReferenceSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads the controller configuration from the controller configuration file.
 */
public class ControlConfigurationSaxHandler extends DefaultHandler
{
    /** The Controller configuration which is configured by this Handler. */
    private ControlConfiguration controllerConfiguration;

    /** The known configuration handlers. */
    private ConfigurationHandlers configurationHandlers;

    /**
     * The SAX handler which handles the output declarations, or null
     * if output declarations need not currently be handled.
     */
    private OutputSaxHandler outputSaxHandler;

    /**
     * The SAX handler which handles the entity reference declarations, or null
     * if entity reference declarations need not currently be handled.
     */
    private EntityReferenceSaxHandler entityReferenceSaxHandler;

    /**
     * The SAX handler which handles the options declarations, or null
     * if options declarations need not currently be handled.
     */
    private OptionsSaxHandler optionsSaxHandler;

    /**
     * Object for accessing the configuration.
     */
    private ConfigurationProvider configurationProvider;

    /**
     * The description of the generation unit, not null.
     */
    private UnitDescriptor unitDescriptor;

    /**
     * Constructor.
     *
     * @param controllerConfiguration the configuration object to fill, no null.
     * @param configurationProvider the Object for accessing the configuration,
     *        not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers the available configuration handlers,
     *        not null.
     *
     * @throws NullPointerException if an argument is null.
     */
    public ControlConfigurationSaxHandler(
            ControlConfiguration controllerConfiguration,
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor,
            ConfigurationHandlers configurationHandlers)
    {
        if (controllerConfiguration == null)
        {
            throw new NullPointerException(
                    "controllerConfiguration must not be null");
        }
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
        this.controllerConfiguration = controllerConfiguration;
        this.configurationHandlers = configurationHandlers;
        this.unitDescriptor = unitDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes)
                    throws SAXException
    {
        String unqualifiedName = SaxHelper.getUnqualifiedName(localName, qName);
        if (outputSaxHandler != null)
        {
            outputSaxHandler.startElement(
                    uri,
                    localName,
                    qName,
                    attributes);
        }
        else if (optionsSaxHandler != null)
        {
            optionsSaxHandler.startElement(
                    uri,
                    localName,
                    qName,
                    attributes);
        }
        else if (entityReferenceSaxHandler != null)
        {
            entityReferenceSaxHandler.startElement(
                    uri,
                    localName,
                    qName,
                    attributes);
        }
        else if (CONTROL_TAG.equals(unqualifiedName))
        {
            String loglevel = attributes.getValue(CONTROL_LOGLEVEL_ATTRIBUTE);
            if (loglevel != null)
            {
                Loglevel level = Loglevel.getByKey(loglevel);
                controllerConfiguration.setLoglevel(level);
            }
        }
        else if (OPTIONS_TAG.equals(unqualifiedName))
        {
            String type = attributes.getValue(
                    XMLConstants.XSI_NAMESPACE,
                    XMLConstants.XSI_TYPE_ATTRBUTE_NAME);
            if (type == null)
            {
                throw new SAXException("The tag " + OPTIONS_TAG
                        + " requires the attribute "
                        + XMLConstants.XSI_NAMESPACE
                        + ":"
                        + XMLConstants.XSI_TYPE_ATTRBUTE_NAME);
            }

            OptionsSaxHandlerFactories optionsSaxHandlerFactories
            = configurationHandlers.getOptionsSaxHandlerFactories();
            OptionsSaxHandlerFactory optionsSaxHandlerFactory
            = optionsSaxHandlerFactories.getOptionsSaxHandlerFactory(
                    type);
            if (optionsSaxHandlerFactory == null)
            {
                throw new SAXException("No handler found for the action "
                        + "of type "
                        + type);
            }
            optionsSaxHandler = optionsSaxHandlerFactory.getOptionsSaxHandler();
            optionsSaxHandler.startElement(uri, localName, qName, attributes);
        }
        else if (OUTPUT_TAG.equals(unqualifiedName))
        {
            outputSaxHandler = new OutputSaxHandler(
                    configurationProvider,
                    unitDescriptor,
                    configurationHandlers);
            outputSaxHandler.startElement(
                    uri,
                    localName,
                    qName,
                    attributes);
        }
        else if (ENTITY_REFERENCE.equals(unqualifiedName))
        {
            entityReferenceSaxHandler = new EntityReferenceSaxHandler(
                    configurationProvider,
                    unitDescriptor);
            entityReferenceSaxHandler.startElement(
                    uri,
                    localName,
                    qName,
                    attributes);
        }
        else
        {
            throw new SAXException("Unknown element " + unqualifiedName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String rawName)
            throws SAXException
    {
        if (optionsSaxHandler != null)
        {
            optionsSaxHandler.endElement(uri, localName, rawName);
            if (optionsSaxHandler.isFinished())
            {
                OptionsConfiguration optionsConfiguration
                = optionsSaxHandler.getOptionsConfiguration();
                controllerConfiguration.addOptionsConfiguration(
                        optionsConfiguration);
                optionsSaxHandler = null;
            }
        }
        else if (OUTPUT_TAG.equals(rawName))
        {
            outputSaxHandler.endElement(uri, localName, rawName);
            Output outputFile = outputSaxHandler.getOutputFile();
            try
            {
                controllerConfiguration.addOutput(outputFile);
            }
            catch (ConfigurationException e)
            {
                throw new SAXException("Could not add output "
                        + outputFile.getName(), e);
            }
            outputSaxHandler = null;
        }
        else if (outputSaxHandler != null)
        {
            outputSaxHandler.endElement(uri, localName, rawName);
        }
        else if (entityReferenceSaxHandler != null)
        {
            entityReferenceSaxHandler.endElement(uri, localName, rawName);
            if (entityReferenceSaxHandler.isFinished())
            {
                try
                {
                    controllerConfiguration.getEntityReferences()
                    .addEntityReference(
                            entityReferenceSaxHandler.getSystemId(),
                            entityReferenceSaxHandler.readResource());
                }
                catch (ConfigurationException e)
                {
                    throw new SAXException(
                            "Could not read entity reference "
                                    + entityReferenceSaxHandler.getSystemId(),
                                    e);
                }
                entityReferenceSaxHandler = null;
            }
        }
    }

    /**
     * Receive notification of character data inside an element.
     *
     * @param ch The characters.
     * @param start The start position in the character array.
     * @param length The number of characters to use from the
     *               character array.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#characters
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        if (outputSaxHandler != null)
        {
            outputSaxHandler.characters(ch, start, length);
        }
    }

    /**
     * EntityResolver implementation. Called by the XML parser
     *
     * @param publicId The public identifier of the external entity.
     * @param systemId The system identifier of the external entity.
     *
     * @return an InputSource for the entity, or null if the URI is not known.
     *
     * @see ConfigurationEntityResolver#resolveEntity(String, String)
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException
    {
        return new ConfigurationEntityResolver().resolveEntity(
                publicId, systemId);
    }

    @Override
    public void error(SAXParseException exception) throws SAXParseException
    {
        throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception)
            throws SAXParseException
    {
        throw exception;
    }

    @Override
    public void warning(SAXParseException exception) throws SAXParseException
    {
        throw exception;
    }
}
