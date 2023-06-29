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

import static org.apache.torque.generator.configuration.controller.OutputConfigurationTags.OUTLET_TAG;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads a Outlet reference file from the controller
 * configuration file.
 */
public class OutletReferenceSaxHandler extends DefaultHandler
{
    /**
     * The outlet reference which is currently populated,
     * or null if no outlet reference is currently populated.
     */
    private OutletReference outletReference;

    /**
     * Whether the complete XML snippet which should be parsed by this
     * Handler has been parsed..
     */
    private boolean finished = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String rawName,
            Attributes attributes)
                    throws SAXException
    {
        if (OUTLET_TAG.equals(rawName))
        {
            String name = attributes.getValue("name");
            outletReference = new OutletReference(name);
        }
        else
        {
            throw new SAXException("Unknown element " + rawName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String rawName)
            throws SAXException
    {
        if (OUTLET_TAG.equals(rawName))
        {
            finished = true;
        }
    }

    /**
     * Returns the outlet reference which was filled by this handler.
     *
     * @return the outlet reference which was filled by this handler,
     *         not null.
     */
    public OutletReference getOutletReference()
    {
        return outletReference;
    }

    /**
     * Returns whether the handler has already parsed the end of the snippet
     * for which it is responsible.
     *
     * @return true if the end of the snippet is reached, false otherwise.
     */
    public boolean isFinished()
    {
        return finished;
    }
}
