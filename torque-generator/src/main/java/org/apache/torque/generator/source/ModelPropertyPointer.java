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

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.jxpath.JXPathBeanInfo;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.JXPathInvalidAccessException;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Pointer pointing to a property or field of a JavaBean.
 *
 * @author Dmitri Plotnikov
 * @author Thomas Fox
 * @version $Id: $
 */
public class ModelPropertyPointer extends PropertyPointer
{
    /** Serial Verion UID. */
    private static final long serialVersionUID = 1L;

    /** The object which signifies that a field is not initialized. */
    private static final Object UNINITIALIZED = new Object();

    /** The name of the property or field pointed to. */
    private String name;

    /** The JavaBean properties of the base object. */
    private final JXPathBeanInfo beanInfo;

    /** The class of the base object. */
    private final Class<?> valueClass;

    /** The base value of the object (which properties should be accessed). */
    private Object baseValue = UNINITIALIZED;

    /** The current value pointed to. */
    private Object value = UNINITIALIZED;

    /** The names of all JavaBean properties and fields of the base object. */
    private transient String[] propertyNames;

    /** The descriptors of all JavaBean properties of the base object. */
    private transient PropertyDescriptor[] propertyDescriptors;

    /** The descriptor of the current property pointed to. */
    private transient PropertyDescriptor propertyDescriptor;

    /** The descriptor of the current field pointed to. */
    private transient Field field;

    /**
     * Create a new BeanPropertyPointer.
     * @param parent parent pointer
     * @param beanInfo describes the target property/ies.
     * @param valueClass the class of the base object
     */
    public ModelPropertyPointer(
            final NodePointer parent,
            final JXPathBeanInfo beanInfo,
            final Class<?> valueClass)
    {
        super(parent);
        this.beanInfo = beanInfo;
        this.valueClass = valueClass;
    }

    /**
     * This type of node is auxiliary.
     * @return true
     */
    @Override
    public boolean isContainer()
    {
        return true;
    }

    @Override
    public int getPropertyCount()
    {
        if (beanInfo.isAtomic())
        {
            return 0;
        }
        return getPropertyNames().length;
    }

    /**
     * Get the names of all properties, sorted alphabetically.
     *
     * @return the names of all properties, not null.
     */
    @Override
    public String[] getPropertyNames()
    {
        if (propertyNames == null)
        {
            final Set<String> names = new TreeSet<>();
            for (final PropertyDescriptor pd : beanInfo.getPropertyDescriptors())
            {
                names.add(pd.getName());
            }
            for (final Field f : valueClass.getFields())
            {
                if (Modifier.isPublic(f.getModifiers()))
                {
                    names.add(f.getName());
                }
            }
            propertyNames = names.toArray(new String[] {});
        }
        return propertyNames;
    }

    /**
     * Select a property by name.
     * @param name String name
     */
    @Override
    public void setPropertyName(final String name)
    {
        setPropertyIndex(UNSPECIFIED_PROPERTY);
        this.name = name;
    }

    /**
     * Selects a property by its offset in the alphabetically sorted list.
     * @param index property index
     */
    @Override
    public void setPropertyIndex(final int index)
    {
        if (this.propertyIndex != index)
        {
            super.setPropertyIndex(index);
            name = null;
            propertyDescriptor = null;
            field = null;
            baseValue = UNINITIALIZED;
            value = UNINITIALIZED;
        }
    }

    /**
     * Get the value of the currently selected property.
     * @return Object value
     */
    @Override
    public Object getBaseValue()
    {
        if (baseValue == UNINITIALIZED)
        {
            final PropertyDescriptor pd = getPropertyDescriptor();
            if (pd == null)
            {
                final Field f = getField();
                if (f == null)
                {
                    return null;
                }
                baseValue = getFieldValue(f);
            }
            else
            {
                baseValue = ValueUtils.getValue(getBean(), pd);
            }
        }
        return baseValue;
    }

