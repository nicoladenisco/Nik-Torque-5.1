package org.apache.torque.generator.source.transform.model;

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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.torque.generator.source.transform.SourceTransformerException;

/**
 * Accesses properties of java classes by reflection.
 *
 * @version $Id: $
 */
public class PropertyAccess
{
    /** The automatic converters used for properties. */
    private static List<TypeConverter> converters;

    static
    {
        converters = new ArrayList<>();
        converters.add(new StringToBooleanConverter());
    }

    /** The object on which the property should be accessed, not null. */
    private final Object target;

    /** The name of the property to access, not null. */
    private final String propertyName;

    /**
     * List of all possible prefixes for field names.
     * I.e. if the property name is "name", "prefix" + "name" is also tried
     * as field name if "prefix" is contained in this list.
     */
    private List<String> prefixList;

    /**
     * List of all possible suffixes for field and method names.
     * I.e. if the property name is "name", "name" + "suffix" is also tried
     * as field name and base method name if "suffix" is contained in this list.
     */
    private List<String> suffixList;

    /**
     * The public field corresponding to the property name,
     * or null if no such field exists.
     */
    private Field field;

    /**
     * The public write method corresponding to the property name,
     * or null if a field with the property name exists
     * or if no such write method exists.
     */
    private Method writeMethod;

    /**
     * The public read method corresponding to the property name,
     * or null if a field with the property name exists
     * or if no such read method exists.
     */
    private Method readMethod;

    /**
     * Constructs a reflection access to a property of an object.
     * The property is assumed to be a public field, or if no such field exists
     * on the target object, by getters and setters using the java beans
     * naming conventions.
     * The searched field and method suffixes are "s", "Array", "List",
     * the searched field prefixes are "_".
     * It is not an error to call this constructor on fields which do not exist.
     *
     * @param target the target object, not null.
     * @param propertyName the name of the property, not null.
     *
     * @throws SourceTransformerException if reflection access to fields
     *         or methods fails.
     * @throws NullPointerException if target or propertyName are null.
     */
    public PropertyAccess(
            final Object target,
            final String propertyName)
                    throws SourceTransformerException
    {
        this(target,
                propertyName,
                Arrays.asList(new String[] {"_"}),
                Arrays.asList(new String[] {"s", "Array", "List"}));
    }

    /**
     * Constructs a reflection access to a property of an object.
     * The property is assumed to be a public field, or if no such field exists
     * on the target object, by getters and setters using the java beans
     * naming conventions.
     * As second order preference, the suffix list is worked first,
     * if no match is found there the prefix list is tried.
     * It is not an error to call this constructor on fields which do not exist.
     *
     * @param target the target object, not null.
     * @param propertyName the name of the property, not null.
     * @param prefixList List of all possible prefixes for field names.
     *        I.e. if the property name is "name",
     *        "prefix" + "name" is also tried as field name
     *        if "prefix" is contained in this list.
     * @param suffixList List of all possible suffixes for field and method
     *        names.  I.e. if the property name is "name",
     *        "name" + "suffix" is also tried as field name and base method name
     *        if "suffix" is contained in this list.
     *
     * @throws SourceTransformerException if reflection access to fields
     *         or methods fails.
     * @throws NullPointerException if target or propertyName are null.
     */
    public PropertyAccess(
            final Object target,
            final String propertyName,
            final List<String> prefixList,
            final List<String> suffixList)
                    throws SourceTransformerException
    {
        if (target == null)
        {
            throw new NullPointerException("target must not be null");
        }
        if (propertyName == null)
        {
            throw new NullPointerException("propertyName must not be null");
        }
        this.target = target;
        this.propertyName = propertyName;
        if (prefixList != null)
        {
            this.prefixList = new ArrayList<>(prefixList);
        }
        else
        {
            this.prefixList = new ArrayList<>();
        }
        if (suffixList != null)
        {
            this.suffixList = new ArrayList<>(suffixList);
        }
        else
        {
            this.suffixList = new ArrayList<>();
        }
        this.suffixList.add(0, "");

        for (final String suffix : this.suffixList)
        {
            this.field = determinePublicField(
                    target,
                    propertyName + suffix);
            if (field != null)
            {
                break;
            }
        }
        if (this.field == null)
        {
            for (final String prefix : this.prefixList)
            {
                this.field = determinePublicField(
                        target,
                        prefix + propertyName);
                if (field != null)
                {
                    break;
                }
            }
        }
        if (this.field == null)
        {
            PropertyDescriptor propertyDescriptor = null;
            for (final String suffix : this.suffixList)
            {
                propertyDescriptor = determinePropertyDescriptor(
                        target,
                        propertyName + suffix);
                if (propertyDescriptor != null)
                {
                    break;
                }
            }
            if (propertyDescriptor != null)
            {
                this.readMethod = propertyDescriptor.getReadMethod();
                this.writeMethod = propertyDescriptor.getWriteMethod();
            }
        }
    }

