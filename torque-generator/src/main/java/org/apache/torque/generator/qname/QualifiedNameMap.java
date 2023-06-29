package org.apache.torque.generator.qname;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A special map with a QualifiedName as a key. This map acts as a normal map,
 * except that it has an additional method, <code>getInNamespace()</code>,
 * which respects the visibility rules in the namespace hierarchy. Null cannot
 * be stored as a key for this map.
 *
 * @param <T> The class of the object which should be stored in the map.
 */
public class QualifiedNameMap<T>
implements Map<QualifiedName, T>, Serializable
{
    /**
     * SerialVersionUid for serializing.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The backing hashMap. Must not contain a mapping for the key null,
     * because null as a qualified name does not make sense.
     */
    private transient HashMap<QualifiedName, T> map;

    /**
     * Constructs an empty QualifiedNameMap.
     */
    public QualifiedNameMap()
    {
        map = new HashMap<>();
    }

    /**
     * Constructs a shallow copy of the supplied map.
     *
     * @param map the map to copy, not null.
     */
    public QualifiedNameMap(Map<? extends QualifiedName, ? extends T> map)
    {
        if (map.containsKey(null))
        {
            throw new IllegalArgumentException(
                    "map must not contain a mapping for the key null");
        }
        this.map = new HashMap<>(map);
    }

    /**
     * Returns the most specific entry in the map which is visible
     * for the provided key. I.e. not only the namespace of the provided
     * QualifiedName is searched, but also all the parent namespaces of the
     * provided QualifiedName.
     *
     * Note that if the most specific matching key is mapped to null,
     * then null is returned, even if another less specific entry exists
     * which is not mapped to null.
     *
     * If only exact matches for the QualifiedName should be returned,
     * use the <code>get(Object)</code> method.
     *
     * @param key the qualified name for which the entries in the map should
     *        be searched, not null.
     * @return The entry which is visible to the supplied QualifiedName,
     *         or null if no such entry exists.
     *         If more such entries exist, the most specific one
     *         (i.e. the one with the longest namespace) is returned.
     *
     * @throws NullPointerException if key is null.
     *
     * @see #get(Object)
     */
    public T getInHierarchy(QualifiedName key)
    {
        while (true)
        {
            if (map.containsKey(key))
            {
                return get(key);
            }

            // the default namespace is not a part of any other namespace,
            // whe have reached the end of the recursion
            if (Namespace.ROOT_NAMESPACE.equals(key.getNamespace()))
            {
                return null;
            }

            key = new QualifiedName(
                    key.getName(),
                    key.getNamespace().getParent());
        }
    }

    /**
     * Returns the most specific key in the map which is visible
     * for the provided key. I.e. not only the namespace of the provided
     * QualifiedName is searched, but also all the parent namespaces of the
     * provided QualifiedName. If no such key exists, null is returned.
     *
     * @param key the qualified name for which the entries in the map should
     *        be searched, not null.
     * @return The key which is visible to the supplied QualifiedName,
     *         or null if no such key exists.
     *         If more such keys exist, the most specific one
     *         (i.e. the one with the longest namespace) is returned.
     *
     * @throws NullPointerException if key is null.
     */
    public QualifiedName getKeyInHierarchy(QualifiedName key)
    {
        while (true)
        {
            if (map.containsKey(key))
            {
                return key;
            }

            // the default namespace is not a part of any other namespace,
            // we have reached the end of the recursion
            if (Namespace.ROOT_NAMESPACE.equals(key.getNamespace()))
            {
                return null;
            }

            key = new QualifiedName(
                    key.getName(),
                    key.getNamespace().getParent());
        }
    }


    /**
     * Returns all mappings which live in the given namespace.
     * If one mapping hides another mapping, i.e. if one mapping
     * is a more specialized version of another, both
     * mappings are present in the returned map.
     *
     * For example, if the map contains mappings for org.apache.torque:name1,
     * org.apache:name1, org.apache.torque:name2 and
     * org.apache.torque.generator:name1, and the queried namespace is
     * org.apache.torque, then the mappings for
     * org.apache.torque:name1 and org.apache.torque:name2 and org.apache:name1
     * are returned. The mapping for org.apache.torque.generator:name1 is not
     * returned, because it is not in the target namespace.
     *
     * @param namespace the namespace in which the desired objects
     *        should be visible.
     * @return all mappings in the desired Namespace, not null.
     */
    public QualifiedNameMap<T> getAllInHierarchy(Namespace namespace)
    {
        QualifiedNameMap<T> result = new QualifiedNameMap<>();
        for (Map.Entry<QualifiedName, T> entry : entrySet())
        {
            QualifiedName qualifiedName = entry.getKey();
            if (!qualifiedName.isVisibleFrom(namespace))
            {
                continue;
            }
            result.put(qualifiedName, entry.getValue());
        }
        return result;
    }

    /**
     * Returns all mappings which live in the given namespace.
     * If one mapping hides another mapping, i.e. if one mapping
     * is a more specialized version of another, the hidden mapping
     * is NOT returned.
     *
     * For example, if the map contains mappings for org.apache.torque:name1,
     * org.apache:name1, org.apache.torque:name2 and
     * org.apache.torque.generator:name1, and the queried namespace is
     * org.apache.torque, then the mappings for
     * org.apache.torque:name1 and org.apache.torque:name2 are returned.
     * The mapping for org.apache:name1 is hidden by org.apache.torque:name1,
     * and org.apache.torque.generator:name1 is not in the target namespace.
     *
     * @param namespace the namespace in which the desired objects
     *        should be visible.
     * @return all mappings in the desired Namespace, except the mappings
     *         which are hidden by another mapping, not null.
     */
    public QualifiedNameMap<T> getInHierarchy(Namespace namespace)
    {
        QualifiedNameMap<T> result = new QualifiedNameMap<>();
        for (Map.Entry<QualifiedName, T> entry : entrySet())
        {
            QualifiedName qualifiedName = entry.getKey();
            T value = entry.getValue();
            if (!qualifiedName.isVisibleFrom(namespace))
            {
                continue;
            }

            Iterator<Map.Entry<QualifiedName, T>> resultEntryIt
            = result.entrySet().iterator();
            while (resultEntryIt.hasNext())
            {
                Map.Entry<QualifiedName, T> resultEntry
                = resultEntryIt.next();
                QualifiedName resultQName = resultEntry.getKey();
                if (!resultQName.getName().equals(qualifiedName.getName()))
                {
                    // the qualified names do not have the same name,
                    // they can not hide each other
                    continue;
                }
                if (resultQName.isVisibleFrom(qualifiedName.getNamespace()))
                {
                    // resultQName is hidden by qualifiedName
                    resultEntryIt.remove();
                }
                else
                {
                    // resultQName hides qualifiedName.
                    // Do not add the mapping for qualifiedName.
                    qualifiedName = resultQName;
                    value = resultEntry.getValue();
                    break;
                }
            }
            result.put(qualifiedName, value);
        }
        return result;
    }

    /**
     * Returns the object which is mapped to the given key.
     * Only Objects of class Namespace are used as keys in this map.
     *
     * @param key the key for which the mapped object should be returned.
     *
     * @return the object mapped to the given key, or null if no mapping exists
     *         for the key or null is mapped to the key.
     *
     * @see Map#get(java.lang.Object)
     * @see #getInHierarchy(QualifiedName)
     */
    @Override
    public T get(Object key)
    {
        return map.get(key);
    }

    /**
     * Creates or overwrites a mapping in the map. Null as key is not supported.
     *
     * @param key the key for the mapping, not null.
     * @param value the object mapped to the key.
     *
     * @return the previous object which was mapped to the given key,
     *         or null if no mapping existed for the given key.
     *
     * @see Map#put(Object, Object)
     */
    @Override
    public T put(QualifiedName key, T value)
    {
        if (key == null)
        {
            throw new NullPointerException("key must not be null");
        }
        return map.put(key, value);
    }

    /**
     * Creates or overrides mappings for all the mappings in the supplied
     * map. The supplied map must not contain a mapping for the key
     * <code>null</code>.
     *
     * @param toPut the Map whcih mappings should be added to this map,
     *        not null.
     *
     * @throws NullPointerException if toPut is null.
     * @throws IllegalArgumentException if toPut contains a mapping for
     *         the key <code>null</code>.
     *
     * @see Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends QualifiedName, ? extends T> toPut)
    {
        if (toPut.containsKey(null))
        {
            throw new IllegalArgumentException(
                    "toPut must not contain a mapping for the key null");
        }
        map.putAll(toPut);
    }

    /**
     * Removes the mapping for the supplied key, if this mapping exists.
     *
     * @param key the key for which the mapping should be removed.
     *
     * @return the object which was previously mapped to the key, or null
     *         if no mapping existed for the provided key.
     *
     * @see Map#remove(java.lang.Object)
     */
    @Override
    public T remove(Object key)
    {
        return map.remove(key);
    }

    /**
     * Removes all mappings from this map.
     *
     * @see Map#clear()
     */
    @Override
    public void clear()
    {
        map.clear();
    }

    /**
     * Returns if this Map contains a mapping for the given key.
     *
     * @param key the key for which the existence of a mapping should be
     *        checked.
     *
     * @return true if the map contains a mapping for the given key,
     *         false otherwise.
     *
     * @see Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key)
    {
        return map.containsKey(key);
    }

    /**
     * Returns all Entries in this map.
     *
     * @return a Set containing all mappings in this map.
     *
     * @see Map#entrySet()
     */
    @Override
    public Set<Map.Entry<QualifiedName, T>> entrySet()
    {
        return map.entrySet();
    }

    /**
     * Checks if any key is mapped to the given value.
     *
     * @param value the value which existence should be checked in the map.
     *
     * @return true if any key is mapped to the given value, false otherwise.
     *
     * @see Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }

    /**
     * Returns all values in this map.
     *
     * @return a collection containing all values in this map.
     *
     * @see Map#values()
     */
    @Override
    public Collection<T> values()
    {
        return map.values();
    }

    /**
     * Counts all mappings in the map.
     *
     * @return the number of mappings in the map.
     *
     * @see Map#size()
     */
    @Override
    public int size()
    {
        return map.size();
    }

    /**
     * Returns if the map contains any mappings at all.
     *
     * @return true if the map contains mappings, false otherwise.
     *
     * @see Map#isEmpty()
     */
    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    /**
     * Returns all keys of the map.
     *
     * @return a Collection containing all keys of the map, never null.
     *
     * @see Map#keySet()
     */
    @Override
    public Set<QualifiedName> keySet()
    {
        return map.keySet();
    }

    /**
     * Returns the more specific object out of two objects (the object
     * which hides the other object).
     *
     * If one of the two objects is null, the other is returned.
     * If both objects is null, null is returned.
     * If both objects are in the same namespace, object1 is returned.
     *
     * It is assumed that namespace1 is an ancestor namespace of
     * namespace2 or that namespace2 is in an ancestor namespace of
     * namespace1 or both namespaces are the same.
     *
     * @param object1 the first object to compare.
     * @param qualifiedName1 the qualified name of the first object,
     *         must not be null if object1 is not null.
     * @param object2 the second object to compare.
     * @param qualifiedName2 the namepsace of the second object,
     *         must not be null, if object2 is not null.
     * @return the more specific object, or null if both object1 and
     *         object2 are null.
     * @throws NullPointerException if object1 and object2 are not null and
     *          namespace1 or namespace2 are null.
     */
    public T getMoreSpecific(
            T object1,
            QualifiedName qualifiedName1,
            T object2,
            QualifiedName qualifiedName2)
    {
        if (object1 == null)
        {
            return object2;
        }
        if (object2 == null)
        {
            return object1;
        }
        Namespace namespace1 = qualifiedName1.getNamespace();
        Namespace namespace2 = qualifiedName2.getNamespace();
        if (namespace2.isVisibleFrom(namespace1))
        {
            // qualifiedName1 is more specific or in the same namespace
            // as qualifiedName2, so return object1
            return object1;
        }
        else
        {
            // qualifiedName2 is more specific than qualifiedName1
            return object2;
        }
    }

    /**
     * Checks if this QualifiedNameMap is equal to another object.
     * This is true if and only if the other object is a QualifiedNameMap,
     * and contains the same mappings as this QualifiedNameMap.
     *
     * @param object the object to check whether it is equal to this object.
     *
     * @return true if this QualifiedNameMap is equal to the object,
     *         false otherwise.
     */
    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof QualifiedNameMap<?>))
        {
            return false;
        }

        QualifiedNameMap<?> other = (QualifiedNameMap<?>) object;

        return map.equals(other.map);
    }

    /**
     * Returns a hashCode for this object. The returned hashCode is consistent
     * with equals().
     *
     * @return a hashcode for this QualifiedNameMap.
     */
    @Override
    public int hashCode()
    {
        return map.hashCode();
    }

    /**
     * Returns a string representation of this map for debugging
     * purposes.
     *
     * @return a string representation of this map, not null.
     */
    @Override
    public String toString()
    {
        return map.toString();
    }
}
