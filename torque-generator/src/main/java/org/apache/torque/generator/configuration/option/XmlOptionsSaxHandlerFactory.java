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

import org.xml.sax.SAXException;

/**
 * A Factory which creates a SAX handler for XML Options.
 */
public class XmlOptionsSaxHandlerFactory
implements OptionsSaxHandlerFactory
{
    /** The type of the configuration. */
    private static final String TYPE = "xmlOptions";


    /**
     * Returns the options type which can be handled by the
     * OptionsSaxHandlers created by this factory.
     *
     * @return "xmlOptions".
     */
    @Override
    public String getType()
    {
        return TYPE;
    }

    /**
     * Returns a FileOptionsSaxHandler for reading the configuration of
     * XML options.
     *
     * @return a new FileOptionsSaxHandler.
     */
    @Override
    public final FileOptionsSaxHandler getOptionsSaxHandler()
            throws SAXException
    {
        return new FileOptionsSaxHandler(new XmlOptionConfiguration());
    }
}
