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

import static org.apache.torque.generator.configuration.controller.OutputConfigurationTags.ENCODING_ATTRIBUTE;
import static org.apache.torque.generator.configuration.controller.OutputConfigurationTags.EXISTING_TARGET_STRATEGY_ATTRIBUTE;
import static org.apache.torque.generator.configuration.controller.OutputConfigurationTags.FILENAME_GENERATOR_TAG;
import static org.apache.torque.generator.configuration.controller.OutputConfigurationTags.FILE_ATTRIBUTE;
import static org.apache.torque.generator.configuration.controller.OutputConfigurationTags.NAME_ATTRIBUTE;
import static org.apache.torque.generator.configuration.controller.OutputConfigurationTags.OUTLET_TAG;
import static org.apache.torque.generator.configuration.controller.OutputConfigurationTags.OUTPUT_DIR_KEY_ATTRIBUTE;
import static org.apache.torque.generator.configuration.controller.OutputConfigurationTags.OUTPUT_TAG;
import static org.apache.torque.generator.configuration.controller.OutputConfigurationTags.TYPE_ATTRIBUTE;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.POSTPROCESSOR_TAG;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.SOURCE_TAG;

import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.XMLConstants;
import org.apache.torque.generator.configuration.outlet.OutletConfigurationSaxHandler;
import org.apache.torque.generator.configuration.outlet.OutletSaxHandler;
import org.apache.torque.generator.configuration.source.ConfigurableClassSaxHandler;
import org.apache.torque.generator.configuration.source.SourceSaxHandler;
import org.apache.torque.generator.configuration.source.SourceSaxHandlerFactories;
import org.apache.torque.generator.configuration.source.SourceSaxHandlerFactory;
import org.apache.torque.generator.control.outputtype.UnknownOutputType;
import org.apache.torque.generator.processor.string.StringProcessor;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.source.PostprocessorDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads an output declaration from the controller configuration file.
 */
public class OutputSaxHandler extends DefaultHandler
{
    /** The qualified name for the filename outlet. */
    private static final QualifiedName FILENAME_OUTLET_NAME
        = new QualifiedName(
            "org.apache.torque.generator.configuration.filenameOutlet");

    /** The access object for the configuration files, not null. */
    private final ConfigurationProvider configurationProvider;

    /** The description of the generation unit, not null. */
    private final UnitDescriptor unitDescriptor;

    /** All known configuration handlers. */
    private final ConfigurationHandlers configurationHandlers;

    /** The output declaration which is currently parsed. */
    private Output output;

    /**
     * The SAX handler which handles the reference to the content outlet,
     * or null if no content outlet is currently processed.
     */
    private OutletReferenceSaxHandler contentOutletSaxHandler;

    /**
     * The SAX handler which handles source tags, or null if source tags
     * need not be handled in the current context.
     */
    private SourceSaxHandler sourceSaxHandler;

    /** The handler which handles postprocessor elements. */
    private ConfigurableClassSaxHandler<StringProcessor>
    postprocessorSaxHandler;

    /**
     * The SAX handler which handles the filename outlet configuration,
     * or null if filename outlet tags need not be handled
     * in the current context.
     */
    private OutletSaxHandler filenameOutletSaxHandler;

    /**
     * Constructor.
     *
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers handlers for reading the configuration.
     *
     * @throws NullPointerException if an argument is null.
     */
    public OutputSaxHandler(
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
        this.configurationHandlers = configurationHandlers;
        this.unitDescriptor = unitDescriptor;
    }