    @Override
    public void setIndex(final int index)
    {
        if (this.index == index)
        {
            return;
        }
        // When dealing with a scalar, index == 0 is equivalent to
        // WHOLE_COLLECTION, so do not change it.
        if (this.index != WHOLE_COLLECTION
                || index != 0
                || isCollection())
        {
            super.setIndex(index);
            value = UNINITIALIZED;
        }
    }

    /**
     * If index == WHOLE_COLLECTION, the value of the property, otherwise
     * the value of the index'th element of the collection represented by the
     * property. If the property is not a collection, index should be zero
     * and the value will be the property itself.
     * @return Object
     */
    @Override
    public Object getImmediateNode()
    {
        if (value == UNINITIALIZED)
        {
            if (index == WHOLE_COLLECTION)
            {
                value = ValueUtils.getValue(getBaseValue());
            }
            else
            {
                final PropertyDescriptor pd = getPropertyDescriptor();
                if (pd == null)
                {
                    final Field f = getField();
                    if (f == null)
                    {
                        value = null;
                    }
                    else
                    {
                        value = ValueUtils.getValue(getFieldValue(f), index);
                    }
                }
                else
                {
                    value = ValueUtils.getValue(getBean(), pd, index);
                }
            }
        }
        return value;
    }

    @Override
    protected boolean isActualProperty()
    {
        return getPropertyDescriptor() != null || getField() != null;
    }

    @Override
    public boolean isCollection()
    {
        final PropertyDescriptor pd = getPropertyDescriptor();
        final int hint;
        if (pd == null)
        {
            final Field f = getField();
            if (f == null)
            {
                return false;
            }
            hint = ValueUtils.getCollectionHint(field.getType());
        }
        else
        {
            if (pd instanceof IndexedPropertyDescriptor)
            {
                return true;
            }
            hint = ValueUtils.getCollectionHint(pd.getPropertyType());
        }
        if (hint == -1)
        {
            return false;
        }
        if (hint == 1)
        {
            return true;
        }

        final Object v = getBaseValue();
        return v != null && ValueUtils.isCollection(v);
    }

    /**
     * If the property contains a collection, then the length of that
     * collection, otherwise - 1.
     * @return int length
     */
    @Override
    public int getLength()
    {
        final PropertyDescriptor pd = getPropertyDescriptor();
        final int hint;
        if (pd == null)
        {
            final Field f = getField();
            if (f == null)
            {
                return 1;
            }
            hint = ValueUtils.getCollectionHint(f.getType());
        }
        else
        {
            if (pd instanceof IndexedPropertyDescriptor)
            {
                return ValueUtils.getIndexedPropertyLength(
                        getBean(),
                        (IndexedPropertyDescriptor) pd);
            }
            hint = ValueUtils.getCollectionHint(pd.getPropertyType());
        }

        if (hint == -1)
        {
            return 1;
        }
        return ValueUtils.getLength(getBaseValue());
    }

    /**
     * If index == WHOLE_COLLECTION, change the value of the property, otherwise
     * change the value of the index'th element of the collection
     * represented by the property.
     * @param value value to set
     */
    @Override
    public void setValue(final Object value)
    {
        final PropertyDescriptor pd = getPropertyDescriptor();
        Field f;
        if (pd == null)
        {
            f = getField();
            if (f == null)
            {
                throw new JXPathInvalidAccessException(
                        "Cannot set property: " + asPath() + " - no such property");
            }
            setFieldValue(f, value);
        }
        else
        {
            if (index == WHOLE_COLLECTION)
            {
                ValueUtils.setValue(getBean(), pd, value);
            }
            else
            {
                ValueUtils.setValue(getBean(), pd, index, value);
            }
        }
        this.value = value;
    }

    @Override
    public NodePointer createPath(final JXPathContext context)
    {
        if (getImmediateNode() == null)
        {
            super.createPath(context);
            baseValue = UNINITIALIZED;
            value = UNINITIALIZED;
        }
        return this;
    }

