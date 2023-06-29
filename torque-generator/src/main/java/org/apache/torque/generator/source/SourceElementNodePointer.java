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

import java.util.Locale;

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * A node pointer pointing to a SourceElement.
 *
 * @author Dmitri Plotnikov
 * @author Thomas Fox
 *
 * @version $Id: $
 */
public class SourceElementNodePointer extends NodePointer
{
    /** SerialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The SourceElement pointed to. */
    private final SourceElement sourceElement;

    protected SourceElementNodePointer(
            final SourceElement sourceElement,
            final Locale locale)
    {
        super(null, locale);
        this.sourceElement = sourceElement;
    }

    protected SourceElementNodePointer(
            final NodePointer parent,
            final SourceElement sourceElement)
    {
        super(parent);
        this.sourceElement = sourceElement;
    }

    @Override
    public boolean isLeaf()
    {
        return sourceElement.getChildren().isEmpty();
    }

    @Override
    public boolean isCollection()
    {
        return true;
    }

    @Override
    public int getLength()
    {
        // get parent in path, which is not necessarily
        // the primary parent of the source element
        NodePointer parentPointer = getParent();
        if (parentPointer == null)
        {
            return 1;
        }
        SourceElement parentInPath
        = ((SourceElement) getParent().getBaseValue());
        return parentInPath.getChildren(sourceElement.getName()).size();
    }

    @Override
    public QName getName()
    {
        return new QName(null, sourceElement.getName());
    }

    @Override
    public Object getBaseValue()
    {
        return sourceElement;
    }

    @Override
    public Object getImmediateNode()
    {
        return sourceElement;
    }

    @Override
    public void setValue(final Object value)
    {
        // do nothing
    }

    @Override
    public int compareChildNodePointers(final NodePointer pointer1,
            final NodePointer pointer2)
    {
        final SourceElement sourceElement1
        = (SourceElement) pointer1.getBaseValue();
        final SourceElement sourceElement2
        = (SourceElement) pointer2.getBaseValue();
        if (sourceElement1 == sourceElement2)
        {
            return 0;
        }
        for (final SourceElement child : sourceElement.getChildren())
        {
            if (child == sourceElement1)
            {
                return -1;
            }
            if (child == sourceElement2)
            {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public NodeIterator childIterator(
            final NodeTest test,
            final boolean reverse,
            final NodePointer startWith)
    {
        return new SourceElementNodeIterator(this, test, reverse, startWith);
    }

    @Override
    public NodeIterator attributeIterator(final QName qname)
    {
        return new SourceElementAttributeIterator(this, qname);
    }

    public boolean testSourceElement(final NodeTest test)
    {
        return testSourceElement(sourceElement, test);
    }

    /**
     * Test a Node.
     * @param sourceElement node to test
     * @param test to execute
     * @return true if node passes test
     */
    public static boolean testSourceElement(
            final SourceElement sourceElement,
            final NodeTest test)
    {
        if (test == null)
        {
            return true;
        }
        if (test instanceof NodeNameTest)
        {

            final NodeNameTest nodeNameTest = (NodeNameTest) test;
            final QName testName = nodeNameTest.getNodeName();
            final boolean wildcard = nodeNameTest.isWildcard();
            final String testPrefix = testName.getPrefix();
            if (wildcard && testPrefix == null)
            {
                return true;
            }
            if (wildcard || testName.getName().equals(sourceElement.getName()))
            {
                return true;
            }
            return false;
        }
        if (test instanceof NodeTypeTest)
        {
            if (((NodeTypeTest) test).getNodeType() == Compiler.NODE_TYPE_NODE)
            {
                return true;
            }
            return false;
        }
        return false;
    }

}
