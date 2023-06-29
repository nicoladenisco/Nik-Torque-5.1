package org.apache.torque.generator.source;

import java.util.Objects;

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

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A node pointer pointing to an attribute of a SourceElement.
 *
 * @author Dmitri Plotnikov
 * @author Thomas Fox
 *
 * @version $Id$
 */
public class SourceElementAttributePointer extends NodePointer
{
    /** SerialVersionUID. */
    private static final long serialVersionUID = 1115085175427555951L;

    /** The source element to which the attribute belongs. */
    private final SourceElement sourceElement;

    /** The name of the attribute. */
    private final String name;

    /**
     * Constructor.
     *
     * @param parent the parent pointer.
     * @param sourceElement the SourceElement to which attribute is pointed.
     * @param name the name of the attribute pointed to.
     */
    public SourceElementAttributePointer(
            final NodePointer parent,
            final SourceElement sourceElement,
            final String name)
    {
        super(parent);
        this.sourceElement = sourceElement;
        this.name = name;
    }

    @Override
    public QName getName()
    {
        return new QName(null, name);
    }

    @Override
    public String getNamespaceURI()
    {
        return null;
    }

    @Override
    public Object getValue()
    {
        return sourceElement.getAttribute(name);
    }

    @Override
    public Object getBaseValue()
    {
        return sourceElement.getAttribute(name);
    }

    @Override
    public boolean isCollection()
    {
        return false;
    }

    @Override
    public int getLength()
    {
        return 1;
    }

    @Override
    public Object getImmediateNode()
    {
        return sourceElement.getAttribute(name);
    }

    @Override
    public boolean isActual()
    {
        return true;
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    }

    @Override
    public boolean testNode(final NodeTest nodeTest)
    {
        return nodeTest == null
                || nodeTest instanceof NodeTypeTest
                        && ((NodeTypeTest) nodeTest).getNodeType() == Compiler.NODE_TYPE_NODE;
    }

    /**
     * Sets the value of this attribute.
     *
     * @param value to set
     */
    @Override
    public void setValue(final Object value)
    {
        sourceElement.setAttribute(name, value);
    }

    /**
     * Removes the attribute pointed to.
     */
    @Override
    public void remove()
    {
        sourceElement.setAttribute(name, null);
    }

    @Override
    public String asPath()
    {
        final StringBuffer buffer = new StringBuffer();
        if (parent != null)
        {
            buffer.append(parent.asPath());
            if (buffer.length() == 0
                    || buffer.charAt(buffer.length() - 1) != '/')
            {
                buffer.append('/');
            }
        }
        buffer.append('@');
        buffer.append(getName());
        return buffer.toString();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(sourceElement)
                .append(name)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object object)
    {
        if (object == this)
        {
            return true;
        }
        if (!(object instanceof SourceElementAttributePointer))
        {
            return false;
        }
        final SourceElementAttributePointer other
        = (SourceElementAttributePointer) object;
        if (sourceElement != other.sourceElement)
        {
            return false;
        }
        return Objects.equals(name, other.name);
    }

    @Override
    public int compareChildNodePointers(
            final NodePointer pointer1,
            final NodePointer pointer2)
    {
        // Won't happen - attributes don't have children
        return 0;
    }

}
