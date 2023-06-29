package org.apache.torque.generator.source.stream;

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

import org.apache.torque.generator.configuration.source.EntityReferences;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * The source type representing an XML source.
 *
 * $Id: XmlSourceFormat.java 1839288 2018-08-27 09:48:33Z tv $
 */
public final class XmlSourceFormat implements StreamSourceFormat
{
    /** The key for a XML source. */
    private static final String KEY = "xml";

    /** The filename extension for a XML source. */
    private static final String FILENAME_EXTENSION = "xml";

    /**
     * The factory from which the parser is created.
     */
    private static final SAXParserFactory SAX_FACTORY;

    static
    {
        SAX_FACTORY = SAXParserFactory.newInstance();
        SAX_FACTORY.setNamespaceAware(true);
        try
        {
            SAX_FACTORY.setFeature(
                    "http://apache.org/xml/features/validation/dynamic", true);
            SAX_FACTORY.setFeature(
                    "http://apache.org/xml/features/validation/schema", true);
        }
        catch (SAXNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
        catch (SAXNotRecognizedException e)
        {
            throw new RuntimeException(e);
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an unique key for the source type.
     *
     * @return "xml".
     */
    @Override
    public String getKey()
    {
        return KEY;
    }

    /**
     * Gets the filename extension this source type typically has.
     *
     * @return "xml".
     */
    @Override
    public String getFilenameExtension()
    {
        return FILENAME_EXTENSION;
    }

    /**
     * Parses a stream containing xml data and creates a source element
     * hierarchy from it.
     *
     * @param xmlStream the stream containing the xml data, not null.
     * @param controllerState the controller state, not null.
     *
     * @return the root element of the created hierarchy.
     *
     * @throws SourceException if an error occurs reading the input stream,
     *         parsing the XML data or if the SAX parser is not configured
     *         correctly.
     */
    @Override
    public SourceElement parse(
            InputStream xmlStream,
            ControllerState controllerState)
                    throws SourceException
    {
        if (xmlStream == null)
        {
            throw new NullPointerException("No Input path specified");
        }
        try
        {
            SAXParser parser = SAX_FACTORY.newSAXParser();

            EntityReferences entityReferences
            = controllerState.getUnitConfiguration()
            .getEntityReferences();
            XmlSourceSaxHandler handler = new XmlSourceSaxHandler(
                    entityReferences);

            parser.parse(xmlStream, handler);
            return handler.getRoot();
        }
        catch (IOException e)
        {
            throw new SourceException(
                    "Error reading XML source file: " + e.getMessage(),
                    e);
        }
        catch (SAXException e)
        {
            throw new SourceException(
                    "Error parsing XML source file: " + e.getMessage(),
                    e);
        }
        catch (ParserConfigurationException e)
        {
            throw new SourceException(
                    "Parser configuration error parsing Properties"
                            + " source file: "
                            + e.getMessage(),
                            e);
        }
    }

    /**
     * Returns a hash code of this instance consistent with equals..
     * As all instances of this class are equal to each other,
     * the hash code is always the same.
     *
     * @return 1.
     */
    @Override
    public int hashCode()
    {
        return 1;
    }

    /**
     * Checks whether other is equal to this instance.
     * All instances of this class are equal to each other.
     *
     * @return true if <code>other</code> is a XmlSourceType, false
     *         otherwise.
     */
    @Override
    public boolean equals(Object other)
    {
        if (other == null)
        {
            return false;
        }
        if (!other.getClass().equals(XmlSourceFormat.class))
        {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
}
