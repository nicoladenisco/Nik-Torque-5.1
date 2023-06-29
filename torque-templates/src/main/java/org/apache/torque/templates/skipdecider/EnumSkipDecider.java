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
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.skipDecider.SkipDecider;
import org.apache.torque.templates.transformer.om.ColumnAttributeName;

/**
 * A skip decider which returns true if the attribute "generateEnum"
 * is set to "true" the current source element.
 *
 * @version $Id: InterfaceSkipDecider.java 1470235 2013-04-20 21:23:39Z tfischer $
 */
public class EnumSkipDecider implements SkipDecider
{
    @Override
    public boolean proceed(final ControllerState controllerState)
    {
        SourceElement sourceElement
        = (SourceElement) controllerState.getModel();
        String generateEnum
        = (String) sourceElement.getAttribute(
                ColumnAttributeName.GENERATE_ENUM);
        return Boolean.parseBoolean(generateEnum);
    }
}
