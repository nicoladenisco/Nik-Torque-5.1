package org.apache.torque.generator.source;

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


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * A node iterator iterating over attributes of a SourceElement.
 *
 * @author Dmitri Plotnikov
 * @author Thomas Fox
 *
 * @version $Id: $
 */
public class SourceElementAttributeIterator implements NodeIterator
{
    /** The parent pointer. */
    private final NodePointer parent;

    /** The source element over which attributes is iterated. */
    private final SourceElement sourceElement;

    /** The name of the selected attribute. */
    private final String name;

    /** All matching names. */
    private final List<String> names;

    /** The position of the iterator. */
    private int position = 0;

    /**
     * Create a new SourceElementAttributeIterator.
     *
     * @param parent the parent pointer
     * @param qName the name to test
     */
    public SourceElementAttributeIterator(final NodePointer parent, final QName qName)
    {
        this.parent = parent;
        this.name = qName.getName();
        names = new ArrayList<>();
        sourceElement = (SourceElement) parent.getNode();
        if (!name.equals("*"))
        {
            final Object attr = sourceElement.getAttribute(name);
            if (attr != null)
            {
                names.add(name);
            }
        }
        else
        {
            final Set<String> attributeNames = sourceElement.getAttributeNames();
            for (final String attributeName : attributeNames)
            {
                final Object value = sourceElement.getAttribute(attributeName);
                if (testAttr(attributeName, value))
                {
                    names.add(name);
                }
            }
        }
    }

    /**
     * Test an attribute.
     * @param attr to test
     * @return whether test succeeded
     */
    private boolean testAttr(final String attributeName, final Object value)
    {
        if (name.equals("*") || name.equals(attributeName))
        {
            return true;
        }
        return false;
    }

    @Override
    public NodePointer getNodePointer()
    {
        if (position == 0)
        {
            if (!setPosition(1))
            {
                return null;
            }
            position = 0;
        }
        int index = position - 1;
        if (index < 0)
        {
            index = 0;
        }
        return new SourceElementAttributePointer(
                parent,
                sourceElement,
                names.get(index));
    }

    @Override
    public int getPosition()
    {
        return position;
    }

    @Override
    public boolean setPosition(final int position)
    {
        this.position = position;
        return position >= 1 && position <= names.size();
    }
}
