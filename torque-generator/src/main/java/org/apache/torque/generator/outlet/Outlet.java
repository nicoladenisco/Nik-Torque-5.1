package org.apache.torque.generator.outlet;

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

import java.util.Map;

import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.mergepoint.MergepointMapping;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * Generates String output from the AST.
 */
public interface Outlet
{
    /**
     * Returns the name of the outlet. The namespace part of the name defines
     * the default context of the outlet.
     *
     * @return the name of the outlet, not null.
     */
    QualifiedName getName();

    /**
     * Adds an mergepoint mapping to the outlet. No mergepoint
     * mappings must exist with the given name.
     *
     * @param mergepointMapping the mergepointMapping to add, not null.
     *
     * @throws NullPointerException if mergepointMapping is null.
     * @throws ConfigurationException if an mergepointMapping
     *          for the given name already exists.
     */
    void addMergepointMapping(MergepointMapping mergepointMapping)
            throws ConfigurationException;

    /**
     * Sets an mergepoint mapping in the outlet. If a mergepoint
     * mapping with the given name already exists, it is replaced.
     *
     * @param mergepointMapping the mergepointMapping to add, not null.
     *
     * @return the replaced mergepoint mapping, not null.
     *
     * @throws NullPointerException if mergepointMapping is null.
     */
    MergepointMapping setMergepointMapping(MergepointMapping mergepointMapping);

    /**
     * Returns the mergepoint mapping for the given mergepoint name.
     *
     * @param name the name of the mergepoint mapping.
     *
     * @return the mergepoint mapping for the given name, or null if no
     *           mergepoint mapping exists for this name.
     */
    MergepointMapping getMergepointMapping(String name);

    /**
     * Returns the map of all mergepoint mappings, keyed by their name.
     *
     * @return the map of mergepoint mappings, not null.
     */
    Map<String, MergepointMapping> getMergepointMappings();

    /**
     * Returns the name of the input root element. If not null, the outlet
     * checks if the name of the input root element corresponds to the set
     * element name and throws an exception if the names do not match.
     *
     * @return inputName the name of the root element of the source,
     *          or if any input name is accepted.
     */
    String getInputElementName();

    /**
     * Sets the name of the input root element. If set, the outlet
     * checks if the name of the input root element corresponds to the set
     * element name and throws an exception if the names do not match.
     *
     * @param inputName the name of the root element of the source,
     *        or null to accept any input name.
     */
    void setInputElementName(String inputName);

    /**
     * Returns the fully qualified name of the input root class.
     * If not null, the outlet checks if the class name of the input root
     * corresponds to the set class name
     * and throws an exception if the names do not match.
     *
     * @return inputName the name of the root element of the source,
     *          or if any input name is accepted.
     */
    String getInputClass();

    /**
     * Sets the fully qualified name of the input model root class.
     * If set, the outlet checks if the class name of the input model root
     * corresponds to the set class name
     * and throws an exception if the names do not match.
     *
     * @param inputClass the name of the root element of the source,
     *        or null to accept any input name.
     */
    void setInputClass(String inputClass);

    /**
     * Adjusts the state of the Controller before generation.
     *
     * @param controllerState the current controller state, not null.
     *
     * @throws GeneratorException if adjusting the controller state fails.
     */
    void beforeExecute(ControllerState controllerState)
            throws GeneratorException;

    /**
     * Adjusts the state of the Controller after generation.
     *
     * @param controllerState the current controller state, not null.
     *
     * @throws GeneratorException if adjusting the controller state fails.
     */
    void afterExecute(ControllerState controllerState)
            throws GeneratorException;

    /**
     * Generates the output for this template into the Generated object.
     *
     * @param controllerState the current controller state, not null.
     *
     * @return the output of the Outlet.
     *
     * @throws GeneratorException if generation fails.
     */
    OutletResult execute(ControllerState controllerState)
            throws GeneratorException;
}
