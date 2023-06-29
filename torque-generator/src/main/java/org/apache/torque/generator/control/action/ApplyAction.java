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

import java.util.Iterator;

import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.control.TokenReplacer;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.Namespace;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.source.SourcePath;
import org.apache.torque.generator.source.SourcePathPointer;

/**
 * Applies an outlet to the matching element.
 * The output of the outlet is appended to the output.
 */
public class ApplyAction implements MergepointAction
{
    /**
     * The path to the source element to be processed, not null.
     */
    private String path;

    /**
     * The name of the outlet to be invoked on the element, not null.
     */
    private final String outletName;

    /**
     * Whether it is correct to have no element to apply.
     */
    private boolean acceptNotSet = true;

    /**
     * Constructor.
     *
     * @param path The path to the source element to be processed.
     *        <code>null</code> means ".".
     * @param outletName the name of the outlet to apply.
     * @param acceptNotSet true if no error should be thrown if no element
     *        matches the given path, true if an error should be thrown in
     *        such a case. null means true.
     */
    public ApplyAction(final String path, final String outletName, final Boolean acceptNotSet)
    {
        if (path == null)
        {
            this.path = ".";
        }
        else
        {
            this.path = path;
        }

        if (outletName == null)
        {
            throw new IllegalArgumentException(
                    "ApplyAction: outletName must not be null");
        }
        this.outletName = outletName;
        if (acceptNotSet != null)
        {
            this.acceptNotSet = acceptNotSet.booleanValue();
        }
    }

    /**
     * Applies an outlet to the matching element.
     * The output of the outlet is appended to the output.
     * ${...} Tokens are replaced within outletName and path.
     */
    @Override
    public OutletResult execute(final ControllerState controllerState)
            throws GeneratorException
    {
        TokenReplacer tokenReplacer = new TokenReplacer(controllerState);
        String detokenizedOutletName = tokenReplacer.process(outletName);
        QualifiedName qualifiedOutletName = new QualifiedName(
                detokenizedOutletName,
                Namespace.ROOT_NAMESPACE);
        Outlet outlet = controllerState.getUnitConfiguration()
                .getOutletConfiguration()
                .getOutlet(qualifiedOutletName);
        if (outlet == null)
        {
            throw new GeneratorException("ApplyAction : The outlet "
                    + outletName
                    + " does not exist");
        }

        Object model = controllerState.getModel();
        String pathToModel = controllerState.getPathToModel();
        String detokenizedPath = tokenReplacer.process(path);

        Iterator<SourcePathPointer> selectedObjectsIt
        = SourcePath.iteratePointer(
                controllerState.getModelRoot(),
                pathToModel,
                model,
                detokenizedPath);
        if (!selectedObjectsIt.hasNext())
        {
            if (!acceptNotSet)
            {
                throw new GeneratorException(
                        "ApplyAction : selected path "
                                + path
                                + " does not match an element"
                                + " and acceptNotSet was set to false");
            }
            return new OutletResult("");
        }
        SourcePathPointer pointer = selectedObjectsIt.next();
        Object selectedObject = pointer.getValue();
        if (selectedObjectsIt.hasNext())
        {
            throw new GeneratorException(
                    "ApplyAction : selected path "
                            + path
                            + " contains more than one element");
        }

        String oldPathToModel = controllerState.getPathToModel();
        controllerState.setModel(selectedObject, pointer.getPath());
        outlet.beforeExecute(controllerState);
        OutletResult result = outlet.execute(controllerState);
        outlet.afterExecute(controllerState);
        controllerState.setModel(model, null);
        controllerState.setPathToModel(oldPathToModel);
        return result;
    }

    @Override
    public String toString()
    {
        return "(ApplyAction: path = "
                + path
                + ", outlet = "
                + outletName
                + ", acceptNotSet = "
                + acceptNotSet
                + ")";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        final int secondPrime = 1231;
        final int thirdPrime = 1237;
        int result = 1;
        result = prime * result + (acceptNotSet ? secondPrime : thirdPrime);
        result = prime * result + path.hashCode();
        result = prime * result + outletName.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final ApplyAction other = (ApplyAction) obj;
        if (acceptNotSet != other.acceptNotSet)
        {
            return false;
        }
        if (!path.equals(other.path))
        {
            return false;
        }
        if (!outletName.equals(other.outletName))
        {
            return false;
        }
        return true;
    }
}
