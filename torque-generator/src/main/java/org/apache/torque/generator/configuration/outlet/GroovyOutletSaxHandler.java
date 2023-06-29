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

import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLET_ENCODING_ATTRIBUTE;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLET_NAME_ATTRIBUTE;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLET_OPTIONS_IN_BINDING_ATTRIBUTE;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLET_PATH_ATTRIBUTE;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLET_SOURCE_ATTRIBUTES_IN_BINDING_ATTRIBUTE;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLET_TEMPLATE_ATTRIBUTE;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLET_VARIABLES_IN_BINDING_ATTRIBUTE;

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.SaxHelper;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.template.groovy.GroovyOutlet;
import org.apache.torque.generator.template.groovy.GroovyScriptOutlet;
import org.apache.torque.generator.template.groovy.GroovyTemplateOutlet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Handles a declaration of a groovy outlet within a outlet
 * configuration file.
 */
class GroovyOutletSaxHandler extends OutletSaxHandler
{
    /**
     * The file suffix for groovy Scripts.
     * All other endings are interpreted as groovy templates.
     */
    public static final String GROOVY_SCRIPT_FILE_SUFFIX = ".groovy";

    /**
     * Constructor.
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
     * @throws SAXException if an error occurs during creation of the outlet.
     */
    public GroovyOutletSaxHandler(
            final QualifiedName outletName,
            final ConfigurationProvider configurationProvider,
            final UnitDescriptor unitDescriptor,
            final ConfigurationHandlers configurationHandlers)
                    throws SAXException
    {
        super(outletName,
                configurationProvider,
                unitDescriptor,
                configurationHandlers);
    }

    /**
     * Instantiates and configures a groovy outlet.
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
    protected GroovyOutlet createOutlet(
            QualifiedName outletName,
            final String uri,
            final String localName,
            final String rawName,
            final Attributes attributes)
                    throws SAXException
    {
        final String path = attributes.getValue(OUTLET_PATH_ATTRIBUTE);
        if (path == null)
        {
            throw new SAXException("The attribute "
                    + OUTLET_PATH_ATTRIBUTE
                    + " must be set on the element "
                    + rawName
                    + " for Groovy Outlets");
        }

        if (outletName == null)
        {
            final String nameAttribute
            = attributes.getValue(OUTLET_NAME_ATTRIBUTE);
            if (nameAttribute != null)
            {
                outletName = new QualifiedName(nameAttribute);
            }
            else
            {
                outletName = OutletConfigurationXmlParser
                        .getOutletNameForFilename(path);
            }
        }

        final String encoding = attributes.getValue(OUTLET_ENCODING_ATTRIBUTE);
        final String template = attributes.getValue(OUTLET_TEMPLATE_ATTRIBUTE);

        try
        {
            GroovyOutlet result;
            boolean isScript = false;
            if ("true".equals(template))
            {
                isScript = false;
            }
            else if ("false".equals(template))
            {
                isScript = true;
            }
            else if (path.endsWith(GROOVY_SCRIPT_FILE_SUFFIX))
            {
                isScript = true;
            }

            if (isScript)
            {
                result = new GroovyScriptOutlet(
                        outletName,
                        getConfigurationProvider(),
                        path,
                        encoding);
            }
            else
            {
                result = new GroovyTemplateOutlet(
                        outletName,
                        getConfigurationProvider(),
                        path,
                        encoding);
            }
            final Boolean optionsInContext = SaxHelper.getBooleanAttribute(
                    OUTLET_OPTIONS_IN_BINDING_ATTRIBUTE,
                    attributes,
                    "the groovyOutlet" + outletName);
            if (optionsInContext != null)
            {
                result.setOptionsInBinding(optionsInContext);
            }
            final Boolean sourceElementAttributesInContext
            = SaxHelper.getBooleanAttribute(
                    OUTLET_SOURCE_ATTRIBUTES_IN_BINDING_ATTRIBUTE,
                    attributes,
                    "the groovyOutlet" + outletName);
            if (sourceElementAttributesInContext != null)
            {
                result.setSourceAttributesInBinding(
                        sourceElementAttributesInContext);
            }
            final Boolean variablesInContext = SaxHelper.getBooleanAttribute(
                    OUTLET_VARIABLES_IN_BINDING_ATTRIBUTE,
                    attributes,
                    "the groovyOutlet" + outletName);
            if (variablesInContext != null)
            {
                result.setVariablesInContext(variablesInContext);
            }
            return result;
        }
        catch (final ConfigurationException e)
        {
            throw new SAXException(e);
        }
    }
}
