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

import static org.apache.torque.generator.configuration.mergepoint.MergepointConfigurationTags.ACTION_TAG;
import static org.apache.torque.generator.configuration.mergepoint.MergepointConfigurationTags.MERGEPOINT_NAME_ATTRIBUTE;
import static org.apache.torque.generator.configuration.mergepoint.MergepointConfigurationTags.MERGEPOINT_TAG;

import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.XMLConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A SAX Handler which processes a mergepoint configuration in an outlet.
 */
public class MergepointSaxHandler
{
    /**
     * The mergepoint mapping which is created and filled using the
     * information in the parsed XML element.
     */
    private MergepointMapping mergepointMapping;

    /**
     * The access object for the configuration files, not null.
     */
    private ConfigurationProvider configurationProvider;

    /**
     * The description of the generation unit, not null.
     */
    private UnitDescriptor unitDescriptor;

    /**
     * The available configuration handlers.
     */
    private ConfigurationHandlers configurationHandlers;

    /**
     * A SAX handler which parses nested elements. Null if no nested element
     * is currently parsed.
     */
    private ActionSaxHandler delegateHandler;

    /**
     * Whether the mergepoint element is completely parsed.
     */
    private boolean finished = false;

    /**
     * Constructor.
     *
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers the available configuration handlers,
     *        not null.
     *
     * @throws NullPointerException if an argument is null.
     */
    public MergepointSaxHandler(
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor,
            ConfigurationHandlers configurationHandlers)
    {
        if (configurationProvider == null)
        {
            throw new NullPointerException(
                    "configurationProvider must not be null");
        }
        if (unitDescriptor == null)
        {
            throw new NullPointerException("unitDescriptor must not be null");
        }
        if (configurationHandlers == null)
        {
            throw new NullPointerException(
                    "configurationHandlers must not be null");
        }
        this.configurationProvider = configurationProvider;
        this.unitDescriptor = unitDescriptor;
        this.configurationHandlers = configurationHandlers;
    }

    /**
     * Returns the mergepointMapping configured by this SaxHandler.
     * If this method is called before the mergepoint tag has been processed
     * completely, it will throw an IllegalStateException.
     *
     * @return the complete mergepointMapping, never null.
     *
     * @throws IllegalStateException if the mergepoint tag has not been
     *           processed completely.
     */
    public MergepointMapping getMergepointMapping()
    {
        if (!finished)
        {
            throw new IllegalStateException("not finished parsing");
        }
        return mergepointMapping;
    }

    /**
     * Callback method which is called by the SAX parser if an XML element is
     * started.
     * If a known element is encountered, its settings are read and applied to
     * the mergepoint; if an unknown element is encountered, a SaxException is
     * thrown.
     *
     * @param uri The namespace URI of the started element,
     *        or the empty string if the element has no namespace URI
     *        or if namespace processing is not being performed.
     * @param localName The local name (without prefix), or
     *        the empty string if Namespace processing is not being performed.
     * @param qName The qualified name (with prefix, if present),
     *        or the empty string if  qualified names are not available.
     * @param attributes The attributes attached to the element.
     *
     * @throws SAXException if an error occurs during parsing.
     *
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
     */
    public void startElement(
            String uri,
            String localName,
            String qName,
            Attributes attributes)
                    throws SAXException
    {
        if (delegateHandler != null)
        {
            delegateHandler.startElement(uri, localName, qName, attributes);
        }
        else if (MERGEPOINT_TAG.equals(qName))
        {
            String name = attributes.getValue(MERGEPOINT_NAME_ATTRIBUTE);
            if (name == null)
            {
                throw new SAXException("The tag "
                        + MERGEPOINT_TAG
                        + " needs to have the attribute "
                        + MERGEPOINT_NAME_ATTRIBUTE);
            }
            mergepointMapping = new MergepointMapping(name);
        }
        else if (ACTION_TAG.equals(qName))
        {
            String type = attributes.getValue(
                    XMLConstants.XSI_NAMESPACE,
                    XMLConstants.XSI_TYPE_ATTRBUTE_NAME);
            if (type == null)
            {
                throw new SAXException("The tag " + ACTION_TAG
                        + " requires the attribute "
                        + XMLConstants.XSI_NAMESPACE
                        + ":"
                        + XMLConstants.XSI_TYPE_ATTRBUTE_NAME);
            }

            ActionSaxHandlerFactories actionSaxHandlerFactories
            = configurationHandlers.getActionSaxHandlerFactories();
            ActionSaxHandlerFactory handlerFactory
            = actionSaxHandlerFactories.getActionSaxHandlerFactory(type);
            if (handlerFactory == null)
            {
                throw new SAXException("No handler found for the action "
                        + "of type "
                        + type);
            }

            delegateHandler = handlerFactory.getActionSaxHandler(
                    uri,
                    localName,
                    qName,
                    attributes,
                    configurationProvider,
                    unitDescriptor);
        }
        else
        {
            throw new SAXException("unknown element : " + qName);
        }
    }

    /**
     * Callback method which is called by the SAX parser if an XML element is
     * ended.
     * If an action element is ended, the action is added to the action
     * list for the mergepoint. If the mergepoint element is ended, the parser
     * marks itself as finished.
     *
     * @param uri The namespace URI of the ended element,
     *        or the empty string if the element has no namespace URI
     *        or if namespace processing is not being performed.
     * @param localName The local name (without prefix), or
     *        the empty string if Namespace processing is not being performed.
     * @param qName The qualified name (with prefix, if present),
     *        or the empty string if  qualified names are not available.
     *
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void endElement(String uri, String localName, String qName)
    {
        if (ACTION_TAG.equals(qName))
        {
            mergepointMapping.addAction(delegateHandler.getAction());
            delegateHandler = null;
        }
        else if (MERGEPOINT_TAG.equals(qName))
        {
            finished = true;
        }
    }

    /**
     * Returns whether the parser has finished parsing the mergepoint tag.
     *
     * @return true if the parser has finished, false otherwise.
     */
    public boolean isFinished()
    {
        return finished;
    }
}
