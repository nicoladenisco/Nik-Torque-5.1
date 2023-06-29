package org.apache.torque.generator.configuration.mergepoint;

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

import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Classes implementing this interface are responsible for creating
 * <code>ActionSaxHandler</code>s for a specific action type.
 *
 * When a action configuration needs to be parsed, a sax handler will read
 * the type of the action and check it against the types of the registered
 * ActionSaxHandlers. The first matching handler will then be used to parse
 * the action configuration.
 */
public abstract interface ActionSaxHandlerFactory
{
    /**
     * Returns the action type handled by the ActionSaxHandlers which are
     * created by this factory.
     *
     * @return the type of the action, not null.
     */
    String getType();

    /**
     * Returns a ActionSaxHandler for reading in the configuration of
     * an action. The SAX Handler is used as a delegate handler
     * whenever an action element with the matching type
     * is encountered in a configuration file.
     *
     * @param uri The namespace URI of the action element,
     *        or the empty string if the element has no namespace URI
     *        or if namespace processing is not being performed.
     * @param localName The local name (without prefix), or
     *        the empty string if namespace processing is not being performed.
     * @param qName The qualified name (with prefix, if present),
     *        or the empty string if  qualified names are not available.
     * @param attributes The attributes attached to the element.
     * @param configurationProvider for accessing the configuration files,
     *        not null.
     * @param unitDescriptor The description of the generation unit, not null.
     *
     * @return a SAX delegate handler for parsing the configuration with the
     *           given type.
     * @throws SAXException if the SAX Handler for the outlet can
     *           not be created from the given XML element.
     */
    ActionSaxHandler getActionSaxHandler(
            String uri,
            String localName,
            String qName,
            Attributes attributes,
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor)
                    throws SAXException;
}
