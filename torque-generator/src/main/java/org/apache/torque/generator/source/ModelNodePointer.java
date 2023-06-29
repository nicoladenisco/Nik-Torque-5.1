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
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.BeanPointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;

/**
 * A Pointer that points to a Java Object or a collection. It is either
 * the first element of a path or a pointer for a property value.
 *
 * @author Dmitri Plotnikov
 * @author Thomas Fox
 *
 * @version $Id: $
 */
public class ModelNodePointer extends BeanPointer
{
    /** SerialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The JavaBean properties of the object pointed to. */
    private final JXPathBeanInfo beanInfo;

    /** The class of the object pointed to. */
    private final Class<?> beanClass;

    /**
     * Create a new BeanPointer.
     * @param name is the name given to the first node
     * @param bean pointed
     * @param beanInfo JXPathBeanInfo
     * @param beanClass the class of the bean
     * @param locale Locale
     */
    public ModelNodePointer(
            final QName name,
            final Object bean,
            final JXPathBeanInfo beanInfo,
            final Class<?> beanClass,
            final Locale locale)
    {
        super(name, bean, beanInfo, locale);
        this.beanInfo = beanInfo;
        this.beanClass = beanClass;
    }

    /**
     * Create a new BeanPointer.
     * @param parent pointer
     * @param name is the name given to the first node
     * @param bean pointed
     * @param beanInfo JXPathBeanInfo
     * @param beanClass the class of the bean
     */
    public ModelNodePointer(
            final NodePointer parent,
            final QName name,
            final Object bean,
            final JXPathBeanInfo beanInfo,
            final Class<?> beanClass)
    {
        super(parent, name, bean, beanInfo);
        this.beanInfo = beanInfo;
        this.beanClass = beanClass;
    }

    @Override
    public PropertyPointer getPropertyPointer()
    {
        return new ModelPropertyPointer(this, beanInfo, beanClass);
    }

    @Override
    public int hashCode()
    {
        return getName() == null ? 0 : getName().hashCode();
    }

    @Override
    public boolean equals(final Object object)
    {
        if (object == this)
        {
            return true;
        }

        if (!(object instanceof ModelNodePointer))
        {
            return false;
        }

        return super.equals(object);
    }
}
