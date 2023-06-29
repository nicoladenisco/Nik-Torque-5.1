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
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.processor.string.ConstantNameCreator;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * Creates the name of a constant from a string. All letters in the String are
 * capitalized, and underscores (_) are used as separators per default.
 */
public class ConstantNameOutlet extends StringInputOutlet
{
    /**
     * The processor which does the character replacement.
     */
    private final ConstantNameCreator constantNameCreator = new ConstantNameCreator();

    /**
     * Constructor.
     *
     * @param qualifiedName the unique name of the outlet, not null.
     */
    public ConstantNameOutlet(QualifiedName qualifiedName)
    {
        super(qualifiedName);
    }

    /**
     * Processes the input according to the camelback rules.
     *
     * @param controllerState the current state of the controller, not null.
     *
     * @throws GeneratorException in processing fails.
     */
    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException
    {
        String input = getInput(controllerState);

        String result = constantNameCreator.process(input);

        return new OutletResult(result);
    }
}
