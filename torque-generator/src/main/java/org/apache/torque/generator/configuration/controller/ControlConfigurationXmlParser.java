package org.apache.torque.generator.configuration.controller;

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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * Parses the xml file which provides the control configuration.
 */
public class ControlConfigurationXmlParser
{
    /**
     * The SaxParserFactory to create the SAX parsers for reading in the
     * configuration files.
     */
    private static SAXParserFactory saxFactory;

    /** The logger. */
    private static Log log
    = LogFactory.getLog(ControlConfigurationXmlParser.class);

    static
    {
        saxFactory = SAXParserFactory.newInstance();
        saxFactory.setNamespaceAware(true);
        try
        {
            saxFactory.setFeature(
                    "http://xml.org/sax/features/validation",
                    true);
            saxFactory.setFeature(
                    "http://apache.org/xml/features/validation/schema", true);
        }
        catch (SAXNotSupportedException | SAXNotRecognizedException | ParserConfigurationException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the controller configuration out of a configurationProvider.
     *
     * @param configurationProvider the object for accessing the configuration,
     *        not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers the available configuration handlers,
     *        not null.
     *
     * @return the Controller configuration.

     * @throws ConfigurationException if an error in the configuration
     *         is encountered.
     * @throws NullPointerException if an argument is null.
     */
    public ControlConfiguration readControllerConfiguration(
            final ConfigurationProvider configurationProvider,
            final UnitDescriptor unitDescriptor,
            final ConfigurationHandlers configurationHandlers)
                    throws ConfigurationException
    {
        InputStream controlConfigurationInputStream
        = configurationProvider.getControlConfigurationInputStream();
        try
        {
            ControlConfiguration result = new ControlConfiguration();
            try
            {
                SAXParser parser = saxFactory.newSAXParser();
                InputSource is
                    = new InputSource(controlConfigurationInputStream);
                parser.parse(
                        is,
                        new ControlConfigurationSaxHandler(
                                result,
                                configurationProvider,
                                unitDescriptor,
                                configurationHandlers));
            }
            catch (SAXParseException e)
            {
                throw new ConfigurationException(
                        "Error parsing controller Configuration "
                                + configurationProvider
                                .getControlConfigurationLocation()
                                + " at line "
                                + e.getLineNumber()
                                + " column "
                                + e.getColumnNumber()
                                + " : "
                                + e.getMessage(),
                                e);

            }
            catch (Exception e)
            {
                throw new ConfigurationException(
                        "Error parsing controller Configuration "
                                + configurationProvider
                                .getControlConfigurationLocation()
                                + " : "
                                + e.getMessage(),
                                e);
            }
            return result;
        }
        finally
        {
            try
            {
                controlConfigurationInputStream.close();
            }
            catch (IOException e)
            {
                log.warn("Could not close controlConfigurationInputStream", e);
            }
        }
    }
}
