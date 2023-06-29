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

import org.apache.torque.generator.configuration.source.EntityReferences;
import org.apache.torque.generator.source.SourceElement;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX Handler for creating a source element tree from xml.
 */
public class XmlSourceSaxHandler extends DefaultHandler
{
    /** The namespace of the xml schema instance. */
    private static final String SCHEMA_INSTANCE_NAMESPACE
    = "http://www.w3.org/2001/XMLSchema-instance";

    /**
     * The currently parsed element.
     */
    private SourceElement element;

    /**
     * The root element of the tree.
     */
    private SourceElement root;

    /** The known entity references, not null. */
    private EntityReferences entityReferences;

    /**
     * Constructor.
     *
     * @param entityReferences the known entity references, not null.
     */
    public XmlSourceSaxHandler(EntityReferences entityReferences)
    {
        if (entityReferences == null)
        {
            throw new NullPointerException("entityReferences must not be null");
        }
        this.entityReferences = entityReferences;
    }

    @Override
    public void startElement(String uri, String localName,
            String qName, Attributes attributes)
                    throws SAXException
    {
        SourceElement current = new SourceElement(qName);
        for (int i = 0; i < attributes.getLength(); ++i)
        {
            if (SCHEMA_INSTANCE_NAMESPACE.equals(attributes.getURI(i)))
            {
                // ignore schema definitions when reading the model
                continue;
            }
            current.setAttribute(
                    attributes.getQName(i), attributes.getValue(i));
        }
        if (element != null)
        {
            element.getChildren().add(current);
        }
        else
        {
            root = current;
        }
        element = current;
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {
        element = element.getParent();
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        String characterData = new String(ch, start, length);
        if (isWhitespace(characterData))
        {
            return;
        }
        String textContent = (String) element.getAttribute((String) null);
        if (textContent != null)
        {
            textContent = textContent + characterData;
        }
        else
        {
            textContent = characterData;
        }
        element.setAttribute((String) null, textContent);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException
    {
    }

    /**
     * EntityResolver implementation. Called by the XML parser
     *
     * @param publicId The public identifier of the external entity.
     * @param systemId The system identifier of the external entity.
     *
     * @return an InputSource for the entity, or null if the URI is not known.
     *
     * @see org.apache.torque.generator.configuration.ConfigurationEntityResolver#resolveEntity(String, String)
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException
    {
        return entityReferences.resolveEntity(publicId, systemId);
    }

    /**
     * Returns the root element of the tree.
     *
     * @return the root element (may be null if no elements were in the xml)
     *         or null if the xml was not yet parsed.
     */
    public SourceElement getRoot()
    {
        return root;
    }

    /**
     * Checks if a String consists only of whitespace
     *
     * @param toCheck the String to check.
     *
     * @return true if the String only contains whitespace characters, false
     *         if it contains other characters..
     */
    private boolean isWhitespace(String toCheck)
    {
        for (int i = 0; i < toCheck.length(); ++i)
        {
            char ch = toCheck.charAt(i);
            if (ch != ' ' && ch != '\r' && ch != '\n' && ch != '\t')
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void error(SAXParseException exception)
            throws SAXParseException
    {
        throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception)
            throws SAXParseException
    {
        throw exception;
    }

    @Override
    public void warning(SAXParseException exception)
            throws SAXParseException
    {
        throw exception;
    }
}
