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

import static org.apache.torque.generator.configuration.mergepoint.MergepointConfigurationTags.ACTION_VALUE_ATTRIBUTE;

import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.control.action.OutputAction;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A SAX handler which reads the configuration for a OutputAction
 * and creates and configures the Action according to the values in the
 * configuration XML.
 */
public class OutputActionSaxHandler extends ActionSaxHandler
{
    /**
     * Creates a OutputActionSaxHandler for reading the configuration
     * of a OptionAction.
     * @param uri - The Namespace URI, or the empty string if the
     *         element has no Namespace URI or if Namespace processing is not
     *         being performed.
     * @param localName - The local name (without prefix), or
     *         the empty string if Namespace processing is not being performed.
     * @param qName - The qualified name (with prefix), or the empty string if
     *          qualified names are not available.
     * @param attributes - The attributes attached to the element.
     *          If there are no attributes, it shall be an empty Attributes
     *          object.
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     *
     * @throws NullPointerException if an argument is null.
     * @throws SAXException if the element cannot be processed correctly.
     */
    public OutputActionSaxHandler(
            String uri,
            String localName,
            String qName,
            Attributes attributes,
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor)
                    throws SAXException
    {
        super(createAction(attributes), configurationProvider, unitDescriptor);
    }

    /**
     * Creates the action from the attributes of the action element.
     *
     * @param attributes the attributes of the action element.
     *
     * @return the action filled with the attribute values, not null.
     *
     * @throws SAXException if the creation of the action fails.
     */
    private static OutputAction createAction(Attributes attributes)
            throws SAXException
    {
        String value = attributes.getValue(ACTION_VALUE_ATTRIBUTE);
        OutputAction action = new OutputAction(value);
        return action;
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
        throw new SAXException("Unknown tag " + rawName);
    }
}
