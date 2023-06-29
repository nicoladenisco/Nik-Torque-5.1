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

import java.util.Collection;
import java.util.Collections;

import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.qname.QualifiedName;
import org.xml.sax.SAXException;

/**
 * Creates Java outlet SAX handlers.
 */
public class JavaOutletSaxHandlerFactory
implements TypedOutletSaxHandlerFactory
{
    /**
     * The type of the outlets which can be processed by the
     * OutletSaxHandlers created by this factory.
     */
    private static final String OUTLET_TYPE = "javaOutlet";

    /**
     * Returns the outlet type which can be handled by the
     * OutletSaxHandlers created by this factory.
     * @return "javaOutlet".
     */
    @Override
    public String getType()
    {
        return OUTLET_TYPE;
    }

    /**
     * Returns the filename extensions for templates which define outlets
     * of this type. These extensions are used for scanning the templates tree.
     *
     * @return the empty list, as this outlet type is not described
     *         by templates.
     */
    @Override
    public Collection<String> getTemplatesFilenameExtensionsForScan()
    {
        return Collections.emptyList();
    }

    /**
     * Creates an outlet for a template with the given file name.
     * This implementation throws a UnsupportedOperationException
     * as java outlets are not template based.
     *
     * @param templatePath the path to the template,
     *        relative to the templates directory, not null.
     * @param configurationProvider the configuration provider.
     *
     * @return always throws an Exception
     *
     * @throws UnsupportedOperationException always.
     */
    @Override
    public Outlet createOutletForTemplate(
            final String templatePath,
            final ConfigurationProvider configurationProvider)

    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a JavaOutletSaxHandler for reading the configuration of
     * Java outlets. This implementation uses the provided name
     * as outlet name.
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
     * @return a new JavaOutletSaxHandler.
     */
    @Override
    public final OutletSaxHandler getOutletSaxHandler(
            final QualifiedName outletName,
            final ConfigurationProvider configurationProvider,
            final UnitDescriptor unitDescriptor,
            final ConfigurationHandlers configurationHandlers)
                    throws SAXException
    {
        return new JavaOutletSaxHandler(
                outletName,
                configurationProvider,
                unitDescriptor,
                configurationHandlers);
    }
}