    /**
     * Returns the public field of the class of the object <code>target</code>
     * with name <code>name</code>.
     *
     * @param target the target object, not null.
     * @param name the name of the field, not null.
     * @return the field if a public field with the given name exists,
     *         or null if no public field with the given name exists.
     *
     * @throws SourceTransformerException if security settings
     *         prevent reading the field of the object.
     */
    private static Field determinePublicField(final Object target, final String name)
            throws SourceTransformerException
    {
        try
        {
            final Field fieldCandidate = target.getClass().getField(name);
            if (Modifier.isPublic(fieldCandidate.getModifiers()))
            {
                return fieldCandidate;
            }
        }
        catch (final SecurityException e)
        {
            throw createSetFieldException(
                    target,
                    name,
                    null,
                    " because access is denied to the field or package",
                    e);
        }
        catch (final NoSuchFieldException e)
        {
            // do nothing, field does not exist
        }
        return null;
    }

    /**
     * Returns the property descriptor of the class of the object
     * <code>target</code> with name <code>name</code>.
     *
     * @param target the target object, not null.
     * @param name the name of the field, not null.
     * @return the field if a public getter or setter corresponding to
     *         the property name exists, or null if no such getter
     *         and setter exists.
     *
     * @throws SourceTransformerException if security settings
     *         prevent reading the field of the object.
     */
    private static PropertyDescriptor determinePropertyDescriptor(
            final Object target,
            final String name)
                    throws SourceTransformerException
    {
        try
        {
            final PropertyDescriptor propertyDescriptor
            = PropertyUtils.getPropertyDescriptor(target, name);
            return propertyDescriptor;
        }
        catch (final NoSuchMethodException e)
        {
            throw new SourceTransformerException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new SourceTransformerException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new SourceTransformerException(e);
        }
    }

    /**
     * Sets the property on the target object to a given value.
     * If the property is an array and value is not an array, value is set
     * as a member of the array.
     * If the property is a collection and value is not a collection,
     * value is added to the collection.
     * In all other cases, the field is set to the value.
     *
     * @param value the value to set the property to.
     *
     * @throws SourceTransformerException if the property cannot be set.
     *         Common reasons for such an exception are that the property
     *         does not exist at all, or is not writeable, or value has the
     *         wrong class.
     */
    public void setProperty(final Object value)
            throws SourceTransformerException
    {
        if (field != null)
        {
            if (field.getType().isArray()
                    && (value == null || !value.getClass().isArray()))
            {
                setMemberinArrayField(value);
            }
            else if (Collection.class.isAssignableFrom(field.getType())
                    && (value == null
                    || !Collection.class.isAssignableFrom(value.getClass())))
            {
                setMemberinCollectionProperty(value);
            }
            else
            {
                setPropertyStrictUsingField(value);
            }
            return;
        }
        else if (writeMethod != null)
        {
            if (writeMethod.getParameterTypes()[0].isArray()
                    && (value == null || !value.getClass().isArray()))
            {
                setMemberinArrayField(value);
            }
            else if (Collection.class.isAssignableFrom(
                    writeMethod.getParameterTypes()[0])
                    && (value == null
                    || !Collection.class.isAssignableFrom(value.getClass())))
            {
                setMemberinCollectionProperty(value);
            }
            else
            {
                setPropertyStrictUsingSetter(value);
            }
            return;
        }
        else if (readMethod != null)
        {
            if (Collection.class.isAssignableFrom(
                    readMethod.getReturnType())
                    && (value == null
                    || !Collection.class.isAssignableFrom(value.getClass())))
            {
                setMemberinCollectionProperty(value);
                return;
            }
            throw new PropertyNotWriteableException(target, propertyName);
        }
        else
        {
            throw new NoSuchPropertyException(
                    target,
                    propertyName,
                    prefixList,
                    suffixList);
        }
    }

