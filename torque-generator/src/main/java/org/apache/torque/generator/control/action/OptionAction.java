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
import org.apache.torque.generator.outlet.OutletResult;

/**
 * Inserts the value of an option into a mergepoint.
 */
public class OptionAction implements MergepointAction
{
    /**
     * The name of the option to insert.
     */
    private final String optionName;

    /**
     * Whether it is acceptable that the option is not set.
     */
    private boolean acceptNotSet = true;

    /**
     * Constructor.
     *
     * @param option the name of the option. May or may not be prefixed
     *        with a namespace (if no namespace is used, the namespace of the
     *        current outlet is used).
     * @param acceptNotSet true if it is acceptable that the option is not set,
     *        false if it is an error that the option is not set.
     *        null means true.
     */
    public OptionAction(String option, Boolean acceptNotSet)
    {
        if (option == null)
        {
            throw new IllegalArgumentException(
                    "OptionAction: option must not be null");
        }
        this.optionName = option;
        if (acceptNotSet != null)
        {
            this.acceptNotSet = acceptNotSet.booleanValue();
        }
    }

    /**
     * Returns the value of the configured option.
     *
     * @param controllerState the current state of the controller.
     *
     * @return The value of the option, or the empty String if
     *         acceptNotSet is true and the option is not set.
     *
     * @throws GeneratorException if acceptNotSet is false and the option
     *         is not set.
     */
    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException
    {
        Object option = controllerState.getOption(optionName);
        if (option == null)
        {
            if (acceptNotSet)
            {
                return new OutletResult("");
            }
            throw new GeneratorException("OptionAction: The option "
                    + optionName
                    + " is not set");
        }
        return new OutletResult(option.toString());
    }

    @Override
    public String toString()
    {
        return "(OptionAction: optionName="
                + optionName
                + ", acceptNotSet="
                + acceptNotSet
                + ")";
    }
}
