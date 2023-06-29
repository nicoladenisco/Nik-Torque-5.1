package org.apache.torque.generator.outlet.java;

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

import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * A base class for a outlet which takes a String as input
 * (not a Source Element as a normal outlet would).
 */
public abstract class StringInputOutlet extends OutletWithoutMergepoints
{
    /**
     * The direct input.
     */
    private String inputValue;

    /**
     * The source element which contains the attribute for the input,
     * relative to the current source element. A dot (.) denotes the current
     * source element itself.
     */
    private String inputSourceElement;

    /**
     * The source element attribute which contains the input.
     */
    private String sourceElementAttribute;

    /**
     * The option which should be used as input.
     */
    private String inputOption;

    /**
     * The variable which should be used as input.
     */
    private String inputVariable;

    /**
     * Constructs a singleInputOutlet.
     *
     * @param qualifiedName the name of the outlet.
     */
    public StringInputOutlet(QualifiedName qualifiedName)
    {
        super(qualifiedName);
    }

    /**
     * Sets the input value directly.
     *
     * @param inputValue the input value, or null if the direct input value
     *        should not be used.
     */
    public void setInputValue(String inputValue)
    {
        this.inputValue = inputValue;
    }

    /**
     * Sets the name of the option which should be used as input.
     *
     * @param inputOption the name of the option which contains the input,
     *        or null if no option should be used as input.
     */
    public void setInputOption(String inputOption)
    {
        this.inputOption = inputOption;
    }

    /**
     * Sets the name of the variable which should be used as input.
     *
     * @param inputVariable the name of the variable which should be used as
     *        input, or null if no variable should be used as input.
     */
    public void setInputVariable(String inputVariable)
    {
        this.inputVariable = inputVariable;
    }

    /**
     * Sets the name of the source element which contains the attribute
     * which should be used as input.
     *
     * @param inputSourceElement the name of the source element, or null if
     *        no source element should be used as input.
     */
    public void setInputSourceElement(String inputSourceElement)
    {
        this.inputSourceElement = inputSourceElement;
    }

    /**
     * Sets the name of the source element attribute from which the input
     * should be read. Only used if inputsourceElement is not null.
     *
     * @param sourceElementAttribute the attribute from which the input is
     *        read.
     */
    public void setSourceElementAttribute(String sourceElementAttribute)
    {
        this.sourceElementAttribute = sourceElementAttribute;
    }

    /**
     * Retrieves the input from the different possibilities nputValue,
     * inputOption, inputVariable, or inputSourceElement.
     *
     * @param controllerState the current controller state.
     *
     * @return the retrieved value, not null.
     *
     * @throws GeneratorException if no possibility or more than one possibility
     *         is chosen, or if the desired input is not set(except when a
     *         variable is not set or set to null, this results in "" being
     *         returned)
     */
    protected String getInput(ControllerState controllerState)
            throws GeneratorException
    {
        return OutletUtils.getFromDifferentPlaces(
                inputValue,
                inputOption,
                inputVariable,
                inputSourceElement,
                sourceElementAttribute,
                controllerState,
                getClass(),
                "inputValue, inputOption, inputVariable, or inputSourceElement");
    }
}
