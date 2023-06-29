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
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLETS_TAG;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLET_TAG;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationEntityResolver;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.OutletTypes;
import org.apache.torque.generator.configuration.SaxHelper;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.XMLConstants;
import org.apache.torque.generator.configuration.mergepoint.MergepointMapping;
import org.apache.torque.generator.configuration.mergepoint.MergepointSaxHandler;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.qname.QualifiedName;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX handler which parses Outlet configuration files.
 * Relies on delegate handlers for parsing the configuration for
 * the different outlet types.
 */
public class OutletConfigurationSaxHandler extends DefaultHandler
{
    /** The access object for the configuration files, not null. */
    private final ConfigurationProvider configurationProvider;

    /** The description of the generation unit, not null. */
    private final UnitDescriptor unitDescriptor;

    /** The list of outlets which configuration was already parsed. */
    private final List<Outlet> outlets = new ArrayList<>();

    /**
     * The list of separate mergepoint mappings (outside the outlets)
     * which configuration was already parsed.
     */
    private final List<MergepointMapping> mergepointMappings
        = new ArrayList<>();

    /** The available configuration handlers. */
    private final ConfigurationHandlers configurationHandlers;

    /**
     * The current delegate handler for parsing the current outlet's
     * configuration. Is null if no outlet is currently parsed.
     */
    private OutletSaxHandler outletHandler;

    /**
     * The current delegate handler for parsing the current mergepoint's
     * configuration. Is null if no mergepoint is currently parsed.
     */
    private MergepointSaxHandler mergepointHandler;

    /** The log. */
    private static Log log = LogFactory.getLog(OutletSaxHandler.class);

    /**
     * Constructor.
     *
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers The available configuration handlers,
     *        not null.
     *
     * @throws NullPointerException if an argument is null.
     */
    public OutletConfigurationSaxHandler(
            final ConfigurationProvider configurationProvider,
            final UnitDescriptor unitDescriptor,
            final ConfigurationHandlers configurationHandlers)
    {
        if (configurationProvider == null)
        {
            log.error("OutletConfigurationSaxHandler: "
                    + " configurationProvider is null");
            throw new NullPointerException("configurationProvider is null");
        }
        if (unitDescriptor == null)
        {
            log.error("OutletConfigurationSaxHandler: "
                    + " unitDescriptor is null");
            throw new NullPointerException("unitDescriptor is null");
        }
        if (configurationHandlers == null)
        {
            log.error("OutletConfigurationSaxHandler: "
                    + " configurationHandlers is null");
            throw new NullPointerException("configurationHandlers is null");
        }
        this.configurationProvider = configurationProvider;
        this.unitDescriptor = unitDescriptor;
        this.configurationHandlers = configurationHandlers;
    }

    /**
     * Returns all outlets which were configured in the parsed outlet
     * configuration file.
     *
     * @return all created outlets, not null.
     */
    public List<Outlet> getOutlets()
    {
        return outlets;
    }