    /**
     * Sets the property on the target object to a given value.
     * Arrays and Lists are not treated specially.
     *
     * @param value the value to set the property to.
     *
     * @throws SourceTransformerException if the property cannot be set.
     *         Common reasons for such an exception are that the property
     *         does not exist at all, or is not writeable, or value has the
     *         wrong class.
     */
    public void setPropertyStrict(final Object value)
            throws SourceTransformerException
    {
        if (field != null)
        {
            setPropertyStrictUsingField(value);
            return;
        }
        else if (writeMethod != null)
        {
            setPropertyStrictUsingSetter(value);
            return;
        }
        else if (readMethod != null)
        {
            throw new PropertyNotWriteableException(target, propertyName);
        }
        else
        {
            throw new NoSuchPropertyException(
                    target,
                    propertyName,
                    prefixList,
                    suffixList);
        }
    }

    /**
     * Sets the field on the target object to a given value.
     * Arrays and Lists are not treated specially.
     * This method should only be called if the member field is not null.
     *
     * @param value the value to set the property to.
     *
     * @throws SourceTransformerException if the property cannot be set.
     *         A common reason is that value has the wrong class.
     */
    private void setPropertyStrictUsingField(Object value)
            throws SourceTransformerException
    {
        for (final TypeConverter converter : converters)
        {
            if (converter.accept(value, field.getType()))
            {
                value = converter.convert(value, field.getType());
                break;
            }
        }
        try
        {
            field.set(target, value);
        }
        catch (final IllegalArgumentException e)
        {
            if (value == null)
            {
                throw createSetFieldException(
                        null,
                        " because the value is null which is not allowed",
                        e);

            }
            throw createSetFieldException(
                    value,
                    " because the argument has the wrong type "
                            + value.getClass().getName() ,
                            e);
        }
        catch (final IllegalAccessException e)
        {
            throw createSetFieldException(
                    null,
                    " because the field cannot be accessed",
                    e);
        }
    }

    /**
     * Sets the property on the target object to a given value using the setter
     * method.
     * Arrays and Lists are not treated specially.
     * This method should only be called if the writeMethod field is not null.
     *
     * @param value the value to set the property to.
     *
     * @throws SourceTransformerException if the property cannot be set.
     *         A common reason is that value has the wrong class.
     */
    private void setPropertyStrictUsingSetter(Object value)
            throws SourceTransformerException
    {
        for (final TypeConverter converter : converters)
        {
            if (converter.accept(value, writeMethod.getParameterTypes()[0]))
            {
                value = converter.convert(
                        value,
                        writeMethod.getParameterTypes()[0]);
                break;
            }
        }
        try
        {
            writeMethod.invoke(target, value);
            return;
        }
        catch (final IllegalArgumentException e)
        {
            if (value == null)
            {
                throw createSetFieldException(
                        null,
                        " because the value is null which is not allowed",
                        e);

            }
            throw createSetFieldException(
                    value,
                    " because the argument has the wrong type "
                            + value.getClass().getName() ,
                            e);
        }
        catch (final IllegalAccessException e)
        {
            throw new SourceTransformerException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new SourceTransformerException(e);
        }
    }

