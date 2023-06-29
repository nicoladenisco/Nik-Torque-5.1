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
 * Takes a package as input and generates a path from it.
 */
public class PackageToPathOutlet extends StringInputOutlet
{
    /**
     * Constructor for use in child classes.
     *
     * @param qualifiedName the fully qualified name of the outlet.
     */
    public PackageToPathOutlet(QualifiedName qualifiedName)
    {
        super(qualifiedName);
    }

    /**
     * Reads the input and replaces all dots by slashes.
     *
     * @see org.apache.torque.generator.outlet.Outlet#execute(ControllerState)
     */
    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException
    {
        String packagenameInput = getInput(controllerState);
        String result = packagenameInput.replace('.', '/');
        return new OutletResult(result);
    }
}