    /**
     * Returns all mergepoint mappings which were configured
     * outside the outlets in the parsed outlet configuration file.
     *
     * @return all created mergepoint mappings, not null.
     */
    public List<MergepointMapping> getMergepointMappings()
    {
        return mergepointMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(
            final String uri,
            final String localName,
            final String qName,
            final Attributes attributes)
                    throws SAXException
    {
        String unqualifiedName = SaxHelper.getUnqualifiedName(localName, qName);
        if (outletHandler != null)
        {
            outletHandler.startElement(
                    uri, localName, qName, attributes);
        }
        else if (mergepointHandler != null)
        {
            mergepointHandler.startElement(
                    uri, localName, qName, attributes);
        }
        else if (OUTLET_TAG.equals(unqualifiedName))
        {
            String outletType = getOutletType(attributes);
            outletHandler = getOutletHandler(
                    null,
                    outletType);
            outletHandler.startElement(
                    uri, localName, qName, attributes);
        }
        else if (MERGEPOINT_TAG.equals(unqualifiedName))
        {
            mergepointHandler = new MergepointSaxHandler(
                    configurationProvider,
                    unitDescriptor,
                    configurationHandlers);
            mergepointHandler.startElement(
                    uri, localName, qName, attributes);
        }
        else if (!OUTLETS_TAG.equals(unqualifiedName))
        {
            throw new SAXException(
                    "Unknown element : " + unqualifiedName
                    + ". First element must be "
                    + OUTLETS_TAG
                    + " or "
                    + OUTLET_TAG);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(final String uri, final String localName, final String rawName)
            throws SAXException
    {
        if (outletHandler != null)
        {
            outletHandler.endElement(uri, localName, rawName);
            if (outletHandler.isFinished())
            {
                Outlet outlet = outletHandler.getOutlet();
                outlets.add(outlet);
                if (log.isDebugEnabled())
                {
                    log.debug("Parsed configuration for the outlet "
                            + outlet.getName());
                }
                outletHandler = null;
            }
        }
        else if (mergepointHandler != null)
        {
            mergepointHandler.endElement(uri, localName, rawName);
            if (mergepointHandler.isFinished())
            {
                MergepointMapping mergepointMapping
                = mergepointHandler.getMergepointMapping();
                mergepointMappings.add(mergepointMapping);
                if (log.isDebugEnabled())
                {
                    log.debug("Parsed configuration for the mergepoint "
                            + mergepointMapping.getName());
                }
                mergepointHandler = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters (final char[] ch, final int start, final int length)
            throws SAXException
    {
        if (outletHandler != null)
        {
            outletHandler.characters(ch, start, length);
        }
    }

    /**
     * Returns the correct handler for a outlet tag.
     * The method uses the type attribute to determine which handler is needed.
     * I.e. it looks up the handler factory type in the outlet types,
     * and asks the factory for a handler.
     *
     * @param outletName the name for the outlet which configuration
     *        will be read in by the generated SaxHandlerFactory,
     *        or null if the name of the outlet should be determined from
     *        the parsed XML.
     * @param outletType the type of the outlet, not null.
     *
     * @return the handler for the tag, not null.
     *
     * @throws SAXException if no matching handler can be identified,
     *         or if an error occurs while creating the handler.
     */
    public OutletSaxHandler getOutletHandler(
            final QualifiedName outletName,
            final String outletType)
                    throws SAXException
    {
        OutletTypes outletTypes = configurationHandlers.getOutletTypes();
        TypedOutletSaxHandlerFactory typedHandlerFactory
        = outletTypes.getTypedOutletHandlerFactories().get(outletType);
        if (typedHandlerFactory != null)
        {
            OutletSaxHandler outletSaxHandler
            = typedHandlerFactory.getOutletSaxHandler(
                    outletName,
                    configurationProvider,
                    unitDescriptor,
                    configurationHandlers);
            return outletSaxHandler;
        }
        UntypedOutletSaxHandlerFactory untypedHandlerFactory = null;
        for (UntypedOutletSaxHandlerFactory candidate
                : outletTypes.getUntypedOutletHandlerFactories())
        {
            if (candidate.canHandle(outletType, unitDescriptor))
            {
                untypedHandlerFactory = candidate;
                break;
            }
        }
        if (untypedHandlerFactory == null)
        {
            throw new SAXException(
                    "Unknown outlet type: "
                            + outletType);
        }
        OutletSaxHandler outletSaxHandler
        = untypedHandlerFactory.getOutletSaxHandler(
                outletType,
                outletName,
                configurationProvider,
                unitDescriptor,
                configurationHandlers);
        return outletSaxHandler;
    }

    /**
     * Reads the outlet type from the attributes of the outlet XML tag.
     *
     * @param attributes the attributes of the XML tag, not null.
     *
     * @return the outlet type, not null.
     *
     * @throws SAXException if the xsi:type attribute is not set.
     */
    public static String getOutletType(final Attributes attributes)
            throws SAXException
    {
        String outletType
        = attributes.getValue(
                XMLConstants.XSI_NAMESPACE,
                XMLConstants.XSI_TYPE_ATTRBUTE_NAME);
        if (outletType == null)
        {
            throw new SAXException("The tag " + OUTLET_TAG
                    + " requires the attribute "
                    + XMLConstants.XSI_NAMESPACE
                    + ":"
                    + XMLConstants.XSI_TYPE_ATTRBUTE_NAME);
        }
        return outletType;
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
    public InputSource resolveEntity(final String publicId, final String systemId)
            throws SAXException, IOException
    {
        return new ConfigurationEntityResolver().resolveEntity(
                publicId, systemId);
    }

    @Override
    public void error(final SAXParseException exception) throws SAXParseException
    {
        throw exception;
    }

    @Override
    public void fatalError(final SAXParseException exception)
            throws SAXParseException
    {
        throw exception;
    }

    @Override
    public void warning(final SAXParseException exception) throws SAXParseException
    {
        throw exception;
    }
}
