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
import org.apache.commons.jcs.access.exception.CacheException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.torque.TorqueException;

/**
 * This class provides a cache for convenient storage of method
 * results.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id: MethodResultCache.java 1870542 2019-11-28 09:32:40Z tv $
 */
public class MethodResultCache
{
    /** The underlying jcs cache. */
    private GroupCacheAccess<MethodCacheKey, Object> jcsCache;

    /** Logging */
    private static final Logger log = LogManager.getLogger(MethodResultCache.class);

    /**
     * Constructor
     *
     * @param cache the cache instance to use
     */
    public MethodResultCache(GroupCacheAccess<MethodCacheKey, Object> cache)
    {
        this.jcsCache = cache;
    }

    /**
     * Allows subclasses to have ctors that do not require a cache.
     * This is used by NullMethodResultCache which has no-op versions
     * of all methods.
     */
    protected MethodResultCache()
    {
        //empty
    }

    /**
     * Clear the cache
     */
    public void clear()
    {
        if (jcsCache != null)
        {
            try
            {
                jcsCache.clear();
            }
            catch (CacheException ce)
            {
                log.error(new TorqueException(
                        "Could not clear cache due to internal JCS error.", ce));
            }
        }
    }

    protected Object getImpl(MethodCacheKey key)
    {
        Object result = null;
        if (jcsCache != null)
        {
            result = jcsCache.getFromGroup(key, key.getGroupKey());
        }

        if (result != null)
        {
            log.debug("MethodResultCache saved expensive operation: {}", key);
        }

        return result;
    }


    protected Object putImpl(MethodCacheKey key, Object value)
            throws TorqueException
    {
        String group = key.getGroupKey();

        Object old = null;
        if (jcsCache != null)
        {
            try
            {
                old = jcsCache.getFromGroup(key, group);
                jcsCache.putInGroup(key, group, value);
            }
            catch (CacheException ce)
            {
                throw new TorqueException("Could not cache due to internal JCS error", ce);
            }
        }
        return old;
    }

    protected Object removeImpl(MethodCacheKey key)
    {
        Object old = null;
        if (jcsCache != null)
        {
            old = jcsCache.getFromGroup(key, key.getGroupKey());
            jcsCache.removeFromGroup(key, key.getGroupKey());
        }
        return old;
    }

    /**
     * Get an object from the method cache
     *
     * @param <T> type of the instance class
     * @param instanceOrClass the Object on which the method is invoked.  if
     * the method is static, a String representing the class name is used.
     * @param method the method name
     * @param arg optional arguments for the method
     *
     * @return the object or null if it does not exist
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Serializable instanceOrClass, String method, Serializable ... arg)
    {
        T result = null;
        if (jcsCache != null)
        {
            MethodCacheKey key = new MethodCacheKey(instanceOrClass, method, arg);
            result = (T) getImpl(key);
        }

        return result;
    }

    /**
     * Put an object into the method cache
     *
     * @param <T> type of the instance class
     * @param value the object to put into the cache
     * @param instanceOrClass the Object on which the method is invoked.  if
     * the method is static, a String representing the class name is used.
     * @param method the method name
     * @param arg optional arguments for the method
     */
    public <T> void put(T value, Serializable instanceOrClass, String method, Serializable ... arg)
    {
        try
        {
            MethodCacheKey key = new MethodCacheKey(instanceOrClass, method, arg);
            putImpl(key, value);
        }
        catch (TorqueException e)
        {
            log.error("Problem putting object into cache", e);
        }
    }

    /**
     * Remove all objects of the same group
     *
     * @param instanceOrClass the Object on which the method is invoked.  if
     * the method is static, a String representing the class name is used.
     * @param method the method name
     */
    public void removeAll(Serializable instanceOrClass, String method)
    {
        if (jcsCache != null)
        {
            MethodCacheKey key = new MethodCacheKey(instanceOrClass, method);
            String groupName = key.getGroupKey();
            jcsCache.invalidateGroup(groupName);
        }
    }

    /**
     * Remove object from method cache
     *
     * @param <T> type of the instance class
     * @param instanceOrClass the Object on which the method is invoked.  if
     * the method is static, a String representing the class name is used.
     * @param method the method name
     * @param arg optional arguments for the method
     *
     * @return the removed object
     */
    @SuppressWarnings("unchecked")
    public <T> T remove(Serializable instanceOrClass, String method, Serializable ... arg)
    {
        T result = null;
        if (jcsCache != null)
        {
            MethodCacheKey key = new MethodCacheKey(instanceOrClass, method, arg);
            result = (T) removeImpl(key);
        }

        return result;
    }
}