    /**
     * Returns the value of the property.
     *
     * @return the value of the property.
     *
     * @throws SourceTransformerException if the property is not readable.
     *         A common reason for this is that no public field and no getter
     *         exists with the given name.
     */
    public Object getProperty() throws SourceTransformerException
    {
        if (field != null)
        {
            return getPropertyUsingField();
        }
        else if (readMethod != null)
        {
            return getPropertyUsingGetter();
        }
        else
        {
            throw new PropertyNotReadableException(target, propertyName);
        }
    }

    /**
     * Returns the value of the property by accessing the field of the target.
     *
     * @return the value of the property.
     *
     * @throws SourceTransformerException if the property is not readable.
     *         A common reason for this is that no public field exists
     *         with the given name.
     */
    private Object getPropertyUsingField() throws SourceTransformerException
    {
        try
        {
            return field.get(target);
        }
        catch (final IllegalArgumentException e)
        {
            throw new SourceTransformerException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new SourceTransformerException(e);
        }
    }

    /**
     * Returns the value of the property by accessing the getter method
     * of the property.
     *
     * @return the value of the property.
     *
     * @throws SourceTransformerException if the property is not readable.
     *         A common reason for this is that no public getter exists
     *         with the corresponding name.
     */
    private Object getPropertyUsingGetter() throws SourceTransformerException
    {
        try
        {
            return readMethod.invoke(target);
        }
        catch (final IllegalArgumentException e)
        {
            throw new SourceTransformerException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new SourceTransformerException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new SourceTransformerException(e);
        }
    }

    public boolean isPropertyAccessible()
    {
        return field != null || readMethod != null || writeMethod != null;
    }

    /**
     * Returns the class of the property.
     *
     * @return the class of the property,
     *         or null if the property does not exist.
     */
    public Class<?> getPropertyType()
    {
        if (field != null)
        {
            return field.getType();
        }
        if (writeMethod != null)
        {
            return writeMethod.getParameterTypes()[0];
        }
        if (readMethod != null)
        {
            return readMethod.getReturnType();
        }
        return null;
    }

    /**
     * Returns the generic type of the property.
     *
     * @return the generic type of the property,
     *         or null if the property does not exist.
     */
    public Type getPropertyGenericType()
    {
        if (field != null)
        {
            return field.getGenericType();
        }
        if (writeMethod != null)
        {
            return writeMethod.getGenericParameterTypes()[0];
        }
        if (readMethod != null)
        {
            return readMethod.getGenericReturnType();
        }
        return null;
    }

    /**
     * Gets the class of the first generic argument of the property.
     * E.g. if the property is a collection, this method will return
     * the class of the members of the collection.
     *
     * @return the class of the first generic argument of the property,
     *         or null if the property is not generified.
     */
    public Class<?> getFirstGenericTypeArgument()
    {
        final Type type = getPropertyGenericType();
        if (type instanceof ParameterizedType)
        {
            final Object firstType
            = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (firstType instanceof Class)
            {
                return (Class<?>) firstType;
            }
        }
        return null;
    }

    /**
     * Returns the list of all possible prefixes for field names.
     * I.e. if the property name is "name", "prefix" + "name" is also tried
     * as field name if "prefix" is contained in this list.
     *
     * @return the prefix list, not null.
     */
    public List<String> getPrefixList()
    {
        return Collections.unmodifiableList(prefixList);
    }

    /**
     * Returns the list of all possible suffixes for field and method names.
     * I.e. if the property name is "name", "name" + "suffix" is also tried
     * as field name and base method name if "suffix" is contained in this list.
     *
     * @return the suffix list with added "" as first entry, not null.
     */
    public List<String> getSuffixList()
    {
        return Collections.unmodifiableList(suffixList);
    }

