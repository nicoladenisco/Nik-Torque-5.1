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


import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * A node iterator iterating over children of a SourceElement.
 *
 * @author Dmitri Plotnikov
 * @author Thomas Fox
 *
 * @version $Id: $
 */
public class SourceElementNodeIterator implements NodeIterator
{
    /** The parent pointer. */
    private final NodePointer parent;

    /** The node test which the selected children must match. */
    private final NodeTest nodeTest;

    /** The source element which is the parent of the nodes iterated over. */
    private final SourceElement sourceElement;

    /** The current matching child in the iteration. */
    private SourceElement child = null;

    /** Whether order is reverse. */
    private final boolean reverse;

    /** The current iterator position. */
    private int position = 0;

    /**
     * Create a new DOMNodeIterator.
     * @param parent parent pointer
     * @param nodeTest test
     * @param reverse whether to iterate in reverse
     * @param startWith starting pointer
     */
    public SourceElementNodeIterator(
            final NodePointer parent,
            final NodeTest nodeTest,
            final boolean reverse,
            final NodePointer startWith)
    {
        this.parent = parent;
        this.sourceElement = (SourceElement) parent.getNode();
        if (startWith != null)
        {
            this.child = (SourceElement) startWith.getNode();
        }
        this.nodeTest = nodeTest;
        this.reverse = reverse;
    }

    @Override
    public NodePointer getNodePointer()
    {
        if (position == 0)
        {
            setPosition(1);
        }
        if (child != null)
        {
            NodePointer result = new SourceElementNodePointer(parent, child);
            result.setIndex(position - 1);
            return result;
        }
        return null;
    }

    @Override
    public int getPosition()
    {
        return position;
    }

    @Override
    public boolean setPosition(final int position)
    {
        while (this.position < position)
        {
            if (!next())
            {
                return false;
            }
        }
        while (this.position > position)
        {
            if (!previous())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Set the previous position.
     * @return whether valid
     */
    private boolean previous()
    {
        position--;
        if (!reverse)
        {
            if (position == 0)
            {
                child = null;
            }
            else if (child == null)
            {
                child = sourceElement.getLastChild();
            }
            else
            {
                child = child.getPrecedingSourceElement(sourceElement);
            }
            while (child != null && !testChild())
            {
                child = child.getPrecedingSourceElement(sourceElement);
            }
        }
        else
        {
            child = child.getFollowingSourceElement(sourceElement);
            while (child != null && !testChild())
            {
                child = child.getFollowingSourceElement(sourceElement);
            }
        }
        return child != null;
    }

    /**
     * Set the next position.
     * @return whether valid
     */
    private boolean next()
    {
        position++;
        if (!reverse)
        {
            if (position == 1)
            {
                if (child == null)
                {
                    child = sourceElement.getFirstChild();
                }
                else
                {
                    child = child.getFollowingSourceElement(sourceElement);
                }
            }
            else
            {
                child = child.getFollowingSourceElement(sourceElement);
            }
            while (child != null && !testChild())
            {
                child = child.getFollowingSourceElement(sourceElement);
            }
        }
        else
        {
            if (position == 1)
            {
                if (child == null)
                {
                    child = sourceElement.getLastChild();
                }
                else
                {
                    child = sourceElement.getPrecedingSourceElement(sourceElement);
                }
            }
            else
            {
                child = sourceElement.getPrecedingSourceElement(sourceElement);
            }
            while (child != null && !testChild())
            {
                child = child.getPrecedingSourceElement(sourceElement);
            }
        }
        return child != null;
    }

    /**
     * Test child.
     * @return result of the test
     */
    private boolean testChild()
    {
        return SourceElementNodePointer.testSourceElement(child, nodeTest);
    }
}
