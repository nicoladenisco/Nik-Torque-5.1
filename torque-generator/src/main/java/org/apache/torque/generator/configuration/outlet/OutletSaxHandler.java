package org.apache.torque.generator.configuration.outlet;

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

import static org.apache.torque.generator.configuration.mergepoint.MergepointConfigurationTags.MERGEPOINT_TAG;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.INPUT_CLASS_ATTRIBUTE;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.INPUT_ELEMENT_NAME_ATTRIBUTE;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.INPUT_TAG;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.mergepoint.MergepointSaxHandler;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.qname.QualifiedName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles a outlet declaration within the outlet configuration.
 * Base class for more specific handlers.
 */
public abstract class OutletSaxHandler extends DefaultHandler
{
    /** the logger for the class. */
    private static Log log = LogFactory.getLog(OutletSaxHandler.class);

    /**
     * the name for the outlet which configuration will be read in
     *  by the generated SaxHandlerFactory, or null if the name
     *  of the outlet should be determined from the parsed XML.
     */
    private QualifiedName outletName;

    /** the outlet to be configured. */
    private Outlet outlet;

    /** The access object for the configuration files, not null. */
    private ConfigurationProvider configurationProvider;

    /** The description of the generation unit, not null. */
    private UnitDescriptor unitDescriptor;

    /** The available configuration handlers. */
    private ConfigurationHandlers configurationHandlers;

    /**
     * The SAX handler for processing mergepoint declarations.
     * Not null only if a mergepoint declaration is currently processed.
     */
    private MergepointSaxHandler mergepointSaxHandler;

    /** whether we are past the end of the outlet declaration. */
    private boolean finished = false;

    /**
     * The raw name of the XML element which started the outlet definition.
     */
    private String startTagRawName = null;

    /**
     * Creates a OutletSaxHandler.
     *
     * @param outletName the name for the outlet which configuration
     *        will be read in by the generated SaxHandlerFactory,
     *        or null if the name of the outlet should be determined from
     *        the parsed XML.
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers the available configuration handlers,
     *        not null.
     *
     * @throws NullPointerException if an argument is null.
     */
    public OutletSaxHandler(
            QualifiedName outletName,
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor,
            ConfigurationHandlers configurationHandlers)
    {
        if (configurationProvider == null)
        {
            log.error("OutletSaxHandler: "
                    + " configurationProvider is null");
            throw new NullPointerException("configurationProvider is null");
        }
        if (unitDescriptor == null)
        {
            log.error("OutletSaxHandler: "
                    + " unitDescriptor is null");
            throw new NullPointerException("unitDescriptor is null");
        }
        if (configurationHandlers == null)
        {
            log.error("OutletSaxHandler: "
                    + " configurationHandlers is null");
            throw new NullPointerException("configurationHandlers is null");
        }
        this.outletName = outletName;
        this.configurationProvider = configurationProvider;
        this.unitDescriptor = unitDescriptor;
        this.configurationHandlers = configurationHandlers;
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
        if (startTagRawName == null)
        {
            startTagRawName = rawName;
            outlet = createOutlet(
                    outletName,
                    uri,
                    localName,
                    rawName,
                    attributes);
        }
        else if (INPUT_TAG.equals(rawName))
        {
            String element = attributes.getValue(INPUT_ELEMENT_NAME_ATTRIBUTE);
            String className = attributes.getValue(INPUT_CLASS_ATTRIBUTE);
            if (element == null && className == null
                    || element != null && className != null)
            {
                throw new SAXException("Either the attribute "
                        + INPUT_ELEMENT_NAME_ATTRIBUTE
                        + " or the attribute "
                        + INPUT_CLASS_ATTRIBUTE
                        + " must be set for the tag "
                        + INPUT_TAG);
            }
            outlet.setInputElementName(element);
            outlet.setInputClass(className);
        }
        else if (MERGEPOINT_TAG.equals(rawName))
        {
            mergepointSaxHandler = new MergepointSaxHandler(
                    configurationProvider,
                    unitDescriptor,
                    configurationHandlers);
            mergepointSaxHandler.startElement(uri, localName, rawName, attributes);
        }
        else if (mergepointSaxHandler != null)
        {
            mergepointSaxHandler.startElement(uri, localName, rawName, attributes);
        }
        else
        {
            throw new SAXException("unknown Element " + rawName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String rawName)
            throws SAXException
    {
        if (mergepointSaxHandler != null)
        {
            mergepointSaxHandler.endElement(uri, localName, rawName);
            if (mergepointSaxHandler.isFinished())
            {
                try
                {
                    outlet.addMergepointMapping(
                            mergepointSaxHandler.getMergepointMapping());
                }
                catch (ConfigurationException e)
                {
                    throw new SAXException(
                            "Could not get mergepoint mapping from the "
                                    + "mergepoint Sax handler",
                                    e);
                }
                mergepointSaxHandler = null;
            }
        }
        else if (startTagRawName.equals(rawName))
        {
            finished = true;
        }
    }

    /**
     * @param outletName the name for the outlet which configuration
     *        will be read in by the generated SaxHandlerFactory,
     *        or null if the name of the outlet should be determined from
     *        the parsed xml.
     * @param uri - The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace processing is not
     *        being performed.
     * @param localName - The local name (without prefix), or
     *        the empty string if Namespace processing is not being performed.
     * @param rawName - The qualified name (with prefix), or the empty string if
     *        qualified names are not available.
     * @param attributes - The attributes attached to the element.
     *          If there are no attributes, it shall be an empty Attributes
     *          object.
     *
     * @return the outlet, not null.
     *
     * @throws  SAXException if the outlet cannot be created.
     */
    protected abstract Outlet createOutlet(
            QualifiedName outletName,
            String uri,
            String localName,
            String rawName,
            Attributes attributes)
                    throws SAXException;

    /**
     * Returns the outlet being configured.
     *
     * @return the outlet, not null.
     */
    public Outlet getOutlet()
    {
        return outlet;
    }

    /**
     * Returns whether we are currently processing a mergepoint tag.
     *
     * @return true if we are currently processing a mergepoint tag,
     *         false otherwise.
     */
    protected boolean isProcessingMergepointTag()
    {
        return mergepointSaxHandler != null;
    }

    /**
     * Returns whether we are past the end of the outlet configuration XML
     * snippet which we are parsing.
     *
     * @return true if the whole snippet has been processed, false otherwise.
     */
    public boolean isFinished()
    {
        return finished;
    }

    /**
     * Returns the ConfigurationProvider.
     *
     * @return the ConfigurationProvider, not null.
     */
    public ConfigurationProvider getConfigurationProvider()
    {
        return configurationProvider;
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
     * Returns the configuration handlers.
     *
     * @return the configuration handlers, not null.
     */
    public ConfigurationHandlers getConfigurationHandlers()
    {
        return configurationHandlers;
    }
}
