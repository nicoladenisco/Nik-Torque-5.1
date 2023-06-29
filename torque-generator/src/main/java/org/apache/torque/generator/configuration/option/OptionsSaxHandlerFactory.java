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
 * Classes implementing this interface are responsible for creating
 * <code>OptionsSaxHandler</code>s for a specific action type.
 *
 * When a action configuration needs to be parsed, a sax handler will read
 * the type of the action and check it against the types of the registered
 * OptionsSaxHandlers. The first matching handler will then be used to parse
 * the action configuration.
 */
public abstract interface OptionsSaxHandlerFactory
{
    /**
     * Returns the action type handled by the ActionSaxHandlers which are
     * created by this factory.
     *
     * @return the type of the action, not null.
     */
    String getType();

    /**
     * Returns a OptionsSaxHandler for reading the configuration of
     * options. The SAX Handler is used as a delegate handler
     * whenever an options element with the matching type
     * is encountered in a configuration file.
     *
     * @return a SAX delegate handler for parsing the configuration with the
     *         given type.
     * @throws SAXException if the SAX Handler for the options can
     *         not be created from the given XML element.
     */
    OptionsSaxHandler getOptionsSaxHandler()
            throws SAXException;
}