    @Override
    public void remove()
    {
        if (index == WHOLE_COLLECTION)
        {
            setValue(null);
        }
        else if (isCollection())
        {
            final Object o = getBaseValue();
            final Object collection = ValueUtils.remove(getBaseValue(), index);
            if (collection != o)
            {
                final PropertyDescriptor pd = getPropertyDescriptor();
                if (pd == null)
                {
                    final Field f = getField();
                    setFieldValue(f, collection);
                }
                else
                {
                    ValueUtils.setValue(
                            getBean(),
                            getPropertyDescriptor(),
                            collection);
                }
            }
        }
        else if (index == 0)
        {
            index = WHOLE_COLLECTION;
            setValue(null);
        }
    }

    /**
     * Get the name of the currently selected property.
     * @return String property name
     */
    @Override
    public String getPropertyName()
    {
        if (name == null)
        {
            final PropertyDescriptor pd = getPropertyDescriptor();
            if (pd != null)
            {
                name = pd.getName();
            }
            else
            {
                final Field f = getField();
                if (f != null)
                {
                    name = f.getName();
                }
            }
        }
        return name != null ? name : "*";
    }

    /**
     * Finds the property descriptor corresponding to the current property
     * index.
     * @return PropertyDescriptor
     */
    private PropertyDescriptor getPropertyDescriptor()
    {
        if (propertyDescriptor == null)
        {
            final int inx = getPropertyIndex();
            if (inx == UNSPECIFIED_PROPERTY)
            {
                propertyDescriptor =
                        beanInfo.getPropertyDescriptor(name);
            }
            else
            {
                final String[] names = getPropertyNames();
                if (inx >= 0 && inx < names.length)
                {
                    propertyDescriptor
                    = beanInfo.getPropertyDescriptor(names[inx]);
                }
                else
                {
                    propertyDescriptor = null;
                }
            }
        }
        return propertyDescriptor;
    }

    /**
     * Finds the property descriptor corresponding to the current property
     * index.
     * @return PropertyDescriptor
     */
    private Field getField()
    {
        if (field == null)
        {
            final int inx = getPropertyIndex();
            if (inx == UNSPECIFIED_PROPERTY)
            {
                field = getField(name);
            }
            else
            {
                final String[] names = getPropertyNames();
                if (inx >= 0 && inx < names.length)
                {
                    field = getField(names[inx]);
                }
                else
                {
                    field = null;
                }
            }
        }
        return field;
    }

    private Field getField(final String name)
    {
        try
        {
            return valueClass.getField(name);
        }
        catch (final SecurityException e)
        {
            throw new JXPathException(
                    "Cannot access property: "
                            + valueClass.getName()
                            + "."
                            + name,
                            e);
        }
        catch (final NoSuchFieldException e)
        {
            throw new JXPathException(
                    "Cannot access property: "
                            + valueClass.getName()
                            + "."
                            + name,
                            e);
        }
    }

    private Object getFieldValue(final Field field)
    {
        try
        {
            return field.get(getBean());
        }
        catch (final IllegalArgumentException e)
        {
            throw new JXPathException(
                    "Cannot access property: "
                            + (bean == null ? "null" : bean.getClass().getName())
                            + "."
                            + name,
                            e);
        }
        catch (final IllegalAccessException e)
        {
            throw new JXPathException(
                    "Cannot access property: "
                            + (bean == null ? "null" : bean.getClass().getName())
                            + "."
                            + name,
                            e);

        }
    }

    private void setFieldValue(final Field field, final Object value)
    {
        try
        {
            field.set(getBean(), value);
        }
        catch (final IllegalArgumentException e)
        {
            throw new JXPathInvalidAccessException(
                    "Cannot set property: " + asPath(), e);
        }
        catch (final IllegalAccessException e)
        {
            throw new JXPathInvalidAccessException(
                    "Cannot set property: " + asPath(), e);
        }
    }

    /**
     * Get all PropertyDescriptors.
     * @return PropertyDescriptor[]
     */
    protected synchronized PropertyDescriptor[] getPropertyDescriptors()
    {
        if (propertyDescriptors == null)
        {
            propertyDescriptors = beanInfo.getPropertyDescriptors();
        }
        return propertyDescriptors;
    }

}
