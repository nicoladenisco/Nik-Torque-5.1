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

import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLET_NAME_ATTRIBUTE;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLET_PATH_ATTRIBUTE;

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.outlet.copy.CopyOutlet;
import org.apache.torque.generator.qname.QualifiedName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Handles a declaration of a copy outlet within a outlet configuration
 * file.
 */
class CopyOutletSaxHandler extends OutletSaxHandler
{
    /**
     * Constructor.
     *
     * @param outletName the name for the outlet which configuration
     *        will be read in by the generated SaxHandlerFactory,
     *        or null if the name of the outlet should be determined from
     *        the parsed xml.
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers the available configuration handlers,
     *        not null.
     *
     * @throws SAXException if an error occurs during creation of the outlet.
     */
    public CopyOutletSaxHandler(
            QualifiedName outletName,
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor,
            ConfigurationHandlers configurationHandlers)
                    throws SAXException
    {
        super(outletName,
                configurationProvider,
                unitDescriptor,
                configurationHandlers);
    }

    /**
     * Instantiates and configures a velocity outlet.
     *
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
     * @return the created outlet, not null.
     *
     * @throws SAXException if an error occurs during creation.
     */
    @Override
    protected CopyOutlet createOutlet(
            QualifiedName outletName,
            String uri,
            String localName,
            String rawName,
            Attributes attributes)
                    throws SAXException
    {
        if (outletName == null)
        {
            String nameAttribute
            = attributes.getValue(OUTLET_NAME_ATTRIBUTE);
            if (nameAttribute == null)
            {
                throw new SAXException("The attribute "
                        + OUTLET_NAME_ATTRIBUTE
                        + " must be set on the element "
                        + rawName
                        + " for Velocity Outlets");
            }
            outletName = new QualifiedName(nameAttribute);
        }

        String path = attributes.getValue(OUTLET_PATH_ATTRIBUTE);

        try
        {
            CopyOutlet result
                = new CopyOutlet(
                    outletName,
                    getConfigurationProvider(),
                    path);
            return result;
        }
        catch (ConfigurationException e)
        {
            throw new SAXException(e);
        }
    }
}
