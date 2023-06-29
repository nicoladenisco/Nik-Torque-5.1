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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.qname.Namespace;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourcePath;
import org.apache.torque.generator.variable.Variable;

/**
 * Utility methods to retrieve information out of the source model or the
 * configuration. The methods provide adequate logging and error handling.
 */
public final class OutletUtils
{
    /**
     * Private constructor for utility class.
     */
    private OutletUtils()
    {
    }

    /** The logger for the class. */
    private static Log log = LogFactory.getLog(OutletUtils.class);

    /**
     * Retrieves the value of a source element attribute. The source element
     * must be found and the attribute must be set, otherwise an exception is
     * thrown.
     *
     * @param elementName The name of the source element relative to the
     *        current source element; a dot (.) denotes the current element.
     * @param attributeName The name of the attribute of the element.
     * @param controllerState The controller state.
     * @param clazz the class in which the attribute should be retrieved;
     *        used only for logging.
     *
     * @return the value of the specified attribute, not null.
     *
     * @throws GeneratorException if the source element cannot be found
     *         or the specified attribute is not set.
     */
    public static String getSourceElementAttribute(
            String elementName,
            String attributeName,
            ControllerState controllerState,
            Class<?> clazz)
                    throws GeneratorException
    {
        SourceElement sourceElement = SourcePath.getElement(
                (SourceElement) controllerState.getModel(),
                elementName,
                false);
        Object attribute
        = sourceElement.getAttribute(attributeName);
        if (attribute == null)
        {
            throw new GeneratorException(
                    "Source element attribute not set in "
                            + clazz.getName()
                            + "\n"
                            + "The attribute "
                            + attributeName
                            + " of the source element "
                            + elementName
                            + " is not set.");
        }
        return attribute.toString();
    }

    /**
     * Reads an option with a given name. The option must be set to a value
     * different from null.
     *
     * @param optionName the name of the option to read, not null.
     * @param controllerState the current state of the controller, not null.
     * @param clazz the class from which this method is called, not null.
     *        Only used for logging purposes.
     *
     * @return the value of the option.
     *
     * @throws GeneratorException if the option is not set or set to null.
     */
    public static String getOption(
            String optionName,
            ControllerState controllerState,
            Class<?> clazz)
                    throws GeneratorException
    {
        Object optionValue = controllerState.getOption(optionName);
        if (optionValue == null)
        {
            throw new GeneratorException("Invalid configuration of "
                    + clazz.getName()
                    + "\n"
                    + "The option "
                    + optionName
                    + " is not set.");
        }
        return optionValue.toString();
    }

    /**
     * Retrieve a value from either a preset value, an option, a variable,
     * or a source element attribute. Exactly one of these must be set to a
     * value different from zero.
     *
     * @param presetValue the plain result, or null if the preset value should
     *        not be used.
     * @param optionName the name of the option to access, or null if
     *        no option value should be returned.
     * @param variableName the name of the variable to access, or null if
     *        no variable should be accessed.
     * @param sourceElementName the name of the source element relative to the
     *        current element which attribute should be read. Null if no source
     *        attribute value should be used.
     * @param sourceElementAttribute the name of the attribute of the above
     *        source element.
     * @param controllerState the current state of the controller, not null.
     * @param clazz the class from which this method is called, not null.
     *        Used only for logging purposes.
     * @param expectedFieldNames the field names in which the information
     *        is expected; for logging purposes only.
     *
     * @return the desired value, not null.
     *
     * @throws GeneratorException if the value is not set or more than one
     *         possibility to get the value exists.
     */
    public static String getFromDifferentPlaces(
            String presetValue,
            String optionName,
            String variableName,
            String sourceElementName,
            String sourceElementAttribute,
            ControllerState controllerState,
            Class<?> clazz,
            String expectedFieldNames)
                    throws GeneratorException
    {
        if (optionName != null
                && sourceElementName == null
                && presetValue == null
                && variableName == null)
        {
            return OutletUtils.getOption(
                    optionName,
                    controllerState,
                    clazz);
        }
        else if (sourceElementName != null
                && optionName == null
                && presetValue == null
                && variableName == null)
        {
            return OutletUtils.getSourceElementAttribute(
                    sourceElementName,
                    sourceElementAttribute,
                    controllerState,
                    clazz);
        }
        else if (variableName != null
                && sourceElementName == null
                && optionName == null
                && presetValue == null)
        {
            Namespace namespace
            = controllerState.getOutlet().getName().getNamespace();
            QualifiedName variableQualifiedName = new QualifiedName(
                    variableName,
                    namespace);
            Variable variable
            = controllerState.getVariableStore().getInHierarchy(
                    variableQualifiedName);
            if (variable == null)
            {
                log.info("clazz.getName() : Variable " + variableQualifiedName
                        + " is not set, returning the empty String");
                return "";
            }
            if (variable.getValue() == null)
            {
                log.info("clazz.getName() : Variable " + variableQualifiedName
                        + " is set to null, returning the empty String");
                return "";
            }
            return variable.getValue().toString();
        }
        else if (presetValue != null
                && sourceElementName == null
                && optionName == null
                && variableName == null)
        {
            return presetValue;
        }
        else
        {
            throw new GeneratorException("Invalid configuration of "
                    + clazz.getName()
                    + "\n"
                    + "Make sure that one and only one of "
                    + expectedFieldNames
                    + " are set.");
        }
    }
}
