package org.apache.torque.generator.configuration.option;

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

import static org.apache.torque.generator.configuration.option.OptionTags.OPTIONS_PATH_ATTRIBUTE;
import static org.apache.torque.generator.configuration.option.OptionTags.OPTIONS_TAG;

import org.apache.torque.generator.configuration.SaxHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Reads a file options configuration.
 */
public class FileOptionsSaxHandler extends OptionsSaxHandler
{
    /** The option configuration which is currently filled. */
    private FileOptionsConfiguration optionsConfiguration;

    public FileOptionsSaxHandler(FileOptionsConfiguration optionsConfiguration)
    {
    	// The super constructor performs the test for null
        super(optionsConfiguration);
        this.optionsConfiguration = optionsConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes)
                    throws SAXException
    {
        String unqualifiedName = SaxHelper.getUnqualifiedName(localName, qName);
        if (OPTIONS_TAG.equals(unqualifiedName))
        {
            String path = attributes.getValue(OPTIONS_PATH_ATTRIBUTE);
            if (path == null)
            {
                throw new SAXException(
                        "path must not be null for file options");
            }
            optionsConfiguration.setPath(path);
        }
        else
        {
            throw new SAXException("Unknown element " + unqualifiedName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {
        String unqualifiedName = SaxHelper.getUnqualifiedName(localName, qName);
        if (OPTIONS_TAG.equals(unqualifiedName))
        {
            finished();
        }
        else
        {
            throw new SAXException("Unknown element " + unqualifiedName);
        }
    }
}
