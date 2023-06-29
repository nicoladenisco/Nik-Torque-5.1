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
import org.apache.torque.generator.outlet.OutletImpl;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * Takes a class name and a package name as input and generates a
 * filename(including path for package) for a java file from it.
 */
public class JavaFilenameOutlet extends OutletImpl
{
    /**
     * The name of the merge point which provides the class name.
     */
    public static final String PACKAGE_MERGEPOINT_NAME = "package";

    /**
     * The name of the merge point which provides the file name.
     */
    public static final String CLASSNAME_MERGEPOINT_NAME = "classname";


    /**
     * Constructor.
     *
     * @param qualifiedName the qualified name of the outlet, not null.
     */
    public JavaFilenameOutlet(QualifiedName qualifiedName)
    {
        super(qualifiedName);
    }

    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException
    {
        String packageName
        = mergepoint(PACKAGE_MERGEPOINT_NAME, controllerState);
        String className
        = mergepoint(CLASSNAME_MERGEPOINT_NAME, controllerState);

        String result = packageName.replace('.', '/')
                + "/"
                + className
                + ".java";
        return new OutletResult(result);
    }
}
