package org.apache.torque.generator.outlet;

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

import java.util.Map;

import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.controller.Output;
import org.apache.torque.generator.configuration.mergepoint.MergepointMapping;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.control.outputtype.OutputType;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * An outlet wrapper which adds debug information to the outlet.
 *
 * @version $Id: $
 */
public class DebuggingOutletWrapper implements Outlet
{
    /** The wrapped outlet, not null. */
    private Outlet delegate;

    /**
     * Constructor.
     *
     * @param delegate the wrapped outlet, not null.
     */
    public DebuggingOutletWrapper(Outlet delegate)
    {
        if (delegate == null)
        {
            throw new NullPointerException("delegate must not be null");
        }
        this.delegate = delegate;
    }

    /**
     * Returns the name of the delegate.
     *
     * @return the name of the delegate, not null.
     */
    @Override
    public QualifiedName getName()
    {
        return delegate.getName();
    }

    /**
     * Adds the mergepoint mapping to the delegate.
     *
     * @param mergepointMapping the mergepoint mapping to add, not null.
     *
     * @throws NullPointerException if mergepointMapping is null.
     * @throws ConfigurationException if an mergepointMapping
     *          for the given name already exists.
     */
    @Override
    public void addMergepointMapping(MergepointMapping mergepointMapping)
            throws ConfigurationException
    {
        delegate.addMergepointMapping(mergepointMapping);
    }

    /**
     * Sets an mergepoint mapping in the delegate.
     *
     * @param mergepointMapping the mergepointMapping to add, not null.
     *
     * @return the replaced mergepoint mapping, not null.
     *
     * @throws NullPointerException if mergepointMapping is null.
     */
    @Override
    public MergepointMapping setMergepointMapping(
            MergepointMapping mergepointMapping)
    {
        return delegate.setMergepointMapping(mergepointMapping);
    }

    /**
     * Retrieves the mergepoint mapping for the given mergepoint name
     * from the delegate and returns it.
     *
     * @param name the name of the mergepoint mapping.
     *
     * @return the mergepoint mapping for the given name, or null if no
     *           mergepoint mapping exists for this name.
     */
    @Override
    public MergepointMapping getMergepointMapping(String name)
    {
        return delegate.getMergepointMapping(name);
    }

    /**
     * Returns the map of all mergepoint mappings in the delegate,
     * keyed by their name.
     *
     * @return the map of mergepoint mappings, not null.
     */
    @Override
    public Map<String, MergepointMapping> getMergepointMappings()
    {
        return delegate.getMergepointMappings();
    }

    /**
     * Sets the name of the input root element in the delegate.
     *
     * @param inputName the name of the root element of the source,
     *        or null to accept any input name.
     */
    @Override
    public void setInputElementName(String inputName)
    {
        delegate.setInputElementName(inputName);
    }

    /**
     * Returns the name of the input root element from the delegate.
     *
     * @return the name of the root element of the source,
     *         or null if any input name is accepted.
     */
    @Override
    public String getInputElementName()
    {
        return delegate.getInputElementName();
    }

    /**
     * Sets the class name of the input root object in the delegate.
     *
     * @param className the class name of the root object of the source,
     *        or null to accept any object class.
     */
    @Override
    public void setInputClass(String className)
    {
        delegate.setInputClass(className);
    }

    /**
     * Returns the class name of the input root object from the delegate.
     *
     * @return the class name of the root object of the source,
     *         or null if any object class is accepted.
     */
    @Override
    public String getInputClass()
    {
        return delegate.getInputClass();
    }

    /**
     * Calls the beforeExecute method in the delegate.
     *
     * @param controllerState the current controller state, not null.
     *
     * @throws GeneratorException if adjusting the controller state fails.
     */
    @Override
    public void beforeExecute(ControllerState controllerState)
            throws GeneratorException
    {
        delegate.beforeExecute(controllerState);
    }

    /**
     * Calls the afterExecute method in the delegate.
     *
     * @param controllerState the current controller state, not null.
     *
     * @throws GeneratorException if adjusting the controller state fails.
     */
    @Override
    public void afterExecute(ControllerState controllerState)
            throws GeneratorException
    {
        delegate.afterExecute(controllerState);
    }

    /**
     * Executes the delegate generation and adds debugging output
     * before and after the generated content, if the result is a String result.
     *
     * @param controllerState the current controller state, not null.
     *
     * @return the result of the generation plus possibly debugging information.
     *
     * @throws GeneratorException if adjusting the controller state fails.
     */
    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException
    {
        OutletResult outletResult = delegate.execute(controllerState);
        if (outletResult.isByteArrayResult())
        {
            // do nothing, no debugging for binary result
            return outletResult;
        }
        Output output = controllerState.getOutput();
        String lineBreak = output.getOrDetermineLineBreak(
                outletResult.getStringResult());
        String outputTypeString = output.getType();
        Map<String, OutputType> outputTypes
        = controllerState.getUnitConfiguration()
        .getConfigurationHandlers()
        .getOutputTypes();
        OutputType outputType = outputTypes.get(outputTypeString);
        if (outputType == null)
        {
            throw new GeneratorException("Unknown output type "
                    + outputTypeString
                    + " in output "
                    + output.getName());
        }
        String commentStart = outputType.getCommentStart(lineBreak);
        String commentAtStart = getCommentAtStart(controllerState);
        String commentAtEnd = getCommentAtEnd(controllerState);
        String commentEnd = outputType.getCommentEnd(lineBreak);
        return new OutletResult(commentStart + commentAtStart + commentEnd
                + outletResult.getStringResult()
                + commentStart + commentAtEnd + commentEnd);
    }

    /**
     * Returns the debug information to be added at the start of the outlet's
     * output.
     *
     * @param controllerState The current controller state, not null.
     *
     * @return the debug information, not null.
     */
    protected String getCommentAtStart(ControllerState controllerState)
    {
        return "start output of outlet " + controllerState.getOutlet().getName()
                + ", current model element is "
                + controllerState.getModel();
    }

    /**
     * Returns the debug information to be added at the end of the outlet's
     * output.
     *
     * @param controllerState The current controller state, not null.
     *
     * @return the debug information, not null.
     */
    protected String getCommentAtEnd(ControllerState controllerState)
    {
        return "end output of outlet " + controllerState.getOutlet().getName()
                + ", current model is "
                + controllerState.getModel();
    }
}
