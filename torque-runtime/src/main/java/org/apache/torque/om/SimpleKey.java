package org.apache.torque.om;

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

/**
 * This empty class marks an ObjectKey as being capable of being
 * represented as a single column in a database.
 *
 * @author <a href="mailto:jmcnally@apache.org">John McNally</a>
 * @author <a href="mailto:drfish@cox.net">J. Russell Smyth</a>
 * @version $Id: SimpleKey.java 1849379 2018-12-20 12:33:43Z tv $
 */
public abstract class SimpleKey<T> extends ObjectKey<T>
{
    /** Version id for serializing. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an ObjectKey for the key object.
     *
     * @param key the key value.
     *
     * @return an ObjectKey for <code>key</code>.
     */
    public static NumberKey keyFor(java.math.BigDecimal key)
    {
        return new NumberKey(key);
    }

    /**
     * Creates an ObjectKey for the key object.
     *
     * @param key the key value.
     *
     * @return an ObjectKey for <code>key</code>.
     */
    public static NumberKey keyFor(int key)
    {
        return new NumberKey(key);
    }

    /**
     * Creates an ObjectKey for the key object.
     *
     * @param key the key value.
     *
     * @return an ObjectKey for <code>key</code>.
     */
    public static NumberKey keyFor(long key)
    {
        return new NumberKey(key);
    }

    /**
     * Creates an ObjectKey for the key object.
     *
     * @param key the key value.
     *
     * @return an ObjectKey for <code>key</code>.
     */
    public static NumberKey keyFor(double key)
    {
        return new NumberKey(key);
    }

    /**
     * Creates an ObjectKey for the key object.
     *
     * @param key the key value.
     *
     * @return an ObjectKey for <code>key</code>.
     */
    public static NumberKey keyFor(Number key)
    {
        return new NumberKey(key);
    }

    /**
     * Creates an ObjectKey for the key object.
     *
     * @param key the key value.
     *
     * @return an ObjectKey for <code>key</code>.
     */
    public static NumberKey keyFor(NumberKey key)
    {
        return new NumberKey(key);
    }

    /**
     * Creates an ObjectKey for the key object.
     *
     * @param key the key value.
     *
     * @return an ObjectKey for <code>key</code>.
     */
    public static StringKey keyFor(String key)
    {
        return new StringKey(key);
    }

    /**
     * Creates an ObjectKey for the key object.
     *
     * @param key the key value.
     *
     * @return an ObjectKey for <code>key</code>.
     */
    public static StringKey keyFor(StringKey key)
    {
        return new StringKey(key);
    }

    /**
     * Creates an ObjectKey for the key object.
     *
     * @param key the key value.
     *
     * @return an ObjectKey for <code>key</code>.
     */
    public static DateKey keyFor(java.util.Date key)
    {
        return new DateKey(key);
    }

    /**
     * Creates an ObjectKey for the key object.
     *
     * @param key the key value.
     *
     * @return an ObjectKey for <code>key</code>.
     */
    public static DateKey keyFor(DateKey key)
    {
        return new DateKey(key);
    }

    /**
     * Creates an ObjectKey for the key object.
     *
     * @param key the key value.
     *
     * @return an ObjectKey for <code>key</code>.
     */
    public static BooleanKey keyFor(Boolean key)
    {
        return new BooleanKey(key);
    }
}
