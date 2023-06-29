package org.apache.torque.generator.configuration;

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

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * Helper methods for SAX handlers.
 */
public final class SaxHelper
{
    /**
     * private constructor for utility class.
     */
    private SaxHelper()
    {
    }

    /**
     * Retrieves an attribute as boolean value.
     *
     * @param attributeName the name of the attribute to retrieve, not null.
     * @param attributes The attributes of the current element.
     * @param elementDescription the description of the parsed element,
     *        for producing a user-readable error message. E.g
     *        "the optionAction ${nameOfTheAction}"
     *
     * @return the value of the attribute, or null if the attribute is not set.
     *
     * @throws SAXException if the attribute contains content
     *         other than "true", "1" , "false" or "0".
     */
    public static Boolean getBooleanAttribute(
            String attributeName,
            Attributes attributes,
            String elementDescription)
                    throws SAXException
    {
        String attributeAsString = attributes.getValue(attributeName);
        if (attributeAsString == null)
        {
            return null;
        }
        if ("false".equals(attributeAsString)
                || "0".equals(attributeAsString))
        {
            return false;
        }
        else if ("true".equals(attributeAsString)
                || "1".equals(attributeAsString))
        {
            return true;
        }
        else
        {
            throw new SAXException("The attribute "
                    + attributeName
                    + "of "
                    + elementDescription
                    + " must either be false, 0, true or 1");
        }
    }

    /**
     * Retrieves the unqualified part of an XML element name name,
     * regardless whether namespace processing is switched on or off.
     *
     * @param localName The local name (without prefix), or the
     *        empty string if namespace processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     *
     * @return the unqualified part of the name.
     */
    public static String getUnqualifiedName(String localName, String qName)
    {
        if (!StringUtils.isEmpty(localName))
        {
            return localName;
        }
        return qName;
    }
}
