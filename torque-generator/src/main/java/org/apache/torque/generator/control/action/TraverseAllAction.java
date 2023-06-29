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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.outlet.OutletConfiguration;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.control.TokenReplacer;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.Namespace;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.source.SourcePath;
import org.apache.torque.generator.source.SourcePathPointer;

/**
 * Traverses all matching elements, and applies a outlet to each matching
 * element. The output of each outlet is appended to the output.
 */
public class TraverseAllAction implements MergepointAction
{
    /**
     * The filter for the elements to traverse, not null.
     */
    private final String elementsToTraverseName;

    /**
     * The name of the outlet to be invoked for each element, not null.
     */
    private final String outletName;

    /**
     * Whether it is correct to have no element to traverse.
     */
    private boolean acceptEmpty = true;

    /**
     * Constructor.
     *
     * @param elementsToTraverseName the elements to traverse, not null.
     * @param outletName the name of the outlet to be invoked for each
     *        element, not null.
     * @param acceptEmpty true if it is correct if no matching elements are
     *        found, false to throw an error if no matching elements are found,
     *        null means true.
     */
    public TraverseAllAction(
            final String elementsToTraverseName,
            final String outletName,
            final Boolean acceptEmpty)
    {
        if (elementsToTraverseName == null)
        {
            throw new IllegalArgumentException(
                    "TraverseAllAction: "
                            + "elementsToTraverseName must not be null");
        }
        this.elementsToTraverseName = elementsToTraverseName;

        if (outletName == null)
        {
            throw new IllegalArgumentException(
                    "TraverseAllAction: outletName must not be null");
        }
        this.outletName = outletName;
        if (acceptEmpty != null)
        {
            this.acceptEmpty = acceptEmpty.booleanValue();
        }
    }

    /**
     * Traverses all matching elements, and applies an outlet to each matching
     * element. The output of each outlet is appended to the output.
     * ${...} Tokens are replaced within outletName and
     * elementsToTraverseName.
     */
    @Override
    public OutletResult execute(final ControllerState controllerState)
            throws GeneratorException
    {
        final TokenReplacer tokenReplacer = new TokenReplacer(controllerState);

        Outlet outlet;
        {
            final OutletConfiguration outletConfiguration
            = controllerState.getUnitConfiguration()
            .getOutletConfiguration();
            final String detokenizedOutletName
            = tokenReplacer.process(outletName);
            final QualifiedName outletQName = new QualifiedName(
                    detokenizedOutletName,
                    Namespace.ROOT_NAMESPACE);

            outlet = outletConfiguration.getOutlet(outletQName);
            if (outlet == null)
            {
                throw new GeneratorException("TraverseAllAction : The outlet "
                        + outletName
                        + " does not exist");
            }
        }

        final String detokenizedElementToTraverseName
        = tokenReplacer.process(elementsToTraverseName);

        final Object currentModel = controllerState.getModel();
        final Iterator<SourcePathPointer> toTraverseIt
        = SourcePath.iteratePointer(
                controllerState.getModelRoot(),
                controllerState.getPathToModel(),
                currentModel,
                detokenizedElementToTraverseName);
        if (!acceptEmpty && toTraverseIt.hasNext())
        {
            throw new GeneratorException(
                    "TraverseAllAction : selected element "
                            + elementsToTraverseName
                            + " does not exist and acceptEmpty was set to false");
        }

        final List<OutletResult> resultList = new ArrayList<>();
        String oldPathToModel = controllerState.getPathToModel();
        while (toTraverseIt.hasNext())
        {
            final SourcePathPointer pointer = toTraverseIt.next();
            final Object model = pointer.getValue();
            String path = pointer.getPath();
            controllerState.setModel(
                    model,
                    path);
            outlet.beforeExecute(controllerState);
            resultList.add(outlet.execute(controllerState));
            outlet.afterExecute(controllerState);
            controllerState.setPathToModel(oldPathToModel);
        }
        controllerState.setModel(currentModel, null);

        if (resultList.isEmpty())
        {
            return new OutletResult("");
        }
        return OutletResult.concatenate(resultList);
    }

    @Override
    public String toString()
    {
        return "(TraverseAllAction: element = "
                + elementsToTraverseName
                + ", outlet = "
                + outletName
                + ", acceptEmpty = "
                + acceptEmpty
                + ")";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        final int secondPrime = 1231;
        final int thirdPrime = 1237;
        int result = 1;
        result = prime * result + (acceptEmpty ? secondPrime : thirdPrime);
        result = prime * result + elementsToTraverseName.hashCode();
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

        final TraverseAllAction other = (TraverseAllAction) obj;
        if (acceptEmpty != other.acceptEmpty)
        {
            return false;
        }
        if (!elementsToTraverseName.equals(other.elementsToTraverseName))
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
