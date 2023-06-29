package org.apache.torque.util;

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

import java.util.LinkedHashMap;

/**
 * A subclass of LinkedHashMap that has case insensitive
 * String key methods.
 *
 * @author <a href="mailto:greg.monroe@dukece.com">Greg Monroe</a>
 * @version $Id: ListOrderedMapCI.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class ListOrderedMapCI<T> extends LinkedHashMap<String, T>
{
    /** Version id for serializing. */
    private static final long serialVersionUID = -4349246328751938554L;

    /**
     * Get the object associated with this key.
     *
     * @param key A case insensitive String.
     * @return The value for this key
     */
    @Override
    public T get(Object key)
    {
        return super.get(((String) key).toLowerCase());
    }

    /**
     * Adds a value to the end of the list with the specified key.
     *
     * @param key A case insensitive String.
     * @param value The value to add
     * @return The value for previously mapped to this key
     */
    @Override
    public T put(String key, T value)
    {
        return super.put(key.toLowerCase(), value);
    }

    /**
     * Removes the mapping for the specified key.
     * @param key A case insensitive String.
     * @return the removed value, or null if none existed
     */
    @Override
    public T remove (Object key)
    {
        return super.remove(((String) key).toLowerCase());
    }

    /**
     * Test if the key exists in the mapping.
     *
     * @param key The case insensitive key to test for.
     * @return True if the key exists.
     */
    @Override
    public boolean containsKey(Object key)
    {
        return super.containsKey(((String) key).toLowerCase());
    }
}
