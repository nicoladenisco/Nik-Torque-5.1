package org.apache.torque.generator.option;

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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.torque.generator.qname.Namespace;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.qname.QualifiedNameMap;

/**
 * A Store for all options.
 */
public class Options
{
    /** The options with global scope. */
    private QualifiedNameMap<Option> globalScope
        = new QualifiedNameMap<>();

    /**
     * Sets an option with global scope.
     *
     * @param option the option to set, not null.
     *
     * @throws NullPointerException if option is null.
     */
    public void setGlobalOption(Option option)
    {
        globalScope.put(option.getQualifiedName(), option);
    }

    /**
     * Adds several options with global scope.
     *
     * @param options the option to add, not null, may not contain null.
     *
     * @throws NullPointerException if options is null or contains null.
     */
    public void addGlobalOptions(Collection<Option> options)
    {
        for (Option option : options)
        {
            setGlobalOption(option);
        }
    }

    /**
     * Returns the value of the option which is closest in Hierarchy.
     * If more than one matching options (options with the matching name
     * and in the name space or parent name space of the key) are found,
     * the one which name space is closer to the given key's name space
     * is chosen.
     *
     * @param key the key for the option which value should be retrieved.
     *
     * @return the value of the option (can be null), or null if no matching
     *         option exists.
     */
    public Option getInHierarchy(QualifiedName key)
    {
        QualifiedName globalKey = globalScope.getKeyInHierarchy(key);
        Option globalOption = globalScope.get(globalKey);
        return globalOption;
    }

    /**
     * Returns all mappings which live in the given name space.
     * If one mapping hides another mapping, i.e. if one mapping
     * is a more specialized version of another, the hidden mapping
     * is NOT returned.
     *
     * @param namespace the name space from which the returned options should
     *        be visible.
     *
     * @return an Options object containing the matching options
     */
    public Options getInHierarchy(Namespace namespace)
    {
        Options result = new Options();
        result.globalScope = globalScope.getInHierarchy(namespace);
        return result;
    }

    /**
     * Returns all mappings which live in the given namespace.
     * If one mapping hides another mapping, i.e. if one mapping
     * is a more specialized version of another, both
     * mappings are present in the returned map.
     *
     * @param namespace the name space from which the returned options should
     *        be visible.
     *
     * @return an Options object containing the matching options
     */
    public Options getAllInHierarchy(Namespace namespace)
    {
        Options result = new Options();
        result.globalScope = globalScope.getAllInHierarchy(namespace);
        return result;
    }

    /**
     * Returns all options in a Collection.
     *
     * @return a Collection containing all options, not null.
     *         The collection is unmodifiable.
     */
    public Collection<Option> values()
    {
        Map<QualifiedName, Option> result
            = new HashMap<>();
        for (Option globalOption : globalScope.values())
        {
            result.put(globalOption.getQualifiedName(), globalOption);
        }
        return Collections.unmodifiableCollection(result.values());
    }

    /**
     * Returns all options in a set.
     *
     * @return a Collection containing the qualified names of all options,
     *         not null.
     */
    public Collection<QualifiedName> keySet()
    {
        Collection<Option> options = values();
        Set<QualifiedName> result = new HashSet<>(options.size());
        for (Option option : options)
        {
            result.add(option.getQualifiedName());
        }
        return result;
    }

    /**
     * Returns a map containing all options in the global scope.
     * The key of the map is the key of the option, the value is the
     * option itself.
     *
     * @return a map containing the options in global scope, not null.
     */
    public Map<QualifiedName, Option> getGlobalScope()
    {
        return Collections.unmodifiableMap(globalScope);
    }

    /**
     * Checks if an option with the given key exists in any scope.
     *
     * @param key the key to check.
     *
     * @return true if the key exists, false otherwise.
     */
    public boolean containsKey(QualifiedName key)
    {
        return globalScope.containsKey(key);
    }

    @Override
    public String toString()
    {
        return "global Scope: " + globalScope.toString();
    }
}
