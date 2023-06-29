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
 * A Factory which creates a SAX handler for an OptionAction.
 */
public class OptionActionSaxHandlerFactory
implements ActionSaxHandlerFactory
{
    /**
     * The type of the actions which can be processed by the
     * ActionSaxHandlers created by this factory.
     */
    private static final String ACTION_TYPE = "optionAction";

    /**
     * Returns the action type which can be handled by the
     * ActionSaxHandlers created by this factory.
     * @return "optionAction".
     */
    @Override
    public String getType()
    {
        return ACTION_TYPE;
    }

    /**
     * Returns a OptionActionSaxHandler for reading the configuration of
     * OptionActions.
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
     * @return a new OptionActionSaxHandler.
     */
    @Override
    public final OptionActionSaxHandler getActionSaxHandler(
            String uri,
            String localName,
            String qName,
            Attributes attributes,
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor)
                    throws SAXException
    {
        return new OptionActionSaxHandler(
                uri,
                localName,
                qName,
                attributes,
                configurationProvider,
                unitDescriptor);
    }

}
