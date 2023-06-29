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

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.qname.QualifiedName;
import org.xml.sax.SAXException;

/**
 * Classes implementing this interface are responsible for creating
 * <code>OutletSaxHandler</code>s for a specific outlet type.
 */
public interface TypedOutletSaxHandlerFactory
{
    /**
     * Returns the outlet type handled by the OutletSaxHandlers which are
     * created by this factory.
     *
     * @return the type of the outlets, not null.
     */
    String getType();

    /**
     * Returns the filename extensions for templates which define outlets
     * of this type. These extensions are used for scanning the templates tree.
     *
     * @return the filename extension for scanning the templates,
     *         or null if the templates should not be scanned.
     */
    Collection<String> getTemplatesFilenameExtensionsForScan();

    /**
     * Creates an outlet for a template with the given file name.
     *
     * @param templatePath the path to the template,
     *        relative to the templates directory, not null.
     * @param configurationProvider the configuration provider, not null.
     *
     * @return the outlet, not null.
     *
     * @throws ConfigurationException if the outlet cannot be created.
     * @throws UnsupportedOperationException if the OutletSaxHandlerFactory
     *         is not template based or cannot create outlets on file name
     *         information alone.
     */
    Outlet createOutletForTemplate(
            String templatePath,
            ConfigurationProvider configurationProvider)
                    throws ConfigurationException;

    /**
     * Returns a OutletSaxHandler for reading in the configuration of
     * a outlet. The SAX Handler is used as a delegate handler
     * whenever a outlet element with the matching type
     * is encountered in a outlet configuration file.
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
     * @return a SAX delegate handler for parsing the configuration with the
     *           given type.
     * @throws SAXException if the SAX Handler for the outlet can
     *           not be created from the given XML element.
     */
    OutletSaxHandler getOutletSaxHandler(
            QualifiedName outletName,
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor,
            ConfigurationHandlers configurationHandlers)
                    throws SAXException;
}
