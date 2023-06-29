package org.apache.torque.generator.configuration.outlet;

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
import java.util.Map;

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.mergepoint.MergepointMapping;
import org.apache.torque.generator.outlet.DebuggingOutletWrapper;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * Administers the available Outlets.
 */
public class OutletConfiguration
{
    /** A map containing all outlets, keyed by their name. */
    private final Map<QualifiedName, Outlet> outlets
        = new HashMap<>();

    /** A map containing all isolated mergepoint mappings,
     *  keyed by their name. */
    private final Map<String, MergepointMapping> mergepointMappings
        = new HashMap<>();

    /**
     * Whether the mergepoint mappings have been resolved and added to
     * the respective outlets.
     */
    private boolean mergepointMappingsResolved = false;

    /**
     * Creates a OutletConfiguration containing a list of Outlets.
     *
     * @param outlets the map of outlets, keyed by their name, not null.
     * @param mergepointMappings all isolated mergepoint mappings, not null.
     * @param unitDescriptor the descriptor of the unit of generation,
     *        not null.
     *
     * @throws ConfigurationException if outlets contains
     *         two outlets with the same name.
     * @throws NullPointerException if a parameter is null.
     */
    public OutletConfiguration(
            final Collection<Outlet> outlets,
            final Collection<MergepointMapping> mergepointMappings,
            final UnitDescriptor unitDescriptor)
                    throws ConfigurationException
    {
        if (outlets == null)
        {
            throw new NullPointerException("outlets must not be null");
        }
        if (mergepointMappings == null)
        {
            throw new NullPointerException(
                    "mergepointMappings must not be null");
        }
        if (unitDescriptor == null)
        {
            throw new NullPointerException(
                    "unitDescriptor must not be null");
        }
        for (Outlet outlet : outlets)
        {
            addOutlet(outlet, unitDescriptor);
        }
        for (MergepointMapping mergepointMapping : mergepointMappings)
        {
            this.mergepointMappings.put(
                    mergepointMapping.getName(),
                    mergepointMapping);
        }
    }

    /**
     * Resolves the isolated merge point mappings and adds them to
     * the relevant outlets.
     * 
     * @throws ConfigurationException if configuration not found
     */
    public void resolveMergepointMappings() throws ConfigurationException
    {
        for (Map.Entry<String, MergepointMapping> entry
                : mergepointMappings.entrySet())
        {
            String name = entry.getKey();
            QualifiedName qualifiedMergepointName
                = new QualifiedName(name);
            if (qualifiedMergepointName.getNamespace().isRoot())
            {
                // outlet name is missing
                throw new ConfigurationException(
                        "The isolated mergepoint mapping with the name "
                                + name
                                + " needs to be qualified with the outlet name");
            }
            QualifiedName outletName = new QualifiedName(
                    qualifiedMergepointName.getNamespace().toString());
            Outlet outlet = outlets.get(outletName);
            if (outlet == null)
            {
                throw new ConfigurationException(
                        "No outlet with name "
                                + outletName
                                + "exists (required by the isolated mergepoint mapping"
                                + " with the name "
                                + name
                                + ")");
            }
            MergepointMapping originalMergepointMapping = entry.getValue();
            MergepointMapping resolvedMergepointMapping
                = new MergepointMapping(
                    qualifiedMergepointName.getName(),
                    originalMergepointMapping.getActions());
            outlet.setMergepointMapping(resolvedMergepointMapping);
        }
        mergepointMappingsResolved = true;
        mergepointMappings.clear();
    }

    /**
     * Returns a map containing all the configured outlets, keyed by their
     * name.
     *
     * @return all outlets, not null.
     *
     * @throws IllegalStateException if the mergepoint mappings have not
     *         yet been resolved.
     */
    public Map<QualifiedName, Outlet> getOutlets()
    {
        if (!mergepointMappingsResolved)
        {
            throw new IllegalStateException(
                    "Mergepoint mappings must be resoved first");
        }
        return Collections.unmodifiableMap(outlets);
    }

    /**
     * Returns the outlet with the name <code>name</code>.
     *
     * @param name the name of the outlet to be returned.
     *
     * @return The outlet with the given name, or null if it does not
     *         exist.
     *
     * @throws IllegalStateException if the mergepoint mappings have not
     *         yet been resolved.
     */
    public Outlet getOutlet(final QualifiedName name)
    {
        if (!mergepointMappingsResolved)
        {
            throw new IllegalStateException(
                    "Mergepoint mappings must be resoved first");
        }
        return outlets.get(name);
    }

    /**
     * Adds a outlet.
     *
     * @param outlet the outlet to be added, not null.
     * @param unitDescriptor the descriptor of the generation unit,
     *        not null.
     *
     * @throws ConfigurationException if a outlet with the outlet's name
     *         already exists in the configuration.
     * @throws NullPointerException if outlet is null.
     */
    public void addOutlet(Outlet outlet, final UnitDescriptor unitDescriptor)
            throws ConfigurationException
    {
        if (outlet == null)
        {
            throw new NullPointerException("outlet must not be null");
        }
        if (unitDescriptor == null)
        {
            throw new NullPointerException(
                    "unitDescriptor must not be null");
        }
        Outlet existingOutlet = outlets.get(outlet.getName());
        if (existingOutlet != null)
        {
            throw new ConfigurationException("Trying to add the outlet "
                    + outlet.getName()
                    + " and class "
                    + outlet.getClass().getName()
                    + " : A outlet with the same name "
                    + " already exists, it has the class "
                    + existingOutlet.getClass().getName());
        }
        if (unitDescriptor.isAddDebuggingInfoToOutput())
        {
            outlet = new DebuggingOutletWrapper(outlet);
        }
        outlets.put(outlet.getName(), outlet);
    }

    /**
     * Creates a String view of this object for debuggung purposes.
     *
     * @return a String view of this object, never null.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer("(outlets=[")
                .append(outlets)
                .append(")");
        return result.toString();
    }

    /**
     * Returns whether a outlet for the given name exists.
     * Name and namespace must match exactly.
     *
     * @param qualifiedName the name of the outlet.
     *
     * @return true if a outlet with the name exists, false otherwise.
     */
    public boolean outletExists(final QualifiedName qualifiedName)
    {
        return outlets.containsKey(qualifiedName);
    }
}
