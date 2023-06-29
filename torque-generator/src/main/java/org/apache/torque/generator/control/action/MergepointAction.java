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
 * An action which can be executed in a mergepoint.
 */
public interface MergepointAction
{
    /**
     * Executes the action.
     *
     * @param controllerState The current state of the controller.
     *
     * @return The value to insert into the mergepoint, not null.
     *
     * @throws GeneratorException if an error occurs during generation.
     */
    OutletResult execute(ControllerState controllerState)
            throws GeneratorException;

}
