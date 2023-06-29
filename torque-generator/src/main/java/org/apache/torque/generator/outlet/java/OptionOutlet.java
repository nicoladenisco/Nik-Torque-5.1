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
import org.apache.torque.generator.qname.QualifiedName;

/**
 * Writes an option to the output.
 */
public class OptionOutlet extends OutletWithoutMergepoints
{
    /**
     * The name of the option.
     */
    private String option;

    /**
     * Constructor.
     *
     * @param name the name of the outlet.
     */
    public OptionOutlet(QualifiedName name)
    {
        super(name);
    }

    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException
    {
        return new OutletResult(OutletUtils.getOption(
                option,
                controllerState,
                OptionOutlet.class));
    }

    /**
     * Sets the name of the option to output.
     *
     * @param option the name of the option.
     */
    public void setOption(String option)
    {
        this.option = option;
    }
}
