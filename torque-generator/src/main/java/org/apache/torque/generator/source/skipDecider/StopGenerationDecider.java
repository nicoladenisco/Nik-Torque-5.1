package org.apache.torque.generator.source.skipDecider;

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

import org.apache.torque.generator.control.ControllerState;

/**
 * A SkipDecider which always stops the current generation.
 *
 * $Id: StopGenerationDecider.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class StopGenerationDecider implements SkipDecider
{
    /**
     * Decides whether generation should proceed. This implementation
     * always stops the current generation.
     *
     * @param controllerState the current controller state.
     *
     * @return <code>false</code>
     */
    @Override
    public boolean proceed(ControllerState controllerState)
    {
        return false;
    }
}
