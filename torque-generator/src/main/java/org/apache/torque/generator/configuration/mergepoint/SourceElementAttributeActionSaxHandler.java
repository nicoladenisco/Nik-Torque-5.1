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

import static org.apache.torque.generator.configuration.mergepoint.MergepointConfigurationTags.ACTION_ACCEPT_NOT_SET_ATTRIBUTE;
import static org.apache.torque.generator.configuration.mergepoint.MergepointConfigurationTags.ACTION_ATTRIBUTE_ATTRIBUTE;
import static org.apache.torque.generator.configuration.mergepoint.MergepointConfigurationTags.ACTION_ELEMENT_ATTRIBUTE;

import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.SaxHelper;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.control.action.SourceElementAttributeAction;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A SAX handler which reads the configuration for a
 * SourceElementAttributeAction and creates and configures the Action
 * according to the values in the configuration XML.
 */
public class SourceElementAttributeActionSaxHandler extends ActionSaxHandler
{
    /**
     * Creates a OptionActionSaxHandler for reading the configuration
     * of a SourceElementAttributeAction.
     * @param uri The namespace URI of the action element,
     *        or the empty string if the element has no namespace URI
     *        or if namespace processing is not being performed.
     * @param localName The local name (without prefix), or
     *        the empty string if Namespace processing is not being performed.
     * @param qName - The qualified name (with prefix, if present),
     *        or the empty string if  qualified names are not available.
     * @param attributes The attributes attached to the element.
     * @param configurationProvider for accessing the configuratiopn files,
     *        not null.
     * @param unitDescriptor The description of the generation unit, not null.
     *
     * @throws NullPointerException if an argument is null.
     * @throws SAXException if the element cannot be processed correctly.
     */
    public SourceElementAttributeActionSaxHandler(
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
    private static SourceElementAttributeAction createAction(
            Attributes attributes)
                    throws SAXException
    {
        String element = attributes.getValue(ACTION_ELEMENT_ATTRIBUTE);
        String attribute = attributes.getValue(ACTION_ATTRIBUTE_ATTRIBUTE);
        Boolean acceptNotSet = SaxHelper.getBooleanAttribute(
                ACTION_ACCEPT_NOT_SET_ATTRIBUTE,
                attributes,
                "the SourceElementAttributeAction " + element);
        SourceElementAttributeAction action
            = new SourceElementAttributeAction(
                element,
                attribute,
                acceptNotSet);
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
