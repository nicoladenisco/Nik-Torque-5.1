package org.apache.torque.templates.skipdecider;

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
import org.apache.torque.generator.source.skipDecider.SkipDecider;
import org.apache.torque.templates.TemplateOptionName;

/**
 * A source filter which returns true if the option
 * "torque.om.generateMapInit" is true.
 *
 * @version $Id: MapInitSkipDecider.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class MapInitSkipDecider implements SkipDecider
{
    @Override
    public boolean proceed(ControllerState controllerState)
    {
        return controllerState.getBooleanOption(
                TemplateOptionName.OM_GENERATE_MAP_INIT);
    }
}