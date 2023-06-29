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

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;

/**
 * A node pointer factory for SourceElements.
 *
 * @version $Id: $
 */
public class SourceElementNodePointerFactory implements NodePointerFactory
{
    @Override
    public int getOrder()
    {
        return 1;
    }

    @Override
    public NodePointer createNodePointer(
            final QName name,
            final Object object,
            final Locale locale)
    {
        return object instanceof SourceElement
                ? new SourceElementNodePointer((SourceElement) object, locale)
                        : null;
    }

    @Override
    public NodePointer createNodePointer(
            final NodePointer parent,
            final QName name,
            final Object object)
    {
        return object instanceof SourceElement
                ? new SourceElementNodePointer(parent, (SourceElement) object)
                        : null;
    }

}