    /**
     * Returns the Configuration filled with the contents of the parsed snippet.
     *
     * @return the configuration representing the parsed snippet.
     *         Not null if the mathcing xml snippet was parsed.
     */
    public Output getOutputFile()
    {
        return output;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(
            final String uri,
            final String localName,
            final String rawName,
            final Attributes attributes)
                    throws SAXException
    {
        if (contentOutletSaxHandler != null)
        {
            contentOutletSaxHandler.startElement(
                    uri,
                    localName,
                    rawName,
                    attributes);
        }
        else if (filenameOutletSaxHandler != null)
        {
            filenameOutletSaxHandler.startElement(
                    uri,
                    localName,
                    rawName,
                    attributes);
        }
        else if (sourceSaxHandler != null)
        {
            sourceSaxHandler.startElement(
                    uri,
                    localName,
                    rawName,
                    attributes);
        }
        else if (postprocessorSaxHandler != null)
        {
            postprocessorSaxHandler.startElement(
                    uri, localName, rawName, attributes);
        }
        else if (OUTPUT_TAG.equals(rawName))
        {
            String name = attributes.getValue(NAME_ATTRIBUTE);
            if (name == null)
            {
                throw new SAXException("The attribute "
                        + NAME_ATTRIBUTE
                        + " must be set for the tag "
                        + OUTPUT_TAG);
            }
            this.output = new Output(new QualifiedName(name));

            if (attributes.getValue(FILE_ATTRIBUTE) != null)
            {
                output.setFilename(attributes.getValue(FILE_ATTRIBUTE));
            }
            if (attributes.getValue(EXISTING_TARGET_STRATEGY_ATTRIBUTE) != null)
            {
                output.setExistingTargetStrategy(
                        attributes.getValue(EXISTING_TARGET_STRATEGY_ATTRIBUTE));
            }
            if (attributes.getValue(OUTPUT_DIR_KEY_ATTRIBUTE) != null)
            {
                output.setOutputDirKey(
                        attributes.getValue(OUTPUT_DIR_KEY_ATTRIBUTE));
            }
            if (attributes.getValue(ENCODING_ATTRIBUTE) != null)
            {
                output.setEncoding(attributes.getValue(ENCODING_ATTRIBUTE));
            }
            if (attributes.getValue(TYPE_ATTRIBUTE) != null)
            {
                output.setType(attributes.getValue(TYPE_ATTRIBUTE));
            }
            else
            {
                output.setType(UnknownOutputType.KEY);
            }
        }
        else if (SOURCE_TAG.equals(rawName))
        {
            String type = attributes.getValue(
                    XMLConstants.XSI_NAMESPACE,
                    XMLConstants.XSI_TYPE_ATTRBUTE_NAME);
            SourceSaxHandlerFactories sourceSaxHandlerFactories
            = configurationHandlers.getSourceSaxHandlerFactories();
            SourceSaxHandlerFactory sourceSaxHandlerFactory
            = sourceSaxHandlerFactories.getSourceSaxHandlerFactory(type);
            if (sourceSaxHandlerFactory == null)
            {
                throw new SAXException("Unknown source type "
                        + type
                        + ". Known source types are "
                        + sourceSaxHandlerFactories.getSourceTypes());
            }
            sourceSaxHandler = sourceSaxHandlerFactory.getSourceSaxHandler(
                    configurationProvider,
                    unitDescriptor,
                    configurationHandlers);
            sourceSaxHandler.startElement(uri, localName, rawName, attributes);
        }
        else if (OUTLET_TAG.equals(rawName))
        {
            contentOutletSaxHandler
                = new OutletReferenceSaxHandler();
            contentOutletSaxHandler.startElement(
                    uri,
                    localName,
                    rawName,
                    attributes);
        }
        else if (POSTPROCESSOR_TAG.equals(rawName))
        {
            postprocessorSaxHandler
                = new  ConfigurableClassSaxHandler<>(
                    configurationProvider,
                    unitDescriptor,
                    POSTPROCESSOR_TAG);
            postprocessorSaxHandler.startElement(
                    uri, localName, rawName, attributes);
        }
        else if (FILENAME_GENERATOR_TAG.equals(rawName))
        {
            OutletConfigurationSaxHandler outletConfigurationSaxHandler
                = new OutletConfigurationSaxHandler(
                    configurationProvider,
                    unitDescriptor,
                    configurationHandlers);
            String outletType
            = OutletConfigurationSaxHandler.getOutletType(
                    attributes);
            filenameOutletSaxHandler
            = outletConfigurationSaxHandler.getOutletHandler(
                    FILENAME_OUTLET_NAME,
                    outletType);
            filenameOutletSaxHandler.startElement(
                    uri,
                    localName,
                    rawName,
                    attributes);
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
        if (contentOutletSaxHandler != null)
        {
            contentOutletSaxHandler.endElement(
                    uri,
                    localName,
                    rawName);
            if (contentOutletSaxHandler.isFinished())
            {
                output.setContentOutlet(
                        contentOutletSaxHandler
                        .getOutletReference());
                contentOutletSaxHandler = null;
            }
        }
        else if (sourceSaxHandler != null)
        {
            sourceSaxHandler.endElement(uri, localName, rawName);
            if (sourceSaxHandler.isFinished())
            {
                output.setSourceProvider(
                        sourceSaxHandler.getSourceProvider());
                output.setSourceProcessConfiguration(
                        sourceSaxHandler.getSourceProcessConfiguration());
                sourceSaxHandler = null;
            }
        }
        else if (postprocessorSaxHandler != null)
        {
            postprocessorSaxHandler.endElement(
                    uri,
                    localName,
                    rawName);
            if (postprocessorSaxHandler.isFinished())
            {
                output.getPostprocessorDefinitions().add(
                        new PostprocessorDefinition(
                                postprocessorSaxHandler.getConfiguredClass()));
                postprocessorSaxHandler = null;
            }
        }
        else if (filenameOutletSaxHandler != null)
        {
            filenameOutletSaxHandler.endElement(uri, localName, rawName);
            if (filenameOutletSaxHandler.isFinished())
            {
                output.setFilenameOutlet(
                        filenameOutletSaxHandler
                        .getOutlet());
                filenameOutletSaxHandler = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(final char[] ch, final int start, final int length)
            throws SAXException
    {
        if (contentOutletSaxHandler != null)
        {
            contentOutletSaxHandler.characters(
                    ch, start, length);
        }
        else if (sourceSaxHandler != null)
        {
            sourceSaxHandler.characters(ch, start, length);
        }
        else if (filenameOutletSaxHandler != null)
        {
            filenameOutletSaxHandler.characters(ch, start, length);
        }
        else if (postprocessorSaxHandler != null)
        {
            postprocessorSaxHandler.characters(ch, start, length);
        }
    }
}
