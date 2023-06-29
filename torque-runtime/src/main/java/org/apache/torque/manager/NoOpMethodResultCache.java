package org.apache.torque.manager;

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

import java.io.Serializable;

import org.apache.commons.jcs.access.GroupCacheAccess;
import org.apache.torque.TorqueException;

/**
 * This class provides a no-op cache for convenient storage of method results
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id: NoOpMethodResultCache.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class NoOpMethodResultCache
extends MethodResultCache
{
    public NoOpMethodResultCache(final GroupCacheAccess<MethodCacheKey, Object> cache)
    {
        super();
    }

    /**
     * Clear the cache
     */
    @Override
    public void clear()
    {
        //empty
    }

    /**
     * @see org.apache.torque.manager.MethodResultCache#getImpl(org.apache.torque.manager.MethodCacheKey)
     */
    @Override
    protected Object getImpl(final MethodCacheKey key)
    {
        return null;
    }

    /**
     * @see org.apache.torque.manager.MethodResultCache#putImpl(org.apache.torque.manager.MethodCacheKey, java.lang.Object)
     */
    @Override
    protected Object putImpl(final MethodCacheKey key, final Object value) throws TorqueException
    {
        return null;
    }

    /**
     * @see org.apache.torque.manager.MethodResultCache#removeImpl(org.apache.torque.manager.MethodCacheKey)
     */
    @Override
    protected Object removeImpl(final MethodCacheKey key)
    {
        return null;
    }

    /**
     * @see org.apache.torque.manager.MethodResultCache#get(
     *     java.io.Serializable,
     *     java.lang.String, java.io.Serializable[])
     */
    @Override
    public <T> T get(final Serializable instanceOrClass, final String method, final Serializable... arg)
    {
        return null;
    }

    /**
     * @see org.apache.torque.manager.MethodResultCache#put(
     *     java.lang.Object, java.io.Serializable,
     *     java.lang.String, java.io.Serializable[])
     */
    @Override
    public <T> void put(final T value, final Serializable instanceOrClass, final String method, final Serializable... arg)
    {
        //empty
    }

    /**
     * @see org.apache.torque.manager.MethodResultCache#removeAll(java.io.Serializable, java.lang.String)
     */
    @Override
    public void removeAll(final Serializable instanceOrClass, final String method)
    {
        //empty
    }

    /**
     * @see org.apache.torque.manager.MethodResultCache#remove(java.io.Serializable, java.lang.String, java.io.Serializable[])
     */
    @Override
    public <T> T remove(final Serializable instanceOrClass, final String method, final Serializable... arg)
    {
        return null;
    }
}
