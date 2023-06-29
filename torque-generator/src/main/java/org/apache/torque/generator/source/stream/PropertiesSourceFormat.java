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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;

/**
 * The source type representing an properties source.
 *
 * $Id: PropertiesSourceFormat.java 1855923 2019-03-20 16:19:39Z gk $
 */
public final class PropertiesSourceFormat implements StreamSourceFormat
{
    /** The key for a properties source. */
    private static final String KEY = "properties";

    /** The filename extension for a properties source. */
    private static final String FILENAME_EXTENSION = "properties";

    /**
     * The name of the root element.
     */
    public static final String ROOT_ELEMENT_NAME = "properties";

    /**
     * The name of the entry element.
     */
    public static final String ENTRY_ELEMENT_NAME = "entry";

    /**
     * The name of the key attribute of the entry element.
     */
    public static final String KEY_ATTRIBUTE_NAME = "key";

    /**
     * Returns an unique key for the source type.
     *
     * @return "properties".
     */
    @Override
    public String getKey()
    {
        return KEY;
    }

    /**
     * Gets the filename extension this source type typically has.
     *
     * @return "properties".
     */
    @Override
    public String getFilenameExtension()
    {
        return FILENAME_EXTENSION;
    }

    /**
     * Parses a Stream in Properties format and returns the root element of the
     * created element tree.
     *
     * @param inputStream the input stream to parse, not null.
     * @param controllerState the controller state.
     *
     * @return the root element of the created tree, not null.
     *
     * @throws SourceException if an error occurred when reading from the
     *         input stream.
     * @throws IllegalArgumentException if the input stream contains a
     *         malformed unicode escape sequence.
     * @throws NullPointerException if inputStream is null.
     */
    @Override
    public SourceElement parse(
            InputStream inputStream,
            ControllerState controllerState)
                    throws SourceException
    {
        if (inputStream == null)
        {
            throw new NullPointerException("inputStream is null");
        }
        OrderedProperties properties = new OrderedProperties();
        try
        {
            properties.load(inputStream);
        }
        catch (IOException e)
        {
            throw new SourceException(
                    "Error parsing Properties source file: " + e.getMessage(),
                    e);
        }
        catch (IllegalArgumentException e)
        {
            throw new SourceException(
                    "Error parsing Properties source file: " + e.getMessage(),
                    e);
        }

        SourceElement result
            = new SourceElement(ROOT_ELEMENT_NAME);

        for (String key : properties.orderedKeySet())
        {
            String value = properties.getProperty(key);
            SourceElement entryElement
                = new SourceElement(ENTRY_ELEMENT_NAME);
            entryElement.setAttribute(KEY_ATTRIBUTE_NAME, key);
            entryElement.setAttribute((String) null, value);
            result.getChildren().add(entryElement);
        }
        return result;
    }

    /**
     * Returns a hash code of this instance consistent with equals.
     * As all instances of this class are equal to each other,
     * the hash code is always the same.
     *
     * @return 1.
     */
    @Override
    public int hashCode()
    {
        return 1;
    }

    /**
     * Checks whether other is equal to this instance.
     * All instances of this class are equal to each other.
     *
     * @return true if <code>other</code> is a PropertiesSourceType, false
     *         otherwise.
     */
    @Override
    public boolean equals(Object other)
    {
        if (other == null)
        {
            return false;
        }
        if (!other.getClass().equals(PropertiesSourceFormat.class))
        {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }

    /**
     * Properties which remember the order of the added elements.
     *
     * The class is NOT serializable although it implements the
     * <code>Serializable</code> interface.
     */
    private static class OrderedProperties extends Properties
    {
        /**
         * version for serialization and deserialisation purposes.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The ordered set of keys.
         */
        private final Set<String> keySet = new LinkedHashSet<>();

        @Override
        public Object put(Object key, Object value)
        {
            keySet.add(key.toString());
            return super.put(key, value);
        }

        /**
         * Returns the set of keys, ordered by the order of addition.
         *
         * @return the ordered set of keys, not null.
         *         The returned set is unmodifiable.
         */
        public Set<String> orderedKeySet()
        {
            return Collections.unmodifiableSet(keySet);
        }

        /**
         * Returns a hash code consistent with equals().
         *
         * @return a hash code consistent with equals().
         */
        @Override
        public int hashCode()
        {
            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
            hashCodeBuilder.append(keySet);
            return hashCodeBuilder.toHashCode();
        }

        /**
         * Returns whether this object is equal to another object.
         * This object is considered equal to another object if
         * the other object has the same class and if it has the same
         * content in the same order as this object.
         *
         * @param obj the object to compare.
         *
         * @return true if the other object is equal, false otherwise.
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            OrderedProperties other = (OrderedProperties) obj;
            if (!keySet.equals(other.keySet))
            {
                return false;
            }
            return true;
        }
    }
}
