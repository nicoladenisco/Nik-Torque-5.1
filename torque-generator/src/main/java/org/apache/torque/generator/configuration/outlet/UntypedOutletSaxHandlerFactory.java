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

import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.qname.QualifiedName;
import org.xml.sax.SAXException;

/**
 * Classes implementing this interface can create creating
 * <code>OutletSaxHandler</code>s for more than one outlet type.
 */
public abstract interface UntypedOutletSaxHandlerFactory
{
    /**
     * Returns whether the handler can create OutletSaxHandlers for the given
     * type.
     *
     * @param type the type to check.
     * @param unitDescriptor The description of the generation unit, not null.
     *
     * @return true if the type is supported, false if not.
     */
    boolean canHandle(String type, UnitDescriptor unitDescriptor);

    /**
     * Returns a OutletSaxHandler for reading in the configuration of
     * a outlet. The SAX Handler is used as a delegate handler
     * whenever a outlet element with the matching type
     * is encountered in a outlet configuration file.
     *
     * @param outletType the type of the outlet, not null.
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
     * @return a SAX delegate handler for parsing the configuration with the
     *           given type.
     * @throws SAXException if the SAX Handler for the outlet can
     *           not be created from the given XML element.
     */
    OutletSaxHandler getOutletSaxHandler(
            String outletType,
            QualifiedName outletName,
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor,
            ConfigurationHandlers configurationHandlers)
                    throws SAXException;
}
