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

/**
 * Inserts a fixed String into a mergepoint.
 */
public class OutputAction implements MergepointAction
{
    /**
     * The fixed value to insert.
     */
    private final String value;

    /**
     * Constructor.
     *
     * @param value fixed value to insert, not null.
     */
    public OutputAction(String value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(
                    "OutputAction: value must not be null");
        }
        this.value = value;
    }

    /**
     * Returns the value. ${...} Tokens are replaced within the value.
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
        TokenReplacer tokenReplacer = new TokenReplacer(controllerState);
        String detokenizedValue = tokenReplacer.process(value);
        return new OutletResult(detokenizedValue);
    }

    @Override
    public String toString()
    {
        return "(OutputAction: value="
                + value
                + ")";
    }
}
