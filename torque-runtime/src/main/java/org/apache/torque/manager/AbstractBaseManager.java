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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.access.GroupCacheAccess;
import org.apache.commons.jcs.access.exception.CacheException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.torque.Column;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.Persistent;

/**
 * This class contains common functionality of a Manager for
 * instantiating OM's.
 *
 * @param <T> the class of the database object managed by this class.
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id: AbstractBaseManager.java 1870542 2019-11-28 09:32:40Z tv $
 */
public abstract class AbstractBaseManager<T extends Persistent>
implements Serializable
{
    /** serial version */
    private static final long serialVersionUID = 509798299473481305L;

    /** the log */
    protected static final Logger log = LogManager.getLogger(AbstractBaseManager.class);

    /** used to cache the om objects. cache is set by the region property */
    protected transient CacheAccess<ObjectKey<?>, T> cache;

    /** used to cache the method result objects. cache is set by the region property */
    protected transient GroupCacheAccess<MethodCacheKey, Object> groupCache;

    /** method results cache */
    protected transient MethodResultCache mrCache;

    /** The OM class that the service will instantiate. */
    private Class<T> omClass;

    /** The name of the OM class that the service will instantiate. */
    private String className;

    /** The cache region used for JCS. */
    private String region;

    /** Whether the cache manager has already registered its cache Listeners. */
    private boolean isNew = true;

    /** The fields which are valid fields of interest for a listener. */
    private CopyOnWriteArraySet<Column> validFields = new CopyOnWriteArraySet<>();

    /** The listeners for this manager. */
    private ConcurrentMap<Column, CopyOnWriteArrayList<CacheListener<?>>> listenersMap =
            new ConcurrentHashMap<>();

    /**
     * Get the Class instance
     *
     * @return the om class
     */
    protected Class<T> getOMClass()
    {
        return omClass;
    }

    /**
     * Set the Class that will be instantiated by this manager
     *
     * @param omClass the om class
     */
    protected void setOMClass(final Class<T> omClass)
    {
        this.omClass = omClass;
    }

    /**
     * Add variable number of  fields to the list potentially monitored by a listener
     *
     * @param columns array of columns
     */
    protected void addValidField(Column ...columns)
    {
        this.validFields.addAll(Arrays.asList(columns));
    }

    /**
     * Get a fresh instance of an om
     *
     * @return an instance of the om class
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected T getOMInstance()
            throws TorqueException
    {
        try
        {
            return omClass.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new TorqueException("Could not instantiate " + getClassName(), e);
        }
        catch (IllegalAccessException e)
        {
            throw new TorqueException("Could not access " + getClassName(), e);
        }
    }

    /**
     * Get the classname to instantiate for getInstance()
     *
     * @return value of className.
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Set the classname to instantiate for getInstance()
     * @param v  Value to assign to className.
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    @SuppressWarnings("unchecked")
    public void setClassName(final String v)
            throws TorqueException
    {
        this.className = v;

        try
        {
            Class<?> clazz = Class.forName(getClassName());
            if (Persistent.class.isAssignableFrom(clazz))
            {
                setOMClass((Class<T>) clazz);
            }
            else
            {
                throw new TorqueException(
                        getClassName()
                        + " does not implement the interface Persistent");
            }
        }
        catch (ClassNotFoundException cnfe)
        {
            throw new TorqueException("Could not load " + getClassName(), cnfe);
        }
    }


    /**
     * Return an instance of an om based on the id
     *
     * @param id the primary key of the object
     * @return the object from persistent storage or from cache
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected T getOMInstance(final ObjectKey<?> id)
            throws TorqueException
    {
        return getOMInstance(id, true);
    }

    /**
     * Return an instance of an om based on the id
     *
     * @param key the primary key of the object
     * @param fromCache true if the object should be retrieved from cache
     * @return the object from persistent storage or from cache
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected T getOMInstance(final ObjectKey<?> key, final boolean fromCache)
            throws TorqueException
    {
        T om = null;
        if (fromCache)
        {
            om = cacheGet(key);
        }

        if (om == null)
        {
            om = retrieveStoredOM(key);
            if (fromCache)
            {
                putInstanceImpl(om);
            }
        }

        return om;
    }

    /**
     * Get an object from cache
     *
     * @param key the primary key of the object
     * @return the object from cache
     */
    protected T cacheGet(final ObjectKey<?> key)
    {
        T om = null;
        if (cache != null)
        {
            om = cache.get(key);
        }
        return om;
    }

    /**
     * Clears the cache
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected void clearImpl()
            throws TorqueException
    {
        if (cache != null)
        {
            try
            {
                cache.clear();
            }
            catch (CacheException ce)
            {
                throw new TorqueException(
                        "Could not clear cache due to internal JCS error.", ce);
            }
        }
    }

    /**
     * Disposes of the manager. This triggers a shutdown of the connected cache
     * instances. This method should only be used during shutdown of Torque. The
     * manager instance will not cache anymore after this call.
     */
    public void dispose()
    {
        if (cache != null)
        {
            cache.dispose();
            cache = null;
        }

        if (groupCache != null)
        {
            groupCache.dispose();
            groupCache = null;
        }

        validFields.clear();
        listenersMap.clear();
    }

    /**
     * Remove an object from the cache
     *
     * @param key the cache key for the object
     * @return the object one last time
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected T removeInstanceImpl(final ObjectKey<?> key)
            throws TorqueException
    {
        T oldOm = null;
        if (cache != null)
        {
            oldOm = cache.get(key);
            cache.remove(key);
        }
        return oldOm;
    }

    /**
     * Put an object into the cache
     *
     * @param om the object
     * @return if an object with the same key already is in the cache
     *         this object will be returned, else null
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected T putInstanceImpl(final T om)
            throws TorqueException
    {
        ObjectKey<?> key = om.getPrimaryKey();
        return putInstanceImpl(key, om);
    }

    /**
     * Put an object into the cache
     *
     * @param key the cache key for the object
     * @param om the object
     * @return if an object with this key already is in the cache
     *         this object will be returned, else null
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected T putInstanceImpl(final ObjectKey<?> key, final T om)
            throws TorqueException
    {
        if (getOMClass() != null && !getOMClass().isInstance(om))
        {
            throw new TorqueException(om + "; class=" + om.getClass().getName()
                    + "; id=" + om.getPrimaryKey() + " cannot be cached with "
                    + getOMClass().getName() + " objects");
        }

        T oldOm = null;
        if (cache != null)
        {
            oldOm = cache.get(key);
            cache.put(key, om);
        }
        return oldOm;
    }

    /**
     * Retrieve an object from persistent storage
     *
     * @param id the primary key of the object
     * @return the object
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected abstract T retrieveStoredOM(ObjectKey<?> id)
            throws TorqueException;

    /**
     * Gets a list of om's based on id's.
     *
     * @param ids a number of object ids
     * @return a <code>List</code> of objects
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected List<T> getOMs(final ObjectKey<?> ...ids)
            throws TorqueException
    {
        return getOMs(Arrays.asList(ids));
    }

    /**
     * Gets a list of om's based on id's.
     *
     * @param ids a <code>List</code> of <code>ObjectKey</code>'s
     * @return a <code>List</code> value
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected List<T> getOMs(final List<? extends ObjectKey<?>>  ids)
            throws TorqueException
    {
        return getOMs(ids, true);
    }

    /**
     * Gets a list of om's based on id's.
     *
     * @param ids a <code>List</code> of <code>ObjectKey</code>'s.
     * @param fromCache boolean flag if we are to use the cache
     *
     * @return a <code>List</code> value, not null.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected List<T> getOMs(
            final List<? extends ObjectKey<?>> ids,
            final boolean fromCache)
                    throws TorqueException
    {
        if (ids != null && ids.size() > 0)
        {
            // start a new list where we will replace the id's with om's
            Map<ObjectKey<?>, T> omsMap = new HashMap<>();
            List<ObjectKey<?>> newIds = new ArrayList<>(ids);

            if (fromCache)
            {
                ids.stream()
                    .map(this::cacheGet)
                    .filter(om -> om != null)
                    .forEach(om -> omsMap.put(om.getPrimaryKey(), om));

                newIds.removeAll(omsMap.keySet());
            }

            if (!newIds.isEmpty())
            {
                List<T> newOms = retrieveStoredOMs(newIds);
                for (T om : newOms)
                {
                    omsMap.put(om.getPrimaryKey(), om);
                    if (fromCache)
                    {
                        putInstanceImpl(om);
                    }
                }
            }

            return ids.stream()
                    .map(omsMap::get)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    /**
     * Gets a list of om's based on id's.
     * This method must be implemented in the derived class
     *
     * @param ids a <code>List</code> of <code>ObjectKey</code>'s
     * @return a <code>List</code> value
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected abstract List<T> retrieveStoredOMs(List<? extends ObjectKey<?>> ids)
            throws TorqueException;

    /**
     * Get the cache region used for JCS.
     *
     * @return the cache region used for JCS.
     */
    public String getRegion()
    {
        return region;
    }

    /**
     * Set the cache region used for JCS.
     *
     * @param v  Value to assign to region.
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public void setRegion(final String v)
            throws TorqueException
    {
        this.region = v;
        try
        {
            if (Torque.getConfiguration().getBoolean(Torque.CACHE_KEY, false))
            {
                if (cache != null)
                {
                    cache.dispose();
                }
                if (groupCache != null)
                {
                    groupCache.dispose();
                }
                cache = JCS.getInstance(getRegion());
                // FIXME: This is actually the same cache instance which will cause cross-effects
                groupCache = JCS.getGroupCacheInstance(getRegion());
                mrCache = new MethodResultCache(groupCache);
            }
            else
            {
                mrCache = new NoOpMethodResultCache(null);
            }
        }
        catch (CacheException e)
        {
            throw new TorqueException("Cache could not be initialized", e);
        }

        if (cache == null)
        {
            log.info("Cache could not be initialized for region: {}", v);
        }
    }

    /**
     * Get the object usable for result caching
     *
     * @return The cache instance.
     */
    public synchronized MethodResultCache getMethodResultCache()
    {
        if (isNew)
        {
            registerAsListener();
            isNew = false;
        }
        return mrCache;
    }

    /**
     * NoOp version.  Managers should override this method to notify other
     * managers that they are interested in CacheEvents.
     */
    protected void registerAsListener()
    {
        // empty
    }

    /**
     * Add a new listener
     *
     * @param listener A new listener for cache events.
     */
    public void addCacheListenerImpl(final CacheListener<?> listener)
    {
        listener.getInterestedFields().forEach(key ->
        {
            // Peer.column names are the fields
            if (validFields.contains(key))
            {
                CopyOnWriteArrayList<CacheListener<?>> listeners =
                        listenersMap.computeIfAbsent(key, key1 -> new CopyOnWriteArrayList<>());
                listeners.addIfAbsent(listener);
            }
        });
    }

    /**
     * Notify all listeners associated to the column that an object has changed
     *
     * @param <TT> column type class
     * @param column the column related to the listeners
     * @param oldOm the previous object, null if the object has been added
     * @param om the new object, null if the object has been removed
     */
    protected <TT extends Persistent> void notifyListeners(
            final Column column,
            final TT oldOm,
            final TT om)
    {
        List<CacheListener<?>> listeners = listenersMap.get(column);
        if (listeners != null)
        {
            listeners.forEach(cl ->
            {
                @SuppressWarnings("unchecked")
                CacheListener<TT> listener = (CacheListener<TT>) cl;

                if (oldOm == null)
                {
                    // object was added
                    listener.addedObject(om);
                }
                else if (om == null)
                {
                    // object was removed
                    listener.removedObject(oldOm);
                }
                else
                {
                    // object was refreshed
                    listener.refreshedObject(om);
                }
            });
        }
    }


    /**
     * helper methods for the Serializable interface
     *
     * @param out the output stream
     * @throws IOException if output stream not found
     */
    private void writeObject(final java.io.ObjectOutputStream out)
            throws IOException
    {
        out.defaultWriteObject();
    }

    /**
     * Helper methods for the <code>Serializable</code> interface.
     *
     * @param in The stream to read a <code>Serializable</code> from.
     * @throws IOException if input stream cannot be read
     * @throws ClassNotFoundException if the class is not found
     */
    private void readObject(final ObjectInputStream in)
            throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        // initialize the cache
        try
        {
            if (region != null)
            {
                setRegion(region);
            }
        }
        catch (TorqueException e)
        {
            log.error("Cache could not be initialized for region '{}' after deserialization", region);
        }
    }
}
