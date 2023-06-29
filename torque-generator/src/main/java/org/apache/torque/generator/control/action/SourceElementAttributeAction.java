package org.apache.torque.generator.control.action;

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
import org.apache.torque.generator.control.TokenReplacer;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourcePath;

/**
 * Applies an outlet to a given source element.
 */
public class SourceElementAttributeAction implements MergepointAction
{
    /**
     * The SourePath to find the element which should be used as input
     * relative to the current element.
     */
    private final String elementPath;

    /**
     * The name of the attribute which should be output.
     */
    private final String attributeName;

    /**
     * Whether it is acceptable that the element does not exist or the given
     * attribute is not set.
     */
    private boolean acceptNotSet = true;

    /**
     * Constructor.
     *
     * @param element the SourcePath to the element, not null.
     * @param attributeName The name of the attribute to evaluate,
     *        or null to access the attribute with name null.
     * @param acceptNotSet true if the attribute may not exist,
     *        false if it is an error that the attribute does not exist,
     *        null means true.
     */
    public SourceElementAttributeAction(
            final String element,
            final String attributeName,
            final Boolean acceptNotSet)
    {
        if (element == null)
        {
            throw new IllegalArgumentException(
                    "SourceElementAttributeAction: element must not be null");
        }
        this.elementPath = element;
        this.attributeName = attributeName;
        if (acceptNotSet != null)
        {
            this.acceptNotSet = acceptNotSet.booleanValue();
        }
    }

    /**
     * Returns the value of the configured attribute of the configured source
     * element. ${...} Tokens are replaced within the element path and the
     * attribute name.
     *
     * @param controllerState the current state of the controller.
     *
     * @return The value of the attribute, or the empty String if
     *         acceptNotSet is true and the attribute is not set.
     *
     * @throws GeneratorException if acceptNotSet is false and either no
     *         source element can be found or the attribute is not set.
     */
    @Override
    public OutletResult execute(final ControllerState controllerState)
            throws GeneratorException
    {
        TokenReplacer tokenReplacer = new TokenReplacer(controllerState);
        String detonizedElementPath = tokenReplacer.process(elementPath);

        SourceElement sourceElement = SourcePath.getElement(
                (SourceElement) controllerState.getModel(),
                detonizedElementPath,
                acceptNotSet);
        if (sourceElement == null)
        {
            if (acceptNotSet)
            {
                return new OutletResult("");
            }
            else
            {
                throw new GeneratorException("SourceElementAttributeAction: "
                        + "No element "
                        + elementPath
                        + "can be found.");
            }
        }
        String detokenizedAttributeName = tokenReplacer.process(attributeName);
        Object result = sourceElement.getAttribute(detokenizedAttributeName);
        if (result == null)
        {
            if (acceptNotSet)
            {
                return new OutletResult("");
            }
            throw new GeneratorException("SourceElementAttributeAction: "
                    + "The attribute "
                    + attributeName
                    + " is not set on the element "
                    + sourceElement.getName()
                    + " (element path was "
                    + elementPath
                    + ", value was"
                    + sourceElement
                    + ")");
        }
        return new OutletResult(result.toString());
    }

    @Override
    public String toString()
    {
        return "(ApplyAction: element="
                + elementPath
                + ", attribute="
                + attributeName
                + ", acceptNotSet="
                + acceptNotSet
                + ")";
    }
}
