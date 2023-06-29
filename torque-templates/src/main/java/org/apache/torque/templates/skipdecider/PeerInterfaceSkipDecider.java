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
import org.apache.torque.templates.TorqueSchemaAttributeName;

/**
 * A skip decider which returns true if the attribute "peerInterface"
 * is set on the current source element and if the name does not contain
 * a dot.
 *
 * @version $Id$
 */
public class PeerInterfaceSkipDecider implements SkipDecider
{
    @Override
    public boolean proceed(ControllerState controllerState)
    {
        SourceElement sourceElement
        = (SourceElement) controllerState.getModel();
        String interfaceName
        = (String) sourceElement.getAttribute(
                TorqueSchemaAttributeName.PEER_INTERFACE.getName());
        if (interfaceName == null)
        {
            return false;
        }
        boolean containsDot = interfaceName.indexOf('.') != -1;
        return !containsDot;
    }
}
