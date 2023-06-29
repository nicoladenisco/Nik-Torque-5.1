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

import org.apache.commons.jxpath.JXPathBeanInfo;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.apache.commons.jxpath.ri.model.beans.NullPointer;

/**
 * Implements NodePointerFactory for JavaBeans.
 *
 * @author Dmitri Plotnikov
 * @author Thomas Fox
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class ModelNodeFactory implements NodePointerFactory
{

    /** factory order constant */
    public static final int MODEL_NODE_FACTORY_ORDER = 2;

    @Override
    public int getOrder()
    {
        return MODEL_NODE_FACTORY_ORDER;
    }

    @Override
    public NodePointer createNodePointer(
            final QName name,
            final Object bean,
            final Locale locale)
    {
        final JXPathBeanInfo bi
        = JXPathIntrospector.getBeanInfo(bean.getClass());
        return new ModelNodePointer(name, bean, bi, bean.getClass(), locale);
    }

    @Override
    public NodePointer createNodePointer(
            final NodePointer parent,
            final QName name,
            final Object bean)
    {
        if (bean == null)
        {
            return new NullPointer(parent, name);
        }

        final JXPathBeanInfo bi
        = JXPathIntrospector.getBeanInfo(bean.getClass());
        return new ModelNodePointer(parent, name, bean, bi, bean.getClass());
    }
}