    /**
     * Adds value as a member of an array to the property of the target object.
     * This method should only be called ifthe property is an array.
     *
     * @param value the value to add to the array.
     *
     * @throws SourceTransformerException if adding the value fails.
     *         A common reason is that value has the wrong class.
     */
    private void setMemberinArrayField(final Object value)
            throws SourceTransformerException
    {
        final Object[] oldContent = (Object[]) getProperty();
        int newIndex;
        Object newContent;
        if (oldContent == null)
        {
            newContent = Array.newInstance(
                    getPropertyType().getComponentType(),
                    1);
            newIndex = 0;
        }
        else
        {
            newContent = Array.newInstance(
                    getPropertyType().getComponentType(),
                    oldContent.length + 1);
            System.arraycopy(oldContent, 0, newContent, 0, oldContent.length);
            newIndex = oldContent.length;
        }
        ((Object[]) newContent)[newIndex] = value;
        setPropertyStrict(newContent);
    }

    /**
     * Adds value as a member of an collection to the property of the target
     * object.
     * This method should only be called if the property is a collection.
     *
     * @param value the value to add to the array.
     *
     * @throws SourceTransformerException if adding the value fails.
     *         A common reason is that value has the wrong class.
     */
    private void setMemberinCollectionProperty(final Object value)
            throws SourceTransformerException
    {
        @SuppressWarnings("unchecked")
        Collection<Object> content = (Collection<Object>) getProperty();
        if (content == null)
        {
            content = getCollectionInstance();
            setPropertyStrict(content);
        }
        content.add(value);
    }

    /**
     * Returns the collection instance to use if a member is added
     * to a collection property which is still null.
     * If the property is of type Collection or List, an instance of ArrayList
     * will be returned.
     * If the property is of type Set, an instance of HashSet will be returned.
     * If the property is of type Queue, an instance of LinkedList will be
     * returned.
     * In all other cases the class of the collection will be instantiated.
     *
     * @return the collection instance to use, not null.
     *
     * @throws SourceTransformerException if instantiation fails,
     *         e.g. if the collection is an unknown abstract collection.
     */
    private Collection<Object> getCollectionInstance()
            throws SourceTransformerException
    {
        final Class<?> type = getPropertyType();
        if (Collection.class == type || List.class == type)
        {
            return new ArrayList<>();
        }
        else if (Set.class == type)
        {
            return new HashSet<>();
        }
        else if (Queue.class == type)
        {
            return new LinkedList<>();
        }
        else
        {
            try
            {
                @SuppressWarnings("unchecked")
                final Collection<Object> result
                = (Collection<Object>) type.newInstance();
                return result;
            }
            catch (final InstantiationException e)
            {
                throw new SourceTransformerException(e);
            }
            catch (final IllegalAccessException e)
            {
                throw new SourceTransformerException(e);
            }
        }
    }

    /**
     * Constructs a SourceTransformerException when setting a field fails.
     *
     * @param value the value to which the field was set.
     * @param reason the reason why setting the field failed.
     * @param cause the root cause of the exception.
     *
     * @return An appropriate SourceTransformerException to throw.
     */
    private SourceTransformerException createSetFieldException(
            final Object value,
            final String reason,
            final Throwable cause)
    {
        return createSetFieldException(
                target,
                propertyName,
                value,
                reason,
                cause);
    }

    /**
     * Constructs a SourceTransformerException when setting a field fails.
     *
     * @param target the target object on which the property was set.
     * @param propertyName the name of the property which was set.
     * @param value the value to which the field was set.
     * @param reason the reason why setting the field failed.
     * @param cause the root cause of the exception.
     *
     * @return An appropriate SourceTransformerException to throw.
     */
    private static SourceTransformerException createSetFieldException(
            final Object target,
            final String propertyName,
            final Object value,
            final String reason,
            final Throwable cause)
    {
        final StringBuilder message = new StringBuilder("The field ")
                .append(propertyName)
                .append(" of class ")
                .append(target.getClass().getName());
        if (value != null)
        {
            message.append(" cannot be set to ").append(value);
        }
        message.append(reason);
        return new SourceTransformerException(message.toString(), cause);
    }
}
