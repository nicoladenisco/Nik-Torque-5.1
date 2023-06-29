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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;

/**
 * Creates an xml String out of a source tree.
 */
public class SourceToXml
{
    /**
     * the id of the last created element.
     */
    private int idCounter = 0;

    /**
     * The String by which a child is indented relative to its parent.
     */
    private String indent = "  ";

    /**
     * the name of the Id attribute.
     */
    private static final String ID_ATTRIBUTE = "id";

    /**
     * The name of the refid Attribute.
     */
    private static final String REFID_ATTRIBUTE = "refid";

    /**
     * The map with XML special characters as key and their replacement
     * as value.
     */
    private static final Map<Character, String> ESCAPE_MAP;
    static
    {
        Map<Character, String> escapeMap = new HashMap<>();
        escapeMap.put('&', "&amp;");
        escapeMap.put('>', "&gt;");
        escapeMap.put('<', "&lt;");
        escapeMap.put('"', "&quot;");
        escapeMap.put('\'', "&apo;");
        ESCAPE_MAP = Collections.unmodifiableMap(escapeMap);
    }

    /**
     * Renders a source element and its children as XML.
     *
     * @param rootElement the element to render.
     * @param createIdAttributes whether id attributes should be created
     *        and filled automatically.
     *
     * @return the created XML.
     *
     * @throws SourceException if automaticIds is false and an element
     *         is encountered twice in the source tree.
     */
    public String toXml(SourceElement rootElement, boolean createIdAttributes)
            throws SourceException
    {
        Map<SourceElement, Integer> knownElements
            = new HashMap<>();
        StringBuilder result = new StringBuilder();
        String currentIndent = "";
        outputElement(
                rootElement,
                knownElements,
                result,
                currentIndent,
                true,
                createIdAttributes);
        result.append("\n"); // end with newline
        return result.toString();
    }

    /**
     * Renders a source element and its children as XML.
     *
     * @param currentElement the element to render.
     * @param knownElements a map containing the elements which have already
     *        been rendered as key, and their id as value
     * @param result the buffer to which the XML should be appended.
     * @param currentIndent the current indentation.
     * @param identAtStart Whether the start element should be idented.
     * @param createIdAttributes whether id attributes should be created
     *        and filled automatically.
     *
     * @throws SourceException if createIdAttributes is false and an element
     *         is encountered twice in the source tree.
     */
    private void outputElement(
            SourceElement currentElement,
            Map<SourceElement, Integer> knownElements,
            StringBuilder result,
            String currentIndent,
            boolean identAtStart,
            boolean automaticIds)
                    throws SourceException
    {
        // check whether element is already known, output reference if yes
        Integer currentId;
        {
            Integer knownElementId = knownElements.get(currentElement);

            if (knownElementId != null)
            {
                if (!automaticIds)
                {
                    throw new SourceException(
                            "An element with name " + currentElement.getName()
                            + " occurs at least twice in the source graph,"
                            + " but createIdAttributes is false.");
                }
                result.append(currentIndent)
                .append("<")
                .append(currentElement.getName())
                .append(" ")
                .append(REFID_ATTRIBUTE)
                .append("=\"")
                .append(knownElementId)
                .append("\"/>");
                return;
            }
            currentId = getId();
            knownElements.put(currentElement, currentId);
        }

        // output start tag
        if (identAtStart)
        {
            result.append(currentIndent);
        }
        result.append("<")
        .append(currentElement.getName());
        boolean hasTextAttribute = false;
        for (String attributeName : currentElement.getAttributeNames())
        {
            if (attributeName == null) // null is text node
            {
                hasTextAttribute = true;
            }
            else
            {
                result.append(" ")
                .append(attributeName)
                .append("=\"");
                appendWithEscaping(
                        currentElement.getAttribute(attributeName).toString(),
                        result);
                result.append("\"");
            }
        }
        if (automaticIds)
        {
            result.append(" ")
            .append(ID_ATTRIBUTE)
            .append("=\"")
            .append(currentId)
            .append("\"");
        }
        boolean hasChildren = !currentElement.getChildren().isEmpty();
        if (hasChildren || hasTextAttribute)
        {
            result.append(">");
            if (hasTextAttribute)
            {
                appendWithEscaping(
                        currentElement.getAttribute((String) null).toString(),
                        result);
            }
        }
        else
        {
            result.append("/>");
        }

        // process children
        {
            String childIndent = currentIndent + indent;
            SourceElement previousChild = null;
            for (SourceElement child : currentElement.getChildren())
            {
                if (!hasTextAttribute)
                {
                    result.append("\n");
                }
                outputElement(
                        child,
                        knownElements,
                        result,
                        childIndent,
                        previousChild != null || !hasTextAttribute,
                        automaticIds);
                previousChild = child;
            }
            if (previousChild != null)
            {
                result.append("\n");
            }
        }

        if (hasChildren || hasTextAttribute)
        {
            //output end tag
            if (hasChildren)
            {
                result.append(currentIndent);
            }
            result.append("</")
            .append(currentElement.getName())
            .append(">");
        }
    }

    /**
     * returns a unique id.
     *
     * @return a unique id.
     */
    private synchronized Integer getId()
    {
        ++idCounter;
        return idCounter;
    }

    /**
     * Returns the String by which a child element is indented relative to
     * its parent.
     *
     * @return the indent String.
     */
    public String getIndent()
    {
        return indent;
    }

    /**
     * Sets the String by which a child element is indented relative to
     * its parent.
     *
     * @param indent the indent String to set.
     */
    public void setIndent(String indent)
    {
        if (indent == null)
        {
            throw new NullPointerException("indent is null");
        }
        this.indent = indent;
    }

    /**
     * Appends a string to a string builder. XML special characters in the
     * string are escaped.
     *
     * @param toAppend the string to append to the string builder,
     *        not null.
     * @param stringBuilder the string builder to append to, not null.
     */
    private void appendWithEscaping(
            String toAppend,
            StringBuilder stringBuilder)
    {
        for (int pos = 0; pos < toAppend.length(); ++pos)
        {
            char current = toAppend.charAt(pos);
            String escapeSequence = ESCAPE_MAP.get(current);
            if (escapeSequence != null)
            {
                stringBuilder.append(escapeSequence);
            }
            else
            {
                stringBuilder.append(current);
            }
        }
    }
}
